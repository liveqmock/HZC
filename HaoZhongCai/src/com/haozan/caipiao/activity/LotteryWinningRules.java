package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.umeng.analytics.MobclickAgent;

public class LotteryWinningRules
    extends BasicActivity {
    private WebView lotteryInfContent;
    private TextView title;
    private Button feedBack;
    private String subUrl;
    private String lotteryName;
    private String dataType;
    final Activity context = this;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.lottery_winning_rules);
        setupViews();
        init();
    }

    private void init() {
        String url = "file:///android_asset/help/" + subUrl;

        if (dataType != null && dataType.equals("score")) {
            if (HttpConnectUtil.isNetworkAvailable(LotteryWinningRules.this)) {
                url = "http://download.haozan88.com/publish/about/score.html";
            }
        }
        else if (dataType != null && dataType.equals("notice")) {
            if (HttpConnectUtil.isNetworkAvailable(LotteryWinningRules.this)) {
                url = subUrl;
            }
        }else if (dataType != null && dataType.equals("table")) {
            if (HttpConnectUtil.isNetworkAvailable(LotteryWinningRules.this)) {
                url =subUrl;
            }
        }

        lotteryInfContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        lotteryInfContent.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        lotteryInfContent.loadUrl(url);
    }

    private void setupViews() {
        title = (TextView) findViewById(R.id.newCmtextView);
        title.setText("中奖规则提示");
        feedBack = (Button) this.findViewById(R.id.title_btinit_right);
        feedBack.setText("反  馈");
        feedBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LotteryWinningRules.this, Feedback.class);
                startActivity(intent);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        subUrl = this.getIntent().getExtras().getString("lottery_help");
        lotteryName = this.getIntent().getExtras().getString("lottery_name");
        dataType = this.getIntent().getExtras().getString("data_type");
        title.setText(lotteryName);
        lotteryInfContent = (WebView) findViewById(R.id.lottery_inf_content);
        lotteryInfContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        lotteryInfContent.getSettings().setJavaScriptEnabled(true);
        lotteryInfContent.getSettings().setBuiltInZoomControls(true);
        lotteryInfContent.getSettings().setSupportZoom(true);
        lotteryInfContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open lottery inf winning rules help");
        String eventName = "v2 open lottery inf winning rules help";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_lottery_help";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LotteryWinningRules.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
