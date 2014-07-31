package com.haozan.caipiao.weibo.sdk;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.weiboutil.AccessInfoHelper;
import com.haozan.caipiao.weibo.sdk.keep.AccessTokenKeeper;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class AuthDialogListener
    implements WeiboAuthListener {
    Context context;
    IWeiboAPI weiboAPI;
    Editor databaseData;
    Oauth2AccessToken accessToken;

    OnFinishSinaAuthListener listener;

    public AuthDialogListener(Context context, IWeiboAPI weiboAPI) {
        this.context = context;
        this.weiboAPI = weiboAPI;
        databaseData = context.getSharedPreferences("user", 0).edit();

    }

    @Override
    public void onComplete(Bundle values) {

        String code = values.getString("code");
        if (code != null) {
            return;
        }
        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");

        // TODO
        // add by vincent
        String uid = values.getString("uid");
        LotteryApp accessInfo = new LotteryApp();
        accessInfo.setWeiboUserID(uid);
        accessInfo.setAccessToken(token);
        AccessInfoHelper accessDBHelper = new AccessInfoHelper(context);
        accessDBHelper.open();
        accessDBHelper.create(accessInfo);
        accessDBHelper.close();
        // 保存token到本地
        databaseData.putString("token", token);
        databaseData.putString("expires_in", expires_in);
        databaseData.putString("sinaweibo_userid", uid);
        Logger.inf("vincent", "sinaweibo_userid : " + uid);
        // TODO
// databaseData.putString("username_for_third", phoneid);
        databaseData.commit();

        accessToken = new Oauth2AccessToken(token, expires_in);
        if (accessToken.isSessionValid()) {
            AccessTokenKeeper.keepAccessToken(context, accessToken);
            Toast.makeText(context, "认证成功", Toast.LENGTH_SHORT).show();
        }

        listener.doOnceCompleteAuth();
    }

    @Override
    public void onError(WeiboDialogError e) {
        Toast.makeText(context, "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(context, "Auth cancel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Toast.makeText(context, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    public final void setOnFinishSinaAuthListener(OnFinishSinaAuthListener listener) {
        this.listener = listener;
    }

    public static interface OnFinishSinaAuthListener {
        public void doOnceCompleteAuth();
    }

}
