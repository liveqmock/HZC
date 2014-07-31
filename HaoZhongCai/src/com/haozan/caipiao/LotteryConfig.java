package com.haozan.caipiao;

import android.content.Context;

public class LotteryConfig {

    // 是否连接测试服务器
    public static final boolean B_TEST_ENVIRONMENT = false;
    // 是否输出log信息
    public static final boolean B_LOG_OPEN = false;
    // 是否出错跳到出错友好提示页面
    public static final boolean B_CRASH_TO_HELP = false;

    public static String getWeixinAPPID(Context context) {
        String weixinId = "wxec766c16d0b6e7fa";
        String appName = context.getResources().getString(R.string.app_name);
        if (appName.equals("好中彩")) {
            weixinId = "wx8e37e9c1444edbdd";
        }
        else if (appName.equals("号百彩票")) {
            weixinId = "wx31f34c35dfd06719";
        }
        else if (appName.equals("淘彩")) {
            weixinId = "wxd5e3d204778dc88f";
        }
        else if (appName.equals("彩王彩票")) {
            weixinId = "wxd5e3d204778dc88f";
        }
        return weixinId;
    }

    public static String getQQAPPID(Context context) {
        String qqId = "100440200";
        String appName = context.getResources().getString(R.string.app_name);
        if (appName.equals("好中彩")) {
            qqId = "100448705";
        }
        return qqId;
    }

    public static String getSinaKey(Context context) {
        String sinaKey = "2641908462";
        String appName = context.getResources().getString(R.string.app_name);
        if (appName.equals("好中彩")) {
            sinaKey = "4026411725";
        }
        return sinaKey;
    }

    public static String getSinaSecret(Context context) {
        String sinaSecret = "d7481a280924f31b6ee955787be172cd";
        String appName = context.getResources().getString(R.string.app_name);
        if (appName.equals("好中彩")) {
            sinaSecret = "b2f12d0c8d233527c1df782dabce05fa";
        }
        return sinaSecret;
    }
}
