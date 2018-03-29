package com.example.maurer.sensorstream.Frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidplot.xy.CatmullRomInterpolator;
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
import java.util.LinkedList;
import java.util.List;

public class Geschwindigkeit extends AppCompatActivity {
    public static List<String> x = new LinkedList<>();
    public static List<String> y = new LinkedList<>();
    public static List<String> z = new LinkedList<>();
    XYPlot plot;

    public Geschwindigkeit() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigengeklickt);

        plotting(x, y, z);
    }

    private void plotting(List<String> x, List<String> y, List<String> z) {
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Geschwindigkeit");
        plot.setDomainLabel("Zeit");
        plot.setRangeLabel("Kraft [g]");
        List series1Numbers = new LinkedList();
        List series2Numbers = new LinkedList();
        List series3Numbers = new LinkedList();

        series1Numbers.add(0); //Start bei 0
        series2Numbers.add(0); //Start bei 0
        series3Numbers.add(0); //Start bei 0

        for (int i=0;i<x.size();i++){
            series1Numbers.add(Float.valueOf(x.get(i)));
            series2Numbers.add(Float.valueOf(y.get(i)));
            series3Numbers.add(Float.valueOf(z.get(i)));
        }

        XYSeries series1 = new SimpleXYSeries(
                series1Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "X");
        XYSeries series2 = new SimpleXYSeries(
                series2Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Y");
        XYSeries series3 = new SimpleXYSeries(
                series3Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Z");

        LineAndPointFormatter series1Format= new LineAndPointFormatter(Color.GREEN,Color.GREEN,Color.TRANSPARENT,null);
        LineAndPointFormatter series2Format= new LineAndPointFormatter(Color.YELLOW,Color.YELLOW,Color.TRANSPARENT,null);
        LineAndPointFormatter series3Format= new LineAndPointFormatter(Color.WHITE,Color.WHITE,Color.TRANSPARENT,null);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        series3Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);
        plot.addSeries(series3, series3Format);

        Log.i("Accelerometer", "updated graph");
    }
}