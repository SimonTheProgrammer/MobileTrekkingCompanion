package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.Frontend.Geschwindigkeit;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

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

public class Accelerometer_stream1{

    public LinkedList list = new LinkedList();
    Timer t;
    public static List<String> x = new LinkedList<>();
    public static List<String> y = new LinkedList<>();
    public static List<String> z = new LinkedList<>();
    long period = 1500;

    public void start(final Activity act,final Accelerometer accelerometer){
        t = new Timer();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,5);
        Date date = c.getTime();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                String data = method(accelerometer);
                try{
                    //Liste füllen
                    if (!data.equals(0.0) && !data.equals(null)){
                        list.add(s);
                        list.add(data);
                        Log.i("Accelerometer",list.getLast()+"  ("+list.size()+")");

                        getCoord(list.getLast()+"");
                    }


                    //in die DB speichern
                    if (list.size() == 12)
                        Fetch(list, act);

                    //Liste leeren:
                    if (list.size()>=12)
                        list.clear();
                }catch(Exception e){}

                Log.i("AccList(ret)",x.size()+", "+data);

                if (x.size()>3) { //mind. 3 Werte für Anzeige in Graph
                    Geschwindigkeit.x = (LinkedList) x;
                    Geschwindigkeit.y = (LinkedList) y;
                    Geschwindigkeit.z = (LinkedList) z;
                    Log.i("AccGraph", "startklar");
                    period = 5000;
                }
            }
        },date,period);
    }

    private void getCoord(String line) {
        String valX;
        String valY;
        String valZ;

        char[]c_arr = line.toCharArray();

        if (c_arr[4] == '-') {
            valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+"" + c_arr[9]+""; //-valX
            if (c_arr[16] == '-') {
                valY = c_arr[16]+"" + c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+"" + c_arr[21]+""; //-valY
                if (c_arr[28] == '-')
                    valZ = c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+""; //-valZ
                else
                    valZ = c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //valZ
            } else {
                valY = c_arr[16]+"" + c_arr[17] +""+ c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+""; //valY
                if (c_arr[27] == '-')
                    valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //-valZ
                else
                    valZ = c_arr[27] +""+ c_arr[28]+"" + c_arr[29] +""+ c_arr[30]+"" + c_arr[31]+""; //valZ
            }
        }
//                                                       < ... >
        else {
            valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+""; //valX
            if (c_arr[15] == '-') {
                valY = c_arr[15] +""+ c_arr[16] +""+ c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+""; //-valY
                if (c_arr[27] == '-')
                    valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29] +""+ c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //-valZ
                else
                    valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+""; //valZ
            } else {
                valY = c_arr[15]+"" + c_arr[16]+"" + c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+""; //valY
                if (c_arr[26] == '-')
                    valZ = c_arr[26]+"" + c_arr[27] +""+ c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+""; //-valZ
                else
                    valZ = c_arr[26]+"" + c_arr[27]+"" + c_arr[28] +""+ c_arr[29] +""+ c_arr[30]+""; //valZ
            }
        }
        Log.i("X Wert: ",""+valX);
        Log.i("Y Wert: ",""+valY);
        Log.i("Z Wert: ",""+valZ);

        x.add(valX);
        y.add(valY);
        z.add(valZ);
    }

    String dat;
    String s;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    private String method(final Accelerometer accelerometer) {
        try{
        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            s = format.format(calendar.getTime());
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
                accelerometer.acceleration().start();
                accelerometer.start();
                return null;
            }
        });
    } catch (Exception ex) {
        ex.printStackTrace();
    }
        return dat;
    }

    private void Fetch(LinkedList l, Activity activity) {
        //in DB einschreiben
        String valX;
        String valY;
        String valZ;

        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<l.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l.get(i)));
            else{ //ungerade
                String line = (String) l.get(i);
                char[]c_arr = line.toCharArray();

                if (c_arr[4] == '-') {
                    valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+"" + c_arr[9]+""; //-valX
                    if (c_arr[16] == '-') {
                        valY = c_arr[16]+"" + c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+"" + c_arr[21]+""; //-valY
                        if (c_arr[28] == '-')
                            valZ = c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+""; //-valZ
                        else
                            valZ = c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //valZ
                    } else {
                        valY = c_arr[16]+"" + c_arr[17] +""+ c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+""; //valY
                        if (c_arr[27] == '-')
                            valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //-valZ
                        else
                            valZ = c_arr[27] +""+ c_arr[28]+"" + c_arr[29] +""+ c_arr[30]+"" + c_arr[31]+""; //valZ
                    }
                }
//                                             < ... >
                else {
                    valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+""; //valX
                    if (c_arr[15] == '-') {
                        valY = c_arr[15] +""+ c_arr[16] +""+ c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+""; //-valY
                        if (c_arr[27] == '-')
                            valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29] +""+ c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+""; //-valZ
                        else
                            valZ = c_arr[27]+"" + c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+""; //valZ
                    } else {
                        valY = c_arr[15]+"" + c_arr[16]+"" + c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+""; //valY
                        if (c_arr[26] == '-')
                            valZ = c_arr[26]+"" + c_arr[27] +""+ c_arr[28]+"" + c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+""; //-valZ
                        else
                            valZ = c_arr[26]+"" + c_arr[27]+"" + c_arr[28] +""+ c_arr[29] +""+ c_arr[30]+""; //valZ
                    }
                }
                Log.i("X Wert: ",""+valX);
                Log.i("Y Wert: ",""+valY);
                Log.i("Z Wert: ",""+valZ);

                cv.put("valueX", valX);
                cv.put("valueY", valY);
                cv.put("valueZ", valZ);
            }
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Accelerometer", "Parameter", cv, SQLiteDatabase.CONFLICT_FAIL);
        }
        db.close();
    }

    public void stop(){
        t.cancel();
    }
}