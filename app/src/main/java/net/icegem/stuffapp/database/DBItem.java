package net.icegem.stuffapp.database;

import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Item;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class DBItem {
    public static final String TABLE = "Item";

    public static final String[] columns = {
            Item.COLUMN_IDENTIFIER,
            Item.COLUMN_COLLECTION,
            Item.COLUMN_DESCRIPTION,
            Item.COLUMN_TYPE,
            Item.COLUMN_CODE,
            Item.COLUMN_LINK,
            Item.COLUMN_VOLUME,
            Item.COLUMN_PICTURE,
            Item.COLUMN_LOCATION
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    Item.COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    Item.COLUMN_COLLECTION + " integer, " +
                    Item.COLUMN_DESCRIPTION + " integer, " +
                    Item.COLUMN_TYPE + " integer, " +
                    Item.COLUMN_CODE + " text, " +
                    Item.COLUMN_LINK + " text, " +
                    Item.COLUMN_VOLUME + " text, " +
                    Item.COLUMN_PICTURE + " text, " +
                    Item.COLUMN_LOCATION + " text " +
                    ");";

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
