package com.example.mathias.weatheraarhusgroup05;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection weatherConnection;
    private WeatherService wService;
    private Button update;
    private WeatherInfo current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update = (Button) findViewById(R.id.getCurrentButton);

        setupAndBindWeatherService();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wService != null) {
                    try {
                        current = wService.getCurrentWeather();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(current != null) {
                        Log.d("current", "Aarhus weather: " + current.getDescription() + " " + current.getTemp());
                    }
                } else {
                    Log.d("error", "service is null");
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(wService != null) {
            stopService(new Intent(this, WeatherService.class));
        }
        super.onDestroy();
    }

    private void setupAndBindWeatherService() {
        weatherConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wService = ((WeatherService.WeatherBinder)service).getWeatherService();
                setupListView();
                Log.d("conn", "Weather service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                wService = null;
                Log.d("diconn", "Weather service disconnected");
            }
        };
        Intent weatherIntent = new Intent(MainActivity.this, WeatherService.class);
        bindService(weatherIntent, weatherConnection, Context.BIND_AUTO_CREATE);
        startService(weatherIntent);
    }
   private void setupListView() {
       ArrayList<WeatherInfo> testList = new ArrayList<WeatherInfo>();
       for (int i = 0; i < 10; i++) {
           testList.add(i, new WeatherInfo(i, "Cloudy", (i + 10), new Timestamp(System.currentTimeMillis())));
       }
       WeatherAdapter adapter = new WeatherAdapter(this, (ArrayList<WeatherInfo>) wService.getPastWeather());
       ListView listview = (ListView) findViewById(R.id.listView);
       listview.setAdapter(adapter);

   }
}
