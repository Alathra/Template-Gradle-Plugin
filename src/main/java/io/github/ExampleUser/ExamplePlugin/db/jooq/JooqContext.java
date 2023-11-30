package io.github.ExampleUser.ExamplePlugin.db.jooq;

import io.github.ExampleUser.ExamplePlugin.utility.Cfg;
import org.jooq.*;
import org.jooq.conf.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.JooqLogger;

import java.sql.Connection;
import java.util.regex.Pattern;

/**
 * Utility class for getting jOOQ Context to use with HikariCP.
 */
public final class JooqContext {
	static {
		JooqLogger.globalThreshold(Log.Level.ERROR); // Silence JOOQ warnings
	}

	private static final String TABLE_PREFIX = Cfg.get().getOrDefault("db.prefix", "example_"); // The table prefix as grabbed from config
	private static final Pattern MATCH_ALL_EXCEPT_INFORMATION_SCHEMA = Pattern.compile("^(?!INFORMATION_SCHEMA)(.*?)$");
	private static final Pattern MATCH_ALL = Pattern.compile("^(.*?)$");
	private static final String REPLACEMENT = "%s$0".formatted(TABLE_PREFIX); //
	private final SQLDialect dialect;

    /**
     * Instantiates a new Jooq context.
     *
	 * @param dialect the getSQLDialect
     */
    public JooqContext(SQLDialect dialect) {
		this.dialect = dialect;
	}

    /**
     * Create DSL Context.
     *
     * @param connection the connection
     * @return the dsl context
     */
    public DSLContext createContext(Connection connection) {
		record SimpleConnectionProvider(Connection connection) implements ConnectionProvider {

			@Override
			public Connection acquire() throws DataAccessException {
				return connection;
			}

			@Override
			public void release(Connection connection) throws DataAccessException {}
		}
		return createWith(new SimpleConnectionProvider(connection));
	}

	/**
     * Applies default configuration
     *
     * @param connectionProvider A connection lifecycle handler API.
     * @return DSLContext
     */
	private DSLContext createWith(ConnectionProvider connectionProvider) {
		return new DefaultConfiguration()
				.set(connectionProvider)
				.set(dialect)
				.set(createSettings())
				.set(new ExecuteListenerProvider[0])
				.dsl();
	}

    /**
     * Returns base settings for DSL Contexts.
     *
     * @return Settings
     */
	private Settings createSettings() {
		return new Settings()
				.withBackslashEscaping(BackslashEscaping.OFF)
				.withRenderSchema(false)
				.withRenderMapping(new RenderMapping() // Support the tables having custom prefix
						.withSchemata(new MappedSchema()
								.withInputExpression(MATCH_ALL_EXCEPT_INFORMATION_SCHEMA)
								.withTables(new MappedTable()
										.withInputExpression(MATCH_ALL)
										.withOutput(REPLACEMENT)
								)
						)
				)
            .withRenderQuotedNames(RenderQuotedNames.ALWAYS)
            .withRenderNameCase(RenderNameCase.LOWER);
	}
}