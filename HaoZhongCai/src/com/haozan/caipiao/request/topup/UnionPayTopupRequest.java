package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * 银联语音充值接口
 * 
 * @author peter_wang
 * @create-time 2013-11-8 下午4:07:18
 */
public class UnionPayTopupRequest
    extends AsyncConnectionInf {
    private static final String UNIONPAY_REQUEST_SERVICE = "1005081";

    private static final int DEBITCARD = 1;
    private static final int CREDITCARD = 2;

    private int mCardType;

    private UEDataAnalyse mUploadRequestTime;

    private String mProvince;
    private String mLocation;
    private String mCardNum;
    private String mRechargeMoney;
    private String mUserAddress;// 用户户籍地址，部分情况下需要输入

    private String mReservedPhone;// 预留手机号码，信用卡充值

    private Context mContext;
    private Handler mHandler;

    public UnionPayTopupRequest(Context context, Handler handler, String province, String location,
                                String cardNum, String money, String userAddress) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mProvince = province;
        this.mLocation = location;
        this.mCardNum = cardNum;
        this.mRechargeMoney = money;
        this.mUserAddress = userAddress;

        mUploadRequestTime = new UEDataAnalyse(context);
        mCardType = DEBITCARD;
    }

    public UnionPayTopupRequest(Context context, Handler handler, String cardNum, String money,
                                String userAddress, String reservedPhone) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mReservedPhone = reservedPhone;
        this.mCardNum = cardNum;
        this.mRechargeMoney = money;
        this.mUserAddress = userAddress;

        mUploadRequestTime = new UEDataAnalyse(context);
        mCardType = CREDITCARD;
    }

    private HashMap<String, String> initDebitCardHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", UNIONPAY_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("bank_city", HttpConnectUtil.encodeParameter(mProvince + "," + mLocation));
        parameter.put("card_no",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(mContext, mCardNum)));
        parameter.put("money", mRechargeMoney);
        if (TextUtils.isEmpty(mUserAddress) == false) {
            parameter.put("address=", HttpConnectUtil.encodeParameter(mUserAddress));
        }

        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    private HashMap<String, String> initCreditCardHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", UNIONPAY_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("card_no",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(mContext, mCardNum)));
        parameter.put("reserved", HttpConnectUtil.encodeParameter(mReservedPhone));
        parameter.put("bank", "3");
        parameter.put("money", mRechargeMoney);
        if (TextUtils.isEmpty(mUserAddress) == false) {
            parameter.put("address=", HttpConnectUtil.encodeParameter(mUserAddress));
        }

        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        if (mCardType == DEBITCARD) {
            connection.getSecuryGateways(true, initDebitCardHashMap());
        }
        else {
            connection.getSecuryGateways(true, initCreditCardHashMap());
        }
        createUrlGet(connection.getmUrl());
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess() {
        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        if (mCardType == DEBITCARD) {
            map1.put("way", "union_bankcard");
        }
        else {
            map1.put("way", "union_creditcard");
        }
        map1.put("money", mRechargeMoney);
        UEDataAnalyse.onEvent(mContext, eventNameMob, map1);
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectSuccess(UNIONPAY_REQUEST_SERVICE);

        submitStatisticsTopupSuccess();

        Message message = Message.obtain(mHandler, ControlMessage.UNIONPAY_VOICE_TOPUP_SUCCESS);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(UNIONPAY_REQUEST_SERVICE, statusCode + ":" + rspContent);

        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, statusCode, 0, rspContent);
        message.sendToTarget();
    }
}