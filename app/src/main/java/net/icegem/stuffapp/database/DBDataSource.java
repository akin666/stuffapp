package net.icegem.stuffapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.icegem.stuffapp.MutablePair;
import net.icegem.stuffapp.SQLiteHelper;
import net.icegem.stuffapp.Translated;
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.ui.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 19.5.2015.
 */
public class DBDataSource {
    private SQLiteOpenHelper helper;
    private Context context;

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 3;

    public DBDataSource(Context context)
    {
        this.context = context;

        helper = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase database) {
                DBText.onCreate(database);
                DBCollection.onCreate(database);
                DBType.onCreate(database);
                DBItem.onCreate(database);
            }

            @Override
            public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

                Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

                DBText.onUpgrade(database, oldVersion, newVersion);
                DBCollection.onUpgrade(database, oldVersion, newVersion);
                DBType.onUpgrade(database, oldVersion, newVersion);
                DBItem.onUpgrade(database, oldVersion, newVersion);

                onCreate(database);
            }
        };
    }

    private Item toItem(Cursor cursor) {
        int uid = cursor.getInt(0);
        String id = cursor.getString(1);
        String type = cursor.getString(2);
        String location = cursor.getString(3);

        Item item = new Item(uid,id);
        item.setLocation(location);
        item.setType(type);

        return item;
    }

    private void populate(SQLiteDatabase db, Item item)
    {
        final int uid = item.getUID();
        Translated translated = new Translated();

        Cursor cursor = db.query(Translated.TABLE, Translated.columns, Translated.COLUMN_PARENT + " = " + uid, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            // int uid = cursor.getInt(0);
            // int paernt = cursor.getInt(1);
            String language = cursor.getString(2);
            String value = cursor.getString(3);

            translated.set(language,value);
            cursor.moveToNext();
        }
        item.setName(translated);
    }

    private void nukeTranslations(SQLiteDatabase db, int uid )
    {
        db.delete(Translated.TABLE, Translated.COLUMN_PARENT + " = " + uid, null);
    }

    private void save(SQLiteDatabase db, int uid , Translated translated )
    {
        for( int i = 0 ; i < translated.size() ; ++i )
        {
            MutablePair<String,String> pair = translated.at(i);

            ContentValues values = new ContentValues();
            values.put(Translated.COLUMN_PARENT, uid);
            values.put(Translated.COLUMN_LANGUAGE, pair.first);
            values.put(Translated.COLUMN_VALUE, pair.second);

            long id = (int)db.insert(Translated.TABLE, null, values);
        }
    }

    public void save( Item item ) {
        int uid = item.getUID();

        ContentValues values = new ContentValues();
        values.put(Item.COLUMN_ID, item.getID());
        values.put(Item.COLUMN_LOCATION, item.getLocation());
        values.put(Item.COLUMN_TYPE, item.getType());

        Translated translated = item.getNameObject();

        SQLiteDatabase db = null;
        try
        {
            db = helper.getWritableDatabase();
            if( uid < 0 )
            {
                // New!
                // todo! change all ID's to long.. as this implementation wants ID to be long.. sigh..
                uid = (int)db.insert(Item.TABLE, null, values);
            }
            else
            {
                // Update!
                db.update(Item.TABLE, values, Item.COLUMN_UID + " = " + uid,  null);

                // Clean translations..
                nukeTranslations(db, uid);
            }

            // Save translations
            save(db, uid, translated);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }
    }

    public void deleteItem( Item item ) {
        deleteItem(item.getUID());
    }

    public void deleteItem( int uid ) {
        if( uid < 0 )
        {
            // it has not been created.
            return;
        }

        SQLiteDatabase db = null;
        try
        {
            db = helper.getWritableDatabase();
            db.delete(Item.TABLE, Item.COLUMN_UID + " = " + uid, null);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }
    }

    public void deleteItem( String id ) {
        SQLiteDatabase db = null;
        try
        {
            db = helper.getWritableDatabase();
            db.delete(Item.TABLE, Item.COLUMN_ID + " = " + id, null);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }
    }

    public List<Item> getItems()
    {
        List<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = null;
        try
        {
            db = helper.getReadableDatabase();

            Cursor cursor = db.query(Item.TABLE, Item.columns, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Item item = toItem(cursor);
                items.add(item);
                cursor.moveToNext();
            }
            cursor.close();

            // Populate translations..
            for( Item item : items )
            {
                populate(db, item);
            }
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }

        return items;
    }

    public List<Item> getItemsOfType(String type)
    {
        List<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = null;
        try
        {
            db = helper.getReadableDatabase();

            Cursor cursor = db.query(Item.TABLE, Item.columns, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Item item = toItem(cursor);
                items.add(item);
                cursor.moveToNext();
            }
            cursor.close();

            // Populate translations..
            for( Item item : items )
            {
                populate(db, item);
            }
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }

        return items;
    }

    public Item getItem( int uid )
    {
        Item item = null;
        SQLiteDatabase db = null;
        try
        {
            db = helper.getReadableDatabase();
            Cursor cursor = db.query(Item.TABLE, Item.columns, Item.COLUMN_UID + " = " + uid, null, null, null, null);

            cursor.moveToFirst();
            if(cursor.isAfterLast())
            {
                return item;
            }
            item = toItem(cursor);
            cursor.close();

            populate(db, item);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }

        return item;
    }

    public Item getItem( String id )
    {
        Item item = null;
        SQLiteDatabase db = null;
        try
        {
            db = helper.getReadableDatabase();
            Cursor cursor = db.query(Item.TABLE, Item.columns, Item.COLUMN_ID + " = " + id, null, null, null, null);

            cursor.moveToFirst();
            if(cursor.isAfterLast())
            {
                return item;
            }
            item = toItem(cursor);
            cursor.close();

            populate(db, item);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }

        return item;
    }

    public void deleteData()
    {
        SQLiteDatabase db = null;
        try
        {
            db = helper.getWritableDatabase();
            db.delete(Item.TABLE, null, null);
            db.delete(Translated.TABLE, null, null);
        }
        catch (Exception e)
        {
            Common.log(context, e);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }
    }
}
