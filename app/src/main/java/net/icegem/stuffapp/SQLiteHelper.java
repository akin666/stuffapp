package net.icegem.stuffapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mikael.korpela on 19.5.2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 2;

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(Item.DATABASE_CREATE);
        database.execSQL(Translated.DATABASE_CREATE);

        Text.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Translated.TABLE + ";");

        Text.onUpgrade(db,oldVersion,newVersion);
        onCreate(db);
    }
}
