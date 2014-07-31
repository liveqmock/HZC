package com.haozan.caipiao.request;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 大厅竞猜数据显示接口
 * 
 * @author peter_wang
 * @create-time 2013-10-22 上午11:14:10
 */
public class GuessFirstQuetionRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    public GuessFirstQuetionRequest(Context context, Handler handler) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2010050");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("act_code", "0000");
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getServletEnt(false, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(rspContent, "response_data");
        String guessHallInf = analyse.getData(response, "scheme_name");
        Message message = Message.obtain(mHandler, ControlMessage.HALL_GUESS_COTENT, guessHallInf);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.HALL_GUESS_COTENT, null);
        message.sendToTarget();
    }
}