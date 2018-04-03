package com.example.maurer.sensorstream.Frontend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WanderungBeenden extends AppCompatActivity {
    public static float avg_temp;
    public static float avg_hoehe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datenanalyse);

        long abgelaufeneZeit=0;
        long zeitbeendet;
        zeitbeendet = System.currentTimeMillis();
        long endZeit = zeitbeendet - abgelaufeneZeit;
        //zeitaufzeichnung.setText((int) endZeit);
        TextView v1 = (TextView) findViewById(R.id.tvdurchschnittlicheTemperaturErgebnis);
        v1.setText(avg_temp+" °C");

        TextView v2 = (TextView) findViewById(R.id.tvZurueckgelegteHoehenmeterErgebnis);
        v2.setText(avg_hoehe+" m");

        Bundle bundle = getIntent().getExtras();
        String start = bundle.getString("Start");
        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm:ss");
        Calendar kalender = Calendar.getInstance();
        String ende = zeitformat.format(kalender.getTimeInMillis());
        Log.i("Zeit",start+";"+ende);

        char[]c_arr1 = start.toCharArray();
        String h_start = c_arr1[0]+""+c_arr1[1];
        String m_start = c_arr1[3]+""+c_arr1[4];
        String s_start = c_arr1[6]+""+c_arr1[7];
        char[]c_arr2 = ende.toCharArray();
        String h_ende = c_arr2[0]+""+c_arr2[1];
        String m_ende = c_arr2[3]+""+c_arr2[4];
        String s_ende = c_arr2[6]+""+c_arr2[7];

        int h = Integer.parseInt(h_ende)-Integer.parseInt(h_start);
        int min = Integer.parseInt(m_ende)-Integer.parseInt(m_start);
        int sec = Integer.parseInt(s_ende)-Integer.parseInt(s_start);
        Log.i("ZwischenErg",h+"-"+min+"-"+sec);
        if (sec<0){
            min = min-1;
            sec=-sec;
        }

        if (min<0){
            h = h-1;
            min=60-min;
        }
        TextView v3 = (TextView) findViewById(R.id.tvBenoetigteZeit);
        Log.i("ZeitErgebnis",h+"-"+min+"-"+sec);
        v3.setText(h+" h : "+min+" min : "+sec+" sek");
    }
}