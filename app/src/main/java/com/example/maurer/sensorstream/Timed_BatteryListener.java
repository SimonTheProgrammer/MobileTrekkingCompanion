package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.MetaWearBoard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 04.03.2018.
 */

public class Timed_BatteryListener {
    Timer t;
    public LinkedList list = new LinkedList();
    int lvl;

    public void startListener(final Activity act, final MetaWearBoard board){
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                board.readBatteryLevelAsync().continueWith(new Continuation<Byte, Void>() {
                    @Override
                    public Void then(Task<Byte> task) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        lvl = task.getResult().intValue();

                        TextView v = (TextView) act.findViewById(R.id.batteryLevel);
                        v.setText(task.getResult().toString() + "%");
                        list.add(task.getResult().toString());
                        Log.i("Battery (" + format.format(calendar.getTime())+")", task.getResult().toString() + "%  "+"  ("+list.size()+")");
                        if (list.size()>6) {
                            Fetch(act);
                            list.clear();
                        }
                        return null;
                    }
                });
            }
        },0,12000);//all 12 sec
    }

    private void Fetch(Activity activity) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
                cv.put("value", (int)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Battery", null, cv, SQLiteDatabase.CONFLICT_IGNORE); //doesn´t write null in DB
        }
        Log.i("Battery (List)",list.getLast()+"");

        //Liste leeren:
        for (int i=0; i<list.size();i++){
            Log.i("Liste", list.get(i) +"");
            list.remove(i);
        }
    }

    public  void stop(){
        t.cancel();
    }

    public int getBatteryLevel() {
        return lvl;
    }
}