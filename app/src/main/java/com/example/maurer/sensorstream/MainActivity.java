package com.example.maurer.sensorstream;

import android.app.Activity;
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
import android.widget.Toast;

import com.mbientlab.metawear.DeviceInformation;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.GyroBmi160.Range;
import com.mbientlab.metawear.module.GyroBmi160.OutputDataRate;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Temperature;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private BtleService.LocalBinder serviceBinder;
    private MetaWearBoard board;
    private Accelerometer accelerometer;
    private GyroBmi160 gyro;
    private Accelerometer_stream t;
    private Falling_stream t1;
    private Temperature_stream t2;
    final Activity act = this;

    /**
        @author: Simon Maurer
        @problem: Starten mehrerer Sensoren am Board (mit Threads)
                  => müssen parallel laufen -->(quasi-parallel!!)
        @sensors:
            Accelerometer
            Barometer
            Gyroskop
            Temperatur
            Magnetometer
        @link: C:\...\Dropbox\201718_DA_Wanderapp\05_Tätigkeitsberichte\Programm_Simon.docx
        @MainActivity: Wahrscheinlich etwas ausgelastet
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        //Turn on Bluetooth (if disabled)
        new Bluetooth(act).execute();

        // configure start button: (Start der Wanderung + Starten der Sensoren
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","start");
                //Sensoren einstellen:
                /**
                 * Accelerometer (+fallen)
                 * Druck (mit Höhenmeter)
                 * Temperatur
                 * [Rest folgt] */
                //Beschleunigungsmesser
                accelerometer = board.getModule(Accelerometer.class);
                accelerometer.configure()
                        .odr(5) //Sampling frequency
                        .range(4f)
                        .commit();
                //accelerometer.start();
                /*t = new Accelerometer_stream(); //Rohdaten (für DB)
                t.execute(accelerometer);*/

                t1 = new Falling_stream(); //fallen -> ja oder nein (2s)
                t1.execute(accelerometer);

                t2 = new Temperature_stream();
                final Temperature temperature = board.getModule(Temperature.class);
                final Temperature.Sensor tempSensor = temperature.findSensors
                        (Temperature.SensorType.PRESET_THERMISTOR)[0];
                t2.execute(tempSensor);

                //Drucksensor
                /*gyro = board.getModule(GyroBmi160.class);
                gyro.configure()
                        .odr(OutputDataRate.ODR_50_HZ)
                        .range(Range.FSR_2000)
                        .commit();
                //t1 = new Barometer_stream();
                Barometer_stream.execute((Runnable) gyro);*/
            }
        });
        // configure stop button:
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","stop");
                accelerometer.stop();
                t.cancel(true);
                /*accelerometer.stop();
                accelerometer.acceleration().stop();*/
            }
        });
        //configure reset button:
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.tearDown(); //shuts down the board -> saves energy
            }
        });


        //Battery level Listener: (Akkuanzeige)
        findViewById(R.id.battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView v = (TextView) findViewById(R.id.battery);
                new BatteryListener(act).execute(board);
                //Integer.toHexString(new BatteryListener().getBatteryLife());
                Log.i("wtffff",Integer.toHexString(new BatteryListener(act).getBatteryLife()));
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
        Log.wtf("sensorstream", "Service disconnected.");
    }

    //Bei Disconnect vom Service entbinden (Ressourcen freigeben)
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind the service once the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    //Verbindung mit dem MetaBoard herstellen
    public void retrieveBoard(final String macAddr) {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(macAddr);

        // MetaWear board object for the Bluetooth Device
        board = serviceBinder.getMetaWearBoard(remoteDevice);

        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    Lost();
                } else {
                    Log.i("Board", "Connected to " + macAddr);
                    Toast.makeText(MainActivity.this, "Connected to "+macAddr, Toast.LENGTH_LONG).show();
                    playLed(Led.Color.GREEN); //Output for user
                    TextView v = (TextView) findViewById(R.id.Con_status);
                    v.setTextColor(getResources().getColor(R.color.accepted));
                    v.setText("Connection succeded:");
                }
                return null;
            }
        });

                // get Signal strength (RSSI): selbes Problem wie unten beschrieben
                /**board.readRssiAsync().continueWith(new Continuation<Integer, Void>() {
                    @Override
                    public Void then(Task<Integer> task) throws Exception {
                        Log.i("sensorstream", "RSSI: " + task.getResult());
                        return null;
                    }
                });*/
                //Device info: (funktioniert nicht)=> wird zu früh aufgerufen->bevor Board connected ist
                /**board.readDeviceInformationAsync()
                        .continueWith(new Continuation<DeviceInformation, Void>() {
                            @Override
                            public Void then(Task<DeviceInformation> task) throws Exception {
                                Log.i("sensorstream", "Device Information: " + task.getResult());
                                return null;
                            }
                        });*/

                // Verbindungsabbruch:
                board.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
                    @Override
                    public void disconnected(int status) {
                        Log.i("MainActivity", "Unexpectedly lost connection: " + status);
                    }
                });

        //Manueller Verbindungsabbruch:
        /**board.disconnectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("MainActivity", "Disconnected");
                return null;
            }
        });*/
    }

    //Aufruf, wenn Connection verloren geht; Anzeige durch TextView
    private void Lost() {
        //Toast.makeText(MainActivity.this, "Failed to connect (CHECK BLUETOOTH CONNECTION)", Toast.LENGTH_LONG).show();
        Log.i("Board", "Connection failed");
        TextView v = (TextView) findViewById(R.id.Con_status);
        v.setText("Connection failed\n  Check Bluetooth Connection");
        v.setTextColor(getResources().getColor(R.color.error));

    }

    //spielt LED am Board mit bestimmter Farbe
    private void playLed(Led.Color colour) {
        //LED
        Led led;
        if ((led= board.getModule(Led.class)) != null) {
            led.editPattern(colour, Led.PatternPreset.BLINK)
                    .repeatCount((byte) 11) //plays led 10 times
                    .commit();
            led.play();
        }
    }
    /**private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accelerometer.configure()
                        .odr(60f)   // Sampling frequency(50Hz)
                        .commit();
                Log.i("stream","accelerometer");
            }
            else if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
                gyro.configure()
                .odr(GyroBmi160.OutputDataRate.ODR_25_HZ)
                .range(GyroBmi160.Range.FSR_125)
                .commit();
                Log.i("stream","gyroscope");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };*/
}