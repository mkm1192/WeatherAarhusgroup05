package com.example.mathias.weatheraarhusgroup05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mathias on 06-05-2016.
 */
public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WeatherInfo> list;

    public WeatherAdapter(Context context, ArrayList<WeatherInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         /*
           Stolen from Kasper's slide, L4.
         */

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.weather_list_item, null);
        }

        WeatherInfo w = list.get(position);
        if(w != null) {
            TextView weatherDesc = (TextView) convertView.findViewById(R.id.listDescTxtView);
            weatherDesc.setText(w.getDescription());

            TextView weatherTemp = (TextView) convertView.findViewById(R.id.listTempTxtView);
            weatherTemp.setText(String.valueOf(w.getTemp()));

            TextView weatherDate = (TextView) convertView.findViewById(R.id.listDateTxtView);
            weatherDate.setText(w.getTimestamp().toString().substring(0, 10));

            TextView weatherTime = (TextView) convertView.findViewById(R.id.listTimeTxtView);
            weatherTime.setText(w.getTimestamp().toString().substring(10, 16));
        }

        return convertView;
    }
}
