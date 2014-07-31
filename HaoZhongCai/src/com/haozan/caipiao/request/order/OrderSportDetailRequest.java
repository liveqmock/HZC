package com.haozan.caipiao.request.order;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 竞猜足球订单详细比赛数据
 * 
 * @author peter_wang
 * @create-time 2013-11-2 下午1:00:27
 */
public class OrderSportDetailRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    private String mKind;
    private String mOrderId;
    private boolean isUnite;

    public OrderSportDetailRequest(Context context, Handler handler, String kind, String orderId) {
        this(context, handler, kind, orderId, false);
    }

    public OrderSportDetailRequest(Context context, Handler handler, String kind, String orderId,
                                   boolean isUnite) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mKind = kind;
        this.mOrderId = orderId;
        this.isUnite = isUnite;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009041");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("lottery_id", mKind);
        parameter.put("order_id", mOrderId);
        if (isUnite) {
            parameter.put("is_cobuy", "1");
        }
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getSport(false, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        Message message =
            Message.obtain(mHandler, ControlMessage.SPORT_ORDER_DETAIL_SUCCESS_RESULT, rspContent);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.SPORT_ORDER_DETAIL_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}