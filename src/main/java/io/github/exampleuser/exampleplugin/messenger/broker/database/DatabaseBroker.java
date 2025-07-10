package io.github.exampleuser.exampleplugin.messenger.broker.database;

import io.github.exampleuser.exampleplugin.database.Queries;
import io.github.exampleuser.exampleplugin.messenger.MessageReceiver;
import io.github.exampleuser.exampleplugin.messenger.adapter.task.TaskAdapter;
import io.github.exampleuser.exampleplugin.messenger.broker.Broker;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.messenger.message.IncomingMessage;
import io.github.exampleuser.exampleplugin.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of a database as a message broker
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class DatabaseBroker extends Broker {
    private final String name;
    private final String channelName;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(); // Used to prevent writing to database while reading

    private final TaskAdapter syncTask;
    private final TaskAdapter cleanupTask;

    private final AtomicInteger latestSyncId = new AtomicInteger(-1); // Tracks the last read message id to prevent re-reading messages
    private MessengerConfig config;

    public DatabaseBroker(MessageReceiver messageReceiver, String name, TaskAdapter syncTask, TaskAdapter cleanupTask) {
        super(messageReceiver);
        this.name = name;
        this.channelName = "%s:message".formatted(name.toLowerCase());
        this.syncTask = syncTask;
        this.cleanupTask = cleanupTask;
    }

    @Override
    public <T> void send(@NotNull OutgoingMessage<T> message) {
        try {
            lock.writeLock().lock();
            Queries.Sync.send(message);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void init(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
        this.config = config;
        latestSyncId.set(Queries.Sync.fetchLatestMessageId().orElse(-1));
    }

    @Override
    public void enable(MessengerConfig config) throws IOException, InterruptedException, NoSuchAlgorithmException {
        syncTask.init(this::fetch, config.pollingInterval(), config.pollingInterval(), TimeUnit.MILLISECONDS);
        cleanupTask.init(this::cleanup, config.cleanupInterval(), config.cleanupInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        if (syncTask != null)
            syncTask.cancel();

        if (cleanupTask != null)
            cleanupTask.cancel();

        latestSyncId.set(-1);
    }

    private void fetch() {
        try {
            lock.readLock().lock();
            final int oldId = latestSyncId.get();
            final Map<Integer, IncomingMessage<?, ?>> messages = Queries.Sync.receive(oldId, config.cleanupInterval());

            // Consume messages and update the latest id
            int newId = oldId;
            for (Map.Entry<Integer, IncomingMessage<?, ?>> message : messages.entrySet()) {
                final int messageId = message.getKey();
                newId = Math.max(newId, messageId);
                getMessageConsumer().receive(message.getValue());
            }

            latestSyncId.set(newId);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void cleanup() {
        try {
            lock.readLock().lock();
            Queries.Sync.cleanup(config.cleanupInterval());
        } finally {
            lock.readLock().unlock();
        }
    }
}
