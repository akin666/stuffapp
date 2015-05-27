package net.icegem.stuffapp.database;

import android.database.sqlite.SQLiteDatabase;

import net.icegem.stuffapp.data.Text;

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
            Text.COLUMN_IDENTIFIER,
            Text.COLUMN_COMMENT
    };

    public static final String DATABASE_CREATE_TRANSLATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSLATE + "(" +
                    Text.COLUMN_TEXT + " integer, " +
                    Text.COLUMN_LANGUAGE + " text not null," +
                    Text.COLUMN_VALUE + " text," +
                    "PRIMARY KEY (" + Text.COLUMN_IDENTIFIER + "," + Text.COLUMN_LANGUAGE + "));";

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    Text.COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    Text.COLUMN_COMMENT + " text" +
                    ");";

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_TRANSLATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATE + ";");
    }
}
