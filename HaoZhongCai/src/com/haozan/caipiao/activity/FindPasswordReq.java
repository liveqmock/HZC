package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.Domain;
import com.haozan.caipiao.R;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.umeng.analytics.MobclickAgent;

public class FindPasswordReq
    extends BasicActivity
    implements OnClickListener {

    private static final String SUBMITFAIL = "提交失败";

    private String phoneNum = null;
    private EditText phone;
    private Button submit;
    private ProgressDialog progress;
    private ScrollView scrollView;
    private SharedPreferences preferences;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password_req);
        setupViews();
        init();
    }

    private void setupViews() {
        phone = (EditText) this.findViewById(R.id.phone_num);
        submit = (Button) this.findViewById(R.id.get_verification_code);
        submit.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_find_pw);
    }

    private void init() {
        progress = new ProgressDialog(this);
        progress.setMessage("申请重置密码提交中...");
        preferences = getSharedPreferences("user", 0);
        phoneNum = preferences.getString("username", null);
        if (phoneNum != null)
            phone.setText(phoneNum);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.get_verification_code) {
            if (checkInput() == true) {
                if (HttpConnectUtil.isNetworkAvailable(FindPasswordReq.this)) {
                    FindPasswordReqTask submitTask = new FindPasswordReqTask();
                    submitTask.execute();
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
        }
    }

    // check whether the client has input the information and if the information is correct
    private Boolean checkInput() {
        String warning = null;
        phoneNum = phone.getText().toString();
        if (phoneNum == null || phoneNum.equals("")) {
            warning = "请输入手机号码";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (phoneNum.length() < 11) {
            warning = "请输入11位手机号码";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (phoneNum.length() > 11) {
            warning = "请输入11位手机号码";
            phone.requestFocus();
            phone.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (warning != null) {
// ViewUtil.showTipsToast(this,warning);
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open find password request");
        String eventName = "v2 open find password request";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    class FindPasswordReqTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            String time = appState.getTime();
            if (time == null) {
                GetServerTime getServerTime = new GetServerTime(FindPasswordReq.this);
                time = getServerTime.getFormatTime();
                OperateInfUtils.refreshTime(FindPasswordReq.this, time);
            }
            StringBuilder parameter = new StringBuilder();
            parameter.append(Domain.getHTTPURL(FindPasswordReq.this));
            parameter.append("services/secury_gateway?service=get_image_url&pid=" +
                LotteryUtils.getPid(FindPasswordReq.this) + "&phone=");
            parameter.append(HttpConnectUtil.encodeParameter(phoneNum));
            parameter.append("&timestamp=" + time);
            parameter.append("&sign=");
            String code = EncryptUtil.MD5Encrypt(time + LotteryUtils.getKey(FindPasswordReq.this));
            parameter.append(code);
            AndroidHttpClient client = new AndroidHttpClient(FindPasswordReq.this);
            String json = client.get(parameter.toString());
            parameter = null;
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            submit.setEnabled(true);
            submit.setText("开始重置密码");
            progress.cancel();
            String inf = null;
            if (json == null)
                inf = SUBMITFAIL;
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                // get the status of the http data
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
// String response = analyse.getData(json, "response_data");
// String iconSite = analyse.getData(response, "auth_code_site");
// String sessionId = analyse.getData(response, "sessionid");
                    String iconSite = analyse.getData(json, "image_url");
                    String sessionId = analyse.getData(json, "sessionid");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phoneNum);
                    bundle.putString("site", iconSite);
                    bundle.putString("session_id", sessionId);
                    intent.putExtras(bundle);
                    intent.setClass(FindPasswordReq.this, GetMessageAuthCodeReq.class);
                    startActivityForResult(intent, 0);
                }
                else if (status.equals("300")) {
                    String response = analyse.getData(json, "error_desc");
                    inf = response;
                }
                else
                    inf = SUBMITFAIL;
            }
            if (inf != null) {
                ViewUtil.showTipsToast(FindPasswordReq.this, inf);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            submit.setText("提交申请中..");
            progress.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_findpasword_request";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FindPasswordReq.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(FindPasswordReq.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
