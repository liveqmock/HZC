package com.haozan.caipiao.activity.bet.sd;

import java.util.ArrayList;
import java.util.Random;

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
import com.haozan.caipiao.activity.LotteryDiviningActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
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

public class SDActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private static final String SD_TIPS01 = "每位选1个，顺序猜中3个开奖号，奖金1000元";
    private static final String SD_TIPS02 = "至少选2个，猜中3个开奖号（2个相同），奖金320元";
    private static final String SD_TIPS03 = "至少选3个，猜中3个开奖号（3个不同），奖金160元";
    private static final String SD_TIPS04 = "每位至少选1个，开出对子中对号和单号，奖金320元";
    // add by vincent
    private static final String SD_TIPS05 = "请任意选择1个号码，猜3个开奖号之和";

    public static final int SD_HONGQIU_START = 0;
    public static final int SD_HONGQIU_LENGTH = 10;
    public static final int SD_HONGQIU_LIMIT = 10;
    public static final int SD_HONGQIU_MIN = 1;
    public static final int SD_HONGQIU_ZHIXUAN_HEZHI = 28;
    public static final int SD_HONGQIU_ZUSAN_HEZHI = 26;
    public static final int SD_HONGQIU_ZULIU_HEZHI = 22;

    private static String hotCondintion = null;
    private int lotteryType = 1;
    private String[] analyseHundred;
    private String[] analyseTen;
    private String[] analyseUnit;

    // add by vincent
    private static final int[] zxhz = {1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 63, 69, 73, 75};
    private static final int[] zshz = {1, 2, 1, 3, 3, 3, 4, 5, 4, 5, 5, 4, 5};
    private static final int[] zlhz = {1, 1, 2, 3, 4, 5, 7, 8, 9, 10, 10};

    long finalBetMoney = 0;

    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
    private NewBetBallsLayout redBallsLayout01;
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
    private NewBetBallsLayout redBallsLayout02;
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
    private NewBetBallsLayout redBallsLayout03;

    // add by vincent
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
    private NewBetBallsLayout redBallsLayout04;

    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
    private NewBetBallsLayout redBallsLayout05;

    private ArrayList<BetBall> hongqiu06;
    private BetBallsData hongqiuInf06;
    private NewBetBallsLayout redBallsLayout06;

    private RelativeLayout termLayout;
// private TextView choosingCountHundred;
// private TextView choosingCountTen;
// private TextView choosingCountUnit;
    // add by vincent
// private TextView choosingCountZxhz;
// private TextView choosingCountZshz;
// private TextView choosingCountZlhz;

    private LinearLayout choosingCountHundredLinear;
    private LinearLayout choosingCountTenLinear;
    private LinearLayout choosingCountUnitLinear;
    // add by vincent
    private LinearLayout choosingCountZxhzLinear;
    private LinearLayout choosingCountZshzLinear;
    private LinearLayout choosingCountZlhzLinear;
    private RelativeLayout pullDown;
    private TextView lotteryIndroduce;
