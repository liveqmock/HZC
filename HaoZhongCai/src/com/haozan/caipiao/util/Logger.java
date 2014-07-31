package com.haozan.caipiao.util;

import android.util.Log;

import com.haozan.caipiao.LotteryConfig;

/**
 * 用于打统一日志,开放平台Android SDK调试日志以 TAG="OpenSdkLog" 的形式打出
 * 在LotteryConfig里通过开关isDebug开启日志和关闭日志，应用发布前一定要关闭日志，提供应用运行性能
 */
public class Logger {
    public static final String TAG = "lottery";

    public static void inf(String msg) {
        if (!LotteryConfig.B_LOG_OPEN)
            return;
        Log.i(TAG, msg);
    }

    public static void inf(String tag, String msg) {
        if (!LotteryConfig.B_LOG_OPEN)
            return;
        Log.i(tag, msg);
    }
}
