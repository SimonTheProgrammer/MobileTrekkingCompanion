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

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Barometer_stream1 extends Thread {
/**
 * TODO: Zeitpunkt richtig timen (überschlägt sich selbst)
 */
    Activity act;
    LinkedList l_h;
    LinkedList l_pa;
    BarometerBmp280 barometer = null;

    public Barometer_stream1(Activity act, BarometerBmp280 barometer) {
        this.act = act;
        this.barometer = barometer;
    }

    @Override
    public void run() {
        super.run();
        try{
                //Pressure Data:
                barometer.start();
                barometer.pressure().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                try {
                                    Log.i("Barometer_stream", "Pressure (Pa) = " + data.value(Float.class));
                                    //l_pa.add(data.value(Float.class));
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                //Höhenmeter:
                barometer.altitude().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                    Log.i("MainActivity", "Altitude (m) = " + data.value(Float.class));
                                    //l_h.add(data.value(Float.class));
                                    /*if (l_h.size() > 100) {
                                        Fetch_Hoehe();
                                    }*/
                            }
                        });
                    }
                });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Fetch_Hoehe() {
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

    private void Fetch_Pressure() {
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
}