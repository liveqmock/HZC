package com.haozan.caipiao.activity.bet.dlt;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.adapter.unite.CommissionAdapter;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;

public class DLTActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener, OnItemClickListener {
    private static final String DLT_TIPS01 = "请选择至少5个红球和2个蓝球";
    private static final String DLT_TIPS02 = "红球：胆1-4个，胆+拖>5个";

    public static final int DLT_HONGQIU_START = 1;
    public static final int DLT_HONGQIU_LENGTH = 35;
    public static final int DLT_HONGQIU_LIMIT = 20;
    public static final int DLT_HONGQIU_MIN = 5;
    public static final int DLT_HONGQIU_QIANQU_LIMIT = 4;
    public static final int DLT_HONGQIU_QIANQU_TUOMA_MIN = 2;
    public static final int DLT_HONGQIU_QIANQU_TUOMA_MAX = 16;
    public static final int DLT_LANQIU_HOUQU_DANMA = 1;
    public static final int DLT_LANQIU_HOUQU_TUOMA = 2;
    public static final int DLT_LANQIU_START = 1;
    public static final int DLT_LANQIU_LENGTH = 12;
    public static final int DLT_LANQIU_LIMIT = 12;
    public static final int DLT_LANQIU_MIN = 2;

    private static String hotCondintion = null;
    private String[] redBallTips;
    private String[] blueBallTips;
    // red ball variable
    private ArrayList<BetBall> hongqiu1;
    private BetBallsData hongqiuInf1;
    private NewBetBallsLayout redBallsLayout1;
    private ArrayList<BetBall> hongqiu2;
    private BetBallsData hongqiuInf2;
    private NewBetBallsLayout redBallsLayout2;
    // blue ball variable
    private ArrayList<BetBall> lanqiu1;
    private BetBallsData lanqiuInf1;
    private NewBetBallsLayout blueBallsLayout1;
    private ArrayList<BetBall> lanqiu2;
    private BetBallsData lanqiuInf2;
    private NewBetBallsLayout blueBallsLayout2;
    private TextView choosingCountRed1;
    private TextView choosingCountRed2;
    private TextView choosingCountBlue1;
    private TextView choosingCountBlue2;
    private RelativeLayout termLayout;
    private LinearLayout dltLinearHongqiu1;
    private LinearLayout dltLinearHongqiu2;
    private LinearLayout dltLinearLanqiu1;
    private LinearLayout dltLinearLanqiu2;

// private RelativeLayout topBgLinear;
// private LinearLayout firstIcon;
// private LinearLayout secondIcon;
// private LinearLayout thirdIcon;
// private TextView sdZhixuan;
// private TextView sdZusan;
// private TextView sdDantuo;

    private int lotteryType = 1;

    private PopMenu titlePopup;
    private int index_num = 0;
    private TextView selectInfo;
    private TextView selectInfoLanqiu;

    private TextView flagHongqiu01, flagHongqiu02, flagLanqiu01, flagLanqiu02, flagLengre01, flagLengre02,
        flagLengre03, flagLengre04, flagYilou01, flagYilou02, flagYilou03, flagYilou04;

