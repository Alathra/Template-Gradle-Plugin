package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandler;
import io.github.exampleuser.exampleplugin.database.config.DatabaseConfigBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@Tag("embeddeddatabase")
public abstract class AbstractEmbeddedDatabaseTest extends AbstractDatabaseTest {
    static @TempDir Path TEMP_DIR;

    public AbstractEmbeddedDatabaseTest(String jdbcPrefix, DatabaseType requiredDatabaseType) {
        super(jdbcPrefix, requiredDatabaseType);
    }

    @BeforeAll
    @DisplayName("Initialize connection pool")
    void beforeAllTests() {
        databaseConfig = new DatabaseConfigBuilder()
            .withDatabaseType(jdbcPrefix)
            .withPath(TEMP_DIR)
            .build();
        Assertions.assertEquals(requiredDatabaseType, databaseConfig.getDatabaseType());

        databaseHandler = new DatabaseHandler(databaseConfig, logger);
        databaseHandler.startup();
    }
}
