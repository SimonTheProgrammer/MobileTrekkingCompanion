package com.example.maurer.sensorstream.web.Weather_Models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maurer.sensorstream.R;

import java.util.ArrayList;

/**
 * Created by Kalti on 02.03.2018.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ListViewHolder> {

    private ArrayList<WeatherItem> weatherItemArrayList;
    private OnItemClickListener mListener;

   public interface OnItemClickListener{
       void onItemClick(int position);
       void onDeleteViewClicked(int position);
   }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder{
        public TextView cityview;
        public TextView latitudeview;
        public TextView longtitudeview;
        public ImageView deleteview;



        public ListViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            cityview = (TextView) itemView.findViewById(R.id.weather_item_city);
            latitudeview = (TextView) itemView.findViewById(R.id.weather_item_latitude);
            longtitudeview =  (TextView) itemView.findViewById(R.id.weather_item_longtitude);
            deleteview = (ImageView)itemView.findViewById(R.id.deleteweatheritem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getLayoutPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteViewClicked(position);
                        }
                    }
                }
            });
        }
    }

    public WeatherAdapter(ArrayList<WeatherItem> weatherItems){

        weatherItemArrayList = weatherItems;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false);
        ListViewHolder lvh = new ListViewHolder(view,mListener);

        return lvh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

        WeatherItem currentItem = weatherItemArrayList.get(position);
        holder.cityview.setText(currentItem.getCityname());
        holder.latitudeview.setText(currentItem.getLatitute()+"");
        holder.longtitudeview.setText(currentItem.getLongitude()+"");


    }

    @Override
    public int getItemCount() {

        return (weatherItemArrayList == null) ? 0 : weatherItemArrayList.size();

    }
}
