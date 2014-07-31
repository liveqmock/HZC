package com.haozan.caipiao.activity.weibo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.Face;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.umeng.analytics.MobclickAgent;

public class NewConmentsActivity
    extends BasicActivity
    implements OnClickListener, OnLongClickListener {
    private static String FEEDBACKFAIL = "发表评论失败";
    private ProgressBar progress;

    private TextView titleTextView;
    private Button conmentButton;
    private Button clearButton;
    private EditText conmentEditText;
    private String conment = null;

    private Button listentButton;
    private int MAX_LENGTH = 140;
    int Rest_Length = MAX_LENGTH;

    private String weiboId;
    private String nickName;

    private Button face;
    private Button keyboard;
    private GridView faceGrid;
    private ImageView imageVerticalLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_conments);
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
        imageVerticalLine = (ImageView) findViewById(R.id.vertical_line);
        conmentButton = (Button) findViewById(R.id.title_btinit_right);
        conmentEditText = (EditText) findViewById(R.id.comentEditText);
        conmentEditText.setOnClickListener(this);
        conmentEditText.setOnLongClickListener(this);
        listentButton = (Button) findViewById(R.id.clear_button);
// listentButton.setOnClickListener(new clearButtonLitener());
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        titleTextView.setText("写评论");
        conmentButton.setText("   发表   ");
        clearButton.setText("   清空   ");
        clearButton.setVisibility(View.VISIBLE);
        imageVerticalLine.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(this);
        conmentButton.setOnClickListener(new conmentButtonLitener());

        // 接收传过来的weiboId
        weiboId = this.getIntent().getExtras().getString("weiboId");
        nickName = this.getIntent().getExtras().getString("nickName");

        conmentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if (Rest_Length > 0) {
                Rest_Length = MAX_LENGTH - conmentEditText.getText().length();
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

    }

    public void init() {
        if (nickName != null) {
            if (nickName.equals(appState.getNickname())) {
                conmentEditText.setText("回复@我" + ":");
            }
            else {
                conmentEditText.setText(TextUtil.formatContent("回复@" + nickName + ":",
                                                               NewConmentsActivity.this));
            }
        }
        listentButton.setText("   " + (140 - conmentEditText.getText().length()) + "   ");

        // 将光标移动到EditText文本的右边
        Editable b = conmentEditText.getText();
        conmentEditText.setSelection(b.length());
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
        if (v.getId() == R.id.face_button) {
            // 隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            showFace();
        }
        else if (v.getId() == R.id.keyboard_button) {
            showKeyboard();
        }
        else if (v.getId() == R.id.comentEditText) {
            keyboard.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            faceGrid.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.title_btinit_left) {
            conmentEditText.setText("");
            MAX_LENGTH = 140;
            listentButton.setText("   " + MAX_LENGTH + "   ");
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
                int index = conmentEditText.getSelectionStart();
                Editable edit = conmentEditText.getEditableText();
                edit.insert(index,
                            TextUtil.formatImage("[" + Face.faceNames[arg2] + "]", NewConmentsActivity.this));
            }
        }
    };

    private void showKeyboard() {
        // 打开（自动控制的再次点击按钮就会消失的）
        InputMethodManager imm =
            (InputMethodManager) NewConmentsActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        faceGrid.setVisibility(View.GONE);
        keyboard.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
    }

    private Boolean checkInput() {
        String warning = null;
        conment = conmentEditText.getText().toString();
        if (conment == null || conment.equals("")) {
            warning = "写点什么吧";
        }
        else if (conment.length() > 140) {
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
        if (HttpConnectUtil.isNetworkAvailable(NewConmentsActivity.this)) {
            NewConmentsTask task = new NewConmentsTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class NewConmentsTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            restoreSubmit();
            String inf = null;
            if (json == null) {
                inf = FEEDBACKFAIL;
                ViewUtil.showTipsToast(NewConmentsActivity.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                String errordesc = analyse.getData(json, "error_desc");
                if (status.equals("200")) {
                    inf = "发表评论成功";
                    ViewUtil.showTipsToast(NewConmentsActivity.this, inf);

// OperateInfUtils.broadcast(NewConmentsActivity.this, "myProfile_num");

                    Intent intent = new Intent();
                    intent.setClass(NewConmentsActivity.this, MyWeiboActivity.class);
                    setResult(RESULT_OK, intent); // 这里有2个参数(int resultCode,
                    // Intent intent)

                    // Intent intent1 = new Intent();
                    // intent1.setClass(NewConmentsActivity.this,
                    // WeiboHallActivity.class);
                    // setResult(RESULT_OK, intent1);

                    NewConmentsActivity.this.finish();
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(NewConmentsActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                 R.anim.push_to_left_out);
                    }
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(NewConmentsActivity.this, "该用户已将你拉黑，不能对该用户动态进行评论");
                }
                else if (status.equals("306")) {
                    ViewUtil.showTipsToast(NewConmentsActivity.this, errordesc);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(NewConmentsActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(NewConmentsActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = FEEDBACKFAIL;
                    ViewUtil.showTipsToast(NewConmentsActivity.this, inf);
                }
            }
            progress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            conmentButton.setText("  提交中  ");
            conmentButton.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // 将输入的火车符换成空格，否则会造成显示篇幅过长问题
            String newText =
                conmentEditText.getText().toString().replaceAll(System.getProperty("line.separator"), " ");
            String c = HttpConnectUtil.encodeParameter(newText);
            NewConmentsService newweibo = new NewConmentsService(NewConmentsActivity.this, c);
            String status = newweibo.sending();
            return status;
        }
    }

    private void restoreSubmit() {
        conmentButton.setText("   评论   ");
        conmentButton.setEnabled(true);
    }

    // NewConmentsService
    class NewConmentsService {

        private Context context;
        private String content;

        public NewConmentsService(Context context, String content) {
            this.context = context;
            this.content = content;
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004030");
            parameter.put("pid", LotteryUtils.getPid(NewConmentsActivity.this));
            parameter.put("phone", appState.getUsername());
            parameter.put("reply_id", weiboId);
            parameter.put("content", content);
            parameter.put("type", "1");
            return parameter;
        }

        public String sending() {
            ConnectService connectNet = new ConnectService(NewConmentsActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonPost(4, true, initHashMap());
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
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal create comment");
        String eventName = "v2 open garden create comment";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_submit_event";
        MobclickAgent.onEvent(this, eventName, "comment");
        besttoneEventCommint(eventName);
    }

    private void immIsActiveOrNot() {
        InputMethodManager imm =
            (InputMethodManager) NewConmentsActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive() && (faceGrid.getVisibility() == View.VISIBLE)) {
            faceGrid.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.comentEditText) {
            immIsActiveOrNot();
        }
        return false;
    }
}
