package com.haozan.caipiao.activity.userinf;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.CaiJinList;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.activity.UserWithdraw;
import com.haozan.caipiao.activity.UserWithdrawExtra;
import com.haozan.caipiao.adapter.UserDetailInfExpanAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.types.UserAccountDetail;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.RefreshableView;
import com.haozan.caipiao.widget.AutoLoadListView.GetListItemPositionListener;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.RefreshableView.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class UserDetailInf
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnGetUserInfListener, LoadDataListener, AnimationListener,
    OnHeaderRefreshListener, GetListItemPositionListener {
    private static final String NO_DATA = "三个月内没有交易记录\n去购彩试试运气吧..";
    private static final String NO_ADD_INF = "您还没有填写详细资料\n完善信息保证购彩安全";
    private static final String RESPONSE = "response_data";
    private static final int ANIMA_TRANS = 1;
    private String infoType = "000";
    private String startDate = "2011-03-18";
    private String endDate = "";
    private TextView userMoneyExpansed;
    private TextView userMoneySupplement;
    private TextView allMoney;
    private TextView allScore;
    private TextView winMoney;
    private TextView withdrawMoney;
    private ImageView flipButton;
    private ImageView flipBackButton;
    private LinearLayout caijinTitleTv;
    private DisplayMetrics dm;
    // value
    private double accountBalance;
    private double totalPrize;
    private double totalBuy;
    private double totalDraw;
    private double totalCharge;
    private double score = 0.0;
    private double balance;
    private double win;
    private double withDrawMoney;
    // top tool bar
    private TextView betLotteryInf;
    private TextView awardLotteryInf;
    private TextView chageLotteryInf;
    private TextView withdrawalLotteryInf;
    // header tool bar
    private TextView betLotteryInfBv;
    private TextView awardLotteryInfBv;
    private TextView chageLotteryInfBv;
    private TextView withdrawalLotteryInfBv;

    private Button userAccountWithdraw;
    private TextView refreshText;
    private ProgressBar refreshProgress;
    private DecimalFormat myFormatter;
    private DecimalFormat scoreFormatter;
    private View headView;
    private View barView;
    private View headViewContentViewOne;
    private View headViewContentViewTwo;
    private LayoutInflater mLayoutInflater;
    private ArrayList<UserAccountDetail> accountDetailArrayList;
    private AutoLoadListView expanableListView;
    private UserDetailInfExpanAdapter userDetailInfExpanAdapter;
    private LinearLayout indicatorGroup;
    private boolean kindTwoDismiss = false;
    // list data
    private ArrayList<Boolean> clickStatus = new ArrayList<Boolean>();
    private ArrayList<UserAccountDetail> userAccountDetailListSame = new ArrayList<UserAccountDetail>();
    private Set<String> set = new HashSet<String>();
    private ArrayList<String> dateArray = new ArrayList<String>();
    private int clickIndex = 1;
    // 下拉刷新
    private RefreshableView mPullToRefreshView;
    // 箭头滑动
    private int currentID = 0;
    private int btnArr[] = {R.id.bet_lottery_inf, R.id.award_lottery_inf, R.id.charge_lottery_inf,
            R.id.withdraw_lottery_inf};
    private ImageView btnBg;
    private ImageView btnBgBv;
    private int startPoint;
    private int endPoint;
    private int animationType;
    private ViewFlipper mContainer;
    void ViewFlipper(){}@Override
    public void onDetachedFromWindow() {
    	// TODO Auto-generated method stub
    	try {
    		super.onDetachedFromWindow();
    	}catch (IllegalArgumentException e) {
    	}
    };
    //
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account_detail);
        setupViews();
        barView = mLayoutInflater.inflate(R.layout.user_account_list_head, indicatorGroup, true);
        indicatorGroup.setVisibility(View.INVISIBLE);
        setUpHeaderViewButtonBv(barView);
        init();
    }

    private void setUpHeaderViewButton(View view) {
        betLotteryInf = (TextView) view.findViewById(R.id.bet_lottery_inf);
        betLotteryInf.setOnClickListener(this);
        awardLotteryInf = (TextView) view.findViewById(R.id.award_lottery_inf);
        awardLotteryInf.setOnClickListener(this);
        chageLotteryInf = (TextView) view.findViewById(R.id.charge_lottery_inf);
        chageLotteryInf.setOnClickListener(this);
        withdrawalLotteryInf = (TextView) view.findViewById(R.id.withdraw_lottery_inf);
        withdrawalLotteryInf.setOnClickListener(this);
        btnBg = (ImageView) view.findViewById(R.id.point_up_image_view);
        setWidgetSize(btnBg);
    }

    private void setUpHeaderViewButtonBv(View view) {
        betLotteryInfBv = (TextView) view.findViewById(R.id.bet_lottery_inf);
        betLotteryInfBv.setOnClickListener(this);
        awardLotteryInfBv = (TextView) view.findViewById(R.id.award_lottery_inf);
        awardLotteryInfBv.setOnClickListener(this);
        chageLotteryInfBv = (TextView) view.findViewById(R.id.charge_lottery_inf);
        chageLotteryInfBv.setOnClickListener(this);
        withdrawalLotteryInfBv = (TextView) view.findViewById(R.id.withdraw_lottery_inf);
        withdrawalLotteryInfBv.setOnClickListener(this);
        btnBgBv = (ImageView) view.findViewById(R.id.point_up_image_view);
        setWidgetSize(btnBgBv);
    }

    private void setWidgetSize(View widget) {
     // 获取屏幕分辨率
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        int screenWidth = display.getWidth();
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) widget.getLayoutParams();
        linearParams.width = screenWidth / 4; // 当控件的高强制设成365象素
