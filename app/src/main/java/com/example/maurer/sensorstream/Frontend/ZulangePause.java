package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;

public class ZulangePause extends AppCompatActivity {

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
                /* MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(act);
                SQLiteDatabase read = db.getReadableDatabase();

                // Anruf
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + telNr.toString()));
                startActivity(intent);
                if (smsCheck.isChecked()) {
                    //SMS senden
                    SmsManager manager = SmsManager.getDefault();
                    String smsText = "Test Trekking Companion";
                    manager.sendTextMessage(SmsSenden, null, smsText, null, null);
                }
                if (anrufCheck.isChecked()) {
                    //Email senden
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{("helena.bayer98@gmail.com")});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer der Trekking Companion App ist gestürzt!!");

                }
                if (smsCheck.isChecked() && emailCheck.isChecked()) {
                    //SMS senden
                    SmsManager manager = SmsManager.getDefault();
                    String smsText = "Test Trekking Companion";
                    manager.sendTextMessage(SmsSenden, null, smsText, null, null);

                    //Email senden
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{("helena.bayer98@gmail.com")});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Warnung!!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Der Benutzer der Trekking Companion App ist gestürzt!!");
                }*/
            }
        }.start();



    }
}
