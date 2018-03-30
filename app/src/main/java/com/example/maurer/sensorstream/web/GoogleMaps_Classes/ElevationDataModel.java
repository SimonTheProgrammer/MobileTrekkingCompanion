package com.example.maurer.sensorstream.web.GoogleMaps_Classes;

import java.util.ArrayList;

/**
 * Created by Kalti on 18.03.2018.
 */

public class ElevationDataModel {
    ArrayList<ElevationData> elevationDataList = new ArrayList<>();

    public ArrayList<ElevationData> getElevationDataList() {
        return elevationDataList;
    }

    public void setElevationDataList(ArrayList<ElevationData> elevationDataList) {
        this.elevationDataList = elevationDataList;
    }
}
