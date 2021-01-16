package com.mcmaster.wiser.idyll.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

public class SettingActivity extends PreferenceActivity {
    private DataFacade datafacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.preference);
        datafacade = (DataFacade) getApplication();
        setListeners();
    }

    @Override
    protected void onPause() {
        datafacade.saveAll();
        super.onPause();
    }

    private void onSettingChange(String key, int value) {
        datafacade.setInt(key, value);
    }

    private void setListeners(){
    }

    
}
