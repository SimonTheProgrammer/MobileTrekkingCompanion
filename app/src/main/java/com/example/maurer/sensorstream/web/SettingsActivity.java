package com.example.maurer.sensorstream.web;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.maurer.sensorstream.R;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerLayoutSettings;
    private String[] spinnerItems = {"Night","Retro","Default"};
    public String layoutStyle = "Default";
    private Button saveSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initButton();
        spinnerLayoutSettings = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> layoutspinnerAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerLayoutSettings.setAdapter(layoutspinnerAdapter);
        spinnerLayoutSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:{

                        layoutStyle = "Night";
                        break;
                    }
                    case 1:{

                        layoutStyle = "Retro";
                        break;
                    }
                    case 2:{

                        layoutStyle = "Default";
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }
    public void initButton(){
        saveSettings = (Button) findViewById(R.id.btnSave);
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityToGoogleMaps = new Intent(SettingsActivity.this, GoogleMapsActivity.class);
                switchActivityToGoogleMaps.putExtra("STYLEVALUE",layoutStyle);
                startActivity(switchActivityToGoogleMaps);
            }
        });

    }

    }





