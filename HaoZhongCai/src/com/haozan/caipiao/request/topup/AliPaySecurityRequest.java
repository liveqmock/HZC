package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 支付宝快捷充值获取订单号等信息
 * 
 * @author peter_wang
 * @create-time 2013-11-7 上午11:56:02
 */
public class AliPaySecurityRequest
    extends AsyncConnectionInf {

    private String mMoney;

    private Context mContext;
    private Handler mHandler;

    public AliPaySecurityRequest(Context context, Handler handler, String money) {
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
        parameter.put("pay_type", "ALI");
        parameter.put("money", mMoney);
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getSecuryGateways(true, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(rspContent, "response_data");
        String[] alipayResult = alipayResponseData(response);

        Message message =
            Message.obtain(mHandler, ControlMessage.ALIPAY_SECURITY_TOPUP_SUCCESS, alipayResult);
        message.sendToTarget();
    }

    private String[] alipayResponseData(String response) {
        String callUrl = RequestResultAnalyse.getData(response, "call_url");
        String orderNo = RequestResultAnalyse.getData(response, "order_no");
        String partner = RequestResultAnalyse.getData(response, "mid");

        String[] result = {callUrl, orderNo, partner};
        return result;
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, statusCode, 0, rspContent);
        message.sendToTarget();
    }
}