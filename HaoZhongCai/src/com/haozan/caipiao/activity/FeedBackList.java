package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.FeedBackAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FeedBackMessageData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadExpandableListView;
import com.haozan.caipiao.widget.AutoLoadExpandableListView.LoadDataListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 留言板
 * 
 * @author peter_feng
 * @create-time 2013-3-25 下午2:26:46
 */
public class FeedBackList
    extends ContainTipsPageBasicActivity
    implements LoadDataListener {
    private static final String NODATA = "没有留言记录\n您的留言，我们会择优回复到这里..";
    private static final int SIZE = 10;
    private int page = 1;
    private TextView title;
    private TextView subTitle;
    private ArrayList<FeedBackMessageData> feedbackListData;

    private FeedBackAdapter adapter;
    private AutoLoadExpandableListView feedbackListView;
    private boolean ifMyadvice = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_list);
        setupViews();
        initData();
        init();
    }

    public void initData() {
        feedbackListData = new ArrayList<FeedBackMessageData>();
        feedbackListView = (AutoLoadExpandableListView) findViewById(R.id.feedback_list);
        feedbackListView.setOnLoadDataListener(this);
        feedbackListView.addLoadingFootView();
        Bundle bundle = getIntent().getExtras();
        ifMyadvice = bundle.getBoolean("if_my_advice");
    }

    public void setupViews() {
        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.feedback_list_tips);
    }

    public void init() {
        if (ifMyadvice == true) {
            title.setText("我的反馈");
            subTitle.setVisibility(View.GONE);
        }
        else {
            title.setText("留言板");
        }
        adapter = new FeedBackAdapter(FeedBackList.this, feedbackListData);
        feedbackListView.setAdapter(adapter);

        loadData();
    }

    // 判断是否登录
    private boolean checkLogin() {
        String userid = appState.getSessionid();
        if (userid == null) {
            return false;
        }
        else {
            return true;
        }
    }

    private void lottery(String flag) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", flag);
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
// intent.setClass(FeedBackList.this, StartUp.class);
        intent.setClass(FeedBackList.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open feedback feedbackList");
        String eventName = "v2 open feedback feedbackList";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        if (feedbackListData.size() == 0) {
            showTipsPage(failInf);
            feedbackListView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        feedbackListView.removeLoadingFootView();
    }

    private void showNoData() {
        if (feedbackListData.size() == 0) {
            showTipsPage(NODATA);
            feedbackListView.setVisibility(View.GONE);
        }
        feedbackListView.removeLoadingFootView();
    }

    class FeedBackTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "feedback_list");
            parameter.put("pid", LotteryUtils.getPid(FeedBackList.this));
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + SIZE);
            parameter.put("", "");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(FeedBackList.this);
            String json = null;
            try {
                if (ifMyadvice == true) {
                    json = connectNet.getJsonGet(1, true, iniHashMap());
                }
                else {
                    json = connectNet.getJsonGet(1, false, iniHashMap());
                }

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
                        int lastSize = feedbackListData.size();
                        feedbackListResponseData(data);
                        int preSize = feedbackListData.size();
                        if (preSize - lastSize < 10) {
                            feedbackListView.removeLoadingFootView();
                        }
                        else {
                            feedbackListView.addLoadingFootView();
                            feedbackListView.readyToLoad();
                        }
                        page++;
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        showNoData();
                    }
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

    private void feedbackListResponseData(String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    FeedBackMessageData message = new FeedBackMessageData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    message.setName(jo.getString("nickname"));
                    message.setTime(jo.getString("issue_date"));
                    message.setContent(jo.getString("msg_content"));
                    message.setHuifu(jo.getString("reply_content"));
                    feedbackListData.add(message);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_feedbacklist";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            FeedBackTask feedbackTask = new FeedBackTask();
            feedbackTask.execute();
        }
        else {
            showFail(noNetTips);
        }
    }
}
