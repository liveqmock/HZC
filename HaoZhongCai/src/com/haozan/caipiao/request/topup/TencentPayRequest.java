package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 财付通充值接口
 * 
 * @author peter_wang
 * @create-time 2013-10-31 下午5:00:32
 */
public class TencentPayRequest
    extends AsyncConnectionInf {

    private String mMoney;

    private Context mContext;
    private Handler mHandler;

    public TencentPayRequest(Context context, Handler handler, String money) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mMoney = money;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005051");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("pay_type", "QQP");
        parameter.put("money", mMoney);
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getSecuryGateways(false, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(rspContent, "response_data");
        Message message = Message.obtain(mHandler, ControlMessage.TENCENT_TOPUP_SUCCESS, response);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, null);
        message.sendToTarget();
    }
}