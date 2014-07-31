package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.umeng.analytics.MobclickAgent;

public class UserInf_ModifyPassword
    extends BasicActivity
    implements OnClickListener, OnGetUserInfListener {
    private TextView title1;
    private TextView title2;
    // 账户信息
    private static final String SEARCHFAIL = "查询失败";
    private Boolean checkShow = false;
    private LinearLayout user_name_linear;
    private LinearLayout user_id_linear;
    private LinearLayout user_phone_linear;
    private TextView name;
    private TextView id;
    private TextView lastLoginTime;
    private TextView centerMessage;
    private TextView perfectInfTips;
    private TextView idVeifyInf;
    private TextView userPhoneTv;
    private Button bindPhoneBtn;
    private Button perfectInfBt;
    private EditText lastIdNum;
    private Button checkId;
    private Button submitCheck;
    private RelativeLayout checkIdLayout;
    private ProgressBar progress;
    private String lastLogin;
    private ScrollView scrollView1;
    private LinearLayout selectLin;

    // 修改密码
    private final static String FAIL = "修改密码失败";

    private String oldPassworNum;
    private String passwordNum1;
    private String passwordNum2;

    private Button submit;
    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;
    private ProgressDialog progressDialog;
    private ScrollView scrollView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinf_modifypassword);
        setupViews();
        initViews();
        init();
    }

    private void initViews() {
        String registerType = appState.getRegisterType();
        if (!"1".equals(registerType)) {
            selectLin.setVisibility(View.GONE);// 隐藏按钮，使不能修改密码
            String reservedPhone = appState.getReservedPhone();
            if ("".equals(reservedPhone)) {
                bindPhoneBtn.setText("绑    定");
                userPhoneTv.setVisibility(View.GONE);
            }
            else {
                userPhoneTv.setVisibility(View.VISIBLE);
                userPhoneTv.setText(reservedPhone);
                bindPhoneBtn.setText("更换绑定手机");
            }
        }
        else {
            user_phone_linear.setVisibility(View.GONE);
        }

    }

    private void setupViews() {
        setupCheckIdViews();
        setupWholeViews();
    }

    private void setupCheckIdViews() {
        checkIdLayout = (RelativeLayout) this.findViewById(R.id.check_id_layout);
        lastIdNum = (EditText) this.findViewById(R.id.id_card_last);
        checkId = (Button) this.findViewById(R.id.check_id_card);
        checkId.setOnClickListener(this);
        submitCheck = (Button) this.findViewById(R.id.submit_check);
        submitCheck.setOnClickListener(this);
        idVeifyInf = (TextView) findViewById(R.id.id_verify_inf);
    }

    private void setupWholeViews() {
        title1 = (TextView) this.findViewById(R.id.title_left);
        title1.setOnClickListener(this);
        title2 = (TextView) this.findViewById(R.id.title_right);
        title2.setOnClickListener(this);
        selectLin = (LinearLayout) findViewById(R.id.ll_select);
        // 身份信息
        user_name_linear = (LinearLayout) findViewById(R.id.user_name_linear);
        user_id_linear = (LinearLayout) findViewById(R.id.user_id_linear);
        user_phone_linear = (LinearLayout) findViewById(R.id.user_phone_linear);
        name = (TextView) this.findViewById(R.id.user_name);
        id = (TextView) this.findViewById(R.id.user_id);
        lastLoginTime = (TextView) this.findViewById(R.id.last_login);
        centerMessage = (TextView) this.findViewById(R.id.message);
        perfectInfTips = (TextView) findViewById(R.id.to_perfect_inf_tips);
        userPhoneTv = (TextView) findViewById(R.id.user_phone);
        bindPhoneBtn = (Button) findViewById(R.id.btn_bind_phone);
        bindPhoneBtn.setOnClickListener(this);
        setDefaultView();

        perfectInfBt = (Button) findViewById(R.id.perfect_inf);
        perfectInfBt.setOnClickListener(this);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        scrollView1 = (ScrollView) findViewById(R.id.scroll1);

        // 修改密码
        oldPassword = (EditText) this.findViewById(R.id.old_password);
        newPassword1 = (EditText) this.findViewById(R.id.new_password1);
        newPassword2 = (EditText) this.findViewById(R.id.new_password2);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        scrollView2 = (ScrollView) findViewById(R.id.scroll2);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("修改密码提交中...");

        if (HttpConnectUtil.isNetworkAvailable(UserInf_ModifyPassword.this)) {
            UserInfTask getUserInf = new UserInfTask(UserInf_ModifyPassword.this);
            getUserInf.setOnGetUserInfListener(this);
            getUserInf.execute(1);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            disappearContent(inf);
        }

        scrollView1.setVisibility(View.VISIBLE);
        scrollView2.setVisibility(View.GONE);

        String registerType = appState.getRegisterType();
        if (!"1".equals(registerType)) {
            user_phone_linear.setVisibility(View.VISIBLE);
        }
        else {
            user_phone_linear.setVisibility(View.GONE);
        }
    }

    private void setDefaultView() {
        name.setText("正在查询中...");
        lastLoginTime.setText("正在查询中...");
        userPhoneTv.setText("正在查询中...");
        id.setText("正在查询中...");
        SharedPreferences preferences = getSharedPreferences("user", 0);
        lastLogin = preferences.getString("lastLogin", null);
        if (lastLogin != null)
            lastLoginTime.setText(lastLogin);
    }

    // when fail to connect to the server,disappear some view
    public void disappearContent(String inf) {
        centerMessage.setVisibility(View.VISIBLE);
        centerMessage.setText(inf);
        name.setVisibility(View.GONE);
        user_name_linear.setVisibility(View.GONE);
        lastLoginTime.setVisibility(View.GONE);
        id.setVisibility(View.GONE);
        user_id_linear.setVisibility(View.GONE);
        perfectInfBt.setVisibility(View.GONE);
        perfectInfTips.setVisibility(View.GONE);
    }

    public void searchFail() {
        name.setText("数据查询不成功");
        id.setText("数据查询不成功");
        lastLoginTime.setText("数据查询不成功");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open user inf");
        String eventName = "v2 open user inf";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.check_id_card) {
            if (!checkShow) {
                lastIdNum.setVisibility(View.VISIBLE);
                submitCheck.setVisibility(View.VISIBLE);
                idVeifyInf.setVisibility(View.GONE);
                checkId.setText("隐藏验证");
                checkShow = true;
            }
            else {
                lastIdNum.setVisibility(View.GONE);
                submitCheck.setVisibility(View.GONE);
                idVeifyInf.setVisibility(View.VISIBLE);
                checkId.setText("核实身份证号");
                checkShow = false;
            }
        }
        else if (view.getId() == R.id.submit_check) {
            if (checkInput()) {
                if (HttpConnectUtil.isNetworkAvailable(UserInf_ModifyPassword.this)) {
                    CheckIdCardTask checkIdCard = new CheckIdCardTask();
                    checkIdCard.execute();
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
        }
        else if (view.getId() == R.id.perfect_inf) {
            Intent intnet = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("origin", 2);
            bundle.putString("commer_class", ".activity.UserInf_ModifyPassword");
            intnet.putExtras(bundle);
            intnet.setClass(UserInf_ModifyPassword.this, PerfectInf.class);
            startActivity(intnet);
            UserInf_ModifyPassword.this.finish();
        }
        else if (view.getId() == R.id.submit) {
            Boolean checkInf = checkInputPwd();
            if (checkInf == true) {
                ModifyPasswordTask submitTask = new ModifyPasswordTask();
                submitTask.execute();
            }
            final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
        }
        else if (view.getId() == R.id.title_left) {
            String eventName = "open_user_inf";
            MobclickAgent.onEvent(this, eventName);
            besttoneEventCommint(eventName);
            scrollView1.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.GONE);
            title1.setBackgroundResource(R.drawable.btn_tab_left_pressed);
            title2.setBackgroundResource(R.drawable.btn_tab_right_normal);
            final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
        }
        else if (view.getId() == R.id.title_right) {
            String eventName = "open_modify_password";
            MobclickAgent.onEvent(this, eventName);
            besttoneEventCommint(eventName);
            scrollView1.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
            title1.setBackgroundResource(R.drawable.btn_tab_left_normal);
            title2.setBackgroundResource(R.drawable.btn_tab_right_pressed);
            final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
        }
        // TODO
        else if (view.getId() == R.id.btn_bind_phone) {
            Intent intnet = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("origin", 3);
            bundle.putString("commer_class", ".activity.UserInf_ModifyPassword");
            intnet.putExtras(bundle);
            intnet.setClass(UserInf_ModifyPassword.this, PerfectInf.class);
            startActivity(intnet);
            UserInf_ModifyPassword.this.finish();
        }
    }

    private Boolean checkInputPwd() {
        oldPassworNum = oldPassword.getText().toString();
        passwordNum1 = newPassword1.getText().toString();
        passwordNum2 = newPassword2.getText().toString();
        String warning = null;
        if (oldPassworNum.equals("")) {
            warning = "请输入旧密码";
            oldPassword.requestFocus();
            oldPassword.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (oldPassworNum.length() < 6) {
            warning = "旧密码请输入6-12位数字";
            oldPassword.requestFocus();
            oldPassword.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (oldPassworNum.length() > 12) {
            warning = "旧密码请输入6-12位数字";
            oldPassword.requestFocus();
            oldPassword.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!oldPassworNum.matches("\\d{6,12}")) {
            warning = "您只能输入数字";
            oldPassword.requestFocus();
            oldPassword.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.equals("")) {
            warning = "请输入新密码";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.length() < 6) {
            warning = "第一个密码请输入6-12位数字";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.length() > 12) {
            warning = "第一个密码请输入6-12位数字";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!passwordNum1.matches("\\d{6,12}")) {
            warning = "您只能输入数字";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.equals("")) {
            warning = "请再次输入新密码";
            newPassword2.requestFocus();
            newPassword2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() > 12) {
            warning = "第二个密码请输入6-12位数字";
            newPassword2.requestFocus();
            newPassword2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() < 6) {
            warning = "第二个密码请输入6-12位数字";
            newPassword2.requestFocus();
            newPassword2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!passwordNum2.matches("\\d{6,12}")) {
            warning = "您只能输入数字";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.equals(passwordNum2) == false) {
            warning = "两次输入的密码不一致,请重新输入";
            newPassword1.requestFocus();
            newPassword1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        else
            return true;
    }

    private boolean checkInput() {
        if (lastIdNum.getText().toString().length() == 0) {
            ViewUtil.showTipsToast(this, "请输入身份证后六位");
            return false;
        }
        else if (lastIdNum.getText().toString().length() < 6) {
            ViewUtil.showTipsToast(this, "输入验证身份证号码不足6位");
            return false;
        }
        else
            return true;
    }

    class ModifyPasswordTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(UserInf_ModifyPassword.this);
            String json = null;
            try {
                json = connectNet.getJsonPost(3, true, initHashMapPwd());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private HashMap<String, String> initHashMapPwd()
            throws Exception {
            LotteryApp appState = ((LotteryApp) UserInf_ModifyPassword.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1002041");
            parameter.put("pid", LotteryUtils.getPid(UserInf_ModifyPassword.this));
            parameter.put("password", encryptOldPassWord());
            parameter.put("new_password", encryptNewPassWord());
            parameter.put("type", "1");
            parameter.put("phone", phone);
            return parameter;
        }

        private String md5Encrypt(String passWord01) {
            return EncryptUtil.MD5Encrypt(passWord01);
        }

        private String encryptOldPassWord()
            throws Exception {
            return EncryptUtil.encryptString(UserInf_ModifyPassword.this, oldPassworNum);
        }

        private String encryptNewPassWord()
            throws Exception {
            return EncryptUtil.encryptString(UserInf_ModifyPassword.this, passwordNum1);
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            submit.setEnabled(true);
            submit.setText("修改密码");
            progressDialog.cancel();
            String inf = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    inf = "修改密码成功";
                    ViewUtil.showTipsToast(UserInf_ModifyPassword.this, inf);
                    clearLoginPassword();
                    finish();
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(UserInf_ModifyPassword.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                    R.anim.push_to_left_out);
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                    inf = "密码修改不成功。" + getResources().getString(R.string.login_timeout);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                    inf = "密码修改不成功。" + getResources().getString(R.string.login_again);
                    showLoginAgainDialog(inf);
                }
                else {
                    inf = "旧密码输入错误";
                }
            }
            else {
                inf = FAIL;
            }
            ViewUtil.showTipsToast(UserInf_ModifyPassword.this, inf);
        }

        private void clearLoginPassword() {
            Editor databaseData = getSharedPreferences("user", 0).edit();
            databaseData.putString("userpassword", null);
            databaseData.commit();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            submit.setText("提交中...");
            progressDialog.show();
        }
    }

    class CheckIdCardTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPostExecute(String result) {
            progress.setVisibility(View.GONE);
            submitCheck.setEnabled(true);
            submitCheck.setText("验证");
            if (result != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(UserInf_ModifyPassword.this, "身份证后六位验证成功");
                }
                else if (status.equals("202")) {
                    ViewUtil.showTipsToast(UserInf_ModifyPassword.this, "您还没填写身份证信息");
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(UserInf_ModifyPassword.this, "身份证后六位验证错误");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(UserInf_ModifyPassword.this, "验证身份证后六位失败");
                }
            }
            else {
                ViewUtil.showTipsToast(UserInf_ModifyPassword.this, "验证身份证后六位失败");
            }
        }

        @Override
        protected String doInBackground(Void... kind) {
            ConnectService connectNet = new ConnectService(UserInf_ModifyPassword.this);
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
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            submitCheck.setEnabled(false);
            submitCheck.setText("验证中..");
        }
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) UserInf_ModifyPassword.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1002061");
        parameter.put("pid", LotteryUtils.getPid(UserInf_ModifyPassword.this));
        parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
        parameter.put("idcard", lastIdNum.getText().toString());
        return parameter;
    }

    @Override
    public void onPost(String result) {
        String inf;
        if (result == null) {
            searchFail();
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(result);
            if (status.equals("200")) {
                String data = analyse.getData(result, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    name.setText(jo.getString("name"));
                    id.setText(jo.getString("idcard"));
                    if (jo.getString("name").equals("null") || !"1".equals(appState.getRegisterType()) &&
                        "".equals(appState.getReservedPhone())) {
                        checkIdLayout.setVisibility(View.GONE);
                        perfectInfBt.setVisibility(View.VISIBLE);
                        perfectInfTips.setVisibility(View.VISIBLE);
                        name.setVisibility(View.GONE);
                        user_name_linear.setVisibility(View.GONE);
                        user_phone_linear.setVisibility(View.GONE);
                        id.setVisibility(View.GONE);
                        user_id_linear.setVisibility(View.GONE);
                        idVeifyInf.setVisibility(View.GONE);
                        if (lastLogin != null)
                            lastLoginTime.setVisibility(View.VISIBLE);
                    }
                    else {
                        checkIdLayout.setVisibility(View.VISIBLE);
                        idVeifyInf.setVisibility(View.VISIBLE);
                        if (lastLogin != null)
                            lastLoginTime.setVisibility(View.VISIBLE);
                        else
                            lastLoginTime.setVisibility(View.GONE);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    inf = SEARCHFAIL;
                    disappearContent(inf);
                }
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(UserInf_ModifyPassword.this);
                showLoginAgainDialog(getResources().getString(R.string.login_again));
            }
            else {
                searchFail();
            }
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onPre() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void submitData() {
        String eventName = "open_user_inf";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}