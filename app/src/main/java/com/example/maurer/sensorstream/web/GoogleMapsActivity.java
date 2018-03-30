package com.example.maurer.sensorstream.web;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.example.maurer.sensorstream.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.web.GoogleMaps_Classes.ElevationData;
import com.example.maurer.sensorstream.web.GoogleMaps_Classes.ElevationDataModel;
import com.example.maurer.sensorstream.web.GoogleMaps_Classes.GoogleMapsData;
import com.example.maurer.sensorstream.web.GoogleMaps_Classes.GoogleMapsDataParser;
import com.example.maurer.sensorstream.web.GoogleMaps_Classes.GoogleMapsHttpClient;
import com.example.maurer.sensorstream.web.Model_Classes.PlaceInformation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,  GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {
//region variables
    private static final String TAG = "MapActivity";
    //Google-map permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Map Zoom
    private static final float DEFAULT_ZOOM = 15;

    //Constanten
    public static final String GEO_LOCATION = "geoLocation";
    private static final String SELECTED_STYLE = "selected_style";

    //Variablen (GoogleMaps)
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    protected GeoDataClient mGeoDataClient;

    //Map-init LatLng (full covered map)
    private static  final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168), new LatLng(71,136));
    public String layoutStyle = "Default";
    public MapStyleOptions style;

    //Placepicker + Information
    private static final int PLACE_PICKER_REQUEST = 1;
    private PlaceInformation mPlace;

    //Markers (Origin, Destination)
    private Marker currentLocationMarker;
    private Marker mMarker;
    private Marker destinationMarker;
    private MarkerOptions markerOptionsDestination;

    //lat, longs
    double latitude, longitude;
    double end_latitude, end_longitude;

    //SharedPreferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //Calculation (Duration + Distance)
    Button calcDuration;
    Button calcHeight;
    //lat, longs (destination, origin)
    Double latitudeDestinationA, longitudeDestinationA;
    Double latitudeDestinationB, longitudeDestinationB;

    //Marker (destination, origin)
    private MarkerOptions mMarkerOptionsA;
    private Marker destinationMarkerA;
    private MarkerOptions mMarkerOptionsB;
    private Marker destinationMarkerB;

    //Views + Activities
    TextView durationView;
    TextView distanceView;
    GoogleMapsData googleMapsData = new GoogleMapsData();
    String mode = "walking";
    ElevationDataModel elevationDataModel;
    ArrayList<ElevationData> elevationArrayList = new ArrayList<>();
    ElevationGraphActivity elevationGraphActivity = new ElevationGraphActivity();
    ArrayList<Polyline> polylines= new ArrayList<>();
    int samplerate = 100;


    //Widget
    private AutoCompleteTextView editTextSearch;
    private AutoCompleteTextView editTextSearch2;
    private ImageView mGps;
    private ImageView mplacePicker;
    private ImageView mInfo;
    private ImageView mDistance;
    private ImageView travelMode;


//endregion variables



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);


        preferences = getSharedPreferences("maptype",MODE_PRIVATE);
        editor = preferences.edit();


        //init view-components
        distanceView = (TextView) findViewById(R.id.distanceBetweenAB);
        durationView = (TextView) findViewById(R.id.PeriodBetweenAB);
        calcDuration = (Button)findViewById(R.id.findpath);
        calcHeight = (Button)findViewById(R.id.hightprofile);
        mDistance = (ImageView) findViewById(R.id.distance);
        mInfo = (ImageView) findViewById(R.id.place_info);
        mplacePicker = (ImageView) findViewById(R.id.placePicker);
        editTextSearch = (AutoCompleteTextView) findViewById(R.id.mapSearchText);
        editTextSearch2 = (AutoCompleteTextView)findViewById(R.id.mapSearchText2);
        mGps = (ImageView)findViewById(R.id.ic_gps);
        mGps.bringToFront();

        travelMode = (ImageView)findViewById(R.id.travelmodeView);
        mGps.setClickable(true);
        mGeoDataClient = Places.getGeoDataClient(this, null);

        checkPermission();



    }




    //Event-Handler => durch welche Aktion wird nach einer "Addresse" auf der Karte gesucht => Trigger-Event
    private void initialize(){


        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient ,LAT_LNG_BOUNDS,null);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this,this).build();
        editTextSearch.setAdapter(placeAutocompleteAdapter);
        editTextSearch2.setAdapter(placeAutocompleteAdapter);

