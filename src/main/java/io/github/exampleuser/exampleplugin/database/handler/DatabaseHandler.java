package io.github.exampleuser.exampleplugin.database.handler;

import com.zaxxer.hikari.HikariDataSource;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import io.github.exampleuser.exampleplugin.database.config.DatabaseConfig;
import io.github.exampleuser.exampleplugin.database.exception.DatabaseInitializationException;
import io.github.exampleuser.exampleplugin.database.exception.DatabaseMigrationException;
import io.github.exampleuser.exampleplugin.database.jooq.JooqContext;
import io.github.exampleuser.exampleplugin.database.migration.MigrationHandler;
import io.github.exampleuser.exampleplugin.database.pool.ConnectionPoolFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Class that handles HikariCP connection pool, jOOQ and Flyway migrations.
 */
public class DatabaseHandler implements Reloadable {
    private JooqContext jooqContext;
    private HikariDataSource connectionPool;
    private @Nullable DatabaseConfig databaseConfig = null;
    private @Nullable ConfigHandler configHandler = null;
    private final Logger logger;
    private boolean isReady = false; // If the connection pool is connected and working, and Flyway migrations executed without any errors during startup.

    /**
     * Instantiates a new Database handler.
     *
     * @param configHandler the config handler
     * @param logger        the logger
     */
    DatabaseHandler(@NotNull ConfigHandler configHandler, Logger logger) {
        this.configHandler = configHandler;
        this.logger = logger;
    }

    /**
     * Instantiates a new Database handler.
     *
     * @param databaseConfig the database config
     * @param logger         the logger
     */
    @TestOnly
    DatabaseHandler(@NotNull DatabaseConfig databaseConfig, Logger logger) {
        this.databaseConfig = databaseConfig;
        this.logger = logger;
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad(ExamplePlugin plugin) {
        try {
            // Load database config from file, or use provided databaseConfig from constructor
            if (configHandler != null)
                this.databaseConfig = DatabaseConfig.getDatabaseConfigFromFile(configHandler.getDatabaseConfig());

            // Start connection pool
            startup();
            migrate();
        } catch (DatabaseInitializationException e) {
            logger.error("[DB] Database initialization error: {}", e.getMessage());
        } finally {
            if (!isReady()) {
                logger.warn("[DB] Error while initializing database. Functionality will be limited.");
            }
        }
    }

    /**
     * On plugin enable.
     */
    @Override
    public void onEnable(ExamplePlugin plugin) {
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable(ExamplePlugin plugin) {
        if (!isReady())
            return;

        try {
            shutdown();
        } catch (Exception e) {
            logger.error("[DB] Error while shutting down database:", e);
        }
    }

    /**
     * Returns if the database is setup and functioning properly.
     *
     * @return the boolean
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Gets database.
     *
     * @return the database
     */
    public DatabaseType getDB() {
        return getDatabaseConfig().getDatabaseType();
    }

    /**
     * Gets jooq context.
     *
     * @return the jooq context
     */
    public JooqContext getJooqContext() {
        return jooqContext;
    }

    /**
     * Gets connection pool.
     *
     * @return the connection pool
     */
    public HikariDataSource getConnectionPool() {
        return connectionPool;
    }

    /**
     * Gets database config.
     *
     * @return the database config
     */
    public DatabaseConfig getDatabaseConfig() {
        if (databaseConfig == null)
            throw new IllegalStateException("Database config is still null but was accessed in getDB!");

        return databaseConfig;
    }

    /**
     * Gets a connection from the connection pool.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        if (connectionPool == null)
            throw new SQLException("[DB] Unable to getConnection a connection from the pool. (connectionPool is null)");

        final Connection connection = connectionPool.getConnection();
        if (connection == null)
            throw new SQLException("[DB] Unable to getConnection a connection from the pool. (connectionPool#getConnection returned null)");

        return connection;
    }

    /**
     * Creates a connection pool using HikariCP and setups jOOQ DSLContext.
     * Should always be followed by running Flyway migrations with {@link #migrate()}.
     */
    public void startup() throws DatabaseInitializationException {
        if (isReady())
            return;

        if (databaseConfig == null)
            throw new DatabaseInitializationException("Attempted to start a database connection pool but database config is null!");

        if (connectionPool != null)
            throw new DatabaseInitializationException("Attempted to create a new database connection pool while running! (connectionPool is not null)");

        // Initialize connection pool
        connectionPool = ConnectionPoolFactory.create(
            databaseConfig,
            logger
        );

        // Check if using invalid database type (Note, these cases throw in the previous method)
        try (Connection connection = connectionPool.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String vendorVersionName = databaseMetaData.getDatabaseProductVersion().toLowerCase();

            if (vendorVersionName.contains("mariadb") && getDB().equals(DatabaseType.MYSQL)) {
                throw new RuntimeException("Attempted to connect to a mariadb database using mysql as database type! (Please change the type to mariadb in database.yml)");
            } else if (vendorVersionName.contains("mysql") && getDB().equals(DatabaseType.MARIADB)) {
                throw new RuntimeException("Attempted to connect to a mysql database using mariadb as database type! (Please change the type to mysql in database.yml)");
            }
        } catch (Throwable t) {
            throw new DatabaseInitializationException(t.getMessage());
        }

        // Disable JOOQ nonsense
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        // Setup JOOQ
        jooqContext = new JooqContext(
            databaseConfig.getDatabaseType().getSQLDialect(),
            databaseConfig.getTablePrefix()
        );
    }

    /**
     * Closes the connection pool.
     */
    public void shutdown() {
        if (!isReady())
            return;

        logger.info("[DB] Shutting down database pool...");

        if (connectionPool == null) {
            logger.error("[DB] Skipped closing database pool because the connection pool is null. Was there a previous error which needs to be fixed? Check your logs!");
            return;
        }

        if (connectionPool.isClosed()) {
            logger.error("[DB] Skipped closing database pool: connection is already closed.");
            return;
        }

        connectionPool.close();
        connectionPool = null;
        isReady = false;

        logger.info("[DB] Closed database pool.");
    }

    /**
     * Execute database migrations with Flyway.
     * Should always be run following the database being started using {@link #startup()}.
     */
    public void migrate() throws DatabaseInitializationException {
        try {
            new MigrationHandler(
                connectionPool,
                databaseConfig
            )
                .migrate();

            // If we got here without errors the database is working correctly
            isReady = true;
        } catch (DatabaseMigrationException e) {
            throw new DatabaseInitializationException("Failed to migrate database schemas to new version! Please backup your database and report the issue.", e);
        }
    }
}
