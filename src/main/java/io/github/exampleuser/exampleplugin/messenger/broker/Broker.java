package io.github.exampleuser.exampleplugin.messenger.broker;

import io.github.exampleuser.exampleplugin.messenger.MessageReceiver;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract class used to create a pub/sub message broker implementation.
 */
public abstract class Broker implements AutoCloseable {
    private final MessageReceiver messageReceiver;

    protected Broker(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public MessageReceiver getMessageConsumer() {
        return messageReceiver;
    }

    public abstract <T> void send(@NotNull OutgoingMessage<T> message) throws IOException, RuntimeException;

    public void init(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
    }

    public void enable(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
    }

    @Override
    public void close() {
    }
}
