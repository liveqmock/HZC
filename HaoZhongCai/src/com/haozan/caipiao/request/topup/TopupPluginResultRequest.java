package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.types.topup.TopupPluginResult;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 将第三方支付插件错误信息上传服务器
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午3:15:25
 */
public class TopupPluginResultRequest
    extends AsyncConnectionInf {

    private TopupPluginResult mResult;

    private Context mContext;

    public TopupPluginResultRequest(Context context, TopupPluginResult result) {
        super(context, null);
        this.mContext = context;

        this.mResult = result;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2001080");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("order_no", mResult.getOrderNo());
        parameter.put("trade_id", mResult.getTradeId());
        parameter.put("trace_no", mResult.getTradeNo());
        parameter.put("trade_code", mResult.getTradeCode());
        parameter.put("trade_desc", mResult.getTradeDesc());
        parameter.put("trade_type", mResult.getTradeType());
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getGateways(true, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
    }
}