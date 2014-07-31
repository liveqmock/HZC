package com.haozan.caipiao.connect.unite;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetUniteHallService {

    private Context context;
    private int kind;// 0:按进度（默认）；1：按总额；2:按战绩
    private int orderby;//0：升序；1：降序
    private int page;
    private int size;
// private int recordType;
    private String lotteryId;

// private String start;
// private String end;

    public GetUniteHallService(Context context, int kind, int orderby, int page, int size, String lotteryId) {
        this.context = context;
        this.kind = kind;//分类类型
        this.orderby = orderby;//升降序
        this.page = page;
        this.size = size;
// this.recordType = recordType;
        this.lotteryId = lotteryId;
// this.start = start;
// this.end = end;
// this.recordType=recordType;
    }

    public String sending()
        throws Exception {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            // TODO 参数修改
            json = connectNet.getJsonGet(2, false, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2003090");//
        parameter.put("pid", LotteryUtils.getPid(context));//
//        parameter.put("phone", appState.getUsername());//此参数不添加，用户在未登录情况下也可以看到当前合买信息
        parameter.put("type", String.valueOf(kind));// 分类类型
        parameter.put("orderby", String.valueOf(orderby));//升降序
        parameter.put("page_no", String.valueOf(page));//页数
        parameter.put("size", String.valueOf(size));//每页数目
        if (lotteryId != null)
            parameter.put("lottery_id", String.valueOf(lotteryId));//彩种代码
        return parameter;
    }
}
