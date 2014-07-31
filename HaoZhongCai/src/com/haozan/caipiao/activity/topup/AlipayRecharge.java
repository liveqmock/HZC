package com.haozan.caipiao.activity.topup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.AliPayTopupControl;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.alipayfastlogin.AlixId;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;

public class AlipayRecharge
    extends BasicActivity
    implements OnClickListener, PopupSelectTypeClickListener {
    private boolean fromFast = false;
    private String initType;

    private String[] alipayToServer = {"20", "50", "100", "200", "300", "500", "1000", "2000"};

    private TextView aliPaySubTitle;
    private TextView aliPayFirstTips;
    private ImageView aliPaySubTitleIcon;

    private LinearLayout layoutMoneySpinner;
    private TextView tvMoneySelected;
    private ImageView spinnerIcon;

    private Button btnWapPay;
    private Button btnSecurityPay;
    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private TextView tvAlipayContact;
    private TextView tvRegisterAlipay;
    private TextView tvFeedback;

    private int selectedIndex = 0;

    private SelecteTypePopupWindow popupWindowWaySelect;
    private BetShowLotteryWay[] wayDataArray;
    public String rechargeMoney = alipayToServer[0];

    public AliPayTopupControl mTopupControl;

    private MyHandler mHandler;

    private static class MyHandler
        extends WeakReferenceHandler<AlipayRecharge> {

        public MyHandler(AlipayRecharge reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(AlipayRecharge activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.SHOW_PROGRESS:
                    activity.showProgress();
                    break;

                case ControlMessage.DISMISS_PROGRESS:
                    activity.dismissProgress();
                    break;

                case AlixId.RQF_PAY:
                    activity.mTopupControl.onPluginPayResult((String) msg.obj);
                    break;

                case ControlMessage.ALIPAY_SECURITY_TOPUP_SUCCESS:
                    activity.mTopupControl.pay(activity.rechargeMoney, (String[]) msg.obj);
                    break;

                case ControlMessage.TOPUP_FAIL:
                    if (ActionUtil.checkIfLogoff(activity, msg.arg1) == false) {
                        activity.mTopupControl.payFail((String) msg.obj);
                    }
                    break;

                case ControlMessage.FINISH_ACTIVITY:
                    activity.finish();
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_alipay);
        setupViews();
        init();
    }

    private void initData() {
        mHandler = new MyHandler(this);

        mTopupControl = new AliPayTopupControl(this, mHandler);

        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();

        String[] moneyText = new String[alipayToServer.length];
        for (int i = 0; i < alipayToServer.length; i++) {
            moneyText[i] = alipayToServer[i] + "元";
        }
        lotteryway.setUpsInf(moneyText);
        wayDataArray[0] = lotteryway;
    }

    private void init() {
        if (this.getIntent().getExtras() != null) {
            selectedIndex = this.getIntent().getExtras().getInt("money_index_num");
            fromFast = this.getIntent().getExtras().getBoolean("from_fast");
            rechargeMoney = alipayToServer[selectedIndex];
            initType = this.getIntent().getExtras().getString("alipay_recharge_type");
        }
        tvMoneySelected.setText(Html.fromHtml(mTopupControl.createMoneyShow(rechargeMoney)));

        aliPaySubTitleIcon.setImageResource(R.drawable.alipay_icon);
        aliPaySubTitle.setText("支付宝支付");

        aliPayFirstTips.setText(R.string.alipay_tip01);
        tvRegisterAlipay.setText(R.string.alipay_tip02);
        tvFeedback.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        tvAlipayContact.setText(Html.fromHtml("3.支付宝客服电话：<u> <font color='blue'>" + "0571-88156688"
            + "</color></u>。"));
        tvAlipayContact.setOnClickListener(this);

        if (fromFast) {
            if ("waprecharge".equals(initType)) {
                goWapPayment();
            }
            else if ("fastrecharge".equals(initType)) {
                goSecurePayment();
            }
        }
    }

    private void setupViews() {
        layoutMoneySpinner = (LinearLayout) this.findViewById(R.id.alipay_spinner);
        layoutMoneySpinner.setOnClickListener(this);
        tvMoneySelected = (TextView) this.findViewById(R.id.alipay_spinner_text);
        spinnerIcon = (ImageView) this.findViewById(R.id.alipay_spinner_icon);

        aliPaySubTitle = (TextView) this.findViewById(R.id.alipay_sub_title);
        aliPaySubTitleIcon = (ImageView) this.findViewById(R.id.alipay_subtitle_icon);

        btnWapPay = (Button) this.findViewById(R.id.wap_payment);
        btnWapPay.setOnClickListener(this);
        btnSecurityPay = (Button) this.findViewById(R.id.secure_payment);
        btnSecurityPay.setOnClickListener(this);

        // 页面提示控件
        aliPayFirstTips = (TextView) this.findViewById(R.id.tip);
        tvRegisterAlipay = (TextView) findViewById(R.id.registered_alipay);
        tvRegisterAlipay.setMovementMethod(LinkMovementMethod.getInstance());
        tvAlipayContact = (TextView) this.findViewById(R.id.call_alipay);
        tvFeedback = (TextView) this.findViewById(R.id.contact);
        tvFeedback.setOnClickListener(this);

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
        else if (v.getId() == R.id.secure_payment) {
            goSecurePayment();
        }
        else if (v.getId() == R.id.call_alipay) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0571-88156688"));
            startActivity(intent);
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.alipay_spinner) {
            showPopupViews();
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    private void showPopupViews() {
        wayDataArray[0].setSelectedIndex(selectedIndex);

        popupWindowWaySelect = new SelecteTypePopupWindow(AlipayRecharge.this, wayDataArray);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);

        popupWindowWaySelect.showAsDropDown(layoutMoneySpinner);
    }

    private void goWapPayment() {
        mTopupControl.toWapTopup(rechargeMoney);
    }

    private void goSecurePayment() {
        mTopupControl.goSecurePay(rechargeMoney);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTopupControl.dispose();
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void selecteType(int type, int index) {
        rechargeMoney = alipayToServer[index];
        tvMoneySelected.setText(Html.fromHtml(mTopupControl.createMoneyShow(rechargeMoney)));
        popupWindowWaySelect.dismiss();
        selectedIndex = index;
    }
}