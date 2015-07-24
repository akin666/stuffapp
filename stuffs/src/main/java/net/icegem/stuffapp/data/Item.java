package net.icegem.stuffapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import net.icegem.stuffapp.Helpers;
import net.icegem.stuffapp.Jasonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by mikael.korpela on 12.5.2015.
 */
public class Item implements Parcelable, Jasonable {
    public static final String PLURAL = "Items";

    // Actions
    public static final String EDIT_ACTION = "Item_Edit_Action";

    // DB Strings
    public static final String COLUMN_IDENTIFIER = "_id";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_LOCATION = "location";

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
    private Text name = new Text();
    private Text description = new Text();
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

    public void resetId() {
        _id = -(++nid);
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        if( name == null ) {
            this.name = new Text();
            return;
        }
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        if( description == null ) {
            this.description = new Text();
            return;
        }
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

    public Uri getPictureUri() {
        if( picture == null || picture.isEmpty() ) {
            return null;
        }

        return Uri.parse(picture);
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

    public boolean equals(Item other) {
        if( collection.getId() != other.getCollection().getId() ) {
            return false;
        }
        return volume.equals(other.getVolume());
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
        out.writeString(link);
        out.writeString(volume);
        out.writeString(picture);
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
        link = in.readString();
        volume = in.readString();
        picture = in.readString();
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
            json.put( COLUMN_LINK , link );
            json.put( COLUMN_VOLUME , volume );
            json.put( COLUMN_PICTURE , picture );
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
        try {
            code = json.getString(COLUMN_CODE);
        } catch( Exception e ) {}
        try {
            link = json.getString(COLUMN_LINK);
        } catch( Exception e ) {}
        try {
            volume = json.getString(COLUMN_VOLUME);
        } catch( Exception e ) {}
        try {
            picture = json.getString(COLUMN_PICTURE);
        } catch( Exception e ) {}
        try {
            location = json.getString(COLUMN_LOCATION);
        } catch( Exception e ) {}
    }
}
