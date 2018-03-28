package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.Frontend.Luftdruck;
import com.example.maurer.sensorstream.Frontend.Temperatur;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBmp280;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.03.2018.
 */

public class Barometer_stream {
    public LinkedList l_pa = new LinkedList();
    Timer t = null;
    public static List<Float> f = new LinkedList<>();

    public void start(final Activity act, final BarometerBmp280 barometer) {
        t = new Timer();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,3);
        Date date = c.getTime();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                float data = method(barometer);
                try {
                    //Liste füllen
                    if (data != 0.0){
                        l_pa.add(s);
                        l_pa.add(data);
                        Log.i("Pressure",l_pa.getLast()+"  ("+l_pa.size()+")");

                        f.add(method(barometer));
                    }

                    //in die DB speichern
                    if (l_pa.size() == 12)
                        Fetch_Pressure(act, l_pa);

                    //Liste leeren:
                    if (l_pa.size()>=12)
                        l_pa.clear();
                }catch(Exception e){}

                Log.i("Pressure(stream)",f.size()+", "+data);
                if (f.size()>3){ //mind. 3 Werte für Anzeige in Graph
                    Luftdruck.f = (LinkedList) f;
                    Log.i("PressureGraph","startklar");
                    //Analyse
                    for (int i=0; i<f.size();i++){

                    }
                }
            }
        },date,5000);
    }

    float pressure;
    String s;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    private float method(final BarometerBmp280 barometer) {
        try{
            barometer.start();
        barometer.pressure().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            s = format.format(calendar.getTime());
                            pressure = data.value(Float.class);

                            //mehr Daten bei Ausgabe!!
                            /*Log.i("Daten",pressure");*/
                        } catch (Exception e) {
                            //e.printStackTrace();
                            barometer.stop();
                        }
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) {
                barometer.start();
                return null;
            }
        });
    } catch (Exception ex) {
        ex.printStackTrace();
    }
        return pressure;
    }

    private void Fetch_Pressure(Activity act, LinkedList l) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<l.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l.get(i)));
            else //ungerade
                cv.put("value", (float)l.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Barometer_Druck", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }
        db.close();
    }

    public void stop(){
        t.cancel();
    }
}