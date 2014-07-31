package com.haozan.caipiao.activity.bet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.GiveLottery;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.LotteryStatisticsSetting;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.adapter.GameListAdapter;
import com.haozan.caipiao.adapter.unite.CommissionAdapter;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.interfaceUtil.IBuyLottery;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetBigBallViews;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.JsonUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.view.CustomScrollView;
import com.haozan.caipiao.view.NewBetBallsLayout.OnTouchMoveListener;
import com.haozan.caipiao.widget.SelectLuckyNumPup.ButtonClickListener;
import com.umeng.analytics.MobclickAgent;

public abstract class BetDigitalBasic
    extends BasicActivity
    implements IBuyLottery, ButtonClickListener, OnClickListener, OnTouchMoveListener {
    private static final String DEDUCTDES[] = {"冷热门号码", "冷热门号码显示", "遗漏显示"};
    private static final int DEDUCTSCORE[] = {0, 3, 5, 10};
    private static final String BETEND = "投注已截止";
    private static final String GETTINGBETIN = "信息获取中..";
    private static final String GETBETINFFAIL = "信息获取失败";
    private static final String SHAKERANDOM = "使劲摇摇手机，试试你的运气吧！";
    protected static final String MONEY_TIPS = "0注 <font color='red'>0元</font>";
    protected static final String HOT_NUM_FONT = "<font color='#EEEE00'>";
    protected static final String COLD_NUM_FONT = "<font color='blue'>";
    protected static final String HOT_NUM_FONT_ANALASE = "<font color='red'>";
    protected static final String COLD_NUM_FONT_ANALASE = "<font color='blue'>";
    protected static final String[] PLAYTYPE_STR = {" 购 彩 ", " 加 单 ", " 合 买 ", "送彩票"};

    private static final int GETNEWINF = 1;
    public static final int RANDOMBALLS = 2;
    protected static final int UPDATEBETTIME = 3;
    public static final int FLASHBALL = 4;
    public static final int VIBRATE = 5;
    public static final int SOUND = 6;
    protected static final int SHAKEIMAGE = 7;
    private static final int SHACKTIMES = 20;
    private static final int FORCE_THRESHOLD = 700;
    protected static final int SEARCHNUM[] = {20, 30, 50, 100};
    protected String[] list = new String[] {"20期", "30期", "50期", "100期"};

    public boolean ifShowInf = false;
    public static String requestCode;
    public static String requestDFLJYCode;
    protected int betWay = 0;
    protected boolean isSingle = true;
    private Boolean shakeSensor;
// private Boolean showProgressBar = false;
    protected int searchType = 1;
    protected int term_num;
    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    private int vibrateTimes = 0;
    protected int randomTimes = 0;
    private int hitOkSfx;
    protected int loteryMethodType = 0;
    private long lastShakeTime;
    private long presentShakeTime;
    private long duration;
    private float lastX = 0.0f;
    private float lastY = 0.0f;
    private float lastZ = 0.0f;
    private float presentX = 0.0f;
    private float presentY = 0.0f;
    private float presentZ = 0.0f;
    private float currenForce;
    // 是否显示冷热门号码
    protected Boolean showHotNum = false;
    // 是否显示遗漏号码
    protected Boolean showOmitNum = false;
    // whether locking the shake
    protected Boolean shakeLock;
    // whether shaking at present
    protected Boolean shaking;
    // the term of the lottery
    protected String term;
    // the English name of the lottery
    protected String kind;
    protected String awardTime;
    // the way of choosing ball
    protected String mode = "0000";
    protected String luckynum;
    protected String mstar;
    protected String todayluck;
    private StringBuilder betLastTime;
    protected int searchLuckyType;
    // 投注注数
    protected long betNumber = 0;
    // the money of each betting
    protected long betMoney = 2;
    protected long betMoneySub = 2;// 当一个选号界面有两种玩法时用
    // the code send to server to buy
    protected String code = null;
    // the word to show what number choosed
    protected String displayCode = null;
    protected String orgCode;

    protected Button analyseTips;
    protected ImageView help;
    protected LinearLayout helpLin;
    protected ImageView topArrow;
    protected ImageView shakeLockView;
    protected RelativeLayout shakeRela;
    protected Button btnShake;
    protected TextView betTerm;
    protected TextView betTimeInf;
    protected TextView countDownTime;
    protected TextView betDirection;
    protected Button betBt;
    protected Button betWayBt;
    protected Button imgShowBet;
    protected TextView title;
    protected TextView redballChoosingNum;
    protected TextView moneyInf;
    protected TextView choosingInf;
    protected RelativeLayout choosingInfLayout;

    private RelativeLayout bigBallLayout;
    private TextView bigBallTv;
    private TextView hotNumTv;
    protected BetBigBallViews bigBallViews;

    protected Button random;
    protected ImageView clear;
    protected CustomScrollView scrollView;

    private ProgressDialog progressDialog;
    protected Animation shakeAnim;
    protected SoundPool notify;
    protected SimpleDateFormat dateFormat;
    private SensorManager sm;
    private Sensor acceleromererSensor;
    private SensorEventListener acceleromererListener;
    protected PopupWindow betWayPopupWindow;

    protected ImageView img_help_info_bg;
    protected boolean ifShowImgHelp;

    protected Boolean isOld = false;
    protected int id = 1;
    protected String betOrgNum = null;
    private boolean isFrequent;
    // 彩种是否具有合买功能合买
    protected boolean isUnite = false;
    // 是否发起合买
    protected boolean doUnite = false;
    // 工具弹出界面布局元素
    protected PopupWindow popMenu;
    protected View popContentView;
    protected LinearLayout normalToolsLayout;
    protected LinearLayout numAnalyse;
    protected LinearLayout lotteryCalculator;
    protected LinearLayout lotteryChart;
    protected LinearLayout luckyBallSelect;
    protected TextView toolBoxAnalyse;
    protected TextView toolBoxChart;
    protected TextView toolBoxCalculator;
    protected TextView toolBoxLuckyBall;
    protected LinearLayout numberStatisticsLayout;
    protected LinearLayout hotNumStatisticsLayout;
    protected LinearLayout omitStatistticsLayout;
    protected RelativeLayout omiRela;
    protected RelativeLayout hotnumRela;
    protected LinearLayout showSetting;
    protected GridView hotGrid;
    protected CommissionAdapter adapter;
// protected boolean ifOmiSelected, ifHotnumSelected;
    protected ImageView omiImg, hotnumImg;
// protected TextView statisticsText;
    protected LinearLayout gameLayout;
    protected GridView gameGridView;
    private ArrayList<GameDownloadInf> gameList;
    private GameListAdapter gameListAdapter;
    protected RelativeLayout layoutTerm;

    protected boolean clickOne = true;
    protected Animation anim3;
    protected Animation anim4;

    protected boolean ifBetEnd = false;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETNEWINF:// 向服务器获取投注信息
                    if (HttpConnectUtil.isNetworkAvailable(BetDigitalBasic.this)) {
                        GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                        getLotteryInf.execute();
                    }
                    else
                        setGetTermFail();
                    break;
                case UPDATEBETTIME:// 投注倒计时
                    betLastTime.delete(0, betLastTime.length());
                    long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
                    countDownTime.setText(TimeUtils.getCountDownTime(millis));
                    millis -= 1000;
                    if (millis >= 0) {
                        if (millis > 60 * 60 * 1000) {
                            layoutTerm.setVisibility(View.GONE);
                        }
                        else {
                            layoutTerm.setVisibility(View.VISIBLE);
                        }
                        handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                        ifBetEnd = false;
                    }
                    else {
                        ifShowInf = true;
                        betTimeInf.setVisibility(View.GONE);
                        countDownTime.setText(BETEND);
                        ifBetEnd = true;
                        handler.sendEmptyMessageDelayed(GETNEWINF, 5000);
                    }
                    break;
                case RANDOMBALLS:
                    randomBallsShow();
                    break;
                case FLASHBALL:
                    randomBalls();
                    break;
                case SOUND:
                    notify.play(hitOkSfx, (float) 0.1, (float) 0.1, 0, 0, 1);
                    shaking = false;
                    break;
                case VIBRATE:
                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    mVibrator.vibrate(50);
                    break;
                case SHAKEIMAGE:
                    if (shakeLockView != null && shakeLockView.isShown()) {
                        shakeLockView.startAnimation(shakeAnim);
                        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
                    }
                    break;
                default:
                    otherHandler(msg.what);
            }
        }
    };

    protected void initBasic() {
        ifShowImgHelp = preferences.getBoolean("if_show_img_help", true);
        popWindow();
        anim3 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim4 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        searchType = preferences.getInt("hot_way", 1);
        searchLuckyType = 1;
        shakeLock = true;
        shaking = false;
        betLastTime = new StringBuilder();
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_flicker);
        notify = new SoundPool(10, AudioManager.STREAM_SYSTEM, 10);
        hitOkSfx = notify.load(BetDigitalBasic.this, R.raw.ding, 0);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acceleromererListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 3 ||
                    shaking || shakeLock)
                    return;
                presentX = event.values[SensorManager.DATA_X];
                presentY = event.values[SensorManager.DATA_Y];
                presentZ = event.values[SensorManager.DATA_Z];
                presentShakeTime = System.currentTimeMillis();
                if (presentShakeTime - lastShakeTime > 100) {
                    duration = presentShakeTime - lastShakeTime;
                    lastShakeTime = presentShakeTime;

                    float deltaX = presentX - lastX;
                    float deltaY = presentY - lastY;
                    float deltaZ = presentZ - lastZ;
                    currenForce =
                        FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / duration *
                            10000;
                    if (currenForce > FORCE_THRESHOLD) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("inf", "username [" + appState.getUsername() + "]: v2 bet vibrate");
                        map.put("more_inf", "bet vibrate " + kind);
                        vibrateTimes++;
                        map.put("extra_inf", "bet vibrate " + vibrateTimes);
                        String eventName = "v2 bet vibrate";
                        FlurryAgent.onEvent(eventName, map);
                        besttoneEventCommint(eventName);
                        String eventNameMob = "bet_vibrate";
                        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind, vibrateTimes);

                        mode = "1002";
                        shaking = true;
                        flashBall();
                    }
                    lastX = presentX;
                    lastY = presentY;
                    lastZ = presentZ;
                }
            }
        };
        shakeSensor =
            sm.registerListener(acceleromererListener, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void otherHandler(int what) {

    }

    protected void popWindow() {
        setPopContentView();
        popMenu = new PopupWindow(this);
        popMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popMenu.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2 + 20);
        popMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popMenu.setOutsideTouchable(true);
        popMenu.setFocusable(true);
        popMenu.setContentView(getPopContentView());
    }

    protected void setPopContentView() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        popContentView = mLayoutInflater.inflate(R.layout.tools_bet, null);
    }

    protected View getPopContentView() {
        return popContentView;
    }

    protected void setupViews() {
        setupMainViews();
        setupBetToolViews();
    }

    // 初始化界面中主要的view
    private void setupMainViews() {
        scrollView = (CustomScrollView) this.findViewById(R.id.scroller);
        help = (ImageView) this.findViewById(R.id.bet_help);
        helpLin = (LinearLayout) this.findViewById(R.id.bet_help_lin);
        helpLin.setOnClickListener(this);
        topArrow = (ImageView) this.findViewById(R.id.arrow_top);
        title = (TextView) this.findViewById(R.id.bet_title);
        betTerm = (TextView) this.findViewById(R.id.bet_term);
        betTimeInf = (TextView) this.findViewById(R.id.bet_time_inf);
        countDownTime = (TextView) this.findViewById(R.id.bet_countdown_time);
        shakeLockView = (ImageView) this.findViewById(R.id.bet_shake);
        shakeRela = (RelativeLayout) findViewById(R.id.rela_bet_shake);
        btnShake = (Button) findViewById(R.id.btn_bet_shake);
        btnShake.setOnClickListener(this);
        choosingInf = (TextView) this.findViewById(R.id.choosing_inf);
        choosingInfLayout = (RelativeLayout) this.findViewById(R.id.compare_layout);
        choosingInfLayout.setOnClickListener(this);
        analyseTips = (Button) this.findViewById(R.id.analyse_tips);
        analyseTips.setOnClickListener(this);
        moneyInf = (TextView) this.findViewById(R.id.bet_money_inf);
        random = (Button) this.findViewById(R.id.bet_random_button);
        random.setOnClickListener(this);
        betBt = (Button) this.findViewById(R.id.bet_button);
        betBt.setOnClickListener(this);
        betWayBt = (Button) this.findViewById(R.id.bet_way_button);
        betWayBt.setOnClickListener(this);
        imgShowBet = (Button) this.findViewById(R.id.img_show_bet_way);
        clear = (ImageView) this.findViewById(R.id.bet_clear_button);
        clear.setEnabled(false);
        clear.setOnClickListener(this);

        bigBallLayout = (RelativeLayout) this.findViewById(R.id.layout_big_ball);
        bigBallTv = (TextView) this.findViewById(R.id.big_ball);
        hotNumTv = (TextView) this.findViewById(R.id.hot_num_count);
        bigBallViews = new BetBigBallViews();
        bigBallViews.setBigBallLayout(bigBallLayout);
        bigBallViews.setBigBallTv(bigBallTv);
        bigBallViews.setHotNumTv(hotNumTv);
        layoutTerm = (RelativeLayout) this.findViewById(R.id.rl_term);
    }

    // 初始化界面中工具按钮展现的view
    private void setupBetToolViews() {
        normalToolsLayout = (LinearLayout) popContentView.findViewById(R.id.layout_normal_tools);
        lotteryChart = (LinearLayout) popContentView.findViewById(R.id.layout_lottery_chart);
        lotteryChart.setOnClickListener(this);
        numAnalyse = (LinearLayout) popContentView.findViewById(R.id.layout_num_analyse);
        numAnalyse.setOnClickListener(this);
        lotteryCalculator = (LinearLayout) popContentView.findViewById(R.id.layout_lottery_calculator);
        lotteryCalculator.setOnClickListener(this);
        luckyBallSelect = (LinearLayout) popContentView.findViewById(R.id.layout_selected_lucky_ball);
        luckyBallSelect.setOnClickListener(this);
        toolBoxAnalyse = (TextView) popContentView.findViewById(R.id.tool_box_analyse);
        toolBoxChart = (TextView) popContentView.findViewById(R.id.tool_box_chart);
        toolBoxCalculator = (TextView) popContentView.findViewById(R.id.tool_box_calculator);
        toolBoxLuckyBall = (TextView) popContentView.findViewById(R.id.tool_box_lucky_ball);
        numberStatisticsLayout = (LinearLayout) popContentView.findViewById(R.id.layout_number_statistics);
        hotNumStatisticsLayout = (LinearLayout) popContentView.findViewById(R.id.layout_hot_num_statistics);
        hotNumStatisticsLayout.setOnClickListener(this);
        omitStatistticsLayout = (LinearLayout) popContentView.findViewById(R.id.layout_omit_statistics);
        omitStatistticsLayout.setOnClickListener(this);
        omiRela = (RelativeLayout) popContentView.findViewById(R.id.re_omit);
        omiRela.setOnClickListener(this);
        adapter = new CommissionAdapter(this, list, searchType, "bet_tools");
        hotGrid = (GridView) popContentView.findViewById(R.id.hotnum_grid);
        hotGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        hotGrid.setAdapter(adapter);
        hotnumRela = (RelativeLayout) popContentView.findViewById(R.id.re_hotnum);
        hotnumRela.setOnClickListener(this);
        showSetting = (LinearLayout) popContentView.findViewById(R.id.show_setting);
        omiImg = (ImageView) popContentView.findViewById(R.id.img_omit_select);
        hotnumImg = (ImageView) popContentView.findViewById(R.id.img_hotnum_select);

// statisticsText = (TextView) popContentView.findViewById(R.id.statistics_text);
        gameLayout = (LinearLayout) popContentView.findViewById(R.id.layout_game);
        gameGridView = (GridView) popContentView.findViewById(R.id.layout_game_list);
        initGameList();
    }

    public void appearAnimation(final View ll) {
        AnimationSet as = new AnimationSet(true);

        TranslateAnimation animation1 = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF + 30, 0);
        animation1.setDuration(300);
        animation1.setFillEnabled(true);
        animation1.setFillAfter(true);

        AlphaAnimation alpha = new AlphaAnimation((float) 0.0, (float) 1.0);
        alpha.setDuration(300);

        as.addAnimation(animation1);
        as.addAnimation(alpha);
        as.setFillEnabled(true);
        as.setFillAfter(true);
        as.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll.setVisibility(View.VISIBLE);
            }
        });

        ll.startAnimation(as);
    }

    public void disappearAnimation(final View ll) {
        AnimationSet as = new AnimationSet(true);

        TranslateAnimation animation1 = new TranslateAnimation(0, 0, 0, Animation.RELATIVE_TO_SELF + 30);
        animation1.setDuration(300);
        animation1.setFillEnabled(true);
        animation1.setFillAfter(true);

        AlphaAnimation alpha = new AlphaAnimation((float) 1.0, (float) 0.0);
        alpha.setDuration(300);

        as.addAnimation(animation1);
        as.addAnimation(alpha);
        as.setFillEnabled(true);
        as.setFillAfter(true);
        as.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll.setVisibility(View.GONE);
            }
        });

        ll.startAnimation(as);
    }

    private void initGameList() {
        gameList = new ArrayList<GameDownloadInf>();
        gameListAdapter = new GameListAdapter(BetDigitalBasic.this, gameList);
        gameListAdapter.setLotteryBet(kind, "1");
        gameGridView.setAdapter(gameListAdapter);
        getOldGameList();
    }

    // 获取上次保存的游戏列表，初始化界面
    private void getOldGameList() {
        String gameListJson = preferences.getString("game_list", null);
        if (gameListJson != null) {
            JsonUtil.analyseGameListData(gameList, gameListJson, kind);
        }
        if (gameList.size() == 0) {
            gameLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题栏弹出玩法居中显示
     * 
     * @param popup 传入的标题栏弹出框
     */
    protected void showPopupCenter(PopupWindow popup) {
// Rect frame = new Rect();
// getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
// int statusBarHeight = frame.top;
        popup.showAtLocation(scrollView,
                             Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                             0,
                             findViewById(R.id.top).getHeight() -
                                 (findViewById(R.id.rl_term).getVisibility() == View.VISIBLE
                                     ? findViewById(R.id.rl_term).getHeight() : 0) + 20);
    }

    protected void resetInf() {
        betMoney = 0;
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        resetBtn();
    }

    protected void resetBtn() {
        clear.setEnabled(false);
        betBt.setEnabled(false);
        betWayBt.setEnabled(false);
    }

    protected void enableClearBtn() {
        clear.setEnabled(true);
    }

    protected void enableBetBtn() {
        betBt.setEnabled(true);
        betWayBt.setEnabled(true);
    }

    protected void disableBetBtn() {
        betBt.setEnabled(false);
        betWayBt.setEnabled(false);
    }

    /**
     * 判断是否投注按钮可以点击
     * 
     * @param money 传入金额，根据金额判断，金额>0可以点击
     */
    protected void checkBet(long money) {
        if (money > 0) {
            enableBetBtn();
        }
        else {
            disableBetBtn();
        }
    }

    protected void invalidateAll() {
    };

    protected void shakeLock() {
        shakeLockView.startAnimation(shakeAnim);
        handler.sendEmptyMessageDelayed(SHAKEIMAGE, 10000);
    }

    protected void initInf() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Boolean fromHall = bundle.getBoolean("from_hall", false);
            betWay = bundle.getInt("bet_way_bottom", 0);
            doUnite = bundle.getBoolean("bet_is_unite", false);
            if (!fromHall) {
                betWay = 2;// 为1，购彩；为2，加单
                betWayBt.setVisibility(View.GONE);
                imgShowBet.setVisibility(View.GONE);
// betWay = 1;
// betBt.setText("  购 彩  ");
                int newId = bundle.getInt("bet_id");
                if (newId != 0) {
                    isOld = true;
                    id = newId;
                }
                betOrgNum = bundle.getString("bet_code");
                if (betOrgNum != null)
                    defaultNum(betOrgNum);
                String way = bundle.getString("mode");
                if (way != null) {
                    mode = way;
                    luckynum = bundle.getString("lucky_num");
                    mstar = bundle.getString("mstar");
                    todayluck = bundle.getString("today_lucky");
                }
            }
            else
                orgBetWay();
        }
        if (bundle != null) {
            term = bundle.getString("bet_term");
            betTerm.setText(" " + term + "期");
            awardTime = bundle.getString("awardtime");
            endTimeMillis = (bundle.getLong("endtime"));
            gapMillis = (bundle.getLong("gaptime"));
            long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
            if (millis > 0)
                handler.sendEmptyMessage(UPDATEBETTIME);
            else {
                if (HttpConnectUtil.isNetworkAvailable(BetDigitalBasic.this)) {
                    GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                    getLotteryInf.execute();
                }
                else
                    setGetTermFail();
            }
        }
        else {
            orgBetWay();
            if (HttpConnectUtil.isNetworkAvailable(BetDigitalBasic.this)) {
                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                getLotteryInf.execute();
            }
            else
                setGetTermFail();
        }
        getInitInf();
    }

    protected void defaultNum(String betNum) {
    };

    protected void getInitInf() {
        if (!shakeSensor)
            shakeLockView.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(BetDigitalBasic.this);
        progressDialog.setMessage("冷热门号码数据更新中..");
    }

    protected void getAnalyseData() {
        if (HttpConnectUtil.isNetworkAvailable(BetDigitalBasic.this)) {
            AnalyseHistoryTask history = new AnalyseHistoryTask();
            history.execute(kind);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this, inf);
        }
    }

    protected void flashBall() {
        for (int i = 1; i <= SHACKTIMES / 4 * 3; i++)
            handler.sendEmptyMessageDelayed(FLASHBALL, i * 50);
        for (int i = 1; i <= SHACKTIMES / 4; i++) {
            if (i == SHACKTIMES / 4)
                handler.sendEmptyMessageDelayed(RANDOMBALLS, i * i * 100 + SHACKTIMES / 4 * 3 * 50);
            else
                handler.sendEmptyMessageDelayed(FLASHBALL, i * i * 100 + SHACKTIMES / 4 * 3 * 50);
        }
        handler.sendEmptyMessageDelayed(SOUND, (SHACKTIMES / 4) * (SHACKTIMES / 4) * 100 + SHACKTIMES / 4 *
            3 * 50);
        handler.sendEmptyMessageDelayed(VIBRATE, (SHACKTIMES / 4) * (SHACKTIMES / 4) * 100 + SHACKTIMES / 4 *
            3 * 50);
    }

    // 初始化摇奖锁头
    protected void initShakeLock() {
        shakeLock = preferences.getBoolean("shake_lock", false);
        if (shakeLock) {
            shakeLockView.setBackgroundResource(R.drawable.bet_shake_close);
        }
        else {
            shakeLockView.setBackgroundResource(R.drawable.bet_shake);
        }
    }

    protected void initSubViews() {
        initShakeLock();
        showOmitNum = preferences.getBoolean("showOmitNum", false);
        showHotNum = preferences.getBoolean("showHotNum", false);
        if (showOmitNum) {
            omiImg.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            omiImg.setBackgroundResource(R.drawable.choosing_not_select);
        }
        if (showHotNum) {
            hotnumImg.setBackgroundResource(R.drawable.choosing_select);
            hotGrid.setVisibility(View.VISIBLE);
        }
        else {
            hotnumImg.setBackgroundResource(R.drawable.choosing_not_select);
            hotGrid.setVisibility(View.GONE);
        }
        refreshHotNumShow();
        btnShake.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shakeLock) {
                    shakeLockView.setBackgroundResource(R.drawable.bet_shake);
                    shakeLock = false;
                    if (!shaking) {
                        ViewUtil.showTipsToast(BetDigitalBasic.this, SHAKERANDOM);
                    }
                }
                else {
                    if (shaking) {
                        handler.removeMessages(FLASHBALL);
                        handler.removeMessages(SOUND);
                        handler.removeMessages(VIBRATE);
                        handler.removeMessages(RANDOMBALLS);
                        handler.sendEmptyMessage(SOUND);
                        handler.sendEmptyMessage(VIBRATE);
                    }
                    shakeLockView.setBackgroundResource(R.drawable.bet_shake_close);
                    shakeLock = true;
                }
                databaseData.putBoolean("shake_lock", shakeLock);
                databaseData.commit();
            }
        });
        imgShowBet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showBetWayPopupViews();
            }
        });
        shakeLock();
    }

    protected String getBetInf(long betNumber, long betMoney) {
        if (betNumber < 0 || betMoney < 0)
            return null;
        String betInf = null;
        betInf = betNumber + "注  <font color='red'>" + betMoney + "元</font>";
        return betInf;
    }

    private void startCountDown(String endtime, String systemtime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (endtime == null || systemtime == null)
            return;
        try {
            Date date1 = format1.parse(endtime);
            Date date2 = format1.parse(systemtime);
            endTimeMillis = date1.getTime();
            gapMillis = date2.getTime() - System.currentTimeMillis();
            long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
            if (millis >= 0) {
                countDownTime.setText(TimeUtils.getCountDownTime(millis));
                millis -= 1000;
                if (millis >= 0) {
                    handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                    ifBetEnd = false;
                }
                else {
                    betTimeInf.setVisibility(View.GONE);
                    countDownTime.setText(BETEND);
                    ifBetEnd = true;
                    betLastTime = null;
                }
            }
            else {
                betTimeInf.setVisibility(View.GONE);
                countDownTime.setText(BETEND);
                ifBetEnd = true;
            }
        }
        catch (NotFoundException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void orgBetWay() {
        // changed by vincent
        if (betWay == 0) {
            betWay = preferences.getInt("bet_way", 1);
            Logger.inf("vincent", "bet_way " + betWay);
        }

// if (betWay == 1)
// betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
// else if (betWay == 2)
// betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
// else if (betWay == 3)
// betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
// else if (betWay == 4)
// betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
        for (int i = 0; i < PLAYTYPE_STR.length; i++) {
            if (betWay == (i + 1)) {
                betWayBt.setText(PLAYTYPE_STR[i]);
                break;
            }
        }
        betWayBt.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showBetWayPopupViews();

                return false;
            }
        });
        betBt.setVisibility(View.GONE);
        betWayBt.setVisibility(View.VISIBLE);
        imgShowBet.setVisibility(View.VISIBLE);
    }

    @Override
    public void bet() {
        if (checkInput()) {
            if (code == null)
                return;
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("bet_id", id);
            bundle.putString("bet_kind", kind);
            bundle.putLong("endtime", endTimeMillis);
            bundle.putLong("gaptime", gapMillis);
            bundle.putString("bet_term", term);
            bundle.putBoolean("ifShowInf", ifShowInf);
            bundle.putString("awardtime", awardTime);
            bundle.putString("mode", mode);
            bundle.putString("bet_code", code);
            bundle.putString("bet_display_code", displayCode);
            bundle.putString("luckynum", luckynum);
            bundle.putString("mstar", mstar);
            bundle.putString("today_lucky", todayluck);
            bundle.putLong("bet_money", betMoney);
            bundle.putLong("bet_money_sub", betMoneySub);
            bundle.putBoolean("bet_is_old", isOld);
            // add by vincent
            bundle.putBoolean("bet_is_unite", doUnite);
            bundle.putBoolean("if_bet_end", ifBetEnd);

            extraBundle(bundle);
            if (betWay == 1) {
                if (appState.getUsername() == null) {
                    bundle.putBoolean("fromBet", true);
                    bundle.putString("forwardFlag", "收银台");
                    bundle.putString("about", "left");
                    bundle.putBoolean("ifStartSelf", false);
                    bundle.putBoolean("is_continue_pass", true);
                    bundle.putString("class_name", BetPayDigital.class.getName());
                    intent.putExtras(bundle);
                    intent.setClass(BetDigitalBasic.this, Login.class);
                }
                else {
                    intent.putExtras(bundle);
                    intent.setClass(BetDigitalBasic.this, BetPayDigital.class);
                }
                startActivityForResult(intent, 1);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(BetDigitalBasic.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
            else if (betWay == 2 || betWay == 3) {// changed by vincent
                if (betWay == 2) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }
                intent.putExtras(bundle);
                for (int i = 0; i < LotteryUtils.LOTTERY_ID.length; i++) {
                    if (kind.equals(LotteryUtils.LOTTERY_ID[i])) {
                        intent.setClass(BetDigitalBasic.this, LotteryUtils.lotteryclass[i]);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                        break;
                    }
                }
                finish();
            }
            // add by vincent 赠送彩票
            else if (betWay == 4) {
                if (appState.getUsername() == null) {
                    bundle.putString("forwardFlag", "送彩票");
                    bundle.putString("about", "left");
                    bundle.putBoolean("ifStartSelf", false);
                    bundle.putBoolean("is_continue_pass", true);
                    bundle.putString("class_name", GiveLottery.class.getName());
                    intent.putExtras(bundle);
                    intent.setClass(BetDigitalBasic.this, Login.class);
                }
                else {
                    intent.putExtras(bundle);
                    intent.setClass(BetDigitalBasic.this, GiveLottery.class);
                }
                startActivityForResult(intent, 1);
            }
        }
    }

    protected void extraBundle(Bundle bundle) {
    }

    protected boolean checkInput() {
        return false;
    };

    @Override
    public void clearBalls() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: v2 bet clear");
        map.put("more_inf", "bet clear " + kind);
        String eventName = "v2 bet clear";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_clear";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind);
    }

    @Override
    public void randomBalls() {
    }

    @Override
    public void randomBallsShow() {
        randomBalls();
        enableBetBtn();
        enableClearBtn();
    }

    abstract public void setKind();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (img_help_info_bg.getVisibility() == View.GONE) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        else {
            img_help_info_bg.setVisibility(View.GONE);
            ifShowImgHelp = false;
            databaseData.putBoolean("if_show_img_help", ifShowImgHelp);
            databaseData.commit();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "玩法说明").setIcon(R.drawable.icon_user_center);
