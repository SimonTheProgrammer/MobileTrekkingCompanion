package com.example.maurer.sensorstream;

import android.app.Activity;
import android.util.Log;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.BarometerBosch;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Maurer on 05.03.2018.
 */

public class ThreadPool {
    private BarometerBmp280 baro;
    private Accelerometer accelerometer;
    Barometer_stream barometer_stream;
    Barometer_stream1 barometer_stream1;
    Timed_BatteryListener timer;
    MetaWearBoard board;

    public void initialize_Sensors(MetaWearBoard meta){
        this.board = meta;
        baro = meta.getModule(BarometerBmp280.class);
        baro.configure()
                .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                .pressureOversampling(BarometerBosch.OversamplingMode.ULTRA_LOW_POWER)
                .standbyTime(4f)
                .commit();
        accelerometer = meta.getModule(Accelerometer.class);
        accelerometer.configure()
                .odr(1f) //Sampling frequency
                .range(4f) //Range: +/-4g
                .commit();
        //accelerometer.start();
    }
    public void start_Threads(final Activity act){
        Log.i("ThreadPool", "----------start Threads-------------");
        timer = new Timed_BatteryListener();
        timer.startListener(act, board);
        Log.i("Main","ClickListener");

         barometer_stream = new Barometer_stream();
        barometer_stream.start(act, baro);

        barometer_stream1 = new Barometer_stream1();
        barometer_stream1.start(act, baro);

    }

    public void stop_Threads(){
        //accelerometer.stop(); //t
        barometer_stream.stop();
        barometer_stream1.stop();
        timer.stop();
        //accelerometer.stop();
        //t2.cancel(true);
        //t5.cancel(true);
    }
}