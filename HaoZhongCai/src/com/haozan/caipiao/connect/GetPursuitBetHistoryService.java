package com.haozan.caipiao.connect;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetPursuitBetHistoryService {

    private Context context;
    private int kind;
    private int page;
    private int size;
    private int type;
    private String lotteryId;
    private String start;
    private String end;

    // kind:1 means the betting history,2 means the winning prize
    // history;page:the page of the
    // history;size:the amount of the record in each page
    public GetPursuitBetHistoryService(Context context, int page, int size, int type, String lotteryId,
                                       String start, String end) {
        this.context = context;
        this.page = page;
        this.size = size;
        this.type = type;
        this.lotteryId = lotteryId;
        this.start = start;
        this.end = end;
    }

    public String sending()
        throws Exception {
        if (type == 0) {
            ConnectService connectNet = new ConnectService(context);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, true, initHashMap1());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        else {
            ConnectService connectNet = new ConnectService(context);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, initHashMap2());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private HashMap<String, String> initHashMap1()
        throws Exception {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "user_pursuit_record");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("phone", appState.getUsername());
        parameter.put("page_no", "" + page);
        parameter.put("size", "" + size);
        if (lotteryId != null)
            parameter.put("lottery_id", String.valueOf(lotteryId));
        parameter.put("start", String.valueOf(start));
        if (end != null)
            parameter.put("end", String.valueOf(end));
        return parameter;
    }

    private HashMap<String, String> initHashMap2()
        throws Exception {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        HashMap<String, String> parameter = new HashMap<String, String>();
        if (type == 1) {
            parameter.put("service", "1003021");
            parameter.put("pid", LotteryUtils.getPid(context));
            parameter.put("phone", appState.getUsername());
            parameter.put("record_type", "2");
            parameter.put("kind", "2");
        }
        parameter.put("page_no", String.valueOf(page));
        parameter.put("size", String.valueOf(size));
        if (lotteryId != null)
            parameter.put("lottery_id", String.valueOf(lotteryId));
        parameter.put("start", String.valueOf(start));
        if (end != null)
            parameter.put("end", String.valueOf(end));
        return parameter;
    }
}
