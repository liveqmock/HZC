package com.haozan.caipiao.activity.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.adapter.FaceAdapter;
import com.haozan.caipiao.adapter.LotteryNewsCommentAdapter;
import com.haozan.caipiao.adapter.LotteryNewsCommentAdapter.OnReplyClickListener;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.NewsCommentPostTask;
import com.haozan.caipiao.task.NewsCommentPostTask.OnStatusChangedExtraListener;
import com.haozan.caipiao.types.NewsCommentItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.weiboutil.Face;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.umeng.analytics.MobclickAgent;

public class NewsCommentList
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnStatusChangedExtraListener, OnFocusChangeListener, OnReplyClickListener,
    LoadDataListener {
    private static final String RESPONSE = "response_data";
    private static final int LOADAGAIN = 0;
    private static final int FIRSTLOAD = 1;
    private static final int POSTCOMMENT = 2;
    private static final String GETFIRSTPLACE = "还没有评论哦，抢沙发!";
    private static final String GETSECONDPLACE = "来晚一步，抢个板凳吧!";
    private static final String GETTHIRDPLACE = "留点足迹，沾点财气...";
    private static final String HOTPLACES = "财气火爆,给点评论凑凑热闹!";

    private AutoLoadListView listView;
    // 下拉刷新
    private TextView topTitle;
    private ArrayList<NewsCommentItem> newsCommentItemRecord;
// private ProgressBar progressBar;
    private LotteryNewsCommentAdapter lncAdapter;
    private int newsId;
    private int pageNo = 1;
    private int itemPosition;
    private int like;
    private int dislike;
    private String title;
    private String commet_content;
    private TextView popupCommentTop;
    private TextView popupCommentDown;
    private TextView popupComment;
    private ArrayList<Integer> limitNumD;
    private ArrayList<Integer> limitNumC;
    private int firstListAccountD = 0;
    private int firstListAccountC = 0;

    private TextView newsTitle;
    private TextView newsContent;
    private TextView newsIssueDate;
    private EditText comment_content;
    private Button comment_post;
    private Button face;
    private GridView faceGrid;
// private LinearLayout news_bottom_default;
    private LinearLayout news_content_bottom;
    private TextView tv_news_bottom;

    private ImageView top_comment_line;
// private ImageView bottom_comment_line;

    private int news_id;
    private String phone_num;
    private View view;
    private TextView postLotteryComment;
    private boolean isReply = false;
    private TextWatcher mTextWatcher;
    private String commentNewsId;
    private String currentCommentNewsId;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADAGAIN:
                    listView.addLoadingFootView();
                    pageNo = 1;
                    newsCommentItemRecord.clear();
                    loadData();
                    break;
                case FIRSTLOAD:
                    loadData();
                    break;
                case POSTCOMMENT:
                    commentPost();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_comment_list);
        setUpView();
        init();
    }

    private void init() {
        commentNewsId = preferences.getString("news_comment_id", "-1");
        limitNumD = new ArrayList<Integer>();
        limitNumC = new ArrayList<Integer>();
        newsId = this.getIntent().getExtras().getInt("news_id");
        title = this.getIntent().getExtras().getString("title");
        topTitle.setText(" 评  论 ");
        newsCommentItemRecord = new ArrayList<NewsCommentItem>();
        listView.addHeaderView(view);
        lncAdapter = new LotteryNewsCommentAdapter(NewsCommentList.this, newsCommentItemRecord, comment_num);
        lncAdapter.setClickListener(this);
        listView.setAdapter(lncAdapter);

        newsContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                comment_content.setCursorVisible(false);
                comment_content.clearFocus();
                final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                if (faceGrid.isShown())
                    faceGrid.setVisibility(View.GONE);
                return false;
            }
        });
        newsTitle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                comment_content.setCursorVisible(false);
                comment_content.clearFocus();
                final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                if (faceGrid.isShown())
                    faceGrid.setVisibility(View.GONE);
                return false;
            }
        });
        initHeaderView();
    }

    private void initHeaderView() {
        news_id = this.getIntent().getExtras().getInt("news_id");
        topTitle.setText(title);
        if (HttpConnectUtil.isNetworkAvailable(NewsCommentList.this)) {
            LotteryNewsContentTask lnct = new LotteryNewsContentTask();
            lnct.execute(1);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }

    }

    private void activeUpHeadView() {
        LayoutInflater inflater = LayoutInflater.from(NewsCommentList.this);
        view = inflater.inflate(R.layout.lotterynewscontent, null);
        view.setVisibility(View.GONE);
        faceGrid = (GridView) findViewById(R.id.updater_faceGrid);
        faceGrid.setOnItemClickListener(itemClickListener);

        face = (Button) findViewById(R.id.face_button);
        face.setOnClickListener(this);
        comment_post = (Button) findViewById(R.id.btn_comment_post);
        comment_post.setOnClickListener(this);
        comment_content = (EditText) findViewById(R.id.input_comment);
        comment_content.setOnClickListener(this);
        comment_content.setCursorVisible(false);
        comment_content.setFocusable(false);
        comment_content.setFocusableInTouchMode(true);
        comment_content.setOnFocusChangeListener(this);

        newsTitle = (TextView) view.findViewById(R.id.lottery_news_title);
        newsContent = (TextView) view.findViewById(R.id.lottery_news_content);
        newsIssueDate = (TextView) view.findViewById(R.id.lottery_news_issue_date);
        postLotteryComment = (TextView) view.findViewById(R.id.post_lottery_newss_comment);
        top_comment_line = (ImageView) view.findViewById(R.id.top_comment_bk);
// bottom_comment_line = (ImageView) view.findViewById(R.id.bottom_comment_bk);

    }

    private void setUpView() {
        activeUpHeadView();
        listView = (AutoLoadListView) findViewById(R.id.news_list_comment);
        listView.setOnLoadDataListener(this);
        topTitle = (TextView) findViewById(R.id.title);

        news_content_bottom = (LinearLayout) findViewById(R.id.news_content_bottom);
        tv_news_bottom = (TextView) findViewById(R.id.tv_news_bottom);
        tv_news_bottom.setOnClickListener(this);
        mTextWatcher = new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() == 0) {
                    comment_post.setEnabled(false);
                    comment_post.setTextColor(getResources().getColor(R.color.gray));
                }
                else {
                    comment_post.setEnabled(true);
                    comment_post.setTextColor(getResources().getColor(R.color.black));
                }
            }
        };
        comment_content.addTextChangedListener(mTextWatcher);
    }

    private void logIn() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", "评论");
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
// intent.setClass(NewsCommentList.this, StartUp.class);
        intent.setClass(NewsCommentList.this, Login.class);
        startActivityForResult(intent, 1);
    }

    private boolean analyse(String commentNewId, String currentId) {
        boolean isAnalyse = true;
        if (!commentNewId.equals("-1")) {
            String[] commentsArray = commentNewId.split("\\|");
            for (int i = 0; i < commentsArray.length; i++) {
                if (commentsArray[i].equals(currentId)) {
                    isAnalyse = false;
                    break;
                }
            }
        }
        return isAnalyse;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_comment_post) {
            commentPost();
            comment_post.setEnabled(false);
            faceGrid.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.input_comment) {
            faceGrid.setVisibility(View.GONE);
            comment_content.setCursorVisible(true);
        }
        else if (v.getId() == R.id.face_button) {
            if (faceGrid.isShown()) {
                faceGrid.setVisibility(View.GONE);
            }
            else {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                showFace();
            }

        }
        else if (v.getId() == R.id.tv_news_bottom) {
            comment_content.setText("");
            comment_content.setCursorVisible(true);
            isReply = false;
            news_content_bottom.setVisibility(View.VISIBLE);
            tv_news_bottom.setVisibility(View.GONE);
            comment_content.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(comment_content, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void saveData() {
        StringBuilder newsCommentIdSb = new StringBuilder();
        if (!preferences.getString("news_comment_id", "-1").equals("-1")) {
            newsCommentIdSb.append(preferences.getString("news_comment_id", "-1"));
            newsCommentIdSb.append("|");
        }
        newsCommentIdSb.append(String.valueOf(newsId) + newsCommentItemRecord.get(itemPosition).getComentId());
        newsCommentIdSb.append("|");
        newsCommentIdSb.delete(newsCommentIdSb.length() - 1, newsCommentIdSb.length());
        databaseData.putString("news_comment_id", newsCommentIdSb.toString());
        databaseData.commit();
    }

    private void showFace() {
        faceGrid.setVisibility(View.VISIBLE);
        if (faceGrid.getAdapter() == null) {
            faceGrid.setAdapter(new FaceAdapter(this, Face.faceNames));
        }
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 < Face.faceNames.length) {
                int index = comment_content.getSelectionStart();
                Editable edit = comment_content.getEditableText();
                if (comment_content.length() < 55) {// 输入字符长度大于55则不能再输入表情
                    edit.insert(index,
                                TextUtil.formatImage("[" + Face.faceNames[arg2] + "]", NewsCommentList.this));
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceGrid.isShown()) {
                faceGrid.setVisibility(View.GONE);
            }
            else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void commentPost() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_post.getWindowToken(), 0);
        if (appState.getUsername() == null || appState.getUsername().equals("")) {
            logIn();
        }
        else {
            if (checkCommentInput()) {
                if (isReply) {
                    isReply = false;
                    if (newsCommentItemRecord.get(itemPosition).getContent().length() == 60 ||
                        newsCommentItemRecord.get(itemPosition).getContent().length() > 60) {
                        commet_content =
                            HttpConnectUtil.encodeParameter("@" +
                                newsCommentItemRecord.get(itemPosition).getNickname() + ":" +
                                comment_content.getText().toString());
                    }
                    else {
                        commet_content =
                            HttpConnectUtil.encodeParameter(comment_content.getText().toString() + "//@" +
                                newsCommentItemRecord.get(itemPosition).getNickname() + ":" +
                                newsCommentItemRecord.get(itemPosition).getContent());
                    }
                }
                else {
                    commet_content = HttpConnectUtil.encodeParameter(comment_content.getText().toString());
                }
                if (HttpConnectUtil.isNetworkAvailable(NewsCommentList.this)) {
                    LotteryPostCommentTask lpct = new LotteryPostCommentTask();
                    lpct.execute(1);
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
        }

    }

    public boolean checkCommentInput() {
        String inf = null;
        if (comment_content.getText().toString().equals("")) {
            inf = "请输入评论内容";
        }
        else if (comment_content.getText().toString().matches("(^\\s*)|(\\s*$)")) {
            inf = "请不要输入空格";
            ViewUtil.showTipsToast(this, inf);
        }
        else if (comment_content.getText().length() > 60) {
            inf = "评论内容超过60字";
            ViewUtil.showTipsToast(this, inf);
        }
        if (inf == null) {
            return true;
        }
        else {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 22) {
            pageNo = 1;
            handler.sendEmptyMessageDelayed(POSTCOMMENT, 1000);
        }
    }

    @Override
    public void onStatusExtraChanged(int withDrawStatus) {
        if (withDrawStatus == 304) {
            String inf = getResources().getString(R.string.login_again);
            OperateInfUtils.clearSessionId(NewsCommentList.this);
            showLoginAgainDialog(inf);
        }
        if (withDrawStatus == 302) {
            String inf = getResources().getString(R.string.login_timeout);
            OperateInfUtils.clearSessionId(NewsCommentList.this);
            showLoginAgainDialog(inf);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(appState.getUsername() == null)) {
            phone_num = appState.getUsername();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("zzc_desc", "username [" + appState.getUsername() + "]: open news comment list");
        FlurryAgent.onEvent("user open news comment list", map);
    }

    class LotteryNewsCommentListTask
        extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "list_comments");
            parameter.put("pid", LotteryUtils.getPid(NewsCommentList.this));
            parameter.put("article_id", "" + news_id);
            parameter.put("page_no", "" + pageNo);
            parameter.put("size", "10");
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(NewsCommentList.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            dismissProgress();
            int size = 0;
            String json = (String) result;
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, RESPONSE);
                    if (data.equals("[]") == false) {
                        int lastSize = newsCommentItemRecord.size();
                        getNewsListCommentItemArray(newsCommentItemRecord, data);
                        size = newsCommentItemRecord.size();
                        if (size - lastSize < 10) {
                            listView.removeLoadingFootView();
                        }
                        else {
                            listView.addLoadingFootView();
                            listView.readyToLoad();
                        }
                        if (isPosted) {
                            comment_num = comment_num + 1;
                            postLotteryComment.setText("评论(" + comment_num + ")");
                            isPosted = false;
                        }

                        lncAdapter.comment_num = comment_num;
                        lncAdapter.notifyDataSetChanged();
                        pageNo++;
                        for (int i = firstListAccountD; i < size; i++) {
                            limitNumD.add(i, 0);
                        }
                        firstListAccountD = size;

                        for (int i = firstListAccountC; i < size; i++) {
                            limitNumC.add(i, 0);
                        }
                        firstListAccountC = size;
                    }
                    else {
                        showNoData();
                    }

                }
                else {
                    showFail(failTips);
                }
            }
            super.onPostExecute(result);
        }

        private void getNewsListCommentItemArray(ArrayList<NewsCommentItem> newsCommentListItemArray,
                                                 String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    for (int i = 0; i < hallArray.length(); i++) {
                        NewsCommentItem newsCommmentItem = new NewsCommentItem();
                        JSONObject jo = hallArray.getJSONObject(i);
                        newsCommmentItem.setCommentId(jo.getInt("comment_id"));
                        newsCommmentItem.setNickname(jo.getString("nickname"));
                        newsCommmentItem.setAddTime(jo.getString("issue_date"));
                        newsCommmentItem.setContent(jo.getString("content"));
                        newsCommmentItem.setCommentGood(jo.getInt("good"));
                        newsCommmentItem.setCommentBad(jo.getInt("bad"));
                        newsCommmentItem.setReplyId(jo.getInt("reply_id"));
                        newsCommentListItemArray.add(newsCommmentItem);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class LotteryNewsContentTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "show_article");
            parameter.put("pid", LotteryUtils.getPid(NewsCommentList.this));
            parameter.put("news_id", "" + news_id);
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(NewsCommentList.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            dismissProgress();
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
                        view.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        JSONArray hallArray = new JSONArray(data);
                        JSONObject jo = hallArray.getJSONObject(0);
                        newsTitle.setText(Html.fromHtml(jo.getString("title")));
                        newsContent.setText(Html.fromHtml(jo.getString("content")));
                        newsIssueDate.setText(Html.fromHtml(jo.getString("issue_date")));
                        like = jo.getInt("good");
                        dislike = jo.getInt("bad");
                        postLotteryComment.setText("评论(" + jo.getString("reply_count") + ")");
                        resetBottomTv(jo.getString("reply_count"));
                        comment_num = Integer.valueOf(jo.getString("reply_count"));
                        top_comment_line.setVisibility(View.VISIBLE);
// bottom_comment_line.setVisibility(View.VISIBLE);
                        postLotteryComment.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(FIRSTLOAD);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (status.equals("302")) {
                    inf = getResources().getString(R.string.login_timeout);
                    OperateInfUtils.clearSessionId(NewsCommentList.this);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    inf = getResources().getString(R.string.login_again);
                    OperateInfUtils.clearSessionId(NewsCommentList.this);
                    showLoginAgainDialog(inf);
                }
                else {
                    inf = "查询失败";
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

    private boolean isPosted = false;
    private int comment_num = 0;

    class LotteryPostCommentTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004120");
            parameter.put("pid", LotteryUtils.getPid(NewsCommentList.this));
            parameter.put("article_id", "" + news_id);
            parameter.put("content", commet_content.trim());
            parameter.put("phone", phone_num);
            parameter.put("source", LotteryUtils.versionName(NewsCommentList.this, true));
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(NewsCommentList.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected void onPostExecute(Object result) {
            dismissProgress();
            String inf;
            String json = (String) result;
            if (result == null) {
                ViewUtil.showTipsToast(NewsCommentList.this, "提交失败");
                comment_post.setEnabled(true);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    isPosted = true;
                    handler.sendEmptyMessageDelayed(LOADAGAIN, 1500);
                    comment_content.setText(null);
                    resetBottomTv("2");
                    ViewUtil.showTipsToast(NewsCommentList.this, "成功");
                    comment_post.setEnabled(false);
                }
                else if (status.equals("302")) {
                    inf = getResources().getString(R.string.login_timeout);
                    OperateInfUtils.clearSessionId(NewsCommentList.this);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    inf = getResources().getString(R.string.login_again);
                    OperateInfUtils.clearSessionId(NewsCommentList.this);
                    showLoginAgainDialog(inf);
                }
                else {
                    ViewUtil.showTipsToast(NewsCommentList.this, "提交失败");
                }
            }
            super.onPostExecute(result);
        }
    }

    public void showNoData() {
        if (newsCommentItemRecord.size() == 0) {
// ViewUtil.showTipsToast(this,"暂无评论");
// listView.setVisibility(View.GONE);
        }
        listView.removeLoadingFootView();
    }

    public void showFail(String failInf) {
        if (newsCommentItemRecord.size() == 0) {
            showTipsPage(failInf);
            listView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        listView.removeLoadingFootView();
    }

    public void resetBottomTv(String num) {
        if (num.equals("0")) {
            tv_news_bottom.setText(GETFIRSTPLACE);
        }
        else if (num.equals("1")) {
            tv_news_bottom.setText(GETSECONDPLACE);
        }
        else if (Integer.valueOf(num) < 10) {
            tv_news_bottom.setText(GETTHIRDPLACE);
        }
        else {
            tv_news_bottom.setText(HOTPLACES);
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_news_content";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (comment_content.hasFocus() == false) {
            if (comment_content.getText().toString() == null ||
                comment_content.getText().toString().equals("")) {
                news_content_bottom.setVisibility(View.GONE);
                tv_news_bottom.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean checkClick(int position, int flag) {
        currentCommentNewsId = String.valueOf(newsId) + newsCommentItemRecord.get(position).getComentId();
        itemPosition = position;
        if (flag == 0) {// 回复
            if (appState.getSessionid() == null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("forwardFlag", "评论");
                bundle.putBoolean("ifStartSelf", false);
                intent.putExtras(bundle);
// intent.setClass(NewsCommentList.this, Login.class);
// intent.setClass(NewsCommentList.this, StartUp.class);
                intent.setClass(NewsCommentList.this, Login.class);
                startActivity(intent);
            }
            else {
                isReply = true;
                comment_content.setText("");
                comment_content.setCursorVisible(true);
                news_content_bottom.setVisibility(View.VISIBLE);
                tv_news_bottom.setVisibility(View.GONE);
                comment_content.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(comment_content, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            return true;
        }
        else if (flag == 1) {// 顶
            if (appState.getSessionid() == null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("forwardFlag", "评论");
                bundle.putBoolean("ifStartSelf", false);
                intent.putExtras(bundle);
// intent.setClass(NewsCommentList.this, StartUp.class);
                intent.setClass(NewsCommentList.this, Login.class);
                startActivity(intent);
            }
            else {
                commentNewsId = preferences.getString("news_comment_id", "-1");
                if (analyse(commentNewsId, currentCommentNewsId)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("zzc_desc", "username [" + appState.getUsername() +
                        "]: open lottery news comment like");
                    FlurryAgent.onEvent("user open lottery news comment like", map);
                    saveData();
                    newsCommentItemRecord.get(itemPosition).setDing(true);
                    like = newsCommentItemRecord.get(itemPosition).getCommentGood();
                    newsCommentItemRecord.get(itemPosition).setCommentGood(like + 1);
                    lncAdapter.notifyDataSetChanged();

                    NewsCommentPostTask ncpt =
                        new NewsCommentPostTask(this, popupCommentTop, popupCommentDown,
                                                newsCommentItemRecord.get(itemPosition).getComentId(), 1,
                                                itemPosition, like, newsCommentItemRecord, lncAdapter);
                    ncpt.setStatusChangeExtraListener(NewsCommentList.this);
                    ncpt.execute(1);
                }
                else {
                    ViewUtil.showTipsToast(this, "您已经评论过");
                }
            }
            return true;
        }
        else
            return false;
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(NewsCommentList.this)) {
            LotteryNewsCommentListTask lnclt = new LotteryNewsCommentListTask();
            lnclt.execute(1);
        }
        else {
            listView.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            ViewUtil.showTipsToast(this, noNetTips);
        }

    }

}
