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

public class Temperatur extends AppCompatActivity {
    public static List<Float> f;
    public List series1Numbers = new LinkedList();
    XYPlot plot;
    public float avg;

    public Temperatur() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanzeigengeklickt);


        plotting((LinkedList) f);
    }

    public void plotting(LinkedList f) {
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Temperatur");
        plot.setDomainLabel("Zeit");
        plot.setRangeLabel("°C");

        //Anfangsvariable berechnen: Durchschnittswert von bisherigen Daten
        /*float nr;
        float ges = (float) 0.0;
        for (int i=0;i<f.size();i++)
            ges += (float) f.get(i);
        nr = (ges/f.size());//*/
        series1Numbers.add(0); //Start bei 0°C (Anfangswert)

        int count = 0;
        for (int i=0;i<f.size();i++){
            count++;
            Log.i("size",f.get(i)+"");
            series1Numbers.add(f.get(i));
            this.avg += (float) f.get(i);
            this.avg = avg/ count;
            Log.e("Checkpoint","avg="+avg);
            Datenanalyse.avg_temp = avg;
        }

        XYSeries series1 = new SimpleXYSeries(
                series1Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperatur [°C]");
        //int orange = Color.rgb(255,140,0);
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.LTGRAY, Color.CYAN, Color.parseColor("#ffa500"), null);

        series1Format.getLinePaint().setPathEffect(new DashPathEffect(new float[]{
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(series1, series1Format);
        Log.i("Temperature", "updated graph");

        Log.e("STOP",stop(f)+"");
        //Datenanalyse.avg_temp=stop(f);
        WanderungBeenden.avg_temp=stop(f);
    }

    public float stop(LinkedList<Float> f){
        List list = new LinkedList();
        int count=0;
        float erg=0;

        for (int i=0;i<f.size();i++){
            Log.i("size",f.get(i)+"");
            list.add(f.get(i));
        }
        for (int i=0;i<list.size();i++){
            erg+=(float) list.get(i);
            count++;
        }
        return erg/count;
    }
}