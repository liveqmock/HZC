package com.haozan.caipiao.activity.topup;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.UnionPayDebitCardTopupControl;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.CommonUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;

/**
 * 银联储蓄卡语音充值
 * 
 * @author peter_wang
 * @create-time 2013-9-18 上午11:45:46
 */
public class UnionpayDebitCardTopup
    extends BasicActivity
    implements OnClickListener, PopMenuButtonClickListener {
    public static final int SHOW_GET_USERINF_PROGRESS_DIALOG = 0;
    public static final int SHOW_SUBMIT_PROGRESS_DIALOG = 1;

    private static final String[] MONEY = {"20", "50", "100", "200", "300", "500", "1000"};
    private static final String[] MONEY_BIND = {"50", "100", "200", "300", "500", "1000"};
    private static final String[] availableProvinces = {"安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南",
            "河南", "河北", "黑龙江", "湖南", "湖北", "江苏", "江西", "吉林", "辽宁", "宁夏", "内蒙古", "上海", "山东", "四川", "山西", "陕西",
            "天津", "青海", "西藏", "新疆", "云南", "浙江"};

    private String[] mTopupMoney = MONEY_BIND;
    private int mMoneyIndex = 0;

    private int mProvinceIndex = 0;
    private int mCityIndex = 0;

    private String mBankCardNum = null;

    // 未绑定银行卡的显示
    private LinearLayout mLayoutInputBankInf;
    private EditText mEtBankCardNum;
    private RelativeLayout mLayoutProvince;
    private TextView mTvProvince;
    private RelativeLayout mLayoutCity;
    private TextView mTvCity;
    // 绑定银行卡的显示
    private LinearLayout mLayoutShowBankInf;
    private TextView mTvBankCardInf;
    private RelativeLayout mLayoutTopupMoney;
    private TextView mTvTopupMoney;
    private Button mBtnSubmit;

    private ImageButton mIbHelp;

    // 银联充值需要4个参数
    private String province = availableProvinces[0];
    private String location = LotteryUtils.initProvinceCityMap(province)[0];
    private String rechargeMoney = MONEY[0];

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private UnionPayDebitCardTopupControl mTopupControl;

    private TextView mContact;
    // 省份,城市，充值额菜单
    private PopMenu titlePopup;
    private int buttonClickId;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler
        extends WeakReferenceHandler<UnionpayDebitCardTopup> {

        public MyHandler(UnionpayDebitCardTopup reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(UnionpayDebitCardTopup activity, Message msg) {
            switch (msg.what) {
                case SHOW_GET_USERINF_PROGRESS_DIALOG:
                    activity.showProgressDialog(activity.getString(R.string.get_user_inf));
                    break;

                case SHOW_SUBMIT_PROGRESS_DIALOG:
                    activity.showProgressDialog(activity.getString(R.string.topup_submit_request));
                    break;

                case ControlMessage.UNIONPAY_VOICE_TOPUP_SUCCESS:
                    activity.dismissProgressDialog();
                    activity.mTopupControl.showSuccessDialog(activity.getString(R.string.unionpay_voice_topup_success) +
                        LotteryUtils.getConnectionQQ(activity));
                    break;

                case ControlMessage.TOPUP_FAIL:
                    activity.dismissProgressDialog();
                    if (ActionUtil.checkIfLogoff(activity, msg.arg1) == false) {
                        // 充值失败返回需要填写户籍地址，显示户籍地址显示
                        if (msg.arg1 == 203) {
                            activity.showAddAddressDialog();
                        }
                        else {
                            activity.mTopupControl.payFail((String) msg.obj);
                        }
                    }
                    break;

                case ControlMessage.FINISH_ACTIVITY:
                    activity.finish();
                    break;

                case ControlMessage.USER_INF_SUCCESS_RESULT:
                    activity.dismissProgressDialog();
                    activity.mBtnSubmit.setEnabled(true);

                    activity.analyseUserInf((String) msg.obj);
                    break;

                case ControlMessage.USER_INF_FAIL_RESULT:
                    activity.dismissProgressDialog();
                    activity.mBtnSubmit.setEnabled(true);

                    if (ActionUtil.checkIfLogoff(activity, msg.arg1) == false) {
                        ViewUtil.showTipsToast(activity, (String) msg.obj);
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_unionpay_debitcard);
        setupViews();
        init();
    }

    protected void analyseUserInf(String data) {
        try {
            JSONArray hallArray = new JSONArray(data);
            JSONObject jo = hallArray.getJSONObject(0);
            int bandState = jo.getInt("is_band");
            String banCard = jo.getString("bankcard");
            String banCity = jo.getString("bank_city");
            appState.setBand(bandState);
            // 账号绑定
            if (appState.getBand() == 1) {
                mTvBankCardInf.setText("卡号：" + banCard);
                mLayoutInputBankInf.setVisibility(View.GONE);
                mLayoutShowBankInf.setVisibility(View.VISIBLE);

                mBankCardNum = banCard;
                mTopupMoney = MONEY_BIND;
                province = availableProvinces[0];
                location = LotteryUtils.initProvinceCityMap(province)[0];

                showMoney();
            }
            else {
                mLayoutInputBankInf.setVisibility(View.VISIBLE);
                mLayoutShowBankInf.setVisibility(View.GONE);

                mTopupMoney = MONEY;
            }

            rechargeMoney = mTopupMoney[mMoneyIndex];
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddAddressDialog() {
        View addAddressView = View.inflate(this, R.layout.dlg_topup_address, null);
        final EditText enterAdderss = (EditText) addAddressView.findViewById(R.id.user_address);

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setWarning().setContentView(addAddressView).setPositiveButton("确  定",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        public void onClick(DialogInterface dialog,
                                                                                                            int which) {
                                                                                            String address =
                                                                                                enterAdderss.getText().toString().trim();
                                                                                            if (checkDialog(address)) {
                                                                                                mTopupControl.toTopup(province,
                                                                                                                      location,
                                                                                                                      mBankCardNum,
                                                                                                                      rechargeMoney,
                                                                                                                      address);
                                                                                            }
                                                                                            dialog.dismiss();

                                                                                        }
                                                                                    }).setNegativeButton("取  消",
                                                                                                         new DialogInterface.OnClickListener() {
                                                                                                             public void onClick(DialogInterface dialog,
                                                                                                                                 int which) {
                                                                                                                 dialog.dismiss();
                                                                                                             }
                                                                                                         });
        customBuilder.create().show();
    }

    private void initData() {
        mTopupControl = new UnionPayDebitCardTopupControl(this, mHandler);
    }

    private void init() {
        mContact.setOnClickListener(this);
        mContact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        if (this.getIntent().getExtras() != null) {
            mMoneyIndex = this.getIntent().getExtras().getInt("mMoneyIndex");
        }

        mTopupControl.getUserInf();

        showProvince();
        showMoney();
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int lastIndex) {
        titlePopup = new PopMenu(UnionpayDebitCardTopup.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1,
                             CommonUtil.getScreenWdith(this) - 20, lastIndex, false, true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - mLayoutProvince.getWidth() + mLayoutProvince.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    private void setupViews() {
        // 未绑定状态显示的view
        mLayoutInputBankInf = (LinearLayout) this.findViewById(R.id.debit_card_input_layout);
        mEtBankCardNum = (EditText) this.findViewById(R.id.bank_card_num_et);
        mLayoutProvince = (RelativeLayout) this.findViewById(R.id.province_layout);
        mLayoutProvince.setOnClickListener(this);
        mTvProvince = (TextView) this.findViewById(R.id.provinces_text);
        mLayoutCity = (RelativeLayout) this.findViewById(R.id.city_layout);
        mLayoutCity.setOnClickListener(this);
        mTvCity = (TextView) this.findViewById(R.id.city_text);

        // 绑定状态显示的view
        mLayoutShowBankInf = (LinearLayout) this.findViewById(R.id.bind_card_layout);
        mTvBankCardInf = (TextView) this.findViewById(R.id.bank_card_num_tv);

        mLayoutTopupMoney = (RelativeLayout) this.findViewById(R.id.topup_money_layout);
        mLayoutTopupMoney.setOnClickListener(this);
        mTvTopupMoney = (TextView) this.findViewById(R.id.topup_money_text);

        mContact = (TextView) this.findViewById(R.id.contact);

        mBtnSubmit = (Button) findViewById(R.id.submit);
        mBtnSubmit.setOnClickListener(this);

        mIbHelp = (ImageButton) this.findViewById(R.id.help);
        mIbHelp.setOnClickListener(this);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            mTopupControl.toHelp();
        }
        else if (v.getId() == R.id.submit) {
            if (checkInput()) {
                mBankCardNum = mEtBankCardNum.getText().toString();

                mTopupControl.toTopup(province, location, mBankCardNum, rechargeMoney, null);
            }
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.province_layout) {
            showPopupViews(mLayoutProvince, availableProvinces, mProvinceIndex);
            buttonClickId = R.id.province_layout;
        }
        else if (v.getId() == R.id.city_layout) {
            showPopupViews(mLayoutCity, LotteryUtils.initProvinceCityMap(province), mCityIndex);
            buttonClickId = R.id.city_layout;
        }
        else if (v.getId() == R.id.topup_money_layout) {
            showPopupViews(mLayoutTopupMoney, mTopupMoney, mMoneyIndex);
            buttonClickId = R.id.topup_money_layout;
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    // 提交用户的居住的地址
    private boolean checkDialog(String address) {
        String warning = null;
        if (address.equals("")) {
            warning = "请输入住址！";
        }
        else if (address.matches("^[\u4e00-\u9fa5]{0,}$") == false) {
            warning = "请输入以汉字描述的住址";
        }

        if (warning == null) {
            return true;
        }
        else {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
    }

    // 检查所有输入框是否输入内容且内容是否符合规范
    private boolean checkInput() {
        String warning = null;
        String num = mEtBankCardNum.getText().toString();

        if (mEtBankCardNum.isShown() && num.equals("")) {
            warning = "请输入银行卡号";
            mEtBankCardNum.requestFocus();
            ViewUtil.showTipsToast(this, warning);
        }

        if (warning == null)
            return true;
        else
            return false;
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    private void showProvince() {
        mTvProvince.setText(Html.fromHtml("<font color='#988d97'>省份：</font>" + province));
        location = LotteryUtils.initProvinceCityMap(province)[0];
        showCity();
    }

    private void showCity() {
        mTvCity.setText(Html.fromHtml("<font color='#988d97'>城市：</font>" + location));
    }

    private void showMoney() {
        mTvTopupMoney.setText(Html.fromHtml("<font color='#988d97'>金额：</font>" + rechargeMoney + "元"));
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        if (buttonClickId == R.id.province_layout) {
            province = availableProvinces[index];
            mProvinceIndex = index;

            showProvince();
        }
        else if (buttonClickId == R.id.city_layout) {
            location = LotteryUtils.initProvinceCityMap(province)[index];
            mCityIndex = index;

            showCity();
        }
        else if (buttonClickId == R.id.topup_money_layout) {
            rechargeMoney = mTopupMoney[index];
            mMoneyIndex = index;

            showMoney();
        }
        titlePopup.dismiss();
    }
}
