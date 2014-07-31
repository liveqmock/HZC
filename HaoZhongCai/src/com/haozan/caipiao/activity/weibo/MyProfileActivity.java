package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.connect.GetMyProfileService;
import com.haozan.caipiao.jgravatar.Gravatar;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyProfileData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class MyProfileActivity
    extends BasicActivity
    implements OnClickListener {
    private static final Uri PROFILE_URI = Uri.parse(BasicWeibo.SCHEMANAME);
    private String uid;
    private RelativeLayout myweiboItem;
    private RelativeLayout myfansItem;
    private RelativeLayout myfocusItem;
    private RelativeLayout blacklist;
    private RelativeLayout myShouCang;
    private RelativeLayout myConment;
    private RelativeLayout myForward;

    private String weiboCt;
    private int fansCt;
    private String foucsCt;
    private String favoriteCt;
    private String blaclistkCt;
    private String conmensCt;
    private String forwardCt;

    private Button update;
    private Button weiboHallBt;
    private TextView newWeibo;
    private TextView titleTextView;

    private TextView niceName;
    private TextView city;
    private TextView content;
    private ImageView genderPic;

    private TextView weiboCount;
    private TextView fansCount;
    private TextView foucsCount;
    private TextView favoriteCount;
    private TextView blacklistCount;
//    private TextView conmensCount;
    private TextView forwardCount;

    private TextView fansNewNum;
    private TextView conmentNewNum;
    private TextView forwardNewNum;

    private ProgressBar progressBar;
    private Bitmap bitmap;
    private ImageView avatar;
    MyProfileData profileArray;

    private int profileNum;
    private String newFansNum;
    private String newConmentNum;
    private String newRetweetNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        setupViews();
        init();
    }

    @Override
    protected void onResume() {
        if (HttpConnectUtil.isNetworkAvailable(MyProfileActivity.this)) {
            init();
        }
        else {
            progressBar.setVisibility(View.GONE);
            ViewUtil.showTipsToast(this, noNetTips);
        }
        super.onResume();
    }

    private void setupViews() {
        myweiboItem = (RelativeLayout) findViewById(R.id.weibo_layout);
        myfansItem = (RelativeLayout) findViewById(R.id.fans_layout);
        myfocusItem = (RelativeLayout) findViewById(R.id.focus_layout);
        blacklist = (RelativeLayout) findViewById(R.id.black_layout);
        myShouCang = (RelativeLayout) findViewById(R.id.shouCang_layout);
        myConment = (RelativeLayout) findViewById(R.id.conment_layout);
        myForward = (RelativeLayout) findViewById(R.id.forward_layout);
        myweiboItem.setClickable(false);
        myfansItem.setClickable(false);
        myfocusItem.setClickable(false);
        blacklist.setClickable(false);
        myShouCang.setClickable(false);
        myConment.setClickable(false);
        myForward.setClickable(false);

        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        titleTextView.setText("网络资料");

        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);

        weiboHallBt = (Button) findViewById(R.id.title_btinit_right);
        weiboHallBt.setOnClickListener(this);
        weiboHallBt.setText("财园大厅");

        newWeibo = (TextView) findViewById(R.id.title_btinit_left);
        newWeibo.setOnClickListener(this);

        genderPic = (ImageView) findViewById(R.id.genderPic);
        content = (TextView) findViewById(R.id.qianmingtext);
        niceName = (TextView) findViewById(R.id.niceName);
        city = (TextView) findViewById(R.id.citytext);
        avatar = (ImageView) findViewById(R.id.ivItemPortrait);
        weiboCount = (TextView) findViewById(R.id.weiboCount);
        fansCount = (TextView) findViewById(R.id.fansCount);
        foucsCount = (TextView) findViewById(R.id.foucsCount);
        favoriteCount = (TextView) findViewById(R.id.favoriteCount);
        blacklistCount = (TextView) findViewById(R.id.blacklistCount);
