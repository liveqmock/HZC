package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 上传用户信息，包括绑定的个推id
 * 
 * @author peter_feng
 * @create-time 2013-4-25 下午5:39:55
 */
public class CommitInfTask
    extends AsyncTask<Void, Object, String> {
    private Context context;

    public CommitInfTask(Context context) {
        this.context = context;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2002070");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone_version", HttpConnectUtil.encodeParameter(android.os.Build.MODEL));
        parameter.put("os_version", HttpConnectUtil.encodeParameter(android.os.Build.VERSION.SDK) + "," +
            HttpConnectUtil.encodeParameter(android.os.Build.VERSION.RELEASE));
        parameter.put("soft_version", LotteryUtils.fullVersion(context));
        TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        parameter.put("udid", HttpConnectUtil.encodeParameter(telephone.getDeviceId()));
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String geTuiId = appState.getGexinId();
        if (!TextUtils.isEmpty(geTuiId)) {
            parameter.put("gtid", geTuiId);
        }
        return parameter;
    }

    @Override
    protected String doInBackground(Void... para) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            LotteryApp appState = ((LotteryApp) context.getApplicationContext());
            String phone = appState.getUsername();
            if (TextUtils.isEmpty(phone)) {
                json = connectNet.getJsonGet(2, false, initHashMap());
            }
            else {
                json = connectNet.getJsonGet(2, true, initHashMap());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
