package com.example.maurer.sensorstream;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.03.2018.
 */

public class Timed_BatteryListener {

    public void startListener(final Activity act, final MetaWearBoard board){
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                board.readBatteryLevelAsync().continueWith(new Continuation<Byte, Object>() {
                    @Override
                    public Object then(Task<Byte> task) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Log.i("Battery (" + format.format(calendar.getTime())+")", task.getResult().toString() + "%");
                        TextView v = (TextView) act.findViewById(R.id.battery);
                        v.setText(task.getResult().toString() + "%");
                        return task.getResult();
                    }
                });
            }
        },0,10000);//ale 10 sek
    }
}