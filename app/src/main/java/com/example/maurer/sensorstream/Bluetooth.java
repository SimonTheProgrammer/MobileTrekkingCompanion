package com.example.maurer.sensorstream;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;

/**
 * Created by Maurer on 28.11.2017.
 */
    /*@author: Sinn der Klasse ist die automatische Aktivierung von Bluetooth,
                da zur Verbindung mit dem Board Bluetooth benutzt wird*/
public class Bluetooth extends AsyncTask<Void,Void,Void>{
    @Override
    protected Void doInBackground(Void... voids) {
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (! mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
