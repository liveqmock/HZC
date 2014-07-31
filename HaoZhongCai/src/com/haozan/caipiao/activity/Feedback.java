package com.haozan.caipiao.activity;

// different from haobai
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.GetPluginInfTask;
import com.haozan.caipiao.types.PluginInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.JsonUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.StringUtils;
import com.umeng.analytics.MobclickAgent;

public class Feedback
    extends BasicActivity
    implements OnClickListener {

    private static String FEEDBACKFAIL = "反馈失败";
    private String contactStr;
    private TextView contactInf;
    private TextView qqTv;
    private TextView title;
    private EditText content;
    private Button submit;
    private RelativeLayout myAdvice;
    private RelativeLayout allAdvice;
    private RelativeLayout mmService;
    private RelativeLayout qqServiceRelative;
    private RelativeLayout customServicePhone;
    private ClipboardManager cm;

    // 客服插件信息
    private PluginInf csPluginInf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        csPluginInf = new PluginInf();
    }

    private void setupViews() {
        title = (TextView) this.findViewById(R.id.initName);
        title.setText("意见反馈");
        submit = (Button) this.findViewById(R.id.submit);
        submit.setText("提  交");
        submit.setOnClickListener(this);
        content = (EditText) this.findViewById(R.id.feedback_content);
// content.setMovementMethod(ScrollingMovementMethod.getInstance());
        contactInf = (TextView) this.findViewById(R.id.phone);
        contactStr =
            "客服电话：<u> <font color='blue'>" + LotteryUtils.getConnectionPhone(mContext) + "</color></u>";
        contactInf.setOnClickListener(this);
        contactInf.setText(Html.fromHtml(contactStr));
        qqTv = (TextView) this.findViewById(R.id.custom_service_qq);
        qqTv.setText("客服QQ：" + LotteryUtils.getConnectionQQ(mContext));
        qqTv.setOnClickListener(this);
        myAdvice = (RelativeLayout) findViewById(R.id.my_advice);
        myAdvice.setOnClickListener(this);
        allAdvice = (RelativeLayout) findViewById(R.id.all_advice);
        allAdvice.setOnClickListener(this);
        mmService = (RelativeLayout) findViewById(R.id.online_service);
        mmService.setOnClickListener(this);
        qqServiceRelative = (RelativeLayout) findViewById(R.id.qq_service_relative);
        qqServiceRelative.setOnClickListener(this);
        customServicePhone = (RelativeLayout) findViewById(R.id.custom_service_phone);
        customServicePhone.setOnClickListener(this);
    }

    private void init() {
// Bundle bundle = getIntent().getExtras();
// if (bundle != null) {
// Boolean fromCenter = bundle.getBoolean("from_center");
// if (fromCenter) {
// submit.setVisibility(View.VISIBLE);
// }
// else {
// submit.setVisibility(View.GONE);
// }
// }

        // 获取剪贴板管理服务
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        getClientServerData();
    }

    protected void sending() {
        if (HttpConnectUtil.isNetworkAvailable(Feedback.this)) {
            FeedbackTask task = new FeedbackTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private boolean checkContent() {
        boolean ret = true;
        if (StringUtils.isBlank(content.getText().toString())) {
            Toast.makeText(mContext, "请输入反馈内容，谢谢", Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phone) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + LotteryUtils.getConnectionPhone(mContext)));
            startActivity(intent);
        }
        else if (v.getId() == R.id.submit) {
            Boolean canSubmit = checkContent();
            if (canSubmit) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("zzc_desc", "username [" + appState.getUsername() + "]: click feedback submit");
                FlurryAgent.onEvent("user click feedback submit", map);
                sending();
            }
        }
        else if (v.getId() == R.id.my_advice) {
            if (!checkLogin()) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("forwardFlag", "我的反馈");
                bundle.putBoolean("ifStartSelf", false);
                bundle.putBoolean("if_my_advice", true);
                intent.putExtras(bundle);
// intent.setClass(Feedback.this, StartUp.class);
                intent.setClass(Feedback.this, Login.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("if_my_advice", true);
                intent.putExtras(bundle);
                intent.setClass(Feedback.this, FeedBackList.class);
                startActivity(intent);
            }
        }
        else if (v.getId() == R.id.all_advice) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("if_my_advice", false);
            intent.putExtras(bundle);
            intent.setClass(Feedback.this, FeedBackList.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.online_service) {
// CSAPIFactory.getCSAPI().startCS(Feedback.this, "hall");
            toClientServer();
        }
        else if (v.getId() == R.id.custom_service_qq) {
            // 将文本数据复制到剪贴板
            cm.setText(LotteryUtils.getConnectionQQ(mContext));
            ViewUtil.showTipsToast(this, "您已复制客服qq号码，欢迎给我们提出宝贵意见");
        }
        else if (v.getId() == R.id.qq_service_relative) {
            cm.setText(LotteryUtils.getConnectionQQ(mContext));
            ViewUtil.showTipsToast(this, "您已复制客服qq号码，欢迎给我们提出宝贵意见");
        }
        else if (v.getId() == R.id.custom_service_phone) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
            intent.setData(Uri.parse("tel:" + LotteryUtils.getConnectionPhone(mContext)));
            startActivity(intent);
        }
    }

    private void toClientServer() {
        if (csPluginInf.getGameName() == null) {
            ViewUtil.showTipsToast(this, "检测客服插件失败，请稍后再试");
        }
        else {
            if (PluginUtils.checkGameExist(this, csPluginInf.getGamePackageName())) {
                Bundle bundle = new Bundle();
                bundle.putString("username", appState.getUsername());
                PluginUtils.goPlugin(this, bundle, csPluginInf.getGamePackageName(),
                                     csPluginInf.getGameActivityName());
            }
            else {
                PluginUtils.showPluginDownloadDialog(this, csPluginInf.getGameName(),
                                                     csPluginInf.getGameDescription(),
                                                     csPluginInf.getGameDownloadUrl(), false);
            }
        }
    }

    private void getClientServerData() {
        String csLastData = preferences.getString("plugin_customerservice", null);
        if (csLastData != null) {
            JsonUtil.analysePluginData(csPluginInf, csLastData);
        }
        else {
            if (HttpConnectUtil.isNetworkAvailable(this)) {
                GetPluginInfTask pluginTask = new GetPluginInfTask(this, csPluginInf);
                pluginTask.execute("customerservice");
            }
        }
    }

    // 判断是否登录
    private boolean checkLogin() {
        String userid = appState.getSessionid();
        if (userid == null) {
            return false;
        }
        else {
            return true;
        }
    }

    class FeedbackTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            dismissProgressDialog();
            String inf = null;
            if (json == null) {
                inf = FEEDBACKFAIL;
                ViewUtil.showTipsToast(Feedback.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    inf = "提交成功，感谢您的反馈！";
                    ViewUtil.showTipsToast(Feedback.this, inf);
                    Feedback.this.finish();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(Feedback.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(Feedback.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = FEEDBACKFAIL;
                    ViewUtil.showTipsToast(Feedback.this, inf);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("正在提交...");
        }

        private HashMap<String, String> iniHashMap(String inf) {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1001030");
            parameter.put("pid", LotteryUtils.getPid(Feedback.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("msg_content", HttpConnectUtil.encodeParameter(content.getText().toString()));
            parameter.put("client_info", HttpConnectUtil.encodeParameter(inf));
            return parameter;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String version = LotteryUtils.fullVersion(Feedback.this);
            String infExtra =
                "Product Model:" + android.os.Build.MODEL + ",SDK Version:" +
                    android.os.Build.VERSION.RELEASE + ",versionName:" + version + ",PhoneNum:" +
                    appState.getUsername();
            ConnectService connectNet = new ConnectService(Feedback.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, iniHashMap(infExtra));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

// private void restoreSubmit() {
// submit.setText("提         交");
// submit.setEnabled(true);
// }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open feedback");
        String eventName = "v2 open feedback";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_feedback";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Feedback.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(Feedback.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                              R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}