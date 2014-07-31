package com.haozan.caipiao.activity.guess;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.unite.CommissionAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.ExpandableHeightGridView;
import com.umeng.analytics.MobclickAgent;

/**
 * 竞猜详细
 * 
 * @author peter_feng
 * @create-time 2013-3-30 上午11:37:32
 */
public class GuessParty
    extends BasicActivity
    implements OnClickListener {
    private final static int PIC_PROGRESS_ARRAY[] = {R.drawable.vote_bule, R.drawable.vote_green,
            R.drawable.vote_red};
    private String integralArray[] = {"100", "200", "500", "1000", "2000", "3000", "5000", "10000", "15000",
            "20000", "30000", "50000"};
    /**
     * 正在进行中
     */
    public static final int IN_PROGRESS = 0;
    /**
     * 已截止未开奖
     */
    public static final int NO_OPEN_LOTTERY = 1;
    /**
     * 已开奖
     */
    public static final int OPEN_LOTTERY = 2;
    /**
     * 失效的题目
     */
    public static final int INVALID_LOTTERY = 3;

    private static final String TITLE_STATUS_ = "状态";
    private static final String TITLE_ENDED = "已截止";
    private static final String TITLE_WAITING = "等待开奖";
    private static final String TITLE_INVALID = "题目无结果";
    private static final String TITLE_AWARD = "获取";

    private static final int NORMAL_STATUS = 0;
    private static final int NOT_LOGIN_STATUS = 1;
    private static final int TOPUP_STATUS = 2;
    private static final int BUY_INTEGRAL_STATUS = 3;
    private int status = NORMAL_STATUS;

    private static final int GET_GUESS_INF = 0;
    private static final int SUBMIT_GUESS_INF = 1;
    private int handleGuessInf = GET_GUESS_INF;

    private TextView guessRule;
    private LinearLayout guessPartyLinear;
    private String question;
    private TextView prgQuestion;
    private TextView issueQuestion;
    private TextView guessSumPeople;
    private TextView guessSumScore;
    private TextView guessDate;
    private Button integralSelect;
    private TextView integralOwn;
    private Button buyIntegral;
    private TextView[] choosingTvArray;
    private LinearLayout tipsLayout;
    private TextView tips;
    private ImageView shareCheck;
    private boolean ifShare = true;
    private Button requestSubmit;

    private ExpandableHeightGridView integralGridView;
    private CommissionAdapter integralAdapter;

    private String schemaId;
    private String issueId;
    private int answerId = -1;
    private String schemaTopic;
    // add by vincent
    private String answerStr = null;
    // 0代表竞猜列表过来的，1代表竞猜记录过来的
    private int listDataType = 0;
    private int earnedScore;
    private String endTime = null;
    private String nowTime = null;

    private int sumPeople;
    private int sumScore;
    private int length;
    private int guessStatus = IN_PROGRESS;
    private int[] answerIdArray;
    private int[] peoleNumArray;
    private int[] scoreArray;
    private String[] answerTextArray;
    private NumberFormat ddf1 = NumberFormat.getNumberInstance();
    // add by vincent
    private LinearLayout guess_party_sub;
    private TextView status_title;
    private int intergralNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_party);
        setUpView();
        init();
    }

    private void setUpView() {
        setupMainView();
        setupIntegralSelect();
    }

    private void setupMainView() {
        guessRule = (TextView) findViewById(R.id.help);
        guessRule.setOnClickListener(this);
        guessPartyLinear = (LinearLayout) findViewById(R.id.guess_party_linear);
        prgQuestion = (TextView) findViewById(R.id.prg_question);
        issueQuestion = (TextView) findViewById(R.id.issue_question);
        guessSumPeople = (TextView) findViewById(R.id.guess_people_num);
        guessSumScore = (TextView) findViewById(R.id.guess_sum_score);
        guessDate = (TextView) findViewById(R.id.guess_date);
        integralSelect = (Button) this.findViewById(R.id.integral_select);
        integralSelect.setOnClickListener(this);
        integralOwn = (TextView) this.findViewById(R.id.integral_own);
        buyIntegral = (Button) this.findViewById(R.id.buy_integral);
        buyIntegral.setOnClickListener(this);
        tipsLayout = (LinearLayout) this.findViewById(R.id.tips_layout);
        tips = (TextView) this.findViewById(R.id.tips);
        shareCheck = (ImageView) this.findViewById(R.id.guess_submit_check);
        shareCheck.setOnClickListener(this);
        requestSubmit = (Button) findViewById(R.id.request_submit);
        requestSubmit.setOnClickListener(this);
        // add by vincent
        guess_party_sub = (LinearLayout) findViewById(R.id.guess_party_sub);
        status_title = (TextView) findViewById(R.id.status_title);
    }

    private void setupIntegralSelect() {
        integralAdapter = new CommissionAdapter(this, integralArray, intergralNumber, "unite_commissin");
        integralGridView = (ExpandableHeightGridView) this.findViewById(R.id.integral_gridview);
        integralGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        integralGridView.setAdapter(integralAdapter);
        integralGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t_v =
                    (TextView) parent.getChildAt(intergralNumber).findViewById(R.id.unite_grid_view_item_click);
                t_v.setTextColor(getResources().getColor(R.color.dark_purple));
                t_v.setBackgroundResource(R.drawable.bet_popup_item_normal);
                TextView tv =
                    (TextView) parent.getChildAt(position).findViewById(R.id.unite_grid_view_item_click);
                tv.setTextColor(getResources().getColor(R.color.light_white));
                tv.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                intergralNumber = position;
                showIntergralBet();
                integralGridView.setVisibility(View.GONE);
            }
        });
    }

    // 设置竞猜选择点击事件
    private void choosingGuessing(int answer) {
        if (answerId == -1) {
            choosingTvArray[answer].setBackgroundResource(R.drawable.choosing_select);
            answerId = answer;
        }
        else {
            if (answerId == answer) {
                choosingTvArray[answer].setBackgroundResource(R.drawable.choosing_not_select);
                answerId = -1;
            }
            else {
                choosingTvArray[answerId].setBackgroundResource(R.drawable.choosing_not_select);
                choosingTvArray[answer].setBackgroundResource(R.drawable.choosing_select);
                answerId = answer;
            }
        }
    }

    // 初始化竞猜选项及选项选择情况
    private void initGuessingItem() {
        choosingTvArray = new TextView[length];
        guessPartyLinear.removeAllViews();
        for (int i = 0; i < length; i++) {
            View guessingItemView = View.inflate(this, R.layout.guess_quetion_item, null);
            TextView choosing = (TextView) guessingItemView.findViewById(R.id.choosing);
            choosingTvArray[i] = choosing;
            choosing.setBackgroundResource(R.drawable.choosing_not_select);
            final int index = i;
            ImageView answerIcon = (ImageView) guessingItemView.findViewById(R.id.answer_icon);
            answerIcon.setImageResource(R.drawable.bet_order_successed);
            TextView content = (TextView) guessingItemView.findViewById(R.id.content);
            content.setText(answerTextArray[i]);
            TextView percentNum = (TextView) guessingItemView.findViewById(R.id.percent_num);
            float scorePercent;
            if (sumScore == 0) {
                scorePercent = 0;
            }
            else {
                scorePercent = Float.valueOf(scoreArray[i]) / Float.valueOf(sumScore) * 100;
            }
            percentNum.setText(" " + ddf1.format(scorePercent) + "%");
            TextView percent = (TextView) guessingItemView.findViewById(R.id.percent);
            percent.setBackgroundResource(PIC_PROGRESS_ARRAY[i % 3]);
            initAnimation(percent, scorePercent);

            if (TextUtils.isEmpty(answerStr) == false && !answerStr.equals("0")) {
                if (Integer.parseInt(answerStr) - 1 == i) {
                    answerIcon.setVisibility(View.VISIBLE);
                    choosing.setVisibility(View.GONE);
                }
                else {
                    choosing.setVisibility(View.INVISIBLE);
                }
            }
            else if (guessStatus == NO_OPEN_LOTTERY || guessStatus == OPEN_LOTTERY) {
                choosing.setVisibility(View.GONE);
            }
            guessingItemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    choosingGuessing(index);
                }
            });
            guessPartyLinear.addView(guessingItemView);
        }
    }

    private void init() {
        ddf1.setMaximumFractionDigits(1);
        Bundle bundle = this.getIntent().getExtras();
        schemaTopic = bundle.getString("schema_name");
        prgQuestion.setText(schemaTopic);
        schemaId = bundle.getString("schema_id");
        listDataType = bundle.getInt("listDataType");
        earnedScore = bundle.getInt("earned_score");

        if (listDataType == 0) {
            guessStatus = bundle.getInt("status");
            setSubmitButtonStatus(guessStatus);
        }
        initShareCheckStatus();
        showIntergralBet();
        executeTask();
    }

    private void initShareCheckStatus() {
        ifShare = preferences.getBoolean("guess_if_share", true);
        if (ifShare) {
            shareCheck.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            shareCheck.setBackgroundResource(R.drawable.choosing_not_select);
        }
    }

    private void showIntegralOwn() {
        if (appState.getUsername() != null) {
            integralOwn.setText("现有积分：" + appState.getScore());
        }
    }

    public void executeTask() {
        if (HttpConnectUtil.isNetworkAvailable(GuessParty.this)) {
            GetGuessPartyTask getGuessPartyTask = new GetGuessPartyTask();
            getGuessPartyTask.execute();
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this, inf);
        }
    }

    // changed by vincent
    /*
     * private void setSubmitButtonStatus(int status) { if (status == 0 || status == 1)
     * requestSubmit.setEnabled(true); else if (status == 2) requestSubmit.setEnabled(false); }
     */
    private void setSubmitButtonStatus(int status) {
        if (status == 0) {
            guess_party_sub.setVisibility(View.VISIBLE);
            requestSubmit.setEnabled(true);
        }
        else {
            guess_party_sub.setVisibility(View.GONE);
            requestSubmit.setEnabled(false);
        }
    }

    private void initAnimation(TextView v, float pepelPercent) {
        // 设置初始为2.0及时为0也有一点图片可以看到
        ScaleAnimation scale =
            new ScaleAnimation((float) 2.0, (float) (2 + pepelPercent), (float) 1.0, (float) 1.0);
        scale.setFillAfter(true);
        scale.setDuration((long) (10 * pepelPercent));
        v.startAnimation(scale);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#jingcai");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(GuessParty.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.buy_integral) {
            buyIntegral();
        }
        else if (v.getId() == R.id.request_submit) {
            requestSubmit();
        }
        else if (v.getId() == R.id.submit_dlg) {
            handleGuessInf = SUBMIT_GUESS_INF;
            executeTask();
        }
        else if (v.getId() == R.id.integral_select) {
            if (integralGridView.isShown()) {
                integralGridView.setVisibility(View.GONE);
            }
            else {
                integralGridView.setVisibility(View.VISIBLE);
            }
        }
        else if (v.getId() == R.id.guess_submit_check) {
            if (ifShare) {
                ifShare = false;
                shareCheck.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                ifShare = true;
                shareCheck.setBackgroundResource(R.drawable.choosing_select);
            }
            databaseData.putBoolean("guess_if_share", ifShare);
            databaseData.commit();
        }
    }

    private void buyIntegral() {
        if (status == NOT_LOGIN_STATUS) {
            ActionUtil.toLogin(this, "购买积分");
        }
        else if (appState.getAccount() > 0) {
            ActionUtil.buyIntegral(GuessParty.this);
        }
        else {
            ActionUtil.toTopupNew(this);
            ViewUtil.showTipsToast(this, "余额不足，请先充值");
        }
    }

    private void requestSubmit() {
        if (status == NOT_LOGIN_STATUS) {
            ActionUtil.toLogin(this, "竞猜");
        }
        else if (status == TOPUP_STATUS) {
            ActionUtil.toTopupNew(this);
        }
        else if (status == BUY_INTEGRAL_STATUS) {
            ActionUtil.buyIntegral(GuessParty.this);
        }
        else {
            if (checkClickSatus()) {
                handleGuessInf = SUBMIT_GUESS_INF;
                executeTask();
            }
        }
    }

    private void showIntergralBet() {
        integralSelect.setText(integralArray[intergralNumber]);
        judgeAccountStatus();
    }

    private void judgeAccountStatus() {
        if (appState.getUsername() != null) {
            if (appState.getScore() < Integer.valueOf(integralArray[intergralNumber])) {
                if (judgeHasMoney()) {
                    status = BUY_INTEGRAL_STATUS;
                    tips.setText(getResources().getString(R.string.guess_to_buy_integral_tips));
                    tipsLayout.setVisibility(View.VISIBLE);
                    requestSubmit.setEnabled(false);
                }
            }
            else {
                status = NORMAL_STATUS;
                tipsLayout.setVisibility(View.GONE);
                requestSubmit.setText(" 提  交 ");
                requestSubmit.setEnabled(true);
            }
        }
        else {
            status = NOT_LOGIN_STATUS;
            tips.setText(getResources().getString(R.string.guess_to_login_tips));
            tipsLayout.setVisibility(View.VISIBLE);
            requestSubmit.setText(" 登  录 ");
            requestSubmit.setEnabled(true);
        }
    }

    /**
     * 判断是否有余额
     * 
     * @return
     */
    private boolean judgeHasMoney() {
        if (appState.getAccount() > 0) {
            return true;
        }
        else {
            status = TOPUP_STATUS;
            tips.setText(getResources().getString(R.string.guess_to_topup_tips));
            tipsLayout.setVisibility(View.VISIBLE);
            requestSubmit.setText(" 充  值 ");
            return false;
        }
    }

    class GetGuessPartyTask
        extends AsyncTask<String, Object, String> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2010020");
            parameter.put("pid", LotteryUtils.getPid(GuessParty.this));
            parameter.put("prg_id", schemaId);
            return parameter;
        }

        private HashMap<String, String> initHashMapRequest() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2010040");
            parameter.put("pid", LotteryUtils.getPid(GuessParty.this));
            parameter.put("phone", appState.getUsername());
            parameter.put("prg_id", schemaId);
            parameter.put("iss_id", issueId);
            parameter.put("answ_id", "" + answerIdArray[answerId]);
            parameter.put("bet_score", integralArray[intergralNumber]);
            parameter.put("m", HttpConnectUtil.encodeParameter("竞猜：" + question));
            parameter.put("p",
                          HttpConnectUtil.encodeParameter("选择了答案：\n" + (answerId + 1) + "." +
                              answerTextArray[answerId]));
            parameter.put("source",
                          HttpConnectUtil.encodeParameter(LotteryUtils.versionName(GuessParty.this, true)));
            if (ifShare) {
                parameter.put("action", "1");
            }
            return parameter;
        }

        @Override
        protected String doInBackground(String... para) {
            ConnectService connectNet = new ConnectService(GuessParty.this);
            String json = null;
            try {
                if (handleGuessInf == GET_GUESS_INF)
                    json = connectNet.getJsonGet(6, false, initHashMap());
                else if (handleGuessInf == SUBMIT_GUESS_INF)
                    json = connectNet.getJsonGet(6, true, initHashMapRequest());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dismissProgress();
            String inf = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response_data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (handleGuessInf == SUBMIT_GUESS_INF) {
                        ViewUtil.showTipsToast(GuessParty.this, "参与竞猜成功");
                        caculateUserScore();
                    }

                    if (response_data.equals("[]") == false) {
                        if (handleGuessInf == GET_GUESS_INF) {
                            try {
                                sumPeople = jsonObject.getInt("all_pepl");
                                guessSumPeople.setText(String.valueOf(sumPeople));
                                sumScore = jsonObject.getInt("all_score");
                                guessSumScore.setText("" + sumScore);
                                guessDate.setText(TimeUtils.convertDate(jsonObject.getString("end_time"),
                                                                        "yyyy-MM-dd HH:mm:ss", "MM-dd HH:mm"));
                                String title = jsonObject.getString("scheme_name");
                                if (!TextUtils.isEmpty(title)) {
                                    prgQuestion.setText(title);
                                }
                                question = jsonObject.getString("question");
                                issueQuestion.setText(question);
                                length = jsonObject.getInt("answer_size");
                                issueId = jsonObject.getString("guess_id");
                                String scoreScope = jsonObject.getString("score_scope");
                                if (!TextUtils.isEmpty(scoreScope)) {
                                    String[] scoreScopeArray = scoreScope.trim().split(",");
                                    integralArray = scoreScopeArray;
                                    setupIntegralSelect();
                                    showIntergralBet();
                                }

                                answerIdArray = new int[length];
                                answerTextArray = new String[length];
                                peoleNumArray = new int[length];
                                scoreArray = new int[length];

                                for (int i = 0; i < length; i++) {
                                    answerIdArray[i] = jsonObject.getInt("answerid_" + String.valueOf(i + 1));
                                    answerTextArray[i] =
                                        jsonObject.getString("answname_" + String.valueOf(i + 1));
                                    peoleNumArray[i] = jsonObject.getInt("pepls_" + String.valueOf(i + 1));
                                    scoreArray[i] = jsonObject.getInt("score_" + String.valueOf(i + 1));
                                }

                                // add by vincent
                                if (listDataType == 0) {
                                    answerStr = jsonObject.getString("answer_id");
                                    if (!answerStr.equals("0")) {
                                        status_title.setText(TITLE_STATUS_);
                                        guessDate.setText(TITLE_ENDED);
                                    }
                                    else {
                                        if (guessStatus == NO_OPEN_LOTTERY) {
                                            status_title.setText(TITLE_STATUS_);
                                            guessDate.setText(TITLE_WAITING);
                                        }
                                        else if (guessStatus == INVALID_LOTTERY) {
                                            status_title.setText(TITLE_STATUS_);
                                            guessDate.setText(TITLE_INVALID);
                                        }
                                    }
                                }
                                else if (listDataType == 1) {
                                    endTime = jsonObject.getString("end_time");
                                    nowTime = jsonObject.getString("nowTime");
                                    SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        Date endDate = fo.parse(endTime);
                                        Date nowDate = fo.parse(nowTime);
                                        long millis = endDate.getTime() - nowDate.getTime();
                                        if (millis < 0) {
                                            answerStr = jsonObject.getString("answer_id");
                                            if (answerStr != null && !answerStr.equals("0")) {
                                                if (earnedScore != 0) {
                                                    status_title.setText(TITLE_AWARD);
                                                    guessDate.setText(earnedScore + "");
                                                }
                                                else {
                                                    status_title.setText(TITLE_STATUS_);
                                                    guessDate.setText(TITLE_ENDED);
                                                }
                                            }
                                            else if (answerStr != null && answerStr.equals("0")) {
                                                if (guessStatus == NO_OPEN_LOTTERY) {
                                                    status_title.setText(TITLE_STATUS_);
                                                    guessDate.setText(TITLE_WAITING);
                                                }
                                                else if (guessStatus == INVALID_LOTTERY) {
                                                    status_title.setText(TITLE_STATUS_);
                                                    guessDate.setText(TITLE_INVALID + ",积分已返");
                                                }
                                            }
                                            guess_party_sub.setVisibility(View.GONE);
                                            requestSubmit.setEnabled(false);
                                        }
                                        else {
                                            guess_party_sub.setVisibility(View.VISIBLE);
                                            requestSubmit.setEnabled(true);
                                        }
                                    }
                                    catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                            initGuessingItem();
                            if (answerId == -1) {
                                choosingGuessing(0);
                            }
                            else {
                                int answer = answerId;
                                answerId = -1;
                                choosingGuessing(answer);
                            }
                        }
                        else if (handleGuessInf == SUBMIT_GUESS_INF) {
                            handleGuessInf = GET_GUESS_INF;
                            executeTask();
                        }
                    }
                }
                else if (status.equals("202")) {
                    if (handleGuessInf == GET_GUESS_INF)
                        inf = "数据已读取完毕";
                    else {
                        inf = "提交失败";
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(GuessParty.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(GuessParty.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else if (status.equals("400")) {
                    if (handleGuessInf == SUBMIT_GUESS_INF)
                        inf = "对赌已过期或不存在";
                    else {
                        inf = "提交失败";
                    }
                }
                else {
                    if (handleGuessInf == GET_GUESS_INF)
                        inf = "数据读取失败";
                    else {
                        inf = "提交失败";
                    }
                }
            }
            else {
                if (handleGuessInf == GET_GUESS_INF)
                    inf = "数据读取失败";
                else {
                    inf = "提交失败";
                }
            }

            if (inf != null) {
                ViewUtil.showTipsToast(GuessParty.this, inf);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    int prevId = -1;

    private boolean checkClickSatus() {
        String inf = null;
        if (answerId == -1) {
            inf = "请选择问题答案";
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else {
            return true;
        }

    }

    public void caculateUserScore() {
        appState.setScore(appState.getScore() - Integer.valueOf(integralArray[intergralNumber]));
        showIntegralOwn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showIntergralBet();
        showIntegralOwn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open guess list detail");
        String eventName = "open guess list detail";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_guess_list_detail";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
