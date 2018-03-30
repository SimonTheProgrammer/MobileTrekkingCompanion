package com.example.maurer.sensorstream.web.Weather_Models;

/**
 * Created by Kalti on 11.02.2018.
 */

public class Temperature {
    private double temp;
    private float minTemp;
    private float maxTemp;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }
}
