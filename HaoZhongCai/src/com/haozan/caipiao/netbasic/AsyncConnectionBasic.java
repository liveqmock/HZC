package com.haozan.caipiao.netbasic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import android.util.Log;

import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.util.Logger;

/**
 * 访问服务器接口的线程
 * 
 * @author peter_wang
 * @create-time 2013-10-14 下午3:07:25
 */
public class AsyncConnectionBasic
    extends Task {
    private static final String TAG = "peter_wang";

    private static final int TIME_OUT_CONNECTION = 8000;
    private static final int TIME_OUT_SOCKET = 6000;

    public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;
    public static final int DID_OAUTH_ERROR = 3;

    public static final int GET_METHOD_INDEX = 0;
    public static final int POST_METHOD_INDEX = 1;

    public static final String GET_METHOD = "get";
    public static final String POST_METHOD = "post";

    public static final int GET_SUCCEED_STATUS = 200; // 请求成功
    public static final int POST_SUCCEED_STATUS = 201; // 上传数据成功
    public static final int PROCESSING_SUCCEED_STATUS = 202; // 请求已经接受但是还没处理

    public static final int INVALID_ERROR_STATUS = 400; // 请求无效
    public static final int OAUTH_ERROR_STATUS = 401; // 签名出错 -9034,-9035
    public static final int DATA_ERROR_STATUS = 404; // 数据不存在

    public static final int ACCESS_FAIL_STATUS = 405;// 访问没成功统一处理

    public static final int PLATFORM_ERROR_STATUS = 500; // 内部服务器错误

    public static final String ERROE_DESCRIPTION = "error_desc";

    private HttpClient mHttpClient;
    private HttpRequestBase mRequest;

    private AsyncConnectionInf mConnectionInf;

    public static final String ERR_MSG_REQ = "请求异常";

    public AsyncConnectionBasic(AsyncConnectionInf connectionInf) {
        this.mConnectionInf = connectionInf;
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
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }
        catch (UnsupportedEncodingException e) {
        }
        return httpPost;
    }

    public HttpPost post(String url, HashMap<String, String> para) {
        HttpPost httpPost = new HttpPost(url);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            Object[] oo = para.keySet().toArray();
            for (Object o : oo) {
                nameValuePairs.add(new BasicNameValuePair((String) o, para.get(o)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        }
        catch (Exception ex) {
            httpPost.abort();
        }
        return httpPost;
    }

    @Override
    public void runTask() {
        mConnectionInf.showProgress();
 
        mConnectionInf.preRun();

        mHttpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), TIME_OUT_CONNECTION);
        HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), TIME_OUT_SOCKET);

        checkProxySetting(mHttpClient);
        try {
            HttpResponse response = null;
            switch (mConnectionInf.mRequestMethod) {
                case GET_METHOD_INDEX: {
                    HttpGet httpGet = new HttpGet(mConnectionInf.mUrl);
                    response = mHttpClient.execute(httpGet);
                    break;
                }
                case POST_METHOD_INDEX: {
                    response = mHttpClient.execute(post(mConnectionInf.mUrl, mConnectionInf.mPostData));
                    break;
                }
            }
            String[] result = processEntity(response.getEntity(), response.getStatusLine().getStatusCode());
            if (Integer.valueOf(result[0]) == HttpStatus.SC_OK) {
                String status = RequestResultAnalyse.getStatus(result[1]);//
                if (status.equals(String.valueOf(GET_SUCCEED_STATUS)) == false) {
                    result[0] = status;
                    result[1] = RequestResultAnalyse.getData(result[1], ERROE_DESCRIPTION);
                }
            }
            mConnectionInf.afterRun(result);
        }
        catch (Exception e) {
            e.printStackTrace();
            String aResultArray[] = {String.valueOf(ACCESS_FAIL_STATUS), "数据获取失败"};
            mConnectionInf.afterRun(aResultArray);
        }

        mConnectionInf.dismissProgress();
    }

    private void checkProxySetting(HttpClient httpClient) {
        boolean useProxy = APNUtils.hasProxy(mConnectionInf.mContext);
        if (useProxy) {
            HttpHost proxy =
                new HttpHost(APNUtils.getApnProxy(mConnectionInf.mContext),
                             APNUtils.getApnPortInt(mConnectionInf.mContext));
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
    }

    // 字符串 JSON
    private String[] processEntity(HttpEntity entity, int statusCode)
        throws IllegalStateException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        while ((line = br.readLine()) != null)
            result += line;
//        result = URLDecoder.decode(result, "utf-8");
        String aResultArray[] = {"" + statusCode, result};
        return aResultArray;
    }

    public void dispose() {
        if (mRequest != null)
            mRequest.abort();
        if (mHttpClient != null)
            mHttpClient.getConnectionManager().shutdown();
        mHttpClient = null;
    }

}
