package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class RunChartWebview
    extends BasicActivity {
    private static final String urls[] = {"http://www.swlc.sh.cn/cams/analyse/01_0.html",
            "http://www.swlc.sh.cn/cams/analyse/05_0.html", "http://www.swlc.sh.cn/cams/analyse/07_0.html",
            "http://www.swlc.sh.cn/cams/analyse/13_0.html", "http://www.swlc.sh.cn/cams/analyse/10_0.html",
            "http://www.swlc.sh.cn/cams/analyse/02_0.html"};
    private int position = -1;
    private String kind;
    private TextView title;
    private WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web_brower);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            kind = bundle.getString("kind");
        else
            return;
        title = (TextView) this.findViewById(R.id.title);
        webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                if (progress == 100) {
                    dismissProgress();
                }
            }
        });
// Intent it=new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
// startActivity(it);
        int length = LotteryUtils.LOTTERY_ID.length;
        for (int i = 0; i < length; i++) {
            if (kind.equals(LotteryUtils.LOTTERY_ID[i])) {
                position = i;
            }
        }
        if (position <= 5 && position >= 0) {
            title.setText(LotteryUtils.LOTTERY_NAMES[position] + "走势图");
            webview.loadUrl(urls[position]);
        }
        else {
            return;
        }
        showProgress();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RunChartWebview.this.finish();
            webview.clearCache(true);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(RunChartWebview.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open run chart web view");
        String eventName = "v2 open run chart web view";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearCache(true);
    }

    @Override
    protected void submitData() {
        String eventName = "open_runchart";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}