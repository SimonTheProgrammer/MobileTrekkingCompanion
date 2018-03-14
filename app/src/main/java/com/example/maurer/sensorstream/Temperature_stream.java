package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.Temperature;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 08.01.2018.
 */
public class Temperature_stream {
    LinkedList list = new LinkedList();
    Timer t = null;
    float data;
    //volatile boolean run = true;

    public float start(final Activity activity, final Temperature.Sensor temperature){
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    data = method(temperature);
                    if (data != 0.0){
                        list.add(s);
                        list.add(data);
                        Log.i("Temperature",list.getLast()+" ("+list.size()+")");
                    }

                    //in die DB speichern
                    if (list.size() == 12)
                        Fetch(activity, list);

                    //Liste leeren:
                    if (list.size()>=12)
                        list.clear();
                }catch (Exception e){e.printStackTrace();}
            }
        },0,5000);
        return data;
    }

    float temper;
    String s;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    private float method(final Temperature.Sensor temp) {
        try{
            temp.addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {
                            Calendar calendar = Calendar.getInstance();
                            s = format.format(calendar.getTime());
                            temper = data.value(Float.class);
                            //mehr Daten bei Ausgabe!!
                            /*Log.i("Daten",temper);*/
                        }
                    });
                }
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) {
                    temp.read();
                    return null;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return temper;
    }

    private void Fetch(Activity act, LinkedList list) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
                cv.put("val", (float)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Temperature", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }
    }

    public void stop(){
        t.cancel();
    }
}