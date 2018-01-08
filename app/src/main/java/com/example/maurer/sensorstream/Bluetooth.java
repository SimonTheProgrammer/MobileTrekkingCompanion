package com.example.maurer.sensorstream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Maurer on 28.11.2017.
 */
    /*@author: Sinn der Klasse ist die automatische Aktivierung von Bluetooth,
                da zur Verbindung mit dem Board Bluetooth benutzt wird*/
public class Bluetooth extends AsyncTask<Void,Boolean,Void>{
    Activity activity;

    public Bluetooth(Activity act) {
        activity = act;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if  (BluetoothAdapter.ACTION_REQUEST_ENABLE.equals(Activity.RESULT_OK) ){
            Log.i("good job","yay, you deserve a sip or two");
        }
        if (! mBtAdapter.isEnabled()) {
            Toast.makeText(activity,"Keine Bluetooth Connection",Toast.LENGTH_LONG);
            //mBtAdapter.enable();
            /**well, it SHOULD work.
            @note: it actually does, but it makes the whole program turn to nothing*/
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}