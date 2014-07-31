package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class HistoryDetailsTask {

    private Context context;

    public HistoryDetailsTask(Context context) {
        this.context = context;
    }

    private HashMap<String, String> initHashMap(String kind, String term) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "lottery_open_detail");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("lottery_id", kind);
        parameter.put("term", term);
        return parameter;
    }

    public String getting(String kind, String term) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(1, false, initHashMap(kind, term));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
