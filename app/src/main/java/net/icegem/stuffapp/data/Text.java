package net.icegem.stuffapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import net.icegem.stuffapp.Jasonable;
import net.icegem.stuffapp.MutablePair;
import net.icegem.stuffapp.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by mikael.korpela on 25.5.2015.
 */
public class Text implements Parcelable, Jasonable {

    // Actions
    public static final String EDIT_ACTION = "Text_Edit_Action";
    public static final String VIEW_ACTION = "Text_View_Action";

    // DB Strings
    public static final String TABLE = "Text";
    public static final String TABLE_TRANSLATE = "TextTranslate";

    public static final String COLUMN_IDENTIFIER = "_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TEXT = "text";

    public static final String[] translationColumns = {
            COLUMN_TEXT,
            COLUMN_LANGUAGE,
            COLUMN_VALUE
    };

    public static final String[] columns = {
            COLUMN_IDENTIFIER,
            COLUMN_COMMENT
    };

    public static final String DATABASE_CREATE_TRANSLATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSLATE + "(" +
                COLUMN_TEXT + " integer, " +
                COLUMN_LANGUAGE + " text not null," +
                COLUMN_VALUE + " text," +
                "PRIMARY KEY (" + COLUMN_IDENTIFIER + "," + COLUMN_LANGUAGE + "));";

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    COLUMN_COMMENT + " text" +
                    ");";

    // Members
    private static int nid = 10;

    private int _id;
    private String comment;
    private Vector<MutablePair<String, String>> values = new Vector<MutablePair<String, String>>();

    public Text() {
        _id = -(++nid);
    }

    public Text(JSONObject json) throws JSONException {
        parse(json);
    }

    public Text( int id ) {
        setId(id);
    }

    public int getId() {
        return _id;
    }

    public void setId( int id ) {
        _id = id;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String value)
    {
        this.comment = value;
    }

    public String get() {
        return get( null );
    }

    public String get( String lang ) {
        if( lang == null ) {
            lang = Settings.language;
        }
        MutablePair<String,String> pair = at(lang);
        if( pair != null ) {
            return pair.second;
        }

        if( lang == Settings.language ) {
            // lets be nice..
            // We could throw, and that would be preferrable in debug situations.
            return "" + _id;
        }
        return get(null);
    }

    public void set( String value ) {
        set(null, value);
    }

    public void set( String lang, String value ) {
        if( lang == null ) {
            lang = Settings.language;
        }
        lang = lang.toLowerCase();
        MutablePair<String,String> item = new MutablePair<String,String>(lang,value);
        for( int i = 0 ; i < values.size() ; ++i ) {
            final MutablePair<String,String> pair = values.elementAt(i);
            if( lang.equals(pair.first) ) {
                values.add( i , item );
                return;
            }
        }
        values.add(item);
        sort();
    }

    public void sort() {
        Collections.sort(values, new Comparator<MutablePair<String, String>>() {
            @Override
            public int compare(MutablePair<String, String> lhs, MutablePair<String, String> rhs) {
                return lhs.first.compareTo(rhs.first);
            }
        });
    }

    public void clean() {
        for( int i = 0 ; i < values.size() ; ++i ) {
            final MutablePair<String,String> pair = values.elementAt(i);
            if( pair.first.isEmpty() ) {
                // clear the item & redo the position..
                values.remove(i);
                --i;
            }
        }
    }

    public MutablePair<String,String> at(String lang) {
        for( MutablePair<String,String> pair : values ) {
            if( lang.equals(pair.first) ) {
                return pair;
            }
        }
        return null;
    }

    public MutablePair<String,String> at( int index ) {
        return values.elementAt(index);
    }

    public void add() {
        if( values.size() <= 0 ) {
            values.add(new MutablePair<String, String>(Settings.language, ""));
        }
        else {
            values.add(new MutablePair<String, String>("", ""));
        }
    }

    public void remove( int index ) {
        values.remove(index);
    }


    public int size() {
        return values.size();
    }

    @Override
    public String toString() {
        return get();
    }

    public int compareTo(Text other) {
        return get().compareTo(other.get());
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        clean();

        Bundle bundle = new Bundle();
        for( MutablePair<String,String> pair : values ) {
            bundle.putString( pair.first , pair.second );
        }

        out.writeInt(getId());
        out.writeString( getComment() );
        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() {
        public Text createFromParcel(Parcel in) {
            return new Text(in);
        }

        public Text[] newArray(int size) {
            return new Text[size];
        }
    };

    private Text(Parcel in) {
        setId( in.readInt() );
        setComment( in.readString() );

        Bundle bundle = in.readBundle();
        for( String key : bundle.keySet() ) {
            String value = bundle.getString(key);
            if(value != null) {
                MutablePair<String,String> item = new MutablePair<String,String>(key,value);
                values.add(item);
            }
        }
        sort();
    }

    /*
    If any of the translations contains the "key" value.
     */
    public boolean contains(String key) {
        for( MutablePair<String, String> pair : values ) {
            if(pair.second.contains(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JSONObject toJSON() {
        clean();

        JSONObject json = new JSONObject();
        try {
            json.put(COLUMN_IDENTIFIER,getId());
            json.put(COLUMN_COMMENT,getComment());
            for( MutablePair<String,String> pair : values ) {
                json.put( pair.first , pair.second );
            }
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException {
        // Iterator that.. cant be iterated by for( String key : json.keys() ) ... Jeez these
        // javafolks are dumf*cks..
        Iterator<String> keys = json.keys();

        while( keys.hasNext() ) {
            String key = keys.next();
            if( key.equals(COLUMN_IDENTIFIER)) {
                setId(json.getInt(COLUMN_IDENTIFIER));
                continue;
            }
            if( key.equals(COLUMN_COMMENT)) {
                setComment(json.getString(COLUMN_COMMENT));
                continue;
            }
            MutablePair<String,String> item = new MutablePair<String,String>(key,json.getString(key));
            values.add(item);
        }
        sort();
    }

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