// private RelativeLayout topBgLinear;
    private PopMenu popMneu;
    private int index_num = 0;
    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03;
    private TextView selectInfo;

    @Override
    public void setKind() {
        this.kind = "3d";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.sd);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = SD_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(SD_HONGQIU_LIMIT);
        hongqiuInf01.setBallType(1);
        // init red section two
        hongqiuInf02 = new BetBallsData();
        hongqiu02 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu02.add(ball);
        }
        hongqiuInf02.setBetBalls(hongqiu02);
        hongqiuInf02.setCount(0);
        hongqiuInf02.setColor("red");
        hongqiuInf02.setLimit(SD_HONGQIU_LIMIT);
        hongqiuInf02.setBallType(2);
        // init red section three
        hongqiuInf03 = new BetBallsData();
        hongqiu03 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu03.add(ball);
        }
        hongqiuInf03.setBetBalls(hongqiu03);
        hongqiuInf03.setCount(0);
        hongqiuInf03.setColor("red");
        hongqiuInf03.setLimit(SD_HONGQIU_LIMIT);
        hongqiuInf03.setBallType(3);

        // add by vincent
        // init red section four
        hongqiuInf04 = new BetBallsData();
        hongqiu04 = new ArrayList<BetBall>();
        redLength = SD_HONGQIU_ZHIXUAN_HEZHI;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu04.add(ball);
        }
        hongqiuInf04.setBetBalls(hongqiu04);
        hongqiuInf04.setCount(0);
        hongqiuInf04.setColor("red");
        hongqiuInf04.setLimit(SD_HONGQIU_ZHIXUAN_HEZHI);
        hongqiuInf04.setBallType(4);
        // init red section five
        hongqiuInf05 = new BetBallsData();
        hongqiu05 = new ArrayList<BetBall>();
        redLength = SD_HONGQIU_ZUSAN_HEZHI;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu05.add(ball);
        }
        hongqiuInf05.setBetBalls(hongqiu05);
        hongqiuInf05.setCount(0);
        hongqiuInf05.setColor("red");
        hongqiuInf05.setLimit(SD_HONGQIU_ZUSAN_HEZHI);
        hongqiuInf05.setBallType(5);
        // init red section six
        hongqiuInf06 = new BetBallsData();
        hongqiu06 = new ArrayList<BetBall>();
        redLength = SD_HONGQIU_ZULIU_HEZHI;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 3));
            hongqiu06.add(ball);
        }
        hongqiuInf06.setBetBalls(hongqiu06);
        hongqiuInf06.setCount(0);
        hongqiuInf06.setColor("red");
        hongqiuInf06.setLimit(SD_HONGQIU_ZULIU_HEZHI);
        hongqiuInf06.setBallType(6);
    }

    protected void setupViews() {
        super.setupViews();
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        lotteryIndroduce = (TextView) this.findViewById(R.id.lottery_introdution);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
// choosingCountHundred = (TextView) this.findViewById(R.id.sd_hongqiu01_text);
// choosingCountTen = (TextView) this.findViewById(R.id.sd_hongqiu02_text);
// choosingCountUnit = (TextView) this.findViewById(R.id.sd_hongqiu03_text);
        // add by vincent
// choosingCountZxhz = (TextView) this.findViewById(R.id.sd_hongqiu04_text);
// choosingCountZshz = (TextView) this.findViewById(R.id.sd_hongqiu05_text);
// choosingCountZlhz = (TextView) this.findViewById(R.id.sd_hongqiu06_text);

        choosingCountHundredLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu01_linear);
        choosingCountTenLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu02_linear);
        choosingCountUnitLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu03_linear);
        // add by vincent
        choosingCountZxhzLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu04_linear);
        choosingCountZshzLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu05_linear);
        choosingCountZlhzLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu06_linear);
        pullDown = (RelativeLayout) this.findViewById(R.id.analyse_tips_rala);

        // setup the first section
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls01);
        // setup the second section
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls02);
        // setup the third section
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls03);

        // add by vincent
        // setup the forth section
        redBallsLayout04 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls04);
        // setup the fifth section
        redBallsLayout05 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls05);
        // setup the sixth section
        redBallsLayout06 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls06);

// topBgLinear = (RelativeLayout) findViewById(R.id.top_bg_linear);
        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagHongqiu03 = (TextView) findViewById(R.id.tv_flag_hongqiu03);

        selectInfo = (TextView) findViewById(R.id.select_info);
    }

    private void showPopupViews() {
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        popMneu = new PopMenu(SDActivity.this, false);
        popMneu.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayThreeD,
                          LotteryUtils.moneyArrayThreeD, 1, findViewById(R.id.top).getMeasuredWidth() - 20,
                          index_num, true, true);
        popMneu.setButtonClickListener(this);
