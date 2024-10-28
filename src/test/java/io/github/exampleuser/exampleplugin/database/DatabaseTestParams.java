package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseType;

record DatabaseTestParams(String jdbcPrefix, DatabaseType requiredDatabaseType, String tablePrefix) {
}
