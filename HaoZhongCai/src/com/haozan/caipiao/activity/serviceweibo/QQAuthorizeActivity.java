package com.haozan.caipiao.activity.serviceweibo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class QQAuthorizeActivity
    extends BasicActivity {
    private final static String TAG = "Weibo-WebView";
    private final static String TAG_Vincent = "vincent";
    private TextView title;
    private WebView mWebView;
    private String commerClass;
    private ProgressDialog mSpinner;
    private QQAuthDialogListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_brower);
        setupViews();
        init();
        initWebView();
    }

    private void initWebView() {
        String mUrl =
            LotteryUtils.AUTHORIZE_URL_HEAD + "&type=qq&pid=" + LotteryUtils.getPid(QQAuthorizeActivity.this);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new QQWebViewClient());
        mWebView.loadUrl(mUrl);
        mWebView.setVisibility(View.INVISIBLE);
    }

    private void init() {
        mSpinner = new ProgressDialog(QQAuthorizeActivity.this);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("加载中...");
        mListener = new QQAuthDialogListener();
    }

    public void setupViews() {
        title = (TextView) findViewById(R.id.newCmtextView);
        title.setText("腾讯QQ授权");
        findViewById(R.id.title_btinit_right).setVisibility(View.GONE);
        mWebView = (WebView) findViewById(R.id.webView);
    }

    class QQAuthDialogListener
        implements QQDialogListener {

        @Override
        public void onComplete(Bundle values) {
        }

//        @Override
//        public void onError(DialogError e) {
//
//        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onComplete(String json) {
            if (json == null) {
                setResult(RESULT_CANCELED);
            }
            else {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(QQAuthorizeActivity.this, "授权成功");
                    String data = ja.getData(json, "response_data");
                    JsonAnalyse analyse = new JsonAnalyse();
                    String access_token = analyse.getData(data, "access_token");
                    String expires_in = analyse.getData(data, "expires_in");
                    String openid = analyse.getData(data, "openid");
                    String phoneid = analyse.getData(data, "phoneid");
                    databaseData.putString("qq_openid", openid);
                    databaseData.putString("qq_access_token", access_token);
                    databaseData.putString("username_for_third", phoneid);
                    // 计算token失效日期
                    expires_in =
                        String.valueOf(System.currentTimeMillis() + Long.parseLong(expires_in) * 1000);
                    databaseData.putString("qq_expires_in", expires_in);
                    databaseData.commit();

                    setResult(RESULT_OK);
                }
                else {
                    setResult(RESULT_CANCELED);
                }
            }
            QQAuthorizeActivity.this.finish();
        }
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO
// Utility.clearCookies(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            QQAuthorizeActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class QQWebViewClient
        extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(LotteryUtils.REDIRECT_URL_START)) {
                view.stopLoading();
                handleRedirectUrl(view, url);
                return true;
            }
            else {
                view.loadUrl(url);
            }
            // launch non-dialog URLs in a full browser
// getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            mListener.onError(new DialogError(description, errorCode, failingUrl));
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // google issue. shouldOverrideUrlLoading not executed
            if (url.startsWith(LotteryUtils.REDIRECT_URL_START)) {
                view.stopLoading();
                handleRedirectUrl(view, url);
                return;
            }

            super.onPageStarted(view, url, favicon);
            try{
            	mSpinner.show();
            }catch(Exception e){
            	Log.i("log", "spinner show error!");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try{
            	mSpinner.dismiss();
            }catch(Exception e){
            	Log.i("log", "spinner dismiss error!");
            }
            mWebView.setVisibility(View.VISIBLE);
        }

// public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        public void onReceivedSslError(WebView view, SslErrorHandler handler) {
            handler.proceed();
        }

    }

    private void handleRedirectUrl(WebView view, String url) {
        view.setVisibility(View.GONE);
        String json;
        AndroidHttpClient client = new AndroidHttpClient(QQAuthorizeActivity.this);
        json = client.get(url.toString());
        mListener.onComplete(json);

// Bundle values = parseUrl(url);
// String error = values.getString("error");
// String error_code = values.getString("error_code");
//
// if (error == null && error_code == null) {
// mListener.onComplete(values);
// }
// else if (error.equals("access_denied")) {
// // 用户或授权服务器拒绝授予数据访问权限
// mListener.onCancel();
// }
// else {
// // mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
// }
    }
}
