package com.haozan.caipiao.activity.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.util.anamation.AnimationModel;

public class WebViewActivity
    extends Activity {
    private WebView webView;
    private Intent intent = null;
    private TextView title;
    public static WebViewActivity webInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web_brower);
        setTitle("新浪授权");

        webInstance = this;
// mContext = getApplicationContext();
        title = (TextView) findViewById(R.id.title);
        webView = (WebView) findViewById(R.id.webView);
        title.setText("新浪授权");
        title.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                webView.requestFocus();
                return false;
            }
        });

        intent = this.getIntent();
        if (!intent.equals(null)) {
            Bundle b = intent.getExtras();
            if (b != null && b.containsKey("url")) {
                webView.loadUrl(b.getString("url"));
                webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress1) {
                        setTitle("请等待，授权加载中..." + progress1 + "%");
                        setProgress(progress1 * 100);

                        if (progress1 == 100)
                            setTitle("新浪授权");
                    }
                });

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
// MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
// MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        SendBroadcastResult();
    }

    private void SendBroadcastResult() {
        Intent intent = new Intent("com.weibo.result");
        sendBroadcast(intent);
    }

// 监听BACK键

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
// WeiboOauthActvity.webInstance.finish();
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(WebViewActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                         R.anim.push_to_left_out);
                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}