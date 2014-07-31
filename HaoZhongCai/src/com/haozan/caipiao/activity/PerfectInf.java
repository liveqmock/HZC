package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.RechargeSelection;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.util.security.RSA;
import com.umeng.analytics.MobclickAgent;

public class PerfectInf
    extends BasicActivity
    implements OnClickListener {
    private final static String FAILSUBMIT = "添加失败";
    private final static String BINDFAIL = "绑定失败，请稍后重新尝试";
    private final static int COUNTDOWNTIME = 0;
    private final static int PERFECT_INF_RESULT = 31;
    private final static long MILLS = 90 * 1000;

    private int rechargeWay = 0;
    private boolean fromRecharge = false;
    private boolean fromBet = false;
    // the name of the user in the add more information dialog
    private String realName;
    // the idcard of the user in the add more information dialog
    private String identitycard;

    private LinearLayout besidePhoneVerifyLin;
    private LinearLayout phoneLin;
    private EditText userInfName;
    private EditText userInfId;
    private Button userInfConfirm1;
    private Button userInfConfirm2;
    private TextView tips;
    private Button exit1;
    private Button exit2;
    private TextView userName;
    private TextView userId;
    private LayoutInflater factory;
    private View eventview;
    private Button submit_bt_dlg;
    private Button cancle_bt_dlg;
    private int origin = 2;
    private EditText phoneNum;
    private String phoneNumStr;
    private LinearLayout verifyLin;
    private Button btnVerify;
    private EditText ediVerify;
    private String verifyCodeStr;
    private long millis;
    private StringBuilder betLastTime;
    private String toVerifiedPhoneNum;
    private String commerClass;
    private int commitType;// 1.姓名、身份证 2.获取验证码 3.绑定手机

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COUNTDOWNTIME:
                    betLastTime.delete(0, betLastTime.length());
// betLastTime.append("倒计时:");
                    int seconds = (int) millis / 1000;
                    int minutes = seconds / 60;
                    seconds %= 60;
                    minutes %= 60;
                    if (minutes < 10)
                        betLastTime.append("0");
                    betLastTime.append(minutes + ":");
                    if (seconds < 10)
                        betLastTime.append("0");
                    betLastTime.append(seconds);
                    btnVerify.setText(betLastTime.toString());
                    millis -= 1000;
                    if (millis >= 0)
                        handler.sendEmptyMessageDelayed(COUNTDOWNTIME, 1000);
                    else {
// stopSubmit();
                        btnVerify.setText("获取验证码");
                        btnVerify.setEnabled(true);
                        btnVerify.setTextColor(getResources().getColor(R.color.dark_purple));

                        ediVerify.setText("");
                        phoneNum.setEnabled(true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfect_inf);
        setupViews();
        init();
    }

    private void setupViews() {
        setupMainViews();
        comfirDialog();
    }

    private void setupMainViews() {
        besidePhoneVerifyLin = (LinearLayout) findViewById(R.id.ll_beside_phonevirify);
        phoneLin = (LinearLayout) findViewById(R.id.ll_phone);
        tips = (TextView) this.findViewById(R.id.register_tips);
        exit1 = (Button) this.findViewById(R.id.exit1);
        exit1.setOnClickListener(this);
        exit2 = (Button) this.findViewById(R.id.exit2);
        exit2.setOnClickListener(this);
        userInfName = (EditText) this.findViewById(R.id.user_name);
        userInfId = (EditText) this.findViewById(R.id.user_id);
        userInfConfirm1 = (Button) findViewById(R.id.submit1);
        userInfConfirm1.setOnClickListener(this);
        userInfConfirm2 = (Button) findViewById(R.id.submit2);
        userInfConfirm2.setOnClickListener(this);
        phoneNum = (EditText) findViewById(R.id.edi_phonenum);
        verifyLin = (LinearLayout) findViewById(R.id.ll_verify);
        btnVerify = (Button) findViewById(R.id.btn_to_verify);
        btnVerify.setOnClickListener(this);
        ediVerify = (EditText) findViewById(R.id.edi_verify_code);
    }

    private void comfirDialog() {
        factory = LayoutInflater.from(PerfectInf.this);
        eventview = factory.inflate(R.layout.perfect_make_sure_dlg, null);

        submit_bt_dlg = (Button) eventview.findViewById(R.id.submit_dlg);
        submit_bt_dlg.setOnClickListener(this);
        cancle_bt_dlg = (Button) eventview.findViewById(R.id.cancle_dlg);
        cancle_bt_dlg.setOnClickListener(this);

        userName = (TextView) eventview.findViewById(R.id.dialog_item_first);
        userName.setVisibility(View.VISIBLE);
        setTextBold(userName);
        userId = (TextView) eventview.findViewById(R.id.dialog_item_second);
        userId.setVisibility(View.VISIBLE);
        setTextBold(userId);
    }

    private Dialog customDialog_re;

    private void init() {
        ediVerify.setEnabled(false);
        betLastTime = new StringBuilder();
        customDialog_re = new Dialog(this, R.style.dialog);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rechargeWay = bundle.getInt("recharge_way");
            fromRecharge = bundle.getBoolean("from_recharge");
            fromBet = bundle.getBoolean("from_bet");
            commerClass = bundle.getString("commer_class");
        }

        // 银联语音充值等必须保定手机号码和完善信息时
        if (fromRecharge == true) {
            exit1.setVisibility(View.GONE);
            exit2.setVisibility(View.GONE);
        }
        if ("UserCenter".equals(commerClass)) {
            exit1.setVisibility(View.GONE);
            exit2.setVisibility(View.VISIBLE);
        }
        String registerType = appState.getRegisterType();
        if (!"1".equals(registerType)) {
            String reservedPhone = appState.getReservedPhone();
            if ("".equals(reservedPhone)) {
                phoneLin.setVisibility(View.VISIBLE);
                besidePhoneVerifyLin.setVisibility(View.GONE);
                initShowData();
            }
            else {
                phoneLin.setVisibility(View.GONE);
                besidePhoneVerifyLin.setVisibility(View.VISIBLE);
            }
        }
        else {
            phoneLin.setVisibility(View.GONE);
        }

        origin = this.getIntent().getExtras().getInt("origin");
        if (origin == 1) {
            tips.setText("恭喜您注册成功，获取150积分。");
            tips.setVisibility(View.VISIBLE);
        }
    }

    private void initShowData() {
        long refreshTime = preferences.getLong("bind_phone_down_time", 0);
        if (System.currentTimeMillis() - refreshTime < 90 * 1000) {
            toVerifiedPhoneNum = preferences.getString("toVerifiedPhoneNum", null);
            millis = 90 * 1000 - System.currentTimeMillis() + refreshTime;
            ediVerify.setEnabled(true);
            phoneNum.setEnabled(false);
            phoneNum.setText(toVerifiedPhoneNum);
            handler.sendEmptyMessage(COUNTDOWNTIME);
            verifyLin.setVisibility(View.VISIBLE);
            btnVerify.setEnabled(false);
            btnVerify.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    // check whether the client has input the information and if the information
    // is correct
    private Boolean checkVerifyInput() {
        String warning = null;
        verifyCodeStr = ediVerify.getText().toString();
        if (verifyCodeStr.length() < 6) {
            warning = "验证码少于6位";
            ediVerify.setText("");
            ediVerify.requestFocus();
            ediVerify.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }

        if (warning != null) {
            return false;
        }
        return true;
    }

    private Boolean checkNameAndIdentityInput() {
        String warning = null;
        realName = userInfName.getText().toString();
        identitycard = userInfId.getText().toString();

        if (realName == null || realName.equals("")) {
            warning = "请输入姓名";
            userInfName.requestFocus();
            userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (realName.contains("·")) {
            if (realName.matches("^[·]*$")) {
                warning = "请用汉字或汉字加“·”的组合填写真实姓名";
                userInfName.requestFocus();
                userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (realName.split("\\·").length < 2) {
                warning = "请用汉字或汉字加“·”的组合填写真实姓名";
                userInfName.requestFocus();
                userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else {
                if (!realName.split("\\·")[0].equals("") && !realName.split("\\·")[1].equals("")) {
                    if (realName.split("\\·")[0].matches("^[\u4e00-\u9fa5]{0,}$") == false ||
                        realName.split("\\·")[1].matches("^[\u4e00-\u9fa5]{0,}$") == false) {
                        warning = "请用汉字或汉字加“·”的组合填写真实姓名";
                        userInfName.requestFocus();
                        userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
                    }
                }
                else {
                    warning = "请用汉字或汉字加“·”的组合填写真实姓名";
                    userInfName.requestFocus();
                    userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
                }
            }
        }
        else if (!realName.contains("·") && realName.matches("^[\u4e00-\u9fa5]{0,}$") == false) {
// if (realName.matches("^[\u4e00-\u9fa5]{0,}$") == false) {
            warning = "请用汉字或汉字加“·”的组合填写真实姓名";
            userInfName.requestFocus();
            userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
// }
        }
        else if (realName.length() > 12) {
            warning = "请输入12位以内的名字";
            userInfName.requestFocus();
            userInfName.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (identitycard == null || identitycard.equals("")) {
            warning = "请输入身份证号";
            userInfId.requestFocus();
            userInfId.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!(identitycard.length() == 15 || identitycard.length() == 18)) {
            warning = "您输入的身份证号码有误，请输入15或18位正确的身份证号码";
            userInfId.requestFocus();
            userInfId.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (warning != null) {
            return false;
        }
        return true;
    }

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(16[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) PerfectInf.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("pid", LotteryUtils.getPid(PerfectInf.this));
        parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
        if (commitType == 1) {// 提交用户名、身份证
            parameter.put("service", "1002031");
            parameter.put("realname", HttpConnectUtil.encodeParameter(realName));
            parameter.put("idcard",
                          HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(this, toDBC(identitycard))));
        }
        else {
            parameter.put("service", "3002151");
            if (commitType == 2) {// 获取验证码
                parameter.put("op", "1");
            }
            else if (commitType == 3) {// 绑定手机号码
                parameter.put("op", "2");
                parameter.put("validcode", verifyCodeStr);
            }
            parameter.put("reserved_phone", toVerifiedPhoneNum);
        }
        return parameter;
    }

    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            }
            else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }

    // 提交注册成功统计信息
    private void submitStatisticsAddInfSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: user add inf success");
        String eventName = "v2 user add inf success";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);

        String eventNameMob = "add_inf_success";
        MobclickAgent.onEvent(PerfectInf.this, eventNameMob);
    }

    class BindPhoneTask
        extends AsyncTask<String, Object, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (commitType == 3)
                showProgressDialog("正在提交...");
        }

        @Override
        protected String doInBackground(String... arg0) {
            ConnectService connectNet = new ConnectService(PerfectInf.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(8, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            // TODO Auto-generated method stub
            super.onPostExecute(json);
            if (commitType == 3) {
                dismissProgressDialog();
                String inf = null;
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    if (status.equals("200")) {
                        // 倒计时清零
                        databaseData.putLong("bind_phone_down_time", 0);
                        databaseData.commit();
                        appState.setReservedPhone(toVerifiedPhoneNum);
                        inf = "绑定成功";
                        // 加入判断是否已完善姓名、身份证信息
                        if ("0".equals(appState.getPerfectInf())) {
                            animation(phoneLin, besidePhoneVerifyLin);
                        }
                        else {
                            PerfectInf.this.setResult(PERFECT_INF_RESULT);
                            PerfectInf.this.finish();
                        }
                    }
                    else if (status.equals("300")) {
                        inf = analyse.getData(json, "error_desc");
                    }
                    else {
                        inf = BINDFAIL;
                    }
                }
                else {
                    inf = BINDFAIL;
                }
                ViewUtil.showTipsToast(PerfectInf.this, inf);
            }
        }

    }

    class AddUserInfTask
        extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            ConnectService connectNet = new ConnectService(PerfectInf.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dismissProgressDialog();
            userInfConfirm1.setText("提交");
            userInfConfirm1.setEnabled(true);
            String inf = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    if ((!"1".equals(appState.getRegisterType())) &&
                        (!"".equals(appState.getReservedPhone())) || "1".equals(appState.getRegisterType())) {
                        setResult(RESULT_OK);
                    }
                    submitStatisticsAddInfSuccess();
                    inf = "添加身份证成功";
                    appState.setPerfectInf(1);
                    if (fromRecharge)
                        goRecharge();

// Intent intent = new Intent();
// if (null != commerClass && !"".equals(commerClass)) {
// try {
// intent.setClass(PerfectInf.this,
// Class.forName(PerfectInf.this.getPackageName() + commerClass));
// }
// catch (ClassNotFoundException e) {
// e.printStackTrace();
// }
// }
// startActivity(intent);
                    PerfectInf.this.setResult(PERFECT_INF_RESULT);
                    PerfectInf.this.finish();
                }
                else if (status.equals("300")) {
                    inf = analyse.getData(json, "error_desc");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(PerfectInf.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("303")) {
                    inf = analyse.getData(json, "error_desc");
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(PerfectInf.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = FAILSUBMIT;
                }
            }
            else {
                inf = FAILSUBMIT;
            }
            ViewUtil.showTipsToast(PerfectInf.this, inf);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userInfConfirm1.setText("提交中...");
            userInfConfirm1.setEnabled(false);
            showProgressDialog("正在提交...");
        }
    }

