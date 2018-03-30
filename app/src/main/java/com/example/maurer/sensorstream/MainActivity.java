package com.example.maurer.sensorstream;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.*;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maurer.sensorstream.Frontend.Datenanzeigen;
import com.example.maurer.sensorstream.Frontend.Notfallkontakthinzufuegen;
import com.example.maurer.sensorstream.Frontend.Sturz;
import com.example.maurer.sensorstream.Frontend.ZulangePause;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Led;

import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private BtleService.LocalBinder serviceBinder;
    private MetaWearBoard board;
    Activity act = this;
    String address;
    ThreadPool pool = null;
    Accelerometer accelerometer;
    Boolean pressedOnce = false;

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
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.startseite);

        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);
        start();

        // configure start button: (Start der Wanderung + Starten der Sensoren
        findViewById(R.id.Wnd_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*logging.start(false);
                accelerometer.acceleration().start();
                accelerometer.start();*/
                Log.i("sensorstream","start");
                pool = new ThreadPool();
                try {
                    pool.initialize_Sensors(board);
                }catch(Exception e){
                    Log.e("ERROR","No connected Board");
                }
                if (board!=null){
                    Intent intent = new Intent(act, com.example.maurer.sensorstream.Frontend.Datenanzeigen.class);
                    Datenanzeigen.b = board;
                    startActivity(intent);
                }
            }
        });

    //configure reset button:
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //board.tearDown(); //removes routes-> resources!
                Intent intent1 = new Intent(act,com.example.maurer.sensorstream.DB.DB_Anzeige.class);
                startActivity(intent1);
            }
        });

        findViewById(R.id.magnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(act,com.example.maurer.sensorstream.Magnetometer_stream.class);
                startActivity(intent1);
            }
        });

        findViewById(R.id.btnNotfallkontakt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.maurer.sensorstream.MainActivity.this, Notfallkontakthinzufuegen.class);
                Notfallkontakthinzufuegen.act = act;
                startActivity(intent);
            }
        });
    }

    public void start(){
        Intent intent = getIntent();
        address = intent.getStringExtra("Address"); //MAC ADDRESS
        this.setTitle("MobileTrekkingCompanion");
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;
        Log.i("sensorstream","Service Connected");
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
        Log.wtf("MAC",macAddr);
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        try {
            if (btManager == null) {
                if (macAddr != null && macAddr != "")
                    retrieveBoard(macAddr);
            }
            final BluetoothDevice remoteDevice =
                    btManager.getAdapter().getRemoteDevice(macAddr);

        // MetaWear board object for the Bluetooth Device
            board = serviceBinder.getMetaWearBoard(remoteDevice);

            board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
                @Override
                public Task<Route> then(Task<Void> task) throws Exception {
                    accelerometer  = board.getModule(Accelerometer.class);
                    accelerometer.configure()
                            .odr(50f)
                            .commit();

                    /*return accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                        @Override
                        public void configure(RouteComponent source) {
                            source.map(Function1.RSS).lowpass((byte) 4).filter(ThresholdOutput.BINARY, 0.3f)
                                    .multicast()
                                    .to().filter(Comparison.EQ, -1).stream(new Subscriber() {
                                @Override
                                public void apply(Data data, Object... env) {
                                    Log.i(LOG_TAG, data.formattedTimestamp() + ": Entered Free Fall");
                                }
                            }).to().filter(Comparison.EQ, 1).stream(new Subscriber() {
                                @Override
                                public void apply(Data data, Object... env) {
                                    Log.i(LOG_TAG, data.formattedTimestamp() + ": Left Free Fall");
                                }
                            }).end();
                        }
                    });*/
                return null;}
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) throws Exception {
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
        }catch (Exception e){
            Log.i("Board","null");
        }
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
                if (board != null) {
                    if (board.isConnected()) {
                        timer.cancel();
                        Log.i("Board", "Connection successful");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Status_board(1);
                                Toast.makeText(MainActivity.this, "Verbunden", Toast.LENGTH_LONG).show();
                                Succeed(macAddr);
                                cancel();
                            }
                        });
                    }
                    counter++;
                    if (counter > 2) {
                        timer.cancel();
                        Log.i("Board", "Connection failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Status_board(0);
                                final AlertDialog.Builder alert = new AlertDialog.Builder(act);
                                alert.setMessage("Keine Verbindung zum Board!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alert.show();
                                Toast.makeText(MainActivity.this, "Keine Verbindung zum Board!", Toast.LENGTH_LONG).show();
                                cancel();
                            }
                        });
                    }
                }
            }
        },0,6000);
    }

    public void Status_board(int i){
        if (i == 0){
            TextView v = (TextView) findViewById(R.id.tvVerbingungYesNo);
            v.setText("Connection failed");
            v.setTextColor(getResources().getColor(R.color.error));
        }else{
            TextView v = (TextView) findViewById(R.id.tvVerbingungYesNo);
            v.setText("Connected");
            Succeed(address);
            v.setTextColor(getResources().getColor(R.color.accepted));
        }
    }
    private void Succeed(String macAddr) {
        Log.i("Board", "Connected to " + macAddr);
        Toast.makeText(MainActivity.this, "Connected to "+macAddr, Toast.LENGTH_LONG).show();
        playLed(Led.Color.GREEN); //Output for user
    }
    //Aufruf, wenn keine Verbindung möglich
    private void Lost() {
        Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
        Log.i("Board", "Connection failed");
    }
    //plays LED in given color(RGB):
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

    //--------------------FRONTEND---------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {
            if (!pressedOnce) {
                pressedOnce = true;
                Toast.makeText(getApplicationContext(), "Zum beenden erneut drücken!!", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pressedOnce = false;
                    }
                }, 4000);
            } else if (pressedOnce) {
                pressedOnce = false;
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //int i = getFragmentManager().getBackStackEntryCount();
        //if (i > 0) {
        //   getFragmentManager().popBackStack();
        // if (i == 1) {
        //  ImageView ivStart = (ImageView) findViewById(R.id.ivStart);
        // ivStart.setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
        //   }
        // } //else
        //super.onBackPressed();

    }

    public void zuLangePause(final View sources) {

        startActivity(new Intent(com.example.maurer.sensorstream.MainActivity.this, ZulangePause.class));

    }

    public void sturzWahrgenommen(final View sources) {

        startActivity(new Intent(com.example.maurer.sensorstream.MainActivity.this, Sturz.class));


    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }
}