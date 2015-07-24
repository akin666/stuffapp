package net.icegem.stuffapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.icegem.stuffapp.ui.Common;

/**
 * Created by mikael.korpela on 29.5.2015.
 */
public class DBConnection {
    private SQLiteOpenHelper helper;
    private Context context;

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 4;

    public DBConnection(Context context)
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
                Common.log("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

                DBText.onUpgrade(database, oldVersion, newVersion);
                DBCollection.onUpgrade(database, oldVersion, newVersion);
                DBType.onUpgrade(database, oldVersion, newVersion);
                DBItem.onUpgrade(database, oldVersion, newVersion);

                onCreate(database);
            }
        };
    }

    public void clear() {
        SQLiteDatabase db = null;
        try
        {
            db = helper.getWritableDatabase();

            DBText.clear(db);
            DBCollection.clear(db);
            DBType.clear(db);
            DBItem.clear(db);
        }
        finally {
            if( db != null )
            {
                db.close();
            }
        }
    }

    public SQLiteDatabase getWriteAccess()
    {
        return helper.getWritableDatabase();
    }

    public SQLiteDatabase getReadAccess()
    {
        return helper.getWritableDatabase();
    }
}
