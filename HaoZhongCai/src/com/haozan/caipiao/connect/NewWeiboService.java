package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class NewWeiboService {

    private Context context;
    private String content;

    public NewWeiboService(Context context, String content) {
        this.context = context;
        this.content = content;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2004030");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", appState.getUsername());
        parameter.put("content", content);
        return parameter;
    }

    public String sending() {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonPost(4, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
