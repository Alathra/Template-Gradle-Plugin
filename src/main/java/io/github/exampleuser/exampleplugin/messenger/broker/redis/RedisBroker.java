package io.github.exampleuser.exampleplugin.messenger.broker.redis;

import io.github.exampleuser.exampleplugin.messenger.MessageReceiver;
import io.github.exampleuser.exampleplugin.messenger.adapter.task.TaskAdapter;
import io.github.exampleuser.exampleplugin.messenger.broker.Broker;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.messenger.message.Message;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of jedis client as a message broker
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class RedisBroker extends Broker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisBroker.class);

    private final String name;
    private final String channelName;
    private final TaskAdapter task;
    private final Subscriber subscriber;

    private RedisClient client;
    private volatile boolean closing;

    public RedisBroker(MessageReceiver messageReceiver, String name, TaskAdapter task) {
        super(messageReceiver);
        this.name = name;
        this.channelName = "%s:message".formatted(name.toLowerCase());
        this.task = task;
        this.subscriber = new Subscriber();
    }

    @Override
    public <T> void send(@NotNull OutgoingMessage<T> message) {
        client.publish(channelName, message.encode());
    }

    @Override
    public void init(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
        client = new RedisClient(config);
    }

    @Override
    public void enable(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
        task.init(subscriber, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        closing = true;
        subscriber.unsubscribeSafely();
        task.cancel();
        client.close();
    }

    /**
     * Subscriber that defines handling of incoming messages
     */
    private final class Subscriber extends JedisPubSub implements Runnable {
        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(channelName))
                return;

            final Message<?> message2 = Message.from(message);
            getMessageConsumer().receive(message2);
        }

        @Override
        public void run() {
            boolean firstStartup = true;
            while (!closing && !Thread.interrupted() && client.isAlive()) {
                try {
                    if (firstStartup) {
                        firstStartup = false;
                    } else {
                        LOGGER.info("Connection to Redis instance reestablished!");
                    }
                    client.subscribe(this, channelName);
                } catch (Exception e) {
                    if (closing)
                        return;

                    LOGGER.warn("Unable to connect to Redis instance, retrying in 5 seconds...", e);
                    unsubscribeSafely();
                    sleepBeforeRetry();
                }
            }
        }

        private void unsubscribeSafely() {
            try {
                unsubscribe();
            } catch (Exception ignored) {
            }
        }

        private void sleepBeforeRetry() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
