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
import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 08.01.2018.
 */
public class Temperature_stream extends AsyncTask<Temperature.Sensor,Void,Void> {
    Temperature.Sensor tempSensor;
    LinkedList list = new LinkedList();
    volatile boolean run = true;
    Activity activity;
    int grab = 10000; //Wartezeit bis neue Daten

    public Temperature_stream(Activity act) {
        this.activity = act;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Log.i("TempSensor","PLZ STAHP");
    }

    @Override
    protected Void doInBackground(Temperature.Sensor... sensors) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            tempSensor = sensors[0];
            while (run){
                if (isCancelled()) break;
                tempSensor.addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                list.add(format.format(calendar.getTime())); //[0]
                                list.add(data.value((Float.class))); //[1]
                            }
                        });
                    }
                }).continueWith(new Continuation<Route, Void>() {
                    @Override
                    public Void then(Task<Route> task) throws Exception {
                        if (run) {
                            tempSensor.read();
                            Log.i("sensorstream: ", "(C): " + list.getLast()+": ");
                        }
                        else {
                            //Thread.sleep(999999999); //999.999s warten = 11,5 Tage
                            return null;
                        }
                        try {
                            Thread.sleep(grab); //alle x Sekunden
                            if (list.size()>100)
                                Fetch(list);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }return null;
    }

    private void Fetch(LinkedList list) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
                cv.put("value", (float)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Temperature", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        //Liste leeren:
        for (int i=0; i<list.size();i++){
            Log.i("Liste", list.get(i) +"");
            list.remove(i);
        }
        list = new LinkedList();
    }
}




/**backback
 * Rucksack*/