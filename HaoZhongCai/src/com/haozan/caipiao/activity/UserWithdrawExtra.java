package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.userinf.WithdrawHistory;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.ZhongguoCityName;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PopUpEditText;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.PopUpEditText.EditTextTextWatcher;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;
import com.umeng.analytics.MobclickAgent;

public class UserWithdrawExtra
    extends BasicActivity
    implements OnClickListener, OnGetUserInfListener, EditTextTextWatcher, PopupSelectTypeClickListener {
    public static final String SELECT_BANK = "点击选择银行";
    private String moneyAmount;
    private String bankNumString;
    private String bankFullNameString;
    private String bankNameString;
    private String bankLocationString;
    private Double windrawMoney;

    private LinearLayout body;

    private TextView bankLocation;

    private TextView message;
    private ImageButton help;

    // 提现金额
    private PopUpEditText money;
    private Button moneyDeleButton;
    // 用户名
    private TextView name;
    // 可提现额
    private TextView account;
    // 银行卡号
    private TextView bankCardNum;
    // 身份证号
    private TextView userId;
    // 银行名称
    private RelativeLayout popUpEditextContainer;
    private TextView bankName;
    private TextView bankNameTvTitle;

    // 银行网点
    private TextView bankFullName;
    private PopUpEditText bankFullNameEt;
    private LinearLayout bankFullNameTvLinear;
    private RelativeLayout bankFullNameEtLinear;
    private Button bankFullNameDeleButton;
    // 按钮
    private Button submit;
    private Button getInfAgain;
    private Button historyButton;

    // 对话框
    private TextView accountnamedlg;
    private TextView accountmoneydlg;
    private TextView accountbankcarnumdlg;
    private TextView accountbanknamedlg;
    private TextView accountbankfullnamedlg;
    private TextView accountbankcitydlg;

    private LayoutInflater inflater;
    private WithdrawTask withdrawTask;
    private ImageView line3;
    private ImageView line4;
    private ScrollView scrollView;
    // 银行名称下拉列表
    private BetShowLotteryWay[] wayDataArray;
    private SelecteTypePopupWindow popupWindowWaySelect;
    private View eventView;
    private EditText bankNameEdi;
// private PopMenu titlePopup;
    private int screenWidth;
    private String bankNameN = ZhongguoCityName.bankName[0];
    private LinearLayout bankNameSpinner;
    private TextView bankNameSpinnerText;
    private ImageView bankNameSpinnerIcon;
    private int last_index_num = 0;

    private View makeSureView;
    private CustomDialog dlgCheckInf;
    private CustomDialog dlgSubmitSuccessTips;
    private CustomDialog ediBankNameDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_withdraw_extra);
        setupViews();
        initData();
        init();
        this.getWindow().getDecorView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                money.setError(null);
                bankFullNameEt.setError(null);
                return false;
            }
        });
    }

    private void initData() {
        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();
        lotteryway.setUpsInf(ZhongguoCityName.bankName);
        wayDataArray[0] = lotteryway;
    }

    private void init() {
// bankNameSpinnerText.setText(bankNameN);
        // 获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        if (HttpConnectUtil.isNetworkAvailable(UserWithdrawExtra.this)) {
            UserInfTask getUserInf = new UserInfTask(UserWithdrawExtra.this);
            getUserInf.setOnGetUserInfListener(this);
            getUserInf.execute(1);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            message.setText(inf);
            message.setVisibility(View.VISIBLE);
            body.setVisibility(View.GONE);
        }
    }

    private void setupViews() {
        setupWholeViews();
        setupEdiBankNameViews();
        setupDialogViews();
    }

    private void setupWholeViews() {
        body = (LinearLayout) this.findViewById(R.id.body);
        message = (TextView) this.findViewById(R.id.message);
        name = (TextView) this.findViewById(R.id.user_name);
        userId = (TextView) this.findViewById(R.id.user_id);
        account = (TextView) this.findViewById(R.id.user_balance);

        money = (PopUpEditText) this.findViewById(R.id.user_withdraw_money);
        money.setTextWatcher(this);

        bankFullNameEt = (PopUpEditText) findViewById(R.id.bank_full_name_et);
        bankFullNameEt.setTextWatcher(this);

        popUpEditextContainer = (RelativeLayout) this.findViewById(R.id.bank_name_et_rela_container);

        historyButton = (Button) this.findViewById(R.id.history_button);
        historyButton.setOnClickListener(this);

        bankCardNum = (TextView) this.findViewById(R.id.bank_card_num_tv);
        bankName = (TextView) this.findViewById(R.id.bank_name_tv);
        bankFullName = (TextView) this.findViewById(R.id.bank_full_name_tv);
        bankLocation = (TextView) this.findViewById(R.id.bank_location_tv);
        bankNameTvTitle = (TextView) findViewById(R.id.bank_name_title_tv);

        help = (ImageButton) this.findViewById(R.id.chzhelp);
        help.setOnClickListener(this);

        submit = (Button) this.findViewById(R.id.rechargebt);
        submit.setOnClickListener(this);

        getInfAgain = (Button) this.findViewById(R.id.get_inf_again);
        getInfAgain.setOnClickListener(this);

        line3 = (ImageView) this.findViewById(R.id.line3);
        line4 = (ImageView) this.findViewById(R.id.line4);

        bankFullNameTvLinear = (LinearLayout) this.findViewById(R.id.bank_full_name_tv_linear);
        bankFullNameEtLinear = (RelativeLayout) this.findViewById(R.id.bank_full_name_et_linear);

        scrollView = (ScrollView) this.findViewById(R.id.scroll_view_withdraw_extra);
        // 银行名称菜单
        bankNameSpinner = (LinearLayout) this.findViewById(R.id.withdraw_extra_bank_name);
        bankNameSpinner.setOnClickListener(this);
        bankNameSpinnerText = (TextView) this.findViewById(R.id.withdraw_extra_bank_name_text);
        bankNameSpinnerIcon = (ImageView) this.findViewById(R.id.withdraw_extra_bank_name_icon);

        // 清空按钮
        moneyDeleButton = (Button) this.findViewById(R.id.user_withdraw_money_delete);
        moneyDeleButton.setOnClickListener(this);
        bankFullNameDeleButton = (Button) this.findViewById(R.id.bank_full_name_et_delete);
        bankFullNameDeleButton.setOnClickListener(this);
    }

    private void setupEdiBankNameViews() {
        inflater = LayoutInflater.from(UserWithdrawExtra.this);
        eventView = inflater.inflate(R.layout.edi_and_btn_view, null);
        bankNameEdi = (EditText) eventView.findViewById(R.id.edi);
    }

    private void setupDialogViews() {
        inflater = LayoutInflater.from(UserWithdrawExtra.this);
        makeSureView = inflater.inflate(R.layout.withdraw_extra_recharge_make_sure_dlg, null);
        accountnamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_first);
        accountmoneydlg = (TextView) makeSureView.findViewById(R.id.dialog_item_second);
        accountbankcarnumdlg = (TextView) makeSureView.findViewById(R.id.dialog_item_three);
        accountbanknamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_foth);
        accountbankfullnamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_fifth);
        accountbankcitydlg = (TextView) makeSureView.findViewById(R.id.dialog_item_sixth);
    }

    @Override
    public void onClick(View v) {
        isFirst = true;

        if (v.getId() == R.id.chzhelp) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putInt("help03", R.string.helpinf03);
            intent.putExtras(bundel);
            intent.setClass(UserWithdrawExtra.this, SubHelp.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.rechargebt) {
            if (checkInput()) {
                if (bankFullNameEt.isShown())
                    bankFullNameString = bankFullNameEt.getText().toString();
// if (bankNameEt.isShown())
// bankNameString = bankNameEt.getText().toString();
// else
// bankNameString = bankNameN;
                bankNameString = bankNameSpinnerText.getText().toString();
                accountnamedlg.setVisibility(View.GONE);
                accountmoneydlg.setText(money.getText().toString() + "元");
                accountbankcarnumdlg.setText(setStringLength(bankNumString));
                accountbanknamedlg.setText(bankNameString);
                accountbankfullnamedlg.setText(bankFullNameString);
                accountbankcitydlg.setText(bankLocationString);
                if (checkInput()) {
                    showConfirmDialog();
                }
            }
        }
        else if (v.getId() == R.id.get_inf_again) {
            if (HttpConnectUtil.isNetworkAvailable(UserWithdrawExtra.this)) {
                UserInfTask getUserInf = new UserInfTask(UserWithdrawExtra.this);
                getUserInf.setOnGetUserInfListener(this);
                getUserInf.execute(1);
            }
            else {
                String inf = getResources().getString(R.string.network_not_avaliable);
                message.setText(inf);
                message.setVisibility(View.VISIBLE);
                body.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.history_button) {
            Intent intent = new Intent();
            intent.setClass(UserWithdrawExtra.this, WithdrawHistory.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.withdraw_extra_bank_name) {
            showPopView();
        }
        else if (v.getId() == R.id.user_withdraw_money_delete) {
            if (money.getText().toString() != null) {
                money.setText(null);
                money.requestFocus();
            }
        }
        else if (v.getId() == R.id.bank_full_name_et_delete) {
            if (bankFullNameEt.getText().toString() != null) {
                bankFullNameEt.setText(null);
                bankFullNameEt.requestFocus();
            }
        }
    }

    private void showConfirmDialog() {
        if (dlgCheckInf == null) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
            customBuilder.setTitle("信息确认").setContentView(makeSureView).setPositiveButton("确  定",
                                                                                          new DialogInterface.OnClickListener() {
                                                                                              public void onClick(DialogInterface dialog,
                                                                                                                  int which) {
                                                                                                  withdrawTask =
                                                                                                      new WithdrawTask(
                                                                                                                       UserWithdrawExtra.this,
                                                                                                                       submit,
                                                                                                                       bankNumString,
                                                                                                                       bankFullNameString,
                                                                                                                       bankNameString,
                                                                                                                       bankLocationString);
                                                                                                  withdrawTask.execute(moneyAmount);
                                                                                                  dlgCheckInf.dismiss();
                                                                                              }
                                                                                          }).setNegativeButton("取  消",
                                                                                                               new DialogInterface.OnClickListener() {
                                                                                                                   public void onClick(DialogInterface dialog,
                                                                                                                                       int which) {
                                                                                                                       dlgCheckInf.dismiss();
                                                                                                                   }
                                                                                                               });
            dlgCheckInf = customBuilder.create();
        }
        dlgCheckInf.show();
    }

    private void showPopView() {
        wayDataArray[0].setSelectedIndex(last_index_num);

        popupWindowWaySelect = new SelecteTypePopupWindow(UserWithdrawExtra.this, wayDataArray);
        popupWindowWaySelect.setColumn(2);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);
        showPopupCenter(popupWindowWaySelect);
    }

    /**
     * 设置标题栏弹出玩法居中显示
     * 
     * @param popup 传入的标题栏弹出框
     */
    protected void showPopupCenter(PopupWindow popup) {
        popup.showAtLocation(scrollView, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
                             findViewById(R.id.title_bar).getHeight() + 20);
    }

    private String setStringLength(String text) {
        StringBuilder sb_text = new StringBuilder();
        sb_text.append(text.substring(0, 4));
        sb_text.append("*****");
        sb_text.append(text.substring(text.length() - 4, text.length()));
        return sb_text.toString();
    }

    private boolean checkInput() {

        String warning = null;
        moneyAmount = money.getText().toString();
// if (bankNameEt.isShown() && bankNameEt.getText().toString().equals("")) {
// warning = "银行名称不能为空！";
// bankNameEt.requestFocus();
// bankNameEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
// }
        if (bankNameSpinnerText.getText().toString().equals(SELECT_BANK) ||
            "".equals(bankNameSpinnerText.getText().toString().trim())) {
            bankNameEdi.setText("");
            bankNameSpinnerText.setText(SELECT_BANK);
            showPopView();
            warning = "银行名称不能为空！";
        }
        else if (bankFullNameEt.isShown() && bankFullNameEt.getText().toString().equals("")) {
            warning = "开户网点不能为空！";
            bankFullNameEt.requestFocus();
            bankFullNameEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (moneyAmount.length() == 0) {
            warning = "请输入提现金额";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!moneyAmount.matches("\\d{1,6}")) {
            warning = "金额只能输入纯数字";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (Double.valueOf(moneyAmount) < 5) {
            warning = "提现金额最少为5元";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (Double.valueOf(moneyAmount) > windrawMoney) {
            warning = "提取金额超过中奖金额";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }

        if (warning == null)
            return true;
        else {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
    }

    private boolean checkInputDialog() {
        boolean b = true;
        if (bankNameEdi.getText().toString().equals("")) {
            String warning = "银行名称不能为空！";
            bankNameEdi.requestFocus();
            bankNameEdi.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            b = false;
        }
        return b;
    }

    private void setGetFail(String inf) {
        name.setText(inf);
        userId.setText(inf);
        account.setText(inf);
        bankCardNum.setText(inf);
        bankLocation.setText(inf);
        bankName.setText(inf);
        bankFullName.setText(inf);
        submit.setText("信息获取失败");
    }

    private void infGetting() {
        String inf = getResources().getString(R.string.inf_getting);
        name.setText(inf);
        userId.setText(inf);
        account.setText(inf);
        bankCardNum.setText(inf);
        bankLocation.setText(inf);
        bankName.setText(inf);
        bankFullName.setText(inf);
        submit.setText("查询资料中，请稍后");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open withdraw money");
        String eventName = "v2 open withdraw money";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onPost(String json) {
        String inf = null;
        if (json == null) {
            inf = getResources().getString(R.string.inf_getting_fail);
            setGetFail(inf);
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                String data = analyse.getData(json, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);

                    String nameStr = jo.getString("name");// 用户姓名
                    String useIdStr = jo.getString("idcard");// 用户身份证
                    String bankCardStr = jo.getString("bankcard");// 银行账户
                    String bankNameStr = jo.getString("bank_name");// 开户银行名称,例如：xxx支行
                    String bankStr = jo.getString("bank");// 银行
                    String bankCityStr = jo.getString("bank_city");// 开户银行所在城市
                    String balance = jo.getString("balance");// 账户与额（info_type=2时，只显示该字段）（充值金额+奖金余额-追号冻结金额）
                    String win = jo.getString("win_balance");// 中奖帐户余额 (可提现额)

                    Logger.inf("vincent", "获取到的银行名称：" + bankStr);

                    name.setText(nameStr);
                    userId.setText(useIdStr);

                    // 银行卡号
                    if (bankCardStr.equals("null") == false && bankCardStr.equals("") == false) {
                        bankNumString = bankCardStr;
                        bankCardNum.setText(bankCardStr);
                    }
                    else
                        bankCardNum.setText("获取失败");

                    // 银行网点
                    if (bankNameStr.equals("null") == false && bankNameStr.equals("") == false) {
                        bankFullNameString = bankNameStr;
                        bankFullName.setText(bankNameStr);
                    }
                    else {
                        bankFullName.setText("银行网点：");
                        bankFullNameTvLinear.setVisibility(View.GONE);
                        bankFullNameEtLinear.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.VISIBLE);
                    }

                    // 银行名称
                    if (bankStr.equals("null") == false && bankStr.equals("") == false) {
                        bankNameString = bankStr;
                        bankName.setText(bankStr);
                        bankName.setVisibility(View.VISIBLE);
                        bankNameTvTitle.setVisibility(View.VISIBLE);
                        bankNameSpinnerText.setText(bankStr);
                        bankNameSpinner.setVisibility(View.GONE);
                        popUpEditextContainer.setVisibility(View.GONE);
                    }
                    else {
                        bankNameSpinnerText.setText(SELECT_BANK);
                        bankName.setVisibility(View.GONE);
                        bankNameTvTitle.setVisibility(View.GONE);
                        popUpEditextContainer.setVisibility(View.GONE);

                        line3.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.GONE);
                    }

                    // 银行所在城市
                    if (bankCityStr.equals("null") == false && bankCityStr.equals("") == false) {
                        bankLocationString = bankCityStr;
                        bankLocation.setText(bankCityStr);
                    }
                    else
                        bankLocation.setText("获取失败");

                    // 可提现金额
                    if (Double.valueOf(balance) > Double.valueOf(win)) {
                        windrawMoney = Double.valueOf(win);
                        account.setText(win + "元");
                    }
                    else {
                        windrawMoney = Double.valueOf(balance);
                        account.setText(balance + "元");
                    }

                    submit.setText("提         现");
                    submit.setEnabled(true);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    inf = getResources().getString(R.string.inf_getting_fail);
                    setGetFail(inf);
                }
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(UserWithdrawExtra.this);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(UserWithdrawExtra.this);
                showLoginAgainDialog(getResources().getString(R.string.login_again));
            }
            else {
                inf = failTips;
                setGetFail(inf);
            }
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
        }
    }

    @Override
    public void onPre() {
        infGetting();
        submit.setVisibility(View.VISIBLE);
        getInfAgain.setVisibility(View.GONE);
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) UserWithdrawExtra.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1005041");
        parameter.put("pid", LotteryUtils.getPid(UserWithdrawExtra.this));
        parameter.put("phone", String.valueOf(phone));

        parameter.put("type", "1");
        parameter.put("money", moneyAmount);
        parameter.put("card_no",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(UserWithdrawExtra.this,
                                                                                  bank_card_num_01)));
        parameter.put("bank", HttpConnectUtil.encodeParameter(bank_name_01));
        parameter.put("bank_name", HttpConnectUtil.encodeParameter(bank_full_name_01));
        parameter.put("bank_city", HttpConnectUtil.encodeParameter(bank_city_01));
        System.out.println("withdraw:" + bank_card_num_01 + "|" + bank_name_01 + "|" + bank_full_name_01 +
            "|" + bank_city_01 + "|" + moneyAmount);
        return parameter;
    }

    private String bank_card_num_01;
    private String bank_full_name_01;
    private String bank_name_01;
    private String bank_city_01;

    class WithdrawTask
        extends AsyncTask<String, Void, String> {

        private Context context;

        private Button bt;
        private ProgressDialog progressDialog;

        public WithdrawTask(Context context, Button bt, String bank_card_num, String bank_full_name,
                            String bank_name, String bank_city) {
            this.context = context;
            this.bt = bt;
            bank_card_num_01 = bank_card_num;
            bank_full_name_01 = bank_full_name;
            bank_name_01 = bank_name;
            bank_city_01 = bank_city;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在提交提现请求...");
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectService connectNet = new ConnectService(UserWithdrawExtra.this);
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
        protected void onPostExecute(String json) {
            progressDialog.dismiss();
            bt.setText("提         现");
            bt.setEnabled(true);
            if (json != null) {
                String inf = null;
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    String chargeMsg = ja.getData(json, "error_desc");
                    appState.setPerfectInf(2);
                    showSuccessDialog(chargeMsg);
                }
                else if (status.equals("300")) {
                    String chargeMsg = ja.getData(json, "error_desc");
                    showSuccessDialog(chargeMsg);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWithdrawExtra.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWithdrawExtra.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = "提现失败";
                }
                if (inf != null) {
                    ViewUtil.showTipsToast(UserWithdrawExtra.this, inf);
                }
            }
            else {
                ViewUtil.showTipsToast(UserWithdrawExtra.this, "提现失败");
            }
        }

        private void showSuccessDialog(String chargeMsg) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(UserWithdrawExtra.this);
            customBuilder.setMessage(chargeMsg).setPositiveButton("确定",
                                                                  new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog,
                                                                                          int which) {
                                                                          dlgSubmitSuccessTips.dismiss();
                                                                      }
                                                                  });
            dlgSubmitSuccessTips = customBuilder.create();
            dlgSubmitSuccessTips.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            bt.setText("提现请求提交中...");
            bt.setEnabled(false);
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_withdraw";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UserWithdrawExtra.this.finish();
            bankFullNameEt.setError(null);
            money.setError(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    boolean isFirst = true;

    @Override
    public void setEditTextTextWatcher(View view, boolean isNull) {
        if (view.getId() == R.id.user_withdraw_money) {
            if (isNull)
                moneyDeleButton.setVisibility(View.INVISIBLE);
            else
                moneyDeleButton.setVisibility(View.VISIBLE);
        }
        else if (view.getId() == R.id.bank_full_name_et) {
            if (isNull)
                bankFullNameDeleButton.setVisibility(View.INVISIBLE);
            else
                bankFullNameDeleButton.setVisibility(View.VISIBLE);
        }
    }

// @Override
// public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
// int index, String tabName) {
// bankNameN = ZhongguoCityName.bankName[index];
// bankNameSpinnerText.setText(bankNameN);
// titlePopup.dismiss();
// last_index_num = index;
//
// if (bankNameN.equals("其他")) {
// popUpEditextContainer.setVisibility(View.VISIBLE);
// bankNameEt.requestFocus();
//
// bankName.setVisibility(View.GONE);
// bankNameTvTitle.setVisibility(View.GONE);
// bankNameSpinner.setVisibility(View.GONE);
// }
// }

    @Override
    public void selecteType(int type, int index) {
        // TODO Auto-generated method stub
        bankNameN = ZhongguoCityName.bankName[index];
        bankNameSpinnerText.setText(bankNameN);
        popupWindowWaySelect.dismiss();
        last_index_num = index;
        if (bankNameN.equals("其他")) {
// popUpEditextContainer.setVisibility(View.VISIBLE);
// bankNameEt.requestFocus();
//
// bankName.setVisibility(View.GONE);
// bankNameTvTitle.setVisibility(View.GONE);
// bankNameSpinner.setVisibility(View.GONE);

            showInputBankNameDialog();
        }
    }

    private void showInputBankNameDialog() {
        if (null == ediBankNameDialog) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
            customBuilder.setTitle("请输入银行名称:").setContentView(eventView);
            customBuilder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO
                    if (checkInputDialog()) {
                        bankNameSpinnerText.setText(bankNameEdi.getText().toString());
                        ediBankNameDialog.dismiss();
                        bankFullNameEt.requestFocus();
                    }
                }
            }).setNegativeButton("取  消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    bankNameEdi.setText("");
                    ediBankNameDialog.dismiss();
                }
            });
            ediBankNameDialog = customBuilder.create();
        }
        ediBankNameDialog.show();
    }
}
