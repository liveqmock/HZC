package com.haozan.caipiao.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.bet.jczq.JCZQBasicActivity;
import com.haozan.caipiao.adapter.AwardConditionAdapter;
import com.haozan.caipiao.adapter.SportsExpandableListAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.LotteryAwardRank;
import com.haozan.caipiao.types.SportsHistory;
import com.haozan.caipiao.types.SportsHistoryItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateArrayAdapter;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;
import com.umeng.analytics.MobclickAgent;

/**
 * get the lottery history from the Internet,if false,display the history stored in the database
 * 
 * @author peter
 */
public class SportsLotteryHistory
    extends BasicActivity
    implements OnClickListener, OnTabSelectedItemListener, OnHeaderRefreshListener<ListView>,
    OnPageChangeListener {
    private static final int TYPE_NUM = 3;
    private static final long SIZE = 10;
    private String[] tabContent = new String[] {"开奖记录", "最新中奖", "中奖排行"};
    private TopMenuLayout topMenuLayout;
    private PullToRefreshListView[] pullToRefresh;
    private ArrayList<View> arrayViews;
    private ArrayList<SportsHistory> lotteryHistoryData;
    private ArrayList<LotteryAwardRank> lotteryNewlyAwardRankList;// 最新中奖
    private ArrayList<LotteryAwardRank> lotteryAwardRankList;// 中奖排行
    private SportsExpandableListAdapter lotteryHistoryAdapter;
    private AwardConditionAdapter awardNewlyCondtionAdapter;
    private AwardConditionAdapter awardRankingAdapter;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private View[] views;
    private ProgressBar[] progressBars;
    private View[] failPages;
    private TextView[] messageTvs;
    private ListView[] actualListView;
    private boolean[] ifGetMoreData = {false, false, false};
    private View[] footView;

    private String kind;
    private String lotteryName;
    private TextView title;
    private Button timeSelect;
    private String todayDate;
    private String selectDate;
    private boolean first = true;
    private String[] dateArray = new String[2];
    private int[] timeArray = new int[] {-1, -1, -1};
    private PopupWindow mPopupWindow;
    private CustomExpandleListView lv;

    private RelativeLayout timer;
    private TextView startDate;
    private TextView calenderMakeSure;
    private TextView calendarCancle;
    private int wheelYear;
    private int wheelMonth;
    private int wheelDay;
    private WheelView day;
    private WheelView month;
    private WheelView year;

    private final Calendar calendar = Calendar.getInstance();
    private int mYear = calendar.get(Calendar.YEAR);
    private int mMonth = calendar.get(Calendar.MONTH) + 1;
    private int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    private int lastDay = mDay;
    private OnWheelChangedListener listener;
    private String months[] = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String[] years;
    int flag;
    GetHistoryTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sports_history_record);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null)
            return;
        kind = bundle.getString("kind");
        lotteryName = bundle.getString("lotteryname");
        arrayViews = new ArrayList<View>();
        lotteryHistoryData = new ArrayList<SportsHistory>();
        lotteryNewlyAwardRankList = new ArrayList<LotteryAwardRank>();
        lotteryAwardRankList = new ArrayList<LotteryAwardRank>();
    }

    private void setupViews() {
        timer = (RelativeLayout) findViewById(R.id.title_btinit_right_rala);
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTopMenuItemContent(tabContent);
        topMenuLayout.setTabSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        LayoutInflater inflater = getLayoutInflater();
        views = new View[TYPE_NUM];
        progressBars = new ProgressBar[TYPE_NUM];
        failPages = new View[TYPE_NUM];
        messageTvs = new TextView[TYPE_NUM];
        footView = new View[TYPE_NUM];
        pullToRefresh = new PullToRefreshListView[TYPE_NUM];
        actualListView = new ListView[TYPE_NUM];
        views[0] = inflater.inflate(R.layout.jczq_analyse_duizhen, null);
        progressBars[0] = (ProgressBar) views[0].findViewById(R.id.progress_large);
        failPages[0] = views[0].findViewById(R.id.show_fail_page);
        messageTvs[0] = (TextView) views[0].findViewById(R.id.message);
        lv = (CustomExpandleListView) views[0].findViewById(R.id.match_list);
        arrayViews.add(views[0]);
        for (int i = 1; i < TYPE_NUM; i++) {
            views[i] = inflater.inflate(R.layout.unite_hall_viewflow_left, null);
            progressBars[i] = (ProgressBar) views[i].findViewById(R.id.progress_large);
            failPages[i] = views[i].findViewById(R.id.show_fail_page);
            messageTvs[i] = (TextView) views[i].findViewById(R.id.message);
            pullToRefresh[i] = (PullToRefreshListView) views[i].findViewById(R.id.unite_hall_listview);
            pullToRefresh[i].setOnHeaderRefreshListener(this);
            actualListView[i] = pullToRefresh[i].getRefreshableView();
            arrayViews.add(views[i]);
            footView[i] = View.inflate(SportsLotteryHistory.this, R.layout.list_item_load_more_view, null);
        }
        viewPagerAdapter = new ViewPagerAdapter(arrayViews);
        viewPagerAdapter.setTabContent(tabContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        title = (TextView) this.findViewById(R.id.history_title);
        timeSelect = (Button) this.findViewById(R.id.history_data);
        timeSelect.setOnClickListener(this);
    }

    private void init() {
        title.setText(lotteryName + "开奖历史");
        lotteryHistoryAdapter =
            new SportsExpandableListAdapter(SportsLotteryHistory.this, lotteryHistoryData, lotteryName);
        lv.setAdapter(lotteryHistoryAdapter, R.layout.sports_history_group_view);
        awardNewlyCondtionAdapter =
            new AwardConditionAdapter(SportsLotteryHistory.this, lotteryNewlyAwardRankList, true);
        actualListView[1].setAdapter(awardNewlyCondtionAdapter);
        awardRankingAdapter =
            new AwardConditionAdapter(SportsLotteryHistory.this, lotteryAwardRankList, false);
        actualListView[2].setAdapter(awardRankingAdapter);

        loadData();
    }

    private void loadData() {
        flag = viewPager.getCurrentItem();
        if (HttpConnectUtil.isNetworkAvailable(SportsLotteryHistory.this)) {
            task = new GetHistoryTask();
            task.execute();
        }
        else {
        	try{
        		pullToRefresh[flag].onHeaderRefreshComplete();
        	}catch(NullPointerException e){
        		
        	}
            if (flag == 0) {
                if (lotteryHistoryData.size() == 0) {
                    showFail(noNetTips);
                }
                else {
                    ViewUtil.showTipsToast(SportsLotteryHistory.this, noNetTips);
                }
            }
            else if (flag == 1) {
                if (lotteryNewlyAwardRankList.size() == 0) {
                    showFail(noNetTips);
                }
                else {
                    ViewUtil.showTipsToast(SportsLotteryHistory.this, noNetTips);
                }
            }
            else if (flag == 2) {
                if (lotteryAwardRankList.size() == 0) {
                    showFail(noNetTips);
                }
                else {
                    ViewUtil.showTipsToast(SportsLotteryHistory.this, noNetTips);
                }
            }
        }
    }

    class GetHistoryTask
        extends AsyncTask<Void, Void, String> {
        int tag;

        private HashMap<String, String> iniHisHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2009030");
            parameter.put("pid", LotteryUtils.getPid(SportsLotteryHistory.this));
            parameter.put("lottery_id", kind);
            if (!first) {
                String date = wheelYear + "-" + wheelMonth + "-" + wheelDay;
                parameter.put("query_date", date);
            }
            parameter.put("size", "3");
            return parameter;
        }

        private HashMap<String, String> initNewAwardHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "get_latest_win");
            parameter.put("pid", LotteryUtils.getPid(SportsLotteryHistory.this));
            parameter.put("lottery_id", kind);
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
            parameter.put("page_no", "1");
            parameter.put("size", "30");
            return parameter;
        }

        private HashMap<String, String> initRankHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "get_user_by_prize");
            parameter.put("pid", LotteryUtils.getPid(SportsLotteryHistory.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
            parameter.put("num", "30");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(SportsLotteryHistory.this);
            String json = null;
            try {
                if (flag == 0) {// 开奖历史
                    json = connectNet.getJson(5, false, iniHisHashMap());
                }
                else if (flag == 1) {// 最新中奖
                    json = connectNet.getJsonGet(1, false, initNewAwardHashMap());
                }
                else if (flag == 2) {// 中奖排行
                    json = connectNet.getJsonGet(1, false, initRankHashMap());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            progressBars[flag].setVisibility(View.GONE);
            ifGetMoreData[flag] = true;
            if (flag != 0) {
                pullToRefresh[flag].onHeaderRefreshComplete();
            }
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    clearArray();
                    String response_data = analyse.getData(json, "response_data");
                    if (flag == 0) {
                        dealWithHisData(response_data);
                    }
                    else {
                        if (response_data.equals("[]")) {
                            showFail();
                        }
                        else {
                            if (flag == 1) {
                                setNewAward(response_data);
                            }
                            else if (flag == 2) {
                                setAwardRanking(response_data);
                            }
                            dataChange();
                        }
                    }
                }
                else if (status.equals("202")) {
                    showFail();
                }
            }
            else {
                showFail();
            }
        }

        private void setAwardRanking(String response_data) {
            try {
                JSONArray hallArray = new JSONArray(response_data);
                int length = hallArray.length();

                for (int i = 0; i < length; i++) {
                    LotteryAwardRank lotteryAwardRank = new LotteryAwardRank();
                    JSONObject jo = hallArray.getJSONObject(i);
                    lotteryAwardRank.setLotteryPrize(jo.getString("prize"));
                    lotteryAwardRank.setPhone(jo.getString("phone"));
                    lotteryAwardRank.setUserId(jo.getInt("user_id"));
                    lotteryAwardRankList.add(lotteryAwardRank);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                showFail();
            }
        }

        private void setNewAward(String response_data) {
            try {
                JSONArray hallArray = new JSONArray(response_data);

                int length = hallArray.length();
                for (int i = 0; i < length; i++) {
                    LotteryAwardRank lotteryNewlyAwardRank = new LotteryAwardRank();
                    JSONObject jo = hallArray.getJSONObject(i);
                    lotteryNewlyAwardRank.setLotteryPrize(jo.getString("prize"));
                    lotteryNewlyAwardRank.setPhone(jo.getString("phone"));
                    lotteryNewlyAwardRank.setUserId(jo.getInt("user_id"));
                    lotteryNewlyAwardRank.setAwardTime(jo.getString("date"));
                    lotteryNewlyAwardRankList.add(lotteryNewlyAwardRank);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                showFail();
            }
        }

        public void dealWithHisData(String response_data) {
            try {
                JSONArray hallArray = new JSONArray(response_data);
                int length = hallArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jo = hallArray.getJSONObject(i);
                    String gameDate = jo.getString("game_date");
                    int index;
                    boolean hasSameDay = false;
                    for (index = 0; index < lotteryHistoryData.size(); index++) {
                        if (lotteryHistoryData.get(index).getDate().equals(gameDate)) {
                            hasSameDay = true;
                            break;
                        }
                    }
                    // 格式化获取比赛是一周第几天
                    int date = TimeUtils.getDate(gameDate);
                    // 列表中无此天的比赛，新增该天次
                    if (!hasSameDay) {
                        SportsHistory groupTemp = new SportsHistory();
                        groupTemp.setDate(gameDate);
                        // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                        if (date == 1) {
                            date = 7;
                        }
                        else {
                            date = date - 1;
                        }
                        if (date != -1) {
                            groupTemp.setDay(JCZQBasicActivity.WEEKDAY[date - 1]);
                        }
                        else {
                            groupTemp.setDay("");
                        }
                        ArrayList<SportsHistoryItem> sportsItemList = new ArrayList<SportsHistoryItem>();
                        addSportsItem(jo, sportsItemList, JCZQBasicActivity.WEEKDAY[date - 1]);
                        groupTemp.setItemList(sportsItemList);
                        lotteryHistoryData.add(groupTemp);
                    }
                    else {
                        addSportsItem(jo, lotteryHistoryData.get(index).getItemList(),
                                      lotteryHistoryData.get(index).getDay());
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < lotteryHistoryData.size(); i++) {
                SportsHistory groupTemp = lotteryHistoryData.get(i);
                groupTemp.setGameNumber("共" + groupTemp.getItemList().size() + "场赛事");
            }
            lv.refreshContent();
            lotteryHistoryAdapter.notifyDataSetChanged();
            lv.expandList();
        }

        private void clearArray() {
            if (flag == 0) {
                lotteryHistoryData.clear();
            }
            else if (flag == 1) {
                lotteryNewlyAwardRankList.clear();
            }
            else if (flag == 2) {
                lotteryAwardRankList.clear();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tag = viewPager.getCurrentItem();
            flag = tag;
            progressBars[flag].setVisibility(View.VISIBLE);
            failPages[flag].setVisibility(View.GONE);
        }
    }

    private void dataChange() {
        if (flag == 1) {
            awardNewlyCondtionAdapter.notifyDataSetInvalidated();
        }
        else if (flag == 2) {
            awardRankingAdapter.notifyDataSetChanged();
        }
    }

    private void setupWheelCanlenderViews(View view) {
        startDate = (TextView) view.findViewById(R.id.start_date);
        startDate.setOnClickListener(this);
        calenderMakeSure = (TextView) view.findViewById(R.id.calendar_make_sure);
        calenderMakeSure.setOnClickListener(this);
        calendarCancle = (TextView) view.findViewById(R.id.calendar_cancle);
        calendarCancle.setOnClickListener(this);
        day = (WheelView) view.findViewById(R.id.calendar_day_wheel_view);
        day.setNormalTx("日");
        month = (WheelView) view.findViewById(R.id.calendar_month_wheel_view);
        month.setNormalTx("月");
        year = (WheelView) view.findViewById(R.id.calendar_year_wheel_view);
        year.setNormalTx("年");
    }

    public void showFail() {
        showFail(failTips);
    }

    private void showFail(String failTips) {
        failPages[flag].setVisibility(View.VISIBLE);
        messageTvs[flag].setText(failTips);
        if (flag == 0) {
            lotteryHistoryAdapter.notifyDataSetChanged();
        }
        else {
            dataChange();
        }
    }

    public void addSportsItem(JSONObject jo, ArrayList<SportsHistoryItem> sportsItemList, String day)
        throws JSONException {
        // changed by vincent
        SportsHistoryItem sportItem = new SportsHistoryItem();
        sportItem.setIdBetNum(jo.getString("no"));
        sportItem.setLeague(jo.getString("match_name"));
        sportItem.setMatchHomeTeamName(jo.getString("master"));
        sportItem.setMatchGuessTeamName(jo.getString("guest"));
        sportItem.setDay(day);
        if (kind.equals("jczq")) {
            sportItem.setResult(jo.getString("spf_result"));
            sportItem.setConcede(jo.getString("handicap"));
            sportItem.setBqcResult(jo.getString("bqc_result"));
            sportItem.setPoint(jo.getString("final_score"));
        }
        else if (kind.equals("jclq")) {
            // 胜负结果
            if (jo.has("sf_result"))
                sportItem.setResult(jo.getString("sf_result"));
            // 胜分差结果
            if (jo.has("sfc_result"))
                sportItem.setSfcResult(jo.getString("sfc_result"));
            // 联赛 颜色
            if (jo.getString("color").equals("null") || jo.getString("color") == null) {
                sportItem.setColor("");
            }
            else {
                sportItem.setColor(jo.getString("color"));
            }
            // 比分处理
            String bf1 = null;
            String bf2 = null;
            if (jo.has("final_score"))
                bf1 = jo.getString("final_score");

            if (bf1 != null && !bf1.equals("")) {
                bf2 = bf1.replace(":", "-");
            }
            sportItem.setPoint(bf2);
            // 让分相关
            String[] rf = new String[10];
            if (jo.has("rf_result_gg"))
                rf = jo.getString("rf_result_gg").split("\\,");
            StringBuilder rfPoint = new StringBuilder();
            String rfPointTemp = null;
            for (int i = 0; i < rf.length; i++) {
                if (!rf[i].equals("") && rf[i] != null) {
// sportItem.setRfResult(rf[i].substring(0, 4));
// rfPoint.append(rf[i].substring(4) + "\n");
                    rfPoint.append(rf[i] + "\n");
                }
            }
            if (rfPoint.toString() != null && !rfPoint.toString().equals("")) {
                rfPointTemp = rfPoint.toString().substring(0, rfPoint.length() - 1);
            }
            sportItem.setConcede(rfPointTemp);

            // 大小分相关
            String[] dxf = new String[10];
            if (jo.has("dxf_result_gg"))
                dxf = jo.getString("dxf_result_gg").split("\\,");
            StringBuilder dxfPoint = new StringBuilder();
            String dxfPointTemp = null;
            for (int i = 0; i < dxf.length; i++) {
                if (!dxf[i].equals("") && dxf[i] != null) {
// sportItem.setDxfResult(dxf[i].substring(0, 1) + "分");
// dxfPoint.append(dxf[i].substring(1) + "\n");
                    dxfPoint.append(dxf[i] + "\n");
                }
            }
            if (dxfPoint.toString() != null && !dxfPoint.toString().equals("")) {
                dxfPointTemp = dxfPoint.toString().substring(0, dxfPoint.length() - 1);
            }
            sportItem.setDxfPoint(dxfPointTemp);
        }else if(kind.equals("dcsfgg")){
        	Log.i("log", jo.getString("status_desc"));
            sportItem.setResult(jo.getString("status_desc"));
            sportItem.setConcede(jo.getString("handicap"));
        }

        sportsItemList.add(sportItem);
    }

    private void initWheelView(int dYear, int dMonth, int dDay, int dEndYear, int dEndMonth, int dEndDay) {
        listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateWheelDays(year, month, day);
            }
        };

        if (dYear == -1 || dMonth == -1 || dDay == -1) {
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH) + 1;
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            lastDay = mDay;
        }
        else {
            mYear = dYear;
            mMonth = dMonth;
            mDay = dDay;
            lastDay = mDay;
        }

        startDate.setText(mYear + "年" + mMonth + "月" + mDay + "日");
    }

