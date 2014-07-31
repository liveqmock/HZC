package com.haozan.caipiao.request.topup;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * ShenZhouFu request
 * 
 * @author lugq
 * @create-time 2014-7-25 下午10:27:47
 */
public class ShenZhouFuTopupRequest extends AsyncConnectionInf {
    private static final String PHONECARD_REQUEST_SERVICE = "2006001";

    private UEDataAnalyse mUploadRequestTime;

    private String mCardType;
    private String mCardAmt;
    private String payMoney;
    private String mCardNumber;
    private String mCardPassword;

    private Context mContext;
    private Handler mHandler;

    /**
     * @param context 
     * @param handler 
     * @param cardType  type of prepaid phone card. 
     * @param cardAmt  card money.
     * @param payMoney  actual money to account.
     * @param cardNum  number of card.
     * @param cardPwd  password of card.
     */
    public ShenZhouFuTopupRequest(Context context, Handler handler, String cardType, String cardAmt,String payMoney, String cardNum, String cardPwd) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
        this.mCardType = cardType;
        this.mCardAmt = cardAmt;
        this.payMoney = payMoney;
        this.mCardNumber = cardNum;
        this.mCardPassword = cardPwd;
        mUploadRequestTime = new UEDataAnalyse(context);
    }

    /**
     * save parameter for accessing network.
     * @return HashMap
     */
    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", PHONECARD_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone", HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        
        parameter.put("cardTypeCombine", mCardType);
        parameter.put("cardMoney", mCardAmt);	
        parameter.put("payMoney", payMoney);	
        parameter.put("sn", mCardNumber);
        //parameter.put("password", HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(mContext, mCardPassword)));
        parameter.put("password", mCardPassword);
        
        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getSecuryGateways(true, initHashMap());
        Logger.inf("ShenZhouFu", connection.getmUrl());
        createUrlGet(connection.getmUrl());
        
    }
    
    @Override
    public void afterRun(String[] data) {
        // 打印基本网络访问信息
        //Logger.inf("lottery_url", mUrl);
        /*if (mRequestMethod == AsyncConnectionBasic.POST_METHOD_INDEX) {
        	// Logger.inf("lottery_url_post_map", mPostData);
        }*/
        Logger.inf("lottery_url_result", data[0] + "," + data[1]);

        Integer httpCode = Integer.valueOf(data[0]);
        if (httpCode.intValue() == AsyncConnectionBasic.GET_SUCCEED_STATUS) {
            onSuccess(" ", httpCode.intValue());
        }
        else {
            onFailure(data[1], httpCode.intValue());
        }
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectSuccess(PHONECARD_REQUEST_SERVICE);

        Message message = Message.obtain(mHandler, ControlMessage.UNIONPAY_PLUGIN_TOPUP_SUCCESS);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(PHONECARD_REQUEST_SERVICE, statusCode + ":" + rspContent);

        Message message = Message.obtain(mHandler, ControlMessage.TOPUP_FAIL, statusCode, 0, rspContent);
        message.sendToTarget();
    }
}