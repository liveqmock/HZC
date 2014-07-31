package com.haozan.caipiao.activity.bet.dfljy;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.LotteryBalls;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;

public class DFLJYActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener {
    private static final String DFLJY_TIPS = "请选择至少6个红球和1个蓝球";
    
    public static final int DFLJY_HONGQIU_START = 0;
    public static final int DFLJY_HONGQIU_LENGTH = 10;
    public static final int DFLJY_HONGQIU_LIMIT = 10;
    public static final int DFLJY_HONGQIU_MIN = 1;

    public static final int DFLJY_LANQIU_START = 1;
    public static final int DFLJY_LANQIU_LENGTH = 12;
    public static final int DFLJY_LANQIU_LIMIT = 12;
    public static final int DFLJY_LANQIU_MIN = 1;
    
    private static String hotCondintion = null;

    // red ball variable 01
    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
    private TextView choosingCount01;
    private NewBetBallsLayout redBallsLayout01;
    // red ball variable 02
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
    private TextView choosingCount02;
    private NewBetBallsLayout redBallsLayout02;
    // red ball variable 03
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
    private TextView choosingCount03;
    private NewBetBallsLayout redBallsLayout03;
    // red ball variable 04
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
    private TextView choosingCount04;
    private NewBetBallsLayout redBallsLayout04;
    // red ball variable 05
    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
    private TextView choosingCount05;
    private NewBetBallsLayout redBallsLayout05;
    // red ball variable 06
    private ArrayList<BetBall> hongqiu06;
    private BetBallsData hongqiuInf06;
    private TextView choosingCount06;
    private NewBetBallsLayout redBallsLayout06;
    // red ball variable 07
    private ArrayList<BetBall> lanqiu07;
    private BetBallsData lanqiuInf07;
    private TextView choosingCount07;
    private NewBetBallsLayout blueBallsLayout07;

    private String[] analyseRed;
    private String[] analyseBlue;

    @Override
    public void setKind() {
        this.kind = "dfljy";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.dfljy);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // init red section 01
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = DFLJY_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(DFLJY_HONGQIU_LIMIT);
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
        hongqiuInf02.setLimit(DFLJY_HONGQIU_LIMIT);
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
        hongqiuInf03.setLimit(DFLJY_HONGQIU_LIMIT);
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
        hongqiuInf04.setLimit(DFLJY_HONGQIU_LIMIT);
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
        hongqiuInf05.setLimit(DFLJY_HONGQIU_LIMIT);
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
        hongqiuInf06.setLimit(DFLJY_HONGQIU_LIMIT);
        hongqiuInf06.setBallType(6);

