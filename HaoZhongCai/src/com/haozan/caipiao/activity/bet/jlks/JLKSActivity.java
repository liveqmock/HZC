package com.haozan.caipiao.activity.bet.jlks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryDiviningActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.OpenHistory;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.adapter.JLKSGridViewAdapter;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.types.JLKSItem;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.SelecteTypeK3PopupWindow;
import com.haozan.caipiao.widget.SelecteTypeK3PopupWindow.PopupSelectTypeClickListener;

/**
 * 吉林快三
 * 
 * @author Vincent
 * @create-time 2013-6-26 下午2:03:52
 */
public class JLKSActivity
    extends BetDigitalBasic
    implements OnClickListener, OnItemClickListener, PopupSelectTypeClickListener {
    protected static final String MONEY_TIPS = "0注 <font color='#d7c10f'>0元</font>";

    private static final int START_RANDOM = 8;
    private static final int STOP_RANDOM = 9;
    private static final int SHAIZI_MOVE = 10;
    private static final int SHOW_SHAIZI = 11;
    private static final int ZOOM_IN_SHAIZI = 12;

    private static final int ONE_SHAIZI = 1;
    private static final int TWO_SHAIZI = 2;
    private static final int THREE_SHAIZI = 3;

    private static final int RAMDOM_MAX_NUM = 6;
// private static final int HZ = 1;
// private static final int STHTX = 2;
// private static final int STHDX = 3;
// private static final int ETHDX = 4;
// private static final int ETHFX = 5;
// private static final int SBTH = 6;
// private static final int SLH = 7;
// private static final int EBTH = 8;
    private static final int HZ = 1;
    private static final int STH = 2;
    private static final int ETH = 3;
    private static final int SBTH = 4;
    private static final int EBTH = 5;
    private static final String[] hzList1 = {"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
            "15", "16", "17", "18"};
    private static final String[] hzList2 = {"中240元", "中80元", "中40元", "中25元", "中16元", "中12元", "中10元", "中9元",
            "中9元", "中10元", "中12元", "中16元", "中25元", "中40元", "中80元", "中240元"};
    private static final int[] hzAward = {240, 80, 40, 25, 16, 12, 10, 9, 9, 10, 12, 16, 25, 40, 80, 240};
    private static final String[] sthList = {"111", "222", "333", "444", "555", "666"};
    private static final String[] eth_dx_thList = {"11", "22", "33", "44", "55", "66"};
    private static final String[] eth_dx_bthList = {"1", "2", "3", "4", "5", "6"};
    private static final String[] eth_fx_List = {"11*", "22*", "33*", "44*", "55*", "66*"};

    public static final String[] textArrayJLKS = {"和值", "三同号", "二同号", "三不同号", "二不同号"};
    public static final String[] moneyArrayJLKS = {"奖金9-240元", "奖金40-240元", "奖金15-80元", "奖金10-40元", "奖金8元"};
    public static final String[] JLKSWay = {"jlks_hz", "jlks_sth", "jlks_eth", "jlks_sbth", "jlks_ebth"};

    private static final int[] DRAWABLE_SHAIZI = {R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};

    private ArrayList<JLKSItem> array1;
    private ArrayList<JLKSItem> array2;
    private ArrayList<JLKSItem> array3;
    private JLKSGridViewAdapter adapter1;
    private JLKSGridViewAdapter adapter2;
    private JLKSGridViewAdapter adapter3;
    private GridView gridView1;
    private GridView gridView2;
    private GridView gridView3;
    private LinearLayout sub1;
    private LinearLayout sub2;
    private LinearLayout sub3;
    private RelativeLayout subTitleRe1;
    private RelativeLayout subTitleRe2;
    private RelativeLayout subTitleRe3;
    private RelativeLayout subButton;
    private boolean ifSubButtonPressed = false;
    private TextView subButtonTv1;
    private TextView subButtonTv2;
    private TextView subTitle1;
    private TextView subTitle2;
    private TextView subTitle3;

    private int lotteryType = 1;

    private RelativeLayout termLayout;
    private TextView lotteryIntroduce;
    private boolean ifLotteryIntroduceShown = false;

    private SelecteTypeK3PopupWindow popupWindowWaySelect;
    private BetShowLotteryWay[] wayDataArray;

    // lotteryType 与玩法对照
    // 1.和值 2.三同号 3.二同号 4.三不同号 5.二不同号

    private RelativeLayout layoutContainer;
    private ImageView[] ivShaizi;
    private int[] selectedIndex;
    private AnimationDrawable[] anim;
    private int shaiziWay = THREE_SHAIZI;// 1代表1个筛子，以此类推，最多3个

    private int shaiziWidth;
    private int shaiziHeight;

    private int soundRotate;
    private int soundSelected;

    private int[] tempDice;

    @Override
    public void setKind() {
        this.kind = "jlk3";
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                vibratorWhenClick();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBasic();
        setContentView(R.layout.jlks);
        initData();
        setupViews();
        initSubViews();
        init();
    }

    private void initData() {
        initShaiziAnamation();

        array1 = new ArrayList<JLKSItem>();
        array2 = new ArrayList<JLKSItem>();
        array3 = new ArrayList<JLKSItem>();
        adapter1 = new JLKSGridViewAdapter(this, array1);
        adapter2 = new JLKSGridViewAdapter(this, array2);
        adapter3 = new JLKSGridViewAdapter(this, array3);

        tempDice = new int[3];

        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();
        lotteryway.setUpsInf(textArrayJLKS);
        lotteryway.setDownsInf(moneyArrayJLKS);
        wayDataArray[0] = lotteryway;
    }

    private void initShaiziAnamation() {
        ivShaizi = new ImageView[3];
        selectedIndex = new int[3];
        anim = new AnimationDrawable[3];
    }

    protected void setupViews() {
        super.setupViews();
        layoutContainer = (RelativeLayout) this.findViewById(R.id.container_layout);

        scrollView.setVisibility(View.GONE);
        gridView1 = (GridView) findViewById(R.id.grid1);
        gridView1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView1.setAdapter(adapter1);
        gridView1.setOnItemClickListener(this);
        gridView2 = (GridView) findViewById(R.id.grid2);
        gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView2.setAdapter(adapter2);
        gridView2.setOnItemClickListener(this);
        gridView3 = (GridView) findViewById(R.id.grid3);
        gridView3.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView3.setAdapter(adapter3);
        gridView3.setOnItemClickListener(this);
        sub1 = (LinearLayout) findViewById(R.id.sub1);
        sub2 = (LinearLayout) findViewById(R.id.sub2);
        sub3 = (LinearLayout) findViewById(R.id.sub3);
        subTitleRe1 = (RelativeLayout) findViewById(R.id.sub_title1);
        subTitleRe2 = (RelativeLayout) findViewById(R.id.sub_title2);
        subTitleRe3 = (RelativeLayout) findViewById(R.id.sub_title3);
        subTitle1 = (TextView) findViewById(R.id.tv_sub1);
        subTitle2 = (TextView) findViewById(R.id.tv_sub2);
        subTitle3 = (TextView) findViewById(R.id.tv_sub3);
        subButton = (RelativeLayout) findViewById(R.id.sub2_re);
        subButton.setOnClickListener(this);
        subButtonTv1 = (TextView) findViewById(R.id.sub2_tv1);
        subButtonTv2 = (TextView) findViewById(R.id.sub2_tv2);

        // 隐藏分析工具
// findViewById(R.id.analyse_tips_rala).setVisibility(View.GONE);
        normalToolsLayout.setVisibility(View.GONE);
        numAnalyse.setVisibility(View.GONE);

        img_help_info_bg = (ImageView) findViewById(R.id.img_help_info_bg);
        img_help_info_bg.setOnClickListener(this);
        lotteryIntroduce = (TextView) this.findViewById(R.id.lottery_introdution);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
    }

    protected void showBetWayPopupViews() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View waySwitchLayout = null;
        waySwitchLayout = mLayoutInflater.inflate(R.layout.popup_bet_way_swtich, null);
        waySwitchLayout.findViewById(R.id.popup_bet_way_layout).setBackgroundResource(R.drawable.bg_k3_show_bet_way);

        TextView betDirectly = (TextView) waySwitchLayout.findViewById(R.id.bet_directly);
        betDirectly.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (betWay != 1) {
                    betWay = 1;
                    databaseData.putInt("bet_way", betWay);
                    databaseData.commit();
                    betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
                }
                betWayPopupWindow.dismiss();
            }
        });
        TextView betAdd = (TextView) waySwitchLayout.findViewById(R.id.bet_add);
        betAdd.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (betWay != 2) {
                    betWay = 2;
                    databaseData.putInt("bet_way", betWay);
                    databaseData.commit();
                    betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
                }
                betWayPopupWindow.dismiss();
            }
        });

        betWayPopupWindow = new PopupWindow(this);
        betWayPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        betWayPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        betWayPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        betWayPopupWindow.setOutsideTouchable(true);
        betWayPopupWindow.setFocusable(true);
        betWayPopupWindow.setContentView(waySwitchLayout);
        betWayPopupWindow.setAnimationStyle(R.style.popup_ball);
        if (betWay == 1) {
            betDirectly.setBackgroundResource(R.drawable.bg_k3_tips);
            betDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            betAdd.setBackgroundResource(R.drawable.bet_k3_show_way_popup_item);
            betAdd.setTextColor(getResources().getColor(R.color.white));
        }
        else if (betWay == 2) {
            betAdd.setBackgroundResource(R.drawable.bg_k3_tips);
            betAdd.setTextColor(getResources().getColor(R.color.dark_purple));
            betDirectly.setBackgroundResource(R.drawable.bet_k3_show_way_popup_item);
            betDirectly.setTextColor(getResources().getColor(R.color.white));
        }
        betWayPopupWindow.showAsDropDown(betWayBt, 0, -4 * betWayBt.getHeight());

    }

    private void showPopupViews() {
        wayDataArray[0].setSelectedIndex(lotteryType - 1);

        popupWindowWaySelect = new SelecteTypeK3PopupWindow(JLKSActivity.this, wayDataArray);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);
        popupWindowWaySelect.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
            }
        });
        topArrow.setImageResource(R.drawable.arrow_up_white);

        showPopupCenter(popupWindowWaySelect);
    }

    protected void showPopupBalls(LinearLayout layout) {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    private void init() {
        initK3UI();
        initSounds();

        if (ifShowImgHelp) {
            img_help_info_bg.setVisibility(View.VISIBLE);
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
        }

        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initLotteryIntroduce();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            lotteryType = bundle.getInt("bet_way");
            boolean flag = false;
            for (int i = 0; i < JLKSWay.length; i++) {
                if (lotteryType == i + 1) {
                    databaseData.putString("jlks_way", JLKSWay[i]);
                    databaseData.commit();
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                resetLotteryType();
            }
        }
        else {
            resetLotteryType();
        }
        initArrayData();
        showWay();
        initInf();

        measureShaiziView();
    }

    private void initK3UI() {
        RelativeLayout layoutTop = (RelativeLayout) this.findViewById(R.id.top_bg_linear);
        RelativeLayout layoutBottom = (RelativeLayout) this.findViewById(R.id.bottom);
        layoutTop.setBackgroundResource(R.drawable.bg_k3_bet_beam);
        layoutBottom.setBackgroundResource(R.drawable.bg_k3_bet_beam);

        clear.setBackgroundResource(R.drawable.btn_k3_custom_button);
        clear.setImageDrawable(getResources().getDrawable(R.drawable.icon_k3_img_clear_normal));

        moneyInf.setTextColor(getResources().getColor(R.color.white_gray));

        random.setBackgroundResource(R.drawable.btn_k3_custom_button);
        random.setTextColor(getResources().getColor(R.color.k3_custom_button));
        betBt.setBackgroundResource(R.drawable.btn_k3_custom_button);
        betBt.setTextColor(getResources().getColor(R.color.k3_custom_button));
// betBt.setBackgroundResource(R.drawable.btn_k3_bet);
        betWayBt.setBackgroundResource(R.drawable.btn_k3_bet);
        betWayBt.setTextColor(getResources().getColor(R.color.k3_bet_custom_button));
        imgShowBet.setBackgroundResource(R.drawable.btn_k3_bet_way);

        layoutTerm.setBackgroundColor(Color.TRANSPARENT);
        betTerm.setBackgroundResource(R.drawable.bg_k3_time);
        betTerm.setTextColor(getResources().getColor(R.color.white_gray));
        betTimeInf.setTextColor(getResources().getColor(R.color.white_gray));
        countDownTime.setBackgroundResource(R.drawable.bg_k3_time);
        countDownTime.setTextColor(getResources().getColor(R.color.white_gray));

        lotteryIntroduce.setBackgroundResource(R.drawable.bg_k3_tips);

        helpLin.setBackgroundResource(R.drawable.btn_k3_title);
        btnShake.setBackgroundResource(R.drawable.btn_k3_title);
        analyseTips.setBackgroundResource(R.drawable.btn_k3_title);
        ((ImageView) findViewById(R.id.vertical_line1)).setImageDrawable(getResources().getDrawable(R.drawable.vertical_line_k3));
        ((ImageView) findViewById(R.id.vertical_line2)).setImageDrawable(getResources().getDrawable(R.drawable.vertical_line_k3));
        ((ImageView) findViewById(R.id.vertical_line3)).setImageDrawable(getResources().getDrawable(R.drawable.vertical_line_k3));
        termLayout.setBackgroundResource(R.drawable.btn_k3_title);
    }

    private void initSounds() {
        soundRotate = notify.load(this, R.raw.rotate, 0);
        soundSelected = notify.load(this, R.raw.chip_location, 0);
    }

    private void measureShaiziView() {
        ImageView ivShaizi = new ImageView(this);
        ivShaizi.setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[0]]);

        ivShaizi.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                         MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        shaiziWidth = ivShaizi.getMeasuredWidth();
        shaiziHeight = ivShaizi.getMeasuredHeight();
    }

    /**
     * 初始化不同玩法按钮中显示的内容 无初始化选择
     */
    private void initArrayData() {
        ifSubButtonPressed = false;
        if (lotteryType == HZ) {
            array1.clear();
            gridView1.setNumColumns(4);
            for (int i = 0; i < hzList1.length; i++) {
                JLKSItem item = new JLKSItem();
                item.setUpStr(hzList1[i]);
                item.setDownStr(hzList2[i]);
                item.setIfSelected(false);
                array1.add(item);
            }
            adapter1.notifyDataSetChanged();
        }
        else if (lotteryType == STH) {
            array1.clear();
            gridView1.setNumColumns(3);
            for (int i = 0; i < sthList.length; i++) {
                JLKSItem item = new JLKSItem();
                item.setUpStr(sthList[i]);
                item.setDownStr("中240元");
                item.setIfSelected(false);
                array1.add(item);
            }
            adapter1.notifyDataSetChanged();
        }
        else if (lotteryType == ETH) {
            array1.clear();
            array2.clear();
            array3.clear();
            gridView1.setNumColumns(6);
            gridView2.setNumColumns(6);
            for (int i = 0; i < eth_dx_thList.length; i++) {
                JLKSItem item1 = new JLKSItem();
                item1.setUpStr(eth_dx_thList[i]);
                item1.setDownStr("");
                item1.setIfSelected(false);
                array1.add(item1);
                JLKSItem item2 = new JLKSItem();
                item2.setUpStr(eth_dx_bthList[i]);
                item2.setDownStr("");
                item2.setIfSelected(false);
                array2.add(item2);
                JLKSItem item3 = new JLKSItem();
                item3.setUpStr(eth_fx_List[i]);
                item3.setDownStr("");
                item3.setIfSelected(false);
                array3.add(item3);
            }
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            adapter3.notifyDataSetChanged();
        }
        else if (lotteryType == SBTH || lotteryType == EBTH) {
            array1.clear();
            if (lotteryType == SBTH) {
                gridView1.setNumColumns(6);
            }
            else {
                gridView1.setNumColumns(3);
            }
            for (int i = 0; i < eth_dx_bthList.length; i++) {
                JLKSItem item = new JLKSItem();
                item.setUpStr(eth_dx_bthList[i]);
                item.setDownStr("");
                item.setIfSelected(false);
                array1.add(item);
            }
            adapter1.notifyDataSetChanged();
        }
    }

    private void clearSubButton() {
        if (lotteryType == STH || lotteryType == SBTH) {
            ifSubButtonPressed = false;
            subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice);
            subButtonTv1.setTextColor(getResources().getColor(R.color.white));
            subButtonTv2.setTextColor(getResources().getColor(R.color.white_gray));
        }

    }

    private void resetLotteryType() {
        String sdWay = preferences.getString("jlks_way", "jlks_hz");
        for (int i = 0; i < JLKSWay.length; i++) {
            if (sdWay.equals(JLKSWay[i])) {
                lotteryType = i + 1;
                break;
            }
        }
    }

    private void showWay() {
        initLotteryIntroduce();
        title.setText(textArrayJLKS[lotteryType - 1]);
        if (lotteryType == HZ) {
            subButton.setVisibility(View.GONE);
// gridView2.setVisibility(View.VISIBLE);
            gridView2.setVisibility(View.GONE);
// sub2.setVisibility(View.VISIBLE);
            sub2.setVisibility(View.GONE);
            sub3.setVisibility(View.GONE);
            subTitle1.setText("猜开奖号码相加的和，奖金9-240元");
        }
        else if (lotteryType == STH) {
            subButton.setVisibility(View.VISIBLE);
            gridView2.setVisibility(View.GONE);
            sub2.setVisibility(View.VISIBLE);
            sub3.setVisibility(View.GONE);
            subTitleRe2.setVisibility(View.GONE);
            subTitle1.setText("猜豹子号(三个号相同)，奖金40-240元");
            subButtonTv1.setText("三同号通选");
            subButtonTv2.setVisibility(View.VISIBLE);
            subButtonTv2.setText("任意一个豹子开出即中40元");
            subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice);
            subButtonTv1.setTextColor(getResources().getColor(R.color.white));
            subButtonTv2.setTextColor(getResources().getColor(R.color.white_gray));
        }
        else if (lotteryType == ETH) {
            subButton.setVisibility(View.GONE);
            gridView2.setVisibility(View.VISIBLE);
            sub2.setVisibility(View.VISIBLE);
            sub3.setVisibility(View.VISIBLE);
            subTitleRe2.setVisibility(View.VISIBLE);
            subTitle1.setText("选择同号和不同的号的组合,奖金80元");
            subTitle2.setVisibility(View.GONE);
// subTitle2.setText("不同号");
            subTitle3.setText("复选:猜开奖号码中2个指定的相同号码,奖金15元");
        }
        else if (lotteryType == SBTH) {
            subButton.setVisibility(View.VISIBLE);
            gridView2.setVisibility(View.GONE);
            sub2.setVisibility(View.VISIBLE);
            sub3.setVisibility(View.GONE);
            subTitleRe2.setVisibility(View.VISIBLE);
            subTitle1.setText("三不同号:猜开奖的三个不同号码,奖金40元");
            subTitle2.setText("三连号:123,234,345,456任意开出即中10元");
            subButtonTv1.setText("三连号通选");
            subButtonTv2.setVisibility(View.GONE);
            subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice);
            subButtonTv1.setTextColor(getResources().getColor(R.color.white));
            subButtonTv2.setTextColor(getResources().getColor(R.color.white_gray));
        }
        else if (lotteryType == EBTH) {
            sub2.setVisibility(View.GONE);
            sub3.setVisibility(View.GONE);
            subTitle1.setText("猜开奖号码中2个指定的不同号码,奖金8元");
        }
    }

    private void initHZData() {
// gridView1.setco
    }

    protected void defaultNum(String betNum) {
        initArrayData();
        String[] lotteryMode = betNum.split("\\:");
        if (lotteryType == HZ) {
            String[] nums = lotteryMode[0].split(",");
            for (int i = 0; i < nums.length; i++) {
                int num = Integer.parseInt(nums[i]);
                array1.get(num - 3).setIfSelected(true);
            }
            adapter1.notifyDataSetChanged();
        }
        else if (lotteryType == STH) {
            String[] nums = lotteryMode[0].split(",");
            if (nums[0].equals("777")) {
                ifSubButtonPressed = true;
                subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                subButtonTv1.setTextColor(getResources().getColor(R.color.dark_purple));
                subButtonTv2.setTextColor(getResources().getColor(R.color.dark_purple));
            }
            else {
                for (int i = 0; i < nums.length; i++) {
                    for (int j = 0; j < sthList.length; j++) {
                        if (nums[i].equals(sthList[j])) {
                            array1.get(j).setIfSelected(true);
                        }
                    }
                }
                adapter1.notifyDataSetChanged();
            }
        }
        else if (lotteryType == ETH) {
            if (betNum.indexOf("|") < 0) {
                String[] nums = lotteryMode[0].split(",");
                for (int i = 0; i < nums.length; i++) {
                    for (int j = 0; j < eth_dx_thList.length; j++) {
                        if (nums[i].equals(eth_dx_thList[j])) {
                            array3.get(j).setIfSelected(true);
                        }
                    }
                }
                adapter3.notifyDataSetChanged();
            }
            else {
                String[] nums = lotteryMode[0].split("\\|");
                String[] thNums = nums[0].split(",");
                String[] bthNums = nums[1].split(",");
                for (int i = 0; i < thNums.length; i++) {
                    for (int j = 0; j < eth_dx_thList.length; j++) {
                        if (thNums[i].equals(eth_dx_thList[j])) {
                            array1.get(j).setIfSelected(true);
                        }
                    }
                }
                for (int i = 0; i < bthNums.length; i++) {
                    for (int j = 0; j < eth_dx_bthList.length; j++) {
                        if (bthNums[i].equals(eth_dx_bthList[j])) {
                            array2.get(j).setIfSelected(true);
                        }
                    }
                }
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        }
        else if (lotteryType == SBTH) {
            String[] nums = lotteryMode[0].split(",");
            if (nums[0].equals("789")) {
                ifSubButtonPressed = true;
                subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                subButtonTv1.setTextColor(getResources().getColor(R.color.dark_purple));
                subButtonTv2.setTextColor(getResources().getColor(R.color.dark_purple));
            }
            else {
                for (int i = 0; i < nums.length; i++) {
                    int num = Integer.parseInt(nums[i]);
                    array1.get(num - 1).setIfSelected(true);
                }
                adapter1.notifyDataSetChanged();
            }
        }
        else if (lotteryType == EBTH) {
            String[] nums = lotteryMode[0].split(",");
            for (int i = 0; i < nums.length; i++) {
                int num = Integer.parseInt(nums[i]);
                array1.get(num - 1).setIfSelected(true);
            }
            adapter1.notifyDataSetChanged();
        }
        ballClick(null, -1);
        checkBet(betMoney);
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
        super.disableBetBtn();
        if (ifLotteryIntroduceShown == true) {
            disappearAnimation(lotteryIntroduce);
            ifLotteryIntroduceShown = false;
        }
    }

    private String getBallsBetInf() {
        if (lotteryType == HZ)
            return getBallsBetHZInf();
        else if (lotteryType == STH)
            return getBallsBetSTHInf();
        else if (lotteryType == ETH)
            return getBallsBetETHInf();
        else if (lotteryType == SBTH)
            return getBallsBetSBTHInf();
        else if (lotteryType == EBTH)
            return getBallsBetEBTHInf();
        else
            return null;
    }

    // 投注格式
    // 投注号码:单注金额:方式玩法:投注倍数
    private String getBallsBetHZInf() {
        StringBuilder betBallText = new StringBuilder();
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(hzList1[i]);
                betBallText.append(",");
                num++;
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (num > 1) {
            betBallText.append("102:");
        }
        else {
            betBallText.append("101:");
        }
        return betBallText.toString();
    }

    private String getBallsBetSTHInf() {
        StringBuilder betBallText = new StringBuilder();
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(sthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        if (num > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + (ifSubButtonPressed ? (betMoney - 2) : betMoney) + ":");
        if (num > 1) {
            betBallText.append("105:");
        }
        else {
            betBallText.append("104:");
        }

        // 三同号通选
        StringBuilder sthBetText = new StringBuilder();
        if (ifSubButtonPressed) {
            if (num > 0) {
                sthBetText.append(";");
            }
            else {
                betBallText.delete(0, betBallText.length());
            }
            sthBetText.append("777");
            sthBetText.append(":2:103:");
            betBallText.append(sthBetText);
        }
        return betBallText.toString();
    }

    private String getBallsBetETHInf() {
        // 二同号单选
        StringBuilder betBallText1 = new StringBuilder();
        int num1 = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText1.append(eth_dx_thList[i]);
                betBallText1.append(",");
                num1++;
            }
        }
        if (num1 > 0) {
            betBallText1.deleteCharAt(betBallText1.length() - 1);
            betBallText1.append("|");
        }
        else {
            betBallText1.delete(0, betBallText1.length());
        }

        int num2 = 0;
        for (int i = 0; i < array2.size(); i++) {
            if (array2.get(i).isIfSelected()) {
                betBallText1.append(eth_dx_bthList[i]);
                betBallText1.append(",");
                num2++;
            }
        }
        if (num2 > 0) {
            betBallText1.deleteCharAt(betBallText1.length() - 1);
        }

        // 二同号复选
        int num3 = 0;
        StringBuilder betBallText2 = new StringBuilder();
        if (num1 > 0 && num2 > 0) {
            betBallText2.append(";");
        }
        else {
            betBallText1.delete(0, betBallText1.length());
        }
        for (int i = 0; i < array3.size(); i++) {
            if (array3.get(i).isIfSelected()) {
                betBallText2.append(eth_dx_thList[i]);
                betBallText2.append(",");
                num3++;
            }
        }
        if (num1 > 0 && num2 > 0) {
            // 二同号单选
            orgCode = betBallText1.toString();
            betBallText1.append(":" + (betMoney - num3 * 2) + ":");
            if (num1 > 1 || num2 > 1) {
                betBallText1.append("109:");
            }
            else {
                betBallText1.append("108:");
            }
        }
        // 二同号复选
        if (num3 > 0) {
            betBallText2.deleteCharAt(betBallText2.length() - 1);
            betBallText2.append(":" + (num3 * 2) + ":");
            if (num3 > 1) {
                betBallText2.append("107:");
            }
            else {
                betBallText2.append("106:");
            }
            betBallText1.append(betBallText2);
        }
        return betBallText1.toString();
    }

    private String getBallsBetSBTHInf() {
        StringBuilder betBallText = new StringBuilder();
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(eth_dx_bthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        if (num > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        orgCode = betBallText.toString();
        betBallText.append(":" + (ifSubButtonPressed ? (betMoney - 2) : betMoney) + ":");
        if (num > 3) {
            betBallText.append("111:");
        }
        else {
            betBallText.append("110:");
        }

        // 三同号通选
        if (ifSubButtonPressed) {
            StringBuilder betBallText2 = new StringBuilder();
            if (num > 2) {
                betBallText2.append(";");
            }
            else {
                betBallText.delete(0, betBallText.length());
            }

            betBallText2.append("789");
            betBallText2.append(":2:116:");
            betBallText.append(betBallText2);
        }
        return betBallText.toString();
    }

    private String getBallsBetEBTHInf() {
        StringBuilder betBallText = new StringBuilder();
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(eth_dx_bthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        orgCode = betBallText.toString();
        betBallText.append(":" + betMoney + ":");
        if (num > 2) {
            betBallText.append("114:");
        }
        else {
            betBallText.append("113:");
        }
        return betBallText.toString();
    }

    private String getBallsDisplayInf() {
        if (lotteryType == HZ)
            return getBallsDisplayHZInf();
        else if (lotteryType == STH)
            return getBallsDisplaySTHInf();
        else if (lotteryType == ETH)
            return getBallsDisplayETHInf();
        else if (lotteryType == SBTH)
            return getBallsDisplaySBTHInf();
        else if (lotteryType == EBTH)
            return getBallsDisplayEBTHInf();
        else
            return null;
    }

    private String getBallsDisplayHZInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[和值] ");
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(hzList1[i]);
                betBallText.append(",");
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySTHInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三同号] ");
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(sthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        if (num > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("</font>");
        }

        // 三同号通选
        StringBuilder sthTxBetText = new StringBuilder();
        if (ifSubButtonPressed) {
            if (num > 0) {
                sthTxBetText.append(";");
            }
            else {
                betBallText.delete(0, betBallText.length());
            }
            sthTxBetText.append("<font color='red'>");
            sthTxBetText.append("[三同号通选] 三同号通选");
            sthTxBetText.append("</font>");
            betBallText.append(sthTxBetText);
        }
        return betBallText.toString();
    }

    private String getBallsDisplayETHInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二同号] ");
        int num1 = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(eth_dx_thList[i]);
                betBallText.append(",");
                num1++;
            }
        }
        if (num1 > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("|");
        int num2 = 0;
        for (int i = 0; i < array2.size(); i++) {
            if (array2.get(i).isIfSelected()) {
                betBallText.append(eth_dx_bthList[i]);
                betBallText.append(",");
                num2++;
            }
        }
        if (num2 > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("</font>");
        }

        // 二同号复式
        StringBuilder betBallText2 = new StringBuilder();
        if (num1 > 0 && num2 > 0) {
            betBallText2.append(";");
        }
        else {
            betBallText.delete(0, betBallText.length());
        }
        betBallText2.append("<font color='red'>");
        betBallText2.append("[二同号] ");
        int num3 = 0;
        for (int i = 0; i < array3.size(); i++) {
            if (array3.get(i).isIfSelected()) {
                betBallText2.append(eth_fx_List[i]);
                betBallText2.append(",");
                num3++;
            }
        }
        if (num3 > 0) {
            betBallText2.deleteCharAt(betBallText2.length() - 1);
            betBallText2.append("</font>");
        }
        else {
            betBallText2.delete(0, betBallText.length());
        }
        if (num3 > 0) {
            betBallText.append(betBallText2);
        }
        return betBallText.toString();
    }

    private String getBallsDisplaySBTHInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三不同号] ");
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(eth_dx_bthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        if (num > 0) {
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("</font>");
        }

        // 三连号通选
        StringBuilder betBallText2 = new StringBuilder();
        if (ifSubButtonPressed) {
            if (num > 2) {
                betBallText2.append(";");
            }
            else {
                betBallText.delete(0, betBallText.length());
            }
            betBallText2.append("<font color='red'>");
            betBallText2.append("[三连号通选] 三连号通选");
            betBallText2.append("</font>");
            betBallText.append(betBallText2);
        }
        return betBallText.toString();
    }

    private String getBallsDisplayEBTHInf() {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二不同号] ");
        int num = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betBallText.append(eth_dx_bthList[i]);
                betBallText.append(",");
                num++;
            }
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    int first, third;

    protected boolean checkInput() {
        String inf = null;
// if (lotteryType == WXTX) {
// }
// else if (lotteryType == WXZX) {
// }
        if (inf != null) {
            ViewUtil.showTipsToast(this, inf);
            return false;
        }
        else
            return true;
    }

