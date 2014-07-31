package com.haozan.caipiao.activity.lotterychart;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.view.lotterychart.ZoushituFrame;

public class SSQLotterychartActivity
    extends BasicActivity {

    private static final long SIZE = 20;

    String[] jsonNum = {"07,13,17,19,22,26|13", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15",
            "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15",
            "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15",
            "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15",
            "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15", "03,05,17,18,26,27|15",
            "03,05,17,18,26,27|15"};
    private String[] termArray = {"2013050", "2013050", "2013050", "2013050", "2013050", "2013050",
            "2013050", "2013050", "2013050", "2013050", "2013050", "2013050", "2013050", "2013050",
            "2013050", "2013050", "2013050", "2013050", "2013050", "2013050"};

    private String[][] zoushiTuArray = new String[20][49];

    private ZoushituFrame tableView;
    private String lastTerm = null;
    private String kind = "ssq";
    private String lotteryName;
    private GetLotteryHistoryTask getHistoryTask;

    private int row = 21;
    private int col = 49;
    private int viewHeight;
    private int viewWidth;
    private int gridHeight;
    private int gridWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssq_zoushitu);
        tableView = (ZoushituFrame) findViewById(R.id.table_view);
        initChartView();

        ViewTreeObserver viewTreeObserver = tableView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                viewHeight = tableView.getHeight();
                viewWidth = tableView.getWidth();

                gridHeight = viewHeight / row;
                gridWidth = gridHeight;

                excuteChartTask();
            }
        });
    }

    private void excuteChartTask() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            getHistoryTask = new GetLotteryHistoryTask();
            getHistoryTask.execute();
        }
    }

    private void initChartView() {
// DisplayMetrics metric = new DisplayMetrics();
// getWindowManager().getDefaultDisplay().getMetrics(metric);
// int width = metric.widthPixels; // 屏幕宽度（像素）
// int height = metric.heightPixels;

//        tableView.setTableData(zoushiTuArray);
  

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 49; j++)
                zoushiTuArray[i][j] = "";
        }

        for (int n = 0; n < 20; n++) {
            String a = jsonNum[n].replaceAll("\\|", ",");
            String[] aArray = a.split("\\,");
            for (int k = 0; k < aArray.length - 1; k++)
                zoushiTuArray[n][Integer.parseInt(aArray[k]) - 1] = aArray[k];
            zoushiTuArray[n][33 + Integer.parseInt(aArray[aArray.length - 1]) - 1] =
                aArray[aArray.length - 1];
        }

        tableView.setTableData(zoushiTuArray);
        tableView.setTableRowAndCol(row, col);
        tableView.setGridWidthAndGridHeight(gridWidth, gridHeight);
        tableView.setTableViewProperty();
    }

    class GetLotteryHistoryTask
        extends AsyncTask<Void, Object, String> {
        int tag;

        private HashMap<String, String> iniHisHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1001060");
            parameter.put("pid", LotteryUtils.getPid(SSQLotterychartActivity.this));
            parameter.put("lottery_id", kind);
// if (!beRefresh && lastTerm != null)
            parameter.put("start_term", lastTerm);
// if (beRefresh)
            parameter.put("size", "" + SIZE);
// else
// parameter.put("size", "" + (SIZE + 1));
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(SSQLotterychartActivity.this);
            String json = null;
            try {
// if (flag == 0) {// 开奖历史
                json = connectNet.getJsonGet(2, false, iniHisHashMap());
// }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
// if (flag == 0) {
// actualListView[0].removeFooterView(footView);
// }
// progressBars[flag].setVisibility(View.GONE);
// ifGetMoreData[flag] = true;
// if (ifClearArray[flag] == true) {
// pullToRefresh[flag].onHeaderRefreshComplete();
// }
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
// clearArray();
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
// showFail();
                    }
                    else {
// int lastSize = lotteryHistoryData.size();
// if (flag == 0) {
                        lotteryHistoryResponse(response_data);
// }
// else if (flag == 1) {
// setNewAward(response_data);
// }
// else if (flag == 2) {
// setAwardRanking(response_data);
// }
// int preSize = lotteryHistoryData.size();
// if (preSize - lastSize < SIZE) {
// ifGetMoreData[flag] = false;
// }
// dataChange(flag);
                    }
                }
                else {
// showFail();
                }
            }
            else {
// showFail();
            }
// if (flag == 0) {
// beRefresh = false;
// }
        }

// public void clearArray() {
// if (ifClearArray[flag] == true) {
// if (flag == 0) {
// lotteryHistoryData.clear();
// }
// else if (flag == 1) {
// lotteryNewlyAwardRankList.clear();
// }
// else if (flag == 2) {
// lotteryAwardRankList.clear();
// }
// }
// ifClearArray[flag] = false;
// }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// tag = viewPager.getCurrentItem();
// flag = tag;
// progressBars[flag].setVisibility(View.VISIBLE);
// failPages[flag].setVisibility(View.GONE);
        }
    }

    public void lotteryHistoryResponse(String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            int i = 0;
            for (; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);

                String lotteryKind = kind;
                String term = jo.getString("term");
                String date = jo.getString("date");
                String balls = jo.getString("codes");
                if (kind.equals("dfljy"))
                    balls = StringUtil.getDFLJY(balls);

                jsonNum[i] = getLotteryOpenNum(balls);
                termArray[i] = term;
            }
            initChartView();
            tableView.invalidate();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getLotteryOpenNum(String lotteryOpenNum) {
        return lotteryOpenNum.split("\\:")[0];
    }
}
