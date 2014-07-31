package com.haozan.caipiao.activity.topup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.UnionPayCreditCardTopupControl;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.security.DESPlus;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;

/**
 * 银联信用卡语音充值
 * 
 * @author peter_wang
 * @create-time 2013-9-18 上午11:47:52
 */
public class UnionPayCreditCardTopup
    extends BasicActivity
    implements OnClickListener, PopMenuButtonClickListener {
    public static final int SHOW_SUBMIT_PROGRESS_DIALOG = 1;
    private String[] MONEY = {"50", "100", "200", "300", "500", "1000"};

    private String mTopupMoney = MONEY[0];
    private int mMoneyIndex = 0;
    private String mBankCardNum = null;

    // true：信息需要保存在本地 false：信息不需要保存在本地
    private boolean isSaveCard = true;

    // 顶部标题栏
    private ImageButton mIbHelp;

    // 文本输入框：卡号，金额，手机号
    private EditText mEtReservedNum;
    private EditText mEtBankCardNum;

    private RelativeLayout mLayoutMoney;
    private TextView mTvMoney;

    private CheckBox mCbSaveCard;
    private Button mBtnSubmit;

    private TextView mContact;

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private UnionPayCreditCardTopupControl mTopupControl;

    private boolean isSaveed = false;
    // 充值额菜单
    private PopMenu titlePopup;
    private int screenWidth;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler
        extends WeakReferenceHandler<UnionPayCreditCardTopup> {

        public MyHandler(UnionPayCreditCardTopup reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(UnionPayCreditCardTopup activity, Message msg) {
            switch (msg.what) {
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

                default:
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_unionpay_creditcard);
        setupViews();
        init();
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
                                                                                                mTopupControl.toTopup(mBankCardNum,
                                                                                                                      mTopupMoney,
                                                                                                                      address,
                                                                                                                      mEtReservedNum.getText().toString().trim());
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

    private void initData() {
        mTopupControl = new UnionPayCreditCardTopupControl(this, mHandler);
    }

    private void saveData() {
        String creditCardNum = DESPlus.enCode(mEtBankCardNum.getText().toString().trim());// 加密后保存，跟登录密码加密算法一样

        databaseData.putString("credit_card_num", creditCardNum);
        databaseData.putBoolean("isSave", true);
        databaseData.putString("phone_num", appState.getUsername());
        databaseData.putString("reserved_phone", mEtReservedNum.getText().toString().trim());
        databaseData.commit();
    }

    private void clearData() {
        databaseData.putInt("credit_card_money", 0);
        databaseData.putBoolean("isSave", false);
        databaseData.putString("phone_num", "0");
        databaseData.putString("reserved_phone", "0");
        databaseData.commit();
    }

    private void getSavedData() {
        String phone_card = preferences.getString("phone_num", "0");
        isSaveed = preferences.getBoolean("isSave", false);
        String preserveNum = preferences.getString("reserved_phone", "0");

        String creditCardNum = preferences.getString("credit_card_num", null);

        // 如果其他用户登录就会清除上一个用户的信息
        if (!appState.getUsername().equals("0") && !phone_card.equals(appState.getUsername())) {
            clearData();
        }
        else {
            // 解密
            if (creditCardNum != null && creditCardNum.length() > 16) {
                creditCardNum = DESPlus.deCode(creditCardNum);
            }

            mEtBankCardNum.setText(creditCardNum);
            mEtBankCardNum.requestFocus();
        }

        if (preserveNum.equals("0")) {
            String registerType = appState.getRegisterType();
            if ("1".equals(registerType)) {
                mEtReservedNum.setText(appState.getUsername());
            }
            else {
                mEtReservedNum.setText(appState.getReservedPhone());
            }
        }

        else
            mEtReservedNum.setText(preserveNum);
    }

    private void init() {
        if (this.getIntent().getExtras() != null) {
            mMoneyIndex = this.getIntent().getExtras().getInt("money_index_num");
            mTopupMoney = MONEY[mMoneyIndex];
        }

        // 获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mTvMoney.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + mTopupMoney + "元"));

        mContact.setOnClickListener(this);
        mContact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        getSavedData();

        mCbSaveCard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isSaveCard = true;
                else {
                    isSaveCard = false;
                    if (isSaveed && TextUtils.isEmpty(mEtBankCardNum.getText().toString()) == false) {
                        showClearPawDialog();
                    }
                }
            }
        });

    }

    protected void showClearPawDialog() {
        CustomDialog dlgSuccess = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setWarning().setMessage("现将清空您原来保存的信息，确定清空吗？").setPositiveButton("确  定",
                                                                                       new DialogInterface.OnClickListener() {
                                                                                           public void onClick(DialogInterface dialog,
                                                                                                               int which) {
                                                                                               dialog.dismiss();
                                                                                               clearData();
                                                                                               mEtBankCardNum.setText(null);
                                                                                               mEtReservedNum.setText(null);
                                                                                           }
                                                                                       }).setNegativeButton("取  消",
                                                                                                            new DialogInterface.OnClickListener() {
                                                                                                                public void onClick(DialogInterface dialog,
                                                                                                                                    int which) {
                                                                                                                    dialog.dismiss();
                                                                                                                }
                                                                                                            });
        dlgSuccess = customBuilder.create();
        dlgSuccess.show();
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int last_index) {
        titlePopup = new PopMenu(UnionPayCreditCardTopup.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1, screenWidth - 20, last_index, false,
                             true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - mLayoutMoney.getWidth() + mLayoutMoney.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    private void setupViews() {
        mContact = (TextView) this.findViewById(R.id.contact);
        mLayoutMoney = (RelativeLayout) this.findViewById(R.id.topup_money_layout);
        mLayoutMoney.setOnClickListener(this);
        mTvMoney = (TextView) this.findViewById(R.id.topup_money_text);

        mIbHelp = (ImageButton) this.findViewById(R.id.help);
        mIbHelp.setOnClickListener(this);
        mBtnSubmit = (Button) findViewById(R.id.submit);
        mBtnSubmit.setOnClickListener(this);

        mEtBankCardNum = (EditText) findViewById(R.id.bank_card_num_et);
        mEtReservedNum = (EditText) findViewById(R.id.user_connect_phone);

        mCbSaveCard = (CheckBox) findViewById(R.id.recharge_inf_save_checkBox);

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
                if (isSaveCard)
                    saveData();

                mBankCardNum = mEtBankCardNum.getText().toString();

                mTopupControl.toTopup(mBankCardNum, mTopupMoney, null,
                                      mEtReservedNum.getText().toString().trim());
            }
        }
        else if (v.getId() == R.id.topup_money_layout) {
            showPopupViews(mLayoutMoney, MONEY, mMoneyIndex);
        }
        else if (v.getId() == R.id.btn_contact_customer_service) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.btn_more_top_up_way) {
            mTopupControl.toAllTopupWay();
            finish();
        }
    }

    private boolean checkInput() {
        String warning = null;
        if (mEtBankCardNum.isShown() && mEtBankCardNum.getText().toString().equals("")) {
            warning = "卡号不能为空！";
            mEtBankCardNum.requestFocus();
        }
        else if (mEtBankCardNum.isShown() &&
            !(mEtBankCardNum.getText().toString().length() == 13 || mEtBankCardNum.getText().toString().length() == 16)) {
            warning = "您输入的号码不正确，请输入13,16位的正确卡号";
            mEtBankCardNum.requestFocus();
        }
        else if (mEtReservedNum.getText().toString().trim().equals("")) {
            warning = "预留手机号不能为空";
            mEtReservedNum.requestFocus();
        }
        else if (mEtReservedNum.getText().toString().trim().length() < 7) {
            warning = "预留手机号位数不对";
            mEtReservedNum.requestFocus();
        }
        else if (mEtReservedNum.getText().toString().trim().length() > 15) {
            warning = "预留手机号位数不对";
            mEtReservedNum.requestFocus();
        }

        if (warning == null) {
            return true;
        }
        else {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        mTopupMoney = MONEY[index];
        mTvMoney.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + mTopupMoney + "元"));
        titlePopup.dismiss();
        mMoneyIndex = index;
    }
}
