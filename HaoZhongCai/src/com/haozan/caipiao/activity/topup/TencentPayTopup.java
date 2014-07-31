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
import com.haozan.caipiao.control.topup.TencentPayTopupControl;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;

/**
 * 财付通充值
 * 
 * @author peter_wang
 * @create-time 2013-10-31 下午4:29:43
 */
public class TencentPayTopup
    extends BasicActivity
    implements OnClickListener, PopupSelectTypeClickListener {
    private static final String[] MONEY = {"20", "50", "100", "200", "300", "500", "1000"};

    private TextView mHelp;
    private TextView mTvTitle;

    private ImageView mIvPayIcon;
    private TextView mTvPayDesc;

    private TextView contact;

    private LinearLayout mMoneySpinner;
    private TextView mMoneySpinnerText;
    private ImageView mMoneySpinnerIcon;

    private Button mBtnTopup;
    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private BetShowLotteryWay[] mMoneyDataArray;
    private SelecteTypePopupWindow mPpwWaySelect;
    private int mMoneyIndex;

    private TencentPayTopupControl mTopupControl;

    private MyHandler mHandler;

    private static class MyHandler
        extends WeakReferenceHandler<TencentPayTopup> {

        public MyHandler(TencentPayTopup reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(TencentPayTopup activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.SHOW_PROGRESS:
                    activity.showProgress();
                    break;

                case ControlMessage.DISMISS_PROGRESS:
                    activity.dismissProgress();
                    break;

                case ControlMessage.DELAY_INIT:
                    activity.delayInit();
                    break;

                case TencentPayTopupControl.MSG_PAY_RESULT:
                    activity.mTopupControl.analyseTopupResult((String) msg.obj);
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_topup);
        setupViews();
        init();
    }

    private void initData() {
        mHandler = new MyHandler(this);

        mMoneyDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();

        String[] moneyText = new String[MONEY.length];
        for (int i = 0; i < MONEY.length; i++) {
            moneyText[i] = MONEY[i] + "元";
        }
        lotteryway.setUpsInf(moneyText);
        mMoneyDataArray[0] = lotteryway;

        mTopupControl = new TencentPayTopupControl(this, mHandler);
    }

    private void init() {
        mHandler.sendEmptyMessage(ControlMessage.DELAY_INIT);
    }

    private void setupViews() {
        mTvTitle = (TextView) findViewById(R.id.title);
        mHelp = (TextView) findViewById(R.id.help);

        mIvPayIcon = (ImageView) findViewById(R.id.pay_icon);
        mTvPayDesc = (TextView) findViewById(R.id.pay_description);

        contact = (TextView) this.findViewById(R.id.contact);
        contact.setOnClickListener(this);

        mMoneySpinner = (LinearLayout) findViewById(R.id.money_spinner_layout);
        mMoneySpinner.setOnClickListener(this);
        mMoneySpinnerText = (TextView) findViewById(R.id.money_spinner_text);
        mMoneySpinnerIcon = (ImageView) findViewById(R.id.money_spinner_icon);

        mBtnTopup = (Button) findViewById(R.id.submit);
        mBtnTopup.setOnClickListener(this);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    private void delayInit() {
        mHelp.setVisibility(View.GONE);
        mTvTitle.setText("财付通充值");
        mIvPayIcon.setImageResource(R.drawable.icon_tencent_pay);
        mTvPayDesc.setText("财付通充值（支持财付通账号和部分银行卡信用卡充值）");

        mMoneySpinnerText.setText(Html.fromHtml(mTopupControl.createMoneyShow(MONEY[mMoneyIndex])));
        contact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));
    }

    private void showPopupViews() {
        mMoneyDataArray[0].setSelectedIndex(mMoneyIndex);

        mPpwWaySelect = new SelecteTypePopupWindow(TencentPayTopup.this, mMoneyDataArray);
        mPpwWaySelect.init();
        mPpwWaySelect.setPopupSelectTypeListener(this);

        mPpwWaySelect.showAsDropDown(mMoneySpinner);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            mTopupControl.toHelp();
        }
        else if (v.getId() == R.id.submit) {
            mTopupControl.submitTopup(MONEY[mMoneyIndex]);
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.money_spinner_layout) {
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

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void selecteType(int type, int index) {
        mMoneyIndex = index;

        mMoneySpinnerText.setText(Html.fromHtml(mTopupControl.createMoneyShow(MONEY[mMoneyIndex])));
        mPpwWaySelect.dismiss();
    }
}
