package com.haozan.caipiao.activity.bet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.BetFootBallTeamListShow;
import com.haozan.caipiao.activity.Compass;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PredicateLayout;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;
import com.umeng.analytics.MobclickAgent;

public class BetPay
    extends BasicActivity
    implements OnClickListener, OnGetUserInfListener {

    private static final String BETEND = "投注已截止";
    protected static final int UPDATEBETTIME = 0;
// private static final int REQUESTLOCATION = 1;
    private static final int GETNEWINF = 2;

    private boolean isFrequent;
    private Boolean isPause = false;
    // 判断分享内容是否被编辑
    private boolean isEdited = false;
    // 判断是否分享内容改变非用户编辑
    private boolean changeShareContent = false;
    private Boolean canBet = true;
    protected String kind;
    protected String kindChineseName = null;
    protected String term;
    protected boolean ifShowInf = false;
    protected String code;
    protected String mode;
    protected String displayCode;
    protected String luckyNum;
    protected String mStar;
    protected String todayLucky;
    protected String address = null;
// private int requestLocationTimes = 1;
    protected int timesNum = 1;
    protected int followNum = 1;
    protected int timesNumWheel = 1;
    protected int followNumWheel = 1;
    protected long money;
    protected long subMoney = 0;
    protected long allMoney = 0;// 为money与subMoney之和，吉林快三时用
    protected long wholeMoney;
    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    private float lastOrentation = 0.0f;
    protected float presentOrentation = -1.0f;
    protected double latitude;
    protected double longitude;
    private boolean scrolling = false;
    private String direction = null;
    private StringBuilder betLastTime;
    private Location location;

    private Boolean locationCommit;
    protected Boolean betInfTranspond;
    private Boolean showBetInf = false;

    private TextView title;
    protected TextView betTimeInf;
    protected TextView countDownTime;
    private LinearLayout accountLayout;
    private TextView account;
    private TextView accountStatus;
    private ImageView line;
    protected TextView lotteryNum;
    protected TextView warningTips;

    protected RelativeLayout directionLayout;
// private TextView directionInf;
    private TextView toCompass;
    private TextView locationInf;
    private RelativeLayout locationTipsLayout;
    private RelativeLayout locationLayout;
    private ImageView closeMore;
    private TextView showMore;
    private ImageView locationSelect;
    private RelativeLayout transpondLayout;
    private LinearLayout shareToGardenLayout;
    private ImageView transpondSelect;
    protected LinearLayout bottomLayout;
    protected TextView betMoney;
    private Boolean readProtocol = true;
    private ImageView check;
    private TextView protocol;
    private Boolean toRechage = false;
    protected boolean ifBetEnd;
    protected LinearLayout sportsBunchLayout;
    Button submit;

    protected TextView betNum;
    protected TextView lotteryCodetitle;

    protected LinearLayout wheelLayout;
    protected TextView wheelBetMoney;
    private TextView cancle;
    private TextView makeSure;
    protected LinearLayout layoutWholeWheel;
    protected WheelView timesWheel;
    protected WheelView followWheel;
    private ImageView stopPursuit;
    private ImageView superaddition;
    protected RelativeLayout stopPursuitLayout;
    protected RelativeLayout superadditionLayout;
    protected LinearLayout layoutSingleWheel;
    protected WheelView timesWheel2;
    protected Boolean isStopPursuit = false;
    protected Boolean isSuperaddion = false;
    private StringBuilder shareContent;
    protected RelativeLayout zucaiNumContainer;
    protected PredicateLayout zucaiPredicate;

// private WheelView followWheel;
    protected SensorManager sm;
    protected Sensor orienttationSensor;
    protected SensorEventListener orienttationListener;
    private UserInfTask userInfTask;
    protected EditText editShareContent;
    protected EditText editShare;// 用户自己输入的内容
    protected TextView sfcBetDetail;

    // map variable
    private LocationManager locationManager = null;
    private LocationListener mLocationListener = null;
    private static final long mLocationUpdateMinTime = 1000;
    private static final float mLocationUpdateMinDistance = 10.0f;
    public boolean ifRefreshMoney = false;
// protected String[] timesArray = new String[111];

    ProgressDialog progress;
    protected CustomDialog dlgTermChangeTips;

    protected UEDataAnalyse mUploadRequestTime;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEBETTIME:
                    betLastTime.delete(0, betLastTime.length());
                    long millis = endTimeMillis - gapMillis - System.currentTimeMillis();
                    countDownTime.setText(TimeUtils.getCountDownTime(millis));
                    millis -= 1000;
                    if (millis >= 0)
                        handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                    else {
                        ifShowInf = true;
                        ifBetEnd = true;
                        stopBet();
                        countDownTime.setText(BETEND);
                        handler.sendEmptyMessageDelayed(GETNEWINF, 5000);
                    }
                    break;
                case GETNEWINF:
                    if (HttpConnectUtil.isNetworkAvailable(BetPay.this)) {
                        GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                        getLotteryInf.execute();
                    }
                    else
                        setGetTermFail();
                    break;
            }
        }
    };

    protected void stopBet() {
        betTimeInf.setVisibility(View.GONE);
        submit.setText("投注截止");
        disableBet();
    }

    protected void disableBet() {
        submit.setEnabled(false);
    }

    protected void enableBet() {
        submit.setEnabled(true);
    }

    private void startBet() {
        submit.setText("  付 款  ");
        enableBet();
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
            millis = date1.getTime() - date2.getTime();
            if (millis >= 0) {
                startBet();
                countDownTime.setText(TimeUtils.getCountDownTime(millis));
                millis -= 1000;
                if (millis >= 0)
                    handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                else {
                    betTimeInf.setVisibility(View.GONE);
                    countDownTime.setText(BETEND);
                }
            }
            else {
                stopBet();
            }
        }
        catch (NotFoundException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_pay);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        mUploadRequestTime = new UEDataAnalyse(this);

        betLastTime = new StringBuilder();
        shareContent = new StringBuilder();
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("bet_kind");
        money = bundle.getLong("bet_money");
        mode = bundle.getString("mode");
        term = bundle.getString("bet_term");
        ifShowInf = bundle.getBoolean("ifShowInf");
        ifBetEnd = bundle.getBoolean("if_bet_end");
        displayCode = bundle.getString("bet_display_code");
        if (kind.equals("jlk3")) {
            if (displayCode.indexOf(";") > 0) {
                subMoney = bundle.getLong("bet_money_sub");
            }
        }
        allMoney = money + subMoney;
    }

    private void setupViews() {
        setupWholeViews();
        setupWheelViews();
    }

    private void setupWholeViews() {
        title = (TextView) this.findViewById(R.id.title);
        betTimeInf = (TextView) this.findViewById(R.id.bet_time_inf);// 离截止。。。
        countDownTime = (TextView) this.findViewById(R.id.bet_countdown_time);
        accountLayout = (LinearLayout) this.findViewById(R.id.layout_account);
// accountLayout.setOnClickListener(this);
        betMoney = (TextView) this.findViewById(R.id.order_money);
        account = (TextView) this.findViewById(R.id.account);
        accountStatus = (TextView) this.findViewById(R.id.account_status_tips);
        line = (ImageView) this.findViewById(R.id.bet_line);
        lotteryNum = (TextView) this.findViewById(R.id.lottery_num);
        warningTips = (TextView) findViewById(R.id.bet_warning_tips);

        directionLayout = (RelativeLayout) this.findViewById(R.id.layout_direction);
        locationInf = (TextView) this.findViewById(R.id.location_inf);
// directionInf = (TextView) this.findViewById(R.id.direction_inf);
        toCompass = (Button) this.findViewById(R.id.bet_to_compass);
        toCompass.setOnClickListener(this);
        locationTipsLayout = (RelativeLayout) this.findViewById(R.id.layout_location_operation);
        locationLayout = (RelativeLayout) this.findViewById(R.id.layout_location);
        locationLayout.setOnClickListener(this);
        closeMore = (ImageView) this.findViewById(R.id.more_inf_close);
        closeMore.setOnClickListener(this);
        showMore = (TextView) this.findViewById(R.id.show_more_inf);
        showMore.setOnClickListener(this);
        locationSelect = (ImageView) this.findViewById(R.id.location_select);
        transpondLayout = (RelativeLayout) this.findViewById(R.id.layout_transpond);
        transpondLayout.setOnClickListener(this);
        transpondSelect = (ImageView) this.findViewById(R.id.transpond_select);
        shareToGardenLayout = (LinearLayout) this.findViewById(R.id.layout_share_to_garden);
        betNum = (TextView) this.findViewById(R.id.bet_times_pursuit_num);
        betNum.setOnClickListener(this);
        bottomLayout = (LinearLayout) this.findViewById(R.id.bottom);
        check = (ImageView) this.findViewById(R.id.bet_check);
        check.setOnClickListener(this);
        protocol = (TextView) this.findViewById(R.id.bet_protocol);
        protocol.setText(Html.fromHtml("<u>" + "同意委托投注协议" + "</u>"));
        protocol.setOnClickListener(this);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        sportsBunchLayout = (LinearLayout) this.findViewById(R.id.layout_sports_extra_inf);

        lotteryCodetitle = (TextView) findViewById(R.id.lottery_code_title);
        editShareContent = (EditText) findViewById(R.id.edit_share_content);
        editShare = (EditText) findViewById(R.id.edit_share);
        editShareContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 判断是否被编辑过，没有被编辑过的话，如果是用户编辑的行为，设置被用户编辑过，不再改变分享内容
                if (!isEdited) {
                    if (changeShareContent == false) {
                        isEdited = true;
                    }
                    else {
                        changeShareContent = false;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        sfcBetDetail = (TextView) findViewById(R.id.foot_ball_bet_detail);
        sfcBetDetail.setOnClickListener(this);
        zucaiNumContainer = (RelativeLayout) findViewById(R.id.zucai_lottery_num_container);
        zucaiPredicate = (PredicateLayout) findViewById(R.id.zucai_lottery_num_container_predicate);
    }

    protected int getTerms(String kind) {
        if (kind.equals("jx11x5") || kind.equals("cqssc") || kind.equals("hnklsf") || kind.equals("ssl") ||
            kind.equals("jxssc") || kind.equals("jlk3"))
            return 350;
        return 100;
    }

    private void setupWheelViews() {
// initTimesArray();
        wheelBetMoney = (TextView) this.findViewById(R.id.wheel_order_money);
        stopPursuit = (ImageView) this.findViewById(R.id.stop_pursuit_select);
        stopPursuitLayout = (RelativeLayout) this.findViewById(R.id.stop_pursuit_layout);// 中奖停止追号
        stopPursuitLayout.setOnClickListener(this);
        superaddition = (ImageView) this.findViewById(R.id.superaddition_select);
        superadditionLayout = (RelativeLayout) this.findViewById(R.id.superaddition_layout);
        superadditionLayout.setOnClickListener(this);
        wheelLayout = (LinearLayout) this.findViewById(R.id.wheel_layout);
        layoutWholeWheel = (LinearLayout) this.findViewById(R.id.layout_whole_wheel);
        timesWheel = (WheelView) this.findViewById(R.id.times_choose);
        timesWheel.setNormalTx("倍");
// timesWheel.setViewAdapter(new DateArrayAdapter(this, timesArray, 0));
        timesWheel.setViewAdapter(new DateNumericAdapter(this, 1, 99, 0));
        timesWheel.setCurrentItem(0);
        timesWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateDoubleText(timesWheel);
                }
            }
        });
        followWheel = (WheelView) this.findViewById(R.id.follow_choose);
        followWheel.setNormalTx("期");
        followWheel.setViewAdapter(new DateNumericAdapter(this, 1, getTerms(kind), 0));
        followWheel.setCurrentItem(0);
        followWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateFollowText(followWheel);
                }
            }
        });
        cancle = (TextView) this.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        makeSure = (TextView) this.findViewById(R.id.make_sure);
        makeSure.setOnClickListener(this);
        layoutSingleWheel = (LinearLayout) this.findViewById(R.id.layout_single_wheel);
        timesWheel2 = (WheelView) this.findViewById(R.id.times_choose2);
        timesWheel2.setNormalTx("倍");
    }

    private void updateDoubleText(WheelView item) {
        timesNumWheel = item.getCurrentItem() + 1;
// timesNumWheel = Integer.valueOf(timesArray[item.getCurrentItem()]);
        invalidateWheelMoney();
    }

    private void updateFollowText(WheelView item) {
        followNumWheel = item.getCurrentItem() + 1;
        invalidateWheelMoney();

    }

    protected void invalidateMoney() {
        if (kind.equals("jlk3")) {
            wholeMoney = allMoney * followNum * timesNum;
        }
        else {
            wholeMoney = money * followNum * timesNum;
        }
        long num = wholeMoney / 2;
        if (isSuperaddion)
            wholeMoney = wholeMoney / 2 * 3;
        String moneyStr = num + "注     <font color='red'>" + wholeMoney + "元</font>";
        betMoney.setText(Html.fromHtml(moneyStr));
        if (submit.isEnabled() && canBet) {
            if (wholeMoney > Double.valueOf(appState.getAccount())) {
                line.setVisibility(View.VISIBLE);
                accountStatus.setVisibility(View.VISIBLE);
                accountStatus.setText(getResources().getString(R.string.bet_pay_status_topup));
                toRechage = true;
                submit.setText("  充值  ");
            }
            else {
                line.setVisibility(View.GONE);
                accountStatus.setVisibility(View.GONE);
                toRechage = false;
                submit.setText("  付 款  ");
            }
        }
        showShareContent();
    }

    long shareMoney;

