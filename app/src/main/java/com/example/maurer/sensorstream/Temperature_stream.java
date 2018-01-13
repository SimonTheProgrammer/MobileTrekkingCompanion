package com.example.maurer.sensorstream;

import android.os.AsyncTask;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.Temperature;

import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 08.01.2018.
 */
public class Temperature_stream extends AsyncTask<Temperature.Sensor,Void,Void> {
    Temperature.Sensor tempSensor;
    LinkedList list = new LinkedList();

    @Override
    protected Void doInBackground(Temperature.Sensor... sensors) {
        try {
            while (true){
                tempSensor = sensors[0];
                tempSensor.addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                list.add(data.value((Float.class)));
                            }
                        });
                    }
                }).continueWith(new Continuation<Route, Void>() {
                    @Override
                    public Void then(Task<Route> task) throws Exception {
                        tempSensor.read();

                        Log.i("sensorstream", "(C): "+list.getLast());
                        try {
                            Thread.sleep(20000); //alle 20s
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
}

/**backback
 * Rucksack*/