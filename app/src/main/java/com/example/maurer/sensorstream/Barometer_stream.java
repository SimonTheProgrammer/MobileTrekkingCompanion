package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBmp280;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.03.2018.
 */

public class Barometer_stream {
    LinkedList l_h;
    LinkedList l_pa;
    LinkedList save_me;
    Timer t = null;

    public void start(final BarometerBmp280 barometer) {
        t = new Timer();
        t.schedule(new TimerTask() {
                       @Override
                       public void run() {
                           Log.i("WORK",method(barometer)+"");
                       }
                   },0,5000);
    }
    float pressure;
    Object altitude;
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
                            pressure = data.value(Float.class);
                            //Log.i("HERE YA GO BUD",pressure+"");
                            //Log.i("Barometer_stream", "Pressure (Pa) = " + data.value(Float.class));
                            save_me.add(data.value(Float.class));
                            l_pa.add(data.value(Float.class));
                            if (save_me.size()==2){
                                pressure = (float) save_me.get(1);
                                Log.i("Ergebnis(0)",pressure+"");
                                save_me.remove(0); save_me.remove(1);
                            }
                            /*if (l_pa.size() > 100)
                                Fetch_Pressure(act);*/
                            //Durchschnitt berechnen (Liste)

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
                //barometer.start();
                return null;
            }
        });
            Log.i("Barometer_stream", "------------(5sek)-------------");
        //Höhenmeter:
       /* barometer.altitude().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        try {
                            Log.i("MainActivity", "Altitude (m) = " + data.value(Float.class));
                            l_h.add(data.value(Float.class));
                            if (l_h.size() > 100)
                                Fetch_Hoehe(act);
                            data.value(Float.class);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) {
                //barometer.altitude().start();
                //barometer.start();
                return null;
            }
        });*/
    } catch (Exception ex) {
        ex.printStackTrace();
    }
        return pressure;
    }

    private void Fetch_Hoehe(Activity act) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<l_h.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l_h.get(i)));
            else //ungerade
                cv.put("value", (float)l_h.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Barometer_Hoehe", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        //Liste leeren:
        for (int i=0; i<l_h.size();i++){
            Log.i("Liste", l_h.get(i) +"");
            l_h.remove(i);
        }
    }

    private void Fetch_Pressure(Activity act) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<l_pa.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l_pa.get(i)));
            else //ungerade
                cv.put("value", (float)l_pa.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Barometer_Druck", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        //Liste leeren:
        for (int i=0; i<l_pa.size();i++){
            Log.i("Liste", l_pa.get(i) +"");
            l_pa.remove(i);
        }
    }

    public void stop(){
        t.cancel();
    }
}
