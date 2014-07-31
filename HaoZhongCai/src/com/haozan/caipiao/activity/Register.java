package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.CommitInfTask;
import com.haozan.caipiao.task.GetSMSServiceSideNumTask.OnGetServiceSideSMSNumListener;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.error.ExceptionHandler;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.util.security.RSA;
import com.umeng.analytics.MobclickAgent;

public class Register
    extends BasicActivity
    implements OnClickListener, OnGetServiceSideSMSNumListener {
    private static final String RESIGTER_REQUET_SERVICE = "1002011";

    private final static String REGISTERFAIL = "注册失败";
    private final static int NORMAL_REGISTER = 1;
    private final static int AUTO_REGISTER = 2;
    private int registerType = NORMAL_REGISTER;
    private final static int FIRST_TWENTY_SECOND = 1;
    private final static int SECOND_TEN_SECOND = 2;
    private boolean isFirst = true;

    private TextView protocol;
    private ImageButton help;
    private TextView phoneTv;
    private TextView nameTv;
    private TextView idTv;
    private TextView reHelp;

    private Button submit_bt_dlg;
    private Button cancle_bt_dlg;
    private TextView namme_tv_dlg;
    private TextView id_tv_dlg;
    private TextView num_tv_dlg;

    private String phoneNum = "";
    private String passwordNum1;
    private String passwordNum2;

    private Button registerBt;
    private ImageView checkProtocol;

    private EditText phone;
    private EditText password1;
    private EditText password2;

    private Boolean readProtocol = true;
    private LayoutInflater factory;
    private View eventview;
    private ProgressDialog progressDialog;
    private RegisterTask registerTask;
    private ScrollView scrollView;
    // 自动化注册
    private String txtPhoneNumber = null;
    private String simStateString = "NA";
    private String deviceId = null;
    private Button autoRegister;
    private String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    private Intent sentIntent;
    private PendingIntent sentPI;
    private String simOperationName;

    private UEDataAnalyse mUploadRequestTime;
    private String mService;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FIRST_TWENTY_SECOND:
                    registerTask = new RegisterTask();
                    registerTask.execute();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionHandler.register(this);
        setContentView(R.layout.register);
        setupViews();
        init();
    }

    private void setupViews() {
        comfirDialog();
        setupMainViews();
        setupDialogViews();
    }

    private void setupMainViews() {
        help = (ImageButton) this.findViewById(R.id.help);
        help.setOnClickListener(this);

        phone = (EditText) this.findViewById(R.id.register_phone_et);
        password1 = (EditText) this.findViewById(R.id.register_code1_et);
        password2 = (EditText) this.findViewById(R.id.register_code2_et);

        checkProtocol = (ImageView) this.findViewById(R.id.register_check);
        checkProtocol.setOnClickListener(this);
        protocol = (TextView) this.findViewById(R.id.register_protocol);
        protocol.setText(Html.fromHtml("<u>" + "同意注册协议" + "</u>"));
        protocol.setOnClickListener(this);
// reHelp = (ImageButton) findViewById(R.id.help);
// reHelp.setOnClickListener(this);
        scrollView = (ScrollView) this.findViewById(R.id.scroll_view_register);
        autoRegister = (Button) this.findViewById(R.id.auto_register);
        autoRegister.setOnClickListener(this);
    }

    private void setupDialogViews() {
        registerBt = (Button) this.findViewById(R.id.register_submit);
        registerBt.setOnClickListener(this);
    }

    private void comfirDialog() {
        factory = LayoutInflater.from(Register.this);
        eventview = factory.inflate(R.layout.register_make_sure_dlg, null);

        submit_bt_dlg = (Button) eventview.findViewById(R.id.submit_dlg);
        submit_bt_dlg.setOnClickListener(this);
        cancle_bt_dlg = (Button) eventview.findViewById(R.id.cancle_dlg);
        cancle_bt_dlg.setOnClickListener(this);

        phoneTv = (TextView) eventview.findViewById(R.id.dialog_item_first);
        phoneTv.setVisibility(View.VISIBLE);
        setTextBold(phoneTv);
        nameTv = (TextView) eventview.findViewById(R.id.dialog_item_second);
        nameTv.setVisibility(View.VISIBLE);
        setTextBold(nameTv);
        idTv = (TextView) eventview.findViewById(R.id.dialog_item_three);
        idTv.setVisibility(View.VISIBLE);
        setTextBold(idTv);

        namme_tv_dlg = (TextView) eventview.findViewById(R.id.name_tv_dlg);
        namme_tv_dlg.setVisibility(View.VISIBLE);
        id_tv_dlg = (TextView) eventview.findViewById(R.id.id_tv_dlg);
        id_tv_dlg.setVisibility(View.VISIBLE);
        num_tv_dlg = (TextView) eventview.findViewById(R.id.num_tv_dlg);
        num_tv_dlg.setVisibility(View.VISIBLE);
    }

    private void setTextBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

