package com.example.maurer.sensorstream;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.mbientlab.metawear.MetaWearBoard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 28.11.2017.
 * @author: Akkuanzeige für Board (funktioniert nicht beim 1. Klicken)
 */
public class BatteryListener extends AsyncTask<MetaWearBoard,Void,Void>{
    private MetaWearBoard board;
    LinkedList list = new LinkedList();
    private Activity activity;
    int batter;
    int lev = 0;
    final Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");


    public BatteryListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(MetaWearBoard... metaWearBoards) {
        board = metaWearBoards[0]; //retrieve Board
        batter = getBatteryLife();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (list.size()>100)
            Fetch(list);
        return null;
    }

    private void Fetch(LinkedList list) {
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
        for (int i=0;i<list.size();i++) {
            ContentValues cv = new ContentValues();
            if (i%2==0) //gerade
                cv.put("Time", String.valueOf(list.get(i)));
            else //ungerade
                cv.put("value", (int)list.get(i));
            SQLiteDatabase write = db.getWritableDatabase();
            write.insertWithOnConflict("Battery", null, cv, SQLiteDatabase.CONFLICT_FAIL);
        }

        //Liste leeren:
        for (int i=0; i<list.size();i++){
            Log.i("Liste", list.get(i) +"");
            list.remove(i);
        }
        list = new LinkedList();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        final TextView v = (TextView) activity.findViewById(R.id.battery);
        Log.i("GO FOR IT",batter+"%");
        v.setText(batter+"%");
    }

    public int getBatteryLife(){
        board.readBatteryLevelAsync()
                .continueWith(new Continuation<Byte, Object>() {
                    @Override
                    public Object then(Task<Byte> task) throws Exception {
                        Log.i("Battery", "Battery level: "+task.getResult()+"%");
                        batter = ((int)task.getResult()) & 0xFF;
                        if (batter < 100){
                            list.add(format.format(calendar.getTime())); //[0]
                            list.add(batter); //[1]
                            Log.i("Battery","Checking...");
                            //v.setText("Checking...");
                        } else
                            //v.setText(batter+"%");
                            lev = batter;
                        return batter;
                    }
                });
        return lev & 0xFF;
    }
}