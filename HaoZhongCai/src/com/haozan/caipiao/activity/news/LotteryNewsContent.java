package com.haozan.caipiao.activity.news;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class LotteryNewsContent
    extends BasicActivity
    implements OnClickListener {
    private static final String RESPONSE = "response_data";
    private TextView newsTitle;
    private TextView newsContent;
    private TextView newsIssueDate;
    private TextView newsTop;
    private TextView newsDown;
    private TextView message;

    private TextView lotteryNewsSources;
// private TextView checkNewsComment;
    private TextView topTitle;
    private ImageView top_comment_line;
//    private ImageView bottom_comment_line;
    private LinearLayout layout;

    private int news_id;
    private int ding = 0;
    private int like = 0;
    private int dislike = 0;
    private String originalLink = null;
    private String title;
    private String phone_num;
    private ProgressBar progressBar;
    private TextView postLotteryComment;

// private TextView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lotterynewscontent);
        setUpView();
        init();
    }

    private void init() {
        news_id = this.getIntent().getExtras().getInt("news_id");
        title = this.getIntent().getExtras().getString("title");
        topTitle.setText(title);
        if (HttpConnectUtil.isNetworkAvailable(LotteryNewsContent.this)) {
            LotteryNewsContentTask lnct = new LotteryNewsContentTask();
            lnct.execute(1);
        }
        else {
            layout.setVisibility(View.GONE);
            String inf = getResources().getString(R.string.network_not_avaliable);
            message.setVisibility(View.VISIBLE);
            message.setText(inf);
        }
    }

    private void setUpView() {
        layout = (LinearLayout) findViewById(R.id.LinearLayout01);
        topTitle = (TextView) findViewById(R.id.title);
        newsTitle = (TextView) findViewById(R.id.lottery_news_title);
        newsContent = (TextView) findViewById(R.id.lottery_news_content);
        newsIssueDate = (TextView) findViewById(R.id.lottery_news_issue_date);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        message = (TextView) findViewById(R.id.message);
// newsTop = (TextView) findViewById(R.id.top_news);
// newsTop.setOnClickListener(this);
// newsDown = (TextView) findViewById(R.id.down_news);
        newsDown.setOnClickListener(this);
// checkNewsComment = (TextView) findViewById(R.id.news_comment_link);
// checkNewsComment.setOnClickListener(this);
        postLotteryComment = (TextView) findViewById(R.id.post_lottery_newss_comment);
// postLotteryComment.setOnClickListener(this);
// lotteryNewsSources = (TextView) findViewById(R.id.lottery_news_sources);
        top_comment_line = (ImageView) findViewById(R.id.top_comment_bk);
//        bottom_comment_line = (ImageView) findViewById(R.id.bottom_comment_bk);
    }

    class LotteryNewsContentTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "show_article");
            parameter.put("pid", LotteryUtils.getPid(LotteryNewsContent.this));
            parameter.put("news_id", "" + news_id);
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(LotteryNewsContent.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, false, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            String inf;
            String json = (String) result;
            if (result == null) {
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, RESPONSE);
                    try {
                        JSONArray hallArray = new JSONArray(data);
                        JSONObject jo = hallArray.getJSONObject(0);
                        newsTitle.setText(Html.fromHtml(jo.getString("title")));
                        newsContent.setText(Html.fromHtml(jo.getString("content")));
                        newsIssueDate.setText(Html.fromHtml(jo.getString("issue_date")));
                        originalLink = "";
                        lotteryNewsSources.setText(Html.fromHtml("<a href=" + originalLink + ">" + "原文" +
                            "</a>"));
                        like = jo.getInt("good");
                        dislike = jo.getInt("bad");
                        newsTop.setText("顶：[" + String.valueOf(like) + "]");
                        newsDown.setText("踩：[" + String.valueOf(dislike) + "]");
                        top_comment_line.setVisibility(View.VISIBLE);
//                        bottom_comment_line.setVisibility(View.VISIBLE);
                        postLotteryComment.setVisibility(View.VISIBLE);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // newsTitle.setText(d)
// analyse.getNewsListItemArray(newsListItemRecord, data);
                    progressBar.setVisibility(View.GONE);
                }
                else if (status.equals("302")) {
                    inf = getResources().getString(R.string.login_timeout);
// message.setText(inf);
                }
                else if (status.equals("304")) {
                    inf = getResources().getString(R.string.login_again);
// message.setText(inf);
                }
                else {
                    inf = "查询失败";
// message.setText(inf);
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
    public void onClick(View v) {
// if (v.getId() == R.id.top_news) {
// ding = 1;
// if (appState.getSessionid() != null) {
// LotteryNewsPostTask lnpt = new LotteryNewsPostTask();
// lnpt.execute(1);
// }
// else {
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putString("forwardFlag", "顶");
// intent.putExtras(bundle);
// intent.setClass(LotteryNewsContent.this, Login.class);
// startActivity(intent);
// }
// }
// else if (v.getId() == R.id.down_news) {
// ding = -1;
// if (appState.getSessionid() != null) {
// LotteryNewsPostTask lnpt = new LotteryNewsPostTask();
// lnpt.execute(1);
// }
// else {
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putString("forwardFlag", "踩");
// bundle.putBoolean("ifStartSelf", false);
// intent.putExtras(bundle);
// intent.setClass(LotteryNewsContent.this, Login.class);
        // intent.setClass(LotteryNewsContent.this, StartUp.class);
// startActivity(intent);
// }
// newsDown.setText("踩：[" + String.valueOf(dislike) + "]");
// }
// else if (v.getId() == R.id.news_comment_link) {
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putInt("news_id", news_id);
// bundle.putString("title", topTitle.getText().toString());
// intent.putExtras(bundle);
// intent.setClass(LotteryNewsContent.this, NewsCommentList.class);
// startActivity(intent);
// }
// else if (v.getId() == R.id.post_lottery_newss_comment) {
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putInt("news_id", news_id);
// bundle.putString("from", "content");
// intent.putExtras(bundle);
// intent.setClass(LotteryNewsContent.this, LotteryNewsCommentPost.class);
// startActivity(intent);
// }

    }

    class LotteryNewsPostTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "like_one");
            parameter.put("pid", LotteryUtils.getPid(LotteryNewsContent.this));
            parameter.put("like_id", "" + news_id);
            parameter.put("like", "" + ding);
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone_num));
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(LotteryNewsContent.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, false, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            String inf;
            String json = (String) result;
            if (result == null) {
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    progressBar.setVisibility(View.GONE);
                    if (ding == 1) {
                        newsTop.setText("顶：[" + String.valueOf(like + 1) + "]");
                    }
                    else if (ding == -1) {
                        newsDown.setText("踩：[" + String.valueOf(dislike + 1) + "]");
                    }
                    ViewUtil.showTipsToast(LotteryNewsContent.this, "成功");
                }
                else if (status.equals("302")) {
                    inf = getResources().getString(R.string.login_timeout);
// message.setText(inf);
                }
                else if (status.equals("304")) {
                    inf = getResources().getString(R.string.login_again);
// message.setText(inf);
                }
                else {
// inf = getResources().getString(R.string.search_fail);
// message.setText(inf);
                    ViewUtil.showTipsToast(LotteryNewsContent.this, analyse.getData(json, "error_desc"));
                }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appState.getSessionid() != null)
            phone_num = appState.getUsername();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open news content");
        String eventName = "v2 open news content";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_news_content";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LotteryNewsContent.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(LotteryNewsContent.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                        R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
