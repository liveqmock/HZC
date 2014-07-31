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

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.WeiboHallAdapter;
import com.haozan.caipiao.db.WeiboDBHandler;
import com.haozan.caipiao.db.WeiboHallDB;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.umeng.analytics.MobclickAgent;

public class WeiboTypesActivity
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnHeaderRefreshListener, OnLastItemVisibleListener {
    /** Called when the activity is first created. */
    private final static int SIZE = 10;
    private String lotteryType = null;
    private int page = 1;

    WeiboHallAdapter adapter;
// private CustomListView weibotypelist;
    private PullToRefreshListView pulllistView;
    // 下拉刷新
    private TextView titleTextView;
    private ImageButton toTopButton;
    private RelativeLayout titleLy;
    protected Button titleRightButton;

    private ArrayList<WeiboData> weibohallArray;
    private boolean ring = false;
    public boolean ifClearArray = false;
    ListView actualListView;
    View footView;
    boolean ifGetMoreData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weibo_general);
        setupViews();
        init();
    }

    private void setupViews() {
        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        titleLy = (RelativeLayout) findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        pulllistView = (PullToRefreshListView) findViewById(R.id.weibo_general_list);
        pulllistView.setOnHeaderRefreshListener(this);
        pulllistView.setOnLastItemVisibleListener(this);
        actualListView = pulllistView.getRefreshableView();
        footView = View.inflate(WeiboTypesActivity.this, R.layout.list_item_load_more_view, null);
        titleRightButton = (Button) findViewById(R.id.title_btinit_right);
        titleRightButton.setVisibility(View.GONE);
        toTopButton = (ImageButton) findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);
    }

    private void init() {
        // 返回顶部按钮显示
// BasicWeibo.backTopShow(weibotypelist, toTopButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lotteryType = bundle.getString("type");
            for (int i = 0; i < BasicWeibo.weiboTypesName.length; i++) {
                if (lotteryType.equals(BasicWeibo.weiboTypesEnglishNames[i])) {
                    titleTextView.setText(BasicWeibo.weiboTypesName[i]);
                }
            }
        }
        weibohallArray = new ArrayList<WeiboData>();
        adapter = new WeiboHallAdapter(WeiboTypesActivity.this, weibohallArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(WeiboTypesActivity.this)) {
            WeiboTypesTask task = new WeiboTypesTask();
            task.execute();
        }
        else {
            LocalDataTask task = new LocalDataTask();
            task.execute();
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title) {
            BasicWeibo.backTop(actualListView);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.list_quick_to_top) {
            BasicWeibo.backTop(actualListView);
            toTopButton.setVisibility(View.GONE);
        }

    }

    /**
     * 保存微博数据
     * 
     * @param status
     */
    private void saveData(List<WeiboData> status) {
        WeiboDBHandler dbHandler = new WeiboDBHandler(WeiboTypesActivity.this);
        dbHandler.clearTable(WeiboHallDB.HOME_TABLE, lotteryType);
        if (status.size() > 10) { // 只保存前10项
            status = status.subList(0, 9);
        }
        for (int i = 0; i < status.size(); i++) {
            dbHandler.save(status.get(i), lotteryType);
        }
    }

    /**
     * 异步加载本地的微博数据的监听器
     */

    private class LocalDataTask
        extends AsyncTask<Void, Void, ArrayList<WeiboData>> {
        @Override
        protected ArrayList<WeiboData> doInBackground(Void... params) {
            weibohallArray = new WeiboDBHandler(WeiboTypesActivity.this).getStatus(lotteryType);
            return weibohallArray;
        }

        // doInBackground执行之后
        @Override
        protected void onPostExecute(ArrayList<WeiboData> weibohallArray) {
            WeiboHallAdapter adapter1 = new WeiboHallAdapter(WeiboTypesActivity.this, weibohallArray);
            actualListView.setAdapter(adapter1);
            super.onPostExecute(weibohallArray);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void showFail(String failInf) {
        if (weibohallArray.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    private void showNoData() {
        if (weibohallArray.size() == 0) {
            LocalDataTask task = new LocalDataTask();
            task.execute();
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    class WeiboTypesTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004140");
            parameter.put("pid", LotteryUtils.getPid(WeiboTypesActivity.this));
            parameter.put("page_no", "" + page);
            parameter.put("size", "" + SIZE);
            if (lotteryType.equals("goucai")) {
                parameter.put("kind", "11");
            }
            else if (lotteryType.equals("putong")) {
                parameter.put("kind", "30");
            }
            else if (lotteryType.equals("xinwen")) {
                parameter.put("kind", "31");
            }
            else if (lotteryType.equals("zhongjiang")) {
                parameter.put("kind", "12");
            }
            else if (lotteryType.equals("games")) {
                // parameter.put("lottery_id", "games");
                parameter.put("kind", "41");
            }
            else if (lotteryType.equals("hemai")) {// 合买
                parameter.put("kind", "51");
            }
            else if (lotteryType.equals("jingcai")) {// 竞猜
                parameter.put("kind", "61");
            }
            else if (lotteryType.equals("sd")) {
                parameter.put("lottery_id", "3d");
                parameter.put("kind", "10");
            }
            else if (lotteryType.equals("eexw")) {
                parameter.put("lottery_id", "22x5");
                parameter.put("kind", "10");
            }
            else if (lotteryType.equals("rxj")) {
                parameter.put("lottery_id", "r9");
                parameter.put("kind", "10");
            }
            else {
                parameter.put("lottery_id", lotteryType);
                parameter.put("kind", "10");
            }

// if (lotteryType.equals("ssq")) {
// parameter.put("lottery_id", "ssq");
// parameter.put("kind", "10");
// }

// else if (lotteryType.equals("qlc")) {
// parameter.put("lottery_id", "qlc");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("ssl")) {
// parameter.put("lottery_id", "ssl");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("dfljy")) {
// parameter.put("lottery_id", "dfljy");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("swxw")) {
// parameter.put("lottery_id", "swxw");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("dlt")) {
// parameter.put("lottery_id", "dlt");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("plw")) {
// parameter.put("lottery_id", "plw");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("pls")) {
// parameter.put("lottery_id", "pls");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("qxc")) {
// parameter.put("lottery_id", "qxc");
// parameter.put("kind", "10");
// }

// else if (lotteryType.equals("jczq")) {
// parameter.put("lottery_id", "jczq");
// parameter.put("kind", "10");
// }

// else if (lotteryType.equals("sfc")) {
// parameter.put("lottery_id", "sfc");
// parameter.put("kind", "10");
// }

            // add by vincent
// else if (lotteryType.equals("jx11x5")) {
// parameter.put("lottery_id", "jx11x5");
// parameter.put("kind", "10");
// }

// else if (lotteryType.equals("hnklsf")) {
// parameter.put("lottery_id", "hnklsf");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("jclq")) {
// parameter.put("lottery_id", "jclq");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("klsf")) {
// parameter.put("lottery_id", "klsf");
// parameter.put("kind", "10");
// }
// else if (lotteryType.equals("cqssc")) {
// parameter.put("lottery_id", "cqssc");
// parameter.put("kind", "10");
// }

            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(WeiboTypesActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, false, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        // doInBackground执行之后
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
                    ring = true;
                    if (ifClearArray == true) {
                        weibohallArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = weibohallArray.size();
                        getWeiboHallArray(weibohallArray, data);
                        saveData(weibohallArray);
                        int allSize = weibohallArray.size();
                        if (allSize - lastSize < 10) {
                            ifGetMoreData = false;
                        }
                        adapter.notifyDataSetChanged();
                        page++;
                    }
                    else {
                        showNoData();
                    }
                }
                else {
                    showFail(failTips);
                }

            }
        }

        // doInBackground执行之前
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
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

        // /////////////////////////////////////

        // 获取图片
        public InputStream getImage()
            throws IOException {
            String url = "";
            String parameter = "";
            InputStream inputStream = null;
            AndroidHttpClient client = new AndroidHttpClient(WeiboTypesActivity.this);
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
                    ViewUtil.showTipsToast(WeiboTypesActivity.this, "头像获取失败");
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
                // AndroidHttpClient httpClient = new AndroidHttpClient();
                // HttpEntity client = httpClient.getHttpEntity(params[0]);
                return null;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden type event");
        String eventName = "v2 open garden type event";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_type_event";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WeiboTypesActivity.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboTypesActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                        R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        page = 1;
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