// popMneu.showAsDropDown(termLayout);
        topArrow.setImageResource(R.drawable.arrow_up_white);
        showPopupCenter(popMneu);
        popMneu.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
            }
        });
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        redBallsLayout01.initData(hongqiuInf01, bigBallViews, this);
        redBallsLayout01.setFullListener(this);
        redBallsLayout01.setTouchMoveListener(this);

        redBallsLayout02.initData(hongqiuInf02, bigBallViews, this);
        redBallsLayout02.setFullListener(this);
        redBallsLayout02.setTouchMoveListener(this);

        redBallsLayout03.initData(hongqiuInf03, bigBallViews, this);
        redBallsLayout03.setFullListener(this);
        redBallsLayout03.setTouchMoveListener(this);

        // add by vincent
        redBallsLayout04.initData(hongqiuInf04, bigBallViews, this);
        redBallsLayout04.setFullListener(this);
        redBallsLayout04.setTouchMoveListener(this);

        redBallsLayout05.initData(hongqiuInf05, bigBallViews, this);
        redBallsLayout05.setFullListener(this);
        redBallsLayout05.setTouchMoveListener(this);

        redBallsLayout06.initData(hongqiuInf06, bigBallViews, this);
        redBallsLayout06.setFullListener(this);
        redBallsLayout06.setTouchMoveListener(this);

        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("bet_way");
            if (type != 0) {
                lotteryType = type;
                if (lotteryType == 1)
                    databaseData.putString("sd_way", "sdzx");
                else if (lotteryType == 2)
                    databaseData.putString("sd_way", "sdzs");
                else if (lotteryType == 3)
                    databaseData.putString("sd_way", "sdzl");
                else if (lotteryType == 4)
                    databaseData.putString("sd_way", "sdzsdf");
                // add by vincent
                else if (lotteryType == 5)
                    databaseData.putString("sd_way", "sdzxhz");
                else if (lotteryType == 6)
                    databaseData.putString("sd_way", "sdzshz");
                else if (lotteryType == 7)
                    databaseData.putString("sd_way", "sdzlhz");

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
        showBallNum();
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
        lotteryCalculator.setVisibility(View.GONE);
        index_num = lotteryType - 1;
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("sd_way", "sdzx");
        if (sdWay.equals("sdzx")) {
            lotteryType = 1;
        }
        else if (sdWay.equals("sdzs")) {
            lotteryType = 2;
        }
        else if (sdWay.equals("sdzl")) {
            lotteryType = 3;
        }
        else if (sdWay.equals("sdzsdf")) {
            lotteryType = 4;
        }
        // add by vincent
        else if (sdWay.equals("sdzxhz")) {
            lotteryType = 5;
        }
        else if (sdWay.equals("sdzshz")) {
            lotteryType = 6;
        }
        else if (sdWay.equals("sdzlhz")) {
            lotteryType = 7;
        }
    }

    // display the appointed way of the 3d
    private void showWay() {
        if (lotteryType == 1) {
            title.setText("3D直选单复式");
            selectInfo.setText(SD_TIPS01);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);

            choosingCountHundredLinear.setVisibility(View.VISIBLE);
            choosingCountTenLinear.setVisibility(View.VISIBLE);
            choosingCountUnitLinear.setVisibility(View.VISIBLE);

            // add by vincent
// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 2) {
            title.setText("3D组三包号");
            selectInfo.setText(SD_TIPS02);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.VISIBLE);

            choosingCountHundredLinear.setVisibility(View.VISIBLE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);

            // add by vincent
// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 3) {
            title.setText("3D组六包号");
            selectInfo.setText(SD_TIPS03);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);

// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.VISIBLE);

            choosingCountHundredLinear.setVisibility(View.VISIBLE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);

            // add by vincent
// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 4) {
            title.setText("3D组三单复式");
            selectInfo.setText(SD_TIPS04);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.VISIBLE);

// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);

            choosingCountHundredLinear.setVisibility(View.VISIBLE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.VISIBLE);

            // add by vincent
// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.VISIBLE);
        }

        // add by vincent
        else if (lotteryType == 5) {
            title.setText("3D直选和值");
            selectInfo.setText(SD_TIPS05);

            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);

// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);

            choosingCountHundredLinear.setVisibility(View.GONE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);

// choosingCountZxhz.setVisibility(View.VISIBLE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.VISIBLE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.GONE);
        }
        else if (lotteryType == 6) {
            title.setText("3D组三和值");
            selectInfo.setText(SD_TIPS05);

            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);

// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);

            choosingCountHundredLinear.setVisibility(View.GONE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);

// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.VISIBLE);
// choosingCountZlhz.setVisibility(View.GONE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.VISIBLE);
            choosingCountZlhzLinear.setVisibility(View.GONE);
            pullDown.setVisibility(View.GONE);
        }
        else if (lotteryType == 7) {
            title.setText("3D组六和值");
            selectInfo.setText(SD_TIPS05);

            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);

// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);

            choosingCountHundredLinear.setVisibility(View.GONE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);

