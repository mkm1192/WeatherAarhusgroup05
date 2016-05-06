package com.example.mathias.weatheraarhusgroup05;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Mathias on 06-05-2016.
 */
public class WeatherInfo {
    private int id;
    private String description;
    private int temp;
    private Timestamp timestamp;

    public WeatherInfo(int id, String description, int temp, Timestamp timestamp) {
        this.id = id;
        this.description = description;
        this.temp = temp;
        this.timestamp = timestamp;
    }

    public WeatherInfo(String description, int temp) {
        this.description = description;
        this.temp = temp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


}
