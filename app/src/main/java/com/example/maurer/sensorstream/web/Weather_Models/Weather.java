package com.example.maurer.sensorstream.web.Weather_Models;

/**
 * Created by Kalti on 11.02.2018.
 */

public class Weather {

    public Place place;
    public String iconData;
    public Temperature temperature = new Temperature();
    public current_Condition currentCondition = new current_Condition();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getIconData() {
        return iconData;
    }

    public void setIconData(String iconData) {
        this.iconData = iconData;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public current_Condition getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(current_Condition currentCondition) {
        this.currentCondition = currentCondition;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Snow getSnow() {
        return snow;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }
}
