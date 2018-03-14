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

import bolts.Task;

/**
 * Created by Maurer on 23.02.2018.
 */

public class Falling_stream1 {

    public void start(final Accelerometer accelerometer){
        accelerometer.acceleration().start();
        accelerometer.start();
        while(true)
            method(accelerometer);

    }

    private Task<Route> method(final Accelerometer accelerometer) {
            return accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.map(Function1.RSS).lowpass((byte) 5).filter(ThresholdOutput.BINARY,0.5f)
                                    .multicast()
                                    .to().filter(Comparison.EQ,-1).stream(new Subscriber() {
                                @Override
                                public void apply(Data data, Object... env) {
                                    Log.i("falling","in freefall");
                                }
                            }).to().filter(Comparison.EQ,1).stream(new Subscriber() {
                                @Override
                                public void apply(Data data, Object... env) {
                                    Log.i("freefall", "no freefall");
                                }
                            });
                }
            });
        //Log.i("Falling",state);
    }

    public void stop(Accelerometer acc){
        acc.acceleration().stop();
        acc.stop();
    }
}