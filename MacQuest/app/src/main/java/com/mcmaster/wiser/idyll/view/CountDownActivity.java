package com.mcmaster.wiser.idyll.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

public class CountDownActivity extends AppCompatActivity {
    private DataFacade datafacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        datafacade = (DataFacade) getApplication();
    }


    private boolean ecrMode() {
        if (datafacade.getInt("outdoor_ecr") == 1 &&
                datafacade.getInt("pin") != -1 && datafacade.getInt("ec") != -1) {
            return false;
        }
        return true;
    }

    /**
     * Ask the user to enter PIN and check whether PIN is correct.
     * return True if is correct.
     */
    private boolean check_pin() {

        return false;
    }

    private void trigger_alarm() {
        if (datafacade.getInt("pin") != -1) {
            if (check_pin() == false) {

            }
        }
//        if(check_pin() == false){
//            new Callback(){
//                @Override
//                public void excute() {
//
//                }
//            }
//        }
    }


}