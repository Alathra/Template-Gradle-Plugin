package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseType;

@SuppressWarnings("unused")
public record DatabaseTestParams(String jdbcPrefix, DatabaseType requiredDatabaseType, String tablePrefix) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String jdbcPrefix;
        private DatabaseType requiredDatabaseType;
        private String tablePrefix;

        private Builder() {}

        public Builder withJdbcPrefix(String jdbcPrefix) {
            this.jdbcPrefix = jdbcPrefix;
            return this;
        }

        public Builder withRequiredDatabaseType(DatabaseType requiredDatabaseType) {
            this.requiredDatabaseType = requiredDatabaseType;
            return this;
        }

        public Builder withTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
            return this;
        }

        public DatabaseTestParams build() {
            return new DatabaseTestParams(jdbcPrefix, requiredDatabaseType, tablePrefix);
        }
    }
}
