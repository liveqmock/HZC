package com.haozan.caipiao.request.topup;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.JsonAnalyse;

/**
 * 获取银行列表版本号及地址
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午3:48:19
 */
public class GetBankListVersionRequest
    extends AsyncConnectionInf {
    private static final String URL = "http://m.haozan88.com/User/Bank/index";

    private Context mContext;
    private Handler mHandler;

    public GetBankListVersionRequest(Context context, Handler handler) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void preRun() {
        createUrlGet(URL);
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String version = analyse.getData(rspContent, "version");
        String url = analyse.getData(rspContent, "url");
        String[] params = {version, url};
        Message message = Message.obtain(mHandler, ControlMessage.GET_BANK_LIST_VERSION_SUCCESS, params);
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.GET_BANK_LIST_VERSION_FAIL, rspContent);
        message.sendToTarget();
    }
}