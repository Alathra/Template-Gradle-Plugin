package io.github.exampleuser.exampleplugin.database.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;

/**
 * A singleton wrapping the database handler class for the plugin.
 */
@Singleton
public class DatabaseHolder {
    private static DatabaseHolder instance; // The global instance of database holder

    /**
     * Private constructor as this should only be instantiated through {@link #getInstance()}.
     */
    private DatabaseHolder() {
    }

    /**
     * Get the singleton instance or create it, if doesn't already exist.
     *
     * @return the {@link DatabaseHolder} instance
     */
    @NotNull
    public static DatabaseHolder getInstance() {
        if (instance == null)
            instance = new DatabaseHolder();

        return instance;
    }

    private DatabaseHandler databaseHandler = null;

    /**
     * Get the {@link DatabaseHandler} instance for the plugin.
     *
     * @return the {@link DatabaseHandler} instance
     * @throws IllegalStateException thrown if there is no instance
     */
    public DatabaseHandler getDatabaseHandler() {
        if (databaseHandler == null)
            throw new IllegalStateException("Tried to access non-existent database handler!");

        return databaseHandler;
    }

    /**
     * Set the {@link DatabaseHandler} instance for this plugin
     *
     * @param databaseHandler the {@link DatabaseHandler} instance
     */
    @SuppressWarnings("UnusedAssignment")
    public void setDatabaseHandler(@Nullable DatabaseHandler databaseHandler) {
        // If a database handler already exists and is running then stop it
        if (this.databaseHandler != null && this.databaseHandler.isReady()) {
            getDatabaseHandler().shutdown();
            this.databaseHandler = null;
        }

        this.databaseHandler = databaseHandler;
    }
}
