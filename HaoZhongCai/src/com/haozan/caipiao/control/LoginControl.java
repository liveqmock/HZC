package com.haozan.caipiao.control;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.serviceweibo.QQAuthorizeActivity;
import com.haozan.caipiao.activity.topup.AlipayRecharge;
import com.haozan.caipiao.db.UserDbBean;
import com.haozan.caipiao.db.UserTable;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.CommitUserInfRequest;
import com.haozan.caipiao.request.LoginRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.AccountsData;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.alipayfastlogin.AlixId;
import com.haozan.caipiao.util.alipayfastlogin.BaseHelper;
import com.haozan.caipiao.util.alipayfastlogin.MobileSecurePayHelper;
import com.haozan.caipiao.util.alipayfastlogin.MobileSecurePayer;
import com.haozan.caipiao.util.alipayfastlogin.PartnerConfig;
import com.haozan.caipiao.util.alipayfastlogin.Result;
import com.haozan.caipiao.util.alipayfastlogin.ResultChecker;
import com.haozan.caipiao.util.alipayfastlogin.Rsa;
import com.haozan.caipiao.util.security.DESPlus;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener.OnFinishSinaAuthListener;
import com.haozan.caipiao.weibo.sdk.demo.ConstantS;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboDownloadListener;
import com.tencent.tauth.Tencent;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;

/**
 * 登录控制
 * 
 * @author peter_wang
 * @create-time 2013-10-23 下午6:20:55
 */
