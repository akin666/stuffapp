package net.icegem.stuffapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import net.icegem.stuffapp.Jasonable;
import net.icegem.stuffapp.MutablePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by mikael.korpela on 25.5.2015.
 */
public class Collection implements Parcelable, Jasonable {
    public static final String PLURAL = "Collections";

    // Actions
    public static final String EDIT_ACTION = "Collection_Edit_Action";

    // DB Strings
    public static final String COLUMN_IDENTIFIER = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LINK = "link";

    // OrderBy
    public static final Comparator<Collection> orderByName = new Comparator<Collection>() {
        @Override
        public int compare(Collection lhs, Collection rhs) {
            return lhs.name.compareTo(rhs.name);
        }
    };

    // Members
    private static int nid = 10;

    private int _id;
    private Text name = new Text();
    private Text description = new Text();
    private String picture;
    private String link;

    public Collection() {
        _id = -(++nid);
    }

    public Collection(int id) {
        _id = id;
    }

    public Collection(JSONObject json) throws JSONException {
        parse(json);
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

    public Text getName()
    {
        return name;
    }

    public Text getDescription() {
        return description;
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

    public void setPicture(String value) {
        picture = value;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String value) {
        link = value;
    }

    /*
    If any of the translations contains the "key" value.
     */
    public boolean contains(String key) {
        return name.contains(key);
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public int compareTo(Collection other) {
        return name.compareTo(other.name);
    }

    public boolean equals(Collection other) {
        return name.equals(other.getName());
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_id);
        out.writeParcelable(name, flags);
        out.writeParcelable(description, flags);
        out.writeString(picture);
        out.writeString(link);
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    private Collection(Parcel in) {
        _id = in.readInt();
        name = in.readParcelable(Text.class.getClassLoader());
        description = in.readParcelable(Text.class.getClassLoader());
        picture = in.readString();
        link = in.readString();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(COLUMN_IDENTIFIER, _id);
            json.put(COLUMN_NAME, name.toJSON());
            json.put(COLUMN_DESCRIPTION, description.toJSON());
            json.put(COLUMN_PICTURE, picture);
            json.put(COLUMN_LINK, link);
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException {
        _id = json.getInt(COLUMN_IDENTIFIER);
        name = new Text(json.getJSONObject(COLUMN_NAME));
        description = new Text(json.getJSONObject(COLUMN_DESCRIPTION));
        try {
            picture = json.getString(COLUMN_PICTURE);
        } catch( Exception e ) {}
        try {
            link = json.getString(COLUMN_LINK);
        } catch( Exception e ) {}
    }
}
