package com.example.maurer.sensorstream.web.Weather_Models;

import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;
import com.example.maurer.sensorstream.web.Weather_Models.Forecast.WeatherForecast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kalti on 08.03.2018.
 */

public class ForecastAdapter  extends RecyclerView.Adapter<ForecastAdapter.ListViewHolder> {

    private ArrayList<WeatherForecast> weatherItemArrayList;


    public static class ListViewHolder extends RecyclerView.ViewHolder{
        public TextView dateview;
        public TextView maxtempview;
        public TextView mintempview;
        public TextView tempview;
        public TextView dayview;
        public TextView timeview;
        public TextView conditionview;
        public TextView pressureview;
        public TextView windview;
        public TextView humidityview;


        public ListViewHolder(View itemView) {
            super(itemView);
            dateview = (TextView) itemView.findViewById(R.id.weather_forecast_date);
            maxtempview = (TextView) itemView.findViewById(R.id.weather_forecast_maxtemp);
            mintempview = (TextView) itemView.findViewById(R.id.weather_forecast_mintemp);
            tempview = (TextView) itemView.findViewById(R.id.weather_forecast_temp);
            dayview = (TextView) itemView.findViewById(R.id.weather_forecast_day);
            timeview = (TextView) itemView.findViewById(R.id.weather_forecast_time);
            conditionview =(TextView) itemView.findViewById(R.id.weather_forecast_condition);
            pressureview = (TextView) itemView.findViewById(R.id.weather_forecast_pressure);
            windview = (TextView) itemView.findViewById(R.id.weather_forecast_wind);
            humidityview = (TextView) itemView.findViewById(R.id.weather_forecast_humidity);

        }


    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item,parent,false);
        ForecastAdapter.ListViewHolder lvh = new ForecastAdapter.ListViewHolder(view);

        return lvh;
    }

    public ForecastAdapter(ArrayList<WeatherForecast> weatherItems){

        weatherItemArrayList = weatherItems;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

        WeatherForecast currentItem = weatherItemArrayList.get(position);
        long dvupdatetime = Long.valueOf(currentItem.getForecastPlace().getLastupdate())*1000;
        Date dfupdatetime = new java.util.Date(dvupdatetime);
        String timeValue = new SimpleDateFormat("HH:mm").format(dfupdatetime);

        long dvupdatedate = Long.valueOf(currentItem.getForecastPlace().getLastupdate())*1000;
        Date dfupdatedate = new java.util.Date(dvupdatedate);
        String dateValue = new SimpleDateFormat("dd/MM/yyyy").format(dfupdatedate);

        long dvupdateday = Long.valueOf(currentItem.getForecastPlace().getLastupdate())*1000;
        Date dfupdateday = new java.util.Date(dvupdateday);
        String dayValue = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dfupdateday);



        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String tempFormat = decimalFormat.format(currentItem.getForecastCondition().getTemperature());

        float windspeed = (float) (currentItem.getForecastWind().getSpeed() * 1.61);
        holder.tempview.setText("Temp: "+"\n"+tempFormat + " C°");
        holder.maxtempview.setText(currentItem.getForecastCondition().getMaxTemp()+ " C°" +"(max)");
        holder.mintempview.setText(currentItem.getForecastCondition().getMinTemp()+ " C°"+ "(min)");
        holder.dateview.setText(dateValue);
        holder.dayview.setText(dayValue);
        holder.timeview.setText(timeValue);
        holder.humidityview.setText("Humidity: " +currentItem.getForecastCondition().getHumidity() + "%");
        holder.windview.setText("Wind: " + windspeed + " km/h");
        holder.conditionview.setText("Condition: " + currentItem.getForecastCondition().getCondition());
        holder.pressureview.setText("Pressure:" + currentItem.getForecastCondition().getPressure() + "hPa");

    }

    @Override
    public int getItemCount() {
        return (weatherItemArrayList == null) ? 0 : weatherItemArrayList.size();
    }


}
