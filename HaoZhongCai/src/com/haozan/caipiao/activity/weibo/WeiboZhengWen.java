package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class WeiboZhengWen
    extends BasicActivity
    implements OnClickListener {

    private TextView titleTextView;
    private Button deleteWeibo;
    private Button conmentButton01;
    private Button forwardButton;
    private Button conmentButton;
    private Button shouCangButton;
    private Button qixiaoshouCangButton;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private int conmentCount;
    private int islike;

    private TextView niceName;
    private TextView time;
    private TextView content;
    private ImageView avatar;
    private LinearLayout avatarLy;
    private TextView newsTitle;
    private LinearLayout newsTitleLy;
    private TextView weiboFrom;
    private ImageView imageVerticalLine;

    private String weiboId;
    private CustomDialog loginAgainDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weibo_zhengwen);
        setupViews();
    }

    @Override
    protected void onResume() {
        if (HttpConnectUtil.isNetworkAvailable(WeiboZhengWen.this)) {
            GetDataTask weibotask = new GetDataTask();
            weibotask.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
        super.onResume();
    }

    public void setupViews() {
        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        titleTextView.setText(R.string.weibozhengwen);
        conmentButton01 = (Button) findViewById(R.id.title_btinit_right);
        conmentButton01.setText("写评论");
        conmentButton01.setOnClickListener(this);

        deleteWeibo = (Button) findViewById(R.id.title_btinit_left);
        imageVerticalLine = (ImageView) findViewById(R.id.vertical_line);

        forwardButton = (Button) findViewById(R.id.Forward);
        forwardButton.setOnClickListener(this);
        conmentButton = (Button) findViewById(R.id.Conment);
        conmentButton.setOnClickListener(this);
        shouCangButton = (Button) findViewById(R.id.shouCang);
        shouCangButton.setOnClickListener(this);
        qixiaoshouCangButton = (Button) findViewById(R.id.quxiaoshouCang);
        qixiaoshouCangButton.setOnClickListener(this);
        weiboFrom = (TextView) findViewById(R.id.weibo_from);

        niceName = (TextView) findViewById(R.id.niceName);
        time = (TextView) findViewById(R.id.tvItemDate);
        content = (TextView) findViewById(R.id.tvItemContent);
        avatar = (ImageView) findViewById(R.id.ivItemPortrait);
        avatarLy = (LinearLayout) findViewById(R.id.detail_userinfo);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitleLy = (LinearLayout) findViewById(R.id.newsTitleLy);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // 接收传过来的weiboId
        weiboId = this.getIntent().getExtras().getString("weiboId");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(WeiboZhengWen.this, NewConmentsActivity.class);
            Bundle b = new Bundle();
            b.putString("weiboId", weiboId);
            intent.putExtras(b);
            WeiboZhengWen.this.startActivity(intent);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboZhengWen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                   R.anim.push_to_right_out);
            }
        }
        else if (v.getId() == R.id.title_btinit_left) {
            if (HttpConnectUtil.isNetworkAvailable(WeiboZhengWen.this)) {
                DeleteWeiboTask delete = new DeleteWeiboTask();
                delete.execute();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.Conment) {
            if (HttpConnectUtil.isNetworkAvailable(WeiboZhengWen.this)) {
                if (conmentCount == 0) {
                    conmentButton.setEnabled(false);
                    ViewUtil.showTipsToast(this, "暂无评论");
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(WeiboZhengWen.this, ConmentListActivity.class);
                    Bundle b = new Bundle();
                    // 把动态对象传到NewConmentsActivity
                    b.putString("weiboId", weiboId);
                    intent.putExtras(b);
                    WeiboZhengWen.this.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(WeiboZhengWen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                    }
                }
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.Forward) {
            Intent intent = new Intent();
            intent.setClass(WeiboZhengWen.this, ForwardActivity.class);
            Bundle b = new Bundle();
            b.putString("weiboId", weiboId);
            intent.putExtras(b);
            // WeiboZhengWen.this.startActivity(intent);
            WeiboZhengWen.this.startActivityForResult(intent, 0);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboZhengWen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                   R.anim.push_to_right_out);
            }
        }
        else if (v.getId() == R.id.shouCang) {
            if (HttpConnectUtil.isNetworkAvailable(WeiboZhengWen.this)) {
                progressBar.setVisibility(View.VISIBLE);
                AddShouCangTask task = new AddShouCangTask();
                task.execute();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.quxiaoshouCang) {
            if (HttpConnectUtil.isNetworkAvailable(WeiboZhengWen.this)) {
                progressBar.setVisibility(View.VISIBLE);
                DeleteFavoriteTask task = new DeleteFavoriteTask();
                task.execute();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
    }

    class AddShouCangTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004070");
            parameter.put("pid", LotteryUtils.getPid(WeiboZhengWen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("like", "yes");
            parameter.put("like_id", weiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(WeiboZhengWen.this);
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
        protected void onPostExecute(String result) {
            String json = (String) result;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(WeiboZhengWen.this, "收藏成功");
                    qixiaoshouCangButton.setVisibility(View.VISIBLE);
                    shouCangButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(WeiboZhengWen.this, "已经收藏过该动态");
                    progressBar.setVisibility(View.GONE);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    ViewUtil.showTipsToast(WeiboZhengWen.this, "收藏失败");
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class DeleteFavoriteTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004070");
            parameter.put("pid", LotteryUtils.getPid(WeiboZhengWen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("like", "no");
            parameter.put("like_id", weiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(WeiboZhengWen.this);
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
        protected void onPostExecute(String result) {
            String json = (String) result;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(WeiboZhengWen.this, "取消收藏成功");
                    qixiaoshouCangButton.setVisibility(View.GONE);
                    shouCangButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(WeiboZhengWen.this, "取消收藏失败");
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                ViewUtil.showTipsToast(WeiboZhengWen.this, "取消收藏失败");
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class GetDataTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004110");
            parameter.put("pid", LotteryUtils.getPid(WeiboZhengWen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("weibo_id", weiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(WeiboZhengWen.this);
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
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    progressBar.setVisibility(View.GONE);
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {

                    }
                    else {
                        try {
                            JSONArray hallArray = new JSONArray(response_data);
                            JSONObject jo = hallArray.getJSONObject(0);
                            final String title = jo.getString("title");
                            niceName.setText(jo.getString("nickname"));
                            time.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(jo.getString("issue_date"),
                                                                                   "yyyy-MM-dd HH:mm:ss")));
                            final String uiserId = jo.getString("user_id");

                            // GetPicTask task = new GetPicTask();
                            // task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?"
// +
                            // "&phone=" + appState.getUsername() + "&user_id=" + jo.getString("user_id") +
                            // "&service=2004100");

                            // 第一个空格替换为“+”号
                            if (title.equals("dfljy")) {
                                String money = jo.getString("content");
                                String money1 = money.replaceFirst(" ", "+");
                                content.setText(money1);
                            }
                            else {
                                content.setText(TextUtil.formatContent(jo.getString("content"),
                                                                       WeiboZhengWen.this));
// BasicWeibo.contextLink(content);//昵称跳转
                            }

                            forwardButton.setText("转发:" + "(" + jo.getString("retweet_count") + ")");
                            conmentButton.setText("评论:" + "(" + jo.getString("reply_count") + ")");

                            // 如果是自己则显示删除按钮
                            if (appState.getUserid() == uiserId || appState.getUserid().equals(uiserId)) {
                                deleteWeibo.setVisibility(View.VISIBLE);
                                imageVerticalLine.setVisibility(View.VISIBLE);
                                deleteWeibo.setText("删 除");
                                deleteWeibo.setOnClickListener(WeiboZhengWen.this);
                            }
                            else {
                                deleteWeibo.setVisibility(View.GONE);
                            }

                            avatarLy.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    if (appState.getUserid().equals(uiserId) ||
                                        appState.getUserid() == uiserId) {
                                        intent.setClass(WeiboZhengWen.this, MyProfileActivity.class);
                                    }
                                    else {
                                        intent.setClass(WeiboZhengWen.this, UserProfileActivity.class);
                                        Bundle b = new Bundle();
                                        b.putInt("userId", Integer.valueOf(uiserId));
                                        intent.putExtras(b);
                                    }
                                    WeiboZhengWen.this.startActivity(intent);
                                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                                        (new AnimationModel(WeiboZhengWen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                           R.anim.push_to_right_out);
                                    }
                                }
                            });

                            // /////////
                            final String preview = jo.getString("preview");
                            int type = Integer.valueOf(jo.getString("type"));
                            String version = jo.getString("source");
                            final String newsId = jo.getString("attach_id");
                            // 子内容显示
                            BasicWeibo.subContentShow(mContext, newsTitle, type, title, preview, newsTitleLy,
                                                      content, jo.getString("content"));
                            // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
                            BasicWeibo.weiboFrom(WeiboZhengWen.this, type, version, title, weiboFrom,
                                                 newsTitle, newsId, preview);

                            conmentCount = Integer.valueOf(jo.getString("reply_count"));
                            if (conmentCount == 0) {
                                conmentButton.setEnabled(false);
                            }
                            else {
                                conmentButton.setEnabled(true);
                            }

                            islike = jo.getInt("is_like");
                            if (islike == 0) {
                                shouCangButton.setVisibility(View.VISIBLE);
                                qixiaoshouCangButton.setVisibility(View.GONE);
                            }
                            else if (islike == 1) {
                                shouCangButton.setVisibility(View.GONE);
                                qixiaoshouCangButton.setVisibility(View.VISIBLE);
                            }
                        }

                        catch (JSONException e) {
                            ViewUtil.showTipsToast(WeiboZhengWen.this, "网络数据有误");
                            finish();
                        }
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }

    // 获取图片线程
    class GetPicTask
        extends AsyncTask<String, Long, byte[]> {

        @Override
        protected void onPostExecute(byte[] data) {
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                if (bitmap != null) {
                    avatar.setImageBitmap(bitmap);// display image
                }
                else {
                    avatar.setImageResource(R.drawable.lucky_cat);
                }
            }
            else {
                ViewUtil.showTipsToast(WeiboZhengWen.this, "头像获取失败");
            }

        }

        // 任务被执行之后
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

    private class DeleteWeiboTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004080");
            parameter.put("pid", LotteryUtils.getPid(WeiboZhengWen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("content_id", weiboId);
            parameter.put("type", "2");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(WeiboZhengWen.this);
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
        protected void onPostExecute(String result) {
            String json = (String) result;
            if (json == null) {
                ViewUtil.showTipsToast(WeiboZhengWen.this, "删除失败");
            }
            else {
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    if (status.equals("200")) {
                        progressBar.setVisibility(View.GONE);
                        ViewUtil.showTipsToast(WeiboZhengWen.this, "删除成功");
                        WeiboZhengWen.this.finish();

                    }
                    else if (status.equals("302")) {
                        OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                    }
                    else if (status.equals("304")) {
                        OperateInfUtils.clearSessionId(WeiboZhengWen.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_again));
                    }
                    else {
                        ViewUtil.showTipsToast(WeiboZhengWen.this, "删除失败");
                    }
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden event content");
        String eventName = "v2 open garden event content";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_event_content";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WeiboZhengWen.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboZhengWen.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                   R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
