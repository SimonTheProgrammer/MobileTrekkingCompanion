package com.example.maurer.sensorstream.web.WeatherFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.web.Weather_Models.Forecast.WeatherForecast;
import com.example.maurer.sensorstream.web.Weather_Models.ForecastAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    ArrayList<WeatherForecast> weatherForecastArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ForecastAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public ArrayList<WeatherForecast> getWeatherForecastArrayList() {
        return weatherForecastArrayList;
    }

    public void setWeatherForecastArrayList(ArrayList<WeatherForecast> weatherForecastArrayList) {
        this.weatherForecastArrayList = weatherForecastArrayList;
    }

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_forecast,container,false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.forecastrecyclervieww);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ForecastAdapter(weatherForecastArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return layout;

    }

}
