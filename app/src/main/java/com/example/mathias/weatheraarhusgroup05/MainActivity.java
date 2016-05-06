package com.example.mathias.weatheraarhusgroup05;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection weatherConnection;
    private WeatherService wService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAndBindWeatherService();
    }

    private void setupAndBindWeatherService() {
        weatherConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wService = ((WeatherService.WeatherBinder)service).getWeatherService();
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
}
