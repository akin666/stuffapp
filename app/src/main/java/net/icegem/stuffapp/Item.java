package net.icegem.stuffapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by mikael.korpela on 12.5.2015.
 */
public class Item implements Parcelable, Jasonable {
    public static final String TABLE = "Item";
    public static final String COLUMN_UID = "_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOCATION = "location";

    public static final String[] columns = {
            COLUMN_UID,
            COLUMN_ID,
            COLUMN_TYPE,
            COLUMN_LOCATION
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    COLUMN_UID + " integer primary key autoincrement, " +
                    COLUMN_ID + " text, " +
                    COLUMN_TYPE + " text, " +
                    COLUMN_LOCATION + " text" +
                    ");";

    private static int nid = 10;
    private int _id = 0;
    private String id;
    private String type;
    private Translated name = new Translated();
    private String location;

    public static final Comparator<Item> orderByName = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    public static final Comparator<Item> orderByID = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.getID().compareTo(rhs.getID());
        }
    };

    public static final Comparator<Item> orderByType = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.getType().compareTo(rhs.getType());
        }
    };

    public Item(JSONObject json) throws JSONException
    {
        this._id = -(++nid);
        parse(json);
    }

    public Item(String id, String name)
    {
        this._id = -(++nid);
        this.id = id;
        this.name.set(name);
    }

    public Item(int uid, String id)
    {
        this._id = uid;
        this.id = id;
    }

    public int getUID() {
        return _id;
    }

    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Translated getNameObject() { return name; }

    public void setName(Translated translated) { name = translated; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean validate()
    {
        if( id == null || id.isEmpty() )
        {
            return false;
        }

        String name = getName();
        if( name == null || name.isEmpty() )
        {
            return false;
        }

        return true;
    }

    public boolean contains(String key)
    {
        if(key.equals(id))
        {
            return true;
        }

        if( name.contains(key) )
        {
            return true;
        }

        if(location.toLowerCase().contains(key))
        {
            return true;
        }

        if(type.toLowerCase().contains(key))
        {
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();

        try {
            json.put( COLUMN_ID , id);
            json.put( COLUMN_TYPE , type);
            json.put( COLUMN_LOCATION , location);
            json.put( COLUMN_NAME , name.toJSON() );
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException {
        id = json.getString( COLUMN_ID );
        type = json.getString( COLUMN_TYPE );
        location = json.getString( COLUMN_LOCATION );
        name.parse( json.getJSONObject( COLUMN_NAME ) );
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_id);
        out.writeString(id);
        out.writeString(type);
        out.writeParcelable(name, 0);
        out.writeString(location);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        _id = in.readInt();
        id = in.readString();
        type = in.readString();
        name = (Translated)in.readParcelable(Translated.class.getClassLoader());
        location = in.readString();
    }
}
