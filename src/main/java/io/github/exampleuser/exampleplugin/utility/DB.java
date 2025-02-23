package io.github.exampleuser.exampleplugin.utility;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseHolder;
import io.github.exampleuser.exampleplugin.database.handler.DatabaseType;
import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandler;
import io.github.exampleuser.exampleplugin.database.jooq.JooqContext;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Convenience class for accessing methods in {@link DatabaseHandler#getConnection}
 * This class abstracts away accessing the {@link DatabaseHolder} singleton
 */
public abstract class DB {
    /**
     * Convenience method for {@link DatabaseHolder#setDatabaseHandler(DatabaseHandler)}
     * Used to set the globally used database handler instance for the plugin
     */
    public static void init(DatabaseHandler handler) {
        DatabaseHolder.getInstance().setDatabaseHandler(handler);
    }

    /**
     * Convenience method for {@link DatabaseHandler#isReady()}
     *
     * @return if the database is ready
     */
    public static boolean isReady() {
        DatabaseHandler handler = DatabaseHolder.getInstance().getDatabaseHandler();
        if (handler == null)
            return false;

        return handler.isReady();

    }

    /**
     * Convenience method for {@link DatabaseHandler#getConnection} to getConnection {@link Connection}
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
        return DatabaseHolder.getInstance().getDatabaseHandler().getConnection();
    }

    /**
     * Convenience method for {@link JooqContext#createContext(Connection)} to getConnection {@link DSLContext}
     *
     * @param con the con
     * @return the context
     */
    @NotNull
    public static DSLContext getContext(Connection con) {
        return DatabaseHolder.getInstance().getDatabaseHandler().getJooqContext().createContext(con);
    }

    /**
     * Convenience method for accessing the {@link DatabaseHandler} instance
     * @return the database handler
     */
    @NotNull
    public static DatabaseHandler getHandler() {
        return DatabaseHolder.getInstance().getDatabaseHandler();
    }

    /**
     * Convenience method for {@link DatabaseHandler#getDB()} to getConnection {@link DatabaseType}
     *
     * @return the database
     */
    public static DatabaseType getDB() {
        return DatabaseHolder.getInstance().getDatabaseHandler().getDB();
    }
}
