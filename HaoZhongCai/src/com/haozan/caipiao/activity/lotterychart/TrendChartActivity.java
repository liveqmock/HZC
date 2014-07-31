package com.haozan.caipiao.activity.lotterychart;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.HorizontalScrollView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.activity.bet.ssq.SSQActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.Lottery;
import com.haozan.caipiao.types.TrendChartTopBarData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.TrendChartViewGroup;
import com.haozan.caipiao.widget.TrendChartViewGroup.TrendChartSelecteBallsListener;

public class TrendChartActivity
    extends ContainTipsPageBasicActivity
    implements TrendChartSelecteBallsListener {
    private TrendChartViewGroup trendChartView;
    private HorizontalScrollView scrollview;

    private GetLotteryHistoryTask getHistoryTask;

    private String lotteryId;
    private String lotteryName;

    private String lastTerm = null;
    private Lottery[] lotteryInf;

    private boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_chart);
        setupViews();
        init();
    }

    private void setupViews() {
        trendChartView = (TrendChartViewGroup) this.findViewById(R.id.trend_chart);
        trendChartView.setSelecteBallListener(this);
        scrollview = (HorizontalScrollView) this.findViewById(R.id.horizontal_scrollview);
        scrollview.setBackgroundColor(Color.TRANSPARENT);
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        lotteryId = bundle.getString("lottery_id");

        String[] firstGroup = new String[SSQActivity.SSQ_HONGQIU_LENGTH];
        for (int i = 0; i < SSQActivity.SSQ_HONGQIU_LENGTH; i++) {
            firstGroup[i] = "" + (i + 1);
        }
        String[] secondGroup = new String[SSQActivity.SSQ_LANQIU_LENGTH];
        for (int i = 0; i < SSQActivity.SSQ_LANQIU_LENGTH; i++) {
            secondGroup[i] = "" + (i + 1);
        }

        TrendChartTopBarData[] trendChartData = new TrendChartTopBarData[2];
        trendChartData[0] = new TrendChartTopBarData();
        trendChartData[0].setColor(getResources().getColor(R.color.red_ball));
        trendChartData[0].setTrendTopText(firstGroup);
        trendChartData[1] = new TrendChartTopBarData();
        trendChartData[1].setColor(getResources().getColor(R.color.blue_ball));
        trendChartData[1].setTrendTopText(secondGroup);
        trendChartView.setTrendChartTopBarData(trendChartData);

        getLotteryHistory();
    }

    private void getLotteryHistory() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            getHistoryTask = new GetLotteryHistoryTask();
            getHistoryTask.execute();
        }
        else {
            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        ViewUtil.showTipsToast(this, noNetTips);
    }

    private void showNoData() {
        ViewUtil.showTipsToast(this, "开奖历史无数据");
    }

    private void showFail() {
        ViewUtil.showTipsToast(this, failTips);
    }

    class GetLotteryHistoryTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> iniHisHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1001060");
            parameter.put("pid", LotteryUtils.getPid(TrendChartActivity.this));
            parameter.put("lottery_id", lotteryId);
            if (!isRefresh && lastTerm != null)
                parameter.put("start_term", lastTerm);
            if (isRefresh)
                parameter.put("size", "" + 20);
            else
                parameter.put("size", "" + 21);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(TrendChartActivity.this);
            String json = connectNet.getJsonGet(2, false, iniHisHashMap());
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            completeLoadingProgress();

            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        lotteryHistoryResponse(response_data);
// if (preSize - lastSize < 10) {
// lvAutoLoad[flag].loadNoMoreData();
// }
// else {
// lvAutoLoad[flag].readyToLoad();
// }
                    }
                }
                else {
                    showFail();
                }
            }
            else {
                showFail();
            }
            if (isRefresh) {
                isRefresh = false;
            }
        }

        private void completeLoadingProgress() {
            dismissProgress();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    public void lotteryHistoryResponse(String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            int i = 0;
            if (!isRefresh) {
                i = 1;
            }
            lotteryInf = new Lottery[length - i];
            int j = 0;
            for (; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                Lottery temp = new Lottery();
                temp.setId(lotteryId);
                if (i == length - 1) {
                    lastTerm = jo.getString("term");
                }
                temp.setTerm(jo.getString("term"));
                temp.setDate(jo.getString("date"));
                String balls = jo.getString("codes");
                if (lotteryId.equals("dfljy")) {
                    balls = StringUtil.getDFLJY(balls);
                }
                temp.setBalls(balls);
                lotteryInf[j] = temp;
                j++;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        trendChartView.setLotteryInf(lotteryInf);
        trendChartView.invalidate();
    }

    @Override
    public void selectedBalls(String ballsCode) {
        if (ballsCode != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("bet_code", ballsCode);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}
