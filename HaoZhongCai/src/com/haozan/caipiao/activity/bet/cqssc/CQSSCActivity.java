package com.haozan.caipiao.activity.bet.cqssc;

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

public class CQSSCActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private static final String CQSSC_TIPS01 = "每位至少选1个，顺序猜中前2、前3、后2、后3都有奖";
    private static final String CQSSC_TIPS02 = "每位至少选1个，顺序猜中全部5个开奖号，奖金10万";
    private static final String CQSSC_TIPS03 = "每位至少选1个，顺序中后4、中3、后3都有奖";// 四星直选，暂时没有
    private static final String CQSSC_TIPS04 = "至少选3个，猜中开奖号后3个(不同号)，奖金160元";
    private static final String CQSSC_TIPS05 = "至少选2个，猜中开奖号后3个(2位相同)，奖金320元";
    private static final String CQSSC_TIPS06 = "每位至少选1个，顺序猜中开奖号后3个，奖金1000元";
    private static final String CQSSC_TIPS07 = "至少选2个，猜中开奖号后2个，奖金50元";
    private static final String CQSSC_TIPS08 = "每位至少选1个，顺序猜中开奖号后2个，奖金100元";
    private static final String CQSSC_TIPS09 = "每位至少选1个，猜中开奖号最后位，奖金11元";
    private static final String CQSSC_TIPS010 = "每位选1个，猜中最后两位大小单双，奖金4元";

    public static final int CQSSC_HONGQIU_START = 0;
    public static final int CQSSC_HONGQIU_LENGTH = 10;
    public static final int CQSSC_HONGQIU_LIMIT = 10;
    public static final int CQSSC_HONGQIU_MIN = 1;

// private static final String[] arrayTips = {"五星通选", "五星直选", "四星直选", "三星组六", "三星组三", "三星直选", "二星组选",
// "二星直选", "一星直选", "大小单双"};
// private static final String[] awardMoneyArray = {"最高2万元", "最高10万元", "奖金1000元", "奖金320元", "奖金160元",
// "奖金100元", "奖金50元", "奖金100元", "奖金10元", "奖金4元"};
    private static final int[] reword = {0, 20440, 100000, 0, 160, 320, 1000, 50, 100, 10, 4};
    private static String hotCondintion = null;
    private int lotteryType = 1;
    private String[] analyseTenthousand;
    private String[] analyseThousand;
    private String[] analyseHundred;
    private String[] analyseTen;
    private String[] analyseUnit;

    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
    private NewBetBallsLayout redBallsLayout01;
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
    private NewBetBallsLayout redBallsLayout02;
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
    private NewBetBallsLayout redBallsLayout03;
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
    private NewBetBallsLayout redBallsLayout04;
    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
    private NewBetBallsLayout redBallsLayout05;
    private ArrayList<BetBall> hongqiu06;
    private BetBallsData hongqiuInf06;
    private NewBetBallsLayout redBallsLayout06;
    private ArrayList<BetBall> hongqiu07;
    private BetBallsData hongqiuInf07;
    private NewBetBallsLayout redBallsLayout07;

    private RelativeLayout termLayout;
