package com.haozan.caipiao.fragment;

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
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.news.LotteryNewsOfOneType;
import com.haozan.caipiao.activity.news.NewsCommentList;
import com.haozan.caipiao.adapter.NewsExpanableAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.NewsListItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadExpandableListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.umeng.analytics.MobclickAgent;

public class LotteryNewsHallFragment
    extends BasicFragment
    implements OnTabSelectedItemListener, OnPageChangeListener, OnGetDataAgainListener,
    OnHeaderRefreshListener {
    private static final String NO_DATA = "暂无新闻\n我们会尽快更新哦";

    private static final int TYPE_NUM = 5;
    private int DATA_NUM = 5;
    private int[] pageNo = {1, 1, 1, 1, 1};
    private int size = 10;

    private String types = "1,2,3";
    private String[] jsonName = types.split("\\,");
    private String[] jsonNameText = {"新闻", "预测", "技巧"};

    private ArrayList<ArrayList<NewsListItem>> newsItemList = new ArrayList<ArrayList<NewsListItem>>();
    private ArrayList<ArrayList<ArrayList<NewsListItem>>> newsList =
        new ArrayList<ArrayList<ArrayList<NewsListItem>>>();
    private ArrayList<NewsExpanableAdapter> newsExpanableAdapter = new ArrayList<NewsExpanableAdapter>();

    private String[] lottryTypeArray = {"ssq", "dlt", "zq", "3d", "other"};
    private int[] otherNewsType = {29, 30, 31};
    private String[] typeArray = {"1,2,3", "20,21,22", "32,33,34", "4,5,6", "29,30,31"};

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<View> arrayViews;
    private View[] views;
    private EmptyLayout[] layoutsEmpty;
    private AutoLoadExpandableListView[] autoLoadListView;
    private RefreshLayout[] layoutsPullToRefresh;
    private TopMenuLayout topMenuLayout;
    private String[] topContent = new String[] {"双色球", "大乐透", "竞 彩", "3 D", "其 他"};

    private NewsTask task;

    private boolean isRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = View.inflate(mContext, R.layout.news_selection, null);
        setupViews(layout);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void setupViews(View layout) {
        topMenuLayout = (TopMenuLayout) layout.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTopMenuItemContent(topContent);
        topMenuLayout.setTabSelectedListener(this);
        viewPager = (ViewPager) layout.findViewById(R.id.view_pager);

        arrayViews = new ArrayList<View>();
        views = new View[TYPE_NUM];
        layoutsEmpty = new EmptyLayout[TYPE_NUM];
        layoutsPullToRefresh = new RefreshLayout[TYPE_NUM];
        autoLoadListView = new AutoLoadExpandableListView[TYPE_NUM];
        for (int i = 0; i < TYPE_NUM; i++) {
            views[i] = View.inflate(mContext, R.layout.include_new_viewpager_expandable_listview, null);
            layoutsEmpty[i] = (EmptyLayout) views[i].findViewById(R.id.empty_layout);
            layoutsEmpty[i].setOnGetDataAgainListener(this);
            layoutsPullToRefresh[i] = (RefreshLayout) views[i].findViewById(R.id.pull_refresh_view);
            layoutsPullToRefresh[i].setOnHeaderRefreshListener(this);
            autoLoadListView[i] =
                (AutoLoadExpandableListView) views[i].findViewById(R.id.expandable_listview);
            arrayViews.add(views[i]);
        }

        viewPagerAdapter = new ViewPagerAdapter(arrayViews);
        viewPagerAdapter.setTabContent(topContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void init() {
        for (int i = 0; i < TYPE_NUM; i++) {
            final int j = i;
            ArrayList<NewsListItem> list0 = new ArrayList<NewsListItem>();
            newsItemList.add(list0);
            ArrayList<ArrayList<NewsListItem>> list1 = new ArrayList<ArrayList<NewsListItem>>();
            newsList.add(list1);
            NewsExpanableAdapter adapter = new NewsExpanableAdapter(mContext, list1);
            newsExpanableAdapter.add(adapter);
            autoLoadListView[i].setAdapter(adapter);
            autoLoadListView[i].setGroupIndicator(null);
            autoLoadListView[i].setChildIndicator(null);
            autoLoadListView[i].setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
                    Intent intent = new Intent();
                    intent = new Intent();
                    Bundle bundle = new Bundle();
                    if (viewPager.getCurrentItem() == 4)
                        bundle.putInt("lottery_type", otherNewsType[arg2]);
                    else
                        bundle.putInt("lottery_type", Integer.valueOf(jsonName[arg2]));
                    if (arg2 == 0)
                        bundle.putInt("news_kind", 0);
                    else if (arg2 == 1)
                        bundle.putInt("news_kind", 1);
                    else if (arg2 == 2)
                        bundle.putInt("news_kind", 2);
                    bundle.putString("lottery_name", lottryTypeArray[viewPager.getCurrentItem()]);
                    intent.putExtras(bundle);
                    intent.setClass(mContext, LotteryNewsOfOneType.class);
                    startActivity(intent);
                    return true;
                }
            });
            autoLoadListView[i].setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    try {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("news_id",
                                      newsList.get(j).get(groupPosition).get(childPosition).getNewsId());
                        bundle.putString("title", jsonNameText[groupPosition]);
                        intent.putExtras(bundle);
                        intent.setClass(mContext, NewsCommentList.class);
                        startActivity(intent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
        loadData();
    }

// private int[][] newsTypeArray = { {7, 10, 13, 16, 23}, {8, 11, 14, 17, 24}, {9, 12, 15, 18, 25}};

    class NewsTask
        extends AsyncTask<Object, Object, Object> {
        private int type = viewPager.getCurrentItem();

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "list_articles");
            parameter.put("pid", LotteryUtils.getPid(mContext));
            parameter.put("types", typeArray[type]);
            parameter.put("page_no", "" + pageNo[type]);
            parameter.put("num", "" + DATA_NUM);
            parameter.put("size", "" + size);
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(mContext);
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
        protected void onPostExecute(Object json) {
            super.onPostExecute(json);
            dismissProgress();
            type = viewPager.getCurrentItem();
            layoutsPullToRefresh[type].onHeaderRefreshComplete();

            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus((String) json);
                if (status.equals("200")) {
                    autoLoadListView[type].setVisibility(View.VISIBLE);
                    if (isRefresh) {
                        newsItemList.get(type).clear();
                        newsList.get(type).clear();
                        isRefresh = false;
                    }
                    autoLoadListView[type].loadNoMoreData();
                    String data = analyse.getData((String) json, "response_data");
                    try {
                        JSONArray hallArray = new JSONArray(data);
                        JSONObject jo = hallArray.getJSONObject(0);

                        jsonName = typeArray[type].split("\\,");
                        ArrayList<NewsListItem> tempList = new ArrayList<NewsListItem>();
                        for (int i = 0; i < jo.length(); i++) {
                            JSONArray dataArray = jo.getJSONArray(jsonName[i]);
                            for (int j = 0; j < dataArray.length(); j++) {
                                NewsListItem newListItem = new NewsListItem();
                                newListItem.setNewsId(dataArray.getJSONObject(j).getInt("news_id"));
                                newListItem.setTitle(dataArray.getJSONObject(j).getString("title"));
                                newListItem.setDate(dataArray.getJSONObject(j).getString("issue_date"));
                                tempList.add(newListItem);
                            }
                            newsList.get(type).add(tempList);
                            tempList = new ArrayList<NewsListItem>();
                        }
                        if (viewPager.getCurrentItem() == type) {
                            autoLoadListView[type].setVisibility(View.GONE);
                            newsExpanableAdapter.get(type).notifyDataSetChanged();
                            autoLoadListView[type].setVisibility(View.VISIBLE);
                        }

                        for (int n = 0; n < 3; n++)
                            autoLoadListView[type].expandGroup(n);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        showFail();
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(mContext);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(mContext);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (newsList.get(viewPager.getCurrentItem()).size() == 0) {
                showProgress();
            }

            layoutsEmpty[type].setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open news select");
        String eventName = "v2 open news select";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    private void showNoData() {
        if (newsList.get(viewPager.getCurrentItem()).size() == 0) {
            layoutsEmpty[viewPager.getCurrentItem()].setVisibility(View.VISIBLE);
            layoutsEmpty[viewPager.getCurrentItem()].showNoDataPage(NO_DATA);
        }
    }

    public void showFail() {
        int flag = viewPager.getCurrentItem();
        if (newsList.get(flag).size() == 0) {
            layoutsEmpty[flag].setVisibility(View.VISIBLE);
            layoutsEmpty[flag].showFailPage();
            autoLoadListView[flag].setVisibility(View.GONE);
        }
        else {
            autoLoadListView[flag].setVisibility(View.VISIBLE);
            ViewUtil.showTipsToast(mContext, failTips);
        }
    }

    @Override
    public void submitData() {
        String eventName = "open_news_kind_select";
        MobclickAgent.onEvent(mContext, eventName);
        besttoneEventCommint(eventName);
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

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            if (null != task) {
                task.cancel(true);
            }
            task = new NewsTask();
            task.execute();
        }
        else {
            layoutsPullToRefresh[viewPager.getCurrentItem()].onHeaderRefreshComplete();
            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (newsList.get(viewPager.getCurrentItem()).size() == 0) {
            layoutsEmpty[viewPager.getCurrentItem()].showNetErrorPage();
        }
        else {
            ViewUtil.showTipsToast(mContext, noNetTips);
        }
    }

    @Override
    public void onPageSelected(int index) {
        topMenuLayout.check(index);

        if (newsList.get(index).size() == 0) {
            loadData();
        }
    }

    @Override
    public void onClickToGetData() {
        loadData();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;

        loadData();
    }
}
