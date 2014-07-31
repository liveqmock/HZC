package com.haozan.caipiao.activity.topup;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.PerfectInf;
import com.haozan.caipiao.adapter.CategoryAdapter;
import com.haozan.caipiao.adapter.RechargeSelectionListAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;
import com.umeng.analytics.MobclickAgent;

public class RechargeSelection
    extends BasicActivity
    implements OnClickListener, OnItemClickListener, OnGetUserInfListener, PopMenuButtonClickListener {
    public final static String[] topUpNameM_cmpay = {"移动手机支付"};
    public final static String[] topUpNameM_union = {"储蓄卡语音支付", "信用卡语音支付", "快捷支付(信用卡,储蓄卡)", "银行卡网页支付"};
    public final static String[] topUpNameM_aly = {"支付宝支付", "盛付通"};
    public final static String[] topUpNameM_other = {"手机充值卡", "WAP银行", "彩票码"};

    public static String[] topUpNameM_cmpay_null = {""};
    public static String[] topUpNameM_union_null = {"", "", "", ""};
    public static String[] topUpNameM_aly_null = {"", ""};
    public static String[] topUpNameM_other_null = {"", "", ""};

    public final static String[] topUpNameM_cmpay_str = {"MobileRecharge"};
    public final static String[] topUpNameM_union_str = {"UnionpayRecharge", "UnionRreditCardpayRecharge",
            "ChinaUnionPaycharge", "BankCardNetRecharge"};
    public final static String[] topUpNameM_aly_str = {"AlipayRecharge", "SNDARecharge"};
    public final static String[] topUpNameM_other_str = {"PhoneCardRecharge", "WapBankRecharge",
            "BonusRecharge"};

    public final static String[] topUpClassNameM = {"$", "MobileRecharge", "$", "UnionpayRecharge",
            "UnionRreditCardpayRecharge", "ChinaUnionPaycharge", "BankCardNetRecharge", "$",
            "AlipayRecharge", "SNDARecharge", "$", "PhoneCardRecharge", "WapBankRecharge", "BonusRecharge"};

    public final static String[] topUpNameM = {"$", "移动手机支付", "$", "储蓄卡语音支付", "信用卡语音支付", "快捷支付（信用卡，储蓄卡）",
            "银行卡网页支付", "$", "支付宝支付", "盛付通", "$", "手机充值卡", "WAP银行", "彩票码"};

    private String[] alipayMoney = {"20元", "50元", "100元", "200元", "300元", "500元", "1000元"};
    private String[] alipayToServer = {"20", "50", "100", "200", "300", "500", "1000"};
    private String[] alipayMoneyS = {"50元", "100元", "200元", "300元", "500元", "1000元"};
    private String[] alipayToServerS = {"50", "100", "200", "300", "500", "1000"};
    private String[] bankCardNetMoney = {"20元", "50元", "100元", "200元", "300元", "500元", "1000元", "2000元"};
    private String[] bankCardNetToServer = {"20", "50", "100", "200", "300", "500", "1000", "2000"};
    private String[] sndaMoney = {"20元", "30元", "50元", "100元", "200元", "300元", "500元", "1000元"};
    private String[] sndaToServer = {"20", "30", "50", "100", "200", "300", "500", "1000"};
    private static final String[] phoneRechargeCardMoney = {"10元", "20元", "30元", "50元", "100元", "300元",
            "500元"};
    private static final String[] phoneRechargeCardToServer = {"10", "20", "30", "50", "100", "300", "500"};
    private String[] mobileMoney = {"10元", "20元", "50元", "100元", "200元", "500元", "1000元"};
    private String[] mobileToServer = {"10", "20", "50", "100", "200", "500", "1000"};
    private ListView rechargeListView;
    private String[] topUpClassName;
    private String[] topUpName;
    private Button serviceTv;
    private LayoutInflater mLayoutInflater;
    // headerview
    private LinearLayout spinner;
    private TextView spinnerText;
    private ImageView spinnerIcon;
    private TextView subTitle;
    private ImageView subTitleIcon;
    private Button toRecharge;
    private Button alipayWapRecharge;
    private String rechargeMoney = alipayToServer[0];
    private int money_index_num = 0;
    private PopMenu titlePopup;
    private EditText bonusEditText;
    private TextView activityTv;
    private String className;
    RechargeSelectionListAdapter[] adapter = new RechargeSelectionListAdapter[5];
    
    private int screenWidth;

    private CategoryAdapter mCategoryAdapter = new CategoryAdapter() {
        @Override
        protected View getTitleView(String title, int index, View convertView, ViewGroup parent) {
            TextView titleView;

            if (convertView == null) {
                titleView = (TextView) getLayoutInflater().inflate(R.layout.title, null);
            }
            else {
                titleView = (TextView) convertView;
            }

            titleView.setText(title);

            Log.i(TAG, "CategoryAdapter getTitleView");

            return titleView;
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_selection_page);
        initData();
        setUpView();
        setUpHeaderView();
        initListView();
        init();
    }

    private void init() {
        // 获取屏幕分辨率
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        screenWidth = display.getWidth();

        className = preferences.getString("last_topup_way", "RechargeSelection");
        if ("UnionpayRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.unionpay_icon);
            subTitle.setText("储蓄卡语音支付");
            if (HttpConnectUtil.isNetworkAvailable(RechargeSelection.this)) {
                UserInfTask getUserInf = new UserInfTask(RechargeSelection.this);
                getUserInf.setOnGetUserInfListener(this);
                getUserInf.execute(1);
            }
            else {
                setGetFail("信息获取失败");
                ViewUtil.showTipsToast(this, getResources().getString(R.string.network_not_avaliable));
            }
        }
        else if ("UnionRreditCardpayRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.unionpay_icon);
            subTitle.setText("信用卡语音支付");
            alipayMoney = alipayMoneyS;
            alipayToServer = alipayToServerS;
            rechargeMoney = alipayToServer[0];
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("ChinaUnionPaycharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.unionpay_icon);
            subTitle.setText("快捷支付(信用卡、储蓄卡)");
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("BankCardNetRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.yinhangka);
            subTitle.setText("银行卡网页支付");
            alipayMoney = bankCardNetMoney;
            alipayToServer = bankCardNetToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("AlipayRecharge".equals(className)) {
            toRecharge.setText("快捷支付");
            alipayWapRecharge.setVisibility(View.VISIBLE);
            subTitleIcon.setBackgroundResource(R.drawable.alipay_icon);
            subTitle.setText("支付宝支付");
            alipayMoney = bankCardNetMoney;
            alipayToServer = bankCardNetToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("SNDARecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.icon_snda);
            subTitle.setText("盛付通");
            alipayMoney = sndaMoney;
            alipayToServer = sndaToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("PhoneCardRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.phone_card_icon);
            subTitle.setText("手机充值卡");
            alipayMoney = phoneRechargeCardMoney;
            alipayToServer = phoneRechargeCardToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("WapBankRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.wap_bank_icon);
            subTitle.setText("WAP银行");
            alipayMoney = bankCardNetMoney;
            alipayToServer = bankCardNetToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else if ("BonusRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            bonusEditText.setVisibility(View.VISIBLE);
            toRecharge.setText("兑换");
            subTitleIcon.setBackgroundResource(R.drawable.red_packet);
            subTitle.setText("彩票码");
        }
        else if ("MobileRecharge".equals(className)) {
            alipayWapRecharge.setVisibility(View.GONE);
            toRecharge.setText("充值");
            subTitleIcon.setBackgroundResource(R.drawable.cmpay_safe);
            subTitle.setText("移动手机支付");
            alipayMoney = mobileMoney;
            alipayToServer = mobileToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
        else {
            // TODO
            databaseData.putString("last_topup_way", "AlipayRecharge");
            databaseData.putBoolean("recharge_yy", false);
            databaseData.commit();
            className = "AlipayRecharge";
            toRecharge.setText("快捷支付");
            alipayWapRecharge.setVisibility(View.VISIBLE);
            subTitleIcon.setBackgroundResource(R.drawable.alipay_icon);
            subTitle.setText("支付宝支付");
            alipayMoney = bankCardNetMoney;
            alipayToServer = bankCardNetToServer;
            spinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
        }
    }

    private void initData() {
        mLayoutInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        activityTv.setVisibility(View.GONE);
        init();
        exectueGetNewRechargeNameTask();
    }

    private void setUpHeaderView() {
        View headView = mLayoutInflater.inflate(R.layout.recharge_head, null);
        spinner = (LinearLayout) headView.findViewById(R.id.spinner);
        spinner.setOnClickListener(this);
        spinnerText = (TextView) headView.findViewById(R.id.spinner_text);
        spinnerIcon = (ImageView) headView.findViewById(R.id.spinner_icon);
        subTitle = (TextView) headView.findViewById(R.id.sub_title);
        subTitleIcon = (ImageView) headView.findViewById(R.id.subtitle_icon);
        bonusEditText = (EditText) headView.findViewById(R.id.red_packet_recharge_code);
        activityTv = (TextView) headView.findViewById(R.id.recharge_tv_activity);

        toRecharge = (Button) headView.findViewById(R.id.torecharge);
        alipayWapRecharge = (Button) headView.findViewById(R.id.alipay_wap);
        toRecharge.setOnClickListener(this);
        alipayWapRecharge.setOnClickListener(this);
        rechargeListView.addHeaderView(headView, null, false);
    }

    private void setUpView() {
        rechargeListView = (ListView) findViewById(R.id.recharge_list_view);
        rechargeListView.setOnItemClickListener(this);
        serviceTv = (Button) findViewById(R.id.service_tv);
        serviceTv.setOnClickListener(this);
    }

    private void initListView() {
        if (getString(R.string.app_name).equals("好中彩")) {// 多了移动支付
            topUpClassName = topUpClassNameM;
            topUpName = topUpNameM;
            adapter[0] = new RechargeSelectionListAdapter(this, 1, topUpNameM_cmpay_null);
            mCategoryAdapter.addCategory("所有充值方式", adapter[0]);
        }
        else {
            topUpClassName = LotteryUtils.topUpClassName;
            topUpName = LotteryUtils.topUpName;
        }
        adapter[1] = new RechargeSelectionListAdapter(this, 2, topUpNameM_union_null);
        mCategoryAdapter.addCategory("有银行卡", adapter[1]);
        adapter[2] = new RechargeSelectionListAdapter(this, 3, topUpNameM_aly_null);
        mCategoryAdapter.addCategory("第三方支付", adapter[2]);

// mCategoryAdapter.addCategory("有银行卡", new RechargeSelectionListAdapter(this, 6));

// mCategoryAdapter.addCategory("有充值卡", new RechargeSelectionListAdapter(this, 4));
        adapter[4] = new RechargeSelectionListAdapter(this, 5, topUpNameM_other_null);
        mCategoryAdapter.addCategory("其他方式", adapter[4]);

        rechargeListView.setAdapter(mCategoryAdapter);
        exectueGetNewRechargeNameTask();
    }

    private void exectueGetNewRechargeNameTask() {
        if (HttpConnectUtil.isNetworkAvailable(RechargeSelection.this)) {
            GetNewRechargeNameTask task = new GetNewRechargeNameTask();
            task.execute();
        }
    }

    class GetNewRechargeNameTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            String json =
                ConnectService.getURLJson(RechargeSelection.this,
                                          "http://download.haozan88.com/publish/pay/?v=" +
                                              LotteryUtils.getPid(RechargeSelection.this));
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (json != null) {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    doOnSuccess(json);
                }
            }
        }
    }

    public void doOnSuccess(String json) {
        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(json, "response_data");
        if (!"[]".equals(response)) {
            try {
                JSONArray hallArray = new JSONArray(response);
                int length = hallArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jo = hallArray.getJSONObject(i);
                    dealWithRecharges(jo);
                }
            }
            catch (Exception e) {
            }
        }
    }

    private void dealWithRecharges(JSONObject jo) {
        String rechargeWay = null;
        String description = null;
        try {
            rechargeWay = jo.getString("payChannel");
            description = jo.getString("descript");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        String[] descriptions = {};
        String name = null;
        int type = -1;
        if (!"".equals(rechargeWay) && null != rechargeWay) {
            boolean ifHasData = false;
            for (int i = 0; i < topUpNameM_cmpay_str.length; i++) {
                if (rechargeWay.equals(topUpNameM_cmpay_str[i])) {
                    ifHasData = true;
                    descriptions = topUpNameM_cmpay_null;
                    descriptions[i] = description;
                    name = topUpNameM_cmpay[i];
                    type = 0;
                    break;
                }
            }
            if (!ifHasData) {
                for (int i = 0; i < topUpNameM_union_str.length; i++) {
                    if (rechargeWay.equals(topUpNameM_union_str[i])) {
                        ifHasData = true;
                        descriptions = topUpNameM_union_null;
                        descriptions[i] = description;
                        name = topUpNameM_union[i];
                        type = 1;
                        break;
                    }
                }
            }
            if (!ifHasData) {
                for (int i = 0; i < topUpNameM_aly_str.length; i++) {
                    if (rechargeWay.equals(topUpNameM_aly_str[i])) {
                        ifHasData = true;
                        descriptions = topUpNameM_aly_null;
                        descriptions[i] = description;
                        name = topUpNameM_aly[i];
                        type = 2;
                        break;
                    }
                }
            }
            if (!ifHasData) {
                for (int i = 0; i < topUpNameM_other_str.length; i++) {
                    if (rechargeWay.equals(topUpNameM_other_str[i])) {
                        ifHasData = true;
                        descriptions = topUpNameM_other_null;
                        descriptions[i] = description;
                        name = topUpNameM_other[i];
                        type = 4;
                        break;
                    }
                }
            }
            if (type != -1) {
                className = preferences.getString("last_topup_way", "RechargeSelection");
                if (rechargeWay.equals(className)) {
                    subTitle.setText(name);
                    if (!"".equals(description) && null != description) {
                        activityTv.setVisibility(View.VISIBLE);
                        activityTv.setText(description);
                    }
                    else {
                        activityTv.setVisibility(View.GONE);
                    }

                }
                adapter[type] = new RechargeSelectionListAdapter(this, 1, descriptions);
                mCategoryAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.service_tv) {
            Intent intent = new Intent();
            intent.setClass(RechargeSelection.this, Feedback.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.spinner) {
            showPopupViews(spinner, alipayMoney, money_index_num);
        }
        else if (v.getId() == R.id.torecharge || v.getId() == R.id.alipay_wap) {
            String packName = LotteryUtils.getPackageName(this) + ".activity.topup";
            String clsName = ActionUtil.getTopUpClassName(this);
            Class<?> c = null;
            Intent intent = new Intent();
            try {
                c = Class.forName(packName + "." + clsName);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("from", true);
            bundle.putInt("money_index_num", money_index_num);
            bundle.putBoolean("from_fast", true);
            if ("AlipayRecharge".equals(clsName)) {
                if (v.getId() == R.id.torecharge)
                    bundle.putString("alipay_recharge_type", "fastrecharge");
                else
                    bundle.putString("alipay_recharge_type", "waprecharge");
            }
            else if ("BonusRecharge".equals(clsName)) {
                if (checkIput()) {
                    bundle.putString("bonus_codes", bonusEditText.getText().toString());
                    intent.putExtras(bundle);
                    intent.setClass(RechargeSelection.this, c);
                    startActivity(intent);
                    return;
                }
                else
                    return;
            }
            intent.putExtras(bundle);
            intent.setClass(RechargeSelection.this, c);
            startActivity(intent);
        }
    }

    private boolean checkIput() {
        String inf = null;
        if (bonusEditText.getText().toString().equals("")) {
            inf = "请输入12位兑换码";
            bonusEditText.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (bonusEditText.getText().toString().length() != 12 &&
            bonusEditText.getText().toString().length() != 16) {
            inf = "请输入12位兑换码";
            bonusEditText.setError(inf, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (inf != null) {
// ViewUtil.showTipsToast(this,inf);
            return false;
        }
        else
            return true;
    }

    // 弹出充值金额选择菜单
    private void showPopupViews(View anchor, String[] textArray, int lastIndex) {
        titlePopup = new PopMenu(RechargeSelection.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, textArray, null, 1, screenWidth - 20, lastIndex, false,
                             true);
        titlePopup.setButtonClickListener(this);
        int xoff = -(titlePopup.getWidth() / 2 - spinner.getWidth() + spinner.getWidth() / 2);
        titlePopup.showAsDropDown(anchor, xoff, 0);
    }

    private boolean checkPerfectInf(int way) {
        if ("0".equals(appState.getPerfectInf()) || !"1".equals(appState.getRegisterType()) &&
            "".equals(appState.getReservedPhone())) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("recharge_way", way);
            bundle.putInt("origin", 2);
            bundle.putBoolean("from_recharge", true);
            intent.putExtras(bundle);
            intent.setClass(RechargeSelection.this, PerfectInf.class);
            startActivity(intent);
            return false;
        }
        else
            return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open top up selection");
        String eventName = "v2 open top up selection";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_topup_selection";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RechargeSelection.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        boolean isNeedCompleted = false;
        // 加入headview后调整index
        index--;
        if (!topUpClassName[index].equals("$")) {
            String packName = LotteryUtils.getPackageName(this) + ".activity.topup";
            String clsName = topUpClassName[index];
            String topUpNameText = topUpName[index];
            Class<?> c = null;

            if (topUpNameText.equals("储蓄卡语音支付") || topUpNameText.equals("信用卡语音支付")) {
                isNeedCompleted = checkPerfectInf(index + 1);
            }
            else
                isNeedCompleted = true;

            if (isNeedCompleted) {
                this.setResult(25);
                Intent intent = new Intent();
                try {
                    c = Class.forName(packName + "." + clsName);
                    Bundle bundel = new Bundle();
                    bundel.putBoolean("from", false);
                    bundel.putString("sub_type", topUpName[index]);
                    intent.putExtras(bundel);
                    intent.setClass(this, c);
                    startActivity(intent);
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.getIntent().getExtras() != null) {
            boolean from = this.getIntent().getExtras().getBoolean("from");
            if (!from)
                this.finish();
        }
    }

    boolean isFirstClick = true;

    @Override
    public void onPre() {
        // TODO Auto-generated method stub
        infGetting();
    }

    @Override
    public void onPost(String json) {
        String inf = null;
        if (json == null)
            setGetFail("信息获取失败");
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                String data = analyse.getData(json, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    int bandState = jo.getInt("is_band");
                    appState.setBand(bandState);
                    if (appState.getBand() == 1) {// 账号已绑定
                        alipayMoney = alipayMoneyS;
                        alipayToServer = alipayToServerS;
                        // 初始化绑定账户后的充值金额，省份，城市
                        rechargeMoney = alipayToServer[0];
                    }
                    else {// 账号未绑定
                        rechargeMoney = alipayToServer[0];
                    }
                    toRecharge.setText("充值");
                    toRecharge.setEnabled(true);

                    // 初始化省份，城市，金额这3个控件的显示内容
                    spinnerText.setText(Html.fromHtml("<font color='#988d97'>金额：</font>" + rechargeMoney +
                        "元"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    inf = getResources().getString(R.string.inf_getting_fail);
                    setGetFail(inf);
                }
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(RechargeSelection.this);
                showLoginAgainDialog(getResources().getString(R.string.login_timeout));
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(RechargeSelection.this);
                showLoginAgainDialog(getResources().getString(R.string.login_again));
            }
            else {
                setGetFail("信息获取失败");
            }
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
        }
    }

    private void setGetFail(String inf) {
        toRecharge.setText("信息获取失败");
        toRecharge.setEnabled(false);
    }

    private void infGetting() {
        toRecharge.setText("查询资料中，请稍后");
        toRecharge.setEnabled(false);
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        rechargeMoney = alipayToServer[index];
        spinnerText.setText(Html.fromHtml("<font color='#988d97'>金额：</font>" + rechargeMoney + "元"));
        money_index_num = index;
        titlePopup.dismiss();
    }
}
