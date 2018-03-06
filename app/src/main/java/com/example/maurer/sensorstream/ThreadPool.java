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

    public void initialize_Sensors(MetaWearBoard board){
        baro = board.getModule(BarometerBmp280.class);
        baro.configure()
                .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                .pressureOversampling(BarometerBosch.OversamplingMode.ULTRA_LOW_POWER)
                .standbyTime(4f)
                .commit();
        accelerometer = board.getModule(Accelerometer.class);
        accelerometer.configure()
                .odr(1f) //Sampling frequency
                .range(4f) //Range: +/-4g
                .commit();
        //accelerometer.start();
    }
    public void start_Threads(final Activity act){
        Log.i("ThreadPool", "----------start Threads-------------");
         barometer_stream = new Barometer_stream();
        barometer_stream.start(act, baro);

        barometer_stream1 = new Barometer_stream1();
        barometer_stream1.start(act, baro);

    }

    public void stop_Threads(){
        //accelerometer.stop(); //t
        baro.stop();
        barometer_stream.stop();
        barometer_stream1.stop();
        //accelerometer.stop();
        //t2.cancel(true);
        //t5.cancel(true);
    }
}