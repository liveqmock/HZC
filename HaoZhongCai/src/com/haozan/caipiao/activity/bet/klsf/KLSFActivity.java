package com.haozan.caipiao.activity.bet.klsf;

import java.util.ArrayList;

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
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
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

public class KLSFActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private static final String KLSF_TIPS01 = "请选择1个号码";
    private static final String KLSF_TIPS02 = "请至少选择1个号码";
    private static final String KLSF_TIPS03 = "请至少选择2个号码";
    private static final String KLSF_TIPS04 = "请至少选择3个号码";
    private static final String KLSF_TIPS05 = "请至少选择4个号码";
    private static final String KLSF_TIPS06 = "请至少选择5个号码";

    public static final int KLSF_HONGQIU_START = 1;
    public static final int KLSF_HONGQIU_LENGTH = 21;
    public static final int KLSF_HONGQIU_LIMIT01 = 10;
    public static final int KLSF_HONGQIU_LIMIT02 = 10;
    public static final int KLSF_HONGQIU_LIMIT03 = 14;
    public static final int KLSF_HONGQIU_LIMIT04 = 18;
    public static final int KLSF_HONGQIU_MIN01 = 1;
    public static final int KLSF_HONGQIU_MIN02 = 2;
    public static final int KLSF_HONGQIU_MIN03 = 3;
    public static final int KLSF_HONGQIU_MIN04 = 4;
    public static final int KLSF_HONGQIU_MIN05 = 5;
    
    private static final String KLSF_HONGQIU_NAME = "hongqiu";
    private static String hotCondintion = null;
    private int[] klsf_num_min_array = {KLSF_HONGQIU_MIN01, KLSF_HONGQIU_MIN02,
            KLSF_HONGQIU_MIN03, KLSF_HONGQIU_MIN04,
            KLSF_HONGQIU_MIN05};
    private int[] klsf_num_limit_array = {KLSF_HONGQIU_LIMIT01,
            KLSF_HONGQIU_LIMIT02, KLSF_HONGQIU_LIMIT03,
            KLSF_HONGQIU_LIMIT04};
    private String[] klsf_tip_array = {KLSF_TIPS01, KLSF_TIPS02, KLSF_TIPS03, KLSF_TIPS04, KLSF_TIPS05,
            KLSF_TIPS06};
    private int lotteryType = 9;
    private int index = 0;
    private BetBallsData hongqiuInf01;
    private ArrayList<BetBall> hongqiu01;
    private RelativeLayout termLayout;
    private NewBetBallsLayout redBallsLayout01;
    private LinearLayout ssl_bet_field_bg01;
    private TextView choosingCountHundred;
    private PopMenu titlePopup;
    private int index_num = 0;
    private TextView lotteryIntroduce;

// private RelativeLayout topBgLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.klsf);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        databaseData = getSharedPreferences("user", 0).edit();
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        for (int i = 1; i < KLSF_HONGQIU_LENGTH + 1; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setBallType(1);
        hongqiuInf01.setLimit(getKLSFLimitNum(lotteryType));
    }

    protected void setupViews() {
        super.setupViews();
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        choosingCountHundred = (TextView) this.findViewById(R.id.klsf_hongqiu01_text);
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.klsf_hongqiu_balls01);
        ssl_bet_field_bg01 = (LinearLayout) this.findViewById(R.id.klsf_hongqiu_balls01_linear);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        lotteryIntroduce = (TextView) this.findViewById(R.id.lottery_introdution);
// topBgLinear = (RelativeLayout) findViewById(R.id.top_bg_linear);
    }

    private void init() {
        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }
        lotteryIntroduce.setVisibility(View.GONE);
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lotteryType = bundle.getInt("bet_way");
            if (lotteryType == 9) {
                databaseData.putString("klsf_way", "klsf_lucky_special");
                index = 1;
            }
            else if (lotteryType == 5) {
                databaseData.putString("klsf_way", "klsf_lucky_one");
                index = 2;
            }
            else if (lotteryType == 4) {
                databaseData.putString("klsf_way", "klsf_lucky_two");
                index = 3;
            }
            else if (lotteryType == 3) {
                databaseData.putString("klsf_way", "klsf_lucky_three");
                index = 4;
            }
            else if (lotteryType == 2) {
                databaseData.putString("klsf_way", "klsf_lucky_four");
                index = 5;
            }
            else if (lotteryType == 1) {
                databaseData.putString("klsf_way", "klsf_lucky_five");
                index = 6;
            }
            else
                resetLotteryType();
            databaseData.commit();
        }
        else {
            resetLotteryType();
        }

