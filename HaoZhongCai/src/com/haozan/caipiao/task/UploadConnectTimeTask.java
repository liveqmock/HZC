package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.netbasic.APNUtils;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.util.CommonUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 上传访问服务器接口时间
 * 
 * @author peter_wang
 * @create-time 2013-11-3 下午8:44:39
 */
public class UploadConnectTimeTask
    extends Task {
    private String mService;
    private String mErrorInf;
    private long mResponseTime;

    private Context mContext;

    public UploadConnectTimeTask(Context context, String service, long time) {
        this.mContext = context;

        this.mService = service;
        this.mResponseTime = time;
    }

    public UploadConnectTimeTask(Context context, String service, String errorInf, long time) {
        this.mContext = context;

        this.mService = service;
        this.mErrorInf = errorInf;
        this.mResponseTime = time;

        if (mErrorInf == null) {
            mErrorInf = "数据获取失败";
        }
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", mService);
        parameter.put("ip", HttpConnectUtil.getLocalIpAddress());
        parameter.put("version", LotteryUtils.fullVersion(mContext));
        parameter.put("udid", CommonUtil.getUdid(mContext));
        parameter.put("netType", APNUtils.getNetType(mContext));
        parameter.put("responseTime", String.valueOf(mResponseTime));
        if (mErrorInf != null) {
            parameter.put("errorDesc", mErrorInf);
        }
        return parameter;
    }

    @Override
    public void runTask() {
        ConnectionBasic connect = new ConnectionBasic(mContext);
        connect.requestGet(Domain.PERSONAL_SERVER_DOMAIN + HttpConnectUtil.generateQueryString(initHashMap()));
        Logger.inf(Domain.PERSONAL_SERVER_DOMAIN + HttpConnectUtil.generateQueryString(initHashMap()));
    }
}