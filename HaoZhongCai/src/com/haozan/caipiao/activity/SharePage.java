package com.haozan.caipiao.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.bethistory.NormalOrderDetail;
import com.haozan.caipiao.activity.weibo.WeiboSelectFansList;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.weibo.sdk.api.WeiboAPI;
import com.haozan.caipiao.weibo.sdk.api.WeiboUtilAPI;
import com.haozan.caipiao.weibo.sdk.keep.AccessTokenKeeper;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.RequestListener;

public class SharePage
    extends ContainTipsPageBasicActivity
    implements OnClickListener {
    private static final int THUMB_SIZE = 150;
    private static final int SHARE_TYPE_SINA = 1;
    private static final int SHARE_TYPE_WEIXIN = 3;
    private static final int SHARE_TYPE_QQWEIBO = 5;
    private static final String FAIL = "发送失败";
    private String mContent = "";
    private String commer_class;
    public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
// public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
// public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
// public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";

    private int from;
    private int shareType;// 1.新浪微博 3.微信好友 5.腾讯微博
    private String PicPath = "/sdcard/MyScreenshot.png";
    private int MAX_LENGTH = 140;
    int Rest_Length = MAX_LENGTH;
    private Bitmap bitmap;
    private TextView titleName;
    private EditText content;
    private Button listentButton;
    private Button shareButton;
    private String shareText;
    private ImageView imageView;
    private ImageView imageViewBig;
    private ProgressBar progress;
    private ImageView toFans;

    private String token;
    private String expires_in;
    private String selectedNames;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    // qq互联平台
    private Tencent mTencent;
    private static final String SCOPE = "all";
    private static final String CANCEL_QQ = "取消QQ登录";
    private static final String LOGIN_BYQQ_FAILED = "绑定QQ失败,请稍后重试";

    private Bitmap thumbBmp;
    private String text;
    private boolean ifFileExists = true;
    public static Oauth2AccessToken accessToken;
    public RequestListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_page);
        setupViews();
        initViews();
        init();
    }

    private void setupViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            shareText = this.getIntent().getExtras().getString("shareText");
            from = this.getIntent().getExtras().getInt("from");
            shareType = this.getIntent().getExtras().getInt("share_type");
            commer_class = this.getIntent().getExtras().getString("commer_class");
        }
        if (shareType == SHARE_TYPE_SINA) {
            Intent in = this.getIntent();
            mContent = in.getStringExtra(EXTRA_WEIBO_CONTENT);

            token = preferences.getString("token", null);
            expires_in = preferences.getString("expires_in", null);
        }
        titleName = (TextView) findViewById(R.id.initName);
        content = (EditText) findViewById(R.id.sharComentText);
        listentButton = (Button) findViewById(R.id.clear_button);
        imageViewBig = (ImageView) findViewById(R.id.share_pic_big);
        imageView = (ImageView) findViewById(R.id.share_pic);
        imageView.setOnClickListener(this);
        if (from < 0) {
            imageView.setVisibility(View.GONE);
        }
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        shareButton = (Button) findViewById(R.id.title_btinit_right);
        shareButton.setOnClickListener(this);
        toFans = (ImageView) findViewById(R.id.img_fans);
        toFans.setOnClickListener(this);
    }

    private void initViews() {
        if (shareType == SHARE_TYPE_SINA) {
            titleName.setText("分享到新浪微博");
            toFans.setVisibility(View.VISIBLE);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sinaweibo_logo, 0, 0, 0);
        }
        else if (shareType == SHARE_TYPE_WEIXIN) {
            titleName.setText("分享到微信");
            toFans.setVisibility(View.GONE);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weixin_logo, 0, 0, 0);
        }
        else if (shareType == SHARE_TYPE_QQWEIBO) {
            titleName.setText("分享到腾讯微博");
            toFans.setVisibility(View.GONE);
            // TODO
            shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.qqweibo_logo, 0, 0, 0);
        }
    }

    private void init() {
        // QQ互联平台
        mTencent = Tencent.createInstance(LotteryConfig.getQQAPPID(this), SharePage.this);
        content.setText(shareText);
        // sina
        accessToken = AccessTokenKeeper.readAccessToken(this);
        // 监听输入字符
        content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Rest_Length = MAX_LENGTH - content.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Rest_Length > 0) {
                    listentButton.setText("   " + Rest_Length + "   ");
                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Rest_Length > 0) {
                    listentButton.setText("   " + Rest_Length + "   ");
                    shareButton.setEnabled(true);
                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));
                    if (Rest_Length == 0) {
                        shareButton.setEnabled(true);
                    }
                    else if (Rest_Length < 0) {
                        shareButton.setEnabled(false);
                    }
                }
            }

        });
        listentButton.setText("   " + (140 - content.getText().length()) + "   ");
        File file = new File(PicPath);
        if (file.exists()) {
            ifFileExists = true;
// bitmap = BitmapFactory.decodeFile(PicPath);
            // 解决图片内存溢出问题
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(PicPath, options);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else {
            ifFileExists = false;
// ViewUtil.showTipsToast(this,"找不到图片!");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_right) {
            if (shareType == SHARE_TYPE_SINA) {
                if (HttpConnectUtil.isNetworkAvailable(SharePage.this)) {
                    UpdateStatusTask task = new UpdateStatusTask();
                    task.execute();
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
            else if (shareType == SHARE_TYPE_WEIXIN) {
                String text = content.getText().toString();
                if (text == null || text.length() == 0) {
                    return;
                }
                // 通过WXAPIFactory工厂，获取IWXAPI的实例
                api = WXAPIFactory.createWXAPI(this, LotteryConfig.getWeixinAPPID(this), false);
                // 将该app注册到微信
                api.registerApp(LotteryConfig.getWeixinAPPID(this));

                // 初始化一个WXTextObject对象
                WXTextObject textObj = new WXTextObject();
                textObj.text = text;

                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = textObj;
                // 发送文本类型的消息时，title字段不起作用
                // msg.title = "Will be ignored";
                msg.description = text;

                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
                req.message = msg;
// req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline :
// SendMessageToWX.Req.WXSceneSession;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                // 调用api接口发送数据到微信
                api.sendReq(req);
                finish();
                /*
                 * String path = PicPath; File file = new File(path); if (!file.exists()) { String tip =
                 * "图片不存在"; ViewUtil.showTipsToast(SharePage.this, tip); } WXImageObject imgObj = new
                 * WXImageObject(); imgObj.setImagePath(path); WXMediaMessage msg = new WXMediaMessage();
                 * msg.mediaObject = imgObj; Bitmap bmp = BitmapFactory.decodeFile(path); Bitmap thumbBmp =
                 * Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true); bmp.recycle(); msg.thumbData
                 * = ViewUtil.bmpToByteArray(thumbBmp, true); SendMessageToWX.Req req = new
                 * SendMessageToWX.Req(); req.transaction = buildTransaction("img"); req.message = msg; //
                 * req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline :
                 * SendMessageToWX.Req.WXSceneSession; req.scene = SendMessageToWX.Req.WXSceneSession;
                 * api.sendReq(req); finish();
                 */
            }
            else if (shareType == SHARE_TYPE_QQWEIBO) {
                text = content.getText().toString();
                if (text == null || text.length() == 0) {
                    return;
                }
                String path = PicPath;
                File file = new File(path);
                if (!file.exists()) {
                    String tip = "图片不存在";
// ViewUtil.showTipsToast(SharePage.this, tip);
                    thumbBmp = null;
                }
                else {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                }

                // TODO
                String openid = preferences.getString("qq_openid", null);
                String access_token = preferences.getString("qq_access_token", null);
                String expires_in = preferences.getString("qq_expires_in", null);
                mTencent.setOpenId(openid);
                mTencent.setAccessToken(access_token, expires_in);
                if (ready()) {
                    sendQQWeibo(text, thumbBmp);
                }
            }
        }
        else if (v.getId() == R.id.share_pic) {
            ViewUtil.showTipsToast(this, "查看原图");
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PicPath)), "image/*");
            startActivity(intent);
        }
        else if (v.getId() == R.id.img_fans) {
            Intent intent = new Intent();
            intent.setClass(SharePage.this, WeiboSelectFansList.class);
            startActivityForResult(intent, 0);
        }
    }

    private void sendQQWeibo(String text, Bitmap thumbBmp) {
        final Bundle params = new Bundle();
        params.putString("format", "json");
        params.putString("content", text);
        params.putString("oauth_consumer_key", LotteryConfig.getQQAPPID(this));
        params.putString("compatibleflag", "0");
        if (null != thumbBmp) {
            params.putByteArray("pic", ViewUtil.bmpToByteArray(thumbBmp, true));
            mTencent.requestAsync("t/add_pic_t", params, Constants.HTTP_POST,
                                  new BaseApiListener("add_pic_t", false), null);
        }
        else {
            mTencent.requestAsync("t/add_t", params, Constants.HTTP_POST,
                                  new BaseApiListener("add_t", false), null);
        }
    }

    private boolean ready() {
        boolean ready = mTencent.isSessionValid() && mTencent.getOpenId() != null;
        if (!ready) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    analyseAndSaveQQparams(values);
                    ViewUtil.showTipsToast(SharePage.this, "QQ登录成功！");
                    sendQQWeibo(text, thumbBmp);
                }

                private void analyseAndSaveQQparams(JSONObject values) {
                    String openid;
                    String access_token;
                    String expires_in;
                    try {
                        openid = values.getString("openid");
                        access_token = values.getString("access_token");
                        expires_in = values.getString("expires_in");
                        databaseData.putString("qq_openid", openid);
                        databaseData.putString("qq_access_token", access_token);
                        // 计算token失效日期
                        expires_in =
                            String.valueOf(System.currentTimeMillis() + Long.parseLong(expires_in) * 1000);
                        databaseData.putString("qq_expires_in", expires_in);
                        databaseData.commit();
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            mTencent.login(this, SCOPE, listener);
        }
// Toast.makeText(this, "login and get openId first, please!", Toast.LENGTH_SHORT).show();

        return ready;
    }

    private class BaseApiListener
        implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            doComplete(response, state);
            Intent intent = new Intent();
            if (commer_class.equals("About"))
                intent.setClass(SharePage.this, About.class);
            else if (commer_class.equals("NormalOrderDetail"))
                intent.setClass(SharePage.this, NormalOrderDetail.class);
            setResult(RESULT_OK, intent);
            finish();
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(SharePage.this, mScope, new BaseUiListener());
                            }
                        };
                        SharePage.this.runOnUiThread(r);
                    }
                }
                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
                // 如果cgi返回的值没有token, 则会清空原来的token
