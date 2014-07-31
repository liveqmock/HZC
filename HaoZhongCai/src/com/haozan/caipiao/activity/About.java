package com.haozan.caipiao.activity;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener.OnFinishSinaAuthListener;
import com.haozan.caipiao.weibo.sdk.demo.ConstantS;
import com.haozan.caipiao.widget.CustomDialog;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;

public class About
    extends BasicActivity
    implements OnFinishSinaAuthListener, OnClickListener, IWeiboHandler.Response {
    private static final int lOADLOCAL = 1;
    private static final int SHARE_TYPE_SINA = 1;
    private static final int SHARE_TYPE_OTHERS = 2;
    private static final int SHARE_TYPE_WEIXIN = 3;
    private static final int SHARE_TYPE_WEIXINFANS = 4;
    private static final int SHARE_TYPE_QQWEIBO = 5;
    public static final String UNINSTALL_WXAPP = "未安装微信";
    public static final String SEND_QQWEIBO_SUCCESS = "发送成功";
    private static final int THUMB_SIZE = 150;

    private int shareWay = 1;
    private Button shareImg;
    private PopupWindow shareWayPopupWindow;
    private String contactStr;
    private TextView contactInf;
    private TextView textAbout;
    private TextView versionInf;
    private TextView newVerNum;
    private TextView updateContent;
    private RelativeLayout update;
    private LinearLayout updateInfo;
    private Button feedback;
    private Button share;
    private ImageView barcode;
    Weibo mWeibo;
    private WebView lotteryAboutInfContent;
    private RelativeLayout relateBottomMenu;
    private boolean isLoaded = false;
    private Bitmap bm;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private CustomDialog dlgCheckBindSina;

    private SsoHandler mSsoHandler;
    public static Oauth2AccessToken accessToken;
    // 新浪微博分享的开放接口
    IWeiboAPI weiboAPI;
    private String PicPath = "/sdcard/MyScreenshot.png";

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case lOADLOCAL:
                    if (!isLoaded) {
                        lotteryAboutInfContent.stopLoading();
                        lotteryAboutInfContent.loadUrl("file:///android_asset/help/lottery_about_page.html");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupViews();
        initData();
        init();
    }

    private void initData() {
        mWeibo =
            Weibo.getInstance(LotteryConfig.getSinaKey(About.this), ConstantS.REDIRECT_URL, ConstantS.SCOPE);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, LotteryConfig.getWeixinAPPID(this), false);
        // 将该app注册到微信
        api.registerApp(LotteryConfig.getWeixinAPPID(this));
        // sina
        weiboAPI = WeiboSDK.createWeiboAPI(this, LotteryConfig.getSinaKey(About.this));
        weiboAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
            @Override
            public void onCancel() {
                Toast.makeText(About.this, "取消下载", Toast.LENGTH_SHORT).show();
            }
        });
        weiboAPI.registerApp();
    }

    private void setupViews() {
        newVerNum = (TextView) this.findViewById(R.id.new_version_num_about);
        updateContent = (TextView) this.findViewById(R.id.update_content);
        versionInf = (TextView) this.findViewById(R.id.menu_about_version);
        textAbout = (TextView) findViewById(R.id.menu_about_text);
        textAbout.setMovementMethod(LinkMovementMethod.getInstance());
        versionInf.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ViewUtil.showTipsToast(About.this, LotteryUtils.getPid(About.this));
                return false;
            }
        });
        update = (RelativeLayout) this.findViewById(R.id.layout_update);
        update.setOnClickListener(this);
        updateInfo = (LinearLayout) this.findViewById(R.id.new_version_info);
        feedback = (Button) findViewById(R.id.about_feedback);
        feedback.setOnClickListener(this);
        share = (Button) findViewById(R.id.about_share);
        share.setOnClickListener(this);
        barcode = (ImageView) findViewById(R.id.barcode);
        shareImg = (Button) findViewById(R.id.img_show_share_way);
        shareImg.setOnClickListener(this);

        contactInf = (TextView) this.findViewById(R.id.phone);
        TextView contactQQ = (TextView) this.findViewById(R.id.service_qq);
        contactQQ.setText("客服QQ：" + LotteryUtils.getConnectionQQ(mContext));
        contactInf = (TextView) this.findViewById(R.id.phone);
        contactStr =
            "客服电话：<u> <font color='blue'>" + LotteryUtils.getConnectionPhone(mContext) + "</color></u>";
        contactInf.setOnClickListener(this);
        contactInf.setText(Html.fromHtml(contactStr));
        contactInf.setText(Html.fromHtml(contactStr));
        relateBottomMenu = (RelativeLayout) findViewById(R.id.relate_bottom_menu);
    }

    private void init() {
        if (preferences.getString("update_version", "BUKEIDZZCVHBUKEIDZZCVH").length() >= 17) {
            newVerNum.setText("点击升级到：" +
                preferences.getString("update_version", "BUKEIDZZCVHBUKEIDZZCVH").substring(11));
        }
        else {
            newVerNum.setText("点击升级到：" + preferences.getString("update_version", "BUKEIDZZCVHBUKEIDZZCVH"));
        }
        updateContent.setText(preferences.getString("update_content", "未获取数据"));
        String url = "";
        orgShareWay();
        versionInf.setText("版本号：" + LotteryUtils.simpleVersion(About.this) + " (" +
            LotteryUtils.getVersion(About.this) + ")");
        String updateWebUrl = preferences.getString("update_web_url", null);
        if (updateWebUrl != null) {
            update.setVisibility(View.VISIBLE);
            updateInfo.setVisibility(View.VISIBLE);
        }

        lotteryAboutInfContent = (WebView) findViewById(R.id.about_inf_content);
        lotteryAboutInfContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        lotteryAboutInfContent.getSettings().setJavaScriptEnabled(true);
        lotteryAboutInfContent.getSettings().setBuiltInZoomControls(true);
        lotteryAboutInfContent.getSettings().setSupportZoom(true);
        if (HttpConnectUtil.isNetworkAvailable(About.this)) {
            url = "http://download.haozan88.com/publish/about/index.php?v=" + LotteryUtils.getPid(this);
        }
        else {
            url = "file:///android_asset/help/lottery_about_page.html";

        }
        lotteryAboutInfContent.loadUrl(url);
        showProgress();
        handler.sendEmptyMessageDelayed(lOADLOCAL, 60000);
// lotteryAboutInfContent.setWebViewClient(new WebViewClient() {
// @Override
// public boolean shouldOverrideUrlLoading(WebView view, String url) {
// view.loadUrl(url);
// return true;
// }
// });
        lotteryAboutInfContent.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                if (progress == 100) {
                    dismissProgress();
                    relateBottomMenu.setVisibility(View.VISIBLE);
                    versionInf.setVisibility(View.VISIBLE);
                    barcode.setVisibility(View.VISIBLE);
                    isLoaded = true;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open about");
        String eventName = "v2 open about";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.about_feedback) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: open about click feedback list");
            String eventName = "v2 open about click feedback list";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMob = "about_click_feedbacklist";
            MobclickAgent.onEvent(this, eventNameMob);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("if_my_advice", false);
            intent.putExtras(bundle);
            intent.setClass(About.this, Feedback.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.about_share) {
            if (shareWay == SHARE_TYPE_SINA) {
                submitAnalyseDataShare("新浪微博");
                String uid = preferences.getString("sinaweibo_userid", null);
                if (null != uid && LotteryUtils.isBindSina(this)) {
                    doOnceCompleteAuth();
                }
                else {
                    if (dlgCheckBindSina == null) {
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                        customBuilder.setTitle("绑定提示").setMessage("还未绑定新浪微博，是否马上绑定？").setPositiveButton("绑  定",
                                                                                                        new DialogInterface.OnClickListener() {
                                                                                                            public void onClick(DialogInterface dialog,
                                                                                                                                int which) {
                                                                                                                dlgCheckBindSina.dismiss();
// Intent intent =
// new Intent();
// intent.setClass(About.this,
// AuthorizeActivity.class);
// startActivityForResult(intent,
// 1);
// startActivity(new Intent(
// About.this,
// RegisActivity.class));
                                                                                                                AuthDialogListener lis =
                                                                                                                    new AuthDialogListener(
                                                                                                                                           About.this,
                                                                                                                                           weiboAPI);
                                                                                                                lis.setOnFinishSinaAuthListener(About.this);
                                                                                                                mSsoHandler =
                                                                                                                    new SsoHandler(
                                                                                                                                   About.this,
                                                                                                                                   mWeibo);
                                                                                                                mSsoHandler.authorize(lis,
                                                                                                                                      null);
                                                                                                            }
                                                                                                        }).setNegativeButton("下次再说",
                                                                                                                             new DialogInterface.OnClickListener() {
                                                                                                                                 public void onClick(DialogInterface dialog,
                                                                                                                                                     int which) {
                                                                                                                                     dlgCheckBindSina.dismiss();
                                                                                                                                     // add
// by vincet
                                                                                                                                     shareBySina();
                                                                                                                                 }
                                                                                                                             });
                        dlgCheckBindSina = customBuilder.create();
                        dlgCheckBindSina.show();
                    }
                    else {
                        dlgCheckBindSina.show();
                    }
                }
            }
            else if (shareWay == SHARE_TYPE_OTHERS) {
                submitAnalyseDataShare("系统分享");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "@" + getString(R.string.lottery_name) + " 很好用，下载#" +
                    getString(R.string.app_name) + "#试试你的运气,一起发财吧 " + getString(R.string.download_url));
                startActivity(Intent.createChooser(intent, "分享"));
            }
            else if (shareWay == SHARE_TYPE_WEIXIN) {// 分享给微信好友
                if (api.isWXAppInstalled()) {
                    submitAnalyseDataShare("微信好友");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("shareText", getString(R.string.lottery_name) + " 很好用啊，下载#" +
                        getString(R.string.app_name) + "#试试你的运气,一起发财吧 " + getString(R.string.download_url));
                    // changed by vincent
                    bundle.putInt("from", -1);
                    bundle.putInt("share_type", SHARE_TYPE_WEIXIN);
                    saveBarcodeLocal();

                    intent.putExtras(bundle);
                    intent.setClass(About.this, SharePage.class);
                    About.this.startActivity(intent);
                }
                else {
                    ViewUtil.showTipsToast(About.this, UNINSTALL_WXAPP);
                }
            }
            else if (shareWay == SHARE_TYPE_WEIXINFANS) {// 分享到微信朋友圈
                if (api.isWXAppInstalled()) {
                    submitAnalyseDataShare("微信朋友圈");
                    // 初始化一个WXTextObject对象
                    String text =
                        getString(R.string.lottery_name) + " 很好用啊，下载#" + getString(R.string.app_name) +
                            "#试试你的运气,一起发财吧 " + getString(R.string.download_url);
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
                    // req.scene = SendMessageToWX.Req.WXSceneSession;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    // 调用api接口发送数据到微信
                    api.sendReq(req);
                }
                else {
                    ViewUtil.showTipsToast(About.this, UNINSTALL_WXAPP);
                }
            }
            else if (shareWay == SHARE_TYPE_QQWEIBO) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("shareText", getString(R.string.lottery_name) + " 很好用啊，下载#" +
                    getString(R.string.app_name) + "#试试你的运气,一起发财吧 " + getString(R.string.download_url));
                // changed by vincent
                bundle.putInt("from", 1);
                bundle.putInt("share_type", SHARE_TYPE_QQWEIBO);
                bundle.putString("commer_class", "About");
                saveBarcodeLocal();

                intent.putExtras(bundle);
                intent.setClass(About.this, SharePage.class);
                startActivityForResult(intent, 0);
            }
        }
        else if (v.getId() == R.id.phone) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + LotteryUtils.getConnectionPhone(mContext)));
            startActivity(intent);
        }
        else if (v.getId() == R.id.layout_update) {
            String updateWebUrl = preferences.getString("update_web_url", null);
            if (updateWebUrl != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateWebUrl));
                startActivity(intent);
            }
        }
        else if (v.getId() == R.id.img_show_share_way) {
            showShareWayPopupViews();
        }
    }

    public void saveBarcodeLocal() {
        InputStream inp = getResources().openRawResource(R.drawable.barcode);
        bm = BitmapFactory.decodeStream(inp);
        try {
            BasicWeibo.saveMyBitmap("MyScreenshot", bm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareBySina() {
        if (weiboAPI.isWeiboAppSupportAPI()) {
            databaseData.putString("place_sina_share", "about");
            databaseData.commit();
            saveBarcodeLocal();
            int supportApi = weiboAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351) {
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.textObject = getTextObj();
                weiboMessage.imageObject = getImageObj();
                // 初始化从三方到微博的消息请求
                SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
                req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
                req.multiMessage = weiboMessage;
                // 发送请求消息到微博
                weiboAPI.sendRequest(this, req);
            }
            else {
                WeiboMessage weiboMessage = new WeiboMessage();
                weiboMessage.mediaObject = getTextObj();
                // 初始化从三方到微博的消息请求
                SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
                req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
                req.message = weiboMessage;
                // 发送请求消息到微博
                weiboAPI.sendRequest(this, req);
            }
        }
        else {
            // TODO
// ViewUtil.showTipsToast(this, "当前微博版本不支持SDK分享");
        }
    }

    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        String path = PicPath;
        File file = new File(path);
        Bitmap thumbBmp;
        if (!file.exists()) {
            thumbBmp = null;
        }
        else {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        }
        if (null != thumbBmp)
            imageObject.setImageObject(thumbBmp);
        return imageObject;
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text =
            "@" + getString(R.string.lottery_name) + " 很好用，下载#" + getString(R.string.app_name) +
                "#试试你的运气,一起发财吧 " + getString(R.string.download_url);
        return textObject;
    }

// class AuthDialogListener
// implements WeiboAuthListener {
//
// @Override
// public void onComplete(Bundle values) {
//
// String code = values.getString("code");
// if (code != null) {
// // mText.setText("取得认证code: \r\n Code: " + code);
// Toast.makeText(About.this, "认证code成功", Toast.LENGTH_SHORT).show();
// return;
// }
// String token = values.getString("access_token");
// String expires_in = values.getString("expires_in");
//
// // TODO
// // add by vincent
// String uid = values.getString("uid");
// LotteryApp accessInfo = new LotteryApp();
// accessInfo.setWeiboUserID(uid);
// accessInfo.setAccessToken(token);
// AccessInfoHelper accessDBHelper = new AccessInfoHelper(About.this);
// accessDBHelper.open();
// accessDBHelper.create(accessInfo);
// accessDBHelper.close();
// // 保存token到本地
// databaseData.putString("token", token);
// databaseData.putString("expires_in", expires_in);
// databaseData.putString("sinaweibo_userid", uid);
// Logger.inf("vincent", "sinaweibo_userid : " + uid);
// // TODO
// // databaseData.putString("username_for_third", phoneid);
// databaseData.commit();
//
// About.accessToken = new Oauth2AccessToken(token, expires_in);
// if (About.accessToken.isSessionValid()) {
// AccessTokenKeeper.keepAccessToken(About.this, accessToken);
// Toast.makeText(About.this, "认证成功", Toast.LENGTH_SHORT).show();
// }
//
// // OAuth2.0认证时调用
// if (!weiboAPI.isWeiboAppSupportAPI()) {
// gotoSharePage();
// }
// }
//
// @Override
// public void onError(WeiboDialogError e) {
// Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
// }
//
// @Override
// public void onCancel() {
// Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
// }
//
// @Override
// public void onWeiboException(WeiboException e) {
// Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
// }
//
// }

    public void doOnceCompleteAuth() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("shareText", "@" + getString(R.string.lottery_name) + " 很好用，下载#" +
            getString(R.string.app_name) + "#试试你的运气,一起发财吧 " + getString(R.string.download_url));
        // changed by vincent
// bundle.putInt("from", -1);
        bundle.putInt("from", 1);
        bundle.putInt("share_type", SHARE_TYPE_SINA);
        saveBarcodeLocal();

        intent.putExtras(bundle);
        intent.setClass(About.this, SharePage.class);
        About.this.startActivity(intent);
    }

    private void submitAnalyseDataShare(String way) {
        Map<String, String> map = new HashMap<String, String>();
        String user = appState.getUsername();
        if (user == null) {
            user = "";
        }
        map.put("user", user);
        map.put("way", way);
        map.put("location", "关于");
        String eventNameMob = "share_information";
        MobclickAgent.onEvent(this, eventNameMob, way);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                ViewUtil.showTipsToast(About.this, SEND_QQWEIBO_SUCCESS);
            }
        }
        // TODO 删除
