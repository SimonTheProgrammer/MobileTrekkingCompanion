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
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.GyroBmi160;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 31.01.2018.
 */

public class Gyroscope_stream {
    private String dat;
    public LinkedList list = new LinkedList();
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    String s;
    Timer t;

    public void start(final Activity act, final GyroBmi160 gyro) {
        t = new Timer();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,5);
        Date date = c.getTime();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                String data = method(gyro);
                try {
                    //Liste füllen
                    if (!data.equals(0.0) && !data.equals(null)) {
                        list.add(s);
                        list.add(data);
                        Log.i("Gyroscope", list.getLast() +"  ("+list.size()+")");
                    }

                    //in die DB speichern
                    if (list.size() == 12)
                        Fetch(act, list);

                    //Liste leeren:
                    if (list.size()>=12)
                        list.clear();
                }catch(Exception e){}

            }
        },date,5000);
    }

    private String method(final GyroBmi160 gyroBmi160) {
        try {
            gyroBmi160.angularVelocity().start();
            gyroBmi160.start();
            gyroBmi160.angularVelocity().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {
                            Calendar calendar = Calendar.getInstance();
                            s = format.format(calendar.getTime());

                            try{
                                dat = data.value(AngularVelocity.class)+"";
                            }catch(Exception e){
                                gyroBmi160.stop();
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
        return dat;
    }

    private void Fetch (Activity act, LinkedList l) {
        String valX;
        String valY;
        String valZ;

        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<l.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(l.get(i)));
            else { //ungerade
                String line = (String) l.get(i);
                char[] c_arr = line.toCharArray();

                if (c_arr[4] == '-') {
                    valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+"" + c_arr[9]+""; //-valX
                    if (c_arr[18] == '-') {
                        valY = c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+"" + c_arr[21]+"" + c_arr[22]+"" + c_arr[23]+""; //-valY
                        if (c_arr[30] == '-')
                            valZ = c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+"" + c_arr[34]+"" + c_arr[35]+""; //-valZ
                        else
                            valZ = c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+"" + c_arr[34]+""; //valZ
                    } else {
                        valY = c_arr[18]+"" + c_arr[19] +""+ c_arr[20]+"" + c_arr[21]+"" + c_arr[22]+""; //valY
                        if (c_arr[29] == '-')
                            valZ = c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+"" + c_arr[34]+""; //-valZ
                        else
                            valZ = c_arr[29] +""+ c_arr[30]+"" + c_arr[31] +""+ c_arr[32]+"" + c_arr[33]+""; //valZ
                    }
                }
//                                                       < ... >
                else {
                    valX = c_arr[4]+"" + c_arr[5]+"" + c_arr[6]+"" + c_arr[7]+"" + c_arr[8]+""; //valX
                    if (c_arr[17] == '-') {
                        valY = c_arr[17] +""+ c_arr[18] +""+ c_arr[19]+"" + c_arr[20]+"" + c_arr[21]+"" + c_arr[22]+""; //-valY
                        if (c_arr[29] == '-')
                            valZ = c_arr[29]+"" + c_arr[30]+"" + c_arr[31] +""+ c_arr[32]+"" + c_arr[33]+"" + c_arr[34]+""; //-valZ
                        else
                            valZ = c_arr[29]+"" + c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+""; //valZ
                    } else {
                        valY = c_arr[17]+"" + c_arr[18]+"" + c_arr[19]+"" + c_arr[20]+"" + c_arr[21]+""; //valY
                        if (c_arr[28] == '-')
                            valZ = c_arr[28]+"" + c_arr[29] +""+ c_arr[30]+"" + c_arr[31]+"" + c_arr[32]+"" + c_arr[33]+""; //-valZ
                        else
                            valZ = c_arr[28]+"" + c_arr[29]+"" + c_arr[30] +""+ c_arr[31] +""+ c_arr[32]+""; //valZ
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
            write.insertWithOnConflict("Gyroscope", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }
    }

    public void stop(){
        t.cancel();
    }
    /**/
}