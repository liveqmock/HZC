package com.haozan.caipiao.activity.bet.jxssc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.haozan.caipiao.activity.bet.cqssc.CQSSCActivity;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.BallTextView;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;

/**
 * 江西时时彩
 * 
 * @author Vincent
 * @create-time 2013-6-26 下午2:03:52
 */
public class JXSSCActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private static final String CQSSC_TIPS01 = "每位至少选1个，顺序猜中前2、前3、后2、后3都有奖";
    private static final String CQSSC_TIPS02 = "每位至少选1个，顺序猜中全部5个开奖号，奖金11.6万";
    private static final String CQSSC_TIPS03 = "每位至少选1个，顺序中后4、中3、后3都有奖";
    private static final String CQSSC_TIPS04 = "至少选3个，猜中开奖号后3个(不同号)，奖金190元";
    private static final String CQSSC_TIPS05 = "至少选2个，猜中开奖号后3个(2位相同)，奖金385元";
    private static final String CQSSC_TIPS06 = "每位至少选1个，顺序猜中开奖号后3个，奖金1160元";
    private static final String CQSSC_TIPS07 = "每位至少选1个，顺序猜中开奖号后2个，奖金116元";
    private static final String CQSSC_TIPS08 = "至少选两位，猜中对应开奖号，奖金116元";// 二星直选复式
    private static final String CQSSC_TIPS09 = "每位至少选1个，猜中开奖号最后位，奖金11元";
    private static final String CQSSC_TIPS10 = "至少选一位，猜中对应开奖号，奖金11元";
    private static final String CQSSC_TIPS010 = "每位选1个，猜中最后两位大小单双，奖金4元";
    private static final int WXTX = 1;
    private static final int WXZX = 2;
    private static final int SIXZX = 3;
    private static final int SXZL = 4;
    private static final int SXZS = 5;
    private static final int SXZX = 6;
    private static final int EXZX = 7;
    private static final int RXER = 8;
    private static final int YXZX = 9;
    private static final int RXY = 10;
    private static final int DXDS = 11;
    private static final int[] reword = {20460, 116000, 10000, 190, 385, 1160, 116, 116, 11, 11, 4};
    private int lotteryType = 1;
    private String[] analyseHundred;
    private String[] analyseTen;
    private String[] analyseUnit;

    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
// private NewBetBallsLayout redBallsLayouts[0];
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
// private NewBetBallsLayout redBallsLayouts[1];
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
// private NewBetBallsLayout redBallsLayouts[2];
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
// private NewBetBallsLayout redBallsLayouts[3];
    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
// private NewBetBallsLayout redBallsLayouts[4];
    private ArrayList<BetBall> hongqiu06;
    private BetBallsData hongqiuInf06;
// private NewBetBallsLayout redBallsLayouts[5];
    private ArrayList<BetBall> hongqiu07;
    private BetBallsData hongqiuInf07;