// else if (requestCode == 1) {
// if (resultCode == RESULT_OK) {
// doOnceCompleteAuth();
// }
// }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type +
            System.currentTimeMillis();
    }

    private void orgShareWay() {
        shareWay = preferences.getInt("share_way", SHARE_TYPE_SINA);
        if (shareWay == SHARE_TYPE_SINA)
            share.setText("  分享到新浪  ");
        else if (shareWay == SHARE_TYPE_OTHERS)
            share.setText("    其他分享    ");
        else if (shareWay == SHARE_TYPE_WEIXIN)
            share.setText("  分享到微信  ");
        else if (shareWay == SHARE_TYPE_WEIXINFANS)
            share.setText("  微信朋友圈  ");
        else if (shareWay == SHARE_TYPE_QQWEIBO)
            share.setText("    腾讯微博    ");
        share.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showShareWayPopupViews();
                return false;
            }
        });
// betBt.setVisibility(View.GONE);
// betWayBt.setVisibility(View.VISIBLE);
// imgShowBet.setVisibility(View.VISIBLE);
    }

    private void showShareWayPopupViews() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(R.layout.share_ways, null);
        // 分享到新浪
        TextView shareDirectly = (TextView) waySwitchLayout.findViewById(R.id.share_to_sina_weibo);
        shareDirectly.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_SINA) {
                    shareWay = SHARE_TYPE_SINA;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    share.setText("  分享到新浪  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到微信好友
        TextView shareWeixin = (TextView) waySwitchLayout.findViewById(R.id.share_to_weixin);
        shareWeixin.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_WEIXIN) {
                    shareWay = SHARE_TYPE_WEIXIN;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    share.setText("  分享到微信  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到朋友圈
        TextView shareWeixinFriends = (TextView) waySwitchLayout.findViewById(R.id.share_to_weixin_fans);
        shareWeixinFriends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_WEIXINFANS) {
                    shareWay = SHARE_TYPE_WEIXINFANS;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    share.setText("  微信朋友圈  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到腾讯微博
        TextView shareQQWeibo = (TextView) waySwitchLayout.findViewById(R.id.share_to_qq_weibo);
        shareQQWeibo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_QQWEIBO) {
                    shareWay = SHARE_TYPE_QQWEIBO;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    share.setText("    腾讯微博    ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 其他分享
        TextView shareOther = (TextView) waySwitchLayout.findViewById(R.id.share_to_others);
        shareOther.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_OTHERS) {
                    shareWay = SHARE_TYPE_OTHERS;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    share.setText("    其他分享    ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        shareWayPopupWindow = new PopupWindow(this);
        shareWayPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        shareWayPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        shareWayPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        shareWayPopupWindow.setOutsideTouchable(true);
        shareWayPopupWindow.setFocusable(true);
        shareWayPopupWindow.setContentView(waySwitchLayout);
        shareWayPopupWindow.setAnimationStyle(R.style.popup_ball);
        if (shareWay == SHARE_TYPE_SINA) {
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareDirectly.setTextColor(getResources().getColor(R.color.white));
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(getResources().getColor(R.color.dark_purple));
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(getResources().getColor(R.color.dark_purple));
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(getResources().getColor(R.color.dark_purple));
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        else if (shareWay == SHARE_TYPE_OTHERS) {
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareOther.setTextColor(getResources().getColor(R.color.white));
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(getResources().getColor(R.color.dark_purple));
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(getResources().getColor(R.color.dark_purple));
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        else if (shareWay == SHARE_TYPE_WEIXIN) {
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareWeixin.setTextColor(getResources().getColor(R.color.white));
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(getResources().getColor(R.color.dark_purple));
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(getResources().getColor(R.color.dark_purple));
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        else if (shareWay == SHARE_TYPE_WEIXINFANS) {
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareWeixinFriends.setTextColor(getResources().getColor(R.color.white));
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(getResources().getColor(R.color.dark_purple));
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(getResources().getColor(R.color.dark_purple));
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        else if (shareWay == SHARE_TYPE_QQWEIBO) {
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareQQWeibo.setTextColor(getResources().getColor(R.color.white));
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(getResources().getColor(R.color.dark_purple));
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(getResources().getColor(R.color.dark_purple));
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(getResources().getColor(R.color.dark_purple));
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        shareWayPopupWindow.showAsDropDown(share, 0, -7 * share.getHeight());
    }

    @Override
    protected void submitData() {
        String eventName = "open_about";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String way = preferences.getString("place_sina_share", "about");
        if ("normalorderdetail".equals(way)) {
            databaseData.putString("place_sina_share", "about");
            databaseData.commit();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        weiboAPI.responseListener(getIntent(), this);
    }

    /**
     * 调用sina客户端发送微博
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
                Toast.makeText(this, "成功！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, "用户取消！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
                break;
        }

    }
}
