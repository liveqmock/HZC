package com.haozan.caipiao.activity.topup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.ChinaUnionOldPayTopupControl;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;
import com.unionpay.upomp.lthj.plugin.ui.PayActivity;

/**
 * 银联插件充值
 * 
 * @author peter_wang
 * @create-time 2013-11-8 上午8:48:34
 */
public class ChinaUnionPaycharge
    extends BasicActivity
    implements OnClickListener, PopupSelectTypeClickListener {
    private ImageButton help;
    private TextView contact;

    private LinearLayout eposSpinner;
    private TextView eposSpinnerText;
    private ImageView eposSpinnerIcon;

    private Button btnTopup;
    private String[] eposMoneyToServer = {"20", "50", "100", "200", "300", "500", "1000"};
    private String rechargeMoney;
    private int selectedIndex = 0;
    private boolean fromFast = false;
    private String bankName;
    private String type;

    private SelecteTypePopupWindow popupWindowWaySelect;
    private BetShowLotteryWay[] wayDataArray;

    private String mOrderId;

    private Button mBtnContackServer;
    private Button mBtnOtherTopup;

    private ChinaUnionOldPayTopupControl mTopupControl;

    private MyHandler mHandler;

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;
    private static String mMode = "01";
    private static String tn = "";
    private static String orderNumber = "";

    private static class MyHandler
        extends WeakReferenceHandler<ChinaUnionPaycharge> {

        public MyHandler(ChinaUnionPaycharge reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(ChinaUnionPaycharge activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.SHOW_PROGRESS:
                    activity.showProgress();
                    break;

                case ControlMessage.DISMISS_PROGRESS:
                    activity.dismissProgress();
                    break;

                case ControlMessage.UNIONPAY_PLUGIN_TOPUP_SUCCESS:
                    activity.mOrderId = (String) msg.obj;
//                    activity.mTopupControl.callTopupPlugin(activity.mOrderId);
                    tn = RequestResultAnalyse.getData((String) msg.obj, "tn");
                    orderNumber = RequestResultAnalyse.getData((String) msg.obj, "orderNumber");
                    /************************************************* 
                     * 
                     *  步骤2：通过银联工具类启动支付插件 
                     *  
                     ************************************************/
                    doStartUnionPayPlugin(activity, tn, mMode);
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
        setContentView(R.layout.activity_new_union_topup);
        setupViews();
        init();
    }

    private void initData() {
        mHandler = new MyHandler(this);

        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();

        String[] moneyText = new String[eposMoneyToServer.length];
        for (int i = 0; i < eposMoneyToServer.length; i++) {
            moneyText[i] = eposMoneyToServer[i] + "元";
        }
        lotteryway.setUpsInf(moneyText);
        wayDataArray[0] = lotteryway;

        mTopupControl = new ChinaUnionOldPayTopupControl(this, mHandler);
    }

    private void init() {
        rechargeMoney = eposMoneyToServer[0];

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            selectedIndex = bundle.getInt("money_index_num");
            fromFast = bundle.getBoolean("from_fast");
            rechargeMoney = eposMoneyToServer[selectedIndex];
            bankName = bundle.getString("bank_name");
            type = bundle.getString("type");
        }
        eposSpinnerText.setText(Html.fromHtml(mTopupControl.createMoneyShow(rechargeMoney)));

        contact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));

        if (fromFast) {
            mTopupControl.toTopup(bankName, type, rechargeMoney);
        }
    }

    private void setupViews() {
        setupMainViews();
    }

    private void setupMainViews() {
        contact = (TextView) this.findViewById(R.id.contact);
        contact.setOnClickListener(this);
        eposSpinner = (LinearLayout) findViewById(R.id.epos_spinner);
        eposSpinner.setOnClickListener(this);
        eposSpinnerText = (TextView) findViewById(R.id.epos_spinner_text);
        eposSpinnerIcon = (ImageView) findViewById(R.id.epos_spinner_icon);
        help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(this);
        btnTopup = (Button) findViewById(R.id.submit);
        btnTopup.setOnClickListener(this);

        mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
        mBtnContackServer.setOnClickListener(this);
        mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
        mBtnOtherTopup.setOnClickListener(this);
    }

    private void showPopupViews() {
        wayDataArray[0].setSelectedIndex(selectedIndex);

        popupWindowWaySelect = new SelecteTypePopupWindow(ChinaUnionPaycharge.this, wayDataArray);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);

        popupWindowWaySelect.showAsDropDown(eposSpinner);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.help) {
            mTopupControl.toHelp();
        }
        else if (v.getId() == R.id.submit) {	//点击充值
            mTopupControl.toTopup(bankName, type, rechargeMoney);
        }
        else if (v.getId() == R.id.contact) {
            mTopupControl.toFeedback();
        }
        else if (v.getId() == R.id.epos_spinner) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 插件返回支付信息的**回调函数**
//        mTopupControl.onPayResult(mOrderId, requestCode, resultCode, data);
         //处理银联手机支付控件返回的支付结果 
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel
         *      分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void submitData() {
        mTopupControl.submitDataStatisticsOpenTopup();
    }

    @Override
    public void selecteType(int type, int index) {
        rechargeMoney = eposMoneyToServer[index];
        eposSpinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        selectedIndex = index;
        popupWindowWaySelect.dismiss();
    }

    int startpay(Activity act, String tn, int serverIdentifier) {
        return 0;
    }
    
    public static void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
//        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
//                tn, mode);
    }
}
