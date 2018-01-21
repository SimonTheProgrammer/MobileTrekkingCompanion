package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.*;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Simon, 28.11.2017.
 *
 * nach der Koppelung mit dem Board kann man Daten von den Sensoren
 * kriegen.
 * => durch API von Hersteller
 * @link: https://mbientlab.com/androiddocs/3/sensors.html
 * @info: Es wäre hier auch noch möglich gewesen, einen Konstruktor
 *        zu bauen, anstatt den Sensor über ein Array zu erhalten.
 */
class Accelerometer_stream extends AsyncTask<Accelerometer,Void,Void> {
    private String dat;
    LinkedList list = new LinkedList();
    Activity activity;

    public Accelerometer_stream(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Accelerometer... accelerometers) {
        //if (!isCancelled()) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            while (Slow_down()){
                final Accelerometer accelerometer = accelerometers[0];
                accelerometer.acceleration().start();
                accelerometer.start();
                accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                dat = data.value(Acceleration.class).toString();
                            }
                        });
                    }
                }).continueWith(new Continuation<Route, Void>() {
                    @Override
                    public Void then(Task<Route> task) throws Exception {
                        if (task.isFaulted()) {
                            Log.i("Accelerometer", "fail");
                        } else {
                            Log.i("Accelerometer", "success");
                            list.add(format.format(calendar.getTime())); //[0]
                            list.add(dat); //[1]
                            Log.i("Accelerometer",format.format(calendar.getTime())+": "+dat);
                            if (list.size()>20){
                                Fetch(list);
                            }
                            accelerometer.acceleration().start();
                            accelerometer.start();
                        }
                        return null;
                    }
                });
            }
            //}

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
    }

    private boolean Slow_down() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void Fetch(LinkedList list) {
        //in DB einschreiben
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
            /**DATEN MÜSSEN NOCH RICHTIG EINGESCHRIEBEN WERDEN*/
                cv.put("value", (float)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Accelerometer", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        /*//löschen (Tbl-Einträge)
        SQLiteDatabase write = db.getWritableDatabase();
        write.delete("Accelerometer",null,null);//*/

        //Liste leeren:
        list.clear();
    }
}