// choosingCountZxhz.setVisibility(View.GONE);
// choosingCountZshz.setVisibility(View.GONE);
// choosingCountZlhz.setVisibility(View.VISIBLE);
            choosingCountZxhzLinear.setVisibility(View.GONE);
            choosingCountZshzLinear.setVisibility(View.GONE);
            choosingCountZlhzLinear.setVisibility(View.VISIBLE);
            pullDown.setVisibility(View.GONE);
        }
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        if (lotteryType == 1) {
            String[] nums = lotteryMode[0].split(",");
            int firstLength = nums[0].length();
            for (int i = 0; i < firstLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
            int secondLength = nums[1].length();
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu02.get(num).setChoosed(true);
                redBallsLayout02.chooseBall(num);
            }
            int thirdLength = nums[2].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayout03.chooseBall(num);
            }
            onBallClickInf(-1, -1);
        }
        else if (lotteryType == 2) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
            onBallClickInf(-1, -1);
        }
        else if (lotteryType == 3) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
            onBallClickInf(-1, -1);
        }
        else if (lotteryType == 4) {
            if (lotteryMode[2].equals("1")) {
                String[] nums = lotteryMode[0].split(",");
                int num1 = Integer.valueOf(nums[0]);
                hongqiu01.get(num1).setChoosed(true);
                redBallsLayout01.chooseBall(num1);
                int num2 = Integer.valueOf(nums[2]);
                hongqiu03.get(num2).setChoosed(true);
                redBallsLayout03.chooseBall(num2);
            }
            else if (lotteryMode[2].equals("2")) {
                String[] nums = lotteryMode[0].split(",");
                int firstLength = nums[0].length();
                for (int i = 0; i < firstLength; i++) {
                    int num = Integer.valueOf(nums[0].substring(i, i + 1));
                    hongqiu01.get(num).setChoosed(true);
                    redBallsLayout01.chooseBall(num);
                }
                int secondLength = nums[1].length();
                for (int i = 0; i < secondLength; i++) {
                    int num = Integer.valueOf(nums[1].substring(i, i + 1));
                    hongqiu03.get(num).setChoosed(true);
                    redBallsLayout03.chooseBall(num);
                }
            }
            onBallClickInf(-1, -1);
        }
        // add by vincent
        else if (lotteryType == 5) {
            int num04 = Integer.valueOf(lotteryMode[0]);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            onBallClickInf(-1, num04);
        }
        else if (lotteryType == 6) {
            int num05 = Integer.valueOf(lotteryMode[0]);
            hongqiu05.get(num05 - 1).setChoosed(true);
            redBallsLayout05.chooseBall(num05 - 1);
            onBallClickInf(-1, num05 - 1);
        }
        else if (lotteryType == 7) {
            int num06 = Integer.valueOf(lotteryMode[0]);
            hongqiu06.get(num06 - 3).setChoosed(true);
            redBallsLayout06.chooseBall(num06 - 3);
            onBallClickInf(-1, num06 - 3);
        }

