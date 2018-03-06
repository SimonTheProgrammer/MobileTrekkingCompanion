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
import java.util.concurrent.ExecutionException;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.03.2018.
 */

public class Barometer_stream {
    LinkedList l_pa = new LinkedList();
    Timer t = null;

    public void start(final Activity act, final BarometerBmp280 barometer) {
        t = new Timer();
        t.schedule(new TimerTask() {
                       @Override
                       public void run() {
                           try {
                               Log.i("Barometer data", method(barometer) + " Pa");
                           }catch(Exception e){}

                           l_pa.add(method(barometer));
                       }
                   },0,5000);

        if (l_pa.size() < 100)
            Fetch_Pressure(act);
    }
    float pressure;

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
                            //mehr Daten bei Ausgabe!!
                            l_pa.add(data.value(Float.class));
                            while (pressure == 0.0){
                                Log.i("Data","0.0");
                                pressure = data.value(Float.class);
                            }
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
    } catch (Exception ex) {
        ex.printStackTrace();
    }
        Log.i("Barometer", "------------(5sek)-------------");
        return pressure;
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