package com.haozan.caipiao.activity.userinf;

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
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.guess.CreateMyGuess;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.NoticeListAdapter;
import com.haozan.caipiao.adapter.UserMessageAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.NoticeContent;
import com.haozan.caipiao.types.UserMessageData;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MySpanableSet;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadExpandableListView;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.EmptyLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.AutoLoadExpandableListView.LoadDataListener;
import com.haozan.caipiao.widget.EmptyLayout.OnGetDataAgainListener;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息中心
 * 
 * @author peter_feng
 * @create-time 2013-6-8 下午6:08:35
 */
public class MessageCenter
    extends BasicActivity
    implements OnClickListener, LoadDataListener, OnTabSelectedItemListener, OnPageChangeListener,
    OnHeaderRefreshListener, com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener,
    OnGetDataAgainListener {
    private static final int SYSTEM_NOTICE = 0;
    private static final int USER_MESSAGE = 1;

    private static final String NO_NOTICE_DATA = "暂无公告，有任何活动我们会及时通知哦..";
    private static final String NO_MESSAGE_DATA = "暂无个人消息，有任何消息我们会及时通知哦..";

    private String[] tabContent = new String[] {"系统公告", "个人消息"};

    private TopMenuLayout topMenuLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<View> viewList;

    private int pageNotice = 1;
    private NoticeListAdapter noticeAdapter;
    private AutoLoadListView noticeListView;
    private ArrayList<NoticeContent> noticeData;
    private NoticeListTask noticeTask;
    private EmptyLayout layoutEmptyNotice;
    private RefreshLayout viewRefreshNotice;

    private int pageMessage = 1;
    private UserMessageAdapter messageAdapter;
    private AutoLoadExpandableListView messageListView;
    private ArrayList<UserMessageData> titleData;
    private ArrayList<SpannableStringBuilder> messageData;
    private GetMessageTask messageTask;
    private EmptyLayout layoutEmptyMessage;
    private RefreshLayout viewRefreshMessage;

    // 选中0代表系统公告，1代表个人消息
    private int selectedIndex = SYSTEM_NOTICE;

    private boolean toLogin = false;

    private boolean isRefresh = true;
    
    private MySpanableSet mySpanableSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        initData();
        setUpView();
        init();
    }

    private void initData() {
        noticeData = new ArrayList<NoticeContent>();
        titleData = new ArrayList<UserMessageData>();
        messageData = new ArrayList<SpannableStringBuilder>();

        viewList = new ArrayList<View>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("message_type");
            if (type == USER_MESSAGE) {
                selectedIndex = USER_MESSAGE;
            }
        }
    }

    private void setUpView() {
        setupMainViews();
        setupNoticeViews();
        setupMessageViews();
    }

    private void setupMainViews() {
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTabSelectedListener(this);
        viewPager = (ViewPager) this.findViewById(R.id.view_pager);
    }

    private void setupNoticeViews() {
        LayoutInflater inflater = getLayoutInflater();
        View viewNotice = inflater.inflate(R.layout.include_new_viewpager_listview, null);

        noticeListView = (AutoLoadListView) viewNotice.findViewById(R.id.listview);
        noticeListView.setOnLoadDataListener(this);
        layoutEmptyNotice = (EmptyLayout) viewNotice.findViewById(R.id.empty_layout);
        layoutEmptyNotice.setOnGetDataAgainListener(this);
        viewRefreshNotice = (RefreshLayout) viewNotice.findViewById(R.id.pull_refresh_view);
        viewRefreshNotice.setOnHeaderRefreshListener(this);

        viewList.add(viewNotice);
    }

    private void setupMessageViews() {
        LayoutInflater inflater = getLayoutInflater();
        View viewMessage = inflater.inflate(R.layout.include_new_viewpager_expandable_listview, null);

        messageListView = (AutoLoadExpandableListView) viewMessage.findViewById(R.id.expandable_listview);
        messageListView.setOnLoadDataListener(this);
        layoutEmptyMessage = (EmptyLayout) viewMessage.findViewById(R.id.empty_layout);
        layoutEmptyMessage.setOnGetDataAgainListener(this);
        viewRefreshMessage = (RefreshLayout) viewMessage.findViewById(R.id.pull_refresh_view);
        viewRefreshMessage.setOnHeaderRefreshListener(this);

        viewList.add(viewMessage);
    }

    private void init() {
        mySpanableSet = new MySpanableSet(this);
        
        viewPagerAdapter = new ViewPagerAdapter(viewList);
        viewPagerAdapter.setTabContent(tabContent);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        noticeAdapter = new NoticeListAdapter(MessageCenter.this, noticeData);
        noticeListView.setAdapter(noticeAdapter);

        messageAdapter = new UserMessageAdapter(MessageCenter.this, titleData, messageData);
        messageListView.setAdapter(messageAdapter);
        messageListView.setGroupIndicator(null);
        messageListView.setChildIndicator(null);
        messageListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                UserMessageData message =
                    (UserMessageData) parent.getExpandableListAdapter().getGroup(groupPosition);
                if (message.getIsRead() == false) {
                    titleData.get(groupPosition).setIsRead(true);
                    messageAdapter.notifyDataSetChanged();
                    SendMessageReadTask messageIsRead = new SendMessageReadTask();
                    messageIsRead.execute(message.getId());
                    appState.setMessageNumber(appState.getMessageNumber() - 1);
                    if (appState.getMessageNumber() <= 0) {
                        OperateInfUtils.broadcast(MessageCenter.this, "invalidate_notice");
                    }
                }
                return false;
            }
        });

        topMenuLayout.setTopMenuItemContent(tabContent);

        topMenuLayout.check(selectedIndex);
        loadData();
    }

    private void showFail() {
        if (selectedIndex == SYSTEM_NOTICE) {
            if (noticeData.size() == 0) {
                noticeListView.setVisibility(View.GONE);
                layoutEmptyNotice.setVisibility(View.VISIBLE);
                layoutEmptyNotice.showFailPage();
            }
            else {
                ViewUtil.showTipsToast(this, failTips);
                noticeListView.showFootText();
            }
        }
        else if (selectedIndex == USER_MESSAGE) {
            if (titleData.size() == 0) {
                messageListView.setVisibility(View.GONE);
                layoutEmptyMessage.setVisibility(View.VISIBLE);
                layoutEmptyMessage.showFailPage();
            }
            else {
                ViewUtil.showTipsToast(this, failTips);
                messageListView.showFootText();
            }
        }
    }

    private void showNoData() {
        if (selectedIndex == SYSTEM_NOTICE) {
            if (noticeData.size() == 0) {
                layoutEmptyNotice.setVisibility(View.VISIBLE);
                layoutEmptyNotice.showNoDataPage(NO_NOTICE_DATA);
                noticeListView.setVisibility(View.GONE);
            }
        }
        else if (selectedIndex == USER_MESSAGE) {
            if (titleData.size() == 0) {
                layoutEmptyMessage.setVisibility(View.VISIBLE);
                layoutEmptyMessage.showNoDataPage(NO_MESSAGE_DATA);
                messageListView.setVisibility(View.GONE);
            }
        }
    }

    class GetMessageTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "get_user_notice");
            parameter.put("pid", LotteryUtils.getPid(MessageCenter.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
            parameter.put("page_no", "" + pageMessage);
            parameter.put("size", "10");
            parameter.put("query_type", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MessageCenter.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            viewRefreshMessage.onHeaderRefreshComplete();
            dismissProgress();
            if (json == null) {
                showFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    if (isRefresh) {
                        titleData.clear();
                        messageData.clear();
                        isRefresh = false;
                    }

                    messageListView.setVisibility(View.VISIBLE);

                    String data = analyse.getData(json, "response_data");
                    if (data.equals("[]") == false) {
                        int lastSize = titleData.size();
                        messageResponseData(data);
                        int preSize = titleData.size();
                        if (preSize - lastSize < 10) {
                            messageListView.loadNoMoreData();
                        }
                        else {
                            messageListView.readyToLoad();
                        }
                        pageMessage++;
                        messageAdapter.notifyDataSetChanged();
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(MessageCenter.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MessageCenter.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail();
                }
            }
        }

        private void messageResponseData(String json) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    UserMessageData message = new UserMessageData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    message.setId(jo.getString("id"));
                    message.setSubject(jo.getString("subject"));
                    message.setTime(jo.getString("time"));
                    int isRead = jo.getInt("is_read");
                    if (isRead == 0)
                        message.setIsRead(false);
                    else if (isRead == 1)
                        message.setIsRead(true);
                    titleData.add(message);
                    messageData.add(mySpanableSet.getText("　　" + jo.getString("content")));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (messageData.size() == 0) {
                layoutEmptyMessage.setVisibility(View.GONE);
                showProgress();
            }
        }
    }

    class NoticeListTask
        extends AsyncTask<String, Object, String> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001090");
            parameter.put("pid", LotteryUtils.getPid(MessageCenter.this));
            parameter.put("page_no", "" + pageNotice);
            parameter.put("num", "10");
            parameter.put("version", LotteryUtils.fullVersion(MessageCenter.this));
            return parameter;
        }

        @Override
        protected String doInBackground(String... para) {
            ConnectService connectNet = new ConnectService(MessageCenter.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private void noticeListResponseData(JSONArray jsonArray) {
            try {
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    NoticeContent notice = new NoticeContent();
                    JSONObject jo = jsonArray.getJSONObject(i);
                    notice.setId(jo.getString("id"));
                    notice.setTitle(jo.getString("subject"));
                    notice.setDigest(mySpanableSet.getText(jo.getString("digest")));
                    notice.setUrgent(jo.getString("urgent"));
                    notice.setContent(jo.getString("content"));
                    String time = jo.getString("issue_date");
                    time = time.substring(0, 11);
                    notice.setTime(time);
                    noticeData.add(notice);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            viewRefreshNotice.onHeaderRefreshComplete();
            dismissProgress();

            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    if (isRefresh) {
                        noticeData.clear();
                        isRefresh = false;
                    }
                    String response_data = analyse.getData(json, "response_data");
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(response_data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (response_data.equals("[]") == false) {
                        int lastSize = noticeData.size();
                        noticeListResponseData(jsonArray);
                        int preSize = noticeData.size();
                        if (preSize - lastSize < 10) {
                            noticeListView.loadNoMoreData();
                        }
                        else {
                            noticeListView.readyToLoad();
                        }
                        pageNotice++;
                        noticeAdapter.notifyDataSetChanged();

                        noticeListView.setVisibility(View.VISIBLE);
                    }
                    else {
                        showNoData();
                    }
                }
                else {
                    showFail();
                }
            }
            else {
                showFail();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (noticeData.size() == 0) {
                layoutEmptyNotice.setVisibility(View.GONE);
                showProgress();
            }
        }
    }

    class SendMessageReadTask
        extends AsyncTask<String, Void, String> {

        private HashMap<String, String> initHashMap(String id) {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "return_user_read");
            parameter.put("pid", LotteryUtils.getPid(MessageCenter.this));
            parameter.put("notice_id", id);
            parameter.put("phone", appState.getUsername());
            return parameter;
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectService connectNet = new ConnectService(MessageCenter.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, true, initHashMap(params[0]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
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
            intent.setClass(MessageCenter.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.to_submit_quetion) {
            Intent intent = new Intent();
            intent.setClass(MessageCenter.this, CreateMyGuess.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open notice list");
        String eventName = "open notice list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_notice_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            if (noticeTask != null) {
                noticeTask.cancel(true);
            }
            if (messageTask != null) {
                messageTask.cancel(true);
            }
            if (selectedIndex == SYSTEM_NOTICE) {
                noticeTask = new NoticeListTask();
                noticeTask.execute();
            }
            else if (selectedIndex == USER_MESSAGE) {
                if (appState.getUsername() != null) {
                    messageTask = new GetMessageTask();
                    messageTask.execute();
                }
                else {
                    toLogin = true;
                    ActionUtil.toLogin(this, null);
                }
            }
        }
        else {
            viewRefreshNotice.onHeaderRefreshComplete();
            viewRefreshMessage.onHeaderRefreshComplete();

            showNetWorkErrorPage();
        }
    }

    private void showNetWorkErrorPage() {
        if (selectedIndex == SYSTEM_NOTICE) {
            if (noticeData.size() == 0) {
                layoutEmptyMessage.setVisibility(View.VISIBLE);
                layoutEmptyNotice.showNetErrorPage();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
                noticeListView.showFootText();
            }
        }
        else if (selectedIndex == USER_MESSAGE) {
            if (messageData.size() == 0) {
                layoutEmptyMessage.setVisibility(View.VISIBLE);
                layoutEmptyMessage.showNetErrorPage();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
                messageListView.showFootText();
            }
        }
    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toLogin) {
            toLogin = false;
            if (((LotteryApp) getApplication()).getUsername() != null) {
                selectedIndex = USER_MESSAGE;
                if (titleData.size() != 0) {
                    viewRefreshMessage.headerRefreshing();
                }
                onRefresh();
            }
            else {
                topMenuLayout.check(SYSTEM_NOTICE);
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
        if ((index == SYSTEM_NOTICE && noticeData.size() == 0) ||
            (index == USER_MESSAGE && titleData.size() == 0)) {
            loadData();
        }
    }

    @Override
    public void onRefresh() {
        if (selectedIndex == SYSTEM_NOTICE) {
            pageNotice = 1;
        }
        else {
            pageMessage = 1;
        }
        isRefresh = true;

        loadData();
    }

    @Override
    public void onClickToGetData() {
        onRefresh();
    }
}
