package com.haozan.caipiao.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.requestInf.NoticeRequestInf;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.TimeUtils;

/**
 * 大厅公告请求和解析数据
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:43:11
 */
public class HallNoticeRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    public HallNoticeRequest(Context context, Handler handler) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void preRun() {
        NoticeRequestInf requestInf = new NoticeRequestInf(mContext);
        createUrlGet(requestInf.getFirstNoticeUrl());
    }

    private void noticeListResponseData(JSONArray jsonArray) {
        try {
            JSONObject jo = jsonArray.getJSONObject(0);
            int urgent = jo.getInt("urgent");
            if (urgent == 1) {
                String date = jo.getString("issue_date");
                if (isNewNotice(date)) {
                    String title = jo.getString("subject");
                    String content = jo.getString("digest");
                    String url = jo.getString("content");

                    String notice =
                        "<font color='#000000'>" + "<b>" + title + "</b>" + "</font>" + "<br/>" +
                            "<font color='#4F4F4F' size='4'>" + content + "</font>";
                    // 公告内容，跳转到网页显示的地址和公告时间
                    String[] inf = {notice, url, date};
                    Message msg = Message.obtain(mHandler, ControlMessage.HALL_NOTICE, 0, 0, inf);
                    msg.sendToTarget();

                    Editor databaseData = ActionUtil.getEditor(mContext);
                    databaseData.putLong("issue_date", TimeUtils.getDateMillisecond(date));
                    databaseData.commit();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否有新公告
     * 
     * @param time 最新公告发布时间
     * @return
     */
    private boolean isNewNotice(String date) {
        long time2 = ActionUtil.getSharedPreferences(mContext).getLong("issue_date", 0);
        long noticeTime = TimeUtils.getDateMillisecond(date);
        long millis = noticeTime - time2 - 60 * 1000;
        if (millis > 0)
            return true;
        else
            return false;
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String responseData = analyse.getData(rspContent, "response_data");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(responseData);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (responseData.equals("[]") == false) {
            noticeListResponseData(jsonArray);
        }
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {

    }
}