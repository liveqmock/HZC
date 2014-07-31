package com.haozan.caipiao.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class GetMessageAuthCodeReq
    extends BasicActivity
    implements OnClickListener {

    private static final String SUBMITFAIL = "提交失败";

    private String phoneNum = null;
    private String site = null;
    private String sessionId = null;
    private String authNum = null;
    private EditText authCode;
    private Button submit;
    private ImageView image;
    private TextView phone;
    private TextView refreshInf;
    private LinearLayout layout;

    private Bitmap bitmap;
    private ProgressDialog progress;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_message_auth_code_req);
        setupViews();
        init();
    }

    private void setupViews() {
        phone = (TextView) this.findViewById(R.id.phone);
        authCode = (EditText) this.findViewById(R.id.auth_code);
        refreshInf = (TextView) this.findViewById(R.id.refresh_inf);
        image = (ImageView) this.findViewById(R.id.image);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        layout = (LinearLayout) this.findViewById(R.id.org_auth_code_layout);
        layout.setOnClickListener(this);
    }

    private void init() {
        progress = new ProgressDialog(this);
        progress.setMessage("短信验证码生成中...");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNum = bundle.getString("phone");
            site = bundle.getString("site");
            sessionId = bundle.getString("session_id");
        }
        if (phoneNum != null)
            phone.setText(phoneNum);
        if (site != null) {
            GetPicTask getPic = new GetPicTask();
            getPic.execute(site);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit) {
            if (checkInput() == true) {
                if (HttpConnectUtil.isNetworkAvailable(GetMessageAuthCodeReq.this)) {
                    GetMessageAuthTask submitTask = new GetMessageAuthTask();
                    submitTask.execute();
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
        }
        else if (v.getId() == R.id.org_auth_code_layout) {
            GetPicTask getPic = new GetPicTask();
            getPic.execute(site);
        }
    }

    // check whether the client has input the information and if the information is correct
    private Boolean checkInput() {
        String warning = null;
        authNum = authCode.getText().toString();
        if (authNum.equals("")) {
            warning = "请输入四位验证码";
            authCode.requestFocus();
            authCode.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (authNum.length() < 4) {
            warning = "验证码输入不足4位";
            authCode.requestFocus();
            authCode.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
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
        map.put("inf", "username [" + appState.getUsername() + "]: open find password submit auth code");
        String eventName = "v2 open find password submit auth code";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    class GetMessageAuthTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            String time = appState.getTime();
            if (time == null) {
                GetServerTime getServerTime = new GetServerTime(GetMessageAuthCodeReq.this);
                time = getServerTime.getFormatTime();
                OperateInfUtils.refreshTime(GetMessageAuthCodeReq.this, time);
            }
            StringBuilder parameter = new StringBuilder();
            parameter.append(Domain.getHTTPURL(GetMessageAuthCodeReq.this));
            parameter.append("secury_gateways;jsessionid=" + sessionId);
            parameter.append("?service=2002101&pid=" + LotteryUtils.getPid(GetMessageAuthCodeReq.this) +
                "&phone=");
            parameter.append(HttpConnectUtil.encodeParameter(phoneNum));
            if (authNum != null || authNum.equals("")) {
                parameter.append("&autocode=");
                parameter.append(HttpConnectUtil.encodeParameter(authNum));
            }
// parameter.append("&type=1");
            parameter.append("&timestamp=" + time);
            parameter.append("&sign=");
            String code =
                EncryptUtil.MD5Encrypt(phoneNum + "2002101" + time +
                    LotteryUtils.getKey(GetMessageAuthCodeReq.this));
            parameter.append(code);
            AndroidHttpClient client = new AndroidHttpClient(GetMessageAuthCodeReq.this);
            String json = client.get(parameter.toString());
            parameter = null;
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            submit.setEnabled(true);
            submit.setText("获取短信验证码");
            progress.cancel();
            String inf = null;
            if (json == null)
                inf = SUBMITFAIL;
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                // get the status of the http data
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String perfectInf = analyse.getData(json, "integrity");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phoneNum);
                    bundle.putString("per_inf", perfectInf);
                    bundle.putString("session_id", sessionId);
                    intent.putExtras(bundle);
                    intent.setClass(GetMessageAuthCodeReq.this, ResetPassword.class);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                }
                else if (status.equals("300")) {
                    String response = analyse.getData(json, "error_desc");
                    inf = response;
                }
                else
                    inf = SUBMITFAIL;
            }
            if (inf != null) {
                ViewUtil.showTipsToast(GetMessageAuthCodeReq.this, inf);
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

    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(this);
        HttpClient httpClient = client.getDefaultHttpClient();
        HttpPost httpPost = client.getHttpPost(url, parameter);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    entity.consumeContent();
                }
            }
        }
        catch (IOException ex) {
            httpPost.abort();
        }
        if (inputStream != null) {
            return inputStream;
        }
        else
            return null;
    }

    class GetPicTask
        extends AsyncTask<String, Long, byte[]> {

        @Override
        protected void onPostExecute(byte[] data) {
            refreshInf.setText("点击刷新");
            layout.setEnabled(true);
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                image.setImageBitmap(bitmap);// display image
            }
            else
                Toast.makeText(GetMessageAuthCodeReq.this, "获取验证码图片失败!", 1).show();
        }

        @Override
        protected void onPreExecute() {
            layout.setEnabled(false);
            refreshInf.setText("验证码刷新中..");
        }

        @Override
        protected byte[] doInBackground(String... params) {
            HttpClient c = new DefaultHttpClient();
            HttpGet get =
                new HttpGet(Domain.getHTTPURL(GetMessageAuthCodeReq.this) + params[0] + ";jsessionid=" +
                    sessionId + "?phone=" + phoneNum);
            HttpResponse response = null;
            // try {
            try {
                response = c.execute(get);
                HttpEntity client = response.getEntity();
                if (client != null) {
                    InputStream is = null;
                    ByteArrayOutputStream outStream = null;
                    try {
                        is = client.getContent();
                        if (is != null) {
                            outStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                outStream.write(buffer, 0, len);
                            }
                            return outStream.toByteArray();
                        }
                        else
                            return null;
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                        return null;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    finally {
                        if (is != null)
                            is.close();
                        if (outStream != null)
                            outStream.close();
                    }
                }
                else {
                    return null;
                }
            }
            catch (ClientProtocolException e1) {
                e1.printStackTrace();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            // AndroidHttpClient httpClient = new AndroidHttpClient();
            // HttpEntity client = httpClient.getHttpEntity(params[0]);
            return null;
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_get_message_authcode";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GetMessageAuthCodeReq.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(GetMessageAuthCodeReq.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                           R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
