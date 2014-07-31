package com.haozan.caipiao.activity.guess;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.StringUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户发起竞猜，上传竞猜题目及答案
 * 
 * @author Vincent
 * @create-time 2013-4-25 下午10:47:50
 */
public class CreateMyGuess
    extends BasicActivity {

    private static String COMMIT_FAIL = "提交失败";
    private EditText content;
    private EditText answer;
    private Button submit;
    private StringBuilder commitStr;

// private ProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_my_guess);
        setupViews();
        init();
    }

    private void init() {
        commitStr = new StringBuilder();
    }

    private void setupViews() {
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean canSubmit = checkContent();
                if (canSubmit) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("zzc_desc", "username [" + appState.getUsername() + "]: click feedback submit");
                    FlurryAgent.onEvent("user click feedback submit", map);
                    sending();
                }

            }
        });
        content = (EditText) this.findViewById(R.id.feedback_content);
        answer = (EditText) this.findViewById(R.id.feedback_content_answer);
    }

    protected void sending() {
        if (HttpConnectUtil.isNetworkAvailable(CreateMyGuess.this)) {
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
            ViewUtil.showTipsToast(CreateMyGuess.this, "请输入竞猜题目");
            ret = false;
        }
        return ret;
    }

    class FeedbackTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            String inf = null;
            if (json == null) {
                inf = COMMIT_FAIL;
                ViewUtil.showTipsToast(CreateMyGuess.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    inf = "提交成功！";
                    ViewUtil.showTipsToast(CreateMyGuess.this, inf);
                    CreateMyGuess.this.finish();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(CreateMyGuess.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(CreateMyGuess.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = COMMIT_FAIL;
                    ViewUtil.showTipsToast(CreateMyGuess.this, inf);
                }
            }
            dismissProgress();
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        private HashMap<String, String> iniHashMap(String inf) {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "1001030");
            parameter.put("type", "2");
            parameter.put("pid", LotteryUtils.getPid(CreateMyGuess.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("msg_content", HttpConnectUtil.encodeParameter(getContent()));
            parameter.put("client_info", HttpConnectUtil.encodeParameter(inf));
            return parameter;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String version = LotteryUtils.fullVersion(CreateMyGuess.this);
            String infExtra =
                "Product Model:" + android.os.Build.MODEL + ",SDK Version:" +
                    android.os.Build.VERSION.RELEASE + ",versionName:" + version + ",PhoneNum:" +
                    appState.getUsername();
            ConnectService connectNet = new ConnectService(CreateMyGuess.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open feedback");
        String eventName = "v2 open feedback";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    public String getContent() {
        commitStr.replace(0, commitStr.length(), "");
        commitStr.append("题目:" + content.getText().toString());
        commitStr.append("答案:" + answer.getText().toString());
        return commitStr.toString();
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
            CreateMyGuess.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(CreateMyGuess.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                   R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}