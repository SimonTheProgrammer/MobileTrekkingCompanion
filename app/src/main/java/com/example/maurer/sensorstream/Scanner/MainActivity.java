package com.example.maurer.sensorstream.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.maurer.sensorstream.R;

import java.util.ArrayList;

/**
 * Created by Maurer on 26.01.2018.
 */

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    int REQUEST_ENABLE_BT = 1;
    ArrayList<Device_Information> listItems=new ArrayList<>();
    ArrayAdapter<Device_Information> adapter;
    private ListView mListView;
    Activity a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_logging);

        this.setTitle("Neue Verbindung");
        if (mBluetoothAdapter == null){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Gerät unterstützt kein Bluetooth!");
            dlgAlert.setTitle("Fehler");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            Log.i("Bluetooth","not available");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (mListView == null)
            mListView = (ListView) findViewById(R.id.list);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Device_Information selected = (Device_Information) adapterView.getItemAtPosition(i);
                Log.i("Click",selected.toString());
                //ContextCompat.getColor(context, R.color.right);
                mListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.right));

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(a);
                dlgAlert.setMessage(" Adresse: "+selected.getAddress()+"\n Name: "+selected.getName()+
                        "\n Typ: "+selected.getType()+"\n Klasse: "+selected.getClas());
                dlgAlert.setTitle("Informationen");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Device_selected","start coding here");
                        Intent intent = new Intent(a, com.example.maurer.sensorstream.MainActivity.class);
                        intent.putExtra("Address",selected.getAddress());
                        startActivity(intent);
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });
    }

    public void addItems(Device_Information v) {
        listItems.add(v);
        adapter.notifyDataSetChanged();
    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int type = device.getType();
                BluetoothClass clas = device.getBluetoothClass();

                Device_Information d = new Device_Information(deviceHardwareAddress,clas,deviceName,type);
                Log.i("New Device",deviceHardwareAddress + "; "+deviceName);
                addItems(d);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
