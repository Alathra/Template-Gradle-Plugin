package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandler;
import io.github.exampleuser.exampleplugin.database.config.DatabaseConfigBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("externaldatabase")
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractExternalDatabaseTest extends AbstractDatabaseTest {
    @Container
    public static GenericContainer<?> container;

    public AbstractExternalDatabaseTest(String jdbcPrefix, DatabaseType requiredDatabaseType, GenericContainer<?> container) {
        super(jdbcPrefix, requiredDatabaseType);
        AbstractExternalDatabaseTest.container = container;
        container.start();
    }

    @BeforeAll
    @DisplayName("Initialize connection pool")
    void beforeAllTests() {
        Assertions.assertTrue(container.isRunning());

        databaseConfig = new DatabaseConfigBuilder()
            .withDatabaseType(jdbcPrefix)
            .withDatabase("testing")
            .withHost(container.getHost())
            .withPort(container.getFirstMappedPort())
            .withUsername("root")
            .withPassword("")
            .build();
        Assertions.assertEquals(requiredDatabaseType, databaseConfig.getDatabaseType());

        databaseHandler = new DatabaseHandler(databaseConfig, logger);
        databaseHandler.startup();
    }

    @AfterAll
    void afterAllTests() {
        container.stop();
    }
}
