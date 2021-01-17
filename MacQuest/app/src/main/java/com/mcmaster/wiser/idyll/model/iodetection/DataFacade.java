package com.mcmaster.wiser.idyll.model.iodetection;

import android.app.Application;
import android.content.SharedPreferences;

import com.mcmaster.wiser.idyll.R;



public class DataFacade extends Application {
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        settings = getSharedPreferences(getResources().getString(R.string.config_filename), 0);
        editor = settings.edit();
    }

    public void setInt(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }
    public int getInt(String key){
        return settings.getInt(key, -1);
    }
}