//region view.setOnClickListener
        calcHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coord;


                if(latitudeDestinationA == null
                        && longitudeDestinationA == null){

                    Toast.makeText(GoogleMapsActivity.this,"Choose a 'origin' location", Toast.LENGTH_SHORT).show();

                }else if(latitudeDestinationB == null
                        && longitudeDestinationB == null){

                    Toast.makeText(GoogleMapsActivity.this,"Choose a 'destination' location", Toast.LENGTH_SHORT).show();

                }else if(latitudeDestinationA != null
                        && longitudeDestinationA != null
                        && latitudeDestinationB != null
                        && longitudeDestinationB != null){

                    coord ="path="+latitudeDestinationA +","+longitudeDestinationA+"|"+latitudeDestinationB+","+longitudeDestinationB+"&samples="+samplerate;

                    if(coord == null ||
                            coord.equals("")){
                        //Do nothing
                        Log.d(TAG, "Nothing to do");
                    }else{

                        loadElevationData(coord);

                    }
                }
            }
        });

        calcDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String coord;
                if(polylines != null){
                    for(Polyline line : polylines)
                    {
                        line.remove();
                    }

                }

                if(latitudeDestinationA == null && longitudeDestinationA == null){

                    Toast.makeText(GoogleMapsActivity.this,"Choose a 'origin' location", Toast.LENGTH_SHORT).show();

                }else if(latitudeDestinationB == null && longitudeDestinationB == null){

                    Toast.makeText(GoogleMapsActivity.this,"Choose a 'destination' location", Toast.LENGTH_SHORT).show();

                }else if(latitudeDestinationA != null && longitudeDestinationA != null && latitudeDestinationB != null && longitudeDestinationB != null){

                    coord ="&mode="+mode+ "&origin="+latitudeDestinationA +","+longitudeDestinationA+"&destination="+latitudeDestinationB+","+longitudeDestinationB;

                    if(coord == null ||
                            coord.equals("")){
                    }else{
                        loadGoogleMapsdata(coord);
                    }
                }
            }
        });



        editTextSearch2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int EditorActionID, KeyEvent keyEvent) {
                if(EditorActionID == EditorInfo.IME_ACTION_SEARCH
                        || EditorActionID == EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                        ){

                    geoLocate2();

                }

                return false;

            }

        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int EditorActionID, KeyEvent keyEvent) {
                if(EditorActionID == EditorInfo.IME_ACTION_SEARCH
                        || EditorActionID == EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                        ){

                    geoLocate();

                }

                return false;

            }

        });

        travelMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenuTravelMode = new PopupMenu(GoogleMapsActivity.this, travelMode);
                popupMenuTravelMode.getMenuInflater().inflate(R.menu.travelmode_menu, popupMenuTravelMode.getMenu());

                popupMenuTravelMode.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch(item.getItemId()){
                            case R.id.modeWalking:
                                mode = "walking";
                                return true;
                            case R.id.modeBicycling:
                                mode = "bicycling";
                                return true;
                            case R.id.modeDriving:
                                mode = "driving";
                                return true;
                        }
                        return true;
                    }
                });
                popupMenuTravelMode.show();

            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getLocation();
           }
        });

        mplacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(intentBuilder.build(GoogleMapsActivity.this),PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }
            }
        });

        mDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Destination();

                noti();
                if(markerOptionsDestination != null){
                    calculateDistance();
                }
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {


                if(markerOptionsDestination != null){
                    destinationMarker.remove();
                }
                end_longitude = point.longitude;
                end_latitude = point.latitude;
                markerOptionsDestination = new MarkerOptions().position(
                        new LatLng(end_latitude,end_longitude)).title("Selected Destination");

                destinationMarker = mMap.addMarker(markerOptionsDestination);
            }
        });
//endregion view.setOnClickListener

        hideKeyboard();
    }
    private void noti(){
        if(markerOptionsDestination == null){
            Toast.makeText(this, "Select Destination", Toast.LENGTH_SHORT).show();

        }
    }

    private void calculateDistance(){


        float results[] = new float[20];

        Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);
        //markerOptionsDestination.snippet("Distance =" +results[0] + " m");
        Toast.makeText(this, String.valueOf("Distance = " + results[0] + " m"), Toast.LENGTH_LONG).show();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> pendingResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                pendingResult.setResultCallback(UpdatePlaceDetailsCallback);


            }
        }
    }


    //No more used (maybe for different purpose)
    private void Destination(){
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(end_latitude,end_longitude));
        markerOptions.title("Destination");

        float results[] = new float[20];
        Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);
        markerOptions.snippet("Distance = " + results[0]);
        mMap.addMarker(markerOptions);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

