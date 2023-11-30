package io.github.ExampleUser.ExamplePlugin.db;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.Reloadable;
import io.github.ExampleUser.ExamplePlugin.db.flyway.DatabaseMigrationException;
import io.github.ExampleUser.ExamplePlugin.db.flyway.DatabaseMigrationHandler;
import io.github.ExampleUser.ExamplePlugin.db.jooq.JooqContext;
import io.github.ExampleUser.ExamplePlugin.utility.Cfg;
import io.github.ExampleUser.ExamplePlugin.utility.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Class that handles HikariCP connection pool, jOOQ and Flyway migrations.
 */
@Singleton
public class DatabaseHandler implements Reloadable {
    private final ExamplePlugin plugin;
    private boolean isConnected = false;
    private HikariDataSource hikariDataSource;
    private DatabaseType database;
    private JooqContext jooqContext;

    /**
     * Instantiates a new Data handler.
     *
     * @param plugin the plugin instance
     */
    public DatabaseHandler(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad() {
        try {
            openConnection();
            isConnected = true;
        } catch (Exception e) {
            Logger.get().error("[DB] Database initialization error: ", e);
        } finally {
            if (!isConnected()) {
                Logger.get().warn("[DB] Failed to initialize database connection. Functionality will be limited.");
            }
        }
    }

    /**
     * On plugin enable.
     */
    @Override
    public void onEnable() {
    }

    /**
     * On plugin  disable.
     */
    @Override
    public void onDisable() {
        try {
            closeDatabaseConnection();
            isConnected = false;
        } catch (Exception e) {
            Logger.get().error("[DB] Error closing database connections:", e);
        }
    }

    /**
     * Returns if the database is setup and functioning properly.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Gets db.
     *
     * @return the db
     */
    public DatabaseType getDB() {
        return database;
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
     * Gets a connection from the connection pool.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        if (hikariDataSource == null)
            throw new SQLException("[DB] Unable to getConnection a connection from the pool. (HikariDataSource returned null)");

        final Connection connection = hikariDataSource.getConnection();
        if (connection == null)
            throw new SQLException("[DB] Unable to getConnection a connection from the pool. (HikariDataSource#getConnection returned null)");

        return connection;
    }

    /**
     * Creates a connection pool using HikariCP and executes Flyway migrations.
     */
    private void openConnection() {
        if (hikariDataSource != null) {
            Logger.get().warn("[DB] Attempted to create a new database pool while running!");
            return;
        }

        final HikariConfig hikariConfig = new HikariConfig();
        final String vendorName = Cfg.get().get("db.type", DatabaseType.HSQLDB.getDriverName());
        final DatabaseType db = DatabaseType.getDatabaseTypeFromJdbcPrefix(vendorName);

        if (db == null) {
            Logger.get().warn("[DB] Invalid database type was specified!");
            return;
        }

        final String host = Cfg.get().get("db.host", "127.0.0.1");
        final String port = Cfg.get().get("db.port", "3306");
        final String database = Cfg.get().get("db.database", "database");
        final String username = Cfg.get().get("db.user", "root");
        final String password = Cfg.get().get("db.pass", "");

        final String connectionProperties = switch (db) {
            case H2 -> DatabaseType.H2.formatJdbcConnectionProperties(
                Map.of(
                    "AUTO_SERVER", "TRUE",
                    "MODE", "MySQL",  // MySQL support mode
                    "CASE_INSENSITIVE_IDENTIFIERS", "TRUE",
                    "IGNORECASE", "TRUE"
                )
            );
            case HSQLDB -> DatabaseType.HSQLDB.formatJdbcConnectionProperties(
                Map.of(
                    "sql.syntax_mys", "true", // MySQL support mode
                    // Prevent execution of multiple queries in one Statement
                    "sql.restrict_exec", true,
                    // Make the names of generated indexes the same as the names of the constraints
                    "sql.sys_index_names", true,
                    /*
                     * Enforce SQL standards on
                     * 1.) table and column names
                     * 2.) ambiguous column references
                     * 3.) illegal type conversions
                     */
                    "sql.enforce_names", true,
                    "sql.enforce_refs", true,
                    "sql.enforce_types", true,
                    // Respect interrupt status during query execution
                    "hsqldb.tx_interrupt_rollback", true,
                    // Use CACHED tables by default
                    "hsqldb.default_table_type", "cached",
                    // Needed for use with connection init-SQL (hikariConf.setConnectionInitSql)
                    "allowMultiQueries", true,
                    // Help debug in case of exceptions
                    "dumpQueriesOnException", true
                )
            );
            case MARIADB -> DatabaseType.MARIADB.formatJdbcConnectionProperties(
                Map.of(
                    // Performance improvements
                    "autocommit", false,
                    "defaultFetchSize", 1000,

                    // Help debug in case of deadlock
                    "includeInnodbStatusInDeadlockExceptions", true,
                    "includeThreadDumpInDeadlockExceptions", true,

                    // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery#mysql
                    "socketTimeout", 14000L,
                    // Needed for use with connection init-SQL (hikariConf.setConnectionInitSql)
                    "allowMultiQueries", true,
                    // Help debug in case of exceptions
                    "dumpQueriesOnException", true
                )
            );
            case MYSQL -> DatabaseType.MYSQL.formatJdbcConnectionProperties(
                Map.of(
                    // Performance improvements
                    "autocommit", false,
                    "defaultFetchSize", 1000,

                    // Help debug in case of deadlock
                    "includeInnodbStatusInDeadlockExceptions", true,
                    "includeThreadDumpInDeadlockExceptions", true,

                    // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery#mysql
                    "socketTimeout", 14000L,
                    // Needed for use with connection init-SQL (hikariConf.setConnectionInitSql)
                    "allowMultiQueries", true,
                    // Help debug in case of exceptions
                    "dumpQueriesOnException", true
                )
            );
        };

        switch (db) {
            case HSQLDB, H2 -> {
                final String subfolder = "data";
                final String fileName = "database";
                final String fileExtension = switch (db) {
                    case HSQLDB -> ".hsql";
                    case H2 -> ".mv.db";
                    default -> "";
                };
                final String fileNameWithExtension = fileName + fileExtension;

                // Set credentials for HSQL and H2
                hikariConfig.setUsername("SA");
                hikariConfig.setPassword("");

                hikariConfig.setDataSourceClassName(db.getDataSourceClassName());
                hikariConfig.addDataSourceProperty("url", "jdbc:%s:%s".formatted(
                    db.getJdbcPrefix(),
                    plugin.getDataFolder().getAbsolutePath() + File.separatorChar + subfolder + File.separatorChar + (db.equals(DatabaseType.H2) ? fileName : fileNameWithExtension) + connectionProperties
                ));
            }
            case MYSQL, MARIADB -> {
                hikariConfig.setDataSourceClassName(db.getDataSourceClassName());
                hikariConfig.addDataSourceProperty("url", "jdbc:%s://%s%s/%s".formatted(
                    db.getJdbcPrefix(),
                    host,
                    ":%s".formatted(port),
                    database + connectionProperties
                ));

                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);

                hikariConfig.setKeepaliveTime(0);
            }
        }

        hikariConfig.setConnectionTimeout(14000);
        hikariConfig.setMaxLifetime(25000000);
        hikariConfig.setInitializationFailTimeout(-1); // We try to create tables after this anyways which will error if no connection

        final int poolSize = Cfg.get().getOrDefault("db.poolsize", 10);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMinimumIdle(poolSize);

        hikariConfig.setPoolName("%s-hikari".formatted(ExamplePlugin.getInstance().getName()));
        hikariConfig.setAutoCommit(true);
        hikariConfig.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
        hikariConfig.setIsolateInternalQueries(true);
        hikariConfig.setConnectionInitSql(db.getConnectionInitSql());

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        this.database = db;
        this.jooqContext = new JooqContext(db.getSQLDialect());

        try {
            new DatabaseMigrationHandler(Cfg.get(), hikariDataSource, db)
                .migrate();
        } catch (DatabaseMigrationException e) {
            Logger.get().error("[DB] Failed to migrate database. Please backup your database and report the issue.", e);
        }
    }

    /**
     * Closes the connection pool.
     */
    private void closeDatabaseConnection() throws Exception {
        Logger.get().info("[DB] Closing database connection...");

        if (hikariDataSource == null) {
            Logger.get().error("[DB] Skipped closing database connection because the data source is null. Was there a previous error which needs to be fixed? Check your console logs!");
            return;
        }

        if (hikariDataSource.isClosed()) {
            Logger.get().info("[DB] Skipped closing database connection: connection is already closed.");
            return;
        }

        hikariDataSource.close();
        hikariDataSource = null;

        Logger.get().info("[DB] Closed database connection.");
    }
}
