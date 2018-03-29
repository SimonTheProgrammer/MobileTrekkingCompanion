package com.example.maurer.sensorstream.Frontend;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.Barometer_stream1;
import com.example.maurer.sensorstream.Falling_stream1;
import com.example.maurer.sensorstream.MainActivity;
import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.ThreadPool;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import bolts.Continuation;
import bolts.Task;

public class Datenanzeigen extends AppCompatActivity {
    long abgelaufeneZeit;
    Activity act = this;
    ThreadPool pool;
    public static MetaWearBoard b;

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
        /*MediaPlayer m1 = MediaPlayer.create(this.getBaseContext(), 2130968576);
        MediaPlayer m2 = MediaPlayer.create(this.getBaseContext(), 2130968579);
        MediaPlayer m3 = MediaPlayer.create(this.getBaseContext(), 2130968580);
        MediaPlayer m4 = MediaPlayer.create(this.getBaseContext(), 2130968581);
        SensorManager manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Falling_stream1 falling_stream1 = new Falling_stream1(m1,m2,m3,m4,manager);
        falling_stream1.start();*/

        Calendar kalender = Calendar.getInstance();
        //TextView uhrzeit = (TextView) findViewById(R.id.tvUhrzeit);
        TextView datum = (TextView) findViewById(R.id.tvDatum);
        TextView zeitaufzeichnungStart = (TextView) findViewById(R.id.tvZeitaufzeichnungSeitBeginn);
//        Button btnStart;
//        Button btnStopp;

        //Zeitmessung seit Wanderungsbeginn
        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm:ss");
        zeitaufzeichnungStart.setText(zeitformat.format(kalender.getTimeInMillis()));
        SimpleDateFormat zeitaufzeichnungAktuell = new SimpleDateFormat("HH:mm:ss");
        // SimpleDateFormat vergangeneZeit = zeitaufzeichnungStart-zeitaufzeichnungAktuell;
        //uhrzeit
        //SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm:ss");
        //uhrzeit.setText(zeitformat.format(kalender.getTime()));
        //datum
        SimpleDateFormat datumsformat = new SimpleDateFormat("EEEE', 'dd.MM.yyyy");
        datum.setText(datumsformat.format(kalender.getTime()));
        //StartStoppUhr
        //btnStart = (Button) findViewById(R.id.btnWanderungStarten);
        //btnStopp = (Button) findViewById(R.id.btnWanderungBeenden);
        //abgelaufeneZeit = 0;
        //btnStart.setOnClickListener(this);
        //btnStopp.setOnClickListener(this);

        abgelaufeneZeit = 0;

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

        findViewById(R.id.btnPollenflugdaten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Datenanzeigen.this, Pollenflugdaten.class));
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
                startActivity(new Intent(Datenanzeigen.this, GoogleMaps.class));
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
                startActivity(new Intent(Datenanzeigen.this, WanderungBeenden.class));
            }
        });
    }
}