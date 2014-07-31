package com.haozan.caipiao.activity.news;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.LotteryNewsListAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.NewsListItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.tencent.mm.sdk.platformtools.Log;
import com.umeng.analytics.MobclickAgent;

public class LotteryNewsOfOneType
    extends ContainTipsPageBasicActivity
    implements OnTabSelectedItemListener, OnPageChangeListener, OnHeaderRefreshListener,
    OnLastItemVisibleListener {
    private final static String NODATA = "已无新闻\n我们会及时跟进彩市新闻";
    private static final int TYPE_NUM = 3;

    private String[] tabContent = new String[] {"新闻", "预测", "技巧"};
    private TextView topTitle;
    private TopMenuLayout topMenuLayout;
    private PullToRefreshListView[] newsList;
    private ArrayList<ArrayList<NewsListItem>> newsListItemRecord = new ArrayList<ArrayList<NewsListItem>>();
    private ArrayList<LotteryNewsListAdapter> newsListAdapter = new ArrayList<LotteryNewsListAdapter>();

    private int[] pageNo = {1, 1, 1};
    private int size = 10;
    private int newsTypeId = 1;
    private String[] getNewsTypeId;
    private int newsKindIndex = 0;
    private LotteryNewsTask newsTask;
    private String lotteryName;
    private Map<String, String> newstype;
    private String[] typeArray = {"1,2,3", "4,5,6", "20,21,22", "32,33,34", "29,30,31"};
    private String[] lottryTypeArray = {"ssq", "3d", "dlt", "zq", "other"};

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<View> arrayViews;
    private View[] views;
    private ProgressBar[] progressBars;
    private View[] failPages;
    private TextView[] messageTvs;
    private ListView[] actualListView;
    private boolean[] ifGetMoreData = {false, false, false};
    private boolean[] ifClearArray = {false, false, false};
    private View[] footView;
    private boolean ring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_news);
        initData();
        setupViewss();
        init();
    }

    private void initData() {
        initHashMap();
        lotteryName = this.getIntent().getExtras().getString("lottery_name");// "ssq", "dlt", "zq", "3d",
// "other" 中的一个
        newsKindIndex = this.getIntent().getExtras().getInt("news_kind");
        getNewsTypeId = newstype.get(lotteryName).split("\\,");
        newsTypeId = Integer.valueOf(getNewsTypeId[newsKindIndex]);
    }

    private void initHashMap() {
        newstype = new HashMap<String, String>();
        for (int i = 0; i < lottryTypeArray.length; i++) {
            newstype.put(lottryTypeArray[i], typeArray[i]);
        }
    }

    private void setupViewss() {
        topTitle = (TextView) findViewById(R.id.news_title_tv);
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTopMenuItemContent(tabContent);
        topMenuLayout.setTabSelectedListener(this);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        LayoutInflater inflater = getLayoutInflater();
        arrayViews = new ArrayList<View>();
        views = new View[TYPE_NUM];
        progressBars = new ProgressBar[TYPE_NUM];
        failPages = new View[TYPE_NUM];
        messageTvs = new TextView[TYPE_NUM];
        newsList = new PullToRefreshListView[TYPE_NUM];
        actualListView = new ListView[TYPE_NUM];
        footView = new View[TYPE_NUM];
        for (int i = 0; i < TYPE_NUM; i++) {
            final int j = i;
            views[i] = inflater.inflate(R.layout.unite_hall_viewflow_left, null);
            progressBars[i] = (ProgressBar) views[i].findViewById(R.id.progress_large);
            failPages[i] = views[i].findViewById(R.id.show_fail_page);
            messageTvs[i] = (TextView) views[i].findViewById(R.id.message);
            newsList[i] = (PullToRefreshListView) views[i].findViewById(R.id.unite_hall_listview);
            newsList[i].setOnHeaderRefreshListener(this);
            newsList[i].setOnLastItemVisibleListener(this);
            actualListView[i] = newsList[i].getRefreshableView();
            actualListView[i].setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    try {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("news_id", newsListItemRecord.get(j).get(position - 1).getNewsId());
                        bundle.putString("title", topTitle.getText().toString());
                        intent.putExtras(bundle);
                        intent.setClass(LotteryNewsOfOneType.this, NewsCommentList.class);
                        startActivity(intent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            arrayViews.add(views[i]);
            footView[i] = View.inflate(LotteryNewsOfOneType.this, R.layout.list_item_load_more_view, null);
        }
        viewPagerAdapter = new ViewPagerAdapter(arrayViews);
        viewPagerAdapter.setTabContent(tabContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void init() {
        setNewSTitle();
        for (int i = 0; i < TYPE_NUM; i++) {
            ArrayList<NewsListItem> list = new ArrayList<NewsListItem>();
            newsListItemRecord.add(list);
            LotteryNewsListAdapter adapter = new LotteryNewsListAdapter(LotteryNewsOfOneType.this, list);
            newsListAdapter.add(adapter);
            actualListView[i].setAdapter(adapter);

        }
        viewPager.setCurrentItem(newsKindIndex);
        loadData();
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        int flag = viewPager.getCurrentItem();
        failPages[flag].setVisibility(View.VISIBLE);
        messageTvs[flag].setText(failInf);
        newsListAdapter.get(flag).notifyDataSetChanged();
    }

    private void showNoData() {
        int flag = viewPager.getCurrentItem();
        failPages[flag].setVisibility(View.VISIBLE);
        messageTvs[flag].setText(NODATA);
        newsListAdapter.get(viewPager.getCurrentItem()).notifyDataSetChanged();
    }

    private void setNewSTitle() {
        if (lotteryName.equals("ssq")) {
            topTitle.setText("双色球新闻");
        }
        else if (lotteryName.equals("3d")) {
            topTitle.setText("3D新闻");
        }
        else if (lotteryName.equals("dlt")) {
            topTitle.setText("大乐透新闻");
        }
        else if (lotteryName.equals("zq")) {
            topTitle.setText("竞彩新闻");
        }
        else if (lotteryName.equals("other")) {
            topTitle.setText("其他彩票新闻");
        }
    }

    class LotteryNewsTask
        extends AsyncTask<Void, Object, String> {
        int flag = viewPager.getCurrentItem();

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "list_articles");
            parameter.put("pid", LotteryUtils.getPid(LotteryNewsOfOneType.this));
            parameter.put("type_id", "" + newsTypeId);
            parameter.put("page_no", "" + pageNo[flag]);
            parameter.put("size", "" + size);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(LotteryNewsOfOneType.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            actualListView[flag].removeFooterView(footView[flag]);
            dismissProgress();
            ifGetMoreData[flag] = true;
            if (ifClearArray[flag] == true) {
                newsList[flag].onHeaderRefreshComplete();
            }

            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (ifClearArray[flag] == true) {
                        newsListItemRecord.get(flag).clear();
                    }
                    ifClearArray[flag] = false;
                    if (data.equals("[]") == false) {
                        int lastSize = newsListItemRecord.get(flag).size();
                        Log.i("vincent", "lastSize: " + lastSize);
                        newsResponseData(data);
                        int preSize = newsListItemRecord.get(flag).size();
                        Log.i("vincent", "preSize: " + preSize);
                        if (preSize - lastSize < 10) {
                            ifGetMoreData[flag] = false;
                        }
                        Log.i("vincent", "ifGetMoreData[flag]  " +
                            (ifGetMoreData[flag] == true ? "true" : "false"));
                        pageNo[flag]++;
                        newsListAdapter.get(flag).notifyDataSetChanged();
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(LotteryNewsOfOneType.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(LotteryNewsOfOneType.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
                progressBars[flag].setVisibility(View.GONE);
            }
        }

        private void newsResponseData(String json) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    NewsListItem newsItem = new NewsListItem();
                    JSONObject jo = hallArray.getJSONObject(i);
                    newsItem.setTitle(jo.getString("title"));
                    newsItem.setDate(jo.getString("issue_date"));
                    newsItem.setNewsId(jo.getInt("news_id"));
                    newsListItemRecord.get(flag).add(newsItem);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBars[flag].setVisibility(View.VISIBLE);
            failPages[flag].setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open news list");
        String eventName = "v2 open news list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_news_main";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(LotteryNewsOfOneType.this)) {
            if (null != newsTask) {
                newsTask.cancel(true);
            }
            newsTask = new LotteryNewsTask();
            newsTask.execute();
        }
        else {
            newsList[viewPager.getCurrentItem()].onHeaderRefreshComplete();
            showFail(noNetTips);
        }
    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int selection) {
        topMenuLayout.check(selection);
        if (newsListItemRecord.get(selection).size() == 0) {
            newsKindIndex = selection;
            newsTypeId = Integer.valueOf(getNewsTypeId[newsKindIndex]);
            loadData();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        int flag = viewPager.getCurrentItem();
        pageNo[flag] = 1;
        ifClearArray[flag] = true;
        newsKindIndex = flag;
        newsTypeId = Integer.valueOf(getNewsTypeId[newsKindIndex]);
        loadData();
    }

    @Override
    public void onLastItemVisible() {
        int flag = viewPager.getCurrentItem();
        Log.i("vincent", flag + "  onLastItemVisible" + (ifGetMoreData[flag] == true ? "true" : "false"));
        if (ifGetMoreData[flag]) {
            actualListView[flag].addFooterView(footView[flag]);
            newsKindIndex = flag;
            newsTypeId = Integer.valueOf(getNewsTypeId[newsKindIndex]);
            loadData();
        }
        else {
            actualListView[flag].removeFooterView(footView[flag]);
        }
    }
}
