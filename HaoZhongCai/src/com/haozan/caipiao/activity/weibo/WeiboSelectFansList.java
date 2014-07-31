package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.activity.SharePage;
import com.haozan.caipiao.adapter.WeiboSelectFansAdapter;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.WeiboSelectedItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.weibo.sdk.api.FriendshipsAPI;
import com.haozan.caipiao.weibo.sdk.api.WeiboAPI;
import com.haozan.caipiao.weibo.sdk.keep.AccessTokenKeeper;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 用列表展示用户粉丝，用于发微博时@粉丝
 * 
 * @author Vincent
 * @create-time 2013-4-22 上午9:52:59
 */
public class WeiboSelectFansList
    extends ContainTipsPageBasicActivity
    implements OnHeaderRefreshListener, OnClickListener, OnLastItemVisibleListener {
    public static final int NUM = 20;
    public static final String FAIL = "数据获取失败";
    public int cursor = 0;
    private ArrayList<WeiboSelectedItem> names;
    private WeiboSelectFansAdapter adapter;
    public boolean ifClearArray = false;
    protected boolean ring = false;
    private String uid;
    private String tempCursor;

    private PullToRefreshListView pulllistView;
    private TextView title;
    private Button titleRightBtn;
    ArrayList<LotteryApp> acounts;

    ListView actualListView;
    View footView;
    boolean ifGetMoreData = false;

    public static Oauth2AccessToken accessToken;
    public RequestListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_general);
        setupViews();
        initData();
        init();
    }

    private void init() {
        loadData();
    }

    private void initData() {
        names = new ArrayList<WeiboSelectedItem>();
        adapter = new WeiboSelectFansAdapter(WeiboSelectFansList.this, names);
        actualListView.setAdapter(adapter);
        uid = preferences.getString("sinaweibo_userid", null);
        // sina
        accessToken = AccessTokenKeeper.readAccessToken(this);
// uid = "3568150861";
// AccessInfoHelper accessDBHelper = new AccessInfoHelper(WeiboSelectFansList.this);
// accessDBHelper.open();
// acounts = accessDBHelper.getAccessInfos();
// accessDBHelper.close();
// uid = acounts.get(0).getWeiboUserID();
    }

    private void setupViews() {
        title = (TextView) findViewById(R.id.newCmtextView);
        title.setText("粉丝列表");
        titleRightBtn = (Button) findViewById(R.id.title_btinit_right);
        titleRightBtn.setText("完成");
        titleRightBtn.setOnClickListener(this);
        pulllistView = (PullToRefreshListView) findViewById(R.id.weibo_general_list);
        pulllistView.setOnHeaderRefreshListener(this);
        pulllistView.setOnLastItemVisibleListener(this);
        actualListView = pulllistView.getRefreshableView();
        footView = View.inflate(WeiboSelectFansList.this, R.layout.list_item_load_more_view, null);
    }

    class GetFriendsTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
            listener = new RequestListener() {

                @Override
                public void onIOException(final IOException exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissProgress();
                            Toast.makeText(WeiboSelectFansList.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "IOException:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onError(final com.weibo.sdk.android.WeiboException exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissProgress();
                            Toast.makeText(WeiboSelectFansList.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "WeiboException:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onComplete4binary(final ByteArrayOutputStream exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissProgress();
                            Toast.makeText(WeiboSelectFansList.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "ByteArrayOutputStream:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onComplete(final String result) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            actualListView.removeFooterView(footView);
                            dismissProgress();
                            ifGetMoreData = true;
                            pulllistView.onHeaderRefreshComplete();
                            if (result == null) {
                                showFail(failTips);
                            }
                            else {
                                if (ifClearArray == true) {
                                    names.clear();
                                    ifClearArray = false;
                                }

                                try {
                                    JSONObject obj = new JSONObject(result);
                                    tempCursor = obj.getString("next_cursor");
                                    JSONArray array = obj.getJSONArray("users");
                                    JsonAnalyse analyse = new JsonAnalyse();
                                    String data = analyse.getData(result, "users");
                                    if (data.equals("[]") == false) {
                                        int lastSize = names.size();
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject jo = array.getJSONObject(i);
                                            parseWeiboStatus(jo, names);
                                        }
                                        int preSize = names.size();
                                        if (preSize - lastSize < 20) {
                                            ifGetMoreData = false;
                                        }
                                        adapter.notifyDataSetChanged();
                                        cursor = Integer.parseInt(tempCursor);
                                    }
                                    else {
                                        showNoData();
                                    }

                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            dismissProgress();
                        }
                    });

                }
            };
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String rlt = null;
            if (accessToken.isSessionValid()) {
                String url = WeiboAPI.API_SERVER + "/friendships/followers.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", Weibo.getApp_key());
                bundle.add("count", NUM);
                bundle.add("uid", uid);
                bundle.add("cursor", String.valueOf(cursor));
                new FriendshipsAPI(accessToken).followers(Long.parseLong(preferences.getString("sinaweibo_userid",
                                                                                               "")), NUM,
                                                          cursor, false, listener);
            }
            return rlt;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    public void showNoData() {
        if (names.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    private void showFail(String failInf) {
        if (names.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public void parseWeiboStatus(JSONObject jo, ArrayList<WeiboSelectedItem> names) {
        try {
            String name = jo.getString("screen_name");
            WeiboSelectedItem item = new WeiboSelectedItem();
            item.setName(name);
            item.setSelected(false);
            names.add(item);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(WeiboSelectFansList.this)) {
            GetFriendsTask task = new GetFriendsTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("selected_names", getSelectedNames());
            intent.putExtras(bundle);
            intent.setClass(WeiboSelectFansList.this, SharePage.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private String getSelectedNames() {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).isSelected() == true) {
                strBuffer.append("@" + names.get(i).getName());
            }
        }
        return strBuffer.toString();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        cursor = 0;
        ifClearArray = true;
        loadData();
    }

    @Override
    public void onLastItemVisible() {
        if (ifGetMoreData) {
            actualListView.addFooterView(footView);
            loadData();
        }
        else {
            actualListView.removeFooterView(footView);
        }
    }
}
