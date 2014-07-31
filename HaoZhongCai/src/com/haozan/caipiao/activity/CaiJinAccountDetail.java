package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.UserDetailAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.CaiJinAccountDetailData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class CaiJinAccountDetail
    extends BasicActivity {
    private static final String RESPONSE = "response_data";
    // head view
    private TextView caiJinName;
    private TextView caiJinMoney;
    private TextView caiJinMoneyIndividual;
    private TextView caiJinCycle;
    private TextView caiJinMoneyReturn;
    private TextView caiJinLeft;
    private TextView caiJinTime;
    private TextView titleTimeText;
// private TextView caiJinCycleName;
    // main
    private String detailId = "";
    private View headView;
    private View barView;
    private LayoutInflater mLayoutInflater;
    private ListView caijinListView;
    private UserDetailAdapter userDetailAdapter;
    private ArrayList<CaiJinAccountDetailData> caijinAccountDetailArrayList;
    private TextView userTips;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caijin);
        setUpView();
        init();
    }

    private void setUpView() {
        // head view
        mLayoutInflater = (LayoutInflater) getSystemService(CaiJinAccountDetail.LAYOUT_INFLATER_SERVICE);
        headView = mLayoutInflater.inflate(R.layout.caijin_account_detail_header, null);
        caiJinName = (TextView) headView.findViewById(R.id.caijin_name);
        caiJinMoney = (TextView) headView.findViewById(R.id.caijin_money);
        caiJinMoneyIndividual = (TextView) headView.findViewById(R.id.caijin_money_return_individual);
        caiJinCycle = (TextView) headView.findViewById(R.id.caijin_money_circle);
        caiJinMoneyReturn = (TextView) headView.findViewById(R.id.caijin_money_given);
        caiJinLeft = (TextView) headView.findViewById(R.id.caijin_money_left);
        caiJinTime = (TextView) headView.findViewById(R.id.caijin_money_given_time);
// caiJinCycleName = (TextView) headView.findViewById(R.id.caijin_return_cycle);
        // main
        caijinListView = (ListView) findViewById(R.id.detail_inf_list);
        caijinListView.addHeaderView(headView);
        userTips = (TextView) this.findViewById(R.id.user_tips);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
    }

    private void init() {
        initHeadView();
        caijinAccountDetailArrayList = new ArrayList<CaiJinAccountDetailData>();
        userDetailAdapter = new UserDetailAdapter(CaiJinAccountDetail.this, caijinAccountDetailArrayList);
        caijinListView.setAdapter(userDetailAdapter);
        excuteTask();
    }

    private void initHeadView() {
        ArrayList<String> headViewDataList =
            this.getIntent().getExtras().getStringArrayList("caiJinAccountData");
        detailId = headViewDataList.get(0);
        caiJinName.setText(headViewDataList.get(1));
        caiJinMoney.setText(headViewDataList.get(2) + "元");

        String cycle = headViewDataList.get(4);
        String charge = headViewDataList.get(5);
        String left = headViewDataList.get(6);
        String time = headViewDataList.get(7);
        String discription = headViewDataList.get(8);

        caiJinMoneyReturn.setText(charge + "元");
        if (cycle.equals("W")) {
            if (Integer.valueOf(left) == 0)
                caiJinLeft.setText("0周");
            else
                caiJinLeft.setText(left + "周");
        }
        else if (cycle.equals("M")) {
            if (Integer.valueOf(left) == 0)
                caiJinLeft.setText("0月");
            else
                caiJinLeft.setText(left + "月");
        }
        else if (cycle.equals("O"))
            caiJinLeft.setText("0元");
        else if (cycle.equals("C"))
            caiJinLeft.setText("0元");
        else if (cycle.equals("B"))
            caiJinLeft.setText("0元");
        caiJinTime.setText(time);
        caiJinMoneyIndividual.setText(discription);
    }

    public void searchInfDetailFail(String inf) {
        ViewUtil.showTipsToast(this, inf);
    }

    private void excuteTask() {
        if (HttpConnectUtil.isNetworkAvailable(CaiJinAccountDetail.this)) {
            progress.setVisibility(View.VISIBLE);
            CaiJinAccountDetailTask cjTask = new CaiJinAccountDetailTask();
            cjTask.execute(detailId);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class CaiJinAccountDetailTask
        extends AsyncTask<String, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            String inf;
            if (result == null) {
                userTips.setText("数据查询不成功...");
                userTips.setVisibility(View.VISIBLE);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {

                    String data = analyse.getData(result, RESPONSE);
                    if (data.equals("[]")) {
                        userTips.setText("暂无交易信息...");
                        userTips.setVisibility(View.VISIBLE);
                    }
                    else {
                        try {
                            JSONArray hallArray = new JSONArray(data);
                            for (int i = 0; i < hallArray.length(); i++) {
                                CaiJinAccountDetailData uDT = new CaiJinAccountDetailData();
                                uDT.setPrize(hallArray.getJSONObject(i).getString("prize"));
                                uDT.setStatus(hallArray.getJSONObject(i).getString("status"));
                                uDT.setReturnTime(hallArray.getJSONObject(i).getString("return_time"));
                                uDT.setUsedTime(hallArray.getJSONObject(i).getString("used_time"));
                                uDT.setExpiredTime(hallArray.getJSONObject(i).getString("expire_time"));
                                uDT.setUsedMoney(hallArray.getJSONObject(i).getString("used_prize"));
                                caijinAccountDetailArrayList.add(uDT);
                            }
                            userDetailAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (status.equals("202")) {
                    userTips.setText("暂无交易信息...");
                    userTips.setVisibility(View.VISIBLE);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(CaiJinAccountDetail.this);
                    inf = getResources().getString(R.string.login_timeout);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(CaiJinAccountDetail.this);
                    inf = getResources().getString(R.string.login_again);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    userTips.setText("数据查询不成功...");
                    userTips.setVisibility(View.VISIBLE);
                }
            }
            progress.setVisibility(View.GONE);

        }

        @Override
        protected String doInBackground(String... detailId) {
            ConnectService connectNet = new ConnectService(CaiJinAccountDetail.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, iniHashMap(detailId[0]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private HashMap<String, String> iniHashMap(String detailId) {
        LotteryApp appState = ((LotteryApp) CaiJinAccountDetail.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005121");
        parameter.put("pid", LotteryUtils.getPid(CaiJinAccountDetail.this));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("detail_id", detailId);
        return parameter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]:open handsel detail");
        String eventName = "open handsel detail";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_handsel_detail";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CaiJinAccountDetail.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(CaiJinAccountDetail.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                         R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
