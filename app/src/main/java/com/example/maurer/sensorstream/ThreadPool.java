package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.Temperature;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Maurer on 05.03.2018.
 */

public class ThreadPool {
    private BarometerBmp280 baro;
    private Accelerometer accelerometer;
    private GyroBmi160 gyro;
    private Temperature.Sensor tempSensor;
    Accelerometer_stream1 accelerometer_stream;
    Barometer_stream barometer_stream;
    Barometer_stream1 barometer_stream1;
    Gyroscope_stream gyroscope_stream;
    Timed_BatteryListener battery;
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
        gyro = board.getModule(GyroBmi160.class);
                gyro.configure()
                        .odr(GyroBmi160.OutputDataRate.ODR_25_HZ)
                        .range(GyroBmi160.Range.FSR_500)
                        .commit();
        final Temperature temperature = board.getModule(Temperature.class);
                tempSensor = temperature.findSensors
                        (Temperature.SensorType.PRESET_THERMISTOR)[0];//*/
    }
    public void start_Threads(final Activity act){
        Log.i("ThreadPool", "----------start Threads-------------");
        battery = new Timed_BatteryListener();
        battery.startListener(act, board);
        Log.i("Main","ClickListener");

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
        gyroscope_stream.start(act,gyro);
        //Temperature umbauen

        //Falling_stream extra
    }

    public void stop_Threads(){
        barometer_stream.stop();
        barometer_stream1.stop();
        battery.stop();
        accelerometer_stream.stop();
        gyroscope_stream.stop();
    }

    /*public LinkedList writeDatabase(LinkedList list, Activity act, String tbl_name){
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
                cv.put("value", (float)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict(tbl_name, null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        return new LinkedList();
    }*/
}