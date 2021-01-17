package com.mcmaster.wiser.idyll.presenter;

import android.preference.Preference;

import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

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
                Integer.parseInt(((String) newValue));
            }catch (Exception e){
                cast = -1;
            }
        }
        dataFacade.setInt(preference.getKey(), cast);
        System.out.println("changed "+newValue+"   cast to "+cast);
        return true;
    }
}