// private void setBottomLayoutYearAndMonth(int num) {
// month.setCurrentItem(num);
// year.setCurrentItem(num);
// }

// private int preYear = 0;
// private int preMonth = 0;

    private void updateWheelDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        if (month.getCurrentItem() == 12)
            calendar.set(Calendar.YEAR, 2011 + year.getCurrentItem() - 1);
        else
            calendar.set(Calendar.YEAR, 2011 + year.getCurrentItem());

        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, lastDay - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);

        wheelYear = calendar.get(Calendar.YEAR);
        wheelMonth = month.getCurrentItem() + 1;
        wheelDay = curDay;

        startDate.setText(wheelYear + "年" + wheelMonth + "月" + wheelDay + "日");

// preYear = wheelYear;
// preMonth = wheelMonth;
        try {
            calLstDayOfMonth(wheelYear - 1, wheelMonth);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void calLstDayOfMonth(int year, int month)
        throws ParseException {
        if (year >= 2011 && month >= 1) {
            Date toDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            toDate = dateFormat.parse(String.valueOf(year) + "-" + String.valueOf(month) + "-01");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);// 如果你要计算4月的日期
            c.setTime(toDate);
            int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);// 这就是最后一天
            dateArray[0] = year + "-" + month + "-01";
            dateArray[1] = year + "-" + month + "-" + lastDay;
        }
        else {
            dateArray[0] = "2011-03-18";
            dateArray[1] = null;
        }
    }

    private void InitPopupViews(int layout, String[] textArray) {

        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(layout, null);

        setupWheelCanlenderViews(waySwitchLayout);
        initWheelView(timeArray[0], timeArray[1], timeArray[2], -1, -1, -1);

        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(waySwitchLayout);

    }

    private String[] setDateOfYearArray() {
        Calendar calendar = Calendar.getInstance();
        int length = calendar.get(Calendar.YEAR) - 2011 + 1;
        years = new String[length];
        for (int i = 0; i < length; i++) {
            if (i == 0)
                years[i] = "2011";
            else
                years[i] = String.valueOf((2011 + i));
// years[i] = String.valueOf(calendar.get(Calendar.YEAR));
        }
        return years;
    }

    private void switchBottomLayoutInner() {
        month.setViewAdapter(new DateArrayAdapter(this, months, mMonth - 1));
        month.setCurrentItem(mMonth - 1);
        month.addChangingListener(listener);

        int currentYear = -2011 + mYear;
        year.setViewAdapter(new DateArrayAdapter(this, setDateOfYearArray(), currentYear));
        year.setCurrentItem(currentYear);
        year.addChangingListener(listener);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, mDay - 1));
        day.setCurrentItem(mDay - 1);
        day.addChangingListener(listener);
        updateWheelDays(year, month, day);
    }

    private void switchBottomCalenderLayout() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        else {
            switchBottomLayoutInner();
        }
    }

    // 上传用户点击风云榜按钮事件
    protected void submitStatisticTopList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "lottery history top list");
        map.put("extra_inf", "username [" + appState.getUsername() + "]: top list");
        String eventName = "lottery history operate";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "lottery_history_operate";
        MobclickAgent.onEvent(SportsLotteryHistory.this, eventNameMob, "top list");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.history_data) {
            first = false;
            InitPopupViews(R.layout.sports_pop_wheel_view, null);
            switchBottomCalenderLayout();
            mPopupWindow.showAsDropDown(timeSelect);
        }
        else if (v.getId() == R.id.calendar_cancle) {
            switchBottomCalenderLayout();
// start_date = "2011-03-18";
// end_date = null;
        }
        else if (v.getId() == R.id.calendar_make_sure) {
            // 如果选择的日期大于今天的日期则提示用户
            selectDate = wheelYear + "-" + wheelMonth + "-" + wheelDay;
            todayDate =
                calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH);
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date select = fmt.parse(selectDate);
                Date today = fmt.parse(todayDate);

                if (select.getTime() > today.getTime()) {
                    ViewUtil.showTipsToast(this, "不能查询大于今天的数据");
                }
                else {
                    mYear = wheelYear;
                    mMonth = wheelMonth;
                    mDay = wheelDay;
                    timeArray[0] = mYear;
                    timeArray[1] = mMonth;
                    timeArray[2] = mDay;
                    switchBottomCalenderLayout();
                    loadData();
                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open lottery history");
        map.put("more_inf", "open lottery history of " + lotteryName);
        String eventName = "v2 open lottery history";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("kind");
        }
        String eventName = "open_lottery_history";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SportsLotteryHistory.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(SportsLotteryHistory.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                          R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase<ListView> refreshView) {
        loadData();
    }

    @Override
    public void onPageScrollStateChanged(int flag) {
        progressBars[flag].setVisibility(View.GONE);
        if (null != pullToRefresh[flag]) {
            pullToRefresh[flag].onHeaderRefreshComplete();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int selection) {
        topMenuLayout.check(selection);
        if (null != task) {
            task.cancel(true);
        }
        if (selection == 0 && lotteryHistoryData.size() == 0 || selection == 1 &&
            lotteryNewlyAwardRankList.size() == 0 || selection == 2 && lotteryAwardRankList.size() == 0) {
            loadData();
        }
        if (selection == 0) {
            timer.setVisibility(View.VISIBLE);
        }
        else {
            timer.setVisibility(View.GONE);
        }
    }
}
