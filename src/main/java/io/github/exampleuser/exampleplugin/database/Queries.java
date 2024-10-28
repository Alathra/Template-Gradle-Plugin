package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.utility.DB;
import io.github.exampleuser.exampleplugin.utility.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static io.github.exampleuser.exampleplugin.database.schema.Tables.COLORS;
import static io.github.exampleuser.exampleplugin.database.schema.Tables.SOME_LIST;

/**
 * A class providing access to all SQL queries.
 */
public abstract class Queries {
    /**
     * Example add data to database.
     * <p>
     * Inserts or updates entries dependent on whether a duplicate row exists.
     */
    public static void upsert() {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            context
                .insertInto(SOME_LIST, SOME_LIST.UUID, SOME_LIST._NAME)
                .values(
                    DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()),
                    "testname"
                )
                .onDuplicateKeyUpdate()
                .set(SOME_LIST._NAME, "testname")
                .execute();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    /**
     * Example insert with returning key.
     * <p>
     * This method actually is an upsert and inserts or updates entries dependent on whether a duplicate row exists, and returns the relevant auto-incrementing field from the table.
     *
     * @return a resulting integer or null if null if the query failed
     */
    public static @Nullable Record1<Integer> upsertReturning() {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            return context
                .insertInto(COLORS, COLORS.SOME_FIELD, COLORS.ENABLED)
                .values(
                    "testname",
                    (byte) 1
                )
                .onDuplicateKeyUpdate()
                .set(COLORS.SOME_FIELD, "testname")
                .returningResult(COLORS.COLOR_ID) // Return the auto-incrementing id from the db
                .fetchOne();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
        return null;
    }

    /**
     * Example save all data to database.
     * <p>
     * This method batch executes an upsert and inserts or updates entries dependent on whether a duplicate row exists.
     * Read <a href="https://www.jooq.org/doc/latest/manual/sql-execution/batch-execution/">jOOQ Batch Documentation</a> for more info.
     */
    public static void saveAll() {
        try (
            @NotNull Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            context
                .batch(
                    context
                        .insertInto(SOME_LIST, SOME_LIST.UUID, SOME_LIST._NAME)
                        .values(
                            DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()),
                            "testname"
                        )
                        .onDuplicateKeyUpdate()
                        .set(SOME_LIST._NAME, "testname"),
                    context
                        .insertInto(SOME_LIST, SOME_LIST.UUID, SOME_LIST._NAME)
                        .values(
                            DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()),
                            "othername"
                        )
                        .onDuplicateKeyUpdate()
                        .set(SOME_LIST._NAME, "othername")
                )
                .execute();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    /**
     * Example save all data to database.
     * <p>
     * This method uses a transaction to execute the queries.
     * Read <a href="https://www.jooq.org/doc/latest/manual/sql-execution/transaction-management/">jOOQ Transaction Documentation</a> for more info.
     */
    public static void saveAllTransaction() {
        try (
            @NotNull Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            context
                .transaction(configuration -> {
                    DSLContext internalContext = configuration.dsl();

                    internalContext
                        .insertInto(SOME_LIST, SOME_LIST.UUID, SOME_LIST._NAME)
                        .values(
                            DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()),
                            "testname"
                        )
                        .onDuplicateKeyUpdate()
                        .set(SOME_LIST._NAME, "testname")
                        .execute();

                    internalContext
                        .insertInto(SOME_LIST, SOME_LIST.UUID, SOME_LIST._NAME)
                        .values(
                            DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()),
                            "othername"
                        )
                        .onDuplicateKeyUpdate()
                        .set(SOME_LIST._NAME, "othername")
                        .execute();
                });
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    /**
     * Example load all data from database.
     * <p>
     * You should make this method return whatever it is you're grabbing from database.
     *
     * @return the result
     */
    public static @Nullable Result<Record2<String, byte[]>> loadAll() {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            return context
                .select(SOME_LIST._NAME, SOME_LIST.UUID)
                .from(SOME_LIST)
                .fetch();
        } catch (SQLException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
        return null;
    }

    /**
     * Convert uuid to an array of bytes.
     *
     * @param uuid the uuid
     * @return the byte array
     */
    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Convert byte array to uuid.
     *
     * @param bytes the byte array
     * @return the uuid
     */
    public static UUID convertBytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
