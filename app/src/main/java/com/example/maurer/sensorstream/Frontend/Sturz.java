package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;

public class Sturz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sturz);

        final TextView sekAnzahl = (TextView) findViewById(R.id.tVSekundenAnzahl);
        final CountDownTimer start = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                sekAnzahl.setText((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                sekAnzahl.setText("");
            }
        }.start();
    }
}
