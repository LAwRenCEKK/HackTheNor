package com.mcmaster.wiser.idyll.presenter;

import android.preference.EditTextPreference;
import android.preference.Preference;

import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

public class ListenerDependedPreference extends ListenerSaveData {
    private final Preference[] impactPrefs;
    private final Object disableValue;

    public ListenerDependedPreference(DataFacade dataFacade, Preference[] impactPrefs, Object disableValue) {
        super(dataFacade);
        this.impactPrefs = impactPrefs;
        this.disableValue = disableValue;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean enableFlag;
        enableFlag = !(newValue.equals(disableValue));
        for (Preference pref : impactPrefs) {
            pref.setEnabled(enableFlag);
        }
        if (preference instanceof EditTextPreference) {
            preference.setSummary("Currently: " + (String) newValue);
        }
        return super.onPreferenceChange(preference, newValue);
    }
}
