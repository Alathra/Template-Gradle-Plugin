package io.github.ExampleUser.ExamplePlugin.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.jetbrains.annotations.Nullable;
import org.jooq.SQLDialect;
import org.mariadb.jdbc.MariaDbDataSource;

import java.util.List;
import java.util.Map;

/**
 * Enum containing information for handling different Database types.
 */
public enum DatabaseType {
    /**
     * HSQLDB database type.
     */
    HSQLDB("HSQLDB", org.hsqldb.jdbcDriver.class.getName(), JDBCDataSource.class.getName(), "hsqldb", ';', ';'),
    /**
     * H2 Database Engine database type.
     */
    H2("H2", org.h2.Driver.class.getName(), JdbcDataSource.class.getName(), "h2", ';', ';'),
    /**
     * MySQL database type.
     */
    MYSQL("MySQL", com.mysql.jdbc.Driver.class.getName(), MysqlDataSource.class.getName(), "mysql", '?', '&'),
    /**
     * MariaDB database type.
     */
    MARIADB("MariaDB", org.mariadb.jdbc.Driver.class.getName(), MariaDbDataSource.class.getName(), "mariadb", '?', '&'),
	;

    private final String driverName;
    private final String driverClassName;
	private final String dataSourceClassName;
	private final String jdbcPrefix;
	private final char jdbcPropertyPrefix;
	private final char jdbcPropertySeparator;

	DatabaseType(String driverName, String driverClassName, String dataSourceClassName, String jdbcPrefix, char jdbcPropertyPrefix, char jdbcPropertySeparator) {
		this.driverName = driverName;
		this.driverClassName = driverClassName;
		this.dataSourceClassName = dataSourceClassName;
        this.jdbcPrefix = jdbcPrefix;
		this.jdbcPropertyPrefix = jdbcPropertyPrefix;
		this.jdbcPropertySeparator = jdbcPropertySeparator;
	}

    /**
     * Gets driver name.
     *
     * @return the driver name
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Gets driver class name.
     *
     * @return the driver class name
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * Gets data source class name.
     *
     * @return the data source class name
     */
    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    /**
     * Gets the jdbc prefix.
     *
     * @return the jdbc prefix like <code>mysql</code> or <code>mariadb</code>
     */
    public String getJdbcPrefix() {
        return jdbcPrefix;
    }

    /**
     * Gets the jdbc property prefix.
     *
     * @return the jdbc property prefix like <code>{@literal ;}</code> or <code>{@literal ?}</code>
     */
    public char getJdbcPropertyPrefix() {
        return jdbcPropertyPrefix;
    }

    /**
     * Gets the jdbc property separator.
     *
     * @return the jdbc property separator like <code>{@literal ;}</code> or <code>{@literal &}</code>
     */
    public char getJdbcPropertySeparator() {
        return jdbcPropertySeparator;
    }

    /**
     * Format a map of connection properties into a valid jdbc connection properties string.
     *
     * @param properties the properties
     * @return the string
     */
    public String formatJdbcConnectionProperties(Map<String, Object> properties) {
		if (properties.isEmpty()) return "";

        List<String> connectionProperties = properties.entrySet().stream()
            .map(map -> "%s=%s".formatted(map.getKey(), map.getValue()))
            .toList();

		return jdbcPropertyPrefix + String.join(Character.toString(jdbcPropertySeparator), connectionProperties);
	}


