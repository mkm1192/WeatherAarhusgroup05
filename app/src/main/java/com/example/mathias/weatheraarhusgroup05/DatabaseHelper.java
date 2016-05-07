package com.example.mathias.weatheraarhusgroup05;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mathias on 06-05-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyDatabase.db";

    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_NAME_ID = "my_id";
    private static final String COLUMN_NAME_DESC = "desc";
    private static final String COLUMN_NAME_TEMP = "temp";
    private static final String COLUMN_NAME_TIMESTAMP = "timestamp";

    private static final String TEXT_TYPE = " TEXT";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_NAME_DESC + TEXT_TYPE + "," +
                    COLUMN_NAME_TEMP + " INTEGER NOT NULL," +
                    COLUMN_NAME_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addWeatherInfo(WeatherInfo info) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_DESC, info.getDescription());
        values.put(COLUMN_NAME_TEMP, info.getTemp());

        return (int) db.insert(TABLE_NAME, null, values);
    }

    public List<WeatherInfo> getAllWeatherInfo() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<WeatherInfo> res = new ArrayList<WeatherInfo>();
        String selectTask = "SELECT " + COLUMN_NAME_ID  + ", " +
                COLUMN_NAME_DESC + ", " +
                COLUMN_NAME_TEMP + ", " +
                COLUMN_NAME_TIMESTAMP +
                " FROM " + TABLE_NAME;


        Cursor c = db.rawQuery(selectTask, null);
        c.moveToFirst();

        while(c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(COLUMN_NAME_ID));
            String desc = c.getString(c.getColumnIndex(COLUMN_NAME_DESC));
            int temp = c.getInt(c.getColumnIndex(COLUMN_NAME_TEMP));
            String timestamp = c.getString(c.getColumnIndex(COLUMN_NAME_TIMESTAMP));

            WeatherInfo w = new WeatherInfo(id, desc, temp, Timestamp.valueOf(timestamp));
            res.add(w);
        }
        Collections.sort(res);
        Collections.reverse(res);
        return res;
    }
}
