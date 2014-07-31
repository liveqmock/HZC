package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.SearchPeopleAdapter;
import com.haozan.caipiao.adapter.SearchWeiboAdapter;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.SearchPeopleDate;
import com.haozan.caipiao.types.SearchWeiboDate;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.StringUtils;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.umeng.analytics.MobclickAgent;

public class WeiboSearch
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnHeaderRefreshListener, OnLastItemVisibleListener {
    private static final String WEIBO_FAIL = "没有找到相关动态信息";
    private static final String PEOPLE_FAIL = "没有找到相关用户信息";

    private int bottomType = 1;
    private final static int SIZE = 10;
    private int page = 1;
    private TextView title;
    private Button searchButton;
    private ImageView clearButton;
    private RadioButton searchWeibo;
    private RadioButton searchPeople;
    private EditText searchET;
    private String searchStr;
    private ImageButton toTopButton;
    private String key = null;
    // 下拉刷新

    private SearchPeopleAdapter peopleAdapter;
    private SearchWeiboAdapter weiboAdapter;
    private ArrayList<SearchPeopleDate> peopleArray;
    private ArrayList<SearchWeiboDate> sweiboArray;
    private PullToRefreshListView pulllistView;
    private RelativeLayout titleLy;
    private boolean ring = false;
    public boolean ifClearArray = false;
    ListView actualListView;
    View footView;
    boolean ifGetMoreData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weibo_search);
        setupViews();
        init();
    }

    private void setupViews() {
        titleLy = (RelativeLayout) findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        title = (TextView) findViewById(R.id.initName);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        clearButton = (ImageView) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);
        searchWeibo = (RadioButton) findViewById(R.id.searchWeibo);
        searchPeople = (RadioButton) findViewById(R.id.searchPeople);
        pulllistView = (PullToRefreshListView) findViewById(R.id.search_autolist);
        pulllistView.setOnHeaderRefreshListener(this);
        pulllistView.setOnLastItemVisibleListener(this);
        actualListView = pulllistView.getRefreshableView();
        footView = View.inflate(WeiboSearch.this, R.layout.list_item_load_more_view, null);
        searchET = (EditText) findViewById(R.id.sreachET);
        searchET.setOnClickListener(this);
        title.setText("搜索");
        toTopButton = (ImageButton) findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);
    }

    private void init() {
        peopleArray = new ArrayList<SearchPeopleDate>();
        sweiboArray = new ArrayList<SearchWeiboDate>();
        weiboAdapter = new SearchWeiboAdapter(WeiboSearch.this, sweiboArray, searchET);
        peopleAdapter = new SearchPeopleAdapter(WeiboSearch.this, peopleArray, searchET);
        // 接收全局搜索传过来的数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ring = false;
            searchStr = this.getIntent().getExtras().getString("searchStr");
            if (searchStr != null) {
                searchET.setText(searchStr);
                // 将光标移动到EditText文本的右边
                Editable b = searchET.getText();
                searchET.setSelection(b.length());
                if (checkInput() == true) {
                    loadData();
                }
            }
        }
        // 返回顶部按钮显示
        // 监听searchET输入
        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchET.length() != 0) {
                    clearButton.setVisibility(View.VISIBLE);
                }
                else {
                    clearButton.setVisibility(View.GONE);
                }
            }

        });
        // 从全局搜索传入判断searchET是否有字符
        if (searchET.length() != 0) {
            clearButton.setVisibility(View.VISIBLE);
        }
        else {
            clearButton.setVisibility(View.GONE);
        }
    }

    // 屏蔽搜索键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            WeiboSearch.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboSearch.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                 R.anim.push_to_left_out);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * // 保存最近搜索的关键字记录 private void saveRecentSearchHistory(String queryKeyword) { SearchRecentSuggestions
     * suggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY,
     * SearchSuggestionProvider.MODE); suggestions.saveRecentQuery(queryKeyword, null); } // 清除最近搜索的关键字记录
     * private void clearRecentSearchHistory() { SearchRecentSuggestions suggestions = new
     * SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
     * suggestions.clearHistory(); }
     */

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
        else if (v.getId() == R.id.searchButton) {
            ifClearArray = true;
            page = 1;
            // 隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            ring = false;
            if (checkInput() == true) {
                if (searchWeibo.isChecked()) {
                    bottomType = 1;
                    actualListView.setAdapter(weiboAdapter);
                }
                else if (searchPeople.isChecked()) {
                    bottomType = 2;
                    actualListView.setAdapter(peopleAdapter);
                }
                loadData();
            }
        }
        else if (v.getId() == R.id.clearButton) {
            searchET.setText("");
            clearButton.setVisibility(View.GONE);
        }
    }

    private Boolean checkInput() {
        String warning = null;
        key = searchET.getText().toString();
        if (StringUtils.isBlank(key)) {
            warning = "请输入关键字";
        }
        else if (!Pattern.matches("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w\\s]+$", key)) {
            warning = "输入不合法,请输入汉字、字母或者数字";
        }
        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        return true;
    }

    private class SearchTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dismissProgress();
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
                        sweiboArray.clear();
                        peopleArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        pulllistView.setVisibility(View.VISIBLE);
                        page++;
                        if (bottomType == 1) {
                            int lastSize = sweiboArray.size();
                            getWeiboArray(sweiboArray, data);
                            int preSize = sweiboArray.size();
                            if (preSize - lastSize < 10) {
                                ifGetMoreData = false;
                            }
                            weiboAdapter.notifyDataSetChanged();
                        }
                        else if (bottomType == 2) {
                            int lastSize = sweiboArray.size();
                            getPeopleArray(peopleArray, data);
                            int preSize = peopleArray.size();
                            if (preSize - lastSize < 10) {
                                ifGetMoreData = false;
                            }
                            peopleAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(WeiboSearch.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WeiboSearch.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(failTips);
                }
            }
            setButtonClickable(true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
            setButtonClickable(false);
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004090");
            parameter.put("pid", LotteryUtils.getPid(WeiboSearch.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + SIZE);
            parameter.put("key", HttpConnectUtil.encodeParameter(searchET.getText().toString()));
            if (bottomType == 1) {
                parameter.put("&type", "1");
            }
            else if (bottomType == 2) {
                parameter.put("&type", "2");
            }
            return parameter;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            ConnectService connectNet = new ConnectService(WeiboSearch.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void showNoData() {
        if (bottomType == 1) {
            if (sweiboArray.size() == 0) {
                showTipsPage(WEIBO_FAIL);
                pulllistView.setVisibility(View.GONE);
            }
            else {
                ViewUtil.showTipsToast(this, noMoreData);
            }
        }
        else if (bottomType == 2) {
            if (peopleArray.size() == 0) {
                showTipsPage(PEOPLE_FAIL);
                pulllistView.setVisibility(View.GONE);
            }
            else {
                ViewUtil.showTipsToast(this, noMoreData);
            }
        }

    }

    private void showFail(String failInf) {
        if (bottomType == 1) {
            if (sweiboArray.size() == 0) {
                showTipsPage(failInf);
                peopleAdapter.notifyDataSetChanged();
            }
            else {
                ViewUtil.showTipsToast(WeiboSearch.this, failInf);
            }
        }
        else if (bottomType == 2) {
            if (peopleArray.size() == 0) {
                showTipsPage(failInf);
                weiboAdapter.notifyDataSetChanged();
            }
            else {
                ViewUtil.showTipsToast(WeiboSearch.this, failInf);
            }
        }
    }

    /*
     * private void setWeiboButtonStatus() { weibolist.setVisibility(View.VISIBLE);
     * peoplelist.setVisibility(View.GONE); weibolist.setClickable(false); peoplelist.setClickable(true); }
     */

    /*
     * private void setPeopleButtonStatus() { weibolist.setVisibility(View.GONE);
     * peoplelist.setVisibility(View.VISIBLE); weibolist.setClickable(true); peoplelist.setClickable(false); }
     */

    private void setButtonClickable(boolean isClick) {
        if (isClick) {
            searchButton.setClickable(true);
            searchWeibo.setClickable(true);
            searchPeople.setClickable(true);
        }
        else {
            searchButton.setClickable(false);
            searchWeibo.setClickable(false);
            searchPeople.setClickable(false);
        }
    }

    public void getPeopleArray(ArrayList<SearchPeopleDate> peopleArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    SearchPeopleDate data = new SearchPeopleDate();
                    JSONObject jo = hallArray.getJSONObject(i);
                    data.setUserid(jo.getString("user_id"));
                    data.setGender(jo.getString("gender"));
                    data.setName(jo.getString("nickname"));
                    data.setCity(jo.getString("city"));
                    data.setQianming(jo.getString("signature"));
                    data.setBitmap(null);
// GetPicTask task = new GetPicTask(data);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + data.getUserid() +
// "&service=2004100");
                    peopleArray.add(data);
                }
// peopleAdapter.notifyDataSetChanged();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取图片
    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(WeiboSearch.this);
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
        private SearchPeopleDate data;

        public GetPicTask(SearchPeopleDate data) {
            this.data = data;
        }

        @Override
        protected void onPostExecute(byte[] data1) {
            if (data != null) {
                data.setBitmap(BitmapFactory.decodeByteArray(data1, 0, data1.length));// bitmap
                peopleAdapter.notifyDataSetChanged();
            }
            else {
                ViewUtil.showTipsToast(WeiboSearch.this, "头像获取失败");
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected byte[] doInBackground(String... params) {
            HttpClient c = new DefaultHttpClient();
// Gravatar g = new Gravatar();
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

    public void getWeiboArray(ArrayList<SearchWeiboDate> sweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    SearchWeiboDate data = new SearchWeiboDate();
                    JSONObject jo = hallArray.getJSONObject(i);
                    data.setUserid(jo.getString("user_id"));
                    data.setWeiboid(jo.getString("weibo_id"));
                    data.setTime(jo.getString("issue_date"));
                    data.setContent(jo.getString("content"));
                    data.setName(jo.getString("nickname"));
                    data.setRetweetCount(jo.getString("retweet_count"));
                    data.setReplyCount(jo.getString("reply_count"));
                    data.setAttachid(jo.getString("attach_id"));
                    data.setTitle(jo.getString("title"));
                    data.setPreview(jo.getString("preview"));
                    data.setType(jo.getInt("type"));
                    data.setSource(jo.getString("source"));
                    data.setBitmap(null);
// WeiboGetPicTask task = new WeiboGetPicTask(data);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + data.getUserid() +
// "&service=2004100");
                    sweiboArray.add(data);
                }
// weiboAdapter.notifyDataSetChanged();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class WeiboGetPicTask
        extends AsyncTask<String, Long, byte[]> {
        private SearchWeiboDate data;

        public WeiboGetPicTask(SearchWeiboDate data) {
            this.data = data;
        }

        @Override
        protected void onPostExecute(byte[] data1) {
            if (data != null) {
                data.setBitmap(BitmapFactory.decodeByteArray(data1, 0, data1.length));// bitmap
                weiboAdapter.notifyDataSetChanged();
            }
            else {
                ViewUtil.showTipsToast(WeiboSearch.this, "头像获取失败");
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

    @Override
    protected void onDestroy() {
// clearRecentSearchHistory();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden search");
        String eventName = "v2 open garden search";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_search";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    public void loadData() {
// saveRecentSearchHistory(searchStr);
        if (HttpConnectUtil.isNetworkAvailable(WeiboSearch.this)) {
            SearchTask task = new SearchTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        // TODO Auto-generated method stub
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