    @Override
    public void setKind() {
        this.kind = "dlt";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.dlt);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // 大乐透（标准）
        hongqiuInf1 = new BetBallsData();
        hongqiu1 = new ArrayList<BetBall>();
        int redLength = DLT_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu1.add(ball);
        }
        hongqiuInf1.setBetBalls(hongqiu1);
        hongqiuInf1.setCount(0);
        hongqiuInf1.setColor("red");
        hongqiuInf1.setLimit(DLT_HONGQIU_LIMIT);
        hongqiuInf1.setBallType(1);

        hongqiuInf2 = new BetBallsData();
        hongqiu2 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu2.add(ball);
        }
        hongqiuInf2.setBetBalls(hongqiu2);
        hongqiuInf2.setCount(0);
        hongqiuInf2.setColor("red");
        hongqiuInf2.setLimit(DLT_HONGQIU_QIANQU_TUOMA_MAX);
        hongqiuInf2.setBallType(3);

        // init blue setion
        lanqiuInf1 = new BetBallsData();
        lanqiu1 = new ArrayList<BetBall>();
        int blueLength1 = DLT_LANQIU_LENGTH;
        for (int i = 0; i < blueLength1; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            lanqiu1.add(ball);
        }
        lanqiuInf1.setBetBalls(lanqiu1);
        lanqiuInf1.setCount(0);
        lanqiuInf1.setColor("blue");
        lanqiuInf1.setLimit(DLT_LANQIU_LIMIT);
        lanqiuInf1.setBallType(2);

        lanqiuInf2 = new BetBallsData();
        lanqiu2 = new ArrayList<BetBall>();
        int blueLength2 = DLT_LANQIU_LENGTH;
        for (int i = 0; i < blueLength2; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            lanqiu2.add(ball);
        }
        lanqiuInf2.setBetBalls(lanqiu2);
        lanqiuInf2.setCount(0);
        lanqiuInf2.setColor("blue");
        lanqiuInf2.setLimit(DLT_LANQIU_LIMIT);
        lanqiuInf2.setBallType(4);
    }

    protected void setupViews() {
        super.setupViews();
        selectInfo = (TextView) findViewById(R.id.select_info);
        selectInfoLanqiu = (TextView) findViewById(R.id.select_info_lanqiu);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        choosingCountRed1 = (TextView) this.findViewById(R.id.dlt_hongqiu_text);
        choosingCountRed2 = (TextView) this.findViewById(R.id.dlt_hongqiu_text2);
        choosingCountBlue1 = (TextView) this.findViewById(R.id.dlt_lanqiu_text);
        choosingCountBlue2 = (TextView) this.findViewById(R.id.dlt_lanqiu_text2);
        // setup red setion
        redBallsLayout1 = (NewBetBallsLayout) this.findViewById(R.id.dlt_hongqiu_balls);
        redBallsLayout2 = (NewBetBallsLayout) this.findViewById(R.id.dlt_hongqiu_balls2);
        // setup blue setion
        blueBallsLayout1 = (NewBetBallsLayout) this.findViewById(R.id.dlt_lanqiu_balls);
        blueBallsLayout2 = (NewBetBallsLayout) this.findViewById(R.id.dlt_lanqiu_balls2);
        luckyBallSelect.setEnabled(false);
        luckyBallSelect.setVisibility(View.INVISIBLE);
        dltLinearHongqiu1 = (LinearLayout) findViewById(R.id.dlt_hongqiu_ball_field);
        dltLinearHongqiu2 = (LinearLayout) findViewById(R.id.dlt_hongqiu_ball_field2);
        dltLinearLanqiu1 = (LinearLayout) findViewById(R.id.dlt_lanqiu_ball_field);
        dltLinearLanqiu2 = (LinearLayout) findViewById(R.id.dlt_lanqiu_ball_field2);
// lotteryChart.setEnabled(false);
// toolBoxChart.setBackgroundResource(R.drawable.tools_box_chart_disable);
// hotNumStatisticsLayout.setVisibility(View.VISIBLE);
// hotNumStatisticsLayout.setClickable(true);
// omitStatistticsLayout.setVisibility(View.VISIBLE);
// omitStatistticsLayout.setClickable(true);
        showSetting.setVisibility(View.VISIBLE);
        omiRela.setVisibility(View.VISIBLE);
        hotnumRela.setVisibility(View.VISIBLE);

        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);

        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagLengre01 = (TextView) findViewById(R.id.tv_flag_lengre01);
        flagYilou01 = (TextView) findViewById(R.id.tv_flag_yilou01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagLengre02 = (TextView) findViewById(R.id.tv_flag_lengre02);
        flagYilou02 = (TextView) findViewById(R.id.tv_flag_yilou02);
        flagLanqiu01 = (TextView) findViewById(R.id.tv_flag_lanqiu01);
        flagLengre03 = (TextView) findViewById(R.id.tv_flag_lengre03);
        flagYilou03 = (TextView) findViewById(R.id.tv_flag_yilou03);
        flagLanqiu02 = (TextView) findViewById(R.id.tv_flag_lanqiu02);
        flagLengre04 = (TextView) findViewById(R.id.tv_flag_lengre04);
        flagYilou04 = (TextView) findViewById(R.id.tv_flag_yilou04);

        hotGrid.setOnItemClickListener(this);
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    private void showPopView() {
        titlePopup = new PopMenu(DLTActivity.this, true);
        titlePopup.setLotteryType("dlt");
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayDLT, LotteryUtils.moneyArrayDLT,
                             1, findViewById(R.id.top).getMeasuredWidth() - 20, index_num, false, true);
        titlePopup.setButtonClickListener(this);
        showPopupCenter(titlePopup);
        topArrow.setImageResource(R.drawable.arrow_up_white);
        titlePopup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
            }
        });
    }

    // display the appointed way of the 3d
    private void showWay() {
        if (lotteryType == 1) {
            title.setText("大乐透 标准");
            selectInfo.setText(DLT_TIPS01);
            selectInfoLanqiu.setVisibility(View.GONE);
            shakeLockView.setVisibility(View.VISIBLE);
            shakeRela.setVisibility(View.VISIBLE);
            shakeLock = false;
            initShakeLock();// 锁图标改变
            random.setEnabled(true);// 机选按钮可用
            random.setTextColor(getResources().getColor(R.color.light_white));
            random.setVisibility(View.VISIBLE);
            dltLinearHongqiu1.setVisibility(View.VISIBLE);
            dltLinearHongqiu2.setVisibility(View.GONE);
            dltLinearLanqiu1.setVisibility(View.VISIBLE);
            dltLinearLanqiu2.setVisibility(View.GONE);
            hongqiuInf1.setLimit(DLT_HONGQIU_LIMIT);
            lanqiuInf1.setLimit(DLT_LANQIU_LIMIT);

            flagHongqiu01.setText("红球");
            flagLanqiu01.setText("蓝球");
        }
        else if (lotteryType == 2) {
            title.setText("大乐透 胆拖");
            selectInfo.setText(DLT_TIPS02);
            selectInfoLanqiu.setVisibility(View.VISIBLE);
            dltLinearHongqiu1.setVisibility(View.VISIBLE);
            dltLinearHongqiu2.setVisibility(View.VISIBLE);
            dltLinearLanqiu1.setVisibility(View.VISIBLE);
            dltLinearLanqiu2.setVisibility(View.VISIBLE);
            hongqiuInf1.setLimit(DLT_HONGQIU_QIANQU_LIMIT);
            lanqiuInf1.setLimit(DLT_LANQIU_HOUQU_DANMA + 1);
            hongqiuInf2.setLimit(DLT_HONGQIU_QIANQU_TUOMA_MAX);
            lanqiuInf2.setLimit(DLT_LANQIU_LIMIT);

            choosingCountRed1.setText("红胆：" + "0/1个");
            choosingCountRed2.setText("红拖：" + "0/2个");
            choosingCountBlue1.setText("蓝胆：" + "0/0个");
            choosingCountBlue2.setText("蓝拖：" + "0/2个");

            flagHongqiu01.setText("红胆");
            flagLanqiu01.setText("蓝胆");

            shakeLockView.setVisibility(View.GONE);
            shakeRela.setVisibility(View.GONE);
            shakeLock = true;// 胆拖时摇动选号不可用（将图标隐藏）
            shaking = false;
            handler.removeMessages(FLASHBALL);
            handler.removeMessages(SOUND);
            handler.removeMessages(VIBRATE);
            handler.removeMessages(RANDOMBALLS);
            random.setEnabled(false);// 机选按钮设为不可用
            random.setTextColor(getResources().getColor(R.color.gray));
            random.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        title.setText("大乐透 标准");
        // topArrow.setVisibility(View.GONE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        selectInfo.setText(DLT_TIPS01);
        // init red ball data
        redBallsLayout1.initData(hongqiuInf1, bigBallViews, this);
        redBallsLayout1.setFullListener(this);
        redBallsLayout1.setTouchMoveListener(this);
        redBallsLayout2.initData(hongqiuInf2, bigBallViews, this);
        redBallsLayout2.setFullListener(this);
        redBallsLayout2.setTouchMoveListener(this);
        blueBallsLayout1.initData(lanqiuInf1, bigBallViews, this);
        blueBallsLayout1.setFullListener(this);
        blueBallsLayout1.setTouchMoveListener(this);
        blueBallsLayout2.initData(lanqiuInf2, bigBallViews, this);
        blueBallsLayout2.setFullListener(this);
        blueBallsLayout2.setTouchMoveListener(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("bet_way");
            if (type != 0) {
                // 设置从其他地方过来的无法修改玩法
                termLayout.setEnabled(false);
                topArrow.setVisibility(View.INVISIBLE);
                lotteryType = type;
                if (lotteryType == 1)
                    databaseData.putString("dlt_way", "dlt_stanbdar");
                else if (lotteryType == 2)
                    databaseData.putString("dlt_way", "dlt_dantuo");
                else
                    lotteryType = 1;
                databaseData.commit();
            }
            else {
                resetLotteryType();
            }
        }
        else {
            resetLotteryType();
        }
        showWay();
        initInf();
        if (bundle != null) {
            Boolean fromHall = bundle.getBoolean("from_hall", false);
            if (!fromHall) {
                // 设置从其他地方过来的无法修改玩法
                termLayout.setEnabled(false);
                topArrow.setVisibility(View.INVISIBLE);
            }
        }
        if (hotCondintion == null)
            getAnalyseData();
        else
            analyseData(hotCondintion);
        index_num = lotteryType - 1;
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("dlt_way", "dlt_stanbdar");
        if (sdWay.equals("dlt_stanbdar")) {
            lotteryType = 1;
        }
        else if (sdWay.equals("dlt_dantuo")) {
            lotteryType = 2;
        }
    }

    protected void defaultNum(String betNum) {
        String[] blueBall = null;
        if (lotteryType == 1) {
            String[] nums = betNum.split("\\|");
            String[] redBall = nums[0].split("\\,");
            blueBall = nums[1].split(":")[0].split("\\,");
            int[] hongBalls = new int[DLT_HONGQIU_LIMIT];
            for (int i = 0; i < redBall.length; i++)
                hongBalls[i] = Integer.parseInt(redBall[i]);
            for (int i = 0; i < redBall.length; i++) {
                hongqiu1.get(hongBalls[i] - 1).setChoosed(true);
                redBallsLayout1.chooseBall(hongBalls[i] - 1);
            }

            // 蓝球
            int[] lanBalls = new int[DLT_LANQIU_LIMIT];
            for (int i = 0; i < blueBall.length; i++)
                lanBalls[i] = Integer.parseInt(blueBall[i]);
            for (int i = 0; i < blueBall.length; i++) {
                lanqiu1.get(lanBalls[i] - 1).setChoosed(true);
                blueBallsLayout1.chooseBall(lanBalls[i] - 1);
            }
        }

        if (lotteryType == 2) {
            String[] nums = betNum.split("\\|");

            String[] redballs = nums[0].split("\\$");
            String[] redBallDan = redballs[0].split("\\,");
            String[] redBallTuo = redballs[1].split("\\,");
            // 选中红胆
            int[] hongBalls1 = new int[DLT_HONGQIU_LIMIT];
            for (int i = 0; i < redBallDan.length; i++)
                hongBalls1[i] = Integer.parseInt(redBallDan[i]);
            for (int i = 0; i < redBallDan.length; i++) {
                hongqiu1.get(hongBalls1[i] - 1).setChoosed(true);
                redBallsLayout1.chooseBall(hongBalls1[i] - 1);
            }
            // 选中红拖
            int[] hongBalls2 = new int[DLT_HONGQIU_LIMIT];
            for (int i = 0; i < redBallTuo.length; i++)
                hongBalls2[i] = Integer.parseInt(redBallTuo[i]);
            for (int i = 0; i < redBallTuo.length; i++) {
                hongqiu2.get(hongBalls2[i] - 1).setChoosed(true);
                redBallsLayout2.chooseBall(hongBalls2[i] - 1);
            }

            blueBall = nums[1].split(":")[0].split("\\$");
// String[] blueBallDan = blueBall[0].split("\\,");
            String[] blueBallTuo = blueBall[1].split("\\,");

// if (blueBallDan.length >= 1) {
// if(blueBallDan != null){
// int[] blueBalls1 = new int[DLT_LANQIU_HOUQU_DANMA];
// for (int i = 0; i < blueBallDan.length; i++)
// blueBalls1[i] = Integer.parseInt(blueBallDan[i]);
// for (int i = 0; i < blueBallDan.length; i++) {
// lanqiu1.get(blueBalls1[i] - 1).setChoosed(true);
// blueBallsLayout1.chooseBall(blueBalls1[i] - 1);
// }
// }
            if (blueBall[0] != null && !blueBall[0].equals("")) {
                int blueBallDan = Integer.parseInt(blueBall[0]);
                lanqiu1.get(blueBallDan - 1).setChoosed(true);
                blueBallsLayout1.chooseBall(blueBallDan - 1);
            }
            // 蓝拖
            int[] blueBalls2 = new int[DLT_HONGQIU_LIMIT];
            for (int i = 0; i < blueBallTuo.length; i++)
                blueBalls2[i] = Integer.parseInt(blueBallTuo[i]);
            for (int i = 0; i < blueBallTuo.length; i++) {
                lanqiu2.get(blueBalls2[i] - 1).setChoosed(true);
                blueBallsLayout2.chooseBall(blueBalls2[i] - 1);
            }
        }
        onBallClickInf(-1, -1);
    }

    private String getBallsBetInf() {
        StringBuilder betBallText = new StringBuilder();
        int redLength1 = hongqiu1.size();
        int redLength2 = hongqiu2.size();
        int blueLength1 = lanqiu1.size();
        int blueLength2 = lanqiu2.size();

        if (lotteryType == 1) {
            for (int i = 0; i < redLength1; i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), redLength1));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("|");

            for (int i = 0; i < blueLength1; i++) {
                if (lanqiu1.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), blueLength1));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        else if (lotteryType == 2) {
            // 红球胆号
            for (int i = 0; i < redLength1; i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), redLength1));
                    betBallText.append(",");
                }
            }
            if (betBallText.length() > 0)
                betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("$");

            // 红球拖号
            for (int i = 0; i < redLength2; i++) {
                if (hongqiu2.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), redLength2));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("|");

            // 蓝球胆号
            if (blueLength1 != 0) {
                for (int i = 0; i < blueLength1; i++) {
                    if (lanqiu1.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), blueLength1));
                        break;
                    }
                }
            }
            betBallText.append("$");

            // 蓝球拖号
            for (int i = 0; i < blueLength2; i++) {
                if (lanqiu2.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), blueLength2));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        orgCode = betBallText.toString();
        return betBallText.toString();
    }

    private String getBallsDisplayInf() {
        StringBuilder betBallText = new StringBuilder();
        if (lotteryType == 1) {
            int redLength1 = hongqiu1.size();
            betBallText.append("<font color='red'>");
            betBallText.append("[标准]");
            if (hongqiuInf1.getCount() == 0) {
                betBallText.append("—");
            }
            else {
                for (int i = 0; i < redLength1; i++) {
                    if (hongqiu1.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), redLength1));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
            }
            betBallText.append("</font>");
            betBallText.append("<font color='blue'>|");

            int lanLength = lanqiu1.size();
            if (lanqiuInf1.getCount() == 0) {
                betBallText.append("—");
            }
            else {
                for (int i = 0; i < lanLength; i++) {
                    if (lanqiu1.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), lanLength));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
            }
            betBallText.append("</font>");
        }
        else if (lotteryType == 2) {
            int redLength1 = hongqiu1.size();
            betBallText = new StringBuilder();
            if (hongqiuInf1.getCount() != 0) {
                betBallText.append("<font color='red'>[胆](");
                redLength1 = hongqiu1.size();
                for (int i = 0; i < redLength1; i++) {
                    if (hongqiu1.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), DLT_HONGQIU_QIANQU_LIMIT));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
                betBallText.append(")</font>");
            }
            else {
                betBallText.append("<font color='red'>—</font>");
            }
            if (hongqiuInf2.getCount() != 0) {
                betBallText.append("<font color='red'>");
                int redLength2 = hongqiu2.size();
                for (int i = 0; i < redLength2; i++) {
                    if (hongqiu2.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), DLT_HONGQIU_QIANQU_TUOMA_MAX));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
                betBallText.append("</font>");
            }

            betBallText.append("<font color='blue'>");
            betBallText.append("|");
            if (lanqiuInf1.getCount() == 0) {
                betBallText.append("—");
            }
            else if (lanqiuInf1.getCount() != 0) {
                betBallText.append("<font color='red'>[胆](");
                int blueLength1 = lanqiu1.size();
                for (int i = 0; i < blueLength1; i++) {
                    if (lanqiu1.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), DLT_LANQIU_HOUQU_DANMA));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
                betBallText.append(")</font>");
            }

            if (lanqiuInf2.getCount() != 0) {
                int blueLength2 = lanqiu2.size();
                for (int i = 0; i < blueLength2; i++) {
                    if (lanqiu2.get(i).isChoosed()) {
                        betBallText.append(StringUtil.betDataTransite((i + 1), DLT_LANQIU_HOUQU_TUOMA));
                        betBallText.append(",");
                    }
                }
                betBallText.deleteCharAt(betBallText.length() - 1);
            }
            betBallText.append("</font>");
        }

        return betBallText.toString();
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 1) {
            if (hongqiuInf1.getCount() < DLT_HONGQIU_MIN) {
                inf = "请至少输入5个红球";
            }
            else if (lanqiuInf1.getCount() < DLT_LANQIU_MIN) {
                inf = "请至少输入2个蓝球";
            }
        }
        else if (lotteryType == 2) {
            if (hongqiuInf1.getCount() < 1) {
                inf = "前区胆码请至少输入1个红球";
            }
            else if (hongqiuInf2.getCount() < DLT_HONGQIU_QIANQU_TUOMA_MIN) {
                inf = "前区拖码请至少输入2个红球";
            }
            else if (lanqiuInf2.getCount() > DLT_HONGQIU_QIANQU_TUOMA_MAX) {
                inf = "前区拖码请至多输入16个红球";
            }
            else if (lanqiuInf2.getCount() < DLT_LANQIU_HOUQU_TUOMA) {
                inf = "后区拖码请至少输入2个蓝球";
            }
        }

        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    @Override
    public void randomBalls() {
        clearBalls();
        String betInf = "1注     <font color='red'>2元</font>";
        moneyInf.setText(Html.fromHtml(betInf));

        int[] randomRedNum = MathUtil.getRandomNumNotEquals(DLT_HONGQIU_MIN, DLT_HONGQIU_LENGTH);

        if (lotteryType == 1)
            for (int i = 0; i < 5; i++) {
                hongqiu1.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout1.chooseBall(randomRedNum[i]);
            }

        int[] randomBlueNum = MathUtil.getRandomNumNotEquals(DLT_LANQIU_MIN, DLT_LANQIU_LENGTH);
        for (int i = 0; i < 2; i++) {
            lanqiu1.get(randomBlueNum[i]).setChoosed(true);
            blueBallsLayout1.chooseBall(randomBlueNum[i]);
        }
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        invalidateAll();
        betMoney = 2;
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        redBallsLayout1.resetBalls();
        redBallsLayout2.resetBalls();
        blueBallsLayout1.resetBalls();
        blueBallsLayout2.resetBalls();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        if (lotteryType == 1)
            choosingInf.setText(DLT_TIPS01);
        else if (lotteryType == 2)
            choosingInf.setText(DLT_TIPS02);
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        if (lotteryType == 1) {
            if (ballType == 1) {
                ViewUtil.showTipsToast(this, "您只能选" + DLT_HONGQIU_LIMIT + "个红球");
            }
            else if (ballType == 2) {
                ViewUtil.showTipsToast(this, "您只能选" + DLT_LANQIU_LIMIT + "个蓝球");
            }
        }
        else if (lotteryType == 2) {
            if (ballType == 1) {
                ViewUtil.showTipsToast(this, "前区胆码您最多只能选" + DLT_HONGQIU_QIANQU_LIMIT + "个红球");
            }
            else if (ballType == 3) {
                ViewUtil.showTipsToast(this, "前区拖码您至多只能选" + DLT_HONGQIU_QIANQU_TUOMA_MAX + "个红球");
            }
        }
    }

    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        int hongNumber1 = hongqiuInf1.getCount();
        int hongNumber2 = hongqiuInf2.getCount();
        int lanNumber1 = lanqiuInf1.getCount();
        int lanNumber2 = lanqiuInf2.getCount();
        if (hongNumber1 == 0 && lanNumber1 == 0 && hongNumber2 == 0 && lanNumber2 == 0) {
            resetInf();
        }
        else {
            enableClearBtn();
            Boolean refreshMoney = false;
            if (lotteryType == 1) {
                if (hongNumber1 >= 5 && lanNumber1 >= 2) {
                    refreshMoney = true;
                    // 计算投注总数
                    betNumber =
                        (MathUtil.factorial(hongNumber1, 5) / MathUtil.factorial(5, 5)) *
                            (MathUtil.factorial(lanNumber1, 2) / 2);
                    betMoney = betNumber * 2 * 1;
                }
                else {
                    invalidateNum();
                }
            }
            // 大乐透胆拖
            else if (lotteryType == 2) {
                if (ballType == 1 && index >= 0) {
                    if (hongqiu2.get(index).isChoosed()) {
                        hongqiu2.get(index).setChoosed(false);
                        hongqiuInf2.setCount(hongqiuInf2.getCount() - 1);
                    }
                    redBallsLayout2.refreshAllBall();
                }
                else if (ballType == 3 && index >= 0) {
                    if (hongqiu1.get(index).isChoosed()) {
                        hongqiu1.get(index).setChoosed(false);
                        hongqiuInf1.setCount(hongqiuInf1.getCount() - 1);
                    }
                    redBallsLayout1.refreshAllBall();
                }
                else {
                    invalidateNum();
                }

                if (ballType == 2 && index >= 0) {
                    for (int i = 0; i < DLT_LANQIU_LENGTH; i++) {
                        if (i != index) {
                            if (lanqiu1.get(i).isChoosed()) {
                                lanqiu1.get(i).setChoosed(false);
                                lanqiuInf1.setCount(1);
                            }
                        }
                    }
                    blueBallsLayout1.refreshAllBall();

                    if (lanqiu2.get(index).isChoosed()) {
                        lanqiu2.get(index).setChoosed(false);
                        lanqiuInf2.setCount(lanqiuInf2.getCount() - 1);
                    }
                    blueBallsLayout2.refreshAllBall();
                }
                else if (ballType == 4 && index >= 0) {
                    if (lanqiu1.get(index).isChoosed()) {
                        lanqiu1.get(index).setChoosed(false);
                        lanqiuInf1.setCount(lanqiuInf1.getCount() - 1);
                    }
                    blueBallsLayout1.refreshAllBall();
                }
                else {
                    invalidateNum();
                }

                int danNum1 = hongqiuInf1.getCount();
                int tuoNum1 = hongqiuInf2.getCount();
                int danNum2 = lanqiuInf1.getCount();
                int tuoNum2 = lanqiuInf2.getCount();
                if (danNum1 == 0 && tuoNum1 == 0 && danNum2 == 0 && tuoNum2 == 0) {
                    resetInf();
                }
                else {
                    enableClearBtn();
                    if (danNum1 + tuoNum1 >= 6 && danNum1 >= 1 && tuoNum1 >= 2 && tuoNum2 >= 2) {
                        refreshMoney = true;
                        // 计算投注总数
                        betNumber =
                            MathUtil.combination(tuoNum1, 5 - danNum1) *
                                MathUtil.combination(tuoNum2, 2 - danNum2);
                        betMoney = betNumber * 2 * 1;
                        String betInf = getBetInf(betNumber, betMoney);
                        moneyInf.setText(Html.fromHtml(betInf));
// invalidateAll();
                    }
                    else {
                        invalidateNum();
                    }
                }
            }// here
            if (refreshMoney) {
                String betInf = getBetInf(betNumber, betMoney);
                moneyInf.setText(Html.fromHtml(betInf));
                invalidateAll();
            }
        }
        checkBet(betMoney);
    }

    private void invalidateNum() {
        betMoney = 0;
        analyseTips.setVisibility(View.VISIBLE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml("已选:" + displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
        analyseTips.setVisibility(View.VISIBLE);
        code = getBallsBetInf();
        invalidateDisplay();
    }

    @Override
    protected void showBallNum() {
        if (lotteryType == 2) {
            choosingCountRed1.setText("红胆：" + hongqiuInf1.getCount() + "/1个");
            choosingCountRed2.setText("红拖：" + hongqiuInf2.getCount() + "/2个");
            choosingCountBlue1.setText("蓝胆：" + lanqiuInf1.getCount() + "/0个");
            choosingCountBlue2.setText("蓝拖：" + lanqiuInf2.getCount() + "/2个");

            flagHongqiu01.setText("红胆");
            flagHongqiu02.setText("红拖");
            flagLanqiu01.setText("蓝胆");
            flagLanqiu02.setText("蓝拖");

        }
        else {
            choosingCountRed1.setText("红球：" + hongqiuInf1.getCount() + "/5个");
            choosingCountBlue1.setText("蓝球：" + lanqiuInf1.getCount() + "/2个");

            flagHongqiu01.setText("红球");
            flagLanqiu01.setText("蓝球");
        }

    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "大乐透游戏规则");
        bundel.putString("lottery_help", "help_new/dlt.html");
        intent.putExtras(bundel);
        intent.setClass(DLTActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goCalculator() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "大乐透奖金计算器");
        bundel.putString("lottery_help", "dlt_calculator/dlt_dublicate_calculator.html");
        intent.putExtras(bundel);
        intent.setClass(DLTActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "大乐透走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=dlt&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(DLTActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopView();
        }
        else if (v.getId() == R.id.layout_lottery_calculator) {
            popMenu.dismiss();
            submitStatisticClickAwardCount();
            goCalculator();
        }
        else if (v.getId() == R.id.img_help_info_bg) {
            img_help_info_bg.setVisibility(View.GONE);
            ifShowImgHelp = false;
            databaseData.putBoolean("if_show_img_help", ifShowImgHelp);
            databaseData.commit();
        }
        else if (v.getId() == R.id.layout_lottery_chart) {
            goZouShiTu();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout1.refreshAllBallInf(showHotNum, showOmitNum);
        redBallsLayout2.refreshAllBallInf(showHotNum, showOmitNum);
        blueBallsLayout1.refreshAllBallInf(showHotNum, showOmitNum);
        blueBallsLayout2.refreshAllBallInf(showHotNum, showOmitNum);

        // 小标签的显示控制
        if (showHotNum) {
            flagLengre01.setVisibility(View.VISIBLE);
            flagLengre02.setVisibility(View.VISIBLE);
            flagLengre03.setVisibility(View.VISIBLE);
            flagLengre04.setVisibility(View.VISIBLE);
        }
        else {
            flagLengre01.setVisibility(View.GONE);
            flagLengre02.setVisibility(View.GONE);
            flagLengre03.setVisibility(View.GONE);
            flagLengre04.setVisibility(View.GONE);
        }
        if (showOmitNum) {
            flagYilou01.setVisibility(View.VISIBLE);
            flagYilou02.setVisibility(View.VISIBLE);
            flagYilou03.setVisibility(View.VISIBLE);
            flagYilou04.setVisibility(View.VISIBLE);
        }
        else {
            flagYilou01.setVisibility(View.GONE);
            flagYilou02.setVisibility(View.GONE);
            flagYilou03.setVisibility(View.GONE);
            flagYilou04.setVisibility(View.GONE);
        }
    }

    @Override
    protected void analyseData(String json) {
        String inf = null;
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            // get the status of the http data
            String status = ja.getStatus(json);
            if (status.equals("200")) {
                String analyseData = ja.getData(json, "response_data");
                if (!analyseData.equals("{}")) {
                    hotCondintion = json;
                    String basicNum = ja.getData(analyseData, "red_distrb");
                    String specialNum = ja.getData(analyseData, "blue_distrb");
                    redBallTips = StringUtil.spliteString(basicNum, ",");
                    String[] redballAnalyse = new String[redBallTips.length];
                    redballAnalyse = StringUtil.spliteString(basicNum, ",");
                    blueBallTips = StringUtil.spliteString(specialNum, ",");
                    String[] blueballAnalyse = new String[blueBallTips.length];
                    blueballAnalyse = StringUtil.spliteString(specialNum, ",");
                    int redLength = redBallTips.length;
                    int blueLength = blueBallTips.length;
                    int[] red = new int[redLength];
                    for (int i = 0; i < redLength; i++) {
                        red[i] = Integer.valueOf(redBallTips[i]);
                    }
                    int redMax = MathUtil.getMax(red);
                    int redMin = MathUtil.getMin(red);
                    for (int i = 0; i < redLength; i++) {
                        if (red[i] == redMin) {
                            redballAnalyse[i] = COLD_NUM_FONT_ANALASE + red[i] + "</font>";
                            redBallTips[i] =
                                SEARCHNUM[searchType] + "期<br>出" + COLD_NUM_FONT + red[i] + "</font>次";
                        }
                        else if (red[i] == redMax) {
                            redballAnalyse[i] = HOT_NUM_FONT_ANALASE + red[i] + "</font>";
                            redBallTips[i] =
                                SEARCHNUM[searchType] + "期<br>出" + HOT_NUM_FONT + red[i] + "</font>次";
                        }
                        else {
                            redballAnalyse[i] = red[i] + "</font>";
                            redBallTips[i] = SEARCHNUM[searchType] + "期<br>出" + red[i] + "次";
                        }
                        hongqiu1.get(i).setBallsInf(redBallTips[i]);
                        hongqiu1.get(i).setBallAnalase(redballAnalyse[i]);
                        hongqiu2.get(i).setBallsInf(redBallTips[i]);
                        hongqiu2.get(i).setBallAnalase(redballAnalyse[i]);
                    }
                    int[] blue = new int[blueLength];
                    for (int i = 0; i < blueLength; i++) {
                        blue[i] = Integer.valueOf(blueBallTips[i]);
                    }
                    int blueMax = MathUtil.getMax(blue);
                    int blueMin = MathUtil.getMin(blue);
                    for (int i = 0; i < blueLength; i++) {
                        if (blue[i] == blueMin) {
                            blueballAnalyse[i] = COLD_NUM_FONT_ANALASE + blue[i] + "</font>";
                            blueBallTips[i] =
                                SEARCHNUM[searchType] + "期<br>出" + COLD_NUM_FONT + blue[i] + "</font>次";
                        }
                        else if (blue[i] == blueMax) {
                            blueballAnalyse[i] = HOT_NUM_FONT_ANALASE + blue[i] + "</font>";
                            blueBallTips[i] =
                                SEARCHNUM[searchType] + "期<br>出" + HOT_NUM_FONT + blue[i] + "</font>次";
                        }
                        else {
                            blueballAnalyse[i] = blue[i] + "</font>";
                            blueBallTips[i] = SEARCHNUM[searchType] + "期<br>出" + blue[i] + "次";
                        }
                        lanqiu1.get(i).setBallAnalase(blueballAnalyse[i]);
                        lanqiu1.get(i).setBallsInf(blueBallTips[i]);
                        lanqiu2.get(i).setBallAnalase(blueballAnalyse[i]);
                        lanqiu2.get(i).setBallsInf(blueBallTips[i]);
                    }
                    refreshHotNumShow();
                    OmitHistoryTask history = new OmitHistoryTask();
                    history.execute(kind);
                }
                else {
                    inf = "冷热门号码数据获取失败";
                }
            }
            else {
                inf = "冷热门号码数据获取失败";
            }
        }
        else {
            inf = "冷热门号码数据获取失败";
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
        }
    }

    @Override
    protected void omitData(String json) {
        String inf = null;
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            // get the status of the http data
            String status = ja.getStatus(json);
            if (status.equals("200")) {
                String analyseData = ja.getData(json, "response_data");
                if (!analyseData.equals("{}")) {
                    String basicNum = ja.getData(analyseData, "missing");
                    String[] num = StringUtil.spliteString(basicNum, "\\|");
                    String[] redballOmit = StringUtil.spliteString(num[0], ",");
                    int redLength = redballOmit.length;
                    int[] red = new int[redLength];
                    for (int i = 0; i < redLength; i++) {
                        red[i] = Integer.valueOf(redballOmit[i]);
                    }
                    int redMax = MathUtil.getMax(red);
                    int redMin = MathUtil.getMin(red);
                    for (int i = 0; i < redLength; i++) {
                        if (red[i] == redMin) {
                            redballOmit[i] = COLD_NUM_FONT_ANALASE + red[i] + "</font>";
                            redBallTips[i] += "<br>漏" + COLD_NUM_FONT + red[i] + "</font>期";
                        }
                        else if (red[i] == redMax) {
                            redballOmit[i] = HOT_NUM_FONT_ANALASE + red[i] + "</font>";
                            redBallTips[i] += "<br>漏" + HOT_NUM_FONT + red[i] + "</font>期";
                        }
                        else {
                            redballOmit[i] = red[i] + "</font>";
                            redBallTips[i] += "<br>漏" + red[i] + "期";
                        }
                        hongqiu1.get(i).setBallsInf(redBallTips[i]);
                        hongqiu1.get(i).setBallOmit(redballOmit[i]);
                        hongqiu2.get(i).setBallsInf(redBallTips[i]);
                        hongqiu2.get(i).setBallOmit(redballOmit[i]);
                    }
                    String[] blueballOmit = StringUtil.spliteString(num[1], ",");
                    int blueLength = blueballOmit.length;
                    int[] blue = new int[blueLength];
                    for (int i = 0; i < blueLength; i++) {
                        blue[i] = Integer.valueOf(blueballOmit[i]);
                    }
                    int blueMax = MathUtil.getMax(blue);
                    int blueMin = MathUtil.getMin(blue);
                    for (int i = 0; i < blueLength; i++) {
                        if (blue[i] == blueMin) {
                            blueballOmit[i] = COLD_NUM_FONT_ANALASE + blue[i] + "</font>";
                            blueBallTips[i] += "<br>漏" + COLD_NUM_FONT + blue[i] + "</font>期";
                        }
                        else if (blue[i] == blueMax) {
                            blueballOmit[i] = HOT_NUM_FONT_ANALASE + blue[i] + "</font>";
                            blueBallTips[i] += "<br>漏" + HOT_NUM_FONT + blue[i] + "</font>期";
                        }
                        else {
                            blueballOmit[i] = blue[i] + "</font>";
                            blueBallTips[i] += "<br>漏" + blue[i] + "期";
                        }
                        lanqiu1.get(i).setBallOmit(blueballOmit[i]);
                        lanqiu1.get(i).setBallsInf(blueBallTips[i]);
                        lanqiu2.get(i).setBallOmit(blueballOmit[i]);
                        lanqiu2.get(i).setBallsInf(blueBallTips[i]);
                    }
                    refreshHotNumShow();
                }
            }
        }
    }

    @Override
    public String getQ_code() {
        dltNum01 = 0;
        dltNum02 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu1.size(); i++) {
            if (hongqiu1.get(i).isChoosed()) {
                ballText.append(StringUtil.betDataTransite(i + 1));
                ballText.append(",");
                dltNum01 = dltNum01 + 1;
            }
        }

        if (lotteryType == 2) {
            for (int i = 0; i < hongqiu2.size(); i++) {
                if (hongqiu2.get(i).isChoosed()) {
                    ballText.append(StringUtil.betDataTransite(i + 1));
                    ballText.append(",");
                    dltNum01 = dltNum01 + 1;
                }
            }
        }
        // 去掉字符串末尾的"，"
        if (ballText.length() > 0) {
            ballText.deleteCharAt(ballText.length() - 1);
        }

        ballText.append("|");
        for (int i = 0; i < lanqiu1.size(); i++) {
            if (lanqiu1.get(i).isChoosed()) {
// int speialNum = Integer.valueOf(StringUtil.betDataTransite(i + 1));
// if (speialNum >= 10) {
// isSingle = false;
// }
// else if (speialNum < 10) {
// isSingle = true;
// }
                ballText.append(StringUtil.betDataTransite(i + 1));
                ballText.append(",");
                dltNum02 = dltNum02 + 1;
            }
        }
        if (lotteryType == 2) {
            for (int i = 0; i < lanqiu2.size(); i++) {
                if (lanqiu2.get(i).isChoosed()) {
                    ballText.append(StringUtil.betDataTransite(i + 1));
                    ballText.append(",");
                    dltNum02 = dltNum02 + 1;
                }
            }
        }

        if (ballText.length() > 0) {
            ballText.deleteCharAt(ballText.length() - 1);
        }
        return ballText.toString();
    }

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals("")) {
            ViewUtil.showTipsToast(this, "请输入一个号码");
        }
        else {
            if (dltNum01 > 5) {
                ViewUtil.showTipsToast(this, "至多允许输入5个红球");
            }
            else if (dltNum02 > 2) {
                ViewUtil.showTipsToast(this, "至多允许输入2个蓝球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                requestCode = setMatchCode();
                setMatchCode();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", term_num);
                bundle.putString("kind", "dlt");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(DLTActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    int dltNum01;
    int dltNum02;

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        titlePopup.dismiss();
        databaseData.putString("dlt_way", LotteryUtils.dltWay[kind]);
        databaseData.commit();
        clearBalls();
        lotteryType = kind + 1;
        index_num = kind;
        title.setText("大乐透 " + LotteryUtils.textArrayDLT[kind]);
        showWay();
        showBallNum();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
        popMenu.dismiss();
        TextView t_v = (TextView) parent.getChildAt(searchType).findViewById(R.id.unite_grid_view_item_click);
        t_v.setTextColor(getResources().getColor(R.color.dark_purple));
        t_v.setBackgroundResource(R.drawable.bet_popup_item_normal);
        TextView tv = (TextView) parent.getChildAt(position).findViewById(R.id.unite_grid_view_item_click);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        searchType = position;
        adapter = new CommissionAdapter(DLTActivity.this, list, searchType,"bet_tools");
        hotGrid.setAdapter(adapter);
        databaseData.putInt("hot_way", searchType);
        getAnalyseData();
    }
}
