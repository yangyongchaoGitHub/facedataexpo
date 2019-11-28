package com.dataexpo.facedataexpo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.listener.OnServeiceCallback;

public class BgService extends Service {
    private static final String TAG = BgService.class.getSimpleName();
    public static final int ACTION_TIMEOUT = 1;
    public static final int ACTION_HAVE_FACE = 2;

    public static final int STATUS_VIDEO = 1;
    public static final int STATUS_ACTIVITY = 2;
    public static final int STATUS_SCREENSAVE = 3;
    private volatile int mScreenStatus = STATUS_VIDEO;

    private int touch = 0;
    private MsgBinder mb = null;
    private OnServeiceCallback onServeiceCallback;
    private long startTime;
    private long endTime;

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
                long timgout = 0;
                int action = 0;

                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    endTime = Utils.timeNow_();
                    timgout = endTime - startTime;

                    //检测时间是否超时
                    if (timgout > 1000*10) {
                        //在视频显示状态或者菜单设置的界面时
                        if (mScreenStatus == STATUS_VIDEO || mScreenStatus == STATUS_ACTIVITY) {
                            action = ACTION_TIMEOUT;
                            mScreenStatus = STATUS_SCREENSAVE;
                        }
                    } else if (mScreenStatus == STATUS_SCREENSAVE) {
                        //如果未超时并且在屏保状态下，则发送恢复回调
                        action = ACTION_HAVE_FACE;
                        mScreenStatus = STATUS_VIDEO;
                    }

                    //发送超时的回调
                    if (onServeiceCallback != null && action != 0) {
                        onServeiceCallback.onCallback(action);
                    }

                    Log.i(TAG, "dingdong!! " + startTime + " " + endTime + " " + mScreenStatus + " " + action);

                    action = 0;
                }
            }
        }).start();
    }

    public final int getmScreenStatus() {
        return mScreenStatus;
    }

    public void touch() {
        startTime = Utils.timeNow_();
        endTime = Utils.timeNow_();
    }

    public void setCallback(OnServeiceCallback onServeiceCallback) {
        this.onServeiceCallback = onServeiceCallback;
    }

    public class MsgBinder extends Binder {
        public BgService getService() {
            return BgService.this;
        }
    }
}