// linearParams.height=44;
        widget.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa
    }

    private void setupViews() {
        indicatorGroup = (LinearLayout) findViewById(R.id.topGroup);

        mLayoutInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

        headViewContentViewOne =
            mLayoutInflater.inflate(R.layout.user_detail_inf_header_content_view_one, null);
        flipButton = (ImageView) headViewContentViewOne.findViewById(R.id.flip_button);
        flipButton.setOnClickListener(this);
        allMoney = (TextView) headViewContentViewOne.findViewById(R.id.sub_title_01);
        winMoney = (TextView) headViewContentViewOne.findViewById(R.id.user_win_money);
        caijinTitleTv = (LinearLayout) headViewContentViewOne.findViewById(R.id.caijin_title_tv);
        caijinTitleTv.setOnClickListener(this);
        allScore = (TextView) headViewContentViewOne.findViewById(R.id.sub_score_01);

        headViewContentViewTwo =
            mLayoutInflater.inflate(R.layout.user_detail_inf_header_content_view_two, null);
        flipBackButton = (ImageView) headViewContentViewTwo.findViewById(R.id.flip_back_button);
        flipBackButton.setOnClickListener(this);
        withdrawMoney = (TextView) headViewContentViewTwo.findViewById(R.id.user_withdraw_money);
        userMoneySupplement = (TextView) headViewContentViewTwo.findViewById(R.id.user_money_earn);
        userMoneyExpansed = (TextView) headViewContentViewTwo.findViewById(R.id.user_money_expanse);

        headView = mLayoutInflater.inflate(R.layout.user_account_detail_header, null);
        setUpHeaderViewButton(headView);
        mContainer = (ViewFlipper) headView.findViewById(R.id.container);
        frameLayout = (FrameLayout) headView.findViewById(R.id.fl_main);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mContainer.addView(headViewContentViewOne, layoutParams);
        mContainer.addView(headViewContentViewTwo, layoutParams);
        frameLayout.setLayoutParams(layoutParams);

        refreshText = (TextView) findViewById(R.id.refresh);
        refreshText.setOnClickListener(this);
        userAccountWithdraw = (Button) findViewById(R.id.user_account_detail_withdraw);
        userAccountWithdraw.setOnClickListener(this);
        refreshProgress = (ProgressBar) this.findViewById(R.id.refreshProgress);
        expanableListView = (AutoLoadListView) findViewById(R.id.detail_inf_list_expan);
        expanableListView.setOnLoadDataListener(this);
        expanableListView.setOnGetListItemPositionListener(this);

        // 下拉刷新
        mPullToRefreshView = (RefreshableView) findViewById(R.id.user_new_center_pull_refresh_view);
    }

    private void setAcountDetailDefaultView() {
        userMoneySupplement.setText(Html.fromHtml(""));
        userMoneyExpansed.setText(Html.fromHtml(""));
    }

    private void setDefaultView() {
        allMoney.setText("");
        allScore.setText("");
        winMoney.setText("");
        withdrawMoney.setText(Html.fromHtml(""));
        appearContent();
    }

    private void init() {
        // 下拉刷新接口
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        startDate = getDate(true);
        endDate = getDate(false);
        accountDetailArrayList = new ArrayList<UserAccountDetail>();
        userDetailInfExpanAdapter =
            new UserDetailInfExpanAdapter(UserDetailInf.this, userAccountDetailListSame, clickStatus);
        expanableListView.addHeaderView(headView, null, false);
        expanableListView.setAdapter(userDetailInfExpanAdapter);

        if (HttpConnectUtil.isNetworkAvailable(UserDetailInf.this)) {
            setDefaultView();
            setAcountDetailDefaultView();
            UserInfTask getUserInf = new UserInfTask(UserDetailInf.this);
            getUserInf.setOnGetUserInfListener(this);
            getUserInf.execute(1);
            UserAccountDetailTask uadt = new UserAccountDetailTask();
            uadt.execute();
        }
        else {
            showFail(noNetTips);
        }

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // popup window
        myFormatter = new DecimalFormat("###,##0.00");
        scoreFormatter = new DecimalFormat("##");
        setButtonBackGround(0);
        setButtonBackGroundBv(0);
    }

    private void showFail(String failInf) {
        if (accountDetailArrayList.size() == 0) {
            showTipsPage(failInf);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        expanableListView.removeLoadingFootView();
    }

    int page_num = 1;

    public void searchInfDetailFail(String inf) {
        ViewUtil.showTipsToast(this, inf);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open user account details");
        String eventName = "v2 open user account details";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    public void disappearContent(String inf) {
        allMoney.setVisibility(View.GONE);
        allScore.setVisibility(View.GONE);
        winMoney.setVisibility(View.GONE);
        withdrawMoney.setVisibility(View.GONE);
        userMoneySupplement.setVisibility(View.GONE);
        userMoneyExpansed.setVisibility(View.GONE);
    }

    public void appearContent() {
        allMoney.setVisibility(View.VISIBLE);
        allScore.setVisibility(View.VISIBLE);
        winMoney.setVisibility(View.VISIBLE);
        withdrawMoney.setVisibility(View.VISIBLE);
        userMoneySupplement.setVisibility(View.VISIBLE);
        userMoneyExpansed.setVisibility(View.VISIBLE);
    }

    public void searchFail() {
        allMoney.setText("-- ");
        allScore.setText("--");
        winMoney.setText("--");
        withdrawMoney.setText(Html.fromHtml("--"));
        userMoneySupplement.setText(Html.fromHtml("--"));
        userMoneyExpansed.setText(Html.fromHtml("--"));
    }

    private void setButtonBackGround(int index) {
        betLotteryInf.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        awardLotteryInf.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        chageLotteryInf.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        withdrawalLotteryInf.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);

        betLotteryInf.setClickable(true);
        awardLotteryInf.setClickable(true);
        chageLotteryInf.setClickable(true);
        withdrawalLotteryInf.setClickable(true);

        betLotteryInf.setTextColor(getResources().getColor(R.color.light_purple));
        awardLotteryInf.setTextColor(getResources().getColor(R.color.light_purple));
        chageLotteryInf.setTextColor(getResources().getColor(R.color.light_purple));
        withdrawalLotteryInf.setTextColor(getResources().getColor(R.color.light_purple));

        if (index == 0) {
            betLotteryInf.setClickable(false);
            betLotteryInf.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 1) {
            awardLotteryInf.setClickable(false);
            awardLotteryInf.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 2) {
            chageLotteryInf.setClickable(false);
            chageLotteryInf.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 3) {
            withdrawalLotteryInf.setClickable(false);
            withdrawalLotteryInf.setTextColor(getResources().getColor(R.color.red));
        }
        expanableListView.addLoadingFootView();
    }

    private void setButtonBackGroundBv(int index) {

        betLotteryInfBv.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        awardLotteryInfBv.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        chageLotteryInfBv.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);
        withdrawalLotteryInfBv.setBackgroundResource(R.drawable.user_new_center_tool_bar_button_bg);

        betLotteryInfBv.setClickable(true);
        awardLotteryInfBv.setClickable(true);
        chageLotteryInfBv.setClickable(true);
        withdrawalLotteryInfBv.setClickable(true);

        betLotteryInfBv.setTextColor(getResources().getColor(R.color.light_purple));
        awardLotteryInfBv.setTextColor(getResources().getColor(R.color.light_purple));
        chageLotteryInfBv.setTextColor(getResources().getColor(R.color.light_purple));
        withdrawalLotteryInfBv.setTextColor(getResources().getColor(R.color.light_purple));

        if (index == 0) {
            betLotteryInfBv.setClickable(false);
            betLotteryInfBv.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 1) {
            awardLotteryInfBv.setClickable(false);
            awardLotteryInfBv.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 2) {
            chageLotteryInfBv.setClickable(false);
            chageLotteryInfBv.setTextColor(getResources().getColor(R.color.red));
        }
        else if (index == 3) {
            withdrawalLotteryInfBv.setClickable(false);
            withdrawalLotteryInfBv.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.bet_lottery_inf) {
            animationType = ANIMA_TRANS;
            clickIndex = 0;
            setButtonBackGround(clickIndex);
            setButtonBackGroundBv(clickIndex);
            startDate = getDate(true);
            endDate = getDate(false);
            infoType = "000";
            aniMateButtonBg(arg0);
        }
        else if (arg0.getId() == R.id.award_lottery_inf) {
            animationType = ANIMA_TRANS;
            clickIndex = 1;
            setButtonBackGround(clickIndex);
            setButtonBackGroundBv(clickIndex);
            startDate = getDate(true);
            endDate = getDate(false);
            infoType = "102";
            aniMateButtonBg(arg0);
        }
        else if (arg0.getId() == R.id.charge_lottery_inf) {
            animationType = ANIMA_TRANS;
            clickIndex = 2;
            setButtonBackGround(clickIndex);
            setButtonBackGroundBv(clickIndex);
            startDate = getDate(true);
            endDate = getDate(false);
            infoType = "101";
            aniMateButtonBg(arg0);
        }
        else if (arg0.getId() == R.id.withdraw_lottery_inf) {
            animationType = ANIMA_TRANS;
            clickIndex = 3;
            setButtonBackGround(clickIndex);
            setButtonBackGroundBv(clickIndex);
            startDate = getDate(true);
            endDate = getDate(false);
            infoType = "202";
            aniMateButtonBg(arg0);
        }
        // add by vincent
        else if (arg0.getId() == R.id.caijin_title_tv) {
            Intent intent = new Intent();
            intent.setClass(UserDetailInf.this, CaiJinList.class);
            startActivity(intent);
        }
        else if (arg0.getId() == R.id.flip_button) {
            allMoney.setText("￥" + myFormatter.format(accountBalance));
            allScore.setText("￥" + Html.fromHtml(scoreFormatter.format(score)));
            winMoney.setText("￥" + myFormatter.format(totalPrize));
            flipRight();
// viewFlipRight();
            userDetailInfExpanAdapter.notifyDataSetChanged();
        }
        else if (arg0.getId() == R.id.flip_back_button) {
            withdrawMoney.setText("￥" + Html.fromHtml(myFormatter.format(withDrawMoney)));
            userMoneySupplement.setText("￥" + Html.fromHtml(myFormatter.format(totalCharge + totalPrize)));
            userMoneyExpansed.setText("￥" + Html.fromHtml(myFormatter.format(totalBuy + totalDraw)));
            flipLeft();
// viewFlipLeft();
        }
        else if (arg0.getId() == R.id.user_account_detail_withdraw) {
            Intent intent = new Intent();
            if (appState.getBand() == 0) {
                intent.setClass(UserDetailInf.this, UserWithdraw.class);
            }
            else {
                intent.setClass(UserDetailInf.this, UserWithdrawExtra.class);
            }
            startActivity(intent);
        }
    }

    boolean isLeft = true;

