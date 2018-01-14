package com.example.maurer.sensorstream;

import android.os.AsyncTask;
import android.util.Log;

import com.mbientlab.metawear.*;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Simon, 28.11.2017.
 *
 * nach der Koppelung mit dem Board kann man Daten von den Sensoren
 * kriegen.
 * => durch API von Hersteller
 * @link: https://mbientlab.com/androiddocs/3/sensors.html
 * @info: Es wäre hier auch noch möglich gewesen, einen Kontstruktor
 *        zu bauen, anstatt den Sensor über ein Array zu erhalten.
 */
class Accelerometer_stream extends AsyncTask<Accelerometer,Void,Void> {
    private String dat;
    LinkedList list = new LinkedList();
    int slow_down;


    @Override
    protected Void doInBackground(Accelerometer... accelerometers) {
        //if (!isCancelled()) {
        try {
            while (Slow_down()){
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
                                list.add(dat);
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
                            if (list.size()>300){
                                Fetch(list);
                            }
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

    private boolean Slow_down() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void Fetch(LinkedList list) {
        //Liste manipulieren
        //in DB einschreiben
        for (int i=0; i<list.size();i++){
            if (i%2!=0){ //jeden 2. Wert rausschmeißen
                list.remove(i);
            }
        }

        for (int i=0; i<list.size();i++){
            Log.i("Liste", list.get(i) +"");
            list.remove(i);
        }
        list = new LinkedList();
    }
}