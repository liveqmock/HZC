package com.haozan.caipiao.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.WeiboHallScroll;
import com.haozan.caipiao.activity.weibo.WeiboUserHallActivity;
import com.haozan.caipiao.adapter.WeiboHallAdapter;
import com.haozan.caipiao.connect.GetWeiboHallService;
import com.haozan.caipiao.db.WeiboDBHandler;
import com.haozan.caipiao.db.WeiboHallDB;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.weiboutil.AccessInfoHelper;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class WeiboHallFragment
    extends BasicFragment
    implements OnClickListener, OnHeaderRefreshListener, LoadDataListener, OnGetDataAgainListener {
    private final static int SIZE = 10;
    private int page = 1;

    private WeiboHallAdapter adapter;

    // 下拉刷新
    private Button gardenSquare;
    private Button zhuyeButton;
    private TextView titleTextView;
    private TextView allProfileCount;
    private ImageButton toTopButton;
    private RelativeLayout titleLy;
    private RelativeLayout promptLy;

    private RefreshLayout layoutPullToRefresh;
    private ArrayList<WeiboData> weibohallArray;
    private AutoLoadListView listView;
    private EmptyLayout layoutEmpty;

    private boolean isRefresh = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = View.inflate(mContext, R.layout.weibo_hall, null);
        setupViews(layout);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        // 显示用户资料消息数
        BasicWeibo.showAllProfileCount(mParentActivity, promptLy, allProfileCount);
        super.onResume();
    }

    private void setupViews(View layout) {
        titleLy = (RelativeLayout) layout.findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        promptLy = (RelativeLayout) layout.findViewById(R.id.layout_update);
        promptLy.setOnClickListener(this);
        allProfileCount = (TextView) layout.findViewById(R.id.profileCount);
        gardenSquare = (Button) layout.findViewById(R.id.title_btinit_right);
        gardenSquare.setText(" 广    场 ");
        zhuyeButton = (Button) layout.findViewById(R.id.title_btinit_left);
        zhuyeButton.setText(" 我的财园 ");
        zhuyeButton.setOnClickListener(this);
        gardenSquare.setOnClickListener(this);
        titleTextView = (TextView) layout.findViewById(R.id.newCmtextView);
        titleTextView.setText("财  园");
        toTopButton = (ImageButton) layout.findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);

        layoutPullToRefresh = (RefreshLayout) layout.findViewById(R.id.pull_refresh_view);
        layoutPullToRefresh.setOnHeaderRefreshListener(this);
        listView = (AutoLoadListView) layout.findViewById(R.id.listview);
        listView.setOnLoadDataListener(this);
        layoutEmpty = (EmptyLayout) layout.findViewById(R.id.empty_layout);
        layoutEmpty.setOnGetDataAgainListener(this);
    }

    private void init() {
        weibohallArray = new ArrayList<WeiboData>();
        adapter = new WeiboHallAdapter(mParentActivity, weibohallArray);
        listView.setAdapter(adapter);
        loadData();
    }

    /**
     * 异步加载本地的微博数据的监听器
     */

    private class LocalDataTask
        extends AsyncTask<Void, Void, ArrayList<WeiboData>> {
        @Override
        protected ArrayList<WeiboData> doInBackground(Void... params) {
            weibohallArray = new WeiboDBHandler(mParentActivity).getStatus("all");
            return weibohallArray;
        }

        // doInBackground执行之后
        @Override
        protected void onPostExecute(ArrayList<WeiboData> weibohallArray) {
            super.onPostExecute(weibohallArray);
            if (weibohallArray.size() == 0) {
                showFail();
            }
            else {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
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
        intent.setClass(mParentActivity, Login.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title) {
            // 返回ListView顶部
            BasicWeibo.backTop(listView);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.list_quick_to_top) {
            BasicWeibo.backTop(listView);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.layout_update) {
            Intent intent = new Intent();
            intent.setClass(mParentActivity, MyProfileActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.title_btinit_left) {
            if (!checkLogin()) {
                lottery("我的财园");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(mParentActivity, WeiboUserHallActivity.class);
                mParentActivity.startActivity(intent);
            }
        }
        else if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(mParentActivity, WeiboHallScroll.class);
            mParentActivity.startActivity(intent);
        }
    }

    public Handler endSessionHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ViewUtil.showTipsToast(mParentActivity, "取消绑定成功");
        }
    };

    public class EndSessionThread
        implements Runnable {
        public void run() {
            AccessInfoHelper accessDBHelper = new AccessInfoHelper(mParentActivity);
            accessDBHelper.open();
            accessDBHelper.delete();
            accessDBHelper.close();
            endSessionHandle.sendEmptyMessage(201);
        }
    }

    public class WeiboHallTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetWeiboHallService getWeibo = new GetWeiboHallService(mParentActivity, page, SIZE);
            String json = getWeibo.sending();
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            layoutPullToRefresh.onHeaderRefreshComplete();
            dismissProgress();

            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    listView.setVisibility(View.VISIBLE);

                    String data = analyse.getData(json, "response_data");
                    // 显示用户资料消息数
                    BasicWeibo.showAllProfileCount(mParentActivity, promptLy, allProfileCount);
                    // 如果点击的是刷新按钮
                    if (isRefresh == true) {
                        weibohallArray.clear();
                        isRefresh = false;
                    }

                    if (data.equals("[]") == false) {
                        int lastSize = weibohallArray.size();
                        getWeiboHallArray(weibohallArray, data);
                        int allSize = weibohallArray.size();
                        page++;
                        if (allSize - lastSize < 10) {
                            listView.loadNoMoreData();
                        }
                        else {
                            listView.readyToLoad();
                        }
                        adapter.notifyDataSetChanged();

                        saveData(weibohallArray);
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
            if (weibohallArray.size() == 0) {
                layoutEmpty.setVisibility(View.GONE);
                showProgress();
            }
        }

        public void getWeiboHallArray(ArrayList<WeiboData> weibohallArray, String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    for (int i = 0; i < hallArray.length(); i++) {
                        WeiboData weibodata = new WeiboData();
                        JSONObject jo = hallArray.getJSONObject(i);
                        weibodata.setUserid(jo.getString("user_id"));
                        weibodata.setId(jo.getInt("weibo_id"));
                        weibodata.setName(jo.getString("nickname"));
                        weibodata.setTime(jo.getString("issue_date"));
                        weibodata.setContent(jo.getString("content"));
                        weibodata.setRetweetCount(jo.getInt("retweet_count"));
                        weibodata.setReplyCount(jo.getString("reply_count"));
                        weibodata.setAttachid(jo.getString("attach_id"));
                        weibodata.setTitle(jo.getString("title"));
                        weibodata.setPreview(jo.getString("preview"));
                        weibodata.setType(jo.getInt("type"));
                        weibodata.setSource(jo.getString("source"));
// weibodata.setPage(page-1);
                        weibodata.setBitmap(null);
// GetPicTask task = new GetPicTask(weibodata);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + weibodata.getUserid() +
// "&service=2004100");
                        weibohallArray.add(weibodata);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 保存微博数据
         * 
         * @param status
         */
        private void saveData(List<WeiboData> status) {
            WeiboDBHandler dbHandler = new WeiboDBHandler(mParentActivity);
            dbHandler.clearTable(WeiboHallDB.HOME_TABLE, "all");
            if (status.size() > 10) { // 只保存前10项
                status = status.subList(0, 9);
            }
            for (int i = 0; i < status.size(); i++) {
                dbHandler.save(status.get(i), "all");
            }
        }

        // /////////////////////////////////////

        // 获取图片
        public InputStream getImage()
            throws IOException {
            String url = "";
            String parameter = "";
            InputStream inputStream = null;
            AndroidHttpClient client = new AndroidHttpClient(mParentActivity);
            HttpClient httpClient = client.getDefaultHttpClient();
            HttpPost httpPost = client.getHttpPost(url, parameter);
            try {
                HttpResponse response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        inputStream = entity.getContent();
                        entity.consumeContent();
                    }
                }
            }
            catch (IOException ex) {
                httpPost.abort();
            }
            if (inputStream != null) {
                return inputStream;
            }
            else
                return null;
        }

        class GetPicTask
            extends AsyncTask<String, Long, byte[]> {
            private WeiboData weiboData;

            public GetPicTask(WeiboData weiboData) {
                this.weiboData = weiboData;

            }

            @Override
            protected void onPostExecute(byte[] data) {
                if (data != null) {
                    weiboData.setBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));// bitmap
                    adapter.notifyDataSetChanged();
                }
                else {
                    ViewUtil.showTipsToast(mParentActivity, "头像获取失败");
                }
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected byte[] doInBackground(String... params) {
                HttpClient c = new DefaultHttpClient();
                String url = null;
                try {

                    url = params[0];

                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }

                HttpGet get = new HttpGet(url);

                HttpResponse response = null;
                // try {
                try {
                    response = c.execute(get);
                    HttpEntity client = response.getEntity();
                    if (client != null) {
                        InputStream is = null;
                        ByteArrayOutputStream outStream = null;
                        try {
                            is = client.getContent();
                            if (is != null) {
                                outStream = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = is.read(buffer)) != -1) {
                                    outStream.write(buffer, 0, len);
                                }
                                return outStream.toByteArray();
                            }
                            else
                                return null;
                        }
                        catch (IllegalStateException e) {
                            e.printStackTrace();
                            return null;
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        finally {
                            if (is != null)
                                is.close();
                            if (outStream != null)
                                outStream.close();
                        }
                    }
                    else {
                        return null;
                    }
                }
                catch (ClientProtocolException e1) {
                    e1.printStackTrace();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden whole hall list");
        String eventName = "v2 open garden whole hall list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_hall";
        MobclickAgent.onEvent(mParentActivity, eventName, "whole");
        besttoneEventCommint(eventName);
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(mParentActivity)) {
            WeiboHallTask getWeiboTask = new WeiboHallTask();
            getWeiboTask.execute();
        }
        else {
            layoutPullToRefresh.onHeaderRefreshComplete();
            LocalDataTask task = new LocalDataTask();
            task.execute();
            ViewUtil.showTipsToast(mParentActivity, noNetTips);
        }
    }

    private void showFail() {
        if (weibohallArray.size() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showFailPage();
        }
        else {
            ViewUtil.showTipsToast(mParentActivity, failTips);
            listView.showFootText();
        }
    }

    private void showNoData() {
        if (weibohallArray.size() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showNoDataPage("财园没有信息\n发表动态参与互动");
        }
        else {
            listView.loadNoMoreData();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;

        loadData();
    }

    @Override
    public void onClickToGetData() {
        onRefresh();
    }
}