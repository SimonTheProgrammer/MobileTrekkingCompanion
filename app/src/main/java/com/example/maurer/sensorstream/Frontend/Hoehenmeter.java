package com.example.maurer.sensorstream.Frontend;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.maurer.sensorstream.R;

import java.util.LinkedList;
import java.util.List;

public class Hoehenmeter extends AppCompatActivity {
    XYPlot plot;
    public static List<Float> f;

    public Hoehenmeter() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigengeklickt);

        plotting((LinkedList) f);
    }

    public void plotting(LinkedList f) {
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Hoehenmeter");
        plot.setDomainLabel("Zeit");
        plot.setRangeLabel("m");
        List series1Numbers = new LinkedList();

        series1Numbers.add(0); //Start bei 0°C (Anfangswert)

        for (int i=0;i<f.size();i++){
            Log.i("sizeHoehe",f.get(i)+"");
            series1Numbers.add(f.get(i));
        }
        XYSeries series1 = new SimpleXYSeries(
                series1Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Hoehenmeter [m]");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.GRAY, null);

        series1Format.getLinePaint().setPathEffect(new DashPathEffect(new float[]{
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(series1, series1Format);
        Log.i("Temperature", "updated graph");
    }
}
