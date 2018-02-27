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
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Accelerometer_stream1 extends Thread {

    private String dat;
    LinkedList list = new LinkedList();
    Activity activity;
    Accelerometer accelerometer = null;

    public Accelerometer_stream1(Accelerometer accelerometer, Activity activity) {
        this.accelerometer = accelerometer;
        this.activity = activity;
    }

    @Override
    public void run() {
        super.run();
        final Calendar calendar = Calendar.getInstance();
        try {
            while (Slow_down()){
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
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                            list.add(format.format(calendar.getTime())); //[0]
                            list.add(dat); //[1]
                            Log.i("Accelerometer",format.format(calendar.getTime())+": "+dat);
                            //Datenanzeige(dat);
                            if (list.size()>120){ //jede Minute
                                Fetch(list);
                            }
                            accelerometer.acceleration().start();
                            accelerometer.start();
                        }
                        return null;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Datenanzeige(String last) {
        Object valX = null;
        Object valY = null;
        Object valZ = null;

        Log.i("DATA",last+"");
        char[] c_arr = last.toCharArray();

            if (c_arr[4] == '-') {
                valX = c_arr[4] + c_arr[5] + c_arr[6] + c_arr[7] + c_arr[8] + c_arr[9]; //-valX
                if (c_arr[16] == '-') {
                    valY = c_arr[16] + c_arr[17] + c_arr[18] + c_arr[19] + c_arr[20] + c_arr[21]; //-valY
                    if (c_arr[28] == '-')
                        valZ = c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31] + c_arr[32] + c_arr[33]; //-valZ
                    else
                        valZ = c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31] + c_arr[32]; //valZ
                } else {
                    valY = c_arr[16] + c_arr[17] + c_arr[18] + c_arr[19] + c_arr[20]; //valY
                    if (c_arr[27] == '-')
                        valZ = c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31] + c_arr[32]; //-valZ
                    else
                        valZ = c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31]; //valZ
                }
            }
//                                             < ... >
            else {
                valX = c_arr[4] + c_arr[5] + c_arr[6] + c_arr[7] + c_arr[8]; //valX
                if (c_arr[15] == '-') {
                    valY = c_arr[15] + c_arr[16] + c_arr[17] + c_arr[18] + c_arr[19] + c_arr[20]; //-valY
                    if (c_arr[27] == '-')
                        valZ = c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31] + c_arr[32]; //-valZ
                    else
                        valZ = c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31]; //valZ
                } else {
                    valY = c_arr[15] + c_arr[16] + c_arr[17] + c_arr[18] + c_arr[19]; //valY
                    if (c_arr[26] == '-')
                        valZ = c_arr[26] + c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30] + c_arr[31]; //-valZ

                    else
                        valZ = c_arr[26] + c_arr[27] + c_arr[28] + c_arr[29] + c_arr[30]; //valZ
                }
            }

        Log.i("X-Wert",valX+"");
        Log.i("Y-Wert",valY+"");
        Log.i("Z-Wert",valZ+"");
        }

    private boolean Slow_down() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            accelerometer.acceleration().stop();
            accelerometer.stop();
        }
        return true;
    }

    private void Fetch(LinkedList list) {
        //in DB einschreiben
        Object valX = null;
        Object valY = null;
        Object valZ = null;

        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else{ //ungerade
                String line = (String) list.get(i);
                char[]c_arr = line.toCharArray();

                if (c_arr[4]=='-'){
                    valX = c_arr[4]+c_arr[5]+c_arr[6]+c_arr[7]+c_arr[8]+c_arr[9]; //-valX
                    if (c_arr[16]=='-'){
                        valY = c_arr[16]+c_arr[17]+c_arr[18]+c_arr[19]+c_arr[20]+c_arr[21]; //-valY
                        if (c_arr[28]=='-')
                            valZ = c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]+c_arr[32]+c_arr[33]; //-valZ
                        else
                            valZ = c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]+c_arr[32]; //valZ
                    }
                    else{
                        valY = c_arr[16]+c_arr[17]+c_arr[18]+c_arr[19]+c_arr[20]; //valY
                        if (c_arr[27]=='-')
                            valZ = c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]+c_arr[32]; //-valZ
                        else
                            valZ = c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]; //valZ
                    }
                }
//                                             < ... >
                else{
                    valX = c_arr[4]+c_arr[5]+c_arr[6]+c_arr[7]+c_arr[8]; //valX
                    if (c_arr[15]=='-'){
                        valY = c_arr[15]+c_arr[16]+c_arr[17]+c_arr[18]+c_arr[19]+c_arr[20]; //-valY
                        if (c_arr[27]=='-')
                            valZ = c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]+c_arr[32]; //-valZ
                        else
                            valZ = c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]; //valZ
                    }
                    else{
                        valY = c_arr[15]+c_arr[16]+c_arr[17]+c_arr[18]+c_arr[19]; //valY
                        if (c_arr[26]=='-')
                            valZ = c_arr[26]+c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]+c_arr[31]; //-valZ

                        else
                            valZ = c_arr[26]+c_arr[27]+c_arr[28]+c_arr[29]+c_arr[30]; //valZ
                    }
                }
                Log.i("X Wert: ",""+valX);
                Log.i("Y Wert: ",""+valY);
                Log.i("Z Wert: ",""+valZ);

                cv.put("valueX", (float) valX);
                cv.put("valueY", (float) valY);
                cv.put("valueZ", (float) valZ);
            }
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Accelerometer", "Parameter", cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        //Liste leeren:
        list.clear();
    }
}
