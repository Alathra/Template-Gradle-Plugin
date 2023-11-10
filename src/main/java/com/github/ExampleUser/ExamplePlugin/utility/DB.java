package com.github.ExampleUser.ExamplePlugin.utility;

import com.github.ExampleUser.ExamplePlugin.db.DBHandler;
import com.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import org.jetbrains.annotations.NotNull;

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
}
