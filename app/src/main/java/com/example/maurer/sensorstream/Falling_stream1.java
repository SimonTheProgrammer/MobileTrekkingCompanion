package com.example.maurer.sensorstream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
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

public class Falling_stream1 implements SensorEventListener{
    public static double[] window;
    static int BUFF_SIZE = 50;
    public MediaPlayer m1_fall;
    public MediaPlayer m2_sit;
    public MediaPlayer m3_stand;
    public MediaPlayer m4_walk;
    private SensorManager sensorManager;
    public static String curr_state;
    public static String prev_state;
    double sigma = 0.5D;
    double th = 10.0D;
    double th1 = 5.0D;
    double th2 = 2.0D;
    public double ax;
    public double ay;
    public double az;
    public double a_norm;
    public int i = 0;

    static {
        window = new double[BUFF_SIZE];
    }


    public Falling_stream1(MediaPlayer m1_fall, MediaPlayer m2_sit, MediaPlayer m3_stand, MediaPlayer m4_walk, SensorManager sensorManager) {
        this.m1_fall = m1_fall;
        this.m2_sit = m2_sit;
        this.m3_stand = m3_stand;
        this.m4_walk = m4_walk;
        this.sensorManager = sensorManager;
    }

    public void start(){
        this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(1), 2);
        this.initialize();
    }

    private void initialize() {
        for(this.i = 0; this.i < BUFF_SIZE; ++this.i) {
            window[this.i] = 0.0D;
        }

        prev_state = "none";
        curr_state = "none";
    }

    @SuppressLint({"ParserError"})
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == 1) {
            this.ax = (double)event.values[0];
            this.ay = (double)event.values[1];
            this.az = (double)event.values[2];
            this.AddData(this.ax, this.ay, this.az);
            this.posture_recognition(window, this.ay);
            this.SystemState(curr_state, prev_state);
            if(!prev_state.equalsIgnoreCase(curr_state)) {
                prev_state = curr_state;
            }
        }
    }

    private void posture_recognition(double[] window2, double ay2) {
        int zrc = this.compute_zrc(window2);
        if(zrc == 0) {
            if(Math.abs(ay2) < this.th1) {
                curr_state = "sitting";
                Log.i("State","sitting");
            } else {
                curr_state = "standing";
                Log.i("State","standing");
            }
        } else if((double)zrc > this.th2) {
            curr_state = "walking";
            Log.i("State","walking");
        } else {
            curr_state = "none";
        }
    }
    private int compute_zrc(double[] window2) {
        int count = 0;

        for(this.i = 1; this.i <= BUFF_SIZE - 1; ++this.i) {
            if(window2[this.i] - this.th < this.sigma && window2[this.i - 1] - this.th > this.sigma) {
                ++count;
            }
        }

        return count;
    }

    private void SystemState(String curr_state1, String prev_state1) {
        if(!prev_state1.equalsIgnoreCase(curr_state1)) {
            if(curr_state1.equalsIgnoreCase("fall")) {
                this.m1_fall.start();
                Log.i("State","falling");
            }

            if(curr_state1.equalsIgnoreCase("sitting")) {
                this.m2_sit.start();
            }

            if(curr_state1.equalsIgnoreCase("standing")) {
                this.m3_stand.start();
            }

            if(curr_state1.equalsIgnoreCase("walking")) {
                this.m4_walk.start();
            }
        }

    }

    private void AddData(double ax2, double ay2, double az2) {
        this.a_norm = Math.sqrt(this.ax * this.ax + this.ay * this.ay + this.az * this.az);

        for(this.i = 0; this.i <= BUFF_SIZE - 2; ++this.i) {
            window[this.i] = window[this.i + 1];
        }

        window[BUFF_SIZE - 1] = this.a_norm;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}