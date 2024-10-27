package io.github.exampleuser.exampleplugin.database.handler;

import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import io.github.exampleuser.exampleplugin.database.config.DatabaseConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;

/**
 * The type Database handler builder.
 */
public class DatabaseHandlerBuilder {
    private ConfigHandler configHandler;
    private Logger logger;
    private DatabaseConfig databaseConfig;

    /**
     * With config handler database handler builder.
     *
     * @param configHandler the config handler
     * @return the database handler builder
     */
    public DatabaseHandlerBuilder withConfigHandler(@NotNull ConfigHandler configHandler) {
        this.configHandler = configHandler;
        return this;
    }

    /**
     * With logger database handler builder.
     *
     * @param logger the logger
     * @return the database handler builder
     */
    public DatabaseHandlerBuilder withLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * With database config database handler builder.
     *
     * @param databaseConfig the database config
     * @return the database handler builder
     */
    @TestOnly
    public DatabaseHandlerBuilder withDatabaseConfig(@NotNull DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
        return this;
    }

    /**
     * Build database handler.
     *
     * @return the database handler
     */
    public DatabaseHandler build() {
        if (configHandler == null && databaseConfig == null)
            throw new RuntimeException("Failed to build database handler as configHandler and databaseConfig are null!");

        if (configHandler != null && logger != null)
            return new DatabaseHandler(configHandler, logger);

        if (databaseConfig != null && logger != null)
            return new DatabaseHandler(databaseConfig, logger);

        throw new RuntimeException("Failed to build database handler!");
    }
}