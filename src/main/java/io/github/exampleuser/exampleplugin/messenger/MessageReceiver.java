package io.github.exampleuser.exampleplugin.messenger;

import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;

/**
 * A class with the capability of receiving incoming messages.
 */
public interface MessageReceiver {
    /**
     * Handle receiving a message
     *
     * @param message the message
     */
    void receive(final IncomingMessage<?, ?> message);
}
