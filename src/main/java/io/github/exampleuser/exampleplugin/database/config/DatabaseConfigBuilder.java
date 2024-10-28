package io.github.exampleuser.exampleplugin.database.config;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Database config builder.
 */
public class DatabaseConfigBuilder {
    private @Nullable DatabaseType databaseType;
    private @Nullable String tablePrefix;

    private @Nullable Path path;
    private @Nullable String host;
    private @Nullable Integer port;
    private @Nullable String database;
    private @Nullable String username;
    private @Nullable String password;

    private @Nullable Boolean repair;

    private @Nullable Integer maxPoolSize;
    private @Nullable Integer minIdle;
    private @Nullable Long maxLifeTime;
    private @Nullable Long keepAliveTime;
    private @Nullable Long connectionTimeout;

    private @Nullable Map<String, Object> connectionProperties;

    /**
     * With database type database config builder.
     *
     * @param vendorPrefix the vendor prefix
     * @return the database config builder
     */
    public DatabaseConfigBuilder withDatabaseType(String vendorPrefix) {
        this.databaseType = DatabaseType.getDatabaseTypeFromJdbcPrefix(vendorPrefix.toLowerCase());
        return this;
    }

    /**
     * With table prefix database config builder.
     *
     * @param tablePrefix the table prefix
     * @return the database config builder
     */
    public DatabaseConfigBuilder withTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    /**
     * With path database config builder.
     *
     * @param path the path
     * @return the database config builder
     */
    public DatabaseConfigBuilder withPath(Path path) {
        this.path = path;
        return this;
    }

    /**
     * With host database config builder.
     *
     * @param host the host
     * @return the database config builder
     */
    public DatabaseConfigBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * With port database config builder.
     *
     * @param port the port
     * @return the database config builder
     */
    public DatabaseConfigBuilder withPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * With database database config builder.
     *
     * @param database the database
     * @return the database config builder
     */
    public DatabaseConfigBuilder withDatabase(String database) {
        this.database = database;
        return this;
    }

    /**
     * With username database config builder.
     *
     * @param username the username
     * @return the database config builder
     */
    public DatabaseConfigBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * With password database config builder.
     *
     * @param password the password
     * @return the database config builder
     */
    public DatabaseConfigBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * With repair database config builder.
     *
     * @param repair the repair
     * @return the database config builder
     */
    public DatabaseConfigBuilder withRepair(boolean repair) {
        this.repair = repair;
        return this;
    }

    /**
     * With max pool size database config builder.
     *
     * @param maxPoolSize the max pool size
     * @return the database config builder
     */
    public DatabaseConfigBuilder withMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * With min idle database config builder.
     *
     * @param minIdle the min idle
     * @return the database config builder
     */
    public DatabaseConfigBuilder withMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    /**
     * With max life time database config builder.
     *
     * @param maxLifeTime the max life time
     * @return the database config builder
     */
    public DatabaseConfigBuilder withMaxLifeTime(Long maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
        return this;
    }

    /**
     * With keep alive time database config builder.
     *
     * @param keepAliveTime the keep alive time
     * @return the database config builder
     */
    public DatabaseConfigBuilder withKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * With connection timeout database config builder.
     *
     * @param connectionTimeout the connection timeout
     * @return the database config builder
     */
    public DatabaseConfigBuilder withConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * With connection properties database config builder.
     *
     * @param connectionProperties the connection properties
     * @return the database config builder
     */
    public DatabaseConfigBuilder withConnectionProperties(Map<String, Object> connectionProperties) {
        this.connectionProperties = connectionProperties;
        return this;
    }

    /**
     * Build database config.
     *
     * @return the database config
     */
    public DatabaseConfig build() {
        if (databaseType == null)
            databaseType = DatabaseType.SQLITE;

        if (tablePrefix == null)
            tablePrefix = "";

        if (host == null)
            host = "localhost";

        if (port == null)
            port = 3306;

        if (database == null)
            database = "database_name";

        if (username == null)
            username = "root";

        if (password == null)
            password = "";

        if (repair == null)
            repair = false;

        if (maxPoolSize == null)
            maxPoolSize = 10;

        if (minIdle == null)
            minIdle = 10;

        if (maxLifeTime == null)
            maxLifeTime = 180_000L;

        if (keepAliveTime == null)
            keepAliveTime = 60_000L;

        if (connectionTimeout == null)
            connectionTimeout = 20_000L;

        if (connectionProperties == null) {
            connectionProperties = new HashMap<>();

            if (databaseType.equals(DatabaseType.MYSQL) || databaseType.equals(DatabaseType.MARIADB)) {
                connectionProperties.putIfAbsent("useSSL", "false");
                connectionProperties.putIfAbsent("cachePrepStmts", true);
                connectionProperties.putIfAbsent("prepStmtCacheSize", 250);
                connectionProperties.putIfAbsent("prepStmtCacheSqlLimit", 2048L);
                connectionProperties.putIfAbsent("useServerPrepStmts", true);
                connectionProperties.putIfAbsent("useLocalSessionState", true);
                connectionProperties.putIfAbsent("rewriteBatchedStatements", true);
                connectionProperties.putIfAbsent("cacheResultSetMetadata", true);
                connectionProperties.putIfAbsent("cacheServerConfiguration", true);
                connectionProperties.putIfAbsent("elideSetAutoCommits", true);
                connectionProperties.putIfAbsent("maintainTimeStats", false);
            }
        }

        return new DatabaseConfig(databaseType, tablePrefix, path, host, port, database, username, password, repair, maxPoolSize, minIdle, maxLifeTime, keepAliveTime, connectionTimeout, connectionProperties);
    }
}