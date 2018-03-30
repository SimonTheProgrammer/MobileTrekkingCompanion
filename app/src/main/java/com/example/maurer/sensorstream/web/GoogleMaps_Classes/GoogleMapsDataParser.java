package com.example.maurer.sensorstream.web.GoogleMaps_Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kalti on 16.03.2018.
 */

public class GoogleMapsDataParser {
    public static GoogleMapsData getMapsData(String data){

        GoogleMapsData googleMapsData = new GoogleMapsData();
        String distance;
        String duration;
        String[] paths = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
            distance = jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
            duration = jsonArray.getJSONObject(0).getJSONObject("duration").getString("text");

            JSONArray jsonArraySteps = jsonArray.getJSONObject(0).getJSONArray("steps");
            paths = getPaths(jsonArraySteps);

            googleMapsData.setPaths(paths);
            googleMapsData.setDistance(distance);
            googleMapsData.setDuration(duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleMapsData;
    }


    public static String getPath(JSONObject jsonPath){
        String polyline = "";
        try {
            polyline = jsonPath.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

    public static String[] getPaths(JSONArray jsonSteps){
        int jsonsize = jsonSteps.length();
        String[] polylines = new String[jsonsize];

        for(int i = 0; i<jsonsize;i++){

            try {
                polylines[i]= getPath(jsonSteps.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;

    }


    public static ElevationDataModel getElevationData(String data){
        ElevationData elevationData;
        ElevationDataModel elevationDataModel = new ElevationDataModel();
        ArrayList<ElevationData> elevationDatas = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray locationArray = jsonObject.getJSONArray("results");


            for(int i = 0; i < locationArray.length();i++){
                elevationData = new ElevationData();
                JSONObject listObject = locationArray.getJSONObject(i);
                double elevation = GoogleMapsRequest.getDouble("elevation",listObject);
                elevationData.setElevation(elevation);
                elevationDatas.add(elevationData);

            }
            elevationDataModel.setElevationDataList(elevationDatas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elevationDataModel;
    }




}

