package io.github.exampleuser.exampleplugin.database.migration;

import io.github.exampleuser.exampleplugin.database.config.DatabaseConfig;
import io.github.exampleuser.exampleplugin.database.exception.DatabaseMigrationException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Handles Flyway migrations.
 */
@SuppressWarnings({"UnusedReturnValue"})
public final class MigrationHandler {

    private final DataSource dataSource;
    private final DatabaseConfig databaseConfig;
    private Flyway flyway;

    /**
     * Instantiates a new Database migration handler.
     *
     * @param dataSource     the data source
     * @param databaseConfig the database config
     */
    public MigrationHandler(DataSource dataSource, DatabaseConfig databaseConfig) {
        this.dataSource = dataSource;
        this.databaseConfig = databaseConfig;
        initializeFlywayInstance();
    }

    private void initializeFlywayInstance() {
        final String packagePath = getClass().getPackageName().replace('.', '/');
        final Map<String, String> SQL_PLACEHOLDERS = Map.of(
            "tablePrefix", databaseConfig.getTablePrefix()
        );

        this.flyway = Flyway
            .configure(getClass().getClassLoader())
            .loggers("slf4j")
            .baselineOnMigrate(true)
            .baselineVersion("0.0")
            .validateMigrationNaming(true)
            .dataSource(dataSource)
            .locations(
                "classpath:%s/migrations".formatted(packagePath),
                "db/migration/%s".formatted(databaseConfig.getDatabaseType().getJdbcPrefix())
            )
            .table(databaseConfig.getTablePrefix() + "schema_history") // Configure tables and migrations
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
            if (databaseConfig.isRepair())
                this.flyway.repair();
        } catch (FlywayException e) {
            throw new DatabaseMigrationException(e);
        }
    }
}