// Boolean hasLucky = false;
// for (int i = 0; i < 6; i++)
// if (kind.equals(LotteryUtils.lotteryEnglishNames[i]))
// hasLucky = true;
// if (hasLucky)
// menu.add(0, 2, 0, "紫微选号").setIcon(R.drawable.icon_help);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected void goRules() {
    }

    protected void goSelectLuckyBall() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Map<String, String> map = new HashMap<String, String>();
        String eventName;
        switch (item.getItemId()) {
            case 1:
                map.put("inf", "username [" + appState.getUsername() + "]: bet click help");
                map.put("more_inf", "bet click help of " + kind);
                map.put("extra_inf", "bet click help from menu");
                eventName = "v2 bet click help";
                FlurryAgent.onEvent(eventName, map);
                besttoneEventCommint(eventName);
                String eventNameMob = "bet_help";
                MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind);
                goRules();
                return true;
// case 2:
// map.put("inf", "username [" + appState.getUsername() + "]: bet click divine ball");
// map.put("more_inf", "bet click divine ball of " + kind);
// map.put("extra_inf", "bet click divine ball from menu");
// eventName = "v2 bet click divine ball";
// FlurryAgent.onEvent(eventName, map);
// besttoneEventCommint(eventName);
// String eventNameMobNew = "bet_divine";
// MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMobNew, kind);
// goSelectLuckyBall();
// return true;
        }
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        sm.unregisterListener(acceleromererListener, acceleromererSensor);
    }

    protected void showBetWayPopupViews() {
        boolean hasUnite = false;
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // changed by vincent
        View waySwitchLayout = null;
        TextView betUnite = null;
        // add by vincent 赠送彩票
        TextView betGivelottery = null;

        // 隐藏合买
// waySwitchLayout = mLayoutInflater.inflate(R.layout.popup_bet_way_swtich, null);
        for (int i = 0; i < LotteryUtils.uniteLotteryEnglishNames.length; i++) {
            if (kind.equals(LotteryUtils.uniteLotteryEnglishNames[i])) {
                hasUnite = true;
                waySwitchLayout =
                    mLayoutInflater.inflate(R.layout.unite_popup_bet_way_swtich_givelottery, null);
                isUnite = true;
                betUnite = (TextView) waySwitchLayout.findViewById(R.id.bet_unite);
                betUnite.setOnClickListener(new TextView.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (betWay != 3) {
                            betWay = 3;
                            doUnite = true;
// databaseData.putInt("bet_way", betWay);
// databaseData.commit();
                            betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
                        }
                        betWayPopupWindow.dismiss();
                    }
                });
                betGivelottery = (TextView) waySwitchLayout.findViewById(R.id.bet_givelottery);
                betGivelottery.setOnClickListener(new TextView.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (betWay != 4) {
                            betWay = 4;
                            doUnite = false;
// databaseData.putInt("bet_way", betWay);
// databaseData.commit();
                            betWayBt.setText(PLAYTYPE_STR[betWay - 1]);
                        }
                        betWayPopupWindow.dismiss();
                    }
                });
                break;
            }
        }

        if (!isUnite) {
            waySwitchLayout = mLayoutInflater.inflate(R.layout.popup_bet_way_swtich, null);
        }

        TextView betDirectly = (TextView) waySwitchLayout.findViewById(R.id.bet_directly);
        betDirectly.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (betWay != 1) {
                    betWay = 1;
                    doUnite = false;
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
                    doUnite = false;
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
            betDirectly.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            betDirectly.setTextColor(getResources().getColor(R.color.white));
            betAdd.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betAdd.setTextColor(getResources().getColor(R.color.dark_purple));
            if (betUnite != null) {
                betUnite.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                betUnite.setTextColor(getResources().getColor(R.color.dark_purple));
                betGivelottery.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                betGivelottery.setTextColor(getResources().getColor(R.color.dark_purple));
            }
        }
        else if (betWay == 2) {
            betAdd.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            betAdd.setTextColor(getResources().getColor(R.color.white));
            betDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            if (betUnite != null) {
                betUnite.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                betUnite.setTextColor(getResources().getColor(R.color.dark_purple));
                betGivelottery.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                betGivelottery.setTextColor(getResources().getColor(R.color.dark_purple));
            }
        }
        else if (betWay == 3) {
            betUnite.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            betUnite.setTextColor(getResources().getColor(R.color.white));
            betDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            betAdd.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betAdd.setTextColor(getResources().getColor(R.color.dark_purple));
            betGivelottery.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betGivelottery.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        // 赠送彩票
        else if (betWay == 4) {
            betDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
            betAdd.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betAdd.setTextColor(getResources().getColor(R.color.dark_purple));
            if (betUnite != null) {
                betUnite.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                betUnite.setTextColor(getResources().getColor(R.color.dark_purple));
                betGivelottery.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                betGivelottery.setTextColor(getResources().getColor(R.color.white));
            }
        }
        // TODO 加入合买后更改显示位置
        if (hasUnite) {
            betWayPopupWindow.showAsDropDown(betWayBt, 0, -6 * betWayBt.getHeight());
        }
        else
            betWayPopupWindow.showAsDropDown(betWayBt, 0, -4 * betWayBt.getHeight());

        /*
         * if (hasUnite) { betWayPopupWindow.showAsDropDown(betWayBt, 0, -4 * betWayBt.getHeight() - 30); }
         * else betWayPopupWindow.showAsDropDown(betWayBt, 0, -3 * betWayBt.getHeight() - 30);
         */

    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "lottery_base_info");
        parameter.put("pid", LotteryUtils.getPid(this));
        parameter.put("lottery_id", kind);
        parameter.put("new", "1");
        return parameter;
    }

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initFastHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001070");
            parameter.put("pid", LotteryUtils.getPid(BetDigitalBasic.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetDigitalBasic.this);
            String json = null;
            try {
                isFrequent = LotteryUtils.isFrequentLottery(kind);
                if (isFrequent) {
                    json = connectNet.getJsonGet(2, false, initFastHashMap());
                }
                else {
                    json = connectNet.getJsonGet(1, false, initHashMap());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (json != null) {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    String response = ja.getData(json, "response_data");
                    String getTerm;
                    String systemtime;
                    // 旧接口用term、systemtime，新接口用new_term、datetime，为了兼容
                    if (isFrequent) {
                        getTerm = ja.getData(response, "newterm");
                        systemtime = ja.getData(json, "datetime");
                    }
                    else {
                        getTerm = ja.getData(response, "term");
                        systemtime = ja.getData(response, "systemtime");
                    }
                    String endtime = ja.getData(response, "endtime");
                    String awardtime = ja.getData(response, "awardtime");
                    if (getTerm != null && systemtime != null && endtime != null && !endtime.equals("") &&
                        !systemtime.equals("")) {
                        countDownTime.setVisibility(View.VISIBLE);
                        term = getTerm;
                        betTerm.setText(" " + getTerm + "期");
                        GetServerTime time = new GetServerTime(BetDigitalBasic.this);
                        OperateInfUtils.refreshTime(BetDigitalBasic.this, time.formatTime(systemtime));
                        startCountDown(endtime, systemtime);
                        awardTime = awardtime;
                    }
                    else {
                        setGetTermEnd();
                    }
                }
                else
                    setGetTermFail();
            }
            else {
                setGetTermFail();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            betTerm.setText(GETTINGBETIN);
            betTimeInf.setVisibility(View.GONE);
            countDownTime.setVisibility(View.GONE);
        }
    }

    protected void setGetTermFail() {
// title.setVisibility(View.GONE);
        betTerm.setText(GETBETINFFAIL);
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.GONE);
    }

    protected void setGetTermEnd() {
        // title.setVisibility(View.GONE);
        betTerm.setText(BETEND);
        ifBetEnd = true;
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.GONE);
    }

    abstract protected void analyseData(String json);

    public class AnalyseHistoryTask
        extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200") || searchType != 0) {
