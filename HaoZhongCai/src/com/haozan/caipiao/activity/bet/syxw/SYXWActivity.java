package com.haozan.caipiao.activity.bet.syxw;

import java.util.ArrayList;
import java.util.Random;

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
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.adapter.unite.CommissionAdapter;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;
import com.haozan.caipiao.widget.PopMenu;
import com.haozan.caipiao.widget.PopMenu.PopMenuButtonClickListener;

public class SYXWActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener, OnItemClickListener {
    private static final String SYXW_TIPS01 = "选2个猜中任2个，奖金6元";
    private static final String SYXW_TIPS02 = "选3个猜中任3个，奖金19元";
    private static final String SYXW_TIPS03 = "选4个猜中任4个，奖金78元";
    private static final String SYXW_TIPS04 = "选5个猜中全部，奖金540元";
    private static final String SYXW_TIPS05 = "选6个猜中全部，奖金90元";
    private static final String SYXW_TIPS06 = "选7个猜中全部，奖金26元";
    private static final String SYXW_TIPS07 = "选8个猜中全部，奖金9元";
    private static final String SYXW_TIPS08 = "猜中第1个开奖号，奖金13元";
    private static final String SYXW_TIPS09 = "顺序猜中前2个开奖号，奖金130元";
    private static final String SYXW_TIPS10 = "猜中前2个，奖金65元";
    private static final String SYXW_TIPS11 = "顺序猜中前3个开奖号，奖金1170元";
    private static final String SYXW_TIPS12 = "猜中前3个开奖号，奖金195元";
    private static final String SYXW_TIPSDANTUO = "胆(认为必出的号))";

    public static final int SYXW_HONGQIU_LENGTH = 11;
    public static final int SYXW_HONGQIU_LIMIT = 11;
    public static final int SYXW_HONGQIU_RXB_LIMIT = 8;

    private static final String[] SYXWTIPSARRAY = {SYXW_TIPS01, SYXW_TIPS02, SYXW_TIPS03, SYXW_TIPS04,
            SYXW_TIPS05, SYXW_TIPS06, SYXW_TIPS07, SYXW_TIPS08, SYXW_TIPS09, SYXW_TIPS10, SYXW_TIPS11,
            SYXW_TIPS12, SYXW_TIPSDANTUO};
    private static String hotCondintion = null;
// private PopupWindow mPopupWindow;
    private String[] redBallTips;
    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
    private NewBetBallsLayout redBallsLayout01;
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
    private NewBetBallsLayout redBallsLayout02;
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
    private NewBetBallsLayout redBallsLayout03;

    private int lotteryType = 1;
    private RelativeLayout termLayout;

    private TextView choosingCountTenThousand;
    private TextView choosingCountThousand;
    private TextView choosingCountHundred;
    private LinearLayout choosingCountTenThousandLinear;
    private LinearLayout choosingCountThousandLinear;
    private LinearLayout choosingCountHundredLinear;

    private TextView lotteryIntroduce;
    private TextView switchButtonNormal;
    private TextView switchButtonDantuo;

    private PopMenu titlePopup;
    private int index_num = 0;
    private int lotteryMethodTypeDT = -1;
    private boolean isNotDantuo = true;
    private String tabName;
    private boolean danTuoReflashMoney;
    private static final int[] reword = {6, 19, 78, 540, 90, 26, 9, 13, 130, 65, 1170, 195};
    private boolean ifLotteryIntroduceShown = false;

    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03, flagLengre01, flagLengre02, flagLengre03,
        flagYilou01, flagYilou02, flagYilou03;;
    private TextView selectInfo;

