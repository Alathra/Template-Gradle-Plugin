package io.github.ExampleUser.ExamplePlugin.utility;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.db.DatabaseHandler;
import io.github.ExampleUser.ExamplePlugin.db.DatabaseType;
import io.github.ExampleUser.ExamplePlugin.db.jooq.JooqContext;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Convenience class for accessing methods in {@link DatabaseHandler#getConnection}
 */
public abstract class DB {
    /**
     * Convenience method for {@link DatabaseHandler#getConnection} to getConnection {@link Connection}
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
        return ExamplePlugin.getInstance().getDataHandler().getConnection();
    }

    /**
     * Convenience method for {@link JooqContext#createContext(Connection)} to getConnection {@link DSLContext}
     */
    @NotNull
    public static DSLContext getContext(Connection con) {
        return ExamplePlugin.getInstance().getDataHandler().getJooqContext().createContext(con);
    }

    /**
     * Convenience method for {@link DatabaseHandler#getDB()} to getConnection {@link DatabaseType}
     */
    public static DatabaseType getDB() {
        return ExamplePlugin.getInstance().getDataHandler().getDB();
    }
}
