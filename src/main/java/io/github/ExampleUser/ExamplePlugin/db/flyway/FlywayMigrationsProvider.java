package io.github.ExampleUser.ExamplePlugin.db.flyway;

import org.flywaydb.core.api.ClassProvider;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.Collection;
import java.util.List;

/**
 * Flyway migrations provider. Convenience/utility record for using Java migrations with Flyway.
 */
public record FlywayMigrationsProvider(
    List<Class<? extends JavaMigration>> migrations
) implements ClassProvider<JavaMigration> {
    /**
     * Instantiates a new Flyway migrations provider.
     *
     * @param migrations the migrations
     */
    public FlywayMigrationsProvider(List<Class<? extends JavaMigration>> migrations) {
        this.migrations = List.copyOf(migrations);
    }

    @Override
    public Collection<Class<? extends JavaMigration>> getClasses() {
        return migrations;
    }
}
