package com.example.maurer.sensorstream.web.GoogleMaps_Classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kalti on 16.03.2018.
 */

public class GoogleMapsRequest {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String ELEVATION_URL = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String API_KEY = "&key=AIzaSyBPjxs15bMCLo8Gfrw35akM6AJaS86mcv8";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException {
        JSONObject jsonObject1 = jsonObject.getJSONObject(tagName);
        return jsonObject1;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException {
        return (float)jsonObject.getDouble(tagName);
    }

    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt(tagName);
    }
}
