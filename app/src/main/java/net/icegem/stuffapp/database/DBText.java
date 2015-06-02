package net.icegem.stuffapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.MutablePair;
import net.icegem.stuffapp.data.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class DBText {
    public static final String TABLE = "Text";
    public static final String TABLE_TRANSLATE = "TextTranslate";

    public static final String[] translationColumns = {
            Text.COLUMN_TEXT,
            Text.COLUMN_LANGUAGE,
            Text.COLUMN_VALUE
    };

    public static final String[] columns = {
            Text.COLUMN_IDENTIFIER
    };

    public static final String DATABASE_CREATE_TRANSLATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSLATE + "(" +
                    Text.COLUMN_TEXT + " integer, " +
                    Text.COLUMN_LANGUAGE + " text not null," +
                    Text.COLUMN_VALUE + " text," +
                    "PRIMARY KEY (" + Text.COLUMN_TEXT + "," + Text.COLUMN_LANGUAGE + "));";

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    Text.COLUMN_IDENTIFIER + " integer primary key autoincrement " +
                    ");";

    public static Text get(DBConnection connection , int id) {
        SQLiteDatabase db = null;
        try {
            db = connection.getReadAccess();
            return get(db,id);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static Text save(DBConnection connection , Text item) {
        SQLiteDatabase db = null;
        try {
            db = connection.getWriteAccess();
            return save(db, item);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static void delete(DBConnection connection , Text item) {
        SQLiteDatabase db = null;
        try {
            db = connection.getWriteAccess();
            delete(db, item);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static List<Text> list(DBConnection connection) {
        SQLiteDatabase db = null;
        try {
            db = connection.getReadAccess();
            return list(db);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static Text get(SQLiteDatabase db , int id) {
        Text item = new Text(id);
        Cursor cursor = db.query(TABLE_TRANSLATE, translationColumns, Text.COLUMN_TEXT + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // int text = cursor.getInt(0);
            String language = cursor.getString(1);
            String value = cursor.getString(2);

            item.set(language,value);

            cursor.moveToNext();
        }
        return item;
    }

    public static Text save(SQLiteDatabase db , Text item) {
        int id = item.getId();

        // NEW!
        if( id < 0 )
        {
            id = (int)db.insert(TABLE, null, null);
            item.setId(id);
        }

        // Save the texts themselves
        for( int i = 0 ; i < item.size() ; ++i ) {
            MutablePair<String,String> pair = item.at(i);
            db.execSQL("INSERT OR REPLACE INTO " + TABLE_TRANSLATE + " (" +
                    Text.COLUMN_TEXT + ", " +
                    Text.COLUMN_LANGUAGE + ", " +
                    Text.COLUMN_VALUE + " VALUES ( " +
                    id + ", " +
                    "'" + pair.first + "', " +
                    "'" + pair.second + "' " +
                    ")", null);
        }

        return item;
    }

    public static void delete(SQLiteDatabase db , Text item) {
        int id = item.getId();

        // NEW!
        if( id < 0 )
        {
            return;
        }

        // delete text
        db.delete(TABLE, Text.COLUMN_IDENTIFIER + " = " + id, null);

        // Delete the texts themselves
        db.delete(TABLE_TRANSLATE , Text.COLUMN_TEXT + " = " + id, null);
    }

    public static List<Text> list(SQLiteDatabase db) {
        ArrayList<Text> items = new ArrayList<Text>();
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(get(db, cursor.getInt(0)));

            cursor.moveToNext();
        }
        return items;
    }

    /// DB Management
    public static void clear(SQLiteDatabase db) {
        db.delete(TABLE, null, null);
        db.delete(TABLE_TRANSLATE, null, null);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_TRANSLATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATE + ";");
    }
}