// DeductPointTask task = new DeductPointTask();
// task.execute(DEDUCTSCORE[searchType], 0);
                }
            }
            analyseData(json);
        }

        @Override
        protected String doInBackground(String... kind) {
            ConnectService connectNet = new ConnectService(BetDigitalBasic.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, initHashMap(kind[0]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private HashMap<String, String> initHashMap(String kind)
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_analysis");
            parameter.put("pid", LotteryUtils.getPid(BetDigitalBasic.this));
            parameter.put("lottery_id", kind);
            parameter.put("phone", appState.getUsername());
            parameter.put("size", "" + SEARCHNUM[searchType]);
            return parameter;
        }
    }

    protected void omitData(String json) {
    };

    public class OmitHistoryTask
        extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String json) {
            omitData(json);
        }

        @Override
        protected String doInBackground(String... kind) {
            ConnectService connectNet = new ConnectService(BetDigitalBasic.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, false, initHashMap(kind[0]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private HashMap<String, String> initHashMap(String kind)
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2008020");
            parameter.put("pid", LotteryUtils.getPid(BetDigitalBasic.this));
            parameter.put("lottery_id", kind);
            return parameter;
        }
    }

    String q_sno = "";
    StringBuffer q_snum = new StringBuffer();

    protected String q_codeSwitch(String sourceQ_code) {
        if (!sourceQ_code.equals("")) {
            String[] temp = sourceQ_code.split("\\|");
            String[] n_no = temp[0].split(",");
            StringBuffer n_number = new StringBuffer();
            for (int i = 0; i < n_no.length; i++) {
                if (!n_no[i].equals("")) {
                    int a = Integer.valueOf(n_no[i]);
                    n_no[i] = String.valueOf(a);
                    n_number.append(n_no[i] + ",");
                }
            }
            StringBuffer s_number = new StringBuffer();
            if (temp.length > 1) {
                String[] s_no = temp[1].split(",");
                q_snum.delete(0, q_snum.length());
                for (int i = 0; i < s_no.length; i++) {
                    int a = Integer.valueOf(s_no[i]);
                    if (kind.equals("3d") || kind.equals("qlc") || kind.equals("swxw") || kind.equals("ssl"))
                        s_no[i] = String.valueOf(a);
                    q_snum.append(String.valueOf(a));
                    q_snum.append(",");
                    s_number.append(s_no[i] + ",");
                }
                q_snum.delete(q_snum.length() - 1, q_snum.length());
                q_sno = q_snum.toString();
            }
            if (temp.length == 1) {
                if (kind.equals("dfljy")) {
                    try {
                        requestDFLJYCode = n_number.deleteCharAt(n_number.length() - 1).toString();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    // setMatchCode();
                    return sourceQ_code;
                }
                else if (n_number.length() > 0) {
                    String q_nno = n_number.deleteCharAt(n_number.length() - 1).toString();
                    q_sno = q_nno;
                    // setMatchCode();
                    return sourceQ_code = q_nno;
                }
            }
            else {
                if (n_number.length() > 0 && s_number.length() > 0)
                    if (kind.equals("dfljy")) {
                        requestDFLJYCode =
                            n_number.deleteCharAt(n_number.length() - 1).toString() + "|" +
                                s_number.deleteCharAt(s_number.length() - 1).toString();
                        // setMatchCode();
                        return sourceQ_code;
                    }
                    else {
                        String q_nno = n_number.deleteCharAt(n_number.length() - 1).toString();
                        q_sno = q_nno + "|" + q_sno;
                        // setMatchCode();
                        return sourceQ_code =
                            q_nno + "|" + s_number.deleteCharAt(s_number.length() - 1).toString();
                    }
            }
            int lenth = getSourcesCodeLength();
            if (kind.equals("dfljy")) {
                requestDFLJYCode =
                    sourceQ_code.substring(sourceQ_code.length() - lenth, sourceQ_code.length());
                // setMatchCode();
                return sourceQ_code;
            }
            else if (kind.equals("dlt") || kind.equals("ssq")) {
                q_sno = "|" + q_sno;
                // setMatchCode();
                return sourceQ_code;
            }
            else {
                // setMatchCode();
                return sourceQ_code.substring(sourceQ_code.length() - lenth, sourceQ_code.length());
            }
        }
        else {
            return "";
        }
    }

    private int getSourcesCodeLength() {
        if (kind.equals("ssq")) {
            if (isSingle == false)
                return 3;
            else
                return 2;
        }
        if (kind.equals("dfljy"))
            return 2;
        return 0;
    }

    protected String setMatchCode() {
        return q_sno;
    }

    abstract public String getQ_code();

    abstract protected void searchLuckyNum();

    protected void showLuckyNum() {
    };

    private void forwardTologin() {
        ViewUtil.showTipsToast(this, "请登录。");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", "查幸运号");
        bundle.putString("about", "left");
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
        intent.setClass(BetDigitalBasic.this, Login.class);
// intent.setClass(BetDigitalBasic.this, StartUp.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet");
        map.put("more_inf", "open bet " + kind);
        String eventName = "v2 open bet";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void setButtonClickListener(int buttonId) {
        if (buttonId == R.id.item_first_layout) {
            searchLuckyType = 1;
            term_num = 20;
            searchLuckyNum();
            showLuckyNum();
        }
        else if (buttonId == R.id.item_second_layout) {
            if (appState.getSessionid() != null) {
                searchLuckyType = 2;
                term_num = 50;
                searchLuckyNum();
                showLuckyNum();
            }
            else {
                forwardTologin();
            }

        }
        else if (buttonId == R.id.item_third_layout) {
            if (appState.getSessionid() != null) {
                searchLuckyType = 3;
                term_num = 100;
                searchLuckyNum();
                showLuckyNum();
            }
            else {
                forwardTologin();
            }
        }
        else if (buttonId == R.id.item_forth_layout) {
            if (appState.getSessionid() != null) {
                searchLuckyType = 4;
                term_num = 200;
                searchLuckyNum();
                showLuckyNum();
            }
            else {
                forwardTologin();
            }
        }
        else if (buttonId == R.id.item_fifth_layout) {
            if (appState.getSessionid() != null) {
                searchLuckyType = 5;
                term_num = 300;
                searchLuckyNum();
                showLuckyNum();
            }
            else {
                forwardTologin();
            }
        }
        else if (buttonId == R.id.item_sixth_layout) {
            if (appState.getSessionid() != null) {
                searchLuckyType = 6;
                term_num = 500;
                searchLuckyNum();
                showLuckyNum();
            }
            else {
                forwardTologin();
            }
        }
    }

    // 重置双色球投注号码格式信息显示
    protected void initDispalyInf() {

    }

    // 显示选择号码数，比如红球0/1个
    protected void showBallNum() {

    }

    // 上传用户点击随机按钮事件
    private void submitStatisticRandomInf() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: v2 bet random");
        randomTimes++;
        map.put("extra_inf", "bet random " + randomTimes);
        String eventName = "v2 bet random";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_random";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind, randomTimes);
    }

    // 上传用户点击帮助按钮事件
    private void submitStatisticClickRules() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click help");
        map.put("more_inf", "bet click help of " + kind);
        map.put("extra_inf", "bet click help from top");
        String eventName = "v2 bet click help";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_help";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind);
    }

    // 上传用户点击紫薇选号按钮事件
    private void submitStatisticClickDivineBall() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click divine ball");
        map.put("more_inf", "bet click divine ball of " + kind);
        map.put("extra_inf", "bet click divine ball from top");
        String eventName = "v2 bet click divine ball";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        Map<String, String> map1 = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "divine");
        map.put("extra_inf", kind);
        String eventName1 = "bet tools";
        FlurryAgent.onEvent(eventName1, map1);
        String eventNameMob = "bet_divine";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind);
        String eventNameMob1 = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob1, "divine");
    }

    // 上传用户点击号码分析按钮事件
    private void submitStatisticClickNumAnalyseWhole() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "num analyse");
        map.put("extra_inf", kind);
        String eventName = "bet tools";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, "num_analyse");
    }

    // 上传用户点击号码分析冷热门按钮事件
    private void submitStatisticClickNumAnalyseHot() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "num analyse hot");
        map.put("extra_inf", kind);
        String eventName = "bet tools";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, "num_analyse_hot");
    }

    // 上传用户点击号码分析遗漏按钮事件
    private void submitStatisticClickNumAnalyseOmit() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "num analyse omit");
        map.put("extra_inf", kind);
        String eventName = "bet tools";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, "num_analyse_omit");
    }

    // 上传用户点击清空按钮事件
    private void submitStatisticClearBall() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet clear ball");
        map.put("more_inf", "bet clear of " + kind);
        String eventName = "v2 bet clear";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_clear";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, kind);
    }

    // 上传用户点击号码统计按钮事件
    private void submitStatisticClickStatistic() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "num statistic");
        map.put("extra_inf", kind);
        String eventName = "bet tools";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob, "num_statistic");
    }

    // 上传用户点击奖金计算器按钮事件
    protected void submitStatisticClickAwardCount() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: v2 bet calculator dlt");
        map.put("more_inf", "bet calculator " + kind);
        String eventName = "v2 bet calculator";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_click_calculator";
        MobclickAgent.onEvent(this, eventNameMob, kind);

        Map<String, String> map1 = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click bet tools");
        map.put("more_inf", "awardcount");
        map.put("extra_inf", kind);
        String eventName1 = "bet tools";
        FlurryAgent.onEvent(eventName1, map1);
        besttoneEventCommint(eventName);
        String eventNameMob1 = "bet_tools";
        MobclickAgent.onEvent(BetDigitalBasic.this, eventNameMob1, "award_count");
    }

    @Override
    public void onClick(View v) {
        // 进行点击事件时把popMenu消失
        if (v.getId() == R.id.bet_clear_button) {
            popMenu.dismiss();
            clear();
        }
        else if (v.getId() == R.id.bet_button) {
            popMenu.dismiss();
            bet();
        }
        else if (v.getId() == R.id.bet_way_button) {
            popMenu.dismiss();
            bet();
        }
        else if (v.getId() == R.id.bet_random_button) {
            popMenu.dismiss();
            submitStatisticRandomInf();
            mode = "1001";
            randomBallsShow();
        }
        else if (v.getId() == R.id.bet_help_lin) {
            popMenu.dismiss();
            submitStatisticClickRules();
            goRules();
        }
        else if (v.getId() == R.id.analyse_tips) {
            popMenu.dismiss();
// popMenu.showAsDropDown(analyseTips);
            popMenu.showAsDropDown(analyseTips, -analyseTips.getRight() + 130, 0);
        }
        else if (v.getId() == R.id.layout_num_analyse) {
            popMenu.dismiss();
            submitStatisticClickNumAnalyseWhole();
            searchLuckyType = 1;
            term_num = 20;
            searchLuckyNum();
            showLuckyNum();
            popMenu.dismiss();
        }
        else if (v.getId() == R.id.layout_selected_lucky_ball) {
            popMenu.dismiss();
            submitStatisticClickDivineBall();
            popMenu.dismiss();
            goSelectLuckyBall();
        }
        else if (v.getId() == R.id.layout_hot_num_statistics) {
            popMenu.dismiss();
            submitStatisticClickNumAnalyseHot();
            popMenu.dismiss();
// goStatisticsSetting("hot");
            if (showHotNum) {
                showHotNum = false;
                hotnumImg.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                showHotNum = true;
                hotnumImg.setBackgroundResource(R.drawable.choosing_select);
            }

            databaseData.putBoolean("showHotNum", showHotNum);
            databaseData.commit();
            refreshHotNumShow();
        }
        else if (v.getId() == R.id.re_hotnum) {
            submitStatisticClickNumAnalyseHot();
            if (showHotNum) {
                showHotNum = false;
                hotnumImg.setBackgroundResource(R.drawable.choosing_not_select);
                hotGrid.setVisibility(View.GONE);
                popMenu.dismiss();
                refreshHotNumShow();
            }
            else {
                showHotNum = true;
                hotnumImg.setBackgroundResource(R.drawable.choosing_select);
                hotGrid.setVisibility(View.VISIBLE);
                getAnalyseData();
            }

            databaseData.putBoolean("showHotNum", showHotNum);
            databaseData.commit();
        }
        else if (v.getId() == R.id.layout_omit_statistics || v.getId() == R.id.re_omit) {
            popMenu.dismiss();
            submitStatisticClickNumAnalyseOmit();
            popMenu.dismiss();
// goStatisticsSetting("omit");
            if (showOmitNum) {
                showOmitNum = false;
                omiImg.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                showOmitNum = true;
                omiImg.setBackgroundResource(R.drawable.choosing_select);
            }
            databaseData.putBoolean("showOmitNum", showOmitNum);
            databaseData.commit();
            refreshHotNumShow();
        }
        else if (v.getId() == R.id.layout_lottery_chart) {
            popMenu.dismiss();
            submitStatisticClickNumAnalyseOmit();
            popMenu.dismiss();
        }
    }

    protected void clear() {
        submitStatisticClearBall();
        mode = "0000";
        luckynum = null;
        mstar = null;
        todayluck = null;
        clearBalls();
        moneyInf.setText(Html.fromHtml(MONEY_TIPS));
        initDispalyInf();
        showBallNum();
    }

    // type为hot代表冷热门号码显示功能，type为omit代表号码遗漏功能
    private void goStatisticsSetting(String type) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        if (type.equals("hot")) {
            bundle.putInt("index", searchType);
            bundle.putBoolean("show_hot", showHotNum);
        }
        else if (type.equals("omit")) {
            bundle.putBoolean("show_omit", showOmitNum);
        }
        intent.putExtras(bundle);
        intent.setClass(BetDigitalBasic.this, LotteryStatisticsSetting.class);
        startActivityForResult(intent, 2);
    }

    // 显示数据统计信息，如冷热门号码
    protected void refreshHotNumShow() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(BetDigitalBasic.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                         R.anim.push_to_left_out);
                }
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                String type = b.getString("type");
                if (type.equals("hot")) {
                    int index = b.getInt("index");
                    if (index != searchType) {
                        searchType = index;
                        // statisticsText.setText("统计显示(" + SEARCHNUM[index] + "期)");
                        getAnalyseData();
                    }
                    boolean showHot = b.getBoolean("show_hot");
                    if (showHot != showHotNum) {
                        showHotNum = showHot;
// DeductPointTask task = new DeductPointTask();
// task.execute(3, 1);
                        refreshHotNumShow();
                    }
                }
                else {
                    boolean showOmit = b.getBoolean("show_omit");
                    if (showOmit != showOmitNum) {
                        showOmitNum = showOmit;
// DeductPointTask task = new DeductPointTask();
// task.execute(3, 2);
                        refreshHotNumShow();
                    }
                }
            }
        }
    }

