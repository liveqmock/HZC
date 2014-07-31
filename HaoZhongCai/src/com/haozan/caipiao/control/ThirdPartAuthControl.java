package com.haozan.caipiao.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.activity.serviceweibo.QQAuthorizeActivity;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener.OnFinishSinaAuthListener;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.tencent.tauth.Tencent;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;

/**
 * 第三方登录控制
 * 
 * @author peter_wang
 * @create-time 2013-10-23 下午6:20:55
 */
public class ThirdPartAuthControl
    implements OnFinishSinaAuthListener {
    public static final int NOT_REMENBER_PASSWORD = 0;
    public static final int REMENBER_PASSWORD = 1;

    private Context mContext;
    private Handler mHandler;

    private SharedPreferences preferences;
    private Editor databaseData;

    private Tencent mTencent;

    // 新浪微博分享的开放接口
    IWeiboAPI weiboAPI;
    private SsoHandler mSsoHandler;
    Weibo mWeibo;

    public ThirdPartAuthControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        this.preferences = ActionUtil.getSharedPreferences(mContext);
        databaseData = ActionUtil.getEditor(mContext);
    }

    public boolean qqServiceReady() {
        mTencent = Tencent.createInstance(LotteryConfig.getQQAPPID(mContext), mContext);

        String openid = preferences.getString("qq_openid", null);
        String access_token = preferences.getString("qq_access_token", null);
        String expires_in = preferences.getString("qq_expires_in", null);
        mTencent.setOpenId(openid);
        mTencent.setAccessToken(access_token, expires_in);

        boolean ready = mTencent.isSessionValid() && mTencent.getOpenId() != null;
        if (!ready) {
            Intent intent = new Intent();
            intent.setClass(mContext, QQAuthorizeActivity.class);
            ((Activity) mContext).startActivityForResult(intent, 2);
        }
        return ready;
    }

    public void sinaAuth() {
        AuthDialogListener lis = new AuthDialogListener(mContext, weiboAPI);
        lis.setOnFinishSinaAuthListener(this);
        mSsoHandler = new SsoHandler((Activity) mContext, mWeibo);
        mSsoHandler.authorize(lis, null);
    }

    @Override
    public void doOnceCompleteAuth() {
        databaseData.putString("last_login_type", "sina_weibo");
        databaseData.commit();
    }
}
