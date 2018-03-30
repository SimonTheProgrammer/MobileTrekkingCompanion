package com.example.maurer.sensorstream.web;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DecimalFormat;
import android.support.design.widget.BottomNavigationView;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.example.maurer.sensorstream.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.web.Model_Classes.Forecast.JSONWeatherForecastParser;
import com.example.maurer.sensorstream.web.Model_Classes.Forecast.WeatherForecastHttpClient;
import com.example.maurer.sensorstream.web.Model_Classes.JSONWeatherParser;
import com.example.maurer.sensorstream.web.Model_Classes.WeatherHttpClient;
import com.example.maurer.sensorstream.web.Model_Classes.WeatherRequest;
import com.example.maurer.sensorstream.web.WeatherFragments.ForecastFragment;
import com.example.maurer.sensorstream.web.WeatherFragments.MapsFragment;
import com.example.maurer.sensorstream.web.WeatherFragments.NotificationFragment;
import com.example.maurer.sensorstream.web.WeatherFragments.WeatherFragment;
import com.example.maurer.sensorstream.web.Weather_Models.Forecast.ForecastWeatherModel;
import com.example.maurer.sensorstream.web.Weather_Models.Forecast.WeatherForecast;
import com.example.maurer.sensorstream.web.Weather_Models.Weather;
import com.example.maurer.sensorstream.web.Weather_Models.WeatherItem;
import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements LocationListener{

    //Variablen + Views
    public static final String SHARED_PREFS = "cityReferences";
    public static String SHARED_VALUE = "";
    public static final String TAG_State ="WeatherActivitycurrSt";


    private String coord;
    private static final String TAG = "WeatherActivity";





    //Coordinaten
    double latitute;
    double longitude;
    private LocationManager locationManager;


    //Fragments
    private BottomNavigationView bottomNavigationView;
    private Button accept;
    private FrameLayout weatherframeLayout;
    private ForecastFragment forecastFragment;
    private NotificationFragment notificationFragment;
    private MapsFragment mapsFragment;
    private WeatherFragment weatherFragment;
    private ArrayList<WeatherItem> weatherItemArrayList;
    private ArrayList<String> cityList;
    SharedPreferences sharedPreferences;
    String unknownplace = "";




    Weather weather = new Weather();
    ArrayList<WeatherForecast> weatherForecastArrayList = new ArrayList<>();
    ForecastWeatherModel forecastWeatherModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        Log.e(TAG_State,"onCreate called");






        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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





        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();




        //Toast.makeText(this,sharedPreferences.getString("coords",""),Toast.LENGTH_SHORT).show();





        //init-Fragments
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.weathernav);
        weatherframeLayout = (FrameLayout)findViewById(R.id.weatherframe);


        weatherFragment = new WeatherFragment();
        forecastFragment = new ForecastFragment();
        notificationFragment = new NotificationFragment();
        mapsFragment = new MapsFragment();
        loadData();
        notificationFragment.setWeatherItemsList(weatherItemArrayList);
        setFragment(weatherFragment);







        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.nav_weather:

                        saveData();
                        setFragment(weatherFragment);
                        if(internet_connection_status()) {
                            loadWeatherData(sharedPreferences.getString("coords", ""));
                            loadForecastWeatherData(sharedPreferences.getString("coords",""));
                        }else{
                            internetConnectionBuilder(WeatherActivity.this).show();
                        }
                        return true;
                    case R.id.nav_forecast:
                        saveData();
                        if(internet_connection_status()) {

                            if(weatherForecastArrayList != null){
                                forecastFragment.setWeatherForecastArrayList(weatherForecastArrayList);
                            }

                            loadForecastWeatherData(sharedPreferences.getString("coords",""));
                        }else{
                            internetConnectionBuilder(WeatherActivity.this).show();
                        }
                        setFragment(forecastFragment);
                        return true;
                    case R.id.nav_notification:
                        saveData();
                        setFragment(notificationFragment);
                        return true;
                    default:
                        return false;



                }

            }
        });
