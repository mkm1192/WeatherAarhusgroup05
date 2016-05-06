package com.example.mathias.weatheraarhusgroup05;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class WeatherService extends Service {
    private final IBinder binder = new WeatherBinder();

    public WeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class WeatherBinder extends Binder {
        WeatherService getWeatherService() {
            return WeatherService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
