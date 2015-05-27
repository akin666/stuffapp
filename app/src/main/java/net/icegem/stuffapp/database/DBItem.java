package net.icegem.stuffapp.database;

import android.database.Cursor;
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

    public static Item get(SQLiteDatabase db , int id)
    {
        Item item = new Item(id);

        Cursor cursor = db.query(TABLE, columns, Item.COLUMN_IDENTIFIER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            // int id = cursor.getInt(0);
            int collection = cursor.getInt(1);
            int description = cursor.getInt(2);
            int type = cursor.getInt(3);

            item.setCode( cursor.getString(4) );
            item.setLink(cursor.getString(5));
            item.setVolume(cursor.getString(6));
            item.setPicture(cursor.getString(7));
            item.setLocation(cursor.getString(8));
            cursor.close();

            item.setCollection(DBCollection.get(db, collection));
            item.setDescription(DBText.get(db, description));
            item.setType(DBType.get(db, type));
        }
        return item;
    }

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
