package com.haozan.caipiao.activity.topup;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.control.topup.TopupBasic;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.SecurePaymentHelper;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.alipayfastlogin.BaseHelper;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.shengpay.smk.SMKHelper;
import com.umeng.analytics.MobclickAgent;

public class SNDARecharge
    extends BasicActivity
    implements OnClickListener, PopMenuButtonClickListener {
    private boolean isChecked = false;

    private String[] alipayMoney = {"20元", "30元", "50元", "100元", "200元", "300元", "500元", "1000元"};
    private String[] alipayToServer = {"20", "30", "50", "100", "200", "300", "500", "1000"};

    private TextView contactInf;
    private String contactStr;
    private String orderNo = null;
    private String orderTime = null;
    private String callUrl = null;
    private String rechargeMoney;
    private LinearLayout spinner;
    private TextView spinnerText;
    private ImageView spinnerIcon;
// private TextView rechargeNum;
    private TextView title;
    private TextView tip;
    private Button submit;
    private TextView contact;

    private ProgressDialog mProgress = null;

    private String senderId;
    private String key;
    // 充值额菜单
    private PopMenu titlePopup;
    private int screenWidth;
    private int money_index_num = 0;
    private boolean fromFast = false;

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private TopupBasic mTopupControl;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 1: {
                        closeProgress();
                    }
                        break;
                }

                super.handleMessage(msg);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Boolean from = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_snda);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        mTopupControl = new TopupBasic(this);
    }

    private void init() {
        rechargeMoney = alipayToServer[0];
        // 获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        contact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));
        restoreSubmit();
// rechargeNum.setText(appState.getUsername());
        title.setText("盛付通支付");
        tip.setText(R.string.snda_tip);
        if (this.getIntent().getExtras() != null) {
            from = this.getIntent().getExtras().getBoolean("from");
            money_index_num = this.getIntent().getExtras().getInt("money_index_num");
            fromFast = this.getIntent().getExtras().getBoolean("from_fast");
            rechargeMoney = alipayToServer[money_index_num];
        }
        spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));

        if (Build.MODEL.equals("PM700-c3") ||
            Build.MODEL.equals("Android for Telechips TCC8900 Evaluation Board")) {
            contactStr = "2.盛付通客服电话：" + "4007208888。";
        }
        else {
            contactStr = "2.盛付通客服电话：<u> <font color='blue'>" + "4007208888" + "</color></u>。";
            contactInf.setOnClickListener(this);
        }
        contactInf.setText(Html.fromHtml(contactStr));
        if (fromFast) {
            executeSNDARechargeTask();
        }
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int last_index) {
        titlePopup = new PopMenu(SNDARecharge.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1, screenWidth - 20, last_index, false,
                             true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - spinner.getWidth() + spinner.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    private void restoreSubmit() {
        submit.setText("充        值");
        submit.setEnabled(true);
    }

    private void setupViews() {
        contact = (TextView) this.findViewById(R.id.contact);
        contact.setOnClickListener(this);
        spinner = (LinearLayout) this.findViewById(R.id.snda_spinner);
        spinner.setOnClickListener(this);
        spinnerText = (TextView) this.findViewById(R.id.snda_spinner_text);
        spinnerIcon = (ImageView) this.findViewById(R.id.snda_spinner_icon);

// rechargeNum = (TextView) this.findViewById(R.id.recharge_num);
        title = (TextView) this.findViewById(R.id.title);
        tip = (TextView) this.findViewById(R.id.tip);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        contactInf = (TextView) this.findViewById(R.id.call_snda);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit) {
            executeSNDARechargeTask();
        }
        else if (v.getId() == R.id.call_snda) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:4007208888"));
            startActivity(intent);
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.snda_spinner) {
            showPopupViews(spinner, alipayMoney, money_index_num);
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    public void executeSNDARechargeTask() {
        if (HttpConnectUtil.isNetworkAvailable(SNDARecharge.this)) {
            databaseData.putString("last_topup_way", SNDARecharge.class.getName());
            databaseData.putBoolean("recharge_yy", false);
            databaseData.commit();
            SNDARechargeTask task = new SNDARechargeTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private void pay() {
        if (!isChecked) {
            if (!SMKHelper.getInstance(this).detectSmkExist()) {
                return;
            }
            else {
                isChecked = true;
            }
        }

        try {
            // prepare the order info.
            String orderInfo = getOrderInfo();

            // start the pay.
            SecurePaymentHelper msp = new SecurePaymentHelper();
            boolean bRet = msp.pay(orderInfo, mHandler, 1, this);

            if (bRet) {
                closeProgress();
                mProgress = BaseHelper.showProgress(this, null, "支付中...", false, true);
            }
        }
        catch (Exception ex) {

        }
    }

    private static final String SMK_PACKAGE_NAME = "com.shengpay.smk.plugin";

    private boolean isInstalled() {
        PackageManager pm = getPackageManager();
        for (PackageInfo ai : pm.getInstalledPackages(0)) {
            if (SMK_PACKAGE_NAME.equals(ai.packageName)) {
                return true;
            }
        }
        return false;
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user top up snda success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user top up success");
        String eventName = "top up success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", "snda");
        map1.put("money", rechargeMoney);
        MobclickAgent.onEvent(SNDARecharge.this, eventNameMob, map1);
        besttoneEventCommint(eventNameMob);
    }

    class SNDARechargeTask
        extends AsyncTask<Integer, Object, String> {

        @Override
        protected String doInBackground(Integer... params) {
            ConnectService connectNet = new ConnectService(SNDARecharge.this);
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
// progressDialog.dismiss();
            restoreSubmit();
            String inf = null;
            if (result == null) {
                inf = "网络连接不稳定，无法确认充值是否成功，请查看消息以确认";
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String response = analyse.getData(result, "response_data");
                    orderTime = analyse.getData(response, "trans_time");
                    callUrl = analyse.getData(response, "call_url");
                    orderNo = analyse.getData(response, "order_no");
                    senderId = analyse.getData(response, "mid");
                    key = analyse.getData(response, "key");
                    if (orderTime != null && callUrl != null && orderNo != null && senderId != null &&
                        key != null) {
                        submitStatisticsTopupSuccess();
                        if (isInstalled()) {
                            pay();
                        }
                        else {
                            showDownloadPluginDialog();
                        }
                    }
                    else
                        inf = "充值失败";
// showSuccessDialog(inf);
                    setResult(RESULT_OK);
                }
                else if (status.equals("300")) {
                    inf = analyse.getData(result, "error_desc");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(SNDARecharge.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(SNDARecharge.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = "充值失败";
                }
            }
            if (inf != null) {
                ViewUtil.showTipsToast(SNDARecharge.this, inf);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setText("正在充值...");
            submit.setEnabled(false);
// progressDialog.show();
        }
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005051");
        parameter.put("pid", LotteryUtils.getPid(SNDARecharge.this));
        parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
        parameter.put("pay_type", "SNDA");
        parameter.put("money", rechargeMoney);
        return parameter;
    }

    public void showDownloadPluginDialog() {
        CustomDialog dlgSuccess = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setMessage("需要下载盛付通充值插件，确定下载？").setPositiveButton("确  定",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int which) {
                                                                                dialog.dismiss();
                                                                                pay();
                                                                            }
                                                                        }).setNegativeButton("取  消",
                                                                                             new DialogInterface.OnClickListener() {
                                                                                                 public void onClick(DialogInterface dialog,
                                                                                                                     int which) {
                                                                                                     dialog.dismiss();
                                                                                                 }
                                                                                             });
        dlgSuccess = customBuilder.create();
        dlgSuccess.show();
    }

    String getOrderInfo()
        throws JSONException {

        String transNo = getTradeNo();
        String sendTime = getSendTime();

        JSONObject request = new JSONObject();

        JSONObject header = new JSONObject();
        // fixed parameters固定信息
        JSONObject service = new JSONObject();
        service.put("serviceCode", "B2CPayment");
        service.put("version", "V4.1.1.1.1");
        header.put("service", service);
        header.put("charset", "UTF-8");
        // from merchant server接入商户给出
        JSONObject sender = new JSONObject();
        sender.put("senderId", senderId);
        header.put("sender", sender);
        header.put("traceNo", orderNo);
        header.put("sendTime", sendTime);
        request.put("header", header);

        request.put("orderNo", orderNo);
        request.put("orderAmount", rechargeMoney);
        request.put("orderTime", orderTime);
        // show payment successfully information just for the payment of network
        // bank 网银支付时跳转的支付信息页面
        request.put("pageUrl", callUrl);
        // notify merchant server based on the url 后台服务器通知商户服务器地址
        request.put("notifyUrl", callUrl);
        request.put("productName", "彩票");
        request.put("productDesc", getString(R.string.lottery_name));

        String ipAddress = getLocalIpAddress();
        if (ipAddress == null) {
            // 输入从商户服务器获得手机客户端的IP地址
            ipAddress = "127.0.0.1";
        }
        request.put("buyerIp", ipAddress);

        JSONObject extension = new JSONObject();
        extension.put("ext1", "PRE");
        extension.put("ext2", orderTime);
        request.put("extension", extension);

        JSONObject signature = new JSONObject();
        signature.put("signType", "MD5");
        // 商户服务器进行签名
        String signMsg = getSignMsg(request, "MD5");
        signature.put("signMsg", signMsg);

        request.put("signature", signature);

        JSONObject json = new JSONObject();
        json.put("receB2COrderRequest", request);

        return json.toString();
    }

    private String getSignMsg(JSONObject request, String signType)
        throws JSONException {
        StringBuilder sb = new StringBuilder();

        JSONObject header = request.getJSONObject("header");
        sb.append(header.getJSONObject("service").getString("serviceCode")).append(header.getJSONObject("service").getString("version")).append(header.getString("charset")).append(header.getString("traceNo")).append(header.getJSONObject("sender").getString("senderId")).append(header.getString("sendTime"));

        sb.append(request.getString("orderNo")).append(request.getString("orderAmount")).append(request.getString("orderTime")).append(request.getString("pageUrl")).append(request.getString("notifyUrl")).append(request.getString("productName")).append(request.getString("productDesc")).append(request.getString("buyerIp"));

        JSONObject extension = request.getJSONObject("extension");
        sb.append(extension.getString("ext1")).append(extension.getString("ext2"));

        sb.append(signType).append(key);

        return EncryptUtil.MD5Encrypt(sb.toString());
    }

    String getSendTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    String getTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
        String strKey = format.format(date);
        java.util.Random r = new java.util.Random();
        strKey = strKey + r.nextInt();
        strKey = strKey.substring(0, 15);
        return strKey;
    }

    void closeProgress() {
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

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex) {

        }
        return null;
    }

    class CheckSmkTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress = BaseHelper.showProgress(SNDARecharge.this, null, "支付中...", false, true);
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open snda recharge");
        String eventName = "v2 open snda recharge";
        FlurryAgent.onEvent(eventName, map);
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("inf", "open snda top up");
        map1.put("more_inf", "username [" + appState.getUsername() + "]: open top up");
        String eventName1 = "open top up";
        FlurryAgent.onEvent(eventName1, map1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Boolean success = b.getBoolean("addsuccess");
            if (!success) {
                SNDARecharge.this.finish();
            }
        }
        else if (requestCode == 16 && resultCode == 25) {
            SNDARecharge.this.finish();
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_snda_topup";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
        String eventName1 = "open_topup";
        MobclickAgent.onEvent(this, eventName1, "snda");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SNDARecharge.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        rechargeMoney = alipayToServer[index];
        spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        titlePopup.dismiss();
        money_index_num = index;
    }
}
