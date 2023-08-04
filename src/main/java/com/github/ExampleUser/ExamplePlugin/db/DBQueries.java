package com.github.ExampleUser.ExamplePlugin.db;

import com.github.ExampleUser.ExamplePlugin.utility.DB;
import com.github.ExampleUser.ExamplePlugin.utility.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.UUID;

/**
 * A holder class for all SQL queries
 */
public abstract class DBQueries {
    /**
     * Create or migrate tables on startup
     */
    public static void init() {
        try (final Statement statement = DB.get().createStatement()) {
            statement.addBatch("""
                    CREATE TABLE IF NOT EXISTS `some_list` (
                      `uuid` uuid NOT NULL DEFAULT uuid(),
                      `name` tinytext NOT NULL,
                      PRIMARY KEY (`uuid`)
                    );
                """);

            statement.executeLargeBatch();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    /**
     * Example save all data to database.
     * <p>
     * This method actually is an upsert and inserts or updates entries dependent on whether a duplicate row exists.
     */
    public static void saveAll() {
        try (
            @NotNull Connection con = DB.get();
            PreparedStatement warStatement = con.prepareStatement("INSERT INTO `some_list` (`uuid`, `name`) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE `name` = ?")
        ) {
            warStatement.setString(1, UUID.randomUUID().toString());
            warStatement.setString(2, "darksaid98");
            warStatement.setString(3, "darksaid98");

            warStatement.executeUpdate();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    /**
     * Example load all data from database.
     * <p>
     * You should make this method return whatever it is you're grabbing from db.
     */
    public static void loadAll() {
        try (
            Connection con = DB.get();
            PreparedStatement listStatement = con.prepareStatement("SELECT * FROM `some_list`")
        ) {
            ResultSet someResult = listStatement.executeQuery();

            while (someResult.next()) {
                UUID uuid = UUID.fromString(someResult.getString("uuid"));
                String name = someResult.getString("name");
            }
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }
}
