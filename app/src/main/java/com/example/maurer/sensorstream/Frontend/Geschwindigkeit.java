package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.xy.XYPlot;
import com.example.maurer.sensorstream.R;

public class Geschwindigkeit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigengeklickt);
        XYPlot plot;
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Geschwindigkeit");
        plot.setDomainLabel("Zeit");
        plot.setRangeLabel("km/h");    }
}
