package com.mcmaster.wiser.idyll.view;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The activity that is called when the reminder happens.
 */
public class ReminderActivity extends AppCompatActivity {

    private DataFacade datafacade;
    private boolean isLocked = false;
    public TextView H ;
    public TextView M ;
    public TextView S ;
    public int totalTime;
    private Vibrator vibrator;
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

        totalTime = 10;
        H = findViewById(R.id.hour);
        M = findViewById(R.id.minute);
        S = findViewById(R.id.second);
        setButtonAction();
        playAlarm();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                updateTextView(H,M,S,totalTime);
                                totalTime = totalTime - 1;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTextView(TextView H , TextView M, TextView S, int t) {

        H.setText("00");
        M.setText((Math.floorDiv(t,60))+"");
        S.setText(t%60+"");
        if (t == 0){
            sendSMSS();
            stopAlarm();
        }
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

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //震动5秒
        vibrator.vibrate(30000);
        int duration_minutes = datafacade.getInt("alarm_duration");
        // todo: 闹钟持续执行这么多分钟之后开始调用stopAlarm();
    }

    private void stopAlarm() {
        // todo 停止播放铃声
        vibrator.cancel();
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
        if (pin == 1234) {
            isLocked = false;
            stopAlarm();
        }
    }
    private void sendSMSS() {
        String content = "Hello from Lawrence"; //todo: read from tempelate
        String phone = "9059204695"; //todo: read this from property
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(phone)) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
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