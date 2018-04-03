package com.example.maurer.sensorstream.Frontend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.Barometer_stream1;
import com.example.maurer.sensorstream.Falling_stream1;
import com.example.maurer.sensorstream.Magnetometer_stream;
import com.example.maurer.sensorstream.MainActivity;
import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.ThreadPool;
import com.example.maurer.sensorstream.web.mainactivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

public class Datenanzeigen extends AppCompatActivity {
    Activity act = this;
    ThreadPool pool;
    public static MetaWearBoard b;
    private LocationManager locationManager;
    String coord;
    LinkedList liste = new LinkedList();

    public Datenanzeigen() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigen);
        if(b != null) {
            pool = new ThreadPool();
            pool.initialize_Sensors(b);
            pool.start_Threads(act);

            Barometer_stream1.activity = act;
        }

        Calendar kalender = Calendar.getInstance();
        TextView datum = (TextView) findViewById(R.id.tvDatum);
        final TextView zeitaufzeichnungStart = (TextView) findViewById(R.id.tvZeitaufzeichnungSeitBeginn);

        //Zeitmessung seit Wanderungsbeginn
        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm:ss");
        zeitaufzeichnungStart.setText(zeitformat.format(kalender.getTimeInMillis()));
        SimpleDateFormat datumsformat = new SimpleDateFormat("EEEE', 'dd.MM.yyyy");
        datum.setText(datumsformat.format(kalender.getTime()));

        //zu lange Pause
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                if (location!=null && location.getLatitude()!=0.0 && location.getLongitude()!=0.0) {
                    coord = "" + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                    Log.i("coordinates",coord);
                    liste.add(coord);
                }
                else {
                    Log.e("GPS","Ort nicht gefunden!!");
                            Toast.makeText(act, "GPS: Ort nicht gefunden!", Toast.LENGTH_LONG);
                            Button button = (Button) findViewById(R.id.btnGoogleMaps);
                            button.setClickable(false);
                            button.setText("Google Maps(deaktiviert)");
                    Toast.makeText(act, "GPS: Ort nicht gefunden!", Toast.LENGTH_LONG);
                    //button.setEnabled(false);
                }
                if (liste.size()>2) {
                    for (int i = liste.size() - 1; i < liste.size(); i++) {
                        if (liste.get(i - 1).equals(liste.get(i))) {
                            Log.e("coordinates", "waiting for too long! U dead yet?");
                            startActivity(new Intent(act,ZulangePause.class));
                        }
                    }
                }
            }
        },0,600000); //alle 10min

        findViewById(R.id.btnTemperatur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Datenanzeigen.this, Temperatur.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnGeschwindigkeit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Datenanzeigen.this, Geschwindigkeit.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnHoehenmeter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Datenanzeigen.this, Hoehenmeter.class));
            }
        });

        findViewById(R.id.btnLuftdruck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Datenanzeigen.this, Luftdruck.class));
            }
        });

        findViewById(R.id.btnGoogleMaps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, com.example.maurer.sensorstream.web.mainactivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    pool.stop_Threads();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(act,"(1-7) Thread(s) konnte(n) nicht beendet werden!", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(Datenanzeigen.this, WanderungBeenden.class);
                intent.putExtra("Start",zeitaufzeichnungStart.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.compass_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.compass:
                startActivity(new Intent(act, Magnetometer_stream.class));
        }
        return super.onOptionsItemSelected(item);
    }
}