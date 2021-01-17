package com.mcmaster.wiser.idyll.view;

import android.Manifest;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.IODetectionHandler;
import com.mcmaster.wiser.idyll.view.serviceh;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;
    private NotificationManager notificationManager;
    public static int duration;
    private static final int SEND_SMS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AudioManager mobilemode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ioDetectionHandler = new IODetectionHandler(this);
        ioDetectionHandler.setOnIOChangeListener(new IODetectionHandler.OnIOChangeListener() {
            @Override
            public void onIOChange(boolean isOut) {
                isOutdoor = isOut;
                changeMode(isOutdoor, mobilemode);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS);
            }
        }

        ((FloatingActionButton) findViewById(R.id.button_setting))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // todo: make preference activity
//                        Intent intent = new Intent(MainActivity.this,
//                                CountDownActivity.class);
//                        startActivity(intent);
                    }
                });

        ((FloatingActionButton) findViewById(R.id.button_alarm))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TimePickerDialog time = new TimePickerDialog(MainActivity.this,
                                R.style.Float,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar rightNow = Calendar.getInstance();
                                        int correntHour = rightNow.get(Calendar.HOUR_OF_DAY);
                                        int correntMin = rightNow.get(Calendar.MINUTE);
                                        int diffInHour = hourOfDay - correntHour;
                                        int diffInMin = minute - correntMin;
                                        int durationInMin = diffInHour*60 + diffInMin;
//                                        Toast.makeText(getApplicationContext(), ""+ diffInHour, Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getApplicationContext(), ""+ diffInMin, Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getApplicationContext(), ""+ durationInMin, Toast.LENGTH_SHORT).show();
                                        duration = durationInMin;
                                    }
                                },
                                0, 30, true
                        ) {
                            {
                                setTitle("Timer Duration, hour:minute");
                            }
                        };
                        time.show();
                    }
                });

    Intent intent = new Intent(this, serviceh.class);
    startService(intent);

    }


    public void changeMode(boolean isout, AudioManager mobilemode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            this.startActivity(intent);
        }
        if (isout) {
            Toast.makeText(getApplicationContext(), "Out max", Toast.LENGTH_SHORT).show();
            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mobilemode.setStreamVolume(AudioManager.STREAM_RING, mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        } else if (mobilemode.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            Toast.makeText(getApplicationContext(), "Inside viberate", Toast.LENGTH_SHORT).show();
            mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ioDetectionHandler != null) {
            ioDetectionHandler.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ioDetectionHandler != null) {
            ioDetectionHandler.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}