// private void viewFlipRight() {
// mContainer.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
// mContainer.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
// mContainer.showNext();
// }
//
// private void viewFlipLeft() {
// mContainer.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
// mContainer.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
// mContainer.showPrevious();
// }

    private void flipLeft() {
        Animation lInAnim = AnimationUtils.loadAnimation(this, R.anim.push_to_left_in); // 向左滑动左侧进入的渐变效果（alpha
// // 0.1 -> 1.0）
        Animation lOutAnim = AnimationUtils.loadAnimation(this, R.anim.push_to_left_out); // 向左滑动右侧滑出的渐变效果（alpha
// // 1.0 -> 0.1）

        mContainer.setInAnimation(lInAnim);
        mContainer.setOutAnimation(lOutAnim);
        mContainer.showNext();
    }

    private void flipRight() {
        Animation rInAnim = AnimationUtils.loadAnimation(this, R.anim.push_to_right_in); // 向右滑动左侧进入的渐变效果（alpha
        Animation rOutAnim = AnimationUtils.loadAnimation(this, R.anim.push_to_right_out); // 向右滑动右侧滑出的渐变效果（alpha

        mContainer.setInAnimation(rInAnim);
        mContainer.setOutAnimation(rOutAnim);
        mContainer.showNext();
    }

    private void aniMateButtonBg(View v) {
// int width = v.getWidth();
        if (btnArr[currentID] != v.getId()) {
            TextView currentBtn = (TextView) findViewById(btnArr[currentID]);
            if (v.getId() == R.id.bet_lottery_inf) {
                startPoint = currentBtn.getLeft();
                endPoint = v.getLeft();
            }
            else if (v.getId() == R.id.award_lottery_inf) {
                startPoint = currentBtn.getLeft();
                endPoint = v.getLeft();
            }
            else if (v.getId() == R.id.charge_lottery_inf) {
                startPoint = currentBtn.getLeft();
                endPoint = v.getLeft();
            }
            else if (v.getId() == R.id.withdraw_lottery_inf) {
                startPoint = currentBtn.getLeft();
                endPoint = v.getLeft();
            }

            TranslateAnimation animation = new TranslateAnimation(startPoint, endPoint, 0, 0);
            animation.setAnimationListener(this);
            animation.setDuration(200);
            animation.setFillAfter(true);
            btnBg.startAnimation(animation);
            btnBgBv.startAnimation(animation);
            int i = 0;
            while (i < btnArr.length) {
                if (btnArr[i] == v.getId()) {
                    currentID = i;
                }
                i++;
            }
        }
        else {
            return;
        }
    }

    @Override
    public void onPost(String result) {
        dismissProgress();
        String inf;
        if (result == null) {
            searchFail();
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(result);
            if (status.equals("200")) {
                String data = analyse.getData(result, RESPONSE);
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    balance = jo.getDouble("balance");
                    win = jo.getDouble("win_balance");
                    totalCharge = jo.getDouble("total_charge");
                    totalBuy = jo.getDouble("total_buy");
                    totalDraw = jo.getDouble("total_draw");
                    totalPrize = jo.getDouble("total_win");
                    if (jo.has("bonus"))
                        score = jo.getDouble("bonus");
                    accountBalance = balance;
                    allMoney.setText("￥" + myFormatter.format(accountBalance));
                    allScore.setText("￥" + Html.fromHtml(scoreFormatter.format(score)));
                    winMoney.setText("￥" + myFormatter.format(totalPrize));

                    if (balance > win)
                        withDrawMoney = win;
                    else
                        withDrawMoney = balance;

                    withdrawMoney.setText("￥" + Html.fromHtml(myFormatter.format(withDrawMoney)));
                    userMoneySupplement.setText("￥" +
                        Html.fromHtml(myFormatter.format(totalCharge + totalPrize)));
                    userMoneyExpansed.setText("￥" + Html.fromHtml(myFormatter.format(totalBuy + totalDraw)));
                    userDetailInfExpanAdapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    searchFail();
                }
            }
            else if (status.equals("202")) {
                inf = NO_ADD_INF;
                disappearContent(inf);
            }
            else if (result.equals("302")) {
                OperateInfUtils.clearSessionId(UserDetailInf.this);
                inf = getResources().getString(R.string.login_timeout);
                disappearContent(inf);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (result.equals("304")) {
                OperateInfUtils.clearSessionId(UserDetailInf.this);
                inf = getResources().getString(R.string.login_again);
                disappearContent(inf);
                showLoginAgainDialog(getResources().getString(R.string.login_again));
            }
            else {
                searchFail();
            }
        }
    }

    @Override
    public void onPre() {
        showProgress();
    }

    private void showNoData() {
        if (accountDetailArrayList.size() == 0) {
            showTipsPage(NO_DATA);
        }
        else {
            ViewUtil.showTipsToast(this, "现只能查询最近3个月的数据");
        }
        expanableListView.removeLoadingFootView();
    }

    class UserAccountDetailTask
        extends AsyncTask<Integer, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            String inf;
            mPullToRefreshView.onHeaderRefreshComplete();
            dismissTipsPage();
            dismissProgress();
            if (result == null) {
                String inf1 = getString(R.string.search_fail);
                searchInfDetailFail(inf1);
                showFail(inf1);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, RESPONSE);
                    if (data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        int lastSize = accountDetailArrayList.size();
                        accountDetailResponseData(data);
                        int preSize = accountDetailArrayList.size();
                        if (preSize - lastSize < 10) {
                            expanableListView.removeLoadingFootView();
                        }
                        else {
                            expanableListView.addLoadingFootView();
                            expanableListView.readyToLoad();
                        }
                        page_num++;
                        userDetailInfExpanAdapter.notifyDataSetChanged();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserDetailInf.this);
                    inf = getResources().getString(R.string.login_timeout);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserDetailInf.this);
                    inf = getResources().getString(R.string.login_again);
                    searchInfDetailFail(inf);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(getString(R.string.search_fail));
                }
            }
            startDate = getDate(true);
            endDate = getDate(false);
        }

        private void accountDetailResponseData(String data) {
            try {
                JSONArray hallArray = new JSONArray(data);
                for (int i = 0; i < hallArray.length(); i++) {
                    UserAccountDetail uDT = new UserAccountDetail();
                    uDT.setType(hallArray.getJSONObject(i).getString("t"));
                    uDT.setMoney(hallArray.getJSONObject(i).getString("m"));
                    uDT.setDate(hallArray.getJSONObject(i).getString("d"));
                    if (hallArray.getJSONObject(i).getInt("a") == -1) {
                        uDT.setBalance("-1");
                        uDT.setDescription("-1");
                        uDT.setOrderId("-1");
                    }
                    else {
                        uDT.setBalance(hallArray.getJSONObject(i).getString("a"));
                        uDT.setDescription(hallArray.getJSONObject(i).getString("r"));
                        uDT.setOrderId(hallArray.getJSONObject(i).getString("o"));
                    }
                    accountDetailArrayList.add(uDT);
                }
                getDataDate();
                getEachDateDetail();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Integer... kind) {
            ConnectService connectNet = new ConnectService(UserDetailInf.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private String formateDate(String riqi) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        return dateFormat.format(stringConvertToDate(riqi));
    }

    private Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            toDate = dateFormat.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    // 左边日期
    private void getDataDate() {
        set.clear();
        dateArray.clear();
        set.add(formateDate(accountDetailArrayList.get(0).getDate()));
        dateArray.add(formateDate(accountDetailArrayList.get(0).getDate()));
        for (int i = 1; i < accountDetailArrayList.size(); i++) {
            if (!set.contains(formateDate(accountDetailArrayList.get(i).getDate()))) {
                dateArray.add(formateDate(accountDetailArrayList.get(i).getDate()));
                set.add(formateDate(accountDetailArrayList.get(i).getDate()));
            }
        }
    }

    // 整合数据（MAP）
    private void getEachDateDetail() {
        userAccountDetailListSame.clear();
        for (int i = 0; i < dateArray.size(); i++) {
            boolean isDate = true;
            for (int j = 0; j < accountDetailArrayList.size(); j++) {
                if (dateArray.get(i).equals(formateDate(accountDetailArrayList.get(j).getDate()))) {
                    // 判断列表项的奇偶
// if (i % 2 == 0)
// accountDetailArrayList.get(j).setIsSingleBg(R.drawable.user_list_item_click_dublicate);
// else
                    accountDetailArrayList.get(j).setIsSingleBg(R.drawable.list_bg_no_line);
                    // 是否需要分界线
                    accountDetailArrayList.get(j).setIsListDiviverLeft(R.drawable.user_account_detail_list_left_bottom_line);
                    accountDetailArrayList.get(j).setIsListDiviverRight(R.drawable.list_divider_right);

                    // 处理列表左侧的时间
                    Date accountDetailDate =
                        TimeUtils.stringConvertToDate(accountDetailArrayList.get(j).getDate());
                    if (isDate) {
                        accountDetailArrayList.get(j).setMonth(reconstructDateInfMonthAndDay(dateArray.get(i),
                                                                                             0) +
                                                                   "月");
                        accountDetailArrayList.get(j).setDayOfMonth(reconstructDateInfMonthAndDay(dateArray.get(i),
                                                                                                  1));
                        accountDetailArrayList.get(j).setDayOfweek("周" +
                                                                       reconstructDateInfDayOfWeek(accountDetailDate));
                    }
                    else {
                        accountDetailArrayList.get(j).setMonth("");
                        accountDetailArrayList.get(j).setDayOfMonth("");
                        accountDetailArrayList.get(j).setDayOfweek("");
                    }
                    String type = accountDetailArrayList.get(j).getType();
                    accountDetailArrayList.get(j).setTextOfType(generateInfoType(type));
                    accountDetailArrayList.get(j).setInOrOut(type.substring(0, type.length() - 2));

                    String date = accountDetailArrayList.get(j).getDate();
                    accountDetailArrayList.get(j).setTime(formateTime(date));

                    userAccountDetailListSame.add(accountDetailArrayList.get(j));
                    isDate = false;
                }
            }
            userAccountDetailListSame.get(userAccountDetailListSame.size() - 1).setIsListDiviverLeft(R.drawable.list_divider_left);
            userAccountDetailListSame.get(userAccountDetailListSame.size() - 1).setIsListDiviverRight(R.drawable.list_divider_right);
        }
        for (int n = 0; n < userAccountDetailListSame.size(); n++) {
            clickStatus.add(false);
        }
    }

    private String reconstructDateInfMonthAndDay(String date, int type) {
        String[] dateStr = date.split("\\-");
        if (type == 0)
            return String.valueOf(Integer.parseInt(dateStr[0]));
        else if (type == 1)
            return String.valueOf(dateStr[1]);
        return "";
    }

    private String reconstructDateInfDayOfWeek(Date dt) {
        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    private String generateInfoType(String infType) {
        String infoType = "";
        String infoInput = infType.substring(0, infType.length() - 2);
        if (infoInput.equals("1")) {
            infoType = infType.substring(infType.length() - 1, infType.length());
            return LotteryUtils.popMenuInBtnText[Integer.valueOf(infoType) - 1];
        }
        else if (infoInput.equals("2")) {
            infoType = infType.substring(infType.length() - 1, infType.length());
            return LotteryUtils.popMenuOutBtnText[Integer.valueOf(infoType) - 1];
        }
        return null;
    }

    private String formateTime(String riqi) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(stringConvertToDate(riqi));
    }

    private HashMap<String, String> iniHashMap() {
        LotteryApp appState = ((LotteryApp) UserDetailInf.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005071");
        parameter.put("pid", LotteryUtils.getPid(UserDetailInf.this));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("type", infoType);
        parameter.put("start", startDate);
        parameter.put("end", endDate);
        parameter.put("page_no", String.valueOf(page_num));
        parameter.put("size", "10");
        return parameter;
    }

    private String getDate(boolean isStart) {
        String date = "";
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        if (isStart) {
            c.add(Calendar.MONTH, -3);
            date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        }
        else {
            date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        }
        return date;
    }

    private void reGetDataFromServer() {
        dismissTipsPage();
        page_num = 1;
        accountDetailArrayList.clear();
        userAccountDetailListSame.clear();
        userDetailInfExpanAdapter.notifyDataSetChanged();
        expanableListView.readyToLoad();
        expanableListView.addLoadingFootView();
    }

    private String dayDura = "90";

    @Override
    protected void submitData() {
        String eventName = "open_user_account_details";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UserDetailInf.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(UserDetailInf.this)) {
            UserAccountDetailTask ask = new UserAccountDetailTask();
            ask.execute(1);
        }
        else {
            showFail(noNetTips);
        }
    }

// private LinearLayout backView;
// private LinearLayout foreView;
// private int direction;
// private boolean isZoom = true;
// public View view1;
// public View view2;

// public void onSwitchClick(boolean iszoom, int directin_s) {
// view1 = foreView;
// view2 = backView;
// direction = directin_s;
// View animView = null;
// // 获取目前显示的视图对象
// animView = view1.getVisibility() == View.VISIBLE ? view1 : view2;
// // 定义一个旋转到远处消失的动画效果
// Rotate3dAnimation animation = new Rotate3dAnimation(isZoom, direction);
// // 监听动画运行事件,因为当动画结束后(视图旋转到远处消失时)
// // 需要对原来隐藏的视图执行从远处旋转到近处显示的动画
// animation.setAnimationListener(this);
//
// // 开始执行旋转到远处消失的动画效果
// animView.startAnimation(animation);
// }

    @Override
    public void onAnimationEnd(Animation arg0) {
        if (animationType == ANIMA_TRANS) {
            reGetDataFromServer();
            System.out.println("UserAccountDetailTask:==============1");
        }
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    @Override
    public void onHeaderRefresh(RefreshableView view) {
        System.out.println("UserAccountDetailTask:==============2");
        reGetDataFromServer();
    }

    @Override
    public void processListItemPosition(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                        int totalItemCount) {
        ListView listView = (ListView) view;
        int listChildViewTop = 0;
        if (listView.getChildAt(firstVisibleItem) != null) {
            int viewHeight = 46;
            viewHeight = barView.getMeasuredHeight();
            if (firstVisibleItem == 0) {
                listChildViewTop = listView.getChildAt(0).getHeight() + listView.getChildAt(0).getTop();
                indicatorGroup.setVisibility(View.INVISIBLE);
                setUpHeaderViewButton(headView);
                if (listChildViewTop <= viewHeight)
                    indicatorGroup.setVisibility(View.VISIBLE);
// indicatorGroup.setVisibility(View.INVISIBLE);
            }
            else {
                if (indicatorGroup.getVisibility() == View.INVISIBLE)
                    indicatorGroup.setVisibility(View.VISIBLE);
// indicatorGroup.setVisibility(View.INVISIBLE);
            }
        }

    }
}