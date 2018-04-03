package com.example.maurer.sensorstream.Scanner;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.maurer.sensorstream.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Maurer on 26.01.2018.
 */

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int PERMISSION_LOCATION = 9001;
    int REQUEST_ENABLE_BT = 1;
    ArrayList<Device_Information> listItems=new ArrayList<>();
    ArrayAdapter<Device_Information> adapter;
    private ListView mListView;
    Activity a = this;
    private LocationManager locationManager;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_logging);

        if (ContextCompat.checkSelfPermission(a,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            Log.e("SMS","Permission denied");
            requestSmsPermission();
        }
        if (ContextCompat.checkSelfPermission(a,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.e("AccessFINE","Permission denied");
            requestLocationPermission();
        }
        if (ContextCompat.checkSelfPermission(a,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.e("AccessCOARSE","Permission denied");
            requestLocationCoarsePermission();
        }

        BluetoothAdapter mBluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();
        this.setTitle("Gerät verbinden");
        if (mBluetoothAdapter == null){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Gerät unterstützt kein Bluetooth!");
            dlgAlert.setTitle("Fehler");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            Log.i("Bluetooth","not available");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } //Anzeige für Benutzer
        if (mListView == null)
            mListView = (ListView) findViewById(R.id.list);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location != null) {
            if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0) {
                //Enable GPS:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(a);
                alertDialog.setTitle("GPS deaktiviert");
                alertDialog
                        .setMessage("GPS ist deaktiviert. Wollen Sie ihre Einstellungen ändern?");

                // On pressing Settings button
                alertDialog.setPositiveButton("Aktivieren",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                a.startActivity(intent);
                            }
                        });
                alertDialog.setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        }

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                counter++;
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
                        Intent intent = new Intent(a, com.example.maurer.sensorstream.MainActivity.class);
                        intent.putExtra("Address",selected.getAddress());
                        unregisterReceiver(mReceiver);
                        startActivity(intent);
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });

        Button b = (Button) findViewById(R.id.btn_offline);
        b.setText("Abbrechen");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(a);
                    alert.setTitle("Keine Geräte gefunden!");
                    alert.setMessage("Wollen sie ohne Gerätekopplung fortfahren?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(a, com.example.maurer.sensorstream.MainActivity.class);
                                    intent.putExtra("Address","");
                                    unregisterReceiver(mReceiver);
                                    startActivity(intent);
                                }
                            });
                    alert.show();
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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int type = device.getType();
                BluetoothClass bclass = device.getBluetoothClass();

                Device_Information d = new Device_Information(deviceHardwareAddress,bclass,deviceName,type);
                Log.i("New Device",deviceHardwareAddress + ";"+deviceName);
                if (deviceName!=null && deviceName.equals("MetaWear")) //nur MetaWear-Geräte anzeigen lassen
                    addItems(d);
            }
        }
    };

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(a, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(a, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }

    private void requestLocationCoarsePermission() {
        if (ContextCompat.checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}