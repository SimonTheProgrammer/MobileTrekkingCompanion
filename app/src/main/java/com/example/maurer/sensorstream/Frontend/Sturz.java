package com.example.maurer.sensorstream.Frontend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.maurer.sensorstream.DB.MTCDatabaseOpenHelper;
import com.example.maurer.sensorstream.R;

public class Sturz extends AppCompatActivity {
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sturz);

        final TextView sekAnzahl = (TextView) findViewById(R.id.tVSekundenAnzahl);
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                sekAnzahl.setText(""+(int) (millisUntilFinished / 1000));
            }
            public void onFinish() {
                //Notruf absetzen
                sekAnzahl.setText("---Timer abgelaufen---");

                MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(activity);
                SQLiteDatabase read = db.getReadableDatabase();
                Cursor c = read.query("Kontaktdaten",new String[]{"Senden_Mail","Senden_Sms","TelefonNr","EMail"}
                        ,null,null,null,null,null,"5");
                while (c.moveToNext()){
                    Log.i("Senden_mail",""+c.getInt(0));
                    Log.i("Senden_sms",""+c.getInt(1));
                    Log.i("TelefonNr",""+c.getString(2));
                    Log.i("EMail",""+c.getString(3));

                    if (c.getInt(0)==1 && c.getInt(1)==1){
                        //SMS senden
                        SmsManager manager= SmsManager.getDefault();
                        String smsText = "Test Trekking Companion: Nutzer tot";
                        manager.sendTextMessage(c.getString(2), null, smsText, null, null);
////
                        //Email senden
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, c.getString(3));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                        intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer der Trekking Companion App ist gestürzt!!");
                    }else if (c.getInt(0)==1){
                        //Email senden
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, c.getString(3));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                        intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer der Trekking Companion App ist gestürzt!!");
                    }else if (c.getInt(1)==1){
                        //SMS senden
                        SmsManager manager= SmsManager.getDefault();
                        String smsText = "Test Trekking Companion: Nutzer tot";
                        manager.sendTextMessage(c.getString(2), null, smsText, null, null);
                    }
                }

            }
        }.start();//*/

        findViewById(R.id.push_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity,Datenanzeigen.class));
            }
        });
    }
}
