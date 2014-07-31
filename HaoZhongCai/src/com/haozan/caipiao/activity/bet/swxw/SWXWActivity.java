package com.haozan.caipiao.activity.bet.swxw;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryDiviningActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;

public class SWXWActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener {
    private static final String SWXW_TIPS = "至少选5个，猜中任4个10元，5个奖金看奖池";

    public static final int SWXW_HONGQIU_START = 1;
    public static final int SWXW_HONGQIU_LENGTH = 15;
    public static final int SWXW_HONGQIU_LIMIT = 12;
    public static final int SWXW_HONGQIU_MIN = 5;

    private static String hotCondintion = null;
    private ArrayList<BetBall> hongqiu;
    private BetBallsData hongqiuInf;
// private TextView redballChoosingNum;
    private NewBetBallsLayout redBallsLayout;
    private String[] analyseRed;

    @Override
    public void setKind() {
        this.kind = "swxw";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.swxw);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        hongqiuInf = new BetBallsData();
        hongqiu = new ArrayList<BetBall>();
        int redLength = SWXW_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu.add(ball);
        }
        hongqiuInf.setBetBalls(hongqiu);
        hongqiuInf.setCount(0);
        hongqiuInf.setColor("red");
        hongqiuInf.setLimit(redLength);
        hongqiuInf.setBallType(1);
    }

    protected void setupViews() {
        super.setupViews();
// redballChoosingNum = (TextView) this.findViewById(R.id.swxw_hongqiu_text);
        redBallsLayout = (NewBetBallsLayout) this.findViewById(R.id.swxw_hongqiu_balls);
        lotteryCalculator.setEnabled(false);
        lotteryCalculator.setVisibility(View.GONE);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        title.setText("十五选五");
        topArrow.setVisibility(View.GONE);
// redballChoosingNum.setText("红球：0/5个");
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        choosingInf.setText(SWXW_TIPS);
        redBallsLayout.initData(hongqiuInf, bigBallViews, this);
        redBallsLayout.setFullListener(this);
        redBallsLayout.setTouchMoveListener(this);
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        String[] nums = lotteryMode[0].split(",");
        int[] balls = new int[SWXW_HONGQIU_LIMIT];
        for (int i = 0; i < nums.length; i++) {
            balls[i] = Integer.parseInt(nums[i]);
            hongqiu.get(balls[i] - 1).setChoosed(true);
            redBallsLayout.chooseBall(balls[i] - 1);
        }
        onBallClickInf(-1, -1);
    }

    private String getBallsBetInf() {
        StringBuilder betText = new StringBuilder();
        int length = hongqiu.size();
        for (int i = 0; i < length; i++) {
            if (hongqiu.get(i).isChoosed()) {
                betText.append(StringUtil.betDataTransite((i + 1), length));
                betText.append(",");
            }
        }
        betText.deleteCharAt(betText.length() - 1);
        orgCode = betText.toString();
        betText.append(":1:");
        if (hongqiuInf.getCount() > 5) {
            betText.append("2:");
        }
        else {
            betText.append("1:");
        }
        return betText.toString();
    }

    private String getBallsDisplayInf() {
        StringBuilder betText = new StringBuilder();
        betText.append("<font color='red'>");
        int length = hongqiu.size();
        for (int i = 0; i < length; i++) {
            if (hongqiu.get(i).isChoosed()) {
                betText.append(StringUtil.betDataTransite((i + 1), length));
                betText.append(",");
            }
        }
        betText.deleteCharAt(betText.length() - 1);
        betText.append("</font>");
        return betText.toString();
    }

    protected boolean checkInput() {
        String inf = null;
        if (hongqiuInf.getCount() < SWXW_HONGQIU_MIN) {
            inf = SWXW_TIPS;
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
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(SWXW_HONGQIU_MIN, SWXW_HONGQIU_LENGTH);
        for (int i = 0; i < 5; i++) {
            hongqiu.get(randomRedNum[i]).setChoosed(true);
            redBallsLayout.chooseBall(randomRedNum[i]);
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
        redBallsLayout.resetBalls();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        choosingInf.setText(SWXW_TIPS);
        showBallNum();

    }

    @Override
    public void onBallClickFull(int ballType) {
        ViewUtil.showTipsToast(this, "您只能选" + SWXW_HONGQIU_LIMIT + "个红球");
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateAll();
    }

    @Override
    public void onBallClickInf(int ballType, int index) {
        Boolean refreshMoney = false;
        int hongNumber = hongqiuInf.getCount();
        if (hongNumber == 0)
            resetInf();
        else {
            enableClearBtn();
            if (hongNumber < 5) {
                invalidateNum();
            }
            else if (hongNumber > 5) {
                refreshMoney = true;
                // 计算投注总数
                betNumber = (int) (MathUtil.factorial(hongNumber, 5) / MathUtil.factorial(5, 5));
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber == 5) {
                refreshMoney = true;
                betNumber = 1;
                betMoney = betNumber * 2 * 1;
            }
        }
        checkBet(betMoney);
        if (refreshMoney) {
            invalidateAll();
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null)
                moneyInf.setText(Html.fromHtml(betInf));
        }
    }

    protected void invalidateAll() {
        analyseTips.setVisibility(View.VISIBLE);
        code = getBallsBetInf();
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml("已选:" + displayCode));
        showBallNum();
    }

    @Override
    protected void showBallNum() {
// redballChoosingNum.setText("红球：" + hongqiuInf.getCount() + "/5个");
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "15选5游戏规则");
        bundel.putString("lottery_help", "help_new/d15x5.html");
        intent.putExtras(bundel);
        intent.setClass(SWXWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(SWXWActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "swxw");
        bundle.putString("lotteryTypeZS", "0");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "15选5走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=swxw&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(SWXWActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

// 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout.refreshAllBallInf(showHotNum);
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
                    String basicNum = ja.getData(analyseData, "no_distrb");
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
                        hongqiu.get(i).setBallsInf(analyseRed[i]);
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
                for (int i = 0; i < 5; i++) {
                    hongqiu.get(vertor[i] - 1).setChoosed(true);
                    redBallsLayout.chooseBall(vertor[i] - 1);
                }
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
        swxwNum01 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu.size(); i++) {
            if (hongqiu.get(i).isChoosed()) {
                ballText.append(StringUtil.betDataTransite(i + 1));
                ballText.append(",");
                swxwNum01 = swxwNum01 + 1;
            }
        }
        if (ballText.length() > 0)
            ballText.deleteCharAt(ballText.length() - 1);
        if (ballText.toString() == "") {
            return null;
        }
        else {
            luckynum = ballText.toString();
            return ballText.toString();
        }
    }

    private int swxwNum01;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code() == null) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (swxwNum01 > 5) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理5个号码");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("kind", "swxw");
                bundle.putInt("no", 20);
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(SWXWActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }
}
