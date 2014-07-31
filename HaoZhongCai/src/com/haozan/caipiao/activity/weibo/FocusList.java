package com.haozan.caipiao.activity.weibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.weibo.FocusAdapter;
import com.haozan.caipiao.connect.GetFocusService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FocusListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 关注的财园信息
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:45:46
 */
public class FocusList
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询关注信息失败或信息已删除";

    FocusAdapter adapter;
    private ArrayList<FocusListData> focusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        super.setupViews();
        titleTextView.setText("关注");
    }

    public void initSub() {
        focusList = new ArrayList<FocusListData>();
        adapter = new FocusAdapter(FocusList.this, focusList);
        actualListView.setAdapter(adapter);
        loadData();

    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(FocusList.this)) {
            FocusTask getFansTask = new FocusTask();
            getFansTask.execute();

        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private class FocusTask
        extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            GetFocusService getWeibo = new GetFocusService(FocusList.this, page, SIZE);
            String json = getWeibo.sending();
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            actualListView.removeFooterView(footView);
            dismissProgress();
            ifGetMoreData = true;
            pulllistView.onHeaderRefreshComplete();
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (ifClearArray == true) {
                        focusList.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = focusList.size();
                        getFocusArray(focusList, data);
                        int preSize = focusList.size();
                        if (preSize - lastSize < 10) {
                            ifGetMoreData = false;
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
                    OperateInfUtils.clearSessionId(FocusList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(FocusList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(failTips);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
        }
    }

    public void getFocusArray(ArrayList<FocusListData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    FocusListData fansdata = new FocusListData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    fansdata.setName(jo.getString("nickname"));
                    fansdata.setAvatar(jo.getString("email"));
                    fansdata.setGender(jo.getString("gender"));
                    fansdata.setId(jo.getString("user_id"));
                    fansdata.setSignature(jo.getString("signature"));
                    myweiboArray.add(fansdata);
                    fansdata.setBitmap(null);
                }
                adapter.notifyDataSetChanged();
                page++;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNoData() {
        if (focusList.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    public void showFail(String failInf) {
        if (focusList.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal focus list");
        String eventName = "v2 open garden personal focus list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_focus_list";
        MobclickAgent.onEvent(this, eventName, "personal");
        besttoneEventCommint(eventName);
    }
}
