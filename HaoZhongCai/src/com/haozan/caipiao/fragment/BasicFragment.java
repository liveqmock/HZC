package com.haozan.caipiao.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.Main;
import com.haozan.caipiao.activity.userinf.UserCenter;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.error.ExceptionHandler;
import com.haozan.caipiao.widget.ExitDialog;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class BasicFragment
    extends Fragment {

    protected String failTips;
    protected String noMoreData;
    protected String noNetTips;
// protected Weibo weibo;
    protected LotteryApp appState;
    protected SharedPreferences preferences;
    protected Editor databaseData;

    private ProgressBar progress;
    private ProgressDialog loadingProgressDialog;

    protected Context mContext;
    protected Activity mParentActivity;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mContext = activity;
        ExceptionHandler.register(mContext);
        preferences = mContext.getSharedPreferences("user", 0);
        databaseData = mContext.getSharedPreferences("user", 0).edit();
        initString();

        MobclickAgent.onError(mContext);
        TCAgent.init(mContext);
        TCAgent.setReportUncaughtExceptions(true);

        submitData();
        appState = (LotteryApp) ((Activity) mContext).getApplication();

        mParentActivity = activity;
    }

    private void initString() {
        failTips = getResources().getString(R.string.search_fail);
        noMoreData = getResources().getString(R.string.no_more_data);
        noNetTips = getResources().getString(R.string.network_not_avaliable);
    }

    protected void showProgress() {
        if (progress == null) {
        	try{
        		progress = (ProgressBar) getView().findViewById(R.id.progress);
        	}catch(NullPointerException e){
        		Log.i("log", "hall progress error!");
        	}
        }
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    protected void showProgressDialog(String str) {
        loadingProgressDialog = new ProgressDialog(mContext);
        loadingProgressDialog.setMessage(str);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
    }

    protected void dismissProgress() {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    protected void dismissProgressDialog() {
        if (loadingProgressDialog != null) {
            loadingProgressDialog.dismiss();
        }
    }

    protected void submitData() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "个人中心").setIcon(R.drawable.icon_user_center);
        if (LotteryUtils.getPid(mContext).equals("101201"))
            menu.add(0, 10, 0, "退出彩票").setIcon(R.drawable.icon_exit);
        else
            menu.add(0, 10, 0, "退  出").setIcon(R.drawable.icon_exit);
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
                    intent.setClass(mContext, Login.class);
                    startActivity(intent);
                }
                else {
                    intent.setClass(mContext, UserCenter.class);
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
        ExitDialog exitDialog = new ExitDialog(mContext);
        exitDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        initString();
        FlurryAgent.onStartSession(mContext, LotteryUtils.getFlurry(mContext));
        FlurryAgent.onPageView();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
        TCAgent.onResume((Activity) mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
        TCAgent.onPause((Activity) mContext);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(mContext);
    }

    protected void showExit() {
        if (LotteryUtils.getPid(mContext).equals("101201")) {
            exit();
        }
        else {
            logoff();
        }
    }

    protected void showLoginAgainDialog(String message) {
        ActionUtil.toLogin(mContext, "");
        ViewUtil.showTipsToast(mContext, "登陆超时，请重新登录");
    }

    protected void logoff() {
        Intent intent = new Intent();
        intent.setClass(mContext, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("caipiao.action.LOGOFF");
        startActivity(intent);
    }

    protected void exit() {
        Intent intent = new Intent();
        intent.setClass(mContext, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("caipiao.action.EXIT");
        startActivity(intent);
    }

    protected void SendBroadcastResult() {
        Intent intent = new Intent("com.weibo.result");
        mContext.sendBroadcast(intent);
    }
}
