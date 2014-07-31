package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetWeiboService {

    private Context context;
    private int page;
    private int size;

    public GetWeiboService(Context context, int page, int size) {
        this.context = context;
        this.page = page;
        this.size = size;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2004040");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", appState.getUsername());
        parameter.put("type", "4");
        parameter.put("page_no", String.valueOf(page));
        parameter.put("page_size", String.valueOf(size));
        return parameter;
    }

    public String sending() {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(4, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
