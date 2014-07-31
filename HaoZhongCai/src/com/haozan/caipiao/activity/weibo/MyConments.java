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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.haozan.caipiao.types.MyConmentData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.umeng.analytics.MobclickAgent;

public class MyConments
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询评论信息失败或信息已删除";

    MyConmentsAdapter adapter;
    private ArrayList<MyConmentData> subArray;
    private String weiboId;
    private String userId;
    private String originWeiboId;
    private String nickName;
    private boolean ring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("收到的评论");
        // 注册上下文菜单
        registerForContextMenu(this.pulllistView);
    }

    public void initSub() {
        subArray = new ArrayList<MyConmentData>();
        adapter = new MyConmentsAdapter(MyConments.this, subArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(MyConments.this)) {
            super.loadData();
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
            parameter.put("pid", LotteryUtils.getPid(MyConments.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("weibo_id", weiboId);
            parameter.put("type", "6");
            parameter.put("page_no", "" + page);
            parameter.put("size", "" + SIZE);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyConments.this);
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
                        subArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = subArray.size();
                        getMyConmentArray(subArray, data);
                        int preSize = subArray.size();
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
                    OperateInfUtils.clearSessionId(MyConments.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyConments.this);
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

    // Json解析
    public void getMyConmentArray(ArrayList<MyConmentData> myconmentArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    MyConmentData data = new MyConmentData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    data.setUserId(jo.getString("user_id"));
                    data.setWeiboId(jo.getString("weibo_id"));
                    data.setOriginWeiboId(jo.getString("origin_wid"));
                    data.setUserName(jo.getString("nickname"));
                    data.setContent(jo.getString("content"));
                    data.setSrcContent(jo.getString("origin"));
                    data.setTime(jo.getString("issue_date"));
// data.setSource(jo.getString("source"));
                    data.setBitmap(null);
                    // GetPicTask task = new GetPicTask(data);
                    // task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?"
                    // +
                    // "&phone=" + appState.getUsername() + "&user_id=" +
                    // data.getUserid() +
                    // "&service=2004100");
                    myconmentArray.add(data);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public void showFail(String failInf) {
        if (subArray.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public class MyConmentsAdapter
        extends BaseAdapter {
        private ArrayList<MyConmentData> condata;
        private Context context;

        public final class ViewHolder {
            private TextView userName;
            private TextView time;
            private TextView content;
            private TextView srcContent;
            private ImageView avatar;
        }

        public MyConmentsAdapter(Context context, ArrayList<MyConmentData> condata) {
            super();
            this.context = context;
            this.condata = condata;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            MyConmentData cm = condata.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.my_conments_item, null);

                viewHolder.userName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tvItemDate);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.srcContent = (TextView) convertView.findViewById(R.id.src_content);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (cm.getBitmap() != null) {
                viewHolder.avatar.setImageBitmap(cm.getBitmap());
            }
            else {
                viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
            }

            String name = cm.getUserName();
            if (name == null) {
                viewHolder.userName.setText("匿名彩友");
            }
            else {
                if (cm.getUserName().equals("null") || cm.getUserName().equals("")) {
                    viewHolder.userName.setText("匿名彩友");
                }
                else {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
// if (appState.getUserid().equals(cm.getUserId()) || appState.getUserid() == cm.getUserId()) {
// viewHolder.userName.setText("我");
// }
// else {
                    viewHolder.userName.setText(cm.getUserName());
// }
                }
            }
            final String weiboId = cm.getWeiboId();
            final String userId = cm.getUserId();
            final String originWeiboId = cm.getOriginWeiboId();
            final String name1 = cm.getUserName();
            viewHolder.time.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(cm.getTime(),
                                                                              "yyyy-MM-dd HH:mm:ss")));
            viewHolder.content.setText(TextUtil.formatContent(cm.getContent(), context));
            viewHolder.srcContent.setText(TextUtil.formatContent("回复我的动态：" + "“" + cm.getSrcContent() + "”" +
                "\n", context));

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openContextMenu(v, weiboId, userId, originWeiboId, name1);
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return condata.size();
        }

        @Override
        public Object getItem(int position) {
            return condata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    protected void openContextMenu(View v, String weiboId1, String userId1, String originWeiboId2,
                                   String nickName2) {
        weiboId = weiboId1;
        userId = userId1;
        originWeiboId = originWeiboId2;
        nickName = nickName2;
        openContextMenu(v);
    }

    public boolean onContextItemSelected(MenuItem item) {
// AdapterContextMenuInfo lm = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1://
                Intent intent = new Intent();
                intent.setClass(MyConments.this, NewConmentsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("weiboId", originWeiboId);
                bundle.putString("nickName", nickName);
                intent.putExtras(bundle);
                MyConments.this.startActivity(intent);
                break;
            case 2://
                if (appState.getUserid() == userId || appState.getUserid().equals(userId)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(MyConments.this, MyProfileActivity.class);
                    MyConments.this.startActivity(intent1);
                }
                else {
                    Intent intent1 = new Intent();
                    intent1.setClass(MyConments.this, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("userId", Integer.valueOf(userId));
                    intent1.putExtras(b);
                    MyConments.this.startActivity(intent1);
                }
                break;
            case 3://
                Intent intent1 = new Intent();
                intent1.setClass(MyConments.this, WeiboZhengWen.class);
                Bundle b = new Bundle();
                b.putString("weiboId", originWeiboId);
                intent1.putExtras(b);
                MyConments.this.startActivity(intent1);
                break;
        }
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            (new AnimationModel(MyConments.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                            R.anim.push_to_right_out);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
// AdapterContextMenuInfo lm = (AdapterContextMenuInfo) menuInfo;
// if (lm.id != 0 && lm.id != -1) {
        menu.setHeaderTitle("功能");
        menu.add(1, 1, 1, "回复评论");
        menu.add(1, 2, 2, "查看用户资料");
        menu.add(1, 3, 3, "查看原动态");
    }

// }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden my comments list");
        String eventName = "v2 open garden my comments list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_my_comments_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
