package com.haozan.caipiao.activity.bet.plw;

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

public class PLWActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener {
    private static final String PLW_TIPS = "顺序猜中全部5个开奖号，奖金10万";
    private static String hotCondintion = null;

    // red ball variable 01
    private ArrayList<BetBall> hongqiu01;
    private BetBallsData hongqiuInf01;
//    private TextView choosingCount01;
    private NewBetBallsLayout redBallsLayout01;
    // red ball variable 02
    private ArrayList<BetBall> hongqiu02;
    private BetBallsData hongqiuInf02;
//    private TextView choosingCount02;
    private NewBetBallsLayout redBallsLayout02;
    // red ball variable 03
    private ArrayList<BetBall> hongqiu03;
    private BetBallsData hongqiuInf03;
//    private TextView choosingCount03;
    private NewBetBallsLayout redBallsLayout03;
    // red ball variable 04
    private ArrayList<BetBall> hongqiu04;
    private BetBallsData hongqiuInf04;
//    private TextView choosingCount04;
    private NewBetBallsLayout redBallsLayout04;
    // red ball variable 05
    private ArrayList<BetBall> hongqiu05;
    private BetBallsData hongqiuInf05;
//    private TextView choosingCount05;
    private NewBetBallsLayout redBallsLayout05;

    private String[] analyseNo1;
    private String[] analyseNo2;
    private String[] analyseNo3;
    private String[] analyseNo4;
    private String[] analyseNo5;

