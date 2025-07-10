package io.github.exampleuser.exampleplugin.messenger;

import io.github.exampleuser.exampleplugin.event.MockEventSystem;
import io.github.exampleuser.exampleplugin.messenger.adapter.receiver.ReceiverAdapter;
import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;

public class MockReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(IncomingMessage<?, ?> message) {
        MockEventSystem.fireEvent(new MockSyncMessageEvent(message));
    }
}
