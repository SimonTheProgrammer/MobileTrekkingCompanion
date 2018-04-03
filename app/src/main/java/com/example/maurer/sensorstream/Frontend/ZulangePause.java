package com.example.maurer.sensorstream.Frontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.R;

public class ZulangePause extends AppCompatActivity implements LocationListener {
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zulangeruhepause);

        final TextView sekAnzahl = (TextView) findViewById(R.id.tVSekundenAnzahl);
        final CountDownTimer start = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                sekAnzahl.setText((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                sekAnzahl.setText("----Timer abgelaufen----");
                MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
                SQLiteDatabase read = db.getReadableDatabase();

                Cursor c = read.query("Kontaktdaten",new String[]{"Senden_Mail","Senden_Sms","TelefonNr","EMail"}
                                                                        ,null,null,null,null,null,"5");
                while (c.moveToNext()){
                    Log.i("Senden_mail",""+c.getInt(0));
                    Log.i("Senden_sms",""+c.getInt(1));
                    Log.i("TelefonNr",""+c.getString(2));
                    Log.i("EMail",""+c.getString(3));
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitude = loc.getLongitude();
                    double latitude = loc.getLatitude();

                    Intent intent = new Intent();

                    if (c.getInt(0)==1 && c.getInt(1)==1){
                        //SMS senden
                        SmsManager manager= SmsManager.getDefault();
                        String smsText = "Der Benutzer hat sich seit längerem nicht mehr bewegt " + " Längengrad= "
                                + longitude + " Breitengrad= "+ latitude;
                        manager.sendTextMessage(c.getString(2), null, smsText, null, null);

                        //Anruf
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+c.getString(2)));

                        //Email senden
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, c.getString(3));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                        intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer hat sich seit längerem nicht mehr bewegt " + " Längengrad= "
                                + longitude + " Breitengrad= "+ latitude);

                    }else if (c.getInt(0)==1){
                        //Anruf
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+c.getString(2)));

                        //Email senden

                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, c.getString(3));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                        intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer hat sich seit längerem nicht mehr bewegt " + " Längengrad= "
                                + longitude + " Breitengrad= "+ latitude);
                    }else if (c.getInt(1)==1){
                        //SMS senden
                        SmsManager manager= SmsManager.getDefault();
                        String smsText = "Der Benutzer hat sich seit längerem nicht mehr bewegt " + " Längengrad= "+ longitude
                                + " Breitengrad= "+ latitude;
                        manager.sendTextMessage(c.getString(2), null, smsText, null, null);
                    }
                }


                /* Anruf
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + telNr.toString()));
                startActivity(intent);*/
            }
        }.start();

        findViewById(R.id.push_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act,Datenanzeigen.class));
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
