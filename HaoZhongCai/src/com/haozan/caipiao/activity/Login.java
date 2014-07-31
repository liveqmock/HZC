/**
 * 登录部分修改 融入开机自动登录、帐号信息保存在本地数据库 密码已加密 每次登录都会保存登录方式，下次运行客户端时会自动登录，方式为上次保存的方式
 * 
 * @author Vincent
 */
package com.haozan.caipiao.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.haozan.caipiao.R;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.LoginOperateControl;
import com.haozan.caipiao.request.LoginRequest;
import com.haozan.caipiao.types.AccountsData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.alipayfastlogin.AlixId;
import com.haozan.caipiao.util.security.DESPlus;
import com.haozan.caipiao.widget.CustomDialog;

/**
 * 登录页面
 * 
 * @author peter_wang
 * @create-time 2013-10-28 上午9:20:22
 */
public class Login
    extends BasicActivity
    implements OnClickListener {
    private static final int GET_ALL_ACCOUNT = 0;
    private static final int GET_LAST_ACCOUNT = 2;

    private TextView mTvTitle;
    private TextView mTvForwoard;

    private LinearLayout mLayoutPhoneLogin;
    private ImageView mIvMoreAccounts;
    private EditText mEtAccount;
    private EditText mEtPassword;
    private ImageView mIvSavePassword;
    private LinearLayout mLayoutSavePassword;
    private TextView mTvForgetPassword;
    private Button mBtnRegister;
    private Button mBtnLogin;

    private LinearLayout mLayoutCancleLogin;
    private TextView mTvAccout;
    private Button mBtnCancle;

    private ImageView mIvAlipay;
    private ImageView mIvSinaWeibo;
    private ImageView mIvQQ;

    private PopupWindow mPpwAllAcount;
    private ArrayList<AccountsData> mAccountsList;

    private Boolean isSavePassword = false;

    private String mLoginType;// 参数见LoginRequest

    private LoginOperateControl mLoginOperateControl;

    // the handler use to receive the pay result.
    // 这里接收支付结果，支付宝手机端同步通知
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AlixId.RQF_PAY:
                    try {
                        String ret = (String) msg.obj;

                        Logger.inf(ret); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
                        mLoginOperateControl.analyseAlipayLoginInf(ret);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case ControlMessage.FINISH_ACTIVITY:
                    finish();
                    break;

                case ControlMessage.LOGIN_OK_RESULT:
                    setResult(RESULT_OK);
                    finish();
                    mLoginOperateControl.toNextPage(getIntent().getExtras());
                    break;

                case ControlMessage.LOGIN_FAIL_RESULT:
                    String inf = (String) msg.obj;
                    ViewUtil.showTipsToast(mContext, inf);
                    mLayoutPhoneLogin.setVisibility(View.VISIBLE);
                    mLayoutCancleLogin.setVisibility(View.GONE);
                    break;

                case GET_ALL_ACCOUNT:
                    mAccountsList = mLoginOperateControl.getAllLoginPhoneInf();

                    if (mAccountsList.size() == 0) {
                        mIvMoreAccounts.setVisibility(View.GONE);
                    }
                    else {
                        mIvMoreAccounts.setVisibility(View.VISIBLE);
                    }
                    break;

                case GET_LAST_ACCOUNT:
                    initPhone();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        seupViews();
        init();
    }

    private void initData() {
        mLoginOperateControl = new LoginOperateControl(this, mHandler);
    }

    private void seupViews() {
        mTvTitle = (TextView) findViewById(R.id.initName);
        mTvTitle.setText("登录");
        mTvForwoard = (TextView) this.findViewById(R.id.forward);

        mIvMoreAccounts = (ImageView) findViewById(R.id.more_accounts);
        mIvMoreAccounts.setOnClickListener(this);
        mEtAccount = (EditText) findViewById(R.id.login_num_new);
        mEtAccount.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mEtPassword.requestFocus();
                }
                return false;
            }
        });
        mEtPassword = (EditText) findViewById(R.id.login_password_new);
        mLayoutSavePassword = (LinearLayout) this.findViewById(R.id.layout_save_pas_new);
        mLayoutSavePassword.setOnClickListener(this);
        mIvSavePassword = (ImageView) findViewById(R.id.save_select_new);
        mBtnLogin = (Button) findViewById(R.id.login_bt_new);
        mBtnLogin.setOnClickListener(this);
        mTvForgetPassword = (TextView) findViewById(R.id.login_findpassword_new);
        mTvForgetPassword.setText(Html.fromHtml("<u><font color='#817ce2'>忘记密码?</font></u>"));
        mTvForgetPassword.setOnClickListener(this);
        mBtnRegister = (Button) findViewById(R.id.login_register_new);
        mBtnRegister.setOnClickListener(this);

        mLayoutPhoneLogin = (LinearLayout) findViewById(R.id.ll_login_part);
        mLayoutCancleLogin = (LinearLayout) findViewById(R.id.ll_cancle_part);
        mTvAccout = (TextView) findViewById(R.id.tv_startup_accounts);
        mBtnCancle = (Button) this.findViewById(R.id.cancel_new);
        mBtnCancle.setOnClickListener(this);

        mIvAlipay = (ImageView) findViewById(R.id.alipay_logo);
        mIvAlipay.setOnClickListener(this);
        mIvSinaWeibo = (ImageView) findViewById(R.id.sina_logo);
        mIvSinaWeibo.setOnClickListener(this);
        mIvQQ = (ImageView) findViewById(R.id.qq_logo);
        mIvQQ.setOnClickListener(this);
    }

    private void init() {
        showForwordPageName();

        mHandler.sendEmptyMessage(GET_LAST_ACCOUNT);

        mHandler.sendEmptyMessage(GET_ALL_ACCOUNT);
    }

    private void showForwordPageName() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String forward = bundle.getString("forwardFlag");
            if (forward != null) {
                mTvForwoard.setText(forward);
            }
        }
    }

    /**
     * 初始化上次用手机号码登录的信息,包括手机号码和密码
     */
    private void initPhone() {
        String[] lastLoginInf = mLoginOperateControl.getLastLoginPhoneInf();
        if (lastLoginInf != null) {
            if (lastLoginInf.length == 2) {
                if (TextUtils.isEmpty(lastLoginInf[0]) == false) {
                    mEtAccount.setText(lastLoginInf[0]);
                }

                if (TextUtils.isEmpty(lastLoginInf[1]) == false) {
                    mEtPassword.setText(DESPlus.deCode(lastLoginInf[1]));
                    mEtPassword.requestFocus();
                    isSavePassword = true;
                    refreshPasStatus();
                }
            }
        }
    }

    /**
     * 检查手机号码、密码的输入是否符合标准
     * 
     * @return boolean
     */
    private boolean checkInput() {
        String warning = null;
        String name = mEtAccount.getText().toString();
        String password = mEtPassword.getText().toString();
        if (name == null || name.equals("")) {
            warning = "请输入手机号码";
            mEtAccount.requestFocus();
        }
        else if (name.length() < 11) {
            warning = "您输入的手机号码少于11位";
            mEtAccount.requestFocus();
        }
        else if (password == null || password.equals("")) {
            warning = "请输入密码";
            mEtPassword.requestFocus();
        }
        // else if (!password.matches("\\d{6,12}")) {
        // warning = "您只能输入纯数字密码";
        // userPassword.requestFocus();
        // }
        else if (password.length() < 6) {
            warning = "您输入的密码少于6位";
            mEtPassword.requestFocus();
        }
        else if (password.length() > 12) {
            warning = "您输入的密码多于12位";
            mEtPassword.requestFocus();
        }

        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        return true;
    }

    public void showDeleteAutoLoginDialog() {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setWarning().setMessage("取消自动登录，将清空您原来保存的密码，确定取消自动登陆？").setPositiveButton("确  定",
                                                                                                new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog,
                                                                                                                        int which) {
                                                                                                        dialog.dismiss();
                                                                                                        isSavePassword =
                                                                                                            false;
                                                                                                        mEtPassword.setText("");
                                                                                                        refreshPasStatus();

                                                                                                        mLoginOperateControl.cancleAutoLogin(mEtAccount.getText().toString());

                                                                                                        mLoginOperateControl.submitRememberPasswordEvent(isSavePassword);
                                                                                                    }
                                                                                                }).setNegativeButton("取 消",
                                                                                                                     new DialogInterface.OnClickListener() {
                                                                                                                         public void onClick(DialogInterface dialog,
                                                                                                                                             int which) {
                                                                                                                             dialog.dismiss();
                                                                                                                         }
                                                                                                                     });
        CustomDialog dlgDeleteAutoLogin = customBuilder.create();
        dlgDeleteAutoLogin.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.login_bt_new) {
            mLoginType = LoginRequest.PHONE_LOGIN;

            // 隐藏输入法
            final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBtnLogin.getWindowToken(), 0);

            if (checkInput() == true) {
                if (HttpConnectUtil.isNetworkAvailable(mContext)) {
                    mTvAccout.setText(mEtAccount.getText().toString());
                    mLayoutPhoneLogin.setVisibility(View.GONE);
                    mLayoutCancleLogin.setVisibility(View.VISIBLE);

                    int save = 0;
                    if (isSavePassword) {
                        save = 1;
                    }

                    mLoginOperateControl.phoneLogin(mEtAccount.getText().toString(),
                                                    mEtPassword.getText().toString(), save);
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
            }
        }
        else if (id == R.id.login_register_new) {
            Intent intent = new Intent();
            intent.setClass(this, Register.class);
            startActivityForResult(intent, 3);
        }
        else if (id == R.id.login_findpassword_new) {
            Intent intent = new Intent();
            long refreshTime = preferences.getLong("reset_password_time", 0);
            if (System.currentTimeMillis() - refreshTime < 20 * 60 * 1000) {
                String phoneNum = preferences.getString("reset_password_phone", null);
                String perfectInf = preferences.getString("reset_password_inf", null);
                String sessionId = preferences.getString("reset_password_session", null);
                Bundle bundle = new Bundle();
                bundle.putString("phone", phoneNum);
                bundle.putString("per_inf", perfectInf);
                bundle.putString("session_id", sessionId);
                intent.putExtras(bundle);
                intent.setClass(this, ResetPassword.class);
            }
            else {
                intent.setClass(this, FindPasswordReq.class);
            }
            startActivity(intent);
        }
        else if (id == R.id.layout_save_pas_new) {
            if (isSavePassword) {
                if (TextUtils.isEmpty(mEtPassword.getText().toString()) == false) {
                    showDeleteAutoLoginDialog();
                }
                else {
                    isSavePassword = false;
                    refreshPasStatus();
                }
            }
            else {
                isSavePassword = true;
                refreshPasStatus();
            }

            mLoginOperateControl.submitRememberPasswordEvent(isSavePassword);
        }
        else if (id == R.id.more_accounts) {
            showAllAccounts();
        }
        else if (id == R.id.qq_logo) {
            mLoginType = LoginRequest.QQ_LOGIN;

            mLoginOperateControl.loginByQQ();
            mLoginOperateControl.submitStatisticClickLogin(LoginRequest.QQ_LOGIN);
        }
        else if (id == R.id.sina_logo) {
            mLoginType = LoginRequest.SINA_WEIBO_LOGIN;

            mLoginOperateControl.loginBySinaWeibo();
            mLoginOperateControl.submitStatisticClickLogin(LoginRequest.SINA_WEIBO_LOGIN);
        }
        else if (id == R.id.alipay_logo) {
            mLoginType = LoginRequest.ALIPAY_LOGIN;

            mLoginOperateControl.loginByAlipay();
            mLoginOperateControl.submitStatisticClickLogin(LoginRequest.ALIPAY_LOGIN);
        }
    }

    private void refreshPasStatus() {
        if (isSavePassword) {
            mIvSavePassword.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            mIvSavePassword.setBackgroundResource(R.drawable.choosing_not_select);
        }
    }

    private void showAllAccounts() {
        if (mPpwAllAcount == null) {
            AllAccountsAdapter adapter = new AllAccountsAdapter(mContext, mAccountsList);

            ListView listView = new ListView(Login.this);
            listView.setCacheColorHint(0x00000000);
            listView.setBackgroundResource(R.drawable.lottery_way_popup_bg_light);
            listView.setDivider(getResources().getDrawable(R.drawable.new_devide_line));
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPpwAllAcount.dismiss();

                    AccountsData data = mAccountsList.get(position);

                    String name = mAccountsList.get(position).getAccount();
                    mEtAccount.setText(name);

                    if (data.getStatus() != 0) {
                        String password = mAccountsList.get(position).getPassword();
                        // 解密
                        password = DESPlus.deCode(password);
                        mEtPassword.setText(password);
                    }
                    else {
                        mEtPassword.setFocusable(true);
                    }

                    isSavePassword = mAccountsList.get(position).getStatus() == 0 ? false : true;
                    refreshPasStatus();
                }
            });
            mPpwAllAcount = new PopupWindow(this);
            mPpwAllAcount.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPpwAllAcount.setWidth(mEtAccount.getMeasuredWidth());
            mPpwAllAcount.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mPpwAllAcount.setOutsideTouchable(true);
            mPpwAllAcount.setFocusable(true);
            mPpwAllAcount.setContentView(listView);
            mPpwAllAcount.setAnimationStyle(R.style.popup_ball);
        }

        // moreAccounts.showAsDropDown(userNameEdit);
        mPpwAllAcount.showAsDropDown(mEtAccount, mEtAccount.getLeft(), -7);
    }

    /**
     * 点击手机号码编辑框右侧的下拉按钮后，弹出的listview的适配器
     * 
     * @author Vincent
     * @create-time 2013-5-24 上午10:08:29
     */
    class AllAccountsAdapter
        extends BaseAdapter {
        private Context context;
        private ArrayList<AccountsData> list;

        public AllAccountsAdapter(Context context, ArrayList<AccountsData> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(list.get(position).getAccount());
            tv.setTextColor(getResources().getColor(R.color.dark_purple));
            tv.setTextSize(getResources().getDimension(R.dimen.basic_text_size));
            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginOperateControl.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void submitData() {
        mLoginOperateControl.submitStatisticOpenLogin();
    }
}