//region async-tasks
//Async-Task load directions data (distance + duration)
    public void loadGoogleMapsdata(String coord){
        GoogleMapsDirectionsTask googleMapsDirectionsTask = new GoogleMapsDirectionsTask();
        googleMapsDirectionsTask.execute(new String[]{coord});
    }

    public void loadElevationData(String coord){
        GoogleMapsElevationTask googleMapsElevationTask = new GoogleMapsElevationTask();
        googleMapsElevationTask.execute(new String[]{coord});
    }

    private class GoogleMapsElevationTask extends AsyncTask<String, Void, ElevationDataModel> {

        @Override
        protected ElevationDataModel doInBackground(String... strings) {
            String data = new GoogleMapsHttpClient().getElevationData(strings[0]);
            if(data != null){

                elevationDataModel = GoogleMapsDataParser.getElevationData(data);

            }
            return elevationDataModel;
        }

        @Override
        protected void onPostExecute(ElevationDataModel elevationDataModel) {
            super.onPostExecute(elevationDataModel);
            if(elevationDataModel != null) {
                elevationArrayList= elevationDataModel.getElevationDataList();


                elevationGraphActivity.setElevationDataArrayList(elevationArrayList);


                Intent intent = new Intent(GoogleMapsActivity.this, ElevationGraphActivity.class);
                for(int i = 0; i< elevationArrayList.size();i++){
                    ElevationData elevationData = elevationArrayList.get(i);
                    double elevation = elevationData.getElevation();
                    intent.putExtra("data"+i,elevation);
                    intent.putExtra("samplerate",samplerate);
                }

                startActivity(intent);




            }

        }

    }





    private class GoogleMapsDirectionsTask extends AsyncTask<String, Void, GoogleMapsData> {

        @Override
        protected GoogleMapsData doInBackground(String... strings) {
            String data = new GoogleMapsHttpClient().getDirectionsData(strings[0]);

            if(data != null){
                googleMapsData = GoogleMapsDataParser.getMapsData(data);
            }
            return googleMapsData;
        }

        @Override
        protected void onPostExecute(GoogleMapsData googleMapsData) {
            super.onPostExecute(googleMapsData);
            String[] directionsList;
            if(googleMapsData != null){
                String duration = googleMapsData.getDuration();
                String distance = googleMapsData.getDistance();
                directionsList = googleMapsData.getPaths();
                displayDirection(directionsList);
                durationView.setText(duration);
                distanceView.setText(distance);
            }
        }
    }

//endregion async-tasks


    public void displayDirection(String[] directionsList){

        Polyline polyline;
        int count = directionsList.length;
        for(int i = 0; i<count; i++){

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(10);
            polylineOptions.addAll(PolyUtil.decode(directionsList[i]));

            polyline = mMap.addPolyline(polylineOptions);
            polylines.add(polyline);
        }
    }




//region geolocate
//"Addresse" eingeben und dieser auf der Karte anzeigen
    private void geoLocate(){
        Log.d(TAG, "geoLocate()");

        String searchString = editTextSearch.getText().toString();

        Geocoder geocoder = new Geocoder(GoogleMapsActivity.this);
        List<Address> addresses = new ArrayList<>();

        try{
            addresses = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate(): IOException: " + e.getMessage());
        }

        if(addresses.size() > 0){
            Address address = addresses.get(0);
            Toast.makeText(this, addresses.get(0).toString(), Toast.LENGTH_SHORT);
            Log.d(TAG,"geoLocate" + address.toString());


            try{
                if(mMarkerOptionsA != null){
                    destinationMarkerA.remove();

                }


                latitudeDestinationA = address.getLatitude();
                longitudeDestinationA = address.getLongitude();
                mMarkerOptionsA = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude()));
                mMarkerOptionsA.title("Origin");
                destinationMarkerA = mMap.addMarker(mMarkerOptionsA);

                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
            }catch (NullPointerException e){
                Log.e(TAG, "onComplete: NullPointerException: " +e.getMessage() );
            }

        }
    }

    private void geoLocate2(){
        Log.d(TAG, "geoLocate()");

        String searchString = editTextSearch2.getText().toString();

        Geocoder geocoder = new Geocoder(GoogleMapsActivity.this);
        List<Address> addresses = new ArrayList<>();

        try{
            addresses = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate(): IOException: " + e.getMessage());
        }

        if(addresses.size() > 0){
            Address address = addresses.get(0);
            Toast.makeText(this, addresses.get(0).toString(), Toast.LENGTH_SHORT);
            Log.d(TAG,"geoLocate" + address.toString());


            try{

                if(mMarkerOptionsB != null){
                    destinationMarkerB.remove();

                }


                latitudeDestinationB = address.getLatitude();
                longitudeDestinationB = address.getLongitude();
                mMarkerOptionsB = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude()));
                mMarkerOptionsB.title("Destination");
                destinationMarkerB = mMap.addMarker(mMarkerOptionsB);



                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
            }catch (NullPointerException e){
                Log.e(TAG, "onComplete: NullPointerException: " +e.getMessage() );
            }

        }
    }




