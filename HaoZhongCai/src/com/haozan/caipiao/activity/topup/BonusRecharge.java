package com.haozan.caipiao.activity.topup;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.control.topup.TopupBasic;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 彩票码充值
 * 
 * @author peter_feng
 * @create-time 2013-6-28 下午4:33:02
 */
public class BonusRecharge
    extends BasicActivity
    implements OnClickListener {
    private EditText editText;
    private Button button;
    private Button mBtnContackServer;
    private Button mBtnOtherTopup;
    private TopupBasic mTopupControl;
    private TextView contact;
    private ScrollView scrollView;
    private String bonesCodes;

    private boolean fromFast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_bonus);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        mTopupControl = new TopupBasic(this);
    }

    public void init() {
        if (this.getIntent().getExtras() != null) {
            bonesCodes = this.getIntent().getExtras().getString("bonus_codes");
            editText.setText(bonesCodes);
            if (null != bonesCodes && !"".equals(bonesCodes))
                editText.setSelection(bonesCodes.length());
            fromFast = this.getIntent().getExtras().getBoolean("from_fast");
        }
        if (fromFast) {
            executeRedPacketRechargeTask();
        }
    }

    public void setupViews() {
        editText = (EditText) findViewById(R.id.red_packet_recharge_code);
        button = (Button) findViewById(R.id.red_packet_recharge_submit);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                executeRedPacketRechargeTask();
                final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
            }

        });

        TextView redPacketRechargeInf = (TextView) findViewById(R.id.red_packet_recharge_inf);
        redPacketRechargeInf.setText(Html.fromHtml("4.赶紧填写彩票码领取红包吧！千万元大奖等着你哦！<br>温馨提示：一些不定期的彩票活动会赠送彩票码。"));
        scrollView = (ScrollView) findViewById(R.id.scroll_view_bonus);
        scrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setError(null);
                return false;
            }
        });
        contact = (TextView) this.findViewById(R.id.contact);
        String contactStr = "5.如有任何问题请<u><font color='blue'>联系我们</color></u>。";
        contact.setOnClickListener(this);
        contact.setText(Html.fromHtml(contactStr));

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    public void executeRedPacketRechargeTask() {
        if (checkIput()) {
            if (HttpConnectUtil.isNetworkAvailable(BonusRecharge.this)) {
                databaseData.putString("last_topup_way", BonusRecharge.class.getName());
                databaseData.putBoolean("recharge_yy", false);
                databaseData.commit();
                RedPacketChargeTask rpct = new RedPacketChargeTask();
                rpct.execute();
            }
            else {
                String inf = getResources().getString(R.string.network_not_avaliable);
                ViewUtil.showTipsToast(BonusRecharge.this, inf);
            }
        }
    }

    private boolean checkIput() {
        String inf = null;
        if (editText.getText().toString().equals("")) {
            inf = "请输入12位兑换码";
            editText.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (editText.getText().toString().length() != 12 && editText.getText().toString().length() != 16) {
            inf = "请输入12位兑换码";
            editText.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (inf != null) {
            return false;
        }
        else
            return true;
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess(String inf) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user top up snda success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user top up success");
        String eventName = "top up success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", "bonus");
        map1.put("money", inf);
        MobclickAgent.onEvent(BonusRecharge.this, eventNameMob, map1);
        besttoneEventCommint(eventNameMob);
    }

    class RedPacketChargeTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPostExecute(String result) {
            dismissProgress();
            if (result != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String inf = analyse.getData(analyse.getData(result, "response_data"), "amount");
                    ViewUtil.showTipsToast(BonusRecharge.this, "你已经成功兑换" + inf + "元");
                    submitStatisticsTopupSuccess(inf);
                }
                else if (status.equals("300")) {
                    String inf = analyse.getData(result, "error_desc");
                    ViewUtil.showTipsToast(BonusRecharge.this, inf);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(BonusRecharge.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(BonusRecharge.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    ViewUtil.showTipsToast(BonusRecharge.this, "失败");
                }
            }
            else {
                ViewUtil.showTipsToast(BonusRecharge.this, "网络连接不稳定，无法确认充值是否成功，请查看消息以确认");
            }
        }

        @Override
        protected String doInBackground(Void... kind) {
            ConnectService connectNet = new ConnectService(BonusRecharge.this);
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
            showProgress();
        }
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) BonusRecharge.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005061");
        parameter.put("pid", LotteryUtils.getPid(BonusRecharge.this));
        parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
        parameter.put("code", editText.getText().toString().trim());
        return parameter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            BonusRecharge.this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bonus recharge");
        String eventName = "v2 open bonus recharge";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("inf", "open bonus top up");
        map1.put("more_inf", "username [" + appState.getUsername() + "]: open top up");
        String eventName1 = "open top up";
        FlurryAgent.onEvent(eventName1, map1);
    }

    @Override
    protected void submitData() {
        String eventName = "open_bonus_recharge";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
        String eventName1 = "open_topup";
        MobclickAgent.onEvent(this, eventName1, "bonus");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }
}
