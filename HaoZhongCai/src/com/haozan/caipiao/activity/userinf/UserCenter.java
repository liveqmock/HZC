package com.haozan.caipiao.activity.userinf;

import java.text.DecimalFormat;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.CaiJinList;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.PerfectInf;
import com.haozan.caipiao.activity.UserWithdraw;
import com.haozan.caipiao.activity.UserWithdrawExtra;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.adapter.UserNewCenterAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.types.userinf.UserCenterBetHistoryItem;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.AutoLoadListView.GetListItemPositionListener;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人中心
 * 
 * @author peter_wang
 * @create-time 2013-9-17 下午10:21:34
 */
public class UserCenter
    extends BasicActivity
    implements OnClickListener, OnGetUserInfListener, PopMenuButtonClickListener, LoadDataListener,
    OnHeaderRefreshListener, GetListItemPositionListener, OnTabSelectedItemListener, OnGetDataAgainListener {
    private static final int BET_HISTORY_WHOLE = 0;
    private static final int BET_HISTORY_WAIT_OPEN = 1;
    private static final int BET_HISTORY_AWARD = 2;
    private static final int BET_HISTORY_PURSUIT = 3;
    private static final int BET_HISTORY_GROUPBUY = 4;

    private static final String[] TAB_CONTENT = new String[] {"全部", "待开奖", "中奖", "追号", "合买"};

    private static final String NO_DATA = "没有交易记录\n到购彩页面试试运气吧..";

    private static final int PERFECT_INF = 30;
    private static final int PERFECT_INF_RESULT = 31;
    private static final int PERFECT_INF_DRAW = 32;

    // 菜单列表序号
    private static final int USER_MSG = 0;
    private static final int TOPUP_REG = 1;
// private static final int WITHDRAW = 2;
    private static final int GARDEN_INF = 2;
    private static final int PERSONAL_INF = 3;
    private static final int AWARD_NOTICE = 4;
    private static final int FEED_BACK = 5;

    private TopMenuLayout topMenuBarLayout;
    private TopMenuLayout topMenuLayout;
    private ImageButton iBTopMenu;

    private AutoLoadListView autoListView;
    private UserNewCenterAdapter userNewCenterAdapter;

    private RelativeLayout warningInfLayou;
    private TextView tvTopup;
    private TextView tvWithdraw;

    private ArrayList<UserCenterBetHistoryItem> betHistoryList;
    private Set<String> set = new HashSet<String>();
    private ArrayList<String> dateArray = new ArrayList<String>();
    private int clickIndex = 0;

    // Condition of activated the server
    private int q_bet = 9;
    private int q_win = 9;
    private String startDate;

    // user new center header view
    private TextView nickName;
    private LinearLayout layoutScore;
    private TextView tvScore;
    private LinearLayout layoutBalance;
    private TextView tvBalance;

    private EmptyLayout layoutsEmpty;

    private PopMenu titlePopup;
    private String[] titlePopupTextName = {"消息", "充值记录", "财园资料", "个人信息", "中奖通知", "反馈"};
    private String[] topBarMenuClassName = {"userinf.MessageCenter", "userinf.TopupHistory",
            "weibo.MyProfileActivity", "UserInf_ModifyPassword", "AdditionalService", "Feedback"};

    private int listDataType = LotteryUtils.AlL_lOTTERY_DATA;

    // 短信中奖通知
    boolean isRegister = false;

    private BetHistoryTask betHistoryTask;

    // 下拉刷新
    private RefreshLayout mPullToRefreshView;
    private String registerType;

    private boolean isRefresh = true;

    private boolean isSuccessGetData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        betHistoryList = new ArrayList<UserCenterBetHistoryItem>();
    }

    private void setupViews() {
        setupMainViews();
        setupHeaderViews();
    }

    private void setupMainViews() {
        mPullToRefreshView = (RefreshLayout) findViewById(R.id.user_new_center_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);

        autoListView = (AutoLoadListView) findViewById(R.id.bet_history_list);
        autoListView.setOnLoadDataListener(this);
        autoListView.setOnGetListItemPositionListener(this);
        warningInfLayou = (RelativeLayout) findViewById(R.id.warning_inf_layout);
        warningInfLayou.setOnClickListener(this);

        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTabSelectedListener(this);
        iBTopMenu = (ImageButton) this.findViewById(R.id.top_menu);
        iBTopMenu.setOnClickListener(this);

        layoutsEmpty = (EmptyLayout) this.findViewById(R.id.show_fail_page);
        layoutsEmpty.setOnGetDataAgainListener(this);
    }

    private void setupHeaderViews() {
        View headView = View.inflate(this, R.layout.user_new_center_list_header, null);

        layoutScore = (LinearLayout) headView.findViewById(R.id.user_new_center_score_detail);
        layoutScore.setOnClickListener(this);
        layoutBalance = (LinearLayout) headView.findViewById(R.id.user_new_center_account_detail);
        layoutBalance.setOnClickListener(this);
        tvScore = (TextView) headView.findViewById(R.id.user_new_center_header_points);
        tvBalance = (TextView) headView.findViewById(R.id.user_new_center_header_balances);
        tvTopup = (TextView) headView.findViewById(R.id.top_up);
        tvTopup.setOnClickListener(this);
        nickName = (TextView) headView.findViewById(R.id.user_nick_name);
        nickName.setOnClickListener(this);
        tvWithdraw = (TextView) headView.findViewById(R.id.withdraw);
        tvWithdraw.setOnClickListener(this);
        tvWithdraw.setText(Html.fromHtml("<u><font color='#FFFFFF'>奖金提现></font></u>"));

        topMenuBarLayout = (TopMenuLayout) headView.findViewById(R.id.top_menu_bar_layout);
        topMenuBarLayout.setTabSelectedListener(this);

        autoListView.addHeaderView(headView, null, false);
    }

    private void checkPerfectInf() {
        // 如果没有完善信息弹出提示框，提示用进行完善信息 0:没完善信息
        if ("0".equals(appState.getPerfectInf()) || !"1".equals(appState.getRegisterType()) &&
            "".equals(appState.getReservedPhone())) {
            warningInfLayou.setVisibility(View.VISIBLE);
        }
        else {
            warningInfLayou.setVisibility(View.GONE);
        }
    }

    private void init() {
        // 注册类型 1.普通方式 2.sina 3.qq
        registerType = appState.getRegisterType();
        startDate = getDate(-3);

        userNewCenterAdapter = new UserNewCenterAdapter(UserCenter.this, betHistoryList);
        autoListView.setAdapter(userNewCenterAdapter);
        onRefresh();
        // 判断是否有订阅中奖短信通知
        if (appState.getServiceed() != null) {
            if (getSmsNoticeservice(appState.getServiceed())) {
                isRegister = true;
                titlePopupTextName[AWARD_NOTICE] = "中奖通知（已订阅）";
            }
            else {
                isRegister = false;
                titlePopupTextName[AWARD_NOTICE] = "中奖通知";
            }
        }

        topMenuLayout.setTopMenuItemContent(TAB_CONTENT);
        topMenuBarLayout.setTopMenuItemContent(TAB_CONTENT);
    }

    private boolean getSmsNoticeservice(String serviceCode) {
        String[] serviceCodeArray = serviceCode.split("\\,");
        for (int i = 0; i < serviceCodeArray.length; i++)
            if (serviceCodeArray[i].equals("WSN"))
                return true;
        return false;
    }

    private void showPopView() {
        titlePopup = new PopMenu(UserCenter.this, false);
        // 设定类型 如果类型是unc就用列表的形式显示。
        titlePopup.setLotteryType("unc");
        titlePopup.setLayout(R.layout.pop_list_view, titlePopupTextName, null, 3,
                             findViewById(R.id.title_layout).getWidth() - 150, -1, false, false);
        titlePopup.setButtonClickListener(this);
        titlePopup.showAtLocation(mPullToRefreshView, Gravity.RIGHT | Gravity.TOP, 10,
                                  findViewById(R.id.title_layout).getHeight() + 40);
    }

    private void showFail(String failInf) {
        if (betHistoryList.size() == 0) {
            autoListView.loadNoMoreData();
            layoutsEmpty.setVisibility(View.VISIBLE);
            layoutsEmpty.showFailPage();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
            autoListView.showFootText();
        }
    }

    private void showNoData() {
        if (betHistoryList.size() == 0) {
            autoListView.loadNoMoreData();
            layoutsEmpty.setVisibility(View.VISIBLE);
            layoutsEmpty.showNoDataPage(NO_DATA);
        }
        else {
            ViewUtil.showTipsToast(this, "现只能查询最近3个月的数据");
        }
    }

    int page_num = 1;

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open user account details");
        String eventName = "v2 open user account details";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.caijin_title_tv) {
            Intent intent = new Intent();
            intent.setClass(UserCenter.this, CaiJinList.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.top_menu) {
            showPopView();
        }
        else if (view.getId() == R.id.top_up) {
            ActionUtil.toTopupNew(this);
        }
        else if (view.getId() == R.id.user_new_center_score_detail) {
            Intent intent = new Intent();
            intent.setClass(UserCenter.this, UserScoreInf.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.user_new_center_account_detail) {
            Intent intent = new Intent();
            intent.setClass(UserCenter.this, UserDetailInf.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.warning_inf_layout) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("origin", 2);
            bundle.putString("commer_class", "UserCenter");
            intent.putExtras(bundle);
            intent.setClass(UserCenter.this, PerfectInf.class);
            startActivityForResult(intent, PERFECT_INF);
        }
        else if (view.getId() == R.id.user_nick_name) {
            Intent intent = new Intent();
            intent.setClass(UserCenter.this, MyProfileActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.withdraw) {
            Intent intent = new Intent();
            if ("0".equals(appState.getPerfectInf()) || !"1".equals(appState.getRegisterType()) &&
                "".equals(appState.getReservedPhone())) {
                Bundle bundle = new Bundle();
                bundle.putInt("origin", 2);
                bundle.putString("commer_class", "UserCenter");
                intent.putExtras(bundle);
                intent.setClass(UserCenter.this, PerfectInf.class);
                startActivityForResult(intent, PERFECT_INF_DRAW);
            }
            else {
                if (appState.getBand() == 0) {
                    intent.setClass(UserCenter.this, UserWithdraw.class);
                }
                else if (appState.getBand() == 1) {
                    intent.setClass(UserCenter.this, UserWithdrawExtra.class);
                }
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HttpConnectUtil.isNetworkAvailable(UserCenter.this)) {
            if (appState.getUsername() != null) {
                if (appState.getMessageNumber() <= 0) {
                    GetMessageTask task = new GetMessageTask();
                    task.execute();
                }

                if (isSuccessGetData == false) {
                    onRefresh();
                }
            }
        }
        checkPerfectInf();
    }

    @Override
    public void onPost(String result) {
        if (result == null) {
            ViewUtil.showTipsToast(this, getString(R.string.search_fail));
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(result);
            if (status.equals("200")) {
                String data = analyse.getData(result, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    double balance = jo.getDouble("balance");
                    double score = jo.getDouble("score");
                    // 设置全局变量
                    appState.setNickname(jo.getString("nickname"));

                    appState.setScore(jo.getInt("score"));
                    appState.setAccount(Double.valueOf(jo.getString("balance")));
                    appState.setBand(jo.getInt("is_band"));

                    String balanceText;
                    if (balance > 10000) {
                        balanceText = "￥" + (int) (balance / 10000) + "万+";
                    }
                    else {
                        DecimalFormat myFormatter = new DecimalFormat("###,##0.00");
                        balanceText = "￥" + myFormatter.format(balance);
                    }
                    tvBalance.setText(balanceText);

                    String scoreText;
                    if (score > 100000) {
                        scoreText = (int) (score / 10000) + "万+";
                    }
                    else {
                        DecimalFormat scoreFormatter = new DecimalFormat("##");
                        scoreText = scoreFormatter.format(score);
                    }
                    tvScore.setText(scoreText);

                    nickName.setText(jo.getString("nickname"));
                }
                catch (JSONException e) {
                    ViewUtil.showTipsToast(this, getString(R.string.search_fail));
                }
            }
            else if (status.equals("202")) {

            }
            else if (result.equals("302")) {
                OperateInfUtils.clearSessionId(UserCenter.this);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (result.equals("304")) {
                OperateInfUtils.clearSessionId(UserCenter.this);
                showLoginAgainDialog(getResources().getString(R.string.login_again));
            }
            else
                ViewUtil.showTipsToast(this, getString(R.string.search_fail));
        }
    }

    @Override
    public void onPre() {
    }

    class BetHistoryTask
        extends AsyncTask<Integer, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            mPullToRefreshView.onHeaderRefreshComplete();
            if (result == null) {
                showFail(getString(R.string.search_fail));
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    isSuccessGetData = true;

                    String data = analyse.getData(result, "response_data");
                    if (data.equals("[]"))
                        showNoData();
                    else {
                        if (isRefresh) {
                            isRefresh = false;
                            betHistoryList.clear();
                        }

                        int lastSize = betHistoryList.size();
                        userBetHistoryResponseData(data);
                        int preSize = betHistoryList.size();
                        if (preSize - lastSize < 10)
                            autoListView.loadNoMoreData();
                        else {
                            autoListView.readyToLoad();
                        }
                        page_num++;
                        userNewCenterAdapter.notifyDataSetChanged();
                    }
                }
                else if (status.equals("202"))
                    showNoData();
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserCenter.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserCenter.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else
                    showFail(getString(R.string.search_fail));
            }
        }

        private void userBetHistoryResponseData(String data) {
            try {
                JSONArray hallArray = new JSONArray(data);
                int oldLength = betHistoryList.size();
                for (int i = 0; i < hallArray.length(); i++) {
                    JSONObject jsonObject = hallArray.getJSONObject(i);
                    UserCenterBetHistoryItem item = new UserCenterBetHistoryItem();
                    item.setType(jsonObject.getInt("type"));
                    item.setOrderId(jsonObject.getString("no"));
                    item.setId(jsonObject.getString("lot_id"));
                    item.setName(LotteryUtils.getLotteryName(item.getId()));
                    item.setTerm(jsonObject.getString("term"));

                    String date = jsonObject.getString("time");
                    String month =
                        StringUtil.deleteZeroPrefix(TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm:ss", "MM"));
                    String day = TimeUtils.convertDate(date, "yyyy-MM-dd HH:mm:ss", "dd");
                    item.setMonth(month + "月");
                    item.setDay(day);
                    item.setDayOfWeek(TimeUtils.dayOfWeek(TimeUtils.stringConvertToDate(date)));
                    if (i == 0 && oldLength == 0) {
                        item.setShowDate(true);
                    }
                    else if (i == hallArray.length()) {
                        item.setShowFinished(true);
                    }
                    else {
                        String lastMonth = betHistoryList.get(oldLength + i - 1).getMonth();
                        String lastDay = betHistoryList.get(oldLength + i - 1).getDay();
                        if (lastMonth.equals(month) && lastDay.equals(day)) {
                            item.setShowDate(false);
                            betHistoryList.get(oldLength + i - 1).setShowFinished(false);
                        }
                        else {
                            item.setShowDate(true);
                            betHistoryList.get(oldLength + i - 1).setShowFinished(true);
                        }
                    }

                    item.setMoney(jsonObject.getDouble("money"));

                    int lotteryOrderStatus = jsonObject.getInt("bet");
                    int lotteryOrderWinStatus = jsonObject.getInt("win");
                    double prize = jsonObject.getDouble("prize");

                    setOrderInf(item, lotteryOrderStatus, item.getType(), lotteryOrderWinStatus, prize);
                    betHistoryList.add(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void setOrderInf(UserCenterBetHistoryItem item, int lotteryOrderStatus, int lotteryOrdertype,
                                 int lotteryOrderWinStatus, double prize) {
            // 购彩类型 1＝普通投注 2＝追号投注 3＝发起合买4＝参与合买5＝发起赠送6＝ 接受赠送
            // 记录状态（9＝处理中，0＝成功/进行中，1＝追号完成，2＝失败，3＝撤销/手工停止）
            item.setWinMoney(prize);

            if (listDataType == LotteryUtils.AlL_lOTTERY_DATA ||
                listDataType == LotteryUtils.WAIT_lOTTERY_DATA ||
                listDataType == LotteryUtils.PURSUIT_lOTTERY_DATA ||
                listDataType == LotteryUtils.UNITE_lOTTERY_DATA) {
                if (((lotteryOrderStatus == 0 && lotteryOrdertype != 2) || (lotteryOrderStatus != 0 && lotteryOrdertype == 2)) &&
                    lotteryOrderWinStatus == 1) {
                    item.setStatusColor(getResources().getColor(R.color.red));
                    item.setStatus("中" + prize + "元");
                    item.setTypeDescription(getOrderType(lotteryOrdertype));
                    item.setAward(true);
                }
                else if (lotteryOrderStatus == 0 && lotteryOrdertype == 2 && lotteryOrderWinStatus == 1) {
                    item.setStatusColor(getResources().getColor(R.color.red));
                    item.setStatus("进行中,中" + prize + "元");
                    item.setTypeDescription(getOrderType(lotteryOrdertype));
                    item.setAward(true);
                }
                else if (lotteryOrdertype != 5 && lotteryOrderStatus == 0 && lotteryOrderWinStatus == 0) {
                    item.setStatusColor(getResources().getColor(R.color.light_purple));
                    item.setStatus("未中奖");
                    item.setTypeDescription(getOrderType(lotteryOrdertype));
                    item.setAward(false);
                }
                else {
                    if (checkEmphasize(lotteryOrdertype, lotteryOrderStatus)) {
                        item.setStatusColor(getResources().getColor(R.color.black));
                    }
                    else {
                        item.setStatusColor(getResources().getColor(R.color.light_purple));
                    }
                    item.setStatus(getBetStatus(lotteryOrdertype, lotteryOrderStatus));
                    item.setTypeDescription(getOrderType(lotteryOrdertype));
                    item.setAward(false);
                }
            }
            else if (listDataType == LotteryUtils.WIN_lOTTERY_DATA) {
                if (lotteryOrderWinStatus == 1) {
                    item.setStatusColor(getResources().getColor(R.color.red));
                    item.setStatus("中" + String.valueOf(prize) + "元");
                    item.setTypeDescription(getOrderType(lotteryOrdertype));
                    item.setAward(true);
                }
                else {
                    item.setStatusColor(getResources().getColor(R.color.black));
                    item.setAward(false);
                }
            }
        }

        private boolean checkEmphasize(int type, int status) {
            if ((type == 1 || type == 5 || type == 6) && status == 0)
                return true;
            else if (type == 2 && status == 0)
                return true;
            else if ((type == 3 && status == 0) || (type == 4 && status == 0))
                return true;
            else if ((type == 1 || type == 5 || type == 6) && status == 9)
                return true;
            else if ((type == 3 || type == 4) && status == 9)
                return true;
            return false;
        }

        private String getBetStatus(int type, int status) {
            if ((type == 1 || type == 6) && status == 0)
                return "出票成功";
            if (type == 5 && status == 0)
                return "赠送成功";
            else if (type == 2 && status == 0)
                return "进行中";
            else if ((type == 3 && status == 0) || (type == 4 && status == 0))
                return "合买出票成功";
            else if (type == 2 && status == 1)
                return "追号完成";
            else if (type == 5 && status == 2)
                return "赠送失败退款";
            else if (status == 2)
                return "失败退款";
            else if (type == 2 && status == 3)
                return "手动停止";
            else if ((type == 3 && status == 3) || (type == 4 && status == 3))
                return "撤销退款";
            else if ((type == 1 || type == 5 || type == 6) && status == 9)
                return "处理中";
            else if ((type == 3 || type == 4) && status == 9)
                return "进行中";
            return "";
        }

        private String getOrderType(int status) {
            if (status == 1)
                return "普通投注";
            else if (status == 2)
                return "追号投注";
            else if (status == 3)
                return "发起合买";
            else if (status == 4)
                return "参与合买";
            else if (status == 5)
                return "发起赠送";
            else if (status == 6)
                return "接受赠送";
            return "";
        }

        @Override
        protected void onPreExecute() {
            layoutsEmpty.setVisibility(View.GONE);
            autoListView.restoreListView();
        }

        private HashMap<String, String> iniHashMap() {
            LotteryApp appState = ((LotteryApp) UserCenter.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "3003161");
            parameter.put("pid", LotteryUtils.getPid(UserCenter.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("q_bet", String.valueOf(q_bet));
            parameter.put("q_win", String.valueOf(q_win));
            parameter.put("lottery_id", "");
            parameter.put("start", startDate);
            parameter.put("end", getDate(0));
            parameter.put("page_no", String.valueOf(page_num));
            parameter.put("size", "10");
            return parameter;
        }

        @Override
        protected String doInBackground(Integer... kind) {
            ConnectService connectNet = new ConnectService(UserCenter.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(8, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private String getDate(int skipMonth) {
        String date = "";
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, skipMonth);
        date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return date;
    }

    @Override
    public void setPopMenuButtonClickListener(String dateStart, String dateEnd, String dayDuration, int kind,
                                              String tabName) {
        titlePopup.dismiss();
        String packName = LotteryUtils.getPackageName(this) + ".activity";

        if (appState.getBand() == 0 && tabName.equals("提现")) {
            topBarMenuClassName[kind] = "UserWithdraw";
        }
        else if (appState.getBand() == 1 && tabName.equals("提现")) {
            topBarMenuClassName[kind] = "UserWithdrawExtra";
        }

        String clsName = topBarMenuClassName[kind];

        Class<?> c = null;
        Intent intent = new Intent();

        if (!clsName.equals("")) {
            try {
                c = Class.forName(packName + "." + clsName);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (tabName.contains("中奖通知")) {
            if ("1".equals(registerType))
                SMSNoticeService(intent, c);
            else if ("2".equals(registerType) && !"0".equals(appState.getPerfectInf()))
                SMSNoticeService(intent, c);
            else if ("3".equals(registerType) && !"0".equals(appState.getPerfectInf()))
                SMSNoticeService(intent, c);
            else {
                Bundle bundle = new Bundle();
                bundle.putInt("origin", 2);
                bundle.putString("commer_class", "UserCenter");
                intent.putExtras(bundle);
                intent.setClass(UserCenter.this, PerfectInf.class);
                startActivityForResult(intent, PERFECT_INF);
            }

        }
        else if (tabName.equals("反馈")) {
            intent.setClass(this, c);
            startActivityForResult(intent, 1);
        }
        else if (kind == 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("message_type", 1);
            intent.putExtras(bundle);
            intent.setClass(this, c);
            startActivity(intent);
        }
        else {
            intent.setClass(this, c);
            startActivity(intent);
        }
    }

    private void SMSNoticeService(Intent intent, Class<?> c) {
        intent.setClass(this, c);
        startActivityForResult(intent, 2);
    }

    @Override
    public void loadData() {
        if (betHistoryTask != null)
            betHistoryTask.cancel(true);
        if (HttpConnectUtil.isNetworkAvailable(UserCenter.this)) {
            betHistoryTask = new BetHistoryTask();
            betHistoryTask.execute(1);
        }
        else {
            mPullToRefreshView.onHeaderRefreshComplete();

            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (betHistoryList.size() == 0) {
            layoutsEmpty.setVisibility(View.VISIBLE);
            layoutsEmpty.showNetErrorPage();
            autoListView.removeLoadingFootView();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
            autoListView.showFootText();
        }
    }

    private void getUserInf() {
        if (HttpConnectUtil.isNetworkAvailable(UserCenter.this)) {
            UserInfTask getUserInf = new UserInfTask(UserCenter.this);
            getUserInf.setOnGetUserInfListener(this);
            getUserInf.execute(3);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getUserInf();
        }
        else if (requestCode == 2 && resultCode == 3) {
            isRegister = true;
            titlePopupTextName[AWARD_NOTICE] = "中奖通知(已订阅)";
        }
        if (requestCode == 2 && resultCode == 4) {
            isRegister = false;
            titlePopupTextName[AWARD_NOTICE] = "中奖通知";
        }

        if (requestCode == PERFECT_INF_DRAW && resultCode == PERFECT_INF_RESULT) {
            Intent intent = new Intent();
            if (appState.getBand() == 0) {
                intent.setClass(UserCenter.this, UserWithdraw.class);
            }
            else if (appState.getBand() == 1) {
                intent.setClass(UserCenter.this, UserWithdrawExtra.class);
            }
            startActivity(intent);
        }
    }

    /**
     * 获取大厅用户消息数
     * 
     * @author peter_feng
     * @create-time 2012-8-14 下午03:00:19
     */
    class GetMessageTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001020");
            parameter.put("pid", LotteryUtils.getPid(UserCenter.this));
            parameter.put("type", "2");
            parameter.put("phone", appState.getUsername());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(UserCenter.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String msgNum = analyse.getData(json, "num");
                    titlePopupTextName[USER_MSG] = "消息|" + msgNum + "未读";
                    appState.setMessageNumber(Integer.valueOf(msgNum));
                    OperateInfUtils.broadcast(UserCenter.this, "invalidate_notice");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "留言板").setIcon(R.drawable.icon_feedback_list);
        if (LotteryUtils.getPid(UserCenter.this).equals("101201"))
            menu.add(0, 10, 0, "退出彩票").setIcon(R.drawable.icon_exit);
        else
            menu.add(0, 10, 0, "退  出").setIcon(R.drawable.icon_exit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        Map<String, String> map = new HashMap<String, String>();
        String eventName;
        switch (item.getItemId()) {
            case 1:
                map.put("inf", "username [" + appState.getUsername() + "]: user center click feedback list");
                eventName = "v2 user center click feedback list";
                FlurryAgent.onEvent(eventName, map);
                besttoneEventCommint(eventName);
                Bundle bundle = new Bundle();
                bundle.putString("about", "vertical");
                bundle.putBoolean("if_my_advice", false);
                intent.putExtras(bundle);
                intent.setClass(UserCenter.this, Feedback.class);
                startActivity(intent);
                return true;
            case 10:
                exit();
        }
        return false;
    }

    @Override
    public void processListItemPosition(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                        int totalItemCount) {

        ListView listView = (ListView) view;
        int listChildViewTop = 0;
        if (listView.getChildAt(firstVisibleItem) != null) {
            int viewHeight = 46;
            viewHeight = topMenuBarLayout.getMeasuredHeight();
            if (firstVisibleItem == 0) {
                listChildViewTop = listView.getChildAt(0).getHeight() + listView.getChildAt(0).getTop();
                topMenuLayout.setVisibility(View.INVISIBLE);
                if (listChildViewTop <= viewHeight) {
                    topMenuLayout.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (topMenuLayout.getVisibility() == View.INVISIBLE) {
                    topMenuLayout.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (clickIndex == selection) {
            return;
        }
        clickIndex = selection;
        topMenuBarLayout.check(clickIndex);
        topMenuLayout.check(clickIndex);

        if (selection == BET_HISTORY_AWARD) {
            startDate = getDate(-3);
            q_bet = 9;
            q_win = 1;
        }
        else if (selection == BET_HISTORY_WAIT_OPEN) {
            startDate = getDate(-3);
            q_bet = 9;
            q_win = 2;
        }
        else if (selection == BET_HISTORY_PURSUIT) {
            startDate = getDate(-12);
            q_bet = 2;
            q_win = 9;
        }
        else if (selection == BET_HISTORY_GROUPBUY) {
            startDate = getDate(-3);
            q_bet = 3;
            q_win = 9;
        }
        else {
            startDate = getDate(-3);
            q_bet = 9;
            q_win = 9;
        }

        betHistoryList.clear();
        userNewCenterAdapter.notifyDataSetChanged();
        onClickToGetData();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        onClickToGetData();
        getUserInf();
    }

    @Override
    protected void submitData() {
        String eventName = "open_user_center";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClickToGetData() {
        page_num = 1;
        loadData();
    }

}