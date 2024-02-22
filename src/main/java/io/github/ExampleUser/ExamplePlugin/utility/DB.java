package io.github.exampleuser.exampleplugin.utility;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.db.DatabaseHandler;
import io.github.exampleuser.exampleplugin.db.DatabaseType;
import io.github.exampleuser.exampleplugin.db.jooq.JooqContext;
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
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
        return ExamplePlugin.getInstance().getDataHandler().getConnection();
    }

    /**
     * Convenience method for {@link JooqContext#createContext(Connection)} to getConnection {@link DSLContext}
     *
     * @param con the con
     * @return the context
     */
    @NotNull
    public static DSLContext getContext(Connection con) {
        return ExamplePlugin.getInstance().getDataHandler().getJooqContext().createContext(con);
    }

    /**
     * Convenience method for {@link DatabaseHandler#getDB()} to getConnection {@link DatabaseType}
     *
     * @return the db
     */
    public static DatabaseType getDB() {
        return ExamplePlugin.getInstance().getDataHandler().getDB();
    }
}
