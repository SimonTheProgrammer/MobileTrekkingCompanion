package com.example.maurer.sensorstream.web.Model_Classes;

import com.example.maurer.sensorstream.web.Weather_Models.Place;
import com.example.maurer.sensorstream.web.Weather_Models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kalti on 11.02.2018.
 */

public class JSONWeatherParser {
    public static Weather getWeather(String data) {

        Weather weather = new Weather();

        try {

            JSONObject jsonObject = new JSONObject(data);

            //init JSON-Data
            Place place = new Place();

            JSONObject coordObj = WeatherRequest.getObject("coord", jsonObject);
            place.setLon(WeatherRequest.getFloat("lon", coordObj));
            place.setLat(WeatherRequest.getFloat("lat", coordObj));
            weather.place = place;

            JSONObject sysObj = WeatherRequest.getObject("sys", jsonObject);

            if (sysObj.isNull("country"))
            {
                place.setCountry("?");
            }else{

                place.setCountry(WeatherRequest.getString("country", sysObj));
            }


            if (jsonObject.isNull("name")||WeatherRequest.getString("name",jsonObject).equals("")){

                place.setCity("Unknown");

            }else{

                place.setCity(WeatherRequest.getString("name", jsonObject));

            }


            if (jsonObject.isNull("dt")){
                place.setLastupdate(0);
            }else{
                place.setLastupdate(WeatherRequest.getInt("dt", jsonObject));
            }


            if (sysObj.isNull("sunrise")){

                place.setSunrise(0);
            }else{
                place.setSunrise(WeatherRequest.getInt("sunrise", sysObj));
            }


            if (sysObj.isNull("sunset")){

                place.setSunset(0);
            }else{
                place.setSunset(WeatherRequest.getInt("sunset", sysObj));
            }


            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeatherObject = jsonArray.getJSONObject(0);

            if (jsonWeatherObject.isNull("id")){
                weather.currentCondition.setWeatherId(0);
            }else{
                weather.currentCondition.setWeatherId(WeatherRequest.getInt("id", jsonWeatherObject));
            }

            if (jsonWeatherObject.isNull("description")||WeatherRequest.getString("description",jsonWeatherObject).equals("")){
            }else{
                weather.currentCondition.setDescription(WeatherRequest.getString("description", jsonWeatherObject));
            }

            if (jsonWeatherObject.isNull("main")||WeatherRequest.getString("main",jsonWeatherObject).equals("")){
                weather.currentCondition.setCondition("");
            }else{
                weather.currentCondition.setCondition(WeatherRequest.getString("main", jsonWeatherObject));
            }

            if (jsonWeatherObject.isNull("icon")||WeatherRequest.getString("icon",jsonWeatherObject).equals("")){
                weather.currentCondition.setIcon("");
            }else{
                weather.currentCondition.setIcon(WeatherRequest.getString("icon", jsonWeatherObject));
            }


            JSONObject mainObj = WeatherRequest.getObject("main", jsonObject);
            if (mainObj.isNull("humidity")){
                weather.currentCondition.setHumidity(0);
            }else{
                weather.currentCondition.setHumidity(WeatherRequest.getInt("humidity", mainObj));
            }

            if (mainObj.isNull("pressure")){
                weather.currentCondition.setPressure(0);
            }else{
                weather.currentCondition.setPressure(WeatherRequest.getInt("pressure", mainObj));
            }

            if (mainObj.isNull("temp_min")){
                weather.currentCondition.setMinTemp(0);
            }else{
                weather.currentCondition.setMinTemp(WeatherRequest.getFloat("temp_min", mainObj));
            }
            if (mainObj.isNull("temp_max")){
                weather.currentCondition.setMaxTemp(0);
            }else{
                weather.currentCondition.setMaxTemp(WeatherRequest.getFloat("temp_max", mainObj));
            }

            if (mainObj.isNull("sunset")){
                weather.currentCondition.setMaxTemp(0);
            }else{
                weather.currentCondition.setMaxTemp(WeatherRequest.getFloat("temp_max", mainObj));
            }

            if (mainObj.isNull("temp")){
                weather.currentCondition.setTemperature(0);
            }else{
                weather.currentCondition.setTemperature(WeatherRequest.getDouble("temp", mainObj));
            }


            JSONObject windObj = WeatherRequest.getObject("wind", jsonObject);
            if (windObj.isNull("speed")){
                weather.wind.setSpeed(0);
            }else{
                weather.wind.setSpeed(WeatherRequest.getFloat("speed", windObj));
            }

            if (windObj.isNull("deg")){
                weather.wind.setDeg(0);
            }else{
                weather.wind.setDeg(WeatherRequest.getFloat("deg", windObj));
            }


            JSONObject cloudObj = WeatherRequest.getObject("clouds", jsonObject);
            if (cloudObj.isNull("all")){
                weather.clouds.setPrecipitation(0);
            }else{
                weather.clouds.setPrecipitation(WeatherRequest.getInt("all", cloudObj));
            }


            return weather;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
