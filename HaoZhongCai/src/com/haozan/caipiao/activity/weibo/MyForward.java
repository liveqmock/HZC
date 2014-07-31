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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.news.NewsCommentList;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyForwardData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 财园对我的转发
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:55:05
 */
public class MyForward
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询信息失败或信息已删除";

    MyForwardAdapter adapter;
    private ArrayList<MyForwardData> myForwardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("对我的转发");
    }

    public void initSub() {
        myForwardList = new ArrayList<MyForwardData>();
        adapter = new MyForwardAdapter(MyForward.this, myForwardList);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(MyForward.this)) {
            MyConmentTask Task = new MyConmentTask();
            Task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class MyConmentTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004130");
            parameter.put("pid", LotteryUtils.getPid(MyForward.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "6");
            parameter.put("page_no", "" + page);
            parameter.put("size", "" + SIZE);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyForward.this);
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
            pulllistView.onHeaderRefreshComplete();
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (ifClearArray == true) {
                        myForwardList.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = myForwardList.size();
                        getmyforwardArray(myForwardList, data);
                        int preSize = myForwardList.size();
                        if (preSize - lastSize < 10) {
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
                    OperateInfUtils.clearSessionId(MyForward.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyForward.this);
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
        }
    }

    public void getmyforwardArray(ArrayList<MyForwardData> myforwardArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    MyForwardData data = new MyForwardData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    data.setUserid(jo.getString("user_id"));
                    data.setUserid(jo.getString("user_id"));
                    data.setName(jo.getString("nickname"));
                    data.setTime(jo.getString("issue_date"));
                    data.setContent(jo.getString("content"));
                    data.setAttachid(jo.getString("attach_id"));
                    data.setTitle(jo.getString("title"));
                    data.setPreview(jo.getString("preview"));
                    data.setType(jo.getInt("type"));
                    data.setSource(jo.getString("source"));
                    data.setBitmap(null);
                    // GetPicTask task = new GetPicTask(data);
                    // task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?"
                    // +
                    // "&phone=" + appState.getUsername() + "&user_id=" +
                    // data.getUserid() +
                    // "&service=2004100");
                    myforwardArray.add(data);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNoData() {
        if (myForwardList.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    public void showFail(String failInf) {
        if (myForwardList.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public class MyForwardAdapter
        extends BaseAdapter {
        private ArrayList<MyForwardData> forward;
        private Context context;
        private LayoutInflater inflater;

        public MyForwardAdapter(Context context, ArrayList<MyForwardData> forward) {
            super();
            this.context = context;
            this.forward = forward;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final MyForwardData data = forward.get(position);
            View statusView = null;
            if ((convertView != null) && (convertView.findViewById(R.id.ivItemPortrait) != null)) {
                statusView = convertView;
            }
            else {
                statusView = LayoutInflater.from(context).inflate(R.layout.weibo_list, null);
            }

            ImageView avatar = (ImageView) statusView.findViewById(R.id.ivItemPortrait);
            if (data.getBitmap() != null) {

                avatar.setImageBitmap(data.getBitmap());
            }
            else {
                avatar.setImageResource(R.drawable.lucky_cat);
            }

            TextView niceName = (TextView) statusView.findViewById(R.id.niceName);
            TextView content = (TextView) statusView.findViewById(R.id.tvItemContent);
            TextView creattime = (TextView) statusView.findViewById(R.id.tvItemDate);
            LinearLayout weiboLayout = (LinearLayout) statusView.findViewById(R.id.weibolistLayout);
            TextView newsTitle = (TextView) statusView.findViewById(R.id.news_title);
            TextView weiboFrom = (TextView) statusView.findViewById(R.id.weibo_from);
            final String title = data.getTitle();
            String name = data.getName();
            if (name == null) {
                niceName.setText("匿名彩友");
            }
            else {
                if (data.getName().equals("null") || data.getName().equals("")) {
                    niceName.setText("匿名彩友");
                }
                else {
                    niceName.setText(data.getName());
                }
            }

            if (title.equals("dfljy")) {
                String money = data.getContent();
                String money1 = money.replaceFirst(" ", "+");
                content.setText(money1);
            }
            else {
                content.setText(TextUtil.formatContent(data.getContent(), context));
            }
            creattime.setText(data.getTime());
            String preview = data.getPreview();
            int type = data.getType();
            String version = data.getSource();

            if (title.equals("null") || preview.equals("")) {
                newsTitle.setVisibility(View.GONE);
            }
            else {
                if (type == 3) {
                    newsTitle.setVisibility(View.VISIBLE);
                    newsTitle.setText(Html.fromHtml("<font color='#000000' size='5'>" + "<b>" + "文章标题：" +
                        title + "</b>" + "</font>" + "    " + preview + "..."));
                }
                else if (type == 4) {
                    newsTitle.setVisibility(View.VISIBLE);
                    String[] previews = preview.split(";");
                    StringBuilder s = new StringBuilder();
                    s.append("<font color='#000000' size='5'>" + "<b>" + "投注信息：");
                    s.append(LotteryUtils.getLotteryName(title));
                    s.append("</b>" + "</font>" + "<br/>");

                    if (title.equals("dfljy")) {
                        for (int i = 0; i < previews.length; i++) {
                            String[] lotteryNum = previews[i].split("\\:");
                            String[] ball = lotteryNum[0].split("\\|");
                            String[] animals = ball[1].split(",");
                            ball[1] = "";
                            for (int i1 = 0; i1 < animals.length; i1++) {
                                animals[i1] = LotteryUtils.animals[Integer.valueOf(animals[i1]) - 1];
                                ball[1] += animals[i1];
                            }
                            s.append(ball[0] + "|" + ball[1].subSequence(0, ball[1].length()) + "<br/>");
                        }
                        newsTitle.setText(Html.fromHtml(s.toString()));
                    }
                    else {
                        for (int i = 0; i < previews.length; i++) {
                            String[] lotteryNum = previews[i].split("\\:");
                            s.append(lotteryNum[0] + "<br/>");
                        }
                        newsTitle.setText(Html.fromHtml(s.toString()));
                    }
                }
                else {
                    newsTitle.setVisibility(View.VISIBLE);
                    newsTitle.setText(Html.fromHtml("<font color='#000000' size='5'>" + "<b>" + title +
                        "</b>" + "</font>" + "    " + preview + "..."));
                }
            }
            if (type == 0) {
                weiboFrom.setText("发自：动态发表");
            }
            else if (type == 1) {
                weiboFrom.setText("发自：动态评论");
            }
            else if (type == 2) {
                weiboFrom.setText("发自：动态转发");
            }
            else if (type == 3) {
                weiboFrom.setText("发自：" + version + "新闻");
                newsTitle.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "新　 闻");
                        bundle.putInt("news_id", Integer.valueOf(data.getAttachid()));
                        // bundle.putString("title", topTitle.getText().toString());
                        intent.putExtras(bundle);
                        intent.setClass(context, NewsCommentList.class);
                        context.startActivity(intent);
                    }
                });
            }
            else if (type == 4) {
                weiboFrom.setText("发自：" + version + "彩票投注");
                newsTitle.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("resource", "bet_weibo");
                        bundle.putString("bet_code", data.getPreview());
                        intent.putExtras(bundle);
                        // <通用方法>判断来自哪个彩种，与if语句效果相同
                        Boolean isFail = false;
                        for (int i = 0; i < LotteryUtils.lotteryclass.length; i++) {
                            if (title.equals(LotteryUtils.LOTTERY_ID[i])) {
                                isFail = true;
                                intent.setClass(context, LotteryUtils.lotteryclass[i]);
                                context.startActivity(intent);
                            }
                        }
                        if (isFail == false) {
                            Toast.makeText(context, "版本暂不支持该彩种", 1000).show();
                        }
                    }
                });
            }

            weiboLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(context, WeiboZhengWen.class);
                    Bundle b = new Bundle();
                    b.putString("weiboId", String.valueOf(data.getId()));
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });

            avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(data.getUserid()) ||
                        appState.getUserid() == data.getUserid()) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyProfileActivity.class);
                        context.startActivity(intent);
                    }
                    else {
                        Intent intent1 = new Intent();
                        intent1.setClass(context, UserProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("userId", Integer.valueOf(data.getUserid()));
                        intent1.putExtras(b);
                        context.startActivity(intent1);
                    }
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(MyForward.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                       R.anim.push_to_right_out);
                    }
                }
            });
            return statusView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return forward.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return forward.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden my forward list");
        String eventName = "v2 open garden my forward list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_my_forward_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

}
