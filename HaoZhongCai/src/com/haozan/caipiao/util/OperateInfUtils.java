package com.haozan.caipiao.util;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;

public class OperateInfUtils {

    public static void clearSessionId(Context context) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        appState.setSessionid(null);
        appState.setUsername(null);
        appState.setNickname(null);
        appState.setBand(0);
        appState.setEmail(null);
        appState.setWinBalance(0);
        appState.setAvailableBalance(0);
        appState.setTime(null);
        appState.setPerfectInf(0);
        appState.setAccount(0);
        appState.setServiced(null);
        appState.setMessageNumber(0);
        appState.setReservedPhone("");
        Editor databaseData = context.getSharedPreferences("user", 0).edit();
        databaseData.putString("username_for_third", "");
        databaseData.commit();
    }

    public static void broadcast(Context context, String type) {
        // 登陆、升级、个人资料消息数
        ApplicationInfo appInfo = context.getApplicationInfo();
        // 指定广播目标的 action （注：指定了此 action 的 receiver 会接收此广播）
        Intent intent = new Intent(context.getResources().getString(R.string.broadcast_name));
        // 需要传递的参数
        intent.putExtra("type", type);
        // 发送广播
        context.sendBroadcast(intent);
    }

    public static void refreshTime(Context context, String time) {
        if (time == null)
            return;
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        appState.setTime(time);
    }

    private HashMap<String, String> initHashMap(Context context) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "user_unread_num");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", String.valueOf(phone));
        return parameter;
    }

    public void refreshMessageNum(Context context) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(3, true, initHashMap(context));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            // get the status of the http data
            String status = ja.getStatus(json);
            if (status.equals("200")) {
                String messageNum;
                messageNum = ja.getData(json, "num");
                if (messageNum != null)
                    appState.setMessageNumber(Integer.getInteger(messageNum));
            }
        }
    }
}
