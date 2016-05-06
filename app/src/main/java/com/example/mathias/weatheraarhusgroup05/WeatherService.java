package com.example.mathias.weatheraarhusgroup05;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherService extends Service {
    private final IBinder binder = new WeatherBinder();
    private URL url;
    private int temperature;
    private String wDesc;
    private DatabaseHelper dbHelper;

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
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onDestroy() {
        Log.d("dest","Weather service destroyed");
        super.onDestroy();
    }

    public WeatherInfo getCurrentWeather() throws ExecutionException, InterruptedException {
        AsyncTask task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params){
                return sendRequest();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                dbHelper.addWeatherInfo((WeatherInfo) o);


            }
        };
        task.execute();

        return (WeatherInfo) task.get();

    }

    private WeatherInfo sendRequest() {
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
            Log.d("parse", "parsing in main");
            return parseIt(contentAsString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WeatherInfo parseIt(String s) throws JSONException {

        JSONObject obj = new JSONObject(s);
        temperature = obj.getJSONObject("main").getInt("temp");
        JSONArray w = obj.getJSONArray("weather");
        wDesc = ((JSONObject) w.get(0)).getString("description");
        Log.d("desc", "stuffing: " + temperature + wDesc);
        return new WeatherInfo(wDesc, temperature);

    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public List<WeatherInfo> getPastWeather() {
        return dbHelper.getAllWeatherInfo();
    }
}
