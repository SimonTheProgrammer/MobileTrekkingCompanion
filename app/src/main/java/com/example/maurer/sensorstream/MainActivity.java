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

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.BarometerBmp280;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Temperature;

import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private BtleService.LocalBinder serviceBinder;
    private MetaWearBoard board;
    private Falling_stream1 t1;
    private GyroBmi160 gyro; //TESTEN
    Activity act = this;
    String address;
    ThreadPool pool = null;

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

                pool = new ThreadPool();
                try {
                    pool.initialize_Sensors(board);
                }catch(Exception e){
                    Log.i("ERROR","No connected Board");
                }
                pool.start_Threads(act);
                /*t1 = new Falling_stream1(accelerometer); //fallen -> ja oder nein (2s)
                t1.start();//*/

                /*final Temperature temperature = board.getModule(Temperature.class);
                tempSensor = temperature.findSensors
                        (Temperature.SensorType.PRESET_THERMISTOR)[0];//*/



                /*gyro = board.getModule(GyroBmi160.class);
                gyro.configure()
                        .odr(OutputDataRate.ODR_25_HZ)
                        .range(Range.FSR_500)
                        .commit();//*/
            }
        });

        // configure stop button:
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sensorstream","stop");
                try {
                    pool.stop_Threads();
                }catch(Exception e){
                    Toast.makeText(act,"(1-7) Thread(s) konnte(n) nicht beendet werden!", Toast.LENGTH_LONG).show();
                }
                /*try {
                    Intent intent = new Intent(act, com.example.maurer.sensorstream.DB.DB_Anzeige.class);
                    startActivity(intent);
                }catch (NullPointerException ex){
                    Toast.makeText(act,"NullPointer Exception", Toast.LENGTH_SHORT).show();
                }//*/

                //DB anzeigen lassen
                /**
                 * neues Intent designen, welches die DB-Einträge anzeigt
                    */
            }
        });

        findViewById(R.id.battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    int counter = 0;
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
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (board.isConnected()) {
                    timer.cancel();
                    Log.i("Board", "Connection successful");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Status_board(1);
                            cancel();
                        }
                    });

                }counter++;
                if (counter>2) {
                    timer.cancel();
                    Log.i("Board", "Connection failed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Status_board(0);
                            cancel();
                        }
                    });
                }
            }
        },0,5000);
    }

    public void Status_board(int i){
        if (i == 0){
            TextView v = (TextView) findViewById(R.id.Con_status);
            v.setText("Connection failed");
            v.setTextColor(getResources().getColor(R.color.error));
        }else{
            TextView v = (TextView) findViewById(R.id.Con_status);
            v.setText("Connected");
            v.setTextColor(getResources().getColor(R.color.accepted));
        }
    }
    private void Succeed(String macAddr) {
        Log.i("Board", "Connected to " + macAddr);
        Toast.makeText(MainActivity.this, "Connected to "+macAddr, Toast.LENGTH_LONG).show();
        playLed(Led.Color.GREEN); //Output for user
    }

    //Aufruf, wenn keine Verbindung möglich; Anzeige durch TextView
    private void Lost() {
        Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
        Log.i("Board", "Connection failed");

    }

    //plays LED in given color(3):
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