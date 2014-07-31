package com.haozan.caipiao.activity.topup;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.PerfectInf;
import com.haozan.caipiao.adapter.RechargeSelectionListViewAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.RechargeUiItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.ExpandableHeightGridView;
import com.umeng.analytics.MobclickAgent;

public class RechargeFirstPage extends BasicActivity
    implements OnClickListener, OnItemClickListener {
    public static final String TAG = "vincent";
    // 图标
    public static int[] ICONS = {R.drawable.unionpay_icon, R.drawable.unionpay_icon, R.drawable.alipay_icon,
            R.drawable.phone_card_icon, R.drawable.icon_tencent_pay,R.drawable.icon_szf_pay};
    
    public static String[] NAMES = {"储蓄卡", "信用卡", "支付宝支付", "手机充值卡", "财付通支付","神州付"};
    public static String[] DESCRIPTIONS = {"无需开通网银,安全快捷", "无需开通网银,支持信用卡", "支付宝账户余额支付和银行卡支付",
            "支持移动、联通、电信充值卡充值", "财付通账户余额支付和银行卡支付","支持移动、联通、电信充值卡充值"};
 
    public static String[] CLASS_NAME = {UnionpayDebitCardTopup.class.getSimpleName(),
            UnionPayCreditCardTopup.class.getSimpleName(), AlipayRecharge.class.getSimpleName(),
            PhoneCardRecharge.class.getSimpleName(), TencentPayTopup.class.getSimpleName(),ShenZhouFuRecharge.class.getSimpleName()};

    
    public static int[] ICONS_MOREWAYS = {R.drawable.icon_snda, R.drawable.wap_bank_icon,
            R.drawable.red_packet};
    public static String[] NAMES_MOREWAYS = {"盛付通", "WAP银行", "彩票码"};
    public static String[] DESCRIPTIONS_MOREWAYS = {"盛付通余额、一点充、手机网银支付", "中国建设银行和招商银行手机WAP充值",
            "通过活动获赠彩票码，输入即可使用"};
    public static String[] CLASS_NAME_MOREWAYS = {SNDARecharge.class.getSimpleName(),
            WapBankRecharge.class.getSimpleName(), BonusRecharge.class.getSimpleName()};
    // 好中彩 移动手机支付
    public static int[] ICONS_MOREWAYS_M = {R.drawable.cmpay_safe, R.drawable.icon_snda,
            R.drawable.wap_bank_icon, R.drawable.red_packet};
    public static String[] NAMES_MOREWAYS_M = {"移动手机支付", "盛付通", "WAP银行", "彩票码"};
    public static String[] DESCRIPTIONS_MOREWAYS_M = {"储蓄卡/信用卡,中国移动赠送的电子券", "盛付通余额、一点充、手机网银支付",
            "中国建设银行和招商银行手机WAP充值", "通过活动获赠彩票码，输入即可使用"};
    public static String[] CLASS_NAME_MOREWAYS_M = {"MobileRecharge", "SNDARecharge", "WapBankRecharge",
            "BonusRecharge"};
    private static String MORE_WAYS_TV_M = "移动手机支付、盛付通、WAP银行、彩票码充值";
    private static String MORE_WAYS_TV = "盛付通、WAP银行、彩票码充值";

    private String[] names;
    private int[] icons;
    private String[] des;
    private String[] classNames;
    private ArrayList<RechargeUiItem> arrays = new ArrayList<RechargeUiItem>();
    private ArrayList<RechargeUiItem> arrays_moreways = new ArrayList<RechargeUiItem>();

    private ExpandableHeightGridView rechargeListView;
    private ExpandableHeightGridView rechargeMoreWays;
    private String[] topUpName;
    private Button serviceTv;
    RechargeSelectionListViewAdapter adapter, adapterMoreWays;
    private RelativeLayout moreWays;
    private TextView moreWaysTv;
    private ScrollView scroll;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_first_page);
        setUpView();
        initListView();
