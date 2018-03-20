package com.example.maurer.sensorstream.Frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.maurer.sensorstream.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

public class Luftdruck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigengeklickt);
        XYPlot plot;
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Luftdruck");
        plot.setDomainLabel("Zeit");
        plot.setRangeLabel("bar");

        Intent intent= getIntent();
        float[] temp=intent.getFloatArrayExtra("baro");
        List<Float> f=new ArrayList<>();
        for(int i=0;i<temp.length;i++){
            f.add(new Float(temp[i]));
        }
        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};


        XYSeries series=new SimpleXYSeries(f, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"TEsts1 Perm" );
        LineAndPointFormatter seriesFormat= new LineAndPointFormatter();
        plot.addSeries(series, seriesFormat);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }
}
