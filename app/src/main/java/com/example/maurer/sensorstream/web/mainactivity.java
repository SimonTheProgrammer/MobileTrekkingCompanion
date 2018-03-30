package com.example.maurer.sensorstream.web;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.maurer.sensorstream.R;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class mainactivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "GoogleMapsActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private LocationManager locationManager;
    double latitute;
    double longitude;
    Geocoder geocoder;
    List<Address> addressList;
    private TextView coordinates;
    private TextView locationview;
    private String city;
    String coord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainweb);

        locationview = (TextView)findViewById(R.id.location);
        coordinates = (TextView)findViewById(R.id.coords);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        coord = "lat=" + String.valueOf(location.getLatitude()) +"&lon="+ String.valueOf(location.getLongitude());

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(latitute,longitude,1);

            city = addressList.get(0).getLocality();
            locationview.setText(city);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isServiceVersionOk()){
            init();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitute = location.getLatitude();
        coordinates.setText("Longititude: " + longitude + "\n" + "Latitude: " + latitute);
    }


    private void init(){
        ImageView reloadweather = (ImageView) findViewById(R.id.reloadweatheract);
        reloadweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(WeatherActivity.SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("coords",coord);
                editor.putString("city",city);
                editor.commit();

               // Toast.makeText(MainActivity.this, cityReferences.getCity().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        Button btnWeather = (Button) findViewById(R.id.btnWeather);
        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(WeatherActivity.SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(sharedPreferences.getString("coords","").equals("")){
                    editor.putString("coords",coord);
                }

                editor.putString("city",city);
                editor.commit();
                sendMessageWeather();
            }
        });


        findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageGoogleMaps();
            }
        });
    }


    public void sendMessageWeather(){
        Intent switchActivityToWeather = new Intent(this, WeatherActivity.class);
        startActivity(switchActivityToWeather);
    }
    public void sendMessageGoogleMaps(){

        Intent switchActivityToGoogleMaps = new Intent(this, GoogleMapsActivity.class);
        startActivity(switchActivityToGoogleMaps);
    }

    public boolean isServiceVersionOk(){
        Log.d(TAG,"isServiceVersionOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mainactivity.this);

        if(available == ConnectionResult.SUCCESS){
            //Richtige Version installiert
            Log.d(TAG, "isServiceVersionOk: google-play service is working!");
            return true;

        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Falsche Version vorhanden
            Log.d(TAG,"isServiceVersionOk: wrong version of the google-play service installed!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(mainactivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();

        }else{
            Toast.makeText(this, "Map request is not available", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}