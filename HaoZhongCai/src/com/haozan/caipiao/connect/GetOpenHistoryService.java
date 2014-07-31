package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

public class GetOpenHistoryService {
    private Context context;
    private String kind;
    private int page_no;
    private int size;
    private int no;
    private String q_code;

    // kind:1 means the betting history,2 means the winning prize
    // history;page:the page of the
    // history;size:the amount of the record in each page
    public GetOpenHistoryService(Context context, String kind, String q_code, int no, int page_no, int size) {
        this.context = context;
        this.kind = kind;
        this.q_code = q_code;
        this.no = no;
        this.page_no = page_no;
        this.size = size;
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "get_match_data_19");
        parameter.put("pid", LotteryUtils.getPid(context));
        if (no > 20)
            parameter.put("phone", String.valueOf(phone));
        parameter.put("lottery_id", kind);
        parameter.put("q_code", HttpConnectUtil.encodeParameter(q_code));
        parameter.put("no", "" + no);
        return parameter;
    }

    public String sending() {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(1, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