// String token = response.getString("access_token");
// String expire = response.getString("expires_in");
// String openid = response.getString("openid");
// mTencent.setAccessToken(token, expire);
// mTencent.setOpenId(openid);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e, Object state) {
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
        }

        public void onConnectTimeoutException(final ConnectTimeoutException arg0, Object arg1) {

        }

        @Override
        public void onSocketTimeoutException(final SocketTimeoutException arg0, Object arg1) {
        }

        @Override
        public void onUnknowException(final Exception arg0, Object arg1) {
        }

        @Override
        public void onHttpStatusException(final HttpStatusException arg0, Object arg1) {
        }

        @Override
        public void onNetworkUnavailableException(final NetworkUnavailableException arg0, Object arg1) {
        }
    }

    private class BaseUiListener
        implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            ViewUtil.showTipsToast(SharePage.this, LOGIN_BYQQ_FAILED);
        }

        @Override
        public void onCancel() {
            ViewUtil.showTipsToast(SharePage.this, CANCEL_QQ);
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type +
            System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                selectedNames = bundle.getString("selected_names");
                content.setText(selectedNames + content.getText().toString());
            }
        }
    }

    // 监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (from > 0) {
                if (imageView.isShown() == false) {
                    imageView.setVisibility(View.VISIBLE);
                    imageViewBig.setVisibility(View.GONE);
                    content.setEnabled(true);
                }
                else {
                    finish();
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(SharePage.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                       R.anim.push_to_left_out);
                    }
                }
            }
            else {
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(SharePage.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                   R.anim.push_to_left_out);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class UpdateStatusTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected String doInBackground(Void... params) {
            if (accessToken.isSessionValid()) {
                mContent = content.getText().toString();
                if (from > 0 && ifFileExists == true) {
                    upload(PicPath, mContent, "", "");
                }
                else {
                    // Just update a text weibo!
                    update(mContent, "", "");
                }
            }
            else {
                ViewUtil.showTipsToast(SharePage.this, "请登录");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json) {

        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            listener = new RequestListener() {

                @Override
                public void onIOException(final IOException exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(SharePage.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "IOException:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onError(final com.weibo.sdk.android.WeiboException exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(SharePage.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "WeiboException:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onComplete4binary(final ByteArrayOutputStream exc) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(SharePage.this, FAIL, Toast.LENGTH_LONG).show();
                            Logger.inf("vincent", "ByteArrayOutputStream:" + exc.toString());
                        }
                    });
                }

                @Override
                public void onComplete(String arg0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            Toast.makeText(SharePage.this, "分享成功", Toast.LENGTH_LONG).show();
                            SharePage.this.finish();
                        }
                    });

                }
            };
        }

    }

    // 把Bitmap转换成二进制
    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private String upload(String file, String status, String lon, String lat) {
        WeiboParameters bundle = new WeiboParameters();
// bundle.add("source", source);
        bundle.add("pic", file);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = "https://upload.api.weibo.com/2/statuses/upload.json";
        new WeiboUtilAPI(accessToken).request(url, bundle, WeiboAPI.HTTPMETHOD_POST, listener);

        return rlt;
    }

    private String update(String status, String lon, String lat) {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = WeiboAPI.API_SERVER + "/statuses/update.json";
        new WeiboUtilAPI(accessToken).request(url, bundle, WeiboAPI.HTTPMETHOD_POST, listener);
        return rlt;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open share page");
        String eventName = "open share page";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_share_page";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
