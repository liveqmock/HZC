package com.haozan.caipiao.activity.bet.hnklsf;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
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

public class HNKLSFActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private String[] lotteryTypeArray = {"选一数投", "选一红投", "任选二", "选二连直", "选二连组", "任选三", "选三前直", "选三前组", "任选四",
            "任选五"};
    private String[] lotteryTypeTipsArray = {"选一位，猜中第1个开奖号，奖金24元", "选一位，猜中第1个开奖号，奖金8元", "猜中开奖号的任2个，奖金8元",
            "按开奖号顺序按位猜中2个号码，奖金62元", "按开奖号顺序猜中2个号码，奖金31元", "猜中开奖号的任3个，奖金24元", "按开奖号顺序按位猜中3个号码，奖金8千",
            "按开奖号顺序猜中3个号码，奖金一千三", "猜中开奖号的任4个，奖金80元", "猜中开奖号的任5个，奖320元"};

    public static final int HNKLSF_HONGQIU_START = 1;
    public static final int HNKLSF_HONGQIU_LENGTH01 = 18;
    public static final int HNKLSF_HONGQIU_LENGTH02 = 2;
    public static final int HNKLSF_HONGQIU_LENGTH03 = 20;
    public static final int HNKLSF_HONGQIU_LIMIT01 = 10;
    public static final int HNKLSF_HONGQIU_LIMIT02 = 10;
    public static final int HNKLSF_HONGQIU_LIMIT03 = 14;
    public static final int HNKLSF_HONGQIU_LIMIT04 = 18;
    public static final int HNKLSF_HONGQIU_MIN01 = 1;
    public static final int HNKLSF_HONGQIU_MIN02 = 2;
    public static final int HNKLSF_HONGQIU_MIN03 = 3;
    public static final int HNKLSF_HONGQIU_MIN04 = 4;
    public static final int HNKLSF_HONGQIU_MIN05 = 5;

    private String[] lotteryBetTypeArray = {"101", "102", "20", "22", "21", "30", "34", "33", "40", "50"};
    private int[] numLengthArray = {HNKLSF_HONGQIU_LENGTH01, HNKLSF_HONGQIU_LENGTH02,
            HNKLSF_HONGQIU_LENGTH03, HNKLSF_HONGQIU_LENGTH03, HNKLSF_HONGQIU_LENGTH03,
            HNKLSF_HONGQIU_LENGTH03, HNKLSF_HONGQIU_LENGTH03, HNKLSF_HONGQIU_LENGTH03,
            HNKLSF_HONGQIU_LENGTH03, HNKLSF_HONGQIU_LENGTH03};
    private static final int[] reword = {24, 8, 8, 62, 31, 24, 8000, 1300, 80, 320};
    private static String hotCondintion = null;
    private int lotteryType = 1;
    private int index = 1;
    private RelativeLayout termLayout;
    // bai
    private NewBetBallsLayout redBallsLayout01;
    private LinearLayout klsf_bet_field_bg01;
// private TextView choosingCountHundred;
    private BetBallsData hongqiuInf01;
    private ArrayList<BetBall> hongqiu01;
    // shi
    private NewBetBallsLayout redBallsLayout02;
    private LinearLayout klsf_bet_field_bg02;
// private TextView choosingCountTen;
    private BetBallsData hongqiuInf02;
    private ArrayList<BetBall> hongqiu02;
    // ge
    private NewBetBallsLayout redBallsLayout03;
    private LinearLayout klsf_bet_field_bg03;
// private TextView choosingCountGe;
    private BetBallsData hongqiuInf03;
    private ArrayList<BetBall> hongqiu03;
    private PopMenu titlePopup;
    private int index_num = 0;

    private TextView lotteryIntroduce;
    private boolean ifLotteryIntroduceShown = false;

    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03;
