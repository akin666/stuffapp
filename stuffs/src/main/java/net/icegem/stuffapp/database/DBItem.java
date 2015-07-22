package net.icegem.stuffapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.ui.Common;

import java.util.ArrayList;
import java.util.List;

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

    public static Item get(DBConnection connection , int id) {
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

    public static Item save(DBConnection connection , Item item) {
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

    public static void delete(DBConnection connection , Item item) {
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

    public static List<Item> list(DBConnection connection) {
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

    public static List<Item> listByCollection(DBConnection connection , int id ) {
        SQLiteDatabase db = null;
        try {
            db = connection.getReadAccess();
            return listByCollection(db, id);
        }
        finally {
            if( db != null ) {
                db.close();
            }
        }
    }

    public static Item get(SQLiteDatabase db , int id) {
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

    public static Item save(SQLiteDatabase db , Item item) {
        int id = item.getId();

        DBText.save(db, item.getDescription());

        ContentValues values = new ContentValues();

        values.put(Item.COLUMN_COLLECTION, item.getCollection().getId());
        values.put(Item.COLUMN_DESCRIPTION, item.getDescription().getId());
        values.put(Item.COLUMN_TYPE, item.getType().getId());
        values.put(Item.COLUMN_CODE, item.getCode());
        values.put(Item.COLUMN_LINK, item.getLink());
        values.put(Item.COLUMN_VOLUME, item.getVolume());
        values.put(Item.COLUMN_PICTURE, item.getPicture());
        values.put(Item.COLUMN_LOCATION, item.getLocation());

        // NEW!
        if( id < 0 ) {
            id = (int)db.insert(TABLE, null, values);
            item.setId(id);
        }
        else {
            db.update(TABLE, values, Item.COLUMN_IDENTIFIER + " = " + id,  null);
        }

        return item;
    }

    public static void delete(SQLiteDatabase db , Item item) {
        int id = item.getId();

        DBText.delete(db, item.getDescription());

        // NEW!
        if( id < 0 )
        {
            return;
        }

        db.delete(TABLE, Item.COLUMN_IDENTIFIER + " = " + id, null);
    }

    public static void deleteAll(SQLiteDatabase db) {
        List<Item> items = list(db);
        for( Item iter : items )
        {
            delete(db, iter);
        }
    }

    public static List<Item> list(SQLiteDatabase db) {
        ArrayList<Item> items = new ArrayList<Item>();

        List<Collection> collections = DBCollection.list(db);
        List<Type> types = DBType.list(db);

        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item(cursor.getInt(0));

            int collection = cursor.getInt(1);
            int description = cursor.getInt(2);
            int type = cursor.getInt(3);

            item.setCode(cursor.getString(4));
            item.setLink(cursor.getString(5));
            item.setVolume(cursor.getString(6));
            item.setPicture(cursor.getString(7));
            item.setLocation(cursor.getString(8));

            for( final Collection iter : collections ) {
                if( iter.getId() == collection ) {
                    item.setCollection(iter);
                    break;
                }
            }
            for( final Type iter : types ) {
                if( iter.getId() == type ) {
                    item.setType(iter);
                    break;
                }
            }

            items.add(item);
            cursor.moveToNext();
        }
        return items;
    }

    public static List<Item> listByCollection(SQLiteDatabase db , int id ) {
        ArrayList<Item> items = new ArrayList<Item>();

        final Collection collection = DBCollection.get(db, id);
        List<Type> types = DBType.list(db);

        Cursor cursor = db.query(TABLE, columns, Item.COLUMN_COLLECTION + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item(cursor.getInt(0));

            int description = cursor.getInt(2);
            int type = cursor.getInt(3);

            item.setCode(cursor.getString(4));
            item.setLink(cursor.getString(5));
            item.setVolume(cursor.getString(6));
            item.setPicture(cursor.getString(7));
            item.setLocation(cursor.getString(8));

            item.setCollection(collection);

            for( final Type iter : types ) {
                if( iter.getId() == type ) {
                    item.setType(iter);
                    break;
                }
            }

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
