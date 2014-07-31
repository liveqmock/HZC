package com.haozan.caipiao.netbasic;

import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * 网络连接类
 * 
 * @author peter_feng
 * @create-time 2013-7-6 上午11:49:59
 */
public class ConnectService {

    private Context context;

    public ConnectService(Context context) {
        this.context = context;
    }

    public StringBuilder getJsonBasic(int kind, Boolean withSession) {
        StringBuilder url = new StringBuilder();
        if (kind == 1) {
            url.append(Domain.getHTTPURL(context) + "services/gateway");
        }
        else if (kind == 2) {
            url.append(Domain.getHTTPURL(context) + "gateways");
        }
        else if (kind == 3) {
            url.append(Domain.getHTTPURL(context) + "secury_gateways");
        }
        else if (kind == 4) {
            url.append(Domain.getHTTPURL(context) + "garden");
        }
        else if (kind == 5) {
            url.append(Domain.getHTTPURL(context) + "sports");
        }
        else if (kind == 6) {
            url.append(Domain.getHTTPURL(context) + "servlet/ent");
        }
        else if (kind == 7) {
            url.append(Domain.getHTTPURL(context) + "secury_gateways");
        }
        else if (kind == 8) {
            url.append(Domain.getHTTPURL(context) + "secury_api3");
        }
        else if (kind == 9) {
            url.append(Domain.getHTTPURL(context) + "api3");
        }
        if (withSession == true) {
            url.append(";jsessionid=");
            LotteryApp appState = ((LotteryApp) context.getApplicationContext());
            String sessionId = appState.getSessionid();
            url.append(sessionId);
        }
        return url;
    }

    /**
     * 获取服务器数据接口
     * 
     * @param kind url类型，1代表第一版services/gateway，2代表gateways，3代表secury_gateways，4代表garden，5代表sports,6代表竞猜
     * @param withSession 是否带session
     * @param map 传递进来的map参数
     * @return 获取到的服务器json数据
     */
    public String getJson(int kind, Boolean withSession, HashMap<String, String> map) {
        return getJsonPost(kind, withSession, map);
    }

