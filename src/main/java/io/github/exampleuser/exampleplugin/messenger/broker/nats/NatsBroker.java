package io.github.exampleuser.exampleplugin.messenger.broker.nats;

import io.github.exampleuser.exampleplugin.messenger.MessageReceiver;
import io.github.exampleuser.exampleplugin.messenger.broker.Broker;
import io.github.exampleuser.exampleplugin.messenger.broker.MessagingUtils;
import io.github.exampleuser.exampleplugin.messenger.config.Addresses;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.messenger.message.Message;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import io.nats.client.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

/**
 * Implementation using nats client as a message broker
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class NatsBroker extends Broker {
    private static final Logger LOGGER = LoggerFactory.getLogger(NatsBroker.class);

    private final String name;
    private final String channelName;

    private Connection connection;
    private Dispatcher dispatcher;

    public NatsBroker(MessageReceiver messageReceiver, String name) {
        super(messageReceiver);
        this.name = name;
        this.channelName = "%s:message".formatted(name.toLowerCase());
    }

    @Override
    public <T> void send(@NotNull OutgoingMessage<T> message) {
        connection.publish(channelName, MessagingUtils.ByteUtil.to(message));
    }

    @Override
    public void init(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
        final Options.Builder builder = new Options.Builder()
            .reconnectWait(Duration.ofSeconds(5))
            .maxReconnects(Integer.MAX_VALUE)
            .connectionName(name)
            .userInfo(config.username(), config.password());

        if (config.addresses().getType().equals(Addresses.AddressesType.SINGLE)) {
            builder.server("nats://%s".formatted(config.addresses().getAddress()));
        } else {
            builder.servers(
                config.addresses().getAddresses().stream()
                    .map("nats://%s"::formatted)
                    .toArray(String[]::new)
            );
        }

        if (config.ssl())
            builder.secure();

        connection = Nats.connect(builder.build());
        dispatcher = connection.createDispatcher(new Handler()).subscribe(channelName);
    }

    @Override
    public void close() {
        try {
            connection.closeDispatcher(dispatcher);
            connection.close();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while closing Nats connection", e);
        }
    }

    /**
     * Subscriber that defines handling of incoming messages
     */
    private final class Handler implements MessageHandler {
        @Override
        public void onMessage(io.nats.client.Message msg) {
            final Message<?> message = MessagingUtils.ByteUtil.from(msg.getData());
            getMessageConsumer().receive(message);
        }
    }
}
