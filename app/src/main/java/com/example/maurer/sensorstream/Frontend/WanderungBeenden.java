package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.maurer.sensorstream.R;

public class WanderungBeenden extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanalyse);
        long abgelaufeneZeit=0;
        long zeitbeendet;
        zeitbeendet = System.currentTimeMillis();
        long endZeit = zeitbeendet - abgelaufeneZeit;
        //zeitaufzeichnung.setText((int) endZeit);
    }
}