// private RelativeLayout topBgLinear;
    private TextView selectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.klsf);
        getLotteryType();
        initData(HNKLSF_HONGQIU_LENGTH01);
        setupViews();
        initSubViews();
        init();
    }

    private void initData(int length) {
        databaseData = getSharedPreferences("user", 0).edit();
        resetLotteryType();
        if (lotteryType == 1)
            length = HNKLSF_HONGQIU_LENGTH01;
        else if (lotteryType == 2)
            length = HNKLSF_HONGQIU_LENGTH02;
        else
            length = HNKLSF_HONGQIU_LENGTH03;
        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        for (int i = 1; i < length + 1; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            if (lotteryType == 2)
                ball.setContent(String.valueOf(18 + i));
            else
                ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setBallType(1);
        hongqiuInf01.setLimit(getKLSFLimitNum(lotteryType));
        // init red section two
        hongqiuInf02 = new BetBallsData();
        hongqiu02 = new ArrayList<BetBall>();
        for (int i = 1; i < length + 1; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu02.add(ball);
        }
        hongqiuInf02.setBetBalls(hongqiu02);
        hongqiuInf02.setCount(0);
        hongqiuInf02.setColor("red");
        hongqiuInf02.setBallType(2);
        hongqiuInf02.setLimit(getKLSFLimitNum(lotteryType));
        // init red section three
        hongqiuInf03 = new BetBallsData();
        hongqiu03 = new ArrayList<BetBall>();
        for (int i = 1; i < length + 1; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu03.add(ball);
        }
        hongqiuInf03.setBetBalls(hongqiu03);
        hongqiuInf03.setCount(0);
        hongqiuInf03.setColor("red");
        hongqiuInf03.setBallType(3);
        hongqiuInf03.setLimit(getKLSFLimitNum(lotteryType));
    }

    protected void setupViews() {
        super.setupViews();

        // 隐藏分析工具
// findViewById(R.id.analyse_tips_rala).setVisibility(View.GONE);
        normalToolsLayout.setVisibility(View.GONE);
        numAnalyse.setVisibility(View.GONE);

        lotteryIntroduce = (TextView) this.findViewById(R.id.lottery_introdution);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        // bai
// choosingCountHundred = (TextView) this.findViewById(R.id.klsf_hongqiu01_text);
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.klsf_hongqiu_balls01);
        klsf_bet_field_bg01 = (LinearLayout) this.findViewById(R.id.klsf_hongqiu_balls01_linear);
        // shi
// choosingCountTen = (TextView) this.findViewById(R.id.klsf_hongqiu02_text);
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.klsf_hongqiu_balls02);
        klsf_bet_field_bg02 = (LinearLayout) this.findViewById(R.id.klsf_hongqiu_balls02_linear);
        // ge
// choosingCountGe = (TextView) this.findViewById(R.id.klsf_hongqiu03_text);
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.klsf_hongqiu_balls03);
        klsf_bet_field_bg03 = (LinearLayout) this.findViewById(R.id.klsf_hongqiu_balls03_linear);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
