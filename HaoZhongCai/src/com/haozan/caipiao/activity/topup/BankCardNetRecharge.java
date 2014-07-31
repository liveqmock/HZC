package com.haozan.caipiao.activity.topup;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
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
import com.haozan.caipiao.control.topup.BankCardNetTopupControl;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.alipayfastlogin.AlixId;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;

public class BankCardNetRecharge
    extends BasicActivity
    implements OnClickListener, PopupSelectTypeClickListener {

    private boolean fromFast = false;

    private String[] alipayToServer = {"20", "50", "100", "200", "300", "500", "1000", "2000"};
    private String bankName;
    private String type;

    private TextView aliPaySubTitle;
    private TextView aliPayFirstTips;
    private ImageView aliPaySubTitleIcon;

    private LinearLayout layoutMoneySpinner;
    private TextView tvMoneySelected;
    private ImageView spinnerIcon;

    private Button btnWapPay;
    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private BankCardNetTopupControl mTopupControl;
    private AliPayTopupControl mTopupControl2;

    private TextView tvAlipayContact;
    private TextView tvFeedback;

    private int selectedIndex = 0;

    private SelecteTypePopupWindow popupWindowWaySelect;
    private BetShowLotteryWay[] wayDataArray;
    private String rechargeMoney = alipayToServer[0];
    
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
        setContentView(R.layout.activity_alipay_bank_net_recharge);
        setupViews();
        init();
    }

    private void initData() {
        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();

        String[] moneyText = new String[alipayToServer.length];
        for (int i = 0; i < alipayToServer.length; i++) {
            moneyText[i] = alipayToServer[i] + "元";
        }
        lotteryway.setUpsInf(moneyText);
        wayDataArray[0] = lotteryway;

        mTopupControl = new BankCardNetTopupControl(this);
        mTopupControl2 = new AliPayTopupControl(this, mHandler);
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            selectedIndex = bundle.getInt("money_index_num");
            fromFast = bundle.getBoolean("from_fast");
            bankName = bundle.getString("bank_name");
            type = bundle.getString("type");

            rechargeMoney = alipayToServer[selectedIndex];
        }

        tvMoneySelected.setText(Html.fromHtml(mTopupControl.createMoneyShow(rechargeMoney)));

        tvAlipayContact.setText(R.string.alipay_wap_tip03);
        tvFeedback.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        aliPaySubTitleIcon.setImageResource(R.drawable.yinhangka);
        aliPaySubTitle.setText("银行卡网页支付");
        aliPayFirstTips.setText(R.string.alipay_wap_tip01);

        if (fromFast) {
            mTopupControl.toWapTopup(bankName, type, rechargeMoney);
        }
    }

    private void setupViews() {
        layoutMoneySpinner = (LinearLayout) this.findViewById(R.id.alipay_spinner);
        layoutMoneySpinner.setOnClickListener(this);
        tvMoneySelected = (TextView) this.findViewById(R.id.alipay_spinner_text);
        spinnerIcon = (ImageView) this.findViewById(R.id.alipay_spinner_icon);

        aliPaySubTitle = (TextView) this.findViewById(R.id.alipay_sub_title);
        aliPaySubTitleIcon = (ImageView) this.findViewById(R.id.alipay_subtitle_icon);

        btnWapPay = (Button) this.findViewById(R.id.submit);
        btnWapPay.setOnClickListener(this);

        // 页面提示控件
        aliPayFirstTips = (TextView) this.findViewById(R.id.tip);
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
        if (v.getId() == R.id.submit) {
            //mTopupControl.toWapTopup(bankName, type, rechargeMoney);
            mTopupControl2.toWapTopup(rechargeMoney);
        }
        else if (v.getId() == R.id.call_alipay) {
            mTopupControl.callAlipay();
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

        popupWindowWaySelect = new SelecteTypePopupWindow(BankCardNetRecharge.this, wayDataArray);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);

        popupWindowWaySelect.showAsDropDown(tvMoneySelected);
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void selecteType(int type, int index) {
        rechargeMoney = alipayToServer[index];
        tvMoneySelected.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        popupWindowWaySelect.dismiss();
        selectedIndex = index;
    }

}
