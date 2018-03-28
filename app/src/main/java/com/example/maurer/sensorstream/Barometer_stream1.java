package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.Frontend.Datenanzeigen;
import com.example.maurer.sensorstream.Frontend.Hoehenmeter;
import com.example.maurer.sensorstream.Frontend.Sturz;
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

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Barometer_stream1 {
    Timer t = null;
    public LinkedList l_h = new LinkedList();
    public static List<Float> f = new LinkedList<>();
    public static Activity activity;


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
                    //Liste füllen
                    if (data != 0.0) {
                        l_h.add(s);
                        l_h.add(data);
                        Log.i("Altitude", l_h.getLast() + "  ("+l_h.size()+")");

                        f.add(method(barometer));
                    }
                    //in die DB speichern
                    if (l_h.size() == 12)
                        Fetch_Hoehe(act,l_h);

                    //Liste leeren:
                    if (l_h.size()>=12)
                        l_h.clear();
                }catch(Exception e){}

                Log.i("HoeheGraph",f.size()+", "+data);
                if (f.size()>3){
                    Hoehenmeter.f = (LinkedList) f;
                    for (int i=f.size()-1;i<f.size();i++){
                        float fall = f.get(i)-f.get(i-1);
                        Log.e("falling",fall+"m");
                        if (fall<-1.5){
                            Log.e("falling","in freefall!");
                            Fall_detected(activity);
                        }
                    }
                    Log.i("HoeheGraph","startklar");
                }
            }
        },date,1000);
    }

    private void Fall_detected(Activity act){
        Intent intent = new Intent(act,Sturz.class);
        act.startActivity(intent);
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
                    barometer.start();
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return altitude;
    }

    private void Fetch_Hoehe(Activity act, LinkedList l) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<l.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l.get(i)));
            else //ungerade
                cv.put("value", (float)l.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Barometer_Hoehe", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }
        db.close();
    }

    public void stop(){
        t.cancel();
    }
}