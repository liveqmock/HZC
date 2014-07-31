package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.util.security.RSA;
import com.umeng.analytics.MobclickAgent;

public class ResetPassword
    extends BasicActivity
    implements OnClickListener {

    private final static int COUNTDOWNTIME = 0;
    private final static String RESETFAIL = "重置密码失败";

    private long millis = 20 * 60 * 1000;
    private String phoneNum;
    private String perfectInf = null;
    private String sessionId = null;
    private String id;
    private String code;
    private String passwordNum1;
    private String passwordNum2;
    private StringBuilder betLastTime;

    private TextView phone;
    private TextView countdown;
    private Button submit;
    private Button startAgain;
    private EditText verificationCode;
    private EditText userId;
    private EditText password1;
    private EditText password2;
    private LinearLayout idLayout;
// private TextView iden;
    private EditText idNum;
    private ProgressDialog progress;
    private ScrollView scrollView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COUNTDOWNTIME:
                    betLastTime.delete(0, betLastTime.length());
                    betLastTime.append("倒计时:");
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
                    countdown.setText(betLastTime.toString());
                    millis -= 1000;
                    if (millis >= 0)
                        handler.sendEmptyMessageDelayed(COUNTDOWNTIME, 1000);
                    else {
                        stopSubmit();
                        countdown.setText("已截止");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        setupViews();
        init();
    }

    protected void stopSubmit() {
        submit.setEnabled(false);
        submit.setText("重置倒计时截止");
    }

    private void setupViews() {
        phone = (TextView) this.findViewById(R.id.phone);
        countdown = (TextView) this.findViewById(R.id.count_down);
        verificationCode = (EditText) this.findViewById(R.id.verification_code);
        userId = (EditText) this.findViewById(R.id.id_num);
        password1 = (EditText) this.findViewById(R.id.new_password1);
        password2 = (EditText) this.findViewById(R.id.new_password2);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        startAgain = (Button) this.findViewById(R.id.reset_again);
        startAgain.setOnClickListener(this);
// idLayout = (LinearLayout) this.findViewById(R.id.user_id_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        idNum = (EditText) findViewById(R.id.id_num);
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNum = bundle.getString("phone");
            perfectInf = bundle.getString("per_inf");
            sessionId = bundle.getString("session_id");
        }
        if (phoneNum != null)
            phone.setText("手机号码：" + phoneNum);
        if (perfectInf != null) {
            if (perfectInf.equals("1")) {
// iden.setVisibility(View.VISIBLE);
                idNum.setVisibility(View.VISIBLE);
// idLayout.setVisibility(View.VISIBLE);
            }
            else {
// iden.setVisibility(View.GONE);
                idNum.setVisibility(View.GONE);
// idLayout.setVisibility(View.GONE);
            }
        }
        progress = new ProgressDialog(this);
        progress.setMessage("重置密码中...");
        betLastTime = new StringBuilder();
        SharedPreferences preferences = getSharedPreferences("user", 0);
        long refreshTime = preferences.getLong("reset_password_time", 0);
        if (System.currentTimeMillis() - refreshTime < 20 * 60 * 1000) {
            millis = 20 * 60 * 1000 - System.currentTimeMillis() + refreshTime;
        }
        else {
            Editor databaseData = getSharedPreferences("user", 0).edit();
            databaseData.putLong("reset_password_time", System.currentTimeMillis());
            databaseData.putString("reset_password_phone", phoneNum);
            databaseData.putString("reset_password_inf", perfectInf);
            databaseData.putString("reset_password_session", sessionId);
            databaseData.commit();
        }
        handler.sendEmptyMessage(COUNTDOWNTIME);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit) {
            Boolean checkInf = checkInput();
            if (checkInf == true) {
                ResetPasswordTask submitTask = new ResetPasswordTask();
                submitTask.execute();
            }
        }
        else if (v.getId() == R.id.reset_again) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("zzc_desc", "username [" + appState.getUsername() + "]: click login find password");
            FlurryAgent.onEvent("user click reset password find password", map);
            Editor databaseData = getSharedPreferences("user", 0).edit();
            databaseData.putLong("reset_password_time", 0);
            databaseData.commit();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("phone", phoneNum);
            intent.putExtras(bundle);
            intent.setClass(ResetPassword.this, FindPasswordReq.class);
            startActivity(intent);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(ResetPassword.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                   R.anim.push_to_right_out);
            }
            finish();
        }
    }

    // check whether the client has input the information and if the information is correct
    private Boolean checkInput() {
        code = verificationCode.getText().toString();
        id = userId.getText().toString();
        passwordNum1 = password1.getText().toString();
        passwordNum2 = password2.getText().toString();
        String warning = null;
        if (code.equals("")) {
            warning = "请输入验证码";
            verificationCode.requestFocus();
            verificationCode.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (code.length() != 6) {
            warning = "请输入6位验证码";
            verificationCode.requestFocus();
            verificationCode.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (perfectInf != null) {
            if (perfectInf.equals("1") && id.equals("")) {
                warning = "请输入身份证后六位";
                userId.requestFocus();
                userId.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (perfectInf.equals("1") && id.length() < 6) {
                warning = "请正确输入身份证后六位";
                userId.requestFocus();
                userId.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum1.equals("")) {
                warning = "请输入新密码";
                password1.requestFocus();
                password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            /*
             * else if (!passwordNum1.matches("\\d{6,12}")) { warning = "您只能输入数字"; password1.requestFocus();
             * password1.setError(warning); }
             */
            else if (passwordNum1.length() < 6) {
                warning = "请输入6-12位数字";
                password1.requestFocus();
                password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum1.length() > 12) {
                warning = "请输入6-12位数字";
                password1.requestFocus();
                password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum2.equals("")) {
                warning = "请再次输入新密码";
                password2.requestFocus();
                password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum2.length() < 6) {
                warning = "请输入6-12位数字";
                password2.requestFocus();
                password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum2.length() > 12) {
                warning = "第二个密码请输入6-12位数字";
                password2.requestFocus();
                password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
            else if (passwordNum1.equals(passwordNum2) == false) {
                warning = "两次输入的密码不一致,请重新输入";
                password2.requestFocus();
                password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            }
        }
        else if (passwordNum1.equals("")) {
            warning = "请输入新密码";
            password1.requestFocus();
            password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.length() < 6) {
            warning = "请输入6-12位数字";
            password1.requestFocus();
            password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        /*
         * else if (!passwordNum1.matches("\\d{6,12}")) { warning = "您只能输入数字"; password1.requestFocus();
         * password1.setError(warning); }
         */
        else if (passwordNum1.length() > 12) {
            warning = "请输入6-12位数字";
            password1.requestFocus();
            password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.equals("")) {
            warning = "请再次输入新密码";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() < 6) {
            warning = "请输入6-12位数字";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() > 12) {
            warning = "第二个密码请输入6-12位数字";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.equals(passwordNum2) == false) {
            warning = "两次输入的密码不一致,请重新输入";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (warning != null) {
// ViewUtil.showTipsToast(this,warning);
            return false;
        }
        else
            return true;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1002051");
        parameter.put("pid", LotteryUtils.getPid(ResetPassword.this));
        parameter.put("sms_code", HttpConnectUtil.encodeParameter(code));
        parameter.put("new_password",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptPassword(this, passwordNum1)));
        parameter.put("type", "1");
        parameter.put("phone", HttpConnectUtil.encodeParameter(phoneNum));
        if (id != null || id.equals("")) {
            id = id.replace("x", "X");
            parameter.put("part_idcard", HttpConnectUtil.encodeParameter(id));
        }
        return parameter;
    }

    class ResetPasswordTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(ResetPassword.this);
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
            submit.setEnabled(true);
            submit.setText("申请重置密码");
            progress.cancel();
            String inf = null;
            if (json == null)
                inf = RESETFAIL;
            else {
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    inf = "重置密码成功";
                    Editor databaseData = getSharedPreferences("user", 0).edit();
                    databaseData.putLong("reset_password_time", 0);
                    databaseData.commit();
                    ResetPassword.this.finish();
                }
                else if (status.equals("300")) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String response = analyse.getData(json, "error_desc");
                    inf = response;
                }
                else
                    inf = RESETFAIL;
            }
            if (inf != null) {
                ViewUtil.showTipsToast(ResetPassword.this, inf);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            submit.setText("重置密码中..");
            progress.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open reset password");
        String eventName = "v2 open reset password";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_reset_password";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    private boolean isFirst = true;
}
