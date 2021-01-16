package com.mcmaster.wiser.idyll.view;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mcmaster.wiser.idyll.detection.iodetection.IODetectionHandler;


public class serviceh extends Service {

    private int one;

    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ioDetectionHandler = new IODetectionHandler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Log.i("infomationHaha", one + "");
                        one++;
                        ioDetectionHandler.main();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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



//public class serviceh extends Service {
//
//    private static final String TAG = "HelloService";
//
//    private boolean isRunning  = false;
//    private IODetectionHandler ioDetectionHandler;
//    public static boolean isOutdoor = true;
//    private NotificationManager notificationManager;
//
//    @Override
//    public void onCreate() {
//        Log.i(TAG, "Service onCreate");
//
//        isRunning = true;
//
//        final AudioManager mobilemode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        ioDetectionHandler = new IODetectionHandler(this);
//        ioDetectionHandler.setOnIOChangeListener(new IODetectionHandler.OnIOChangeListener() {
//            @Override
//            public void onIOChange(boolean isOut) {
//                isOutdoor = isOut;
//                changeMode(isOutdoor, mobilemode);
//            }
//        });
//    }
//
//    public void changeMode(boolean isout, AudioManager mobilemode){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                && !notificationManager.isNotificationPolicyAccessGranted()) {
//            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//            this.startActivity(intent);
//        }
//        if (isout){
////            Toast.makeText(getApplicationContext(),"Out max",Toast.LENGTH_SHORT).show();
//            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            mobilemode.setStreamVolume(AudioManager.STREAM_RING,mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING),0);
//        }
//        else if(mobilemode.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
////            Toast.makeText(getApplicationContext(),"Inside viberate",Toast.LENGTH_SHORT).show();
//            mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//        }
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Log.i(TAG, "Service onStartCommand");
//
//        //Creating new thread for my service
//        //Always write your long running tasks in a separate thread, to avoid ANR
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////
////            while (true){
////                //Your logic that service will perform will be placed here
////                //In this example we are just looping and waits for 1000 milliseconds in each loop.
////                    try {
////                        Thread.sleep(1000);
////                    } catch (Exception e) {
////                    }
////                    if(isRunning){
//                        ioDetectionHandler.main().start();
//                        Log.i(TAG, "Service running");
////                    }}
////            }
////        }).start();
//
//        return Service.START_STICKY;
//    }
//
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        Log.i(TAG, "Service onBind");
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//
//        isRunning = false;
//
//        Log.i(TAG, "Service onDestroy");
//    }
//}