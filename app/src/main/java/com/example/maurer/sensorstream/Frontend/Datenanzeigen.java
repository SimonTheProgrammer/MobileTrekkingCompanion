package com.example.maurer.sensorstream.Frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.ThreadPool;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Datenanzeigen extends AppCompatActivity {
    long abgelaufeneZeit;
    Activity act = this;
    ThreadPool pool;
    public static MetaWearBoard b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigen);
        if(b != null) {
            pool = new ThreadPool();
            pool.initialize_Sensors(b);
            pool.start_Threads(act);
        }

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
    }


    public void btnTemperaturGedrueckt(final View sources) {
        Intent intent=new Intent(Datenanzeigen.this, Temperatur.class);
        intent.putExtra("temp",pool.temperature_stream.list.toArray());
        startActivity(intent);
    }

    public void btnGeschwindigkeitGedrueckt(final View sources) {
        Intent intent = new Intent(Datenanzeigen.this, Geschwindigkeit.class);
        intent.putExtra("accelero",pool.accelerometer_stream.list.toArray());
        startActivity(intent);
    }

    public void btnHoehenmeterGedrueckt(final View sources) {
        startActivity(new Intent(Datenanzeigen.this, Hoehenmeter.class));

    }

    public void btnPolltenflugdatenGedrueckt(final View sources) {
        startActivity(new Intent(Datenanzeigen.this, Pollenflugdaten.class));

    }

    public void btnLuftdrukGedrueckt(final View sources) {
        Intent intent=new Intent(Datenanzeigen.this, Geschwindigkeit.class);

        intent.putExtra("baro",pool.barometer_stream.l_pa.toArray()
        );
        startActivity(intent);
    }

    public void btnGoogleMapsGedrueckt(final View sources) {
        startActivity(new Intent(Datenanzeigen.this, GoogleMaps.class));

    }

    public void btnWanderungBeendenGedrueck(final View sources) {
        Log.i("sensorstream","stop");
        try {
            pool.stop_Threads();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(act,"(1-7) Thread(s) konnte(n) nicht beendet werden!", Toast.LENGTH_LONG).show();
        }
        startActivity(new Intent(Datenanzeigen.this, WanderungBeenden.class));

    }

}

