package com.example.maurer.sensorstream;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.builder.filter.Comparison;
import com.mbientlab.metawear.builder.filter.ThresholdOutput;
import com.mbientlab.metawear.builder.function.Function1;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Falling_stream1 extends Thread {
    /**
     * TODO: test it u lil´ bitch
     */

    String state = "";
    Accelerometer accelerometer = null;

    public Falling_stream1(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true){
                Thread.sleep(1000);//STOP wait a minute

                accelerometer.acceleration().start();
                accelerometer.start();

                accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        //Filtern (Durchschnitt von 5 Messwerten)
                        // - 1 = fallen
                        //(+)1 = erheben
                        source.map(Function1.RSS).lowpass((byte) 5).filter(ThresholdOutput.BINARY, 0.5f)
                                .multicast()
                                .to().filter(Comparison.EQ, -1).stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                /**@note: Only happens when IT happens*/
                                Log.i("Accelerometer", "FREEEFAALIIING");
                                state = "Falling";
                            }
                        }).to().filter(Comparison.EQ, 1).stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                /**@note: Checking if dude ain´t dead*/
                                Log.i("Accelerometer", "no freeefaaaalllinng");
                                state = "Not Falling";
                            }
                        }).end();

                    }
                }).continueWith(new Continuation<Route, String>() {
                    @Override
                    public String then(Task<Route> task) throws Exception {
                        if (task.isFaulted()) {
                            Log.i("Accelerometer ", state);
                        } else {
                            Log.i("Accelerometer", state);
                            accelerometer.acceleration().start();
                            accelerometer.start();
                        }
                        return state;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("Status",state);
            accelerometer.acceleration().stop();
            accelerometer.stop();

            accelerometer.acceleration().start();
            accelerometer.start();
        }
    }
}