// topBgLinear= (RelativeLayout)findViewById(R.id.top_bg_linear);
        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagHongqiu03 = (TextView) findViewById(R.id.tv_flag_hongqiu03);
        selectInfo = (TextView) findViewById(R.id.select_info);
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initLotteryIntroduce();
// getLotteryType();
        drawLotteryBall(HNKLSF_HONGQIU_LENGTH01);
        showWay();
        showChoosingInf();
        initInf();
        if (hotCondintion == null)
            getAnalyseData();
        else
            analyseData(hotCondintion);
        index_num = lotteryType - 1;
        luckyBallSelect.setClickable(false);
        luckyBallSelect.setVisibility(View.INVISIBLE);
        loteryMethodType = 1;
    }

    private void getLotteryType() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lotteryType = bundle.getInt("bet_way");
            if (lotteryType == 1)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_zero");
            else if (lotteryType == 2)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_one");
            else if (lotteryType == 3)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_two");
            else if (lotteryType == 4)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_three");
            else if (lotteryType == 5)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_four");
            else if (lotteryType == 6)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_five");
            else if (lotteryType == 7)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_six");
            else if (lotteryType == 8)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_seven");
            else if (lotteryType == 9)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_eight");
            else if (lotteryType == 10)
                databaseData.putString("hnklsf_way", "hnklsf_lucky_nine");
            else
                resetLotteryType();
            databaseData.commit();
        }
        else {
            resetLotteryType();
        }
    }

    private void drawLotteryBall(int length) {
        if (lotteryType == 1)
            length = HNKLSF_HONGQIU_LENGTH01;
        else if (lotteryType == 2)
            length = HNKLSF_HONGQIU_LENGTH02;
        else
            length = HNKLSF_HONGQIU_LENGTH03;
        // bai
        redBallsLayout01.initData(hongqiuInf01, bigBallViews, this);
        redBallsLayout01.drawBalls(length, "red");
        redBallsLayout01.setFullListener(this);
        redBallsLayout01.setTouchMoveListener(this);
        // shi
        redBallsLayout02.initData(hongqiuInf02, bigBallViews, this);
        redBallsLayout02.drawBalls(length, "red");
        redBallsLayout02.setFullListener(this);
        redBallsLayout02.setTouchMoveListener(this);
        // ge
        redBallsLayout03.initData(hongqiuInf03, bigBallViews, this);
        redBallsLayout03.drawBalls(length, "red");
        redBallsLayout03.setFullListener(this);
        redBallsLayout03.setTouchMoveListener(this);
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("hnklsf_way", "hnklsf_lucky_zero");
        if (sdWay.equals("hnklsf_lucky_zero")) {
            lotteryType = 1;
            index = 1;
        }
        else if (sdWay.equals("hnklsf_lucky_one")) {
            lotteryType = 2;
            index = 2;
        }
        else if (sdWay.equals("hnklsf_lucky_two")) {
            lotteryType = 3;
            index = 3;
        }
        else if (sdWay.equals("hnklsf_lucky_three")) {
            lotteryType = 4;
            index = 4;
        }
        else if (sdWay.equals("hnklsf_lucky_four")) {
            lotteryType = 5;
            index = 5;
        }
        else if (sdWay.equals("hnklsf_lucky_five")) {
            lotteryType = 6;
            index = 6;
        }
        else if (sdWay.equals("hnklsf_lucky_six")) {
            lotteryType = 7;
            index = 7;
        }
        else if (sdWay.equals("hnklsf_lucky_seven")) {
            lotteryType = 8;
            index = 8;
        }
        else if (sdWay.equals("hnklsf_lucky_eight")) {
            lotteryType = 9;
            index = 9;
        }
        else if (sdWay.equals("hnklsf_lucky_nine")) {
            lotteryType = 10;
            index = 10;
        }
    }

    private void showWay() {
        initLotteryIntroduce();
        redBallsLayout01.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);
        klsf_bet_field_bg01.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountGe.setVisibility(View.GONE);
        klsf_bet_field_bg02.setVisibility(View.GONE);
        klsf_bet_field_bg03.setVisibility(View.GONE);
        if (lotteryType == 1) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 2) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 3) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 4) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountGe.setVisibility(View.GONE);
            klsf_bet_field_bg02.setVisibility(View.VISIBLE);
            klsf_bet_field_bg03.setVisibility(View.GONE);
        }
        else if (lotteryType == 5) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 6) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 7) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountGe.setVisibility(View.VISIBLE);
            klsf_bet_field_bg02.setVisibility(View.VISIBLE);
            klsf_bet_field_bg03.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 8) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 9) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
        else if (lotteryType == 10) {
            selectInfo.setText(lotteryTypeTipsArray[lotteryType - 1]);
            title.setText(lotteryTypeArray[lotteryType - 1]);
        }
    }

    protected void showChoosingInf() {
        if (lotteryType == 4) {
// choosingCountHundred.setText("前位：" + hongqiuInf01.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
// choosingCountTen.setText("后位：" + hongqiuInf02.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
            flagHongqiu01.setText("前位");
            flagHongqiu02.setText("后位");

        }
        else if (lotteryType == 7) {
// choosingCountHundred.setText("第一位：" + hongqiuInf01.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
// choosingCountTen.setText("第二位：" + hongqiuInf02.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
// choosingCountGe.setText("第三位：" + hongqiuInf03.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
            flagHongqiu01.setText("第一位");
            flagHongqiu02.setText("第二位");
            flagHongqiu03.setText("第三位");
        }
        else {
// choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" +
// getKLSFSingleMinNum(lotteryType) + "个");
            flagHongqiu01.setText("红球");
        }
    }

    @Override
    protected void enableBetBtn() {
        super.enableBetBtn();
        if (ifLotteryIntroduceShown == false) {
            appearAnimation(lotteryIntroduce);
            ifLotteryIntroduceShown = true;
        }
    }

    @Override
    protected void disableBetBtn() {
        super.disableBetBtn();
        if (ifLotteryIntroduceShown == true) {
            disappearAnimation(lotteryIntroduce);
            ifLotteryIntroduceShown = false;
        }
    }

    @Override
    protected void analyseData(String json) {

    }

    @Override
    public String getQ_code() {
        return null;
    }

    @Override
    protected void searchLuckyNum() {

    }

    @Override
    public void setKind() {
        this.kind = "hnklsf";
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopupViews();
        }
        else if (v.getId() == R.id.bet_clear_button) {
            initLotteryIntroduce();
            disableBetBtn();
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

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "快乐十分走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=hnklsf&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(HNKLSFActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "快乐十分游戏规则");
        bundel.putString("lottery_help", "help_new/hnkl10.html");
        intent.putExtras(bundel);
        intent.setClass(HNKLSFActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    private void setAnalyseTipsVisibility(int hongCount01) {
        if (hongCount01 > 1) {
            analyseTips.setVisibility(View.GONE);
        }
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
// choosingInf.setText(Html.fromHtml(displayCode));
        showChoosingInf();
    }

    private String getBallsDisplayInf() {
        return getBallsDisplayFirstKindInf();
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[" + lotteryTypeArray[lotteryType - 1] + "]");
        int hongLength01 = hongqiu01.size();
        int hongLength02 = hongqiu02.size();
        int hongLength03 = hongqiu03.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed())
                if (lotteryType == 2)
                    betBallText.append(i + 19 + ",");
                else
                    betBallText.append(StringUtil.betDataTransite((i + 1)) + ",");
        }
        if (lotteryType == 4 || lotteryType == 7) {
            betBallText.delete(betBallText.length() - 1, betBallText.length());
            betBallText.append("|");
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed())
                    betBallText.append(StringUtil.betDataTransite(i + 1) + ",");
            }
        }
        if (lotteryType == 7) {
            betBallText.delete(betBallText.length() - 1, betBallText.length());
            betBallText.append("|");
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(StringUtil.betDataTransite(i + 1) + ",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private void showPopupViews() {
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        titlePopup = new PopMenu(HNKLSFActivity.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayHNKLSF,
                             LotteryUtils.moneyArrayHNKLSF, 1,
                             findViewById(R.id.top).getMeasuredWidth() - 20, index_num, true, true);
        titlePopup.setButtonClickListener(this);
        topArrow.setImageResource(R.drawable.arrow_up_white);
        showPopupCenter(titlePopup);
        titlePopup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
            }
        });
    }

    private int getKLSFMinNum(int lotteryType) {
        if (lotteryType == 1 || lotteryType == 2)
            return HNKLSF_HONGQIU_MIN01;
        else if (lotteryType == 3 || lotteryType == 4 || lotteryType == 5)
            return HNKLSF_HONGQIU_MIN02;
        else if (lotteryType == 6 || lotteryType == 7 || lotteryType == 8)
            return HNKLSF_HONGQIU_MIN03;
        else if (lotteryType == 9)
            return HNKLSF_HONGQIU_MIN04;
        else
            return HNKLSF_HONGQIU_MIN05;
    }

    private int getKLSFSingleMinNum(int lotteryType) {
        if (lotteryType == 1 || lotteryType == 2 || lotteryType == 4 || lotteryType == 7)
            return HNKLSF_HONGQIU_MIN01;
        else if (lotteryType == 3 || lotteryType == 5)
            return HNKLSF_HONGQIU_MIN02;
        else if (lotteryType == 6 || lotteryType == 8)
            return HNKLSF_HONGQIU_MIN03;
        else if (lotteryType == 9)
            return HNKLSF_HONGQIU_MIN04;
        else
            return HNKLSF_HONGQIU_MIN05;
    }

    private int getKLSFLimitNum(int lotteryType) {
        if (lotteryType == 1)
            return HNKLSF_HONGQIU_LENGTH01;
        else if (lotteryType == 2)
            return HNKLSF_HONGQIU_LENGTH02;
        else
            return HNKLSF_HONGQIU_LENGTH03;
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        if (lotteryType == 7) {
            String[] nums = lotteryMode[0].split("\\|");
            String[] baiNum = nums[0].split("\\,");
            int firstLength = baiNum.length;
            for (int i = 0; i < firstLength; i++) {
                int num = Integer.valueOf(baiNum[i]);
                hongqiu01.get(num - 1).setChoosed(true);
                redBallsLayout01.chooseBall(num - 1);
            }
            String[] shiNum = nums[1].split("\\,");
            int secondLength = shiNum.length;
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(shiNum[i]);
                hongqiu02.get(num - 1).setChoosed(true);
                redBallsLayout02.chooseBall(num - 1);
            }
            String[] geNum = nums[2].split("\\,");
            int thirdLength = geNum.length;
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(geNum[i]);
                hongqiu03.get(num - 1).setChoosed(true);
                redBallsLayout03.chooseBall(num - 1);
            }
        }
        else if (lotteryType == 4) {
            String[] nums = lotteryMode[0].split("\\|");
            String[] baiNum = nums[0].split("\\,");
            int length = baiNum.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(baiNum[i]);
                hongqiu01.get(num - 1).setChoosed(true);
                redBallsLayout01.chooseBall(num - 1);
            }
            String[] shiNum = nums[1].split("\\,");
            int secondLength = shiNum.length;
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(shiNum[i]);
                hongqiu02.get(num - 1).setChoosed(true);
                redBallsLayout02.chooseBall(num - 1);
            }
        }
        else if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 5 ||
            lotteryType == 6 || lotteryType == 8 || lotteryType == 9 || lotteryType == 10) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                if (lotteryType == 2) {
                    hongqiu01.get(num - 19).setChoosed(true);
                    redBallsLayout01.chooseBall(num - 19);
                }
                else {
                    hongqiu01.get(num - 1).setChoosed(true);
                    redBallsLayout01.chooseBall(num - 1);
                }
            }
        }
        onBallClickInf(-1, -1);
    }

    @Override
    public void randomBalls() {
        int[] randomRedNum01;
        int[] randomRedNum02 = null;
        int[] randomRedNum03 = null;
        boolean isSecond = true;
        boolean isThird = true;
        clearBalls();
        String betInf = "1注     <font color='red'>2元</font>";
        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + getReword(lotteryType) +
            "</font>元,您将盈利<font color=\"#CD2626\">" + (getReword(lotteryType) - 2) + "</font>元"));
        moneyInf.setText(Html.fromHtml(betInf));
        randomRedNum01 =
            MathUtil.getRandomNumNotEquals(getKLSFSingleMinNum(lotteryType), getKLSFLimitNum(lotteryType));
        if (lotteryType == 4 || lotteryType == 7) {
            while (isSecond) {
                randomRedNum02 =
                    MathUtil.getRandomNumNotEquals(getKLSFSingleMinNum(lotteryType),
                                                   getKLSFLimitNum(lotteryType));
                if (randomRedNum01[0] != randomRedNum02[0])
                    isSecond = false;
            }
        }
        if (lotteryType == 7) {
            while (isThird) {
                randomRedNum03 =
                    MathUtil.getRandomNumNotEquals(getKLSFSingleMinNum(lotteryType),
                                                   getKLSFLimitNum(lotteryType));
                if (randomRedNum01[0] != randomRedNum03[0]) {
                    if (randomRedNum02[0] != randomRedNum03[0])
                        isThird = false;
                }
            }
        }
        for (int i = 0; i < getKLSFSingleMinNum(lotteryType); i++) {
            hongqiu01.get(randomRedNum01[i]).setChoosed(true);
            redBallsLayout01.chooseBall(randomRedNum01[i]);
        }
        if (lotteryType == 4 || lotteryType == 7) {
            for (int i = 0; i < getKLSFSingleMinNum(lotteryType); i++) {
                hongqiu02.get(randomRedNum02[i]).setChoosed(true);
                redBallsLayout02.chooseBall(randomRedNum02[i]);
            }
        }
        if (lotteryType == 7) {
            for (int i = 0; i < getKLSFSingleMinNum(lotteryType); i++) {
                hongqiu03.get(randomRedNum03[i]).setChoosed(true);
                redBallsLayout03.chooseBall(randomRedNum03[i]);
            }
        }
    }

    private int getReword(int lotteryType) {
        return reword[lotteryType - 1];
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        betMoney = 2;
        invalidateAll();
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        n = 0;
        if (lotteryType == 0 || lotteryType == 1 || lotteryType == 2 || lotteryType == 3 ||
            lotteryType == 5 || lotteryType == 6 || lotteryType == 8 || lotteryType == 9 || lotteryType == 10) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 4) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
        }
        else if (lotteryType == 7) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
        }
        resetInf();
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 1) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN01)
                inf = "请至少输入1个号码";
        }
        else if (lotteryType == 2) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN01)
                inf = "请至少输入1个号码";
        }
        else if (lotteryType == 3) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN02)
                inf = "请至少输入2个号码";
        }
        else if (lotteryType == 4) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN01 &&
                hongqiuInf02.getCount() < HNKLSF_HONGQIU_MIN01)
                inf = "每行请至少输入2个号码";
        }
        else if (lotteryType == 5) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN02)
                inf = "请至少输入2个号码";
        }
        else if (lotteryType == 6) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN03)
                inf = "请至少输入5个号码";
        }
        else if (lotteryType == 7) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN01 &&
                hongqiuInf02.getCount() < HNKLSF_HONGQIU_MIN01 &&
                hongqiuInf03.getCount() < HNKLSF_HONGQIU_MIN01)
                inf = "每行请至少输入1个号码";
        }
        else if (lotteryType == 8) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN03)
                inf = "请至少输入3个号码";
        }
        else if (lotteryType == 9) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN04)
                inf = "请至少输入5个号码";
        }
        else if (lotteryType == 10) {
            if (hongqiuInf01.getCount() < HNKLSF_HONGQIU_MIN05)
                inf = "请至少输入5个号码";
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    private String getBallsBetInf() {
        return getBallsBetKindInf();
    }

    private String getBallsBetKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        int hongLength02 = hongqiu02.size();
        int hongLength03 = hongqiu03.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                if (lotteryType == 2)
                    betBallText.append(StringUtil.betDataTransite((i + 19)));
                else
                    betBallText.append(StringUtil.betDataTransite((i + 1)));
                betBallText.append(",");
            }
        }
        if (lotteryType == 4 || lotteryType == 7) {
            betBallText.delete(betBallText.length() - 1, betBallText.length());
            betBallText.append("|");
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1)));
                    betBallText.append(",");
                }
            }
        }
        if (lotteryType == 7) {
            betBallText.delete(betBallText.length() - 1, betBallText.length());
            betBallText.append("|");
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1)));
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney);
        if (lotteryType == 1)
            betBallText.append(":" + lotteryBetTypeArray[lotteryType - 1] + ":");
        else if (lotteryType == 2)
            betBallText.append(":" + lotteryBetTypeArray[lotteryType - 1] + ":");
        else {
            if (checkIsDuplex() == false)
                betBallText.append(":" + lotteryBetTypeArray[lotteryType - 1] + "1:");
            else
                betBallText.append(":" + lotteryBetTypeArray[lotteryType - 1] + "2:");
        }
        return betBallText.toString();
    }

    private boolean checkIsDuplex() {
        boolean isDuplex = false;
        if (hongqiuInf01.getCount() > getKLSFSingleMinNum(lotteryType))
            isDuplex = true;
        if (lotteryType == 4 || lotteryType == 7) {
            if (hongqiuInf02.getCount() > getKLSFSingleMinNum(lotteryType))
                isDuplex = true;
        }
        if (lotteryType == 7) {
            if (hongqiuInf03.getCount() > getKLSFSingleMinNum(lotteryType))
                isDuplex = true;
        }
        return isDuplex;
    }

    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }

    @Override
    public void onBallClickFull(int kind) {

    }

    int n = 0;

    @Override
    public void onBallClickInf(int kind, int index) {
        if ((lotteryType == 1 || lotteryType == 2) && hongqiuInf01.getCount() > 1) {
            redBallsLayout01.onChooseBall(index);
            ViewUtil.showTipsToast(this, "该玩法不支持复式投注");
        }
        Boolean refreshMoney = false;
        boolean ifSame = false;
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();
        if ((hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) == false) {
            enableClearBtn();
        }
        if (lotteryType == 4) {
            if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0)
                resetInf();
            else {
                if (hongNumber01 == 0 || hongNumber02 == 0) {
                    invalidateNum();
                }
                else {
                    boolean hongq01 = false;
                    boolean hongq02 = false;
                    if (kind != -1 && index != -1) {
                        hongq01 = hongqiuInf01.getBetBalls().get(index).isChoosed();
                        hongq02 = hongqiuInf02.getBetBalls().get(index).isChoosed();
                    }
                    if (!(hongq01 && hongq02)) {
                        refreshMoney = true;
                        betNumber = hongNumber01 * (hongNumber02);
                        betMoney = betNumber * 2 * 1;
                    }
                    else {
                        if (kind == 1)
                            redBallsLayout01.onChooseBall(index);
                        else if (kind == 2)
                            redBallsLayout02.onChooseBall(index);
                        ifSame = true;
                        ViewUtil.showTipsToast(this, "您已经选了相同的号码");
                    }
                }
            }
        }
        if (lotteryType == 7) {
            boolean hongq01 = false;
            boolean hongq02 = false;
            boolean hongq03 = false;
            if (kind != -1 && index != -1) {
                hongq01 = hongqiuInf01.getBetBalls().get(index).isChoosed();
                hongq02 = hongqiuInf02.getBetBalls().get(index).isChoosed();
                hongq03 = hongqiuInf03.getBetBalls().get(index).isChoosed();
            }
            if ((hongq01 == false && hongq02 == false) || (hongq01 == false && hongq03 == false) ||
                (hongq02 == false && hongq03 == false)) {
                if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0)
                    resetInf();
                else {
                    if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber02 == 0) {
                        invalidateNum();
                    }
                    else {
                        refreshMoney = true;
                        betNumber = hongNumber01 * hongNumber02 * hongNumber03;
                        betMoney = betNumber * 2 * 1;
                    }
                }
            }
            else if (hongq01 == false && hongq02 == false && hongq03 == false) {
                if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0)
                    resetInf();
                else {
                    if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber02 == 0) {
                        invalidateNum();
                    }
                    else {
                        refreshMoney = true;
// invalidateAll();
                        betNumber = hongNumber01 * hongNumber02 * hongNumber03;
                        betMoney = betNumber * 2 * 1;

                    }
                }
            }
            else {
                if (kind == 1)
                    redBallsLayout01.onChooseBall(index);
                else if (kind == 2)
                    redBallsLayout02.onChooseBall(index);
                else if (kind == 3)
                    redBallsLayout03.onChooseBall(index);
                ifSame = true;
                ViewUtil.showTipsToast(this, "您已经选了相同的号码");
            }
        }
        if (lotteryType != 4 && lotteryType != 7) {
            if (hongNumber01 == 0)
                resetInf();
            else {
                if (hongNumber01 < getKLSFMinNum(lotteryType)) {
                    invalidateNum();
                }
                else if (hongNumber01 > getKLSFMinNum(lotteryType)) {
                    refreshMoney = true;
                    betNumber =
                        (int) (MathUtil.factorial(hongNumber01, getKLSFMinNum(lotteryType)) / MathUtil.factorial(getKLSFMinNum(lotteryType),
                                                                                                                 getKLSFMinNum(lotteryType)));
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 == getKLSFMinNum(lotteryType)) {
                    refreshMoney = true;
                    betNumber = 1;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }

        checkBet(betMoney);

        if (refreshMoney) {
            invalidateAll();
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null) {
                moneyInf.setText(Html.fromHtml(betInf));
                showRewordInfo(hongNumber01, hongNumber02, hongNumber03);
            }
        }
        else if (ifSame == false) {
            initLotteryIntroduce();
        }

        setAnalyseTipsVisibility(hongNumber01);
    }

    public void initLotteryIntroduce() {
// lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "-"
// + "</font>元,您将盈利<font color=\"#CD2626\">" + "-" + "</font>元"));
    }

    private void showRewordInfo(int hongNumber01, int hongNumber02, int hongNumber03) {
        if (lotteryType == 1 || lotteryType == 2) {
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                (getReword(lotteryType) - betMoney) + "</font>元"));
        }
        else if (lotteryType == 3) {
            if (hongNumber01 == 2) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 3 || hongNumber01 == 4 || hongNumber01 == 5 || hongNumber01 == 6 ||
                hongNumber01 == 7 || hongNumber01 == 8) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 9 || hongNumber01 == 10 || hongNumber01 == 11 || hongNumber01 == 12 ||
                hongNumber01 == 13 || hongNumber01 == 14) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (28 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (28 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 15) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 2) /
                    MathUtil.factorial(2, 2) +
                    "</font>至<font color=\"#CD2626\">" +
                    (28 * getReword(lotteryType)) +
                    "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 2) /
                        MathUtil.factorial(2, 2) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" +
                    (getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 2) /
                        MathUtil.factorial(2, 2) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (28 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 16 || hongNumber01 == 17 || hongNumber01 == 18 || hongNumber01 == 19) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 2) /
                    MathUtil.factorial(2, 2) +
                    "</font>至<font color=\"#CD2626\">" +
                    (28 * getReword(lotteryType)) +
                    "</font>元,您将亏损<font color=\"#1874CD\"" +
                    ">" +
                    (betMoney - getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 2) /
                        MathUtil.factorial(2, 2)) + "</font>元至<font color=\"#1874CD\"" + ">" +
                    (betMoney - 28 * getReword(lotteryType)) + "</font>元"));
            }
            else if (hongNumber01 == 20) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) * 28 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                    (betMoney - getReword(lotteryType) * 28) + "</font>元"));
            }
        }
        else if (lotteryType == 4) {
            if (hongNumber01 > 0 && hongNumber02 == 1 || hongNumber01 == 1 && hongNumber02 > 0) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else
                for (int i = 2; i < 5; i++) {
                    if (hongNumber01 > (i - 1) && hongNumber02 == i || hongNumber01 == i &&
                        hongNumber02 > (i - 1)) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * i) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (getReword(lotteryType) * i - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (getReword(lotteryType) * i - betMoney) + "</font>元"));
                    }
                }
        }
        else if (lotteryType == 5) {
            if (hongNumber01 == 2) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 3 || hongNumber01 == 4 || hongNumber01 == 5 || hongNumber01 == 6 ||
                hongNumber01 == 7 || hongNumber01 == 8) {
                for (int i = 3; i < 9; i++) {
                    if (hongNumber01 == i) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) +
                            "</font>至<font color=\"#CD2626\">" +
                            ((i - 1) * getReword(lotteryType)) +
                            "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" +
                            (getReword(lotteryType) - betMoney) +
                            "</font>元至<font color=" +
                            ((i - 1) * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + ((i - 1) * getReword(lotteryType) - betMoney) + "</font>元"));
                        break;
                    }
                }
            }
            else if (hongNumber01 == 9 || hongNumber01 == 10 || hongNumber01 == 11 || hongNumber01 == 12 ||
                hongNumber01 == 13 || hongNumber01 == 14 || hongNumber01 == 15) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (7 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (7 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 16 || hongNumber01 == 17 || hongNumber01 == 18 || hongNumber01 == 19) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (7 * getReword(lotteryType)) + "</font>元,您将亏损<font color=\"#1874CD\"" + ">" +
                    (betMoney - getReword(lotteryType)) + "</font>元至<font color=\"#1874CD\"" + ">" +
                    (betMoney - 7 * getReword(lotteryType)) + "</font>元"));
            }
            else if (hongNumber01 == 20) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) * 7 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                    (betMoney - getReword(lotteryType) * 7) + "</font>元"));
            }
        }
        else if (lotteryType == 6) {
            if (hongNumber01 == 3) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 4 || hongNumber01 == 5 || hongNumber01 == 6 || hongNumber01 == 7 ||
                hongNumber01 == 8) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 9 || hongNumber01 == 10 || hongNumber01 == 11 || hongNumber01 == 12 ||
                hongNumber01 == 13 || hongNumber01 == 14 || hongNumber01 == 15) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (56 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (56 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 16) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 3) /
                    MathUtil.factorial(3, 3) +
                    "</font>至<font color=\"#CD2626\">" +
                    (56 * getReword(lotteryType)) +
                    "</font>元,您将盈利<font color=\"#1874CD\"" +
                    ">" +
                    (getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 3) /
                        MathUtil.factorial(3, 3) - betMoney) + "</font>元至<font color=\"#CD2626\"" + ">" +
                    (56 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 17 || hongNumber01 == 18 || hongNumber01 == 19) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 3) /
                    MathUtil.factorial(3, 3) +
                    "</font>至<font color=\"#CD2626\">" +
                    (56 * getReword(lotteryType)) +
                    "</font>元,您将亏损<font color=\"#1874CD\"" +
                    ">" +
                    (betMoney - getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 3) /
                        MathUtil.factorial(3, 3)) + "</font>元至<font color=\"#1874CD\"" + ">" +
                    (betMoney - 56 * getReword(lotteryType)) + "</font>元"));
            }
            else if (hongNumber01 == 20) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) * 56 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                    (betMoney - getReword(lotteryType) * 56) + "</font>元"));
            }
        }
        else if (lotteryType == 7) {
            if (hongNumber01 > 0 && hongNumber02 > 0 && hongNumber03 > 0) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
        }
        else if (lotteryType == 8) {
            if (hongNumber01 < 17) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\"" + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 > 16) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将亏损<font color=\"#1874CD\"" + ">" +
                    (betMoney - getReword(lotteryType)) + "</font>元"));
            }
        }
        else if (lotteryType == 9) {
            if (hongNumber01 == 4) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 5 || hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 9 || hongNumber01 == 10 || hongNumber01 == 11 || hongNumber01 == 12 ||
                hongNumber01 == 13 || hongNumber01 == 14 || hongNumber01 == 15 || hongNumber01 == 16) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (70 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (70 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 17) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 4) /
                    MathUtil.factorial(4, 4) +
                    "</font>至<font color=\"#CD2626\">" +
                    (70 * getReword(lotteryType)) +
                    "</font>元,您将盈利<font color=\"#1874CD\"" +
                    ">" +
                    (getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 4) /
                        MathUtil.factorial(4, 4) - betMoney) + "</font>元至<font color=\"#CD2626\"" + ">" +
                    (70 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 18 || hongNumber01 == 19) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 4) /
                    MathUtil.factorial(4, 4) +
                    "</font>至<font color=\"#CD2626\">" +
                    (70 * getReword(lotteryType)) +
                    "</font>元,您将亏损<font color=\"#1874CD\"" +
                    ">" +
                    (betMoney - getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 4) /
                        MathUtil.factorial(4, 4)) + "</font>元至<font color=\"#1874CD\"" + ">" +
                    (betMoney - 70 * getReword(lotteryType)) + "</font>元"));
            }
            else if (hongNumber01 == 20) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) * 70 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                    (betMoney - getReword(lotteryType) * 70) + "</font>元"));
            }
        }
        else if (lotteryType == 10) {
            if (hongNumber01 == 5) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 9 || hongNumber01 == 10 || hongNumber01 == 11 || hongNumber01 == 12 ||
                hongNumber01 == 13 || hongNumber01 == 14 || hongNumber01 == 15 || hongNumber01 == 16 ||
                hongNumber01 == 17 || hongNumber01 == 18) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                    (56 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                    (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                    (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                    ">" + (56 * getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (hongNumber01 == 19) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) *
                    MathUtil.factorial(8 - (20 - hongNumber01), 5) /
                    MathUtil.factorial(5, 5) +
                    "</font>至<font color=\"#CD2626\">" +
                    (56 * getReword(lotteryType)) +
                    "</font>元,您将亏损<font color=\"#1874CD\"" +
                    ">" +
                    (betMoney - getReword(lotteryType) * MathUtil.factorial(8 - (20 - hongNumber01), 5) /
                        MathUtil.factorial(5, 5)) + "</font>元至<font color=\"#1874CD\"" + ">" +
                    (betMoney - 56 * getReword(lotteryType)) + "</font>元"));
            }
            else if (hongNumber01 == 20) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) * 56 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                    (betMoney - getReword(lotteryType) * 56) + "</font>元"));
            }
        }
    }

    protected void goSelectLuckyBall() {

    }

    protected void resetInf() {
        super.resetInf();
        initLotteryIntroduce();
        choosingInf.setText(lotteryTypeTipsArray[lotteryType - 1]);
        showBallNum();
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        titlePopup.dismiss();
        databaseData.putString("hnklsf_way", LotteryUtils.HNKLSFWay[kind]);
        databaseData.commit();
        clearBalls();
        lotteryType = kind + 1;
        index_num = kind;
        showWay();
        showBallNum();
        showChoosingInf();
        initData(numLengthArray[kind]);
        drawLotteryBall(numLengthArray[kind]);
        loteryMethodType = lotteryType;
        if (loteryMethodType == 2 || loteryMethodType == 7 || loteryMethodType == 8 || loteryMethodType == 5) {
            ViewUtil.showTipsToast(this, "系统维护，该玩法暂不支持");
        }
        disableBetBtn();
    }
}
