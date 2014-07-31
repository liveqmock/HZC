package com.haozan.caipiao.activity.weibo;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyWeiboData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.umeng.analytics.MobclickAgent;

public class MyWeiboActivity
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询动态信息失败或信息已删除";

    MyWeiboAdapter adapter;
    private ArrayList<MyWeiboData> subArray;

    private boolean refreshButton1IsClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setupSubViews() {
        titleTextView.setText("我的动态");
    }

    public void initSub() {
        subArray = new ArrayList<MyWeiboData>();
        adapter = new MyWeiboAdapter(MyWeiboActivity.this, subArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(MyWeiboActivity.this)) {
            MyWeiboTask getWeiboTask = new MyWeiboTask();
            getWeiboTask.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class MyWeiboTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004040");
            parameter.put("pid", LotteryUtils.getPid(MyWeiboActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + SIZE);
            parameter.put("type", "5");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyWeiboActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            actualListView.removeFooterView(footView);
            dismissProgress();
            ifGetMoreData = true;
            if (refreshButton1IsClick == true) {
                pulllistView.onHeaderRefreshComplete();
            }
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    // 如果点击的是刷新按钮
                    if (refreshButton1IsClick == true) {
                        subArray.clear();
                    }
                    refreshButton1IsClick = false;
                    
                    if (ifClearArray == true) {
                        subArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = subArray.size();
                        getMyWeiboArray(subArray, data);
                        int allSize = subArray.size();
                        if (allSize - lastSize < 10) {
                            ifGetMoreData = false;
                        }
                        adapter.notifyDataSetChanged();
                        page++;
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(MyWeiboActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyWeiboActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(failTips);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase view) {
        refreshButton1IsClick = true;
        super.onHeaderRefresh(view);
    }

    public void showNoData() {
        if (subArray.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    private void showFail(String failInf) {
        if (subArray.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public void getMyWeiboArray(ArrayList<MyWeiboData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    MyWeiboData weibodata = new MyWeiboData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    weibodata.setName(jo.getString("nickname"));
                    weibodata.setTime(jo.getString("issue_date"));
                    weibodata.setContent(jo.getString("content"));
                    weibodata.setRetweetCount(jo.getString("retweet_count"));
                    weibodata.setReplyCount(jo.getString("reply_count"));
                    weibodata.setId(jo.getString("weibo_id"));
                    weibodata.setUserId(jo.getString("user_id"));
                    weibodata.setAttachid(jo.getString("attach_id"));
                    weibodata.setTitle(jo.getString("title"));
                    weibodata.setPreview(jo.getString("preview"));
                    weibodata.setType(jo.getInt("type"));
                    weibodata.setSource(jo.getString("source"));
                    weibodata.setBitmap(null);
// GetPicTask task = new GetPicTask(weibodata);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + weibodata.getUserId() +
// "&service=2004100");
                    myweiboArray.add(weibodata);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyWeiboAdapter
        extends BaseAdapter {
        private ArrayList<MyWeiboData> myweibo;
        private Context context;
        private LayoutInflater inflater;

        public MyWeiboAdapter(Context context, ArrayList<MyWeiboData> myweibo) {
            super();
            this.context = context;
            this.myweibo = myweibo;
            inflater = LayoutInflater.from(this.context);
        }

        private final class ViewHolder {
            private TextView niceName;
            private TextView content;
            private TextView creattime;
            private TextView conmentCount;
            private TextView forwardCount;
            private TextView newsTitle;
            private LinearLayout ly;
            private TextView weiboFrom;
            private ImageView locationPic;
            private ImageView unitePic;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            final MyWeiboData wb = myweibo.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.myweibo_list, null);
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
                viewHolder.conmentCount = (TextView) convertView.findViewById(R.id.conment_count);
                viewHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
                viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
                viewHolder.weiboFrom = (TextView) convertView.findViewById(R.id.weibo_from);
                viewHolder.ly = (LinearLayout) convertView.findViewById(R.id.lyRightLayout);
                viewHolder.locationPic = (ImageView) convertView.findViewById(R.id.location_pic);
                viewHolder.unitePic = (ImageView) convertView.findViewById(R.id.unite_pic);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ImageView avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);

            if (wb.getBitmap() != null) {

                avatar.setImageBitmap(wb.getBitmap());
            }
            else {
                avatar.setImageResource(R.drawable.lucky_cat);
            }

            final String title = wb.getTitle();
            String name = wb.getName();
            if (name == null) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                if (wb.getName().equals("null") || wb.getName().equals("")) {
                    viewHolder.niceName.setText("匿名彩友");
                }
                else {
                    viewHolder.niceName.setText(wb.getName());
                }
            }
            if (title.equals("dfljy")) {
                String money = wb.getContent();
                String money1 = money.replaceFirst(" ", "+");
                viewHolder.content.setText(money1);
            }
            else {
                viewHolder.content.setText(TextUtil.formatContent(wb.getContent(), context));
            }

            // 如果包含"投注位置"则显示位置图标
            BasicWeibo.locationPicShow(wb.getContent(), viewHolder.locationPic);
            // 如果包含"合买"，则显示合买图标
            BasicWeibo.unitePicShow(wb.getContent(), viewHolder.unitePic);

            viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(wb.getTime(),
                                                                                   "yyyy-MM-dd HH:mm:ss")));
            viewHolder.conmentCount.setText(String.valueOf(wb.getReplyCount()));
            viewHolder.forwardCount.setText(String.valueOf(wb.getRetweetCount()));

            String preview = wb.getPreview();
            int type = wb.getType();
            String version = wb.getSource();

            // 子内容显示
            BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, null,
                                      viewHolder.content, wb.getContent());

            // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
            BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                                 wb.getAttachid(), preview);

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(wb.getUserId()) || appState.getUserid() == wb.getUserId()) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyWeiboZhengwen.class);
                        Bundle b = new Bundle();
                        // 把动态对象传到NewConmentsActivity
                        b.putSerializable("weibodata", wb);
                        intent.putExtras(b);
                        startActivityForResult(intent, 0);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(context, UserWeiboZhengwen.class);
                        Bundle b = new Bundle();
                        // 把动态对象传到NewConmentsActivity
                        b.putInt("data_type", 3);
                        b.putSerializable("userWeiboData", wb);
                        intent.putExtras(b);
                        context.startActivity(intent);
                        // ((Activity) context).finish();
                    }
                }
            });

            viewHolder.ly.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(wb.getUserId()) || appState.getUserid() == wb.getUserId()) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyWeiboZhengwen.class);
                        Bundle b = new Bundle();
                        // 把动态对象传到NewConmentsActivity
                        b.putSerializable("weibodata", wb);
                        intent.putExtras(b);
                        startActivityForResult(intent, 0);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(context, UserWeiboZhengwen.class);
                        Bundle b = new Bundle();
                        // 把动态对象传到NewConmentsActivity
                        b.putInt("data_type", 3);
                        b.putSerializable("userWeiboData", wb);
                        intent.putExtras(b);
                        context.startActivity(intent);
                        // ((Activity) context).finish();
                    }
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(MyWeiboActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                             R.anim.push_to_right_out);
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return myweibo.size();
        }

        @Override
        public Object getItem(int position) {
            return myweibo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                refreshButton1IsClick = true;
                subArray = new ArrayList<MyWeiboData>();
                adapter = new MyWeiboAdapter(MyWeiboActivity.this, subArray);
                actualListView.setAdapter(adapter);
                page = 1;
                MyWeiboTask task = new MyWeiboTask();
                task.execute();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal fans list");
        String eventName = "v2 open garden personal hall";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_hall";
        MobclickAgent.onEvent(this, eventName, "personal");
        besttoneEventCommint(eventName);
    }

}
