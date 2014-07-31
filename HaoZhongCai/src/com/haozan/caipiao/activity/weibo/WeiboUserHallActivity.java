package com.haozan.caipiao.activity.weibo;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.adapter.WeiboAdapter;
import com.haozan.caipiao.connect.GetWeiboService;
import com.haozan.caipiao.db.WeiboDBHandler;
import com.haozan.caipiao.db.WeiboHallDB;
import com.haozan.caipiao.jgravatar.Gravatar;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class WeiboUserHallActivity
    extends BasicActivity
    implements OnClickListener, OnHeaderRefreshListener, LoadDataListener, OnGetDataAgainListener {
    /** Called when the activity is first created. */
    private final static String NO_DATA = "没有财园动态记录\n动态信息记录好友近期投注和娱乐状况..";

    private final static int SIZE = 10;
    private int page = 1;

    // 下拉刷新
    private RelativeLayout titleLy;
    private Button newButton;
    private Button refreshButton;
    private Button refreshButton1;
    private TextView nametv;
    private ImageButton toTopButton;

    private ArrayList<WeiboData> weiboArray;
    private WeiboAdapter adapter;
    private RefreshLayout layoutPullToRefresh;
    private AutoLoadListView listview;
    private EmptyLayout layoutEmpty;

    private boolean isRefresh = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_user_hall);
        setupViews();
        init();
    }

    private void setupViews() {
        titleLy = (RelativeLayout) findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        newButton = (Button) findViewById(R.id.title_btinit_left);
        newButton.setOnClickListener(this);
        newButton.setText("发表动态");
        refreshButton = (Button) findViewById(R.id.title_btinit_right);
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.setText("个人资料");
        nametv = (TextView) findViewById(R.id.newCmtextView);
        refreshButton.setOnClickListener(this);
        refreshButton1 = (Button) findViewById(R.id.title_reflash);
        refreshButton1.setVisibility(View.GONE);
        refreshButton1.setOnClickListener(this);
        nametv.setOnClickListener(this);
        toTopButton = (ImageButton) findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);

        layoutPullToRefresh = (RefreshLayout) findViewById(R.id.pull_refresh_view);
        layoutPullToRefresh.setOnHeaderRefreshListener(this);
        listview = (AutoLoadListView) this.findViewById(R.id.listview);
        listview.setOnLoadDataListener(this);
        layoutEmpty = (EmptyLayout) this.findViewById(R.id.empty_layout);
        layoutEmpty.setOnGetDataAgainListener(this);

        String niceName = appState.getNickname();
        if (niceName == null || niceName.equals("") || niceName.equals("null")) {
        }
        else {
            nametv.setText("我的财园");
        }
    }

    private void init() {
        // 返回顶部按钮显示
// BasicWeibo.backTopShow(weibolist, toTopButton);
        weiboArray = new ArrayList<WeiboData>();
        adapter = new WeiboAdapter(WeiboUserHallActivity.this, weiboArray);
        listview.setAdapter(adapter);
        loadData();
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(WeiboUserHallActivity.this)) {
            WeiboTask getWeiboTask = new WeiboTask();
            getWeiboTask.execute();
        }
        else {
            layoutPullToRefresh.onHeaderRefreshComplete();

            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (weiboArray.size() == 0) {
            layoutEmpty.showNetErrorPage();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
            listview.showFootText();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title) {
            BasicWeibo.backTop(listview);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.list_quick_to_top) {
            BasicWeibo.backTop(listview);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.title_btinit_left) {
            Intent intent = new Intent();
            intent.setClass(WeiboUserHallActivity.this, NewWeiboActivity.class);
            startActivityForResult(intent, 0);
        }
        else if (v.getId() == R.id.title_reflash) {
            isRefresh = true;
            if (HttpConnectUtil.isNetworkAvailable(WeiboUserHallActivity.this)) {
// AnimationRotate(refreshButton1);
                page = 1;
                WeiboTask getWeiboTask = new WeiboTask();
                getWeiboTask.execute();

                LotteryApp appState = ((LotteryApp) getApplicationContext());
                String phone =
                    appState.getUsername().substring(0, 3) + "****" + appState.getUsername().substring(7);
                String niceName = appState.getNickname();
                if (niceName == null || niceName.equals("")) {
                    nametv.setText(phone);
                }
                else {
                    nametv.setText(niceName);
                }
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(WeiboUserHallActivity.this, MyProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                onRefresh();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 保存微博数据
     * 
     * @param status
     */
    private void saveData(List<WeiboData> status) {
        WeiboDBHandler dbHandler = new WeiboDBHandler(WeiboUserHallActivity.this);
        dbHandler.clearTable(WeiboHallDB.HOME_TABLE, "userhall");
        if (status.size() > 10) { // 只保存前10项
            status = status.subList(0, 9);
        }
        for (int i = 0; i < status.size(); i++) {
            dbHandler.save(status.get(i), "userhall");
        }
    }

// /**
// * 异步加载本地的微博数据的监听器
// */
//
// private class LocalDataTask
// extends AsyncTask<Void, Void, ArrayList<WeiboData>> {
// @Override
// protected ArrayList<WeiboData> doInBackground(Void... params) {
// weiboArray = new WeiboDBHandler(WeiboUserHallActivity.this).getStatus("userhall");
// return weiboArray;
// }
//
// // doInBackground执行之后
// @Override
// protected void onPostExecute(ArrayList<WeiboData> weibohallArray) {
// WeiboHallAdapter adapter1 = new WeiboHallAdapter(WeiboUserHallActivity.this, weibohallArray);
// actualListView.setAdapter(adapter1);
// super.onPostExecute(weibohallArray);
// }
//
// @Override
// protected void onPreExecute() {
// super.onPreExecute();
// }
// }

    class WeiboTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetWeiboService getWeibo = new GetWeiboService(WeiboUserHallActivity.this, page, SIZE);
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
                    listview.setVisibility(View.VISIBLE);

                    String data = analyse.getData(json, "response_data");
                    // 如果点击的是刷新按钮
                    if (isRefresh == true) {
                        weiboArray.clear();
                        isRefresh = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = weiboArray.size();
                        getWeiboArray(weiboArray, data);
                        saveData(weiboArray);
                        int allSize = weiboArray.size();
                        if (allSize - lastSize < 10) {
                            listview.loadNoMoreData();
                        }
                        else {
                            listview.readyToLoad();
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
                    OperateInfUtils.clearSessionId(WeiboUserHallActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WeiboUserHallActivity.this);
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
            if (weiboArray.size() == 0) {
                layoutEmpty.setVisibility(View.GONE);
                showProgress();
            }
        }
    }

    private void getWeiboArray(ArrayList<WeiboData> weiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    WeiboData weibodata = new WeiboData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    weibodata.setId(jo.getInt("weibo_id"));
                    weibodata.setUserid(jo.getString("user_id"));
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
// weibodata.setPage(page - 1);
                    weibodata.setBitmap(null);
// GetPicTask task = new GetPicTask(weibodata);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + weibodata.getUserid() +
// "&service=2004100");
                    weiboArray.add(weibodata);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNoData() {
        if (weiboArray.size() == 0) {
            listview.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showNoDataPage(NO_DATA);
        }
    }

    public void showFail() {
        if (weiboArray.size() == 0) {
            listview.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutEmpty.showFailPage();
        }
        else {
            ViewUtil.showTipsToast(this, failTips);
            listview.showFootText();
        }
    }

    // 获取图片
    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(WeiboUserHallActivity.this);
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
                ViewUtil.showTipsToast(WeiboUserHallActivity.this, "头像获取失败");
            }
        }

        // 任务被执行之后
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected byte[] doInBackground(String... params) {
            HttpClient c = new DefaultHttpClient();
            Gravatar g = new Gravatar();
            String url = null;
            try {
                url = params[0];
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            // HttpGet get =new HttpGet(LotteryConfig.serverHTTP + "BuKeServ/" +
            // params[0] + ";jsessionid=" + sessionId +
            // "?phone=" + phoneNum);
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

    @Override
    public void onClickToGetData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        isRefresh = true;

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden user hall");
        String eventName = "v2 open garden user hall";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_hall";
        MobclickAgent.onEvent(this, eventName, "others");
        besttoneEventCommint(eventName);
    }

}