/*
        placeName = (TextView) findViewById(R.id.placeTextView);
        temp = (TextView) findViewById(R.id.tempTextView);
        clouds = (TextView) findViewById(R.id.cloudTextView);
        pressure = (TextView) findViewById(R.id.pressureTextView);
        wind = (TextView) findViewById(R.id.windTextView);
        humidity = (TextView) findViewById(R.id.humidityTextView);
        sunrise = (TextView) findViewById(R.id.sunriseTextView);
        sunset = (TextView) findViewById(R.id.sunsetTextView);
        weatherimg = (ImageView) findViewById(R.id.weathericon);
*/



        if(internet_connection_status()){
            loadWeatherData(sharedPreferences.getString("coords", ""));
            loadForecastWeatherData(sharedPreferences.getString("coords",""));


        }else{
            internetConnectionBuilder(WeatherActivity.this).show();
        }



    }
//region Standard-Events
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG_State,"onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG_State,"onResume called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG_State,"onRestart called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG_State,"onStop called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG_State,"onSaveInstanteState called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG_State,"onRestoreInstanceState called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG_State,"onDestroy called");
    }

    //endregion Standard-Events

    @Override
    public void onLocationChanged(Location location) {

        longitude = location.getLongitude();
        latitute = location.getLatitude();


    }


    //region Datacontroll
    private void saveData(){
        SharedPreferences sharedPreferencesSaveArray = getSharedPreferences("ArrayDataCurrentWeather",MODE_PRIVATE);
        SharedPreferences.Editor editorSaveArray = sharedPreferencesSaveArray.edit();
        Gson gson = new Gson();
        String json = gson.toJson(weatherItemArrayList);
        editorSaveArray.putString("ArrayDataCurrentWeather",json);
        editorSaveArray.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferencesSaveArray = getSharedPreferences("ArrayDataCurrentWeather",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferencesSaveArray.getString("ArrayDataCurrentWeather","");
        Type type = new TypeToken<ArrayList<WeatherItem>>(){}.getType();
        weatherItemArrayList = gson.fromJson(json,type);


        if(weatherItemArrayList == null){
            weatherItemArrayList = new ArrayList<>();
        }
    }

//endregion Datacontroll




    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.weatherframe,fragment);
        fragmentTransaction.commit();

    }

    public void loadImage(String code){
        DownloadImage downloadImage = new DownloadImage();
        downloadImage.execute(WeatherRequest.ICON_URL+code+".png");

    }

    public void loadWeatherData(String coord){

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{coord +"&appid=f80945d80f217ddd75999027fedada18"+ "&units=metric"});

    }
    public void loadForecastWeatherData(String coord){
        WeatherForecastTask forecastTask = new WeatherForecastTask();
        forecastTask.execute(new String[]{coord +"&appid=f80945d80f217ddd75999027fedada18"+ "&units=metric"});
    }



    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            String urlstr = urls[0];
            Bitmap bitmap = null;

            try {
                if(!urlstr.equals("")|| urlstr != null){
                    InputStream inputStream = new URL(urlstr).openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            super.onPostExecute(bitmap);
            if(internet_connection_status()) {
                if(bitmap != null){
                    /*
                    ((ImageView) weatherFragment.getView().findViewById(R.id.weathericon)).setImageBitmap(bitmap);
                    */
                }

                //weatherimg.setImageBitmap(bitmap);
            }else{
                internetConnectionBuilder(WeatherActivity.this).show();
            }
        }

    }

private class WeatherForecastTask extends AsyncTask<String, Void, ForecastWeatherModel> {

    @Override
    protected ForecastWeatherModel doInBackground(String... strings) {

        String data = new WeatherForecastHttpClient().getWeatherData(strings[0]);
        if(data != null){

            forecastWeatherModel = JSONWeatherForecastParser.getForecastWeather(data);

        }
        return forecastWeatherModel;
    }

