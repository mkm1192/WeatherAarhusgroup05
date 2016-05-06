package com.example.mathias.weatheraarhusgroup05;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection weatherConnection;
    private WeatherService wService;
    private Button update;
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

                    Log.d("current", "Aarhus weather: " + wService.getCurrentWeather().getDescription() + " " + wService.getCurrentWeather().getTemp());
                } else {
                    Log.d("error", "service is null");
                }
            }
        });
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
