package io.github.ExampleUser.ExamplePlugin.db.flyway;

import io.github.ExampleUser.ExamplePlugin.db.DatabaseType;
import io.github.ExampleUser.ExamplePlugin.db.flyway.migration.V3__Example;
import com.github.milkdrinkers.Crate.Config;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.ClassProvider;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.migration.JavaMigration;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Handles Flyway migrations.
 */
public class DatabaseMigrationHandler {
    // List of Java migrations
    private final List<Class<? extends JavaMigration>> migrations = List.of(
        V3__Example.class
    );

    private final Config config;
    private final DataSource dataSource;
    private final DatabaseType databaseType;
    private Flyway flyway;

    /**
     * Instantiates a new Database migration handler.
     *
     * @param config       the config
     * @param dataSource   the data source
     * @param databaseType the database type
     */
    public DatabaseMigrationHandler(Config config, DataSource dataSource, DatabaseType databaseType) {
        this.config = config;
        this.dataSource = dataSource;
        this.databaseType = databaseType;
        initializeFlywayInstance();
    }

    private void initializeFlywayInstance() {
        final ClassProvider<JavaMigration> javaMigrationClassProvider = new FlywayMigrationsProvider(migrations);
        final String SQL_TABLE_PREFIX = config.getOrDefault("db.prefix", "example_");
        final Map<String, String> SQL_PLACEHOLDERS = Map.of(
            "tablePrefix", SQL_TABLE_PREFIX,
            "columnSuffix", databaseType.getColumnSuffix(),
            "tableDefaults", databaseType.getTableDefaults(),
            "uuidType", databaseType.getUuidType(),
            "inetType", databaseType.getInetType(),
            "binaryType", databaseType.getBinaryType(),
            "alterViewStatement", databaseType.getAlterViewStatement()
        );

        this.flyway = Flyway
            .configure(getClass().getClassLoader())
            .baselineOnMigrate(true)
            .baselineVersion("0.0")
            .validateMigrationNaming(true)
            .javaMigrationClassProvider(javaMigrationClassProvider)
            .dataSource(dataSource)
            .locations(
                "classpath:database-migrations",
                "db/migration"
            )
            .table(SQL_TABLE_PREFIX + "schema_history") // Configure tables and migrations
            .placeholders(SQL_PLACEHOLDERS)
            .load();
    }

    /**
     * Execute Flyway migration. All pending migrations will be applied in order.
     *
     * @throws DatabaseMigrationException database migration exception
     */
    public void migrate() throws DatabaseMigrationException {
        try {
            this.flyway.migrate();
        } catch (FlywayException e) {
            throw new DatabaseMigrationException(e);
        }
    }

    /**
     * Executes Flyway repair. Repairs the Flyway schema history table. This will perform the following actions:
     *
     * <ul>
     * <li>Remove any failed migrations on databases without DDL transactions (User objects left behind must still be cleaned up manually)</li>
     * <li>Realign the checksums, descriptions and types of the applied migration</li>
     * </ul>
     *
     * @throws DatabaseMigrationException database migration exception
     */
    public void repair() throws DatabaseMigrationException {
        try {
            if (config.getOrDefault("db.repair", false))
                this.flyway.repair();
        } catch (FlywayException e) {
            throw new DatabaseMigrationException(e);
        }
    }
}
