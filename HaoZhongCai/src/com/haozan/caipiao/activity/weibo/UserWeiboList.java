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
import com.haozan.caipiao.adapter.MyWeiboAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyWeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

public class UserWeiboList
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询信息失败或信息已删除";

    MyWeiboAdapter adapter;
    private ArrayList<MyWeiboData> subArray;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        // 接收传过来的id
        userId = this.getIntent().getExtras().getInt("userId");
    }

    public void initSub() {
        subArray = new ArrayList<MyWeiboData>();
        adapter = new MyWeiboAdapter(UserWeiboList.this, subArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(UserWeiboList.this)) {
            UserWeiboTask task = new UserWeiboTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class UserWeiboTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004040");
            parameter.put("pid", LotteryUtils.getPid(UserWeiboList.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "5");
            parameter.put("user_id", "" + userId);
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + SIZE);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserWeiboList.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
                        subArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = subArray.size();
                        getUserWeiboArray(subArray, data);
                        int preSize = subArray.size();
                        if (preSize - lastSize < 10) {
                            ifGetMoreData = false;
                        }
                        adapter.notifyDataSetChanged();
                        page++;
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWeiboList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWeiboList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }

                else {
                    if (subArray.size() == 0) {
                        adapter.notifyDataSetChanged();
                        pulllistView.setVisibility(View.GONE);
                    }
                    else {
                        ViewUtil.showTipsToast(UserWeiboList.this, FAIL);
                    }
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

    public void getUserWeiboArray(ArrayList<MyWeiboData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    MyWeiboData weibodata = new MyWeiboData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    weibodata.setName(jo.getString("nickname"));
                    titleTextView.setText(jo.getString("nickname") + "的动态");
                    weibodata.setTime(jo.getString("issue_date"));
                    weibodata.setContent(jo.getString("content"));
                    weibodata.setRetweetCount(jo.getString("retweet_count"));
                    weibodata.setReplyCount(jo.getString("reply_count"));
                    weibodata.setId(jo.getString("weibo_id"));
                    weibodata.setUserId(jo.getString("user_id"));
                    weibodata.setAttachid(jo.getString("attach_id"));
                    weibodata.setTitle(jo.getString("title"));
                    weibodata.setPreview(jo.getString("preview"));
                    weibodata.setType(jo.getInt("type"));
                    weibodata.setSource(jo.getString("source"));
                    weibodata.setBitmap(null);
// GetPicTask task = new GetPicTask(weibodata);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + weibodata.getUserId() +
// "&service=2004100");
                    myweiboArray.add(weibodata);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNoData() {
        if (subArray.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    public void showFail(String failInf) {
        if (subArray.size() == 0) {
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
        map.put("inf", "username [" + appState.getUsername() + "]: open garden user event list");
        String eventName = "v2 open garden user event list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_user_event_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

}
