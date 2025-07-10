package io.github.exampleuser.exampleplugin.messenger.event;

import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a synchronization message is received by the message broker. This event allows you to react to the incoming message on the main thread.
 *
 * @implNote This event will never be fired during the Bukkit servers {@link JavaPlugin#onLoad()} or {@link JavaPlugin#onDisable()}.
 */
@SuppressWarnings("unused")
public class SyncMessageEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final IncomingMessage<?, ?> message;

    public SyncMessageEvent(final IncomingMessage<?, ?> message) {
        this.message = message;
    }

    public IncomingMessage<?, ?> getMessage() {
        return message;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}