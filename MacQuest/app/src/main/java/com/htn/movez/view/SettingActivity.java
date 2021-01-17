package com.htn.movez.view;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.htn.movez.R;
import com.htn.movez.iodetection.DataFacade;
import com.htn.movez.presenter.ListenerDependedPreference;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends PreferenceActivity {
    private DataFacade datafacade;
    private String[] prefKeys;
    private Map<String, String[]> dependencies;
    private Map<String, Object> disableValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.preference);
        datafacade = (DataFacade) getApplication();
        prefKeys = getResources().getStringArray(R.array.preference_keys);
        dependencies = initializeDependencies();
        disableValues = initializeDisableValue();
        initializeEnableStatus();
        setPrefsListeners();
    }

    @Override
    protected void onPause() {
        datafacade.saveAll();
        super.onPause();
    }

    private void initializeEnableStatus() {
        Map<String, ?> prefMap = PreferenceManager.getDefaultSharedPreferences(this).getAll();
        for (String key : prefKeys) {
            boolean enableFlag;
            Object value = prefMap.get(key);
            enableFlag = !(value.equals(findDisableValue(key)));
            for (Preference pref : findDependents(key)) {
                pref.setEnabled(enableFlag);
            }
            Preference pref = findPreference(key);
            if (pref instanceof EditTextPreference) {
                if (Integer.parseInt((String) value) == -1) {
                    pref.setSummary("Unset");
                } else {
                    pref.setSummary("Currently: " + (String) value);
                }
            }
        }

    }

    private Map<String, String[]> initializeDependencies() {
        Resources res = getResources();
        Map<String, String[]> dependencies = new HashMap<String, String[]>();
        for (String key : prefKeys) {
            int id = res.getIdentifier(key + "_dp", "array", getPackageName());
            if (id != 0) {
                String[] values = res.getStringArray(id);
                dependencies.put(key, values);
            }
        }
        return dependencies;
    }

    private abstract static class CallbackGetter {
        abstract Object get(int id);
    }

    private Map<String, Object> initializeDisableValue() {
        final Resources res = getResources();
        Map<String, Object> disableValues = new HashMap<String, Object>();
        String[] possibleTypes = {"string", "integer", "bool"};
        CallbackGetter[] typesGetter = {
                new CallbackGetter() {
                    @Override
                    Object get(int id) {
                        return res.getString(id);
                    }
                },
                new CallbackGetter() {
                    @Override
                    Object get(int id) {
                        return res.getInteger(id);
                    }
                },
                new CallbackGetter() {
                    @Override
                    Object get(int id) {
                        return res.getBoolean(id);
                    }
                }};
        for (String key : prefKeys) {
            for (int i = 0; i < possibleTypes.length; i++) {
                int id = res.getIdentifier(key + "_ds", possibleTypes[i], getPackageName());
                if (id != 0) {
                    Object value = typesGetter[i].get(id);
                    disableValues.put(key, value);
                    break;
                }
            }
        }
        return disableValues;
    }

    private Preference[] findDependents(String key) {
        if (dependencies.containsKey(key)) {
            String[] dependentNames = dependencies.get(key);
            Preference[] dependents = new Preference[dependentNames.length];
            for (int i = 0; i < dependents.length; i++) {
                dependents[i] = findPreference(dependentNames[i]);
            }
            return dependents;
        } else {
            return new Preference[0];
        }
    }

    private Object findDisableValue(String key) {
        if (disableValues.containsKey(key)) {
            return disableValues.get(key);
        } else {
            return null;
        }
    }


    private void setPrefsListeners() {
        for (String key : prefKeys) {
            Preference[] dependents = findDependents(key);
            Object disableValue = findDisableValue(key);
            (findPreference(key)).setOnPreferenceChangeListener(
                    new ListenerDependedPreference(datafacade, dependents, disableValue)
            );
        }
    }


}
