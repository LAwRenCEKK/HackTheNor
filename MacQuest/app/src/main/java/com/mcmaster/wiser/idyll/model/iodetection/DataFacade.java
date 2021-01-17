package com.mcmaster.wiser.idyll.model.iodetection;

import android.app.Application;
import android.content.SharedPreferences;

import com.mcmaster.wiser.idyll.R;

import java.util.HashMap;
import java.util.Map;


public class DataFacade extends Application {
    private Map <String, Integer> pref = new HashMap<String, Integer>();
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        settings = getSharedPreferences(getResources().getString(R.string.config_filename), 0);
        editor = settings.edit();
        mapInit();
        readAll();
    }

    private void mapInit() {
        // default values
        pref.put("pin", -1);
        pref.put("ec", -1);
        pref.put("alarm_duration", 5);
        pref.put("indoor_ecr", 0);
        pref.put("outdoor_ecr", 0);
        pref.put("no_disturb_time_start", 20);
        pref.put("no_disturb_time_end", 8);
        pref.put("indoor_timer", 300);
        pref.put("outdoor_timer", 60);
        pref.put("volume_adjust", 1);
        pref.put("volume_out", 100);
        pref.put("volume_in", 20);
        pref.put("internet_adjust", 1);
    }

    private int readInt(String key) {
        return settings.getInt(key, -1);
    }

    private void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }


    public void readAll() {
        for (String key : pref.keySet()) {
            int readResult = readInt(key);
            if (readResult != -1) {
                pref.put(key, readResult);
            }
        }
    }

    public void saveAll() {
        for (String key : pref.keySet()) {
            saveInt(key, pref.get(key));
        }
    }


    public void setInt(String key, int value) {
        pref.put(key, value);
    }

    public int getInt(String key) {
        return pref.get(key);
    }
}

