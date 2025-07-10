package io.github.exampleuser.exampleplugin.messenger;

import io.github.exampleuser.exampleplugin.AbstractService;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.messenger.adapter.receiver.BukkitReceiverAdapter;
import io.github.exampleuser.exampleplugin.messenger.adapter.receiver.ReceiverAdapter;
import io.github.exampleuser.exampleplugin.messenger.adapter.task.BukkitTaskAdapter;
import io.github.exampleuser.exampleplugin.messenger.adapter.task.TaskAdapter;
import io.github.exampleuser.exampleplugin.messenger.broker.Broker;
import io.github.exampleuser.exampleplugin.messenger.broker.BrokerType;
import io.github.exampleuser.exampleplugin.messenger.broker.database.DatabaseBroker;
import io.github.exampleuser.exampleplugin.messenger.broker.nats.NatsBroker;
import io.github.exampleuser.exampleplugin.messenger.broker.pluginmsg.PluginBroker;
import io.github.exampleuser.exampleplugin.messenger.broker.rabbitmq.RabbitMQBroker;
import io.github.exampleuser.exampleplugin.messenger.broker.redis.RedisBroker;
import io.github.exampleuser.exampleplugin.messenger.cache.CacheSet;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.messenger.exception.MessengerInitializationException;
import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import io.github.exampleuser.exampleplugin.utility.DB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A class handling the lifecycle and management of the messaging service.
 */
public class MessengerHandler extends AbstractService implements Reloadable, MessageReceiver {
    private final boolean testing;
    private final Logger logger;
    private final String implementationName;
    private final TaskAdapter taskAdapter;
    private final ReceiverAdapter receiverAdapter;
    private MessengerConfig config;
    private @Nullable CacheSet<UUID> receivedMessageIds = null; // Tracks messages consumed by this instance, preventing itself from processing them
    private @Nullable Broker broker = null;

    /**
     * Instantiates a new Messenger handler.
     *
     * @param logger             the logger
     * @param implementationName the implementation name
     */
    private MessengerHandler(Logger logger, String implementationName) {
        this.testing = false;
        this.logger = logger;
        this.implementationName = implementationName;
        this.taskAdapter = new BukkitTaskAdapter();
        this.receiverAdapter = new BukkitReceiverAdapter();
    }

    /**
     * Instantiates a new Messenger handler.
     *
     * @param config             the messenger config
     * @param testing            the testing
     * @param logger             the logger
     * @param implementationName the implementation name
     * @param taskAdapter        the task adapter
     * @param receiverAdapter    the receiver adapter
     */
    @TestOnly
    private MessengerHandler(@NotNull MessengerConfig config, boolean testing, Logger logger, String implementationName, TaskAdapter taskAdapter, ReceiverAdapter receiverAdapter) {
        this.config = config;
        this.testing = testing;
        this.logger = logger;
        this.implementationName = implementationName;
        this.taskAdapter = taskAdapter;
        this.receiverAdapter = receiverAdapter;
    }

    @Override
    public void onLoad(ExamplePlugin plugin) {
        if (config == null)
            this.config = MessengerConfig.fromConfig(plugin.getConfigHandler().getDatabaseConfig());

        if (!config.enabled())
            return;

        doStartup();

        if (!isReady())
            logger.warn("[SYNC] Error while initializing message broker. Functionality will be limited.");
    }

    @Override
    public void onEnable(ExamplePlugin plugin) {
        if (config == null || !config.enabled())
            return;

        scheduleTasks();
    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
        if (config == null || !config.enabled())
            return;

        doShutdown();
    }

    @Override
    protected void startup() throws Exception {
        logger.info("[SYNC] Starting message broker...");

        receivedMessageIds = new CacheSet<>(10, TimeUnit.MINUTES);
        broker = switch (config.brokerType()) {
            case PLUGIN_MESSAGING -> new PluginBroker(this, implementationName);
            case REDIS -> new RedisBroker(this, implementationName, taskAdapter);
            case RABBITMQ -> new RabbitMQBroker(this, implementationName, taskAdapter);
            case NATS -> new NatsBroker(this, implementationName);
            default -> new DatabaseBroker(this, implementationName, taskAdapter, taskAdapter);
        };

        if (config.brokerType().equals(BrokerType.DATABASE) && !DB.isReady())
            throw new MessengerInitializationException("Database is required for this message broker but the database has failed to initialize!");

        if (broker == null)
            throw new MessengerInitializationException("Attempted to initialize message broker but broker is null!");

        try {
            broker.init(config);
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            throw new MessengerInitializationException("Attempt to initialize message broker threw an exception!", e);
        }

        logger.info("[SYNC] Successfully started message broker.");
    }

