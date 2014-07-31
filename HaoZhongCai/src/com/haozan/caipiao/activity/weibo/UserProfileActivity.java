package com.haozan.caipiao.activity.weibo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyProfileData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class UserProfileActivity
    extends BasicActivity
    implements OnClickListener {
    private TextView titleTextView;
    private Button addFoucs;
    private Button deleteFoucs;

    private int weiboCt;
    private int fansCt;
    private int foucsCt;

    private TextView niceName;
    private TextView city;
    private TextView content;
    private ImageView genderPic;
// private ImageView avatar;

    private TextView fansCount;
    private TextView weiboCount;
    private TextView foucsCount;
    private RelativeLayout myweiboItem;
    private RelativeLayout myfocusItem;
    private RelativeLayout myfansItem;

    private ProgressBar bar;
    MyProfileData profileArray;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_profile);
        setupViews();
        init();
    }

    private void setupViews() {
        titleTextView = (TextView) findViewById(R.id.initName);
        addFoucs = (Button) findViewById(R.id.addFoucs);
        addFoucs.setOnClickListener(this);
        deleteFoucs = (Button) findViewById(R.id.deleteFoucs);
        deleteFoucs.setOnClickListener(this);
        addFoucs.setEnabled(false);
        deleteFoucs.setEnabled(false);

        titleTextView.setText("用户网络资料");

        niceName = (TextView) findViewById(R.id.niceName);
        city = (TextView) findViewById(R.id.citytext);
        genderPic = (ImageView) findViewById(R.id.genderPic);
// avatar = (ImageView) findViewById(R.id.ivItemPortrait);
        content = (TextView) findViewById(R.id.qianmingtext);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        fansCount = (TextView) findViewById(R.id.fansCount);
        weiboCount = (TextView) findViewById(R.id.weiboCount);
        foucsCount = (TextView) findViewById(R.id.foucsCount);
        myfansItem = (RelativeLayout) findViewById(R.id.fans_layout);
        myfansItem.setOnClickListener(this);
        myweiboItem = (RelativeLayout) findViewById(R.id.weibo_layout);
        myweiboItem.setOnClickListener(this);
        myfocusItem = (RelativeLayout) findViewById(R.id.foucs_layout);
        myfocusItem.setOnClickListener(this);
    }

    private void init() {
        // 接收传过来的用户id
        userId = (int) this.getIntent().getExtras().getInt("userId");

        if (HttpConnectUtil.isNetworkAvailable(UserProfileActivity.this)) {
            UserProfileTask task = new UserProfileTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "刷新").setIcon(R.drawable.icon_refresh);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                bar.setVisibility(View.VISIBLE);
                init();
                return true;
        }
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addFoucs) {
            if (HttpConnectUtil.isNetworkAvailable(UserProfileActivity.this)) {
                bar.setVisibility(View.VISIBLE);
                AddFoucsTask task = new AddFoucsTask();
                task.execute();
                setButtonClickable(false);
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.deleteFoucs) {
            if (HttpConnectUtil.isNetworkAvailable(UserProfileActivity.this)) {
                bar.setVisibility(View.VISIBLE);
                DeleteFoucsTask task1 = new DeleteFoucsTask();
                task1.execute();
                setButtonClickable(false);
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.weibo_layout) {
            if ((weiboCt == 0)) {
                ViewUtil.showTipsToast(this, "暂无动态");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(UserProfileActivity.this, UserWeiboList.class);
                Bundle b = new Bundle();
                b.putInt("userId", userId);
                intent.putExtras(b);
                startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(UserProfileActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                             R.anim.push_to_right_out);
                }
            }
        }
        else if (v.getId() == R.id.fans_layout) {
            if (fansCt == 0) {
                ViewUtil.showTipsToast(this, "暂无粉丝");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(UserProfileActivity.this, UserFansList.class);
                Bundle b = new Bundle();
                b.putInt("userId", userId);
                intent.putExtras(b);
                startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(UserProfileActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                             R.anim.push_to_right_out);
                }
            }
        }
        else if (v.getId() == R.id.foucs_layout) {
            if ((foucsCt == 0)) {
                ViewUtil.showTipsToast(this, "暂无关注");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(UserProfileActivity.this, UserFocusList.class);
                Bundle b = new Bundle();
                b.putInt("userId", userId);
                intent.putExtras(b);
                startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(UserProfileActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                             R.anim.push_to_right_out);
                }
            }
        }

    }

    class AddFoucsTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(UserProfileActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "1");
            parameter.put("user_id", "" + userId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserProfileActivity.this);
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
                ViewUtil.showTipsToast(UserProfileActivity.this, "关注失败");
            }
            else {
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    String errordesc = analyse.getData(json, "error_desc");
                    if (status.equals("200")) {
                        ViewUtil.showTipsToast(UserProfileActivity.this, "关注成功");

                        int fans = fansCt + 1;
                        fansCount.setText("粉丝          " + fans);
                        fansCt++;
                        bar.setVisibility(View.GONE);
                        addFoucs.setVisibility(View.GONE);
                        deleteFoucs.setVisibility(View.VISIBLE);
                    }
                    else if (status.equals("300")) {
                        ViewUtil.showTipsToast(UserProfileActivity.this, errordesc);
                        bar.setVisibility(View.GONE);
                    }
                    else if (status.equals("302")) {
                        OperateInfUtils.clearSessionId(UserProfileActivity.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                    }
                    else if (status.equals("304")) {
                        OperateInfUtils.clearSessionId(UserProfileActivity.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_again));
                    }
                    else {
                        ViewUtil.showTipsToast(UserProfileActivity.this, "关注失败");
                    }
                }
            }
            setButtonClickable(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class DeleteFoucsTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(UserProfileActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("user_id", "" + userId);
            parameter.put("type", "2");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserProfileActivity.this);
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
                    ViewUtil.showTipsToast(UserProfileActivity.this, "取消成功");

                    int fans = fansCt - 1;
                    fansCount.setText("粉丝          " + fans);
                    fansCt--;

                    bar.setVisibility(View.GONE);
                    addFoucs.setVisibility(View.VISIBLE);
                    deleteFoucs.setVisibility(View.GONE);
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(UserProfileActivity.this, "该用户已将你拉入黑名单，请刷新后操作");
                    bar.setVisibility(View.GONE);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(UserProfileActivity.this, "取消失败");
                }
            }
            setButtonClickable(true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void setButtonClickable(boolean isClick) {
        if (isClick) {
            addFoucs.setClickable(true);
            deleteFoucs.setClickable(true);
        }
        else {
            addFoucs.setClickable(false);
            deleteFoucs.setClickable(false);
        }
    }

    class UserProfileTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004010");
            parameter.put("pid", LotteryUtils.getPid(UserProfileActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("user_id", "" + userId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserProfileActivity.this);
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
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    bar.setVisibility(View.GONE);
                    addFoucs.setEnabled(true);
                    deleteFoucs.setEnabled(true);
                    if (data.equals("[]") == false) {
                        getMyProfileArray(profileArray, data);
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    public void getMyProfileArray(MyProfileData profileArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                JSONObject jo = hallArray.getJSONObject(0);
                niceName.setText(jo.getString("nickname"));
                String gender1 = jo.getString("gender");
                if (gender1.equals("1")) {
                    genderPic.setImageResource(R.drawable.icon_male);
                }
                else {
                    genderPic.setImageResource(R.drawable.icon_female);
                }

                String chengshi = jo.getString("city");
                if (chengshi == null || chengshi.equals("null") || chengshi.equals("")) {
                    city.setText("城市：" + "未知");
                }
                else {
                    city.setText("城市：" + jo.getString("city"));
                }

                String qm = jo.getString("signature");
                if (qm == null || qm.equals("null")) {
                    content.setText("个性签名：" + " ");
                }
                else {
                    content.setText("个性签名：" + jo.getString("signature"));
                }

                int fanscount = jo.getInt("fans_count");
                if (fanscount < 0) {
                    fansCount.setText("粉丝          " + 0);
                }
                else {
                    fansCount.setText("粉丝          " + jo.getString("fans_count"));
                }

                int foucscount = jo.getInt("follows_count");
                if (foucscount < 0) {
                    foucsCount.setText("关注          " + 0);
                }
                else {
                    foucsCount.setText("关注          " + jo.getString("follows_count"));
                }

                int weibocount = jo.getInt("weibo_count");
                if (weibocount < 0) {
                    weiboCount.setText("动态         " + 0);
                }
                else {
                    weiboCount.setText("动态          " + jo.getString("weibo_count"));
                }

                weiboCt = jo.getInt("weibo_count");
                fansCt = jo.getInt("fans_count");
                foucsCt = jo.getInt("follows_count");

                String isfollow = jo.getString("isfollow");
                if (isfollow.equals("0")) {
                    addFoucs.setVisibility(View.VISIBLE);
                    deleteFoucs.setVisibility(View.GONE);
                }
                else if (isfollow.equals("1")) {
                    addFoucs.setVisibility(View.GONE);
                    deleteFoucs.setVisibility(View.VISIBLE);
                }
                else {

                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden user profile");
        String eventName = "v2 open garden user profile";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_user_profile";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UserProfileActivity.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(UserProfileActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                         R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
