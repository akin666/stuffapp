package net.icegem.stuffapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mikael.korpela on 25.5.2015.
 */
public class Collection implements Parcelable, Jasonable {
    // Members
    private Text name;

    public Collection() {
    }

    Text getName()
    {
        return name;
    }

    @Override
    public String toString() {
        return name.getId();
    }

    //// Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(name, flags);
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
        name = in.readParcelable(Text.class.getClassLoader());
    }

    /*
    If any of the translations contains the "key" value.
     */
    public boolean contains(String key)    {
        return name.contains(key);
    }

    @Override
    public JSONObject toJSON()    {
        return name.toJSON();
    }

    @Override
    public void parse(JSONObject json) throws JSONException    {
        name = new Text();
        name.parse(json);
    }
}