    public void scheduleTasks() {
        if (broker == null)
            throw new MessengerInitializationException("Attempted to enable message broker but broker is null!");

        try {
            broker.enable(config);
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            throw new MessengerInitializationException("Attempt to enable message broker threw an exception!", e);
        }
    }

    @Override
    protected void shutdown() throws Exception {
        logger.info("[SYNC] Shutting down message broker...");

        if (broker != null)
            broker.close();

        if (receivedMessageIds != null)
            receivedMessageIds.close();

        broker = null;
        receivedMessageIds = null;
        config = null;

        logger.info("[SYNC] Shut down message broker.");
    }

    /**
     * Sends a message using the configured message broker.
     *
     * @param message the outgoing message
     * @return if the message was successfully sent
     */
    public <T> CompletableFuture<Boolean> send(final OutgoingMessage<T> message) {
        if (!isStarted() || receivedMessageIds == null || broker == null)
            return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
                try {
                    if (!testing)
                        receivedMessageIds.add(message.getUUID()); // Allow receiving sent messages in testing environments
                    broker.send(message);
                    logger.debug("[SYNC] Sent message with uuid \"{}\", channel id \"{}\" and payload of type \"{}\".", message.getUUID(), message.getChannelID(), message.getPayloadType().getName());
                    return true;
                } catch (IOException e) {
                    return false;
                }
            })
            .exceptionally(throwable -> false);
    }

    @Override
    public void receive(final IncomingMessage<?, ?> message) {
        if (!isStarted() || receivedMessageIds == null || receivedMessageIds.contains(message.getUUID()))
            return;

        logger.debug("[SYNC] Received message with uuid \"{}\", channel id \"{}\" and payload of type \"{}\"...", message.getUUID(), message.getChannelID(), message.getPayloadType().getName());
        receivedMessageIds.add(message.getUUID());
        receiverAdapter.accept(message);
    }

    /**
     * Returns if the broker is setup and functioning properly.
     *
     * @return the boolean
     */
    public boolean isReady() {
        return isStarted();
    }

    /**
     * Returns the type of the currently configured message broker.
     *
     * @return the broker type, defaults to {@link BrokerType#DATABASE} if not configured
     */
    public BrokerType getType() {
        if (config == null)
            return BrokerType.DATABASE;

        return config.brokerType();
    }

    /**
     * Get a builder instance for this class.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The type Messenger handler builder.
     */
    public static class Builder {
        private boolean testing = false;
        private Logger logger;
        private String implementationName;
        private TaskAdapter taskAdapter;
        private ReceiverAdapter receiverAdapter;
        private MessengerConfig config;

        private Builder() {
        }

        /**
         * With testing messenger handler builder.
         *
         * @param testing the testing state
         * @return the messenger handler builder
         */
        public Builder withTesting(boolean testing) {
            this.testing = testing;
            return this;
        }

        /**
         * With logger messenger handler builder.
         *
         * @param logger the logger
         * @return the messenger handler builder
         */
        public Builder withLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * With implementation name handler builder.
         *
         * @param implementationName the name of this implementation
         * @return the messenger handler builder
         */
        public Builder withName(String implementationName) {
            this.implementationName = implementationName;
            return this;
        }

        /**
         * With taskAdapter handler builder.
         *
         * @param taskAdapter the taskAdapter of this implementation
         * @return the messenger handler builder
         */
        public Builder withTaskAdapter(TaskAdapter taskAdapter) {
            this.taskAdapter = taskAdapter;
            return this;
        }

        /**
         * With receiverAdapter handler builder.
         *
         * @param receiverAdapter the receiverAdapter of this implementation
         * @return the messenger handler builder
         */
        public Builder withReceiverAdapter(ReceiverAdapter receiverAdapter) {
            this.receiverAdapter = receiverAdapter;
            return this;
        }

        /**
         * With messenger config messenger handler builder.
         *
         * @param config the messenger config
         * @return the messenger handler builder
         */
        @TestOnly
        public Builder withConfig(@NotNull MessengerConfig config) {
            this.config = config;
            return this;
        }

        /**
         * Build messenger handler.
         *
         * @return the messenger handler
         */
        public MessengerHandler build() {
            if (implementationName == null)
                implementationName = "";

            if (logger != null && config == null)
                return new MessengerHandler(logger, implementationName);

            if (config == null)
                throw new RuntimeException("Failed to build messenger handler as config is null!");

            if (logger != null && taskAdapter != null)
                return new MessengerHandler(config, testing, logger, implementationName, taskAdapter, receiverAdapter);

            throw new RuntimeException("Failed to build messenger handler!");
        }
    }
}
