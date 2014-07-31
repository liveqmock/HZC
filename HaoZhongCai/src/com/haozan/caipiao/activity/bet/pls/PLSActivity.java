package com.haozan.caipiao.activity.bet.pls;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
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

public class PLSActivity
    extends BetDigitalBasic
    implements OnClickListener, OnBallOpeListener, PopMenuButtonClickListener {
    private static final String SD_TIPS01 = "每位选1个，顺序猜中3个开奖号，奖金1000元";
    private static final String SD_TIPS02 = "至少选2个，猜中3个开奖号（2个相同），奖金320元";
    private static final String SD_TIPS03 = "至少选3个，猜中3个开奖号（3个不同），奖金160元";
    private static final String SD_TIPS04 = "每位至少选1个，开出对子中对号和单号，奖金320元";
    private static String hotCondintion = null;

    private int lotteryType = 1;
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

    private RelativeLayout termLayout;
// private TextView choosingCountHundred;
// private TextView choosingCountTen;
// private TextView choosingCountUnit;

    private LinearLayout choosingCountHundredLinear;
    private LinearLayout choosingCountTenLinear;
    private LinearLayout choosingCountUnitLinear;

    private PopupWindow mPopupWindow;

    private LinearLayout firstIcon;
    private LinearLayout secondIcon;
    private LinearLayout thirdIcon;
    private LinearLayout forthIcon;

    private TextView sdZhiXuan_df;
    private TextView sdZuSan_df;
    private TextView sdZuLiu_bh;
    private TextView sdZuSan_bh;

    private TextView sdFirstItem;
    private TextView sdSecondItem;
    private TextView sdThirdItem;
    private TextView sdForthItem;
// private RelativeLayout topBgLinear;

    private PopMenu popMneu;
    private int index_num = 0;
    private TextView flagHongqiu01, flagHongqiu02, flagHongqiu03;
    private TextView selectInfo;

    @Override
    public void setKind() {
        this.kind = "pls";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.pls);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        // init red section one
        hongqiuInf01 = new BetBallsData();
        hongqiu01 = new ArrayList<BetBall>();
        int redLength = SDActivity.SD_HONGQIU_LENGTH;
        for (int i = 0; i < redLength; i++) {
            BetBall ball = new BetBall();
            ball.setId(i);
            ball.setContent(String.valueOf(i));
            hongqiu01.add(ball);
        }
        hongqiuInf01.setBetBalls(hongqiu01);
        hongqiuInf01.setCount(0);
        hongqiuInf01.setColor("red");
        hongqiuInf01.setLimit(SDActivity.SD_HONGQIU_LIMIT);
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
        hongqiuInf02.setLimit(SDActivity.SD_HONGQIU_LIMIT);
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
        hongqiuInf03.setLimit(SDActivity.SD_HONGQIU_LIMIT);
        hongqiuInf03.setBallType(3);
    }

    protected void setupViews() {
        super.setupViews();
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
// choosingCountHundred = (TextView) this.findViewById(R.id.sd_hongqiu01_text);
// choosingCountTen = (TextView) this.findViewById(R.id.sd_hongqiu02_text);
// choosingCountUnit = (TextView) this.findViewById(R.id.sd_hongqiu03_text);

        choosingCountHundredLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu01_linear);
        choosingCountTenLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu02_linear);
        choosingCountUnitLinear = (LinearLayout) this.findViewById(R.id.sd_hongqiu03_linear);

        // setup the first section
        redBallsLayout01 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls01);
        // setup the second section
        redBallsLayout02 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls02);
        // setup the third section
        redBallsLayout03 = (NewBetBallsLayout) this.findViewById(R.id.sd_hongqiu_balls03);
        luckyBallSelect.setVisibility(View.INVISIBLE);
        luckyBallSelect.setEnabled(false);
        lotteryCalculator.setVisibility(View.INVISIBLE);
        lotteryCalculator.setEnabled(false);
        normalToolsLayout.setVisibility(View.GONE);
        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        flagHongqiu01 = (TextView) findViewById(R.id.tv_flag_hongqiu01);
        flagHongqiu02 = (TextView) findViewById(R.id.tv_flag_hongqiu02);
        flagHongqiu03 = (TextView) findViewById(R.id.tv_flag_hongqiu03);
        selectInfo = (TextView) findViewById(R.id.select_info);
    }

    private void initDefault(View view) {
        firstIcon = (LinearLayout) view.findViewById(R.id.item_first_layout);
        secondIcon = (LinearLayout) view.findViewById(R.id.item_second_layout);
        thirdIcon = (LinearLayout) view.findViewById(R.id.item_third_layout);
        forthIcon = (LinearLayout) view.findViewById(R.id.item_forth_layout);

        sdZhiXuan_df = (TextView) view.findViewById(R.id.sd_zhixuan);
        sdZuSan_df = (TextView) view.findViewById(R.id.sd_zusan_danfu);
        sdZuLiu_bh = (TextView) view.findViewById(R.id.sd_zuliu);
        sdZuSan_bh = (TextView) view.findViewById(R.id.sd_zusan);

        sdFirstItem = (TextView) view.findViewById(R.id.radio_item_first);
        sdSecondItem = (TextView) view.findViewById(R.id.radio_item_second);
        sdThirdItem = (TextView) view.findViewById(R.id.radio_item_third);
        sdForthItem = (TextView) view.findViewById(R.id.radio_item_forth);
    }

    private void setTextColor(int index) {
        sdZhiXuan_df.setTextColor(Color.BLACK);
        sdZuSan_df.setTextColor(Color.BLACK);
        sdZuLiu_bh.setTextColor(Color.BLACK);
        sdZuSan_bh.setTextColor(Color.BLACK);

        sdFirstItem.setTextColor(getResources().getColor(R.color.dark_purple));
        sdSecondItem.setTextColor(getResources().getColor(R.color.dark_purple));
        sdThirdItem.setTextColor(getResources().getColor(R.color.dark_purple));
        sdForthItem.setTextColor(getResources().getColor(R.color.dark_purple));
        if (index == 1) {
            sdZhiXuan_df.setTextColor(Color.WHITE);
            sdFirstItem.setTextColor(Color.WHITE);
        }
        else if (index == 2) {
            sdZuSan_df.setTextColor(Color.WHITE);
            sdSecondItem.setTextColor(Color.WHITE);
        }
        else if (index == 3) {
            sdZuSan_bh.setTextColor(Color.WHITE);
            sdThirdItem.setTextColor(Color.WHITE);
        }
        else if (index == 4) {
            sdZuLiu_bh.setTextColor(Color.WHITE);
            sdForthItem.setTextColor(Color.WHITE);
        }
    }

    private void iniSelectedItemBg() {
        firstIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose);
        secondIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose);
        thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose);
        forthIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose);
    }

    private void initIcon(int LuckyType) {
        if (LuckyType == 1)
            firstIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        else if (LuckyType == 2)
            secondIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        else if (LuckyType == 3)
            thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        else if (LuckyType == 4)
            forthIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
    }

    private void showPopupViews() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(R.layout.pls_selected_dialog_swtich, null);
        initDefault(waySwitchLayout);
        firstIcon.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lotteryType != 1) {
                    databaseData.putString("pls_way", "plszx");
                    databaseData.commit();
                    clearBalls();
                    lotteryType = 1;
                    title.setText("直选");
                    showWay();
                    showBallNum();
                }
                mPopupWindow.dismiss();
            }
        });
        secondIcon.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lotteryType != 2) {
                    databaseData.putString("pls_way", "plszs");
                    databaseData.commit();
                    clearBalls();
                    lotteryType = 2;
                    title.setText("组三包号");
                    showWay();
                    showBallNum();
                }
                mPopupWindow.dismiss();
            }
        });
        thirdIcon.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (lotteryType != 3) {
                    databaseData.putString("pls_way", "plszl");
                    databaseData.commit();
                    clearBalls();
                    lotteryType = 3;
                    title.setText("组六包号");
                    showWay();
                    showBallNum();
                }
                mPopupWindow.dismiss();
            }
        });
        forthIcon.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lotteryType != 4) {
                    databaseData.putString("pls_way", "plszsdf");
                    databaseData.commit();
                    clearBalls();
                    lotteryType = 4;
                    title.setText("组三单复式");
                    showWay();
                    showBallNum();
                }
                mPopupWindow.dismiss();
            }
        });
        iniSelectedItemBg();
        initIcon(lotteryType);
        setTextColor(lotteryType);
        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setWidth(termLayout.getMeasuredWidth());
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(waySwitchLayout);
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        popMneu = new PopMenu(PLSActivity.this, false);
        popMneu.setLayout(R.layout.pop_grid_view, LotteryUtils.textArrayPLS, LotteryUtils.moneyArrayPLS, 1,
                          findViewById(R.id.top).getMeasuredWidth() - 20, index_num, true, true);
        popMneu.setButtonClickListener(this);
        topArrow.setImageResource(R.drawable.arrow_up_white);
