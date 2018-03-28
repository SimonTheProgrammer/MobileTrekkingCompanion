package com.example.maurer.sensorstream;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.MagneticField;
import com.mbientlab.metawear.module.MagnetometerBmm150;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 14.01.2018.
 */
public class Magnetometer_stream extends AppCompatActivity implements SensorEventListener{
    private ImageView imageView;
    int mAzimuth;
    private TextView txt_azimuth;
    private Sensor mRotate,mAccelerometer,mMagnetometer;
    private SensorManager mSensorManager;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] mAcc = new float[3];
    private float[] mMag = new float[3];
    private boolean Sensor_av, Sensor_av1 = false;
    private boolean bool_Acc = false;
    private boolean bool_Mag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_activity);

        this.setTitle("Kompass: Magnetsensor");

        imageView = (ImageView) findViewById(R.id.compass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        start();
    }
    private void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)==null) {
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null ||
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
                noSensor();
            } else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                Sensor_av = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                Sensor_av1 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotate = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Sensor_av = mSensorManager.registerListener(this,mRotate,SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void noSensor() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Gerät nicht mit App kompatibel")
                .setCancelable(false)
                .setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    public void stop(){
        if (Sensor_av && Sensor_av1){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this,mMagnetometer);
        }
        else{
            if (Sensor_av){
                mSensorManager.unregisterListener(this,mRotate);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Gyro:
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat,sensorEvent.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360;
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(sensorEvent.values,0,mAcc,0,sensorEvent.values.length);
            bool_Acc = true;
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(sensorEvent.values,0,mMag,0,sensorEvent.values.length);
            bool_Mag = true;
        }
        if(bool_Acc && bool_Mag){
            SensorManager.getRotationMatrix(rMat,null,mAcc,mMag);
            SensorManager.getOrientation(rMat,orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360;
        }

        mAzimuth = Math.round(mAzimuth);
        if  (imageView!=null)
            imageView.setRotation(-mAzimuth);

        try {
            txt_azimuth = (TextView) findViewById(R.id.textView);
            txt_azimuth.setText(0+"°");
            txt_azimuth.setText(mAzimuth+"°");

        /*if (mAzimuth >= 350 || mAzimuth <= 10){
            txt_azimuth.setText(mAzimuth+"° N");
        }
        if (mAzimuth < 350 || mAzimuth > 280){
            txt_azimuth.setText(mAzimuth+"° NW");
        }
        if (mAzimuth <= 280 || mAzimuth > 260){
            txt_azimuth.setText(mAzimuth+"° W");
        }
        if (mAzimuth <= 260 || mAzimuth > 190){
            txt_azimuth.setText(mAzimuth+"° SW");
        }
        if (mAzimuth <= 190 || mAzimuth > 170){
            txt_azimuth.setText(mAzimuth+"° S");
        }
        if (mAzimuth <= 170 || mAzimuth > 100){
            txt_azimuth.setText(mAzimuth+"° SO");
        }
        if (mAzimuth <= 100 || mAzimuth > 80){
            txt_azimuth.setText(mAzimuth+"° O");
        }
        if (mAzimuth <= 80 || mAzimuth > 10){
            txt_azimuth.setText(mAzimuth+"° ");
        }*/
        }catch (NullPointerException ex){
            Toast.makeText(this,"Nullpointer: TextView",Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}