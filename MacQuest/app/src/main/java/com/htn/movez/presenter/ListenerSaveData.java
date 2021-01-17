package com.htn.movez.presenter;

import android.preference.Preference;

import com.htn.movez.iodetection.DataFacade;

public class ListenerSaveData implements Preference.OnPreferenceChangeListener {
    private final DataFacade dataFacade;
    public ListenerSaveData(DataFacade dataFacade){
        this.dataFacade = dataFacade;
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int cast = -1;
        if(newValue instanceof Integer){
            cast = (int) newValue;
        }else if(newValue instanceof Boolean){
            if(((boolean) newValue)){
                cast = 1;
            }else{
                cast = 0;
            }
        }else if(newValue instanceof String){
            try {
                cast = Integer.parseInt(((String) newValue));
            }catch (Exception e){
                cast = -1;
            }
        }
        dataFacade.setInt(preference.getKey(), cast);
        return true;
    }
}
