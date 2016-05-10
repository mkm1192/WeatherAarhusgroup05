package com.example.mathias.weatheraarhusgroup05;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection weatherConnection;
    private WeatherService wService;
    private Button update;
    private WeatherInfo current;
    private TextView desc, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update = (Button) findViewById(R.id.getCurrentButton);
        desc = (TextView) findViewById(R.id.descTxtView);
        temp = (TextView) findViewById(R.id.tempTxtView);

        setupAndBindWeatherService();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wService != null) {

                    try {
                        Log.d("get", "Getting current weather");
                        wService.getCurrentWeather();
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
        Log.d("reg", "registering receivers");

        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.BROADCAST_WEATHER_CHANGE);

        LocalBroadcastManager.getInstance(this).registerReceiver(onWeatherServiceBroadcast, filter);

    }

    @Override
    protected void onDestroy() {
        //unbindService(weatherConnection);
        super.onDestroy();
    }

    private void setupAndBindWeatherService() {
        weatherConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wService = ((WeatherService.WeatherBinder)service).getWeatherService();
                try {
                    setupListView();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("conn", "Weather service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                wService = null;
                Log.d("diconn", "Weather service disconnected");
            }
        };
        Intent weatherIntent = new Intent(MainActivity.this, WeatherService.class);
        startService(weatherIntent);
        bindService(weatherIntent, weatherConnection, Context.BIND_AUTO_CREATE);
    }
   private void setupListView() throws ExecutionException, InterruptedException {
       ArrayList<WeatherInfo> WeatherList;
       WeatherList = (ArrayList<WeatherInfo>) wService.getPastWeather();
       if (WeatherList.size() > 0) {
           current = WeatherList.get(0);
           WeatherList.remove(0);
           desc.setText(current.getDescription());
           temp.setText(String.valueOf(current.getTemp()) + "°");
       } else {
           Log.d("list", "list is null");
       }

       WeatherAdapter adapter = new WeatherAdapter(this, WeatherList);
       ListView listview = (ListView) findViewById(R.id.listView);
       listview.setAdapter(adapter);

   }

    private BroadcastReceiver onWeatherServiceBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broad", "Broadcast reveiced from weather service: " + intent.getAction().toString());
            String action = intent.getAction().toString();
            if(action == "change") {
                try {
                    setupListView();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
