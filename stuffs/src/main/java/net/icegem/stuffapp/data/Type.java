package net.icegem.stuffapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import net.icegem.stuffapp.Jasonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mikael.korpela on 27.5.2015.
 */
public class Type implements Parcelable, Jasonable {
    public static final String PLURAL = "Types";

    // Actions
    public static final String EDIT_ACTION = "Type_Edit_Action";
    public static final String VIEW_ACTION = "Type_View_Action";

    // universal column strings
    public static final String COLUMN_IDENTIFIER = "_id";
    public static final String COLUMN_NAME = "name";

    // Members
    private static int nid = 10;

    private int _id;
    private Text name = new Text();;

    public Type() {
        _id = -(++nid);
    }

    public Type(int id) {
        _id = id;
    }

    public Type(JSONObject json) throws JSONException {
        parse(json);
    }

    public int getId() {
        return _id;
    }

    public void resetId() {
        _id = -(++nid);
    }

    public void setId( int id ) {
        _id = id;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Text getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public int compareTo(Type other) {
        return name.compareTo(other.name);
    }

    public boolean equals(Type other) {
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
    }

    public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
        public Type createFromParcel(Parcel in) {
            return new Type(in);
        }

        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

    private Type(Parcel in) {
        _id = in.readInt();
        name = in.readParcelable(Text.class.getClassLoader());
    }

    /*
    If any of the translations contains the "key" value.
     */
    public boolean contains(String key)    {
        return name.contains(key);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(COLUMN_IDENTIFIER, _id);
            json.put(COLUMN_NAME, name.toJSON());
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    @Override
    public void parse(JSONObject json) throws JSONException {
        _id = json.getInt(COLUMN_IDENTIFIER);
        name = new Text(json.getJSONObject(COLUMN_NAME));
    }
}