// private TextView choosingCountTenthousand;
// private TextView choosingCountThousand;
// private TextView choosingCountHundred;
// private TextView choosingCountTen;
// private TextView choosingCountUnit;
// private TextView choosingCountDXDS1;
// private TextView choosingCountDXDS2;
    private LinearLayout cqssc_bet_field_bg01;
    private LinearLayout cqssc_bet_field_bg02;
    private LinearLayout cqssc_bet_field_bg03;
    private LinearLayout cqssc_bet_field_bg04;
    private LinearLayout cqssc_bet_field_bg05;
    private LinearLayout cqssc_bet_field_bg06;
    private LinearLayout cqssc_bet_field_bg07;

    private TextView lotteryIntroduce;
    private boolean ifLotteryIntroduceShown = false;

    private PopMenu titlePopup;
    private int index_num = 0;

    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03, flagHongqiu04, flagHongqiu05,
        flagHongqiu06, flagHongqiu07;

    // lotteryType 与玩法对照
    // 1.五星通选 2.五星直选 3.四星直选 4.三星组六 5.三星组三 6.三星直选 7.二星组选 8.二星直选 9.一星直选 10.大小单双

    private Map<String, Integer> sscWayMap;
    private TextView selectInfo;

    @Override
    public void setKind() {
        this.kind = "cqssc";
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
        sscWayMap = new HashMap<String, Integer>();
        sscWayMap.put("五星通选", 1);
        sscWayMap.put("五星直选", 2);
        sscWayMap.put("四星直选", 3);
        sscWayMap.put("三星组六", 4);
        sscWayMap.put("三星组三", 5);
        sscWayMap.put("三星直选", 6);
        sscWayMap.put("二星组选", 7);
        sscWayMap.put("二星直选", 8);
        sscWayMap.put("一星直选", 9);
        sscWayMap.put("大小单双", 10);
    }

    private void initData() {
        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = CQSSC_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf02.setLimit(CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf03.setLimit(CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf04.setLimit(CQSSC_HONGQIU_LIMIT);
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
        hongqiuInf05.setLimit(CQSSC_HONGQIU_LIMIT);
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
// choosingCountTenthousand = (TextView) this.findViewById(R.id.cqssc_hongqiu01_text);
// choosingCountThousand = (TextView) this.findViewById(R.id.cqssc_hongqiu02_text);
// choosingCountHundred = (TextView) this.findViewById(R.id.cqssc_hongqiu03_text);
// choosingCountTen = (TextView) this.findViewById(R.id.cqssc_hongqiu04_text);
// choosingCountUnit = (TextView) this.findViewById(R.id.cqssc_hongqiu05_text);
// choosingCountDXDS1 = (TextView) this.findViewById(R.id.cqssc_hongqiu06_text);
// choosingCountDXDS2 = (TextView) this.findViewById(R.id.cqssc_hongqiu07_text);
        // setup the first section
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls01);
        // setup the second section
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls02);
        // setup the third section
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls03);
        redBallsLayout04 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls04);
        redBallsLayout05 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls05);
        redBallsLayout06 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls06);
        redBallsLayout07 = (NewBetBallsLayout) this.findViewById(R.id.cqssc_hongqiu_balls07);

        cqssc_bet_field_bg01 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls01_linear);
        cqssc_bet_field_bg02 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls02_linear);
        cqssc_bet_field_bg03 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls03_linear);
        cqssc_bet_field_bg04 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls04_linear);
        cqssc_bet_field_bg05 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls05_linear);
        cqssc_bet_field_bg06 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls06_linear);
        cqssc_bet_field_bg07 = (LinearLayout) this.findViewById(R.id.cqssc_hongqiu_balls07_linear);

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
        titlePopup = new PopMenu(CQSSCActivity.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayCQSSC,
                             LotteryUtils.moneyArrayCQSSC, 1, findViewById(R.id.top).getMeasuredWidth() - 20,
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

// private void initDefault(View view) {
// popupFirstLayout = (LinearLayout) view.findViewById(R.id.item_first_layout);
// popupsecondLayout = (LinearLayout) view.findViewById(R.id.item_second_layout);
// // popupThirdLayout = (LinearLayout) view.findViewById(R.id.item_third_layout);
// popupForthLayout = (LinearLayout) view.findViewById(R.id.item_fourth_layout);
// popupFifthLayout = (LinearLayout) view.findViewById(R.id.item_fifth_layout);
// popupSixthLayout = (LinearLayout) view.findViewById(R.id.item_sixth_layout);
// popupSeventhLayout = (LinearLayout) view.findViewById(R.id.item_seventh_layout);
// popupEighthLayout = (LinearLayout) view.findViewById(R.id.item_eighth_layout);
// popupNinthLayout = (LinearLayout) view.findViewById(R.id.item_ninth_layout);
// popupTenthLayout = (LinearLayout) view.findViewById(R.id.item_tenth_layout);
//
// cqsscWuxingtongxuan = (TextView) view.findViewById(R.id.cqssc_wuxingtongxuan);
// cqsscWuxingfushi = (TextView) view.findViewById(R.id.cqssc_wuxingfushi);
// cqsscSanxingzuliu = (TextView) view.findViewById(R.id.cqssc_sanxingzuliu);
// cqsscSanxingzusanfushi = (TextView) view.findViewById(R.id.cqssc_sanxingzusanfushi);
// cqsscSanxingfushi = (TextView) view.findViewById(R.id.cqssc_sanxingfushi);
// cqsscErxingzuxuan = (TextView) view.findViewById(R.id.cqssc_erxingzuxuan);
// cqsscErxingfushi = (TextView) view.findViewById(R.id.cqssc_erxingfushi);
// cqsscYixingfushi = (TextView) view.findViewById(R.id.cqssc_yixingfushi);
// cqsscDaxiaodanshuang = (TextView) view.findViewById(R.id.cqssc_daxiaodanshuang);
//
// cqsscFirstItem = (TextView) view.findViewById(R.id.radio_item_first);
// cqsscSecondItem = (TextView) view.findViewById(R.id.radio_item_second);
// cqsscForthItem = (TextView) view.findViewById(R.id.radio_item_forth);
// cqsscFifthItem = (TextView) view.findViewById(R.id.radio_item_fifth);
// cqsscSixthItem = (TextView) view.findViewById(R.id.radio_item_sixth);
// cqsscSeventhItem = (TextView) view.findViewById(R.id.radio_item_seventh);
// cqsscEighthItem = (TextView) view.findViewById(R.id.radio_item_eighth);
// cqsscNinthItem = (TextView) view.findViewById(R.id.radio_item_ninth);
// cqsscTenthItem = (TextView) view.findViewById(R.id.radio_item_tenth);
// }

// private void setTextColor(int index) {
// cqsscWuxingtongxuan.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscWuxingfushi.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscSanxingzuliu.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscSanxingzusanfushi.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscSanxingfushi.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscErxingzuxuan.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscErxingfushi.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscYixingfushi.setTextColor(getResources().getColor(R.color.light_purple));
// cqsscDaxiaodanshuang.setTextColor(getResources().getColor(R.color.light_purple));
//
// cqsscFirstItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscSecondItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscForthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscFifthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscSixthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscSeventhItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscEighthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscNinthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// cqsscTenthItem.setTextColor(getResources().getColor(R.color.dark_purple));
// if (index == 1) {
// cqsscWuxingtongxuan.setTextColor(Color.WHITE);
// cqsscFirstItem.setTextColor(Color.WHITE);
// }
// else if (index == 2) {
// cqsscWuxingfushi.setTextColor(Color.WHITE);
// cqsscSecondItem.setTextColor(Color.WHITE);
// }
// else if (index == 4) {
// cqsscSanxingzuliu.setTextColor(Color.WHITE);
// cqsscForthItem.setTextColor(Color.WHITE);
// }
// else if (index == 5) {
// cqsscSanxingzusanfushi.setTextColor(Color.WHITE);
// cqsscFifthItem.setTextColor(Color.WHITE);
// }
// else if (index == 6) {
// cqsscSanxingfushi.setTextColor(Color.WHITE);
// cqsscSixthItem.setTextColor(Color.WHITE);
// }
// else if (index == 7) {
// cqsscErxingzuxuan.setTextColor(Color.WHITE);
// cqsscSeventhItem.setTextColor(Color.WHITE);
// }
// else if (index == 8) {
// cqsscErxingfushi.setTextColor(Color.WHITE);
// cqsscEighthItem.setTextColor(Color.WHITE);
// }
// else if (index == 9) {
// cqsscYixingfushi.setTextColor(Color.WHITE);
// cqsscNinthItem.setTextColor(Color.WHITE);
// }
// else if (index == 10) {
// cqsscDaxiaodanshuang.setTextColor(Color.WHITE);
// cqsscTenthItem.setTextColor(Color.WHITE);
// }
// }

// private void iniSelectedItemBg() {
// popupFirstLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupsecondLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// // popupThirdLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupForthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupFifthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupSixthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupSeventhLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupEighthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupNinthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// popupTenthLayout.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// }

// private void initIcon(int LuckyType) {
// if (LuckyType == 1)
// popupFirstLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 2)
// popupsecondLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// // else if (LuckyType == 3)
// // popupThirdLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed.9);
// else if (LuckyType == 4)
// popupForthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 5)
// popupFifthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 6)
// popupSixthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 7)
// popupSeventhLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 8)
// popupEighthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 9)
// popupNinthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 10)
// popupTenthLayout.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// }

// private void showPopupViews() {
// LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
// View waySwitchLayout = mLayoutInflater.inflate(R.layout.cqssc_selected_dialog_swtich, null);
// initDefault(waySwitchLayout);
//
// popupFirstLayout.setOnClickListener(new TextView.OnClickListener() {
//
// @Override
// public void onClick(View v) {
// if (lotteryType != 1) {
// lotteryType = 1;
//
// databaseData.putString("cqssc_way", "cqssc_wxtx");
// databaseData.commit();
// clearBalls();
// title.setText("五星通选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(1);
// }
// });
//
// popupsecondLayout.setOnClickListener(new TextView.OnClickListener() {
//
// @Override
// public void onClick(View v) {
// if (lotteryType != 2) {
// lotteryType = 2;
// exzxFullBall(10);
// databaseData.putString("cqssc_way", "cqssc_wxfs");
// databaseData.commit();
// clearBalls();
// title.setText("五星直选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(2);
// }
// });
//
// /*
// * popupThirdLayout.setOnClickListener(new TextView.OnClickListener() {
// * @Override public void onClick(View v) { if (lotteryType != 3) { exzxFullBall(10);
// * databaseData.putString("cqssc_way", "cqssc_sixfs"); databaseData.commit(); clearBalls();
// * lotteryType = 3; title.setText("四星复式"); showWay(); showBallNum(); } mPopupWindow.dismiss();
// * lotteryIndroduce.setText("请注意，您现在选择的时时彩玩法是" + arrayTips[2] + "。"); setTextColor(3); } });
// */
//
// popupForthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 4) {
// lotteryType = 4;
// exzxFullBall(8);
// databaseData.putString("cqssc_way", "cqssc_sxzl");
// databaseData.commit();
// clearBalls();
// title.setText("三星组六");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(4);
// }
// });
//
// popupFifthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 5) {
// lotteryType = 5;
// exzxFullBall(10);
// databaseData.putString("cqssc_way", "cqssc_sxzs");
// databaseData.commit();
// clearBalls();
// title.setText("三星组三");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(5);
// }
// });
//
// popupSixthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 6) {
// lotteryType = 6;
// exzxFullBall(10);
// databaseData.putString("cqssc_way", "cqssc_sxfs");
// databaseData.commit();
// clearBalls();
// title.setText("三星直选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(6);
// }
// });
//
// popupSeventhLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 7) {
// lotteryType = 7;
// exzxFullBall(7);
// databaseData.putString("cqssc_way", "cqssc_exzx");
// databaseData.commit();
// clearBalls();
// title.setText("二星组选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(7);
// }
// });
//
// popupEighthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 8) {
// lotteryType = 8;
// exzxFullBall(10);
// databaseData.putString("cqssc_way", "cqssc_exfs");
// databaseData.commit();
// clearBalls();
// title.setText("二星直选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(8);
// }
// });
//
// popupNinthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 9) {
// lotteryType = 9;
// exzxFullBall(5);
// databaseData.putString("cqssc_way", "cqssc_yxfs");
// databaseData.commit();
// clearBalls();
// title.setText("一星直选");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(9);
// }
// });
//
// popupTenthLayout.setOnClickListener(new TextView.OnClickListener() {
// public void onClick(View v) {
// if (lotteryType != 10) {
// lotteryType = 10;
// exzxFullBall(4);
// databaseData.putString("cqssc_way", "cqssc_dxds");
// databaseData.commit();
// clearBalls();
// title.setText("大小单双");
// showWay();
// showBallNum();
// }
// mPopupWindow.dismiss();
// setTextColor(10);
// }
// });
//
// iniSelectedItemBg();
// initIcon(lotteryType);
// setTextColor(lotteryType);
// // topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
// mPopupWindow = new PopupWindow(this);
// mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// mPopupWindow.setWidth(findViewById(R.id.top).getMeasuredWidth() - 20);
// mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
// mPopupWindow.setOutsideTouchable(true);
// mPopupWindow.setFocusable(true);
// mPopupWindow.setContentView(waySwitchLayout);
// // mPopupWindow.showAsDropDown(topBgLinear);
// showPopupCenter(mPopupWindow);
// topArrow.setImageResource(R.drawable.arrow_up);
// mPopupWindow.setOnDismissListener(new OnDismissListener() {
//
// @Override
// public void onDismiss() {
// topArrow.setImageResource(R.drawable.arrow_down);
// // topBgLinear.setBackgroundResource(R.drawable.top_bg);
// }
// });
// }

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
        redBallsLayout01.initData(hongqiuInf01, bigBallViews, this);
        redBallsLayout01.setFullListener(this);
        redBallsLayout01.setTouchMoveListener(this);

        redBallsLayout02.initData(hongqiuInf02, bigBallViews, this);
        redBallsLayout02.setFullListener(this);
        redBallsLayout02.setTouchMoveListener(this);

        redBallsLayout03.initData(hongqiuInf03, bigBallViews, this);
        redBallsLayout03.setFullListener(this);
        redBallsLayout03.setTouchMoveListener(this);

        redBallsLayout04.initData(hongqiuInf04, bigBallViews, this);
        redBallsLayout04.setFullListener(this);
        redBallsLayout04.setTouchMoveListener(this);

        redBallsLayout05.initData(hongqiuInf05, bigBallViews, this);
        redBallsLayout05.setFullListener(this);
        redBallsLayout05.setTouchMoveListener(this);

        redBallsLayout06.initData(hongqiuInf06, bigBallViews, this);
        redBallsLayout06.setFullListener(this);
        redBallsLayout06.setTouchMoveListener(this);

        redBallsLayout07.initData(hongqiuInf07, bigBallViews, this);
        redBallsLayout07.setFullListener(this);
        redBallsLayout07.setTouchMoveListener(this);

        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initLotteryIntroduce();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lotteryType = bundle.getInt("bet_way");
            if (lotteryType == 1) {
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_wxtx");
            }
            else if (lotteryType == 2) {
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_wxfs");
            }
// else if (lotteryType == 3) {
// exzxFullBall(10);
// databaseData.putString("cqssc_way", "cqssc_sixfs");
// }
            else if (lotteryType == 4) {
// exzxFullBall(8);
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_sxzl");
            }
            else if (lotteryType == 5) {
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_sxzs");
            }
            else if (lotteryType == 6) {
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_sxfs");
            }
            else if (lotteryType == 7) {
// exzxFullBall(7);
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_exzx");
            }
            else if (lotteryType == 8) {
                exzxFullBall(10);
                databaseData.putString("cqssc_way", "cqssc_exfs");
            }
            else if (lotteryType == 9) {
                exzxFullBall(5);
                databaseData.putString("cqssc_way", "cqssc_yxfs");
            }
            else if (lotteryType == 10) {
                exzxFullBall(4);
                databaseData.putString("cqssc_way", "cqssc_dxds");
            }
            else {
                resetLotteryType();
            }
            databaseData.commit();
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
        if (lotteryType == 1 || lotteryType == 2)
            index_num = lotteryType - 1;
        else
            index_num = lotteryType - 2;
    }

    // 限制选球个数
    private void exzxFullBall(int limit) {
        if (lotteryType == 7 || lotteryType == 4) {
            hongqiuInf01.setLimit(limit);
            hongqiuInf05.setLimit(CQSSC_HONGQIU_LIMIT);
        }
        else if (lotteryType == 9) {
            hongqiuInf05.setLimit(limit);
            hongqiuInf01.setLimit(CQSSC_HONGQIU_LIMIT);
        }
        else {
            hongqiuInf01.setLimit(CQSSC_HONGQIU_LIMIT);
            hongqiuInf05.setLimit(CQSSC_HONGQIU_LIMIT);
        }
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("cqssc_way", "cqssc_wxtx");
        if (sdWay.equals("cqssc_wxtx")) {// 五星通选
            lotteryType = 1;
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_wxfs")) {// 五星直选
            lotteryType = 2;
            exzxFullBall(10);
        }
// else if (sdWay.equals("cqssc_sixfs")) {
        // lotteryType = 3;
// exzxFullBall(10);
// }
        else if (sdWay.equals("cqssc_sxzl")) {// 三星组六
            lotteryType = 4;
// exzxFullBall(8);
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_sxzs")) {// 三星组三
            lotteryType = 5;
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_sxfs")) {// 三星直选
            lotteryType = 6;
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_exzx")) {// 二星组选
            lotteryType = 7;
// exzxFullBall(7);
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_exfs")) {// 二星直选
            lotteryType = 8;
            exzxFullBall(10);
        }
        else if (sdWay.equals("cqssc_yxfs")) {// 一星直选
            lotteryType = 9;
            exzxFullBall(5);
        }
        else if (sdWay.equals("cqssc_dxds")) {// 大小单双
            lotteryType = 10;
            exzxFullBall(4);
        }
    }

    // display the appointed way of the 3d
    private void showWay() {
        initLotteryIntroduce();
        if (lotteryType == 1) {
            title.setText("五星通选");
            selectInfo.setText(CQSSC_TIPS01);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.VISIBLE);
            redBallsLayout04.setVisibility(View.VISIBLE);
            redBallsLayout05.setVisibility(View.VISIBLE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg02.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg03.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg04.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg05.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 2) {
            title.setText("五星直选");
            selectInfo.setText(CQSSC_TIPS02);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.VISIBLE);
            redBallsLayout04.setVisibility(View.VISIBLE);
            redBallsLayout05.setVisibility(View.VISIBLE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.VISIBLE);
// choosingCountHundred.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg02.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg03.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg04.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg05.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }

        else if (lotteryType == 4) {
            title.setText("三星组六");
            selectInfo.setText(CQSSC_TIPS04);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.GONE);
            redBallsLayout05.setVisibility(View.GONE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.GONE);
            cqssc_bet_field_bg05.setVisibility(View.GONE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 5) {
            title.setText("三星组三");
            selectInfo.setText(CQSSC_TIPS05);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.GONE);
            redBallsLayout05.setVisibility(View.GONE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.GONE);
            cqssc_bet_field_bg05.setVisibility(View.GONE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 6) {
            title.setText("三星直选");
            selectInfo.setText(CQSSC_TIPS06);
            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.VISIBLE);
            redBallsLayout04.setVisibility(View.VISIBLE);
            redBallsLayout05.setVisibility(View.VISIBLE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.GONE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.GONE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg04.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg05.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 7) {
            title.setText("二星组选");
            selectInfo.setText(CQSSC_TIPS07);
            redBallsLayout01.setVisibility(View.VISIBLE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.GONE);
            redBallsLayout05.setVisibility(View.GONE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.VISIBLE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.GONE);
            cqssc_bet_field_bg05.setVisibility(View.GONE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 8) {
            title.setText("二星直选");
            selectInfo.setText(CQSSC_TIPS08);
            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.VISIBLE);
            redBallsLayout05.setVisibility(View.VISIBLE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.GONE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.GONE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg05.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 9) {
            title.setText("一星直选");
            selectInfo.setText(CQSSC_TIPS09);
            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.GONE);
            redBallsLayout05.setVisibility(View.VISIBLE);
            redBallsLayout06.setVisibility(View.GONE);
            redBallsLayout07.setVisibility(View.GONE);
// choosingCountTenthousand.setVisibility(View.GONE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.VISIBLE);
// choosingCountDXDS1.setVisibility(View.GONE);
// choosingCountDXDS2.setVisibility(View.GONE);
            cqssc_bet_field_bg01.setVisibility(View.GONE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.GONE);
            cqssc_bet_field_bg05.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg06.setVisibility(View.GONE);
            cqssc_bet_field_bg07.setVisibility(View.GONE);
        }
        else if (lotteryType == 10) {
            title.setText("大小单双");
            selectInfo.setText(CQSSC_TIPS010);
            redBallsLayout01.setVisibility(View.GONE);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
            redBallsLayout04.setVisibility(View.GONE);
            redBallsLayout05.setVisibility(View.GONE);
            redBallsLayout06.setVisibility(View.VISIBLE);
            redBallsLayout07.setVisibility(View.VISIBLE);
// choosingCountTenthousand.setVisibility(View.GONE);
// choosingCountThousand.setVisibility(View.GONE);
// choosingCountHundred.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);
// choosingCountDXDS1.setVisibility(View.VISIBLE);
// choosingCountDXDS2.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg01.setVisibility(View.GONE);
            cqssc_bet_field_bg02.setVisibility(View.GONE);
            cqssc_bet_field_bg03.setVisibility(View.GONE);
            cqssc_bet_field_bg04.setVisibility(View.GONE);
            cqssc_bet_field_bg05.setVisibility(View.GONE);
            cqssc_bet_field_bg06.setVisibility(View.VISIBLE);
            cqssc_bet_field_bg07.setVisibility(View.VISIBLE);
        }
        // lotteryIndroduce.setText("请注意，您现在选择的时时彩玩法是" + arrayTips[lotteryType - 1] + "。");
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
            int fourthLength = nums[3].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayout04.chooseBall(num);
            }
            int fifthLength = nums[4].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[4].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 2) {
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
            int fourthLength = nums[3].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayout04.chooseBall(num);
            }
            int fifthLength = nums[4].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[4].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 3) {
            String[] nums = lotteryMode[0].split(",");
            int secondLength = nums[0].length();
            for (int i = 0; i < secondLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu02.get(num).setChoosed(true);
                redBallsLayout02.chooseBall(num);
            }
            int thirdLength = nums[1].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayout03.chooseBall(num);
            }
            int fourthLength = nums[2].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayout04.chooseBall(num);
            }
            int fifthLength = nums[3].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[3].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 4) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
        }
        else if (lotteryType == 5) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
        }
        else if (lotteryType == 6) {
            String[] nums = lotteryMode[0].split(",");
            int thirdLength = nums[0].length();
            for (int i = 0; i < thirdLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu03.get(num).setChoosed(true);
                redBallsLayout03.chooseBall(num);
            }
            int fourthLength = nums[1].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayout04.chooseBall(num);
            }
            int fifthLength = nums[2].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[2].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 7) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
        }
        else if (lotteryType == 8) {
            String[] nums = lotteryMode[0].split(",");
            int fourthLength = nums[0].length();
            for (int i = 0; i < fourthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu04.get(num).setChoosed(true);
                redBallsLayout04.chooseBall(num);
            }
            int fifthLength = nums[1].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 9) {
            String[] nums = lotteryMode[0].split(",");
            int fifthLength = nums[0].length();
            for (int i = 0; i < fifthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu05.get(num).setChoosed(true);
                redBallsLayout05.chooseBall(num);
            }
        }
        else if (lotteryType == 10) {
            String[] nums = lotteryMode[0].split(",");
            int sixthLength = nums[0].length();
            for (int i = 0; i < sixthLength; i++) {
                int num = Integer.valueOf(nums[0].substring(i, i + 1));
                hongqiu06.get(num - 1).setChoosed(true);
                redBallsLayout06.chooseBall(num - 1);
            }
            int seventhLength = nums[1].length();
            for (int i = 0; i < seventhLength; i++) {
                int num = Integer.valueOf(nums[1].substring(i, i + 1));
                hongqiu07.get(num - 1).setChoosed(true);
                redBallsLayout07.chooseBall(num - 1);
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
        // TODO Auto-generated method stub
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
// else if (lotteryType == 3)
// return getBallsBetThirdKindInf();
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

    private String getBallsBetSeventhKindInf() {// 二星组选单复式
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
        if (hongqiuInf01.getCount() > 2) {
            betBallText.append("217:");// 二星组选组合(二星组选复式)
        }
        else {
            betBallText.append("211:");// 二星组选单式
        }
        return betBallText.toString();
    }

    private String getBallsBetEighthKindInf() {// 二星单复式
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

    private String getBallsBetNinthKindInf() {// 一星
        StringBuilder betBallText = new StringBuilder();
        int hongLength05 = hongqiu05.size();
        for (int i = 0; i < hongLength05; i++) {
            if (hongqiu05.get(i).isChoosed())
                betBallText.append(i);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
// if (hongqiuInf05.getCount() > 1) {
// betBallText.append("102:");
// }
// else {
// betBallText.append("101:");
// }
        betBallText.append("101:");
        return betBallText.toString();
    }

    private String getBallsBetTenthKindInf() {// 大小单双
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
// else if (lotteryType == 3)
// return getBallsDisplayThirdKindInf();
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
        betBallText.append("[二星组选] ");
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

    private String getBallsDisplayEighthKindInf() {
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
        if (lotteryType == 1) {
            if (hongqiuInf01.getCount() < 1)
                inf = "万位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < 1)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < 1)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 2) {
            if (hongqiuInf01.getCount() < 1)
                inf = "万位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < 1)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < 1)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 3) {
            if (hongqiuInf02.getCount() < 1)
                inf = "千位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < 1)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 4) {
            if (hongqiuInf01.getCount() < 3)
                inf = CQSSC_TIPS04;
        }
        else if (lotteryType == 5) {
            if (hongqiuInf01.getCount() < 2)
                inf = CQSSC_TIPS05;
        }
        else if (lotteryType == 6) {
            if (hongqiuInf03.getCount() < 1)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf04.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 7) {
            if (hongqiuInf01.getCount() < 2)
                inf = CQSSC_TIPS07;
        }
        else if (lotteryType == 8) {
            if (hongqiuInf04.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 9) {
            if (hongqiuInf05.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 10) {
            if (hongqiuInf06.getCount() < 1)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf07.getCount() < 1)
                inf = "个位请至少输入1个号码";
        }
        // 组三单式
// if (lotteryType == 2) {
// if (hongqiuInf01.getCount() == 1 && hongqiuInf03.getCount() == 1) {
// checkZuSan();
// if (first == third)
// inf = "组三单复数百位和个位不可以选相同号码";
// }
// }
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
        for (int i = 0; i < 10; i++) {
            if (lotteryType == i + 1) {

                if (lotteryType == 5) {
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
        int num01 = rd.nextInt(CQSSC_HONGQIU_LENGTH);
        int num02 = rd.nextInt(CQSSC_HONGQIU_LENGTH);
        int num03 = rd.nextInt(CQSSC_HONGQIU_LENGTH);
        int num04 = rd.nextInt(CQSSC_HONGQIU_LENGTH);
        int num05 = rd.nextInt(CQSSC_HONGQIU_LENGTH);
        int num06 = rd.nextInt(4);
        int num07 = rd.nextInt(4);
        if (lotteryType == 1) {
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayout02.chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 2) {
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayout02.chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 3) {
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayout02.chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 4) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(3, CQSSC_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 5) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, CQSSC_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 6) {
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 7) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, CQSSC_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 8) {
            hongqiu04.get(num04).setChoosed(true);
            redBallsLayout04.chooseBall(num04);
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 9) {
            hongqiu05.get(num05).setChoosed(true);
            redBallsLayout05.chooseBall(num05);
        }
        else if (lotteryType == 10) {
            hongqiu06.get(num06).setChoosed(true);
            redBallsLayout06.chooseBall(num06);
            hongqiu07.get(num07).setChoosed(true);
            redBallsLayout07.chooseBall(num07);
        }
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        if (lotteryType == 5) {
            betMoney = 4;
        }
        else
            betMoney = 2;
        invalidateAll();
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        if (lotteryType == 1) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
            redBallsLayout04.resetBalls();
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 2) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
            redBallsLayout04.resetBalls();
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 3) {
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
            redBallsLayout04.resetBalls();
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 4 || lotteryType == 5) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 6) {
            redBallsLayout03.resetBalls();
            redBallsLayout04.resetBalls();
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 7) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 8) {
            redBallsLayout04.resetBalls();
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 9) {
            redBallsLayout05.resetBalls();
        }
        else if (lotteryType == 10) {
            redBallsLayout06.resetBalls();
            redBallsLayout07.resetBalls();
        }
        resetInf();
    }

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
            selectInfo.setText(CQSSC_TIPS010);
    }

    public void initLotteryIntroduce() {
// lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#CD2626\">" + "-"
// + "</font>元,您将盈利<font color=\"#CD2626\">" + "-" + "</font>元"));
    }

    @Override
    public void onBallClickFull(int ballType) {
        if (ballType == 1) {
            if (lotteryType != 7 && lotteryType != 4 && lotteryType != 9) {
                ViewUtil.showTipsToast(this, "您只能选" + CQSSC_HONGQIU_LIMIT + "个红球");
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
            ViewUtil.showTipsToast(this, "您只能选" + CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 3) {
            ViewUtil.showTipsToast(this, "您只能选" + CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 4) {
            ViewUtil.showTipsToast(this, "您只能选" + CQSSC_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 5) {
            if (lotteryType != 9) {
                ViewUtil.showTipsToast(this, "您只能选" + CQSSC_HONGQIU_LIMIT + "个红球");
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
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();
        int hongNumber04 = hongqiuInf04.getCount();
        int hongNumber05 = hongqiuInf05.getCount();
        int hongNumber06 = hongqiuInf06.getCount();
        int hongNumber07 = hongqiuInf07.getCount();
        if ((hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
            hongNumber05 == 0 && hongNumber06 == 0 && hongNumber07 == 0) == false) {
            enableClearBtn();
        }
        if (lotteryType == 1) {
            if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
                hongNumber05 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 ||
                    hongNumber05 == 0) {
                    // 五星通选限制单式
                    invalidateNum();
                }
                else {
                    // 五星通选限制单式
                    refreshMoney = true;
                    betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        else if (lotteryType == 2) {
            if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
                hongNumber05 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 ||
                    hongNumber05 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
// else if (lotteryType == 3) {
// if (hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 && hongNumber05 == 0) {
// resetInf();
// }
// else {
// if (hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 || hongNumber05 == 0) {
// invalidateNum();
// }
// else {
// refreshMoney = true;
// betNumber = hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
// betMoney = betNumber * 2 * 1;
// }
// }
// }
        else if (lotteryType == 4) {
            if (hongNumber01 >= 3) {
                refreshMoney = true;
                betNumber = (int) (MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3));
                betMoney = betNumber * 2 * 1;
                invalidateAll();
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else {
                resetInf();
            }
        }
        else if (lotteryType == 5) {
            if (hongNumber01 >= 2) {
                refreshMoney = true;
                betNumber = MathUtil.factorial(hongNumber01, 2);
                betMoney = betNumber * 2 * 1;
                invalidateAll();
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else
                resetInf();
        }
        else if (lotteryType == 6) {
            if (hongNumber03 == 0 && hongNumber04 == 0 && hongNumber05 == 0) {
                resetInf();
            }
            else {
                if (hongNumber03 == 0 || hongNumber04 == 0 || hongNumber05 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    betNumber = hongNumber03 * hongNumber04 * hongNumber05;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
// else if (lotteryType == 7) {
// if (hongNumber01 >= 2) {
// refreshMoney = true;
// betNumber =
// (int) (MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2)) +
// hongNumber01;
// betMoney = betNumber * 2 * 1;
// invalidateAll();
// }
// else if (hongNumber01 > 0)
// invalidateNum();
// else {
// resetInf();
// }
// }
        else if (lotteryType == 7) {
            if (hongNumber01 >= 2) {
                refreshMoney = true;
                betNumber = (int) (MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2));
                betMoney = betNumber * 2 * 1;
                invalidateAll();
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else {
                resetInf();
            }
        }
        else if (lotteryType == 8) {
            if (hongNumber04 == 0 && hongNumber05 == 0) {
                resetInf();
            }
            else {
                if (hongNumber04 == 0 || hongNumber05 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    betNumber = hongNumber04 * hongNumber05;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        else if (lotteryType == 9) {
            if (hongNumber05 == 0) {
                resetInf();
            }
            else {
                if (hongNumber05 == 0) {
                    invalidateNum();
                }
                else {
                    if (ballType == 5) {
                        for (int i = 0; i < CQSSC_HONGQIU_LENGTH; i++) {
                            if (i != index) {
                                if (hongqiu05.get(i).isChoosed() == true) {
                                    hongqiu05.get(i).setChoosed(false);
                                    hongqiuInf05.setCount(1);
                                }
                            }
                        }
                        redBallsLayout05.refreshAllBall();
                    }
                    refreshMoney = true;
                    betNumber = hongqiuInf05.getCount();
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        else if (lotteryType == 10) {
            if (hongNumber06 == 0 && hongNumber07 == 0) {
                resetInf();
            }
            else {
                if (hongNumber06 == 0 || hongNumber07 == 0) {
                    if (ballType == 6) {
                        for (int i = 0; i < 4; i++) {
                            if (i != index) {
                                if (hongqiu06.get(i).isChoosed() == true) {
                                    hongqiu06.get(i).setChoosed(false);
                                    hongqiuInf06.setCount(1);
                                }
                            }
                        }
                        redBallsLayout06.refreshAllBall();
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
                        redBallsLayout07.refreshAllBall();
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
                        redBallsLayout06.refreshAllBall();
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
                        redBallsLayout07.refreshAllBall();
                    }
                    refreshMoney = true;
                    betNumber = hongqiuInf06.getCount() * hongqiuInf07.getCount();
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
        else {
            initLotteryIntroduce();
        }
    }

    private int getReword(int lotteryType) {
        return reword[lotteryType];
    }

    private int findSameNum() {
        int n = 0;
        for (int i = 0; i < CQSSC_HONGQIU_LENGTH; i++) {
            Boolean first = hongqiu01.get(i).isChoosed();
            Boolean second = hongqiu03.get(i).isChoosed();
            if (first && second)
                n = n + 1;
        }
        return n;
    }

    protected void showBallNum() {
        if (lotteryType == 1) {
// choosingCountTenthousand.setText("万位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountThousand.setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCountHundred.setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu01.setText("万位");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 2) {
// choosingCountTenthousand.setText("万位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountThousand.setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCountHundred.setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu01.setText("万位");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 3) {
// choosingCountThousand.setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCountHundred.setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu02.setText("千位");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 4) {
// choosingCountTenthousand.setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 5) {
// choosingCountTenthousand.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 6) {
// choosingCountHundred.setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu03.setText("百位");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 7) {
// choosingCountTenthousand.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 8) {
// choosingCountTen.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu04.setText("十位");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 9) {
// choosingCountUnit.setText("个位：" + hongqiuInf05.getCount() + "/1个");
            flagHongqiu05.setText("个位");
        }
        else if (lotteryType == 10) {
// choosingCountDXDS1.setText("十位：" + hongqiuInf06.getCount() + "/1个");
// choosingCountDXDS2.setText("个位：" + hongqiuInf07.getCount() + "/1个");
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
        bundel.putString("lottery_name", "时时彩游戏规则");
        bundel.putString("lottery_help", "help_new/ssc.html");
        intent.putExtras(bundel);
        intent.setClass(CQSSCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "时时彩走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=cqssc&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(CQSSCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(CQSSCActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "cqssc");
        if (lotteryType == 1)
            bundle.putString("lotteryTypeWXTX", "1");
        else if (lotteryType == 2)
            bundle.putString("lotteryTypeWXFS", "2");
        else if (lotteryType == 3)
            bundle.putString("lotteryTypeSIXFS", "3");
        else if (lotteryType == 4)
            bundle.putString("lotteryTypeSXZL", "4");
        else if (lotteryType == 5)
            bundle.putString("lotteryTypeSXZS", "5");
        else if (lotteryType == 6)
            bundle.putString("lotteryTypeSXFS", "6");
        else if (lotteryType == 7)
            bundle.putString("lotteryTypeEXZX", "7");
        else if (lotteryType == 8)
            bundle.putString("lotteryTypeEXFS", "8");
        else if (lotteryType == 9)
            bundle.putString("lotteryTypeYXFS", "9");
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
                            analyseHundred[i] =
                                SEARCHNUM[searchType - 1] + "期内冷门，开出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseHundred[i] =
                                SEARCHNUM[searchType - 1] + "期内热门，开出<font color='red'>" + count + "</font>次";
                        else
                            analyseHundred[i] = SEARCHNUM[searchType - 1] + "期内开出" + count + "次";
                        ((BallTextView) redBallsLayout01.getChildAt(i)).setOpenCount(analyseHundred[i]);
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
                        ((BallTextView) redBallsLayout02.getChildAt(i)).setOpenCount(analyseTen[i]);
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
                        ((BallTextView) redBallsLayout03.getChildAt(i)).setOpenCount(analyseUnit[i]);
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
                if (lotteryType == 1) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    redBallsLayout02.chooseBall(vertor[1]);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[2]);
                    hongqiu04.get(vertor[3]).setChoosed(true);
                    redBallsLayout04.chooseBall(vertor[3]);
                    hongqiu05.get(vertor[4]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[4]);
                }
                else if (lotteryType == 2) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    redBallsLayout02.chooseBall(vertor[1]);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[2]);
                    hongqiu04.get(vertor[3]).setChoosed(true);
                    redBallsLayout04.chooseBall(vertor[3]);
                    hongqiu05.get(vertor[4]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[4]);
                }
                else if (lotteryType == 3) {
                    hongqiu02.get(vertor[0]).setChoosed(true);
                    redBallsLayout02.chooseBall(vertor[0]);
                    hongqiu03.get(vertor[1]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[1]);
                    hongqiu04.get(vertor[2]).setChoosed(true);
                    redBallsLayout04.chooseBall(vertor[2]);
                    hongqiu05.get(vertor[3]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[3]);
                }
                else if (lotteryType == 4) {
                    for (int i = 0; i < 3; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 5) {
                    for (int i = 0; i < 2; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 6) {
                    hongqiu03.get(vertor[0]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[0]);
                    hongqiu04.get(vertor[1]).setChoosed(true);
                    redBallsLayout04.chooseBall(vertor[1]);
                    hongqiu05.get(vertor[2]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[2]);
                }
                else if (lotteryType == 7) {
                    for (int i = 0; i < 2; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 8) {
                    hongqiu04.get(vertor[0]).setChoosed(true);
                    redBallsLayout04.chooseBall(vertor[0]);
                    hongqiu05.get(vertor[1]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[1]);
                }
                else if (lotteryType == 9) {
                    hongqiu05.get(vertor[0]).setChoosed(true);
                    redBallsLayout05.chooseBall(vertor[0]);
                }
                else if (lotteryType == 10) {
                    hongqiu06.get(vertor[0]).setChoosed(true);
                    redBallsLayout06.chooseBall(vertor[0]);
                    hongqiu07.get(vertor[1]).setChoosed(true);
                    redBallsLayout07.chooseBall(vertor[1]);
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
        cqsscNum01 = 0;
        cqsscNum02 = 0;
        cqsscNum03 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                cqsscNum01 = cqsscNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                cqsscNum02 = cqsscNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                cqsscNum03 = cqsscNum03 + 1;
            }
        }
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int cqsscNum01;
    private int cqsscNum02;
    private int cqsscNum03;

    @Override
    protected void searchLuckyNum() {

        if (getQ_code().equals(",,")) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (cqsscNum01 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理百位上1个红球");
            }
            else if (cqsscNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理十位上1个红球");
            }
            else if (cqsscNum03 > 1) {
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
                bundle.putString("kind", "cqssc");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(CQSSCActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int index, String tabName) {
        lotteryType = sscWayMap.get(tabName);
        if (tabName.equals("五星通选") || tabName.equals("五星直选"))
            index_num = lotteryType - 1;
        else
            index_num = lotteryType - 2;

        initExzxFullBall(lotteryType);
        databaseData.putString("cqssc_way", LotteryUtils.CQSSCWay[lotteryType - 1]);
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
