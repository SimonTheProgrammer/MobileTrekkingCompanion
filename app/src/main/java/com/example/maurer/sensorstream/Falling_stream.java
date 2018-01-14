package com.example.maurer.sensorstream;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
 * Created by Maurer on 08.01.2018.
 */
public class Falling_stream extends AsyncTask<Accelerometer,Void,Void> {

    String state = "";
    Activity activity;

    @Override
    protected Void doInBackground(Accelerometer... accelerometers) {
        try {
            while (true){
                Thread.sleep(1000);//STOP wait a minute

                final Accelerometer accelerometer = accelerometers[0];
                accelerometer.acceleration().start();
                accelerometer.start();

                accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        //Filtern (Durchschnitt von 10 Messwerten ==> WE HAVE 2 GO DEEPER!!)
                        // - 1 = fallen
                        //(+)1 = erheben
                        source.map(Function1.RSS).lowpass((byte) 10).filter(ThresholdOutput.BINARY, 0.5f)
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
                            Log.i("Accelerometer", state+" and its not working");
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
        }return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
                /*final TextView v = (TextView) activity.findViewById(R.id.tv_RSSi);
                Log.i("GO FOR IT",state);
                v.setText(state+";");*/
                /**REJECTED*/
    }
}