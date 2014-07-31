package com.haozan.caipiao.activity.bet.qxc;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryDiviningActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;

public class QXCActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener {
    private static final String QXC_TIPS = "每位至少选择一个数字";

    public static final int QXC_HONGQIU_START = 0;
    public static final int QXC_HONGQIU_LENGTH = 10;
    public static final int QXC_HONGQIU_LIMIT = 10;
    public static final int QXC_HONGQIU_MIN = 1;

    private static String hotCondintion = null;

    // red ball variable 01
    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;// 记录选球的个数、是否选中状态等
// private TextView choosingCount01;// 显示选中多少位
    private NewBetBallsLayout redBallsLayout01;// 自定义的View，用来显示求
    // red ball variable 02
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
// private TextView choosingCount02;
    private NewBetBallsLayout redBallsLayout02;
    // red ball variable 03
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
// private TextView choosingCount03;
    private NewBetBallsLayout redBallsLayout03;
    // red ball variable 04
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
// private TextView choosingCount04;
    private NewBetBallsLayout redBallsLayout04;
    // red ball variable 05
    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
// private TextView choosingCount05;
    private NewBetBallsLayout redBallsLayout05;
    // red ball variable 06
    private ArrayList<BetBall> hongqiu06;
    private BetBallsData hongqiuInf06;
// private TextView choosingCount06;
    private NewBetBallsLayout redBallsLayout06;
    // red ball variable 07
    private ArrayList<BetBall> hongqiu07;
    private BetBallsData hongqiuInf07;
// private TextView choosingCount07;
    private NewBetBallsLayout redBallsLayout07;

    private String[] analyseRed;

    @Override
    public void setKind() {
        this.kind = "qxc";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.qxc);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // init red section 01
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = QXC_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf01.setBallType(1);

        // init red section 02
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
        hongqiuInf02.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf02.setBallType(2);

        // init red section 03
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
        hongqiuInf03.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf03.setBallType(3);

        // init red section 04
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
        hongqiuInf04.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf04.setBallType(4);

        // init red section 05
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
        hongqiuInf05.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf05.setBallType(5);

        // init red section 06
        hongqiuInf06 = new BetBallsData();
        hongqiu06 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu06.add(ball);
        }
        hongqiuInf06.setBetBalls(hongqiu06);
        hongqiuInf06.setCount(0);
        hongqiuInf06.setColor("red");
        hongqiuInf06.setLimit(QXC_HONGQIU_LIMIT);
        hongqiuInf06.setBallType(6);

        hongqiuInf07 = new BetBallsData();
        hongqiu07 = new ArrayList<BetBall>();
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu07.add(ball);
        }
        hongqiuInf07.setBetBalls(hongqiu07);
        hongqiuInf07.setCount(0);
        hongqiuInf07.setColor("red");
        hongqiuInf07.setLimit(QXC_HONGQIU_LIMIT);
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
// choosingCount01 = (TextView) this.findViewById(R.id.qxc_hongqiu_text01);
// choosingCount02 = (TextView) this.findViewById(R.id.qxc_hongqiu_text02);
// choosingCount03 = (TextView) this.findViewById(R.id.qxc_hongqiu_text03);
// choosingCount04 = (TextView) this.findViewById(R.id.qxc_hongqiu_text04);
// choosingCount05 = (TextView) this.findViewById(R.id.qxc_hongqiu_text05);
// choosingCount06 = (TextView) this.findViewById(R.id.qxc_hongqiu_text06);
// choosingCount07 = (TextView) this.findViewById(R.id.qxc_hongqiu_text07);
        // setup red setion01
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls01);
        // setup red setion02
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls02);
        // setup red setion03
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls03);
        // setup red setion04
        redBallsLayout04 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls04);
        // setup red setion05
        redBallsLayout05 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls05);
        // setup red setion06
        redBallsLayout06 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls06);
        // setup red setion07
        redBallsLayout07 = (NewBetBallsLayout) this.findViewById(R.id.qxc_hongqiu_balls07);
        // tool box
        lotteryCalculator.setEnabled(false);
        lotteryCalculator.setVisibility(View.INVISIBLE);

        // 屏蔽工具按钮
