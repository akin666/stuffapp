package net.icegem.stuffapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import net.icegem.stuffapp.Jasonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by mikael.korpela on 12.5.2015.
 */
public class Item implements Parcelable, Jasonable {
    public static final String TABLE = "Item";
    public static final String COLUMN_IDENTIFIER = "_id";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_LOCATION = "location";

    public static final String[] columns = {
            COLUMN_IDENTIFIER,
            COLUMN_COLLECTION,
            COLUMN_DESCRIPTION,
            COLUMN_TYPE,
            COLUMN_CODE,
            COLUMN_LINK,
            COLUMN_VOLUME,
            COLUMN_PICTURE,
            COLUMN_LOCATION
    };

    public static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    COLUMN_IDENTIFIER + " integer primary key autoincrement, " +
                    COLUMN_COLLECTION + " integer, " +
                    COLUMN_DESCRIPTION + " integer, " +
                    COLUMN_TYPE + " integer, " +
                    COLUMN_CODE + " text, " +
                    COLUMN_LINK + " text, " +
                    COLUMN_VOLUME + " text, " +
                    COLUMN_PICTURE + " text, " +
                    COLUMN_LOCATION + " text " +
                    ");";

    // OrderBy
    public static final Comparator<Item> orderByVolume = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.volume.compareTo(rhs.volume);
        }
    };

    public static final Comparator<Item> orderByType = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.type.compareTo(rhs.type);
        }
    };

    public static final Comparator<Item> orderByCollection = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.collection.compareTo(rhs.collection);
        }
    };

    // Members
    private static int nid = 10;

    private int _id = 0;
    private Collection collection;
    private Text description;
    private Type type;
    private String code;
    private String link;
    private String volume;
    private String picture;
    private String location;

    public Item() {
        _id = -(++nid);
    }

    public Item(JSONObject json) throws JSONException {
        parse(json);
    }

    public Item(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void setId( int id ) {
        _id = id;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection value) {
        collection = value;
    }

    public String getVolume() { return volume; }

    public void setVolume(String value) { volume = value; }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean contains(String key)
    {
        if(key.equals(code))
        {
            return true;
        }

        if( volume.contains(key) )
        {
            return true;
        }

        if(location.toLowerCase().contains(key))
        {
            return true;
        }

        if(type.contains(key))
        {
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return collection.toString() + ". " + volume;
    }

    public int compareTo(Item other) {
        return volume.compareTo(other.volume);
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_id);
        out.writeParcelable(collection, flags);
        out.writeParcelable(description, flags);
        out.writeParcelable(type, flags);
        out.writeString(code);
        out.writeString(volume);
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
        collection = in.readParcelable(Collection.class.getClassLoader());
        description = in.readParcelable(Text.class.getClassLoader());
        type = in.readParcelable(Type.class.getClassLoader());
        code = in.readString();
        volume = in.readString();
        location = in.readString();
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();

        try {
            json.put( COLUMN_IDENTIFIER , _id);
            json.put( COLUMN_COLLECTION , collection.getId());
            json.put( COLUMN_DESCRIPTION , description.toJSON());
            json.put( COLUMN_TYPE , type.getId() );
            json.put( COLUMN_CODE , code );
            json.put( COLUMN_VOLUME , volume );
            json.put( COLUMN_LOCATION , location );
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException {
        _id = json.getInt(COLUMN_IDENTIFIER);
        collection = new Collection(json.getInt(COLUMN_COLLECTION));
        description = new Text(json.getJSONObject(COLUMN_DESCRIPTION));
        type = new Type(json.getInt(COLUMN_TYPE));
        code = json.getString(COLUMN_CODE);
        volume = json.getString(COLUMN_VOLUME);
        location = json.getString(COLUMN_LOCATION);
    }

    /// DB Management
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
    }
}
