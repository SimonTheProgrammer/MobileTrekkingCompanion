package com.example.maurer.sensorstream;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mbientlab.metawear.*;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.builder.filter.Comparison;
import com.mbientlab.metawear.builder.filter.ThresholdOutput;
import com.mbientlab.metawear.builder.function.Function1;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Simon, 28.11.2017.
 *
 * nach der Koppelung mit dem Board kann man Daten von den Sensoren
 * kriegen.
 * => durch API von Hersteller
 * @link:
 */
class Accelerometer_stream extends AsyncTask<Accelerometer,Void,Void> {
    private String dat;

    @Override
    protected Void doInBackground(Accelerometer... accelerometers) {
        //if (!isCancelled()) {

try {
    while (true){
        final Accelerometer accelerometer = accelerometers[0];
        accelerometer.acceleration().start();
        accelerometer.start();
        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        dat = data.value(Acceleration.class).toString();
                        Log.i("Accelerometer",dat);
                    }                       /**=> FUNKTIONIERENDER TEIL (ROHDATEN)*/
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                if (task.isFaulted()) {
                    Log.i("Accelerometer", "fail");
                } else {
                    Log.i("Accelerometer", "success");
                    accelerometer.acceleration().start();
                    accelerometer.start();
                }
                return null;
            }
        });
    }
    //}

    }catch (Exception e){
        e.printStackTrace();
    }
    return null;

    }
}