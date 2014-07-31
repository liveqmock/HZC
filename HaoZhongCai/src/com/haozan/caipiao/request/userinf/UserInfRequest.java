package com.haozan.caipiao.request.userinf;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 用户个人信息接口
 * 
 * @author peter_wang
 * @create-time 2013-11-8 下午12:30:19
 */
public class UserInfRequest
    extends AsyncConnectionInf {
    public static final int REQUEST_USER_BASIC_INF = 1;
    public static final int REQUEST_USER__INF = 2;
    public static final int REQUEST_USER_ALL_INF = 3;

    private Context mContext;
    private Handler mHandler;

    private int mRequestType;

    public UserInfRequest(Context context, Handler handler, int requestType) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mRequestType = requestType;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1005011");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("info_type", String.valueOf(mRequestType));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
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
        String response = RequestResultAnalyse.getData(rspContent, "response_data");
        Message message = Message.obtain(mHandler, ControlMessage.USER_INF_SUCCESS_RESULT, response);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.USER_INF_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}