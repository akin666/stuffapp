package net.icegem.stuffapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mikael.korpela on 22.5.2015.
 */
public interface Jasonable
{
    JSONObject toJSON();
    void parse(JSONObject json) throws JSONException;
}
