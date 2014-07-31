package com.haozan.caipiao.util;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 常用工具类
 * 
 * @author peter_wang
 * @create-time 2013-10-10 下午9:59:31
 */
public class CommonUtil {
    private static int sScreenWidth;
    private static int sScreenHeight;

    public static int getScreenWdith(Context context) {
        if (sScreenWidth == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            sScreenWidth = dm.widthPixels;
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (sScreenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            sScreenHeight = dm.heightPixels;
        }
        return sScreenHeight;
    }

    public static String getScreenResolution(Context context) {
        int width = getScreenWdith(context);
        if (width < 320) {
            return "l";
        }
        else if (width < 480) {
            return "m";
        }
        else if (width < 720) {
            return "h";
        }
        else if (width < 1080) {
            return "xh";
        }
        else
            return "xxh";
    }

    public static String getUdid(Context context) {
        TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephone.getDeviceId();
    }
}
