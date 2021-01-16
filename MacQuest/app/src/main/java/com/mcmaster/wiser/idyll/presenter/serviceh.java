package com.mcmaster.wiser.idyll.presenter;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mcmaster.wiser.idyll.model.iodetection.IODetectionHandler;


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