package com.example.maurer.sensorstream.web.GoogleMaps_Classes;

/**
 * Created by Kalti on 16.03.2018.
 */

public class GoogleMapsData {
    String duration;
    String distance;
    String[] paths;

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
