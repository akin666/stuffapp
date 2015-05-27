package net.icegem.stuffapp.database;

import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Collection;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class DBCollection {
    // DB Strings
    public static final String TABLE = "Collection";

    public static final String[] translationColumns = {
            Collection.COLUMN_IDENTIFIER,
            Collection.COLUMN_NAME,
            Collection.COLUMN_DESCRIPTION,
            Collection.COLUMN_PICTURE,
            Collection.COLUMN_LINK
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    Collection.COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    Collection.COLUMN_NAME + " integer," +
                    Collection.COLUMN_DESCRIPTION + " integer," +
                    Collection.COLUMN_PICTURE + " text," +
                    Collection.COLUMN_LINK + " text" +
                    ");";

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
