package com.haozan.caipiao.activity.topup;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.PhoneCardTopupControl;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.TextUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;

public class PhoneCardRecharge
    extends BasicActivity
    implements OnClickListener, PopupSelectTypeClickListener {
    private static final String[] PHONECARD_TYPE = {"SZX", "UNICOM", "TELECOM"};
    private static final String[] PHONECARD_TYPE_DESC = {"中国移动", "中国联通", "中国电信"};
    private static final String[] MONEY = {"10", "20", "30", "50", "100", "300", "500"};
    private static final String[] COLLECTED_MONEY = {"9元", "18元", "28元", "47元", "95元", "285元", "475元"};

    private static final String[] WARNING_INF = {"本产品支持中国移动的全国卡、江苏卡、浙江卡、福建卡、广东卡、辽宁卡（不支持彩铃充值卡和短信充值卡）进行充值。",
            "本产品支持中国联通的全国卡（卡号15位，密码19位）进行充值。", "本产品支持中国电信的全国卡（卡号为19位、密码18位）进行充值。"};

    private static final int PHONECARD_TYPE_SELECTED = 0;
    private static final int MONEY_SELECTED = 1;

    private RelativeLayout layoutPhoneCardType;
    private RelativeLayout layoutMoney;
    private TextView tvPhoneCardType;
    private TextView tvMoney;
    private ImageView ivPhoneCardType;
    private ImageView ivMoneyIcon;
    private EditText etCardNum;
    private EditText etCardPw;
    private TextView tvAccountedMoney;
    private TextView tvContact;

    private ImageButton help;
    private Button btnTopup;

    // 弹出充值确认对话框
    private TextView tvDlgPhonecardType;
    private TextView tvDlgCardNum;
    private TextView tvDlgCardPw;
    private TextView tvDlgMoney;
    private TextView tvDlgTips;
    private CustomDialog dlgConfirm;

    private String topupCardtype = PHONECARD_TYPE_DESC[0];
    private String topupCardTypeCode = PHONECARD_TYPE[0];
    private String topupMoney01 = MONEY[0];
    private String topupRealMoney01 = COLLECTED_MONEY[0];

    private SelecteTypePopupWindow popupWindowWaySelect;
    private BetShowLotteryWay[] topupWayArray;
    private BetShowLotteryWay[] topupMoneyArray;
    // 选中卡类型还是金额
    private int popupType;
    // 卡类型
    private int phonecardWayIndex = 0;
    // 金额类型
    private int moneyIndex = 0;

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private PhoneCardTopupControl mTopupControl;

    private MyHandler mHandler;

    private static class MyHandler
        extends WeakReferenceHandler<PhoneCardRecharge> {

        public MyHandler(PhoneCardRecharge reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(PhoneCardRecharge activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.SHOW_PROGRESS:
                    activity.showProgress();
                    break;

                case ControlMessage.DISMISS_PROGRESS:
                    activity.dismissProgress();
                    break;

                case ControlMessage.UNIONPAY_PLUGIN_TOPUP_SUCCESS:
                    activity.mTopupControl.showSuccessDialog("充值已提交，请两分钟后查询消息盒充值结果,如需帮助请联系客服。");
                    break;

                case ControlMessage.TOPUP_FAIL:
                    if (ActionUtil.checkIfLogoff(activity, msg.arg1) == false) {
                        activity.mTopupControl.payFail((String) msg.obj);
                    }
                    break;

                case ControlMessage.FINISH_ACTIVITY:
                    activity.finish();
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
        setContentView(R.layout.phone_card_recharge);
        setupViews();
        init();
    }

    private void initData() {
        mHandler = new MyHandler(this);

        topupWayArray = new BetShowLotteryWay[1];
        BetShowLotteryWay way = new BetShowLotteryWay();
        way.setUpsInf(PHONECARD_TYPE_DESC);
        topupWayArray[0] = way;

        topupMoneyArray = new BetShowLotteryWay[1];
        BetShowLotteryWay money = new BetShowLotteryWay();

        String[] moneyText = new String[MONEY.length];
        for (int i = 0; i < MONEY.length; i++) {
            moneyText[i] = MONEY[i] + "元";
        }
        money.setUpsInf(moneyText);
        topupMoneyArray[0] = money;

        mTopupControl = new PhoneCardTopupControl(this, mHandler);
    }

    private void init() {
        tvContact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));
        if (this.getIntent().getExtras() != null) {
            phonecardWayIndex = this.getIntent().getExtras().getInt("money_index_num");
            topupMoney01 = MONEY[phonecardWayIndex];
            topupRealMoney01 = COLLECTED_MONEY[phonecardWayIndex];
        }

        tvPhoneCardType.setText(Html.fromHtml("<font color='#9D9D9D'>类型：</font>" + topupCardtype));
        tvMoney.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + topupMoney01 + "元"));
        tvAccountedMoney.setText("到账金额:" + topupRealMoney01);
    }

    private void setupViews() {
        tvContact = (TextView) this.findViewById(R.id.contact);
        tvContact.setOnClickListener(this);
        help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(this);
        tvAccountedMoney = (TextView) this.findViewById(R.id.real_money);

        layoutPhoneCardType = (RelativeLayout) findViewById(R.id.rechspinner01);
        layoutPhoneCardType.setOnClickListener(this);
        layoutMoney = (RelativeLayout) findViewById(R.id.rechspinner02);
        layoutMoney.setOnClickListener(this);
        tvPhoneCardType = (TextView) findViewById(R.id.rechspinner01_text);
        tvMoney = (TextView) findViewById(R.id.rechspinner02_text);
        ivPhoneCardType = (ImageView) findViewById(R.id.rechspinner01_icon);
        ivMoneyIcon = (ImageView) findViewById(R.id.rechspinner02_icon);

        etCardNum = (EditText) this.findViewById(R.id.cardnum);
        etCardPw = (EditText) this.findViewById(R.id.cardpw);

        btnTopup = (Button) this.findViewById(R.id.submit);
        btnTopup.setOnClickListener(this);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    protected void refreshMoney() {
        tvAccountedMoney.setText("到账金额:" + COLLECTED_MONEY[moneyIndex]);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            mTopupControl.toHelp();
        }
        else if (v.getId() == R.id.submit) {
            if (HttpConnectUtil.isNetworkAvailable(PhoneCardRecharge.this)) {
                if (checkInput()) {
                    showConfirmDialog();
                }
            }
            else {
                ViewUtil.showTipsToast(this, getResources().getString(R.string.network_not_avaliable));
            }
        }
        else if (v.getId() == R.id.dlg_submit) {
            mTopupControl.toTopup(topupCardTypeCode, topupMoney01, etCardNum.getText().toString(),
                                  etCardPw.getText().toString());

            dlgConfirm.dismiss();
        }
        else if (v.getId() == R.id.dlg_cancle) {
            dlgConfirm.dismiss();
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.rechspinner01) {
            popupType = PHONECARD_TYPE_SELECTED;
            showPopupViews();
        }
        else if (v.getId() == R.id.rechspinner02) {
            popupType = MONEY_SELECTED;
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

    private void showConfirmDialog() {
        if (dlgConfirm == null) {
            View eventview = View.inflate(this, R.layout.dlg_phone_card_recharge_make_sure, null);

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
            dlgConfirm = customBuilder.setWarning().setTitle("充值确认").setContentView(eventview).create();

            tvDlgMoney = (TextView) eventview.findViewById(R.id.dialog_item_second);
            tvDlgMoney.setVisibility(View.VISIBLE);
            TextUtil.setTextBold(tvDlgMoney);

            tvDlgPhonecardType = (TextView) eventview.findViewById(R.id.phonecard_type);
            tvDlgPhonecardType.setVisibility(View.VISIBLE);
            TextUtil.setTextBold(tvDlgPhonecardType);

            tvDlgTips = (TextView) eventview.findViewById(R.id.tips);
            tvDlgTips.setVisibility(View.VISIBLE);

            tvDlgCardNum = (TextView) eventview.findViewById(R.id.cardnum);
            tvDlgCardNum.setVisibility(View.VISIBLE);
            TextUtil.setTextBold(tvDlgCardNum);

            tvDlgCardPw = (TextView) eventview.findViewById(R.id.card_password);
            tvDlgCardPw.setVisibility(View.VISIBLE);
            TextUtil.setTextBold(tvDlgCardPw);

            Button btnSubmit = (Button) eventview.findViewById(R.id.dlg_submit);
            btnSubmit.setOnClickListener(this);
            Button btnCancle = (Button) eventview.findViewById(R.id.dlg_cancle);
            btnCancle.setOnClickListener(this);
        }

        insertblank();
        tvDlgMoney.setText(topupMoney01 + "元");
        tvDlgPhonecardType.setText(topupCardtype);
        tvDlgTips.setText(WARNING_INF[phonecardWayIndex]);

        dlgConfirm.show();
    }

    private void showPopupViews() {
        if (popupType == PHONECARD_TYPE_SELECTED) {
            topupWayArray[0].setSelectedIndex(phonecardWayIndex);

            popupWindowWaySelect = new SelecteTypePopupWindow(PhoneCardRecharge.this, topupWayArray);
            popupWindowWaySelect.init();
            popupWindowWaySelect.setPopupSelectTypeListener(this);

            popupWindowWaySelect.showAsDropDown(layoutPhoneCardType);
        }
        else if (popupType == MONEY_SELECTED) {
            topupMoneyArray[0].setSelectedIndex(moneyIndex);

            popupWindowWaySelect = new SelecteTypePopupWindow(PhoneCardRecharge.this, topupMoneyArray);
            popupWindowWaySelect.init();
            popupWindowWaySelect.setPopupSelectTypeListener(this);

            popupWindowWaySelect.showAsDropDown(layoutMoney);
        }
    }

    private void insertblank() {
        StringBuilder sbNum = new StringBuilder();
        StringBuilder sbCode = new StringBuilder();
        String number = etCardNum.getText().toString();
        String code = etCardPw.getText().toString();
        sbNum.append(number);
        sbNum.insert(4, " ");
        for (int i = 9; i < sbNum.length(); i += 5) {
            sbNum.insert(i, " ");
        }
        sbCode.append(code);
        sbCode.insert(4, " ");
        for (int i = 9; i < sbCode.length(); i += 5) {
            sbCode.insert(i, " ");
        }
        tvDlgCardNum.setText(sbNum.toString());
        tvDlgCardPw.setText(sbCode.toString());
    }

    private boolean checkInput() {
        String number = etCardNum.getText().toString();
        String code = etCardPw.getText().toString();
        String inf = null;
        if (number.length() < 15) {
            inf = "请输入15到21位之间的充值卡号码";
            etCardNum.requestFocus();
            etCardNum.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
            etCardPw.setError(null);
        }
        else if (code.length() < 15) {
            inf = "请输入15到21位之间的充值卡卡密";
            etCardPw.requestFocus();
            etCardPw.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
            etCardNum.setError(null);
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void selecteType(int type, int index) {
        if (popupType == PHONECARD_TYPE_SELECTED) {
            phonecardWayIndex = index;
            topupCardtype = PHONECARD_TYPE_DESC[index];
            topupCardTypeCode = PHONECARD_TYPE[index];

            tvPhoneCardType.setText(Html.fromHtml("<font color='#9D9D9D'>类型：</font>" + topupCardtype));
        }
        else if (popupType == MONEY_SELECTED) {
            moneyIndex = index;
            topupMoney01 = MONEY[index];
            topupRealMoney01 = COLLECTED_MONEY[index];

            tvMoney.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + topupMoney01 + "元"));
            refreshMoney();
        }

        popupWindowWaySelect.dismiss();
    }
}