//        exectueGetNewRechargeNameTask();
    }

    private void setUpView() {
        rechargeListView = (ExpandableHeightGridView) findViewById(R.id.recharge_list_view);
        rechargeListView.setOnItemClickListener(this);
        rechargeMoreWays = (ExpandableHeightGridView) findViewById(R.id.recharge_more_ways);
        rechargeMoreWays.setOnItemClickListener(this);
        serviceTv = (Button) findViewById(R.id.service_tv);
        serviceTv.setOnClickListener(this);
        moreWays = (RelativeLayout) findViewById(R.id.more_ways);
        moreWays.setOnClickListener(this);
        moreWaysTv = (TextView) findViewById(R.id.recharge_selection_inf);
        scroll = (ScrollView) findViewById(R.id.scroll);
    }

    private void initListView() {
        RechargeUiItem item = null;
        initListViewData();

        adapter = new RechargeSelectionListViewAdapter(this, arrays);
        rechargeListView.setAdapter(adapter);
        adapterMoreWays = new RechargeSelectionListViewAdapter(this, arrays_moreways);
        rechargeMoreWays.setAdapter(adapterMoreWays);
    }

    public void initListViewData() {
        RechargeUiItem item;
        for (int i = 0; i < NAMES.length; i++) {
            item = new RechargeUiItem();
            item.setIcon(ICONS[i]);
            item.setName(NAMES[i]);
            item.setDescription(DESCRIPTIONS[i]);
            item.setClassName(CLASS_NAME[i]);
            item.setEmphasis(true);
            arrays.add(item);
        }
        if (getString(R.string.app_name).equals("好中彩")) {
            names = NAMES_MOREWAYS_M;
            icons = ICONS_MOREWAYS_M;
            des = DESCRIPTIONS_MOREWAYS_M;
            classNames = CLASS_NAME_MOREWAYS_M;
            moreWaysTv.setText(MORE_WAYS_TV_M);
        }
        else {
            names = NAMES_MOREWAYS;
            icons = ICONS_MOREWAYS;
            des = DESCRIPTIONS_MOREWAYS;
            classNames = CLASS_NAME_MOREWAYS;
            moreWaysTv.setText(MORE_WAYS_TV);
        }

        for (int i = 0; i < names.length; i++) {
            item = new RechargeUiItem();
            item.setIcon(icons[i]);
            item.setName(names[i]);
            item.setDescription(des[i]);
            item.setClassName(classNames[i]);
            item.setEmphasis(true);
            arrays_moreways.add(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.service_tv) {
            Intent intent = new Intent();
            intent.setClass(RechargeFirstPage.this, Feedback.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.more_ways) {
            rechargeMoreWays.setVisibility(View.VISIBLE);
            moreWays.setVisibility(View.GONE);
            scroll.post(new Runnable() {

                @Override
                public void run() {
                    scroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private boolean checkPerfectInf() {
        if ("0".equals(appState.getPerfectInf()) || !"1".equals(appState.getRegisterType()) &&
            "".equals(appState.getReservedPhone())) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("origin", 2);
            bundle.putBoolean("from_recharge", true);
            intent.putExtras(bundle);
            intent.setClass(RechargeFirstPage.this, PerfectInf.class);
            startActivity(intent);
            return false;
        }
        else
            return true;
    }

    private void exectueGetNewRechargeNameTask() {
        if (HttpConnectUtil.isNetworkAvailable(RechargeFirstPage.this)) {
            GetActivityRechargeNameTask task = new GetActivityRechargeNameTask();
            task.execute();
        }
    }

    class GetActivityRechargeNameTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            String json =
                ConnectService.getURLJson(RechargeFirstPage.this,
                                          "http://download.haozan88.com/publish/pay/?v=" +
                                              LotteryUtils.getPid(RechargeFirstPage.this));
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

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
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
        for (int i = 0; i < arrays.size(); i++) {
            if (arrays.get(i).getClassName().equals(rechargeWay)) {
// arrays.get(i).setActivityDes(description);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        for (int i = 0; i < arrays_moreways.size(); i++) {
            if (arrays_moreways.get(i).getClassName().equals(rechargeWay)) {
// arrays_moreways.get(i).setActivityDes(description);
                break;
            }
        }
        adapterMoreWays.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RechargeFirstPage.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void gotoRechargeClass(int index, ArrayList<RechargeUiItem> arrays) {
        Logger.inf(TAG, "gotoRechargeClass");
        String packName = LotteryUtils.getPackageName(this) + ".activity.topup";
        String clsName = arrays.get(index).getClassName();
        Class<?> c = null;
        Intent intent = new Intent();
        try {
            c = Class.forName(packName + "." + clsName);
            Logger.inf(TAG, "clsName: " + clsName);
            Bundle bundel = new Bundle();
            bundel.putBoolean("from", false);
            intent.putExtras(bundel);
            intent.setClass(this, c);
            startActivity(intent);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
        if (arg0.getId() == R.id.recharge_list_view) {
            if (index == 0) {
                if (checkPerfectInf()) {
                    Intent intent = new Intent();
                    Bundle bundel = new Bundle();
                    bundel.putInt("type", TopupSelectBank.TOPUP_DEBIT_CARD);
                    intent.putExtras(bundel);
                    intent.setClass(this, TopupSelectBank.class);//点击储蓄卡
                    startActivity(intent);
                }
            }
            else if (index == 1) {
                if (checkPerfectInf()) {
                    Intent intent = new Intent();
                    Bundle bundel = new Bundle();
                    bundel.putInt("type", TopupSelectBank.TOPUP_CREDIT_CARD);
                    intent.putExtras(bundel);
                    intent.setClass(this, TopupSelectBank.class);
                    startActivity(intent);
                }
            }
            else {
                gotoRechargeClass(index, arrays);
            }
        }
        else if (arg0.getId() == R.id.recharge_more_ways) {
            gotoRechargeClass(index, arrays_moreways);
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_topup_first";
        MobclickAgent.onEvent(this, eventName);
    }
}
