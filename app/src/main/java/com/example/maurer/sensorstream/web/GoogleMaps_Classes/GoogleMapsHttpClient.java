package com.example.maurer.sensorstream.web.GoogleMaps_Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kalti on 14.03.2018.
 */

public class GoogleMapsHttpClient {
    public String getDirectionsData(String directionsLocation){

        HttpURLConnection connection = null;
        String result = "";
        InputStream is = null;
        try {

            URL url = new URL(GoogleMapsRequest.BASE_URL + directionsLocation + GoogleMapsRequest.API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();



            //Auslesen der Daten
            StringBuffer stringBuffer = new StringBuffer();
            is = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(is)));
            String line = "";

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            is.close();
            connection.disconnect();
            return stringBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getElevationData(String ElevationLocation){

        HttpURLConnection connection = null;
        String result = "";
        InputStream is = null;
        try {

            URL url = new URL(GoogleMapsRequest.ELEVATION_URL + ElevationLocation + GoogleMapsRequest.API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();



            //Auslesen der Daten
            StringBuffer stringBuffer = new StringBuffer();
            is = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(is)));
            String line = "";

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            is.close();
            connection.disconnect();
            return stringBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
