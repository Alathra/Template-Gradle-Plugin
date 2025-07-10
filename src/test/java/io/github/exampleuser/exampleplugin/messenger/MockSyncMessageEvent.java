package io.github.exampleuser.exampleplugin.messenger;

import io.github.exampleuser.exampleplugin.event.MockEvent;
import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;

public class MockSyncMessageEvent extends MockEvent {
    private final IncomingMessage<?, ?> message;

    public MockSyncMessageEvent(IncomingMessage<?, ?> message) {
        this.message = message;
    }

    public IncomingMessage<?, ?> getMessage() {
        return message;
    }
}