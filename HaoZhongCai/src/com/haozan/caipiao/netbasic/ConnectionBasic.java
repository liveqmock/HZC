package com.haozan.caipiao.netbasic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;

/**
 * 服务器访问工具类，提供直接访问服务器接口功能
 * 
 * @author peter_wang
 * @create-time 2013-10-14 下午3:04:20
 */
public class ConnectionBasic {
    public int timout = 8000;

    private int mRequestMethod;
    private String mUrl;
    private byte[] mPostData;

    private Context mContext;

    public ConnectionBasic(Context context) {
        this.mContext = context;
    }

    private void create(int method, String url, byte[] postData) {
        this.mRequestMethod = method;
        this.mUrl = url;
        this.mPostData = postData;
    }

    /**
     * @param url
     * @return 0代表状态，1代表返回的完整json数据
     */
    public String[] requestGet(String url) {
        create(AsyncConnectionBasic.GET_METHOD_INDEX, url, null);
        return request();
    }

    /**
     * @param url
     * @param postData
     * @return 0代表状态，1代表返回的完整json数据
     */
    public String[] requestPost(String url, byte[] postData) {
        create(AsyncConnectionBasic.POST_METHOD_INDEX, url, postData);
        return request();
    }

    /**
     * 下载
     * 
     * @param url
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    public InputStream requestGetDownload(String url)
        throws IllegalStateException, IOException {
        create(AsyncConnectionBasic.GET_METHOD_INDEX, url, null);
        return requestDownload();
    }

    private HttpEntity getHttpEntity() {
        HttpClient mHttpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), timout);
        HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), timout);

        checkProxySetting(mHttpClient);
        try {
            HttpResponse response = null;
            switch (mRequestMethod) {
                case AsyncConnectionBasic.GET_METHOD_INDEX: {
                    HttpGet httpGet = new HttpGet(mUrl);
                    response = mHttpClient.execute(httpGet);
                    break;
                }
                case AsyncConnectionBasic.POST_METHOD_INDEX: {
                    HttpPost httpPost = new HttpPost(mUrl);
                    InputStream instream = new ByteArrayInputStream(mPostData);
                    InputStreamEntity inputStreamEntity = new InputStreamEntity(instream, mPostData.length);
                    httpPost.setEntity(inputStreamEntity);

                    response = mHttpClient.execute(httpPost);
                    break;
                }
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getEntity();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream requestDownload()
        throws IllegalStateException, IOException {
        return getHttpEntity().getContent();
    }

    public String[] request() {
        try {
            HttpEntity httpEntity = getHttpEntity();
            if (httpEntity != null) {
                return processEntity(httpEntity);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String json[] = {"405", "数据获取失败"};
        return json;
    }

    private void checkProxySetting(HttpClient httpClient) {
        boolean useProxy = APNUtils.hasProxy(mContext);
        if (useProxy) {
            HttpHost proxy = new HttpHost(APNUtils.getApnProxy(mContext), APNUtils.getApnPortInt(mContext));
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
    }

    // 字符串 JSON
    private String[] processEntity(HttpEntity entity)
        throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        while ((line = br.readLine()) != null)
            result += line;

        JsonAnalyse analyse = new JsonAnalyse();
        String json[] = {analyse.getStatus(result), result};
        return json;
    }
}
