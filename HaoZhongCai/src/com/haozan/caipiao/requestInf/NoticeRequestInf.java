package com.haozan.caipiao.requestInf;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 公告请求接口url
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:45:49
 */
public class NoticeRequestInf {
    private Context mContext;
    private HttpConnection mConnection;

    public NoticeRequestInf(Context context) {
        this.mContext = context;
        mConnection = new HttpConnection(mContext);
    }

    private HashMap<String, String> initHashMap(int page, int num) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2001090");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("page_no", "" + page);
        parameter.put("num", "" + num);
        parameter.put("version", LotteryUtils.fullVersion(mContext));
        return parameter;
    }

    public String getUrl(int page) {
        mConnection.getGateways(false, initHashMap(page, 10));
        return mConnection.getmUrl();
    }

    public String getFirstNoticeUrl() {
        mConnection.getGateways(false, initHashMap(1, 1));
        return mConnection.getmUrl();
    }
}