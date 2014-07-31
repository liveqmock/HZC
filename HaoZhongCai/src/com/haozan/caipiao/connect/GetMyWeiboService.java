package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetMyWeiboService {

    private Context context;
    private int page;
    private int size;

    public GetMyWeiboService(Context context, int page, int size) {
        this.context = context;
        this.page = page;
        this.size = size;
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "list_weibo");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("page_no", String.valueOf(page));
        parameter.put("size", String.valueOf(size));
        parameter.put("type", "5");
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