// private void initTimesArray() {
// for (int i = 0; i < 100; i++)
// timesArray[i] = String.valueOf(i + 1);
// timesArray[100] = "500";
// timesArray[101] = "1000";
// timesArray[102] = "2000";
// timesArray[103] = "3000";
// timesArray[104] = "4000";
// timesArray[105] = "5000";
// timesArray[106] = "6000";
// timesArray[107] = "7000";
// timesArray[108] = "8000";
// timesArray[109] = "9000";
// timesArray[110] = "9999";
//
// }

    private void init() {
        editShare.requestFocus();
        locationCommit = preferences.getBoolean("location_commit", true);
        betInfTranspond = preferences.getBoolean("bet_transpond", true);
        if (locationCommit) {
            locationSelect.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            locationSelect.setBackgroundResource(R.drawable.choosing_not_select);
        }
        if (betInfTranspond) {
            transpondSelect.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            transpondSelect.setBackgroundResource(R.drawable.choosing_not_select);
        }
        progress = new ProgressDialog(this);
        progress.setMessage("投注提交中...");
        setBetPayTitle();
        account.setText(appState.getAccount() + "元");

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        orienttationSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        orienttationListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                // 这个浮点数详细说明一下，是一个长度为三的浮点型数组，每一个元素在不同的传感器都有不同的含义。此处因为使用的是方向传感器，所以只关注第一个浮点数，即角度。而且这个角度是以相对于北方度数的一个角度（北方为0，顺时针转动，东方为90，南方为180，西方为270）
                presentOrentation = event.values[0];
                if (Math.abs(presentOrentation - lastOrentation) >= 2) {
                    lastOrentation = presentOrentation;
                    float range = (float) 22.5;
                    // 指向正北
                    if (presentOrentation > 360 - range && presentOrentation < 360 + range) {
                        direction = "正北";
                    }
                    // 指向正东
                    if (presentOrentation > 90 - range && presentOrentation < 90 + range) {
                        direction = "正东";
                    }
                    // 指向正南
                    if (presentOrentation > 180 - range && presentOrentation < 180 + range) {
                        direction = "正南";
                    }
                    // 指向正西
                    if (presentOrentation > 270 - range && presentOrentation < 270 + range) {
                        direction = "正西";
                    }
                    // 指向东北
                    if (presentOrentation > 45 - range && presentOrentation < 45 + range) {
                        direction = "东北";
                    }
                    // 指向东南
                    if (presentOrentation > 135 - range && presentOrentation < 135 + range) {
                        direction = "东南";
                    }
                    // 指向西南
                    if (presentOrentation > 225 - range && presentOrentation < 225 + range) {
                        direction = "西南";
                    }
                    // 指向西北
                    if (presentOrentation > 315 - range && presentOrentation < 315 + range) {
                        direction = "西北";
                    }
                    if (direction != null) {
                        directionLayout.setVisibility(View.VISIBLE);
                        toCompass.setText(direction);
                        showShareContent();
                    }
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        if (ifBetEnd == true) {
            stopBet();
        }
        else {
            checkAccountStatus();
        }
        locationTask();
    }

    protected void showShareContent() {
        if (isEdited == false) {
            shareContent.delete(0, shareContent.length());
            if (direction != null) {
                shareContent.append("向" + direction);
            }
            shareContent.append("投注了" + kindChineseName + "：" + wholeMoney + "元");
            if (timesNum != 1 && followNum != 1) {
                shareContent.append("," + timesNum + "倍" + "追" + followNum + "期");
            }
            else {
                if (timesNum != 1) {
                    shareContent.append("," + timesNum + "倍");
                }
                if (followNum != 1) {
                    shareContent.append("," + "追" + followNum + "期");
                }
            }
            if (locationCommit && address != null) {
                shareContent.append("\n" + "投注位置:" + address);
            }
            changeShareContent = true;
            editShareContent.setText(shareContent);
            Editable ea = editShareContent.getText();
            Selection.setSelection(ea, ea.length());
        }
    }

    private void checkAccountStatus() {
        if (appState.getUsername() == null) {
            line.setVisibility(View.VISIBLE);
            accountStatus.setVisibility(View.VISIBLE);
            accountStatus.setText(getResources().getString(R.string.bet_pay_status_login));
            submit.setText("  登录  ");
            canBet = false;
        }
// else {
// if (appState.getPerfectInf().equals("0")) {
// line.setVisibility(View.VISIBLE);
// accountStatus.setVisibility(View.VISIBLE);
// accountStatus.setText(getResources().getString(R.string.bet_pay_status_add_more_inf));
// submit.setText("完善信息");
// canBet = false;
// }
// }
    }

    private void locationTask() {
        if (HttpConnectUtil.isNetworkAvailable(BetPay.this)) {
            LocationTask locationTask = new LocationTask();
            locationTask.execute();
        }
    }

    class LocationTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            initLocation();
            return null;
        }

    }

    private void initLocation() {
        String lastLocationInf = preferences.getString("location_inf", null);
        float lastLocationLat = preferences.getFloat("location_lat", 0);
        float lastLocationLong = preferences.getFloat("location_long", 0);
        if (lastLocationInf != null) {
            address = lastLocationInf;
            locationInf.setText(lastLocationInf + ",刷新中..");
        }
        else {
            locationInf.setText("正在定位中..");
        }
        if (lastLocationLat != 0 && lastLocationLong != 0) {
            latitude = lastLocationLat;
            longitude = lastLocationLong;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
// Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
// if (location == null) {
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
// }
        if (location != null) {
            getAddress(location);
        }
    }

    public void getAddress(Location mLocation) {
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> lstAddress =
                geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();

            /* 判断地址是否为多行 */
            if (lstAddress.size() > 0) {
                Address adsLocation = lstAddress.get(0);

                for (int i = 0; i < adsLocation.getMaxAddressLineIndex() - 1; i++) {
                    sb.append(adsLocation.getAddressLine(i)).append("\n");
                }
                if (adsLocation.getMaxAddressLineIndex() > 0) {
                    sb.append(adsLocation.getAddressLine(adsLocation.getMaxAddressLineIndex() - 1));
                }
            }

            /* 将取得到的地址组合后放到stringbuilder对象中输出用 */
            address = sb.toString();
            databaseData.putString("location_inf", address);
            databaseData.commit();
            locationInf.setText(address);
            showShareContent();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBetPayTitle() {
        kindChineseName = LotteryUtils.getLotteryName(kind);
        if (kindChineseName != null) {
            if (term == null)
                lotteryCodetitle.setText(kindChineseName);
            else
                lotteryCodetitle.setText(kindChineseName + " " + term + "期");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wheelLayout.isShown())
                switchBottomLayout();
            else {
                setResult(RESULT_CANCELED);
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(BetPay.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                R.anim.push_to_left_out);
                }
            }
        }
        return false;
    }

    // 切换底部滚轮是否显示，显示的时候设置初始值
    protected void switchBottomLayout() {
    }

    // 点击倍数按钮，显示倍数追期选择滚轮
    protected void setBetSetting() {
    }

    // 倍投按钮显示追期倍数选择情况
    protected void refreshDoubleFollowText() {
    }

    // 刷新滚轮顶部金额
    protected void invalidateWheelMoney() {
    }

    @Override
    public void onClick(View v) {
// if (v.getId() == R.id.layout_account) {
// refreshUserMoney();
// }
        if (v.getId() == R.id.submit) {
            if (!readProtocol) {
                ViewUtil.showTipsToast(this, "请勾选委托投注协议");
                return;
            }
            if (HttpConnectUtil.isNetworkAvailable(BetPay.this)) {
                clickSubmit();
            }
            else {
                ViewUtil.showTipsToast(this, noNetTips);
            }
        }
// else if (v.getId() == R.id.layout_location_operation) {
// Map<String, String> map = new HashMap<String, String>();
// map.put("inf", "username [" + appState.getUsername() + "]: open select location");
// String eventName = "v2 open select location";
// FlurryAgent.onEvent(eventName, map);
// besttoneEventCommint(eventName);
// String eventNameMobNew = "bet_select_location";
// MobclickAgent.onEvent(this, eventNameMobNew);
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putString("address", address);
// bundle.putDouble("latitude", latitude);
// bundle.putDouble("longitude", longitude);
// intent.putExtras(bundle);
// intent.setClass(BetPay.this, LocationAdjust.class);
// startActivityForResult(intent, 0);
// if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
// (new AnimationModel(BetPay.this)).overridePendingTransition(R.anim.push_to_right_in,
// R.anim.push_to_right_out);
// }
// }
        else if (v.getId() == R.id.bet_times_pursuit_num) {
            switchBottomLayout();
        }
        else if (v.getId() == R.id.make_sure) {
            setBetSetting();
        }
        else if (v.getId() == R.id.cancle) {
            switchBottomLayout();
        }
        else if (v.getId() == R.id.layout_location) {
            if (locationCommit) {
// locationTipsLayout.setVisibility(View.GONE);
                locationCommit = false;
                locationSelect.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
// locationTipsLayout.setVisibility(View.VISIBLE);
                locationCommit = true;
                locationSelect.setBackgroundResource(R.drawable.choosing_select);
            }
            showShareContent();
            databaseData.putBoolean("location_commit", locationCommit);
            databaseData.commit();
        }
        else if (v.getId() == R.id.more_inf_close) {
            if (showBetInf) {
                closeMore.setVisibility(View.GONE);
                showMore.setVisibility(View.VISIBLE);
                shareToGardenLayout.setVisibility(View.GONE);
                showBetInf = false;
            }
        }
        else if (v.getId() == R.id.show_more_inf) {
            if (!showBetInf) {
                closeMore.setVisibility(View.VISIBLE);
                showMore.setVisibility(View.GONE);
                shareToGardenLayout.setVisibility(View.VISIBLE);
                showBetInf = true;
            }
        }
        else if (v.getId() == R.id.layout_transpond) {
            if (betInfTranspond) {
                betInfTranspond = false;
                transpondSelect.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                betInfTranspond = true;
                transpondSelect.setBackgroundResource(R.drawable.choosing_select);
            }
            databaseData.putBoolean("bet_transpond", betInfTranspond);
            databaseData.commit();
        }
        else if (v.getId() == R.id.stop_pursuit_layout) {
            if (isStopPursuit) {
                isStopPursuit = false;
                stopPursuit.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                isStopPursuit = true;
                stopPursuit.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.superaddition_layout) {
            if (isSuperaddion) {
                isSuperaddion = false;
                superaddition.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                isSuperaddion = true;
                superaddition.setBackgroundResource(R.drawable.choosing_select);
            }
            invalidateMoney();
            invalidateWheelMoney();
        }
        else if (v.getId() == R.id.bet_protocol) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.BET_PROTOCOL_URL);
            bundle.putString("title", "委托投注协议");
            intent.putExtras(bundle);
            intent.setClass(BetPay.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.bet_check) {
            if (readProtocol) {
                submit.setEnabled(false);
                readProtocol = false;
                check.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                if (ifBetEnd == false) {
                    submit.setEnabled(true);
                }
                readProtocol = true;
                check.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.bet_to_compass) {
            Intent intent = new Intent();
            intent.setClass(BetPay.this, Compass.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.foot_ball_bet_detail) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("displayCode", code);
            bundle.putString("bet_kind", kind);
            bundle.putString("bet_term", term);
            bundle.putBoolean("ifShowInf", ifShowInf);
            intent.putExtras(bundle);
            intent.setClass(BetPay.this, BetFootBallTeamListShow.class);
            startActivity(intent);
        }
    }

    public void refreshUserMoney() {
        if (HttpConnectUtil.isNetworkAvailable(BetPay.this)) {
            userInfTask = new UserInfTask(BetPay.this);
            userInfTask.setOnGetUserInfListener(this);
            userInfTask.execute(1);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private void clickSubmit() {
        if (toRechage) {
            ifRefreshMoney = true;
            ActionUtil.toTopupNew(this);
        }
        else {
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: open bet pay click submit");
            map.put("more_inf", "open bet pay click submit of " + kind);
            String eventName = "v2 bet pay click submit";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMobNew = "bet_pay_click_submit";
            MobclickAgent.onEvent(this, eventNameMobNew, kind);
            // changed by vincent 12.7
            if (ifShowInf) {
                ifShowInf = false;
                showTermChangeWarningDialog();
            }
            else {
                toBet();
            }
        }
    }

    private void showTermChangeWarningDialog() {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("期数改变提示").setWarning().setMessage("投注期数已发生改变,确定投注" + term + "期？").setPositiveButton("确  定",
                                                                                                                   new DialogInterface.OnClickListener() {
                                                                                                                       public void onClick(DialogInterface dialog,
                                                                                                                                           int which) {
                                                                                                                           dlgTermChangeTips.dismiss();
                                                                                                                           toBet();
                                                                                                                       }
                                                                                                                   }).setNegativeButton("取  消",
                                                                                                                                        new DialogInterface.OnClickListener() {
                                                                                                                                            public void onClick(DialogInterface dialog,
                                                                                                                                                                int which) {
                                                                                                                                                dlgTermChangeTips.dismiss();
                                                                                                                                            }
                                                                                                                                        });
        dlgTermChangeTips = customBuilder.create();
        dlgTermChangeTips.show();
    }

    protected void toBet() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                latitude = bundle.getDouble("addr_latitude");
                longitude = bundle.getDouble("addr_longitude");
                address = bundle.getString("addr");
                String locationStr = "投注地址:" + address;
                locationInf.setText(locationStr);
                showShareContent();

            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                canBet = true;
                invalidateMoney();
            }
        }
        // add by vincent
// else if (requestCode == 2) {
// if (resultCode == RESULT_OK) {
// finish();
// setResult(RESULT_OK);
// }
// }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet pay");
        map.put("more_inf", "open bet pay of " + kind);
        String eventName = "v2 open bet pay";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        sm.unregisterListener(orienttationListener, orienttationSensor);
    }

    @Override
    public void onPost(String json) {
        String inf;
        if (json == null) {
            searchFail(null);
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
// ViewUtil.showTipsToast(this,"余额刷新成功");
                String data = analyse.getData(json, "response_data");
                try {
                    JSONArray hallArray = new JSONArray(data);
                    JSONObject jo = hallArray.getJSONObject(0);
                    account.setText(jo.getString("balance") + "元");
                    appState.setAccount(Double.valueOf(jo.getString("balance")));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    searchFail(null);
                }
                invalidateMoney();
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(BetPay.this);
                inf = getResources().getString(R.string.login_timeout);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(BetPay.this);
                inf = getResources().getString(R.string.login_again);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else {
                searchFail(null);
            }
        }
    }

    protected void loginAgainPost() {
        finish();
    }

    private void searchFail(String inf) {
        String failInf = "余额：查询失败";
        if (inf != null)
            failInf = "余额：" + inf;
        ViewUtil.showTipsToast(this, failInf);
    }

    @Override
    public void onPre() {
    }

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_base_info");
            parameter.put("pid", LotteryUtils.getPid(BetPay.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        private HashMap<String, String> initFastHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001070");
            parameter.put("pid", LotteryUtils.getPid(BetPay.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetPay.this);
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
                    Logger.inf("vincent", endtime);
                    String awardtime = ja.getData(response, "awardtime");
                    if (getTerm != null && systemtime != null && endtime != null && !endtime.equals("") &&
                        !systemtime.equals("")) {
                        betTimeInf.setVisibility(View.VISIBLE);
                        countDownTime.setVisibility(View.VISIBLE);
                        term = getTerm;
                        setBetPayTitle();
                        GetServerTime time = new GetServerTime(BetPay.this);
                        OperateInfUtils.refreshTime(BetPay.this, time.formatTime(systemtime));
                        startCountDown(endtime, systemtime);
                    }
                    else
                        setGetTermEnd();
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
            betTimeInf.setVisibility(View.GONE);
            countDownTime.setVisibility(View.GONE);
        }
    }

    protected void setGetTermEnd() {
        // title.setVisibility(View.GONE);
        countDownTime.setText(BETEND);
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.GONE);
    }

    protected void setGetTermFail() {
        stopBet();
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.VISIBLE);
        countDownTime.setText("没有拿到期数信息，无法投注");
    }

    public void disableMyLocation() {
        if (mLocationListener != null) {
            locationManager.removeUpdates(mLocationListener);
        }
    }

    public boolean enableMyLocation() {
        boolean result = true;
        try {
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onLocationChanged(Location location) {
                    try {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            databaseData.putFloat("location_lat", (float) latitude);
                            databaseData.putFloat("location_long", (float) longitude);
                            databaseData.commit();
                            getAddress(location);
                            disableMyLocation();
                        }
                    }
                    catch (Exception e) {
                    }
                }
            };
            if (mLocationListener == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mLocationUpdateMinTime,
                                                       mLocationUpdateMinDistance, locationListener);
            }
        }
        catch (Exception e) {
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        disableMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        enableMyLocation();
        if (ifRefreshMoney == true) {
            ifRefreshMoney = false;
            refreshUserMoney();
        }
    }

    // 提交投注成功统计信息
    protected void submitStatisticsBetSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user bet " + kind + " success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user bet success");
        String eventName = "bet success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "bet_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("kind", kind);
        MobclickAgent.onEvent(BetPay.this, eventNameMob, map1);
        besttoneEventCommint(eventNameMob);
    }

    @Override
    protected void submitData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("bet_kind");
        }
        String eventName = "open_bet_pay";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }
}
