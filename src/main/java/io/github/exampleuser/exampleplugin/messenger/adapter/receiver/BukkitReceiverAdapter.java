package io.github.exampleuser.exampleplugin.messenger.adapter.receiver;

import io.github.exampleuser.exampleplugin.messenger.event.SyncMessageEvent;
import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;
import io.github.milkdrinkers.threadutil.Scheduler;

public class BukkitReceiverAdapter extends ReceiverAdapter {
    @Override
    public void accept(IncomingMessage<?, ?> message) {
        Scheduler.sync(() -> {
            new SyncMessageEvent(message).callEvent();
        }).execute();
    }
}
