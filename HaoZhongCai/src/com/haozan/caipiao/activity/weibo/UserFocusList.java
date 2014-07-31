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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FocusListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class UserFocusList
    extends WeiboGeneral
    implements OnClickListener {
    private final static String FAIL = "查询关注信息失败或信息已删除";

    UserFocusAdapter adapter;
    private ArrayList<FocusListData> subArray;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSubViews();
        initSub();
    }

    public void setupSubViews() {
        titleTextView.setText("关注");
        actualListView.setItemsCanFocus(false);
        actualListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        userId = this.getIntent().getExtras().getInt("userId");
    }

    public void initSub() {
        subArray = new ArrayList<FocusListData>();
        adapter = new UserFocusAdapter(UserFocusList.this, subArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (HttpConnectUtil.isNetworkAvailable(UserFocusList.this)) {
            UserFocusTask getFansTask = new UserFocusTask();
            getFansTask.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private class UserFocusTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004060");
            parameter.put("pid", LotteryUtils.getPid(UserFocusList.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "1");
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + SIZE);
            parameter.put("user_id", "" + userId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserFocusList.this);
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
                        getFocusArray(subArray, data);
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
                    OperateInfUtils.clearSessionId(UserFocusList.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserFocusList.this);
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

        public void getFocusArray(ArrayList<FocusListData> myweiboArray, String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    for (int i = 0; i < hallArray.length(); i++) {
                        FocusListData fansdata = new FocusListData();
                        JSONObject jo = hallArray.getJSONObject(i);
                        fansdata.setName(jo.getString("nickname"));
                        fansdata.setAvatar(jo.getString("email"));
                        fansdata.setGender(jo.getString("gender"));
                        fansdata.setId(jo.getString("user_id"));
                        fansdata.setSignature(jo.getString("signature"));
                        fansdata.setFollows(jo.getString("isfollow"));
                        fansdata.setBitmap(null);
// GetPicTask task = new GetPicTask(fansdata);
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + fansdata.getId() +
// "&service=2004100");
                        myweiboArray.add(fansdata);
                    }
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
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

    class UserFocusAdapter
        extends BaseAdapter {
        private ArrayList<FocusListData> focus;
        private Context context;
        private LayoutInflater inflater;

// private ProgressBar progressBar;

        public final class ViewHolder {
            private TextView niceName;
            private TextView qianming;
            private ImageView genderPic;
            private ImageView avatar;
            private Button deleteFocus;
            private Button addFocus;
        }

        public UserFocusAdapter(Context context, ArrayList<FocusListData> focus) {
            super();
            this.context = context;
            this.focus = focus;
// this.progressBar = progressBar;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final FocusListData fs = focus.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.user_focus_item, null);
                viewHolder = new ViewHolder();
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.qianming = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.genderPic = (ImageView) convertView.findViewById(R.id.genderPic);
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
                viewHolder.deleteFocus = (Button) convertView.findViewById(R.id.deleteFocus);
                viewHolder.addFocus = (Button) convertView.findViewById(R.id.addFocus);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // 头像
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
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(fs.getId()) || appState.getUserid() == fs.getId()) {
                        viewHolder.niceName.setText("我");
                    }
                    else {
                        viewHolder.niceName.setText(fs.getName());
                    }
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

            // 判断是否已关注
            String isfollows = fs.getFollows();
            if (isfollows.equals("0")) {
                viewHolder.addFocus.setVisibility(View.VISIBLE);
                viewHolder.deleteFocus.setVisibility(View.GONE);
            }
            else {
                viewHolder.deleteFocus.setVisibility(View.VISIBLE);
                viewHolder.addFocus.setVisibility(View.GONE);
            }

            // 如果是自己按钮不可点击
            LotteryApp appState = ((LotteryApp) context.getApplicationContext());
            if (appState.getUserid() == fs.getId() || appState.getUserid().equals(fs.getId())) {
                viewHolder.addFocus.setEnabled(false);
            }
            else {
                viewHolder.addFocus.setEnabled(true);
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid().equals(fs.getId()) || appState.getUserid() == fs.getId()) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyProfileActivity.class);
                        context.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(context, UserProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("data_type", 8);
                        b.putInt("userId", Integer.valueOf(fs.getId()));
                        intent.putExtras(b);
                        context.startActivity(intent);
                    }
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(UserFocusList.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                    }
                }
            });

            viewHolder.addFocus.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                    if (appState.getUserid() == fs.getId() || appState.getUserid().equals(fs.getId())) {
                        Toast.makeText(context, "不能关注自己", 2000).show();
                    }
                    else {
                        if (HttpConnectUtil.isNetworkAvailable(context)) {
// progressBar.setVisibility(View.VISIBLE);
                            AddFocusTask delete =
                                new AddFocusTask(fs, viewHolder.deleteFocus, viewHolder.addFocus);
                            delete.execute();
                        }
                        else {
                            Toast.makeText(context, R.string.network_not_avaliable, 2000).show();
                        }
                    }
                }
            });

            viewHolder.deleteFocus.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (HttpConnectUtil.isNetworkAvailable(context)) {
// progressBar.setVisibility(View.VISIBLE);
                        DeleteFocusTask task =
                            new DeleteFocusTask(fs, viewHolder.deleteFocus, viewHolder.addFocus);
                        task.execute();
                    }
                    else {
                        Toast.makeText(context, R.string.network_not_avaliable, 2000).show();
                    }
                }
            });

            return convertView;

        }

        @Override
        public int getCount() {
            return focus.size();
        }

        @Override
        public Object getItem(int position) {
            return focus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class AddFocusTask
            extends AsyncTask<Void, Void, String> {
            private FocusListData fs;
            private Button deleteFocus;
            private Button addFocus;

            public AddFocusTask(FocusListData fs, Button deleteFocus, Button addFocus) {
                this.fs = fs;
                this.deleteFocus = deleteFocus;
                this.addFocus = addFocus;
            }

            private HashMap<String, String> initHashMap()
                throws Exception {
                HashMap<String, String> parameter = new HashMap<String, String>();
                parameter.put("service", "2004050");
                parameter.put("pid", LotteryUtils.getPid(UserFocusList.this));
                parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
                parameter.put("user_id", fs.getId());
                parameter.put("type", "1");
                return parameter;
            }

            @Override
            protected String doInBackground(Void... params) {
                ConnectService connectNet = new ConnectService(UserFocusList.this);
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
                        ViewUtil.showTipsToast(UserFocusList.this, "关注成功");
                        fs.setFollows("1");
// progressBar.setVisibility(View.GONE);
                        addFocus.setVisibility(View.GONE);
                        deleteFocus.setVisibility(View.VISIBLE);
                    }
                    else if (status.equals("300")) {
                        ViewUtil.showTipsToast(UserFocusList.this, "该用户已在你的黑名单,无法关注");
                        addFocus.setVisibility(View.GONE);
                        deleteFocus.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(context, "关注失败", 2000).show();
                    }
                }
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }

        class DeleteFocusTask
            extends AsyncTask<Void, Void, String> {
            private FocusListData fs;
            private Button deleteFocus;
            private Button addFocus;

            public DeleteFocusTask(FocusListData fs, Button deleteFocus, Button addFocus) {
                this.fs = fs;
                this.deleteFocus = deleteFocus;
                this.addFocus = addFocus;
            }

            private HashMap<String, String> initHashMap()
                throws Exception {
                HashMap<String, String> parameter = new HashMap<String, String>();
                parameter.put("service", "2004050");
                parameter.put("pid", LotteryUtils.getPid(UserFocusList.this));
                parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
                parameter.put("type", "2");
                parameter.put("user_id", fs.getId());
                return parameter;
            }

            @Override
            protected String doInBackground(Void... params) {
                ConnectService connectNet = new ConnectService(UserFocusList.this);
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
                        ViewUtil.showTipsToast(UserFocusList.this, "取消关注成功");
                        fs.setFollows("0");
// progressBar.setVisibility(View.GONE);
                        addFocus.setVisibility(View.VISIBLE);
                        deleteFocus.setVisibility(View.GONE);
                    }
                    else {
                        ViewUtil.showTipsToast(UserFocusList.this, "取消关注失败");
                    }
                }
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden user focus list");
        String eventName = "v2 open garden user focus list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_focus_list";
        MobclickAgent.onEvent(this, eventName, "others");
        besttoneEventCommint(eventName);
    }
}
