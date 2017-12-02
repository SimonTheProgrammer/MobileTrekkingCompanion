package com.example.maurer.sensorstream;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Maurer on 28.11.2017.
 */
public class BatteryListener extends AsyncTask<MetaWearBoard,Void,Void>{
    private MetaWearBoard board;
    private Activity activity;
    int batter;
    int lev = 0;

    public BatteryListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(MetaWearBoard... metaWearBoards) {
        board = metaWearBoards[0]; //retrieve Board
        board.readBatteryLevelAsync()
                .continueWith(new Continuation<Byte, Integer>() {
                    @Override
                    public Integer then(final Task<Byte> task) throws Exception {
                        Log.i("Battery", "Battery level: "+task.getResult()+"%");
                        batter = ((int)task.getResult()) & 0xFF;
                        if (batter == 0){
                            Log.i("Battery","Checking...");
                            //v.setText("Checking...");
                        } else
                            //v.setText(batter+"%");
                            lev = batter;
                        return batter;
                    }
                });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.findViewById(R.id.battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView v = (TextView) activity.findViewById(R.id.battery);
                Log.i("GO FOR IT",batter+"%");
                v.setText(batter+"%");
            }
        });
    }

    public int getBatteryLife(){
        Log.i("READY",lev+"%");
        return lev & 0xFF;
    }
}