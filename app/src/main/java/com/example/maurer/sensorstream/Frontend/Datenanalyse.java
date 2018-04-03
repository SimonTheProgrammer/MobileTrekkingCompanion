package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;

public class Datenanalyse extends AppCompatActivity {
    public static float avg_temp;
    public static float avg_hoehe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanalyse);
    }
}