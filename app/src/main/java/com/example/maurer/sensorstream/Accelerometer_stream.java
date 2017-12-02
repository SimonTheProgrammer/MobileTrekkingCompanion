package com.example.maurer.sensorstream;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Maurer on 28.11.2017.
 */
public class Accelerometer_stream extends AsyncTask<Accelerometer,Void,Void> {

    @Override
    protected Void doInBackground(Accelerometer... accelerometers) {
        //if (!isCancelled()) {
try {
    final Accelerometer accelerometer = accelerometers[0];
    accelerometer.acceleration().start();
    accelerometer.start();

    accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
        @Override
        public void configure(RouteComponent source) {
            source.stream(new Subscriber() {
                @Override
                public void apply(Data data, Object... env) {
                    Log.i("Accelerometer",data.value(Acceleration.class).toString());
                }
            });
            source.map(Function1.RSS).lowpass((byte) 1).filter(ThresholdOutput.BINARY, 0.5f)
                    .multicast()
                    .to().filter(Comparison.EQ, -1).stream(new Subscriber() {
                @Override
                public void apply(Data data, Object... env) {
                    Log.i("Accelerometer", "FREEEFAALIIING");
                }
            }).to().filter(Comparison.EQ, 1).stream(new Subscriber() {
                @Override
                public void apply(Data data, Object... env) {
                    Log.i("Accelerometer", "no freeefaaaalllinng");
                }
            }).end();
        }
    }).continueWith(new Continuation<Route, Void>() {
        @Override
        public Void then(Task<Route> task) throws Exception {
            if (task.isFaulted()) {
                Log.i("Accelerometer", "fail");
            } else {
                Log.i("Accelerometer", "success");
            }
            return null;
        }
    });
    //}
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
        }
}