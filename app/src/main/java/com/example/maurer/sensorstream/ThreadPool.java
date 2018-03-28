package com.example.maurer.sensorstream;

import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.Temperature;


/**
 * Created by Maurer on 05.03.2018.
 */

public class ThreadPool {//implements Parcelable{
    private BarometerBmp280 baro;
    private Accelerometer accelerometer;
    private GyroBmi160 gyro;
    private Temperature.Sensor tempSensor;
    public Accelerometer_stream1 accelerometer_stream;
    public Barometer_stream barometer_stream;
    public Barometer_stream1 barometer_stream1;
    public Gyroscope_stream gyroscope_stream;
    public Temperature_stream temperature_stream;
    public Timed_BatteryListener battery;
    MetaWearBoard board;

    public void initialize_Sensors(MetaWearBoard meta){
        this.board = meta;
        baro = meta.getModule(BarometerBmp280.class);
        baro.configure()
                .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                .pressureOversampling(BarometerBosch.OversamplingMode.LOW_POWER)
                .standbyTime(4f)
                .commit();
        accelerometer = meta.getModule(Accelerometer.class);
        accelerometer.configure()
                .odr(1f) //Sampling frequency
                .range(4f) //Range: +/-4g
                .commit();
        gyro = board.getModule(GyroBmi160.class);
                gyro.configure()
                        .odr(GyroBmi160.OutputDataRate.ODR_25_HZ)
                        .range(GyroBmi160.Range.FSR_500)
                        .commit();
        final Temperature temperature = board.getModule(Temperature.class);
                tempSensor = temperature.findSensors
                        (Temperature.SensorType.PRESET_THERMISTOR)[0];//*/
    }

    android.os.Handler handler;
    Runnable runnable;

    public void start_Threads(final Activity act){
        Log.i("ThreadPool", "----------start Threads-------------");
        battery = new Timed_BatteryListener();
        battery.startListener(act, board);
        Log.i("Main","ClickListener");

        //Battery: Image
        final ImageView imageView;

        imageView = (ImageView) act.findViewById(R.id.img_bat);


        runnable = new Runnable() {
            @Override
            public void run() {
                int level = battery.getBatteryLevel();
                Log.i("Battery",""+level+"%");
                if (level == 100)
                    level=99;
                TextView tv_battery = (TextView) act.findViewById(R.id.batteryLevel);
                tv_battery.setText(level+"%");

                if (level>75)
                    imageView.setImageResource(R.drawable.battery_full);
                if(level>50 && level<=75)
                    imageView.setImageResource(R.drawable.battery_75);
                if(level>25 && level<=50)
                    imageView.setImageResource(R.drawable.battery_50);
                if (level>5 && level<=25)
                    imageView.setImageResource(R.drawable.battery_25);
                if (level<=5)
                    imageView.setImageResource(R.drawable.battery_5);
                handler.postDelayed(runnable,15000);
            }
        };
        handler = new android.os.Handler();
        handler.postDelayed(runnable,0);


        //Barometer (Pressure)
        barometer_stream = new Barometer_stream();
        barometer_stream.start(act, baro);
        //Barometer (Altitude)
         barometer_stream1 = new Barometer_stream1();
        barometer_stream1.start(act, baro);

        //Accelerometer (?)
        accelerometer.acceleration().start();
        accelerometer.start();
        accelerometer_stream = new Accelerometer_stream1();
        accelerometer_stream.start(act, accelerometer);

        //Gyroscope (°/s=> Room Location)
        gyroscope_stream  = new Gyroscope_stream();
        gyroscope_stream.start(act,gyro);//

        //Temperature (°C)
        temperature_stream = new Temperature_stream();
        temperature_stream.start(act,tempSensor);//*/
    }

    public void stop_Threads(){
        barometer_stream.stop();
        barometer_stream1.stop();
        battery.stop();
        accelerometer_stream.stop();
        gyroscope_stream.stop();
        temperature_stream.stop();
    }
}