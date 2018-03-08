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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,5);
        Date date = c.getTime();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                float data = method(barometer);
                try{
                    //Log.i("Altitude ("+s+")",data+" m");
                    if (data != 0.0) {
                        l_h.add(s);
                        l_h.add(data);
                        Log.i("Altitude", l_h.getLast() + "");
                    }
                }catch(Exception e){}
            }
        },date,5000);

        if (l_h.size() < 10)
            Fetch_Hoehe(act);
    }

    float altitude;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    String s;

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
                                Calendar calendar = Calendar.getInstance();
                                s = format.format(calendar.getTime());
                                altitude = data.value(Float.class);
                                //mehr Daten bei Ausgabe!!
                                /*Log.i("Daten",altitude");*/
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