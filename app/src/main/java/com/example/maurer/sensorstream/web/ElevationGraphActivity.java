package com.example.maurer.sensorstream.web;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.web.GoogleMaps_Classes.ElevationData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class ElevationGraphActivity extends AppCompatActivity {


    private LineChart lineChart;


    ArrayList<ElevationData> elevationDataArrayList;


    public ArrayList<ElevationData> getElevationDataArrayList() {
        return elevationDataArrayList;
    }

    public void setElevationDataArrayList(ArrayList<ElevationData> elevationDataArrayList) {
        this.elevationDataArrayList = elevationDataArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elevation_graph);






        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        lineChart = (LineChart)findViewById(R.id.linechart);

        //lineChart.setOnChartGestureListener(ElevationGraphActivity.this);
        //lineChart.setOnChartValueSelectedListener(ElevationGraphActivity.this);

        ArrayList<Entry> yValues = new ArrayList<>();




           Bundle bundle = getIntent().getExtras();


        for(int i = 0; i<bundle.getInt("samplerate") ; i++){
            float currentelevation = (float) bundle.getDouble("data"+i);
            yValues.add(new Entry(i, currentelevation));

        }


            LineDataSet lineDataSet = new LineDataSet(yValues, "height");
            lineDataSet.setFillAlpha(100);

            ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
            iLineDataSets.add(lineDataSet);

            LineData lineData = new LineData(iLineDataSets);
            lineChart.setData(lineData);





    }
}
