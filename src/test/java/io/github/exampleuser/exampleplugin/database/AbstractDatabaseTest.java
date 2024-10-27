package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.config.DatabaseConfig;
import io.github.exampleuser.exampleplugin.database.exception.DatabaseInitializationException;
import io.github.exampleuser.exampleplugin.utility.DB;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all test cases.
 */
@Tag("database")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractDatabaseTest {
    private final DatabaseTestParams testConfig;
    public DatabaseConfig databaseConfig;
    public Logger logger = LoggerFactory.getLogger("Database Test Logger");

    AbstractDatabaseTest(DatabaseTestParams testConfig) {
        this.testConfig = testConfig;
    }

    /**
     * Exposes the database parameters of this test.
     * @return the database test config
     */
    public DatabaseTestParams getTestConfig() {
        return testConfig;
    }

    @BeforeEach
    void beforeEachTest() {
    }

    @AfterEach
    void afterEachTest() {
    }

    @AfterAll
    void afterAllTests() {
        DB.getHandler().shutdown(); // Shut down the connection pool after all tests have been run
    }

    @Test
    @Order(1) // This forces migrations to be run before any other queries are tested (User queries won't work if the migrations failed)
    @DisplayName("Flyway migrations")
    void testMigrations() throws DatabaseInitializationException {
        DB.getHandler().migrate();
    }

    @Test
    @DisplayName("Insert query")
    void testQueryInsert() {
        DatabaseQueries.addEntry();
    }

    @Test
    @DisplayName("Batch execute queries")
    void testQueryBatch() {
        DatabaseQueries.saveAll();
    }

    @Test
    @DisplayName("Transaction execute queries")
    void testQueryTransaction() {
        DatabaseQueries.saveAllTransaction();
    }

    @Test
    @DisplayName("Select")
    void testQuerySelect() {
        DatabaseQueries.loadAll();
    }
}
