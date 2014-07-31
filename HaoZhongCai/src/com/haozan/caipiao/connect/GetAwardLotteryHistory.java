package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetAwardLotteryHistory {

    private Context context;
    private int kind;
    private int page;
    private int size;
    private int recordType;
    private String lotteryId;
    private String start;
    private String end;
    private int data_kind;

    // kind:1 means the betting history,2 means the winning prize history;page:the page of the
    // history;size:the amount of the record in each page
    public GetAwardLotteryHistory(Context context, int kind, int page, int size, String lotteryId,
                                  String start, String end, int data_kind) {
        this.context = context;
        this.kind = kind;
        this.page = page;
        this.size = size;
        this.lotteryId = lotteryId;
        this.start = start;
        this.end = end;
        this.data_kind = data_kind;
    }

    public String sending() {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(3, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1003021");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("record_type", String.valueOf(kind));
        parameter.put("page_no", String.valueOf(page));
        parameter.put("size", String.valueOf(size));
        if (data_kind != 0)
            parameter.put("kind", String.valueOf(data_kind));
        if (lotteryId != null)
            parameter.put("lottery_id", String.valueOf(lotteryId));
        parameter.put("start", String.valueOf(start));
        if (end != null)
            parameter.put("end", String.valueOf(end));
        return parameter;
    }

}
