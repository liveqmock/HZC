package com.haozan.caipiao.activity.topup;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.Domain;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.control.topup.TopupBasic;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.error.ExceptionHandler;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.umeng.analytics.MobclickAgent;

public class WapBankRecharge
    extends BasicActivity
    implements OnClickListener, PopMenuButtonClickListener {

    private static final String[] wap_type = {"招商银行", "建设银行"};
    private static final String[] bank = {"CMBCHINA", "CCB"};
    private String[] alipayMoney = {"20元", "50元", "100元", "200元", "300元", "500元", "1000元", "2000元"};
    private String[] alipayToServer = {"20", "50", "100", "200", "300", "500", "1000", "2000"};
    private String time;

    private String rechargeMoney = alipayToServer[0];
    private String type = wap_type[0];
    private String bankName = bank[0];

    private LinearLayout spinnerChose;
    private LinearLayout moneySpinner;
    private TextView spinnerChoseText;
    private TextView moneySpinnerText;
    private ImageView spinnerChoseIcon;
    private ImageView moneySpinnerIcon;

    private ProgressDialog progressDialog;

    private LayoutInflater factory;
    private View eventview;

    private Dialog customDialog_re;
    private TextView text_first_dlg;
    private TextView text_second_dlg;
    private TextView text_third_dlg;
    private Button submit_bt_dlg;
    private Button cancle_bt_dlg;
    
    private Button mBtnContackServer;
    private Button mBtnOtherTopup;
    private TopupBasic mTopupControl;

    private Button submit;
    private ImageButton help;
    private TextView contact;

    private Boolean from = false;
    // 充值额菜单
    private PopMenu titlePopup;
    private int screenWidth;
    private int buttonid;
    private int last_index_num_bank = 0;
    private int money_index_num = 0;
    private boolean fromFast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionHandler.register(this);
        setContentView(R.layout.wap_bank_recharge);
        initData();
        setupViews();
        init();
    }

    private void initData() {
    	mTopupControl = new TopupBasic(this);		
	}

	private void init() {
        if (this.getIntent().getExtras() != null) {
            from = this.getIntent().getExtras().getBoolean("from");
            money_index_num = this.getIntent().getExtras().getInt("money_index_num");
            fromFast = this.getIntent().getExtras().getBoolean("from_fast");
            rechargeMoney = alipayToServer[money_index_num];
        }

        // 获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        progressDialog = new ProgressDialog(WapBankRecharge.this);
        progressDialog.setMessage("正在提交充值...");

        customDialog_re = new Dialog(this, R.style.dialog);

        restoreSubmit();
        String contactStr = "3.如有任何问题请<u><font color='blue'>联系我们</color></u>。";
        contact.setOnClickListener(this);
        contact.setText(Html.fromHtml(contactStr));

        spinnerChoseText.setText(Html.fromHtml("<font color='#9D9D9D'>银行：</font>" + type));
        moneySpinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));

        if (fromFast) {
            showDialog();
        }
    }

    private void restoreSubmit() {
        submit.setText("充        值");
        submit.setEnabled(true);
    }

    private void setupViews() {
        contact = (TextView) this.findViewById(R.id.contact);
        moneySpinner = (LinearLayout) this.findViewById(R.id.wap_bank_recharge_money);
        moneySpinner.setOnClickListener(this);

        spinnerChose = (LinearLayout) this.findViewById(R.id.waprechspinner01);
        spinnerChose.setOnClickListener(this);

        moneySpinnerText = (TextView) this.findViewById(R.id.wap_bank_recharge_money_text);

        spinnerChoseText = (TextView) this.findViewById(R.id.waprechspinner01_text);

        moneySpinnerIcon = (ImageView) this.findViewById(R.id.wap_bank_recharge_money_icon);

        spinnerChoseIcon = (ImageView) this.findViewById(R.id.waprechspinner01_icon);

        help = (ImageButton) this.findViewById(R.id.help);
        help.setOnClickListener(this);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        factory = LayoutInflater.from(WapBankRecharge.this);
        eventview = factory.inflate(R.layout.wap_recharge_make_sure_dialog, null);
        text_first_dlg = (TextView) eventview.findViewById(R.id.dialog_item_first);
        text_second_dlg = (TextView) eventview.findViewById(R.id.dialog_item_second);
        text_third_dlg = (TextView) eventview.findViewById(R.id.dialog_item_three);
        submit_bt_dlg = (Button) eventview.findViewById(R.id.submit_dlg);
        submit_bt_dlg.setOnClickListener(this);
        cancle_bt_dlg = (Button) eventview.findViewById(R.id.cancle_dlg);
        cancle_bt_dlg.setOnClickListener(this);
        
        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
// Intent intent = new Intent();
// Bundle bundel = new Bundle();
// bundel.putInt("help02", R.string.helpinf05);
// bundel.putString("about", "right");
// intent.putExtras(bundel);
// intent.setClass(WapBankRecharge.this, Helpyou.class);
// startActivity(intent);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_RECHARGE_URL + "#wapBankRecharge");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(WapBankRecharge.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.submit) {
            showDialog();
        }
        else if (v.getId() == R.id.submit_dlg) {
            databaseData.putString("last_topup_way", WapBankRecharge.class.getName());
            databaseData.putBoolean("recharge_yy", false);
            databaseData.commit();
            goWapRecharge();
            customDialog_re.dismiss();
        }
        else if (v.getId() == R.id.cancle_dlg) {
            customDialog_re.dismiss();
        }
        else if (v.getId() == R.id.contact) {
            Intent intent = new Intent();
            intent.setClass(this, Feedback.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.wap_bank_recharge_money) {
            showPopupViews(moneySpinner, alipayMoney, money_index_num);
            buttonid = R.id.wap_bank_recharge_money;
        }
        else if (v.getId() == R.id.waprechspinner01) {
            showPopupViews(spinnerChose, wap_type, last_index_num_bank);
            buttonid = R.id.waprechspinner01;
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    public void showDialog() {
        if (HttpConnectUtil.isNetworkAvailable(WapBankRecharge.this)) {
            text_first_dlg.setText(appState.getUsername());
            text_second_dlg.setText(type);
            text_third_dlg.setText(rechargeMoney + "元");
            customDialog_re.setContentView(eventview, new LayoutParams(LayoutParams.FILL_PARENT,
                                                                       LayoutParams.WRAP_CONTENT));
            customDialog_re.show();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int last_index) {
        titlePopup = new PopMenu(WapBankRecharge.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1, screenWidth - 20, last_index, false,
                             true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - moneySpinner.getWidth() + moneySpinner.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open wap bank recharge");
        String eventName = "v2 open wap bank recharge";
        FlurryAgent.onEvent(eventName, map);
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("inf", "open wap bank top up");
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
                WapBankRecharge.this.finish();
            }
        }
        else if (requestCode == 22 && resultCode == 25) {
            WapBankRecharge.this.finish();
        }
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user top up wap bank success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user top up success");
        String eventName = "top up success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", "wap_bank");
        map1.put("money", rechargeMoney);
        MobclickAgent.onEvent(WapBankRecharge.this, eventNameMob, map1);
        besttoneEventCommint(eventNameMob);
    }

    private void goWapRecharge() {
        submitStatisticsTopupSuccess();
        StringBuilder parameter = new StringBuilder();
        parameter.append(Domain.getHTTPURL(this));
        parameter.append("services/");
        parameter.append("gateway");
        parameter.append(";jsessionid=");
        String sessionId = appState.getSessionid();
        parameter.append(sessionId);
        parameter.append("?service=replenish_account_by_wap&pid=" + LotteryUtils.getPid(WapBankRecharge.this));
        parameter.append("&phone=" + appState.getUsername());
        parameter.append("&bank=" + bankName);
        parameter.append("&money=");
        parameter.append(Integer.valueOf(rechargeMoney));
        time = appState.getTime();
        if (time == null) {
            return;
        }
        parameter.append("&timestamp=" + time);
        parameter.append("&sign=");
        String code = EncryptUtil.MD5Encrypt(time + LotteryUtils.getKey(WapBankRecharge.this));
        parameter.append(code);
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(parameter.toString()));
        startActivity(it);
        setResult(RESULT_OK);
        System.out.println("union credit card:" + rechargeMoney + "|" + bankName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_wap_bank_topup";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
        String eventName1 = "open_topup";
        MobclickAgent.onEvent(this, eventName1, "wap_bank");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WapBankRecharge.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {

        if (buttonid == R.id.wap_bank_recharge_money) {
            rechargeMoney = alipayToServer[index];
            moneySpinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
            money_index_num = index;
        }
        else if (buttonid == R.id.waprechspinner01) {
            type = wap_type[index];
            bankName = bank[index];
            rechargeMoney = alipayToServer[money_index_num];
            spinnerChoseText.setText(Html.fromHtml("<font color='#9D9D9D'>银行：</font>" + type));
            moneySpinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
            last_index_num_bank = index;
        }
        titlePopup.dismiss();
    }
}
