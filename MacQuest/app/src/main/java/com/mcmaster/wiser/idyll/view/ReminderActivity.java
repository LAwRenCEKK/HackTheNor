package com.mcmaster.wiser.idyll.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

import org.w3c.dom.Text;

/**
 * The activity that is called when the reminder happens.
 */
public class ReminderActivity extends AppCompatActivity {

    private DataFacade datafacade;
    private boolean isLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        datafacade = (DataFacade) getApplication();
        // todo:
        // 调起这个activity的时候，intent带上reminder文字，
        // 用来更改那两个 textview 显示的文字：
        // "第一行大字"->R.id.status_text
        // "第二行小字"->R.id.note_text
        setButtonAction();
        playAlarm();
    }

    private void setButtonAction() {

        ((FloatingActionButton) findViewById(R.id.button_stop_alarm))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        if(needPIN()){
            ((TextView) findViewById(R.id.status_text)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.note_text)).setText("Please Enter The PIN");
            findViewById(R.id.pin_text).setVisibility(View.VISIBLE);
        }
    }

    private void playAlarm() {
        if (needPIN()) {
            isLocked = true;
        }
        int duration_minutes = datafacade.getInt("alarm_duration");
        // todo: 闹钟持续执行这么多分钟之后开始调用stopAlarm();
    }

    private void stopAlarm() {
        // todo 停止播放铃声
    }


    private boolean needPIN() {
        // todo: 根据提取到的preference进行逻辑判断是否需要pin
        return true;
    }

    private void verifyPIN() {
        String entered_pin = ((EditText) findViewById(R.id.pin_text)).getText().toString();
        int pin;
        try {
            pin = Integer.parseInt(entered_pin);
        }catch (Exception e){
            pin = -2;
        }
        if (pin == datafacade.getInt("pin")) {
            isLocked = false;
        }
    }

    // todo: 发送短信
    private void emergencyText() {
        String notification = getResources().getString(R.string.emergency_notification);
        Toast.makeText(getApplicationContext(), notification, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        if (needPIN()) {
            verifyPIN();
        }
        if (isLocked) {
            emergencyText();
            Intent intent = new Intent(this,
                    ReminderActivity.class);
            startActivity(intent);
        } else{
            stopAlarm();
        }
        super.onPause();
    }
}