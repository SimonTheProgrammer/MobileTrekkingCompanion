package com.example.maurer.sensorstream.web.Weather_Models;

/**
 * Created by Kalti on 01.03.2018.
 */

public class WeatherItem {
    String cityname;
    String temp;
    double latitute;
    double longitude;
    String condition;

    public WeatherItem(String cityname, String temp, double latitute, double longitude, String condition) {
        this.cityname = cityname;
        this.temp = temp;
        this.latitute = latitute;
        this.longitude = longitude;
        this.condition = condition;
    }

public double getText(){
    return latitute + longitude;
}

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
