package com.haozan.caipiao.activity.webbrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.webkit.WebView;

import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.util.MyJavaScript;

public class GeneralWebBrowser extends WebBrowser {
	private boolean toLogin = false;
	private String url;

	Handler mHander = new Handler();

	protected void addMoreWebViewProperty(WebView webview) {
        webview.addJavascriptInterface(new GeneralJavaScript(this, mHander), "myjavascript");
        
    }

	@Override
	protected void onResume() {
		super.onResume();
		if (appState.getUsername() != null && toLogin == true) {
			toLogin = false;
			url = url.replace(";jsessionid=",
					";jsessionid=" + appState.getSessionid());
			webview.loadUrl(url.toString());
			showProgress();
		}
	}

	class GeneralJavaScript extends MyJavaScript {

		public GeneralJavaScript(Context context, Handler handler) {
			super(context, handler);
		}

		/**
		 * 跳去登陆页面
		 */
		public void toLogin(String url) {
			toLogin = true;
			GeneralWebBrowser.this.url = url;
			Intent intent = new Intent();
			intent.setClass(GeneralWebBrowser.this, Login.class);
			GeneralWebBrowser.this.startActivity(intent);
		}

		public void toOther(String url) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			startActivity(intent);
		}
	}
}