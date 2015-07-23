package net.icegem.stuffapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Type;

import java.util.ArrayList;
import java.util.List;

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

    public static Type get(DBConnection connection , int id) {
        SQLiteDatabase db = null;
        try {
            db = connection.getReadAccess();
            return get(db, id);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static Type save(DBConnection connection , Type item) {
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

    public static void delete(DBConnection connection , Type item) {
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

    public static List<Type> list(DBConnection connection) {
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

    public static int count(DBConnection connection) {
        SQLiteDatabase db = null;
        try {
            db = connection.getReadAccess();
            return count(db);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static Type get(SQLiteDatabase db , int id) {
        Type item = new Type(id);

        Cursor cursor = db.query(TABLE, columns, Type.COLUMN_IDENTIFIER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            // int id = cursor.getInt(0);
            int textId = cursor.getInt(1);
            cursor.close();

            item.setName( DBText.get(db , textId) );
        }
        return item;
    }

    public static Type save(SQLiteDatabase db , Type item) {
        int id = item.getId();

        DBText.save(db, item.getName());

        ContentValues values = new ContentValues();
        values.put(Type.COLUMN_NAME, item.getName().getId());

        // NEW!
        if( id < 0 ) {
            id = (int)db.insert(TABLE, null, values);
            item.setId(id);
        }
        else {
            db.update(TABLE, values, Type.COLUMN_IDENTIFIER + " = " + id, null);
        }

        return item;
    }

    public static void delete(SQLiteDatabase db , Type item) {
        int id = item.getId();

        DBText.delete(db, item.getName());

        // NEW!
        if( id < 0 )
        {
            return;
        }

        db.delete(TABLE, Type.COLUMN_IDENTIFIER + " = " + id, null);
    }

    public static void deleteAll(SQLiteDatabase db) {
        List<Type> items = list(db);
        for( Type iter : items )
        {
            delete(db, iter);
        }
    }

    public static List<Type> list(SQLiteDatabase db) {
        ArrayList<Type> items = new ArrayList<Type>();
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Type item = new Type(cursor.getInt(0));
            item.setName( DBText.get(db , cursor.getInt(1)) );

            items.add(item);

            cursor.moveToNext();
        }
        return items;
    }

    public static int count(SQLiteDatabase db) {
        ArrayList<Type> items = new ArrayList<Type>();
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);
        return cursor.getCount();
    }

    /// DB Management
    public static void clear(SQLiteDatabase db) {
        db.delete(TABLE, null, null);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