    @Override
    public void setKind() {
        this.kind = "jx11x5";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.syxw);
        initData();
        setupViews();
        initBetBetShake();
        initSubViews();
        init();
    }

    private void initData() {
        // 屏蔽加单功能
// betWayBt = (Button) this.findViewById(R.id.bet_way_button);
// betWayBt.setTextColor(getResources().getColor(R.color.gray));
// betWay = preferences.getInt("bet_way", 1);
// if (betWay == 2)
// betWay = 1;
// betWayBt.setText("购彩");
//
// databaseData.putInt("bet_way", betWay);
// databaseData.commit();

        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = SYXW_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(SYXW_HONGQIU_LIMIT);
        hongqiuInf01.setBallType(1);
        // init red section two
        hongqiuInf02 = new BetBallsData();
        hongqiu02 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu02.add(ball);
        }
        hongqiuInf02.setBetBalls(hongqiu02);
        hongqiuInf02.setCount(0);
        hongqiuInf02.setColor("red");
        hongqiuInf02.setLimit(SYXW_HONGQIU_LIMIT);
        hongqiuInf02.setBallType(2);
        // init red section three
        hongqiuInf03 = new BetBallsData();
        hongqiu03 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu03.add(ball);
        }
        hongqiuInf03.setBetBalls(hongqiu03);
        hongqiuInf03.setCount(0);
        hongqiuInf03.setColor("red");
        hongqiuInf03.setLimit(SYXW_HONGQIU_LIMIT);
        hongqiuInf03.setBallType(3);
    }

    protected void setupViews() {
        super.setupViews();

        // 隐藏分析工具
// findViewById(R.id.analyse_tips_rala).setVisibility(View.GONE);

        // 屏蔽加单功能
// imgShowBet.setEnabled(false);
        normalToolsLayout.setVisibility(View.GONE);
        numAnalyse.setVisibility(View.GONE);

        lotteryIntroduce = (TextView) this.findViewById(R.id.lottery_introdution);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);

        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.syxw_hongqiu_balls01);
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.syxw_hongqiu_balls02);
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.syxw_hongqiu_balls03);

        choosingCountTenThousandLinear = (LinearLayout) this.findViewById(R.id.syxw_hongqiu01_linear);
        choosingCountThousandLinear = (LinearLayout) this.findViewById(R.id.syxw_hongqiu02_linear);
        choosingCountHundredLinear = (LinearLayout) this.findViewById(R.id.syxw_hongqiu03_linear);
        choosingCountTenThousand = (TextView) this.findViewById(R.id.syxw_hongqiu01_text);
        choosingCountThousand = (TextView) this.findViewById(R.id.syxw_hongqiu02_text);
        choosingCountHundred = (TextView) this.findViewById(R.id.syxw_hongqiu03_text);
        switchButtonNormal = (TextView) this.findViewById(R.id.switch_button_normal);
        switchButtonNormal.setOnClickListener(this);
        switchButtonDantuo = (TextView) this.findViewById(R.id.switch_button_dantuo);
        switchButtonDantuo.setOnClickListener(this);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagLengre01 = (TextView) findViewById(R.id.tv_flag_lengre01);
        flagYilou01 = (TextView) findViewById(R.id.tv_flag_yilou01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagLengre02 = (TextView) findViewById(R.id.tv_flag_lengre02);
        flagYilou02 = (TextView) findViewById(R.id.tv_flag_yilou02);
        flagHongqiu03 = (TextView) findViewById(R.id.tv_flag_hongqiu03);
        flagLengre03 = (TextView) findViewById(R.id.tv_flag_lengre03);
        flagYilou03 = (TextView) findViewById(R.id.tv_flag_yilou03);
        selectInfo = (TextView) findViewById(R.id.select_info);
        showSetting.setVisibility(View.VISIBLE);
        omiRela.setVisibility(View.VISIBLE);
        hotnumRela.setVisibility(View.VISIBLE);
        hotGrid.setOnItemClickListener(this);
    }

    private void showPopupViews() {
        titlePopup = new PopMenu(SYXWActivity.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArray, LotteryUtils.moneyArray, 1,
                             findViewById(R.id.top).getMeasuredWidth() - 20, index_num, true, true);
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

    private void initBetBetShake() {
        // dantuo
        isNotDantuo = preferences.getBoolean("isNotDantuo", true);
        if (isNotDantuo) {
            shakeRela.setVisibility(View.VISIBLE);
            shakeLockView.setVisibility(View.VISIBLE);
            shakeLockView.setEnabled(true);

        }
        else {
            shakeRela.setVisibility(View.GONE);
            shakeLockView.setVisibility(View.GONE);
            shakeLockView.setEnabled(false);
        }
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

        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initLotteryIntroduce();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("bet_way");
            if (type != 0) {
                lotteryType = type;
                if (lotteryType == 1) {
                    databaseData.putString("syxw_way", "syxwrx2");
                    lotteryMethodTypeDT = 1;
                    tabName = "任选二";
                }
                else if (lotteryType == 2) {
                    databaseData.putString("syxw_way", "syxwrx3");
                    lotteryMethodTypeDT = 2;
                    tabName = "任选三";
                }
                else if (lotteryType == 3) {
                    databaseData.putString("syxw_way", "syxwrx4");
                    lotteryMethodTypeDT = 3;
                    tabName = "任选四";
                }
                else if (lotteryType == 4) {
                    databaseData.putString("syxw_way", "syxwrx5");
                    lotteryMethodTypeDT = 4;
                    tabName = "任选五";
                }
                else if (lotteryType == 5) {
                    databaseData.putString("syxw_way", "syxwrx6");
                    lotteryMethodTypeDT = 5;
                    tabName = "任选六";
                }
                else if (lotteryType == 6) {
                    databaseData.putString("syxw_way", "syxwrx7");
                    lotteryMethodTypeDT = 6;
                    tabName = "任选七";
                }
                else if (lotteryType == 7) {
                    databaseData.putString("syxw_way", "syxwrx8");
                }
                else if (lotteryType == 8) {
                    databaseData.putString("syxw_way", "syxwqy");
                }
                else if (lotteryType == 9) {
                    databaseData.putString("syxw_way", "syxwqe_zhixuan");
                }
                else if (lotteryType == 10) {
                    databaseData.putString("syxw_way", "syxwqe_zuxuan");
                    lotteryMethodTypeDT = 7;
                    tabName = "组选二";
                }
                else if (lotteryType == 11) {
                    databaseData.putString("syxw_way", "syxwqs_zhixuan");
                }
                else if (lotteryType == 12) {
                    databaseData.putString("syxw_way", "syxwqs_zuxuan");
                    lotteryMethodTypeDT = 8;
                    tabName = "组选三";
                }

                databaseData.commit();
            }
            else {
                resetLotteryType();
            }
        }
        else {
            resetLotteryType();
        }
        if (isNotDantuo) {
            showWay();
            showBallNum();
            if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
                switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
                switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_selected);
                switchButtonDantuo.setTextColor(getResources().getColor(R.color.dark_purple));
                switchButtonNormal.setTextColor(getResources().getColor(R.color.white));
                switchButtonDantuo.setVisibility(View.VISIBLE);
                switchButtonNormal.setVisibility(View.VISIBLE);
            }
            else {
                switchButtonDantuo.setVisibility(View.GONE);
                switchButtonNormal.setVisibility(View.GONE);
            }
        }
        else {
            // dantuo
            if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
                switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_selected);
                switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
                switchButtonNormal.setTextColor(getResources().getColor(R.color.dark_purple));
                switchButtonDantuo.setTextColor(getResources().getColor(R.color.white));
                selectInfo.setText(SYXW_TIPSDANTUO);
                showPlayType();
                showBallTypeNum();
                random.setVisibility(View.INVISIBLE);
                random.setEnabled(false);
                switchButtonDantuo.setVisibility(View.VISIBLE);
                switchButtonNormal.setVisibility(View.VISIBLE);
            }
            else {
                switchButtonDantuo.setVisibility(View.GONE);
                switchButtonNormal.setVisibility(View.GONE);
                showWay();
                showBallNum();
            }
        }
        initInf();
        if (hotCondintion == null)
            getAnalyseData();
        else
            analyseData(hotCondintion);
        lotteryCalculator.setVisibility(View.INVISIBLE);
        index_num = lotteryType - 1;
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("syxw_way", "syxwrx2");
        if (sdWay.equals("syxwrx2")) {
            lotteryType = 1;
            lotteryMethodTypeDT = 1;
            tabName = "任选二";
        }
        else if (sdWay.equals("syxwrx3")) {
            lotteryType = 2;
            lotteryMethodTypeDT = 2;
            tabName = "任选三";
        }
        else if (sdWay.equals("syxwrx4")) {
            lotteryType = 3;
            lotteryMethodTypeDT = 3;
            tabName = "任选四";
        }
        else if (sdWay.equals("syxwrx5")) {
            lotteryType = 4;
            lotteryMethodTypeDT = 4;
            tabName = "任选五";
        }
        else if (sdWay.equals("syxwrx6")) {
            lotteryType = 5;
            lotteryMethodTypeDT = 5;
            tabName = "任选六";
        }
        else if (sdWay.equals("syxwrx7")) {
            lotteryType = 6;
            lotteryMethodTypeDT = 6;
            tabName = "任选七";
        }
        else if (sdWay.equals("syxwrx8")) {
            lotteryType = 7;
        }
        else if (sdWay.equals("syxwqy")) {
            lotteryType = 8;
        }
        else if (sdWay.equals("syxwqe_zhixuan")) {
            lotteryType = 9;
        }
        else if (sdWay.equals("syxwqe_zuxuan")) {
            lotteryType = 10;
            lotteryMethodTypeDT = 7;
            tabName = "组选二";
        }
        else if (sdWay.equals("syxwqs_zhixuan")) {
            lotteryType = 11;
        }
        else if (sdWay.equals("syxwqs_zuxuan")) {
            lotteryType = 12;
            lotteryMethodTypeDT = 8;
            tabName = "组选三";
        }
    }

    private void showWay() {
        selectInfo.setText(SYXWTIPSARRAY[lotteryType - 1]);
        initLotteryIntroduce();
        hongqiuInf01.setLimit(SYXW_HONGQIU_LIMIT);
        if (lotteryType == 1) {
            title.setText("11选5任选二");
            choosingInf.setText(SYXW_TIPS01);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选二";
        }
        else if (lotteryType == 2) {
            title.setText("11选5任选三");
            choosingInf.setText(SYXW_TIPS02);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选三";
        }
        else if (lotteryType == 3) {
            title.setText("11选5任选四");
            choosingInf.setText(SYXW_TIPS03);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选四";
        }
        else if (lotteryType == 4) {
            title.setText("11选5任选五");
            choosingInf.setText(SYXW_TIPS04);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选五";
        }
        else if (lotteryType == 5) {
            title.setText("11选5任选六");
            choosingInf.setText(SYXW_TIPS05);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选六";
        }
        else if (lotteryType == 6) {
            title.setText("11选5任选七");
            choosingInf.setText(SYXW_TIPS06);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "任选七";
        }
        else if (lotteryType == 7) {
            title.setText("11选5任选八");
            hongqiuInf01.setLimit(SYXW_HONGQIU_RXB_LIMIT);
            choosingInf.setText(SYXW_TIPS07);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
        }
        else if (lotteryType == 8) {
            title.setText("11选5前一");
            choosingInf.setText(SYXW_TIPS08);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
        }
        else if (lotteryType == 9) {
            title.setText("11选5前二直选");
            choosingInf.setText(SYXW_TIPS09);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.VISIBLE);
            choosingCountHundredLinear.setVisibility(View.GONE);
        }
        else if (lotteryType == 10) {
            title.setText("11选5前二组选");
            choosingInf.setText(SYXW_TIPS10);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "组选二";
        }
        else if (lotteryType == 11) {
            title.setText("11选5前三直选");
            choosingInf.setText(SYXW_TIPS11);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.VISIBLE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.VISIBLE);
            choosingCountHundredLinear.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 12) {
            title.setText("11选5前三组选");
            choosingInf.setText(SYXW_TIPS12);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);

            choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
            choosingCountThousandLinear.setVisibility(View.GONE);
            choosingCountHundredLinear.setVisibility(View.GONE);
            tabName = "组选三";
        }
    }

    private void showPlayType() {
// if(lotteryMethodType==1){
        title.setText("11选5" + tabName + "胆拖");
        choosingInf.setText(SYXW_TIPS09);
        redBallsLayout01.setVisibility(View.VISIBLE);
        redBallsLayout02.setVisibility(View.VISIBLE);
        redBallsLayout03.setVisibility(View.GONE);
// choosingCountTenThousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.GONE);
        choosingCountTenThousandLinear.setVisibility(View.VISIBLE);
        choosingCountThousandLinear.setVisibility(View.VISIBLE);
        choosingCountHundredLinear.setVisibility(View.GONE);
// }
    }

    // dantuo
    private void danTuoDefaultBum(String[] danCodeArray, String[] tuoCodeArray) {
        int danNumsLength = danCodeArray.length;
        for (int i = 0; i < danNumsLength; i++) {
            int num = Integer.valueOf(danCodeArray[i]);
            hongqiu01.get(num - 1).setChoosed(true);
            redBallsLayout01.chooseBall(num - 1);
        }

        int tuoNumsLength = tuoCodeArray.length;
        for (int i = 0; i < tuoNumsLength; i++) {
            int num = Integer.valueOf(tuoCodeArray[i]);
            hongqiu02.get(num - 1).setChoosed(true);
            redBallsLayout02.chooseBall(num - 1);
        }
    }

    private String dantuoCodeConvertBack(String dantuoCode) {
        StringBuilder dantuo_sb = new StringBuilder();
        dantuo_sb.append("(");
        dantuo_sb.append(dantuoCode.replace("$", ")"));
        return dantuo_sb.toString().trim();
    }

    protected void defaultNum(String betNum) {
        if (betNum.indexOf("$") != -1)
            betNum = dantuoCodeConvertBack(betNum);
        String[] lotteryMode = null;
        if (betNum.indexOf("(") == -1) {
            lotteryMode = betNum.split("\\:");
            changeNormalSetion();
        }
        else {
            lotteryMode = BetHistoryDetailTool.filterSyxwDantuoCodeBack(betNum).split("\\:");
            changeDantuoSetion();
        }

        if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 4 ||
            lotteryType == 5 || lotteryType == 6 || lotteryType == 7 || lotteryType == 8 ||
            lotteryType == 10 || lotteryType == 12) {

            if (lotteryMode[0].indexOf("(") == -1) {
                String[] nums = lotteryMode[0].split(",");
                int numsLength = nums.length;
                for (int i = 0; i < numsLength; i++) {
                    int num = Integer.valueOf(nums[i]);
                    hongqiu01.get(num - 1).setChoosed(true);
                    redBallsLayout01.chooseBall(num - 1);
                }
            }
            else {
                String danCode = lotteryMode[0].substring(2, lotteryMode[0].indexOf(")")).trim();
                String tuoCode =
                    lotteryMode[0].substring(lotteryMode[0].indexOf(")") + 1, lotteryMode[0].length()).trim();
                danTuoDefaultBum(danCode.split("\\,"), tuoCode.split("\\,"));
            }
            onBallClickInf(-1, -1);
        }
        else if (lotteryType == 9) {
            String[] lm = lotteryMode[0].split("\\|");
            String[] nums1 = lm[0].split(",");
            String[] nums2 = lm[1].split(",");
            int numsLength1 = nums1.length;
            int numsLength2 = nums2.length;
            for (int i = 0; i < numsLength1; i++) {
                int num = Integer.valueOf(nums1[i]);
                hongqiu01.get(num - 1).setChoosed(true);
                redBallsLayout01.chooseBall(num - 1);
            }
            for (int i = 0; i < numsLength2; i++) {
                int num = Integer.valueOf(nums2[i]);
                hongqiu02.get(num - 1).setChoosed(true);
                redBallsLayout02.chooseBall(num - 1);
            }
            onBallClickInf(-1, -1);
        }
        else if (lotteryType == 11) {
            String[] lm = lotteryMode[0].split("\\|");
            String[] nums1 = lm[0].split(",");
            String[] nums2 = lm[1].split(",");
            String[] nums3 = lm[2].split(",");
            int numsLength1 = nums1.length;
            int numsLength2 = nums2.length;
            int numsLength3 = nums3.length;
            for (int i = 0; i < numsLength1; i++) {
                int num = Integer.valueOf(nums1[i]);
                hongqiu01.get(num - 1).setChoosed(true);
                redBallsLayout01.chooseBall(num - 1);
            }
            for (int i = 0; i < numsLength2; i++) {
                int num = Integer.valueOf(nums2[i]);
                hongqiu02.get(num - 1).setChoosed(true);
                redBallsLayout02.chooseBall(num - 1);
            }
            for (int i = 0; i < numsLength3; i++) {
                int num = Integer.valueOf(nums3[i]);
                hongqiu03.get(num - 1).setChoosed(true);
                redBallsLayout03.chooseBall(num - 1);
            }
            onBallClickInf(-1, -1);
        }
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
            return getBallsBetElevenththKindInf();
        else if (lotteryType == 12)
            return getBallsBetTwelfthKindInf();
        else
            return null;
    }

