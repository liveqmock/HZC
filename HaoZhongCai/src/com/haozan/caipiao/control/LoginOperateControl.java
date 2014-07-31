package com.haozan.caipiao.control;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.haozan.caipiao.types.AccountsData;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.UEDataAnalyse;

/**
 * 登录页面操作
 * 
 * @author peter_wang
 * @create-time 2013-10-23 下午6:20:55
 */
public class LoginOperateControl {
    private LoginControl mLoginControl;

    private Context mContext;
    private Handler mHandler;

    public LoginOperateControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        init();
    }

    private void init() {
        mLoginControl = new LoginControl(mContext, mHandler);
    }

    public void checkAutoLogin() {
        mLoginControl.checkAutoLogin();
    }

    /**
     * 手机号码登录
     * 
     * @param account
     * @param password
     * @param type
     */
    public void phoneLogin(String account, String password, int type) {
        mLoginControl.phoneLogin(account, password, type);
    }

    /**
     * 第三方登录，已经提前在外面将需要的信息保存到了数据库中，调用线程的时候再从数据库中读取
     * 
     * @param loginType
     */
    public void thirdPartLogin(String loginType) {
        mLoginControl.thirdPartLogin(loginType);
    }

    // QQ授权
    public void loginByQQ() {
        mLoginControl.loginByQQ();
    }

    // 新浪微博授权
    public void loginBySinaWeibo() {
        mLoginControl.loginBySinaWeibo();
    }

    // 支付宝授权
    public void loginByAlipay() {
        mLoginControl.loginByAlipay();
    }

    /**
     * 上传用户点击记住密码按钮事件
     */
    public void submitRememberPasswordEvent(boolean savePassword) {
        UEDataAnalyse.onEvent(mContext, "login_click_remember_password", "" + savePassword);
    }

    /**
     * 用户登录方式上传
     * 
     * @param type 登录方式
     */
    public void submitStatisticClickLogin(String type) {
        UEDataAnalyse.onEvent(mContext, "login_way", type);
    }

    /**
     * 用户登录信息上传
     * 
     * @param type 登录方式
     */
    public void submitStatisticOpenLogin() {
        UEDataAnalyse.onEvent(mContext, "open_login");
    }

    /**
     * 解析并操作支付宝登录后的信息
     * 
     * @param ret
     */
    public void analyseAlipayLoginInf(String ret) {
        mLoginControl.analyseAlipayLoginInf(ret);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            mHandler.sendEmptyMessage(ControlMessage.FINISH_ACTIVITY);
        }

        mLoginControl.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取上次登录账号密码
     * 
     * @return
     */
    public String[] getLastLoginPhoneInf() {
        return mLoginControl.getLastLoginPhoneInf();
    }

    /**
     * 获取所有登录账号密码
     * 
     * @return
     */
    public ArrayList<AccountsData> getAllLoginPhoneInf() {
        return mLoginControl.getAllLoginPhoneInf();
    }

    public void toNextPage(Bundle bundle) {
        if (bundle != null) {
            boolean isContinuePass = bundle.getBoolean("is_continue_pass");
            String packageName = bundle.getString("package_name");
            String className = bundle.getString("class_name");

            if (className != null) {
                if (packageName != null) {
                    if (isContinuePass) {
                        ActionUtil.toPage(mContext, packageName, className, bundle);
                    }
                    else {
                        ActionUtil.toPage(mContext, packageName, className, null);
                    }
                }
                else {
                    if (isContinuePass) {
                        ActionUtil.toPage(mContext, className, bundle);
                    }
                    else {
                        ActionUtil.toPage(mContext, className);
                    }
                }
            }
        }
    }

    /**
     * 去除手机号码自动登录功能
     */
    public void cancleAutoLogin(String account) {
        mLoginControl.cancleAutoLogin(account);
    }
}
