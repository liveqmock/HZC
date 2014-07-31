package com.haozan.caipiao.requestInf;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 服务器时间接口url
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:47:29
 */
public class ServerTimeRequestInf {

    public HashMap<String, String> initHashMap(Context context) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "get_time");
        parameter.put("pid", LotteryUtils.getPid(context));
        return parameter;
    }

    public String getUrl(Context context) {
        ServerTimeRequestInf requestInf = new ServerTimeRequestInf();
        HttpConnection connection = new HttpConnection(context);
        connection.getServicesGateway(false, requestInf.initHashMap(context));
        return connection.getmUrl();
    }
}