// public class DeductPointTask
// extends AsyncTask<Integer, Void, String> {
//
// @Override
// protected void onPostExecute(String json) {
// if (json != null) {
// JsonAnalyse ja = new JsonAnalyse();
// // get the status of the http data
// String status = ja.getStatus(json);
// if (status.equals("200")) {
// // ViewUtil.showTipsToast(this,"成功了");
// }
// }
// }
//
// @Override
// protected String doInBackground(Integer... num) {
// ConnectService connectNet = new ConnectService(BetDigitalBasic.this);
// String json = null;
// try {
// json = connectNet.getJsonGet(3, true, initHashMap(num[0], num[1]));
// }
// catch (Exception e) {
// e.printStackTrace();
// }
// return json;
// }
//
// private HashMap<String, String> initHashMap(int score, int description)
// throws Exception {
// HashMap<String, String> parameter = new HashMap<String, String>();
// parameter.put("service", "2002121");
// parameter.put("pid", LotteryUtils.getPid(BetDigitalBasic.this));
// parameter.put("phone", appState.getUsername());
// parameter.put("type", "8001");
// parameter.put("score", "" + score);
// parameter.put("description", DEDUCTDES[description]);
// return parameter;
// }
// }

    @Override
    public void touchMove(int move) {
        scrollView.scrollBy(0, move);
    }

    @Override
    protected void submitData() {
        setKind();
        String eventName = "open_bet";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }

}
