package net.icegem.stuffapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by mikael.korpela on 12.5.2015.
 */
public class Translated implements Parcelable , Jasonable {
    public static final String TABLE = "Translation";
    public static final String COLUMN_UID = "_id";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_PARENT = "parent";

    public static final String[] columns = {
            COLUMN_UID,
            COLUMN_PARENT,
            COLUMN_LANGUAGE,
            COLUMN_VALUE
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    COLUMN_UID + " integer primary key autoincrement, " +
                    COLUMN_PARENT + " integer, " +
                    COLUMN_LANGUAGE + " text," +
                    COLUMN_VALUE + " text" +
                    ");";


    public static final String ACTION = "Translated_Action";
    private Vector<MutablePair<String, String>> values = new Vector<MutablePair<String, String>>();

    public Translated()
    {
    }

    public Translated(String value)
    {
        set(value);
    }

    public String get()
    {
        return get(null);
    }

    public String get( String lang )
    {
        if( lang == null )
        {
            lang = Settings.language;
        }

        MutablePair<String,String> pair = at(lang);
        if( pair != null )
        {
            return pair.second;
        }

        if( lang == Settings.language )
        {
            // lets be nice..
            return "";
            //throw new Resources.NotFoundException("No translation defined for the value.");
        }

        // Try to find default
        return get(null);
    }

    public void set( String value )
    {
        set(null, value);
    }

    public void set( String lang, String value )
    {
        if( lang == null )
        {
            lang = Settings.language;
        }

        MutablePair<String,String> item = new MutablePair<String,String>(lang,value);
        for( int i = 0 ; i < values.size() ; ++i )
        {
            final MutablePair<String,String> pair = values.elementAt(i);
            if( lang.equals(pair.first) )
            {
                values.add( i , item );
                return;
            }
        }
        values.add(item);
        sort();
    }

    public void sort()
    {
        Collections.sort(values, new Comparator<MutablePair<String, String>>() {
            @Override
            public int compare(MutablePair<String, String> lhs, MutablePair<String, String> rhs) {
                return lhs.first.compareTo(rhs.first);
            }
        });
    }

    public MutablePair<String,String> at(String lang)
    {
        for( MutablePair<String,String> pair : values )
        {
            if( lang.equals(pair.first) )
            {
                return pair;
            }
        }
        return null;
    }

    public MutablePair<String,String> at( int index )
    {
        return values.elementAt(index);
    }

    public void add()
    {
        values.add(new MutablePair<String, String>(Settings.language, ""));
    }

    public void remove( int index )
    {
        values.remove(index);
    }

    @Override
    public String toString()
    {
        return get();
    }

    public int size()
    {
        return values.size();
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();

        for( MutablePair<String,String> pair : values )
        {
            bundle.putString( pair.first , pair.second );
        }

        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Translated> CREATOR = new Parcelable.Creator<Translated>() {
        public Translated createFromParcel(Parcel in) {
            return new Translated(in);
        }

        public Translated[] newArray(int size) {
            return new Translated[size];
        }
    };

    private Translated(Parcel in) {
        Bundle bundle = in.readBundle();

        for( String key : bundle.keySet() )
        {
            String value = bundle.getString(key);
            if(value != null)
            {
                MutablePair<String,String> item = new MutablePair<String,String>(key,value);
                values.add(item);
            }
        }

        sort();
    }


    public boolean contains(String key)
    {
        for( MutablePair<String, String> pair : values )
        {
            if(pair.second.toLowerCase().contains(key))
                return true;
        }
        return false;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        try {
            for( MutablePair<String,String> pair : values )
            {
                json.put( pair.first , pair.second );
            }
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException
    {
        // Iterator that.. cant be iterated by for( String key : json.keys() ) ... Jeez these
        // javafolks are dumf*cks..
        Iterator<String> keys = json.keys();

        while( keys.hasNext() )
        {
            String key = keys.next();
            String value = json.getString(key);

            set( key , value );
        }
    }
}
