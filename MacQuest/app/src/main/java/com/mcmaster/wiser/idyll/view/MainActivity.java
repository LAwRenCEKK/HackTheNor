package com.mcmaster.wiser.idyll.view;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.IODetectionHandler;
import com.mcmaster.wiser.idyll.presenter.serviceh;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;
    private NotificationManager notificationManager;

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
        Intent intent = new Intent(this, serviceh.class);
        startService(intent);
        setButtonActions();
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

    private void setButtonActions() {
        ((FloatingActionButton) findViewById(R.id.button_setting))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,
                                SettingActivity.class);
                        startActivity(intent);
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
                                        // todo
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

        ((FloatingActionButton) findViewById(R.id.button_stop_alarm2))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,
                                ReminderActivity.class);
                        startActivity(intent);
                    }
                });
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