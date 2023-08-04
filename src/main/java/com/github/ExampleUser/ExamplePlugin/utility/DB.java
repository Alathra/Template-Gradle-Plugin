package com.github.ExampleUser.ExamplePlugin.utility;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.db.DBHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Convenience class for accessing methods in {@link DBHandler#getConnection}
 */
public abstract class DB {
    /**
     * Convenience method for {@link DBHandler#getConnection} to get {@link Connection}
     */
    @NotNull
    public static Connection get() throws SQLException {
        return Main.getInstance().getDataHandler().getConnection();
    }
}
