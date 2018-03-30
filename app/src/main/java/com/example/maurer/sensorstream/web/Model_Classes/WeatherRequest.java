package com.example.maurer.sensorstream.web.Model_Classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kalti on 11.02.2018.
 */

public class WeatherRequest {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String BASE_URLCOORD = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String ICON_URL = "http://openweathermap.org/img/w/";
    public static final String API_KEY = "&appid=f80945d80f217ddd75999027fedada18";

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
