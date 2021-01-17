package com.mcmaster.wiser.idyll.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.IODetectionHandler;
import com.mcmaster.wiser.idyll.model.iodetection.DataFacade;

import com.mcmaster.wiser.idyll.presenter.CountDownFragment;

import java.util.ArrayList;


public class serviceh extends Service {

    private int tick;
    private int dur;
    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;
    public static boolean currentResult = true;
    private WifiManager mWifiManager;
    private DataFacade mDataFacade;
    private boolean ECR;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final AudioManager mobilemode2 = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mDataFacade = (DataFacade) getApplication();

        dur = 50000;
        ioDetectionHandler = new IODetectionHandler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        dur = MainActivity.duration;
                        ECR = MainActivity.ECServise;
                        Log.i("duration", dur + "");
                        Log.i("infomationHaha", tick + "");
                        tick++;

                        if ((ECR)&(tick > dur)&(!isOutdoor)){
//                            sendSMSS();
                            dur = 1000000;
                        }

                        if ((tick > mDataFacade.getInt("outdoor_timer")) & (isOutdoor)) {
                            tick = 0;
                            pushNotification("Be Safe","Practice Social Distancing");
                        }

                        if ((tick > mDataFacade.getInt("indoor_timer")) & (!isOutdoor)) {
                            tick = 0;
                            openWifi();
                            pushNotification("Relax","Go for some exercise");
                        }

                        // if the In/outdoor status changed
                        currentResult = ioDetectionHandler.main();
                        if (isOutdoor != currentResult) {
                            tick = 0;
                            isOutdoor = currentResult;
                            changeMode(isOutdoor, mobilemode2);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void pushNotification(String title, String content) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "my_channel_02";
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(id, name,importance);
            mChannel.setDescription(description);
            mChannel.enableVibration(true);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});

            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String CHANNEL_ID = "my_channel_02";

            Notification notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();
            mNotificationManager.notify(1, notification);
        }
    }


    public void changeMode(boolean isout, AudioManager mobilemode) {
        Log.i("infomationHaha", "I am changing the state");

        if (isout) {
            Log.i("infomationHaha", "Out max");
            if (mDataFacade.getInt("internet_adjust")==1){
            closeWifi();}
            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mobilemode.setStreamVolume(AudioManager.STREAM_RING, mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

        }
        else if
        (mobilemode.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            MainActivity.ECServise = false;
            if (mDataFacade.getInt("internet_adjust")==1){
                openWifi();}
            Log.i("infomationHaha", "Inside viberate");
            mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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


    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }


    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

