package com.haozan.caipiao.request;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 上传用户本地信息
 * 
 * @author peter_wang
 * @create-time 2013-10-29 上午11:02:52
 */
public class CommitUserInfRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;
    private LotteryApp appState;

    public CommitUserInfRequest(Context mContext, Handler handler) {
        super(mContext, handler);
        this.mContext = mContext;
        this.mHandler = handler;

        appState = ((LotteryApp) mContext.getApplicationContext());
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2002070");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone_version", HttpConnectUtil.encodeParameter(android.os.Build.MODEL));
        parameter.put("os_version", HttpConnectUtil.encodeParameter(android.os.Build.VERSION.SDK) + "," +
            HttpConnectUtil.encodeParameter(android.os.Build.VERSION.RELEASE));
        parameter.put("soft_version", LotteryUtils.fullVersion(mContext));
        TelephonyManager telephone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        parameter.put("udid", HttpConnectUtil.encodeParameter(telephone.getDeviceId()));
        String geTuiId = appState.getGexinId();
        if (!TextUtils.isEmpty(geTuiId)) {
            parameter.put("gtid", geTuiId);
        }
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        boolean isWithSession = appState.getUsername() != null;
        connection.getGateways(isWithSession, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
    }
}