    /**
     * Gets database type from jdbc prefix.
     *
     * @param prefix the prefix
     * @return the database type or null
     */
    @Nullable
    public static DatabaseType getDatabaseTypeFromJdbcPrefix(String prefix) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.equals(prefix.toLowerCase())) {
                return type;
            };
        }

        return null;
    }

    @Override
    public String toString() {
        return getDriverName();
    }

    /**
     * Check if this DatabaseType is equal to another.
     *
     * @param DatabaseType the database type
     * @return boolean
     */
    public boolean equals(DatabaseType DatabaseType) {
        return this.equals(DatabaseType.getDriverName());
    }

    /**
     * Check if this DatabaseType is equal to another.
     *
     * @param databaseTypeName the database type name
     * @return boolean
     */
    public boolean equals(String databaseTypeName) {
        return this.getDriverName().equalsIgnoreCase(databaseTypeName);
    }

    /**
     * Gets sql dialect for this DatabaseType.
     *
     * @return the sql dialect
     */
    public SQLDialect getSQLDialect() {
        return switch (this) {
            case HSQLDB -> SQLDialect.HSQLDB;
            case H2 -> SQLDialect.H2;
            case MYSQL -> SQLDialect.MYSQL;
            case MARIADB -> SQLDialect.MARIADB;
        };
    }

    /**
     * Gets column suffix for this DatabaseType.
     *
     * @return the column suffix
     */
    public String getColumnSuffix() {
        return switch (this) {
            case HSQLDB -> "";
            case H2, MARIADB, MYSQL -> " VIRTUAL";
        };
    }

    /**
     * Gets table defaults for this DatabaseType.
     *
     * @return the table defaults
     */
    public String getTableDefaults() {
        return switch (this) {
            case H2, HSQLDB -> "";
            case MARIADB, MYSQL -> " CHARACTER SET utf8mb4 COLLATE utf8mb4_bin";
        };
    }

    /**
     * Gets uuid type for this DatabaseType.
     *
     * @return the uuid type
     */
    public String getUuidType() {
        return switch (this) {
            case HSQLDB -> "UUID";
            case H2, MARIADB, MYSQL -> "BINARY(16)";
        };
    }

    /**
     * Gets inet type for this DatabaseType.
     *
     * @return the inet type
     */
    public String getInetType() {
        return "VARBINARY(16)";
    }

    /**
     * Gets binary type for this DatabaseType.
     *
     * @return the binary type
     */
    public String getBinaryType() {
        return "BLOB";
    }

    /**
     * Gets alter view statement for this DatabaseType.
     *
     * @return the alter view statement
     */
    public String getAlterViewStatement() {
        return switch (this) {
            case H2, HSQLDB, MYSQL, MARIADB -> "ALTER VIEW";
        };
    }

    /**
     * Gets connection init sql for this DatabaseType.
     *
     * @return the connection init sql
     */
    public String getConnectionInitSql() {
        return switch (this) {
            case HSQLDB -> "SET DATABASE TRANSACTION CONTROL MVLOCKS;";
            case MARIADB -> "SET NAMES utf8mb4 COLLATE utf8mb4_bin; " + setSqlModes(
                // MariaDB defaults
                "STRICT_TRANS_TABLES",
                "ERROR_FOR_DIVISION_BY_ZERO",
                "NO_AUTO_CREATE_USER",
                "NO_ENGINE_SUBSTITUTION",
                // ANSI SQL Compliance
                "ANSI",
                "NO_BACKSLASH_ESCAPES",
                "SIMULTANEOUS_ASSIGNMENT", // MDEV-13417
                "NO_ZERO_IN_DATE",
                "NO_ZERO_DATE");
            case H2 -> "";
            case MYSQL -> "SET NAMES utf8mb4 COLLATE utf8mb4_bin; " + setSqlModes(
                // MySQL defaults
                "STRICT_TRANS_TABLES",
                "ERROR_FOR_DIVISION_BY_ZERO",
                "NO_ENGINE_SUBSTITUTION",
                // ANSI SQL Compliance
                "ANSI",
                "NO_BACKSLASH_ESCAPES",
                "NO_ZERO_IN_DATE",
                "NO_ZERO_DATE");
        };
    }

    /**
     * Generates a concatenated SQL query from the specified parameters.
     *
     * @param sqlModes database parameters
     * @return string
     */
    private static String setSqlModes(String... sqlModes) {
        final String modes = String.join(",", sqlModes);
        return "SET @@SQL_MODE = CONCAT(@@SQL_MODE, '," + modes + "')";
    }
}