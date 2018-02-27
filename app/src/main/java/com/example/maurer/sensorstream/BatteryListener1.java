package com.example.maurer.sensorstream;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 25.02.2018.
 */

public class BatteryListener1 extends Thread{
    Activity act = null;
    MetaWearBoard board = null;
    final Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public BatteryListener1(Activity act, MetaWearBoard board) {
        this.act = act;
        this.board = board;
    }

    @Override
    public void run() {
        super.run();
        board.readBatteryLevelAsync().continueWith(new Continuation<Byte, Object>() {
            @Override
            public Object then(Task<Byte> task) throws Exception {
                TextView v = (TextView)act.findViewById(R.id.battery);
                v.setText((int) task.getResult());
                Log.i("Battery1","GOT DIS: "+(int) task.getResult());
                return task.getResult();
            }
        });
    }
}