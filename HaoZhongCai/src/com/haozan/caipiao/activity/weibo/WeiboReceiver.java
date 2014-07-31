package com.haozan.caipiao.activity.weibo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WeiboReceiver
    extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "微博授权回调", 2000).show();
// intent = new Intent();
// intent.setClass(context, AuthorizeActivity.class);
// context.startActivity(intent);

    }
}
