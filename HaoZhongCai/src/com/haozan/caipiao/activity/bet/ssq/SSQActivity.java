package com.haozan.caipiao.activity.bet.ssq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryDiviningActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.activity.lotterychart.TrendChartActivity;
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
import com.umeng.analytics.MobclickAgent;

public class SSQActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener, OnItemClickListener {
    private static final int TREND_CHART = 3;

    private static final String SSQ_TIPS1 = "请选择至少6个红球和1个蓝球";
    private static final String SSQ_TIPS2 = "胆1-5个，胆+拖>6个";

    public static final int SSQ_HONGQIU_LENGTH = 33;
    public static final int SSQ_HONGQIU_LIMIT = 20;
    public static final int SSQ_HONGQIU_MIN = 6;
    public static final int SSQ_LANQIU_LENGTH = 16;
    public static final int SSQ_LANQIU_LIMIT = 16;
    public static final int SSQ_LANQIU_MIN = 1;

    private static final int DROP_BALLS = 7;
    private static final int SHOW_BALLS = 8;
    private static final int CHOOSE_BALLS = 9;

    private static String hotCondintion = null;
    private String[] redBallTips;
    private String[] blueBallTips;
    // red ball variable
    private ArrayList<BetBall> hongqiu1;
    // 这一组球的信息，比如颜色，每个球的信息等
    private BetBallsData hongqiuInf1;
    private NewBetBallsLayout redBallsLayout1;
    private ArrayList<BetBall> hongqiu2;
    // 这一组球的信息，比如颜色，每个球的信息等
    private BetBallsData hongqiuInf2;
    private NewBetBallsLayout redBallsLayout2;
    // 每个球对应的信息，比如球的id，是否选中，显示的文字信息等
    private ArrayList<BetBall> lanqiu;
    private BetBallsData lanqiuInf;
    private NewBetBallsLayout blueBallsLayout;

    private TextView choosingCountRed1;
    private LinearLayout redLayout2;
    private TextView choosingCountRed2;
    private TextView choosingCountBlue;
    private RelativeLayout termLayout;
    private int lotteryType = 1;
    private PopMenu titlePopup;
    private int index_num = 0;

    private TextView flagHongqiu01, flagHongqiu02, flagLanqiu, flagLengre01, flagLengre02, flagLengre03,
        flagYilou01, flagYilou02, flagYilou03;
    private TextView selectInfo;

