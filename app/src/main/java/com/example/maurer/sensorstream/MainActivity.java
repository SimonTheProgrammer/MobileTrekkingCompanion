package com.example.maurer.sensorstream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.*;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DeviceInformation;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.builder.filter.Comparison;
import com.mbientlab.metawear.builder.filter.ThresholdOutput;
import com.mbientlab.metawear.builder.function.Function1;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Led;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private BtleService.LocalBinder serviceBinder;
    private Accelerometer accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        // configure start button:
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","start");
                accelerometer.acceleration().start();
                accelerometer.start();
            }
        });
        // configure stop button:
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","stop");
                accelerometer.stop();
                accelerometer.acceleration().stop();
            }
        });
        //configure reset button:
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.tearDown(); //shuts down the board -> saves energy
            }
        });
        //Yooo

        //Battery level Listener:
        findViewById(R.id.battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView v = (TextView) findViewById(R.id.battery);
                board.readBatteryLevelAsync()
                        .continueWith(new Continuation<Byte, Void>() {
                            @Override
                            public Void then(final Task<Byte> task) throws Exception {
                                Log.i("sensorstream", "Battery level: "+task.getResult()+"%");
                                v.setText("Battery level: "+task.getResult()+"%");
                                return null;
                            }
                        });
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;

        Log.wtf("sensorstream","Service Connected");

        retrieveBoard("C6:EE:AA:23:E4:4F"); //Board Simon (+Drucksensor)
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.wtf("sensorstream", "wtf");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind the service once the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    private MetaWearBoard board;

    public void retrieveBoard(final String macAddr) {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(macAddr);

        // MetaWear board object for the Bluetooth Device
        board = serviceBinder.getMetaWearBoard(remoteDevice);

        board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
            @Override
            public Task<Route> then(Task<Void> task) throws Exception {
                Log.wtf("sensorstream", "Connected to " + macAddr);

                // Accelerometer data:
                accelerometer = board.getModule(Accelerometer.class);
                accelerometer.configure()
                        .odr(60f)   // Sampling frequency(50Hz)
                        .commit();
                return accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        // get the average output from 10 samples
                        source.map(Function1.RSS).average((byte) 10).filter(ThresholdOutput.BINARY, 0.5f)
                                .multicast()
                                //Minuszahlen:
                                .to().filter(Comparison.EQ, -1).stream(new Subscriber() {
                                    @Override
                                    public void apply(Data data, Object... env) {
                                        Log.i("sensorstream", "in freefall");
                                    }
                                })
                                //Pluszahlen:
                                .to().filter(Comparison.EQ, 1).stream(new Subscriber() {
                                    @Override
                                    public void apply(Data data, Object... env) {
                                        Log.i("sensorstream", "NOT in freefall; good to go");
                                    }
                                })
                                .end();

                        //Raw data:
                        /*source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                Log.i("sensorstream", data.value(Acceleration.class).toString());
                            }
                        });*/
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                if (task.isFaulted()){
                    Log.i("sensorstream","Failed to configure app", task.getError());
                } else{
                    Log.i("sensorstream", "app configured");
                }

                //Board information (3):
                // get Signal strength (RSSI):
                board.readRssiAsync().continueWith(new Continuation<Integer, Void>() {
                    @Override
                    public Void then(Task<Integer> task) throws Exception {
                        Log.i("sensorstream", "RSSI: " + task.getResult());
                        return null;
                    }
                });
                //Device info:
                board.readDeviceInformationAsync()
                        .continueWith(new Continuation<DeviceInformation, Void>() {
                            @Override
                            public Void then(Task<DeviceInformation> task) throws Exception {
                                Log.i("sensorstream", "Device Information: " + task.getResult());
                                return null;
                            }
                        });
                return null;
            }

        });
        // Verbindungsabbruch:
        board.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
            @Override
            public void disconnected(int status) {
                Log.i("MainActivity", "Unexpectedly lost connection: " + status);
            }
        });

        //Manueller Verbindungsabbruch: (nötig?)
        /*board.disconnectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("MainActivity", "Disconnected");
                return null;
            }
        });*/

        Led led;
        if ((led= board.getModule(Led.class)) != null) {
            led.editPattern(Led.Color.RED, Led.PatternPreset.BLINK)
                    .repeatCount((byte) 20)
                    .commit();
            led.play();
        }
    }
}