// private String getDantuoBetInf() {
// return getBallsDantuoKindInf();
// }

    private String getBallsBetFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX2:");

        return betBallText.toString();
    }

    private String getBallsBetSecondKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX3:");

        return betBallText.toString();
    }

    private String getBallsBetThirdKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX4:");

        return betBallText.toString();
    }

    private String getBallsBetForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX5:");

        return betBallText.toString();
    }

    private String getBallsBetFifthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX6:");

        return betBallText.toString();
    }

    private String getBallsBetSixthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX7:");

        return betBallText.toString();
    }

    private String getBallsBetSeventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
// if (isNotDantuo) {
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
// }
// else {
// //dantuo
// betBallText = getBallsDantuoKindInf();
// }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX8:");

        return betBallText.toString();
    }

    private String getBallsBetEighthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_RX1:");

        return betBallText.toString();
    }

    private String getBallsBetNinthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() > 0) {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() > 0) {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        orgCode = betBallText.toString();
        if (isHave(betBallText.toString(), ',')) {
            betBallText.append(":" + betMoney + ":11_ZXQ2_F:");

        }
        else {
            betBallText.append(":2:11_ZXQ2_D:");
        }
        return betBallText.toString();
    }

    private String getBallsBetTenthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_ZXQ2:");

        return betBallText.toString();
    }

    private String getBallsBetElevenththKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() > 0) {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() > 0) {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int hongLength03 = hongqiu03.size();
        if (hongqiuInf03.getCount() > 0) {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength03));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        orgCode = betBallText.toString();
        if (isHave(betBallText.toString(), ',')) {
            betBallText.append(":" + betMoney + ":11_ZXQ3_F:");

        }
        else {
            betBallText.append(":2:11_ZXQ3_D:");
        }
        return betBallText.toString();
    }

    private String getBallsBetTwelfthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (isNotDantuo) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        else {
            // dantuo
            betBallText = getBallsDantuoKindInf();
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":11_ZXQ3:");
        return betBallText.toString();
    }

    // dantuo
    private StringBuilder getBallsDantuoKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() > 0) {
            betBallText.append("(");
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append(")");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() > 0) {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02));
                    betBallText.append(",");
                }
            }
        }
        return betBallText;
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
        else if (lotteryType == 12)
            return getBallsDisplayTwelfthKindInf();
        else
            return null;
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选二] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySecondKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选三] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
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
        betBallText.append("[任选四] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
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
        betBallText.append("[任选五] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
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
        betBallText.append("[任选六] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
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
        betBallText.append("[任选七] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选八] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEighthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前一] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayNinthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前二直选] ");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02));
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTenthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前二组选] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEleventhKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前三直选] ");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01) + ",");
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
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02) + ",");
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
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength03) + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTwelfthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前三组选] ");
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayDanTuoKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[胆码](");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength01) + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append(")");
        int hongLength02 = hongqiu02.size();
        if (hongqiuInf02.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength02; i++) {
                if (hongqiu02.get(i).isChoosed())
                    betBallText.append(StringUtil.betDataTransite((i + 1), hongLength02) + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String setCheckCondition(int lotteryType) {
        if (isNotDantuo == false) {
            // dantuo
            if (hongqiuInf01.getCount() < 1 &&
                (hongqiuInf01.getCount() + hongqiuInf02.getCount() < lotteryType + 1)) {
                return SYXW_TIPSDANTUO;
            }
            else if (hongqiuInf01.getCount() > 0 &&
                (hongqiuInf01.getCount() + hongqiuInf02.getCount() == lotteryType + 1)) {
                return "胆拖玩法必须1注以上，你只选择一注。";
            }
        }
        else {
            if (hongqiuInf01.getCount() < lotteryType + 1)
                return SYXWTIPSARRAY[lotteryType - 1];
        }
        return null;
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 4 ||
            lotteryType == 5 || lotteryType == 6) {
            inf = setCheckCondition(lotteryType);
        }
        else if (lotteryType == 7) {
            if (hongqiuInf01.getCount() < 8)
                inf = SYXW_TIPS07;
        }
        else if (lotteryType == 8) {
            if (hongqiuInf01.getCount() < 1)
                inf = SYXW_TIPS08;
        }
        else if (lotteryType == 9) {
            if (hongqiuInf01.getCount() < 1)
                inf = SYXW_TIPS09;
            else if (hongqiuInf02.getCount() < 1)
                inf = SYXW_TIPS09;
        }
        else if (lotteryType == 10) {
            if (isNotDantuo == false) {
                if (hongqiuInf01.getCount() < 1 && (hongqiuInf01.getCount() + hongqiuInf02.getCount() < 2))
                    inf = SYXW_TIPSDANTUO;
                else if (hongqiuInf01.getCount() > 0 &&
                    (hongqiuInf01.getCount() + hongqiuInf02.getCount() == 2)) {
                    inf = "胆拖玩法必须1注以上，你只选择一注。";
                }
            }
            else {
                if (hongqiuInf01.getCount() < 2)
                    inf = SYXW_TIPS10;
            }
        }
        else if (lotteryType == 11) {
            if (hongqiuInf01.getCount() < 1 || hongqiuInf02.getCount() < 1 || hongqiuInf03.getCount() < 1)
                inf = SYXW_TIPS11;
        }
        else if (lotteryType == 12) {
            if (isNotDantuo == false) {
                if (hongqiuInf01.getCount() < 1 && (hongqiuInf01.getCount() + hongqiuInf02.getCount() < 3))
                    inf = SYXW_TIPSDANTUO;
                else if (hongqiuInf01.getCount() > 0 &&
                    (hongqiuInf01.getCount() + hongqiuInf02.getCount() == 3)) {
                    inf = "胆拖玩法必须1注以上，你只选择一注。";
                }

            }
            else {
                if (hongqiuInf01.getCount() < 3)
                    inf = SYXW_TIPS12;
            }
        }

        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    private int getReword(int lotteryType) {
        return reword[lotteryType - 1];
    }

    @Override
    public void randomBalls() {
        clearBalls();
        betMoney = 2;
        String betInf = "1注     <font color='red'>2元</font>";
        for (int i = 0; i < LotteryUtils.textArray.length; i++) {
            if (lotteryType == i + 1) {
                if (isNotDantuo) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - 2) + "</font>元"));

                }

            }
        }
        moneyInf.setText(Html.fromHtml(betInf));
        if (lotteryType == 1) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 2) {
            int[] randomRedNum02 = MathUtil.getRandomNumNotEquals(3, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum02[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum02[i]);
            }
        }
        else if (lotteryType == 3) {
            int[] randomRedNum03 = MathUtil.getRandomNumNotEquals(4, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 4; i++) {
                hongqiu01.get(randomRedNum03[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum03[i]);
            }
        }
        else if (lotteryType == 4) {
            int[] randomRedNum04 = MathUtil.getRandomNumNotEquals(5, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 5; i++) {
                hongqiu01.get(randomRedNum04[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum04[i]);
            }
        }
        else if (lotteryType == 5) {
            int[] randomRedNum05 = MathUtil.getRandomNumNotEquals(6, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 6; i++) {
                hongqiu01.get(randomRedNum05[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum05[i]);
            }
        }
        else if (lotteryType == 6) {
            int[] randomRedNum06 = MathUtil.getRandomNumNotEquals(7, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 7; i++) {
                hongqiu01.get(randomRedNum06[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum06[i]);
                betInf = "1注     <font color='red'>2元</font>";
            }
        }
        else if (lotteryType == 7) {
            int[] randomRedNum07 = MathUtil.getRandomNumNotEquals(8, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 8; i++) {
                hongqiu01.get(randomRedNum07[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum07[i]);
            }
        }
        else if (lotteryType == 8) {
            Random rd = new Random();
            int num = rd.nextInt(SYXW_HONGQIU_LENGTH);
            hongqiu01.get(num).setChoosed(true);
            redBallsLayout01.chooseBall(num);
        }
        else if (lotteryType == 9) {
            int[] randomRedNum09 = MathUtil.getRandomNumNotEquals(2, SYXW_HONGQIU_LENGTH);
            hongqiu01.get(randomRedNum09[0]).setChoosed(true);
            redBallsLayout01.chooseBall(randomRedNum09[0]);
            hongqiu02.get(randomRedNum09[1]).setChoosed(true);
            redBallsLayout02.chooseBall(randomRedNum09[1]);
        }
        if (lotteryType == 10) {
            int[] randomRedNum10 = MathUtil.getRandomNumNotEquals(2, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum10[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum10[i]);
            }
        }
        else if (lotteryType == 11) {
            int[] randomRedNum11 = MathUtil.getRandomNumNotEquals(3, SYXW_HONGQIU_LENGTH);
            hongqiu01.get(randomRedNum11[0]).setChoosed(true);
            redBallsLayout01.chooseBall(randomRedNum11[0]);
            hongqiu02.get(randomRedNum11[1]).setChoosed(true);
            redBallsLayout02.chooseBall(randomRedNum11[1]);
            hongqiu03.get(randomRedNum11[2]).setChoosed(true);
            redBallsLayout03.chooseBall(randomRedNum11[2]);
        }
        else if (lotteryType == 12) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(3, SYXW_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
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
        if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 4 ||
            lotteryType == 5 || lotteryType == 6 || lotteryType == 7 || lotteryType == 8 ||
            lotteryType == 10 || lotteryType == 12) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 9) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
        }
        else if (lotteryType == 11) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
        }

        // /dantuo
        if (isNotDantuo == false) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            resetDantuoInf();
            betMoney = 0;
            moneyInf.setText(Html.fromHtml(MONEY_TIPS));
            resetBtn();
        }
        else {
            resetInf();
        }
    }

    protected void resetInf() {
        super.resetInf();
        initLotteryIntroduce();
        if (lotteryType == 1)
            choosingInf.setText(SYXW_TIPS01);
        else if (lotteryType == 2)
            choosingInf.setText(SYXW_TIPS02);
        else if (lotteryType == 3)
            choosingInf.setText(SYXW_TIPS03);
        else if (lotteryType == 4)
            choosingInf.setText(SYXW_TIPS04);
        else if (lotteryType == 5)
            choosingInf.setText(SYXW_TIPS05);
        else if (lotteryType == 6)
            choosingInf.setText(SYXW_TIPS06);
        else if (lotteryType == 7)
            choosingInf.setText(SYXW_TIPS07);
        else if (lotteryType == 8)
            choosingInf.setText(SYXW_TIPS08);
        else if (lotteryType == 9)
            choosingInf.setText(SYXW_TIPS09);
        else if (lotteryType == 10)
            choosingInf.setText(SYXW_TIPS10);
        else if (lotteryType == 11)
            choosingInf.setText(SYXW_TIPS11);
        else if (lotteryType == 12)
            choosingInf.setText(SYXW_TIPS12);
        showBallNum();
    }

    // dantuo
    private void resetDantuoInf() {
        choosingInf.setText(SYXW_TIPS09);
        showBallTypeNum();
        resetInf();
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    @Override
    public void onBallClickFull(int kind) {
        if (kind == 1) {
            ViewUtil.showTipsToast(this, "您只能选" + SYXW_HONGQIU_RXB_LIMIT + "个红球");
        }
    }

    // dantuo
    private void dantuoBetNum(int ballType, int index) {
        danTuoReflashMoney = false;

        if (lotteryMethodTypeDT == 7) {
            if (hongqiuInf01.getCount() == 1 &&
                (hongqiuInf02.getCount() >= 1 && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
            else if (hongqiuInf01.getCount() == 1 &&
                (hongqiuInf02.getCount() < 1 && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
        }
        else if (lotteryMethodTypeDT == 8) {
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= 2) &&
                (hongqiuInf02.getCount() >= (3 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
            else if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= 2) &&
                (hongqiuInf02.getCount() < (3 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
        }
        else {
            //
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= lotteryMethodTypeDT) &&
                (hongqiuInf02.getCount() >= (lotteryMethodTypeDT + 1 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
            else if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= lotteryMethodTypeDT) &&
                (hongqiuInf02.getCount() < (lotteryMethodTypeDT + 1 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                sameFilter(ballType, index);
            }
        }

        if (lotteryMethodTypeDT == 7) {
            if ((hongqiuInf01.getCount() == 1) &&
                (hongqiuInf02.getCount() >= 1 && hongqiuInf02.getCount() <= 10)) {
                setBetNum(2);
            }
            else {
                setBetBallStatus(ballType, index, 1);
            }
        }
        else if (lotteryMethodTypeDT == 8) {
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= 2) &&
                (hongqiuInf02.getCount() >= (3 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                setBetNum(3);
            }
            else {
                setBetBallStatus(ballType, index, 2);
            }
        }
        else {
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= lotteryMethodTypeDT) &&
                (hongqiuInf02.getCount() >= (lotteryMethodTypeDT + 1 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
                setBetNum(lotteryMethodTypeDT + 1);
            }
            else {
                setBetBallStatus(ballType, index, lotteryMethodTypeDT);
            }
        }

        if (lotteryMethodTypeDT == 7) {
            if (hongqiuInf01.getCount() == 1 &&
                (hongqiuInf02.getCount() >= 1 && hongqiuInf02.getCount() <= 10)) {
            }
            else {
                invalidateNum();
            }
        }
        else if (lotteryMethodTypeDT == 8) {
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= 2) &&
                (hongqiuInf02.getCount() >= (3 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
            }
            else {
                invalidateNum();
            }
        }
        else {
            if ((hongqiuInf01.getCount() >= 1 && hongqiuInf01.getCount() <= lotteryMethodTypeDT) &&
                (hongqiuInf02.getCount() >= (lotteryMethodTypeDT + 1 - hongqiuInf01.getCount()) && hongqiuInf02.getCount() <= 10)) {
            }
            else {
                invalidateNum();
            }
        }

        if (hongqiuInf01.getCount() == 0 && hongqiuInf02.getCount() == 0)
            resetDantuoInf();
    }

    // dantuo
    private void sameFilter(int ballType, int index) {
        if (ballType != -1) {
            if (hongqiu01.get(index).isChoosed() && hongqiu02.get(index).isChoosed()) {
                if (ballType == 1) {
                    hongqiu02.get(index).setChoosed(false);
                    hongqiuInf02.setCount(hongqiuInf02.getCount() - 1);
                    redBallsLayout02.refreshAllBall();
                }
                else if (ballType == 2) {
                    hongqiu01.get(index).setChoosed(false);
                    hongqiuInf01.setCount(hongqiuInf01.getCount() - 1);
                    redBallsLayout01.refreshAllBall();
                }
            }
        }
    }

    // dantuo
    private void setBetNum(int amount) {
        danTuoReflashMoney = true;
        int number = amount - hongqiuInf01.getCount();
        betNumber = MathUtil.factorial(hongqiuInf02.getCount(), number) / MathUtil.factorial(number, number);
        betMoney = betNumber * 2 * 1;

    }

    // dantuo
    private void setBetBallStatus(int ballType, int index, int lotteryMetTypeDT) {
        if (ballType == 1 && hongqiuInf01.getCount() > lotteryMetTypeDT) {
            if (hongqiu01.get(index).isChoosed()) {
                hongqiu01.get(index).setChoosed(false);
                hongqiuInf01.setCount(hongqiuInf01.getCount() - 1);
            }
            redBallsLayout01.refreshAllBall();
            ViewUtil.showTipsToast(this, "胆码最多可选" + lotteryMetTypeDT + "个");
        }
        if (ballType == 2 && hongqiuInf02.getCount() > 10) {
            if (hongqiu02.get(index).isChoosed()) {
                hongqiu02.get(index).setChoosed(false);
                hongqiuInf02.setCount(hongqiuInf02.getCount() - 1);
            }
            redBallsLayout02.refreshAllBall();
            ViewUtil.showTipsToast(this, "拖码最多可选10个");
        }
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        Boolean refreshMoney = false;
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();

        if ((hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) == false) {
            enableClearBtn();
        }

        if (lotteryType == 1) {
            if (isNotDantuo) {
                if (hongNumber01 >= 2) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 2) {
            if (isNotDantuo) {
                if (hongNumber01 >= 3) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 3) {
            if (isNotDantuo) {
                if (hongNumber01 >= 4) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 4) / MathUtil.factorial(4, 4);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 4) {
            if (isNotDantuo) {
                if (hongNumber01 >= 5) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 5) / MathUtil.factorial(5, 5);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 5) {
            if (isNotDantuo) {
                if (hongNumber01 >= 6) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 6) / MathUtil.factorial(6, 6);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 6) {
            if (isNotDantuo) {
                if (hongNumber01 >= 7) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 7) / MathUtil.factorial(7, 7);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 7) {
            /*
             * if(hongNumber01 > 8){ invalidateNum(); Toast.makeText(this, "任选八只支持单式投注。", 1000).show(); } else
             */
            if (isNotDantuo) {
                if (hongNumber01 == 8) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 8) / MathUtil.factorial(8, 8);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 8) {
            if (isNotDantuo) {
                if (hongNumber01 >= 1) {
                    refreshMoney = true;
                    betNumber = hongNumber01;
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 9) {
            if (isNotDantuo) {
                if (hongNumber01 == 0 && hongNumber02 == 0) {
                    resetInf();
                }
                else {
                    refreshMoney = true;
                    // 选球互斥性实现
                    if (ballType == 1 && index >= 0) {
                        if (hongqiu02.get(index).isChoosed()) {
                            hongqiu02.get(index).setChoosed(false);
                            hongqiuInf02.setCount(hongqiuInf02.getCount() - 1);
                        }
                        redBallsLayout02.refreshAllBall();
                    }
                    else if (ballType == 2 && index >= 0) {
                        if (hongqiu01.get(index).isChoosed()) {
                            hongqiu01.get(index).setChoosed(false);
                            hongqiuInf01.setCount(hongqiuInf01.getCount() - 1);
                        }
                        redBallsLayout01.refreshAllBall();
                    }
                    else {
                        invalidateNum();
                    }
                    betNumber = hongqiuInf02.getCount() * hongqiuInf01.getCount();
                    betMoney = betNumber * 2 * 1;
                }

            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 10) {
            if (isNotDantuo) {
                if (hongNumber01 >= 2) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 11) {
            if (isNotDantuo) {
                if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) {
                    resetInf();
                }
                else {
                    refreshMoney = true;
                    // 选球互斥性实现
                    if (ballType == 1 && index >= 0) {
                        if (hongqiu02.get(index).isChoosed()) {
                            hongqiu02.get(index).setChoosed(false);
                            hongqiuInf02.setCount(hongqiuInf02.getCount() - 1);
                            redBallsLayout02.refreshAllBall();
                        }
                        else if (hongqiu03.get(index).isChoosed()) {
                            hongqiu03.get(index).setChoosed(false);
                            hongqiuInf03.setCount(hongqiuInf03.getCount() - 1);
                            redBallsLayout03.refreshAllBall();
                        }
                    }
                    else if (ballType == 2 && index >= 0) {
                        if (hongqiu01.get(index).isChoosed()) {
                            hongqiu01.get(index).setChoosed(false);
                            hongqiuInf01.setCount(hongqiuInf01.getCount() - 1);
                        }
                        else if (hongqiu03.get(index).isChoosed()) {
                            hongqiu03.get(index).setChoosed(false);
                            hongqiuInf03.setCount(hongqiuInf03.getCount() - 1);
                        }
                        redBallsLayout01.refreshAllBall();
                        redBallsLayout03.refreshAllBall();
                    }
                    else if (ballType == 3 && index >= 0) {
                        if (hongqiu01.get(index).isChoosed()) {
                            hongqiu01.get(index).setChoosed(false);
                            hongqiuInf01.setCount(hongqiuInf01.getCount() - 1);
                        }
                        else if (hongqiu02.get(index).isChoosed()) {
                            hongqiu02.get(index).setChoosed(false);
                            hongqiuInf02.setCount(hongqiuInf02.getCount() - 1);
                        }
                        redBallsLayout01.refreshAllBall();
                        redBallsLayout02.refreshAllBall();
                    }
                    else {
                        invalidateNum();
                    }

                    betNumber = hongqiuInf01.getCount() * hongqiuInf02.getCount() * hongqiuInf03.getCount();
                    betMoney = betNumber * 2 * 1;
                }
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }
        else if (lotteryType == 12) {
            if (isNotDantuo) {
                if (hongNumber01 >= 3) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3);
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber01 > 0)
                    invalidateNum();
                else
                    resetInf();
            }
            else {
                dantuoBetNum(ballType, index);
            }
        }

        checkBet(betMoney);
        if (isNotDantuo == false) {
            refreshMoney = danTuoReflashMoney;
// if (betMoney < 4)
// disableBetBtn();
// else
// enableBetBtn();
        }
        if (refreshMoney) {
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null) {
                moneyInf.setText(Html.fromHtml(betInf));
                showRewordInfo(hongNumber01, hongNumber02, hongNumber03);
            }
            invalidateAll();
        }
        else {
            initLotteryIntroduce();
        }
    }

    public void initLotteryIntroduce() {
// lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "-"
// + "</font>元,您将盈利<font color=\"#CD2626\">" + "-" + "</font>元"));
    }

    public void showRewordInfo(int hongNumber01, int hongNumber02, int hongNumber03) {
        hongNumber01 = hongqiuInf01.getCount();
        hongNumber02 = hongqiuInf02.getCount();
        hongNumber03 = hongqiuInf03.getCount();
        if (isNotDantuo) {
            if (lotteryType == 1) {// 任选二
                if (hongNumber01 == 2) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 3 || hongNumber01 == 4 || hongNumber01 == 5) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (10 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" + (10 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 9) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 3 + "</font>至<font color=\"#CD2626\">" +
                        (10 * getReword(lotteryType)) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 3) + "</font>元至<font color=\"#1874CD\">" +
                        (betMoney - 10 * getReword(lotteryType)) + "</font>元"));
                }
                else if (hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 6 + "</font>至<font color=\"#CD2626\">" +
                        (10 * getReword(lotteryType)) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 6) + "</font>元至<font color=\"#1874CD\">" +
                        (betMoney - 10 * getReword(lotteryType)) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 10 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 10) + "</font>元"));
                }
            }
            else if (lotteryType == 2) {// 任选三
                if (hongNumber01 == 3) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 4 || hongNumber01 == 5) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (betNumber * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" + (betNumber * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8 || hongNumber01 == 9) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (10 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (10 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (10 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 4 + "</font>至<font color=\"#CD2626\">" +
                        (10 * getReword(lotteryType)) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 4) + "</font>元至<font color=\"#1874CD\">" +
                        (betMoney - 10 * getReword(lotteryType)) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 10 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 10) + "</font>元"));
                }
            }
            else if (lotteryType == 3) {// 任选四
                if (hongNumber01 == 4) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 5 || hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8 ||
                    hongNumber01 == 9) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (5 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" + (5 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (5 * getReword(lotteryType)) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType)) + "</font>元至<font color=\"#1874CD\">" +
                        (betMoney - 5 * getReword(lotteryType)) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 5 + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * 5) + "</font>元"));
                }
            }
            else if (lotteryType == 4) {// 任选五
                if (hongNumber01 == 5 || hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8 ||
                    hongNumber01 == 9 || hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType)) + "</font>元"));
                }
            }
            else if (lotteryType == 5) {// 任选六
                if (hongNumber01 == 6 || hongNumber01 == 7 || hongNumber01 == 8 || hongNumber01 == 9 ||
                    hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * (hongNumber01 - 5) +
                        "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) * (hongNumber01 - 5) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * (hongNumber01 - 5) +
                        "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * (hongNumber01 - 5)) + "</font>元"));
                }
            }
            else if (lotteryType == 6) {// 任选七
                if (hongNumber01 == 7) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 8 || hongNumber01 == 9 || hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) *
                        MathUtil.factorial(hongNumber01 - 5, 2) /
                        MathUtil.factorial(2, 2) +
                        "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) * MathUtil.factorial(hongNumber01 - 5, 2) /
                            MathUtil.factorial(2, 2) - betMoney) + "</font>元"));
                }

                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) *
                        MathUtil.factorial(hongNumber01 - 5, 2) /
                        MathUtil.factorial(2, 2) +
                        "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * MathUtil.factorial(hongNumber01 - 5, 2) /
                            MathUtil.factorial(2, 2)) + "</font>元"));
                }
            }
            else if (lotteryType == 7) {// 任选八
                if (hongNumber01 == 8) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 9 || hongNumber01 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) *
                        MathUtil.factorial(hongNumber01 - 5, 2) /
                        MathUtil.factorial(2, 2) +
                        "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) * MathUtil.factorial(hongNumber01 - 5, 2) /
                            MathUtil.factorial(2, 2) - betMoney) + "</font>元"));
                }
                else if (hongNumber01 == 11) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) *
                        MathUtil.factorial(hongNumber01 - 5, 2) /
                        MathUtil.factorial(2, 2) +
                        "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType) * MathUtil.factorial(hongNumber01 - 5, 2) /
                            MathUtil.factorial(2, 2)) + "</font>元"));
                }
            }
            else if (lotteryType == 8) {// 前一
                if (getReword(lotteryType) - betMoney < 0) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType)) + "</font>元"));
                }
                else {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
            }
            else if (lotteryType == 9) {// 前二直选
                if (hongqiuInf01.getCount() > 0 && hongqiuInf02.getCount() > 0) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else {
                    initLotteryIntroduce();
                }
            }
            else if (lotteryType == 10) {// 前二组选
                if (getReword(lotteryType) - betMoney < 0) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType)) + "</font>元"));
                }
                else {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
            }
            else if (lotteryType == 11) {// 前三直选
                if (hongqiuInf01.getCount() > 0 && hongqiuInf02.getCount() > 0 && hongqiuInf03.getCount() > 0) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else {
                    initLotteryIntroduce();
                }
            }
            else if (lotteryType == 12) {// 前三组选
                if (getReword(lotteryType) - betMoney < 0) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将亏损<font color=\"#1874CD\">" +
                        (betMoney - getReword(lotteryType)) + "</font>元"));
                }
                else {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
            }
        }
        else {
            // TODO 胆拖时盈亏提示
            if (lotteryType == 1) {// 任选二
                if (hongNumber02 == 1) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber02 == 2 || hongNumber02 == 3 || hongNumber02 == 4) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) +
                        "</font>至<font color=\"#CD2626\">" +
                        (hongNumber02 * getReword(lotteryType)) +
                        "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" +
                        (getReword(lotteryType) - betMoney) +
                        "</font>元至<font color=" +
                        (hongNumber02 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                        ">" + (hongNumber02 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber02 == 5 || hongNumber02 == 6 || hongNumber02 == 7) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                        (4 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                        (4 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (4 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber02 == 8 || hongNumber02 == 9) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) *
                        (hongNumber02 - 6) +
                        "</font>至<font color=\"#CD2626\">" +
                        (4 * getReword(lotteryType)) +
                        "</font>元,您将盈利<font color=" +
                        (getReword(lotteryType) * (hongNumber02 - 6) - betMoney > 0 ? "\"#CD2626\""
                            : "\"#1874CD\"") + ">" +
                        (getReword(lotteryType) * (hongNumber02 - 6) - betMoney) + "</font>元至<font color=" +
                        (4 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                        (4 * getReword(lotteryType) - betMoney) + "</font>元"));
                }
                else if (hongNumber02 == 10) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                        getReword(lotteryType) * 4 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                        (getReword(lotteryType) * 4 - betMoney) + "</font>元"));
                }
            }
            else if (lotteryType == 2) {// 任选三
                if (hongNumber01 == 1) {
                    if (hongNumber02 == 2) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 3 || hongNumber02 == 4) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) +
                            "</font>至<font color=\"#CD2626\">" +
                            (betNumber * getReword(lotteryType)) +
                            "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" +
                            (getReword(lotteryType) - betMoney) +
                            "</font>元至<font color=" +
                            (betNumber * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\""
                                : "\"#1874CD\"") + ">" + (betNumber * getReword(lotteryType) - betMoney) +
                            "</font>元"));
                    }
                    else if (hongNumber02 == 5 || hongNumber02 == 6 || hongNumber02 == 7 || hongNumber02 == 8) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (6 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (6 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (6 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 9) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 3 + "</font>至<font color=\"#CD2626\">" +
                            (6 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) * 3 - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (getReword(lotteryType) * 3 - betMoney) + "</font>元至<font color=" +
                            (6 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (6 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 10) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 6 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * 6 - betMoney) + "</font>元"));
                    }
                }
                else if (hongNumber01 == 2) {
                    if (hongNumber02 == 1) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 2 || hongNumber02 == 3) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) +
                            "</font>至<font color=\"#CD2626\">" +
                            (hongNumber02 * getReword(lotteryType)) +
                            "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" +
                            (getReword(lotteryType) - betMoney) +
                            "</font>元至<font color=" +
                            (hongNumber02 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\""
                                : "\"#1874CD\"") + ">" + (hongNumber02 * getReword(lotteryType) - betMoney) +
                            "</font>元"));
                    }
                    else if (hongNumber02 == 4 || hongNumber02 == 5 || hongNumber02 == 6 || hongNumber02 == 7) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (3 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (3 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (3 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 8) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 2 + "</font>至<font color=\"#CD2626\">" +
                            (3 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) * 2 - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (getReword(lotteryType) * 2 - betMoney) + "</font>元至<font color=" +
                            (3 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (3 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 9) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 3 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * 3 - betMoney) + "</font>元"));
                    }
                }
            }
            else if (lotteryType == 3) {// 任选四
                if (hongNumber01 == 1) {
                    if (hongNumber02 == 3) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 > 3 && hongNumber02 < 10) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (4 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (4 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (4 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 10) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 4 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * 4 - betMoney) + "</font>元"));
                    }
                }
                else if (hongNumber01 == 2) {
                    if (hongNumber02 == 2) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 > 2 && hongNumber02 < 9) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (3 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (3 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (3 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 9) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 3 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * 3 - betMoney) + "</font>元"));
                    }
                }
                else if (hongNumber01 == 3) {
                    if (hongNumber02 == 1) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 > 1 && hongNumber02 < 8) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (2 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=" +
                            (2 * getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                            ">" + (2 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 == 8) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) * 2 + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) * 2 - betMoney) + "</font>元"));
                    }
                }
            }
            else if (lotteryType == 4) {// 任选五
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (lotteryType == 5) {// 任选六
                for (int i = 1; i < 6; i++) {
                    if (hongNumber01 == i) {
                        if (hongNumber02 == (6 - i)) {
                            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                                getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                                (getReword(lotteryType) - betMoney) + "</font>元"));
                        }
                        else if (hongNumber02 > (6 - i) && hongNumber02 < (12 - i)) {
                            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                                getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                                ((hongNumber02 - (5 - i)) * getReword(lotteryType)) +
                                "</font>元,您将盈利<font color=" +
                                (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                                ">" + (getReword(lotteryType) - betMoney) +
                                "</font>元至<font color=\"#CD2626\"" + ">" +
                                ((hongNumber02 - (5 - i)) * getReword(lotteryType) - betMoney) + "</font>元"));
                        }
                        break;
                    }
                }

            }
            if (lotteryType == 6) {// 任选七
                if (hongNumber01 == 1) {
                    if (hongNumber02 == 6) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 > 6 && hongNumber02 < 11) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) *
                            (hongNumber02 - 5) +
                            "</font>至<font color=\"#CD2626\">" +
                            ((MathUtil.factorial(hongNumber02 - 4, 2) / MathUtil.factorial(2, 2)) * getReword(lotteryType)) +
                            "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) * (hongNumber02 - 5) - betMoney > 0 ? "\"#CD2626\""
                                : "\"#1874CD\"") +
                            ">" +
                            (getReword(lotteryType) * (hongNumber02 - 5) - betMoney) +
                            "</font>元至<font color=\"#CD2626\"" +
                            ">" +
                            ((MathUtil.factorial(hongNumber02 - 4, 2) / MathUtil.factorial(2, 2)) *
                                getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                }
                else if (hongNumber01 > 1 && hongNumber01 < 6) {
                    for (int i = 2; i < 6; i++) {
                        if (hongNumber01 == i) {
                            if (hongNumber02 == (7 - i)) {
                                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                                    (getReword(lotteryType) - betMoney) + "</font>元"));
                            }
                            else if (hongNumber02 > (7 - i) && hongNumber02 < (12 - i)) {
                                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                                    getReword(lotteryType) +
                                    "</font>至<font color=\"#CD2626\">" +
                                    ((MathUtil.factorial(hongNumber02 - (5 - i), 2) / MathUtil.factorial(2, 2)) * getReword(lotteryType)) +
                                    "</font>元,您将盈利<font color=" +
                                    (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") +
                                    ">" +
                                    (getReword(lotteryType) - betMoney) +
                                    "</font>元至<font color=\"#CD2626\"" +
                                    ">" +
                                    ((MathUtil.factorial(hongNumber02 - (5 - i), 2) / MathUtil.factorial(2, 2)) *
                                        getReword(lotteryType) - betMoney) + "</font>元"));
                            }
                        }
                    }
                }
                else if (hongNumber01 == 6) {
                    if (hongNumber02 == 1) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                            (getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                    else if (hongNumber02 > 1 && hongNumber02 < 6) {
                        lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                            getReword(lotteryType) + "</font>至<font color=\"#CD2626\">" +
                            (hongNumber02 * getReword(lotteryType)) + "</font>元,您将盈利<font color=" +
                            (getReword(lotteryType) - betMoney > 0 ? "\"#CD2626\"" : "\"#1874CD\"") + ">" +
                            (getReword(lotteryType) - betMoney) + "</font>元至<font color=\"#CD2626\"" + ">" +
                            (hongNumber02 * getReword(lotteryType) - betMoney) + "</font>元"));
                    }
                }
            }
            else if (lotteryType == 10) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
            else if (lotteryType == 12) {
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" +
                    getReword(lotteryType) + "</font>元,您将盈利<font color=\"#CD2626\">" +
                    (getReword(lotteryType) - betMoney) + "</font>元"));
            }
        }
    }

    protected void showBallNum() {
        if (lotteryType == 1) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 2) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 3) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/4个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 4) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/5个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 5) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/6个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 6) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/7个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 7) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/8个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 8) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/1个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 9) {
            choosingCountTenThousand.setText("万位：" + hongqiuInf01.getCount() + "/1个");
            choosingCountThousand.setText("千位：" + hongqiuInf02.getCount() + "/1个");
            flagHongqiu01.setText("万位");
            flagHongqiu02.setText("千位");
        }
        else if (lotteryType == 10) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 11) {
            choosingCountTenThousand.setText("万位：" + hongqiuInf01.getCount() + "/1个");
            choosingCountThousand.setText("千位：" + hongqiuInf02.getCount() + "/1个");
            choosingCountHundred.setText("百位：" + hongqiuInf03.getCount() + "/1个");
            flagHongqiu01.setText("万位");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
        }
        else if (lotteryType == 12) {
            choosingCountTenThousand.setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        }

    }

    private void showBallTypeNum() {
        if (lotteryMethodTypeDT == 7) {
            choosingCountTenThousand.setText("胆码：" + hongqiuInf01.getCount() + "/1个");
            choosingCountThousand.setText("拖码：" + hongqiuInf02.getCount() + "/10个");
            flagHongqiu01.setText("胆码");
            flagHongqiu02.setText("拖码");
        }
        else if (lotteryMethodTypeDT == 8) {
            choosingCountTenThousand.setText("胆码：" + hongqiuInf01.getCount() + "/2个");
            choosingCountThousand.setText("拖码：" + hongqiuInf02.getCount() + "/10个");
            flagHongqiu01.setText("胆码");
            flagHongqiu02.setText("拖码");
        }
        else {
            choosingCountTenThousand.setText("胆码：" + hongqiuInf01.getCount() + "/" + lotteryMethodTypeDT +
                "个");
            choosingCountThousand.setText("拖码：" + hongqiuInf02.getCount() + "/10个");
            flagHongqiu01.setText("胆码");
            flagHongqiu02.setText("拖码");
        }
    }

    private void invalidateDisplay() {
        if (isNotDantuo == false) {
            if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
                displayCode = getBallsDisplayDanTuoKindInf();
            }
            else {
                displayCode = getBallsDisplayInf();
            }
            showBallTypeNum();
        }
        else {
            displayCode = getBallsDisplayInf();
            showBallNum();
        }
        choosingInf.setText(Html.fromHtml(displayCode));
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "11选5游戏规则");
        bundel.putString("lottery_help", "help_new/d11x5.html");
        intent.putExtras(bundel);
        intent.setClass(SYXWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "11选5走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=syxw&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(SYXWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
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
        else if (v.getId() == R.id.bet_clear_button) {
            initLotteryIntroduce();
            disableBetBtn();
        }
        else if (v.getId() == R.id.switch_button_normal) {
            selectInfo.setText(SYXWTIPSARRAY[lotteryType - 1]);
            shakeRela.setVisibility(View.VISIBLE);
            shakeLockView.setVisibility(View.VISIBLE);
            shakeLockView.setEnabled(true);
            if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
                switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
                switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_selected);
                switchButtonDantuo.setTextColor(getResources().getColor(R.color.dark_purple));
                switchButtonNormal.setTextColor(getResources().getColor(R.color.white));
                shakeLock = false;
                initShakeLock();// 锁图标改变
                shaking = false;
                if (isNotDantuo == false) {
                    random.setVisibility(View.VISIBLE);
                    random.setEnabled(true);
                    isNotDantuo = true;
                    resetLotteryType();
                    clearBalls();
                    showWay();
                    showBallNum();
                }
                databaseData.putBoolean("isNotDantuo", isNotDantuo);
                databaseData.commit();
            }
        }
        else if (v.getId() == R.id.switch_button_dantuo) {
            selectInfo.setText(SYXW_TIPSDANTUO);
            shakeRela.setVisibility(View.INVISIBLE);
            shakeLockView.setVisibility(View.INVISIBLE);
            shakeLockView.setEnabled(false);
            handler.removeMessages(FLASHBALL);
            handler.removeMessages(SOUND);
            handler.removeMessages(VIBRATE);
            handler.removeMessages(RANDOMBALLS);
            if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
                switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_selected);
                switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
                switchButtonDantuo.setTextColor(getResources().getColor(R.color.white));
                switchButtonNormal.setTextColor(getResources().getColor(R.color.dark_purple));
                if (isNotDantuo) {
                    isNotDantuo = false;
                    shakeLock = true;
                    title.setText("11选5" + tabName + "胆拖");
                    clearBalls();
                    showPlayType();
                    showBallTypeNum();
                    random.setVisibility(View.INVISIBLE);
                    random.setEnabled(false);

                }
                databaseData.putBoolean("isNotDantuo", isNotDantuo);
                databaseData.commit();
            }
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

                if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 4 ||
                    lotteryType == 5 || lotteryType == 6 || lotteryType == 7 || lotteryType == 8 ||
                    lotteryType == 10 || lotteryType == 12) {
                    for (int i = 0; i < vertor.length; i++) {
                        hongqiu01.get(vertor[i] - 1).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i] - 1);
                    }
                }
                else if (lotteryType == 9) {
                    hongqiu01.get(vertor[0] - 1).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0] - 1);
                    hongqiu02.get(vertor[1] - 1).setChoosed(true);
                    redBallsLayout02.chooseBall(vertor[1] - 1);
                }
                else if (lotteryType == 11) {
                    hongqiu01.get(vertor[0] - 1).setChoosed(true);
                    hongqiu02.get(vertor[1] - 1).setChoosed(true);
                    hongqiu03.get(vertor[2] - 1).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0] - 1);
                    redBallsLayout02.chooseBall(vertor[1] - 1);
                    redBallsLayout03.chooseBall(vertor[2] - 1);
                }
            }
        }
        else if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }

    private boolean isHave(String src, char des) {
        if (src.indexOf(des) != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout01.refreshAllBallInf(showHotNum, showOmitNum);
        redBallsLayout02.refreshAllBallInf(showHotNum, showOmitNum);
        redBallsLayout03.refreshAllBallInf(showHotNum, showOmitNum);

        // 小标签的显示控制
        if (showHotNum) {
            flagLengre01.setVisibility(View.VISIBLE);
            flagLengre02.setVisibility(View.VISIBLE);
            flagLengre03.setVisibility(View.VISIBLE);
        }
        else {
            flagLengre01.setVisibility(View.GONE);
            flagLengre02.setVisibility(View.GONE);
            flagLengre03.setVisibility(View.GONE);
        }
        if (showOmitNum) {
            flagYilou01.setVisibility(View.VISIBLE);
            flagYilou02.setVisibility(View.VISIBLE);
            flagYilou03.setVisibility(View.VISIBLE);
        }
        else {
            flagYilou01.setVisibility(View.GONE);
            flagYilou02.setVisibility(View.GONE);
            flagYilou03.setVisibility(View.GONE);
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
                    String basicNum = ja.getData(analyseData, "no_distrb");
                    redBallTips = StringUtil.spliteString(basicNum, ",");
                    String[] redballAnalyse = new String[redBallTips.length];
                    redballAnalyse = StringUtil.spliteString(basicNum, ",");
                    int redLength = redBallTips.length;
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
                        hongqiu01.get(i).setBallsInf(redBallTips[i]);
                        hongqiu01.get(i).setBallAnalase(redballAnalyse[i]);
                        hongqiu02.get(i).setBallsInf(redBallTips[i]);
                        hongqiu02.get(i).setBallAnalase(redballAnalyse[i]);
                        hongqiu03.get(i).setBallsInf(redBallTips[i]);
                        hongqiu03.get(i).setBallAnalase(redballAnalyse[i]);
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
                        hongqiu01.get(i).setBallsInf(redBallTips[i]);
                        hongqiu01.get(i).setBallOmit(redballOmit[i]);
                        hongqiu02.get(i).setBallsInf(redBallTips[i]);
                        hongqiu02.get(i).setBallOmit(redballOmit[i]);
                        hongqiu03.get(i).setBallsInf(redBallTips[i]);
                        hongqiu03.get(i).setBallOmit(redballOmit[i]);
                    }
                    refreshHotNumShow();
                }
            }
        }
    }

    @Override
    public String getQ_code() {
        return null;
    }

    @Override
    protected void searchLuckyNum() {
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        titlePopup.dismiss();
        // dantuo
        isNotDantuo = preferences.getBoolean("isNotDantuo", true);

        databaseData.putString("syxw_way", LotteryUtils.syxwWay[kind]);
        databaseData.commit();
        lotteryType = kind + 1;
        this.tabName = tabName;
        index_num = kind;
        if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
            if (lotteryType == 10)
                lotteryMethodTypeDT = 7;
            else if (lotteryType == 12)
                lotteryMethodTypeDT = 8;
            else
                lotteryMethodTypeDT = lotteryType;
            switchButtonDantuo.setVisibility(View.VISIBLE);
            switchButtonNormal.setVisibility(View.VISIBLE);
        }
        else {
            switchButtonDantuo.setVisibility(View.GONE);
            switchButtonNormal.setVisibility(View.GONE);
        }
        if (isNotDantuo) {
            changeNormalSetion();
        }
        else {
            // dantuo
            changeDantuoSetion();
        }
        disableBetBtn();
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
    }

    @Override
    protected void enableBetBtn() {
        // TODO Auto-generated method stub
        super.enableBetBtn();
        if (ifLotteryIntroduceShown == false) {
            appearAnimation(lotteryIntroduce);
            ifLotteryIntroduceShown = true;
        }
    }

    @Override
    protected void disableBetBtn() {
        // TODO Auto-generated method stub
        super.disableBetBtn();
        if (ifLotteryIntroduceShown == true) {
            disappearAnimation(lotteryIntroduce);
            ifLotteryIntroduceShown = false;
        }
    }

    private void changeNormalSetion() {
        if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
            switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
            switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_selected);
            switchButtonDantuo.setTextColor(getResources().getColor(R.color.dark_purple));
            switchButtonNormal.setTextColor(getResources().getColor(R.color.white));
        }
        title.setText(tabName);
        clearBalls();
        showWay();
        showBallNum();
        isNotDantuo = true;
        random.setVisibility(View.VISIBLE);
        random.setEnabled(true);
        shakeRela.setVisibility(View.VISIBLE);
        shakeLockView.setVisibility(View.VISIBLE);
    }

    private void changeDantuoSetion() {
        if (lotteryType != 7 && lotteryType != 8 && lotteryType != 9 && lotteryType != 11) {
            switchButtonDantuo.setBackgroundResource(R.drawable.normal_dantuo_selected);
            switchButtonNormal.setBackgroundResource(R.drawable.normal_dantuo_not_selected);
            switchButtonDantuo.setTextColor(getResources().getColor(R.color.white));
            switchButtonNormal.setTextColor(getResources().getColor(R.color.dark_purple));
            title.setText("11选5" + tabName + "胆拖");
            selectInfo.setText(SYXW_TIPSDANTUO);
            clearBalls();
            showPlayType();
            showBallTypeNum();
            random.setVisibility(View.INVISIBLE);
            shakeLockView.setVisibility(View.GONE);
            shakeRela.setVisibility(View.GONE);
            random.setEnabled(false);
            isNotDantuo = false;
        }
        else {
            title.setText(tabName);
            clearBalls();
            showWay();
            showBallNum();
            isNotDantuo = true;
            random.setVisibility(View.VISIBLE);
            random.setEnabled(true);
            shakeRela.setVisibility(View.VISIBLE);
            shakeLockView.setVisibility(View.VISIBLE);
        }
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
        adapter = new CommissionAdapter(SYXWActivity.this, list, searchType, "bet_tools");
        hotGrid.setAdapter(adapter);
        databaseData.putInt("hot_way", searchType);
        getAnalyseData();
    }
}