    @Override
    protected void onPostExecute(ForecastWeatherModel forecastWeatherModel) {
        super.onPostExecute(forecastWeatherModel);
        if(forecastWeatherModel != null) {
            weatherForecastArrayList= forecastWeatherModel.getForecastArrayList();



        }
    }
}




    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... strings) {


            String data = ((new WeatherHttpClient()).getWeatherData(strings[0]));

            if(data != null){
                weather = JSONWeatherParser.getWeather(data);

            }
            return weather;

        }




        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
        if(weather != null) {


            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            long dvsunrise = Long.valueOf(weather.place.getSunrise()) * 1000;
            Date dfsunrise = new java.util.Date(dvsunrise);
            String sunriseValue = new SimpleDateFormat("HH:mm").format(dfsunrise);

            long dvupdate = Long.valueOf(weather.place.getLastupdate())*1000;
            Date dfupdate = new java.util.Date(dvupdate);
            String updateValue = new SimpleDateFormat("EEEE dd/MM/yyyy  HH:mm", Locale.ENGLISH).format(dfupdate);


            float windspeed = (float) (weather.wind.getSpeed() * 1.61);

            long dvsunset = Long.valueOf(weather.place.getSunset()) * 1000;
            Date dfsunset = new java.util.Date(dvsunset);
            String sunsetValue = new SimpleDateFormat("HH:mm").format(dfsunset);



            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());
            double loncurrent = weather.place.getLon();
            double latcurrent = weather.place.getLat();;

            weather.iconData = weather.currentCondition.getIcon();
           /* if (internet_connection_status()) {
                loadImage(weather.iconData);
            } else {
                internetConnectionBuilder(WeatherActivity.this).show();
            }
*/



            ((TextView) weatherFragment.getView().findViewById(R.id.placeTextView)).setText(weather.place.getCity() + "," + weather.place.getCountry());
            ((TextView) weatherFragment.getView().findViewById(R.id.tempTextView)).setText(tempFormat + "°C");
            ((TextView) weatherFragment.getView().findViewById(R.id.cloudTextView)).setText(weather.currentCondition.getCondition());
            ((TextView) weatherFragment.getView().findViewById(R.id.pressureTextView)).setText(weather.currentCondition.getPressure() + "hPa");
            ((TextView) weatherFragment.getView().findViewById(R.id.windTextView)).setText(windspeed + " km/h");
            ((TextView) weatherFragment.getView().findViewById(R.id.humidityTextView)).setText(weather.currentCondition.getHumidity() + "%");
            ((TextView) weatherFragment.getView().findViewById(R.id.sunriseTextView)).setText(sunriseValue);
            ((TextView) weatherFragment.getView().findViewById(R.id.sunsetTextView)).setText(sunsetValue);
            ((TextView)weatherFragment.getView().findViewById(R.id.lastupdateView)).setText("Last update: "+ updateValue);

            String city = weather.place.getCity();
            cityList = new ArrayList<>();


            for (int i = 0; i < weatherItemArrayList.size(); i++) {
                WeatherItem currentWeather = weatherItemArrayList.get(i);
                String cc = currentWeather.getCityname();
                cityList.add(cc);
            }

            if (!cityList.contains(city)) {

                weatherItemArrayList.add(new WeatherItem(weather.place.getCity(), tempFormat, latcurrent, loncurrent, weather.currentCondition.getCondition()));

            }






            mapsFragment.setCurrentlat(latitute);
            mapsFragment.setCurrentlon(longitude);

            saveData();


            notificationFragment.setWeatherItemsList(weatherItemArrayList);

        }else{
            Toast.makeText(WeatherActivity.this,"Failed to load data from : "+ sharedPreferences.getString("coords",""), Toast.LENGTH_SHORT).show();
        }


        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.weathermenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city_bycoordinates) {
            setFragment(mapsFragment);
        }

        return true;
    }

    public void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
        builder.setTitle("Add Locationname");

        final EditText cInput = new EditText(WeatherActivity.this);
        cInput.setInputType(InputType.TYPE_CLASS_TEXT);
        //cInput.setHint("cityname, country");
        builder.setView(cInput);
        builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if(i != -1 || !cInput.getText().toString().matches("")){
                    unknownplace = cInput.getText().toString();
                }



            }
        });
        builder.show();

    }

    boolean internet_connection_status(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }

    public AlertDialog.Builder internetConnectionBuilder(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setTitle("No Internet connection. Make sure that Wi-Fi or mobile data is tuned on. Press OK to Exit");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        return builder;
    }





}
