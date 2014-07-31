package com.haozan.caipiao.netbasic;

import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * 网络连接类
 * 
 * @author peter_wang
 * @create-time 2013-10-10 下午10:04:55
 */
public class HttpConnection {
    // 常用接口前缀
    private static final String URL_SERVICES_GATEWAY = "services/gateway";
    private static final String URL_GATEWAYS = "gateways";
    private static final String URL_SECURY_GATEWAYS = "secury_gateways";
    private static final String URL_GARDEN = "garden";
    private static final String URL_SPORTS = "sports";
    private static final String URL_SERVLET_ENT = "servlet/ent";
    private static final String URL_SECURY_API = "secury_api3";
    private static final String URL_API = "api3";

    private static final int GET_METHOD_INDEX = 0;
    private static final int POST_METHOD_INDEX = 1;

    private int mRequestMethod;
    // 服务器接口名，如services/gateway
    private String mUrlMethod;
    // 整个服务器接口前缀，可能加上session信息
    private String mUrlPrefix;
    private String mUrl;
    private HashMap<String, String> mRequestParams;
    private String mFinalParams;

    private Context mContext;
    private LotteryApp appState;

    public HttpConnection(Context context) {
        this.mContext = context;
        appState = (LotteryApp) ((Activity) mContext).getApplication();
    }

    public void create(boolean isWithSession, int method, String urlMethod,
                       HashMap<String, String> mRequestParams) {
        this.mRequestMethod = method;
        this.mUrlMethod = urlMethod;
        this.mRequestParams = mRequestParams;

        appendTimeSession(mRequestParams);

        if (isWithSession) {
            String sessionId = appState.getSessionid();
            mUrlPrefix = mUrlMethod + ";jsessionid=" + sessionId;
        }
        else {
            mUrlPrefix = mUrlMethod;
        }

        httpAsynSend();
    }

    private void appendTimeSession(HashMap<String, String> mRequestParams) {
        String time = appState.getTime();
        if (time == null) {
            String t =
                (String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
            time = TimeUtils.convertDate(t, "yyyy-MM-dd hh:mm:ss", "yyyyMMddHHmm");
        }
        mRequestParams.put("timestamp", time);

        if (mUrlMethod.equals(URL_SECURY_GATEWAYS) || mUrlMethod.equals(URL_SECURY_API)) {
            Object[] oo = mRequestParams.keySet().toArray();
            Arrays.sort(oo);
            StringBuffer sb = new StringBuffer();
            sb.append(HttpConnectUtil.decodeParameter((String) mRequestParams.get("phone")));
            sb.append(mRequestParams.get("service"));
            sb.append(time);
            time = sb.toString();
        }
        mRequestParams.put("sign", getMD5Sign(time));
    }

    public void getServicesGateway(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_SERVICES_GATEWAY, params);
    }

    public void getGateways(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_GATEWAYS, params);
    }

    public void getSecuryGateways(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_SECURY_GATEWAYS, params);
    }

    public void getGarden(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_GARDEN, params);
    }

    public void getSport(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_SPORTS, params);
    }

    public void getServletEnt(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_SERVLET_ENT, params);
    }

    public void getSecuryApi(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_SECURY_API, params);
    }

    public void getApi(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, GET_METHOD_INDEX, URL_API, params);
    }

    public void postServicesGateway(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_SERVICES_GATEWAY, params);
    }

    public void postGateways(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_GATEWAYS, params);
    }

    public void postSecuryGateways(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_SECURY_GATEWAYS, params);
    }

    public void postGarden(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_GARDEN, params);
    }

    public void postSport(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_SPORTS, params);
    }

    public void postServletEnt(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_SERVLET_ENT, params);
    }

    public void postSecuryApi(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_SECURY_API, params);
    }

    public void postApi(Boolean isWithSession, HashMap<String, String> params) {
        create(isWithSession, POST_METHOD_INDEX, URL_API, params);
    }

    public String getMD5Sign(String parameter) {
        return EncryptUtil.MD5Encrypt(parameter + LotteryUtils.getKey(mContext));
    }

    protected boolean httpAsynSend() {
        mFinalParams = HttpConnectUtil.generateQueryString(mRequestParams);
        if (mRequestMethod == GET_METHOD_INDEX) {
            mUrl = Domain.getHTTPURL(mContext) + mUrlPrefix + "?" + mFinalParams;

        }
        else if (mRequestMethod == POST_METHOD_INDEX) {
            mUrl = Domain.getHTTPURL(mContext) + mUrlPrefix;
        }
        return true;
    }

    public String getmUrl() {
        if (mUrl == null) {
            Logger.inf("请先生成url");
        }

        return mUrl;
    }

    public String getmFinalParams() {
        if (mRequestMethod == GET_METHOD_INDEX) {
            Logger.inf("请求get方法不带params参数");
        }
        if (mFinalParams == null) {
            Logger.inf("请先生成params");
        }

        return mFinalParams;
    }

    public HashMap<String, String> getmFinalMap() {
        if (mRequestMethod == GET_METHOD_INDEX) {
            Logger.inf("请求get方法不带params参数");
        }
        if (mFinalParams == null) {
            Logger.inf("请先生成params");
        }

        return mRequestParams;
    }
}
