package com.dataexpo.facedataexpo.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  常用功能封装
 */
public class Utils {

    public static final String EXTRA_EXPO_USRE = "extra_expo_user";

    /**
     *  获取版本号
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0)
                        .versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "3.0.0";
    }

    /**
     * 时间格式转化
     * @param timeStamp
     * @param pattern
     * @return
     */
    public static String formatTime(long timeStamp, String pattern) {
        Date date = new Date(timeStamp);
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 获取当前时间的字符串
     */
    public static String timeNow() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return dateFormat.format(date);
    }
}


