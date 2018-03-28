package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.util.Log;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.Frontend.Notfallkontakthinzufuegen;
import com.example.maurer.sensorstream.Frontend.Temperatur;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.Temperature;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
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
 * Created by Maurer on 08.01.2018.
 */
public class Temperature_stream {
    public LinkedList list = new LinkedList();
    Timer t = null;
    float data;
    //volatile boolean run = true;
    public static List<Float> f = new LinkedList<>();

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

                        f.add(method(temperature));
                    }
                    //in die DB speichern
                    if (list.size() == 12)
                        Fetch(activity, list);
                    //Liste leeren:
                    if (list.size()>=12)
                        list.clear();
                }catch (Exception e){e.printStackTrace();}

                Log.i("TempList(stream)",f.size()+", "+data);
                if (f.size()>3){
                    Temperatur.f = (LinkedList) f;
                    Log.i("tempGraph","startklar");
                }
            }
        },0,5000);
        return data;
    }

    float temper;
    String s;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    float nr;
    int count = 0;

    private float method(final Temperature.Sensor temp) {
        try{
            temp.addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    count = 0;
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {

                            Calendar calendar = Calendar.getInstance();
                            s = format.format(calendar.getTime());
                            temper = data.value(Float.class);
                            //mehr Daten bei Ausgabe!!
                            Log.i("Daten",temper+"");
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
        Log.i("Temperatur(Endwert)",nr+"");
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