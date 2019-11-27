package com.dataexpo.facedataexpo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.listener.OnLongTime;

public class BgService extends Service {
    private static final String TAG = BgService.class.getSimpleName();
    private int touch = 0;
    private MsgBinder mb = null;
    private OnLongTime onLongTime;
    private long startTime;
    private long endTime;
    private boolean bInLongTimeNoTouch = false;

    @Override
    public IBinder onBind(Intent intent) {
        if (mb == null) {
            mb = new MsgBinder();
        }
        return mb;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        startTime = Utils.timeNow_();
        endTime = Utils.timeNow_();

        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    endTime = Utils.timeNow_();

                    if (endTime - startTime > 1000*30 && !bInLongTimeNoTouch) {
                        if (onLongTime != null) {
                            onLongTime.OnLongTimeNoTouch();
                            bInLongTimeNoTouch = true;
                        }
                    }

                    Log.i(TAG, "dingdong!! " + startTime + " " + endTime);
                }
            }
        }).start();
    }

    public boolean isbInLongTimeNoTouch() {
        return bInLongTimeNoTouch;
    }

    public void setbInLongTimeNoTouch(boolean bInLongTimeNoTouch) {
        this.bInLongTimeNoTouch = bInLongTimeNoTouch;
    }

    public void touch() {
        startTime = Utils.timeNow_();
        endTime = Utils.timeNow_();
    }

    public void setCallback(OnLongTime onLongTime) {
        this.onLongTime = onLongTime;
    }

    public class MsgBinder extends Binder {
        public BgService getService() {
            return BgService.this;
        }
    }
}
