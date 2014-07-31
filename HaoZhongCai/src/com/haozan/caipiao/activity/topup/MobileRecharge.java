package com.haozan.caipiao.activity.topup;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.control.topup.MobileTopupControl;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.umeng.analytics.MobclickAgent;

public class MobileRecharge
    extends BasicActivity
    implements OnClickListener, PopMenuButtonClickListener {
    private String[] alipayMoney = {"10元", "20元", "50元", "100元", "200元", "500元", "1000元"};
    private String[] alipayToServer = {"10", "20", "50", "100", "200", "500", "1000"};
    private String rechargeMoney = alipayToServer[0];
    private Button wapPayment;
    private Button securePayment;
    private Button smsButton;
    private ImageButton help;
    private TextView contact;
    private LinearLayout chooseRechargeMoney;
    private TextView chooseRechargeMoneyText;
    private ImageView chooseRechargeMoneyIcon;

    private Boolean from = false;

    private PopMenu titlePopup;
    private int screenWidth;
    private int money_index_num = 0;
    private boolean fromFast = false;

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private MobileTopupControl mTopupControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_mobile);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        mTopupControl = new MobileTopupControl(this);
    }

    private void init() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        contact.setOnClickListener(this);
        contact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        if (this.getIntent().getExtras() != null) {
            from = this.getIntent().getExtras().getBoolean("from");
            money_index_num = this.getIntent().getExtras().getInt("money_index_num");
            fromFast = this.getIntent().getExtras().getBoolean("from_fast");
            rechargeMoney = alipayToServer[money_index_num];
        }

        chooseRechargeMoney = (LinearLayout) this.findViewById(R.id.cm_recharge_real_money);
        chooseRechargeMoney.setOnClickListener(this);
        chooseRechargeMoneyText = (TextView) this.findViewById(R.id.cm_recharge_real_money_text);
        chooseRechargeMoneyIcon = (ImageView) this.findViewById(R.id.cm_recharge_real_money_icon);
        chooseRechargeMoneyText.setText(Html.fromHtml("<font color='#9D9D9D'>充值金额：</font>" + rechargeMoney +
            "元"));
        if (fromFast) {
            goWapPayment();
        }
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(int last_index) {
        titlePopup = new PopMenu(MobileRecharge.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, alipayMoney, null, 1, screenWidth - 20, last_index,
                             false, true);
        titlePopup.setButtonClickListener(this);
        titlePopup.showAsDropDown(chooseRechargeMoney,
                                  -(titlePopup.getWidth() / 2 - chooseRechargeMoney.getWidth() + chooseRechargeMoney.getWidth() / 2),
                                  0);
        titlePopup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // topArrow.setImageResource(R.drawable.arrow_down);
                titlePopup.dismiss();
            }
        });
    }

    private void setupViews() {
        contact = (TextView) this.findViewById(R.id.contact);
        wapPayment = (Button) this.findViewById(R.id.wap_payment);
        wapPayment.setOnClickListener(this);
        wapPayment.setText("充值");
        securePayment = (Button) findViewById(R.id.secure_payment);
        securePayment.setOnClickListener(this);
        smsButton = (Button) findViewById(R.id.sms_payment);
        smsButton.setOnClickListener(this);
        help = (ImageButton) this.findViewById(R.id.help);
        help.setOnClickListener(this);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wap_payment) {
            goWapPayment();
        }
        else if (v.getId() == R.id.help) {
            mTopupControl.toHelp();
        }
        else if (v.getId() == R.id.call_alipay) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.cm_recharge_real_money) {
            showPopupViews(money_index_num);
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    private HashMap<String, String> initMobileWapHashMap() {
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2005131");
        parameter.put("pid", LotteryUtils.getPid(MobileRecharge.this));
        parameter.put("phone", String.valueOf(phone));
        parameter.put("type", "WAP");
        parameter.put("money", rechargeMoney);
        return parameter;
    }

    private void goWapPayment() {
        if (HttpConnectUtil.isNetworkAvailable(MobileRecharge.this)) {
            submitStatisticsTopupSuccess();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            ConnectService connectNet = new ConnectService(MobileRecharge.this);
            String mobileWapUrl = connectNet.getJsonGetUrl(3, true, initMobileWapHashMap());
            bundle.putString("title", "移动手机wap支付");
            bundle.putString("url", mobileWapUrl);
            intent.putExtras(bundle);
            intent.setClass(MobileRecharge.this, WebBrowser.class);
            startActivity(intent);
            setResult(RESULT_OK);
            databaseData.putString("last_topup_way", MobileRecharge.class.getName());
            databaseData.putBoolean("recharge_yy", false);
            databaseData.commit();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user top up china mobile success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user top up success");
        String eventName = "top up success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", "china mobile");
        map1.put("money", rechargeMoney);
        MobclickAgent.onEvent(MobileRecharge.this, eventNameMob, map1);
        besttoneEventCommint(eventNameMob);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("inf", "open china mobile top up");
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
                MobileRecharge.this.finish();
            }
        }
        else if (requestCode == 18 && resultCode == 25) {
            MobileRecharge.this.finish();
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_topup";
        MobclickAgent.onEvent(this, eventName, "china mobile");
        besttoneEventCommint(eventName);
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        rechargeMoney = alipayToServer[index];
        chooseRechargeMoneyText.setText(Html.fromHtml("<font color='#9D9D9D'>充值金额：</font>" + rechargeMoney +
            "元"));
        money_index_num = index;
        titlePopup.dismiss();
    }

}
