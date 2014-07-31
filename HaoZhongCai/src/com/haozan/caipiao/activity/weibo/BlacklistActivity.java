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
import com.haozan.caipiao.connect.GetBlackService;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BlackListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 黑名单列表
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:41:36
 */
public class BlacklistActivity
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询黑名单信息失败或信息已删除";

    BlackListAdapter adapter;
    private ArrayList<BlackListData> blackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("黑名单列表");
    }

    public void initSub() {
        // ListView下拉刷新监听
        blackList = new ArrayList<BlackListData>();
        adapter = new BlackListAdapter(BlacklistActivity.this, blackList);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(BlacklistActivity.this)) {
            BlackTask task = new BlackTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private class BlackTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetBlackService getBlack = new GetBlackService(BlacklistActivity.this, page, SIZE);
            String json = getBlack.sending();
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
                        blackList.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = blackList.size();
                        getBlackArray(blackList, data);
                        int preSize = blackList.size();
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
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(BlacklistActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(BlacklistActivity.this);
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

        public void getBlackArray(ArrayList<BlackListData> myweiboArray, String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    for (int i = 0; i < hallArray.length(); i++) {
                        BlackListData blackdata = new BlackListData();
                        JSONObject jo = hallArray.getJSONObject(i);
                        blackdata.setName(jo.getString("nickname"));
                        blackdata.setAvatar(jo.getString("email"));
                        blackdata.setGender(jo.getString("gender"));
                        blackdata.setId(jo.getString("user_id"));
                        blackdata.setSignature(jo.getString("signature"));
                        blackdata.setBitmap(null);
                        myweiboArray.add(blackdata);
                    }
                    adapter.notifyDataSetChanged();
                    page++;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void showFail(String failInf) {
        if (blackList.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public void showNoData() {
        if (blackList.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    class BlackListAdapter
        extends BaseAdapter {
        private ArrayList<BlackListData> blacklistArray;
        private Context context;

        public BlackListAdapter(Context context, ArrayList<BlackListData> blacklist) {
            super();
            this.context = context;
            this.blacklistArray = blacklist;
        }

        private class ViewHolder {
            private TextView niceName;
            private TextView qianming;
            private ImageView avatar;
            private ImageView genderPic;
            private Button deleteBlack;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final BlackListData blackData = blacklistArray.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.black_item, null);
                viewHolder = new ViewHolder();
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.qianming = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
                viewHolder.genderPic = (ImageView) convertView.findViewById(R.id.genderPic);
                viewHolder.deleteBlack = (Button) convertView.findViewById(R.id.deleteBlack);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (blackData.getBitmap() != null) {

                viewHolder.avatar.setImageBitmap(blackData.getBitmap());
            }
            else {
                viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("data_type", 5);
                    b.putInt("userId", Integer.valueOf(blackData.getId()));
                    intent.putExtras(b);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(BlacklistActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                               R.anim.push_to_left_out);
                    }
                }
            });

            viewHolder.deleteBlack.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDeleteBlack(position);
                }
            });

            String name = blackData.getName();
            if (name == null) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                if (blackData.getName().equals("null") || blackData.getName().equals("")) {
                    viewHolder.niceName.setText("匿名彩友");
                }
                else {
                    viewHolder.niceName.setText(blackData.getName());
                }
            }

            String qm = blackData.getSignature();
            if (qm == null || qm.equals("null")) {
                viewHolder.qianming.setText("个性签名:" + "");
            }
            else {
                viewHolder.qianming.setText("个性签名：" + blackData.getSignature());
            }

            String gender = blackData.getGender();
            if (gender.equals("1")) {
                viewHolder.genderPic.setImageResource(R.drawable.icon_male);
            }
            else {
                viewHolder.genderPic.setImageResource(R.drawable.icon_female);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return blacklistArray.size();
        }

        @Override
        public Object getItem(int position) {
            return blacklistArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    protected void showDeleteBlack(final int position) {
        CustomDialog dlgDeleteBlack = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setWarning().setMessage("确定将该用户移除黑名单吗？").setPositiveButton("确  定",
                                                                                 new DialogInterface.OnClickListener() {
                                                                                     public void onClick(DialogInterface dialog,
                                                                                                         int which) {
                                                                                         if (HttpConnectUtil.isNetworkAvailable(BlacklistActivity.this)) {
                                                                                             DeleteBlackListTask task =
                                                                                                 new DeleteBlackListTask(
                                                                                                                         position);
                                                                                             task.execute();
                                                                                             dialog.dismiss();
                                                                                         }
                                                                                         else {
                                                                                             ViewUtil.showTipsToast(BlacklistActivity.this,
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
        dlgDeleteBlack = customBuilder.create();
        dlgDeleteBlack.show();
    }

    private class DeleteBlackListTask
        extends AsyncTask<Void, Void, String> {
        private BlackListData blackData;

        public DeleteBlackListTask(int position) {
            this.blackData = blackList.get(position);
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(BlacklistActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "5");
            parameter.put("user_id", blackData.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BlacklistActivity.this);
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
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(BlacklistActivity.this, "移除黑名单成功");
                    // 刷新列表
                    blackList.clear();
                    page = 1;
                    BlackTask task = new BlackTask();
                    task.execute();
                }
                else {
                    ViewUtil.showTipsToast(BlacklistActivity.this, "移除黑名单失败");
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
        map.put("inf", "username [" + appState.getUsername() + "]: open garden black list");
        String eventName = "v2 open garden black list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_black_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

}