    @Override
    public void setKind() {
        this.kind = "plw";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.plw);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // init red section 01
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = 10;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(10);
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
        hongqiuInf02.setLimit(10);
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
        hongqiuInf03.setLimit(10);
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
        hongqiuInf04.setLimit(10);
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
        hongqiuInf05.setLimit(10);
        hongqiuInf05.setBallType(5);
    }

    protected void setupViews() {
        super.setupViews();
//        choosingCount01 = (TextView) this.findViewById(R.id.plw_hongqiu01_text);
//        choosingCount02 = (TextView) this.findViewById(R.id.plw_hongqiu02_text);
//        choosingCount03 = (TextView) this.findViewById(R.id.plw_hongqiu03_text);
//        choosingCount04 = (TextView) this.findViewById(R.id.plw_hongqiu04_text);
//        choosingCount05 = (TextView) this.findViewById(R.id.plw_hongqiu05_text);
        // setup red setion01
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.plw_hongqiu_balls01);
        // setup red setion02
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.plw_hongqiu_balls02);
        // setup red setion03
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.plw_hongqiu_balls03);
        // setup red setion04
        redBallsLayout04 = (NewBetBallsLayout) this.findViewById(R.id.plw_hongqiu_balls04);
        // setup red setion05
        redBallsLayout05 = (NewBetBallsLayout) this.findViewById(R.id.plw_hongqiu_balls05);
        luckyBallSelect.setVisibility(View.INVISIBLE);
        lotteryCalculator.setVisibility(View.INVISIBLE);
        luckyBallSelect.setEnabled(false);
        lotteryCalculator.setEnabled(false);
        normalToolsLayout.setVisibility(View.GONE);
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
        title.setText("排列5");
        topArrow.setVisibility(View.GONE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        choosingInf.setText(PLW_TIPS);
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
        onBallClickInf(-1, -1);
    }

    private String getBallsDisplayInf() {
        String betText = null;
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        int red01Length = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("-");
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
            betBallText.append("-");
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
            betBallText.append("-");
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
            betBallText.append("-");
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
            betBallText.append("-");
        }
        else {
            for (int i = 0; i < red05Length; i++) {
                if (hongqiu05.get(i).isChoosed()) {
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
        orgCode = betBallText.toString();
        betBallText.append(":1:");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1 ||
            hongqiuInf04.getCount() > 1 || hongqiuInf05.getCount() > 1) {
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
        if (hongqiuInf01.getCount() < 1) {
            inf = "个位请至少选择1个号码";
        }
        else if (hongqiuInf02.getCount() < 1) {
            inf = "十位请至少选择1个号码";
        }
        else if (hongqiuInf03.getCount() < 1) {
            inf = "百位请至少选择1个号码";
        }
        else if (hongqiuInf04.getCount() < 1) {
            inf = "千位请至少选择1个号码";
        }
        else if (hongqiuInf05.getCount() < 1) {
            inf = "万位请至少选择1个号码";
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
        int num01 = rd.nextInt(10);
        int num02 = rd.nextInt(10);
        int num03 = rd.nextInt(10);
        int num04 = rd.nextInt(10);
        int num05 = rd.nextInt(10);
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
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        choosingInf.setText(PLW_TIPS);
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        ViewUtil.showTipsToast(this, "您只能选10个红球");
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
        if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0 && hongNumber04 == 0 &&
            hongNumber05 == 0) {
            resetInf();
        }
        else {
            enableClearBtn();
            if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0 || hongNumber04 == 0 ||
                hongNumber05 == 0) {
                invalidateNum();
            }
            else {
                invalidateAll();
                betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
                betMoney = betNumber * 2 * 1;
                if (betMoney > 0) {
                    enableBetBtn();
                }
                else {
                    disableBetBtn();
                }
                betInf = getBetInf(betNumber, betMoney);
                if (betInf != null) {
                    moneyInf.setText(Html.fromHtml(betInf));
                }
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
// choosingCount01.setText("万位：" + hongqiuInf01.getCount() + "/1个");
// choosingCount02.setText("千位：" + hongqiuInf02.getCount() + "/1个");
// choosingCount03.setText("百位：" + hongqiuInf03.getCount() + "/1个");
// choosingCount04.setText("十位：" + hongqiuInf04.getCount() + "/1个");
// choosingCount05.setText("个位：" + hongqiuInf05.getCount() + "/1个");
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "排列五游戏规则");
        bundel.putString("lottery_help", "help_new/pl5.html");
        intent.putExtras(bundel);
        intent.setClass(PLWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "排列五走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=plw&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(PLWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.compare_layout) {
            searchLuckyType = 1;
            term_num = 20;
            searchLuckyNum();
            showLuckyNum();
            popMenu.dismiss();
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
        redBallsLayout01.refreshAllBallInf(showHotNum);
        redBallsLayout02.refreshAllBallInf(showHotNum);
        redBallsLayout03.refreshAllBallInf(showHotNum);
        redBallsLayout04.refreshAllBallInf(showHotNum);
        redBallsLayout05.refreshAllBallInf(showHotNum);
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
                    String no1 = ja.getData(analyseData, "no1_distrb");
                    String no2 = ja.getData(analyseData, "no2_distrb");
                    String no3 = ja.getData(analyseData, "no3_distrb");
                    String no4 = ja.getData(analyseData, "no4_distrb");
                    String no5 = ja.getData(analyseData, "no5_distrb");
                    analyseNo1 = StringUtil.spliteString(no1, ",");
                    analyseNo2 = StringUtil.spliteString(no2, ",");
                    analyseNo3 = StringUtil.spliteString(no3, ",");
                    analyseNo4 = StringUtil.spliteString(no4, ",");
                    analyseNo5 = StringUtil.spliteString(no5, ",");
                    int no1Length = analyseNo1.length;
                    int no2Length = analyseNo2.length;
                    int no3Length = analyseNo3.length;
                    int no4Length = analyseNo4.length;
                    int no5Length = analyseNo5.length;
                    int count;
                    for (int i = 0; i < no1Length; i++) {
                        count = Integer.valueOf(analyseNo1[i]);
                        if (count < 5)
                            analyseNo1[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseNo1[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseNo1[i] = "出" + count + "次";
                        hongqiu01.get(i).setBallsInf(analyseNo1[i]);
                    }
                    for (int i = 0; i < no2Length; i++) {
                        count = Integer.valueOf(analyseNo2[i]);
                        if (count < 5)
                            analyseNo2[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseNo2[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseNo2[i] = "出" + count + "次";
                        hongqiu02.get(i).setBallsInf(analyseNo2[i]);
                    }
                    for (int i = 0; i < no3Length; i++) {
                        count = Integer.valueOf(analyseNo3[i]);
                        if (count < 5)
                            analyseNo3[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseNo3[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseNo3[i] = "出" + count + "次";
                        hongqiu03.get(i).setBallsInf(analyseNo3[i]);
                    }
                    for (int i = 0; i < no4Length; i++) {
                        count = Integer.valueOf(analyseNo4[i]);
                        if (count < 5)
                            analyseNo4[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseNo4[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseNo4[i] = "出" + count + "次";
                        hongqiu04.get(i).setBallsInf(analyseNo4[i]);
                    }
                    for (int i = 0; i < no5Length; i++) {
                        count = Integer.valueOf(analyseNo5[i]);
                        if (count < 5)
                            analyseNo5[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseNo5[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseNo5[i] = "出" + count + "次";
                        hongqiu05.get(i).setBallsInf(analyseNo5[i]);
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
                String betInf = "1注     <font color='red'>2元</font>";
                moneyInf.setText(Html.fromHtml(betInf));
                hongqiu01.get(vertor[0]).setChoosed(true);
                hongqiu02.get(vertor[1]).setChoosed(true);
                hongqiu03.get(vertor[2]).setChoosed(true);
                hongqiu04.get(vertor[3]).setChoosed(true);
                hongqiu05.get(vertor[4]).setChoosed(true);
                redBallsLayout01.chooseBall(vertor[0]);
                redBallsLayout02.chooseBall(vertor[1]);
                redBallsLayout03.chooseBall(vertor[2]);
                redBallsLayout04.chooseBall(vertor[3]);
                redBallsLayout05.chooseBall(vertor[4]);
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
        plwNum01 = 0;
        plwNum02 = 0;
        plwNum03 = 0;
        plwNum04 = 0;
        plwNum05 = 0;
        // plwNum06 = 0;
        // plwNum07 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                plwNum01 = plwNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                plwNum02 = plwNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                plwNum03 = plwNum03 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu04.size(); i++) {
            if (hongqiu04.get(i).isChoosed()) {
                ballText.append(i);
                plwNum04 = plwNum04 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu05.size(); i++) {
            if (hongqiu05.get(i).isChoosed()) {
                ballText.append(i);
                plwNum05 = plwNum05 + 1;
            }
        }
        if (ballText.length() > 0 && ballText.length() < 5)
            ballText.deleteCharAt(ballText.length() - 1);
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int plwNum01 = 0;
    private int plwNum02 = 0;
    private int plwNum03 = 0;
    private int plwNum04 = 0;
    private int plwNum05 = 0;

    // private int plwNum06 = 0;
    // private int plwNum07 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals(",,,")) {
            ViewUtil.showTipsToast(this, "请输入一个号码");
        }
        else {
            if (plwNum01 > 1) {
                ViewUtil.showTipsToast(this, "万位至多输入1个红球");
            }
            else if (plwNum02 > 1) {
                ViewUtil.showTipsToast(this, "千位至多输入1个红球");
            }
            else if (plwNum03 > 1) {
                ViewUtil.showTipsToast(this, "百位至多输入1个红球");
            }
            else if (plwNum04 > 1) {
                ViewUtil.showTipsToast(this, "十位至多输入1个红球");
            }
            else if (plwNum05 > 1) {
                ViewUtil.showTipsToast(this, "个位至多输入1个红球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "plw");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(PLWActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }
}
