package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
/**
 * 插件银联充值获取订单号(新版)
 * 
 * @author fubin
 * @create-time 2014-4-18 11:34 am
 */
public class ChinaUnionOldPayPluginTopupNewRequest extends AsyncConnectionInf {
    private static final String CHINA_NEW_UNION_PAY_REQUEST_SERVICE = "2005141";

    private UEDataAnalyse mUploadRequestTime;

    private String mMoney;

    private Context mContext;
    private Handler mHandler;

    public ChinaUnionOldPayPluginTopupNewRequest(Context context, Handler handler, String money) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mMoney = money;

        mUploadRequestTime = new UEDataAnalyse(context);
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", CHINA_NEW_UNION_PAY_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("card_amt", mMoney);
        parameter.put("channel", "UPMP");
// parameter.put("card_no", "6222003602100336373");

        mUploadRequestTime.onConnectStart();
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
        mUploadRequestTime.submitConnectSuccess(CHINA_NEW_UNION_PAY_REQUEST_SERVICE);

        String topupXml = RequestResultAnalyse.getData(rspContent, "response_data");
        String tn = RequestResultAnalyse.getData(topupXml, "tn");

        Message message = Message.obtain(mHandler, ControlMessage.UNIONPAY_PLUGIN_TOPUP_SUCCESS, tn);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(CHINA_NEW_UNION_PAY_REQUEST_SERVICE, statusCode + ":" +
            rspContent);

        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, statusCode, 0, rspContent);
        message.sendToTarget();
    }
}
