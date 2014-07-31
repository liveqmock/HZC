package com.haozan.caipiao.activity.bet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.SportsBunchAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BunchInf;
import com.haozan.caipiao.types.OddsInf;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsJCLQItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryOperateUtils;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;

public class BetPaySports
    extends BetPay {
    private static final String BET_PAY_SPORTS_REQUEST_SERVICE = "2003041";

    private static final String[] SPFBUNCHNAME = {"2串1", "3串1", "4串1", "5串1", "6串1", "7串1", "8串1"};
    private static final String[] ZJQBUNCHNAME = {"2串1", "3串1", "4串1", "5串1", "6串1"};
    private static final String[] BQCBUNCHNAME = {"2串1", "3串1"};
    private static final int[] SPFCODE = {201, 301, 401, 501, 601, 701, 801};
    private static final int[] ZJQCODE = {201, 301, 401, 501, 601};
    private static final int[] BQCCODE = {201, 301};
    private static final String[] spfNum = new String[] {"3,", "1,", "0,"};
    private static final String[] spfStr = new String[] {"胜", "平", "负"};
    private static final String[] zjqStr = new String[] {"0,", "1,", "2,", "3,", "4,", "5,", "6,", "7,"};
    private static final String[] zjqStr2 = new String[] {"0", "1", "2", "3", "4", "5", "6", "7+"};
    private static final String[] bqcNum =
        new String[] {"33", "31", "30", "13", "11", "10", "03", "01", "00"};
    private static final String[] jczqBfNum = new String[] {"10", "20", "21", "30", "31", "32", "40", "41",
            "42", "50", "51", "52", "90", "00", "11", "22", "33", "99", "01", "02", "03", "04", "05", "12",
            "13", "14", "15", "23", "24", "25", "09"};
    private static final String[] bqcStr =
        new String[] {"胜胜", "胜平", "胜负", "平胜", "平平", "平负", "负胜", "负平", "负负"};
    private static final String[] jczqBfStr = new String[] {"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0",
            "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他", "0:0", "1:1", "2:2", "3:3", "平其他", "0:1", "0:2", "0:3",
            "0:4", "0:5", "1:2", "1:3", "1:4", "1:5", "2:3", "2:4", "2:5", "负其他"};
    private static final String[] sfStr = new String[] {"胜", "负"};
    private static final String[] dxfStr = new String[] {"大", "小"};
    private static final String[] sfNum = new String[] {"1", "2"};

    // 最高理论奖金显示
    private TextView moneyAward;
    // 串数选择布局
    private RelativeLayout bunchWay;
    private TextView bunchInf;
    private ImageView statusIcon;
    private GridView bunchGridView;

    private SportsBunchAdapter bunchAdapter;

    // 显示板上显示的投注具体数据信息
    private StringBuilder betDetail;
    // 后面投注的时候传给服务器的投注队伍id串,用|隔开
    private StringBuilder matchIdArray;
    private StringBuilder spHignArray;
    // 投注格式号码
    private StringBuilder betCode;
    // 加上投注金额投注玩法最终的投注格式
    private StringBuilder betCodeFinal;
    // 过关方式显示
    private StringBuilder bunchInfStr;
    // 是否显示串选择
    private boolean showBunch = true;
    // 最高理论奖金
    private float highAward = -1;

    // 倍投滚轮参数
    private boolean scrolling2 = false;
    private int timesNumSingleWheel = 1;

    // 投注队伍信息
    private ArrayList<SportsItem> spfbetList;
    private ArrayList<SportsItem> zjqbetList;
    private ArrayList<SportsItem> bqcbetList;
    private ArrayList<SportsItem> jczqBfbetList;
    // add by vincent
    private ArrayList<SportsJCLQItem> sfbetList;
    private ArrayList<SportsJCLQItem> rfsfbetList;
    private ArrayList<SportsJCLQItem> dxfbetList;

    // 存储串内容和选择信息
    private ArrayList<BunchInf> bunchInfList;
    // 存储每个球队最高最低赔率信息,[0][0]代表第一场比赛高赔率,[0][1]代表第一场比赛低赔率
    private ArrayList<OddsInf> oddsInfList;
    // add by vincent
    // 玩法
    private int lotteryType;// 竞彩足球：1为让分胜平负，2为总进球，3为半全场，7为比分，8为胜平负 竞彩 蓝球：4为胜负，5为让分胜负，6为大小分
    private String betTerm;
    private String tempType;

    private boolean isGuoguan;
    private int dgNumAll = 0;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSpecailData();
        setupSportsView();
        initSports();
    }

    private void initSports() {
        showBunch = preferences.getBoolean("show_bunch", true);
        if (showBunch) {
            bunchGridView.setVisibility(View.VISIBLE);
            statusIcon.setImageResource(R.drawable.icon_down_new);
        }
        else {
            bunchGridView.setVisibility(View.GONE);
            statusIcon.setImageResource(R.drawable.icon_up_new);
        }
        sportsBunchLayout.setVisibility(View.VISIBLE);
        bunchAdapter = new SportsBunchAdapter(BetPaySports.this, bunchInfList);
        bunchGridView.setAdapter(bunchAdapter);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("bet_kind");
            if (kind != null) {
                initInf();
            }
        }
        showBetDetail();
        createBasicCode();
        bunchAdapter.notifyDataSetChanged();
        if (isGuoguan) {
            generateOdds();
        }
        refreshBunchInf();
        caluculateBetMoney();
        if (kind.equals("jczq")) {
            if (isGuoguan)
                warningTips.setText("奖金以出票时刻固定赔率为准,请留意投注记录赔率");
            else
                warningTips.setText("比分单关赔率以出票时赔率为准，胜平负、总进球、半全场玩法单关赔率以开奖时赔率为准");
            warningTips.setVisibility(View.VISIBLE);
        }
        if (kind.equals("jclq")) {
            if (isGuoguan)
                warningTips.setText("奖金以出票时刻固定赔率为准,请留意投注记录赔率");
            else
                warningTips.setText("赔率以开奖时赔率为准");
            warningTips.setVisibility(View.VISIBLE);
        }
        initWheel();
    }

    private void initWheel() {
        if (kind.equals("jczq") || kind.equals("jclq")) {
            betNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                               getResources().getDimension(R.dimen.large_text_size));
            betNum.setText("倍投");
            stopPursuitLayout.setVisibility(View.GONE);
            layoutWholeWheel.setVisibility(View.GONE);
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
    }

    protected void updateSingleTimesText(WheelView item) {
        timesNumSingleWheel = item.getCurrentItem() + 1;
        invalidateWheelMoney();
    }

    private void initSpecailData() {
        betDetail = new StringBuilder();
        spHignArray = new StringBuilder();
        betCode = new StringBuilder();
        betCodeFinal = new StringBuilder();
        matchIdArray = new StringBuilder();
        bunchInfList = new ArrayList<BunchInf>();
        spfbetList = new ArrayList<SportsItem>();
        zjqbetList = new ArrayList<SportsItem>();
        bqcbetList = new ArrayList<SportsItem>();
        jczqBfbetList = new ArrayList<SportsItem>();
        sfbetList = new ArrayList<SportsJCLQItem>();
        rfsfbetList = new ArrayList<SportsJCLQItem>();
        dxfbetList = new ArrayList<SportsJCLQItem>();
        oddsInfList = new ArrayList<OddsInf>();
        bunchInfStr = new StringBuilder();
        Bundle bundle = getIntent().getExtras();
        endTimeMillis = (bundle.getLong("endtime"));
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setText("截止时间：" + TimeUtils.showSportsEndTime(endTimeMillis));
        // add by vincent
        betTerm = bundle.getString("bet_term");
        isGuoguan = bundle.getBoolean("isGuoguan");
        if (betTerm.equals("71")) {
            lotteryType = 1;// 胜平负
            if (isGuoguan)
                tempType = " - 过关让分胜平负";
            else
                tempType = " - 单关让分胜平负";
        }
        else if (betTerm.equals("72")) {
            lotteryType = 2;// 总进球
            if (isGuoguan)
                tempType = " - 过关总进球";
            else
                tempType = " - 单关总进球";
        }
        else if (betTerm.equals("73")) {
            lotteryType = 7;// 比分
            if (isGuoguan)
                tempType = " - 过关比分";
            else
                tempType = " - 单关比分";
        }
        else if (betTerm.equals("74")) {
            lotteryType = 3;// 半全场
            if (isGuoguan)
                tempType = " - 过关半全场";
            else
                tempType = " - 单关半全场";
        }
        else if (betTerm.equals("75")) {
            lotteryType = 8;// 胜平负
            if (isGuoguan)
                tempType = " - 过关胜平负";
            else
                tempType = " - 单关胜平负";
        }
        else if (betTerm.equals("82")) {
            lotteryType = 4;// 胜负
            if (isGuoguan)
                tempType = " - 过关胜负";
            else
                tempType = " - 单关胜负";
// tempType = "(胜负)";
        }
        else if (betTerm.equals("81")) {
            lotteryType = 5;// 让分胜负
            if (isGuoguan)
                tempType = " - 过关让分胜负";
            else
                tempType = " - 单关让分胜负";
// tempType = "(让分胜负)";
        }
        else if (betTerm.equals("84")) {
            lotteryType = 6;// 大小分
            if (isGuoguan)
                tempType = " - 过关大小分";
            else
                tempType = " - 单关大小分";
// tempType = "(大小分)";
        }

        String kindChineseName = null;
        kindChineseName = LotteryUtils.getLotteryName(kind);
        if (kindChineseName != null && !kindChineseName.equals("胜负过关")) {
            lotteryCodetitle.setText(kindChineseName + tempType);
        }else if(kindChineseName.equals("胜负过关")){
        	lotteryCodetitle.setText(kindChineseName + " - 单场胜负过关");
        }
    }

    private void setupSportsView() {
        bunchWay = (RelativeLayout) this.findViewById(R.id.layout_bunch);
        bunchWay.setOnClickListener(this);
        bunchInf = (TextView) this.findViewById(R.id.bunch_inf);
        statusIcon = (ImageView) this.findViewById(R.id.status_icon);
        moneyAward = (TextView) this.findViewById(R.id.award_inf);
        bunchGridView = (GridView) this.findViewById(R.id.bunch_select_view);
        bunchGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bunchInfList.get(position).reverseChoose();
                bunchAdapter.notifyDataSetChanged();
                refreshBunchInf();
            }
        });
    }

    /**
     * 刷新底部过关方式显示内容
     */
    private void refreshBunchInf() {
        // bunchInfStr 过关方式显示
        bunchInfStr.delete(0, bunchInfStr.length());// 清空StringBuilder
        boolean hasBunch = false;
        for (int i = 0; i < bunchInfList.size(); i++) {
            if (bunchInfList.get(i).isChoose()) {
                hasBunch = true;
                if (isGuoguan) {
                    if (lotteryType == 1 || lotteryType == 4 || lotteryType == 5 || lotteryType == 6 ||
                        lotteryType == 8) {
                        bunchInfStr.append(SPFBUNCHNAME[i] + ",");
                    }
                    else if (lotteryType == 2) {
                        bunchInfStr.append(ZJQBUNCHNAME[i] + ",");
                    }
                    else if (lotteryType == 3 || lotteryType == 7) {
                        bunchInfStr.append(BQCBUNCHNAME[i] + ",");
                    }
                }
                else {
                    bunchInfStr.append("单关,");
                }
            }
        }
        if (hasBunch) {
            enableBet();
            bunchInfStr.delete(bunchInfStr.length() - 1, bunchInfStr.length());
            bunchInf.setText(bunchInfStr.toString());
        }
        else {
            disableBet();
            bunchInf.setText("请选择过关方式");
        }
        if (isGuoguan) {
            moneyAward.setVisibility(View.VISIBLE);
            calculateAward();
        }
        else {
            moneyAward.setVisibility(View.GONE);
        }
        caluculateBetMoney();
    }

    // TODO
    private void initInf() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("bet_kind") != null) {
            if (lotteryType == 1 || lotteryType == 8) {
                spfbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {
                    // 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (spfbetList.size() > 8) {
                        bunchMax = 8;
                    }
                    else {
                        bunchMax = spfbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(SPFBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + SPFBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 2) {
                zjqbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {
                    // 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (zjqbetList.size() > 6) {
                        bunchMax = 6;
                    }
                    else {
                        bunchMax = zjqbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(ZJQBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + ZJQBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 3) {
                bqcbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {// 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (bqcbetList.size() > 3) {
                        bunchMax = 3;
                    }
                    else {
                        bunchMax = bqcbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(BQCBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + BQCBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 7) {
                jczqBfbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {// 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (jczqBfbetList.size() > 3) {
                        bunchMax = 3;
                    }
                    else {
                        bunchMax = jczqBfbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(BQCBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + BQCBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 4) {
                sfbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {
                    // 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (sfbetList.size() > 8) {
                        bunchMax = 8;
                    }
                    else {
                        bunchMax = sfbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(SPFBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + SPFBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 5) {
                rfsfbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {
                    // 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (rfsfbetList.size() > 8) {
                        bunchMax = 8;
                    }
                    else {
                        bunchMax = rfsfbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(SPFBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + SPFBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
            else if (lotteryType == 6) {
                dxfbetList = getIntent().getParcelableArrayListExtra("bet_sports_data");
                if (isGuoguan) {
                    // 根据比赛场次生成串数及默认选择最大的串数
                    int bunchMax;
                    if (dxfbetList.size() > 8) {
                        bunchMax = 8;
                    }
                    else {
                        bunchMax = dxfbetList.size();
                    }
                    for (int i = 0; i < bunchMax - 1; i++) {
                        BunchInf bunch = new BunchInf();
                        bunch.setChoose(false);
                        bunch.setName(SPFBUNCHNAME[i]);
                        bunchInfList.add(bunch);
                    }
                    bunchInfList.get(bunchMax - 2).setChoose(true);
                    bunchInf.setText("当前过关方式：" + SPFBUNCHNAME[bunchMax - 2]);
                }
                else {
                    BunchInf bunch = new BunchInf();
                    bunch.setChoose(true);
                    bunch.setName("单关");
                    bunchInfList.add(bunch);
                    bunchInf.setText("当前过关方式：" + "单关");
                }
            }
        }
    }

    @Override
    protected void invalidateWheelMoney() {
        long wheelMoney = 0;
        if (kind.equals("jczq") || kind.equals("jclq")) {
            wheelMoney = money * timesNumSingleWheel;
        }
        shareMoney = wheelMoney;
        long num = wheelMoney / 2;
        if (isSuperaddion)
            wheelMoney = wheelMoney / 2 * 3;
        String moneyStr = num + "注     <font color='red'>" + wheelMoney + "元</font>";
        wheelBetMoney.setText(Html.fromHtml(moneyStr));
    }

    @Override
    protected void setBetSetting() {
        if (kind.equals("jczq") || kind.equals("jclq")) {
            timesNum = timesNumSingleWheel;
            if (highAward != 0) {
                DecimalFormat format = new DecimalFormat("#.00");
                moneyAward.setText(Html.fromHtml("理论最高奖金：" + format.format(highAward * timesNum) + "元"));
            }
            else {
                moneyAward.setText("");
            }
            refreshDoubleFollowText();
        }
        switchBottomLayout();
    }

    @Override
    protected void refreshDoubleFollowText() {
        if (kind.equals("jczq") || kind.equals("jclq")) {
            if (timesNum == 1)
                betNum.setText("单倍");
            else
                betNum.setText(timesNum + "倍");
        }
        invalidateMoney();
    }

    @Override
    protected void switchBottomLayout() {
        if (wheelLayout.isShown()) {
            wheelLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
        else {
            if (kind.equals("jczq") || kind.equals("jclq")) {
                timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, timesNum - 1));
                timesWheel2.setCurrentItem(timesNum - 1);
            }
            wheelLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            invalidateWheelMoney();
        }
    }

    /**
     * 生成胜平负投注格式,除倍数外
     * 
     * @return 生成的投注格式
     */
    private void createBasicCode() {
        betCode.delete(0, betCode.length());
        matchIdArray.delete(0, matchIdArray.length());
        if (lotteryType == 1 || lotteryType == 8) {
            for (int i = 0; i < spfbetList.size(); i++) {
                SportsItem item = spfbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 3; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(spfNum[m]);
                    }
                }

                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != spfbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }
        }
        else if (lotteryType == 2) {
            for (int i = 0; i < zjqbetList.size(); i++) {
                SportsItem item = zjqbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 8; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(zjqStr[m]);
                    }
                }

                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != zjqbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }
        }
        else if (lotteryType == 3) {
            for (int i = 0; i < bqcbetList.size(); i++) {
                SportsItem item = bqcbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 9; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(bqcNum[m] + ",");
                    }
                }

                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != bqcbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }

        }
        else if (lotteryType == 7) {
            for (int i = 0; i < jczqBfbetList.size(); i++) {
                SportsItem item = jczqBfbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 31; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(jczqBfNum[m] + ",");
                    }
                }

                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != jczqBfbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }

        }
        else if (lotteryType == 4) {
            for (int i = 0; i < sfbetList.size(); i++) {
                SportsJCLQItem item = sfbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(sfNum[1 - m] + ",");
                    }
                }
                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != sfbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }
        }
        else if (lotteryType == 5) {
            for (int i = 0; i < rfsfbetList.size(); i++) {
                SportsJCLQItem item = rfsfbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(sfNum[1 - m] + ",");
                    }
                }
                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != rfsfbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }
        }
        else if (lotteryType == 6) {
            for (int i = 0; i < dxfbetList.size(); i++) {
                SportsJCLQItem item = dxfbetList.get(i);
                betCode.append(item.getWeekDay() + "$" + item.getIdBetNum() + "=");
                matchIdArray.append(item.getId());
                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betCode.append(sfNum[m] + ",");
                    }
                }
                betCode.delete(betCode.length() - 1, betCode.length());
                if (i != dxfbetList.size() - 1) {
                    betCode.append("|");
                    matchIdArray.append(",");
                }
            }
        }
        betCode.append(":");
    }

    /**
     * 计算理论奖金
     */
    // TODO
    private void calculateAward() {
        // 过关方式数组，比如201&301
        highAward = 0;
        boolean hasBunch = false;
        for (int i = bunchInfList.size() - 1; i >= 0; i--) {
            if (bunchInfList.get(i).isChoose()) {
                hasBunch = true;
                if (lotteryType == 1 || lotteryType == 4 || lotteryType == 5 || lotteryType == 6 ||
                    lotteryType == 8) {
                    // 假设是某种过关方式如第一种方式2串1，则需要将最大赔率和过关方式传入计算最高奖金
                    highAward += LotteryOperateUtils.getBiggestAward(SPFCODE[i], spHignArray.toString());
                }
                else if (lotteryType == 2) {
                    highAward += LotteryOperateUtils.getBiggestAward(ZJQCODE[i], spHignArray.toString());
                }
                else if (lotteryType == 3 || lotteryType == 7) {
                    highAward += LotteryOperateUtils.getBiggestAward(BQCCODE[i], spHignArray.toString());
                }
            }
        }
        // 最高理论奖金显示
        if (hasBunch) {
            DecimalFormat format = new DecimalFormat("#.00");
            moneyAward.setText(Html.fromHtml("理论最高奖金：" + format.format(highAward * timesNum) + "元"));
        }
        else {
            moneyAward.setText("");
        }
    }

    /**
     * 计算投注倍数和金额
     */
    // TODO
    private void caluculateBetMoney() {
        betCodeFinal.delete(0, betCodeFinal.length());
        betCodeFinal.append(betCode.toString());
        // 总注数
        int sum = 0;
        int numTemp = 0;
        if (isGuoguan) {
            for (int i = 0; i < bunchInfList.size(); i++) {
                if (bunchInfList.get(i).isChoose()) {
                    if (lotteryType == 1 || lotteryType == 4 || lotteryType == 5 || lotteryType == 6 ||
                        lotteryType == 8) {
                        numTemp = LotteryOperateUtils.getZhushu(SPFCODE[i], betCode.toString());
                    }
                    else if (lotteryType == 2) {
                        numTemp = LotteryOperateUtils.getZhushu(ZJQCODE[i], betCode.toString());
                    }
                    else if (lotteryType == 3 || lotteryType == 7) {
                        numTemp = LotteryOperateUtils.getZhushu(BQCCODE[i], betCode.toString());
                    }
                    betCodeFinal.append(numTemp * 2 + ";");
                    sum += numTemp;
                }
            }
        }
        else {
            betCodeFinal.append(dgNumAll * 2 + ";");
            sum = dgNumAll;
        }
        betCodeFinal.delete(betCodeFinal.length() - 1, betCodeFinal.length());
        betCodeFinal.append(":");
        if (isGuoguan) {
            if (numTemp != 0) {
                for (int i = 0; i < bunchInfList.size(); i++) {
                    if (bunchInfList.get(i).isChoose()) {
                        if (lotteryType == 1 || lotteryType == 4 || lotteryType == 5 || lotteryType == 6 ||
                            lotteryType == 8) {
                            betCodeFinal.append(SPFCODE[i] + ";");
                        }
                        else if (lotteryType == 2) {
                            betCodeFinal.append(ZJQCODE[i] + ";");
                        }
                        else if (lotteryType == 3 || lotteryType == 7) {
                            betCodeFinal.append(BQCCODE[i] + ";");
                        }
                    }
                }
            }
        }
        else {
            betCodeFinal.append("100" + ";");
        }
        betCodeFinal.delete(betCodeFinal.length() - 1, betCodeFinal.length());
        betCodeFinal.append(":");
        money = sum * 2;
        invalidateMoney();
    }

    /**
     * 计算每场比赛选中场次的最大和最小赔率,分别存入oddsHighList列表和oddsLowList列表
     */
    private void generateOdds() {
        if (lotteryType == 1 || lotteryType == 8) {
            for (int i = 0; i < spfbetList.size(); i++) {
                SportsItem item = spfbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 3; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 2) {
            for (int i = 0; i < zjqbetList.size(); i++) {
                SportsItem item = zjqbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 8; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 3) {
            for (int i = 0; i < bqcbetList.size(); i++) {
                SportsItem item = bqcbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 9; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 7) {
            for (int i = 0; i < jczqBfbetList.size(); i++) {
                SportsItem item = jczqBfbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 31; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 4) {
            for (int i = 0; i < sfbetList.size(); i++) {
                SportsJCLQItem item = sfbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 2; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[1 - j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 5) {
            for (int i = 0; i < rfsfbetList.size(); i++) {
                SportsJCLQItem item = rfsfbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 2; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[1 - j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
        else if (lotteryType == 6) {
            for (int i = 0; i < dxfbetList.size(); i++) {
                SportsJCLQItem item = dxfbetList.get(i);
                String[] oddsArray = item.getOdds();
                OddsInf oddsInf = new OddsInf();
                oddsInf.setIndex(i + 1);
                for (int j = 0; j < 2; j++) {
                    if (item.getStatus(j)) {
                        float odds = Float.valueOf(oddsArray[j]);
                        if (oddsInf.getHighOdds() < odds || oddsInf.getHighOdds() == 0) {
                            oddsInf.setHighOdds(odds);
                        }
                        if (oddsInf.getLowOdds() > odds || oddsInf.getLowOdds() == 0) {
                            oddsInf.setLowOdds(odds);
                        }
                    }
                }
                oddsInfList.add(oddsInf);
            }
            for (int i = 0; i < oddsInfList.size(); i++) {
                spHignArray.append(oddsInfList.get(i).getHighOdds() + "|");
            }
            spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
        }
    }

    /**
     * 显示顶部投注比赛信息
     */
    private void showBetDetail() {
        betDetail.delete(0, betDetail.length());// 清空StringBuilder
        if (lotteryType == 1 || lotteryType == 8) {
            for (int i = 0; i < spfbetList.size(); i++) {
                SportsItem item = spfbetList.get(i);
                betDetail.append(item.getMatchHomeTeamName());
                if (lotteryType == 1) {
                    if (item.getConcede().equals("0")) {
                        betDetail.append(" vs ");
                    }
                    else {
                        if (Float.valueOf(item.getConcede()) > 0) {
                            betDetail.append(" <font color=\"#CD2626\">(" + item.getConcede() + ")</font> vs");
                        }
                        else {
                            betDetail.append("<font color=\"#008000\">(" + item.getConcede() + ")</font> vs");
                        }
                    }
                }
                else {
                    betDetail.append(" vs ");
                }
                betDetail.append(item.getMatchGuessTeamName() + "<br>");
                for (int m = 0; m < 3; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + spfStr[m] + "</font>" + "(" +
                            item.getOdds()[m] + ") ");
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }

                betDetail.append("<br>");
            }
        }
        else if (lotteryType == 2) {
            for (int i = 0; i < zjqbetList.size(); i++) {
                SportsItem item = zjqbetList.get(i);
                betDetail.append(item.getMatchHomeTeamName());
                betDetail.append(" vs ");
                betDetail.append(item.getMatchGuessTeamName() + "<br>");
                int n = 0;
                for (int m = 0; m < 8; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + zjqStr2[m] + "</font>" + "(" +
                            item.getOdds()[m].subSequence(0, item.getOdds()[m].length() - 1) + ") ");
                        n++;
                        appBr(n);
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }

                if (!(n % 3 == 0))
                    betDetail.append("<br>");
            }
        }
        else if (lotteryType == 3) {
            for (int i = 0; i < bqcbetList.size(); i++) {
                SportsItem item = bqcbetList.get(i);
                betDetail.append(item.getMatchHomeTeamName());
                betDetail.append(" vs ");
                betDetail.append(item.getMatchGuessTeamName() + "<br>");
                int n = 0;
                for (int m = 0; m < 9; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + bqcStr[m] + "</font>" + "(" +
                            item.getOdds()[m].subSequence(0, item.getOdds()[m].length() - 1) + ") ");
                        n++;
                        appBr(n);
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }

                if (!(n % 3 == 0))
                    betDetail.append("<br>");
            }
        }
        else if (lotteryType == 7) {
            for (int i = 0; i < jczqBfbetList.size(); i++) {
                SportsItem item = jczqBfbetList.get(i);
                betDetail.append(item.getMatchHomeTeamName());
                betDetail.append(" vs ");
                betDetail.append(item.getMatchGuessTeamName() + "<br>");
                int n = 0;
                for (int m = 0; m < 31; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + jczqBfStr[m] + "</font>" + "(" +
                            item.getOdds()[m].subSequence(0, item.getOdds()[m].length() - 1) + ") ");
                        n++;
                        appBr(n);
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }

                if (!(n % 3 == 0))
                    betDetail.append("<br>");
            }
        }
        else if (lotteryType == 4) {
            for (int i = 0; i < sfbetList.size(); i++) {
                SportsJCLQItem item = sfbetList.get(i);

                betDetail.append(item.getMatchGuessTeamName());
                betDetail.append(" vs ");
                betDetail.append(item.getMatchHomeTeamName() + "<br>");

                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + sfStr[1 - m] + "</font>" + "(" +
                            item.getOdds()[1 - m] + ") ");
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }
                betDetail.append("<br>");
            }
        }
        else if (lotteryType == 5) {
            for (int i = 0; i < rfsfbetList.size(); i++) {
                SportsJCLQItem item = rfsfbetList.get(i);
                betDetail.append(item.getMatchGuessTeamName());
                if (item.getConcede().equals("0")) {
                    betDetail.append(" vs ");
                }
                else {
                    if (Float.valueOf(item.getConcede()) > 0) {
                        betDetail.append(" vs <font color=\"#CD2626\">(" + item.getConcede() + ")</font>");
                    }
                    else {
                        betDetail.append(" vs <font color=\"#008000\">(" + item.getConcede() + ")</font>");
                    }
                }
                betDetail.append(item.getMatchHomeTeamName() + "<br>");
                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + sfStr[1 - m] + "</font>" + "(" +
                            item.getOdds()[1 - m] + ") ");
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }
                betDetail.append("<br>");
            }
        }
        else if (lotteryType == 6) {
            for (int i = 0; i < dxfbetList.size(); i++) {
                SportsJCLQItem item = dxfbetList.get(i);
                betDetail.append(item.getMatchGuessTeamName());
                betDetail.append(" vs ");
                betDetail.append(item.getMatchHomeTeamName() + "<br>");
                for (int m = 0; m < 2; m++) {
                    if (item.getStatus(m)) {
                        betDetail.append("<font color='red'>" + dxfStr[m] + "</font>" + "(" +
                            item.getOdds()[m] + ") ");
                        if (!isGuoguan)
                            dgNumAll++;
                    }
                }
                betDetail.append("<br>");
            }
        }
        lotteryNum.setText(Html.fromHtml(betDetail.toString()));
    }

    private void appBr(int n) {
        if (n % 3 == 0)
            betDetail.append("<br>");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.layout_bunch) {
            if (showBunch) {
                bunchGridView.setVisibility(View.GONE);
                showBunch = false;
                statusIcon.setImageResource(R.drawable.icon_up_new);
            }
            else {
                bunchGridView.setVisibility(View.VISIBLE);
                showBunch = true;
                statusIcon.setImageResource(R.drawable.icon_down_new);
            }
            databaseData.putBoolean("show_bunch", showBunch);
            databaseData.commit();
        }
    }

    @Override
    protected void toBet() {
        BetTask task = new BetTask();
        task.execute(kind);
    }

    public class BetTask
        extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String json) {
            String error = null;
            submit.setEnabled(true);
            progress.dismiss();
            if (json != null) {
                String inf = null;
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    mUploadRequestTime.submitConnectSuccess(BET_PAY_SPORTS_REQUEST_SERVICE);

                    submitStatisticsBetSuccess();
                    appState.setAccount(Double.valueOf(appState.getAccount()) - wholeMoney);
// betResultDialog.setTextContent("投注请求发送成功，请查看消息盒是否出票成功，祝您中奖！如需帮助请与客服联系，QQ：" +
// LotteryConfig.QQ);
// betResultDialog.setTextContent("投注请求发送成功，请查看" + "<font color=\"#0000FF\"><u>" + "消息盒" +
// "</u></font>" + "是否出票成功，祝您中奖！如需帮助请与客服联系，QQ：" + LotteryConfig.QQ);
// betResultDialog.show();
                    // TODO changed by vincent
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
                    intent.setClass(BetPaySports.this, BetSuccessPage.class);
                    startActivityForResult(intent, 2);
                    setResult(RESULT_OK);
                    finish();
                }
                else if (status.equals("300")) {
                    error = ja.getData(json, "error_desc");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(BetPaySports.this);
                    inf = getResources().getString(R.string.login_timeout);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(BetPaySports.this);
                    inf = getResources().getString(R.string.login_again);
                    showLoginAgainDialog(inf);
                }
                else {
                    error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
                }
            }
            else {
                error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
            }
            if (error != null) {
                mUploadRequestTime.submitConnectFail(BET_PAY_SPORTS_REQUEST_SERVICE, json);

                ViewUtil.showFailDialog(BetPaySports.this, error);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... kind) {
            ConnectService connect = new ConnectService(BetPaySports.this);
            String json = null;
            try {
                json = connect.getJsonPost(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private HashMap<String, String> initHashMap() {
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", BET_PAY_SPORTS_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(BetPaySports.this));
        parameter.put("lottery_id", kind);
        parameter.put("term", term);
        parameter.put("codes", generateCode());
        parameter.put("money", String.valueOf(wholeMoney / followNum));
        parameter.put("mode", mode);
        parameter.put("phone", phone);
        if (address != null) {
            if (address.length() > 50) {
                address = address.substring(0, 50);
            }
            parameter.put("x", String.valueOf(longitude));
            parameter.put("y", String.valueOf(latitude));
            parameter.put("l", HttpConnectUtil.encodeParameter(address));
        }
        parameter.put("source",
                      HttpConnectUtil.encodeParameter(LotteryUtils.versionName(BetPaySports.this, true)));
        parameter.put("version", LotteryUtils.fullVersion(BetPaySports.this));
        if (betInfTranspond)
            parameter.put("m", HttpConnectUtil.encodeParameter(("".equals(editShare.getText().toString())
                ? "" : (editShare.getText().toString() + "\n")) + editShareContent.getText().toString()));
        if (presentOrentation != -1.0f && presentOrentation >= 0 && presentOrentation <= 360)
            parameter.put("d", String.valueOf(presentOrentation));

        if (followNum > 1) {
            if (isStopPursuit) {
                parameter.put("pursuit", String.valueOf(followNum) + "|1");
            }
            else {
                parameter.put("pursuit", String.valueOf(followNum) + "|0");
            }
        }
        if (betInfTranspond) {
            parameter.put("action", "1");
        }
        parameter.put("g", matchIdArray.toString());

        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    public String generateCode() {
        String betCode = betCodeFinal.toString() + timesNum;
        return betCode;
    }
}