// // 获取本机电话相关信息
// private void getNativePhoneInfo() {
// TelephonyManager phoneMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
// if (phoneMgr.getSimState() == TelephonyManager.SIM_STATE_ABSENT ||
// phoneMgr.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN)
// simStateString = "absent";
// else {
// String operationName = phoneMgr.getSubscriberId();
// if (operationName.startsWith("46003"))// 中国电信
// simOperationName = "CT";
// else if (operationName.startsWith("46000") || operationName.startsWith("46002"))// 中国移动
// simOperationName = "CM";
// else if (operationName.startsWith("46001"))// 中国联通
// simOperationName = "UC";
//
// txtPhoneNumber = phoneMgr.getLine1Number();
// }
// deviceId = "#" + phoneMgr.getDeviceId();
// }
//
// // 获取短信网关
// private void getSmsWanguan() {
// if (HttpConnectUtil.isNetworkAvailable(Register.this)) {
// GetSMSServiceSideNumTask getSMSServiceSideNumTask = new GetSMSServiceSideNumTask(Register.this);
// getSMSServiceSideNumTask.execute("http://download.haozan88.com/publish/constant/?s=" +
// simOperationName);
// getSMSServiceSideNumTask.setOnGetServiceListener(this);
// }
// else
// ViewUtil.showTipsToast(this, noNetTips);
// }
//
// // 处理返回的发送状态
// private void smsReceivedStatus() {
// // register the Broadcast Receivers
// this.registerReceiver(new BroadcastReceiver() {
// @Override
// public void onReceive(Context _context, Intent _intent) {
// switch (getResultCode()) {
// case Activity.RESULT_OK:
// ViewUtil.showTipsToast(Register.this, "成功发送");
// break;
// case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
// break;
// case SmsManager.RESULT_ERROR_RADIO_OFF:
// break;
// case SmsManager.RESULT_ERROR_NULL_PDU:
// break;
// }
// }
// }, new IntentFilter(SENT_SMS_ACTION));
// }

    private void init() {
        sentIntent = new Intent(SENT_SMS_ACTION);
        sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
// String tel = "";
// TelephonyManager tm =
// (TelephonyManager) this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
// tel = tm.getLine1Number();
// if (tel != null && tel.length() == 11)
// phone.setText(tel);
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("注册中，请稍候..");
// // 获取sim卡相关的信息
// getNativePhoneInfo();
// // 如果本地获取到的电话号码为空或不符合规范就调用短信网关来获取用电话号码
// if (!simStateString.equals("absent") && (txtPhoneNumber == null || txtPhoneNumber.equals("")))
// getSmsWanguan();
// else if (!simStateString.equals("absent") &&
// !txtPhoneNumber.matches("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$"))
// getSmsWanguan();
// else if (simStateString.equals("absent"))// 如果手机没有SIM卡，按钮不显示
// autoRegister.setVisibility(View.GONE);
// // 处理返回的发送状态
// smsReceivedStatus();

        mUploadRequestTime = new UEDataAnalyse(this);
    }

    @Override
    public void onClick(View v) {
        isFirst = true;
        if (v.getId() == R.id.register_submit) {
            Boolean checkInf = checkInput();
            if (checkInf == true) {
                if (HttpConnectUtil.isNetworkAvailable(Register.this)) {
                    registerTask = new RegisterTask();
                    registerTask.execute();
                }
                else
                    ViewUtil.showTipsToast(this, noNetTips);

            }
        }
        else if (v.getId() == R.id.register_protocol) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.REGISTER_PROTOCOL_URL);
            bundle.putString("title", "注册协议");
            intent.putExtras(bundle);
            intent.setClass(Register.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.register_check) {
            if (readProtocol) {
                readProtocol = false;
                checkProtocol.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                readProtocol = true;
                checkProtocol.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.help) {
// Intent intent = new Intent();
// Bundle bundel = new Bundle();
// bundel.putInt("help01", R.string.helpinf01);
// bundel.putString("about", "right");
// intent.putExtras(bundel);
// intent.setClass(Register.this, Helpyou.class);
// startActivity(intent);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_RECHARGE_URL + "#register");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Register.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.auto_register) {
            if (txtPhoneNumber != null && HttpConnectUtil.isNetworkAvailable(Register.this)) {
                progressDialog.show();
                registerType = AUTO_REGISTER;
                SmsManager smsManager = SmsManager.getDefault();
                try {
// smsManager.sendTextMessage(txtPhoneNumber, null, deviceId, sentPI, null);
// ViewUtil.showTipsToast(this, "已发送");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessageDelayed(FIRST_TWENTY_SECOND, 20000);
            }
            else
                ViewUtil.showTipsToast(this, "网络异常");
        }
    }

    // check whether the client has input the information and if the information is correct
    private Boolean checkInput() {
        phoneNum = phone.getText().toString();
        passwordNum1 = password1.getText().toString();
        passwordNum2 = password2.getText().toString();

        phoneTv.setText(phoneNum.toString());
        nameTv.setVisibility(View.VISIBLE);
        idTv.setVisibility(View.VISIBLE);
        id_tv_dlg.setVisibility(View.VISIBLE);
        namme_tv_dlg.setVisibility(View.VISIBLE);

        String warning = null;
        if (phoneNum.equals("")) {
            warning = "请输入手机号码";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (phoneNum.length() != 11) {
            warning = "请输入11位手机号码";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!phoneNum.matches("\\d{11}")) {
            warning = "您只能输入数字";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (phoneNum.matches("^(1[0-9]*)$") == false) {
            warning = "你写的手机号码不符合规范，请重新输入！";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.equals("")) {
            warning = "请输入密码";
            password1.requestFocus();
            password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
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
        else if (!passwordNum1.matches("\\d{6,12}")) {
            warning = "您只能输入数字";
            password1.requestFocus();
            password1.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.equals("")) {
            warning = "请再次输入密码";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() < 6) {
            warning = "请输入6-12位数字";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum2.length() > 12) {
            warning = "请输入6-12位数字";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!passwordNum2.matches("\\d{6,12}")) {
            warning = "您只能输入数字";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (passwordNum1.equals(passwordNum2) == false) {
            warning = "两次输入的密码不一致,请重新输入";
            password2.requestFocus();
            password2.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!readProtocol) {
            warning = "请阅读注册协议";
            ViewUtil.showTipsToast(this, warning);
        }
// else if (realNameText.length() == 0 && idText.length() == 0) {
// warning = null;
// nameTv.setVisibility(View.GONE);
// idTv.setVisibility(View.GONE);
// id_tv_dlg.setVisibility(View.GONE);
// namme_tv_dlg.setVisibility(View.GONE);
// }
        if (warning != null)
            return false;
        else
            return true;
    }

    // 普通手动注册
    private HashMap<String, String> initHashMap()
        throws Exception {
        mService = RESIGTER_REQUET_SERVICE;

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", RESIGTER_REQUET_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(Register.this));
        parameter.put("phone", phoneNum);
        parameter.put("password", EncryptUtil.encryptPassword(this, passwordNum1));
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: register action");
        String eventName = "v2 register not all inf";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        return parameter;
    }

    // 全自动一件注册
    private HashMap<String, String> initAutoHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "3002141");
        parameter.put("pid", LotteryUtils.getPid(this));
        parameter.put("phone", phoneNum);
        parameter.put("msg", deviceId);

        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: auto register action");
        String eventName = "v2 register not all inf";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        return parameter;
    }

    // 提交注册成功统计信息
    private void submitStatisticsRegSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: register success");
        String eventName = "v2 register success";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "register_success";
        MobclickAgent.onEvent(Register.this, eventNameMob);
    }

    class RegisterTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            mUploadRequestTime.onConnectStart();

            ConnectService connectNet = new ConnectService(Register.this);
            String json = null;
            try {
                if (registerType == NORMAL_REGISTER)
                    json = connectNet.getJsonPost(3, false, initHashMap());
                else if (registerType == AUTO_REGISTER)
                    json = connectNet.getJsonPost(8, false, initAutoHashMap());

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            registerBt.setEnabled(true);
            registerBt.setText("   注      册   ");
            progressDialog.cancel();
            String inf = null;
            Boolean forward = false;
            if (json == null)
                inf = REGISTERFAIL;
            else {
                JsonAnalyse ja = new JsonAnalyse();
                String status;
                // get the status of the http data
                status = ja.getStatus(json);
                if (status != null) {
                    if (status.equals("200")) {
                        mUploadRequestTime.submitConnectSuccess(mService);

                        appState.setSessionid(ja.getData(json, "sessionid"));
                        appState.setAccount(0);
                        appState.setWinBalance(0);
                        appState.setAvailableBalance(0);
                        appState.setBand(0);
                        appState.setPerfectInf(0);
                        appState.setRegisterType("1");
                        if (phoneNum.length() > 5) {
                            StringBuilder temp = new StringBuilder();
                            temp.append(phoneNum.substring(0, 3));
                            temp.append("****");
                            temp.append(phoneNum.substring(phone.length() - 4));
                            appState.setNickname(temp.toString());
                        }

                        if (registerType == AUTO_REGISTER) {
                            appState.setUsername(ja.getData(json, "phone"));
                        }

                        databaseData.putBoolean("hasAccount", true);
                        databaseData.commit();
                        databaseData.putString("last_topup_way", "");
                        databaseData.commit();

                        CommitInfTask commitInftask = new CommitInfTask(Register.this);
                        commitInftask.execute();
                        initCSMM();
                        submitStatisticsRegSuccess();
                        forward = true;
                    }
                    else if (status.equals("202")) {
                        if (registerType == AUTO_REGISTER)
                            handler.sendEmptyMessageDelayed(FIRST_TWENTY_SECOND, 10000);
                    }
                    else if (status.equals("300")) {
                        inf = ja.getData(json, "error_desc");
                    }
                    else if (status.equals("301")) {
                        if (registerType == AUTO_REGISTER)
                            inf = "该手机号码已注册";
                        else
                            inf = ja.getData(json, "error_desc");
                    }
                    else if (status.equals("303")) {
                        inf = ja.getData(json, "error_desc");
                    }
                    else if (status.equals("400")) {
                        inf = ja.getData(json, "error_desc");
                    }
                    else if (status.equals("500")) {
                        inf = "服务器内部错误";
                    }
                    else {
                        inf = REGISTERFAIL;
                    }
                }
                else {
                    inf = REGISTERFAIL;
                }
            }
            if (forward) {
                initApplication();
// Intent intnet = new Intent();
// Bundle bundle = new Bundle();
// bundle.putInt("origin", 1);
// bundle.putString("commer_class", ".activity.Register");
// intnet.putExtras(bundle);
// intnet.setClass(Register.this, PerfectInf.class);
// startActivity(intnet);
                setResult(RESULT_OK);
                Register.this.finish();
            }
            if (inf != null) {
                mUploadRequestTime.submitConnectFail(mService, inf);
                ViewUtil.showTipsToast(Register.this, inf);
            }
        }

        private void initApplication() {
            appState.setAccount(0);
            appState.setWinBalance(0);
            appState.setAvailableBalance(0);
            appState.setUsername(phone.getText().toString());
            appState.setPerfectInf(0);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (registerType == NORMAL_REGISTER) {
                progressDialog.show();
                registerBt.setEnabled(false);
                registerBt.setText("正在注册");
            }
        }
    }

    private void initCSMM() {
// ICSAPI api = CSAPIFactory.getCSAPI();
// try {
// String username = api.init(this, appState.getUsername());
// api.bindAccount(this, username);
// }
// catch (CSInitialException e) {
// e.printStackTrace();
// }
// catch (CSUnValidateOperationException e) {
// e.printStackTrace();
// }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open register");
        String eventName = "v2 open register";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void submitData() {
        String eventName = "open_register";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onContentPost(String smsNum) {
        txtPhoneNumber = smsNum;
    }
}