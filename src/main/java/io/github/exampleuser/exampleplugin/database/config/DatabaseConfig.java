package io.github.exampleuser.exampleplugin.database.config;

import com.github.milkdrinkers.crate.Config;
import io.github.exampleuser.exampleplugin.database.DatabaseType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * The type Database config.
 */
public class DatabaseConfig {
    // Essential details
    private final DatabaseType databaseType;
    private String tablePrefix;

    // Connection details
    private final @Nullable Path path;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private final boolean repair;

    // Pool options
    private final int maxPoolSize;
    private final int minIdle;
    private final long maxLifeTime;
    private final long keepAliveTime;
    private final long connectionTimeout;

    // JDBC properties
    private final Map<String, Object> connectionProperties;

    /**
     * Instantiates a new Database config.
     *
     * @param databaseType         the database type
     * @param tablePrefix          the table prefix
     * @param path                 the path
     * @param host                 the host
     * @param port                 the port
     * @param database             the database
     * @param username             the username
     * @param password             the password
     * @param repair               the repair
     * @param maxPoolSize          the max pool size
     * @param minIdle              the min idle
     * @param maxLifeTime          the max life time
     * @param keepAliveTime        the keep alive time
     * @param connectionTimeout    the connection timeout
     * @param connectionProperties the connection properties
     */
     DatabaseConfig(
        DatabaseType databaseType,
        String tablePrefix,
        @Nullable Path path,
        String host,
        int port,
        String database,
        String username,
        String password,
        boolean repair,
        int maxPoolSize,
        int minIdle,
        long maxLifeTime,
        long keepAliveTime,
        long connectionTimeout,
        Map<String, Object> connectionProperties
    ) {
        this.databaseType = databaseType;
        this.tablePrefix = tablePrefix;
        this.path = path;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.repair = repair;
        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
        this.maxLifeTime = maxLifeTime;
        this.keepAliveTime = keepAliveTime;
        this.connectionTimeout = connectionTimeout;
        this.connectionProperties = connectionProperties;
    }

    /**
     * Gets database type.
     *
     * @return the database type
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }

    /**
     * Sets table prefix.
     *
     * @param tablePrefix the table prefix
     */
    @TestOnly
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    /**
     * Gets table prefix.
     *
     * @return the table prefix
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets database.
     *
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Is repair boolean.
     *
     * @return the boolean
     */
    public boolean isRepair() {
        return repair;
    }

    /**
     * Gets max pool size.
     *
     * @return the max pool size
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Gets min idle.
     *
     * @return the min idle
     */
    public int getMinIdle() {
        return minIdle;
    }

    /**
     * Gets max life time.
     *
     * @return the max life time
     */
    public long getMaxLifeTime() {
        return maxLifeTime;
    }

    /**
     * Gets keep alive time.
     *
     * @return the keep alive time
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Gets connection timeout.
     *
     * @return the connection timeout
     */
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Gets connection properties.
     *
     * @return the connection properties
     */
    public String getConnectionProperties() {
        return getDatabaseType().formatJdbcConnectionProperties(connectionProperties).isEmpty() ? "" : getDatabaseType().getJdbcPropertySeparator() + getDatabaseType().formatJdbcConnectionProperties(connectionProperties);
    }

    /**
     * Gets database config from file.
     *
     * @param cfg the cfg
     * @return the database config from file
     */
    public static DatabaseConfig getDatabaseConfigFromFile(Config cfg) {
        String databaseType = cfg.getString("database.type");
        String tablePrefix = cfg.getString("database.table-prefix");

        String host = cfg.getString("database.host");
        Integer port = cfg.getInt("database.port");
        String database = cfg.getString("database.database");
        String username = cfg.getString("database.username");
        String password = cfg.getString("database.password");

        boolean repair = cfg.getBoolean("database.advanced.repair");

        Integer maxPoolSize = cfg.getInt("database.advanced.connection-pool.max-pool-size");
        Integer minIdle = cfg.getInt("database.advanced.connection-pool.min-idle");
        Long maxLifeTime = cfg.getLong("database.advanced.connection-pool.max-lifetime");
        Long keepAliveTime = cfg.getLong("database.advanced.connection-pool.keepalive-time");
        Long connectionTimeout = cfg.getLong("database.advanced.connection-pool.connection-timeout");

        Map<String, Object> connectionProperties = cfg.getMapParameterized("database.advanced.connection-properties");

        return new DatabaseConfigBuilder()
            .withDatabaseType(databaseType)
            .withTablePrefix(tablePrefix)
            .withPath(Path.of(cfg.getFilePath()).getParent())
            .withHost(host)
            .withPort(port)
            .withDatabase(database)
            .withUsername(username)
            .withPassword(password)
            .withRepair(repair)
            .withMaxPoolSize(maxPoolSize)
            .withMinIdle(minIdle)
            .withMaxLifeTime(maxLifeTime)
            .withKeepAliveTime(keepAliveTime)
            .withConnectionTimeout(connectionTimeout)
            .withConnectionProperties(connectionProperties)
            .build();
    }
}
