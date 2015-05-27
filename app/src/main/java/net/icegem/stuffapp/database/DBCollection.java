package net.icegem.stuffapp.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Collection;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class DBCollection {
    // DB Strings
    public static final String TABLE = "Collection";

    public static final String[] columns = {
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

    public static Collection get(SQLiteDatabase db , int id)
    {
        Collection collection = new Collection(id);

        Cursor cursor = db.query(TABLE, columns, Collection.COLUMN_IDENTIFIER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            // int id = cursor.getInt(0);
            int nameText = cursor.getInt(1);
            int descriptionText = cursor.getInt(2);

            collection.setPicture( cursor.getString(3) );
            collection.setPicture( cursor.getString(4) );
            cursor.close();

            collection.setName(DBText.get(db, nameText));
            collection.setDescription(DBText.get(db, descriptionText));
        }
        return collection;
    }

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
