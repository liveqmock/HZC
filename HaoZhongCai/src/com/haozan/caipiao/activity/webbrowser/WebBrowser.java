package com.haozan.caipiao.activity.webbrowser;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.util.Logger;
import com.umeng.analytics.MobclickAgent;

public class WebBrowser
    extends BasicActivity
    implements OnClickListener {

    private String title;
    private String url;

    private TextView titleTv;
    private Button feedBack;
    protected WebView webview;

    private Button btnPrev;
    private Button btnNext;
    private Button btnRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web_brower);
        setupViews();
        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            url = bundle.getString("url");
        }

        if (url == null)
            finish();
    }

    private void setupViews() {
        titleTv = (TextView) this.findViewById(R.id.newCmtextView);
        feedBack = (Button) this.findViewById(R.id.title_btinit_right);
        feedBack.setText("联系客服");
        feedBack.setOnClickListener(this);

        webview = (WebView) findViewById(R.id.webView);
        webview.clearView();
        webview.setHorizontalScrollBarEnabled(false);//水平不显示
        webview.setVerticalScrollBarEnabled(false); //垂直不显示
        webview.getSettings().setJavaScriptEnabled(true);
// webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.clearCache(true);
        webview.clearHistory();
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	Logger.inf("lugq", url);
                showProgress();
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
                    btnPrev.setEnabled(webview.canGoBack());
                    btnNext.setEnabled(webview.canGoForward());
                }
            }
        });
        webview.setDownloadListener(new DownloadListener() {
			@Override//WEBVIEW加载下载监听
			public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
//			 	Log.i("tag", "url="+url);             
//	            Log.i("tag", "userAgent="+userAgent);  
//	            Log.i("tag", "contentDisposition="+contentDisposition);           
//	            Log.i("tag", "mimetype="+mimetype);  
//	            Log.i("tag", "contentLength="+contentLength);  
	            Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);      
			}
		});
        addMoreWebViewProperty(webview);

        btnPrev = (Button) this.findViewById(R.id.previous);
        btnPrev.setOnClickListener(this);
        btnNext = (Button) this.findViewById(R.id.next);
        btnNext.setOnClickListener(this);
        btnRefresh = (Button) this.findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(this);
    }

    protected void addMoreWebViewProperty(WebView webview2) {

    }

    private void init() {
        if (title == null) {
            titleTv.setText("公告");
        }
        else {
            titleTv.setText(title);
        }

        urlHandle(url);
        webview.loadUrl(url.toString());
        showProgress();
    }

    /**
     * 判断是否需要在url中增加sessionid，如果检测到;sessionid=?字符串或者url以;sessionid=结尾即需要替换成;sessiondid=xxxxx(
     * xxxxx代表具体的sessionid值)，主要是针对web传递过来的url实现客户端本地登录功能
     * 
     * @param url
     */
    private void urlHandle(String url) {
        boolean addSession = false;
        if (url.contains(";jsessionid=?")) {
            addSession = true;
        }
        else {
            if (url.endsWith(";jsessionid=")) {
                addSession = true;
            }
        }

        if (addSession && appState.getUsername() != null) {
            this.url = url.replace(";jsessionid=", ";jsessionid=" + appState.getSessionid());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open " + title);
        String eventName = "open web browser";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearCache(true);
        webview.clearHistory();
        webview.clearView();
    }

    @Override
    protected void submitData() {
        String eventName = "open_web_browser";
        MobclickAgent.onEvent(this, eventName, title);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(WebBrowser.this, Feedback.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.previous) {
            webview.goBack();
            btnPrev.setEnabled(webview.canGoBack());
        }
        else if (view.getId() == R.id.next) {
            webview.goForward();
            btnNext.setEnabled(webview.canGoForward());
        }
        else if (view.getId() == R.id.refresh) {
            showProgress();
            webview.reload();
        }
    }
}