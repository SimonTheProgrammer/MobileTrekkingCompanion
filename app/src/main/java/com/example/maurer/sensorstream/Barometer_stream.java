package com.example.maurer.sensorstream;

import android.os.AsyncTask;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.GyroBmi160;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.12.2017.
 * @auhor: Barometer-Sensor Klasse. Zurzeit nur probiert mit Rohdaten
 */
public class Barometer_stream extends AsyncTask<GyroBmi160,Void,Void>{
    @Override
    protected Void doInBackground(GyroBmi160... gyroBmi160s) {
        //ArrayIndexOutOfBoundsException
        try{
            final GyroBmi160 gyro = gyroBmi160s[0];
        gyro.angularVelocity().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object ... env) {
                        Log.i("MainActivity", data.value(AngularVelocity.class).toString());
                    }
                });
            }
        }).continueWith(new Continuation<Route, Object>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                gyro.angularVelocity();
                gyro.start();
                Log.i("Barometer","started");
                if (task.isFaulted()){
                    Log.i("Barometer","HAHAHAHA nope");
                }
                return null;
            }
        });
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}