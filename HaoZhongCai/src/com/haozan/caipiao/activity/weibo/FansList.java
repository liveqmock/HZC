package com.haozan.caipiao.activity.weibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.connect.GetFansService;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FansListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 财园粉丝列表
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:24:26
 */
public class FansList
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "暂无粉丝信息，或信息已删除";

    FansListAdapter adapter;
    private ArrayList<FansListData> fansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("粉丝");
    }

    public void initSub() {
        fansList = new ArrayList<FansListData>();
        adapter = new FansListAdapter(FansList.this, fansList);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(FansList.this)) {
            FansTask getFansTask = new FansTask();
            getFansTask.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class FansTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetFansService getWeibo = new GetFansService(FansList.this, page, SIZE);
            String json = getWeibo.sending();
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
                        fansList.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = fansList.size();
                        getFansArray(fansList, data);
                        int allSize = fansList.size();
                        if (allSize - lastSize < 10) {
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
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(FansList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(FansList.this);
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

    public void getFansArray(ArrayList<FansListData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    FansListData fansdata = new FansListData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    fansdata.setName(jo.getString("nickname"));
                    fansdata.setAvatar(jo.getString("email"));
                    fansdata.setGender(jo.getString("gender"));
                    fansdata.setId(jo.getString("user_id"));
                    fansdata.setSignature(jo.getString("signature"));
                    fansdata.setBitmap(null);
                    myweiboArray.add(fansdata);
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
        if (fansList.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    public void showFail(String failInf) {
        if (fansList.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    class FansListAdapter
        extends BaseAdapter {
        private ArrayList<FansListData> fans;
        private Context context;
        private LayoutInflater inflater;

        public FansListAdapter(Context context, ArrayList<FansListData> fans) {
            super();
            this.context = context;
            this.fans = fans;
            inflater = LayoutInflater.from(this.context);
        }

        private class ViewHolder {
            private TextView niceName;
            private TextView qianming;
            private ImageView avatar;
            private ImageView genderPic;
            private Button deleteFans;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final FansListData fs = fans.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fans_item, null);
                viewHolder = new ViewHolder();
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.qianming = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
                viewHolder.genderPic = (ImageView) convertView.findViewById(R.id.genderPic);
                viewHolder.deleteFans = (Button) convertView.findViewById(R.id.deleteFans);
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

            String qm = fs.getSignature();
            if (qm == null || qm.equals("null")) {
                viewHolder.qianming.setText("个性签名:" + "");
            }
            else {
                viewHolder.qianming.setText("个性签名：" + fs.getSignature());
            }

            String gender = fs.getGender();
            if (gender.equals("1")) {
                viewHolder.genderPic.setImageResource(R.drawable.icon_male);
            }
            else {
                viewHolder.genderPic.setImageResource(R.drawable.icon_female);
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("data_type", 3);
                    b.putInt("userId", Integer.valueOf(fs.getId()));
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });

            viewHolder.deleteFans.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAddBlackListDialog(position);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {

            return fans.size();
        }

        @Override
        public Object getItem(int position) {
            return fans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    protected void showAddBlackListDialog(final int position) {
        CustomDialog dlgAddBlack = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setWarning().setMessage("确定删除好友吗？").setPositiveButton("删  除",
                                                                            new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog,
                                                                                                    int which) {
                                                                                    if (HttpConnectUtil.isNetworkAvailable(FansList.this)) {
                                                                                        DeleteFansTask task =
                                                                                            new DeleteFansTask(
                                                                                                               position);
                                                                                        task.execute();
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                    else {
                                                                                        ViewUtil.showTipsToast(FansList.this,
                                                                                                               noNetTips);
                                                                                    }
                                                                                }
                                                                            }).setNegativeButton("取  消",
                                                                                                 new DialogInterface.OnClickListener() {
                                                                                                     public void onClick(DialogInterface dialog,
                                                                                                                         int which) {
                                                                                                         dialog.dismiss();
                                                                                                     }
                                                                                                 });
        dlgAddBlack = customBuilder.create();
        dlgAddBlack.show();
    }

    private class DeleteFansTask
        extends AsyncTask<Void, Void, String> {
        private FansListData fansdata;

        public DeleteFansTask(int groupPosition) {
            this.fansdata = fansList.get(groupPosition);
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(FansList.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "3");
            parameter.put("user_id", fansdata.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(FansList.this);
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
        protected void onPostExecute(String result) {
            String json = (String) result;
            if (json == null) {
                ViewUtil.showTipsToast(FansList.this, "移除粉丝失败");
            }
            else {
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    if (status.equals("200")) {
                        ViewUtil.showTipsToast(FansList.this, "移除成功");
                        // 刷新页面
                        fansList.clear();
                        page = 1;
                        FansTask task = new FansTask();
                        task.execute();
                    }
                    else {
                        ViewUtil.showTipsToast(FansList.this, "移除粉丝失败");
                    }
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal fans list");
        String eventName = "v2 open garden personal fans list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_fans_list";
        MobclickAgent.onEvent(this, eventName, "personal");
        besttoneEventCommint(eventName);
    }
}
