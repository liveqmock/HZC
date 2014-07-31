package com.haozan.caipiao.activity.bet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.cqssc.CQSSCActivity;
import com.haozan.caipiao.activity.bet.dfljy.DFLJYActivity;
import com.haozan.caipiao.activity.bet.dlt.DLTActivity;
import com.haozan.caipiao.activity.bet.hnklsf.HNKLSFActivity;
import com.haozan.caipiao.activity.bet.jlks.JLKSActivity;
import com.haozan.caipiao.activity.bet.jxssc.JXSSCActivity;
import com.haozan.caipiao.activity.bet.klsf.KLSFActivity;
import com.haozan.caipiao.activity.bet.pls.PLSActivity;
import com.haozan.caipiao.activity.bet.plw.PLWActivity;
import com.haozan.caipiao.activity.bet.qlc.QLCActivity;
import com.haozan.caipiao.activity.bet.qxc.QXCActivity;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.activity.bet.ssq.SSQActivity;
import com.haozan.caipiao.activity.bet.swxw.SWXWActivity;
import com.haozan.caipiao.activity.bet.syxw.SYXWActivity;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class BetConfirm
    extends BasicActivity
    implements OnClickListener {

    private static final String BETEND = "投注已截止";
    private static final String GETTINGBETIN = "投注信息获取中..";
    private static final String GETBETINFFAIL = "投注信息获取失败";
    private static final String PROJECT_MIN_MONEY = "方案最少金额为8元";
    private static final String THE_MOST_NUMS = "最多支持十单";
    protected static final int UPDATEBETTIME = 0;
    private static final int GETNEWINF = 1;

    public boolean ifShowInf = false;
    protected boolean ifBetEnd = false;

    // the max of the item id
    protected String resource = null;
    protected int itemNum;
    private long moneyAll = 0;
    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    private boolean isFrequent;
    // the English name of the lottery
    protected String kind = null;
    protected String term;
    protected String awardTime;
    private StringBuilder betLastTime;
    private StringBuilder luckyNum;
    private StringBuilder mStar;
    private StringBuilder todayLucky;
    private StringBuilder mode;
    protected int lotteryType;
    private String[] doubleCount;

    private TextView betKind;

    protected TextView betTerm;
    private TextView betTimeInf;
    protected TextView countDownTime;
    private TextView moneyWhole;
    private Button manualChoose;
    private Button randomChoose;
    private ImageView clear;
    private Button bet;
    private LinearLayout orderLayout;

    protected CustomDialog dlgClearWarning;

    // 是否合买
    boolean isUnite;
    // 最少合买金额提示
    protected LinearLayout ll_checkmoney;

    // store the bet item information
    public ArrayList<BetItem> betList;
    private HashMap<Integer, TextView> itemOrder;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEBETTIME:
                    betLastTime.delete(0, betLastTime.length());
                    long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
                    countDownTime.setText(TimeUtils.getCountDownTime(millis));
                    millis -= 1000;
                    if (millis >= 0) {
                        handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                        ifBetEnd = false;
                    }
                    else {
                        ifShowInf = true;
                        betTimeInf.setVisibility(View.GONE);
                        countDownTime.setText(BETEND);
                        ifBetEnd = true;
                        handler.sendEmptyMessageDelayed(GETNEWINF, 5000);
                    }
                    break;
                case GETNEWINF:
                    if (HttpConnectUtil.isNetworkAvailable(BetConfirm.this)) {
                        GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                        getLotteryInf.execute();
                    }
                    else
                        setGetTermFail();
                    break;
            }
        }
    };

    public BetConfirm() {
        betList = new ArrayList<BetItem>();
        itemOrder = new HashMap<Integer, TextView>();
        itemNum = 0;
    }

    private void startCountDown(String endtime, String systemtime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (endtime == null || systemtime == null)
            return;
        try {
            Date date1 = format1.parse(endtime);
            Date date2 = format1.parse(systemtime);
            endTimeMillis = date1.getTime();
            // 合买时间提前20分钟
            if (isUnite) {
                endTimeMillis -= 1200000;
            }
            gapMillis = date2.getTime() - System.currentTimeMillis();
            long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
            if (millis >= 0) {
                countDownTime.setText(TimeUtils.getCountDownTime(millis));
                millis -= 1000;
                // 合买时间提前20分钟
                if (isUnite && millis < 3600000) {
                    ViewUtil.showTipsToast(this, "合买提前二十分钟截止");
                }
                if (millis >= 0) {
                    handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                    ifBetEnd = false;
                }
                else {
                    betTimeInf.setVisibility(View.GONE);
                    countDownTime.setText(BETEND);
                    ifBetEnd = true;
                }
            }
            else {
// stopBetting = true;
                betTimeInf.setVisibility(View.GONE);
                countDownTime.setText(BETEND);
                ifBetEnd = true;
            }
        }
        catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_confirm);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        doubleCount = new String[50];
        for (int i = 0; i < doubleCount.length; i++) {
            doubleCount[i] = String.valueOf(i + 1);
        }
        betLastTime = new StringBuilder();
        // add by vincent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUnite = bundle.getBoolean("bet_is_unite", false);
        }
    }

    protected void initInf() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || bundle.getString("bet_kind") == null) {
            initViews();
            String betOrgNum = null;
            if (bundle != null) {
                resource = bundle.getString("resource");
                betOrgNum = bundle.getString("bet_code");
                if (betOrgNum != null) {
                    betList.clear();
                    initNumsArrray(betOrgNum);
                }
            }
            if (betOrgNum == null)
                initLastNumsArray();
        }
        String newTerm = null;
        long millis = 0;
        if (bundle != null) {
            newTerm = bundle.getString("bet_term");
            ifShowInf = bundle.getBoolean("ifShowInf");
            if (newTerm != null) {
                betTerm.setText(" " + newTerm + "期");
                term = newTerm;
                awardTime = bundle.getString("awardtime");
                endTimeMillis = (bundle.getLong("endtime"));
                gapMillis = (bundle.getLong("gaptime"));
                millis = endTimeMillis - gapMillis - System.currentTimeMillis();
                if (millis > 0) {
                    handler.removeMessages(UPDATEBETTIME);
                    handler.sendEmptyMessage(UPDATEBETTIME);
                }
            }
        }
        if (newTerm == null || millis <= 0 || isUnite == true) {
            if (HttpConnectUtil.isNetworkAvailable(BetConfirm.this)) {
                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                getLotteryInf.execute();
            }
            else
                setGetTermFail();
        }
    }

    protected void initLastNumsArray() {
    }

    protected void initNumsArrray(String betOrgNum) {
    }

    protected String generateCode(String lotteryInf) {
        String[] lotteryArray = lotteryInf.split("\\:");
        return lotteryArray[0] + ":" + lotteryArray[1] + ":" + lotteryArray[2] + ":";
    }

    protected void toModifyNum(BetItem itemClone) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("bet_id", itemClone.getId());
        bundle.putString("bet_code", itemClone.getCode());
        bundle.putString("bet_display_code", itemClone.getDisplayCode());
        bundle.putInt("bet_way", itemClone.getType());
        bundle.putLong("bet_money", itemClone.getMoney());
        bundle.putString("lucky_num", itemClone.getLuckyNum());
        bundle.putString("mstar", itemClone.getMstar());
        bundle.putString("today_lucky", itemClone.getTodayLucky());
        bundle.putString("mode", itemClone.getMode());
        bundle.putString("bet_term", term);
        bundle.putBoolean("ifShowInf", ifShowInf);
        bundle.putString("awardtime", awardTime);
        bundle.putLong("endtime", endTimeMillis);
        bundle.putLong("gaptime", gapMillis);
        intent.putExtras(bundle);
        if (kind.equals("ssq"))
            intent.setClass(BetConfirm.this, SSQActivity.class);
        else if (kind.equals("3d"))
            intent.setClass(BetConfirm.this, SDActivity.class);
        else if (kind.equals("qlc"))
            intent.setClass(BetConfirm.this, QLCActivity.class);
        else if (kind.equals("swxw"))
            intent.setClass(BetConfirm.this, SWXWActivity.class);
        else if (kind.equals("dfljy"))
            intent.setClass(BetConfirm.this, DFLJYActivity.class);
        else if (kind.equals("pls"))
            intent.setClass(BetConfirm.this, PLSActivity.class);
        else if (kind.equals("plw"))
            intent.setClass(BetConfirm.this, PLWActivity.class);
        else if (kind.equals("dlt"))
            intent.setClass(BetConfirm.this, DLTActivity.class);
        else if (kind.equals("qxc"))
            intent.setClass(BetConfirm.this, QXCActivity.class);
        else if (kind.equals("cqssc"))
            intent.setClass(BetConfirm.this, CQSSCActivity.class);
        else if (kind.equals("hnklsf"))
            intent.setClass(BetConfirm.this, HNKLSFActivity.class);
        // add by vincent
        else if (kind.equals("jx11x5"))
            intent.setClass(BetConfirm.this, SYXWActivity.class);
        else if (kind.equals("klsf"))
            intent.setClass(BetConfirm.this, KLSFActivity.class);
        else if (kind.equals("jxssc"))
            intent.setClass(BetConfirm.this, JXSSCActivity.class);
        else if (kind.equals("jlk3"))
            intent.setClass(BetConfirm.this, JLKSActivity.class);

        startActivityForResult(intent, 0);
    };

    protected void addBetItemView(BetItem item) {
        final BetItem itemClone = item;
        LayoutInflater inflater = LayoutInflater.from(BetConfirm.this);
        final View itemView = inflater.inflate(R.layout.order_each_item, null);
        RelativeLayout layoutOrder = (RelativeLayout) itemView.findViewById(R.id.layout_bet_order);
        layoutOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toModifyNum(itemClone);
            }
        });
        TextView itemCode = (TextView) itemView.findViewById(R.id.bet_code);
        itemCode.setText(Html.fromHtml(item.getDisplayCode()));
        ImageButton btRemove = (ImageButton) itemView.findViewById(R.id.bet_remove);
        btRemove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                orderLayout.removeView(itemView);
                for (int i = 0; i < betList.size(); i++) {
                    if (betList.get(i).getId() == itemClone.getId()) {
                        betList.remove(i);
                        invalidateMoney();
                        // add by vincent
                        checkMoney();

                        break;
                    }
                }
            }
        });
        orderLayout.addView(itemView);
        itemOrder.put(itemClone.getId(), itemCode);
    }

    private void setupViews() {
        setupWholeViews();
        setupWheelViews();
    }

    private void setupWholeViews() {
        betKind = (TextView) this.findViewById(R.id.bet_kind);
        betTimeInf = (TextView) this.findViewById(R.id.bet_time_inf);
        betTerm = (TextView) this.findViewById(R.id.bet_term);
        countDownTime = (TextView) this.findViewById(R.id.bet_countdown_time);
        manualChoose = (Button) this.findViewById(R.id.add_manual_operation);
        manualChoose.setOnClickListener(this);
        randomChoose = (Button) this.findViewById(R.id.add_random_operation);
        randomChoose.setOnClickListener(this);
        orderLayout = (LinearLayout) this.findViewById(R.id.choosing_balls_layout);
        moneyWhole = (TextView) this.findViewById(R.id.bet_money_inf);
        bet = (Button) this.findViewById(R.id.bet);
        bet.setOnClickListener(this);
        clear = (ImageView) this.findViewById(R.id.bet_clear_button);
        clear.setOnClickListener(this);
        // add by vincent
        ll_checkmoney = (LinearLayout) findViewById(R.id.unite_checkmoney_infomation);
        if (isUnite) {
            bet.setText(" 发起合买 ");
            if (betList.size() != 0) {
                betList.clear();
            }
            checkMoney();
        }

    }

    // add by vincent
    protected void checkMoney() {
        if (isUnite) {
            if (moneyAll < 8) {
                ll_checkmoney.setVisibility(View.VISIBLE);
                bet.setEnabled(false);
            }
            else {
                ll_checkmoney.setVisibility(View.GONE);
                bet.setEnabled(true);
            }
        }
    }

    private void setupWheelViews() {

    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("bet_kind");
            if (kind != null) {
                initViews();
                modifyData(bundle);
            }
        }
        invalidateMoney();

        // add by vincent
        checkMoney();
    }

    protected void initViews() {
        betKind.setText(LotteryUtils.getLotteryName(kind) + "投注单");
    }

    private void modifyData(Bundle bundle) {
// if (kind.equals("3d"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("ssl"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("pls"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("dlt"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("klsf"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("ssq"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("cqssc"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("hnklsf"))
// lotteryType = bundle.getInt("bet_way");
// // add by vincent
// else if (kind.equals("jx11x5"))
// lotteryType = bundle.getInt("bet_way");
// else if (kind.equals("jxssc"))
// lotteryType = bundle.getInt("bet_way");

        String[] temparray =
            {"3d", "ssl", "pls", "dlt", "klsf", "ssq", "cqssc", "hnklsf", "jx11x5", "jxssc", "jlk3"};
        for (int i = 0; i < temparray.length; i++) {
            if (kind.equals(temparray[i])) {
                lotteryType = bundle.getInt("bet_way");
                break;
            }
        }

        if (bundle != null) {
            Boolean newOne = true;
            int oldNum = -1;
            BetItem item = new BetItem();
            int id = bundle.getInt("bet_id");
            Boolean isOld = bundle.getBoolean("bet_is_old");
            if (isOld) {
                for (int i = 0; i < betList.size(); i++) {
                    if (id == betList.get(i).getId()) {
                        newOne = false;
                        oldNum = i;
                        break;
                    }
                }
            }
            if (newOne) {
                itemNum++;
                id = itemNum;
                item.setId(id);
                String betTerm = bundle.getString("bet_term");
                ifShowInf = bundle.getBoolean("ifShowInf");
                if (betTerm != null) {
                    item.setTerm(betTerm);
                    term = betTerm;
                    long endtime = bundle.getLong("endtime");
                    long gaptime = bundle.getLong("gaptime");
                    if (endtime != 0) {
                        endTimeMillis = endtime;
                        gapMillis = gaptime;
                    }
                }
                else {
                    item.setTerm(term);
                }
                item.setMode(bundle.getString("mode"));
                // changed by vincent
                String betCode = bundle.getString("bet_code");
                String displayCode = bundle.getString("bet_display_code");
                if (betCode.indexOf(";") < 0) {
                    item.setCode(bundle.getString("bet_code"));
                    item.setType(bundle.getInt("bet_way"));
                    item.setDisplayCode(displayCode);
                    item.setMoney(bundle.getLong("bet_money"));
                    item.setLuckyNum(bundle.getString("luckynum"));
                    item.setMstar(bundle.getString("mstar"));
                    item.setTodayLucky(bundle.getString("today_lucky"));
                    betList.add(item);
                    addBetItemView(item);
                }
                else {
                    String[] codes = new String[2];
                    codes = betCode.split(";");
                    String[] displayCodes = new String[2];
                    displayCodes = displayCode.split(";");

                    item.setCode(codes[0]);
                    item.setType(bundle.getInt("bet_way"));
                    item.setDisplayCode(displayCodes[0]);
                    item.setMoney(bundle.getLong("bet_money"));
                    item.setLuckyNum(bundle.getString("luckynum"));
                    item.setMstar(bundle.getString("mstar"));
                    item.setTodayLucky(bundle.getString("today_lucky"));
                    betList.add(item);
                    addBetItemView(item);

                    BetItem newItem = new BetItem();
                    if (betTerm != null) {
                        newItem.setTerm(betTerm);
                        term = betTerm;
                        long endtime = bundle.getLong("endtime");
                        long gaptime = bundle.getLong("gaptime");
                        if (endtime != 0) {
                            endTimeMillis = endtime;
                            gapMillis = gaptime;
                        }
                    }
                    else {
                        newItem.setTerm(term);
                    }
                    newItem.setMode(bundle.getString("mode"));
                    itemNum++;
                    id = itemNum;
                    newItem.setId(id);
                    newItem.setCode(codes[1]);
                    newItem.setType(bundle.getInt("bet_way"));
                    newItem.setDisplayCode(displayCodes[1]);
                    newItem.setMoney(bundle.getLong("bet_money_sub"));
                    newItem.setLuckyNum(bundle.getString("luckynum"));
                    newItem.setMstar(bundle.getString("mstar"));
                    newItem.setTodayLucky(bundle.getString("today_lucky"));
                    betList.add(newItem);
                    addBetItemView(newItem);
                }
            }
            else {
                if (oldNum >= 0) {
                    String betTerm = bundle.getString("bet_term");
                    ifShowInf = bundle.getBoolean("ifShowInf");
                    // changed by vincent
                    String betCode = bundle.getString("bet_code");
                    String displayCode = bundle.getString("bet_display_code");
                    if (betCode.indexOf(";") < 0) {
                        betList.get(oldNum).setCode(bundle.getString("bet_code"));
                        betList.get(oldNum).setDisplayCode(bundle.getString("bet_display_code"));
                        initItemData(bundle, oldNum, betTerm, betList.get(oldNum), false);
                        itemOrder.get(id).setText(Html.fromHtml(bundle.getString("bet_display_code")));
                    }
                    else {
                        String[] codes = new String[2];
                        codes = betCode.split(";");
                        String[] displayCodes = new String[2];
                        displayCodes = displayCode.split(";");
                        betList.get(oldNum).setCode(codes[0]);
                        betList.get(oldNum).setDisplayCode(displayCodes[0]);
                        itemOrder.get(id).setText(Html.fromHtml(displayCodes[0]));
                        BetItem newItem = new BetItem();
                        itemNum++;
                        id = itemNum;
                        newItem.setId(id);
                        if (betTerm != null) {
                            newItem.setTerm(betTerm);
                            term = betTerm;
                            long endtime = bundle.getLong("endtime");
                            long gaptime = bundle.getLong("gaptime");
                            if (endtime != 0) {
                                endTimeMillis = endtime;
                                gapMillis = gaptime;
                            }
                        }
                        else {
                            newItem.setTerm(term);
                        }
                        newItem.setMode(bundle.getString("mode"));
                        newItem.setCode(codes[1]);
                        newItem.setDisplayCode(displayCodes[1]);
                        initItemData(bundle, oldNum, betTerm, newItem, true);
                        betList.add(newItem);
                        addBetItemView(newItem);
                    }
//
// initItemData(bundle, oldNum, betTerm, betList.get(oldNum));
//
// itemOrder.get(id).setText(Html.fromHtml(bundle.getString("bet_display_code")));
                }
            }
        }
    }

    public void initItemData(Bundle bundle, int oldNum, String betTerm, BetItem betItem, boolean flag) {
        if (betTerm != null) {
            betItem.setTerm(betTerm);
            term = betTerm;
            long endtime = bundle.getLong("endtime");
            long gaptime = bundle.getLong("gaptime");
            if (endtime != 0) {
                endTimeMillis = endtime;
                gapMillis = gaptime;
            }
        }
        else
            betItem.setTerm(term);
        betItem.setMode(bundle.getString("mode"));
        if (flag) {
            betItem.setMoney(bundle.getLong("bet_money_sub"));
        }
        else {
            betItem.setMoney(bundle.getLong("bet_money"));
        }
        betItem.setLuckyNum(bundle.getString("luckynum"));
        betItem.setMstar(bundle.getString("mstar"));
        betItem.setTodayLucky(bundle.getString("today_lucky"));
        if (kind.equals("3d"))
            betItem.setType(lotteryType);
        else if (kind.equals("ssl"))
            betItem.setType(lotteryType);
        else if (kind.equals("pls"))
            betItem.setType(lotteryType);
        else if (kind.equals("dlt"))
            betItem.setType(lotteryType);
        else if (kind.equals("ssq"))
            betItem.setType(lotteryType);
        else if (kind.equals("qcssc"))
            betItem.setType(lotteryType);
        else if (kind.equals("hnklsf"))
            betItem.setType(lotteryType);
        // add by vincent
        else if (kind.equals("jx11x5"))
            betItem.setType(lotteryType);
        else if (kind.equals("klsf"))
            betItem.setType(lotteryType);
        else if (kind.equals("jxssc"))
            betItem.setType(lotteryType);
        else if (kind.equals("jlk3"))
            betItem.setType(lotteryType);
    }

    protected void invalidateMoney() {
        int size = betList.size();
        if (size == 0) {
            bet.setEnabled(false);
        }
        else {
            bet.setEnabled(true);
        }
        moneyAll = 0;
        for (int i = 0; i < size; i++)
            moneyAll += betList.get(i).getMoney();
        long num = moneyAll / 2;
        moneyWhole.setText(Html.fromHtml(num + "注     <font color='red'>" + moneyAll + "元</font>"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                modifyData(data.getExtras());
                invalidateMoney();
                // add by vincent
                checkMoney();
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                betList.clear();
                checkExit();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkExit();
        }
        return false;
    }

    protected void checkExit() {
        this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_random_operation) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: bet confirm click random operation");
            map.put("more_inf", "bet confirm click random operation of " + kind);
            String eventName = "v2 bet confirm click random operation";
            FlurryAgent.onEvent(eventName, map);
            String eventNameMobNew = "bet_confirm_random";
            MobclickAgent.onEvent(this, eventNameMobNew, kind);
            besttoneEventCommint(eventName);
            if (betList.size() >= 10) {
                ViewUtil.showTipsToast(this, THE_MOST_NUMS);
            }
            else {
                itemNum++;
                BetItem item = getRandomItem(itemNum);
                betList.add(item);
                addBetItemView(item);
                invalidateMoney();
            }
            checkMoney();
        }
        else if (v.getId() == R.id.add_manual_operation) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: bet confirm click manual operation");
            map.put("more_inf", "bet confirm click manual operation of " + kind);
            String eventName = "v2 bet confirm click manual operation";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMobNew = "bet_confirm_manual";
            MobclickAgent.onEvent(this, eventNameMobNew, kind);
            if (betList.size() >= 10) {
                ViewUtil.showTipsToast(this, THE_MOST_NUMS);
            }
            else {
                toChooseBall(false);
            }
            checkMoney();
        }
        else if (v.getId() == R.id.bet_clear_button) {
            if (betList.size() > 0) {
                showClearWarningDialog();
            }
            else
                toChooseBall(true);
        }
        else if (v.getId() == R.id.bet) {
            // changed by vincent
            if (isUnite) {
                if (moneyAll < 8) {
                    ViewUtil.showTipsToast(this, "方案最少金额为8元");
                }
                else if (checkUserInf()) {
                    goUnitePaying();
                }
            }
            else if (checkUserInf()) {
                goPaying();
            }
        }
    }

    private void showClearWarningDialog() {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("清空提示").setWarning().setMessage("将清除投注单数据重新选号，确定清除?").setPositiveButton("确  定",
                                                                                                       new DialogInterface.OnClickListener() {
                                                                                                           public void onClick(DialogInterface dialog,
                                                                                                                               int which) {
                                                                                                               betList.clear();
                                                                                                               orderLayout.removeAllViews();
                                                                                                               itemOrder.clear();
                                                                                                               invalidateMoney();
                                                                                                               checkMoney();
                                                                                                               goBet();
                                                                                                               dlgClearWarning.dismiss();
                                                                                                               finish();
                                                                                                           }
                                                                                                       }).setNegativeButton("取  消",
                                                                                                                            new DialogInterface.OnClickListener() {
                                                                                                                                public void onClick(DialogInterface dialog,
                                                                                                                                                    int which) {
                                                                                                                                    dlgClearWarning.dismiss();
                                                                                                                                }
                                                                                                                            });
        dlgClearWarning = customBuilder.create();
        dlgClearWarning.show();
    }

    protected void goBet() {
        Intent intent = new Intent();
        if (kind.equals("ssq"))
            intent.setClass(BetConfirm.this, SSQActivity.class);
        else if (kind.equals("3d"))
            intent.setClass(BetConfirm.this, SDActivity.class);
        else if (kind.equals("qlc"))
            intent.setClass(BetConfirm.this, QLCActivity.class);
        else if (kind.equals("swxw"))
            intent.setClass(BetConfirm.this, SWXWActivity.class);
        else if (kind.equals("dfljy"))
            intent.setClass(BetConfirm.this, DFLJYActivity.class);
        else if (kind.equals("pls"))
            intent.setClass(BetConfirm.this, PLSActivity.class);
        else if (kind.equals("plw"))
            intent.setClass(BetConfirm.this, PLWActivity.class);
        else if (kind.equals("dlt"))
            intent.setClass(BetConfirm.this, DLTActivity.class);
        else if (kind.equals("qxc"))
            intent.setClass(BetConfirm.this, QXCActivity.class);
        else if (kind.equals("klsf"))
            intent.setClass(BetConfirm.this, KLSFActivity.class);
        else if (kind.equals("cqssc"))
            intent.setClass(BetConfirm.this, CQSSCActivity.class);
        else if (kind.equals("hnklsf"))
            intent.setClass(BetConfirm.this, HNKLSFActivity.class);
        else if (kind.equals("jx11x5"))
            intent.setClass(BetConfirm.this, SYXWActivity.class);
        else if (kind.equals("jxssc"))
            intent.setClass(BetConfirm.this, JXSSCActivity.class);
        else if (kind.equals("jlk3"))
            intent.setClass(BetConfirm.this, JLKSActivity.class);
        startActivity(intent);
    }

    protected void toChooseBall(Boolean chooseAgain) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("from_hall", chooseAgain);
        bundle.putString("bet_term", term);
        bundle.putBoolean("ifShowInf", ifShowInf);
        bundle.putString("awardtime", awardTime);
        bundle.putLong("endtime", endTimeMillis);
        bundle.putLong("gaptime", gapMillis);
        intent.putExtras(bundle);
        if (kind.equals("ssq"))
            intent.setClass(BetConfirm.this, SSQActivity.class);
        else if (kind.equals("3d"))
            intent.setClass(BetConfirm.this, SDActivity.class);
        else if (kind.equals("qlc"))
            intent.setClass(BetConfirm.this, QLCActivity.class);
        else if (kind.equals("swxw"))
            intent.setClass(BetConfirm.this, SWXWActivity.class);
        else if (kind.equals("dfljy"))
            intent.setClass(BetConfirm.this, DFLJYActivity.class);
        else if (kind.equals("pls"))
            intent.setClass(BetConfirm.this, PLSActivity.class);
        else if (kind.equals("plw"))
            intent.setClass(BetConfirm.this, PLWActivity.class);
        else if (kind.equals("dlt"))
            intent.setClass(BetConfirm.this, DLTActivity.class);
        else if (kind.equals("qxc"))
            intent.setClass(BetConfirm.this, QXCActivity.class);
        else if (kind.equals("klsf"))
            intent.setClass(BetConfirm.this, KLSFActivity.class);
        else if (kind.equals("cqssc"))
            intent.setClass(BetConfirm.this, CQSSCActivity.class);
        else if (kind.equals("hnklsf"))
            intent.setClass(BetConfirm.this, HNKLSFActivity.class);
        // add by vincent
        else if (kind.equals("jx11x5"))
            intent.setClass(BetConfirm.this, SYXWActivity.class);
        else if (kind.equals("jxssc"))
            intent.setClass(BetConfirm.this, JXSSCActivity.class);
        else if (kind.equals("jlk3"))
            intent.setClass(BetConfirm.this, JLKSActivity.class);

        startActivityForResult(intent, 0);
    }

    protected BetItem getRandomItem(int itemNum) {
        return null;
    }

    protected boolean checkUserInf() {
        if (appState.getUsername() == null) {
            String allCode = "";
            String allDisplayCode = "";
            for (int i = 0; i < betList.size(); i++) {
                allCode += betList.get(i).getCode();
                allCode += ";";
                allDisplayCode += betList.get(i).getDisplayCode();
                allDisplayCode += "<br/>";
            }
            if (!allCode.equals("") && !allDisplayCode.equals("")) {
                allCode = allCode.substring(0, allCode.length() - 1);
                allDisplayCode.subSequence(0, allDisplayCode.length() - 2);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("bet_kind", kind);
                bundle.putString("bet_term", term);
                bundle.putBoolean("ifShowInf", ifShowInf);
                bundle.putLong("endtime", endTimeMillis);
                bundle.putLong("gaptime", gapMillis);
                // add by vincent
                bundle.putBoolean("bet_is_unite", isUnite);

                generateExtraInf(bundle);
                bundle.putString("bet_code", allCode);
                bundle.putString("bet_display_code", allDisplayCode);
                bundle.putLong("bet_money", moneyAll);
                bundle.putString("forwardFlag", "收银台");
                bundle.putBoolean("fromBet", true);
                bundle.putBoolean("ifStartSelf", false);
                bundle.putString("about", "left");
                intent.putExtras(bundle);
                intent.setClass(BetConfirm.this, Login.class);
// intent.setClass(BetConfirm.this, StartUp.class);
                startActivityForResult(intent, 1);
            }
            return false;
        }
        else
            return true;
    }

    protected void goPaying() {
        String allCode = "";
        String allDisplayCode = "";
        for (int i = 0; i < betList.size(); i++) {
            allCode += betList.get(i).getCode();
            allCode += ";";
            allDisplayCode += betList.get(i).getDisplayCode();
            allDisplayCode += "<br/>";
        }
        if (!allCode.equals("") && !allDisplayCode.equals("")) {
            allCode = allCode.substring(0, allCode.length() - 1);
            allDisplayCode.subSequence(0, allDisplayCode.length() - 2);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("bet_kind", kind);
            bundle.putString("bet_term", term);
            bundle.putBoolean("ifShowInf", ifShowInf);
            bundle.putLong("endtime", endTimeMillis);
            bundle.putLong("gaptime", gapMillis);
            generateExtraInf(bundle);
            bundle.putString("bet_code", allCode);
            bundle.putString("bet_display_code", allDisplayCode);
            bundle.putLong("bet_money", moneyAll);
            bundle.putBoolean("if_bet_end", ifBetEnd);
            intent.putExtras(bundle);
            intent.setClass(BetConfirm.this, BetPayDigital.class);
            startActivityForResult(intent, 1);
        }
    }

    // add by vincent
    protected void goUnitePaying() {
        if (moneyAll < 0) {
            ViewUtil.showTipsToast(this, PROJECT_MIN_MONEY);
        }
        else {
            String allCode = "";
            String allDisplayCode = "";
            for (int i = 0; i < betList.size(); i++) {
                allCode += betList.get(i).getCode();
                allCode += ";";
                allDisplayCode += betList.get(i).getDisplayCode();
                allDisplayCode += "<br/>";
            }
            if (!allCode.equals("") && !allDisplayCode.equals("")) {
                allCode = allCode.substring(0, allCode.length() - 1);
                allDisplayCode.subSequence(0, allDisplayCode.length() - 2);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("bet_kind", kind);
                bundle.putString("bet_term", term);
                bundle.putBoolean("ifShowInf", ifShowInf);
                bundle.putLong("endtime", endTimeMillis);
                bundle.putLong("gaptime", gapMillis);
                generateExtraInf(bundle);
                bundle.putString("bet_code", allCode);
                bundle.putString("bet_display_code", allDisplayCode);
                bundle.putLong("bet_money", moneyAll);
                intent.putExtras(bundle);
                intent.setClass(BetConfirm.this, BetPayUniteDigital.class);
                startActivityForResult(intent, 1);
            }
        }
    }

    private void generateExtraInf(Bundle bundle) {
        luckyNum = new StringBuilder();
        mStar = new StringBuilder();
        todayLucky = new StringBuilder();
        mode = new StringBuilder();
        if (betList.size() > 1) {
            for (int i = 0; i < betList.size(); i++) {
                String luckyNumstr = betList.get(i).getLuckyNum();
                if (luckyNumstr != null)
                    luckyNum.append(luckyNumstr + ";");
                else
                    luckyNum.append("-;");
                String mStarStr = betList.get(i).getMstar();
                if (mStarStr != null)
                    mStar.append(mStarStr + ";");
                else
                    mStar.append("-;");
                String todayLuckystr = betList.get(i).getTodayLucky();
                if (todayLuckystr != null)
                    todayLucky.append(todayLuckystr + ";");
                else
                    todayLucky.append("-;");
                mode.append(betList.get(i).getMode() + ";");
            }
            luckyNum.deleteCharAt(luckyNum.length() - 1);
            mStar.deleteCharAt(mStar.length() - 1);
            todayLucky.deleteCharAt(todayLucky.length() - 1);
            mode.deleteCharAt(mode.length() - 1);
        }
        else {
            String luckyNumstr = betList.get(0).getLuckyNum();
            if (luckyNumstr != null)
                luckyNum.append(luckyNumstr);
            String mStarStr = betList.get(0).getLuckyNum();
            if (mStarStr != null)
                mStar.append(mStarStr);
            String todayLuckystr = betList.get(0).getLuckyNum();
            if (todayLuckystr != null)
                todayLucky.append(todayLuckystr);
            mode.append(betList.get(0).getMode());
        }
        bundle.putString("luckynum", luckyNum.toString());
        bundle.putString("mstar", mStar.toString());
        bundle.putString("today_lucky", todayLucky.toString());
        bundle.putString("mode", mode.toString());
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "lottery_base_info");
        parameter.put("pid", LotteryUtils.getPid(this));
        parameter.put("lottery_id", kind);
        parameter.put("new", "1");
        return parameter;
    }

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initFastHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001070");
            parameter.put("pid", LotteryUtils.getPid(BetConfirm.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetConfirm.this);
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
                        betTimeInf.setVisibility(View.VISIBLE);
                        countDownTime.setVisibility(View.VISIBLE);
                        term = getTerm;
                        betTerm.setText(" " + getTerm + "期");
                        GetServerTime time = new GetServerTime(BetConfirm.this);
                        OperateInfUtils.refreshTime(BetConfirm.this, time.formatTime(systemtime));
                        startCountDown(endtime, systemtime);
                        awardTime = awardtime;
                    }
                    else
                        setGetTermEnd();
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
            betTerm.setText(GETTINGBETIN);
            betTimeInf.setVisibility(View.GONE);
            countDownTime.setVisibility(View.GONE);
        }
    }

    protected void setGetTermEnd() {
        // title.setVisibility(View.GONE);
        betTerm.setText(BETEND);
        ifBetEnd = true;
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.GONE);
    }

    protected void setGetTermFail() {
        betTerm.setText(GETBETINFFAIL);
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet comfirm");
        map.put("more_inf", "open bet comfirm of " + kind);
        String eventName = "v2 open bet comfirm";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("bet_kind");
        }
        String eventName = "open_bet_comfirm";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
