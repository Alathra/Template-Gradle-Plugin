package io.github.ExampleUser.ExamplePlugin.db.flyway;

public class DatabaseMigrationException extends Exception {
    public DatabaseMigrationException(Throwable t) {
        super(t);
    }
}
