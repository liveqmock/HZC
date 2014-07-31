package com.haozan.caipiao.activity.bet.qlc;

import java.util.ArrayList;

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
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.view.NewBetBallsLayout;
import com.haozan.caipiao.view.NewBetBallsLayout.OnBallOpeListener;

public class QLCActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener {
    private static final String QLC_TIPS = "请选择至少7个红球";
    
    public static final int QLC_HONGQIU_START = 1;
    public static final int QLC_HONGQIU_LENGTH = 30;
    public static final int QLC_HONGQIU_LIMIT = 16;
    public static final int QLC_HONGQIU_MIN = 7;
    
    private static String hotCondintion = null;

    private ArrayList<BetBall> hongqiu;
    private BetBallsData hongqiuInf;
//    private TextView redballChoosingNum;
    private NewBetBallsLayout redBallsLayout;
    private String[] analyseRed;
    private String[] analyseBlue;
    private String[] analyseInf;

    @Override
    public void setKind() {
        this.kind = "qlc";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.qlc);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        hongqiuInf = new BetBallsData();
        hongqiu = new ArrayList<BetBall>();
        int redLength = QLC_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i + 1));
            hongqiu.add(ball);
        }
        hongqiuInf.setBetBalls(hongqiu);
        hongqiuInf.setCount(0);
        hongqiuInf.setColor("red");
        hongqiuInf.setLimit(QLC_HONGQIU_LIMIT);
        hongqiuInf.setBallType(1);
    }

    protected void setupViews() {
        super.setupViews();
//        redballChoosingNum = (TextView) this.findViewById(R.id.qlc_hongqiu_text);
        redBallsLayout = (NewBetBallsLayout) this.findViewById(R.id.qlc_hongqiu_balls);
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
        if(ifShowImgHelp){
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        title.setText("七乐彩");
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        choosingInf.setText(QLC_TIPS);
        redBallsLayout.initData(hongqiuInf, bigBallViews, this);
        redBallsLayout.setFullListener(this);
        redBallsLayout.setTouchMoveListener(this);
        topArrow.setVisibility(View.GONE);
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        String[] nums = lotteryMode[0].split(",");
        int[] hongBalls = new int[QLC_HONGQIU_LIMIT];
        for (int i = 0; i < nums.length; i++) {
            hongBalls[i] = Integer.parseInt(nums[i]);
            hongqiu.get(hongBalls[i] - 1).setChoosed(true);
            redBallsLayout.chooseBall(hongBalls[i] - 1);
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
        if (hongqiuInf.getCount() > 7) {
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
        if (hongqiuInf.getCount() < QLC_HONGQIU_MIN) {
            inf = QLC_TIPS;
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
        int[] randomRedNum =
            MathUtil.getRandomNumNotEquals(QLC_HONGQIU_MIN,
                                               QLC_HONGQIU_LENGTH);
        for (int i = 0; i < 7; i++) {
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

    @Override
    public void onBallClickFull(int ballType) {
        ViewUtil.showTipsToast(this,"您只能选" + QLC_HONGQIU_LIMIT + "个红球");
    }

    protected void resetInf() {
        super.resetInf();
        choosingInf.setText(QLC_TIPS);
        showBallNum();
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
            if (hongNumber >= 7) {
                refreshMoney = true;
                // 计算投注总数
                betNumber = (int) (MathUtil.factorial(hongNumber, 7) / MathUtil.factorial(7, 7));
                betMoney = betNumber * 2 * 1;
            }
            else
                invalidateNum();
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
        code = getBallsBetInf();
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml("已选:" + displayCode));
        showBallNum();
    }

    @Override
    protected void showBallNum() {
//        redballChoosingNum.setText("红球：" + hongqiuInf.getCount() + "/7个");
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "七乐彩游戏规则");
        bundel.putString("lottery_help", "help_new/qlc.html");
        intent.putExtras(bundel);
        intent.setClass(QLCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }
    
    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "七乐彩走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=qlc&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(QLCActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }


    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(QLCActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "qlc");
        bundle.putString("lotteryTypeZS", "0");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
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

    // 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout.refreshAllBallInf(showHotNum);
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
                    if (redLength == blueLength) {
                        analyseInf = new String[redLength];
                        int count;
                        String hotCondition;
                        for (int i = 0; i < redLength; i++) {
                            count = Integer.valueOf(analyseRed[i]) + Integer.valueOf(analyseBlue[i]);
                            if (count < 5)
                                hotCondition = "出<font color='blue'>" + count + "</font>次";
                            else if (count > 10)
                                hotCondition = "出<font color='red'>" + count + "</font>次";
                            else
                                hotCondition = "出" + count + "次";
                            analyseInf[i] = hotCondition;
                            hongqiu.get(i).setBallsInf(analyseInf[i]);
                        }
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
                for (int i = 0; i < 7; i++) {
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
        qlcNum01 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu.size(); i++) {
            if (hongqiu.get(i).isChoosed()) {
                ballText.append(StringUtil.betDataTransite(i + 1));
                ballText.append(",");
                qlcNum01 = qlcNum01 + 1;
            }
        }
        if (ballText.length() > 0)
            ballText.deleteCharAt(ballText.length() - 1);
        return ballText.toString();
    }

    private int qlcNum01 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals("")) {
            ViewUtil.showTipsToast(this,"分析功能至少需要输入1个号码");
        }
        else {
            if (qlcNum01 > 7) {
                ViewUtil.showTipsToast(this,"分析功能暂时至多能处理7个号码");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "qlc");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(QLCActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }

    }
}