// private NewBetBallsLayout redBallsLayouts[6];

    private NewBetBallsLayout[] redBallsLayouts;
    private int[] redBallsLayoutsIds = {R.id.cqssc_hongqiu_balls01, R.id.cqssc_hongqiu_balls02,
            R.id.cqssc_hongqiu_balls03, R.id.cqssc_hongqiu_balls04, R.id.cqssc_hongqiu_balls05,
            R.id.cqssc_hongqiu_balls06, R.id.cqssc_hongqiu_balls07};;
    private RelativeLayout termLayout;
    private TextView[] choosingCount;
    private int[] choosingCountIds = {R.id.cqssc_hongqiu01_text, R.id.cqssc_hongqiu02_text,
            R.id.cqssc_hongqiu03_text, R.id.cqssc_hongqiu04_text, R.id.cqssc_hongqiu05_text,
            R.id.cqssc_hongqiu06_text, R.id.cqssc_hongqiu07_text};
    private LinearLayout[] betFieldBgs;
    private int[] betFieldBgsIds = {R.id.cqssc_hongqiu_balls01_linear, R.id.cqssc_hongqiu_balls02_linear,
            R.id.cqssc_hongqiu_balls03_linear, R.id.cqssc_hongqiu_balls04_linear,
            R.id.cqssc_hongqiu_balls05_linear, R.id.cqssc_hongqiu_balls06_linear,
            R.id.cqssc_hongqiu_balls07_linear};
    int[] fullNum = {10, 10, 10, 10, 10, 10, 10, 10, 5, 10, 4};

    private TextView lotteryIntroduce;
    private boolean ifLotteryIntroduceShown = false;

    private PopMenu titlePopup;
    private int index_num = 0;
    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03, flagHongqiu04, flagHongqiu05,
        flagHongqiu06, flagHongqiu07;
    private TextView selectInfo;

    // lotteryType 与玩法对照
    // 1.五星通选 2.五星直选 3.四星直选 4.三星组六 5.三星组三 6.三星直选 7.二星直选 8.任选二 9.一星直选 10.任选一 11.大小单双

    private Map<String, Integer> nsscWayMap;

    @Override
    public void setKind() {
        this.kind = "jxssc";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.cqssc);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initSscWayMap() {
        nsscWayMap = new HashMap<String, Integer>();
        nsscWayMap.put("五星通选", 1);
        nsscWayMap.put("五星直选", 2);
        nsscWayMap.put("四星直选", 3);
        nsscWayMap.put("三星组六", 4);
        nsscWayMap.put("三星组三", 5);
        nsscWayMap.put("三星直选", 6);
        nsscWayMap.put("二星直选", 7);
        nsscWayMap.put("任选二", 8);
        nsscWayMap.put("一星直选", 9);
        nsscWayMap.put("任选一", 10);
        nsscWayMap.put("大小单双", 11);
    }

    private void initData() {
        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = CQSSCActivity.CQSSC_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf02.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf03.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        hongqiuInf03.setBallType(3);
        // init red section four
        hongqiuInf04 = new BetBallsData();
        hongqiu04 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu04.add(ball);
        }
        hongqiuInf04.setBetBalls(hongqiu04);
        hongqiuInf04.setCount(0);
        hongqiuInf04.setColor("red");
        hongqiuInf04.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        hongqiuInf04.setBallType(4);
        // init red section five
        hongqiuInf05 = new BetBallsData();
        hongqiu05 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu05.add(ball);
        }
        hongqiuInf05.setBetBalls(hongqiu05);
        hongqiuInf05.setCount(0);
        hongqiuInf05.setColor("red");
        hongqiuInf05.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        hongqiuInf05.setBallType(5);
        // init red section six
        hongqiuInf06 = new BetBallsData();
        hongqiu06 = new ArrayList<BetBall>();
        for (int i = 0; i < 4; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(LotteryUtils.BALL_NAME[i]);
            hongqiu06.add(ball);
        }
        hongqiuInf06.setBetBalls(hongqiu06);
        hongqiuInf06.setCount(0);
        hongqiuInf06.setColor("red");
        hongqiuInf06.setLimit(4);
        hongqiuInf06.setBallType(6);
        // init red section Seven
        hongqiuInf07 = new BetBallsData();
        hongqiu07 = new ArrayList<BetBall>();
        for (int i = 0; i < 4; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(LotteryUtils.BALL_NAME[i]);
            hongqiu07.add(ball);
        }
        hongqiuInf07.setBetBalls(hongqiu07);
        hongqiuInf07.setCount(0);
        hongqiuInf07.setColor("red");
        hongqiuInf07.setLimit(4);
        hongqiuInf07.setBallType(7);
    }

    protected void setupViews() {
        super.setupViews();

        // 隐藏分析工具
// findViewById(R.id.analyse_tips_rala).setVisibility(View.GONE);
        normalToolsLayout.setVisibility(View.GONE);
        numAnalyse.setVisibility(View.GONE);

        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        lotteryIntroduce = (TextView) this.findViewById(R.id.lottery_introdution);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        choosingCount = new TextView[7];
        betFieldBgs = new LinearLayout[7];
        redBallsLayouts = new NewBetBallsLayout[7];
        for (int i = 0; i < choosingCount.length; i++) {
            choosingCount[i] = (TextView) this.findViewById(choosingCountIds[i]);
            betFieldBgs[i] = (LinearLayout) this.findViewById(betFieldBgsIds[i]);
            redBallsLayouts[i] = (NewBetBallsLayout) this.findViewById(redBallsLayoutsIds[i]);
        }

        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagHongqiu03 = (TextView) findViewById(R.id.tv_flag_hongqiu03);
        flagHongqiu04 = (TextView) findViewById(R.id.tv_flag_hongqiu04);
        flagHongqiu05 = (TextView) findViewById(R.id.tv_flag_hongqiu05);
        flagHongqiu06 = (TextView) findViewById(R.id.tv_flag_hongqiu06);
        flagHongqiu07 = (TextView) findViewById(R.id.tv_flag_hongqiu07);
        selectInfo = (TextView) findViewById(R.id.select_info);
    }

    private void showPopupViews() {
        titlePopup = new PopMenu(JXSSCActivity.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayJXSSC,
                             LotteryUtils.moneyArrayJXSSC, 1, findViewById(R.id.top).getMeasuredWidth() - 20,
                             index_num, true, true);
        titlePopup.setButtonClickListener(this);
        topArrow.setImageResource(R.drawable.arrow_up_white);
        showPopupCenter(titlePopup);
        titlePopup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
            }
        });
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    private void init() {
        initSscWayMap();
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        redBallsLayouts[0].initData(hongqiuInf01, bigBallViews, this);
        redBallsLayouts[0].setFullListener(this);
        redBallsLayouts[0].setTouchMoveListener(this);

        redBallsLayouts[1].initData(hongqiuInf02, bigBallViews, this);
        redBallsLayouts[1].setFullListener(this);
        redBallsLayouts[1].setTouchMoveListener(this);

        redBallsLayouts[2].initData(hongqiuInf03, bigBallViews, this);
        redBallsLayouts[2].setFullListener(this);
        redBallsLayouts[2].setTouchMoveListener(this);

        redBallsLayouts[3].initData(hongqiuInf04, bigBallViews, this);
        redBallsLayouts[3].setFullListener(this);
        redBallsLayouts[3].setTouchMoveListener(this);

        redBallsLayouts[4].initData(hongqiuInf05, bigBallViews, this);
        redBallsLayouts[4].setFullListener(this);
        redBallsLayouts[4].setTouchMoveListener(this);

        redBallsLayouts[5].initData(hongqiuInf06, bigBallViews, this);
        redBallsLayouts[5].setFullListener(this);
        redBallsLayouts[5].setTouchMoveListener(this);

        redBallsLayouts[6].initData(hongqiuInf07, bigBallViews, this);
        redBallsLayouts[6].setFullListener(this);
        redBallsLayouts[6].setTouchMoveListener(this);

        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initLotteryIntroduce();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            lotteryType = bundle.getInt("bet_way");
            boolean flag = false;
            for (int i = 0; i < LotteryUtils.JXSSCWay.length; i++) {
                if (lotteryType == i + 1) {
                    exzxFullBall(fullNum[i]);
                    databaseData.putString("jxssc_way", LotteryUtils.JXSSCWay[i]);
                    databaseData.commit();
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                resetLotteryType();
            }
        }
        else {
            resetLotteryType();
        }
        showWay();
        showBallNum();
        initInf();
        index_num = lotteryType - 1;
    }

    // 限制选球个数
    private void exzxFullBall(int limit) {
        if (lotteryType == EXZX || lotteryType == SXZL) {
            hongqiuInf01.setLimit(limit);
            hongqiuInf05.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        }
        else if (lotteryType == YXZX) {
            hongqiuInf05.setLimit(limit);
            hongqiuInf01.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        }
        else {
            hongqiuInf01.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
            hongqiuInf05.setLimit(CQSSCActivity.CQSSC_HONGQIU_LIMIT);
        }
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("jxssc_way", "jxssc_wxtx");
        for (int i = 0; i < LotteryUtils.JXSSCWay.length; i++) {
            if (sdWay.equals(LotteryUtils.JXSSCWay[i])) {
                lotteryType = i + 1;
                exzxFullBall(fullNum[i]);
                break;
            }
        }
    }

    private void showWay() {
        initLotteryIntroduce();
        title.setText(LotteryUtils.textArrayJXSSC[lotteryType - 1]);
        if (lotteryType == WXTX) {
            selectInfo.setText(CQSSC_TIPS01);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i < 5) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == WXZX) {
            selectInfo.setText(CQSSC_TIPS02);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i < 5) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }

        else if (lotteryType == SIXZX) {
            selectInfo.setText(CQSSC_TIPS03);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 0 || i == 5 || i == 6) {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
                else {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
            }
        }

        else if (lotteryType == SXZL) {
            selectInfo.setText(CQSSC_TIPS04);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 0) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == SXZS) {
            selectInfo.setText(CQSSC_TIPS05);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 0) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == SXZX) {
            selectInfo.setText(CQSSC_TIPS06);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 2 || i == 3 || i == 4) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == EXZX) {
            selectInfo.setText(CQSSC_TIPS07);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 3 || i == 4) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == RXER) {
            selectInfo.setText(CQSSC_TIPS08);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i < 5) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == YXZX) {
            selectInfo.setText(CQSSC_TIPS09);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i == 4) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == RXY) {
            selectInfo.setText(CQSSC_TIPS10);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i < 5) {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
                else {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
            }
        }
        else if (lotteryType == DXDS) {
            selectInfo.setText(CQSSC_TIPS010);
            for (int i = 0; i < choosingCount.length; i++) {
                if (i < 5) {
// choosingCount[i].setVisibility(View.GONE);
                    betFieldBgs[i].setVisibility(View.GONE);
                    redBallsLayouts[i].setVisibility(View.GONE);
                }
                else {
// choosingCount[i].setVisibility(View.VISIBLE);
                    betFieldBgs[i].setVisibility(View.VISIBLE);
                    redBallsLayouts[i].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // TODO
    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        if (lotteryType == WXTX) {
            String[] nums = lotteryMode[0].split(",");
            int firstLength = nums[0].length();
            for (int i = 0; i < firstLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu01.get(num).setChoosed(true);
                redBallsLayouts[0].chooseBall(num);
            }
            int secondLength = nums[1].length();
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu02.get(num).setChoosed(true);
                redBallsLayouts[1].chooseBall(num);
            }
            int thirdLength = nums[2].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayouts[2].chooseBall(num);
            }
            int fourthLength = nums[3].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayouts[3].chooseBall(num);
            }
            int fifthLength = nums[4].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[4].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == WXZX) {
            String[] nums = lotteryMode[0].split(",");
            int firstLength = nums[0].length();
            for (int i = 0; i < firstLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu01.get(num).setChoosed(true);
                redBallsLayouts[0].chooseBall(num);
            }
            int secondLength = nums[1].length();
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu02.get(num).setChoosed(true);
                redBallsLayouts[1].chooseBall(num);
            }
            int thirdLength = nums[2].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayouts[2].chooseBall(num);
            }
            int fourthLength = nums[3].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayouts[3].chooseBall(num);
            }
            int fifthLength = nums[4].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[4].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == SIXZX) {
            String[] nums = lotteryMode[0].split(",");
            int secondLength = nums[0].length();
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu02.get(num).setChoosed(true);
                redBallsLayouts[1].chooseBall(num);
            }
            int thirdLength = nums[1].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayouts[2].chooseBall(num);
            }
            int fourthLength = nums[2].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayouts[3].chooseBall(num);
            }
            int fifthLength = nums[3].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == SXZL) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayouts[0].chooseBall(num);
            }
        }
        else if (lotteryType == SXZS) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayouts[0].chooseBall(num);
            }
        }
        else if (lotteryType == SXZX) {
            String[] nums = lotteryMode[0].split(",");
            int thirdLength = nums[0].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayouts[2].chooseBall(num);
            }
            int fourthLength = nums[1].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayouts[3].chooseBall(num);
            }
            int fifthLength = nums[2].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == EXZX) {
            String[] nums = lotteryMode[0].split(",");
            int fourthLength = nums[0].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayouts[3].chooseBall(num);
            }
            int fifthLength = nums[1].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == RXER || lotteryType == RXY) {
            String[] nums = lotteryMode[0].split(",");
            if (!"X".equals(nums[0])) {
                int firstLength = nums[0].length();
                for (int i = 0; i < firstLength; i++) {
                    int num = Integer.valueOf(nums[0].substring(i, i + 1));
                    hongqiu01.get(num).setChoosed(true);
                    redBallsLayouts[0].chooseBall(num);
                }
            }
            if (!"X".equals(nums[1])) {
                int secondLength = nums[1].length();
                for (int i = 0; i < secondLength; i++) {
                    int num = Integer.valueOf(nums[1].substring(i, i + 1));
                    hongqiu02.get(num).setChoosed(true);
                    redBallsLayouts[1].chooseBall(num);
                }
            }
            if (!"X".equals(nums[2])) {
                int thirdLength = nums[2].length();
                for (int i = 0; i < thirdLength; i++) {
                    int num = Integer.valueOf(nums[2].substring(i, i + 1));
                    hongqiu03.get(num).setChoosed(true);
                    redBallsLayouts[2].chooseBall(num);
                }
            }
            if (!"X".equals(nums[3])) {
                int fourthLength = nums[3].length();
                for (int i = 0; i < fourthLength; i++) {
                    int num = Integer.valueOf(nums[3].substring(i, i + 1));
                    hongqiu04.get(num).setChoosed(true);
                    redBallsLayouts[3].chooseBall(num);
                }
            }
            if (!"X".equals(nums[4])) {
                int fifthLength = nums[4].length();
                for (int i = 0; i < fifthLength; i++) {
                    int num = Integer.valueOf(nums[4].substring(i, i + 1));
                    hongqiu05.get(num).setChoosed(true);
                    redBallsLayouts[4].chooseBall(num);
                }
            }
        }
        else if (lotteryType == YXZX) {
            String[] nums = lotteryMode[0].split(",");
            int fifthLength = nums[0].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayouts[4].chooseBall(num);
            }
        }
        else if (lotteryType == DXDS) {
            String[] nums = lotteryMode[0].split(",");
            int sixthLength = nums[0].length();
            for (int i = 0; i < sixthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu06.get(num - 1).setChoosed(true);
                redBallsLayouts[5].chooseBall(num - 1);
            }
            int seventhLength = nums[1].length();
            for (int i = 0; i < seventhLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu07.get(num - 1).setChoosed(true);
                redBallsLayouts[6].chooseBall(num - 1);
            }
        }
        onBallClickInf(-1, -1);
    }

    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
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

    private String getBallsBetInf() {
        if (lotteryType == 1)
            return getBallsBetFirstKindInf();
        else if (lotteryType == 2)
            return getBallsBetSecondKindInf();
        else if (lotteryType == 3)
            return getBallsBetThirdKindInf();
        else if (lotteryType == 4)
            return getBallsBetForthKindInf();
        else if (lotteryType == 5)
            return getBallsBetFifthKindInf();
        else if (lotteryType == 6)
            return getBallsBetSixthKindInf();
        else if (lotteryType == 7)
            return getBallsBetSeventhKindInf();
        else if (lotteryType == 8)
            return getBallsBetEighthKindInf();
        else if (lotteryType == 9)
            return getBallsBetNinthKindInf();
        else if (lotteryType == 10)
            return getBallsBetTenthKindInf();
        else if (lotteryType == 11)
            return getBallsBetEleventhKindInf();
        else
            return null;
    }

    // 投注格式
    private String getBallsBetFirstKindInf() {// 五星通选
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
        betBallText.append(",");
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
// betBallText.append("511:");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1) {
            betBallText.append("512:");
        }
        else {
            betBallText.append("511:");
        }
        return betBallText.toString();
    }

    private String getBallsBetSecondKindInf() {// 五星直选
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
        betBallText.append(",");
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1) {
            betBallText.append("502:");
        }
        else {
            betBallText.append("501:");
        }
        return betBallText.toString();
    }

    private String getBallsBetThirdKindInf() {// 四星直选
        StringBuilder betBallText = new StringBuilder();
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
        betBallText.append(",");
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 || hongqiuInf04.getCount() > 1 ||
            hongqiuInf05.getCount() > 1) {
            betBallText.append("402:");
        }
        else {
            betBallText.append("401:");
        }
        return betBallText.toString();
    }

    private String getBallsBetForthKindInf() {// 三星组六
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
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf01.getCount() > 3) {
            betBallText.append("362:");
        }
        else {
            betBallText.append("361:");
        }
        return betBallText.toString();
    }

    private String getBallsBetFifthKindInf() {// 三星组三
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
        betBallText.append(":" + betMoney + ":");
        betBallText.append("332:");
        return betBallText.toString();
    }

    private String getBallsBetSixthKindInf() {// 三星直选
        StringBuilder betBallText = new StringBuilder();
        int hongLength03 = hongqiu03.size();
        for (int i = 0; i < hongLength03; i++) {
            if (hongqiu03.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf03.getCount() > 1 || hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1) {
            betBallText.append("302:");// 三星复式
        }
        else {
            betBallText.append("301:");// 三星单式
        }
        return betBallText.toString();
    }

    private String getBallsBetSeventhKindInf() {// 二星单复式
        StringBuilder betBallText = new StringBuilder();
        int hongLength04 = hongqiu04.size();
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed())
                betBallText.append(i);
        }
        betBallText.append(",");
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1) {
            betBallText.append("202:");// 二星复式
        }
        else {
            betBallText.append("201:");// 二星单式
        }
        return betBallText.toString();
    }

    private String getBallsBetEighthKindInf() {// 任选二
        StringBuilder betBallText = new StringBuilder();

        int hongLength01 = hongqiu01.size();
        boolean ifAddX = true;
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength02 = hongqiu02.size();
        ifAddX = true;
        for (int i = 0; i < hongLength02; i++) {
            if (hongqiu02.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength03 = hongqiu03.size();
        ifAddX = true;
        for (int i = 0; i < hongLength03; i++) {
            if (hongqiu03.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength04 = hongqiu04.size();
        ifAddX = true;
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength05 = hongqiu05.size();
        ifAddX = true;
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf01.getCount() > 1 ||
            hongqiuInf02.getCount() > 1 ||
            hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 ||
            hongqiuInf05.getCount() > 1 ||
            (hongqiuInf01.getCount() + hongqiuInf02.getCount() + hongqiuInf03.getCount() +
                hongqiuInf04.getCount() + hongqiuInf05.getCount() > 2)) {
            betBallText.append("912:");
        }
        else {
            betBallText.append("911:");
        }
        return betBallText.toString();
    }

    private String getBallsBetNinthKindInf() {// 一星直选
        StringBuilder betBallText = new StringBuilder();
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf05.getCount() > 1) {
            betBallText.append("102:");
        }
        else {
            betBallText.append("101:");
        }
        return betBallText.toString();
    }

    private String getBallsBetTenthKindInf() {// 任选一
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        boolean ifAddX = true;
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength02 = hongqiu02.size();
        ifAddX = true;
        for (int i = 0; i < hongLength02; i++) {
            if (hongqiu02.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength03 = hongqiu03.size();
        ifAddX = true;
        for (int i = 0; i < hongLength03; i++) {
            if (hongqiu03.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength04 = hongqiu04.size();
        ifAddX = true;
        for (int i = 0; i < hongLength04; i++) {
            if (hongqiu04.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        betBallText.append(",");

        int hongLength05 = hongqiu05.size();
        ifAddX = true;
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed()) {
                betBallText.append(i);
                ifAddX = false;
            }
        }
        if (ifAddX == true) {
            betBallText.append("X");
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf01.getCount() > 1 ||
            hongqiuInf02.getCount() > 1 ||
            hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 ||
            hongqiuInf05.getCount() > 1 ||
            (hongqiuInf01.getCount() + hongqiuInf02.getCount() + hongqiuInf03.getCount() +
                hongqiuInf04.getCount() + hongqiuInf05.getCount() > 1)) {
            betBallText.append("902:");
        }
        else {
            betBallText.append("901:");
        }
        return betBallText.toString();
    }

    private String getBallsBetEleventhKindInf() {// 大小单双
        StringBuilder betBallText = new StringBuilder();
        int hongLength06 = hongqiu06.size();
        for (int i = 0; i < hongLength06; i++) {
            if (hongqiu06.get(i).isChoosed())
                betBallText.append(i + 1);
        }
        betBallText.append(",");
        int hongLength07 = hongqiu07.size();
        for (int i = 0; i < hongLength07; i++) {
            if (hongqiu07.get(i).isChoosed())
                betBallText.append(i + 1);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        betBallText.append("701:");
        return betBallText.toString();
    }

    private String getBallsDisplayInf() {
// GetLotteryDisplayCode gldc = new GetLotteryDisplayCode();
        if (lotteryType == 1)
            return getBallsDisplayFirstKindInf();
        else if (lotteryType == 2)
            return getBallsDisplaySecondKindInf();
        else if (lotteryType == 3)
            return getBallsDisplayThirdKindInf();
        else if (lotteryType == 4)
            return getBallsDisplayForthKindInf();
        else if (lotteryType == 5)
            return getBallsDisplayFifthKindInf();
        else if (lotteryType == 6)
            return getBallsDisplaySixthKindInf();
        else if (lotteryType == 7)
            return getBallsDisplaySeventhKindInf();
        else if (lotteryType == 8)
            return getBallsDisplayEighthKindInf();
        else if (lotteryType == 9)
            return getBallsDisplayNinthKindInf();
        else if (lotteryType == 10)
            return getBallsDisplayTenthKindInf();
        else if (lotteryType == 11)
            return getBallsDisplayEleventhKindInf();
        else
            return null;
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[五星通选] ");
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
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
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
        betBallText.append("[五星直选] ");
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
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayThirdKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[四星直选] ");
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
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星组六] ");
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

    private String getBallsDisplayFifthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星组三] ");
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

    private String getBallsDisplaySixthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星直选] ");
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
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二星直选] ");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEighthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选二] ");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("X");
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
            betBallText.append("X");
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
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayNinthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[一星直选] ");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTenthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选一] ");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("X");
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
            betBallText.append("X");
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
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength04 = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength04; i++) {
                if (hongqiu04.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength05 = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("X");
        }
        else {
            for (int i = 0; i < hongLength05; i++) {
                if (hongqiu05.get(i).isChoosed())
                    betBallText.append(i + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEleventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[大小单双] ");
        int hongLength06 = hongqiu06.size();
        if (hongqiuInf06.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength06; i++) {
                if (hongqiu06.get(i).isChoosed())
                    betBallText.append(LotteryUtils.BALL_NAME[i] + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength07 = hongqiu07.size();
        if (hongqiuInf07.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength07; i++) {
                if (hongqiu07.get(i).isChoosed())
                    betBallText.append(LotteryUtils.BALL_NAME[i] + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    int first, third;

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == WXTX) {
            if (hongqiuInf01.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "万位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == WXZX) {
            if (hongqiuInf01.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "万位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == SIXZX) {
            if (hongqiuInf02.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == SXZL) {
            if (hongqiuInf01.getCount() < 3)
                inf = CQSSC_TIPS04;
        }
        else if (lotteryType == SXZS) {
            if (hongqiuInf01.getCount() < 2)
                inf = CQSSC_TIPS05;
        }
        else if (lotteryType == SXZX) {
            if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == EXZX) {
            if (hongqiuInf04.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        // TODO
        else if (lotteryType == RXER) {

        }
        else if (lotteryType == YXZX) {
            if (hongqiuInf05.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        // TODO
        else if (lotteryType == RXY) {

        }
        else if (lotteryType == DXDS) {
            if (hongqiuInf06.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf07.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    private void checkZuSan() {
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                first = i;
            }
        }
        int hongLength03 = hongqiu03.size();
        for (int i = 0; i < hongLength03; i++) {
            if (hongqiu03.get(i).isChoosed()) {
                third = i;
            }
        }
    }

// 机选
    @Override
    public void randomBalls() {
        clearBalls();
        String betInf = null;
        for (int i = 0; i < 11; i++) {
            if (lotteryType == i + 1) {
                if (lotteryType == WXTX) {
                    betInf = "1注     <font color='red'>2元</font>";
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "30" +
                        "</font>至" + "<font color=\"#CD2626\">" + getReword(lotteryType) +
                        "</font>元,您将盈利<font color=\"#CD2626\">" + "28" + "</font>至" +
                        "<font color=\"#CD2626\">" + (getReword(lotteryType) - 2) + "</font>元"));
                }
                else if (lotteryType == SIXZX) {
                    betInf = "1注     <font color='red'>2元</font>";
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "88" +
                        "</font>至" + "<font color=\"#CD2626\">" + getReword(lotteryType) +
                        "</font>元,您将盈利<font color=\"#CD2626\">" + "86" + "</font>至" +
                        "<font color=\"#CD2626\">" + (getReword(lotteryType) - 2) + "</font>元"));
                }
                else if (lotteryType == SXZS) {
                    betInf = "2注     <font color='red'>4元</font>";
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - 4) + "</font>元"));
                }
                else {
                    betInf = "1注     <font color='red'>2元</font>";
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - 2) + "</font>元"));
                }
                break;
            }
        }
        moneyInf.setText(Html.fromHtml(betInf));

        Random rd = new Random();
        int num01 = rd.nextInt(CQSSCActivity.CQSSC_HONGQIU_LENGTH);
        int num02 = rd.nextInt(CQSSCActivity.CQSSC_HONGQIU_LENGTH);
        int num03 = rd.nextInt(CQSSCActivity.CQSSC_HONGQIU_LENGTH);
        int num04 = rd.nextInt(CQSSCActivity.CQSSC_HONGQIU_LENGTH);
        int num05 = rd.nextInt(CQSSCActivity.CQSSC_HONGQIU_LENGTH);
        int num06 = rd.nextInt(4);
        int num07 = rd.nextInt(4);
        if (lotteryType == WXTX) {
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayouts[0].chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayouts[1].chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayouts[2].chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayouts[3].chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == WXZX) {
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayouts[0].chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayouts[1].chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayouts[2].chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayouts[3].chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == SIXZX) {
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayouts[1].chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayouts[2].chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayouts[3].chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == SXZL) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(3, CQSSCActivity.CQSSC_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayouts[0].chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == SXZS) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, CQSSCActivity.CQSSC_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayouts[0].chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == SXZX) {
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayouts[2].chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayouts[3].chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == EXZX) {
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayouts[3].chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == RXER) {
            int[] temp = MathUtil.getRandomNumNotEquals(2, 5);
            redBallsLayouts[temp[0]].chooseBall(num01);
            redBallsLayouts[temp[1]].chooseBall(num02);
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == 0) {
                    hongqiu01.get(i == 0 ? num01 : num02).setChoosed(true);
                }
                else if (temp[i] == 1) {
                    hongqiu02.get(i == 0 ? num01 : num02).setChoosed(true);
                }
                else if (temp[i] == 2) {
                    hongqiu03.get(i == 0 ? num01 : num02).setChoosed(true);
                }
                else if (temp[i] == 3) {
                    hongqiu04.get(i == 0 ? num01 : num02).setChoosed(true);
                }
                else if (temp[i] == 4) {
                    hongqiu05.get(i == 0 ? num01 : num02).setChoosed(true);
                }
            }
        }
        else if (lotteryType == YXZX) {
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayouts[4].chooseBall(num05);
        }
        else if (lotteryType == RXY) {
            int[] temp = MathUtil.getRandomNumNotEquals(1, 5);
            redBallsLayouts[temp[0]].chooseBall(num01);
            if (temp[0] == 0) {
                hongqiu01.get(num01).setChoosed(true);
            }
            else if (temp[0] == 1) {
                hongqiu02.get(num01).setChoosed(true);
            }
            else if (temp[0] == 2) {
                hongqiu03.get(num01).setChoosed(true);
            }
            else if (temp[0] == 3) {
                hongqiu04.get(num01).setChoosed(true);
            }
            else if (temp[0] == 4) {
                hongqiu05.get(num01).setChoosed(true);
            }
        }
        else if (lotteryType == DXDS) {
            hongqiu06.get(num06).setChoosed(true);
            redBallsLayouts[5].chooseBall(num06);
            hongqiu07.get(num07).setChoosed(true);
            redBallsLayouts[6].chooseBall(num07);
        }
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        if (lotteryType == SXZS) {
            betMoney = 4;
        }
        else
            betMoney = 2;
        invalidateAll();
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        if (lotteryType == WXTX || lotteryType == WXZX || lotteryType == RXER || lotteryType == RXY) {
            redBallsLayouts[0].resetBalls();
            redBallsLayouts[1].resetBalls();
            redBallsLayouts[2].resetBalls();
            redBallsLayouts[3].resetBalls();
            redBallsLayouts[4].resetBalls();
        }
        else if (lotteryType == SIXZX) {
            redBallsLayouts[1].resetBalls();
            redBallsLayouts[2].resetBalls();
            redBallsLayouts[3].resetBalls();
            redBallsLayouts[4].resetBalls();
        }
        else if (lotteryType == SXZL || lotteryType == SXZS) {
            redBallsLayouts[0].resetBalls();
        }
        else if (lotteryType == SXZX) {
            redBallsLayouts[2].resetBalls();
            redBallsLayouts[3].resetBalls();
            redBallsLayouts[4].resetBalls();
        }
        else if (lotteryType == EXZX) {
            redBallsLayouts[3].resetBalls();
            redBallsLayouts[4].resetBalls();
        }
        else if (lotteryType == YXZX) {
            redBallsLayouts[4].resetBalls();
        }
        else if (lotteryType == DXDS) {
            redBallsLayouts[5].resetBalls();
            redBallsLayouts[6].resetBalls();
        }
        resetInf();
    }

    // TODO
    protected void resetInf() {
        super.resetInf();
        initLotteryIntroduce();
// analyseTips.setVisibility(View.GONE);
        showBallNum();
        if (lotteryType == 1)
            selectInfo.setText(CQSSC_TIPS01);
        else if (lotteryType == 2)
            selectInfo.setText(CQSSC_TIPS02);
        else if (lotteryType == 3)
            selectInfo.setText(CQSSC_TIPS03);
        else if (lotteryType == 4)
            selectInfo.setText(CQSSC_TIPS04);
        else if (lotteryType == 5)
            selectInfo.setText(CQSSC_TIPS05);
        else if (lotteryType == 6)
            selectInfo.setText(CQSSC_TIPS06);
        else if (lotteryType == 7)
            selectInfo.setText(CQSSC_TIPS07);
        else if (lotteryType == 8)
            selectInfo.setText(CQSSC_TIPS08);
        else if (lotteryType == 9)
            selectInfo.setText(CQSSC_TIPS09);
        else if (lotteryType == 10)
            selectInfo.setText(CQSSC_TIPS10);
        else if (lotteryType == 11)
            selectInfo.setText(CQSSC_TIPS010);
    }

    public void initLotteryIntroduce() {
// lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "-"
// + "</font>元,您将盈利<font color=\"#CD2626\">" + "-" + "</font>元"));
    }

    // TODO
    @Override
    public void onBallClickFull(int ballType) {
        if (ballType == 1) {
            if (lotteryType != 7 && lotteryType != 4 && lotteryType != 9) {
                ViewUtil.showTipsToast(this, "您只能选" + CQSSCActivity.CQSSC_HONGQIU_LIMIT + "个红球");
            }
            else if (lotteryType == 7) {
                ViewUtil.showTipsToast(this, "您只能选" + 7 + "个红球");
            }
            else if (lotteryType == 4) {
                ViewUtil.showTipsToast(this, "您只能选" + 8 + "个红球");
            }
            else if (lotteryType == 9) {
                ViewUtil.showTipsToast(this, "您只能选" + 5 + "个红球");
            }
        }
        else if (ballType == 2) {
            ViewUtil.showTipsToast(this, "您只能选" + CQSSCActivity.CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 3) {
            ViewUtil.showTipsToast(this, "您只能选" + CQSSCActivity.CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 4) {
            ViewUtil.showTipsToast(this, "您只能选" + CQSSCActivity.CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 5) {
            if (lotteryType != 9) {
                ViewUtil.showTipsToast(this, "您只能选" + CQSSCActivity.CQSSC_HONGQIU_LIMIT + "个红球");
            }
            else {
                ViewUtil.showTipsToast(this, "您只能选" + 5 + "个红球");
            }
        }
        else if (ballType == 6) {
            ViewUtil.showTipsToast(this, "您只能选" + 1 + "个红球");
        }
        else if (ballType == 7) {
            ViewUtil.showTipsToast(this, "您只能选" + 1 + "个红球");
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
        int[] hongNumber = new int[7];
        hongNumber[0] = hongqiuInf01.getCount();
        hongNumber[1] = hongqiuInf02.getCount();
        hongNumber[2] = hongqiuInf03.getCount();
        hongNumber[3] = hongqiuInf04.getCount();
        hongNumber[4] = hongqiuInf05.getCount();
        hongNumber[5] = hongqiuInf06.getCount();
        hongNumber[6] = hongqiuInf07.getCount();
        if ((hongNumber[0] == 0 && hongNumber[1] == 0 && hongNumber[2] == 0 && hongNumber[3] == 0 &&
            hongNumber[4] == 0 && hongNumber[5] == 0 && hongNumber[6] == 0) == false) {
            enableClearBtn();
        }
        if (lotteryType == WXTX) {
            refreshMoney = ballClickWxtx(refreshMoney, hongNumber);
        }
        else if (lotteryType == WXZX) {
            refreshMoney = ballClickWxtx(refreshMoney, hongNumber);
        }
        else if (lotteryType == SIXZX) {
            refreshMoney = ballClickSixzx(refreshMoney, hongNumber);
        }
        else if (lotteryType == SXZL) {
            refreshMoney = ballClickSxzl(refreshMoney, hongNumber);
        }
        else if (lotteryType == SXZS) {
            refreshMoney = ballClickSxzs(refreshMoney, hongNumber);
        }
        else if (lotteryType == SXZX) {
            refreshMoney = ballClickSxzx(refreshMoney, hongNumber);
        }
        else if (lotteryType == EXZX) {
            refreshMoney = ballClickExzx(refreshMoney, hongNumber);
        }
        else if (lotteryType == RXER) {
            refreshMoney = ballClickRxer(refreshMoney, hongNumber);
        }
        else if (lotteryType == YXZX) {
            refreshMoney = ballClickYxzx(refreshMoney, hongNumber);
        }
        else if (lotteryType == RXY) {
            refreshMoney = ballClickRxy(refreshMoney, hongNumber);
        }
        else if (lotteryType == DXDS) {
            refreshMoney = ballClickDxds(ballType, index, refreshMoney, hongNumber);
        }
        checkBet(betMoney);
        if (refreshMoney) {
            invalidateAll();
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null) {
                moneyInf.setText(Html.fromHtml(betInf));

                if (lotteryType == WXTX) {
                    int qianErNum = hongNumber[2] * hongNumber[3] * hongNumber[4];
                    int houErNum = hongNumber[0] * hongNumber[1] * hongNumber[2];
                    int minMoney = Math.min(qianErNum, houErNum) * 30;
                    int qianSanNum = hongNumber[3] * hongNumber[4];
                    int houSanNum = hongNumber[0] * hongNumber[1];
                    int maxMoney = (qianErNum + houErNum) * 30 + (qianSanNum + houSanNum) * 200 + 20000;

                    boolean[] b = check(minMoney, maxMoney);
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + minMoney +
                        "</font>至" + "<font color=\"#CD2626\">" + maxMoney + "</font>元,您将盈利<font color=\"" +
                        getColor(b, 0) + "\">" + (minMoney - betMoney) + "</font>至" + "<font color=\"" +
                        getColor(b, 1) + "\">" + (maxMoney - betMoney) + "</font>元"));
                }
                else if (lotteryType == SIXZX) {
                    int minMoney = Math.min(hongNumber[1], hongNumber[4]) * 88;
                    int maxMoney = (hongNumber[1] + hongNumber[4] - 2) * 88 + 10000;
                    boolean[] b = check(minMoney, maxMoney);
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + minMoney +
                        "</font>至" + "<font color=\"#CD2626\">" + maxMoney + "</font>元,您将盈利<font color=\"" +
                        getColor(b, 0) + "\">" + (minMoney - betMoney) + "</font>至" + "<font color=\"" +
                        getColor(b, 1) + "\">" + (maxMoney - betMoney) + "</font>元"));
                }
                else if (lotteryType == RXER) {
                    int j = 0;
                    for (int i = 0; i < 5; i++) {
                        if (hongNumber[i] > 0) {
                            j++;
                        }
                    }
                    if (j > 2) {
                        int minMoney = getReword(lotteryType);
                        int maxMoney = (int) (MathUtil.combination(j, 2) * 116);
                        boolean[] b = check(minMoney, maxMoney);
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + minMoney +
                            "</font>至" + "<font color=\"#CD2626\">" + maxMoney +
                            "</font>元,您将盈利<font color=\"" + getColor(b, 0) + "\">" + (minMoney - betMoney) +
                            "</font>至" + "<font color=\"" + getColor(b, 1) + "\">" + (maxMoney - betMoney) +
                            "</font>元"));
                    }
                    else {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"" +
                            getColor(getReword(lotteryType), betMoney) + "\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                }
                else if (lotteryType == RXY) {
                    int j = 0;
                    for (int i = 0; i < 5; i++) {
                        if (hongNumber[i] > 0) {
                            j++;
                        }
                    }
                    if (j > 1) {
                        int minMoney = getReword(lotteryType);
                        int maxMoney = (int) (j * 11);
                        boolean[] b = check(minMoney, maxMoney);
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + minMoney +
                            "</font>至" + "<font color=\"#CD2626\">" + maxMoney +
                            "</font>元,您将盈利<font color=\"" + getColor(b, 0) + "\">" + (minMoney - betMoney) +
                            "</font>至" + "<font color=\"" + getColor(b, 1) + "\">" + (maxMoney - betMoney) +
                            "</font>元"));
                    }
                    else {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"" +
                            getColor(getReword(lotteryType), betMoney) + "\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                }
                else {
                    if (getReword(lotteryType) - betMoney < 0) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#1874CD\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                }

            }
        }
        else {
            initLotteryIntroduce();
        }
    }

    public Boolean ballClickDxds(int ballType, int index, Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[5] == 0 && hongNumber[6] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[5] == 0 || hongNumber[6] == 0) {
                if (ballType == 6) {
                    for (int i = 0; i < 4; i++) {
                        if (i != index) {
                            if (hongqiu06.get(i).isChoosed() == true) {
                                hongqiu06.get(i).setChoosed(false);
                                hongqiuInf06.setCount(1);
                            }
                        }
                    }
                    redBallsLayouts[5].refreshAllBall();
                }
                else if (ballType == 7) {
                    for (int i = 0; i < 4; i++) {
                        if (i != index) {
                            if (hongqiu07.get(i).isChoosed() == true) {
                                hongqiu07.get(i).setChoosed(false);
                                hongqiuInf07.setCount(1);
                            }
                        }
                    }
                    redBallsLayouts[6].refreshAllBall();
                }
                invalidateNum();
            }
            else {
                if (ballType == 6) {
                    for (int i = 0; i < 4; i++) {
                        if (i != index) {
                            if (hongqiu06.get(i).isChoosed() == true) {
                                hongqiu06.get(i).setChoosed(false);
                                hongqiuInf06.setCount(1);
                            }
                        }
                    }
                    redBallsLayouts[5].refreshAllBall();
                }
                else if (ballType == 7) {
                    for (int i = 0; i < 4; i++) {
                        if (i != index) {
                            if (hongqiu07.get(i).isChoosed() == true) {
                                hongqiu07.get(i).setChoosed(false);
                                hongqiuInf07.setCount(1);
                            }
                        }
                    }
                    redBallsLayouts[6].refreshAllBall();
                }
                refreshMoney = true;
                betNumber = hongqiuInf06.getCount() * hongqiuInf07.getCount();
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickRxy(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[0] == 0 && hongNumber[1] == 0 && hongNumber[2] == 0 && hongNumber[3] == 0 &&
            hongNumber[4] == 0) {
            resetInf();
        }
        else {
            int j = 0;
            int numAll = 0;
            for (int i = 0; i < 5; i++) {
                numAll += hongNumber[i];
                if (hongNumber[i] > 0) {
                    j++;
                }
            }
            if (j < 1) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = numAll;
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickYxzx(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[4] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[4] == 0) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = hongqiuInf05.getCount();
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickRxer(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[0] == 0 && hongNumber[1] == 0 && hongNumber[2] == 0 && hongNumber[3] == 0 &&
            hongNumber[4] == 0) {
            resetInf();
        }
        else {
            int j = 0;
            int numAll = 0;
            for (int i = 0; i < 5; i++) {
                numAll += hongNumber[i];
                if (hongNumber[i] > 0) {
                    j++;
                }
            }
            if (j < 2) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = MathUtil.combination(numAll, 2);
                for (int i = 0; i < 5; i++) {
                    if (hongNumber[i] > 1) {
                        betNumber -= MathUtil.combination(hongNumber[i], 2);
                    }
                }
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickExzx(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[3] == 0 && hongNumber[4] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[3] == 0 || hongNumber[4] == 0) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = hongNumber[3] * hongNumber[4];
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickSxzx(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[2] == 0 && hongNumber[3] == 0 && hongNumber[4] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[2] == 0 || hongNumber[3] == 0 || hongNumber[4] == 0) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = hongNumber[2] * hongNumber[3] * hongNumber[4];
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickSxzs(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[0] >= 2) {
            refreshMoney = true;
            betNumber = MathUtil.factorial(hongNumber[0], 2);
            betMoney = betNumber * 2 * 1;
            invalidateAll();
        }
        else if (hongNumber[0] > 0)
            invalidateNum();
        else
            resetInf();
        return refreshMoney;
    }

    public Boolean ballClickSxzl(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[0] >= 3) {
            refreshMoney = true;
            betNumber = (int) (MathUtil.factorial(hongNumber[0], 3) / MathUtil.factorial(3, 3));
            betMoney = betNumber * 2 * 1;
            invalidateAll();
        }
        else if (hongNumber[0] > 0)
            invalidateNum();
        else {
            resetInf();
        }
        return refreshMoney;
    }

    public Boolean ballClickSixzx(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[1] == 0 && hongNumber[2] == 0 && hongNumber[3] == 0 && hongNumber[4] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[1] == 0 || hongNumber[2] == 0 || hongNumber[3] == 0 || hongNumber[4] == 0) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = hongNumber[1] * hongNumber[2] * hongNumber[3] * hongNumber[4];
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    public Boolean ballClickWxtx(Boolean refreshMoney, int[] hongNumber) {
        if (hongNumber[0] == 0 && hongNumber[1] == 0 && hongNumber[2] == 0 && hongNumber[3] == 0 &&
            hongNumber[4] == 0) {
            resetInf();
        }
        else {
            if (hongNumber[0] == 0 || hongNumber[1] == 0 || hongNumber[2] == 0 || hongNumber[3] == 0 ||
                hongNumber[4] == 0) {
                invalidateNum();
            }
            else {
                refreshMoney = true;
                betNumber = hongNumber[0] * hongNumber[1] * hongNumber[2] * hongNumber[3] * hongNumber[4];
                betMoney = betNumber * 2 * 1;
            }
        }
        return refreshMoney;
    }

    private String getColor(int reword, long betMoney) {
        if (reword > betMoney) {
            return "#CD2626";
        }
        else
            return "#1874CD";
    }

    public boolean[] check(int minMoney, int maxMoney) {
        boolean[] b = new boolean[3];
        for (int i = 0; i < b.length; i++) {
            b[i] = false;
        }
        if (minMoney - betMoney > 0) {
            b[0] = true;
        }
        else if ((minMoney - betMoney < 0) && (maxMoney - betMoney > 0)) {
            b[1] = true;
        }
        else if (maxMoney - betMoney < 0) {
            b[2] = true;
        }
        return b;
    }

    private String getColor(boolean[] b, int flag) {
        if (b[0])
            return "#CD2626";
        else if (b[1]) {
            if (flag == 0)
                return "#1874CD";
            else
                return "#CD2626";
        }
        else if (b[2])
            return "#1874CD";
        else
            return "#CD2626";
    }

    private int getReword(int lotteryType) {
        return reword[lotteryType - 1];
    }

    private int findSameNum() {
        int n = 0;
        for (int i = 0; i < CQSSCActivity.CQSSC_HONGQIU_LENGTH; i++) {
            Boolean first = hongqiu01.get(i).isChoosed();
            Boolean second = hongqiu03.get(i).isChoosed();
            if (first && second)
                n = n + 1;
        }
        return n;
    }

    protected void showBallNum() {
        if (lotteryType == WXTX || lotteryType == WXZX || lotteryType == RXER || lotteryType == RXY) {
// choosingCount[0].setText("万位：" + hongqiuInf01.getCount() + "/1个");
// choosingCount[1].setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCount[2].setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCount[3].setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount[4].setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu01.setText("万位");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == SIXZX) {
// choosingCount[1].setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCount[2].setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCount[3].setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount[4].setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == SXZL) {
// choosingCount[0].setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == SXZS) {
            choosingCount[0].setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == SXZX) {
// choosingCount[2].setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCount[3].setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount[4].setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == EXZX) {
// choosingCount[3].setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount[4].setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == YXZX) {
// choosingCount[4].setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == DXDS) {
// choosingCount[5].setText("十位：" + hongqiuInf06.getCount() + "/1个");
// choosingCount[6].setText("个位：" + hongqiuInf07.getCount() + "/1个");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml(displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
// analyseTips.setVisibility(View.VISIBLE);
        code = getBallsBetInf();
        invalidateDisplay();
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "新时时彩游戏规则");
        bundel.putString("lottery_help", "help_new/jxssc.html");
        intent.putExtras(bundel);
        intent.setClass(JXSSCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "新时时彩走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=jxssc&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(JXSSCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    // TODO
    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(JXSSCActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "jxssc");
        if (lotteryType == 1)
            bundle.putString("lotteryTypeWXTX", "1");
        else if (lotteryType == 2)
            bundle.putString("lotteryTypeWXZX", "2");
        else if (lotteryType == 3)
            bundle.putString("lotteryTypeSIXZX", "3");
        else if (lotteryType == 4)
            bundle.putString("lotteryTypeSXZL", "4");
        else if (lotteryType == 5)
            bundle.putString("lotteryTypeSXZS", "5");
        else if (lotteryType == 6)
            bundle.putString("lotteryTypeSXZX", "6");
        else if (lotteryType == 7)
            bundle.putString("lotteryTypeEXZX", "7");
        else if (lotteryType == 8)
            bundle.putString("lotteryTypeRXER", "8");
        else if (lotteryType == 9)
            bundle.putString("lotteryTypeYXZX", "9");
        else if (lotteryType == 10)
            bundle.putString("lotteryTypeDXDS", "10");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
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
                            analyseHundred[i] =
                                SEARCHNUM[searchType - 1] + "期内冷门，开出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseHundred[i] =
                                SEARCHNUM[searchType - 1] + "期内热门，开出<font color='red'>" + count + "</font>次";
                        else
                            analyseHundred[i] = SEARCHNUM[searchType - 1] + "期内开出" + count + "次";
                        ((BallTextView) redBallsLayouts[0].getChildAt(i)).setOpenCount(analyseHundred[i]);
                    }
                    for (int i = 0; i < tenLength; i++) {
                        count = Integer.valueOf(analyseTen[i]);
                        if (count < 5)
                            analyseTen[i] =
                                SEARCHNUM[searchType - 1] + "期内冷门，开出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseTen[i] =
                                SEARCHNUM[searchType - 1] + "期内热门，开出<font color='red'>" + count + "</font>次";
                        else
                            analyseTen[i] = SEARCHNUM[searchType - 1] + "期内开出" + count + "次";
                        ((BallTextView) redBallsLayouts[1].getChildAt(i)).setOpenCount(analyseTen[i]);
                    }
                    for (int i = 0; i < unitLength; i++) {
                        count = Integer.valueOf(analyseUnit[i]);
                        if (count < 5)
                            analyseUnit[i] =
                                SEARCHNUM[searchType - 1] + "期内冷门，开出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseUnit[i] =
                                SEARCHNUM[searchType - 1] + "期内热门，开出<font color='red'>" + count + "</font>次";
                        else
                            analyseUnit[i] = SEARCHNUM[searchType - 1] + "期内开出" + count + "次";
                        ((BallTextView) redBallsLayouts[2].getChildAt(i)).setOpenCount(analyseUnit[i]);
                    }
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
                String betInf = "1注     <font color='red'>2元</font>";
                moneyInf.setText(Html.fromHtml(betInf));
                // for (int i = 0; i < 7; i++) {
                if (lotteryType == WXTX) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[0].chooseBall(vertor[0]);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[1].chooseBall(vertor[1]);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayouts[2].chooseBall(vertor[2]);
                    hongqiu04.get(vertor[3]).setChoosed(true);
                    redBallsLayouts[3].chooseBall(vertor[3]);
                    hongqiu05.get(vertor[4]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[4]);
                }
                else if (lotteryType == 2) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[0].chooseBall(vertor[0]);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[1].chooseBall(vertor[1]);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayouts[2].chooseBall(vertor[2]);
                    hongqiu04.get(vertor[3]).setChoosed(true);
                    redBallsLayouts[3].chooseBall(vertor[3]);
                    hongqiu05.get(vertor[4]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[4]);
                }
                else if (lotteryType == 3) {
                    hongqiu02.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[1].chooseBall(vertor[0]);
                    hongqiu03.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[2].chooseBall(vertor[1]);
                    hongqiu04.get(vertor[2]).setChoosed(true);
                    redBallsLayouts[3].chooseBall(vertor[2]);
                    hongqiu05.get(vertor[3]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[3]);
                }
                else if (lotteryType == 4) {
                    for (int i = 0; i < 3; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayouts[0].chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 5) {
                    for (int i = 0; i < 2; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayouts[0].chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 6) {
                    hongqiu03.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[2].chooseBall(vertor[0]);
                    hongqiu04.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[3].chooseBall(vertor[1]);
                    hongqiu05.get(vertor[2]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[2]);
                }
                else if (lotteryType == 8) {
                    hongqiu04.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[3].chooseBall(vertor[0]);
                    hongqiu05.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[1]);
                }
                else if (lotteryType == 9) {
                    hongqiu05.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[4].chooseBall(vertor[0]);
                }
                else if (lotteryType == 10) {
                    hongqiu06.get(vertor[0]).setChoosed(true);
                    redBallsLayouts[5].chooseBall(vertor[0]);
                    hongqiu07.get(vertor[1]).setChoosed(true);
                    redBallsLayouts[6].chooseBall(vertor[1]);
                }
                // }
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

    @Override
    public String getQ_code() {
        jxsscNum01 = 0;
        jxsscNum02 = 0;
        jxsscNum03 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                jxsscNum01 = jxsscNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                jxsscNum02 = jxsscNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                jxsscNum03 = jxsscNum03 + 1;
            }
        }
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int jxsscNum01;
    private int jxsscNum02;
    private int jxsscNum03;

    @Override
    protected void searchLuckyNum() {

        if (getQ_code().equals(",,")) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (jxsscNum01 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理百位上1个红球");
            }
            else if (jxsscNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理十位上1个红球");
            }
            else if (jxsscNum03 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理个位上1个红球");
            }
            else {
                // requestCode = q_codeSwitch(getQ_code());
                String str = getQ_code();
                str = str.replace("-,", "");
                str = str.replace(",-", "");
                str = str.replace("-", "");
                requestCode = q_codeSwitch(str);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "jxssc");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(JXSSCActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        lotteryType = nsscWayMap.get(tabName);
// if (tabName.equals("五星通选") || tabName.equals("五星直选"))
        index_num = lotteryType - 1;
// else
// index_num = lotteryType - 2;

        initExzxFullBall(lotteryType);
        databaseData.putString("jxssc_way", LotteryUtils.JXSSCWay[lotteryType - 1]);
        databaseData.commit();
        clearBalls();
        title.setText(tabName);
        showWay();
        showBallNum();
        disableBetBtn();

        titlePopup.dismiss();
    }

    private void initExzxFullBall(int lotType) {
        if (lotType == 1 || lotType == 2 || lotType == 5 || lotType == 6 || lotType == 8)
            exzxFullBall(10);
        else if (lotType == 4)
            exzxFullBall(8);
        else if (lotType == 7)
            exzxFullBall(7);
        else if (lotType == 9)
            exzxFullBall(5);
        else if (lotType == 10)
            exzxFullBall(4);
    }
}
