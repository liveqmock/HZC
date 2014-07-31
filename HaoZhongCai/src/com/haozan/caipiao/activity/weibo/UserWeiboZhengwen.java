package com.haozan.caipiao.activity.weibo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import com.haozan.caipiao.types.MyWeiboData;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.umeng.analytics.MobclickAgent;

public class UserWeiboZhengwen
    extends BasicActivity {

    private TextView titleTextView;
    private Button forwardButton;
    private Button conmentButton;
    private Button shouCangButton;
    private Button qixiaoshouCangButton;
    private Button conmentButton1;
    private int conmentConut;
    private int islike;

    private TextView niceName;
    private TextView time;
    private TextView content;
    private ImageView avatar;
    private TextView newsTitle;
    private LinearLayout newsTitleLy;
    private TextView weiboFrom;
    private ProgressBar progressBar;
    WeiboData wb;
    private int getDataType;
    private int dataType;
    MyWeiboData userWeiboData;

    private String weiboId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.userweibo_zhengwen);
        setupViews();
        init();
    }

    @Override
    protected void onResume() {
        if (HttpConnectUtil.isNetworkAvailable(UserWeiboZhengwen.this)) {
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

        niceName = (TextView) findViewById(R.id.niceName);
        time = (TextView) findViewById(R.id.tvItemDate);
        content = (TextView) findViewById(R.id.tvItemContent);
        avatar = (ImageView) findViewById(R.id.ivItemPortrait);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitleLy = (LinearLayout) findViewById(R.id.newsTitleLy);
        forwardButton = (Button) findViewById(R.id.Forward);
        conmentButton = (Button) findViewById(R.id.Conment);
        conmentButton1 = (Button) findViewById(R.id.title_btinit_right);
        conmentButton1.setText("   写评论   ");
        shouCangButton = (Button) findViewById(R.id.shouCang);
        qixiaoshouCangButton = (Button) findViewById(R.id.quxiaoshouCang);
        weiboFrom = (TextView) findViewById(R.id.weibo_from);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // 接收传过来的动态对象
        weiboId = this.getIntent().getExtras().getString("weiboId");
        wb = (WeiboData) this.getIntent().getExtras().getSerializable("WbData");
        userWeiboData = (MyWeiboData) this.getIntent().getExtras().getSerializable("userWeiboData");
        // data_type 3表示用户动态列表
        getDataType = this.getIntent().getExtras().getInt("data_type");
        dataType = getDataType;

        final String title = userWeiboData.getTitle();
        niceName.setText(userWeiboData.getName());
        time.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(userWeiboData.getTime(), "yyyy-MM-dd HH:mm:ss")));

        if (title.equals("dfljy")) {
            String money = userWeiboData.getContent();
            String money1 = money.replaceFirst(" ", "+");
            content.setText(money1);
        }
        else {
            content.setText(TextUtil.formatContent(userWeiboData.getContent(), UserWeiboZhengwen.this));
        }

        forwardButton.setText("转发:" + "(" + userWeiboData.getRetweetCount() + ")");
        conmentButton.setText("评论:" + "(" + userWeiboData.getReplyCount() + ")");
        // 设置头像
