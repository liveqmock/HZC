package com.haozan.caipiao.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.bet.BetSuccessPage;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.PhoneAdapter;
import com.haozan.caipiao.adapter.PhoneGridAdapter;
import com.haozan.caipiao.adapter.PhoneAdapter.OnBallClickListener;
import com.haozan.caipiao.adapter.PhoneGridAdapter.OnSelectedPhoneNumClickListener;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.types.PhoneItemData;
import com.haozan.caipiao.types.TempData;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;
import com.umeng.analytics.MobclickAgent;

public class GiveLottery
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnBallClickListener, DialogInterface.OnClickListener, OnGetUserInfListener,
    OnSelectedPhoneNumClickListener {
    public static final String ADD_FAIL = "您已经添加过此手机号码";
    public static final int GIVE_NUM = 50;
    public static final String ADD_FAIL_MORE_NUM = "每次最多可以给" + GIVE_NUM + "人赠送彩票";
    public static final String BET_PROTOCOL = "委托投注协议";
    public static final String GIVE_PROTOCOL = "赠送彩票协议";
    public static final String BET_FAIL_NO_TERM = "没有拿到期数信息，无法投注";
    private ArrayList<PhoneItemData> listItem;
    private ContentResolver resolver;
    private Cursor peopleCursor;
    private ListView list;
    private PhoneAdapter adapter;
    private PhoneGridAdapter gridAdapter;
    private GridView grid;
    private EditText ediPhoneNum;
    private Button betSure;
    private String finalNums;
    private String finalMoney;
    private long tempMoney;
    private TextView lotteryNum;
    private TextView lotteryCodetitle;
    private ImageView more;
    private RelativeLayout helpRa;
    private Button record;
    private RelativeLayout showSelectedSub;
    private LinearLayout ll_detail;
    private ImageView imgShowSelectedSub;
    private LinearLayout request_focus;
    private TextView account;
    protected LinearLayout bottomLayout;
    protected TextView betMoney;
    protected TextView betNum;
    private LinearLayout accountLayout;
    protected LinearLayout layoutSingleWheel;
    protected WheelView timesWheel2;
    private TextView cancle;
    private TextView makeSure;
    protected LinearLayout wheelLayout;
    protected TextView wheelBetMoney;
    protected RelativeLayout superadditionLayout;
    private ImageView superaddition;
    // 倍投滚轮参数
    private boolean scrolling2 = false;
    private int timesNumSingleWheel = 1;
    protected int timesNum = 1;

    private ArrayList<String> finalContacts;// 用于保存最终的手机号码数组
    private ArrayList<TempData> tempList;
    private ArrayList<TempData> tempHandList;
    private boolean ifFromContacts = false;
    private PendingIntent sentPI;
    private int flag = 0;
    public boolean ifRefreshMoney = false;

    private Boolean readProtocol = true;
    private ImageView check;
    private TextView protocol;
    private TextView give_protocol;
// private EditText messageContent;
    private AlertDialog.Builder builder;

    private int ediPhoneLength = 0;
    private String displayCode;
    private String code;
    private String kind;
    private long money;
    private String mode;
    private String term;
    private String kindChineseName;
    private boolean ifShowSelectedSub = true;
    private boolean toRechage = false;
    private boolean ifBetEnd;
    private boolean canBet = true;
    private Boolean isSuperaddion = true;
    private UserInfTask userInfTask;
    private boolean isFrequent;
    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    private String finalEndTime;
    private boolean ifReadContacts = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.give_lottery);
        setUpViews();
        init();
    }

    private void setUpViews() {
        more = (ImageView) findViewById(R.id.more_contacts);
// more.setOnClickListener(this);
        grid = (GridView) findViewById(R.id.grid);
        betSure = (Button) findViewById(R.id.bet_sure);
        betSure.setOnClickListener(this);
        account = (TextView) findViewById(R.id.account);
        bottomLayout = (LinearLayout) this.findViewById(R.id.bottom);
        betMoney = (TextView) findViewById(R.id.order_money);
        betNum = (TextView) findViewById(R.id.bet_times_pursuit_num);
        betNum.setOnClickListener(this);
        accountLayout = (LinearLayout) findViewById(R.id.layout_account);
        wheelLayout = (LinearLayout) this.findViewById(R.id.wheel_layout);
        wheelBetMoney = (TextView) findViewById(R.id.wheel_order_money);
        layoutSingleWheel = (LinearLayout) findViewById(R.id.layout_single_wheel);
        superadditionLayout = (RelativeLayout) this.findViewById(R.id.superaddition_layout);
        superadditionLayout.setOnClickListener(this);
        superaddition = (ImageView) this.findViewById(R.id.superaddition_select);
        timesWheel2 = (WheelView) this.findViewById(R.id.times_choose2);
        timesWheel2.setNormalTx("倍");
        cancle = (TextView) findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        makeSure = (TextView) findViewById(R.id.make_sure);
        makeSure.setOnClickListener(this);
        lotteryNum = (TextView) this.findViewById(R.id.lottery_num);
        lotteryCodetitle = (TextView) findViewById(R.id.lottery_code_title);
        helpRa = (RelativeLayout) findViewById(R.id.bet_help_lin);
        helpRa.setOnClickListener(this);
        imgShowSelectedSub = (ImageView) findViewById(R.id.img_show_selected_sub);
        showSelectedSub = (RelativeLayout) findViewById(R.id.show_selected_sub);
        showSelectedSub.setOnClickListener(this);
        ll_detail = (LinearLayout) findViewById(R.id.ll_detail);
        request_focus = (LinearLayout) findViewById(R.id.request_focus);
        request_focus.requestFocus();
        record = (Button) findViewById(R.id.title_btn_right);
        record.setOnClickListener(this);
        record.setVisibility(View.GONE);
        check = (ImageView) this.findViewById(R.id.bet_check);
        check.setOnClickListener(this);
        protocol = (TextView) this.findViewById(R.id.bet_protocol);
        protocol.setText(Html.fromHtml("<u>" + BET_PROTOCOL + "</u>"));
        protocol.setOnClickListener(this);
        give_protocol = (TextView) this.findViewById(R.id.give_protocol);
        give_protocol.setText(Html.fromHtml("<u>" + GIVE_PROTOCOL + "</u>"));
        give_protocol.setOnClickListener(this);
// messageContent = (EditText) findViewById(R.id.edit_share_content);

        ediPhoneNum = (EditText) findViewById(R.id.edi_phonenum);
        ediPhoneNum.setOnClickListener(this);
        ediPhoneNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ediPhoneLength = ediPhoneNum.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ediPhoneLength == 11) {
                    boolean ifAdd = true;
                    TempData td = new TempData();
                    String str = ediPhoneNum.getText().toString();
                    // 判断赠送号码个数是否已达到最大值
                    if (tempList.size() == GIVE_NUM) {
                        ifAdd = false;
                        ediPhoneNum.setText("");
                        ViewUtil.showTipsToast(GiveLottery.this, ADD_FAIL_MORE_NUM, 0);

                    }
                    // 对手动输入的手机号码进行合法性验证
                    if (isMobileNO(str) == false) {
                        ifAdd = false;
                        ediPhoneNum.setText("");
                        ViewUtil.showTipsToast(GiveLottery.this, "号码格式不匹配，请重新输入", 0);

                    }
                    // 判断用户是否已经手动输入过该号码
                    if (ifAdd == true) {
                        for (int i = 0; i < tempHandList.size(); i++) {
                            TempData tt = tempHandList.get(i);
                            if (str.equals(tt.gettPhoneNum())) {
                                ifAdd = false;
                                ViewUtil.showTipsToast(GiveLottery.this, ADD_FAIL, 0);
                                ediPhoneNum.setText("");
                            }
                        }
                    }
                    // 判断用户是否已经从联系人中点选了此号码
                    if (ifAdd == true) {
                        for (int i = 0; i < tempList.size(); i++) {
                            TempData tt = tempList.get(i);
                            if (str.equals(tt.gettPhoneNum())) {
                                ifAdd = false;
                                ViewUtil.showTipsToast(GiveLottery.this, ADD_FAIL + ",姓名是\"" + tt.gettName() +
                                    "\"", 0);
                                ediPhoneNum.setText("");
                            }
                        }
                    }
                    if (ifAdd == true) {
                        td.settName(str);
                        td.settPhoneNum(str);
                        td.setIfFromContacts(false);
                        tempHandList.add(td);
                        tempList.add(td);
                        recreateNameList();
                        ediPhoneNum.setText("");
                    }
                }
            }
        });
        ediPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    more.setImageResource(R.drawable.head);
                }
            }
        });
    }

    private void init() {
        finalContacts = new ArrayList<String>();
        tempList = new ArrayList<TempData>();
        tempHandList = new ArrayList<TempData>();

        listItem = new ArrayList<PhoneItemData>();
        adapter = new PhoneAdapter(GiveLottery.this, listItem);
        adapter.setClickListener(this);
        gridAdapter = new PhoneGridAdapter(GiveLottery.this, tempList);
        grid.setAdapter(gridAdapter);
        gridAdapter.setSelectedPhoneNumClickListener(this);
        initContactsDialog();
        initWheel();
        initData();
        initReceiver();
        if (ifBetEnd == true) {
            stopBet();
        }
        else {
            checkAccountStatus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifRefreshMoney == true) {
            ifRefreshMoney = false;
            refreshUserMoney();
        }
    }

    private void refreshUserMoney() {
        if (HttpConnectUtil.isNetworkAvailable(GiveLottery.this)) {
            userInfTask = new UserInfTask(GiveLottery.this);
            userInfTask.setOnGetUserInfListener(this);
            userInfTask.execute(1);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private void initReceiver() {
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentPI = PendingIntent.getBroadcast(GiveLottery.this, 0, sentIntent, 0);
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        flag++;
                        if (flag == 1) {
                            ViewUtil.showTipsToast(GiveLottery.this, "短信发送成功!");
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));
    }

    private void initWheel() {
        betNum.setText("倍投");
        layoutSingleWheel.setVisibility(View.VISIBLE);
        timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, 0));
        timesWheel2.setCurrentItem(0);
        timesWheel2.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling2) {
                    updateSingleTimesText(timesWheel2);
                }
            }
        });
    }

    protected void updateSingleTimesText(WheelView item) {
        timesNumSingleWheel = item.getCurrentItem() + 1;
        resetShowMoney();
    }

    private void initContactsDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        list = new ListView(GiveLottery.this);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("从通讯录选择赠送号码");
        builder.setIcon(R.drawable.head);
        builder.setView(list);
        builder.setNegativeButton("确定", this);
        list.setAdapter(adapter);
        builder.create();
    }

    private void checkAccountStatus() {
        if (appState.getUsername() == null) {
            canBet = false;
            betSure.setText("登  录");
        }
// else {
// if (appState.getPerfectInf().equals("0")) {
// canBet = false;
// betSure.setText("完善信息");
// }
// }
    }

    public void initData() {
        isSuperaddion = preferences.getBoolean("give_lottery_if_superaddion", true);
        if (isSuperaddion == true) {
            superaddition.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            superaddition.setBackgroundResource(R.drawable.choosing_not_select);
        }
        Bundle bundle = getIntent().getExtras();
        displayCode = bundle.getString("bet_display_code");
        lotteryNum.setText(Html.fromHtml(displayCode));
        code = bundle.getString("bet_code");
        kind = bundle.getString("bet_kind");
        money = bundle.getLong("bet_money");
        mode = bundle.getString("mode");
        term = bundle.getString("bet_term");
        ifBetEnd = bundle.getBoolean("if_bet_end");
        endTimeMillis = (bundle.getLong("endtime"));
        gapMillis = (bundle.getLong("gaptime"));
        long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
        endTimeMillis -= 60 * 60 * 1000;
        if (term != null && millis > 0) {
            finalEndTime = TimeUtils.showSportsEndTime(endTimeMillis);
            resetMessageContent();
        }
        else {
            if (HttpConnectUtil.isNetworkAvailable(GiveLottery.this)) {
                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                getLotteryInf.execute();
            }
            else
                setGetTermFail();
        }

        if (null != kind && !"".equals(kind)) {
            kindChineseName = LotteryUtils.getLotteryName(kind);
        }
        if (kindChineseName != null) {
            if (term == null)
                lotteryCodetitle.setText(kindChineseName);
            else
                lotteryCodetitle.setText(kindChineseName + " " + term + "期");
        }

        account.setText(appState.getAccount() + "元");
        resetShowMoney();
    }

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(16[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    private void stopBet() {
        canBet = false;
        betSure.setText("投注截止");
        betSure.setEnabled(false);
        betSure.setTextColor(Color.GRAY);
    }

    private void executeTask() {
        GetContactsFromPhone task = new GetContactsFromPhone();
        task.execute();
    }

    public void getPhoneNum() {

        resolver = this.getContentResolver();

        int sysVersion = Integer.parseInt(VERSION.SDK);

        if (sysVersion < 7) {// 1.x版本
            peopleCursor = resolver.query(People.CONTENT_URI, null, null, null, null); // 返回所有联系人信息
            if (peopleCursor.moveToFirst()) {
                do {
                    String idColumn = peopleCursor.getString(peopleCursor.getColumnIndex(People._ID));
                    String nameColumn = peopleCursor.getString(peopleCursor.getColumnIndex(People.NAME));
                    // String phoneColumn =
// peopleCursor.getString(peopleCursor.getColumnIndex(People.NUMBER));

                    Cursor phonesCursor =
                        this.managedQuery(Phones.CONTENT_URI, new String[] {Phones.PERSON_ID, Phones.NUMBER},
                                          Phones.PERSON_ID + "=" + idColumn, null, null);
                    int i = 0;
                    PhoneItemData data = new PhoneItemData();
                    data.setName(nameColumn);
                    if (phonesCursor.moveToFirst()) {
                        do {
                            String phoneColumn =
                                phonesCursor.getString(phonesCursor.getColumnIndex(Phones.NUMBER));
                            phoneColumn = phoneColumn.replace(" ", "");
                            if (phoneColumn.startsWith("+")) {
                                phoneColumn = phoneColumn.substring(3, phoneColumn.length());
                            }
                            if (phoneColumn.startsWith("1") && phoneColumn.length() == 11) {
                                data.setPhonenums(i, phoneColumn);
                                i++;
                            }
                        }
                        while (phonesCursor.moveToNext());
                        listItem.add(data);

                        phonesCursor.close();
                    }

                }
                while (peopleCursor.moveToNext());
            }
            peopleCursor.close();
        }
        else {// 2.x版本
            peopleCursor =
                resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                               ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
            // 循环遍历
            if (peopleCursor.moveToFirst()) {
                int idColumn = peopleCursor.getColumnIndex(ContactsContract.Contacts._ID);
                int displayNameColumn = peopleCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                do {
                    // 获得联系人的ID号
                    String contactId = peopleCursor.getString(idColumn);
                    // 获得联系人姓名
                    String disPlayName = peopleCursor.getString(displayNameColumn);
                    int i = 0;
                    PhoneItemData data = new PhoneItemData();
                    data.setName(disPlayName);

                    // 查看该联系人有多少个电话号码。如果没有这返回值为0
                    int phoneCount =
                        peopleCursor.getInt(peopleCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (phoneCount > 0) {
                        // 获得联系人的电话号码
                        Cursor phonesCursor =
                            getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                       null,
                                                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
                                                           " = " + contactId, null, null);
                        if (phonesCursor.moveToFirst()) {
                            do {
                                // 遍历所有的电话号码
                                String phoneNumber =
                                    phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String phoneType =
                                    phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                phoneNumber = phoneNumber.replace(" ", "");
                                if (phoneNumber.startsWith("+")) {
                                    phoneNumber = phoneNumber.substring(3, phoneNumber.length());
                                }
                                if (phoneNumber.startsWith("1") && phoneNumber.length() == 11) {
                                    data.setPhonenums(i, phoneNumber);
                                    i++;
                                }
                            }
                            while (phonesCursor.moveToNext());

                            listItem.add(data);
                            phonesCursor.close();

                        }

                    }

                }
                while (peopleCursor.moveToNext());

            }
            peopleCursor.close();
        }
    }

    /*
     * class SendMessageAsyncTask extends AsyncTask<Void, Object, String> {
     * @Override protected void onPreExecute() { super.onPreExecute(); initProgressBarDialog("正在发送短信...");
     * showProgress(); }
     * @Override protected String doInBackground(Void... params) { sendMessage(); return null; }
     * @Override protected void onPostExecute(String result) { super.onPostExecute(result);
     * clearSelectedPhone(); dismissProgress(); // TODO Intent intent = new Intent(); Bundle bundle = new
     * Bundle(); bundle.putString("bet_kind", kind); bundle.putInt("type", 3); intent.putExtras(bundle);
     * intent.setClass(GiveLottery.this, BetSuccessPage.class); startActivityForResult(intent, 2);
     * setResult(RESULT_OK); finish(); } }
     */

    class GetContactsFromPhone
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("正在加载通讯录...");
            showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            getPhoneNum();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            dismissProgressDialog();
            if (ifReadContacts == true) {
                ifReadContacts = false;
                // 此处需实例化一个子控件，再添加到父控件上
                LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                rp.setMargins(5, 5, 5, 5);
                list = new ListView(GiveLottery.this);
                list.setLayoutParams(rp);
                list.setAdapter(adapter);
                builder.setView(list);
                builder.show();
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.more_contacts) {
            // 隐藏输入法
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (ifReadContacts == true) {
                executeTask();
            }
            else {
                adapter.notifyDataSetChanged();
                // 此处需实例化一个子控件，再添加到父控件上
                LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                rp.setMargins(5, 5, 5, 5);
                list = new ListView(GiveLottery.this);
                list.setLayoutParams(rp);
                list.setAdapter(adapter);
                builder.setView(list);
                builder.show();
            }
        }
        else if (v.getId() == R.id.edi_phonenum) {
            more.setImageResource(R.drawable.head);
        }
        else if (v.getId() == R.id.bet_help_lin) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#givelottery");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(GiveLottery.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.bet_protocol) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.BET_PROTOCOL_URL);
            bundle.putString("title", "委托投注协议");
            intent.putExtras(bundle);
            intent.setClass(GiveLottery.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.give_protocol) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.GIVELOTTERY_PROTOCOL_URL);
            bundle.putString("title", "赠送彩票协议");
            intent.putExtras(bundle);
            intent.setClass(GiveLottery.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.bet_check) {
            if (readProtocol) {
                betSure.setEnabled(false);
                readProtocol = false;
                check.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                if (ifBetEnd == false) {
                    betSure.setEnabled(true);
                }
                readProtocol = true;
                check.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.bet_sure) {
            if (!readProtocol) {
                ViewUtil.showTipsToast(this, "请勾选委托投注及赠送彩票协议");
                return;
            }
            if (HttpConnectUtil.isNetworkAvailable(GiveLottery.this)) {
                clickSubmit();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
        else if (v.getId() == R.id.title_btn_right) {
            // TODO
        }
        else if (v.getId() == R.id.bet_times_pursuit_num) {
            switchBottomLayout();
        }
        else if (v.getId() == R.id.make_sure) {
            setBetSetting();
        }
        else if (v.getId() == R.id.cancle) {
            switchBottomLayout();
        }
        else if (v.getId() == R.id.superaddition_layout) {
            if (isSuperaddion) {
                isSuperaddion = false;
                superaddition.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                isSuperaddion = true;
                superaddition.setBackgroundResource(R.drawable.choosing_select);
            }
            resetIfSuperaddion(isSuperaddion);
            resetShowMoney();
        }
        else if (v.getId() == R.id.show_selected_sub) {
            if (ifShowSelectedSub == true) {
                ifShowSelectedSub = false;
                grid.setVisibility(View.GONE);
                imgShowSelectedSub.setImageResource(R.drawable.arrow_right);
            }
            else {
                ifShowSelectedSub = true;
                grid.setVisibility(View.VISIBLE);
                imgShowSelectedSub.setImageResource(R.drawable.arrow_down);
            }
        }
    }

    private void resetIfSuperaddion(boolean b) {
        databaseData.putBoolean("give_lottery_if_superaddion", isSuperaddion);
        databaseData.commit();
    }

    private void setBetSetting() {
        timesNum = timesNumSingleWheel;
        refreshDoubleFollowText();
        switchBottomLayout();
    }

    private void refreshDoubleFollowText() {
        if (timesNum == 1)
            betNum.setText("单倍");
        else
            betNum.setText(timesNum + "倍");
        resetShowMoney();
    }

    private void switchBottomLayout() {
        if (wheelLayout.isShown()) {
            wheelLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
        else {
            timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, timesNum - 1));
            timesWheel2.setCurrentItem(timesNum - 1);
            wheelLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            resetShowMoney();
        }
    }

    // 弹出的选号dialog的点击监听
    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    private void clickSubmit() {
// if (appState.getPerfectInf().equals("0")) {
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putBoolean("from_bet", true);
// bundle.putInt("origin", 2);
// intent.putExtras(bundle);
// intent.setClass(this, PerfectInf.class);
// startActivityForResult(intent, 1);
// if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
// (new AnimationModel(GiveLottery.this)).overridePendingTransition(R.anim.push_to_right_in,
// R.anim.push_to_right_out);
// }
// }
        if (toRechage) {
            ifRefreshMoney = true;
            ActionUtil.toTopupNew(this);
        }
        else {
            if (tempList.size() < 1) {
                ViewUtil.showTipsToast(this, "您还未选取联系人");
            }
            else {
                toSubmit();
            }
        }
    }

    private void toSubmit() {
        // 开启上传手机号码的任务，返回值是对应上传顺序的短连接。在onPostExecute方法中启动发送短信的任务
        GetShortUrlTask task = new GetShortUrlTask();
        task.execute();
    }

    public class GetShortUrlTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("正在发送数据到服务器...");
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(GiveLottery.this);
            String json = null;
            try {
                json = connectNet.getJsonPost(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private HashMap<String, String> initHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2003131");
            parameter.put("pid", LotteryUtils.getPid(GiveLottery.this));
            parameter.put("phone_numbers", getPhonesStr());
            parameter.put("phone", String.valueOf(phone));
            parameter.put("codes", generateCode());
            parameter.put("subsp", getSubsp());
            parameter.put("lottery_id", kind);
            parameter.put("term", term);
            parameter.put("money", String.valueOf(money * timesNum));
            return parameter;
        }

        @Override
        protected void onPostExecute(String json) {
            // 取得短连接，启动发送短信的任务
            super.onPostExecute(json);
            dismissProgressDialog();
            if (json == null) {
                ViewUtil.showTipsToast(GiveLottery.this, "服务器数据获取失败,请稍后尝试");
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "error_desc");
// ViewUtil.showTipsToast(GiveLottery.this, data);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("bet_kind", kind);
                    if (!(Double.valueOf(appState.getAccount()) < 2)) {
                        bundle.putInt("type", 1);
                    }
                    else {
                        bundle.putInt("type", 0);
                    }
                    intent.putExtras(bundle);
                    intent.setClass(GiveLottery.this, BetSuccessPage.class);
                    startActivityForResult(intent, 2);
                    setResult(RESULT_OK);
                    finish();
                    /*
                     * //后台发送短信 for (int i = 0; i < tempList.size(); i++) { TempData td = tempList.get(i);
                     * td.setShortUrl(analyse.getData(data, td.gettPhoneNum()));// 得到对应每个号码的短连接,key为每个手机号码 }
                     * if (readSIMCard() == true) { SendMessageAsyncTask task = new SendMessageAsyncTask();
                     * task.execute(); } else { ViewUtil.showTipsToast(GiveLottery.this,
                     * "手机卡不可用，赠送短信发送失败，请检查后再次尝试"); }
                     */
                }
                else {
                    ViewUtil.showTipsToast(GiveLottery.this, "服务器数据获取失败,请稍后尝试");
                }
            }
        }

    }

    public void clearSelectedPhone() {
        tempList.clear();
        tempHandList.clear();
        for (int i = 0; i < listItem.size(); i++) {
            for (int j = 0; j < listItem.get(i).getIfChecked().length; j++) {
                PhoneItemData itemData = listItem.get(i);
                if (null != itemData.getPhonenums(j) && !"".equals(itemData.getPhonenums(j))) {
                    itemData.getIfChecked(j);
                    itemData.setIfChecked(j, false);
                }
            }
        }
        adapter.notifyDataSetChanged();
        recreateNameList();
        ediPhoneNum.setText("");
    }

    public String getSubsp() {
        if (isSuperaddion == true)
            return "1";
        else
            return "0";
    }

    public String generateCode() {
        StringBuilder allCode = new StringBuilder();
        String[] prevCode = code.split(";");
        for (String s : prevCode) {
            allCode.append(s);
            if (kind.equals("dlt")) {
                if (isGetDltNormalType(s)) { // 非生肖乐
// if (isSuperaddion) // 追加
// allCode.append(":2:");
// else
                    allCode.append(":1:");

                    if (isGetDltDantuoType(s)) {// 大乐透胆拖时
                        allCode.append("5:");
                    }
                    else {
                        String[] nums = s.split("\\|");
                        String[] redBall = nums[0].split("\\,");
                        String[] blueBall = nums[1].split("\\,");
                        if (redBall.length > 5 || blueBall.length > 2)
                            allCode.append("2:");
                        else
                            allCode.append("1:");
                    }

                }
                else {
                    allCode.append(":3:");
                    String[] blueBall = s.split("\\,");
                    if (blueBall.length > 2)
                        allCode.append("2:");
                    else
                        allCode.append("1:");
                }
            }
            allCode.append(timesNum + ";");
        }
        allCode.delete(allCode.length() - 1, allCode.length());
        return allCode.toString();
    }

    private boolean isGetDltNormalType(String s) {
        if (s.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isGetDltDantuoType(String s) {
        if (s.indexOf('$') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getPhonesStr() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < tempList.size(); i++) {
            TempData tt = tempList.get(i);
            str.append(tt.gettPhoneNum() + ",");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    /*
     * public void sendMessage() { for (int i = 0; i < tempList.size(); i++) { SmsManager smsManager =
     * SmsManager.getDefault(); TempData td = tempList.get(i); String content =
     * messageContent.getText().toString() + td.getShortUrl();// 短信内容 List<String> divideContents =
     * smsManager.divideMessage(content);// 拆分短信 for (String text : divideContents) {
     * smsManager.sendTextMessage(td.gettPhoneNum(), null, text, sentPI, null); } } }
     */
    @Override
    public boolean checkClick(int groupPosition, int childPosition, boolean ifChecked) {
        if (ifChecked == false || ifChecked == true && tempList.size() < GIVE_NUM) {
            listItem.get(groupPosition).setIfChecked(childPosition, ifChecked);
            tempList.clear();
            for (int i = 0; i < tempHandList.size(); i++) {
                TempData td = tempHandList.get(i);
                tempList.add(td);
            }
            for (int i = 0; i < listItem.size(); i++) {
                for (int j = 0; j < listItem.get(i).getIfChecked().length; j++) {
                    PhoneItemData itemData = listItem.get(i);
                    if (null != itemData.getPhonenums(j) && !"".equals(itemData.getPhonenums(j))) {
                        boolean tBoolean = itemData.getIfChecked(j);
                        if (tBoolean == true) {
                            TempData td = new TempData();
                            td.settName(itemData.getName());
                            td.settPhoneNum(itemData.getPhonenums(j));
                            td.setGroupPosition(i);
                            td.setChildPosition(j);
                            td.setIfFromContacts(true);
                            tempList.add(td);
                        }
                    }

                }
            }
            // 重新生成名单
            recreateNameList();
            return true;
        }
        else {
            ViewUtil.showTipsToast(this, ADD_FAIL_MORE_NUM, 0);
            return false;
        }

    }

    private void recreateNameList() {
        gridAdapter.notifyDataSetChanged();
        resetShowMoney();
    }

    public void resetShowMoney() {
        String moneyStr = null;
        int tempNum = tempList.size();
        if (isSuperaddion) {
            tempNum++;
        }
        moneyStr = resetMoney(tempNum);
        betMoney.setText(Html.fromHtml(moneyStr));
        wheelBetMoney.setText(Html.fromHtml(moneyStr));

        if (ifBetEnd == false) {
            resetBetSure();
        }
    }

    public String resetMoney(int tempNum) {
        String moneyStr;
        finalNums = String.valueOf(tempNum);
        tempMoney = money * tempNum * timesNumSingleWheel;
        finalMoney = String.valueOf(tempMoney);
        moneyStr = finalNums + "人     <font color='red'>" + finalMoney + "元</font>";
        return moneyStr;
    }

    public void resetBetSure() {
        if (betSure.isEnabled() && canBet) {
            if (tempMoney > Double.valueOf(appState.getAccount())) {
                toRechage = true;
                betSure.setText("充  值");
            }
            else {
                toRechage = false;
                betSure.setText("赠  送");
            }
        }
    }

    @Override
    public void onPre() {
    }

    @Override
    public void onPost(String json) {
        String inf;
        if (json == null) {
            searchFail(null);
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                String data = analyse.getData(json, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    account.setText(jo.getString("balance") + "元");
                    appState.setAccount(Double.valueOf(jo.getString("balance")));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    searchFail(null);
                }
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(GiveLottery.this);
                inf = getResources().getString(R.string.login_timeout);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(GiveLottery.this);
                inf = getResources().getString(R.string.login_again);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else {
                searchFail(null);
            }
        }
    }

    private void searchFail(String inf) {
        String failInf = "余额：查询失败";
        if (inf != null)
            failInf = "余额：" + inf;
        ViewUtil.showTipsToast(this, failInf);
    }

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_base_info");
            parameter.put("pid", LotteryUtils.getPid(GiveLottery.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        private HashMap<String, String> initFastHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001070");
            parameter.put("pid", LotteryUtils.getPid(GiveLottery.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(GiveLottery.this);
            String json = null;
            try {
                isFrequent = LotteryUtils.isFrequentLottery(kind);
                if (isFrequent) {
                    json = connectNet.getJsonGet(2, false, initFastHashMap());
                }
                else {
                    json = connectNet.getJsonGet(1, false, initHashMap());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (json != null) {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    String response = ja.getData(json, "response_data");
                    String getTerm;
                    String systemtime;
                    // 旧接口用term、systemtime，新接口用new_term、datetime，为了兼容
                    if (isFrequent) {
                        getTerm = ja.getData(response, "newterm");
                        systemtime = ja.getData(json, "datetime");
                    }
                    else {
                        getTerm = ja.getData(response, "term");
                        systemtime = ja.getData(response, "systemtime");
                    }
                    String endtime = ja.getData(response, "endtime");
                    String awardtime = ja.getData(response, "awardtime");
                    if (getTerm != null && systemtime != null && endtime != null && !endtime.equals("") &&
                        !systemtime.equals("")) {
                        term = getTerm;
                        GetServerTime time = new GetServerTime(GiveLottery.this);
                        OperateInfUtils.refreshTime(GiveLottery.this, time.formatTime(systemtime));
                        resetEndTime(endtime);

                    }
                    else {
                        stopBet();
                    }

                }
                else
                    setGetTermFail();
            }
            else {
                setGetTermFail();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    public void resetEndTime(String endtime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(endtime);
            endTimeMillis = date.getTime() - 60 * 60 * 1000;
            finalEndTime = TimeUtils.showSportsEndTime(endTimeMillis);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        resetMessageContent();
    }

    public void resetMessageContent() {
        String appName = getResources().getString(R.string.app_name);
// messageContent.setText("我通过" + appName + "送了你一注彩票,请在" + finalEndTime + "前领取.");
    }

    protected void setGetTermFail() {
        stopBet();
        ViewUtil.showTipsToast(this, BET_FAIL_NO_TERM);
    }

    @Override
    public boolean checkSelectedPhonNumClick(int position) {
        TempData td = tempList.get(position);
        tempList.remove(position);
        if (td.isIfFromContacts() == true) {
            listItem.get(td.getGroupPosition()).setIfChecked(td.getChildPosition(), false);
// adapter.notifyDataSetChanged();
        }
        else {
            tempHandList.clear();
            for (int i = 0; i < tempList.size(); i++) {
                TempData t = tempList.get(i);
                if (t.isIfFromContacts() == false)
                    tempHandList.add(t);
            }
        }

        recreateNameList();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                finish();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]:send lottery");
        String eventName = "open send lottery";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_send_lottery";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
