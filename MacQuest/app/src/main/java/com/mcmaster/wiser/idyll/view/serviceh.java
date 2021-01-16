package com.mcmaster.wiser.idyll.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.IBinder;

import android.util.Log;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.iodetection.IODetectionHandler;



public class serviceh extends Service {

    private int one;

    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;
    public static boolean currentResult = true;

    private NotificationManager notificationManager2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final AudioManager mobilemode2 = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        notificationManager2 = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        ioDetectionHandler = new IODetectionHandler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Log.i("infomationHaha", one + "");
                        one++;
                        // If one being in outdoor for more then 20s
                        if((one>20)&(isOutdoor)){

                            one = 0;
                            doSomeThing();
                        }

                        // if one being in indoor for more then 30s
                        if((one>5)&(!isOutdoor)){
                            one = 0;
                            doSomeThing();
                        }

                        currentResult = ioDetectionHandler.main();
                        if (isOutdoor != currentResult){
                            one = 0;
                            isOutdoor = currentResult;
                            changeMode(isOutdoor,mobilemode2);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void doSomeThing(){

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(id, name,importance);
            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(mChannel);



            mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            // Sets an ID for the notification, so it can be updated.
            int notifyID = 1;

            // The id of the channel.
            String CHANNEL_ID = "my_channel_01";

            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Be Safe")
                    .setContentText("Pracrtice Social distancing")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();

                // Issue the notification.
            mNotificationManager.notify(1, notification);
        }
    }

    public void changeMode(boolean isout, AudioManager mobilemode){

        Log.i("infomationHaha", "I am changing the state");


        if (isout){
//            Toast.makeText(getApplicationContext(),"Out max",Toast.LENGTH_SHORT).show();
            Log.i("infomationHaha", "Out max");

            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mobilemode.setStreamVolume(AudioManager.STREAM_RING,mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING),0);
        }
        else if(mobilemode.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
//            Toast.makeText(getApplicationContext(),"Inside viberate",Toast.LENGTH_SHORT).show();
            Log.i("infomationHaha", "Inside viberate");

            mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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

