package com.haozan.caipiao.activity.guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.adapter.guess.MyGuessAdapter;
import com.haozan.caipiao.adapter.guess.WholeGuessAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.guess.MyGuessItem;
import com.haozan.caipiao.types.guess.WholeGuessItem;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
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
 * 竞猜列表
 * 
 * @author peter_feng
 * @create-time 2013-3-20 上午11:32:20
 */
public class LotteryGuessHome
    extends BasicActivity
    implements OnClickListener, LoadDataListener, OnTabSelectedItemListener, OnPageChangeListener,
    OnHeaderRefreshListener, OnGetDataAgainListener {
    private static final int WHOLE_GUESS = 0;
    private static final int MY_GUESS = 1;

    private static final int EACH_PAGE_SIZE = 10;

    private String[] tabContent = new String[] {"竞猜列表", "我的参与"};

    private static final String NODATA = "没有竞猜记录\n参与竞猜，赚取积分！";

    private int pageWhole = 1;
    private int pageMyGuess = 1;

    private AutoLoadListView lvWholeGuess;
    private WholeGuessAdapter wholeGuesssListAdapter;
    private ArrayList<WholeGuessItem> wholeGuessItemData;
    private EmptyLayout showFailPageWholeGuess;
    private RefreshLayout layoutRefreshWholeGuess;

    private AutoLoadListView lvMyGuess;
    private MyGuessAdapter myGuesssAdapter;
    private ArrayList<MyGuessItem> myGuessItemData;
    private EmptyLayout showFailPageMyGuess;
    private RefreshLayout layoutRefreshMyGuess;

    private ImageView ivHelp;
    private Button toSubmitQuetion;
    private Button btnPointReward;
    private TopMenuLayout topMenuLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<View> viewList;

    private GetGuessListTask guessListTask;

    private boolean toLogin = false;
    private boolean isRefresh = false;

    private int selectedIndex = WHOLE_GUESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_home);
        initData();
        setUpView();
        init();
    }

    private void initData() {
        wholeGuessItemData = new ArrayList<WholeGuessItem>();
        myGuessItemData = new ArrayList<MyGuessItem>();

        viewList = new ArrayList<View>();
    }

    private void setUpView() {
        setupMainViews();
        setupWholeGuessViews();
        setupMyGuessViews();
    }

    private void setupMainViews() {
        ivHelp = (ImageView) findViewById(R.id.help);
        ivHelp.setOnClickListener(this);
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTabSelectedListener(this);

        toSubmitQuetion = (Button) this.findViewById(R.id.to_submit_quetion);
        toSubmitQuetion.setOnClickListener(this);

        viewPager = (ViewPager) this.findViewById(R.id.view_pager);

        btnPointReward = (Button) this.findViewById(R.id.point_reward);
        btnPointReward.setOnClickListener(this);
    }

    private void setupWholeGuessViews() {
        View viewWholeGuess = View.inflate(this, R.layout.include_new_viewpager_listview, null);

        lvWholeGuess = (AutoLoadListView) viewWholeGuess.findViewById(R.id.listview);
        lvWholeGuess.setOnLoadDataListener(this);
        showFailPageWholeGuess = (EmptyLayout) viewWholeGuess.findViewById(R.id.empty_layout);
        showFailPageWholeGuess.setOnGetDataAgainListener(this);
        layoutRefreshWholeGuess = (RefreshLayout) viewWholeGuess.findViewById(R.id.pull_refresh_view);
        layoutRefreshWholeGuess.setOnHeaderRefreshListener(this);

        viewList.add(viewWholeGuess);
    }

    private void setupMyGuessViews() {
        View viewMyGuess = View.inflate(this, R.layout.include_new_viewpager_listview, null);

        lvMyGuess = (AutoLoadListView) viewMyGuess.findViewById(R.id.listview);
        lvMyGuess.setOnLoadDataListener(this);
        showFailPageMyGuess = (EmptyLayout) viewMyGuess.findViewById(R.id.empty_layout);
        showFailPageMyGuess.setOnGetDataAgainListener(this);
        layoutRefreshMyGuess = (RefreshLayout) viewMyGuess.findViewById(R.id.pull_refresh_view);
        layoutRefreshMyGuess.setOnHeaderRefreshListener(this);

        viewList.add(viewMyGuess);
    }

    private void init() {
        viewPagerAdapter = new ViewPagerAdapter(viewList);
        viewPagerAdapter.setTabContent(tabContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        wholeGuesssListAdapter = new WholeGuessAdapter(LotteryGuessHome.this, wholeGuessItemData);
        lvWholeGuess.setAdapter(wholeGuesssListAdapter);

        myGuesssAdapter = new MyGuessAdapter(LotteryGuessHome.this, myGuessItemData);
        lvMyGuess.setAdapter(myGuesssAdapter);

        topMenuLayout.setTopMenuItemContent(tabContent);

        loadData();
    }

    private void showFail() {
        if (selectedIndex == WHOLE_GUESS) {
            if (wholeGuessListNoData()) {
                showFailPageWholeGuess.setVisibility(View.VISIBLE);
                showFailPageWholeGuess.showFailPage();
            }
            else {
                ViewUtil.showTipsToast(this, failTips);
                lvWholeGuess.showFootText();
            }
        }
        else if (selectedIndex == MY_GUESS) {
            if (myGuessListNoData()) {
                showFailPageMyGuess.setVisibility(View.VISIBLE);
                showFailPageMyGuess.showFailPage();
            }
            else {
                ViewUtil.showTipsToast(this, failTips);
                lvMyGuess.showFootText();
            }
        }
    }

    private void showNoData() {
        if (selectedIndex == WHOLE_GUESS) {
            if (wholeGuessListNoData()) {
                showFailPageWholeGuess.setVisibility(View.VISIBLE);
                showFailPageWholeGuess.showNoDataPage(NODATA);
            }
            else {
                lvWholeGuess.loadNoMoreData();
            }
        }
        else if (selectedIndex == MY_GUESS) {
            if (myGuessListNoData()) {
                showFailPageMyGuess.setVisibility(View.VISIBLE);
                showFailPageMyGuess.showNoDataPage(NODATA);
            }
            else {
                lvMyGuess.loadNoMoreData();
            }
        }
    }

    private boolean wholeGuessListNoData() {
        return selectedIndex == WHOLE_GUESS && wholeGuessItemData.size() == 0;
    }

    private boolean myGuessListNoData() {
        return selectedIndex == MY_GUESS && myGuessItemData.size() == 0;
    }

    class GetGuessListTask
        extends AsyncTask<String, Object, String> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2010010");
            parameter.put("pid", LotteryUtils.getPid(LotteryGuessHome.this));
            parameter.put("page_no", "" + pageWhole);
            parameter.put("size", "" + EACH_PAGE_SIZE);
            return parameter;
        }

        private HashMap<String, String> initHashMapHistory() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2010030");
            parameter.put("pid", LotteryUtils.getPid(LotteryGuessHome.this));
            parameter.put("phone_no", "" + appState.getUsername());
            parameter.put("page_no", "" + pageMyGuess);
            parameter.put("size", "" + EACH_PAGE_SIZE);
            return parameter;
        }

        @Override
        protected String doInBackground(String... para) {
            ConnectService connectNet = new ConnectService(LotteryGuessHome.this);
            String json = null;
            try {
                if (selectedIndex == 0)
                    json = connectNet.getJsonGet(6, false, initHashMap());
                else if (selectedIndex == 1)
                    json = connectNet.getJsonGet(6, true, initHashMapHistory());
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

            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    if (selectedIndex == WHOLE_GUESS) {
                        lvWholeGuess.setVisibility(View.VISIBLE);
                    }
                    else if (selectedIndex == MY_GUESS) {
                        lvMyGuess.setVisibility(View.VISIBLE);
                    }

                    if (isRefresh) {
                        if (selectedIndex == WHOLE_GUESS) {
                            wholeGuessItemData.clear();
                        }
                        else if (selectedIndex == MY_GUESS) {
                            myGuessItemData.clear();
                        }
                        isRefresh = false;
                    }

                    String response_data = analyse.getData(json, "response_data");
                    JSONArray hallArray = null;
                    try {
                        hallArray = new JSONArray(response_data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (response_data.equals("[]")) {
                        showNoData();
                    }
                    else {
                        int lastSize = 0;
                        int preSize = 0;
                        if (selectedIndex == WHOLE_GUESS) {
                            lastSize = wholeGuessItemData.size();
                            getWholeGuessResponsData(hallArray, wholeGuessItemData);
                            preSize = wholeGuessItemData.size();
                        }
                        else if (selectedIndex == MY_GUESS) {
                            lastSize = myGuessItemData.size();
                            getMyGuessResponsData(hallArray, myGuessItemData);
                            preSize = myGuessItemData.size();
                        }
                        if (preSize - lastSize < 10) {
                            completeLoadData();
                        }
                        else {
                            readyToLoad();
                        }
                        wholeGuesssListAdapter.notifyDataSetChanged();
                        myGuesssAdapter.notifyDataSetChanged();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(LotteryGuessHome.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(LotteryGuessHome.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
        }

        private void completeLoadData() {
            if (selectedIndex == WHOLE_GUESS) {
                lvWholeGuess.loadNoMoreData();
            }
            else {
                lvMyGuess.loadNoMoreData();
            }
        }

        private void readyToLoad() {
            if (selectedIndex == WHOLE_GUESS) {
                lvWholeGuess.readyToLoad();
            }
            else {
                lvMyGuess.readyToLoad();
            }
        }

        private void completeLoadingProgress() {
            if (selectedIndex == WHOLE_GUESS) {
                layoutRefreshWholeGuess.onHeaderRefreshComplete();
            }
            else {
                layoutRefreshMyGuess.onHeaderRefreshComplete();
            }
            dismissProgress();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isRefresh) {
                if (selectedIndex == WHOLE_GUESS) {
                    pageWhole = 1;
                }
                else if (selectedIndex == MY_GUESS) {
                    pageMyGuess = 1;
                }
            }

            if (selectedIndex == WHOLE_GUESS) {
                if (wholeGuessListNoData()) {
                    showFailPageWholeGuess.setVisibility(View.GONE);
                    showProgress();
                }
            }
            else if (selectedIndex == MY_GUESS) {
                if (myGuessListNoData()) {
                    showFailPageMyGuess.setVisibility(View.GONE);
                    showProgress();
                }
            }
        }
    }

    private void getWholeGuessResponsData(JSONArray hallArray, ArrayList<WholeGuessItem> historyAll) {

        try {
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                WholeGuessItem guessHome = new WholeGuessItem();
                JSONObject jo = hallArray.getJSONObject(i);
                guessHome.setschemaId(jo.getString("scheme_id"));
                guessHome.setSchemaName(jo.getString("scheme_name"));
                guessHome.setStatus(jo.getInt("status"));
                guessHome.setDate(jo.getString("begin_time"));
                guessHome.setEndDate(jo.getString("end_time"));
                guessHome.setNowTime(jo.getString("nowTime"));
                guessHome.setPublishTime(jo.getString("publish_time"));
                guessHome.setAllScore(jo.getString("all_scores"));
                guessHome.setTerm(jo.getString("issue"));
                historyAll.add(guessHome);
            }
            pageWhole++;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMyGuessResponsData(JSONArray hallArray, ArrayList<MyGuessItem> historyAll) {

        try {
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                MyGuessItem guessHome = new MyGuessItem();
                JSONObject jo = hallArray.getJSONObject(i);
                guessHome.setAnwserId(jo.getString("answer_id"));
                guessHome.setBetScore(jo.getInt("bet"));
                guessHome.setDate(jo.getString("createtime"));
                guessHome.setEarnedScore(jo.getInt("earned_score"));
                guessHome.setGuessId(jo.getString("guess_id"));
                guessHome.setQuestion(jo.getString("question"));
                guessHome.setSchemaId(jo.getString("scheme_id"));
                guessHome.setStatus(jo.getInt("status"));
                guessHome.setUserId(jo.getString("user_id"));
                guessHome.setVoteId(jo.getString("vote_id"));
                guessHome.setVote_answer(jo.getString("answer"));
                // add by vincent
                guessHome.setTerm(jo.getString("issue"));// 期数
                guessHome.setSchemaName(jo.getString("scheme_name"));// 方案标题

                historyAll.add(guessHome);
            }
            pageMyGuess++;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void dismissTipsPage() {
        if (selectedIndex == WHOLE_GUESS) {
            showFailPageWholeGuess.setVisibility(View.GONE);
        }
        else {
            showFailPageMyGuess.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#jingcai");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(LotteryGuessHome.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.to_submit_quetion) {
            Intent intent = new Intent();
            intent.setClass(LotteryGuessHome.this, CreateMyGuess.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.point_reward) {
            ActionUtil.toPointReward(LotteryGuessHome.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open guess list");
        String eventName = "open guess list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_guess_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            guessListTask = new GetGuessListTask();
            guessListTask.execute();
        }
        else {
            layoutRefreshWholeGuess.onHeaderRefreshComplete();
            layoutRefreshMyGuess.onHeaderRefreshComplete();
            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (selectedIndex == WHOLE_GUESS) {
            if (wholeGuessListNoData()) {
                showFailPageWholeGuess.showNetErrorPage();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
                lvWholeGuess.showFootText();
            }
        }
        else if (selectedIndex == MY_GUESS) {
            if (myGuessListNoData()) {
                showFailPageMyGuess.showNetErrorPage();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
                lvMyGuess.showFootText();
            }
        }
    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    private void loadAgain(int selection) {
        if (selection == 0) {
            selectedIndex = WHOLE_GUESS;
            loadData();
        }
        else if (selection == 1) {
            if (appState.getUsername() != null) {
                selectedIndex = MY_GUESS;
                loadData();
            }
            else {
                toLogin = true;
                ActionUtil.toLogin(this, "竞猜");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toLogin) {
            toLogin = false;
            if (((LotteryApp) getApplication()).getUsername() != null) {
                selectedIndex = MY_GUESS;
                if (myGuessItemData.size() != 0) {
                    layoutRefreshMyGuess.headerRefreshing();
                }
                onRefresh();
            }
            else {
                topMenuLayout.check(WHOLE_GUESS);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        topMenuLayout.check(index);

        selectedIndex = index;
        if (wholeGuessListNoData() || myGuessListNoData()) {
            loadAgain(selectedIndex);
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;

        loadData();
    }

    @Override
    public void onClickToGetData() {
        loadData();
    }
}
