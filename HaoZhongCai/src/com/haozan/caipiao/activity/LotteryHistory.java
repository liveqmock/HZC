package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.AwardConditionAdapter;
import com.haozan.caipiao.adapter.LotteryHistoryAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.Lottery;
import com.haozan.caipiao.types.LotteryAwardRank;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 开奖历史-数字彩
 * 
 * @author peter_feng
 * @create-time 2013-3-21 上午9:51:12
 */
public class LotteryHistory
    extends ContainTipsPageBasicActivity
    implements OnHeaderRefreshListener, OnTabSelectedItemListener, OnPageChangeListener, LoadDataListener,
    OnGetDataAgainListener {
    private static final int TYPE_NUM = 3;
    private static final long SIZE = 10;
    private String[] tabContent = new String[] {"开奖记录", "最新中奖", "中奖排行"};
    private TopMenuLayout topMenuLayout;
    private ArrayList<View> arrayViews;
    private ArrayList<Lottery> lotteryHistoryData;// 开奖记录
    private ArrayList<LotteryAwardRank> lotteryNewlyAwardRankList;// 最新中奖
    private ArrayList<LotteryAwardRank> lotteryAwardRankList;// 中奖排行
    private LotteryHistoryAdapter lotteryHistoryAdapter;
    private AwardConditionAdapter awardNewlyCondtionAdapter;
    private AwardConditionAdapter awardRankingAdapter;

    private GetLotteryHistoryTask getHistoryTask;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private RefreshLayout[] pullToRefresh;
    private EmptyLayout[] layoutsEmpty;
    private AutoLoadListView[] lvAutoLoad;

    private String lastTerm = null;
    private String kind;
    private String lotteryName;
    private int flag = 0;

    // 下拉刷新
    private TextView title;

    private boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_open_history);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("kind");
        lotteryName = bundle.getString("lotteryname");
        lotteryHistoryData = new ArrayList<Lottery>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open lottery history");
        map.put("more_inf", "open lottery history of " + lotteryName);
        String eventName = "v2 open lottery history";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);

        arrayViews = new ArrayList<View>();
        lotteryHistoryData = new ArrayList<Lottery>();
        lotteryNewlyAwardRankList = new ArrayList<LotteryAwardRank>();
        lotteryAwardRankList = new ArrayList<LotteryAwardRank>();
    }

    private void setupViews() {
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTopMenuItemContent(tabContent);
        topMenuLayout.setTabSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        LayoutInflater inflater = getLayoutInflater();
        View[] views = new View[TYPE_NUM];
        layoutsEmpty = new EmptyLayout[TYPE_NUM];
        pullToRefresh = new RefreshLayout[TYPE_NUM];
        lvAutoLoad = new AutoLoadListView[TYPE_NUM];

        for (int i = 0; i < TYPE_NUM; i++) {
            views[i] = inflater.inflate(R.layout.include_new_viewpager_listview, null);
            layoutsEmpty[i] = (EmptyLayout) views[i].findViewById(R.id.empty_layout);
            layoutsEmpty[i].setOnGetDataAgainListener(this);
            pullToRefresh[i] = (RefreshLayout) views[i].findViewById(R.id.pull_refresh_view);
            pullToRefresh[i].setOnHeaderRefreshListener(this);
            lvAutoLoad[i] = (AutoLoadListView) views[i].findViewById(R.id.listview);
            lvAutoLoad[i].setOnLoadDataListener(this);
            arrayViews.add(views[i]);
        }

        viewPagerAdapter = new ViewPagerAdapter(arrayViews);
        viewPagerAdapter.setTabContent(tabContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        title = (TextView) this.findViewById(R.id.title);
        title.setText(lotteryName + "开奖记录");
        title.setFocusable(true);
        title.setFocusableInTouchMode(true);
    }

    private void init() {
        lotteryHistoryAdapter = new LotteryHistoryAdapter(LotteryHistory.this, lotteryHistoryData);
        lvAutoLoad[0].setAdapter(lotteryHistoryAdapter);
        awardNewlyCondtionAdapter =
            new AwardConditionAdapter(LotteryHistory.this, lotteryNewlyAwardRankList, true);
        lvAutoLoad[1].setAdapter(awardNewlyCondtionAdapter);
        awardRankingAdapter = new AwardConditionAdapter(LotteryHistory.this, lotteryAwardRankList, false);
        lvAutoLoad[2].setAdapter(awardRankingAdapter);

        loadData();
    }

    // 上传用户下拉刷新事件
    protected void submitStatisticRefreshPull() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "lottery history refresh pull");
        map.put("extra_inf", "username [" + appState.getUsername() + "]: refresh");
        String eventName = "lottery history operate";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "lottery_history_operate";
        MobclickAgent.onEvent(LotteryHistory.this, eventNameMob, "refresh pull");
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
        MobclickAgent.onEvent(LotteryHistory.this, eventNameMob, "top list");
    }

    private void showFail() {
        if (ifListNoData()) {
            layoutsEmpty[flag].setVisibility(View.VISIBLE);
            layoutsEmpty[flag].showFailPage();
        }
        else {
            ViewUtil.showTipsToast(this, failTips);
            lvAutoLoad[flag].showFootText();
        }
    }

    private void showNoData() {
        if (ifListNoData()) {
            layoutsEmpty[flag].setVisibility(View.VISIBLE);
            if (flag == 0) {
                layoutsEmpty[flag].showNoDataPage("暂无开奖历史");
            }
            else if (flag == 1) {
                layoutsEmpty[flag].showNoDataPage("暂无最新中奖\n赶紧投一注试试看吧");
            }
            else if (flag == 2) {
                layoutsEmpty[flag].showNoDataPage("暂无中奖排行榜\n赶紧投一注中奖上榜吧");
            }
        }
        else {
            lvAutoLoad[flag].loadNoMoreData();
        }
    }

    class GetLotteryHistoryTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> iniHisHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1001060");
            parameter.put("pid", LotteryUtils.getPid(LotteryHistory.this));
            parameter.put("lottery_id", kind);
            if (!isRefresh && lastTerm != null)
                parameter.put("start_term", lastTerm);
            if (isRefresh)
                parameter.put("size", "" + SIZE);
            else
                parameter.put("size", "" + (SIZE + 1));
            return parameter;
        }

        private HashMap<String, String> initNewAwardHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "get_latest_win");
            parameter.put("pid", LotteryUtils.getPid(LotteryHistory.this));
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
            parameter.put("pid", LotteryUtils.getPid(LotteryHistory.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
            parameter.put("num", "30");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(LotteryHistory.this);
            String json = null;
            try {
                if (flag == 0) {// 开奖历史
                    json = connectNet.getJsonGet(2, false, iniHisHashMap());
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
            super.onPostExecute(json);
            completeLoadingProgress();

            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        clearArray();
                        lvAutoLoad[flag].setVisibility(View.VISIBLE);

                        int lastSize = lotteryHistoryData.size();
                        if (flag == 0) {
                            lotteryHistoryResponse(response_data);

                            int preSize = lotteryHistoryData.size();
                            if (preSize - lastSize < 10) {
                                lvAutoLoad[flag].loadNoMoreData();
                            }
                            else {
                                lvAutoLoad[flag].readyToLoad();
                            }
                        }
                        else if (flag == 1) {
                            setNewAward(response_data);
                            lvAutoLoad[flag].loadNoMoreData();
                        }
                        else if (flag == 2) {
                            setAwardRanking(response_data);
                            lvAutoLoad[flag].loadNoMoreData();
                        }

                        dataChange(flag);
                    }
                }
                else {
                    showFail();
                }
            }
            else {
                showFail();
            }
            if (isRefresh) {
                isRefresh = false;
            }
        }

        private void completeLoadingProgress() {
            pullToRefresh[flag].onHeaderRefreshComplete();

            dismissProgress();
        }

        public void clearArray() {
            if (isRefresh) {
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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ifListNoData()) {
                layoutsEmpty[flag].setVisibility(View.GONE);
                showProgress();
            }
        }
    }

    public void dataChange(int flag) {
        if (flag == 0) {
            lotteryHistoryAdapter.notifyDataSetChanged();
        }
        else if (flag == 1) {
            awardNewlyCondtionAdapter.notifyDataSetInvalidated();
        }
        else if (flag == 2) {
            awardRankingAdapter.notifyDataSetChanged();
        }
    }

    public void analyseJson(String json) {

    }

    public void setNewAward(String response_data) {
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

    public void setAwardRanking(String response_data) {
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

    public void lotteryHistoryResponse(String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            int i = 0;
            if (!isRefresh) {
                i = 1;
            }
            for (; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                Lottery temp = new Lottery();
                temp.setId(kind);
                if (i == length - 1) {
                    lastTerm = jo.getString("term");
                }
                temp.setTerm(jo.getString("term"));
                temp.setDate(jo.getString("date"));
                String balls = jo.getString("codes");
                if (kind.equals("dfljy")) {
                    balls = StringUtil.getDFLJY(balls);
                }
                temp.setBalls(balls);
                lotteryHistoryData.add(temp);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            getHistoryTask = new GetLotteryHistoryTask();
            getHistoryTask.execute();
        }
        else {
            pullToRefresh[flag].onHeaderRefreshComplete();

            showNetWorkErrorPage();
        }
    }

    private boolean ifListNoData() {
        return (flag == 0 && lotteryHistoryData.size() == 0) ||
            (flag == 1 && lotteryNewlyAwardRankList.size() == 0) ||
            (flag == 2 && lotteryAwardRankList.size() == 0);
    }

    private void showNetWorkErrorPage() {
        if (ifListNoData()) {
            layoutsEmpty[flag].showNetErrorPage();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
            lvAutoLoad[flag].showFootText();
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
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageSelected(int selection) {
        topMenuLayout.check(selection);
        flag = selection;

        if (null != getHistoryTask) {
            getHistoryTask.cancel(true);
        }
        if (ifListNoData()) {
            loadData();
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;

        loadData();
    }

    @Override
    public void onClickToGetData() {
        onRefresh();
    }
}
