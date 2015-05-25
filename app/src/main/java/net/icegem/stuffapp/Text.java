package net.icegem.stuffapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

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

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IDENTIFIER = "id";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_VALUE = "value";

    public static final String[] translationColumns = {
            COLUMN_ID,
            COLUMN_LANGUAGE,
            COLUMN_VALUE
    };

    public static final String DATABASE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                COLUMN_ID + " text not null, " +
                COLUMN_LANGUAGE + " text not null," +
                COLUMN_VALUE + " text," +
                "PRIMARY KEY (" + COLUMN_ID + "," + COLUMN_LANGUAGE + "));";

    // Members
    private String id;
    private Vector<MutablePair<String, String>> values = new Vector<MutablePair<String, String>>();

    public Text() {
    }

    public Text( String id ) {
        setId(id);
    }

    String getId()
    {
        return id;
    }

    void setId(String value)
    {
        this.id = value;
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
            return getId();
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

    @Override
    public String toString() {
        return get();
    }

    public int size() {
        return values.size();
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
        bundle.putString(COLUMN_IDENTIFIER , getId() );
        for( MutablePair<String,String> pair : values ) {
            bundle.putString( pair.first , pair.second );
        }

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
        Bundle bundle = in.readBundle();
        for( String key : bundle.keySet() ) {
            String value = bundle.getString(key);
            if(value != null) {
                if( key.equals(COLUMN_IDENTIFIER)) {
                    setId(value);
                    continue;
                }
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
            String value = json.getString(key);
            if( key.equals(COLUMN_IDENTIFIER))
            {
                setId(value);
                continue;
            }
            MutablePair<String,String> item = new MutablePair<String,String>(key,value);
            values.add(item);
        }
        sort();
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
