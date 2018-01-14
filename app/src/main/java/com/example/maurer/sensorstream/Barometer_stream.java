package com.example.maurer.sensorstream;

import android.os.AsyncTask;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.GyroBmi160;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.12.2017.
 * @auhor: Barometer-Sensor Klasse. Zurzeit nur probiert mit Rohdaten
 */
public class Barometer_stream extends AsyncTask<BarometerBosch,Void,Void>{
    @Override
    protected Void doInBackground(BarometerBosch... baro) {
        //ArrayIndexOutOfBoundsException
        try{
            final BarometerBosch barometerBosch = baro[0];
            //Pressure Data:
            barometerBosch.pressure().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {
                            Log.i("Barometer_stream", "Pressure (Pa) = " + data.value(Float.class));
                        }
                    });
                }
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) throws Exception {
                    barometerBosch.start();
                    return null;
                }
            });

            //Höhenmeter:
            barometerBosch.altitude().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {
                            Log.i("MainActivity", "Altitude (m) = " + data.value(Float.class));
                        }
                    });
                }
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) throws Exception {
                    barometerBosch.altitude().start();
                    barometerBosch.start();
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}