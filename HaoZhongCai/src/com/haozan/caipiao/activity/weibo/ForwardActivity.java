package com.haozan.caipiao.activity.weibo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.adapter.FaceAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.weiboutil.Face;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 转发财园信息
 * 
 * @author peter_feng
 * @create-time 2013-6-29 上午11:50:04
 */
public class ForwardActivity
    extends BasicActivity
    implements OnClickListener {
    private static String FEEDBACKFAIL = "转发失败";

    private ProgressBar progress;

    private TextView titleTextView;
    private Button forwardButton;
    private Button clearButton;
    private EditText forwardEditText;
    private String forward = null;

    private Button listentButton;
    private int MAX_LENGTH = 140;
    int Rest_Length = MAX_LENGTH;

    private Button face;
    private Button keyboard;
    private GridView faceGrid;

    private String weiboId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_forward);
        setupViews();
        init();
    }

    public void setupViews() {
        face = (Button) findViewById(R.id.face_button);
        keyboard = (Button) findViewById(R.id.keyboard_button);
        face.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        faceGrid = (GridView) findViewById(R.id.updater_faceGrid);
        faceGrid.setOnItemClickListener(itemClickListener);
        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        clearButton = (Button) findViewById(R.id.title_btinit_left);
        forwardButton = (Button) findViewById(R.id.title_btinit_right);
        forwardEditText = (EditText) findViewById(R.id.forwardEditText);
        forwardEditText.setOnClickListener(this);
        listentButton = (Button) findViewById(R.id.clear_button);
// listentButton.setOnClickListener(new clearButtonLitener());
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        titleTextView.setText("动态转发");
        forwardButton.setText("   发表   ");
        clearButton.setText("   清空   ");
        clearButton.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(this);
        forwardButton.setOnClickListener(new conmentButtonLitener());

        // 接收传过来的动态对象
        weiboId = this.getIntent().getExtras().getString("weiboId");

        forwardEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if (Rest_Length > 0) {
                Rest_Length = MAX_LENGTH - forwardEditText.getText().length();
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Rest_Length > 0) {
                    listentButton.setText("   " + Rest_Length + "   ");

                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Rest_Length > 0) {
                    listentButton.setText("   " + Rest_Length + "   ");

                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));

                }
            }
        });
        listentButton.setText("   " + (140 - forwardEditText.getText().length()) + "   ");

    }

    public void init() {
        GetDataTask task = new GetDataTask();
        task.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceGrid.isShown()) {
                faceGrid.setVisibility(View.GONE);
                keyboard.setVisibility(View.GONE);
                face.setVisibility(View.VISIBLE);
            }
            else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_left) {
            forwardEditText.setText("");
            MAX_LENGTH = 140;
            listentButton.setText("   " + MAX_LENGTH + "   ");
        }
        else if (v.getId() == R.id.face_button) {
            // 隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            showFace();
        }
        else if (v.getId() == R.id.keyboard_button) {
            showKeyboard();
        }
        else if (v.getId() == R.id.forwardEditText) {
            keyboard.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            faceGrid.setVisibility(View.GONE);
        }
    }

    private void showFace() {
        faceGrid.setVisibility(View.VISIBLE);
        face.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
        if (faceGrid.getAdapter() == null) {
            faceGrid.setAdapter(new FaceAdapter(this, Face.faceNames));
        }
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 < Face.faceNames.length) {
                // android EditText插入字符串到光标所在位置
                int index = forwardEditText.getSelectionStart();
                Editable edit = forwardEditText.getEditableText();
                edit.insert(index,
                            TextUtil.formatImage("[" + Face.faceNames[arg2] + "]", ForwardActivity.this));
// forwardEditText.append(TextUtil.formatImage("[" + Face.faceNames[arg2] + "]",
// ForwardActivity.this));
            }
        }
    };

    private void showKeyboard() {
        // 打开（自动控制的再次点击按钮就会消失的）
        InputMethodManager imm =
            (InputMethodManager) ForwardActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        faceGrid.setVisibility(View.GONE);
        keyboard.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
    }

    private Boolean checkInput() {
        String warning = null;
        forward = forwardEditText.getText().toString();
        if (forward == null || forward.equals("")) {
            warning = "写点什么吧";
        }
        else if (forward.length() > 140) {
            warning = "输入字数超出上限";
        }
        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        return true;
    }

    class conmentButtonLitener
        implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (checkInput() == true) {
                sendin();
            }
        }

    }

    protected void sendin() {
        if (HttpConnectUtil.isNetworkAvailable(ForwardActivity.this)) {
            ForwardTask task = new ForwardTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private class ForwardTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPostExecute(String json) {
            restoreSubmit();
            String inf = null;
            if (json == null) {
                inf = FEEDBACKFAIL;
                ViewUtil.showTipsToast(ForwardActivity.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                String errordesc = analyse.getData(json, "error_desc");
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(ForwardActivity.this, "转发成功");

                    ForwardActivity.this.finish();
                }
                else if (status.equals("306")) {
                    ViewUtil.showTipsToast(ForwardActivity.this, errordesc);
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(ForwardActivity.this, "该用户已将你拉黑，不能对该用户动态进行转发");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(ForwardActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(ForwardActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(ForwardActivity.this, FEEDBACKFAIL);
                }
            }
            progress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            forwardButton.setText("   提交中   ");
            forwardButton.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
        }

        private HashMap<String, String> initHashMap(String content)
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004030");
            parameter.put("pid", LotteryUtils.getPid(ForwardActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("reply_id", weiboId);
            parameter.put("content", HttpConnectUtil.encodeParameter(content));
            parameter.put("type", "2");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // 将输入的回车符换成空格，否则会造成显示篇幅过长问题
            String newText =
                forwardEditText.getText().toString().replaceAll(System.getProperty("line.separator"), " ");
            ConnectService connectNet = new ConnectService(ForwardActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap(newText));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private void restoreSubmit() {
        forwardButton.setText("   转发   ");
        forwardButton.setEnabled(true);
    }

    private class GetDataTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004110");
            parameter.put("pid", LotteryUtils.getPid(ForwardActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("weibo_id", weiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(ForwardActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    progress.setVisibility(View.GONE);
                    String response_data = analyse.getData(json, "response_data");
                    if (response_data.equals("[]")) {

                    }
                    else {
                        try {
                            JSONArray hallArray = new JSONArray(response_data);
                            JSONObject jo = hallArray.getJSONObject(0);
                            String name = jo.getString("nickname");
                            String content = jo.getString("content");
                            forwardEditText.setText(TextUtil.formatContent("//@" + name + ":" + content,
                                                                           ForwardActivity.this));
                            // 将光标移动到EditText文本的右边
// Editable b = forwardEditText.getText();
// forwardEditText.setSelection(b.length());
                        }
                        catch (JSONException e) {
                            ViewUtil.showTipsToast(ForwardActivity.this, "网络数据有误");
                            e.printStackTrace();
                        }
                    }
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(ForwardActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(ForwardActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden forward list");
        String eventName = "v2 open garden forward list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_submit_event";
        MobclickAgent.onEvent(this, eventName, "forward");
        besttoneEventCommint(eventName);
    }
}
