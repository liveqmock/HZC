package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.CaiJinListAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.CaiJinListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class CaiJinList
    extends BasicActivity
    implements OnItemClickListener {
    private static final String NO_DATA = "没有彩金记录\n彩金活动定期推出..";
    private static final String RESPONSE = "response_data";
    // main
    private String detailId = "";
    private ListView caijinListView;
    private CaiJinListAdapter userDetailAdapter;
    private ArrayList<CaiJinListData> caijinAccountListArrayList;
    private TextView message;
    private ProgressBar progress;
    private View footView;
    private View showFailPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caijin_list);
        setUpView();
        init();
    }

    private void setUpView() {
        // main
        caijinListView = (ListView) findViewById(R.id.detail_inf_list);
        caijinListView.setOnItemClickListener(this);
        message = (TextView) this.findViewById(R.id.message);
        message.setText(NO_DATA);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        footView = View.inflate(this, R.layout.cai_jin_list_bottom, null);
        footView.setClickable(false);
        footView.setVisibility(View.GONE);

        showFailPage = this.findViewById(R.id.show_fail_page);
    }

    private void init() {
        caijinListView.addFooterView(footView);
        caijinAccountListArrayList = new ArrayList<CaiJinListData>();
        userDetailAdapter = new CaiJinListAdapter(CaiJinList.this, caijinAccountListArrayList);
        caijinListView.setAdapter(userDetailAdapter);
        excuteTask();
    }

    public void searchInfDetailFail(String inf) {
        ViewUtil.showTipsToast(this, inf);
    }

    private void excuteTask() {
        if (HttpConnectUtil.isNetworkAvailable(CaiJinList.this)) {
            progress.setVisibility(View.VISIBLE);
            CaiJinAccountDetailTask cjTask = new CaiJinAccountDetailTask();
            cjTask.execute(detailId);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            message.setText(inf);
            showFailPage.setVisibility(View.VISIBLE);
        }
    }

    class CaiJinAccountDetailTask
        extends AsyncTask<String, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            String inf;
            if (result == null) {
                message.setText(getString(R.string.search_fail));
                showFailPage.setVisibility(View.VISIBLE);
                caijinListView.setVisibility(View.GONE);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, RESPONSE);
                    if (data.equals("[]")) {
// message.setText("暂未获赠彩金...");
                        showFailPage.setVisibility(View.VISIBLE);
                        caijinListView.setVisibility(View.GONE);
                    }
                    else {
                        try {
                            JSONArray hallArray = new JSONArray(data);
                            for (int i = 0; i < hallArray.length(); i++) {
                                CaiJinListData uDT = new CaiJinListData();
                                uDT.setId(hallArray.getJSONObject(i).getString("id"));
                                uDT.setName(hallArray.getJSONObject(i).getString("name"));
                                uDT.setAmount(hallArray.getJSONObject(i).getString("amount"));
                                uDT.setUnit(hallArray.getJSONObject(i).getString("unit"));
                                uDT.setCycle(hallArray.getJSONObject(i).getString("cycle"));
                                uDT.setCharge(hallArray.getJSONObject(i).getString("charge"));
                                uDT.setLeftNum(hallArray.getJSONObject(i).getString("left_num"));
                                uDT.setTime(hallArray.getJSONObject(i).getString("time"));
                                uDT.setDiscription(hallArray.getJSONObject(i).getString("cycle_desc"));
// uDT.setDiscription("每月");
                                caijinAccountListArrayList.add(uDT);
                            }
                            userDetailAdapter.notifyDataSetChanged();
                            footView.setVisibility(View.VISIBLE);
                        }
                        catch (JSONException e) {
// message.setText("暂未获赠彩金...");
                            showFailPage.setVisibility(View.VISIBLE);
                            caijinListView.setVisibility(View.GONE);
                        }
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(CaiJinList.this);
                    inf = getResources().getString(R.string.login_timeout);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(CaiJinList.this);
                    inf = getResources().getString(R.string.login_again);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else if (status.equals("202")) {
// message.setText("暂未获赠彩金...");
                    showFailPage.setVisibility(View.VISIBLE);
                    caijinListView.setVisibility(View.GONE);
                }
                else {
                    message.setText(getString(R.string.search_fail));
                    showFailPage.setVisibility(View.VISIBLE);
                    caijinListView.setVisibility(View.GONE);
                }
            }
            progress.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... detailId) {
            ConnectService connectNet = new ConnectService(CaiJinList.this);
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
        LotteryApp appState = ((LotteryApp) CaiJinList.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005121");
        parameter.put("pid", LotteryUtils.getPid(CaiJinList.this));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("detail_id", detailId);
        return parameter;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 != caijinAccountListArrayList.size()) {
            ArrayList<String> caiJinAccountData = new ArrayList<String>();
            caiJinAccountData.clear();
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getId());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getName());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getAmount());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getUnit());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getCycle());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getCharge());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getLeftNum());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getTime());
            caiJinAccountData.add(caijinAccountListArrayList.get(arg2).getDiscription());
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("caiJinAccountData", caiJinAccountData);
            intent.setClass(CaiJinList.this, CaiJinAccountDetail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open handsel list");
        String eventName = "open handsel list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_handsel_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CaiJinList.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(CaiJinList.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
