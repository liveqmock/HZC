package com.haozan.caipiao.activity.userinf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.userinf.WithdrawHistoryAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.userinf.WithdrawHistoryData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 提现记录
 * 
 * @author peter_feng
 * @create-time 2013-3-16 下午5:38:45
 */
public class WithdrawHistory
    extends ContainTipsPageBasicActivity
    implements LoadDataListener {
    private static final String NODATA = "没有提现记录\n中个大奖提提现..";
    // the pageNum of the records
    private int pageNum = 1;

    private AutoLoadListView listView;
    private TextView title;

    private WithdrawHistoryAdapter adapter;
    private ArrayList<WithdrawHistoryData> historyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup_history);
        initData();
        setupViews();
        init();

    }

    private void init() {
        adapter = new WithdrawHistoryAdapter(WithdrawHistory.this, historyData);
        listView.setAdapter(adapter);
        loadData();
    }

    private void setupViews() {
        listView = (AutoLoadListView) this.findViewById(R.id.history_list);
        title = (TextView) this.findViewById(R.id.history_title);
        title.setText("提现记录");
        listView.setOnLoadDataListener(this);
    }

    private void initData() {
        historyData = new ArrayList<WithdrawHistoryData>();
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        if (historyData.size() == 0) {
            showTipsPage(failInf);
            listView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        listView.removeLoadingFootView();
    }

    private void showNoData() {
        if (historyData.size() == 0) {
            showTipsPage(NODATA);
            listView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
        listView.removeLoadingFootView();
    }

    class GetRechargeHistoryTask
        extends AsyncTask<Long, Object, String> {

        private HashMap<String, String> initHashMap() {
            LotteryApp appState = ((LotteryApp) WithdrawHistory.this.getApplicationContext());
            String phone = appState.getUsername();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String endTime = df.format(System.currentTimeMillis());
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2005111");
            parameter.put("pid", LotteryUtils.getPid(WithdrawHistory.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("start", "2011-04-01");
            parameter.put("end", endTime);
            parameter.put("page_no", String.valueOf(pageNum));
            parameter.put("size", "10");
            return parameter;
        }

        @Override
        protected String doInBackground(Long... para) {
            ConnectService connectNet = new ConnectService(WithdrawHistory.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dismissProgress();
            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (data.equals("[]") == false) {
                        int lastSize = historyData.size();
                        withdrawResponseData(data);
                        int preSize = historyData.size();
                        if (preSize - lastSize < 10) {
                            listView.removeLoadingFootView();
                        }
                        else {
                            listView.addLoadingFootView();
                            listView.readyToLoad();
                        }
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(WithdrawHistory.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WithdrawHistory.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    public void withdrawResponseData(String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            // 原本列表的长度
            int oldLength = historyData.size();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                WithdrawHistoryData withdrawHistoryData = new WithdrawHistoryData();
                String date = jo.getString("time");
                String year = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "yy");
                String month = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "MM");
                String day =
                    StringUtil.deleteZeroPrefix(TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "dd"));
                String time = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "HH:mm");
                withdrawHistoryData.setYear(year);
                withdrawHistoryData.setMonth(month);
                withdrawHistoryData.setDay(day);
                withdrawHistoryData.setTime(time);
                withdrawHistoryData.setMoney(jo.getString("money"));
                withdrawHistoryData.setBank(jo.getString("bank"));
                withdrawHistoryData.setFee(jo.getString("fee"));
                withdrawHistoryData.setStatus(jo.getString("status"));
                if (i == 0 && oldLength == 0) {
                    withdrawHistoryData.setShowDate(true);
                }
                else if (i == hallArray.length()) {
                    withdrawHistoryData.setShowLine(true);
                }
                else {
                    String lastYear = historyData.get(oldLength + i - 1).getYear();
                    String lastMonth = historyData.get(oldLength + i - 1).getMonth();
                    if (lastYear.equals(year) && lastMonth.equals(month)) {
                        withdrawHistoryData.setShowDate(false);
                        historyData.get(oldLength + i - 1).setShowLine(false);
                    }
                    else {
                        withdrawHistoryData.setShowDate(true);
                        historyData.get(oldLength + i - 1).setShowLine(true);
                    }
                }
                historyData.add(withdrawHistoryData);
            }
            adapter.notifyDataSetChanged();
            pageNum++;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open withdraw history");
        String eventName = "v2 open withdraw history";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_withdraw_history";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WithdrawHistory.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WithdrawHistory.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(WithdrawHistory.this)) {
            GetRechargeHistoryTask rechargeHistoryTask = new GetRechargeHistoryTask();
            rechargeHistoryTask.execute();
        }
        else {
            showFail(noNetTips);
        }
    }
}
