package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * 手机充值卡充值请求
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午10:27:47
 */
public class PhoneCardTopupRequest
    extends AsyncConnectionInf {
    private static final String PHONECARD_REQUEST_SERVICE = "1005091";

    private UEDataAnalyse mUploadRequestTime;

    private String mCardType;
    private String mCardAmt;
    private String mCardNumber;
    private String mCardPassword;

    private Context mContext;
    private Handler mHandler;

    public PhoneCardTopupRequest(Context context, Handler handler, String cardType, String cardAmt,
                                 String cardNum, String cardPwd) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mCardType = cardType;
        this.mCardAmt = cardAmt;
        this.mCardNumber = cardNum;
        this.mCardPassword = cardPwd;

        mUploadRequestTime = new UEDataAnalyse(context);
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", PHONECARD_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("card_type", mCardType);
        parameter.put("card_amt", mCardAmt);
        parameter.put("card_no", mCardNumber);
        parameter.put("card_pwd",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(mContext, mCardPassword)));

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
        mUploadRequestTime.submitConnectSuccess(PHONECARD_REQUEST_SERVICE);

        Message message = Message.obtain(mHandler, ControlMessage.PHONECARD_TOPUP_SUCCESS);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(PHONECARD_REQUEST_SERVICE, statusCode + ":" + rspContent);

        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, statusCode, 0, rspContent);
        message.sendToTarget();
    }
}