package com.haozan.caipiao.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;

/**
 * 下载任务
 * 
 * @author peter_wang
 * @create-time 2013-10-16 下午3:04:13
 */
public class DownloadTask
    implements Runnable {

    private Handler mHandler;

    private String mUrl;
    private String mName;

    public DownloadTask(Handler handler, String url, String name) {
        this.mHandler = handler;
        this.mUrl = url;
        this.mName = name;
    }

    @Override
    public void run() {
        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(mUrl);
        HttpResponse response = null;
        try {
            response = c.execute(get);
            HttpEntity client = response.getEntity();
            if (client != null) {
                long length = client.getContentLength();
                if (length <= 0)
                    return;
                try {
                    InputStream is = client.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(mName);
                        if (file.exists() == false) {
                            file.createNewFile();
                        }
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        long count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {

                            }
                        }
                        fileOutputStream.flush();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }

                        String[] inf = {"200", mUrl};
                        Message message = Message.obtain(mHandler, ControlMessage.DOWNLOAD_FINISH, inf);
                        message.sendToTarget();
                    }
                }
                catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (ClientProtocolException e1) {
            e1.printStackTrace();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}