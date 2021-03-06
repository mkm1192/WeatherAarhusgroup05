package com.example.mathias.weatheraarhusgroup05;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Thread;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.currentThread;

public class WeatherService extends Service {
    public static final String BROADCAST_WEATHER_CHANGE = "change";
    private final IBinder binder = new WeatherBinder();
    private URL url;
    private int temperature;
    private String wDesc;
    private DatabaseHelper dbHelper;
    private boolean started = false;
    private PowerManager.WakeLock wl;

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
        Log.d("create","Weather service created");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNjfdhotDimScreen");
        wl.acquire();
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        started = true;
        if(intent != null) {
            backgroundWeatherUpdate();
        }
        return START_STICKY;
    }

    private void backgroundWeatherUpdate() {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
/*                try {
                    Log.d("thread", "Thread is waiting 1 min");
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                Log.d("update", "Getting Weather update");
                sendRequest();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.i("tag","Next update in 30 min");
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if(started) {
                                    backgroundWeatherUpdate();
                                }
                            }
                        }, 1800000);
            }
        };
        task.execute();
    }


    private void broadcastWeatherUpdate(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_WEATHER_CHANGE);
        Log.d("service", "Broadcasting:");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
    @Override
    public void onDestroy() {
        started = false;
        wl.release();
        Log.d("dest","Weather service destroyed");
        super.onDestroy();
    }

    public void getCurrentWeather() throws ExecutionException, InterruptedException {
        AsyncTask task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params){
                sendRequest();
                return null;
            }


        };
        task.execute();


    }

    private void sendRequest() {
        InputStream is = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?id=2624652&APPID=60625d82b841767379c3699f40e44971");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("res", "The response is: " + response);
            is = conn.getInputStream();

            String contentAsString = readIt(is, 500);
            Log.d("parse", "parsing weather data");
            dbHelper.addWeatherInfo(parseIt(contentAsString));
            broadcastWeatherUpdate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private WeatherInfo parseIt(String s) throws JSONException {

        JSONObject obj = new JSONObject(s);
        temperature = obj.getJSONObject("main").getInt("temp");
        temperature -= 273;
        JSONArray w = obj.getJSONArray("weather");
        wDesc = ((JSONObject) w.get(0)).getString("description");
        Log.d("desc", "weather data parsed, temp: " + temperature + " Description: " + wDesc);
        return new WeatherInfo(wDesc, temperature);
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public List<WeatherInfo> getPastWeather() throws ExecutionException, InterruptedException {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                return dbHelper.getAllWeatherInfo();

            }
        };
        task.execute();
        return (List<WeatherInfo>) task.get();
    }
}