        // init blue section 01
        lanqiuInf07 = new BetBallsData();
        lanqiu07 = new ArrayList<BetBall>();
        int blueLength = DFLJY_LANQIU_LENGTH;
        for (int i = 0; i < blueLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(LotteryUtils.animals[i]);
            lanqiu07.add(ball);
        }
        lanqiuInf07.setBetBalls(lanqiu07);
        lanqiuInf07.setCount(0);
        lanqiuInf07.setColor("blue");
        lanqiuInf07.setLimit(DFLJY_LANQIU_LIMIT);
        lanqiuInf07.setBallType(7);
    }

    protected void setupViews() {
        super.setupViews();
        choosingCount01 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text01);
        choosingCount02 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text02);
        choosingCount03 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text03);
        choosingCount04 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text04);
        choosingCount05 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text05);
        choosingCount06 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text06);
        choosingCount07 = (TextView) this.findViewById(R.id.dfljy_hongqiu_text);
        // setup red setion01
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls01);
        // setup red setion02
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls02);
        // setup red setion03
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls03);
        // setup red setion04
        redBallsLayout04 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls04);
        // setup red setion05
        redBallsLayout05 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls05);
        // setup red setion06
        redBallsLayout06 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_hongqiu_balls06);
        // setup blue setion07
        blueBallsLayout07 = (NewBetBallsLayout) this.findViewById(R.id.dfljy_lanqiu_balls);
        // tool box
        lotteryCalculator.setEnabled(false);
        lotteryCalculator.setVisibility(View.GONE);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
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
        title.setText("东方6+1");
        topArrow.setVisibility(View.GONE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        choosingInf.setText(DFLJY_TIPS);
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

        blueBallsLayout07.initData(lanqiuInf07, bigBallViews, this);
        blueBallsLayout07.setFullListener(this);
        blueBallsLayout07.setTouchMoveListener(this);
        topArrow.setVisibility(View.GONE);
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
        String[] blueBall = nums[1].split("\\,");
        int lengthFirst = redBall[0].length();
        for (int i = 0; i < lengthFirst; i++) {
            int num = Integer.valueOf(redBall[0].substring(i, i + 1));
            hongqiu01.get(num).setChoosed(true);
        }
        hongqiuInf01.setCount(lengthFirst);
        redBallsLayout01.refreshAllBall();

        int lengthSecond = redBall[1].length();
        for (int i = 0; i < lengthSecond; i++) {
            int num = Integer.valueOf(redBall[1].substring(i, i + 1));
            hongqiu02.get(num).setChoosed(true);
            redBallsLayout02.chooseBall(num);
        }
        hongqiuInf02.setCount(lengthSecond);
        redBallsLayout02.refreshAllBall();

        int lengthThird = redBall[2].length();
        for (int i = 0; i < lengthThird; i++) {
            int num = Integer.valueOf(redBall[2].substring(i, i + 1));
            hongqiu03.get(num).setChoosed(true);
            redBallsLayout03.chooseBall(num);
        }
        hongqiuInf03.setCount(lengthThird);
        redBallsLayout03.refreshAllBall();

        int lengthForth = redBall[3].length();
        for (int i = 0; i < lengthForth; i++) {
            int num = Integer.valueOf(redBall[3].substring(i, i + 1));
            hongqiu04.get(num).setChoosed(true);
            redBallsLayout04.chooseBall(num);
        }
        hongqiuInf04.setCount(lengthForth);
        redBallsLayout04.refreshAllBall();

        int lengthFifth = redBall[4].length();
        for (int i = 0; i < lengthFifth; i++) {
            int num = Integer.valueOf(redBall[4].substring(i, i + 1));
            hongqiu05.get(num).setChoosed(true);
            redBallsLayout05.chooseBall(num);
        }
        hongqiuInf05.setCount(lengthFifth);
        redBallsLayout05.refreshAllBall();

        int lengthSixth = redBall[5].length();
        for (int i = 0; i < lengthSixth; i++) {
            int num = Integer.valueOf(redBall[5].substring(i, i + 1));
            hongqiu06.get(num).setChoosed(true);
            redBallsLayout06.chooseBall(num);
        }
        hongqiuInf06.setCount(lengthSixth);
        redBallsLayout06.refreshAllBall();

        int lengthSeventh = blueBall.length;
        for (int i = 0; i < lengthSeventh; i++) {
            int num = Integer.valueOf(blueBall[i]);
            lanqiu07.get(num - 1).setChoosed(true);
            blueBallsLayout07.chooseBall(num - 1);
        }
        lanqiuInf07.setCount(lengthSeventh);
        blueBallsLayout07.refreshAllBall();
        onBallClickInf(-1, -1);
    }

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

        betBallText.append("</font><font color='blue'>|");
        int lanLength = lanqiu07.size();
        if (lanqiuInf07.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < lanLength; i++) {
                if (lanqiu07.get(i).isChoosed()) {
                    betBallText.append(LotteryBalls.animals[i] + ",");
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
        betBallText.append("|");
        int lanLength = lanqiu07.size();
        for (int i = 0; i < lanLength; i++) {
            if (lanqiu07.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite((i + 1), lanLength));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append(":1:");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1 || hongqiuInf06.getCount() > 1 ||
            lanqiuInf07.getCount() > 1) {
            betBallText.append("2:");
        }
        else {
            betBallText.append("1:");
        }
        betText = betBallText.toString();
        return betText;
    }

    protected boolean checkInput() {
        String inf = null;
        if (hongqiuInf01.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "个位请至少选择1个号码";
        }
        else if (hongqiuInf02.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "十位请至少选择1个号码";
        }
        else if (hongqiuInf03.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "百位请至少选择1个号码";
        }
        else if (hongqiuInf04.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "千位请至少选择1个号码";
        }
        else if (hongqiuInf05.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "万位请至少选择1个号码";
        }
        else if (hongqiuInf06.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "十万位位请至少选择1个号码";
        }
        else if (lanqiuInf07.getCount() < DFLJY_HONGQIU_MIN) {
            inf = "生肖位请至少选择1个号码";
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this,inf);
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
        int num01 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num02 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num03 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num04 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num05 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num06 = rd.nextInt(DFLJY_HONGQIU_LENGTH);
        int num07 = rd.nextInt(DFLJY_LANQIU_LENGTH);
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

        hongqiu06.get(num06).setChoosed(true);
        redBallsLayout06.chooseBall(num06);

        lanqiu07.get(num07).setChoosed(true);
        blueBallsLayout07.chooseBall(num07);
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
        redBallsLayout01.resetBalls();
        redBallsLayout02.resetBalls();
        redBallsLayout03.resetBalls();
        redBallsLayout04.resetBalls();
        redBallsLayout05.resetBalls();
        redBallsLayout06.resetBalls();
        blueBallsLayout07.resetBalls();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        choosingInf.setText(DFLJY_TIPS);
        showBallNum();
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    @Override
    public void onBallClickFull(int ballType) {

    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        String betInf = null;
        int lanNumber = lanqiuInf07.getCount();
        int hongNumber01 = hongqiuInf01.getCount();
        int hongNumber02 = hongqiuInf02.getCount();
        int hongNumber03 = hongqiuInf03.getCount();
        int hongNumber04 = hongqiuInf04.getCount();
        int hongNumber05 = hongqiuInf05.getCount();
        int hongNumber06 = hongqiuInf06.getCount();
        if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
            hongNumber05 == 0 && +hongNumber06 == 0 && lanNumber == 0) {
            resetInf();
        }
        else {
            if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 ||
                hongNumber05 == 0 || +hongNumber06 == 0 || lanNumber == 0) {
                invalidateNum();
            }
            else {
                invalidateAll();
                betNumber =
                    hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05 * hongNumber06 *
                        lanNumber;
                betMoney = betNumber * 2 * 1;
                checkBet(betMoney);
                betInf = getBetInf(betNumber, betMoney);
                if (betInf != null)
                    moneyInf.setText(Html.fromHtml(betInf));
            }
        }
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
        choosingCount01.setText("个位：" + hongqiuInf01.getCount() + "/1个");
        choosingCount02.setText("十位：" + hongqiuInf02.getCount() + "/1个");
        choosingCount03.setText("百位：" + hongqiuInf03.getCount() + "/1个");
        choosingCount04.setText("千位：" + hongqiuInf04.getCount() + "/1个");
        choosingCount05.setText("万位：" + hongqiuInf05.getCount() + "/1个");
        choosingCount06.setText("十万：" + hongqiuInf06.getCount() + "/1个");
        choosingCount07.setText("生肖：" + lanqiuInf07.getCount() + "/1个");
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "东方6+1游戏规则");
        bundel.putString("lottery_help", "help_new/df6j1.html");
        intent.putExtras(bundel);
        intent.setClass(DFLJYActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }
    
    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "东方6+1走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=dfljy&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(DFLJYActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }


    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(DFLJYActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "dfljy");
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
        blueBallsLayout07.refreshAllBallInf(showHotNum);
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
                    String specialNum = ja.getData(analyseData, "special_distrb");
                    analyseRed = StringUtil.spliteString(basicNum, ",");
                    analyseBlue = StringUtil.spliteString(specialNum, ",");
                    int redLength = analyseRed.length;
                    int blueLength = analyseBlue.length;
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
                    }
                    for (int i = 0; i < blueLength; i++) {
                        count = Integer.valueOf(analyseBlue[i]);
                        if (count < 5)
                            analyseBlue[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseBlue[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseBlue[i] = "出" + count + "次";
                        lanqiu07.get(i).setBallsInf(analyseBlue[i]);
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
            ViewUtil.showTipsToast(this,inf);
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
                lanqiu07.get(vertor[6] - 1).setChoosed(true);
                redBallsLayout01.chooseBall(vertor[0]);
                redBallsLayout02.chooseBall(vertor[1]);
                redBallsLayout03.chooseBall(vertor[2]);
                redBallsLayout04.chooseBall(vertor[3]);
                redBallsLayout05.chooseBall(vertor[4]);
                redBallsLayout06.chooseBall(vertor[5]);
                blueBallsLayout07.chooseBall(vertor[6] - 1);
                invalidateAll();
                betMoney = 2;
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
        dfljyNum01 = 0;
        dfljyNum02 = 0;
        dfljyNum03 = 0;
        dfljyNum04 = 0;
        dfljyNum05 = 0;
        dfljyNum06 = 0;
        dfljyNum07 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum01 = dfljyNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum02 = dfljyNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum03 = dfljyNum03 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu04.size(); i++) {
            if (hongqiu04.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum04 = dfljyNum04 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu05.size(); i++) {
            if (hongqiu05.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum05 = dfljyNum05 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu06.size(); i++) {
            if (hongqiu06.get(i).isChoosed()) {
                ballText.append(i);
                dfljyNum06 = dfljyNum06 + 1;
            }
        }
        ballText.append("|");
        for (int i = 0; i < lanqiu07.size(); i++) {
            if (lanqiu07.get(i).isChoosed()) {
                ballText.append(i + 1);
                ballText.append(",");
                dfljyNum07 = dfljyNum07 + 1;
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

    private int dfljyNum01 = 0;
    private int dfljyNum02 = 0;
    private int dfljyNum03 = 0;
    private int dfljyNum04 = 0;
    private int dfljyNum05 = 0;
    private int dfljyNum06 = 0;
    private int dfljyNum07 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals(",,,,,")) {
            ViewUtil.showTipsToast(this,"分析功能至少需要输入1个号码");
        }
        else {
            if (dfljyNum01 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理个位上1个红球");
            }
            else if (dfljyNum02 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理十位上1个红球");
            }
            else if (dfljyNum03 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理百位上1个红球");
            }
            else if (dfljyNum04 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理千位上1个红球");
            }
            else if (dfljyNum05 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理万位上1个红球");
            }
            else if (dfljyNum06 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理十万位上1个红球");
            }
            else if (dfljyNum07 > 1) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理1个生肖");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "dfljy");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(DFLJYActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }
}
