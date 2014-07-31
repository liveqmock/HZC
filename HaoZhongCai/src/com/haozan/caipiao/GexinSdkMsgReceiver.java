package com.haozan.caipiao;

import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.haozan.caipiao.activity.Main;
import com.haozan.caipiao.task.CommitInfTask;
import com.haozan.caipiao.util.Logger;
import com.igexin.sdk.Consts;

public class GexinSdkMsgReceiver
    extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(Consts.CMD_ACTION)) {

            case Consts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                if (payload != null) {
                    parseInf(payload);
                }
                break;
            case Consts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Logger.inf("getui clientId:" + cid);
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                appState.setGexinId(cid);
                CommitInfTask task = new CommitInfTask(context);
                task.execute();
                break;
            case Consts.BIND_CELL_STATUS:
                String cell = bundle.getString("cell");
                break;
            default:
                break;
        }
    }

    private void parseInf(byte[] payload) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        String data = new String(payload);
        Logger.inf("gexin:" + data);
        String[] msg = data.split(";");
        for (int i = 0; i < msg.length; i++) {
            String[] inf = msg[i].split("=");
            if (inf.length == 2) {
                hashmap.put(inf[0], inf[1]);
            }
        }
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.push;
        CharSequence tickerText = "彩票";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        CharSequence contentTitle = hashmap.get("title");
        CharSequence contentText = hashmap.get("content");

        Intent notificationIntent = new Intent(context, Main.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "notification");
        bundle.putString("action", hashmap.get("action"));
        bundle.putString("action_inf", hashmap.get("action_inf"));
        notificationIntent.putExtras(bundle);

        PendingIntent contentIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(1, notification);
    }
}
