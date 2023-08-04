package com.github.ExampleUser.ExamplePlugin.db;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.Reloadable;
import com.github.ExampleUser.ExamplePlugin.utility.Cfg;
import com.github.ExampleUser.ExamplePlugin.utility.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A class that sets up a HikariCP connection pool & provides access to it.
 */
@Singleton
public class DBHandler implements Reloadable {
    private final Main main;
    private HikariDataSource hikariDataSource;

    /**
     * Instantiates a new Data handler.
     *
     * @param main the plugin instance
     */
    public DBHandler(Main main) {
        this.main = main;
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad() {
        openConnection();
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
        closeDatabaseConnection();
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
            throw new SQLException("[DB] Unable to get a connection from the pool. (HikariDataSource returned null)");

        Connection connection = hikariDataSource.getConnection();
        if (connection == null)
            throw new SQLException("[DB] Unable to get a connection from the pool. (HikariDataSource#getConnection returned null)");

        return connection;
    }

    /**
     * Creates a connection pool using HikariCP, connected to a MariaDB server or using SQLite for storage.
     */
    private void openConnection() {
        if (hikariDataSource != null) {
            Logger.get().warn("[DB] Attempted to create a new database pool while running!");
            return;
        }

        HikariConfig hikariConfig = new HikariConfig();

        if (Cfg.get().get("mysql.enabled", false)) {
            // MariaDB connection
            hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
            hikariConfig.addDataSourceProperty("url", "jdbc:mariadb://%s/%s".formatted(
                Cfg.get().get("mysql.host", "127.0.0.1:3306"),
                Cfg.get().get("mysql.database", "database")
            ));
            hikariConfig.setUsername(Cfg.get().get("mysql.user", "root"));
            hikariConfig.setPassword(Cfg.get().get("mysql.pass", ""));
            hikariConfig.setConnectionTimeout(5000);
            hikariConfig.setKeepaliveTime(0);
        } else {
            // SQLite connection
            File dataFolder = new File(main.getDataFolder(), "database.db");

            if (!dataFolder.exists()) {
                try {
                    if (!dataFolder.createNewFile())
                        Logger.get().error("[DB] File write error: database.db (1)");
                } catch (IOException e) {
                    Logger.get().error("[DB] File write error: database.db (2)", e);
                    main.getServer().getPluginManager().disablePlugin(main);
                    return;
                }
            }

            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dataFolder);
            hikariConfig.setConnectionInitSql("PRAGMA journal_mode=WAL; PRAGMA busy_timeout=30000");
            hikariConfig.setConnectionTimeout(30000);
        }

        hikariConfig.setPoolName("PLUGINNAMES-hikari");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setInitializationFailTimeout(-1); // We try to create tables after this anyways which will error if no connection

        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Closes the connection pool.
     */
    private void closeDatabaseConnection() {
        Logger.get().info("[DB] Closing database connection...");

        if (hikariDataSource == null) {
            Logger.get().error("[DB] Skipped closing database connection because the data source is null. Was there a previous error which needs to be fixed? Check your console logs!");
            return;
        }

        if (hikariDataSource.isClosed()) {
            Logger.get().info("[DB] Skipped closing database connection: connection is already closed.");
            return;
        }

        try {
            hikariDataSource.close();
            hikariDataSource = null;
        } catch (Exception e) {
            Logger.get().error("[DB] Error closing database connections:", e);
            return;
        }

        Logger.get().info("[DB] Closed database connection.");
    }
}
