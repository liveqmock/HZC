package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.jgravatar.Gravatar;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.types.FocusListData;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.umeng.analytics.MobclickAgent;

public class WeiboGeneral
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnHeaderRefreshListener, OnLastItemVisibleListener {
    protected final static int SIZE = 10;
    protected int page = 1;

// FocusAdapter adapter;
    protected PullToRefreshListView pulllistView;
    // 下拉刷新
    protected RelativeLayout titleLy;
    protected TextView titleTextView;
    protected RelativeLayout titleRightButton;
    protected ImageButton toTopButton;
// protected ArrayList<FocusListData> focusArray;

    protected CustomDialog loginAgainDialog;
    public boolean ifClearArray = false;
    ListView actualListView;
    View footView;
    boolean ifGetMoreData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weibo_general);
        setupViews();
        init();
    }

    public void setupViews() {
        titleLy = (RelativeLayout) findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        titleRightButton = (RelativeLayout) findViewById(R.id.rela_title_right_button);
        titleRightButton.setVisibility(View.GONE);
        toTopButton = (ImageButton) findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);
        pulllistView = (PullToRefreshListView) findViewById(R.id.weibo_general_list);
        pulllistView.setOnHeaderRefreshListener(this);
        pulllistView.setOnLastItemVisibleListener(this);
        actualListView = pulllistView.getRefreshableView();
        footView = View.inflate(WeiboGeneral.this, R.layout.list_item_load_more_view, null);
    }

    public void init() {
        // 返回顶部按钮显示
// BasicWeibo.backTopShow(generalList, toTopButton);
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

    // 获取图片
    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(WeiboGeneral.this);
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
        private FocusListData fansdata;

        public GetPicTask(FocusListData fansdata) {
            this.fansdata = fansdata;

        }

        @Override
        protected void onPostExecute(byte[] data) {
            if (data != null) {
                fansdata.setBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));// bitmap
            }
            else {
                ViewUtil.showTipsToast(WeiboGeneral.this, "头像获取失败");
            }
        }

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
            HttpGet get = new HttpGet(url);
            // HttpGet get =
            // new HttpGet(LotteryConfig.serverHTTP + "BuKeServ/" + params[0] +
            // ";jsessionid=" + sessionId +
            // "?phone=" + phoneNum);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WeiboGeneral.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboGeneral.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                  R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadData() {
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
