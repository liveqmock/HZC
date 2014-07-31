package com.haozan.caipiao.netbasic;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.haozan.caipiao.util.LotteryUtils;

/**
 * 网络连接基本类
 * 
 * @author peter_feng
 * @create-time 2013-7-6 上午11:45:24
 */
public class AndroidHttpClient {
    private static final int TIME_OUT_CONNECTION = 8000;
    private static final int TIME_OUT_SOCKET = 6000;

    private Context context;

    public AndroidHttpClient(Context c) {
        this.context = c;
    }

    public HttpClient getDefaultHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIME_OUT_CONNECTION);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIME_OUT_SOCKET);
        httpClient.getParams().setParameter("http.useragent", getHTTPUAString());
        checkProxySetting(httpClient);
        return httpClient;
    }

    /**
     * Http头, ua身份
     * 
     * @return
     */
    public String getHTTPUAString() {
        return "source:android,version:" + LotteryUtils.fullVersion(context);
    }

    public void checkProxySetting(HttpClient httpClient) {
        boolean useProxy = APNUtils.hasProxy(context);
        if (useProxy) {
            HttpHost proxy = new HttpHost(APNUtils.getApnProxy(context), APNUtils.getApnPortInt(context));
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
    }

    public InputStream getInputStream(String url) {
        HttpClient httpClient = getDefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        InputStream is = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    is = entity.getContent();
                }
            }
        }
        catch (Exception ex) {
            httpGet.abort();
            is = null;
        }
        return is;
    }

    public HttpEntity getHttpEntity(String url) {
        HttpClient httpClient = getDefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                return entity;
            }
        }
        catch (Exception ex) {
            httpGet.abort();
        }
        return null;
    }

    public static String encodeParameter(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (Exception ex) {
            return "";
        }
    }

    public static String decodeParameter(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (Exception ex) {
            return "";
        }
    }

    // check whether the network is available
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public HttpPost getHttpPost(String url, String urlParameters) {
        HttpPost httpPost = new HttpPost(url);

        String[] ps = urlParameters.split("&");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (String p : ps) {
            if (p.length() > 0) {
                String[] s = p.split("=");
                String name = s[0];
                String value = "";
                if (s.length > 1)
                    value = s[1];
                nameValuePairs.add(new BasicNameValuePair(name, value));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
        }
        return httpPost;
    }

    public String post(String url, HashMap<String, String> para) {
        HttpClient httpClient = getDefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String result = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            Object[] oo = para.keySet().toArray();
            for (Object o : oo) {
                nameValuePairs.add(new BasicNameValuePair((String) o, para.get(o)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                    entity.consumeContent();
                }
            }
        }
        catch (Exception ex) {
            httpPost.abort();
            result = null;
        }
        return result;
    }

    public String get(String url) {
        HttpClient httpClient = getDefaultHttpClient();
        String result = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                    entity.consumeContent();
                }
            }
        }
        catch (Exception ex) {
            result = null;
        }
        return result;
    }
}
