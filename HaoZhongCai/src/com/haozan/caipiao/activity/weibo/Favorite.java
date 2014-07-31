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
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FavoriteData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 收藏的财园信息
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:43:08
 */
public class Favorite
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询收藏信息失败或信息已删除";

    FavoriteAdapter adapter;
    private ArrayList<FavoriteData> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("收藏");
    }

    public void initSub() {
        favoriteList = new ArrayList<FavoriteData>();
        adapter = new FavoriteAdapter(Favorite.this, favoriteList);
        actualListView.setAdapter(adapter);
        loadData();

    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(Favorite.this)) {
            FavoriteTask Task = new FavoriteTask();
            Task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class FavoriteTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004040");
            parameter.put("pid", LotteryUtils.getPid(Favorite.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "6");
            parameter.put("user_id", appState.getUserid());
            parameter.put("page_no", "" + page);
            parameter.put("size", "" + SIZE);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(Favorite.this);
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
                        favoriteList.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = favoriteList.size();
                        getFavoriteArray(favoriteList, data);
                        int preSize = favoriteList.size();
                        if (preSize - lastSize < 10) {
                            ifGetMoreData = false;
                        }
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(Favorite.this, "暂无收藏数据");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(Favorite.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(Favorite.this);
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

    public void getFavoriteArray(ArrayList<FavoriteData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    FavoriteData data = new FavoriteData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    data.setId(jo.getString("weibo_id"));
                    data.setName(jo.getString("nickname"));
                    data.setTime(jo.getString("issue_date"));
                    data.setUserid(jo.getString("user_id"));
                    data.setContent(jo.getString("content"));
                    data.setReplyCount(jo.getString("reply_count"));
                    data.setRetweetCount(jo.getString("retweet_count"));
                    data.setAttachid(jo.getString("attach_id"));
                    data.setTitle(jo.getString("title"));
                    data.setPreview(jo.getString("preview"));
                    data.setType(jo.getInt("type"));
                    data.setSource(jo.getString("source"));
                    data.setBitmap(null);
                    myweiboArray.add(data);
                }
                adapter.notifyDataSetChanged();
                page++;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNoData() {
        if (favoriteList.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    public void showFail(String failInf) {
        if (favoriteList.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    private class FavoriteAdapter
        extends BaseAdapter {
        private ArrayList<FavoriteData> favdata;
        private Context context;
        private LayoutInflater inflater;

        public FavoriteAdapter(Context context, ArrayList<FavoriteData> favdata) {
            super();
            this.context = context;
            this.favdata = favdata;
            inflater = LayoutInflater.from(this.context);
        }

        private class ViewHolder {
            private TextView niceName;
            private TextView content;
            private TextView creattime;
            private TextView newsTitle;
            private TextView conmentCount;
            private TextView forwardCount;
            private TextView weiboFrom;
            private ImageView avatar;
            private ImageView locationPic;
            private ImageView unitePic;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final FavoriteData fs = favdata.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.favorite_item, null);
                viewHolder = new ViewHolder();
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
                viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
                viewHolder.conmentCount = (TextView) convertView.findViewById(R.id.conment_count);
                viewHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
                viewHolder.weiboFrom = (TextView) convertView.findViewById(R.id.weibo_from);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
                viewHolder.locationPic = (ImageView) convertView.findViewById(R.id.location_pic);
                viewHolder.unitePic = (ImageView) convertView.findViewById(R.id.unite_pic);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (fs.getBitmap() != null) {

                viewHolder.avatar.setImageBitmap(fs.getBitmap());
            }
            else {
                viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
            }
            final String title = fs.getTitle();
            String name = fs.getName();
            if (name == null) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                if (fs.getName().equals("null") || fs.getName().equals("")) {
                    viewHolder.niceName.setText("匿名彩友");
                }
                else {
                    viewHolder.niceName.setText(fs.getName());
                }
            }
            viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(fs.getTime(),
                                                                                   "yyyy-MM-dd HH:mm:ss")));
            viewHolder.conmentCount.setText(String.valueOf(fs.getReplyCount()));// 评论数
            viewHolder.forwardCount.setText(String.valueOf(fs.getRetweetCount()));// 转发数

            if (title.equals("dfljy")) {
                String money = fs.getContent();
                String money1 = money.replaceFirst(" ", "+");
                viewHolder.content.setText(money1);
            }
            else {
                viewHolder.content.setText(TextUtil.formatContent(fs.getContent(), context));
            }

            // 如果包含"投注位置"则显示位置图标
            BasicWeibo.locationPicShow(fs.getContent(), viewHolder.locationPic);
            // 如果包含"合买"，则显示合买图标
            BasicWeibo.unitePicShow(fs.getContent(), viewHolder.unitePic);

            String preview = fs.getPreview();
            int type = fs.getType();
            String version = fs.getSource();

            // 子内容显示
            BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, null,
                                      viewHolder.content, fs.getContent());

            // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
            BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                                 fs.getAttachid(), preview);

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, WeiboZhengWen.class);
                    Bundle b = new Bundle();
                    b.putString("weiboId", fs.getId());
                    intent.putExtras(b);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(Favorite.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                      R.anim.push_to_right_out);
                    }
                }
            });

            viewHolder.avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(fs.getUserid()) || appState.getUserid() == fs.getUserid()) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyProfileActivity.class);
                        context.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(context, UserProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("userId", Integer.valueOf(fs.getUserid()));
                        intent.putExtras(b);
                        context.startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return favdata.size();
        }

        @Override
        public Object getItem(int position) {
            return favdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden favorite list");
        String eventName = "v2 open garden favorite list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_favorite_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