    @Override
    public void setKind() {
        this.kind = "ssq";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.ssq);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        hongqiuInf1 = new BetBallsData();
        hongqiu1 = new ArrayList<BetBall>();
        int redLength = SSQ_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu1.add(ball);
        }
        hongqiuInf1.setBetBalls(hongqiu1);
        hongqiuInf1.setCount(0);
        hongqiuInf1.setColor("red");
        hongqiuInf1.setLimit(SSQ_HONGQIU_LIMIT);
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
        hongqiuInf2.setLimit(SSQ_HONGQIU_LIMIT);
        hongqiuInf2.setBallType(2);

        // init blue setion
        lanqiuInf = new BetBallsData();
        lanqiu = new ArrayList<BetBall>();
        int blueLength = SSQ_LANQIU_LENGTH;
        for (int i = 0; i < blueLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            lanqiu.add(ball);
        }
        lanqiuInf.setBetBalls(lanqiu);
        lanqiuInf.setCount(0);
        lanqiuInf.setColor("blue");
        lanqiuInf.setLimit(SSQ_LANQIU_LIMIT);
        lanqiuInf.setBallType(3);
    }

    protected void setupViews() {
        super.setupViews();

        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        choosingCountRed1 = (TextView) this.findViewById(R.id.ssq_hongqiu_text1);
        redLayout2 = (LinearLayout) this.findViewById(R.id.ssq_hongqiu2);
        choosingCountRed2 = (TextView) this.findViewById(R.id.ssq_hongqiu_text2);
        choosingCountBlue = (TextView) this.findViewById(R.id.ssq_lanqiu_text);
        // setup red setion
        redBallsLayout1 = (NewBetBallsLayout) this.findViewById(R.id.ssq_hongqiu_balls1);
        redBallsLayout2 = (NewBetBallsLayout) this.findViewById(R.id.ssq_hongqiu_balls2);
        // setup blue setion
        blueBallsLayout = (NewBetBallsLayout) this.findViewById(R.id.ssq_lanqiu_balls);
// hotNumStatisticsLayout.setVisibility(View.VISIBLE);
// hotNumStatisticsLayout.setClickable(true);
// omitStatistticsLayout.setVisibility(View.VISIBLE);
// omitStatistticsLayout.setClickable(true);
        showSetting.setVisibility(View.VISIBLE);
        omiRela.setVisibility(View.VISIBLE);
        hotnumRela.setVisibility(View.VISIBLE);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
// topBgLinear= (RelativeLayout)findViewById(R.id.top_bg_linear);
        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagLengre01 = (TextView) findViewById(R.id.tv_flag_lengre01);
        flagYilou01 = (TextView) findViewById(R.id.tv_flag_yilou01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagLengre02 = (TextView) findViewById(R.id.tv_flag_lengre02);
        flagYilou02 = (TextView) findViewById(R.id.tv_flag_yilou02);
        flagLanqiu = (TextView) findViewById(R.id.tv_flag_lanqiu01);
        flagLengre03 = (TextView) findViewById(R.id.tv_flag_lengre03);
        flagYilou03 = (TextView) findViewById(R.id.tv_flag_yilou03);
        selectInfo = (TextView) findViewById(R.id.select_info);

        hotGrid.setOnItemClickListener(this);
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        // init red ball data
        redBallsLayout1.initData(hongqiuInf1, bigBallViews, this);
        redBallsLayout1.setFullListener(this);
        redBallsLayout1.setTouchMoveListener(this);

        redBallsLayout2.initData(hongqiuInf2, bigBallViews, this);
        redBallsLayout2.setFullListener(this);
        redBallsLayout2.setTouchMoveListener(this);

        // init blue ball date
        blueBallsLayout.initData(lanqiuInf, bigBallViews, this);
        blueBallsLayout.setFullListener(this);
        blueBallsLayout.setTouchMoveListener(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("bet_way");
            if (type != 0) {
                lotteryType = type;
                if (lotteryType == 1)
                    databaseData.putString("ssq_way", "ssq_standard");
                else if (lotteryType == 2)
                    databaseData.putString("ssq_way", "ssq_special");
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
        if (hotCondintion == null)
            getAnalyseData();
        else
            analyseData(hotCondintion);
        index_num = lotteryType - 1;
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("ssq_way", "ssq_standard");
        if (sdWay.equals("ssq_standard")) {
            lotteryType = 1;
        }
        else if (sdWay.equals("ssq_special")) {
            lotteryType = 2;
        }
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    private void showPopView() {
        titlePopup = new PopMenu(SSQActivity.this, true);
        titlePopup.setLotteryType("ssq");
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArraySSQ, LotteryUtils.moneyArraySSQ,
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

    // 重置双色球投注号码格式信息显示
    @Override
    protected void initDispalyInf() {
        if (lotteryType == 1) {
            selectInfo.setText(SSQ_TIPS1);
        }
        else if (lotteryType == 2) {
            selectInfo.setText(SSQ_TIPS2);
        }
    }

    // 显示玩法信息
    private void showWay() {
        initDispalyInf();
        if (lotteryType == 1) {
            luckyBallSelect.setVisibility(View.VISIBLE);// 生辰选号
            shakeRela.setVisibility(View.VISIBLE);
            shakeLockView.setVisibility(View.VISIBLE);
            shakeLock = false;
            initShakeLock();// 锁图标改变
            random.setEnabled(true);// 机选按钮可用
            random.setVisibility(View.VISIBLE);
            random.setTextColor(getResources().getColor(R.color.light_white));
            redLayout2.setVisibility(View.GONE);// 拖号区设为不可见
            title.setText("双色球标准");
            hongqiuInf1.setLimit(20);// 设置拖码的可选个数

            flagHongqiu01.setText("红球");
        }
        else if (lotteryType == 2) {
            luckyBallSelect.setVisibility(View.INVISIBLE);// 胆拖时设置生辰选号不可用
            luckyBallSelect.setClickable(false);
            shakeRela.setVisibility(View.GONE);
            shakeLockView.setVisibility(View.GONE);
            shaking = false;
            shakeLock = true;// 胆拖时摇动选号不可用（将图标隐藏）
            random.setEnabled(false);// 机选按钮设为不可用
            random.setTextColor(getResources().getColor(R.color.gray));
            random.setVisibility(View.INVISIBLE);
            redLayout2.setVisibility(View.VISIBLE);
            hongqiuInf1.setLimit(5);// 设置胆号区最多可选5个红球
            title.setText("双色球胆拖");

            flagHongqiu01.setText("红胆");
        }
        showBallNum();
    }

    protected void defaultNum(String betCode) {
        String[] lotteryMode = betCode.split("\\:");
        // 如果是胆拖玩法
        if (lotteryMode.length > 1 && lotteryMode[2].equals("5")) {
            String[] lotteryNum = lotteryMode[0].split("\\|");
            String[] lotteryRed = lotteryNum[0].split("\\$");
            String[] danCode = lotteryRed[0].split(",");
            String[] tuoCode = lotteryRed[1].split(",");
            String[] blueBall = lotteryNum[1].split(",");
            int danLength = danCode.length;
            hongqiuInf1.setCount(danLength);
            for (int i = 0; i < danLength; i++) {
                hongqiu1.get(Integer.valueOf(danCode[i]) - 1).setChoosed(true);
            }
            redBallsLayout1.refreshAllBall();
            int tuoLength = tuoCode.length;
            hongqiuInf2.setCount(tuoLength);
            for (int i = 0; i < tuoLength; i++) {
                hongqiu2.get(Integer.valueOf(tuoCode[i]) - 1).setChoosed(true);
            }
            redBallsLayout2.refreshAllBall();
            int lanLength = blueBall.length;
            lanqiuInf.setCount(lanLength);
            for (int i = 0; i < lanLength; i++) {
                lanqiu.get(Integer.valueOf(blueBall[i]) - 1).setChoosed(true);
            }
            blueBallsLayout.refreshAllBall();
        }
        else {
            String[] nums = lotteryMode[0].split("\\|");
            String[] redBall = nums[0].split("\\,");
            int[] hongBalls = new int[SSQ_HONGQIU_LIMIT];
            for (int i = 0; i < redBall.length; i++) {
                hongBalls[i] = Integer.parseInt(redBall[i]);
                hongqiu1.get(hongBalls[i] - 1).setChoosed(true);
                redBallsLayout1.chooseBall(hongBalls[i] - 1);
            }

            if (nums.length > 1) {
                String[] blueBall = nums[1].split("\\,");
                int[] lanBalls = new int[SSQ_LANQIU_LIMIT];
                for (int i = 0; i < blueBall.length; i++) {
                    lanBalls[i] = Integer.parseInt(blueBall[i]);
                    lanqiu.get(lanBalls[i] - 1).setChoosed(true);
                    blueBallsLayout.chooseBall(lanBalls[i] - 1);
                }
            }
        }
        onBallClickInf(-1, -1);
    }

    // 传递过去投注付款或确认页面额外的投注类型参数
    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
    }

    // 生成投注格式
    private String getBallsBetInf() {
        if (lotteryType == 1) {
            return getNormalSSQBetCode();
        }
        else if (lotteryType == 2) {
            return getDantuoSSQBetCode();
        }
        return null;
    }

    // 生成双色球普通玩法投注格式
    private String getNormalSSQBetCode() {
        StringBuilder betBallText = new StringBuilder();
        int redLength = hongqiu1.size();
        for (int i = 0; i < redLength; i++) {
            if (hongqiu1.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), redLength));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int lanLength = lanqiu.size();
        for (int i = 0; i < lanLength; i++) {
            if (lanqiu.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), lanLength));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":1:");
        if (hongqiuInf1.getCount() > 6 || lanqiuInf.getCount() > 1) {
            betBallText.append("2:");
        }
        else {
            betBallText.append("1:");
        }
        return betBallText.toString();
    }

    // 生成双色球胆拖玩法
    private String getDantuoSSQBetCode() {
        StringBuilder betBallText = new StringBuilder();
        int danLength = hongqiu1.size();
        for (int i = 0; i < danLength; i++) {
            if (hongqiu1.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), SSQ_HONGQIU_LENGTH));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("$");
        int tuoLength = hongqiu2.size();
        for (int i = 0; i < tuoLength; i++) {
            if (hongqiu2.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), SSQ_HONGQIU_LENGTH));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int lanLength = lanqiu.size();
        for (int i = 0; i < lanLength; i++) {
            if (lanqiu.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), lanLength));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":1:5:");
        return betBallText.toString();
    }

    // 选球后显示选球的信息
    private String getBallsDisplayInf() {
        if (lotteryType == 1) {
            return getNormalSSQDispalyInf();
        }
        else if (lotteryType == 2) {
            return getDantuoSSQDispalyInf();
        }
        return null;
    }

    // 双色球普通玩法，选球后显示选球的信息
    private String getNormalSSQDispalyInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        int redLength = hongqiu1.size();
        if (hongqiuInf1.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < redLength; i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), redLength));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");

        betBallText.append("<font color='blue'>");
        int lanLength = lanqiu.size();
        betBallText.append("|");
        if (lanqiuInf.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < lanLength; i++) {
                if (lanqiu.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), lanLength));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    // 双色球胆拖玩法，选球后显示选球的信息
    private String getDantuoSSQDispalyInf() {
        StringBuilder betBallText = new StringBuilder();
        if (hongqiuInf1.getCount() != 0) {
            betBallText.append("<font color='red'>[胆](");
            int redLength = hongqiu1.size();
            for (int i = 0; i < redLength; i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), SSQ_HONGQIU_LENGTH));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append(")</font>");
        }
        if (hongqiuInf2.getCount() != 0) {
            betBallText.append("<font color='red'>");
            int redLength = hongqiu2.size();
            for (int i = 0; i < redLength; i++) {
                if (hongqiu2.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), SSQ_HONGQIU_LENGTH));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("</font>");
        }

        betBallText.append("<font color='blue'>");
        int lanLength = lanqiu.size();
        betBallText.append("|");
        if (lanqiuInf.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < lanLength; i++) {
                if (lanqiu.get(i).isChoosed()) {
                    betBallText.append(StringUtil.betDataTransite((i + 1), SSQ_HONGQIU_LENGTH));
                    betBallText.append(",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    protected boolean checkInput() {
        return true;
    }

    @Override
    public void randomBalls() {
        // 胆拖没有机选功能
        if (lotteryType == 2)
            return;
        clearBalls();
        String betInf = "1注     <font color='red'>2元</font>";
        moneyInf.setText(Html.fromHtml(betInf));

        int[] randomRedNum = MathUtil.getRandomNumNotEquals(SSQ_HONGQIU_MIN, SSQ_HONGQIU_LENGTH);
        for (int i = 0; i < 6; i++) {
            hongqiu1.get(randomRedNum[i]).setChoosed(true);
            redBallsLayout1.chooseBall(randomRedNum[i]);
        }

        int[] randomBlueNum = MathUtil.getRandomNumNotEquals(SSQ_LANQIU_MIN, SSQ_LANQIU_LENGTH);
        for (int i = 0; i < 1; i++) {
            lanqiu.get(randomBlueNum[i]).setChoosed(true);
            blueBallsLayout.chooseBall(randomBlueNum[i]);
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
        blueBallsLayout.resetBalls();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        initDispalyInf();
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        if (lotteryType == 1) {
            ViewUtil.showTipsToast(this, "您只能选" + SSQ_HONGQIU_LIMIT + "个红球");
        }
        else if (lotteryType == 2) {
            if (ballType == 1) {
                ViewUtil.showTipsToast(this, "您最多只能选5个胆码");
            }
            else {
                ViewUtil.showTipsToast(this, "您最多只能选20个拖码");
            }
        }
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        if (lotteryType == 1) {
            int hongNumber = hongqiuInf1.getCount();
            int lanNumber = lanqiuInf.getCount();
            if (hongNumber == 0 && lanNumber == 0) {
                resetInf();
            }
            else {
                enableClearBtn();
                Boolean refreshMoney = false;
                if (hongNumber > 7 && lanNumber >= 1) {
                    refreshMoney = true;
                    // 计算投注总数
                    betNumber =
                        MathUtil.factorial(hongNumber, hongNumber - 6) /
                            MathUtil.factorial(hongNumber - 6, hongNumber - 7) * lanNumber;
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber == 6 && lanNumber >= 1) {
                    refreshMoney = true;
                    betNumber = lanNumber;
                    betMoney = betNumber * 2 * 1;
                }
                else if (hongNumber == 7 && lanNumber >= 1) {
                    refreshMoney = true;
                    betNumber = MathUtil.factorial(hongNumber, hongNumber - 6) * lanNumber;
                    betMoney = betNumber * 2 * 1;
                }
                else {
                    invalidateNum();
                }
                if (refreshMoney) {
                    String betInf = getBetInf(betNumber, betMoney);
                    moneyInf.setText(Html.fromHtml(betInf));
                    invalidateAll();
                }
            }
        }
        else if (lotteryType == 2) {
            if (ballType == 1 && index >= 0) {
                if (hongqiu2.get(index).isChoosed()) {
                    hongqiu2.get(index).setChoosed(false);
                    hongqiuInf2.setCount(hongqiuInf2.getCount() - 1);
                }
                redBallsLayout2.refreshAllBall();
            }
            else if (ballType == 2 && index >= 0) {
                if (hongqiu1.get(index).isChoosed()) {
                    hongqiu1.get(index).setChoosed(false);
                    hongqiuInf1.setCount(hongqiuInf1.getCount() - 1);
                }
                redBallsLayout1.refreshAllBall();
            }
            int danNum = hongqiuInf1.getCount();
            int tuoNum = hongqiuInf2.getCount();
            if (danNum == 0 && tuoNum == 0 && lanqiuInf.getCount() == 0) {
                resetInf();
            }
            else {
                enableClearBtn();
                if (danNum + tuoNum >= 7 && danNum >= 1 && tuoNum >= 2) {
                    // 计算投注总数
                    betNumber = MathUtil.combination(tuoNum, 6 - danNum) * lanqiuInf.getCount();
                    betMoney = betNumber * 2 * 1;
                    String betInf = getBetInf(betNumber, betMoney);
                    moneyInf.setText(Html.fromHtml(betInf));
                    invalidateAll();
                }
                else {
                    invalidateNum();
                }
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
        if (lotteryType == 1) {
            choosingCountRed1.setText("红球：" + hongqiuInf1.getCount() + "/6个");
            choosingCountBlue.setText("蓝球：" + lanqiuInf.getCount() + "/1个");
        }
        else if (lotteryType == 2) {
            choosingCountRed1.setText("胆号：" + hongqiuInf1.getCount() + "/1个");
            choosingCountRed2.setText("拖号：" + hongqiuInf2.getCount() + "/2个");
            choosingCountBlue.setText("蓝球：" + lanqiuInf.getCount() + "/1个");
        }
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "双色球游戏规则");
        bundel.putString("lottery_help", "help_new/ssq.html");
        intent.putExtras(bundel);
        intent.setClass(SSQActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goCalculator() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "双色球奖金计算器");
        bundel.putString("lottery_help", "ssq_calculator/ssql+calculator.html");
        intent.putExtras(bundel);
        intent.setClass(SSQActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_id", "ssq");
        intent.putExtras(bundel);
        intent.setClass(SSQActivity.this, TrendChartActivity.class);
        startActivityForResult(intent, TREND_CHART);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(SSQActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "ssq");
        bundle.putString("lotteryTypeZS", "0");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
        if (v.getId() == R.id.layout_lottery_calculator) {
            popMenu.dismiss();
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: v2 bet calculator ssq");
            map.put("more_inf", "bet calculator ssq");
            String eventName = "v2 bet calculator";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMob = "bet_click_calculator";
            MobclickAgent.onEvent(this, eventNameMob, "ssq");
            goCalculator();
        }
        else if (v.getId() == R.id.bet_top_term_layout) {
            showPopView();
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

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout1.refreshAllBallInf(showHotNum, showOmitNum);
        redBallsLayout2.refreshAllBallInf(showHotNum, showOmitNum);
        blueBallsLayout.refreshAllBallInf(showHotNum, showOmitNum);
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
                        lanqiu.get(i).setBallAnalase(blueballAnalyse[i]);
                        lanqiu.get(i).setBallsInf(blueBallTips[i]);
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
                        lanqiu.get(i).setBallOmit(blueballOmit[i]);
                        lanqiu.get(i).setBallsInf(blueBallTips[i]);
                    }
                    refreshHotNumShow();
                }
            }
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
                for (int i = 0; i < 6; i++) {
                    hongqiu1.get(vertor[i] - 1).setChoosed(true);
                    redBallsLayout1.chooseBall(vertor[i] - 1);
                }
                lanqiu.get(vertor[6] - 1).setChoosed(true);
                blueBallsLayout.chooseBall(vertor[6] - 1);
                invalidateAll();
                betMoney = 2;
                mode = "1003";
                luckynum = orgCode;
            }
        }
        else if (requestCode == TREND_CHART) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String code = bundle.getString("bet_code");
                    mode = "1014";
                    defaultNum(code);
                }
            }
        }
    }

    public String getQ_code() {
        ssqNum01 = 0;
        ssqNum02 = 0;
        StringBuilder ballText = new StringBuilder();
        if (lotteryType == 1) {
            for (int i = 0; i < hongqiu1.size(); i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    ballText.append(StringUtil.betDataTransite(i + 1));
                    ballText.append(",");
                    ssqNum01 = ssqNum01 + 1;
                }
            }
            if (ballText.length() > 0) {
                ballText.deleteCharAt(ballText.length() - 1);
            }
        }
        else {
            for (int i = 0; i < hongqiu1.size(); i++) {
                if (hongqiu1.get(i).isChoosed()) {
                    ballText.append(StringUtil.betDataTransite(i + 1));
                    ballText.append(",");
                    ssqNum01 = ssqNum01 + 1;
                }
            }
            for (int i = 0; i < hongqiu2.size(); i++) {
                if (hongqiu2.get(i).isChoosed()) {
                    ballText.append(StringUtil.betDataTransite(i + 1));
                    ballText.append(",");
                    ssqNum01 = ssqNum01 + 1;
                }
            }
            if (ballText.length() > 0) {
                ballText.deleteCharAt(ballText.length() - 1);
            }
        }
        ballText.append("|");
        for (int i = 0; i < lanqiu.size(); i++) {
            if (lanqiu.get(i).isChoosed()) {
                int speialNum = Integer.valueOf(StringUtil.betDataTransite(i + 1));
                if (speialNum >= 10)
                    isSingle = false;
                else if (speialNum < 10)
                    isSingle = true;
                ballText.append(StringUtil.betDataTransite(i + 1));
                ballText.append(",");
                ssqNum02 = ssqNum02 + 1;
            }
        }
        if (ballText.length() > 0) {
            ballText.deleteCharAt(ballText.length() - 1);
        }
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    int ssqNum01;
    int ssqNum02;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code() == null) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (lotteryType == 1 && ssqNum01 > 6) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理6个红球");
            }
            else if (lotteryType == 1 && ssqNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理1个蓝球");
            }
            else if (lotteryType == 2 && ssqNum01 > 6) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理6个胆和拖球");
            }
            else if (lotteryType == 2 && ssqNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理1个蓝球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                requestCode = setMatchCode();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", term_num);
                bundle.putString("kind", "ssq");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(SSQActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        titlePopup.dismiss();
        if (kind == 1) {
            handler.removeMessages(FLASHBALL);
            handler.removeMessages(RANDOMBALLS);
            handler.removeMessages(SOUND);
            handler.removeMessages(VIBRATE);
        }

        databaseData.putString("ssq_way", LotteryUtils.ssqWay[kind]);
        databaseData.commit();
        clearBalls();
        lotteryType = kind + 1;
        index_num = kind;
        title.setText("双色球  " + LotteryUtils.textArraySSQ[kind]);
        showWay();
        showBallNum();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popMenu.dismiss();
        TextView t_v = (TextView) parent.getChildAt(searchType).findViewById(R.id.unite_grid_view_item_click);
        t_v.setTextColor(getResources().getColor(R.color.dark_purple));
        t_v.setBackgroundResource(R.drawable.bet_popup_item_normal);
        TextView tv = (TextView) parent.getChildAt(position).findViewById(R.id.unite_grid_view_item_click);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        searchType = position;
        adapter = new CommissionAdapter(SSQActivity.this, list, searchType, "bet_tools");
        hotGrid.setAdapter(adapter);
        databaseData.putInt("hot_way", searchType);
        getAnalyseData();
    }

}