public class LoginControl
    implements OnFinishSinaAuthListener {
    private static final String AUTHORIZE_FAIL = "授权失败";

    public static final int NOT_REMENBER_PASSWORD = 0;
    public static final int REMENBER_PASSWORD = 1;

    private Tencent mTencent;

    // 新浪微博分享的开放接口
    IWeiboAPI weiboAPI;
    private SsoHandler mSsoHandler;
    Weibo mWeibo;

    // 支付宝相关
    public static final int FASTLOGON = 0;
    public static final int DATAAUTH = 1;
    public static final String PREFS_NAME = "perfAccount";
    private ProgressDialog mProgress = null;
    private int mSelectType = 0;
    private String mUserIdText;
    private String mTokenText;

    private SharedPreferences preferences;
    private Editor databaseData;

    private Context mContext;
    private Handler mHandler;

    public LoginControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        mTencent = Tencent.createInstance(LotteryConfig.getQQAPPID(mContext), mContext);

        mWeibo =
            Weibo.getInstance(LotteryConfig.getSinaKey(mContext), ConstantS.REDIRECT_URL, ConstantS.SCOPE);
        // sina
        weiboAPI = WeiboSDK.createWeiboAPI(mContext, LotteryConfig.getSinaKey(mContext));
        weiboAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
            @Override
            public void onCancel() {
                Toast.makeText(mContext, "取消下载", Toast.LENGTH_SHORT).show();
            }
        });
        weiboAPI.registerApp();

        this.preferences = ActionUtil.getSharedPreferences(mContext);
        this.databaseData = ActionUtil.getEditor(mContext);
    }

    /**
     * 检测是否自动登录
     */
    public void checkAutoLogin() {
        boolean isCommit = true;

        String loginType = ActionUtil.getSharedPreferences(mContext).getString("last_login_type", "");
        if (!"".equals(loginType)) {
            // 判断上次登录方式
            if (loginType.equals("phone")) {// 第三方登录
                // 手机号码登录方式
                String[] inf = getAutoLoginPhoneInf();
                if (inf != null) {
                    isCommit = false;
                    phoneLogin(inf[0], DESPlus.deCode(inf[1]), REMENBER_PASSWORD);
                }
            }
            else {
                thirdPartLogin(loginType);
            }
        }

        if (isCommit) {
            commitUserInf();
        }
    }

    /**
     * 去除手机号码自动登录功能
     */
    public void cancleAutoLogin() {
        String[] inf = getAutoLoginPhoneInf();
        if (inf != null) {
            cancleAutoLogin(inf[0]);
        }
    }

    /**
     * 去除手机号码自动登录功能
     * 
     * @param account 指定的手机号码
     */
    public void cancleAutoLogin(String account) {
        ContentValues values = new ContentValues();
        values.put(UserDbBean.PASSWORD_STATUS, 0);
        mContext.getContentResolver().update(UserTable.CONTENT_URI, values,
                                             UserDbBean.ACCOUNT + "=" + account, null);
    }

    /**
     * 上传用户信息
     */
    public void commitUserInf() {
        CommitUserInfRequest request = new CommitUserInfRequest(mContext, mHandler);
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(request));
    }

    /**
     * 手机号码登录
     * 
     * @param account
     * @param password
     * @param type
     */
    public void phoneLogin(String account, String password, int type) {
        LoginRequest request = new LoginRequest(mContext, mHandler, account, password, type);
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(request));
    }

    /**
     * 第三方登录，已经提前在外面将需要的信息保存到了数据库中，调用线程的时候再从数据库中读取
     * 
     * @param loginType
     */
    public void thirdPartLogin(String loginType) {
        LoginRequest request = new LoginRequest(mContext, mHandler, loginType, REMENBER_PASSWORD);
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(request));
    }

    /**
     * 获取自动登录账号密码
     * 
     * @return
     */
    public String[] getAutoLoginPhoneInf() {
        Cursor cursorAutoLogin =
            mContext.getContentResolver().query(UserTable.CONTENT_URI,
                                                null,
                                                UserDbBean.LOGIN_STATUS + "=1 and " +
                                                    UserDbBean.PASSWORD_STATUS + "=1", null, null);
        if (cursorAutoLogin != null && cursorAutoLogin.moveToNext()) {
            // 获得用户名
            String username =
                cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.ACCOUNT));

            String password = null;
            password = cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.PASSWORD));
            String[] inf = {username, password};
            return inf;
        }
        cursorAutoLogin.close();

        return null;
    }

    /**
     * 获取上次登录账号密码
     * 
     * @return
     */
    public String[] getLastLoginPhoneInf() {
        Cursor cursorAutoLogin =
            mContext.getContentResolver().query(UserTable.CONTENT_URI, null, UserDbBean.LOGIN_STATUS + "=1",
                                                null, null);
        if (cursorAutoLogin != null && cursorAutoLogin.moveToNext()) {
            // 获得用户名
            String username =
                cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.ACCOUNT));

            String password = null;
            int status =
                cursorAutoLogin.getInt(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.PASSWORD_STATUS));
            if (status != 0) {// 0代表不保存密码
                password =
                    cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.PASSWORD));
            }
            String[] inf = {username, password};
            return inf;
        }
        cursorAutoLogin.close();

        return null;
    }

    /**
     * 获取所有登录账号密码
     * 
     * @return
     */
    public ArrayList<AccountsData> getAllLoginPhoneInf() {
        Cursor cursorAutoLogin =
            mContext.getContentResolver().query(UserTable.CONTENT_URI, null, null, null, null);

        ArrayList<AccountsData> allAccountList = new ArrayList<AccountsData>();
        while (cursorAutoLogin != null && cursorAutoLogin.moveToNext()) {
            AccountsData data = new AccountsData();
            data.setAccount(cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.ACCOUNT)));
            data.setPassword(cursorAutoLogin.getString(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.PASSWORD)));
            data.setStatus(cursorAutoLogin.getInt(cursorAutoLogin.getColumnIndexOrThrow(UserDbBean.PASSWORD_STATUS)));

            allAccountList.add(data);
        }

        return allAccountList;
    }

    // QQ登录相关代码
    public void loginByQQ() {
        if (qqServiceReady()) {
            thirdPartLogin(LoginRequest.QQ_LOGIN);
        }
    }

    public boolean qqServiceReady() {
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

    // 新浪微博登录相关代码
    public void loginBySinaWeibo() {
        String uid = ActionUtil.getSharedPreferences(mContext).getString("sinaweibo_userid", null);
        if (null != uid && LotteryUtils.isBindSina(mContext)) {
            thirdPartLogin(LoginRequest.SINA_WEIBO_LOGIN);
        }
        else {
            sinaAuth();
        }
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

        thirdPartLogin(LoginRequest.SINA_WEIBO_LOGIN);
    }

    private boolean isInitAlipay = false;

    // 支付宝登录相关代码
    public void loginByAlipay() {
        if (isInitAlipay) {
            initAlipayFastLogin();
            isInitAlipay = true;
        }
        performPay(FASTLOGON);
    }

    private void initAlipayFastLogin() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mPackageInstallationListener, filter);
    }

    private BroadcastReceiver mPackageInstallationListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getDataString();
            if (!TextUtils.equals(packageName, "package:com.alipay.android.app")) {
                return;
            }

            performPay(mSelectType);
        }
    };

    private void performPay(int type) {
        //
        // check to see if the MobileSecurePay is already installed.
        // 检测安全支付服务是否安装
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
        boolean isMobile_spExist = mspHelper.detectMobile_sp();
        if (!isMobile_spExist) {
            mSelectType = type;
            return;
        }

        // start pay for this order.
        // 根据订单信息开始进行支付
        try {
            // prepare the order info.
            // 准备订单信息
            String orderInfo = getOrderInfo(type);
            // // 这里根据签名方式对订单信息进行签名
            // String signType = getSignType();
            // String strsign = sign(signType, orderInfo);
            // Log.v("sign:", strsign);
            // // 对签名进行编码
            // strsign = URLEncoder.encode(strsign, "UTF-8");
            // // 组装好参数
            // String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
            // + getSignType();
            // Log.v("orderInfo:", info);
            // start the pay.
            // 调用pay方法进行支付
            MobileSecurePayer msp = new MobileSecurePayer();
            boolean bRet = msp.pay(orderInfo, mHandler, AlixId.RQF_PAY, (Activity) mContext);

            if (bRet) {
                // show the progress bar to indicate that we have started
                // paying.
                // 显示“正在支付”进度条
                closeProgress();
                mProgress = BaseHelper.showProgress(mContext, null, "正在提交", false, true);
            }
            else
                ;
        }
        catch (Exception ex) {
            Toast.makeText(mContext, "Failure calling remote service", Toast.LENGTH_SHORT).show();
        }
    }

    // 关闭进度框
    public void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOrderInfo(int type) {
        switch (type) {
            case FASTLOGON:
                return trustLogin(PartnerConfig.PARTNER, mUserIdText);

            case DATAAUTH:
// return dataAuth(PartnerConfig.PARTNER, mUserIdText);
            default:
                return null;
        }

    }

    String trustLogin(String partnerId, String appUserId) {
        StringBuilder sb = new StringBuilder();
        sb.append("app_name=\"mc\"&biz_type=\"trust_login\"&partner=\"");
        sb.append(partnerId);
        Log.d("TAG", "UserID = " + appUserId);
        if (!TextUtils.isEmpty(appUserId)) {
            appUserId = appUserId.replace("\"", "");
            sb.append("\"&app_userid=\"");
            sb.append(appUserId);
        }
        sb.append("\"&notify_url=\"");
        try {
            sb.append(URLEncoder.encode("http://notify.msp.hk/notify.htm", "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("\"");

        String info = sb.toString();

        // 请求信息签名
        String sign = Rsa.sign(info, PartnerConfig.RSA_PRIVATE);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        info += "&sign=\"" + sign + "\"&" + getSignType();

        return info;
    }

    /**
     * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
     */
    public static class AlixOnCancelListener
        implements DialogInterface.OnCancelListener {
        Activity mcontext;

        public AlixOnCancelListener(Activity context) {
            mcontext = context;
        }

        public void onCancel(DialogInterface dialog) {
            mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
        }
    }

    /**
     * get the sign type we use. 获取签名方式
     * 
     * @return
     */
    String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

    public void analyseAlipayLoginInf(String ret) {
        closeProgress();

        // 处理交易结果
        try {
            // 获取交易状态码，具体状态代码请参看文档
            String tradeStatus = "resultStatus={";
            int imemoStart = ret.indexOf("resultStatus=");
            imemoStart += tradeStatus.length();
            int imemoEnd = ret.indexOf("};memo=");
            tradeStatus = ret.substring(imemoStart, imemoEnd);

            // 先验签通知
            ResultChecker resultChecker = new ResultChecker(ret);
            int retVal = resultChecker.checkSign();
            // 验签失败
            if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
                BaseHelper.showDialog((Activity) mContext, "提示", "您的订单信息已被非法篡改。",
                                      android.R.drawable.ic_dialog_alert);
            }
            else {// 验签成功。验签成功后再判断交易状态码
                if (tradeStatus.equals("9000"))// 判断交易状态码，只有9000表示交易成功
                {

// BaseHelper.showDialog(Login.this, "提示", "操作成功。交易状态码：" + tradeStatus,
// R.drawable.dialog_title_icon);
                    mUserIdText = Result.getAppUserId(ret);
                    mTokenText = Result.getToken(ret);
                    if (!TextUtils.isEmpty(mUserIdText) && !TextUtils.isEmpty(mTokenText)) {
                        saveAlipayAccount();
                    }
                    databaseData.putString("last_login_type", "alipay");
                    databaseData.putString("last_topup_way", AlipayRecharge.class.getSimpleName());
                    databaseData.putBoolean("recharge_yy", false);
                    databaseData.commit();
                    // 启动本地登录

                    thirdPartLogin(LoginRequest.ALIPAY_LOGIN);
                }
                else if (tradeStatus.equals("4000")) {
// BaseHelper.showDialog(Login.this, "提示", "操作失败。交易状态码:" + tradeStatus,
// R.drawable.dialog_title_icon);
                    ViewUtil.showTipsToast(mContext, "系统异常，操作失败");
                }
                else if (tradeStatus.equals("6001")) {
                    ViewUtil.showTipsToast(mContext, "用户中途取消");
                }
                else {
                    ViewUtil.showTipsToast(mContext, "操作失败");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            BaseHelper.showDialog((Activity) mContext, "提示", ret, R.drawable.dialog_title_icon);
        }
    }

    private void saveAlipayAccount() {
        databaseData.putString("alipay_account", mUserIdText);
        databaseData.commit();

        databaseData.putString("alipay_accesstoken", getAlipayToken());
        databaseData.commit();
    }

    private String getAlipayToken() {
        String src = "";
        if (!"".equals(mTokenText) && null != mTokenText) {
            src = mTokenText.replace("\"", "");
        }
        return src;
    }

    /**
     * 释放资源,退出页面记得调用
     */
    public void dispose() {
        if (isInitAlipay) {
            saveAlipayAccount();
            mContext.unregisterReceiver(mPackageInstallationListener);
            try {
                mProgress.dismiss();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                // 保存登录方式
                databaseData.putString("last_login_type", LoginRequest.QQ_LOGIN);
                databaseData.commit();
                // 启动客户端的登陆
                thirdPartLogin(LoginRequest.QQ_LOGIN);
            }
            else {
                ViewUtil.showTipsToast(mContext, AUTHORIZE_FAIL);
            }
        }
    }
}
