package com.haozan.caipiao.activity.userinf;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.userinf.UserScoreHistoryAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.userinf.UserScoreDetailData;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 积分明细页面
 * 
 * @author peter_feng
 * @create-time 2013-3-14 下午5:01:34
 */
public class UserScoreInf
    extends BasicActivity
    implements LoadDataListener, OnClickListener, OnGetDataAgainListener, OnHeaderRefreshListener {
    private static final String RESPONSE = "response_data";
    private static final String NO_DATA = "暂无交易信息";
    private String startDate = "2011-03-18";
    private String endDate = "";
    private int pageNum = 1;

    private ArrayList<UserScoreDetailData> scoreDetailData;
    private AutoLoadListView lvUserScore;
    private UserScoreHistoryAdapter UserScoreHistoryAdapter;
    private RefreshLayout layoutRefresh;
    private EmptyLayout layoutEmpty;

    private TextView tvIntegral;
    private RelativeLayout layoutIntegral;
    private Button btnPointReward;

    private LinearLayout helpScoreTv;

    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_score_history);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        scoreDetailData = new ArrayList<UserScoreDetailData>();
    }

    private void setupViews() {
        helpScoreTv = (LinearLayout) findViewById(R.id.score_help);
        helpScoreTv.setOnClickListener(this);
        tvIntegral = (TextView) this.findViewById(R.id.integral);
        layoutIntegral = (RelativeLayout) this.findViewById(R.id.integral_layout);
        layoutIntegral.setOnClickListener(this);
        btnPointReward = (Button) this.findViewById(R.id.point_reward);
        btnPointReward.setOnClickListener(this);

        lvUserScore = (AutoLoadListView) this.findViewById(R.id.listview);
        lvUserScore.setOnLoadDataListener(this);
        layoutEmpty = (EmptyLayout) this.findViewById(R.id.empty_layout);
        layoutEmpty.setOnGetDataAgainListener(this);
        layoutRefresh = (RefreshLayout) this.findViewById(R.id.pull_refresh_view);
        layoutRefresh.setOnHeaderRefreshListener(this);
    }

    private void init() {
        UserScoreHistoryAdapter = new UserScoreHistoryAdapter(UserScoreInf.this, scoreDetailData);
        lvUserScore.setAdapter(UserScoreHistoryAdapter);

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open user score details");
        String eventName = "v2 open user score details";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        if (scoreDetailData.size() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showFailPage();
        }
        else {
            ViewUtil.showTipsToast(this, failTips);
            lvUserScore.showFootText();
        }
    }

    private void showNoData() {
        if (scoreDetailData.size() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showNoDataPage(NO_DATA);
        }
        else {
            lvUserScore.loadNoMoreData();
        }
    }

    class UserScoreHistoryTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            dismissProgress();
            layoutRefresh.onHeaderRefreshComplete();

            if (result == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, RESPONSE);
                    if (data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        lvUserScore.setVisibility(View.VISIBLE);
                        
                        if (isRefresh) {
                            scoreDetailData.clear();
                            isRefresh = false;
                        }

                        int lastSize = scoreDetailData.size();
                        userScoreHistoryResponseData(data);
                        int preSize = scoreDetailData.size();
                        if (preSize - lastSize < 10) {
                            lvUserScore.loadNoMoreData();
                        }
                        else {
                            lvUserScore.readyToLoad();
                        }

                        UserScoreHistoryAdapter.notifyDataSetChanged();
                        pageNum++;
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserScoreInf.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserScoreInf.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
        }

        private void userScoreHistoryResponseData(String data) {
            try {
                JSONArray hallArray = new JSONArray(data);
                // 原本列表的长度
                int oldLength = scoreDetailData.size();
                for (int i = 0; i < hallArray.length(); i++) {
                    UserScoreDetailData scoreData = new UserScoreDetailData();
                    scoreData.setInf(hallArray.getJSONObject(i).getString("r"));
                    scoreData.setVariation(hallArray.getJSONObject(i).getString("v"));
                    String date = hallArray.getJSONObject(i).getString("d");
                    String month =
                        StringUtil.deleteZeroPrefix(TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm:ss", "MM"));
                    String day = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm:ss", "dd");
                    scoreData.setMonth(month);
                    scoreData.setDay(day);
                    scoreData.setDayOfWeek(TimeUtils.dayOfWeek(TimeUtils.stringConvertToDate(date)));
                    if (i == 0 && oldLength == 0) {
                        scoreData.setShowDate(true);
                    }
                    else if (i == hallArray.length()) {
                        scoreData.setShowLine(true);
                    }
                    else {
                        String lastMonth = scoreDetailData.get(oldLength + i - 1).getMonth();
                        String lastDay = scoreDetailData.get(oldLength + i - 1).getDay();
                        if (lastMonth.equals(month) && lastDay.equals(day)) {
                            scoreData.setShowDate(false);
                            scoreDetailData.get(oldLength + i - 1).setShowLine(false);
                        }
                        else {
                            scoreData.setShowDate(true);
                            scoreDetailData.get(oldLength + i - 1).setShowLine(true);
                        }
                    }
                    scoreDetailData.add(scoreData);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... kind) {
            ConnectService connectNet = new ConnectService(UserScoreInf.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isRefresh) {
                pageNum = 1;
            }

            if (scoreDetailData.size() == 0) {
                layoutEmpty.setVisibility(View.GONE);
                showProgress();
            }
        }

        private HashMap<String, String> iniHashMap() {
            LotteryApp appState = ((LotteryApp) UserScoreInf.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2002111");
            parameter.put("pid", LotteryUtils.getPid(UserScoreInf.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("type", "000");
            parameter.put("start", startDate);
            parameter.put("end", endDate);
            parameter.put("page_no", String.valueOf(pageNum));
            parameter.put("size", "10");
            return parameter;
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_user_score_details";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(UserScoreInf.this)) {
            UserScoreHistoryTask ask = new UserScoreHistoryTask();
            ask.execute();
        }
        else {
            layoutRefresh.onHeaderRefreshComplete();
            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (scoreDetailData.size() == 0) {
            layoutEmpty.showNetErrorPage();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
            lvUserScore.showFootText();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DecimalFormat scoreFormatter = new DecimalFormat("##");
        tvIntegral.setText("现有积分：" + scoreFormatter.format(appState.getScore()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.score_help) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#jifen");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(UserScoreInf.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.integral_layout) {
            ActionUtil.buyIntegral(UserScoreInf.this);
        }
        else if (v.getId() == R.id.point_reward) {
            ActionUtil.toPointReward(UserScoreInf.this);
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;

        loadData();
    }

    @Override
    public void onClickToGetData() {
        loadData();
    }
}