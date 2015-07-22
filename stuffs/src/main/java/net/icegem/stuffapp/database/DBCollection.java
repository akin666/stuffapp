package net.icegem.stuffapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;

import java.util.ArrayList;
import java.util.List;

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

    public static Collection get(DBConnection connection , int id) {
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

    public static Collection save(DBConnection connection , Collection item) {
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

    public static void delete(DBConnection connection , Collection item) {
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

    public static List<Collection> list(DBConnection connection) {
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

    public static Collection get(SQLiteDatabase db , int id) {
        Collection item = new Collection(id);

        Cursor cursor = db.query(TABLE, columns, Collection.COLUMN_IDENTIFIER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            // int id = cursor.getInt(0);
            int nameText = cursor.getInt(1);
            int descriptionText = cursor.getInt(2);

            item.setPicture(cursor.getString(3));
            item.setLink(cursor.getString(4));
            cursor.close();

            item.setName(DBText.get(db, nameText));
            item.setDescription(DBText.get(db, descriptionText) );
        }
        return item;
    }

    public static Collection save(SQLiteDatabase db , Collection item) {
        int id = item.getId();

        DBText.save(db, item.getName());
        DBText.save(db, item.getDescription());

        ContentValues values = new ContentValues();

        values.put(Collection.COLUMN_NAME, item.getName().getId());
        values.put(Collection.COLUMN_DESCRIPTION, item.getDescription().getId());
        values.put(Collection.COLUMN_PICTURE, item.getPicture());
        values.put(Collection.COLUMN_LINK, item.getLink());

        // NEW!
        if( id < 0 ) {
            id = (int)db.insert(TABLE, null, values);
            item.setId(id);
        }
        else {
            db.update(TABLE, values, Collection.COLUMN_IDENTIFIER + " = " + id, null);
        }

        return item;
    }

    public static void delete(SQLiteDatabase db , Collection item) {
        int id = item.getId();

        DBText.delete(db, item.getName());
        DBText.delete(db, item.getDescription());

        List<Item> items = DBItem.listByCollection(db , id);
        for( Item iter : items )
        {
            DBItem.delete(db, iter);
        }

        // NEW!
        if( id < 0 )
        {
            return;
        }

        db.delete(TABLE, Collection.COLUMN_IDENTIFIER + " = " + id, null);
    }

    public static List<Collection> list(SQLiteDatabase db) {
        ArrayList<Collection> items = new ArrayList<Collection>();
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Collection item = new Collection(cursor.getInt(0));

            item.setName( DBText.get(db , cursor.getInt(1)) );
            item.setDescription(DBText.get(db, cursor.getInt(2)));
            item.setPicture(cursor.getString(3));
            item.setLink(cursor.getString(4));

            items.add(item);

            cursor.moveToNext();
        }
        return items;
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
