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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopUpEditText;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.haozan.caipiao.widget.PopUpEditText.EditTextTextWatcher;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;
import com.umeng.analytics.MobclickAgent;

public class UserWithdraw
    extends BasicActivity
    implements OnClickListener, OnGetUserInfListener, PopMenuButtonClickListener, EditTextTextWatcher,
    PopupSelectTypeClickListener {
    public static final String SELECT_BANK = "点击选择银行";

    private String moneyAmount;
    private double windrawMoney;

    private LinearLayout body;
    private TextView name;
    private TextView userId;
    private TextView account;
    private ImageButton help;
// private TextView bankCardNum;
    private TextView message;

    private PopUpEditText bankFullNameEt;
    private PopUpEditText bankCityEt;
    private PopUpEditText bankCardNumEt;
    private PopUpEditText money;
    private Button withdraw;
    private Button getInfAgain;
    private Button editBankCardNum;
    private LinearLayout nameLayout;
    private LinearLayout idLayout;
    private ImageView line;
    private ScrollView scrollView;

    private TextView accountnamedlg;
    private TextView accountmoneydlg;
    private TextView accountbankcarnumdlg;
    private TextView accountbanknamedlg;
    private TextView accountbankfullnamedlg;
    private TextView accountbankcitydlg;
    private Button historyButton;
    private LayoutInflater inflater;
    private WithdrawTask withdrawTask;

    // 银行名称下拉列表
    private BetShowLotteryWay[] wayDataArray;
    private SelecteTypePopupWindow popupWindowWaySelect;
    private View eventView;
    private EditText bankNameEdi;
    private LinearLayout bankNameSpinner;
    private TextView bankNameSpinnerText;
    private ImageView bankNameSpinnerIcon;
    private InputMethodManager imm;
    // 文本编辑框里的删除按钮
    private Button withDrawMoneyDeleteButton;
    private Button deleteWinthdrawMoneyButton;
    private Button deleteBankLocationEtButton;
    private Button deleteWbankFullNameEtButton;
    // 充值额菜单
    private PopMenu titlePopup;
    private int screenWidth;
    private String bankName = ZhongguoCityName.bankName[0];
    private int last_index_num = 0;

    private View makeSureView;
    private CustomDialog dlgCheckInf;
    private CustomDialog dlgSubmitSuccessTips;
    private CustomDialog ediBankNameDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_withdraw);
        initData();
        setupViews();
        init();
        this.getWindow().getDecorView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                money.setError(null);
                bankFullNameEt.setError(null);
                bankCityEt.setError(null);
                bankCardNumEt.setError(null);
                return false;
            }
        });
    }

    public int add(int a, int b) {
        System.out.println("add:" + (a + b));
        return a + b;
    }

    private void initData() {
        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();
        lotteryway.setUpsInf(ZhongguoCityName.bankName);
        wayDataArray[0] = lotteryway;
    }

    private void init() {
// bankNameSpinnerText.setText(bankName);
        // 获取屏幕宽度
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        // 获取输入法对象
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // init withdraw page
        if (HttpConnectUtil.isNetworkAvailable(UserWithdraw.this)) {
            UserInfTask getUserInf = new UserInfTask(UserWithdraw.this);
            getUserInf.setOnGetUserInfListener(this);
            getUserInf.execute(1);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            message.setText(inf);
            message.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            body.setVisibility(View.GONE);
        }
    }

    public void initWithdrawTask() {
        String bank_name__str = "";
// if (bankNameEt.getVisibility() == View.VISIBLE)
// bank_name__str = bankNameEt.getText().toString();
// else
// bank_name__str = bankName;
        bank_name__str = bankNameSpinnerText.getText().toString();
        String bank_num_str = bankCardNumEt.getText().toString();
        String bank_full_name_str = bankFullNameEt.getText().toString();
        String bank_city_str = bankCityEt.getText().toString();
        withdrawTask =
            new WithdrawTask(UserWithdraw.this, withdraw, bank_num_str, bank_full_name_str, bank_name__str,
                             bank_city_str);
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

        help = (ImageButton) this.findViewById(R.id.chzhelp);
        help.setOnClickListener(this);
        withdraw = (Button) findViewById(R.id.rechargebt);
        withdraw.setOnClickListener(this);
        getInfAgain = (Button) findViewById(R.id.get_inf_again);
        getInfAgain.setOnClickListener(this);
        editBankCardNum = (Button) findViewById(R.id.edit_card_num);
        editBankCardNum.setOnClickListener(this);
        historyButton = (Button) findViewById(R.id.history_button);
        historyButton.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView01);

        bankCardNumEt = (PopUpEditText) findViewById(R.id.bank_card_num);
        bankCardNumEt.setTextWatcher(this);

        money = (PopUpEditText) this.findViewById(R.id.user_withdraw_money);
        money.setTextWatcher(this);

        bankCityEt = (PopUpEditText) findViewById(R.id.bank_location_et);
        bankCityEt.setTextWatcher(this);

        bankFullNameEt = (PopUpEditText) findViewById(R.id.bank_full_name_et);
        bankFullNameEt.setTextWatcher(this);

        nameLayout = (LinearLayout) this.findViewById(R.id.name_layout);
        idLayout = (LinearLayout) this.findViewById(R.id.id_layout);
        line = (ImageView) this.findViewById(R.id.line1);

        bankNameSpinner = (LinearLayout) findViewById(R.id.withdraw_bank_name);
        bankNameSpinner.setOnClickListener(this);
        bankNameSpinnerText = (TextView) this.findViewById(R.id.withdraw_bank_name_text);
        bankNameSpinnerIcon = (ImageView) this.findViewById(R.id.withdraw_bank_name_icon);
        // 文本编辑框删除按钮
        withDrawMoneyDeleteButton = (Button) this.findViewById(R.id.edit_card_num);
        withDrawMoneyDeleteButton.setOnClickListener(this);

        deleteWinthdrawMoneyButton = (Button) this.findViewById(R.id.delete_winthdraw_money_button);
        deleteWinthdrawMoneyButton.setOnClickListener(this);

        deleteBankLocationEtButton = (Button) this.findViewById(R.id.delete_bank_location_et_button);
        deleteBankLocationEtButton.setOnClickListener(this);

        deleteWbankFullNameEtButton = (Button) this.findViewById(R.id.delete_wbank_full_name_et_button);
        deleteWbankFullNameEtButton.setOnClickListener(this);
    }

    private void setupEdiBankNameViews() {
        inflater = LayoutInflater.from(UserWithdraw.this);
        eventView = inflater.inflate(R.layout.edi_and_btn_view, null);
        bankNameEdi = (EditText) eventView.findViewById(R.id.edi);
    }

    private void setupDialogViews() {
        inflater = LayoutInflater.from(UserWithdraw.this);
        makeSureView = inflater.inflate(R.layout.withdraw_extra_recharge_make_sure_dlg, null);
        accountnamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_first);
        accountmoneydlg = (TextView) makeSureView.findViewById(R.id.dialog_item_second);
        accountbankcarnumdlg = (TextView) makeSureView.findViewById(R.id.dialog_item_three);
        accountbanknamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_foth);
        accountbankfullnamedlg = (TextView) makeSureView.findViewById(R.id.dialog_item_fifth);
        accountbankcitydlg = (TextView) makeSureView.findViewById(R.id.dialog_item_sixth);
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int lastIndex) {
        titlePopup = new PopMenu(UserWithdraw.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1, screenWidth - 20, lastIndex, false,
                             true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - bankNameSpinner.getWidth() + bankNameSpinner.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    @Override
    public void onClick(View v) {
        isFirst = true;

        String bankNameDlg = bankNameSpinnerText.getText().toString();

        if (v.getId() == R.id.chzhelp) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putInt("help03", R.string.helpinf03);
            bundel.putString("about", "withDrawHelp");
            intent.putExtras(bundel);
            intent.setClass(UserWithdraw.this, SubHelp.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.rechargebt) {
            if (checkInput()) {
                accountnamedlg.setText("姓   名: " + name.getText().toString());
                accountmoneydlg.setText(money.getText().toString() + "元");
                accountbankcarnumdlg.setText(bankCardNumEt.getText().toString());
                accountbanknamedlg.setText(bankNameDlg);
                accountbankfullnamedlg.setText(bankFullNameEt.getText().toString());
                accountbankcitydlg.setText(bankCityEt.getText().toString());
                if (checkInput()) {
                    showConfirmDialog();
                }
            }
        }
        else if (v.getId() == R.id.get_inf_again) {
            if (HttpConnectUtil.isNetworkAvailable(UserWithdraw.this)) {
                UserInfTask getUserInf = new UserInfTask(UserWithdraw.this);
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
            intent.setClass(UserWithdraw.this, WithdrawHistory.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.edit_card_num) {
            if (bankCardNumEt.getText().toString() != null) {
                bankCardNumEt.setText(null);
                bankCardNumEt.requestFocus();
            }
        }
        else if (v.getId() == R.id.delete_winthdraw_money_button) {
            if (money.getText().toString() != null) {
                money.setText(null);
                money.requestFocus();
            }
        }
        else if (v.getId() == R.id.delete_bank_location_et_button) {
            if (bankCityEt.getText().toString() != null) {
                bankCityEt.setText(null);
                bankCityEt.requestFocus();
            }
        }
        else if (v.getId() == R.id.delete_wbank_full_name_et_button) {
            if (bankFullNameEt.getText().toString() != null) {
                bankFullNameEt.setText(null);
                bankFullNameEt.requestFocus();
            }
        }
        else if (v.getId() == R.id.withdraw_bank_name) {
// showPopupViews(bankNameSpinner, ZhongguoCityName.bankName, last_index_num);
            showPopView();
        }
    }

    private void showConfirmDialog() {
        if (dlgCheckInf == null) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
            customBuilder.setTitle("信息确认").setContentView(makeSureView).setPositiveButton("确  定",
                                                                                          new DialogInterface.OnClickListener() {
                                                                                              public void onClick(DialogInterface dialog,
                                                                                                                  int which) {
                                                                                                  initWithdrawTask();
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

        popupWindowWaySelect = new SelecteTypePopupWindow(UserWithdraw.this, wayDataArray);
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

    private boolean checkInput() {
        String warning = null;
        moneyAmount = money.getText().toString();
        if (bankNameSpinnerText.getText().toString().equals(SELECT_BANK) ||
            "".equals(bankNameSpinnerText.getText().toString().trim())) {
            bankNameEdi.setText("");
            bankNameSpinnerText.setText(SELECT_BANK);
            showPopView();
            warning = "银行名称不能为空！";
            money.setError(null);
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
        }
        else if (bankCardNumEt.getText().toString().equals("")) {
            warning = "银行卡号不能为空！";
            bankCardNumEt.requestFocus();
            money.setError(null);
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (!(bankCardNumEt.getText().toString().length() == 16 ||
            bankCardNumEt.getText().toString().length() == 19 || bankCardNumEt.getText().toString().length() == 21)) {
            warning = "您输入的号码不正确，请输入16,19或21位的正确银行卡号";
            bankCardNumEt.requestFocus();
            money.setError(null);
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (moneyAmount.length() == 0) {
            warning = "请输入提现金额";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
        }
        else if (!moneyAmount.matches("\\d{1,6}")) {
            warning = "金额只能输入纯数字";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
        }
        else if (Double.valueOf(moneyAmount) < 5) {
            warning = "提现金额为最少为5元";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
        }
        else if (Double.valueOf(moneyAmount) > windrawMoney) {
            warning = "提取金额超过中奖金额";
            money.requestFocus();
            money.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
        }
        else if (bankCityEt.getText().toString().equals("")) {
            warning = "银行所在城市，不能为空！";
            bankCityEt.requestFocus();
            money.setError(null);
            bankFullNameEt.setError(null);
            bankCityEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankCardNumEt.setError(null);
        }
        else if (bankFullNameEt.getText().toString().equals("")) {
            warning = "开户网点不能为空！";
            bankFullNameEt.requestFocus();
            money.setError(null);
            bankFullNameEt.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Boolean success = b.getBoolean("addsuccess");
            if (!success) {
                UserWithdraw.this.finish();
            }
            else {
                if (HttpConnectUtil.isNetworkAvailable(UserWithdraw.this)) {
                    UserInfTask getUserInf = new UserInfTask(UserWithdraw.this);
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
        }
    }

    private void setGetFail(String inf) {
        name.setText(inf);
        userId.setText(inf);
        account.setText(inf);
        bankCardNumEt.setText(inf);
        bankFullNameEt.setText(inf);
        bankCityEt.setText(inf);
        withdraw.setText("信息获取失败，无法充值");
    }

    private void infGetting() {
        name.setText(getResources().getString(R.string.inf_getting));
        userId.setText(getResources().getString(R.string.inf_getting));
        account.setText(getResources().getString(R.string.inf_getting));
        bankCardNumEt.setText(getResources().getString(R.string.inf_getting));
        bankFullNameEt.setText(getResources().getString(R.string.inf_getting));
        bankCityEt.setText(getResources().getString(R.string.inf_getting));
        withdraw.setText("查询资料中，请稍后");
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
    protected void onDestroy() {
        super.onDestroy();
        if (withdrawTask != null)
            if (!withdrawTask.isCancelled())
                withdrawTask.cancel(true);
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        LotteryApp appState = ((LotteryApp) UserWithdraw.this.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1005041");
        parameter.put("pid", LotteryUtils.getPid(UserWithdraw.this));
        parameter.put("phone", String.valueOf(phone));

        parameter.put("type", "1");
        parameter.put("money", moneyAmount);
        parameter.put("card_no",
                      HttpConnectUtil.encodeParameter(EncryptUtil.encryptString(UserWithdraw.this,
                                                                                  bank_card_num_01)));
        parameter.put("bank", HttpConnectUtil.encodeParameter(bank_name_01));
        parameter.put("bank_name", HttpConnectUtil.encodeParameter(bank_full_name_01));
        parameter.put("bank_city", HttpConnectUtil.encodeParameter(bank_city_01));
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
            this.progressDialog = new ProgressDialog(context);
            bank_card_num_01 = bank_card_num;
            bank_full_name_01 = bank_full_name;
            bank_name_01 = bank_name;
            bank_city_01 = bank_city;
            progressDialog.setMessage("正在提交提现请求...");
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectService connectNet = new ConnectService(UserWithdraw.this);
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
                if (status.equals("200") || status.equals("300")) {
                    String chargeMsg = ja.getData(json, "error_desc");
                    appState.setPerfectInf(2);
                    showSuccessDialog(chargeMsg);
                }
                else if (status.equals("300")) {
                    String chargeMsg = ja.getData(json, "error_desc");
                    showSuccessDialog(chargeMsg);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UserWithdraw.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UserWithdraw.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = "提现失败";
                }
                if (inf != null) {
                    ViewUtil.showTipsToast(UserWithdraw.this, inf);
                }
            }
            else {
                ViewUtil.showTipsToast(UserWithdraw.this, "提现失败");
            }
        }

        private void showSuccessDialog(String chargeMsg) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(UserWithdraw.this);
            customBuilder.setMessage(chargeMsg).setPositiveButton("确  定",
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

                    Logger.inf("vincent", "提现返回：" + data);

                    JSONObject jo = hallArray.getJSONObject(0);
                    String userName = jo.getString("name");
                    String idCard = jo.getString("idcard");
                    if (userName.equals("null"))
                        nameLayout.setVisibility(View.GONE);
                    else
                        name.setText(userName);
                    if (idCard.equals("null")) {
                        idLayout.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }
                    else
                        userId.setText(idCard);

                    if (jo.getString("bankcard").equals("null") == false) {
                        bankCardNumEt.setText(jo.getString("bankcard"));
                        bankCardNumEt.setText(jo.getString("bankcard"));
                    }
                    else {
                        bankCardNumEt.setVisibility(View.VISIBLE);
                        bankCardNumEt.setText(null);
                        bankCardNumEt.requestFocus();
                        editBankCardNum.setVisibility(View.GONE);
                        money.clearFocus();
                    }
                    if (jo.getString("bank_name").equals("null") == false) {
                        bankFullNameEt.setText(jo.getString("bank_name"));
                    }
                    else {
                        bankFullNameEt.setText(null);
                    }
                    if (jo.getString("bank").equals("null") == false) {
                        bankNameSpinnerText.setText(jo.getString("bank"));
                    }
                    else {
                        bankNameSpinnerText.setText(SELECT_BANK);
                    }
                    if (jo.getString("bank_city").equals("null") == false) {
                        bankCityEt.setText(jo.getString("bank_city"));
                    }
                    else {
                        bankCityEt.setText(null);
                    }
                    String balance = jo.getString("balance");
                    String win = jo.getString("win_balance");
                    if (Double.valueOf(balance) > Double.valueOf(win)) {
                        windrawMoney = Double.valueOf(win);
                        account.setText(win + "元");
                    }
                    else {
                        windrawMoney = Double.valueOf(balance);
                        account.setText(balance + "元");
                    }
                    withdraw.setText("提         现");
                    withdraw.setEnabled(true);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    inf = getResources().getString(R.string.inf_getting_fail);
                    setGetFail(inf);
                }
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(UserWithdraw.this);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(UserWithdraw.this);
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
        withdraw.setVisibility(View.VISIBLE);
        getInfAgain.setVisibility(View.GONE);
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
            money.setError(null);
            bankFullNameEt.setError(null);
            bankCityEt.setError(null);
            bankCardNumEt.setError(null);
            UserWithdraw.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    boolean isFirst = true;

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        bankName = ZhongguoCityName.bankName[index];
        bankNameSpinnerText.setText(bankName);
        titlePopup.dismiss();
        last_index_num = index;
    }

    @Override
    public void setEditTextTextWatcher(View view, boolean isNull) {
        if (view.getId() == R.id.bank_card_num) {
            if (isNull)
                editBankCardNum.setVisibility(View.INVISIBLE);
            else
                editBankCardNum.setVisibility(View.VISIBLE);
        }
        else if (view.getId() == R.id.user_withdraw_money) {
            if (isNull)
                deleteWinthdrawMoneyButton.setVisibility(View.INVISIBLE);
            else
                deleteWinthdrawMoneyButton.setVisibility(View.VISIBLE);
        }
        else if (view.getId() == R.id.bank_location_et) {
            if (isNull)
                deleteBankLocationEtButton.setVisibility(View.INVISIBLE);
            else
                deleteBankLocationEtButton.setVisibility(View.VISIBLE);
        }
        else if (view.getId() == R.id.bank_full_name_et) {
            if (isNull)
                deleteWbankFullNameEtButton.setVisibility(View.INVISIBLE);
            else
                deleteWbankFullNameEtButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void selecteType(int type, int index) {
        // TODO Auto-generated method stub
        bankName = ZhongguoCityName.bankName[index];
        bankNameSpinnerText.setText(bankName);
        popupWindowWaySelect.dismiss();
        last_index_num = index;
        if (bankName.equals("其他")) {
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
                    if (checkInputDialog()) {
                        bankNameSpinnerText.setText(bankNameEdi.getText().toString());
                        ediBankNameDialog.dismiss();
                        bankCardNumEt.requestFocus();
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
