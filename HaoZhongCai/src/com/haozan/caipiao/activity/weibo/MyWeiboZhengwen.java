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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyWeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class MyWeiboZhengwen
    extends ContainTipsPageBasicActivity {

    private TextView titleTextView;
    private Button deleteButton;
    private RelativeLayout delegeButtonRela;
    private Button conmentButton1;
    private Button forwardButton;
    private Button conmentButton;
    private Button favoriteButton;

    private TextView niceName;
    private TextView time;
    private TextView content;
    private ImageView avatar;
    private TextView newsTitle;
    private LinearLayout newsTitleLy;
    private TextView weiboFrom;
    private int conmentConut;
    private ImageView imageVerticalLine;

    MyWeiboData wb;
    private CustomDialog loginAgainDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.myweibo_zhengwen);
        setupViews();
        initData();
        init();
    }

    @Override
    protected void onResume() {
        if (HttpConnectUtil.isNetworkAvailable(MyWeiboZhengwen.this)) {
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
        conmentButton1 = (Button) findViewById(R.id.title_btinit_right);
        deleteButton = (Button) findViewById(R.id.title_btinit_left);
        delegeButtonRela = (RelativeLayout) findViewById(R.id.title_btinit_left_rela);
        delegeButtonRela.setVisibility(View.VISIBLE);
        imageVerticalLine = (ImageView) findViewById(R.id.vertical_line);

        titleTextView.setText("动态正文");
        deleteButton.setText("删除");
        deleteButton.setVisibility(View.VISIBLE);
        imageVerticalLine.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(MyWeiboZhengwen.this)) {
                    DeleteWeiboTask delete = new DeleteWeiboTask();
                    delete.execute();
                }
                else {
                    ViewUtil.showTipsToast(MyWeiboZhengwen.this, noNetTips);
                }
            }
        });
        conmentButton1.setText("写评论");
        conmentButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyWeiboZhengwen.this, NewConmentsActivity.class);
                Bundle b = new Bundle();
                b.putString("weiboId", wb.getId());
                intent.putExtras(b);
                MyWeiboZhengwen.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(MyWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
        });

        niceName = (TextView) findViewById(R.id.niceName);
        time = (TextView) findViewById(R.id.tvItemDate);
        content = (TextView) findViewById(R.id.tvItemContent);
        avatar = (ImageView) findViewById(R.id.ivItemPortrait);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitleLy = (LinearLayout) findViewById(R.id.newsTitleLy);
        forwardButton = (Button) findViewById(R.id.Forward);
        conmentButton = (Button) findViewById(R.id.Conment);
        favoriteButton = (Button) findViewById(R.id.shouCang);
        favoriteButton.setVisibility(View.GONE);
        weiboFrom = (TextView) findViewById(R.id.weibo_from);

        // 接收传过来的动态对象
        wb = (MyWeiboData) this.getIntent().getExtras().getSerializable("weibodata");
        String version = wb.getSource();
        final String title = wb.getTitle();
        niceName.setText(wb.getName());
        time.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(wb.getTime(), "yyyy-MM-dd HH:mm:ss")));

        if (title.equals("dfljy")) {
            String money = wb.getContent();
            String money1 = money.replaceFirst(" ", "+");
            content.setText(money1);
        }
        else {
            content.setText(TextUtil.formatContent(wb.getContent(), MyWeiboZhengwen.this));
        }

        forwardButton.setText("转发:" + "(" + wb.getRetweetCount() + ")");
        conmentButton.setText("评论:" + "(" + wb.getReplyCount() + ")");
// conmentConut=Integer.valueOf(wb.getReplyCount());

        String preview = wb.getPreview();
        int type = wb.getType();
        // 子内容显示
        BasicWeibo.subContentShow(mContext, newsTitle, type, title, preview, newsTitleLy, content,
                                  wb.getContent());

        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(MyWeiboZhengwen.this, type, version, title, weiboFrom, newsTitle,
                             wb.getAttachid(), preview);

        conmentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(MyWeiboZhengwen.this)) {
                    if (conmentConut == 0) {
                        ViewUtil.showTipsToast(MyWeiboZhengwen.this, "暂无评论");
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(MyWeiboZhengwen.this, ConmentListActivity.class);
                        Bundle b = new Bundle();
                        b.putString("weiboId", wb.getId());
                        intent.putExtras(b);
                        MyWeiboZhengwen.this.startActivity(intent);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel(MyWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                 R.anim.push_to_right_out);
                        }
                    }
                }
                else {
                    ViewUtil.showTipsToast(MyWeiboZhengwen.this, noNetTips);
                }
            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyWeiboZhengwen.this, ForwardActivity.class);
                Bundle b = new Bundle();
                b.putString("weiboId", wb.getId());
                intent.putExtras(b);
                MyWeiboZhengwen.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(MyWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
        });
    }

    public void initData() {

    }

    public void init() {
    }

    private class DeleteWeiboTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004080");
            parameter.put("pid", LotteryUtils.getPid(MyWeiboZhengwen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "2");
            parameter.put("content_id", wb.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyWeiboZhengwen.this);
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
            dismissProgress();
            String json = (String) result;
            if (json == null) {
                ViewUtil.showTipsToast(MyWeiboZhengwen.this, "删除失败");
            }
            else {
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    if (status.equals("200")) {
                        ViewUtil.showTipsToast(MyWeiboZhengwen.this, "删除成功");
                        Intent intent = new Intent();
                        intent.setClass(MyWeiboZhengwen.this, MyWeiboActivity.class);
                        setResult(RESULT_OK, intent); // 这里有2个参数(int resultCode,
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel(MyWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                 R.anim.push_to_right_out);
                        }
                        // Intent intent)
                        MyWeiboZhengwen.this.finish();

                    }
                    else if (status.equals("302")) {
                        OperateInfUtils.clearSessionId(MyWeiboZhengwen.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                    }
                    else if (status.equals("304")) {
                        OperateInfUtils.clearSessionId(MyWeiboZhengwen.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_again));
                    }
                    else {
                        ViewUtil.showTipsToast(MyWeiboZhengwen.this, "删除失败");
                    }
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    class GetDataTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004110");
            parameter.put("pid", LotteryUtils.getPid(MyWeiboZhengwen.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("weibo_id", wb.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyWeiboZhengwen.this);
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
            dismissProgress();
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
                        String inf = "已无更多数据";
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
                            }
                        }
                        catch (JSONException e) {
                            ViewUtil.showTipsToast(MyWeiboZhengwen.this, "网络数据有误");
                            e.printStackTrace();
                        }
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(MyWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyWeiboZhengwen.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal event content");
        String eventName = "v2 open garden personal event content";
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
            MyWeiboZhengwen.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(MyWeiboZhengwen.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
