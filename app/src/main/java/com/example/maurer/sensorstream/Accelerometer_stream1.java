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
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Accelerometer_stream1{

    LinkedList list = new LinkedList();
    Timer t;

    public void start(final Activity act,final Accelerometer accelerometer){
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    Log.i(method(accelerometer)+"","gangarang");
                    list.add(method(accelerometer));
                    Datenanzeige(method(accelerometer));
                }catch(Exception e){}
            }
        },0,5000);
        if (list.size() < 100)
            Fetch(list, act);
    }

    String dat;
    private String method(final Accelerometer accelerometer) {
        try{
        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        try {
                            dat = data.value(Acceleration.class)+"";
                        }catch(Exception e){
                            accelerometer.stop();
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
    } catch (Exception ex) {
        ex.printStackTrace();
    }
        return dat;
    }

    private void Datenanzeige(String last) {
        float valX;
        float valY;
        float valZ;
        Log.i("DATA",last.toString().replace('{','('));
        char[] c_arr = last.toCharArray();

            if (c_arr[5] == '-') { //5
                valX = c_arr[5] + c_arr[6] + c_arr[7] + c_arr[8] + c_arr[9] + c_arr[10]; //-valX
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
                valX = c_arr[5] + c_arr[6] + c_arr[7] + c_arr[8] + c_arr[9]; //valX
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

    private void Fetch(LinkedList list, Activity activity) {
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

    public void stop(){
        t.cancel();
    }
}
