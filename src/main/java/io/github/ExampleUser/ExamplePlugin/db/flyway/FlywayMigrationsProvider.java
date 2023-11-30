package io.github.ExampleUser.ExamplePlugin.db.flyway;

import org.flywaydb.core.api.ClassProvider;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.Collection;
import java.util.List;

public record FlywayMigrationsProvider(
    List<Class<? extends JavaMigration>> migrations
) implements ClassProvider<JavaMigration> {
    public FlywayMigrationsProvider(List<Class<? extends JavaMigration>> migrations) {
        this.migrations = List.copyOf(migrations);
    }

    @Override
    public Collection<Class<? extends JavaMigration>> getClasses() {
        return migrations;
    }
}
