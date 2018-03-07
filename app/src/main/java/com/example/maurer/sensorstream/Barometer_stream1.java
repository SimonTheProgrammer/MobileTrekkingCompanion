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
 * Created by Maurer on 23.02.2018.
 */

public class Barometer_stream1{

    Timer t = null;
    LinkedList l_h = new LinkedList();

    public void start(final Activity act, final BarometerBmp280 barometer) {
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    Log.i("Altitude",method(barometer)+" m");
                    l_h.add(method(barometer));
                }catch(Exception e){}
                /*if (l_h.size() < 100)
                    Fetch_Hoehe(act);*/
            }
        },0,5000);
    }

    float altitude;
    private float method(final BarometerBmp280 barometer) {
        try{
            barometer.altitude().start();
            barometer.start();
            //Höhenmeter:
            barometer.altitude().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {
                            try {
                                altitude = data.value(Float.class);
                                while (altitude == 0.0){
                                    Log.i("Data","0.0");
                                    altitude = data.value(Float.class);
                                }
                                //mehr Daten bei Ausgabe!!
                            } catch (Exception e) {
                                //e.printStackTrace();
                                barometer.stop();
                            }
                        }
                    });
                }
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) throws Exception {
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return altitude;
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

    public void stop(){
        t.cancel();
    }
}