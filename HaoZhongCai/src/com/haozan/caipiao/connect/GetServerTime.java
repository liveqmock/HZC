package com.haozan.caipiao.connect;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;

public class GetServerTime {

    private Context context;

    public GetServerTime(Context context) {
        this.context = context;
    }

    // get the time of the server
    public String getTime() {
        String time = getTimeJson();
        if (time == null) {
            time = getTimeJson();
        }
        return time;
    }

    private String getTimeJson() {
        ConnectService connectNet = new ConnectService(context);
        String json = connectNet.getJsonTime();
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            try {
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    String time = ja.getData(json, "datetime");
                    return time;
                }
                else
                    return null;
            }
            catch (Exception e) {
// FlurryAgent.onError("get server time Error", e.getMessage(), context.getClass().getName());
                e.printStackTrace();
                return null;
            }
        }
        else
            return null;
    }

    // format the time from server
    public String formatTime(String time) {
        if (time == null)
            return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String forTime = null;
        try {
            forTime = fDateFormat.format(format.parse(time));
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return forTime;
    }

    public String getFormatTime() {
        return formatTime(getTime());
    }
}