// private LotteryDialog lotteryDialog;

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: user open add inf");
        String eventName = "v2 user open add inf";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    public void goRecharge() {
        Intent intent = new Intent();
        intent.setClass(PerfectInf.this, RechargeSelection.class);
        startActivity(intent);
    }

    private String insertblank(String id) {
        StringBuilder idNum = new StringBuilder();
        if (id.length() == 18) {
            idNum.append(id.substring(0, 6));
            idNum.append(" ");
            idNum.append(id.substring(6, 14));
            idNum.append(" ");
            idNum.append(id.substring(14, id.length()));
        }
        else if (id.length() == 15) {
            idNum.append(id.substring(0, 6));
            idNum.append(" ");
            idNum.append(id.substring(6, 12));
            idNum.append(" ");
            idNum.append(id.substring(12, id.length()));
        }
        return idNum.toString();
    }

    private void setTextBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit1) {
            if (HttpConnectUtil.isNetworkAvailable(PerfectInf.this)) {
                if (checkVerifyInput()) {
                    commitType = 3;
                    exectutTask();
                }
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        if (v.getId() == R.id.submit2) {
            if (HttpConnectUtil.isNetworkAvailable(PerfectInf.this)) {
                if (checkNameAndIdentityInput()) {
                    userName.setText(realName);
                    userId.setText(insertblank(identitycard));

                    customDialog_re.setContentView(eventview, new LayoutParams(LayoutParams.FILL_PARENT,
                                                                               LayoutParams.WRAP_CONTENT));
                    customDialog_re.show();
                }
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.submit_dlg) {
            String sessionId = appState.getSessionid();
            if (sessionId == null) {
                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ifStartSelf", false);
                bundle.putString("forwardFlag", "注册");
                intent1.putExtras(bundle);
                intent1.setClass(PerfectInf.this, Login.class);
// intent1.setClass(PerfectInf.this, StartUp.class);
                PerfectInf.this.startActivity(intent1);
            }
            else {
                AddUserInfTask addUserInfTask = new AddUserInfTask();
                commitType = 1;
                addUserInfTask.execute(realName, identitycard);
            }
            customDialog_re.dismiss();
        }
        else if (v.getId() == R.id.cancle_dlg) {
            customDialog_re.dismiss();
        }
        else if (v.getId() == R.id.exit1) {
            // 倒计时清零
            databaseData.putLong("bind_phone_down_time", 0);
            databaseData.commit();
            if ("0".equals(appState.getPerfectInf())) {//
                phoneNum.setError(null);
                animation(phoneLin, besidePhoneVerifyLin);
            }
            else {
                finish();
            }
        }
        else if (v.getId() == R.id.exit2) {
            this.setResult(PERFECT_INF_RESULT);
            finish();
        }
        else if (v.getId() == R.id.btn_to_verify) {
            // TODO
            phoneNumStr = phoneNum.getText().toString();
            String warning = null;
            if (null == phoneNumStr || "".equals(phoneNumStr)) {
                warning = "请输入手机号码";
                phoneNum.requestFocus();
                phoneNum.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (isMobileNO(phoneNumStr) == false) {
                warning = "号码格式不匹配，请重新输入";
                phoneNum.setText("");
                phoneNum.requestFocus();
                phoneNum.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else {
                // TODO 提交手机号码时取这一个，而不是编辑框中的
                toVerifiedPhoneNum = phoneNum.getText().toString();
                databaseData.putString("toVerifiedPhoneNum", toVerifiedPhoneNum);
                databaseData.putLong("bind_phone_down_time", System.currentTimeMillis());
                databaseData.commit();
                ediVerify.setEnabled(true);
                phoneNum.setEnabled(false);
                millis = MILLS;
                handler.sendEmptyMessage(COUNTDOWNTIME);
                verifyLin.setVisibility(View.VISIBLE);
                btnVerify.setEnabled(false);
                btnVerify.setTextColor(getResources().getColor(R.color.gray));
                // TODO 开启任务，将手机号码发往服务器
                commitType = 2;// 获取验证码
                exectutTask();
            }
        }
    }

    public void exectutTask() {
        if (HttpConnectUtil.isNetworkAvailable(PerfectInf.this)) {
            BindPhoneTask task = new BindPhoneTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(PerfectInf.this, noNetTips);
        }
    }

    /**
     * l1消失，l2出现 动画为位移加透明度变化
     * 
     * @param l1
     * @param l2
     */
    private void animation(final LinearLayout l1, final LinearLayout l2) {
        AnimationSet as = new AnimationSet(true);

        TranslateAnimation animation1 = new TranslateAnimation(0, Animation.RELATIVE_TO_SELF - 100, 0, 0);
        animation1.setDuration(200);
        animation1.setFillEnabled(true);
// animation1.setFillBefore(true);
        animation1.setFillAfter(true);

        AlphaAnimation alpha = new AlphaAnimation((float) 1.0, (float) 0.0);
        alpha.setDuration(200);

        as.addAnimation(animation1);
        as.addAnimation(alpha);
        as.setFillEnabled(true);
// as.setFillBefore(true);
        as.setFillAfter(true);
        as.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
// l1.setVisibility(View.GONE);
                l2.setVisibility(View.INVISIBLE);
                AnimationSet as = new AnimationSet(true);

                TranslateAnimation animation1 =
                    new TranslateAnimation(Animation.RELATIVE_TO_SELF + 80, 0, 0, 0);
                animation1.setDuration(500);

                AlphaAnimation alpha = new AlphaAnimation((float) 0.0, (float) 1.0);
                alpha.setDuration(500);

                as.addAnimation(animation1);
                as.addAnimation(alpha);
                as.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        l2.setVisibility(View.VISIBLE);
                    }
                });
                l2.startAnimation(as);
            }
        });
        l1.startAnimation(as);
    }

    @Override
    protected void submitData() {
        String eventName = "open_add_more_inf";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
