package com.haozan.caipiao.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.webkit.WebView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;

public class GeneralJavaScript {
    private WebView webview;
    // 使用一个handler来处理加载事件
    private Handler handler;
    private Context context;

    public GeneralJavaScript(Context context, Handler handler) {
        this.handler = handler;
        webview = (WebView) ((Activity) context).findViewById(R.id.webView);
        this.context = context;
    }

    /*
     * java调用显示网页，异步
     */
    public void show() {
        handler.post(new Runnable() {
            public void run() {
// 重要：url的生成,传递数据给网页
                String url = "javascript:backToClient()";
                webview.loadUrl(url);
            }
        });
    }

    /*
     * 拨打电话方法
     */
    public void call(final String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /*
     * 刷新积分
     */
    public void updateUserScore(final String score) {
        try {
            LotteryApp appState = (LotteryApp) context.getApplicationContext();
            appState.setScore(Integer.valueOf(score));
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /*
     * 结束当前页
     */
    public void closeCurrentActivity() {
        ((Activity) context).finish();
    }

    public void jumpToActivity(String className) {
        String fullClassName = LotteryUtils.getPackageName(context) + className;
        Class<?> c = null;
        Intent intent = new Intent();
        try {
            c = Class.forName(fullClassName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        intent.setClass(context, c);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}