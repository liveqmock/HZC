package com.haozan.caipiao.activity.userinf;

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
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.userinf.TopupHistoryAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.userinf.TopupHistoryData;
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
 * 充值记录
 * 
 * @author peter_feng
 * @create-time 2013-3-15 上午11:53:35
 */
public class TopupHistory
    extends ContainTipsPageBasicActivity
    implements LoadDataListener {
    private static final String TOPUP_WAY_FLAG[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13"};
    private static final String TOPUP_WAY_DEC[] = {"银联语音支付", "手机充值卡", "银行WAP", "信用卡", "支付宝WAP支付", "支付宝安全支付",
            "盛大支付", "电子卷", "中国移动安全支付", "中国移动WAP", "中国移动SMS", "银联快捷支付", "网银支付"};
    private static final String NODATA = "没有充值记录\n充点小钱博个大奖..";

    // the pageNum of the records
    private int pageNum = 1;

    private AutoLoadListView listView;
    private TextView title;

    private TopupHistoryAdapter adapter;
    private ArrayList<TopupHistoryData> topupHistoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup_history);
        initData();
        setupViews();
        init();
    }

    private void init() {
        adapter = new TopupHistoryAdapter(TopupHistory.this, topupHistoryData);
        listView.setAdapter(adapter);
        loadData();
    }

    private void setupViews() {
        listView = (AutoLoadListView) this.findViewById(R.id.history_list);
        listView.setOnLoadDataListener(this);
        title = (TextView) this.findViewById(R.id.history_title);
        title.setText("充值记录");
    }

    private void initData() {
        topupHistoryData = new ArrayList<TopupHistoryData>();
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        if (topupHistoryData.size() == 0) {
            showTipsPage(failInf);
            listView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        listView.removeLoadingFootView();
    }

    private void showNoData() {
        if (topupHistoryData.size() == 0) {
            showTipsPage(NODATA);
            listView.setVisibility(View.GONE);
        }
        listView.removeLoadingFootView();
    }

    class GetRechargeHistoryTask
        extends AsyncTask<Long, Object, String> {

        private HashMap<String, String> initHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1005021");
            parameter.put("pid", LotteryUtils.getPid(TopupHistory.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("record_type", "1");
            parameter.put("page_no", String.valueOf(pageNum));
            parameter.put("size", "10");
            return parameter;
        }

        @Override
        protected String doInBackground(Long... para) {
            ConnectService connectNet = new ConnectService(TopupHistory.this);
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
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        int lastSize = topupHistoryData.size();
                        topupHistoryResponseData(response_data);
                        int preSize = topupHistoryData.size();
                        if (preSize - lastSize < 10) {
                            listView.removeLoadingFootView();
                        }
                        else {
                            listView.addLoadingFootView();
                            listView.readyToLoad();
                        }
                        adapter.notifyDataSetChanged();
                        pageNum++;
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(TopupHistory.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(TopupHistory.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
            else {
                showFail();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    public void topupHistoryResponseData(String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            // 原本列表的长度
            int oldLength = topupHistoryData.size();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                TopupHistoryData data = new TopupHistoryData();
                String date = jo.getString("time");
                String year = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "yy");
                String month = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "MM");
                String day =
                    StringUtil.deleteZeroPrefix(TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "dd"));
                String time = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm", "HH:mm");
                data.setYear(year);
                data.setMonth(month);
                data.setDay(day);
                data.setTime(time);
                data.setMoney(jo.getString("money"));
                data.setInf(jo.getString("desc"));
                String topupWay = jo.getString("type");
                String topupWayDec = null;
                // 根据服务器返回数据解析成相应充值类型描述
                if (topupWay != null) {
                    for (int j = 0; j < TOPUP_WAY_FLAG.length; j++) {
                        if (topupWay.equals(TOPUP_WAY_FLAG[j])) {
                            topupWayDec = TOPUP_WAY_DEC[j];
                        }
                    }
                }
                if (topupWayDec == null) {
                    topupWayDec = "未知方式";
                }
                data.setWay(topupWayDec);
                if (i == 0 && oldLength == 0) {
                    data.setShowDate(true);
                }
                else if (i == hallArray.length()) {
                    data.setShowLine(true);
                }
                else {
                    String lastYear = topupHistoryData.get(oldLength + i - 1).getYear();
                    String lastMonth = topupHistoryData.get(oldLength + i - 1).getMonth();
                    if (lastYear.equals(year) && lastMonth.equals(month)) {
                        data.setShowDate(false);
                        topupHistoryData.get(oldLength + i - 1).setShowLine(false);
                    }
                    else {
                        data.setShowDate(true);
                        topupHistoryData.get(oldLength + i - 1).setShowLine(true);
                    }
                }
                topupHistoryData.add(data);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open top up history");
        String eventName = "v2 open top up history";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_topup_history";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TopupHistory.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(TopupHistory.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                  R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(TopupHistory.this)) {
            GetRechargeHistoryTask rechargeHistoryTask = new GetRechargeHistoryTask();
            rechargeHistoryTask.execute();
        }
        else {
            showFail(noNetTips);
        }
    }
}
