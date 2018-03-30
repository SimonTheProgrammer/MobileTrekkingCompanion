package com.example.maurer.sensorstream.web.WeatherFragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.web.WeatherActivity;
import com.example.maurer.sensorstream.web.Weather_Models.WeatherAdapter;
import com.example.maurer.sensorstream.web.Weather_Models.WeatherItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    ArrayList<WeatherItem> weatherItemsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private WeatherAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String coord;


    public ArrayList<WeatherItem> getWeatherItemsList() {
        return weatherItemsList;
    }

    public void setWeatherItemsList(ArrayList<WeatherItem> weatherItemsList) {
        this.weatherItemsList = weatherItemsList;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_notification,container,false);
        //RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.weatherrecylcerview);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.weatherrecylcerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WeatherAdapter(weatherItemsList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new WeatherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {


                coord = "lat=" +weatherItemsList.get(position).getLatitute()+ "&lon="+weatherItemsList.get(position).getLongitude();

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WeatherActivity.SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("coords",coord);
                editor.commit();


                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();

                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);

            }

            @Override
            public void onDeleteViewClicked(int position) {

                weatherItemsList.remove(position);
                setWeatherItemsList(weatherItemsList);
                mAdapter.notifyItemRemoved(position);

            }
        });


        /*
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        WeatherAdapter listAdapter = new WeatherAdapter(weatherItemsList);
        recyclerView.setAdapter(listAdapter);
        */



        return layout;

    }

    private void saveData(){
        SharedPreferences sharedPreferencesSaveArray = getActivity().getSharedPreferences("ArrayDataCurrentWeather", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSaveArray = sharedPreferencesSaveArray.edit();
        Gson gson = new Gson();
        String json = gson.toJson(weatherItemsList);
        editorSaveArray.putString("ArrayDataCurrentWeather",json);
        editorSaveArray.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferencesSaveArray = getActivity().getSharedPreferences("ArrayDataCurrentWeather", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferencesSaveArray.getString("ArrayDataCurrentWeather","");
        Type type = new TypeToken<ArrayList<WeatherItem>>(){}.getType();
        weatherItemsList = gson.fromJson(json,type);


        if(weatherItemsList == null){
            weatherItemsList = new ArrayList<>();
        }
    }



}
