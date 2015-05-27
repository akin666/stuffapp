package net.icegem.stuffapp.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Type;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class DBType {
    // DB Strings
    public static final String TABLE = "Type";

    public static final String[] columns = {
            Type.COLUMN_IDENTIFIER,
            Type.COLUMN_NAME
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    Type.COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    Type.COLUMN_NAME + " integer " +
                    ");";

    public static Type get(SQLiteDatabase db , int id)
    {
        Type type = new Type(id);

        Cursor cursor = db.query(TABLE, columns, Type.COLUMN_IDENTIFIER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            // int id = cursor.getInt(0);
            int textId = cursor.getInt(1);
            cursor.close();

            type.setName( DBText.get(db , textId) );
        }
        return type;
    }

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