// analyseTips.setVisibility(View.GONE);
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
        title.setText("七星彩");
        topArrow.setVisibility(View.GONE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        choosingInf.setText(QXC_TIPS);
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
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        String[] nums = lotteryMode[0].split("\\|");
        String[] redBall = nums[0].split("\\,");
        int lengthFirst = redBall[0].length();
        for (int i = 0; i < lengthFirst; i++) {
            int num = Integer.valueOf(redBall[0].substring(i, i + 1));
            hongqiu01.get(num).setChoosed(true);
            redBallsLayout01.chooseBall(num);
        }
        int lengthSecond = redBall[1].length();
        for (int i = 0; i < lengthSecond; i++) {
            int num = Integer.valueOf(redBall[1].substring(i, i + 1));
            hongqiu02.get(num).setChoosed(true);
            redBallsLayout02.chooseBall(num);
        }
        int lengthThird = redBall[2].length();
        for (int i = 0; i < lengthThird; i++) {
            int num = Integer.valueOf(redBall[2].substring(i, i + 1));
            hongqiu03.get(num).setChoosed(true);
            redBallsLayout03.chooseBall(num);
        }
        int lengthForth = redBall[3].length();
        for (int i = 0; i < lengthForth; i++) {
            int num = Integer.valueOf(redBall[3].substring(i, i + 1));
            hongqiu04.get(num).setChoosed(true);
            redBallsLayout04.chooseBall(num);
        }
        int lengthFifth = redBall[4].length();
        for (int i = 0; i < lengthFifth; i++) {
            int num = Integer.valueOf(redBall[4].substring(i, i + 1));
            hongqiu05.get(num).setChoosed(true);
            redBallsLayout05.chooseBall(num);
        }
        int lengthSixth = redBall[5].length();
        for (int i = 0; i < lengthSixth; i++) {
            int num = Integer.valueOf(redBall[5].substring(i, i + 1));
            hongqiu06.get(num).setChoosed(true);
            redBallsLayout06.chooseBall(num);
        }
        int lengthSeventh = redBall[6].length();
        for (int i = 0; i < lengthSeventh; i++) {
            int num = Integer.valueOf(redBall[6].substring(i, i + 1));
            hongqiu07.get(num).setChoosed(true);
            redBallsLayout07.chooseBall(num);
        }
        onBallClickInf(-1, -1);
    }