// onBallClickInf(-1, -1);
    }

    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
    }

    private String getBallsBetInf() {
        if (lotteryType == 1)
            return getBallsBetFirstKindInf();
        else if (lotteryType == 2)
            return getBallsBetSecondKindInf();
        else if (lotteryType == 3)
            return getBallsBetThirdKindInf();
        else if (lotteryType == 4)
            return getBallsBetForthKindInf();
        // add by vincent
        else if (lotteryType == 5)
            return getBallsBetFifthKindInf();
        else if (lotteryType == 6)
            return getBallsBetSixthKindInf();
        else if (lotteryType == 7)
            return getBallsBetSeventhKindInf();
        else
            return null;
    }

    private String getBallsBetFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength02 = hongqiu02.size();
        for (int i = 0; i < hongLength02; i++) {
            if (hongqiu02.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength03 = hongqiu03.size();
        for (int i = 0; i < hongLength03; i++) {
            if (hongqiu03.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":1:");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1) {
            betBallText.append("2:");
        }
        else {
            betBallText.append("1:");
        }
        return betBallText.toString();
    }

    private String getBallsBetSecondKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":2:3:");
        return betBallText.toString();
    }

    private String getBallsBetThirdKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":3:3:");
        return betBallText.toString();
    }

    private String getBallsBetForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (hongqiuInf01.getCount() > 1 || hongqiuInf03.getCount() > 1) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i);
            }
            betBallText.append(",");
            int hongLength03 = hongqiu03.size();
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i);
            }
            betBallText.append(":2:2:");
        }
        else {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i + "," + i);
            }
            betBallText.append(",");
            int hongLength03 = hongqiu03.size();
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i);
            }
            betBallText.append(":2:1:");
        }
        return betBallText.toString();
    }

    // add by vincnet
    private String getBallsBetFifthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed()) {
                betBallText.append(i);
                break;
            }
        }
        orgCode = betBallText.toString();
        betBallText.append(":1:4:");
        return betBallText.toString();
    }

    private String getBallsBetSixthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed()) {
                betBallText.append(i + 1);
                break;
            }
        }
        orgCode = betBallText.toString();
        betBallText.append(":2:4:");
        return betBallText.toString();
    }

    private String getBallsBetSeventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength06 = hongqiu06.size();
        for (int i = 0; i < hongLength06; i++) {
            if (hongqiu06.get(i).isChoosed()) {
                betBallText.append(i + 3);
                break;
            }
        }
        orgCode = betBallText.toString();
        betBallText.append(":3:4:");
        return betBallText.toString();
    }

    private String getBallsDisplayInf() {
        if (lotteryType == 1)
            return getBallsDisplayFirstKindInf();
        else if (lotteryType == 2)
            return getBallsDisplaySecondKindInf();
        else if (lotteryType == 3)
            return getBallsDisplayThirdKindInf();
        else if (lotteryType == 4)
            return getBallsDisplayForthKindInf();
        // add by vincent
        else if (lotteryType == 5)
            return getBallsDisplayFifthKindInf();
        else if (lotteryType == 6)
            return getBallsDisplaySixthKindInf();
        else if (lotteryType == 7)
            return getBallsDisplaySeventhKindInf();
        else
            return null;
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[直选]");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength03 = hongqiu03.size();
        if (hongqiuInf03.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySecondKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三包号]");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayThirdKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组六包号]");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三单复式]");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(i);
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int hongLength03 = hongqiu03.size();
        if (hongqiuInf03.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed()) {
                    betBallText.append(i);
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    // add by vincent
    private String getBallsDisplayFifthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[直选和值] ");
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed()) {
                betBallText.append(i);
                break;
            }
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySixthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三和值] ");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed()) {
                betBallText.append(i + 1);
                break;
            }
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组六和值] ");
        int hongLength06 = hongqiu06.size();
        for (int i = 0; i < hongLength06; i++) {
            if (hongqiu06.get(i).isChoosed()) {
                betBallText.append(i + 3);
                break;
            }
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 1) {
            if (hongqiuInf01.getCount() < SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 2) {
            if (hongqiuInf01.getCount() < 2)
                inf = SD_TIPS02;
        }
        else if (lotteryType == 3) {
            if (hongqiuInf01.getCount() < 3)
                inf = SD_TIPS03;
        }
        else if (lotteryType == 4) {
            if (hongqiuInf01.getCount() < SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
            if (hongqiuInf01.getCount() == 1 && hongqiuInf03.getCount() == 1) {
                int p = 0, q = 0;
                int hongLength01 = hongqiu01.size();
                for (int i = 0; i < hongLength01; i++) {
                    if (hongqiu01.get(i).isChoosed())
                        p = i;
                }
                int hongLength03 = hongqiu03.size();
                for (int i = 0; i < hongLength03; i++) {
                    if (hongqiu03.get(i).isChoosed())
                        q = i;
                }
                if (p == q)
                    inf = "组三单式不可以选择相同号码";
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
        String betInf = null;
        // changed by vincent
// if (lotteryType == 2)
// betInf = "2注     <font color='red'>4元</font>";
// else
// betInf = "1注     <font color='red'>2元</font>";
// moneyInf.setText(Html.fromHtml(betInf));
        if (lotteryType == 1) {
            Random rd = new Random();
            int num01 = rd.nextInt(SD_HONGQIU_LENGTH);
            int num02 = rd.nextInt(SD_HONGQIU_LENGTH);
            int num03 = rd.nextInt(SD_HONGQIU_LENGTH);
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayout02.chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            // add by vincent
            betInf = "1注     <font color='red'>2元</font>";
        }
        else if (lotteryType == 2) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, SD_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
                // add by vincent
                betInf = "2注     <font color='red'>4元</font>";
            }
        }
        else if (lotteryType == 3) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(3, SD_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
                // add by vincent
                betInf = "1注     <font color='red'>2元</font>";
            }
        }
        else if (lotteryType == 4) {
            Random rd = new Random();
            int num01 = rd.nextInt(SD_HONGQIU_LENGTH);
            int num03 = rd.nextInt(SD_HONGQIU_LENGTH);
            while (num01 == num03)
                num03 = rd.nextInt(SD_HONGQIU_LENGTH);
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            // add by vincent
            betInf = "1注     <font color='red'>2元</font>";
        }

        // add by vincent
        else if (lotteryType == 5) {
            Random rd = new Random();
            int num04 = rd.nextInt(SD_HONGQIU_ZHIXUAN_HEZHI);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            if (num04 > 13)
                num04 = 27 - num04;
            betNumber = zxhz[num04];
            betMoney = betNumber * 2;
            betInf = betNumber + "注     <font color='red'>" + betMoney + "元</font>";
            finalBetMoney = betMoney;
        }
        else if (lotteryType == 6) {
            Random rd = new Random();
            int num05 = rd.nextInt(SD_HONGQIU_ZUSAN_HEZHI);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
            if (num05 > 12)
                num05 = 25 - num05;
            betNumber = zshz[num05];
            betMoney = betNumber * 2;
            betInf = betNumber + "注     <font color='red'>" + betMoney + "元</font>";
            finalBetMoney = betMoney;
        }
        else if (lotteryType == 7) {
            Random rd = new Random();
            int num06 = rd.nextInt(SD_HONGQIU_ZULIU_HEZHI);
            hongqiu06.get(num06).setChoosed(true);
            redBallsLayout06.chooseBall(num06);
            if (num06 > 10)
                num06 = 21 - num06;
            betNumber = zlhz[num06];
            betMoney = betNumber * 2;
            betInf = betNumber + "注     <font color='red'>" + betMoney + "元</font>";
            finalBetMoney = betMoney;
        }

        moneyInf.setText(Html.fromHtml(betInf));
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        invalidateAll();
        if (lotteryType == 2)
            betMoney = 4;
        // add by vincent
        else if (lotteryType == 5 || lotteryType == 6 || lotteryType == 7)
            betMoney = finalBetMoney;

        else
            betMoney = 2;
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        if (lotteryType == 1) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
        }
        else if (lotteryType == 2 || lotteryType == 3)
            redBallsLayout01.resetBalls();
        else if (lotteryType == 4) {
            redBallsLayout01.resetBalls();
            redBallsLayout03.resetBalls();
        }

        // add by vincent
        else if (lotteryType == 5) {
            redBallsLayout04.resetBalls();
        }
        else if (lotteryType == 6) {
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 7) {
            redBallsLayout06.resetBalls();
        }

        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        if (lotteryType == 1)
            selectInfo.setText(SD_TIPS01);
        else if (lotteryType == 2)
            selectInfo.setText(SD_TIPS02);
        else if (lotteryType == 3)
            selectInfo.setText(SD_TIPS03);
        else if (lotteryType == 4)
            selectInfo.setText(SD_TIPS04);
        // add by vincent
        else if (lotteryType == 4 || lotteryType == 5 || lotteryType == 6)
            selectInfo.setText(SD_TIPS05);
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        if (ballType == 1) {
            ViewUtil.showTipsToast(this, "您只能选" + SD_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 2) {
            ViewUtil.showTipsToast(this, "您只能选" + SD_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 3) {
            ViewUtil.showTipsToast(this, "您只能选" + SD_HONGQIU_LIMIT + "个红球");
        }
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        Boolean refreshMoney = false;
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();

        // add by vincent
        int hongNumber04 = hongqiuInf04.getCount();
        int hongNumber05 = hongqiuInf05.getCount();
        int hongNumber06 = hongqiuInf06.getCount();

        // changed by vincent
        if ((hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
            hongNumber05 == 0 && hongNumber06 == 0) == false) {
            enableClearBtn();
        }
        if (lotteryType == 1) {
            if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    invalidateAll();
                    betNumber = hongNumber01 * hongNumber02 * hongNumber03;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        else if (lotteryType == 2) {
            if (hongNumber01 >= 2) {
                refreshMoney = true;
                invalidateAll();
                betNumber = MathUtil.factorial(hongNumber01, 2);
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else
                resetInf();
        }
        else if (lotteryType == 3) {
            if (hongNumber01 >= 3) {
                refreshMoney = true;
                invalidateAll();
                betNumber = (int) (MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3));
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else {
                resetInf();
            }
        }
        else if (lotteryType == 4) {
            if (hongNumber01 == 0 && hongNumber03 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber03 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    invalidateAll();
                    if ((hongNumber03 + hongNumber01 == 2) && hongNumber01 != 0 && hongNumber03 != 0) {
                        betNumber = 1;
                    }
                    else {
                        int sameCount = findSameNum();
                        betNumber = hongNumber03 * hongNumber01 - sameCount;
                    }
                    betMoney = betNumber * 2 * 1;
                }
            }
        }

        // add by vincent
        else if (lotteryType == 5) {
            if (hongNumber04 == 0) {
                resetInf();
            }
            else {
                for (int i = 0; i < SD_HONGQIU_ZHIXUAN_HEZHI; i++) {
                    if (i != index) {
                        if (hongqiu04.get(i).isChoosed()) {
                            hongqiu04.get(i).setChoosed(false);
                            hongqiuInf04.setCount(1);
                            break;
                        }
                    }
                }
                redBallsLayout04.refreshAllBall();
                invalidateAll();
                refreshMoney = true;
                // 添加对betNumber和betMoney的处理
                if (index > 13)
                    index = 27 - index;
                betNumber = zxhz[index];
                betMoney = betNumber * 2;
            }
        }
        else if (lotteryType == 6) {
            if (hongNumber05 == 0) {
                resetInf();
            }
            else {
                for (int i = 0; i < SD_HONGQIU_ZUSAN_HEZHI; i++) {
                    if (i != index) {
                        if (hongqiu05.get(i).isChoosed()) {
                            hongqiu05.get(i).setChoosed(false);
                            hongqiuInf05.setCount(1);
                            break;
                        }
                    }
                }
                redBallsLayout05.refreshAllBall();
                invalidateAll();
                refreshMoney = true;
                // 添加对betNumber和betMoney的处理
                if (index > 12)
                    index = 25 - index;
                betNumber = zshz[index];
                betMoney = betNumber * 2;
            }
        }
        else if (lotteryType == 7) {
            if (hongNumber06 == 0) {
                resetInf();
            }
            else {
                for (int i = 0; i < SD_HONGQIU_ZULIU_HEZHI; i++) {
                    if (i != index) {
                        if (hongqiu06.get(i).isChoosed()) {
                            hongqiu06.get(i).setChoosed(false);
                            hongqiuInf06.setCount(1);
                            break;
                        }
                    }
                }
                redBallsLayout06.refreshAllBall();
                invalidateAll();
                refreshMoney = true;
                // 添加对betNumber和betMoney的处理
                if (index > 10)
                    index = 21 - index;
                betNumber = zlhz[index];
                betMoney = betNumber * 2;
            }
        }

        checkBet(betMoney);
        if (refreshMoney) {
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null)
                moneyInf.setText(Html.fromHtml(betInf));
        }
    }

    private int findSameNum() {
        int n = 0;
        for (int i = 0; i < SD_HONGQIU_LIMIT; i++) {
            Boolean first = hongqiu01.get(i).isChoosed();
            Boolean second = hongqiu03.get(i).isChoosed();
            if (first && second)
                n = n + 1;
        }
        return n;
    }

    @Override
    protected void showBallNum() {
        if (lotteryType == 1) {
// choosingCountHundred.setText("百位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf02.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf03.getCount() + "/1个");
            flagHongqiu01.setText("百位");
            flagHongqiu02.setText("十位");
            flagHongqiu03.setText("个位");
        }
        else if (lotteryType == 2)
// choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 3)
// choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 4) {
// choosingCountHundred.setText("百位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf03.getCount() + "/1个");
            flagHongqiu01.setText("对号");
            flagHongqiu03.setText("单号");
        }
        // add by vincent
        else if (lotteryType == 5)
// choosingCountZxhz.setText("红球：" + hongqiuInf04.getCount() + "/1个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 6)
// choosingCountZshz.setText("红球：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 7)
// choosingCountZlhz.setText("红球：" + hongqiuInf06.getCount() + "/1个");
            flagHongqiu01.setText("红球");
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
// choosingInf.setText(Html.fromHtml(displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "3D游戏规则");
        bundel.putString("lottery_help", "help_new/x3d.html");
        intent.putExtras(bundel);
        intent.setClass(SDActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "3D走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=sd&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(SDActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(SDActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "3d");
        if (lotteryType == 1)
            bundle.putString("lotteryTypeZS", "sdzx");
        else if (lotteryType == 2)
            bundle.putString("lotteryTypeZS", "sdzs");
        else if (lotteryType == 3)
            bundle.putString("lotteryTypeZS", "sdzl");
        else if (lotteryType == 4)
            bundle.putString("lotteryTypeZS", "sdzsdf");

        // add by vincent
        /*
         * else if (lotteryType == 5) bundle.putString("lotteryTypeZS", "sdzxhz"); else if (lotteryType == 6)
         * bundle.putString("lotteryTypeZS", "sdzshz"); else if (lotteryType == 7)
         * bundle.putString("lotteryTypeZS", "sdzlhz");
         */

        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopupViews();
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
        if (requestCode == 15) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                int[] vertor = b.getIntArray("luckyNumArray");
                mstar = b.getString("mstar");
                todayluck = b.getString("today_lucky");
                clearBalls();
                enableBetBtn();
                enableClearBtn();

                // changed by vincent
// String betInf = "1注     <font color='red'>2元</font>";
// moneyInf.setText(Html.fromHtml(betInf));
                // for (int i = 0; i < 7; i++) { if (lotteryType == 1)

                if (lotteryType == 1) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    redBallsLayout02.chooseBall(vertor[1]);
                    redBallsLayout03.chooseBall(vertor[2]);
                }
                else if (lotteryType == 2) {
                    for (int i = 0; i < 2; i++) {
                        if (!hongqiu01.get(vertor[i]).isChoosed()) {
                            hongqiu01.get(vertor[i]).setChoosed(true);
                            redBallsLayout01.chooseBall(vertor[i]);
                        }
                    }
                }
                else if (lotteryType == 3) {
                    for (int i = 0; i < 3; i++) {
                        if (!hongqiu01.get(vertor[i]).isChoosed()) {
                            hongqiu01.get(vertor[i]).setChoosed(true);
                            redBallsLayout01.chooseBall(vertor[i]);
                        }
                    }
                }
                else if (lotteryType == 4) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    hongqiu03.get(vertor[1]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[1]);
                }
                // add by vincent
                else if (lotteryType == 5) {
                    int num04 = vertor[0];
                    hongqiu04.get(num04).setChoosed(true);
                    redBallsLayout04.chooseBall(num04);
                    if (num04 > 13)
                        num04 = 27 - num04;
                    betNumber = zxhz[num04];
                    betMoney = betNumber * 2;
                }
                else if (lotteryType == 6) {
                    int num05 = vertor[0];
                    hongqiu05.get(num05).setChoosed(true);
                    redBallsLayout05.chooseBall(num05);
                    if (num05 > 12)
                        num05 = 25 - num05;
                    betNumber = zshz[num05];
                    betMoney = betNumber * 2;
                }
                else if (lotteryType == 7) {
                    int num06 = vertor[0];
                    hongqiu06.get(num06).setChoosed(true);
                    redBallsLayout06.chooseBall(num06);
                    if (num06 > 10)
                        num06 = 21 - num06;
                    betNumber = zlhz[num06];
                    betMoney = betNumber * 2;
                }

                // changed by vincent
                String betInf = betNumber + "注     <font color='red'>" + betMoney + "元</font>";
                moneyInf.setText(Html.fromHtml(betInf));
                invalidateAll();
                onBallClickInf(-1, -1);
                mode = "1003";
                luckynum = orgCode;
            }
        }
        else if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout01.refreshAllBallInf(showHotNum);
        redBallsLayout02.refreshAllBallInf(showHotNum);
        redBallsLayout03.refreshAllBallInf(showHotNum);
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
                    String hundred = ja.getData(analyseData, "no1_distrb");
                    String ten = ja.getData(analyseData, "no2_distrb");
                    String unit = ja.getData(analyseData, "no3_distrb");
                    analyseHundred = StringUtil.spliteString(hundred, ",");
                    analyseTen = StringUtil.spliteString(ten, ",");
                    analyseUnit = StringUtil.spliteString(unit, ",");
                    int hundredLength = analyseHundred.length;
                    int tenLength = analyseTen.length;
                    int unitLength = analyseUnit.length;
                    int count;
                    for (int i = 0; i < hundredLength; i++) {
                        count = Integer.valueOf(analyseHundred[i]);
                        if (count < 5)
                            analyseHundred[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseHundred[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseHundred[i] = "出" + count + "次";
                        hongqiu01.get(i).setBallsInf(analyseHundred[i]);
                    }
                    for (int i = 0; i < tenLength; i++) {
                        count = Integer.valueOf(analyseTen[i]);
                        if (count < 5)
                            analyseTen[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseTen[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseTen[i] = "出" + count + "次";
                        hongqiu02.get(i).setBallsInf(analyseTen[i]);
                    }
                    for (int i = 0; i < unitLength; i++) {
                        count = Integer.valueOf(analyseUnit[i]);
                        if (count < 5)
                            analyseUnit[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseUnit[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseUnit[i] = "出" + count + "次";
                        hongqiu03.get(i).setBallsInf(analyseUnit[i]);
                    }
                    refreshHotNumShow();
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
    public String getQ_code() {
        sdNum01 = 0;
        sdNum02 = 0;
        sdNum03 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                sdNum01 = sdNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                sdNum02 = sdNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                sdNum03 = sdNum03 + 1;
            }
        }
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int sdNum01 = 0;
    private int sdNum02 = 0;
    private int sdNum03 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals(",,")) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (sdNum01 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理百位上1个红球");
            }
            else if (sdNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理十位上1个红球");
            }
            else if (sdNum03 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理个位上1个红球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "3d");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(SDActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        popMneu.dismiss();
        if (!LotteryUtils.sdWay[kind].equals("sdzl"))
            lotteryIndroduce.setVisibility(View.GONE);
        databaseData.putString("sd_way", LotteryUtils.sdWay[kind]);
        databaseData.commit();
        clearBalls();
        lotteryType = kind + 1;
        index_num = kind;
        title.setText(tabName);
        showWay();
        showBallNum();
    }
}
