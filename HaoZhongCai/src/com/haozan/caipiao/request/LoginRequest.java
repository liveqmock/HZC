package com.haozan.caipiao.request;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.LoginControl;
import com.haozan.caipiao.db.UserDbBean;
import com.haozan.caipiao.db.UserTable;
import com.haozan.caipiao.db.api.SimpleTable;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.security.DESPlus;
import com.haozan.caipiao.util.security.EncryptUtil;

/**
 * 登陆接口
 * 
 * @author peter_wang
 * @create-time 2013-10-24 下午2:46:59
 */
public class LoginRequest
    extends AsyncConnectionInf {
    public static final String PHONE_LOGIN = "phone";
    public static final String QQ_LOGIN = "qq";
    public static final String SINA_WEIBO_LOGIN = "sina_weibo";
    public static final String ALIPAY_LOGIN = "alipay";

    private static final String LOGIN_PHONE_REQUEST_SERVICE = "1002021";
    private static final String LOGIN_THIRDPART_REQUEST_SERVICE = "2002131";

    private String mService;

    private Context mContext;
    private Handler mHandler;

    private String mAccount;
    private String mPassword;

    private String mLoginType;

    private int mPasswordStatus;

    private SharedPreferences preferences;
    private Editor databaseData;

    private UEDataAnalyse mUploadRequestTime;

    /**
     * 手机号码登陆
     * 
     * @param context
     * @param handler
     * @param account 手机号码
     * @param password 密码
     */
    public LoginRequest(Context context, Handler handler, String account, String password, int passwordStatus) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mAccount = account;
        this.mPassword = password;

        this.mPasswordStatus = passwordStatus;

        this.mLoginType = PHONE_LOGIN;
        init();
    }
 
    /**
     * 第三方登录
     * 
     * @param context
     * @param handler
     * @param mLoginType 登录方式
     */
    public LoginRequest(Context context, Handler handler, String mLoginType, int passwordStatus) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mPasswordStatus = passwordStatus;

        this.mLoginType = mLoginType;
        init();
    }

    private void init() {
        preferences = ActionUtil.getSharedPreferences(mContext);
        databaseData = ActionUtil.getEditor(mContext);

        mUploadRequestTime = new UEDataAnalyse(mContext);
    }

    private HashMap<String, String> initPhoneLoginHashMap() {
        mService = LOGIN_PHONE_REQUEST_SERVICE;

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", LOGIN_PHONE_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone", mAccount);
        parameter.put("password", EncryptUtil.encryptPassword(mContext, mPassword));
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Activity.TELEPHONY_SERVICE);
        String udid = tm.getDeviceId();
        parameter.put("udid", udid);
        String version = LotteryUtils.fullVersion(mContext);
        parameter.put("soft_version", version);
        parameter.put("phone_version", android.os.Build.MODEL);
        parameter.put("os_version", android.os.Build.VERSION.SDK);
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        if (mLoginType.equals(PHONE_LOGIN)) {
            connection.postSecuryGateways(false, initPhoneLoginHashMap());
        }
        else {
            connection.postSecuryGateways(false, initThirdHashMap());
        }
        createUrlPost(connection.getmUrl(), connection.getmFinalMap());

        mUploadRequestTime.onConnectStart();
    }

    private void loginResponseData(String response) {
        JsonAnalyse analyse = new JsonAnalyse();
        String lastLogin = analyse.getData(response, "last_login");
// String str = "                   登录成功\n上次登录：" + lastLogin;
// ViewUtil.showTipsToast(StartUp.this, str);
        databaseData.putString("lastLogin", lastLogin);
        databaseData.putBoolean("hasAccount", true);
        databaseData.commit();
        LotteryApp appState = ((LotteryApp) ((Activity) mContext).getApplicationContext());
        String sessionId = analyse.getData(response, "sessionid");
        appState.setSessionid(sessionId);
        int isPerfectInf = Integer.valueOf(analyse.getData(response, "integrity"));
        appState.setPerfectInf(isPerfectInf);
        String registerType = analyse.getData(response, "type");
        appState.setRegisterType(registerType);// 注册方式

        String reservedPhone = analyse.getData(response, "reserved_phone");// 预留手机号码
        if (null != reservedPhone && !"".equals(reservedPhone) && !"null".equals(reservedPhone)) {
            appState.setReservedPhone(analyse.getData(response, "reserved_phone"));
        }
        else {
            appState.setReservedPhone("");
        }

        String nickName = analyse.getData(response, "nickname");
        if (nickName != null && !nickName.equals(""))
            appState.setNickname(nickName);
        else
            appState.setNickname(null);
        String email = analyse.getData(response, "email");
        appState.setEmail(email);
        String userId = analyse.getData(response, "user_id");
        appState.setUserid(userId);
        double balance = Double.valueOf(analyse.getData(response, "balance"));
        double win = Double.valueOf(analyse.getData(response, "win_balance"));
        String score = analyse.getData(response, "score");
        String isBand = analyse.getData(response, "is_band");
        String service = analyse.getData(response, "service");
        double available;
        if (balance > win)
            available = win;
        else
            available = balance;
        appState.setAccount(balance);
        appState.setWinBalance(win);
        appState.setAvailableBalance(available);

        String phone = null;
        if (mLoginType.equals(PHONE_LOGIN)) {
            appState.setUsername(mAccount);
        }
        else {
            phone = analyse.getData(response, "phone");
            appState.setUsername(phone);
            // 将username保存到本地，供第三方登录时使用
            databaseData.putString("username_for_third", phone);
            databaseData.commit();
        }
        appState.setScore(Integer.valueOf(score));
        appState.setBand(Integer.valueOf(isBand));

        String suscribeJson = "";
        try {
            if (service != null && !service.equals("[]")) {
                suscribeJson = subscribeJsonData(service);
                appState.setServiced(suscribeJson);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // 手机号码登录保存信息到数据库，其他第三方登录处理起来比较复杂（比如每种方式只允许一个账号保存在本地，上次直接使用），不保存，用Editor保存基本登录信息即可，
        // 整个app使用过程中用application保存数据，程序崩溃或被system kill的时候，再取数据库信息重新拿用户资料赋值到Application，第三方的话，就直接重新登录
        ContentValues values = new ContentValues();
        values.put(SimpleTable.SQL_INSERT_OR_REPLACE, true);
        if (mLoginType.equals(PHONE_LOGIN)) {
            resetLoginStatus();

            values.put(UserDbBean.ACCOUNT, mAccount);
            values.put(UserDbBean.PASSWORD, DESPlus.enCode(mPassword));

            values.put(UserDbBean.LOGIN_STATUS, 1);// 标示这是最近一次登录的账号，方便崩溃或被系统kill直接提取信息
            values.put(UserDbBean.PASSWORD_STATUS, mPasswordStatus);
            values.put(UserDbBean.LAST_LOGIN_TIME, lastLogin);
            values.put(UserDbBean.SESSIONID, sessionId);
            values.put(UserDbBean.INTEGRITY, isPerfectInf);
            values.put(UserDbBean.REGISTER_TYPE, registerType);
            values.put(UserDbBean.RESERVED_PHONE, reservedPhone);
            values.put(UserDbBean.NICKNAME, nickName);
            values.put(UserDbBean.EMAIL, email);
            values.put(UserDbBean.USER_ID, userId);
            values.put(UserDbBean.BALANCE, balance);
            values.put(UserDbBean.WIN_BALANCE, win);
            values.put(UserDbBean.SCORE, score);
            values.put(UserDbBean.IS_BAND_BANKCARD, isBand);
            values.put(UserDbBean.SUBSCRIBE_SERVICE, suscribeJson);
            mContext.getContentResolver().insert(UserTable.CONTENT_URI, values);
        }
    }

    /**
     * 重置上次登录的账号，不再标示
     */
    public void resetLoginStatus() {
        ContentValues values = new ContentValues();
        values.put(UserDbBean.LOGIN_STATUS, 0);
        mContext.getContentResolver().update(UserTable.CONTENT_URI, values, null, null);
    }

    /**
     * 解析短信订阅中奖通知信息
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    private String subscribeJsonData(String json)
        throws JSONException {
        StringBuilder sb_json_data = new StringBuilder();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            sb_json_data.append(jsonArray.getJSONObject(i).get("s"));
            sb_json_data.append(",");
        }
        sb_json_data.delete(sb_json_data.length() - 1, sb_json_data.length());
        return sb_json_data.toString();
    }

    /**
     * ThirdLoginTask 第三方登录的hashmap参数获取
     * 
     * @return
     * @throws Exception
     */
    private HashMap<String, String> initThirdHashMap() {
        mService = LOGIN_THIRDPART_REQUEST_SERVICE;

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", mService);
        parameter.put("pid", LotteryUtils.getPid(mContext));
        if (QQ_LOGIN.equals(mLoginType)) {
            parameter.put("phone", preferences.getString("username_for_third", ""));
        }
        else {
            parameter.put("phone", "");
        }
        parameter.put("sid", getSid());
        parameter.put("type", getType());
        if (SINA_WEIBO_LOGIN.equals(mLoginType)) {
            parameter.put("token", preferences.getString("token", ""));
        }
        else if (ALIPAY_LOGIN.equals(mLoginType)) {
            parameter.put("token", preferences.getString("alipay_accesstoken", ""));
        }
        return parameter;
    }

    public String getSid() {
        if ("qq".equals(mLoginType)) {
            return preferences.getString("qq_openid", "");
        }
        else if ("sina_weibo".equals(mLoginType)) {
            return preferences.getString("sinaweibo_userid", "");
        }
        else if ("alipay".equals(mLoginType)) {
            return getAlipaySid();
        }
        else
            return "";
    }

    private String getAlipaySid() {
        String mUserIdText = preferences.getString("alipay_account", null);
        String src = null;
        if (mUserIdText != null) {
            src = mUserIdText.replace("\"", "");
        }
        return src;
    }

    public String getType() {
        if ("qq".equals(mLoginType)) {
            return "3";
        }
        else if ("sina_weibo".equals(mLoginType)) {
            return "2";
        }
        else if ("alipay".equals(mLoginType)) {
            return "4";
        }
        else
            return "";
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectSuccess(mService);

        JsonAnalyse analyse = new JsonAnalyse();
        String responseData = analyse.getData(rspContent, "response_data");
        loginResponseData(responseData);

        Message message = Message.obtain(mHandler, ControlMessage.LOGIN_OK_RESULT);
        message.sendToTarget();

        databaseData.putString("last_login_type", mLoginType);
        databaseData.commit();

        LoginControl control = new LoginControl(mContext, mHandler);
        control.commitUserInf();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        mUploadRequestTime.submitConnectFail(mService, statusCode + ":" + rspContent);

        if (statusCode == AsyncConnectionBasic.ACCESS_FAIL_STATUS) {
            rspContent = "登录失败";
        }

        Message message = Message.obtain(mHandler, ControlMessage.LOGIN_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}