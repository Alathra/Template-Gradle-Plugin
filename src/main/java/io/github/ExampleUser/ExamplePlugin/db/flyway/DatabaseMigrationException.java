package io.github.exampleuser.exampleplugin.db.flyway;

/**
 * Database migration exception is thrown whenever a Flyway migration fails.
 */
public class DatabaseMigrationException extends Exception {
    /**
     * Instantiates a new Database migration exception.
     *
     * @param t the throwable
     */
    public DatabaseMigrationException(Throwable t) {
        super(t);
    }
}