// 机选
    @Override
    public void randomBalls() {
        randomK3();
        showK3Dice();
    }

    private void randomK3() {
        Random rd = new Random();
        if (lotteryType == HZ) {
            tempDice = MathUtil.getRandomNum(3, RAMDOM_MAX_NUM);
        }
        else if (lotteryType == STH) {
            tempDice = new int[3];
            int randomNum = rd.nextInt(RAMDOM_MAX_NUM);
            tempDice[0] = randomNum;
            tempDice[1] = randomNum;
            tempDice[2] = randomNum;
        }
        else if (lotteryType == ETH) {
            int[] temp = MathUtil.getRandomNumNotEquals(2, RAMDOM_MAX_NUM);
            tempDice[0] = temp[0];
            tempDice[1] = temp[0];
            tempDice[2] = temp[1];
        }
        else if (lotteryType == SBTH) {
            tempDice = MathUtil.getRandomNumNotEquals(3, RAMDOM_MAX_NUM);
            Arrays.sort(tempDice);
        }
        else if (lotteryType == EBTH) {
            tempDice = MathUtil.getRandomNumNotEquals(2, RAMDOM_MAX_NUM);
            Arrays.sort(tempDice);
        }
    }

    private void showK3Dice() {
        clearBalls();
        String betInf = null;
        betInf = "1注     <font color='#d7c10f'>2元</font>";
        moneyInf.setText(Html.fromHtml(betInf));

        if (lotteryType == HZ) {
            int result = tempDice[0] + tempDice[1] + tempDice[2] + 3;
            array1.get(result - 3).setIfSelected(true);
            adapter1.notifyDataSetChanged();

            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + hzAward[result - 3] +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + (hzAward[result - 3] - 2) + "</font>元"));
        }
        else if (lotteryType == STH) {
            array1.get(tempDice[0]).setIfSelected(true);
            adapter1.notifyDataSetChanged();
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + 240 +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + 238 + "</font>元"));
        }
        else if (lotteryType == ETH) {
            array1.get(tempDice[0]).setIfSelected(true);
            array2.get(tempDice[2]).setIfSelected(true);
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + 80 +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + 78 + "</font>元"));
        }
        else if (lotteryType == SBTH) {
            for (int i = 0; i < tempDice.length; i++) {
                array1.get(tempDice[i]).setIfSelected(true);
            }
            adapter1.notifyDataSetChanged();
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + 40 +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + 38 + "</font>元"));
        }
        else if (lotteryType == EBTH) {
            for (int i = 0; i < tempDice.length; i++) {
                array1.get(tempDice[i]).setIfSelected(true);
            }
            adapter1.notifyDataSetChanged();
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + 8 +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + 6 + "</font>元"));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mHandler.sendEmptyMessage(0);
// TextView tv = (TextView) parent.getChildAt(position).findViewById(R.id.unite_grid_view_item_click);
        TextView tv1 = (TextView) view.findViewById(R.id.up_tv);
        TextView tv2 = (TextView) view.findViewById(R.id.down_tv);
        if (parent == gridView1) {
            if (array1.get(position).isIfSelected()) {
                array1.get(position).setIfSelected(false);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice);
                tv1.setTextColor(getResources().getColor(R.color.white));
                tv2.setTextColor(getResources().getColor(R.color.white_gray));
            }
            else {
                array1.get(position).setIfSelected(true);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                tv1.setTextColor(getResources().getColor(R.color.dark_purple));
                tv2.setTextColor(getResources().getColor(R.color.dark_purple));
            }
        }
        else if (parent == gridView2) {
            if (array2.get(position).isIfSelected()) {
                array2.get(position).setIfSelected(false);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice);
                tv1.setTextColor(getResources().getColor(R.color.white));
                tv2.setTextColor(getResources().getColor(R.color.white_gray));
            }
            else {
                array2.get(position).setIfSelected(true);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                tv1.setTextColor(getResources().getColor(R.color.dark_purple));
                tv2.setTextColor(getResources().getColor(R.color.dark_purple));
            }
        }
        else if (parent == gridView3) {
            if (array3.get(position).isIfSelected()) {
                array3.get(position).setIfSelected(false);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice);
                tv1.setTextColor(getResources().getColor(R.color.white));
                tv2.setTextColor(getResources().getColor(R.color.white_gray));
            }
            else {
                array3.get(position).setIfSelected(true);
                view.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                tv1.setTextColor(getResources().getColor(R.color.dark_purple));
                tv2.setTextColor(getResources().getColor(R.color.dark_purple));
            }
        }
        ballClick(parent, position);
        checkBet(betMoney);
    }

    public void vibratorWhenClick() {
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(50);
    }

    protected String getBetInf(long betNumber, long betMoney) {
        if (betNumber < 0 || betMoney < 0)
            return null;
        String betInf = null;
        betInf = betNumber + "注  <font color=\"#d7c10f\">" + betMoney + "元</font>";
        return betInf;
    }

    public void ballClick(AdapterView<?> parent, int position) {
        int maxMoney = 0;
        int minMoney = 0;
        betNumber = 0;
        ArrayList<Integer> tempMoney = new ArrayList<Integer>();
        if (lotteryType == HZ) {
            for (int i = 0; i < array1.size(); i++) {
                if (array1.get(i).isIfSelected()) {
                    betNumber++;
                    tempMoney.add(hzAward[i]);
                }
            }
            if (betNumber != 0) {
                enableClearBtn();
// refreshMoney = true;
                betMoney = betNumber * 2 * 1;
                String betInf = getBetInf(betNumber, betMoney);
                moneyInf.setText(Html.fromHtml(betInf));
                invalidateAll();
                if (betNumber == 1) {
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" +
                        tempMoney.get(0) + "</font>元,您将盈利<font color=\"#d7c10f\">" + (tempMoney.get(0) - 2) +
                        "</font>元"));
                }
                else {
                    minMoney = tempMoney.get(0);
                    maxMoney = tempMoney.get(0);
                    for (int i = 1; i < tempMoney.size(); i++) {
                        if (tempMoney.get(i) < minMoney) {
                            minMoney = tempMoney.get(i);
                        }
                        else if (tempMoney.get(i) > maxMoney) {
                            maxMoney = tempMoney.get(i);
                        }
                    }
                    boolean[] b = check(minMoney, maxMoney);
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + minMoney +
                        "</font>至" + "<font color=\"#d7c10f\">" + maxMoney + "</font>元,您将盈利<font color=\"" +
                        getColor(b, 0) + "\">" + (minMoney - betMoney) + "</font>至" + "<font color=\"" +
                        getColor(b, 1) + "\">" + (maxMoney - betMoney) + "</font>元"));
                }
            }
            else {
                resetInf();
            }
        }
        else if (lotteryType == STH) {
            sthAward(maxMoney);
        }
        else if (lotteryType == ETH) {
            // 同号与不同号部分不得相同
            if (parent == gridView1) {
                if (array2.get(position).isIfSelected()) {
                    array2.get(position).setIfSelected(false);
                }
                adapter2.notifyDataSetChanged();
            }
            else if (parent == gridView2) {
                if (array1.get(position).isIfSelected()) {
                    array1.get(position).setIfSelected(false);
                }
                adapter1.notifyDataSetChanged();
            }

            int num1 = 0;
            int num2 = 0;
            int num3 = 0;
            for (int i = 0; i < array1.size(); i++) {
                if (array1.get(i).isIfSelected()) {
                    num1++;
                }
                if (array2.get(i).isIfSelected()) {
                    num2++;
                }
                if (array3.get(i).isIfSelected()) {
                    num3++;
                }
            }
            if (num1 == 0 && num2 == 0 && num3 == 0) {
                resetInf();
            }
            else {
                invalidateNum();
                enableClearBtn();
                betNumber = num1 * num2 + num3;
// refreshMoney = true;
                betMoney = betNumber * 2 * 1;
                String betInf = getBetInf(betNumber, betMoney);
                moneyInf.setText(Html.fromHtml(betInf));
                invalidateAll();
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" +
                    ((num1 * num2 == 0 ? 0 : 80) + (num3 == 0 ? 0 : 15)) +
                    "</font>元,您将盈利<font color=\"#d7c10f\">" +
                    ((num1 * num2 == 0 ? 0 : 80) + (num3 == 0 ? 0 : 15) - betMoney) + "</font>元"));

                if (code.indexOf(";") > 0) {
                    betNumber = num1 * num2;
                    betMoney = betNumber * 2;
                    betMoneySub = num3 * 2;
                }
            }

        }
        if (lotteryType == SBTH) {
            sbthAward();
        }
        if (lotteryType == EBTH) {
            for (int i = 0; i < array1.size(); i++) {
                if (array1.get(i).isIfSelected()) {
                    betNumber++;
                }
            }
            if (betNumber == 0) {
                resetInf();
            }
            else {
                invalidateNum();
                enableClearBtn();
                if (betNumber > 1) {
                    betNumber = MathUtil.combination((int) betNumber, 2);
                    betMoney = betNumber * 2 * 1;
                    String betInf = getBetInf(betNumber, betMoney);
                    moneyInf.setText(Html.fromHtml(betInf));
                    invalidateAll();
                    lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + 8 +
                        "</font>元,您将盈利<font color=\"" + getColor(8, betMoney) + "\">" + (8 - betMoney) +
                        "</font>元"));
                }
            }

        }

    }

    public void sbthAward() {
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                betNumber++;
            }
        }
        if (betNumber == 0 && !ifSubButtonPressed) {
            resetInf();
        }
        else {
            invalidateNum();
            enableClearBtn();
            if (betNumber > 2 || ifSubButtonPressed) {
                int num1 = (int) (betNumber > 2 ? MathUtil.combination((int) betNumber, 3) : 0);
                int num2 = ifSubButtonPressed ? 1 : 0;
                betNumber = num1 + num2;
                betMoney = betNumber * 2 * 1;
                String betInf = getBetInf(betNumber, betMoney);
                moneyInf.setText(Html.fromHtml(betInf));
                invalidateAll();
                lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" +
                    ((num1 > 0 ? 40 : 0) + (num2 == 0 ? 0 : 10)) + "</font>元,您将盈利<font color=\"#d7c10f\">" +
                    ((num1 > 0 ? 40 : 0) + (num2 == 0 ? 0 : 10) - betMoney) + "</font>元"));

                if (code.indexOf(";") > 0) {
                    betNumber = num1;
                    betMoney = betNumber * 2;
                    betMoneySub = num2 * 2;
                }
            }
        }

    }

    public void sthAward(int maxMoney) {
// Boolean refreshMoney;
        int num1 = 0;
        int num2 = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).isIfSelected()) {
                num1++;
            }
        }

        if (ifSubButtonPressed) {
            num2 = 1;
            maxMoney += 40;
        }

        if (num1 == 0 && !ifSubButtonPressed) {
            resetInf();
        }
        else {
            invalidateNum();
            enableClearBtn();
            betNumber = num1 + num2;
            betMoney = betNumber * 2;
            maxMoney = (num1 == 0 ? 0 : 240) + (ifSubButtonPressed ? 40 : 0);
            String betInf = getBetInf(betNumber, betMoney);
            moneyInf.setText(Html.fromHtml(betInf));
            invalidateAll();
            lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + maxMoney +
                "</font>元,您将盈利<font color=\"#d7c10f\">" + (maxMoney - betMoney) + "</font>元"));

            if (code.indexOf(";") > 1) {
                betNumber = num1;
                betMoney = betNumber * 2;
                betMoneySub = num2 * 2;
            }
        }
    }

    public boolean[] check(int minMoney, int maxMoney) {
        boolean[] b = new boolean[3];
        for (int i = 0; i < b.length; i++) {
            b[i] = false;
        }
        if (minMoney - betMoney > 0) {
            b[0] = true;
        }
        else if ((minMoney - betMoney < 0) && (maxMoney - betMoney > 0)) {
            b[1] = true;
        }
        else if (maxMoney - betMoney < 0) {
            b[2] = true;
        }
        return b;
    }

    private String getColor(int reword, long betMoney) {
        if (reword > betMoney) {
            return "#d7c10f";
        }
        else
            return "#1874CD";
    }

    private String getColor(boolean[] b, int flag) {
        if (b[0])
            return "#d7c10f";
        else if (b[1]) {
            if (flag == 0)
                return "#1874CD";
            else
                return "#d7c10f";
        }
        else if (b[2])
            return "#1874CD";
        else
            return "#d7c10f";
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
        initArrayData();
        clearSubButton();
        resetInf();
    }

    protected void resetInf() {
        super.resetInf();
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        showBallNum();
    }

    public void initLotteryIntroduce() {
// lotteryIntroduce.setText(Html.fromHtml("若中奖,奖金:<font color=\"#d7c10f\">" + "-"
// + "</font>元,您将盈利<font color=\"#d7c10f\">" + "-" + "</font>元"));
    }

    private void invalidateNum() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        invalidateDisplay();
    }

    private int getReword(int lotteryType) {
// return reword[lotteryType - 1];
        return 0;
    }

    private void invalidateDisplay() {
        displayCode = getBallsDisplayInf();
// choosingInf.setText(Html.fromHtml(displayCode));
        showBallNum();
    }

    protected void invalidateAll() {
        code = getBallsBetInf();
        invalidateDisplay();
    }

    protected void clear() {
        super.clear();
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
    }

    protected void goRules() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "新快三游戏规则");
        bundel.putString("lottery_help", "help_new/k3.html");
        intent.putExtras(bundel);
        intent.setClass(JLKSActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    private void goZouShiTu() {
        Intent intent = new Intent();
        Bundle bundel = new Bundle();
        bundel.putString("lottery_name", "新快三走势图");
        bundel.putString("data_type", "table");
        bundel.putString("lottery_help",
                         "http://m.haozan88.com/?g=Trend&m=Index&a=index&lot=jlks&style=basic&size=20");
        intent.putExtras(bundel);
        intent.setClass(JLKSActivity.this, LotteryWinningRules.class);
        startActivity(intent);
    }

    protected void goSelectLuckyBall() {
        Intent intent = new Intent();
        intent.setClass(JLKSActivity.this, LotteryDiviningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lotteryType", "jlk3");
        if (lotteryType == 1)
            bundle.putString("lotteryTypeHZ", "1");
        else if (lotteryType == 2)
            bundle.putString("lotteryTypeSTH", "2");
        else if (lotteryType == 3)
            bundle.putString("lotteryTypeETH", "3");
        else if (lotteryType == 4)
            bundle.putString("lotteryTypeSBTH", "4");
        else if (lotteryType == 5)
            bundle.putString("lotteryTypeEBTH", "5");
        intent.putExtras(bundle);
        startActivityForResult(intent, 15);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.analyse_tips) {
            goZouShiTu();
        }
        else {
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
            else if (v.getId() == R.id.sub2_re) {
                vibratorWhenClick();
                if (ifSubButtonPressed) {
                    ifSubButtonPressed = false;
                    subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice);
                    subButtonTv1.setTextColor(getResources().getColor(R.color.white));
                    subButtonTv2.setTextColor(getResources().getColor(R.color.white_gray));
                }
                else {
                    ifSubButtonPressed = true;
                    subButton.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
                    subButtonTv1.setTextColor(getResources().getColor(R.color.dark_purple));
                    subButtonTv2.setTextColor(getResources().getColor(R.color.dark_purple));
                }
                if (lotteryType == STH) {
                    betNumber = 0;
                    sthAward(0);
                    checkBet(betMoney);
                }
                else if (lotteryType == SBTH) {
                    betNumber = 0;
                    sbthAward();
                    checkBet(betMoney);
                }
            }
        }
    }

    @Override
    protected void analyseData(String json) {
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
                String betInf = "1注     <font color='d7c10f'>2元</font>";
                moneyInf.setText(Html.fromHtml(betInf));
                if (lotteryType == 1) {
                }
                else if (lotteryType == 2) {
                }
                else if (lotteryType == 3) {
                }
                else if (lotteryType == 4) {
                }
                else if (lotteryType == 5) {
                }
                invalidateAll();
// onBallClickInf(-1, -1);
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
        return null;
    }

    private int jlksNum01;
    private int jlksNum02;
    private int jlksNum03;

    @Override
    protected void searchLuckyNum() {

        if (getQ_code().equals(",,")) {
            ViewUtil.showTipsToast(this, "分析功能至少需要输入1个号码");
        }
        else {
            if (jlksNum01 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理百位上1个红球");
            }
            else if (jlksNum02 > 1) {
                ViewUtil.showTipsToast(this, "分析功能暂时至多能处理十位上1个红球");
            }
            else if (jlksNum03 > 1) {
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
                bundle.putString("kind", "jlk3");
                bundle.putString("dispaly_q_code", getQ_code());
                bundle.putString("q_code", q_codeSwitch(getQ_code()));
                intent.putExtras(bundle);
                intent.setClass(JLKSActivity.this, OpenHistory.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void selecteType(int type, int index) {
        lotteryType = index + 1;
        databaseData.putString("jlks_way", JLKSWay[lotteryType - 1]);
        databaseData.commit();
        clearBalls();
        title.setText(JLKSWay[lotteryType - 1]);
        showWay();
        showBallNum();
        disableBetBtn();

        popupWindowWaySelect.dismiss();
    }

    protected void flashBall() {
        if (lotteryType == EBTH) {
            shaiziWay = TWO_SHAIZI;
        }
        else {
            shaiziWay = THREE_SHAIZI;
        }

        initShaizi();

        handler.sendEmptyMessage(VIBRATE);
        handler.sendEmptyMessage(START_RANDOM);

        handler.sendEmptyMessageDelayed(STOP_RANDOM, 1000);
        for (int i = 0; i < 5; i++) {
            handler.sendEmptyMessageDelayed(SHAIZI_MOVE, i * 200);
        }
        handler.sendEmptyMessageDelayed(SHOW_SHAIZI, 1000);
        handler.sendEmptyMessageDelayed(ZOOM_IN_SHAIZI, 1500);
    }

    private void initShaizi() {
        layoutContainer.setVisibility(View.VISIBLE);
        layoutContainer.removeAllViews();
        if (shaiziWay == ONE_SHAIZI) {
            ivShaizi[0] = new ImageView(this);
            RelativeLayout.LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutContainer.addView(ivShaizi[0], params);
        }
        else if (shaiziWay == TWO_SHAIZI) {
            ivShaizi[0] = new ImageView(this);
            RelativeLayout.LayoutParams params1 =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params1.leftMargin = layoutContainer.getMeasuredWidth() / 4 - shaiziWidth / 2;
            params1.topMargin = layoutContainer.getMeasuredHeight() / 2 - shaiziHeight / 2;
            layoutContainer.addView(ivShaizi[0], params1);

            ivShaizi[1] = new ImageView(this);
            RelativeLayout.LayoutParams params2 =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params2.leftMargin = layoutContainer.getMeasuredWidth() * 3 / 4 - shaiziWidth / 2;
            params2.topMargin = layoutContainer.getMeasuredHeight() / 2 - shaiziHeight / 2;
            layoutContainer.addView(ivShaizi[1], params2);

            ivShaizi[0].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[0]]);
            ivShaizi[1].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[1]]);
        }
        else if (shaiziWay == THREE_SHAIZI) {
            // 骰子的距离1/3
            int gap = layoutContainer.getMeasuredWidth() / 3;

            // 屏幕中心点
            int centerX = layoutContainer.getMeasuredWidth() / 2;
            int centerY = layoutContainer.getMeasuredHeight() / 2;

            ivShaizi[0] = new ImageView(this);
            RelativeLayout.LayoutParams params1 =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params1.leftMargin = centerX - gap / 2 - shaiziWidth / 2;
            params1.topMargin = (int) (centerY - 1.732 / 6 * gap - shaiziHeight / 2);
            layoutContainer.addView(ivShaizi[0], params1);

            ivShaizi[1] = new ImageView(this);
            RelativeLayout.LayoutParams params2 =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params2.leftMargin = centerX + gap / 2 - shaiziWidth / 2;
            params2.topMargin = (int) (centerY - 1.732 / 6 * gap - shaiziHeight / 2);
            layoutContainer.addView(ivShaizi[1], params2);

            ivShaizi[2] = new ImageView(this);
            RelativeLayout.LayoutParams params3 =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params3.leftMargin = centerX - shaiziWidth / 2;
            params3.topMargin = (int) (centerY + 1 / 1.732 * gap - shaiziHeight / 2);
            layoutContainer.addView(ivShaizi[2], params3);

            ivShaizi[0].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[0]]);
            ivShaizi[1].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[1]]);
            ivShaizi[2].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[2]]);
        }
    }

    protected void otherHandler(int what) {
        switch (what) {
            case START_RANDOM:
                startRandom();
                break;

            case STOP_RANDOM:
                for (int i = 0; i < shaiziWay; i++) {
                    anim[i].stop();
                }
                break;

            case SHAIZI_MOVE:
                moveShaizi();
                break;

            case SHOW_SHAIZI:
                showShaizi();
                break;

            case ZOOM_IN_SHAIZI:
                zoomInDices();
                break;
        }
    }

    private void startRandom() {
        for (int i = 0; i < shaiziWay; i++) {
            ivShaizi[i].setBackgroundResource(R.anim.shaizi_anim);
            anim[i] = (AnimationDrawable) ivShaizi[i].getBackground();
            anim[i].start();
        }
        notify.play(soundRotate, (float) 0.1, (float) 0.1, 0, 0, 1);
    }

    private void showShaizi() {
        randomK3();

        TranslateAnimation animationLast = new TranslateAnimation(0, 0, 0, 0);
        animationLast.setDuration(200);
        animationLast.setFillAfter(true);

        for (int i = 0; i < shaiziWay; i++) {
            ivShaizi[i].startAnimation(animationLast);
            selectedIndex[i] = tempDice[i];
            ivShaizi[i].setBackgroundResource(DRAWABLE_SHAIZI[selectedIndex[i]]);
        }
    }

    private void moveShaizi() {
        int moveWidth = layoutContainer.getWidth() / 4;
        int moveHeight = layoutContainer.getHeight() / 4;

        Random rd = new Random();
        for (int i = 0; i < shaiziWay; i++) {
            int randomIntX = rd.nextInt(moveWidth * 7 / 8) + moveWidth / 8;
            int randomIntY = rd.nextInt(moveHeight * 3 / 4) + moveHeight / 4;
            if (rd.nextInt(2) == 0) {
                randomIntX = -randomIntX;
            }
            if (rd.nextInt(2) == 0) {
                randomIntY = -randomIntY;
            }

            TranslateAnimation animation = new TranslateAnimation(0, randomIntX, 0, randomIntY);
            animation.setDuration(200);
            animation.setInterpolator(new CycleInterpolator(0.5f));
            animation.setFillAfter(true);
            ivShaizi[i].startAnimation(animation);
        }
    }

    private void zoomInDices() {
        for (int i = 0; i < shaiziWay; i++) {
            int location[] = getRandomDiceLocation(i);
            zoomInDice(location, layoutContainer.getChildAt(i));
        }
    }

    private int[] getRandomDiceLocation(int i) {
        View view;
        if (lotteryType == HZ) {
            view = gridView1.getChildAt(tempDice[0] + tempDice[1] + tempDice[2]);
        }
        else if (lotteryType == STH) {
            view = gridView1.getChildAt(tempDice[0]);
        }
        else if (lotteryType == ETH) {
            if (i == 0 || i == 1) {
                view = gridView1.getChildAt(tempDice[0]);
            }
            else {
                view = gridView2.getChildAt(tempDice[2]);
            }
        }
        else if (lotteryType == SBTH) {
            view = gridView1.getChildAt(tempDice[i]);
        }
        else if (lotteryType == EBTH) {
            view = gridView1.getChildAt(tempDice[i]);
        }
        else {
            view = gridView1.getChildAt(tempDice[i]);
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        location[0] += view.getWidth() / 2;
        location[1] += view.getHeight() / 2;
        return location;
    }

    private void zoomInDice(int location[], final View view) {
        int[] diceLocation = new int[2];
        view.getLocationOnScreen(diceLocation);

        Animation translateAnimation =
            new TranslateAnimation(0, location[0] - diceLocation[0] - view.getWidth() / 2, 0, location[1] -
                diceLocation[1] - view.getHeight() / 2);
        translateAnimation.setDuration(500);
        Logger.inf("x:" + (location[0] - diceLocation[0]) + "y:" + (location[1] - diceLocation[1]));

        Animation scaleAnimation =
            new ScaleAnimation(1f, 0.0f, 1f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                               Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);

        final Animation alphaAnimation = new AlphaAnimation(1f, 0.5f); // 设置透明度变化动画
        alphaAnimation.setDuration(500);

        AnimationSet set = new AnimationSet(true); // 创建动画集对象
        set.addAnimation(scaleAnimation); // 添加尺寸变化动画
        set.addAnimation(translateAnimation); // 添加位置变化动画
        set.addAnimation(alphaAnimation); // 添加透明度渐变动画
        set.setFillAfter(true); // 停留在最后的位置
        set.setFillEnabled(true);

        view.startAnimation(set);
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                shaking = false;
                layoutContainer.setVisibility(View.GONE);

                notify.play(soundSelected, (float) 0.1, (float) 0.1, 0, 0, 1);

                showK3Dice();
                view.clearAnimation();

                enableBetBtn();
                enableClearBtn();
                betMoney = 2;
                invalidateAll();
                luckynum = orgCode;
            }
        });
    }

}
