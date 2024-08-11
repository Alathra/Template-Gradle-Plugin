package io.github.exampleuser.exampleplugin.database;

import io.github.exampleuser.exampleplugin.utility.DB;
import io.github.exampleuser.exampleplugin.utility.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static io.github.exampleuser.exampleplugin.database.schema.Tables.SOME_LIST;

/**
 * A holder class for all SQL queries
 */
public abstract class DatabaseQueries {
    /**
     * Example add data to database.
     * <p>
     * This method actually is an upsert and inserts or updates entries dependent on whether a duplicate row exists.
     */
    public static void addEntry() {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            context
                .insertInto(SOME_LIST)
                .set(SOME_LIST.UUID, DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()))
                .set(SOME_LIST._NAME, "testname")
                .onDuplicateKeyUpdate()
                .set(SOME_LIST._NAME, "testname")
                .execute();
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
            @NotNull Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            context
                .insertInto(SOME_LIST)
                .set(SOME_LIST.UUID, DatabaseQueries.convertUUIDToBytes(UUID.randomUUID()))
                .set(SOME_LIST._NAME, "testname")
                .onDuplicateKeyUpdate()
                .set(SOME_LIST._NAME, "testname")
                .execute();
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