// 投注格式
    private String getBallsDisplayInf() {
        String betText = null;
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        int red01Length = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red01Length; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");

        int red02Length = hongqiu02.size();
        if (hongqiuInf02.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red02Length; i++) {
                if (hongqiu02.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");

        int red03Length = hongqiu03.size();
        if (hongqiuInf03.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red03Length; i++) {
                if (hongqiu03.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");

        int red04Length = hongqiu04.size();
        if (hongqiuInf04.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red04Length; i++) {
                if (hongqiu04.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int red05Length = hongqiu05.size();
        if (hongqiuInf05.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red05Length; i++) {
                if (hongqiu05.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");

        int red06Length = hongqiu06.size();
        if (hongqiuInf06.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red06Length; i++) {
                if (hongqiu06.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font><font color='red'>|");
        int red07Length = hongqiu07.size();
        if (hongqiuInf07.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < red07Length; i++) {
                if (hongqiu07.get(i).isChoosed()) {
                    betBallText.append(i + ",");
                }
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        betText = betBallText.toString();
        return betText;
    }

    private String getBallsBetInf() {
        String betText = null;
        StringBuilder betBallText = new StringBuilder();
        int red01Length = hongqiu01.size();
        for (int i = 0; i < red01Length; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");

        int red02Length = hongqiu02.size();
        for (int i = 0; i < red02Length; i++) {
            if (hongqiu02.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");

        int red03Length = hongqiu03.size();
        for (int i = 0; i < red03Length; i++) {
            if (hongqiu03.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");

        int red04Length = hongqiu04.size();
        for (int i = 0; i < red04Length; i++) {
            if (hongqiu04.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");

        int red05Length = hongqiu05.size();
        for (int i = 0; i < red05Length; i++) {
            if (hongqiu05.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");

        int red06Length = hongqiu06.size();
        for (int i = 0; i < red06Length; i++) {
            if (hongqiu06.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(",");
        int red07Length = hongqiu07.size();
        for (int i = 0; i < red07Length; i++) {
            if (hongqiu07.get(i).isChoosed()) {
                betBallText.append(i);
            }
        }
        betBallText.append(":" + betMoney + ":");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1 || hongqiuInf06.getCount() > 1 ||
            hongqiuInf07.getCount() > 1) {
            betBallText.append("102:");
        }
        else {
            betBallText.append("101:");
        }
        betText = betBallText.toString();
        return betText;
    }

    protected boolean checkInput() {
        String inf = null;
        if (hongqiuInf01.getCount() < QXC_HONGQIU_MIN) {
            inf = "第一位请至少选择1个号码";
        }
        else if (hongqiuInf02.getCount() < QXC_HONGQIU_MIN) {
            inf = "第二位请至少选择1个号码";
        }
        else if (hongqiuInf03.getCount() < QXC_HONGQIU_MIN) {
            inf = "第三位请至少选择1个号码";
        }
        else if (hongqiuInf04.getCount() < QXC_HONGQIU_MIN) {
            inf = "第四位请至少选择1个号码";
        }
        else if (hongqiuInf05.getCount() < QXC_HONGQIU_MIN) {
            inf = "第五位请至少选择1个号码";
        }
        else if (hongqiuInf06.getCount() < QXC_HONGQIU_MIN) {
            inf = "第六位位请至少选择1个号码";
        }
        else if (hongqiuInf07.getCount() < QXC_HONGQIU_MIN) {
            inf = "第七位请至少选择1个号码";
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

        Random rd = new Random();
        int num01 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num02 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num03 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num04 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num05 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num06 = rd.nextInt(QXC_HONGQIU_LENGTH);
        int num07 = rd.nextInt(QXC_HONGQIU_LENGTH);

        hongqiu01.get(num01).setChoosed(true);
        hongqiu02.get(num02).setChoosed(true);
        hongqiu03.get(num03).setChoosed(true);
        hongqiu04.get(num04).setChoosed(true);
        hongqiu05.get(num05).setChoosed(true);
        hongqiu06.get(num06).setChoosed(true);
        hongqiu07.get(num07).setChoosed(true);
        redBallsLayout01.chooseBall(num01);
        redBallsLayout02.chooseBall(num02);
        redBallsLayout03.chooseBall(num03);
        redBallsLayout04.chooseBall(num04);
        redBallsLayout05.chooseBall(num05);
        redBallsLayout06.chooseBall(num06);
        redBallsLayout07.chooseBall(num07);
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
        redBallsLayout01.resetBalls();
        redBallsLayout02.resetBalls();
        redBallsLayout03.resetBalls();
        redBallsLayout04.resetBalls();
        redBallsLayout05.resetBalls();
        redBallsLayout06.resetBalls();
        redBallsLayout07.resetBalls();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        choosingInf.setText(QXC_TIPS);
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        ViewUtil.showTipsToast(this, "您只能选" + QXC_HONGQIU_LIMIT + "个红球");
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        String betInf = null;
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();
        int hongNumber04 = hongqiuInf04.getCount();
        int hongNumber05 = hongqiuInf05.getCount();
        int hongNumber06 = hongqiuInf06.getCount();
        int hongNumber07 = hongqiuInf07.getCount();
        if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
            hongNumber05 == 0 && +hongNumber06 == 0 && hongNumber07 == 0) {
            resetInf();
        }
        else {
            if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 ||
                hongNumber05 == 0 || +hongNumber06 == 0 || hongNumber07 == 0) {
                invalidateNum();
            }
            else {
                // 注数
                betNumber =
                    hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05 * hongNumber06 *
                        hongNumber07;
                // 金额
                betMoney = betNumber * 2 * 1;
                invalidateAll();
                betInf = getBetInf(betNumber, betMoney);
                if (betInf != null)
                    moneyInf.setText(Html.fromHtml(betInf));
            }
        }
        checkBet(betMoney);
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml("已选:" + displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    @Override
    protected void showBallNum() {
// choosingCount01.setText("第一位：" + hongqiuInf01.getCount() + "/1个");
// choosingCount02.setText("第二位：" + hongqiuInf02.getCount() + "/1个");
// choosingCount03.setText("第三位：" + hongqiuInf03.getCount() + "/1个");
// choosingCount04.setText("第四位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount05.setText("第五位：" + hongqiuInf05.getCount() + "/1个");
// choosingCount06.setText("第六位：" + hongqiuInf06.getCount() + "/1个");
// choosingCount07.setText("第七位：" + hongqiuInf07.getCount() + "/1个");
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "七星彩游戏规则");
        bundel.putString("lottery_help", "help_new/qxc.html");
        intent.putExtras(bundel);
        intent.setClass(QXCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "七星彩走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=qxc&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(QXCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(QXCActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "qxc");
        bundle.putString("lotteryTypeZS", "0");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
    }

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout01.refreshAllBallInf(showHotNum);
        redBallsLayout02.refreshAllBallInf(showHotNum);
        redBallsLayout03.refreshAllBallInf(showHotNum);
        redBallsLayout04.refreshAllBallInf(showHotNum);
        redBallsLayout05.refreshAllBallInf(showHotNum);
        redBallsLayout06.refreshAllBallInf(showHotNum);
        redBallsLayout07.refreshAllBallInf(showHotNum);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.img_help_info_bg) {
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
                    String basicNum = ja.getData(analyseData, "basic_distrb");
                    analyseRed = StringUtil.spliteString(basicNum, ",");
                    int redLength = analyseRed.length;
                    int count;
                    for (int i = 0; i < redLength; i++) {
                        count = Integer.valueOf(analyseRed[i]);
                        if (count < 5)
                            analyseRed[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseRed[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseRed[i] = "出" + count + "次";
                        hongqiu01.get(i).setBallsInf(analyseRed[i]);
                        hongqiu02.get(i).setBallsInf(analyseRed[i]);
                        hongqiu03.get(i).setBallsInf(analyseRed[i]);
                        hongqiu04.get(i).setBallsInf(analyseRed[i]);
                        hongqiu05.get(i).setBallsInf(analyseRed[i]);
                        hongqiu06.get(i).setBallsInf(analyseRed[i]);
                        hongqiu07.get(i).setBallsInf(analyseRed[i]);
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
                hongqiu01.get(vertor[0]).setChoosed(true);
                hongqiu02.get(vertor[1]).setChoosed(true);
                hongqiu03.get(vertor[2]).setChoosed(true);
                hongqiu04.get(vertor[3]).setChoosed(true);
                hongqiu05.get(vertor[4]).setChoosed(true);
                hongqiu06.get(vertor[5]).setChoosed(true);
                hongqiu07.get(vertor[6]).setChoosed(true);
                redBallsLayout01.chooseBall(vertor[0]);
                redBallsLayout02.chooseBall(vertor[1]);
                redBallsLayout03.chooseBall(vertor[2]);
                redBallsLayout04.chooseBall(vertor[3]);
                redBallsLayout05.chooseBall(vertor[4]);
                redBallsLayout06.chooseBall(vertor[5]);
                redBallsLayout07.chooseBall(vertor[6]);
                betMoney = 2;
                invalidateAll();
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
        qxcNum01 = 0;
        qxcNum02 = 0;
        qxcNum03 = 0;
        qxcNum04 = 0;
        qxcNum05 = 0;
        qxcNum06 = 0;
        qxcNum07 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum01 = qxcNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum02 = qxcNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum03 = qxcNum03 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu04.size(); i++) {
            if (hongqiu04.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum04 = qxcNum04 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu05.size(); i++) {
            if (hongqiu05.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum05 = qxcNum05 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu06.size(); i++) {
            if (hongqiu06.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum06 = qxcNum06 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu07.size(); i++) {
            if (hongqiu07.get(i).isChoosed()) {
                ballText.append(i);
                qxcNum07 = qxcNum07 + 1;
            }
        }
        if (ballText.length() > 0)
            ballText.deleteCharAt(ballText.length() - 1);
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int qxcNum01 = 0;
    private int qxcNum02 = 0;
    private int qxcNum03 = 0;
    private int qxcNum04 = 0;
    private int qxcNum05 = 0;
    private int qxcNum06 = 0;
    private int qxcNum07 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals(",,,,,")) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (qxcNum01 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第一位上1个红球");
            }
            else if (qxcNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第二位上1个红球");
            }
            else if (qxcNum03 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第三位上1个红球");
            }
            else if (qxcNum04 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第四位上1个红球");
            }
            else if (qxcNum05 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第五位上1个红球");
            }
            else if (qxcNum06 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第六位上1个红球");
            }
            else if (qxcNum07 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理第七位上1个红球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "qxc");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(QXCActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }
}
