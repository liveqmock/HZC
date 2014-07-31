package com.haozan.caipiao.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.userinf.UserCenter;
import com.haozan.caipiao.fragment.MainActivity;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.error.ExceptionHandler;
import com.haozan.caipiao.widget.ExitDialog;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class BasicActivity
    extends Activity {
    protected String failTips;
    protected String noMoreData;
    protected String noNetTips;
// protected Weibo weibo;
    protected LotteryApp appState;
    protected Context mContext;
    protected SharedPreferences preferences;
    protected Editor databaseData;
    protected boolean toOut = false;

    private ProgressBar progress;
    private ProgressDialog loadingProgressDialog;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionHandler.register(this);
        preferences = getSharedPreferences("user", 0);
        databaseData = getSharedPreferences("user", 0).edit();
        initString();

        MobclickAgent.onError(this);
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);

        submitData();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        appState = (LotteryApp) this.getApplication();
        mContext = this.getApplicationContext();
    }

    private void initString() {
        failTips = getResources().getString(R.string.search_fail);
        noMoreData = getResources().getString(R.string.no_more_data);
        noNetTips = getResources().getString(R.string.network_not_avaliable);
    }

    public void showProgress() {
        if (progress == null) {
            progress = (ProgressBar) this.findViewById(R.id.progress);
        }
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    protected void showProgressDialog(String str) {
        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setMessage(str);
        loadingProgressDialog.setCancelable(true);
        loadingProgressDialog.show();
    }

    public void dismissProgress() {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    protected void dismissProgressDialog() {
        if (loadingProgressDialog != null && !BasicActivity.this.isFinishing()) {
            loadingProgressDialog.dismiss();
        }
    }

    protected void submitData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "个人中心").setIcon(R.drawable.icon_user_center);
        if (LotteryUtils.getPid(this).equals("101201"))
            menu.add(0, 10, 0, "退出彩票").setIcon(R.drawable.icon_exit);
        else
            menu.add(0, 10, 0, "退  出").setIcon(R.drawable.icon_exit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (appState.getUsername() == null) {
                    bundle.putString("forwardFlag", "个人中心");
                    bundle.putBoolean("ifStartSelf", false);
                    intent.putExtras(bundle);
                    intent.setClass(BasicActivity.this, Login.class);
                    startActivity(intent);
                }
                else {
                    intent.setClass(BasicActivity.this, UserCenter.class);
                    startActivity(intent);
                }

                return true;
            case 10:
                exit();
        }
        return false;
    }

    public void besttoneEventCommint(String eventName) {

    }

    protected void showExitDialog() {
        ExitDialog exitDialog = new ExitDialog(BasicActivity.this);
        exitDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, LotteryUtils.getFlurry(this));
        FlurryAgent.onPageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        TCAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    protected void showLoginAgainDialog(String message) {
        ActionUtil.toLogin(this, "");
        ViewUtil.showTipsToast(this, "登陆超时，请重新登录");
    }

    protected void exit() {
        Intent intent = new Intent();
        intent.setClass(BasicActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("caipiao.action.EXIT");
        startActivity(intent);
        toOut = true;
    }

    protected void SendBroadcastResult() {
        Intent intent = new Intent("com.weibo.result");
        sendBroadcast(intent);
    }

}
