package com.haozan.caipiao.request;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.HallControl;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.LotteryInf;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.UEDataAnalyse;

/**
 * 大厅彩种信息获取
 * 
 * @author peter_wang
 * @create-time 2013-10-19 上午10:35:36
 */
public class LotteryInfRequest
    extends AsyncConnectionInf {
    public static final int LOTTERY_NORMAL_KIND = 0;
    public static final int LOTTERY_QUICK_KIND = 1;

    private static final String LOTTERY_NORMAL_SERVICE = "2001010";
    private static final String LOTTERY_QUICK_SERVICE = "2001070";

    private Context mContext;
    private Handler mHandler;

    // 因为移植问题后面考虑变成long提高性能
    private String mSystemTime;
    private long mSystemTimeMillis;
    private long mGapMillis;

    private int mKind = -1;// 普通彩种还是高频彩
    private String mLottery;// 彩种

    private String mService;

    private UEDataAnalyse mUploadRequestTime;

    /**
     * @param context
     * @param handler
     * @param lottery 彩种名称
     */
    public LotteryInfRequest(Context context, Handler handler, String lottery) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
        this.mLottery = lottery;

        if (LotteryUtils.isFrequentLottery(lottery)) {
            mKind = LOTTERY_QUICK_KIND;
        }
        else {
            mKind = LOTTERY_NORMAL_KIND;
        }

        mUploadRequestTime = new UEDataAnalyse(context);
    }

    /**
     * @param context
     * @param handler
     * @param kind 普通彩种还是高频彩，LotteryInfRequest.LOTTERY_NORMAL_KIND和LotteryInfRequest.LOTTERY_QUICK_KIND
     */
    public LotteryInfRequest(Context context, Handler handler, int kind) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
        this.mKind = kind;

        mUploadRequestTime = new UEDataAnalyse(context);

    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        mService = LOTTERY_NORMAL_SERVICE;
        if (mKind == LOTTERY_NORMAL_KIND) {
            mService = LOTTERY_NORMAL_SERVICE;
        }
        else if (mKind == LOTTERY_QUICK_KIND) {
            mService = LOTTERY_QUICK_SERVICE;
        }
        parameter.put("service", mService);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        if (TextUtils.isEmpty(mLottery)) {
            parameter.put("lottery_id", "all");
        }
        else {
            parameter.put("lottery_id", mLottery);
        }
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getGateways(false, initHashMap());
        createUrlGet(connection.getmUrl());

        mUploadRequestTime.onConnectStart();
    }

    private void lotteryInfResponseData(String json) {
        try {
            JSONArray hallArray = new JSONArray(json);
            int length = hallArray.length();
            String id;
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                LotteryInf temp = new LotteryInf();
                id = jo.getString("id");
                for (int j = 0; j < LotteryUtils.LOTTERY_ID.length; j++) {
                    if (id.equals(LotteryUtils.LOTTERY_ID[j])) {
                        temp.setIcon(LotteryUtils.HALL_ITEM_ICON[j]);
                        temp.setName(LotteryUtils.LOTTERY_NAMES[j]);
                    }
                }
                temp.setId(id);
                temp.setLastTerm(jo.getString("lastterm"));
                temp.setLastOpenTime(jo.getString("date"));
                temp.setLastNum(jo.getString("codes"));
                String jackpot;
                if (jo.isNull("jackpot")) {
                    jackpot = "";
                }
                else {
                    jackpot = jo.getString("jackpot");
                }
                temp.setExtraInf(jackpot);

                if (!jo.isNull("newterm")) {
                    temp.setNewTerm(jo.getString("newterm"));
                    temp.setAwardTime(jo.getString("awardtime"));

                    String endtime = jo.getString("endtime") + ":00";
                    temp.setEndTime(TimeUtils.getDateMillisecond(endtime));

                    temp.setGapTimeMillis(mGapMillis);

                    long lastTime = temp.getEndTime() - System.currentTimeMillis() - temp.getGapTimeMillis();
                    if (lastTime < 0) {
                        lastTime = 0;
                    }

                    temp.setLastTimeMillis(lastTime);
                }
                else {
                    temp.setNewTerm(jo.getString("lastterm"));
                    temp.setEndTime(0);
                    if (id.equals(LotteryUtils.JCLQ) == false && id.equals(LotteryUtils.JCZQ) == false) {
                        temp.setLastTimeMillis(0);
                    }
                }

                HallControl.sLotteryInf.put(id, temp);
            }
        }
        catch (JSONException e) {
        }
    }

    private void singleLotteryInfResponseData(String json) {
        LotteryInf temp = new LotteryInf();

        JsonAnalyse analyase = new JsonAnalyse();
        String id = analyase.getData(json, "id");
        for (int j = 0; j < LotteryUtils.LOTTERY_ID.length; j++) {
            if (id.equals(LotteryUtils.LOTTERY_ID[j])) {
                temp.setIcon(LotteryUtils.HALL_ITEM_ICON[j]);
                temp.setName(LotteryUtils.LOTTERY_NAMES[j]);
            }
        }
        temp.setId(id);
        temp.setLastTerm(analyase.getData(json, "lastterm"));
        temp.setLastOpenTime(analyase.getData(json, "date"));
        temp.setLastNum(analyase.getData(json, "codes"));
        String jackpot = analyase.getData(json, "jackpot");
        temp.setExtraInf(jackpot);

        temp.setNewTerm(analyase.getData(json, "newterm"));

        temp.setGapTimeMillis(mGapMillis);

        String endtime = analyase.getData(json, "endtime") + ":00";
        temp.setEndTime(TimeUtils.getDateMillisecond(endtime));

        long lastTime = temp.getEndTime() - System.currentTimeMillis() - temp.getGapTimeMillis();
        if (lastTime < 0) {
            lastTime = 0;
        }

        temp.setLastTimeMillis(lastTime);

        HallControl.sLotteryInf.put(id, temp);
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectSuccess(mService);

        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(rspContent, "response_data");

        if (TextUtils.isEmpty(mLottery)) {
            mSystemTime = analyse.getData(rspContent, "datetime");
            mSystemTimeMillis = TimeUtils.getDateMillisecond(mSystemTime);
            mGapMillis = mSystemTimeMillis - System.currentTimeMillis();
            lotteryInfResponseData(response);
        }
        else {
            mSystemTime = analyse.getData(rspContent, "datetime");
            mSystemTimeMillis = TimeUtils.getDateMillisecond(mSystemTime);
            mGapMillis = mSystemTimeMillis - System.currentTimeMillis();
            singleLotteryInfResponseData(response);
        }
        Message message = Message.obtain(mHandler, ControlMessage.REVEICE_LOTTERY_INF, mKind, 200, mLottery);// 彩种类型，是否成功（200代表成功，405代表失败），彩种名称
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(mService, statusCode + ":" + rspContent);

        Message message = Message.obtain(mHandler, ControlMessage.REVEICE_LOTTERY_INF, mKind, 405, mLottery);
        message.sendToTarget();
    }
}