// if (userWeiboData.getBitmap() != null) {
//
// avatar.setImageBitmap(userWeiboData.getBitmap());
// }
// else {
// avatar.setImageResource(R.drawable.lucky_cat);
// }

        String preview = userWeiboData.getPreview();
        int type = userWeiboData.getType();
        String version = userWeiboData.getSource();

        // 子内容显示
        BasicWeibo.subContentShow(mContext, newsTitle, type, title, preview, newsTitleLy, content,
                                  userWeiboData.getContent());

        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(UserWeiboZhengwen.this, type, version, title, weiboFrom, newsTitle,
                             userWeiboData.getAttachid(), preview);

        conmentButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserWeiboZhengwen.this, NewConmentsActivity.class);
                Bundle b = new Bundle();
                b.putString("weiboId", weiboId);
                intent.putExtras(b);
                UserWeiboZhengwen.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(UserWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                }
            }
        });

        conmentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(UserWeiboZhengwen.this)) {
                    if (conmentConut == 0) {
                        ViewUtil.showTipsToast(UserWeiboZhengwen.this, "暂无评论");
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(UserWeiboZhengwen.this, ConmentListActivity.class);
                        Bundle b = new Bundle();
                        b.putString("weiboId", weiboId);
                        intent.putExtras(b);
                        UserWeiboZhengwen.this.startActivity(intent);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel(UserWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                   R.anim.push_to_right_out);
                        }
                    }
                }
                else {
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, noNetTips);
                }
            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserWeiboZhengwen.this, ForwardActivity.class);
                Bundle b = new Bundle();
                b.putString("weiboId", weiboId);
                intent.putExtras(b);
                UserWeiboZhengwen.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(UserWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                }
            }
        });

        shouCangButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(UserWeiboZhengwen.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    AddShouCangTask task = new AddShouCangTask();
                    task.execute();
                }
                else {
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, noNetTips);
                }
            }
        });

        qixiaoshouCangButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(UserWeiboZhengwen.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    DeleteFavoriteTask task = new DeleteFavoriteTask();
                    task.execute();
                }
                else {
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, noNetTips);
                }
            }
        });

    }

    public void init() {
// if (HttpConnectUtil.isNetworkAvailable(UserWeiboZhengwen.this)) {
// GetPicTask getPic = new GetPicTask();
// getPic.execute();
// } else {
// ViewUtil.showTipsToast(this,noNetTips);
// }
    }

    class AddShouCangTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004070");
            parameter.put("pid", LotteryUtils.getPid(UserWeiboZhengwen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("like", "yes");
            parameter.put("like_id", "" + weiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserWeiboZhengwen.this);
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
                    progressBar.setVisibility(View.GONE);
                    qixiaoshouCangButton.setVisibility(View.VISIBLE);
                    shouCangButton.setVisibility(View.GONE);
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, "收藏成功");
                }
                else if (status.equals("300")) {
                    progressBar.setVisibility(View.GONE);
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, "已经收藏过该动态");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, "收藏失败");
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
            parameter.put("pid", LotteryUtils.getPid(UserWeiboZhengwen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("like", "no");
            if (dataType == 3) {
// parameter.put("&like_id=", userWeiboData.getId());
                parameter.put("like_id", weiboId);
            }
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserWeiboZhengwen.this);
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
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, "取消收藏成功");
                    qixiaoshouCangButton.setVisibility(View.GONE);
                    shouCangButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(UserWeiboZhengwen.this, "取消收藏失败");
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                ViewUtil.showTipsToast(UserWeiboZhengwen.this, "取消收藏失败");
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
            parameter.put("pid", LotteryUtils.getPid(UserWeiboZhengwen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            if (dataType == 3) {
                parameter.put("weibo_id", userWeiboData.getId());
            }
            else {
                parameter.put("weibo_id", "" + wb.getId());
            }
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserWeiboZhengwen.this);
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
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
// String inf = "已无更多数据";
                    }
                    else {
                        try {
                            JSONArray hallArray = new JSONArray(response_data);
                            int length = hallArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject jo = hallArray.getJSONObject(i);
                                forwardButton.setText("转发:" + "(" + jo.getString("retweet_count") + ")");
                                conmentButton.setText("评论:" + "(" + jo.getString("reply_count") + ")");
                                conmentConut = Integer.valueOf(jo.getString("reply_count"));
                                if (conmentConut == 0) {
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
                        }
                        catch (JSONException e) {
                            ViewUtil.showTipsToast(UserWeiboZhengwen.this, "网络数据有误");
                            finish();
                            e.printStackTrace();
                        }
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden user event content");
        String eventName = "v2 open garden user event content";
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
            UserWeiboZhengwen.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(UserWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                       R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
