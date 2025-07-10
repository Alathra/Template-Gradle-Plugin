package io.github.exampleuser.exampleplugin.messenger.adapter.receiver;

import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;

import java.util.function.Consumer;

/**
 * Defines platform specific behavior when receiving a message from the message broker.
 */
public abstract class ReceiverAdapter implements Consumer<IncomingMessage<?, ?>> {
    @Override
    public abstract void accept(final IncomingMessage<?, ?> message);
}
