package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandler;
import io.github.exampleuser.exampleplugin.database.config.DatabaseConfig;
import io.github.exampleuser.exampleplugin.database.exception.DatabaseMigrationException;
import io.github.exampleuser.exampleplugin.database.migration.MigrationHandler;
import io.github.exampleuser.exampleplugin.database.jooq.JooqContext;
import org.jooq.DSLContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.github.exampleuser.exampleplugin.database.schema.Tables.SOME_LIST;

@Tag("database")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractDatabaseTest {
    public String jdbcPrefix;
    public DatabaseType requiredDatabaseType;
    public DatabaseConfig databaseConfig;
    public DatabaseHandler databaseHandler;
    public Logger logger = LoggerFactory.getLogger("Database Test Logger");
    @SuppressWarnings("unused")
    static List<String> tablePrefixes = Arrays.asList("", "test_", "somelongprefix_");

    public AbstractDatabaseTest(String jdbcPrefix, DatabaseType requiredDatabaseType) {
        this.jdbcPrefix = jdbcPrefix;
        this.requiredDatabaseType = requiredDatabaseType;
    }

    @BeforeEach
    void beforeEachTest() {
    }

    @AfterEach
    void afterEachTest() {
    }

    @AfterAll
    void afterAllTests() {
        databaseHandler.shutdown();
    }

    // Shared tests

    @ParameterizedTest
    @FieldSource("tablePrefixes")
    @Order(1)
    @DisplayName("Flyway migrations")
    void testMigrations(String prefix) throws DatabaseMigrationException {
        databaseHandler.getDatabaseConfig().setTablePrefix(prefix);
        new MigrationHandler(
            databaseHandler.getConnectionPool(),
            databaseHandler.getDatabaseConfig()
        )
            .migrate();
    }

    @ParameterizedTest
    @FieldSource("tablePrefixes")
    @DisplayName("Select query")
    void testQuerySelect(String prefix) throws SQLException {
        databaseHandler.getDatabaseConfig().setTablePrefix(prefix);
        JooqContext jooqContext = new JooqContext(databaseHandler.getDatabaseConfig());

        Connection con = databaseHandler.getConnection();
        DSLContext context = jooqContext.createContext(con);
        context
            .select(SOME_LIST._NAME, SOME_LIST.UUID)
            .from(SOME_LIST)
            .fetch();
        con.close();
    }

    @ParameterizedTest
    @FieldSource("tablePrefixes")
    @DisplayName("Insert query")
    void testQueryInsert(String prefix) throws SQLException {
        databaseHandler.getDatabaseConfig().setTablePrefix(prefix);
        JooqContext jooqContext = new JooqContext(databaseHandler.getDatabaseConfig());

        Connection con = databaseHandler.getConnection();
        DSLContext context = jooqContext.createContext(con);
        context
            .insertInto(SOME_LIST)
            .set(SOME_LIST.UUID, DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()))
            .set(SOME_LIST._NAME, "testname")
            .onDuplicateKeyUpdate()
            .set(SOME_LIST._NAME, "testname")
            .execute();
        con.close();
    }

    @ParameterizedTest
    @FieldSource("tablePrefixes")
    @DisplayName("Set query")
    void testQuerySet(String prefix) throws SQLException {
        databaseHandler.getDatabaseConfig().setTablePrefix(prefix);
        JooqContext jooqContext = new JooqContext(databaseHandler.getDatabaseConfig());

        Connection con = databaseHandler.getConnection();
        DSLContext context = jooqContext.createContext(con);
        context
            .insertInto(SOME_LIST)
            .set(SOME_LIST.UUID, DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()))
            .set(SOME_LIST._NAME, "testname")
            .onDuplicateKeyUpdate()
            .set(SOME_LIST._NAME, "testname")
            .execute();
        con.close();
    }
}
