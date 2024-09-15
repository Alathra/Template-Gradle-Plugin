package io.github.exampleuser.exampleplugin.database;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

/**
 * Agnostically run the same tests for different RDBMS vendors.
 */
public class DatabaseTests {
    @DisplayName("MySQL")
    @Nested
    class MySQLTest extends AbstractExternalDatabaseTest {
        @Container
        private static final GenericContainer<?> container = new MySQLContainer(DockerImageName.parse("mysql:8.0.31"))
            .withDatabaseName("testing")
            .withUsername("root")
            .withPassword("")
            .withExposedPorts(3306);

        public MySQLTest() {
            super("mysql", DatabaseType.MYSQL, container);
        }
    }

    @DisplayName("MariaDB")
    @Nested
    class MariaDBTest extends AbstractExternalDatabaseTest {
        @Container
        private static final GenericContainer<?> container = new MariaDBContainer(DockerImageName.parse("mariadb:10.7"))
            .withDatabaseName("testing")
            .withUsername("root")
            .withPassword("")
            .withExposedPorts(3306);

        public MariaDBTest() {
            super("mariadb", DatabaseType.MARIADB, container);
        }
    }

    @DisplayName("SQLite")
    @Nested
    class SQLiteTest extends AbstractEmbeddedDatabaseTest {
        public SQLiteTest() {
            super("sqlite", DatabaseType.SQLITE);
        }
    }

    @DisplayName("H2")
    @Nested
    class H2Test extends AbstractEmbeddedDatabaseTest {
        public H2Test() {
            super("h2", DatabaseType.H2);
        }
    }
}
