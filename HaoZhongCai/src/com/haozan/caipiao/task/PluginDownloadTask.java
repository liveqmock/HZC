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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.StartUp;
import com.haozan.caipiao.widget.CircleProgress;

public class PluginDownloadTask
    extends AsyncTask<String, Long, Boolean> {

// check whether stop downloading
    private Boolean running = true;
    private static int index;
    private String name = "";
    private int downloadCount;
    private CircleProgress mCircleProgressBar1;
    private ImageView action;
    private LinearLayout ll_right;
    private TextView tv_schedule;

    private Context context;
    private NotificationManager mNotificationManager;
    private Notification notification;
    private PendingIntent contentIntent;

    public PluginDownloadTask(Context context) {
        this.context = context;
    }

    /**
     * 开启通知栏下载线程
     * 
     * @param context
     * @param name 下载的应用名
     * @param mCircleProgressBar1
     * @param action
     * @param ll_right
     * @param tv_schedule
     */
    public PluginDownloadTask(Context context, String name, CircleProgress mCircleProgressBar1,
                              TextView tv_schedule, LinearLayout ll_right) {
        this.context = context;
        this.name = name;
        this.mCircleProgressBar1 = mCircleProgressBar1;
        this.ll_right = ll_right;
        this.tv_schedule = tv_schedule;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification =
            new Notification(android.R.drawable.arrow_down_float, "下载提示", System.currentTimeMillis());
        Intent intent = new Intent(context, StartUp.class);
        index++;
        contentIntent = PendingIntent.getActivity(context, index, intent, 0);
        notification.setLatestEventInfo(context, name + "下载提示", "0%", contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(index, notification);
        if (ll_right != null)
            ll_right.setVisibility(View.VISIBLE);
        if (tv_schedule != null)
            tv_schedule.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(params[0]);
        HttpResponse response = null;
        try {
            response = c.execute(get);
            int now = 0;
            HttpEntity client = response.getEntity();
            if (client != null) {
                long length = client.getContentLength();
                if (length <= 0)
                    return false;
                try {
                    InputStream is = client.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file =
                            new File(Environment.getExternalStorageDirectory(), "zhangzhongcai" + index +
                                ".apk");
                        if (file.exists() == false) {
                            file.createNewFile();
                        }
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        long count = 0;
                        while ((ch = is.read(buf)) != -1 && running) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            publishProgress(count, length);
                            if (length > 0) {

                            }
                            now++;
                        }
                        fileOutputStream.flush();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    }

                }
                catch (IllegalStateException e) {
                    e.printStackTrace();
                    return false;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                return false;
            }
        }
        catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        running = false;
        File file = new File(Environment.getExternalStorageDirectory(), "zhangzhongcai" + index + ".apk");
        if (file.exists())
            file.delete();
        if (mCircleProgressBar1 != null) {
            mCircleProgressBar1.setBackgroundResource(R.drawable.game_list_down);
        }
        if (tv_schedule != null)
            tv_schedule.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mNotificationManager.cancel(index);
            install(context);
        }
        else {
            File file = new File(Environment.getExternalStorageDirectory(), "zhangzhongcai" + index + ".apk");
            if (file.exists())
                file.delete();
        }
        if (ll_right != null)
            ll_right.setVisibility(View.GONE);
        super.onPostExecute(result);
    }

    private void install(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("/sdcard/zhangzhongcai" + index + ".apk")),
                              "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if ((downloadCount == 0) || (values[0] * 100 / values[1] > downloadCount + 2)) {
            downloadCount = (int) (values[0] * 100 / values[1]);
            // changed by vincent
            if (mCircleProgressBar1 != null)
                mCircleProgressBar1.setMainProgress(downloadCount);
            if (tv_schedule != null)
                tv_schedule.setText(downloadCount + "%");

            notification.setLatestEventInfo(context, name + "正在下载", values[0] * 100 / values[1] + "%",
                                            contentIntent);
            mNotificationManager.notify(index, notification);
        }
        super.onProgressUpdate(values);
    }
}