    /**
     * 获取服务器数据接口post方式
     * 
     * @param kind url类型，1代表第一版services/gateway，2代表gateways，3代表secury_gateways，4代表garden，5代表sports,6代表竞猜
     * @param withSession 是否带session
     * @param map 传递进来的map参数
     * @return 获取到的服务器json数据
     */
    public String getJsonPost(int kind, Boolean withSession, HashMap<String, String> map) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String time = appState.getTime();
        String json;
        if (time == null) {
            GetServerTime getServerTime = new GetServerTime(context);
            time = getServerTime.getFormatTime();
            OperateInfUtils.refreshTime(context, time);
        }
        if (time == null) {
            return null;
        }
        AndroidHttpClient client = new AndroidHttpClient(context);
        StringBuilder url = getJsonBasic(kind, withSession);
// url.append("?");
        Object[] oo = map.keySet().toArray();
// for (Object o : oo)
// url.append(o + "=" + map.get(o) + "&");
        map.put("timestamp", time);
        if (kind == 3 || kind == 8) {
            Arrays.sort(oo);
            StringBuffer sb = new StringBuffer();
            sb.append(AndroidHttpClient.decodeParameter(map.get("phone")));
            sb.append(map.get("service"));
            sb.append(time);
            time = sb.toString();
        }
        map.put("sign", getMD5Sign(time));
        json = client.post(url.toString(), map);
        url = null;
        return json;
    }

    /**
     * 获取服务器数据接口get方式
     * 
     * @param kind 
     *            url类型，1代表第一版services/gateway，2代表gateways，3代表secury_gateways，4代表garden，5代表sports,6代表竞猜,7代表送彩票获取短连接
     *            8代表第三版安全接口 9第三版普通接口
     * @param withSession 是否带session
     * @param map 传递进来的map参数
     * @return 获取到的服务器json数据
     */
    public String getJsonGet(int kind, Boolean withSession, HashMap<String, String> map) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String time = appState.getTime();
        String json;
        if (time == null) {
            GetServerTime getServerTime = new GetServerTime(context);
            time = getServerTime.getFormatTime();
            OperateInfUtils.refreshTime(context, time);
        }
        if (time == null) {
            return null;
        }
        AndroidHttpClient client = new AndroidHttpClient(context);
        StringBuilder url = getJsonBasic(kind, withSession);
        url.append("?");
        Object[] oo = map.keySet().toArray();
        for (Object o : oo)
            url.append(o + "=" + map.get(o) + "&");
        url.append("timestamp=" + time + "&");
        if (kind == 3 || kind == 8) {
            Arrays.sort(oo);
            StringBuffer sb = new StringBuffer();
            sb.append(AndroidHttpClient.decodeParameter(map.get("phone")));
            sb.append(map.get("service"));
            sb.append(time);
            time = sb.toString();
        }
        url.append("sign=" + getMD5Sign(time));
        Logger.inf("lottery_url", url.toString());
        json = client.get(url.toString());
        url = null;
        return json;
    }

    /**
     * 获取服务器数据接口post方式
     * 
     * @param url 访问的url连接
     * @param map 传递的参数
     * @return 服务器json数据
     */
    public String getJsonPost(String url, HashMap<String, String> map) {
        if (url == null || map == null)
            return null;
        AndroidHttpClient client = new AndroidHttpClient(context);
        return client.post(url.toString(), map);
    }

    public String getMD5Sign(String parameter) {
        return EncryptUtil.MD5Encrypt(parameter + LotteryUtils.getKey(context));
    }

    /**
     * 直接用url获取数据（get data using url directly）
     * 
     * @param context
     * @param url
     * @return 获取到的数据（the data got）
     */
    public static String getURLJson(Context context, String url) {
        AndroidHttpClient client = new AndroidHttpClient(context);
        String json = client.get(url);
        Logger.inf("lottery_url", url);
        if(null!=json)Logger.inf("lottery_result", json);
        return json;
    }

    /**
     * 获取服务器数据接口get方式
     * 
     * @param kind 
     *            url类型，1代表第一版services/gateway，2代表gateways，3代表secury_gateways，4代表garden，5代表sports,6代表竞猜,7代表送彩票获取短连接
     * @param withSession 是否带session
     * @param map 传递进来的map参数
     * @return 获取到的服务器json数据
     */
    public String getJsonGetUrl(int kind, Boolean withSession, HashMap<String, String> map) {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String time = appState.getTime();
        if (time == null) {
            GetServerTime getServerTime = new GetServerTime(context);
            time = getServerTime.getFormatTime();
            OperateInfUtils.refreshTime(context, time);
        }
        if (time == null) {
            return null;
        }
        StringBuilder url = getJsonBasic(kind, withSession);
        url.append("?");
        Object[] oo = map.keySet().toArray();
        for (Object o : oo)
            url.append(o + "=" + map.get(o) + "&");
        url.append("timestamp=" + time + "&");
        if (kind == 3) {
            Arrays.sort(oo);
            StringBuffer sb = new StringBuffer();
            sb.append(AndroidHttpClient.decodeParameter(map.get("phone")));
            sb.append(map.get("service"));
            sb.append(time);
            time = sb.toString();
        }
        url.append("sign=" + getMD5Sign(time));
        return url.toString();
    }

    public String getJsonTime() {
        String time = "201101010000";
        AndroidHttpClient client = new AndroidHttpClient(context);
        StringBuilder url = getJsonBasic(1, false);
        url.append("?" + "service=get_time&pid=" + LotteryUtils.getPid(context));
        url.append("&timestamp=" + time);
        url.append("&sign=");
        String code = EncryptUtil.MD5Encrypt(time + LotteryUtils.getKey(context));
        url.append(code);
        String json = client.get(url.toString());
        Logger.inf(url.toString() + " 返回信息：" + json);
        url = null;
        return json;
    }
}
