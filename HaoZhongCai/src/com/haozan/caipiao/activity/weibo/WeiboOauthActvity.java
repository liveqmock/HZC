package com.haozan.caipiao.activity.weibo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Window;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.AccessInfoHelper;
import com.haozan.caipiao.util.weiboutil.InfoHelper;
import com.haozan.caipiao.util.weiboutil.StringUtils;
import com.haozan.caipiao.util.weiboutil.WeiboBaseActivity;
import com.umeng.analytics.MobclickAgent;

public class WeiboOauthActvity
    extends BasicActivity {
    private LotteryApp accessInfo = null;
    private WeiboBaseActivity weiboBaseActivity;

    private CommonsHttpOAuthConsumer httpOauthConsumer;
    private OAuthProvider httpOauthprovider;
    private final static String callBackUrl = "myapp://WeiboOauthActvity";

    public static WeiboOauthActvity webInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
// MobclickAgent.onError(this);
        setContentView(R.layout.splash);
        weiboBaseActivity = new WeiboBaseActivity();
        webInstance = this;
        accessInfo = InfoHelper.getAccessInfo(mContext);

        httpOauthConsumer =
            new CommonsHttpOAuthConsumer(getString(R.string.app_sina_consumer_key),
                                         getString(R.string.app_sina_consumer_secret));

        httpOauthprovider =
            new DefaultOAuthProvider("http://api.t.sina.com.cn/oauth/request_token",
                                     "http://api.t.sina.com.cn/oauth/access_token",
                                     "http://api.t.sina.com.cn/oauth/authorize");
    }

    @Override
    protected void onResume() {
        super.onResume();
// MobclickAgent.onResume(this);

        // 之前登陆过
// if (accessInfo != null) {
        if (LotteryUtils.isBindSina(this)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            // 把accessToken、与accessSecret存到本地数据库
            databaseData.putString("accessToken", accessInfo.getAccessToken());
            databaseData.putString("accessSecret", accessInfo.getAccessSecret());
            databaseData.commit();
            // /
            bundle.putString("accessToken", accessInfo.getAccessToken());
            bundle.putString("accessSecret", accessInfo.getAccessSecret());
            intent.putExtras(bundle);
            intent.setClass(WeiboOauthActvity.this, NewWeiboActivity.class);
            startActivity(intent);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboOauthActvity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                       R.anim.push_to_right_out);
            }
            finish();
        }
        else {
            startOAuthView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
// MobclickAgent.onPause(this);
    }

    private void startOAuthView() {
        try {
            String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, callBackUrl);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", authUrl);
            intent.putExtras(bundle);
            intent.setClass(mContext, WebViewActivity.class);
            startActivity(intent);
        }
        catch (Exception e) {

        }
    }

// 捕捉android.intent.action.SEND，并得到捕捉到的图片路径
    private String getImgPathByCaptureSendFilter() {
        String thisLarge = "";
        Uri mUri = null;
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (!StringUtils.isBlank(action) && "android.intent.action.SEND".equals(action)) {
            boolean hasExtra = intent.hasExtra("android.intent.extra.STREAM");
            if (hasExtra) {
                mUri = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
            }

            if (mUri != null) {
                String mUriString = mUri.toString();
                mUriString = Uri.decode(mUriString);

                String pre1 = "file://" + weiboBaseActivity.SDCARD + File.separator;
                String pre2 = "file://" + weiboBaseActivity.SDCARD_MNT + File.separator;

                if (mUriString.startsWith(pre1)) {
                    thisLarge =
                        Environment.getExternalStorageDirectory().getPath() + File.separator +
                            mUriString.substring(pre1.length());
                }
                else if (mUriString.startsWith(pre2)) {
                    thisLarge =
                        Environment.getExternalStorageDirectory().getPath() + File.separator +
                            mUriString.substring(pre2.length());
                }
                else {
                    thisLarge = weiboBaseActivity.getAbsoluteImagePath(mUri);
                }
            }
        }
        return thisLarge;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }

        String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

        try {
            httpOauthprovider.setOAuth10a(true);
            httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
        }
        catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        }
        catch (OAuthNotAuthorizedException ex) {
            ex.printStackTrace();
        }
        catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        }
        catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        }

        SortedSet<String> userInfoSet = httpOauthprovider.getResponseParameters().get("user_id");
        if (userInfoSet != null && !userInfoSet.isEmpty()) {
            String userID = userInfoSet.first();
            String accessToken = httpOauthConsumer.getToken();
            String accessSecret = httpOauthConsumer.getTokenSecret();

            LotteryApp accessInfo = new LotteryApp();
            accessInfo.setWeiboUserID(userID);
            accessInfo.setAccessToken(accessToken);
            accessInfo.setAccessSecret(accessSecret);

            AccessInfoHelper accessDBHelper = new AccessInfoHelper(mContext);
            accessDBHelper.open();
            accessDBHelper.create(accessInfo);
            accessDBHelper.close();

            Intent intent2 = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("accessToken", accessInfo.getAccessToken());
            bundle.putString("accessSecret", accessInfo.getAccessSecret());
            // 把accessToken、与accessSecret存到本地数据库
            databaseData.putString("accessToken", accessInfo.getAccessToken());
            databaseData.putString("accessSecret", accessInfo.getAccessSecret());
            databaseData.putString("sinaweibo_userid", userID);
            databaseData.commit();
            // /
// intent2.putExtras(bundle);
// intent2.setClass(WeiboOauthActvity.this, NewWeiboActivity.class);
// startActivity(intent2);

            ViewUtil.showTipsToast(this, "授权成功");

// WebViewActivity.webInstance.finish();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden xinlang oauth");
        String eventName = "v2 open garden oauth";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_xinlang_oauth";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WeiboOauthActvity.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboOauthActvity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                       R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}