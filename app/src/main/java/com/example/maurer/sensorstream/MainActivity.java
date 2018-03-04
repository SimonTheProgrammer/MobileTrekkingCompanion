package com.example.maurer.sensorstream;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.*;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mbientlab.metawear.AsyncDataProducer;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.DeviceInformation;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.MagneticField;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.GyroBmi160.Range;
import com.mbientlab.metawear.module.GyroBmi160.OutputDataRate;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.MagnetometerBmm150;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.module.Temperature;

import java.io.IOException;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private BtleService.LocalBinder serviceBinder;
    private MetaWearBoard board;
    private Accelerometer accelerometer;
    private Temperature.Sensor tempSensor;
    private Falling_stream1 t1;
    private Temperature_stream t2;
    private Gyroscope_stream t5;
    private GyroBmi160 gyro; //TESTEN
    private BarometerBmp280 baro;
    Activity act = this;
    String address;

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

        Intent intent = getIntent();
        address = intent.getStringExtra("Address"); //MAC ADDRESS
        this.setTitle("MobileTrekkingCompanion");

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
                 *Gyrosensor
                 * Magnetometer*/

                /*accelerometer = board.getModule(Accelerometer.class);
                accelerometer.configure()
                        .odr(1f) //Sampling frequency
                        .range(4f) //Range: +/-4g
                        .commit();
                accelerometer.start();
                t1 = new Falling_stream1(accelerometer); //fallen -> ja oder nein (2s)
                t1.start();//*/

                /*t2 = new Temperature_stream(act);
                final Temperature temperature = board.getModule(Temperature.class);
                tempSensor = temperature.findSensors
                        (Temperature.SensorType.PRESET_THERMISTOR)[0];
                Log.i("MainActivity","start Temp");
                t2.execute(tempSensor);//*/

                //Drucksensor
                /*baro = board.getModule(BarometerBmp280.class);
                baro.configure()
                        .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                        .pressureOversampling(BarometerBosch.OversamplingMode.ULTRA_LOW_POWER)
                        .standbyTime(4f)
                        .commit();
                baro.start();
                Log.i("MainActivity","start Baro");
                Barometer_stream1 bar = new Barometer_stream1(act,baro);
                bar.start();

                /*gyro = board.getModule(GyroBmi160.class);
                gyro.configure()
                        .odr(OutputDataRate.ODR_25_HZ)
                        .range(Range.FSR_500)
                        .commit();
                t5 = new Gyroscope_stream();
                t5.execute(gyro);//*/

                /*Accelerometer_stream1 acc = new Accelerometer_stream1(accelerometer, act);
                acc.start();//*/
            }
        });

        // configure stop button:
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","stop");
                try {
                    accelerometer.stop(); //t
                    baro.stop();
                    t2.cancel(true);
                    t5.cancel(true);
                }catch(Exception e){
                    Toast.makeText(act,"(1-7) Thread(s) konnte(n) nicht beendet werden!", Toast.LENGTH_LONG).show();
                }
                try {
                    Intent intent = new Intent(act, com.example.maurer.sensorstream.DB.DB_Anzeige.class);
                    startActivity(intent);
                }catch (NullPointerException ex){
                    Toast.makeText(act,"NullPointer Exception", Toast.LENGTH_SHORT).show();
                }

                //DB anzeigen lassen
                /**
                 * neues Intent designen, welches die DB-Einträge anzeigt
                    */
            }
        });

        findViewById(R.id.battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timed_BatteryListener timer = new Timed_BatteryListener();
                timer.startListener(act, board);
                Log.i("Main","ClickListener");
                //Log.i("Battery",Integer.toHexString(new BatteryListener(act).getBatteryLife()));
            }
        });
        //configure reset button:
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.tearDown(); //removes routes-> resources!
            }
        });

        findViewById(R.id.btn_magnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(act,com.example.maurer.sensorstream.Magnetometer_stream.class);
                startActivity(intent1);
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;
        Log.wtf("sensorstream","Service Connected");

        retrieveBoard(address); //Board mit MAC-Adresse ansprechen
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
        if  (btManager==null){
            retrieveBoard(macAddr);
        }
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
                    Succeed(macAddr);
                    Timed_BatteryListener timer = new Timed_BatteryListener();
                    timer.startListener(act, board);
                }
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

        //Manueller Verbindungsabbruch:
        /**board.disconnectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("MainActivity", "Disconnected");
                return null;
            }
        });*/
    }

    private void Succeed(String macAddr) {
        Log.i("Board", "Connected to " + macAddr);
        Toast.makeText(MainActivity.this, "Connected to "+macAddr, Toast.LENGTH_LONG).show();
        playLed(Led.Color.GREEN); //Output for user
        TextView v = (TextView) findViewById(R.id.Con_status);
        v.setTextColor(getResources().getColor(R.color.accepted));
        String display = "Connection succeed";
        v.setText(display);
    }

    //Aufruf, wenn Connection verloren geht; Anzeige durch TextView
    private void Lost() {
        Toast.makeText(MainActivity.this, "Failed to connect (CHECK BLUETOOTH CONNECTION)", Toast.LENGTH_LONG).show();
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
}