//        conmensCount = (TextView) findViewById(R.id.conmentCount);
        forwardCount = (TextView) findViewById(R.id.forwardCount);
        fansNewNum = (TextView) findViewById(R.id.fans_num);
        conmentNewNum = (TextView) findViewById(R.id.conment_num);
        forwardNewNum = (TextView) findViewById(R.id.forward_num);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    // 点击昵称跳转获取到用户昵称
    private void extractUidFromUri() {
        Uri uri = getIntent().getData();
        if (uri != null && PROFILE_URI.getScheme().equals(uri.getScheme())) {
            uid = uri.getQueryParameter(BasicWeibo.PARAM_UID);
            ViewUtil.showTipsToast(this, uid);
        }
    }

    public void init() {
        if (HttpConnectUtil.isNetworkAvailable(MyProfileActivity.this)) {
            MyProfileTask task = new MyProfileTask();
            task.execute();
        }
        else {
            progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.VISIBLE);
                init();
                return true;
        }
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(this, WeiboHallActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.title_btinit_left) {
            Intent intent = new Intent();
            intent.setClass(this, NewWeiboActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.update) {
            Intent intent = new Intent();
            intent.setClass(MyProfileActivity.this, UpdateMyProfile.class);
            MyProfileActivity.this.startActivity(intent);
        }
        else if (v.getId() == R.id.forward_layout) {
            if (forwardCt.equals("0")) {
// ViewUtil.showTipsToast(this,"暂无转发");
                ViewUtil.showTipsToast(this, "近期开放，敬请关注");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, MyForward.class);
                MyProfileActivity.this.startActivity(intent);

                // changed by vincent
                // 点击后相应 项数设为0
                profileNum = Integer.valueOf(newFansNum) + Integer.valueOf(newConmentNum) + 0;
                appState.setAllProfileCount(profileNum);

                forwardNewNum.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.conment_layout) {
            if (conmensCt.equals("0")) {
                ViewUtil.showTipsToast(this, "暂无收到评论");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, MyComments.class);
                MyProfileActivity.this.startActivity(intent);

                // changed by vincent
                profileNum = Integer.valueOf(newFansNum) + 0 + Integer.valueOf(newRetweetNum);
                appState.setAllProfileCount(profileNum);

                conmentNewNum.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.shouCang_layout) {
            if (favoriteCt.equals("0")) {
                ViewUtil.showTipsToast(this, "暂无收藏");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, Favorite.class);
                MyProfileActivity.this.startActivity(intent);
            }
        }
        else if (v.getId() == R.id.black_layout) {
            if (blaclistkCt.equals("0")) {
                ViewUtil.showTipsToast(this, "暂未添加黑名单用户");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, BlacklistActivity.class);
                MyProfileActivity.this.startActivity(intent);
            }
        }
        else if (v.getId() == R.id.focus_layout) {
            if (foucsCt.equals("0")) {
                ViewUtil.showTipsToast(this, "暂未添加关注用户");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, FocusList.class);
                MyProfileActivity.this.startActivity(intent);
            }
        }
        else if (v.getId() == R.id.fans_layout) {
            if (fansCt == 0) {
                ViewUtil.showTipsToast(this, "暂无粉丝");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, FansList.class);
                MyProfileActivity.this.startActivity(intent);
// //////////////////
// databaseData.putInt("fans_last_num", fansCt);
// databaseData.putString("phone", appState.getUsername());
// databaseData.commit();
                // //////////////////////

                // changed by vincent
                profileNum = 0 + Integer.valueOf(newConmentNum) + Integer.valueOf(newRetweetNum);
                appState.setAllProfileCount(profileNum);
                fansNewNum.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.weibo_layout) {
            if (weiboCt.equals("0")) {
                ViewUtil.showTipsToast(this, "暂未发表动态");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(MyProfileActivity.this, MyWeiboActivity.class);
                MyProfileActivity.this.startActivity(intent);
            }
        }

    }

    private class GetMyProfileNum
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004150");
            parameter.put("pid", LotteryUtils.getPid(MyProfileActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(MyProfileActivity.this);
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
            if (json == null) {
                ViewUtil.showTipsToast(MyProfileActivity.this, "获取数据失败");
            }
            else {
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String response_data = analyse.getData(json, "response_data");
                    newFansNum = analyse.getData(response_data, "new_fans");
                    newConmentNum = analyse.getData(response_data, "new_comment");
                    newRetweetNum = analyse.getData(response_data, "new_retweet");
                    profileNum =
                        Integer.valueOf(newFansNum) + Integer.valueOf(newConmentNum) +
                            Integer.valueOf(newRetweetNum);
                    appState.setAllProfileCount(profileNum);
                    OperateInfUtils.broadcast(MyProfileActivity.this, "myProfile_num");

                    // 新增粉丝数
                    if (Integer.valueOf(newFansNum) > 0) {
                        fansNewNum.setVisibility(View.VISIBLE);
                        if (Integer.valueOf(newFansNum) > 99) {
                            fansNewNum.setText("99+");
                        }
                        else {
                            fansNewNum.setText(String.valueOf(newFansNum));
                        }
                    }
                    else {
                        fansNewNum.setVisibility(View.GONE);
                    }
                    // 新增评论数
                    if (Integer.valueOf(newConmentNum) > 0) {
                        conmentNewNum.setVisibility(View.VISIBLE);
                        if (Integer.valueOf(newConmentNum) > 99) {
                            conmentNewNum.setText("99+");
                        }
                        else {
                            conmentNewNum.setText(String.valueOf(newConmentNum));
                        }
                    }
                    else {
                        conmentNewNum.setVisibility(View.GONE);
                    }
                    // 新增转发数
                    if (Integer.valueOf(newRetweetNum) > 0) {
                        forwardNewNum.setVisibility(View.VISIBLE);
                        if (Integer.valueOf(newRetweetNum) > 99) {
                            forwardNewNum.setText("99+");
                        }
                        else {
                            forwardNewNum.setText(String.valueOf(newRetweetNum));
                        }
                        forwardNewNum.setText(String.valueOf(newRetweetNum));
                    }
                    else {
                        forwardNewNum.setVisibility(View.GONE);
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(MyProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(MyProfileActivity.this, "获取用户消息数失败");
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class MyProfileTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetMyProfileService getMyProfile = new GetMyProfileService(MyProfileActivity.this);
            String json = getMyProfile.sending();
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    progressBar.setVisibility(View.GONE);
                    if (data.equals("[]") == false) {
                        getMyProfileArray(profileArray, data);
                        myweiboItem.setClickable(true);
                        myfansItem.setClickable(true);
                        myfocusItem.setClickable(true);
                        blacklist.setClickable(true);
                        myShouCang.setClickable(true);
                        myConment.setClickable(true);
                        myForward.setClickable(true);
                        myweiboItem.setOnClickListener(MyProfileActivity.this);
                        myfansItem.setOnClickListener(MyProfileActivity.this);
                        myfocusItem.setOnClickListener(MyProfileActivity.this);
                        blacklist.setOnClickListener(MyProfileActivity.this);
                        myShouCang.setOnClickListener(MyProfileActivity.this);
                        myConment.setOnClickListener(MyProfileActivity.this);
                        myForward.setOnClickListener(MyProfileActivity.this);

                        // changed by vincent
                        GetMyProfileNum task = new GetMyProfileNum();
                        task.execute();
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(MyProfileActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(MyProfileActivity.this);
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

// GetPicTask task = new GetPicTask();
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + jo.getString("user_id") +
// "&service=2004100");
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
                fansCount.setText(jo.getString("fans_count") + "\n" + "粉丝");
                foucsCount.setText(jo.getString("follows_count") + "\n" + "关注");
                weiboCount.setText(jo.getString("weibo_count") + "\n" + "动态");
                favoriteCount.setText(jo.getString("favorite_count") + "\n" + "收藏");
//                conmensCount.setText("收到的评论      " + jo.getString("reply_count"));
                forwardCount.setText(" @我的        ");
                blacklistCount.setText("黑名单      " + jo.getString("blacklist_count"));
                weiboCt = jo.getString("weibo_count");
                fansCt = Integer.valueOf(jo.getString("fans_count"));
                foucsCt = jo.getString("follows_count");
                favoriteCt = jo.getString("favorite_count");
                blaclistkCt = jo.getString("blacklist_count");
                conmensCt = jo.getString("reply_count");
                forwardCt = "0";

                // 粉丝新增数
// int fansLastNum = preferences.getInt("fans_last_num", -1);
// String phone = preferences.getString("phone", "0");
// int fansNewnum = fansCt - fansLastNum;
// if (fansNewnum > 0 && fansLastNum != -1 && phone.equals(appState.getUsername())) {
// fansNewNum.setVisibility(View.VISIBLE);
// fansNewNum.setText("+" + fansNewnum);
// }
// if (fansCt < fansLastNum || !phone.equals(appState.getUsername())) {
// databaseData.putInt("fans_last_num", fansCt);
// databaseData.putString("phone", appState.getUsername());
// databaseData.commit();
// }
// if (fansCt == 0) {
// fansNewNum.setVisibility(View.GONE);
// }
                // ///
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取图片
    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(MyProfileActivity.this);
        HttpClient httpClient = client.getDefaultHttpClient();
        HttpPost httpPost = client.getHttpPost(url, parameter);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    entity.consumeContent();
                }
            }
        }
        catch (IOException ex) {
            httpPost.abort();
        }
        if (inputStream != null) {
            return inputStream;
        }
        else
            return null;
    }

    // 获取图片线程
    class GetPicTask
        extends AsyncTask<String, Long, byte[]> {

        @Override
        protected void onPostExecute(byte[] data) {
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                if (bitmap != null) {
                    avatar.setImageBitmap(bitmap);// display image
                }
                else {
                    avatar.setImageResource(R.drawable.lucky_cat);
                }
            }
            else {
                ViewUtil.showTipsToast(MyProfileActivity.this, "头像获取失败");
            }

        }

        // 任务被执行之后
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected byte[] doInBackground(String... params) {
            HttpClient c = new DefaultHttpClient();
            Gravatar g = new Gravatar();
            String url = null;
            try {

                url = params[0];
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            HttpGet get = new HttpGet(url);
            HttpResponse response = null;
            // try {
            try {
                response = c.execute(get);
                HttpEntity client = response.getEntity();
                if (client != null) {
                    InputStream is = null;
                    ByteArrayOutputStream outStream = null;
                    try {
                        is = client.getContent();
                        if (is != null) {
                            outStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                outStream.write(buffer, 0, len);
                            }
                            return outStream.toByteArray();
                        }
                        else
                            return null;
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                        return null;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    finally {
                        if (is != null)
                            is.close();
                        if (outStream != null)
                            outStream.close();
                    }
                }
                else {
                    return null;
                }
            }
            catch (ClientProtocolException e1) {
                e1.printStackTrace();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal profile");
        String eventName = "v2 open garden personal profile";
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
            MyProfileActivity.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
// if (this.getIntent().getExtras() != null) {
// if (this.getIntent().getExtras().getString("about").equals("vertical"))
// (new AnimationModel(UserInf.this)).overridePendingTransition(R.anim.push_down_in,
// R.anim.push_down_out);
// }
// else
                (new AnimationModel(MyProfileActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                       R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
