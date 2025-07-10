package io.github.exampleuser.exampleplugin.utility;

import io.github.exampleuser.exampleplugin.messenger.MessengerHandler;
import io.github.exampleuser.exampleplugin.messenger.broker.BrokerType;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Convenience class for accessing methods in {@link MessengerHandler}
 */
public final class Messenger {
    private static Messenger INSTANCE;

    private static Messenger getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Messenger();
        return INSTANCE;
    }

    private MessengerHandler messengerHandler;

    private MessengerHandler getMessengerHandler() {
        return messengerHandler;
    }

    private void setMessengerHandler(MessengerHandler handler) {
        this.messengerHandler = handler;
    }

    /**
     * Used to set the globally used messenger handler instance for the plugin
     */
    public static void init(MessengerHandler handler) {
        getInstance().setMessengerHandler(handler);
    }

    /**
     * Convenience method for {@link MessengerHandler#isReady()}
     *
     * @return if the message broker is ready
     */
    public static boolean isReady() {
        final MessengerHandler handler = getInstance().getMessengerHandler();
        if (handler == null)
            return false;

        return handler.isReady();
    }

    /**
     * Convenience method for accessing the {@link MessengerHandler} instance
     *
     * @return the messenger handler
     */
    @NotNull
    public static MessengerHandler getHandler() {
        return getInstance().getMessengerHandler();
    }

    /**
     * Convenience method for {@link MessengerHandler#getType()} to get {@link BrokerType}
     *
     * @return the broker type, defaults to {@link BrokerType#DATABASE} if not loaded
     */
    public static BrokerType getType() {
        return getInstance().getMessengerHandler().getType();
    }

    /**
     * Convenience method for {@link MessengerHandler#send(OutgoingMessage)}
     *
     * @param message the outgoing message
     * @return if the message was successfully sent
     */
    public static <T> CompletableFuture<Boolean> send(final OutgoingMessage<T> message) {
        final MessengerHandler handler = getInstance().getMessengerHandler();
        if (handler == null)
            return CompletableFuture.completedFuture(false);

        return handler.send(message);
    }
}