// redBallsLayout01.initData(hongqiuInf01, bigBallViews, this);
// redBallsLayout01.drawBalls(KLSF_HONGQIU_LENGTH, "red");
// redBallsLayout01.setFullListener(this);
// redBallsLayout01.setTouchMoveListener(this);
        drawBallNum();
        showWay();
        showChoosingInf();
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
        luckyBallSelect.setClickable(false);
        luckyBallSelect.setVisibility(View.INVISIBLE);
        index_num = index - 1;
    }

    private void drawBallNum() {
        redBallsLayout01.initData(hongqiuInf01, bigBallViews, this);
        redBallsLayout01.drawBalls(KLSF_HONGQIU_LENGTH, "red");
        redBallsLayout01.setFullListener(this);
        redBallsLayout01.setTouchMoveListener(this);
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("klsf_way", "klsf_lucky_special");
        if (sdWay.equals("klsf_lucky_special")) {
            lotteryType = 9;
            index = 1;
        }
        else if (sdWay.equals("klsf_lucky_one")) {
            lotteryType = 5;
            index = 2;
        }
        else if (sdWay.equals("klsf_lucky_two")) {
            lotteryType = 4;
            index = 3;
        }
        else if (sdWay.equals("klsf_lucky_three")) {
            lotteryType = 3;
            index = 4;
        }
        else if (sdWay.equals("klsf_lucky_four")) {
            lotteryType = 2;
            index = 5;
        }
        else if (sdWay.equals("klsf_lucky_five")) {
            lotteryType = 1;
            index = 6;
        }
    }

    private void showWay() {
        if (lotteryType == 9) {
            title.setText("快乐十分好运特");
            choosingInf.setText(KLSF_TIPS01);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 5) {
            title.setText("快乐十分好运一");
            choosingInf.setText(KLSF_TIPS02);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 4) {
            title.setText("快乐十分好运二");
            choosingInf.setText(KLSF_TIPS03);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 3) {
            title.setText("快乐十分好运三");
            choosingInf.setText(KLSF_TIPS04);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 2) {
            title.setText("快乐十分好运四");
            choosingInf.setText(KLSF_TIPS05);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 1) {
            title.setText("快乐十分好运五");
            choosingInf.setText(KLSF_TIPS06);
            redBallsLayout01.setVisibility(View.VISIBLE);
            choosingCountHundred.setVisibility(View.VISIBLE);
            ssl_bet_field_bg01.setVisibility(View.VISIBLE);
        }
    }

    protected void showChoosingInf() {
        if (lotteryType == 9) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
        else if (lotteryType == 5) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
        else if (lotteryType == 4) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
        else if (lotteryType == 3) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
        else if (lotteryType == 2) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
        else if (lotteryType == 1) {
            choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/" + getKLSFMinNum(lotteryType) +
                "个");
        }
    }

    @Override
    protected void analyseData(String json) {

    }

    @Override
    public String getQ_code() {
        return null;
    }

    @Override
    protected void searchLuckyNum() {

    }

    @Override
    public void setKind() {
        this.kind = "klsf";
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

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "快乐十分游戏规则");
        bundel.putString("lottery_help", "klsf/klsf_general.html");
        intent.putExtras(bundel);
        intent.setClass(KLSFActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "快乐十分走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=klsf&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(KLSFActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void resetInf() {
        super.resetInf();
        if (lotteryType == 9)
            choosingInf.setText(KLSF_TIPS01);
        else if (lotteryType == 5)
            choosingInf.setText(KLSF_TIPS02);
        else if (lotteryType == 4)
            choosingInf.setText(KLSF_TIPS03);
        else if (lotteryType == 3)
            choosingInf.setText(KLSF_TIPS04);
        else if (lotteryType == 2)
            choosingInf.setText(KLSF_TIPS05);
        else if (lotteryType == 1)
            choosingInf.setText(KLSF_TIPS06);
        showBallNum();
    }

    private void invalidateNum() {
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    private void setAnalyseTipsVisibility(int hongCount01) {
        if (hongCount01 > 1) {
            analyseTips.setVisibility(View.GONE);
        }
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
        choosingInf.setText(Html.fromHtml(displayCode));
        showChoosingInf();
    }

    private String getBallsDisplayInf() {
        if (lotteryType == 9)
            return getBallsDisplayFirstKindInf();
        else if (lotteryType == 5) {
            return getBallsDisplayFirstKindInf();
        }
        else if (lotteryType == 4) {
            return getBallsDisplayFirstKindInf();
        }
        else if (lotteryType == 3) {
            return getBallsDisplayFirstKindInf();
        }
        else if (lotteryType == 2) {
            return getBallsDisplayFirstKindInf();
        }
        else if (lotteryType == 1) {
            return getBallsDisplayFirstKindInf();
        }
        else
            return null;
    }

    private String lotteryDisplayCodeHeader() {
        if (lotteryType == 9)
            return "[好运特]";
        if (lotteryType == 5)
            return "[好运一]";
        if (lotteryType == 4)
            return "[好运二]";
        if (lotteryType == 3)
            return "[好运三]";
        if (lotteryType == 2)
            return "[好运四]";
        if (lotteryType == 1)
            return "[好运五]";
        return null;
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append(lotteryDisplayCodeHeader());
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("—");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i + 1 + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private void showPopupViews() {
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        titlePopup = new PopMenu(KLSFActivity.this, false);
        titlePopup.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayKLSF, LotteryUtils.moneyArrayKLSF,
                             1, findViewById(R.id.top).getMeasuredWidth() - 20, index_num, true, true);
        titlePopup.setButtonClickListener(this);
        showPopupCenter(titlePopup);
        titlePopup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
            }
        });
    }

    private int getKLSFMinNum(int lotteryType) {
        if (lotteryType == 9 || lotteryType == 5)
            return klsf_num_min_array[0];
        else if (lotteryType == 4)
            return klsf_num_min_array[1];
        else if (lotteryType == 3)
            return klsf_num_min_array[2];
        else if (lotteryType == 2)
            return klsf_num_min_array[3];
        else if (lotteryType == 1)
            return klsf_num_min_array[4];
        return 0;
    }

    private int getKLSFLimitNum(int lotteryType) {
        if (lotteryType == 9)
            return klsf_num_limit_array[0];
        else if (lotteryType == 5)
            return klsf_num_limit_array[1];
        else if (lotteryType == 4 || lotteryType == 3 || lotteryType == 2)
            return klsf_num_limit_array[2];
        else if (lotteryType == 1)
            return klsf_num_limit_array[3];
        return 0;
    }

    protected void defaultNum(String betNum) {
        String[] lotteryMode = betNum.split("\\:");
        String[] nums = lotteryMode[0].split(",");
        int[] balls = new int[getKLSFLimitNum(lotteryType)];
        for (int i = 0; i < nums.length; i++)
            balls[i] = Integer.parseInt(nums[i]);
        for (int i = 0; i < nums.length; i++) {
            int num = Integer.valueOf(nums[i]);
            hongqiu01.get(num - 1).setChoosed(true);
            redBallsLayout01.chooseBall(num - 1);
        }
        onBallClickInf(-1, -1);
    }

    @Override
    public void randomBalls() {
        clearBalls();
        String betInf = "1注     <font color='red'>2元</font>";
        moneyInf.setText(Html.fromHtml(betInf));
        int[] randomRedNum =
            MathUtil.getRandomNumNotEquals(getKLSFMinNum(lotteryType), KLSF_HONGQIU_LENGTH);
        if (lotteryType == 9) {
            hongqiu01.get(randomRedNum[0]).setChoosed(true);
            redBallsLayout01.chooseBall(randomRedNum[0]);
        }
        else if (lotteryType == 5) {

            for (int i = 0; i < getKLSFMinNum(lotteryType); i++) {
                hongqiu01.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum[i]);
            }
        }
        else if (lotteryType == 4) {
            for (int i = 0; i < getKLSFMinNum(lotteryType); i++) {
                hongqiu01.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum[i]);
            }
        }
        else if (lotteryType == 3) {
            for (int i = 0; i < getKLSFMinNum(lotteryType); i++) {
                hongqiu01.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum[i]);
            }
        }
        else if (lotteryType == 2) {
            for (int i = 0; i < getKLSFMinNum(lotteryType); i++) {
                hongqiu01.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum[i]);
            }
        }
        else if (lotteryType == 1) {
            for (int i = 0; i < getKLSFMinNum(lotteryType); i++) {
                hongqiu01.get(randomRedNum[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum[i]);
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
        if (lotteryType == 9) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 5) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 4) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 3) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 2) {
            redBallsLayout01.resetBalls();
        }
        else if (lotteryType == 1) {
            redBallsLayout01.resetBalls();
        }
        resetInf();
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 9) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN01)
                inf = "请至少输入1个号码";
        }
        else if (lotteryType == 5) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN01)
                inf = "请至少输入1个号码";
        }
        else if (lotteryType == 4) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN02)
                inf = "请至少输入2个号码";
        }
        else if (lotteryType == 3) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN03)
                inf = "请至少输入3个号码";
        }
        else if (lotteryType == 2) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN04)
                inf = "请至少输入4个号码";
        }
        else if (lotteryType == 1) {
            if (hongqiuInf01.getCount() < KLSF_HONGQIU_MIN05)
                inf = "请至少输入5个号码";
        }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    private String getBallsBetInf() {
        if (lotteryType == 9)
            return getBallsBetKindInf();
        else if (lotteryType == 5)
            return getBallsBetKindInf();
        else if (lotteryType == 4)
            return getBallsBetKindInf();
        else if (lotteryType == 3)
            return getBallsBetKindInf();
        else if (lotteryType == 2)
            return getBallsBetKindInf();
        else if (lotteryType == 1)
            return getBallsBetKindInf();
        else
            return null;
    }

    private String getBallsBetKindInf() {
        StringBuilder betBallText = new StringBuilder();
        int hongLength01 = hongqiu01.size();
        for (int i = 0; i < hongLength01; i++) {
            if (hongqiu01.get(i).isChoosed()) {
                betBallText.append(StringUtil.betDataTransite(i + 1));
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();

        if (lotteryType == 1)
            betBallText.append(":1:");
        else if (lotteryType == 2)
            betBallText.append(":2:");
        else if (lotteryType == 3)
            betBallText.append(":3:");
        else if (lotteryType == 4)
            betBallText.append(":4:");
        else if (lotteryType == 5)
            betBallText.append(":5:");
        else if (lotteryType == 9)
            betBallText.append(":9:");

        if (hongqiuInf01.getCount() > getKLSFMinNum(lotteryType)) {
            betBallText.append("2:");
        }
        else {
            betBallText.append("1:");
        }
        return betBallText.toString();
    }

    @Override
    protected void extraBundle(Bundle bundle) {
        bundle.putInt("bet_way", lotteryType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }

    @Override
    public void onBallClickFull(int kind) {

    }

    @Override
    public void onBallClickInf(int kind, int index) {
        Boolean refreshMoney = false;
        int hongNumber = hongqiuInf01.getCount();
        if (hongNumber == 0)
            resetInf();
        else {
            enableClearBtn();
            if (hongNumber < getKLSFMinNum(lotteryType)) {
                invalidateNum();
            }
            else if (hongNumber > getKLSFMinNum(lotteryType)) {
                refreshMoney = true;
                // 计算投注总数
                if (lotteryType == 5 || lotteryType == 9)
                    betNumber = hongNumber;
                else
                    betNumber =
                        (int) (MathUtil.factorial(hongNumber, getKLSFMinNum(lotteryType)) / MathUtil.factorial(getKLSFMinNum(lotteryType),
                                                                                                               getKLSFMinNum(lotteryType)));
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber == getKLSFMinNum(lotteryType)) {
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
        setAnalyseTipsVisibility(hongNumber);
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        titlePopup.dismiss();
        databaseData.putString("klsf_way", LotteryUtils.KLSFWay[kind]);
        databaseData.commit();
        clearBalls();
        index_num = kind;
        title.setText(tabName);
        setLotteryType(kind + 1);
        showWay();
        showBallNum();
        title.setText("快乐十分" + LotteryUtils.textArrayKLSF[kind]);
        showChoosingInf();
        initData();
        drawBallNum();
    }

    private void setLotteryType(int kind) {
        if (kind == 1) {
            lotteryType = 9;
// index = 1;
        }
        else if (kind == 2) {
            lotteryType = 5;
// index = 2;
        }
        else if (kind == 3) {
            lotteryType = 4;
// index = 3;
        }
        else if (kind == 4) {
            lotteryType = 3;
// index = 4;
        }
        else if (kind == 5) {
            lotteryType = 2;
// index = 5;
        }
        else if (kind == 6) {
            lotteryType = 1;
// index = 6;
        }
    }
}