// popMneu.showAsDropDown(termLayout);
        showPopupCenter(popMneu);
        popMneu.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
            }
        });
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("bet_way");
            if (type != 0) {
                lotteryType = type;
                lotteryType = bundle.getInt("bet_way");
                if (lotteryType == 1)
                    databaseData.putString("pls_way", "plszx");
                else if (lotteryType == 2)
                    databaseData.putString("pls_way", "plszs");
                else if (lotteryType == 3)
                    databaseData.putString("pls_way", "plszl");
                else if (lotteryType == 4)
                    databaseData.putString("pls_way", "plszsdf");
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
        showBallNum();
        initInf();
// if (hotCondintion == null)
// getAnalyseData();
// else
// analyseData(hotCondintion);
        index_num = lotteryType - 1;
    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("pls_way", "plszx");
        if (sdWay.equals("plszx")) {
            lotteryType = 1;
        }
        else if (sdWay.equals("plszs")) {
            lotteryType = 2;
        }
        else if (sdWay.equals("plszl")) {
            lotteryType = 3;
        }
        else if (sdWay.equals("plszsdf")) {
            lotteryType = 4;
        }
    }

// display the appointed way of the 3d
    private void showWay() {
        if (lotteryType == 1) {
            title.setText("直选");
            selectInfo.setText(SD_TIPS01);
            redBallsLayout02.setVisibility(View.VISIBLE);
            redBallsLayout03.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.VISIBLE);
// choosingCountUnit.setVisibility(View.VISIBLE);

            choosingCountTenLinear.setVisibility(View.VISIBLE);
            choosingCountUnitLinear.setVisibility(View.VISIBLE);
        }
        else if (lotteryType == 2) {
            title.setText("组三包号");
            selectInfo.setText(SD_TIPS02);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);

            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);
        }
        else if (lotteryType == 3) {
            title.setText("组六包号");
            selectInfo.setText(SD_TIPS03);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.GONE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.GONE);

            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.GONE);
        }
        else if (lotteryType == 4) {
            title.setText("组三单复式");
            selectInfo.setText(SD_TIPS04);
            redBallsLayout02.setVisibility(View.GONE);
            redBallsLayout03.setVisibility(View.VISIBLE);
// choosingCountTen.setVisibility(View.GONE);
// choosingCountUnit.setVisibility(View.VISIBLE);
            choosingCountTenLinear.setVisibility(View.GONE);
            choosingCountUnitLinear.setVisibility(View.VISIBLE);
        }
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
        }
        else if (lotteryType == 2) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
        }
        else if (lotteryType == 3) {
            String[] nums = lotteryMode[0].split(",");
            int length = nums.length;
            for (int i = 0; i < length; i++) {
                int num = Integer.valueOf(nums[i]);
                hongqiu01.get(num).setChoosed(true);
                redBallsLayout01.chooseBall(num);
            }
        }
        else if (lotteryType == 4) {
            if (lotteryMode[2].equals("1")) {
                String[] nums = lotteryMode[0].split(",");
                int num1 = Integer.valueOf(nums[0]);
                hongqiu01.get(num1).setChoosed(true);
                redBallsLayout01.chooseBall(num1);
                int num2 = Integer.valueOf(nums[2]);
                hongqiu03.get(num2).setChoosed(true);
                redBallsLayout03.chooseBall(num2);
            }
            else if (lotteryMode[2].equals("2")) {
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
                    hongqiu03.get(num).setChoosed(true);
                    redBallsLayout03.chooseBall(num);
                }
            }
        }
        onBallClickInf(-1, -1);
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
        else
            return null;
    }

    private String getBallsBetFirstKindInf() {
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
        orgCode = betBallText.toString();
        betBallText.append(":1:");
        if (hongqiuInf01.getCount() > 1 || hongqiuInf02.getCount() > 1 || hongqiuInf03.getCount() > 1) {
            betBallText.append("2:");
        }
        else {
            betBallText.append("1:");
        }
        return betBallText.toString();
    }

    private String getBallsBetSecondKindInf() {
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
        betBallText.append(":2:3:");
        return betBallText.toString();
    }

    private String getBallsBetThirdKindInf() {
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
        betBallText.append(":3:3:");
        return betBallText.toString();
    }

    private String getBallsBetForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        if (hongqiuInf01.getCount() > 1 || hongqiuInf03.getCount() > 1) {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i);
            }
            betBallText.append(",");
            int hongLength03 = hongqiu03.size();
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i);
            }
            orgCode = betBallText.toString();
            betBallText.append(":2:2:");
        }
        else {
            int hongLength01 = hongqiu01.size();
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed())
                    betBallText.append(i + "," + i);
            }
            betBallText.append(",");
            int hongLength03 = hongqiu03.size();
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
                    betBallText.append(i);
            }
            orgCode = betBallText.toString();
            betBallText.append(":2:1:");
        }
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
        else
            return null;
    }

    private String getBallsDisplayFirstKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[直选]");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("-");
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
            betBallText.append("-");
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
            betBallText.append("-");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed())
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
        betBallText.append("[组三包号]");
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

    private String getBallsDisplayThirdKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组六包号]");
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

    private String getBallsDisplayForthKindInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三单复式]");
        int hongLength01 = hongqiu01.size();
        if (hongqiuInf01.getCount() == 0) {
            betBallText.append("-");
        }
        else {
            for (int i = 0; i < hongLength01; i++) {
                if (hongqiu01.get(i).isChoosed()) {
                    betBallText.append(i);
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int hongLength03 = hongqiu03.size();
        if (hongqiuInf03.getCount() == 0) {
            betBallText.append("-");
        }
        else {
            for (int i = 0; i < hongLength03; i++) {
                if (hongqiu03.get(i).isChoosed()) {
                    betBallText.append(i);
                    betBallText.append(",");
                }
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    protected boolean checkInput() {
        String inf = null;
        if (lotteryType == 1) {
            if (hongqiuInf01.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf02.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "十位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
        }
        else if (lotteryType == 2) {
            if (hongqiuInf01.getCount() < 2)
                inf = SD_TIPS02;
        }
        else if (lotteryType == 3) {
            if (hongqiuInf01.getCount() < 3)
                inf = SD_TIPS03;
        }
        else if (lotteryType == 4) {
            if (hongqiuInf01.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "百位请至少输入1个号码";
            else if (hongqiuInf03.getCount() < SDActivity.SD_HONGQIU_MIN)
                inf = "个位请至少输入1个号码";
            if (hongqiuInf01.getCount() == 1 && hongqiuInf03.getCount() == 1) {
                int p = 0, q = 0;
                int hongLength01 = hongqiu01.size();
                for (int i = 0; i < hongLength01; i++) {
                    if (hongqiu01.get(i).isChoosed())
                        p = i;
                }
                int hongLength03 = hongqiu03.size();
                for (int i = 0; i < hongLength03; i++) {
                    if (hongqiu03.get(i).isChoosed())
                        q = i;
                }
                if (p == q)
                    inf = "组三单式不可以选择相同号码";
            }
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
        String betInf = null;
        if (lotteryType == 2)
            betInf = "2注     <font color='red'>4元</font>";
        else
            betInf = "1注     <font color='red'>2元</font>";
        moneyInf.setText(Html.fromHtml(betInf));
        if (lotteryType == 1) {
            Random rd = new Random();
            int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu02.get(num02).setChoosed(true);
            redBallsLayout02.chooseBall(num02);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
        }
        else if (lotteryType == 2) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(2, SDActivity.SD_HONGQIU_LENGTH);
            for (int i = 0; i < 2; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 3) {
            int[] randomRedNum01 = MathUtil.getRandomNumNotEquals(3, SDActivity.SD_HONGQIU_LENGTH);
            for (int i = 0; i < 3; i++) {
                hongqiu01.get(randomRedNum01[i]).setChoosed(true);
                redBallsLayout01.chooseBall(randomRedNum01[i]);
            }
        }
        else if (lotteryType == 4) {
            Random rd = new Random();
            int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            while (num01 == num03)
                num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            hongqiu01.get(num01).setChoosed(true);
            redBallsLayout01.chooseBall(num01);
            hongqiu03.get(num03).setChoosed(true);
            redBallsLayout03.chooseBall(num03);
        }
    }

    @Override
    public void randomBallsShow() {
        super.randomBallsShow();
        invalidateAll();
        if (lotteryType == 2)
            betMoney = 4;
        else
            betMoney = 2;
        luckynum = orgCode;
    }

    @Override
    public void clearBalls() {
        if (lotteryType == 1) {
            redBallsLayout01.resetBalls();
            redBallsLayout02.resetBalls();
            redBallsLayout03.resetBalls();
        }
        else if (lotteryType == 2 || lotteryType == 3)
            redBallsLayout01.resetBalls();
        else if (lotteryType == 4) {
            redBallsLayout01.resetBalls();
            redBallsLayout03.resetBalls();
        }
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        if (lotteryType == 1)
            selectInfo.setText(SD_TIPS01);
        else if (lotteryType == 2)
            selectInfo.setText(SD_TIPS02);
        else if (lotteryType == 3)
            selectInfo.setText(SD_TIPS03);
        else if (lotteryType == 4)
            selectInfo.setText(SD_TIPS04);
        showBallNum();
    }

    @Override
    public void onBallClickFull(int ballType) {
        if (ballType == 1) {
            ViewUtil.showTipsToast(this, "您只能选" + SDActivity.SD_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 2) {
            ViewUtil.showTipsToast(this, "您只能选" + SDActivity.SD_HONGQIU_LIMIT + "个红球");
        }
        else if (ballType == 3) {
            ViewUtil.showTipsToast(this, "您只能选" + SDActivity.SD_HONGQIU_LIMIT + "个红球");
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
        if ((hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) == false) {
            enableClearBtn();
        }
        if (lotteryType == 1) {
            if (hongNumber01 == 0 && hongNumber02 == 0 && hongNumber03 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber02 == 0 || hongNumber03 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    invalidateAll();
                    betNumber = hongNumber01 * hongNumber02 * hongNumber03;
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        else if (lotteryType == 2) {
            if (hongNumber01 >= 2) {
                refreshMoney = true;
                invalidateAll();
                betNumber = MathUtil.factorial(hongNumber01, 2);
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else
                resetInf();
        }
        else if (lotteryType == 3) {
            if (hongNumber01 >= 3) {
                refreshMoney = true;
                invalidateAll();
                betNumber = (int) (MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3));
                betMoney = betNumber * 2 * 1;
            }
            else if (hongNumber01 > 0)
                invalidateNum();
            else {
                resetInf();
            }
        }
        else if (lotteryType == 4) {
            if (hongNumber01 == 0 && hongNumber03 == 0) {
                resetInf();
            }
            else {
                if (hongNumber01 == 0 || hongNumber03 == 0) {
                    invalidateNum();
                }
                else {
                    refreshMoney = true;
                    invalidateAll();
                    if ((hongNumber03 + hongNumber01 == 2) && hongNumber01 != 0 && hongNumber03 != 0) {
                        betNumber = 1;
                    }
                    else {
                        int sameCount = findSameNum();
                        betNumber = hongNumber03 * hongNumber01 - sameCount;
                    }
                    betMoney = betNumber * 2 * 1;
                }
            }
        }
        if (refreshMoney) {
            String betInf = getBetInf(betNumber, betMoney);
            if (betInf != null)
                moneyInf.setText(Html.fromHtml(betInf));
        }
        checkBet(betMoney);
    }

    private int findSameNum() {
        int n = 0;
        for (int i = 0; i < SDActivity.SD_HONGQIU_LENGTH; i++) {
            Boolean first = hongqiu01.get(i).isChoosed();
            Boolean second = hongqiu03.get(i).isChoosed();
            if (first && second)
                n = n + 1;
        }
        return n;
    }

    @Override
    protected void showBallNum() {
        if (lotteryType == 1) {
// choosingCountHundred.setText("百位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountTen.setText("十位：" + hongqiuInf02.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf03.getCount() + "/1个");
            flagHongqiu01.setText("百位");
            flagHongqiu02.setText("十位");
            flagHongqiu03.setText("个位");
        }
        else if (lotteryType == 2)
// choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/2个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 3)
// choosingCountHundred.setText("红球：" + hongqiuInf01.getCount() + "/3个");
            flagHongqiu01.setText("红球");
        else if (lotteryType == 4) {
// choosingCountHundred.setText("百位：" + hongqiuInf01.getCount() + "/1个");
// choosingCountUnit.setText("个位：" + hongqiuInf03.getCount() + "/1个");
            flagHongqiu01.setText("百位");
            flagHongqiu03.setText("个位");
        }
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
// choosingInf.setText(Html.fromHtml(displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
        analyseTips.setVisibility(View.VISIBLE);
        code = getBallsBetInf();
        invalidateDisplay();
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "排列三游戏规则");
        bundel.putString("lottery_help", "help_new/pl3.html");
        intent.putExtras(bundel);
        intent.setClass(PLSActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "排列三走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=pls&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(PLSActivity.this, LotteryWinningRules.class);
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
                String betInf = "1注     <font color='red'>2元</font>";
                moneyInf.setText(Html.fromHtml(betInf));
                // for (int i = 0; i < 7; i++) { if (lotteryType == 1)
                if (lotteryType == 1) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    hongqiu02.get(vertor[1]).setChoosed(true);
                    redBallsLayout02.chooseBall(vertor[1]);
                    hongqiu03.get(vertor[2]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[2]);
                }
                else if (lotteryType == 2) {
                    for (int i = 0; i < 2; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 3) {
                    for (int i = 0; i < 3; i++) {
                        hongqiu01.get(vertor[i]).setChoosed(true);
                        redBallsLayout01.chooseBall(vertor[i]);
                    }
                }
                else if (lotteryType == 4) {
                    hongqiu01.get(vertor[0]).setChoosed(true);
                    redBallsLayout01.chooseBall(vertor[0]);
                    hongqiu03.get(vertor[1]).setChoosed(true);
                    redBallsLayout03.chooseBall(vertor[1]);
                }
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

// 刷新是否显示冷热门号码
    @Override
    protected void refreshHotNumShow() {
        redBallsLayout01.refreshAllBallInf(showHotNum);
        redBallsLayout02.refreshAllBallInf(showHotNum);
        redBallsLayout03.refreshAllBallInf(showHotNum);
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
                            analyseHundred[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseHundred[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseHundred[i] = "出" + count + "次";
                        hongqiu01.get(i).setBallsInf(analyseHundred[i]);
                    }
                    for (int i = 0; i < tenLength; i++) {
                        count = Integer.valueOf(analyseTen[i]);
                        if (count < 5)
                            analyseTen[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseTen[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseTen[i] = "出" + count + "次";
                        hongqiu02.get(i).setBallsInf(analyseTen[i]);
                    }
                    for (int i = 0; i < unitLength; i++) {
                        count = Integer.valueOf(analyseUnit[i]);
                        if (count < 5)
                            analyseUnit[i] = "出<font color='blue'>" + count + "</font>次";
                        else if (count > 10)
                            analyseUnit[i] = "出<font color='red'>" + count + "</font>次";
                        else
                            analyseUnit[i] = "出" + count + "次";
                        hongqiu03.get(i).setBallsInf(analyseUnit[i]);
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
    public String getQ_code() {
        sdNum01 = 0;
        sdNum02 = 0;
        sdNum03 = 0;
        StringBuilder ballText = new StringBuilder();
        for (int i = 0; i < hongqiu01.size(); i++) {
            if (hongqiu01.get(i).isChoosed()) {
                ballText.append(i);
                sdNum01 = sdNum01 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu02.size(); i++) {
            if (hongqiu02.get(i).isChoosed()) {
                ballText.append(i);
                sdNum02 = sdNum02 + 1;
            }
        }
        ballText.append(",");
        for (int i = 0; i < hongqiu03.size(); i++) {
            if (hongqiu03.get(i).isChoosed()) {
                ballText.append(i);
                sdNum03 = sdNum03 + 1;
            }
        }
        if (ballText.toString() == "") {
            return null;
        }
        else {
            return ballText.toString();
        }
    }

    private int sdNum01 = 0;
    private int sdNum02 = 0;
    private int sdNum03 = 0;

    @Override
    protected void searchLuckyNum() {
        if (getQ_code().equals(",,")) {
            ViewUtil.showTipsToast(this, "请输入一个号码");
        }
        else {
            if (sdNum01 > 1) {
                ViewUtil.showTipsToast(this, "百位至多允许输入1个红球");
            }
            else if (sdNum02 > 1) {
                ViewUtil.showTipsToast(this, "十位至多允许输入1个红球");
            }
            else if (sdNum03 > 1) {
                ViewUtil.showTipsToast(this, "个位至多允许输入1个红球");
            }
            else {
                requestCode = q_codeSwitch(getQ_code());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("no", 20);
                bundle.putString("kind", "pls");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(PLSActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                              int kind, String tabName) {
        popMneu.dismiss();
        databaseData.putString("pls_way", LotteryUtils.plsWay[kind]);
        databaseData.commit();
        clearBalls();
        lotteryType = kind + 1;
        index_num = kind;
        title.setText(LotteryUtils.textArrayPLS[kind]);
        showWay();
        showBallNum();
    }
}
