package com.haozan.caipiao.util.error;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.activity.FriendlyCrashPage;

public class DefaultExceptionHandler
    implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultExceptionHandler;

    private static final String TAG = "DefaultExceptionHandler";

    private Context context;

    // constructor
    public DefaultExceptionHandler(Context context, UncaughtExceptionHandler pDefaultExceptionHandler) {
        defaultExceptionHandler = pDefaultExceptionHandler;
        this.context = context;
    }

    // Default exception handler
    public void uncaughtException(Thread t, Throwable e) {
        // Here you should have a more robust, permanent record of problems
        LocalExceptionHandler.exportExceptionInf(e);
        if (LotteryConfig.B_CRASH_TO_HELP) {
            Intent bugReportIntent = new Intent(context, FriendlyCrashPage.class);
            context.startActivity(bugReportIntent);
            ((Activity) context).finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        // call original handler
        defaultExceptionHandler.uncaughtException(t, e);
    }
}