//Aktuelle Position auf der Karte finden => beim start der App
    public void getLocation() {

        if(currentLocationMarker != null){
            currentLocationMarker.remove();

        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Location clocation = (Location) task.getResult();

                        try{
                            latitude = clocation.getLatitude();
                            longitude = clocation.getLongitude();
                            moveCamera(new LatLng(clocation.getLatitude(), clocation.getLongitude()), DEFAULT_ZOOM, "Current Place");
                        }catch (NullPointerException e){
                            Log.e(TAG, "onComplete: NullPointerException: " +e.getMessage() );
                        }


                    }
                });
            }
        } catch (SecurityException e) {

        }

    }
    //endregion geolocate


//region camera-navigation
    //Camera "Map-Position" je nach "Event", verändern. (Start der App, Suche nach einer "Addresse")
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + "; lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
/*
        if(!title.equals("Current Place")){
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
        */

    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInformation placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new InfoWindowAdapter(GoogleMapsActivity.this));

        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n";



                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideKeyboard();
    }
    //endregion camera-navigation

    //Verstecken des SoftKeyboards nach der Eingabe einer Addresse (funktioniert derzeit nichtmehr ?!?)
    private void hideKeyboard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextSearch2.getWindowToken(), 0);
    }


//region map-creation
    //User nach Berächtigung abfragen => darf die App auf den Standort des Users zugreifen, etc.?
    private void checkPermission() {
        Log.d(TAG, "checkPermission: checking location Permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called onRequestPermissionsResult");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission denied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //Init map

                    initMap();
                }
            }
        }
    }

    //Map-Fragment "initiallisieren"
    private void initMap() {
        Log.d(TAG, "initMap: init your map");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(GoogleMapsActivity.this);
    }

    //Erstellen der Map+Layout
    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        setSelectedStyle();


        if (mLocationPermissionsGranted) {
            getLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            initialize();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent switchActivityToSettings = new Intent(this, SettingsActivity.class);
            startActivity(switchActivityToSettings);

            //showStylesDialog();
        }else if(item.getItemId() == R.id.samplerate_setting){

            final EditText editText;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Change Samplerate");
            editText = new EditText(this);
            alertDialog.setView(editText);

            alertDialog.setMessage("Enter value between 2-500");
            alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    samplerate = Integer.parseInt(editText.getText().toString());
                }
            });
            Dialog dialog = alertDialog.create();
            dialog.show();
        }
        return true;
    }


//Überprüfen welcher LayoutStyle in den Settings ausgewählt wurde
    private void setSelectedStyle() {
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            layoutStyle = bundle.getString("STYLEVALUE");

            editor.putString("maptype",layoutStyle);
            editor.commit();
            layoutStyle = preferences.getString("maptype","");
        MapStyleOptions style;
        switch (layoutStyle) {
            case "Retro":

                //Layout der Map basierend auf ein JSON-File
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
                break;
            case "Night":

                //Layout der Map basierend auf ein JSON-File
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                break;
            case "Default":

                style = null;
                break;
            default:
                return;
        }
        mMap.setMapStyle(style);

    }else{
            layoutStyle = preferences.getString("maptype","");
            MapStyleOptions style;
            switch (layoutStyle) {
                case "Retro":

                    //Layout der Map basierend auf ein JSON-File
                    style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
                    break;
                case "Night":

                    //Layout der Map basierend auf ein JSON-File
                    style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                    break;
                case "Default":

                    style = null;
                    break;
                default:
                    return;
            }
            mMap.setMapStyle(style);
        }
    }

    //endregion map-creation


//region nearby-places
//Plätze in der Umgebung
    private AdapterView.OnItemClickListener AutocompleteClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeBufferPendingResult.setResultCallback(UpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> UpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "Place query did not complete successfully "+ places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try{
            mPlace = new PlaceInformation();
            mPlace.setName(place.getName().toString());
            mPlace.setAddress(place.getAddress().toString());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            mPlace.setId(place.getId());
            mPlace.setWebsiteUri(place.getWebsiteUri());
            mPlace.setLatlng(place.getLatLng());
            mPlace.setRating(place.getRating());



                Log.d(TAG, "onResult: UpdatePlaceDetailsCallback: "+ mPlace.toString());

            }catch (NullPointerException e){
                Log.d(TAG, "NullPointerException "+ e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);
            places.release();

        }
    };

//endregion nearby-places


//region marker-events

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentLocationMarker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        end_latitude = currentLocationMarker.getPosition().latitude;
        end_longitude = currentLocationMarker.getPosition().longitude;
    }

    //endregion marker-events
}
