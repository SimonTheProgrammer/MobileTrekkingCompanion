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
    volatile boolean run = true;

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Log.i("TempSensor","PLZ STAHP");
    }

    @Override
    protected Void doInBackground(Temperature.Sensor... sensors) {
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
                                list.add(data.value((Float.class)));
                            }
                        });
                    }
                }).continueWith(new Continuation<Route, Void>() {
                    @Override
                    public Void then(Task<Route> task) throws Exception {
                        String runner = "";
                        if (run) {
                            tempSensor.read();
                            Log.i("sensorstream: ", "(C): " + list.getLast()+": ");
                        }
                        else {
                            //Thread.sleep(999999999); //999.999s warten = 11,5 Tage
                            return null;
                        }
                        try {
                            Thread.sleep(10000); //alle 10s
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
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