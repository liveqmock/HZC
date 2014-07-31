package com.haozan.caipiao.request.bet;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.types.bet.UniteJoinSubmitOrder;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 合买订单详情接口
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午3:44:39
 */
public class UniteJoinOrderRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    private UniteJoinSubmitOrder mOrder;

    public UniteJoinOrderRequest(Context context, Handler handler, UniteJoinSubmitOrder order) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mOrder = order;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2003111");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone", ActionUtil.getLotteryApp(mContext).getUsername());
        parameter.put("program_id", mOrder.getProgramId());
        parameter.put("tog_num", String.valueOf(mOrder.getJoinNum()));
        if (mOrder.isShare()) {
            parameter.put("action", "1");
            parameter.put("m", mOrder.getShareContent());
        }

        String address = mOrder.getLocation();
        if (address != null) {
            if (address.length() > 50) {
                address = address.substring(0, 50);
            }
            parameter.put("x", String.valueOf(mOrder.getLongitude()));// 用户投注时的手机所处的经纬度
            parameter.put("y", String.valueOf(mOrder.getLatitude()));//
            parameter.put("l", HttpConnectUtil.encodeParameter(address));
        }
        parameter.put("source", HttpConnectUtil.encodeParameter(LotteryUtils.versionName(mContext, true)));
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
        Message message = Message.obtain(mHandler, ControlMessage.UNITE_JOIN_SUCCESS_RESULT);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.UNITE_JOIN_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}