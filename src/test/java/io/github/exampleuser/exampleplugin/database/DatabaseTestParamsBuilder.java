package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseType;

class DatabaseTestParamsBuilder {
    private String jdbcPrefix;
    private DatabaseType requiredDatabaseType;
    private String tablePrefix;

    public DatabaseTestParamsBuilder withJdbcPrefix(String jdbcPrefix) {
        this.jdbcPrefix = jdbcPrefix;
        return this;
    }

    public DatabaseTestParamsBuilder withRequiredDatabaseType(DatabaseType requiredDatabaseType) {
        this.requiredDatabaseType = requiredDatabaseType;
        return this;
    }

    public DatabaseTestParamsBuilder withTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    public DatabaseTestParams build() {
        return new DatabaseTestParams(jdbcPrefix, requiredDatabaseType, tablePrefix);
    }
}