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
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.haozan.caipiao.adapter.unite.CommissionAdapter;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.UserInfTask;
import com.haozan.caipiao.task.UserInfTask.OnGetUserInfListener;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PredicateLayout;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.umeng.analytics.MobclickAgent;

public class BetPayUnite
    extends BasicActivity
    implements OnClickListener, OnItemClickListener, OnGetUserInfListener {
    private static final String BETEND = "投注已截止";
    protected static final int UPDATEBETTIME = 0;
// private static final int REQUESTLOCATION = 1;
    private static final int GETNEWINF = 2;
    private String[] list = new String[] {"1%", "2%", "3%", "4%", "5%", "6%", "7%", "8%", "9%", "10%"};
    private ImageView imgHelp;
    protected RelativeLayout relay_commission;
    protected LinearLayout bg_commission;
    protected ImageView imgCommission;
    protected ImageView imgComDevider;// 佣金部分分割线
    protected boolean ifCommission;
    protected GridView mGridView;
    protected CommissionAdapter adapter;
    protected int index;
    protected String kind;
    protected long money = 1;
    protected String mode;
    protected String term;// 期
    protected boolean ifShowInf = false;
    protected TextView wheelBetMoney;
    protected ImageView stopPursuit;
    protected RelativeLayout stopPursuitLayout;
    protected ImageView superaddition;
    protected RelativeLayout superadditionLayout;
    protected LinearLayout wheelLayout;
    protected LinearLayout ll_unite_betnum;
    protected LinearLayout ll_unite_basic_detail;
    protected RelativeLayout re_ifshow_detail;
    protected ImageView img_show_detail;
    protected TextView cancle;
    protected TextView makeSure;
    protected LinearLayout layoutSingleWheel;
    protected WheelView timesWheel2;
    protected boolean scrolling = false;
    protected int timesNumWheel = 1;
// protected int followNumWheel = 1;
    protected TextView title;
    protected TextView betTimeInf;
    protected TextView countDownTime;
    protected LinearLayout accountLayout;
    protected TextView betMoney;
    protected TextView account;
    protected TextView accountStatus;
    protected ImageView line;
    protected TextView lotteryNum;
    protected TextView warningTips;

    protected RelativeLayout directionLayout;
    protected TextView locationInf;
    protected Button toCompass;
    protected RelativeLayout locationTipsLayout;
    protected RelativeLayout locationLayout;
    protected ImageView closeMore;
    protected TextView showMore;
    protected ImageView locationSelect;
    protected RelativeLayout transpondLayout;
    protected ImageView transpondSelect;
    protected TextView betNum;
    protected LinearLayout shareToGardenLayout;
    protected LinearLayout bottomLayout;
    protected ImageView check;
    protected TextView protocol;
    protected Button submit;
    protected LinearLayout sportsBunchLayout;
    protected TextView lotteryCodetitle;
    protected EditText editShareContent;
    protected EditText editShare;// 用户自己输入的内容
    private Location location;
    // 判断分享内容是否被编辑过
    protected boolean isEdited;
    // 判断是否分享内容改变非用户编辑
    protected boolean changeShareContent = false;
    protected TextView sfcBetDetail;
    protected RelativeLayout zucaiNumContainer;
    protected PredicateLayout zucaiPredicate;
    protected boolean locationCommit;
    protected boolean betInfTranspond;
    protected ProgressDialog progress;
    protected String kindChineseName = null;
    protected SensorManager sm;
    protected Sensor orienttationSensor;
    protected SensorEventListener orienttationListener;
    protected float presentOrentation = -1.0f;
    protected float lastOrentation = 0.0f;
    protected String direction = null;
    protected StringBuilder shareContent;
    protected int timesNum = 1;
    protected Integer wholeMoney;
    protected String address;
    protected boolean canBet = true;
    protected double latitude;
    protected double longitude;
    protected LocationManager locationManager;
    private LocationListener mLocationListener = null;
    protected UserInfTask userInfTask;
    protected CustomDialog loginAgainDialog;
    public boolean isFrequent;
    private Boolean isPause = false;
    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    protected StringBuilder betLastTime;
    protected boolean readProtocol = true;
    protected boolean toRechage = false;
    protected boolean ifBetEnd;
    protected boolean showBetInf = false;
    protected boolean isStopPursuit = false;
    protected boolean isSuperaddion = false;
    protected boolean ifShowUniteBasicDetail = false;
    protected String code;
    long shareMoney;
    protected String timeStamp;
    // 合买基本信息
    protected String uniteTitle;
    protected String uniteDescribe;
    protected String uniteSecret;
    protected String uniteCommission;
    protected double unitePrice;
    protected String uniteBuyNums;
    protected String uniteGuaranteeNums;

    protected TextView tv_lastnums;
    protected int lastNums;

    protected EditText eduniteTitle;
    protected EditText eduniteDescribe;
    protected TextView tv_toPayMoney;

    protected String[] data1;

    protected int buyNums;// 最少购买数
    protected long tempMoney;
    protected double toPayMoney;
    protected long wheelMoney;// 滚轮生成的临时总额

    protected EditText tv_guanum_ll;// 显示选定的保底份数
    protected EditText tv_buynum_ll;// 显示选定的购买份数

    protected LinearLayout numInfo;
    protected TextView tv_numInfo;

    protected int screenHeight;
    protected int screenWidth;

    private static final long mLocationUpdateMinTime = 1000;
    private static final float mLocationUpdateMinDistance = 10.0f;
    public boolean ifRefreshMoney = false;

    protected UEDataAnalyse mUploadRequestTime;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEBETTIME:
                    betLastTime.delete(0, betLastTime.length());
                    long millis = endTimeMillis - gapMillis - System.currentTimeMillis() - 10 * 1000;
                    countDownTime.setText(TimeUtils.getCountDownTime(millis));
                    millis -= 1000;
                    if (millis >= 0)
                        handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                    else {
                        ifShowInf = true;
                        ifBetEnd = true;
                        stopBet();
                        countDownTime.setText(BETEND);
                        handler.sendEmptyMessageDelayed(GETNEWINF, 15000);
                    }
                    break;
                case GETNEWINF:
                    if (HttpConnectUtil.isNetworkAvailable(BetPayUnite.this)) {
                        GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                        getLotteryInf.execute();
                    }
                    else
                        setGetTermFail();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_pay_unite);
        initData();
        setupviews();
        init();
    }

    protected void init() {
        tempMoney = money;// money为上个类传来的参数
        wheelMoney = money;// 初始化滚轮的总额
        ifCommission = preferences.getBoolean("bet_unite_ifcommission", true);
        ifShowUniteBasicDetail = preferences.getBoolean("bet_unite_show_basic_detail", false);
        if (ifShowUniteBasicDetail == true) {
            ll_unite_basic_detail.setVisibility(View.VISIBLE);
            img_show_detail.setBackgroundResource(R.drawable.arrow_down);
        }
        else {
            ll_unite_basic_detail.setVisibility(View.GONE);
            img_show_detail.setBackgroundResource(R.drawable.arrow_right);
        }
        // 临时默认每份金额为1元
        unitePrice = 1;
        lastNums = (int) (tempMoney / unitePrice);// 总份数，根据设置的每份金额进行改变
        tv_lastnums.setText(String.valueOf(lastNums));

        float currentCommission = 0;
        if (ifCommission) {
            imgCommission.setBackgroundResource(R.drawable.choosing_select);
            imgComDevider.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.VISIBLE);

            currentCommission = (float) ((index + 1) * 0.01);
        }
        else {
            imgCommission.setBackgroundResource(R.drawable.choosing_not_select);
            imgComDevider.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);

            currentCommission = (float) 0.01;
        }
        buyNums = (int) Math.ceil(tempMoney * currentCommission);
        specialTextAddChangedListener();
        initCommission();
        invalidateWheelMoney();// 初始化滚轮顶部显示内容
        tv_buynum_ll.setText(buyNums + "");
        tv_buynum_ll.setHint("最少需自购" + buyNums + "份");

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
        initOrienttationListener();
        if (ifBetEnd == true) {
            stopBet();
        }
        else {
            checkAccountStatus();
        }
        locationTask();
    }

    private boolean checkEdiText() {
        boolean ifEdiOk = false;
        if (null != tv_buynum_ll.getText().toString() && !"".equals(tv_buynum_ll.getText().toString())) {
            if (getSelfBuyNum() == 0 || getGuaNum() < 0) {// 当输入值为0时
                ViewUtil.showTipsToast(this, "份数不能为0或负数");
            }
            else {// 当输入值不为0时
                  // 先判断输入值是否小于最小购买数
                int selfTemp = getSelfBuyNum();
                int guaNumTemp = getGuaNum();
                if (selfTemp < buyNums) {
                    tv_buynum_ll.setText(String.valueOf(buyNums));
                    tv_buynum_ll.setHint("最少需自购" + buyNums + "份");
                    Editable ea = tv_buynum_ll.getText();
                    Selection.setSelection(ea, ea.length());
                    ViewUtil.showTipsToast(this, "您最少需自购" + buyNums + "份");
                }
                // 再判断输入值是否大于可购总数
                else if (selfTemp > (lastNums - guaNumTemp)) {
                    ViewUtil.showTipsToast(this, "所购总份数不能大于" + lastNums + "份");
                }
                else {
                    ifEdiOk = true;
                }
                checkMoney();
            }
        }
        else {
            ifEdiOk = false;
            ViewUtil.showTipsToast(this, "自购份数不能为空");
        }

        return ifEdiOk;
    }

    public void specialTextAddChangedListener() {
        tv_guanum_ll.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkMoney();
                invalidateMoney();
                if (null != tv_guanum_ll.getText().toString() &&
                    !"".equals(tv_guanum_ll.getText().toString())) {
                    resetGuaHintWithoutChangeText();
                }
            }
        });
        tv_buynum_ll.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkMoney();
                invalidateMoney();
                if (null != tv_buynum_ll.getText().toString() &&
                    !"".equals(tv_buynum_ll.getText().toString())) {
                    resetSelfBuyHintWithoutChangeText();
                }
            }
        });

    }

    public void initOrienttationListener() {
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
                    else {
                        directionLayout.setVisibility(View.GONE);
                    }
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    private void locationTask() {
        if (HttpConnectUtil.isNetworkAvailable(BetPayUnite.this)) {
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

    protected void showShareContent() {
        if (isEdited == false) {
            shareContent.delete(0, shareContent.length());
            if (direction != null) {
                shareContent.append("向" + direction);
            }
            shareContent.append("发起了" + kindChineseName + "合买" + "：" + wholeMoney + "元");

            if (timesNum != 1) {
                shareContent.append("," + timesNum + "倍");
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

    private void setBetPayTitle() {
        kindChineseName = LotteryUtils.getLotteryName(kind);
        if (kindChineseName != null) {
            if (term == null)
                lotteryCodetitle.setText(kindChineseName + "合买");
            else
                lotteryCodetitle.setText(kindChineseName + "合买 " + term + "期");
        }
    }

    private void initData() {
        mUploadRequestTime = new UEDataAnalyse(this);

        index = preferences.getInt("commission_percent", 0);
        uniteCommission = String.valueOf(index + 1);
        betLastTime = new StringBuilder();
        shareContent = new StringBuilder();
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("bet_kind");
        money = bundle.getLong("bet_money");
        mode = bundle.getString("mode");
        term = bundle.getString("bet_term");
        ifShowInf = bundle.getBoolean("ifShowInf");
        ifBetEnd = bundle.getBoolean("if_bet_end");
    }

    private void setupviews() {
        setupWholeViews();
        setupWheelViews();
    }

    private void setupWholeViews() {
        tv_guanum_ll = (EditText) findViewById(R.id.tv_guanum_ll);
        tv_buynum_ll = (EditText) findViewById(R.id.tv_buynum_ll);
        numInfo = (LinearLayout) findViewById(R.id.ll_unite_checknum_infomation);
        tv_numInfo = (TextView) findViewById(R.id.tv_unite_checknum_infomation);
        tv_lastnums = (TextView) findViewById(R.id.unite_lastnum);
        tv_toPayMoney = (TextView) findViewById(R.id.order_money_oneself);
        relay_commission = (RelativeLayout) findViewById(R.id.ll_bet_unite_commission);
        relay_commission.setOnClickListener(this);
        bg_commission = (LinearLayout) findViewById(R.id.bg_unite_commission);
        imgCommission = (ImageView) findViewById(R.id.unite_img_ifcommission);
        imgComDevider = (ImageView) findViewById(R.id.commission_devider);
        adapter = new CommissionAdapter(this, list, index, "unite_commissin");
        mGridView = (GridView) findViewById(R.id.commission_gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);
        eduniteTitle = (EditText) findViewById(R.id.edi_unite_title);
        eduniteTitle.setText(appState.getNickname() + "发起的合买");
        eduniteDescribe = (EditText) findViewById(R.id.edi_unite_describe);
        eduniteDescribe.setText("买定离手，即买即中...");

        title = (TextView) this.findViewById(R.id.title);
        betTimeInf = (TextView) this.findViewById(R.id.bet_time_inf);// 离截止。。。
        ll_unite_betnum = (LinearLayout) this.findViewById(R.id.ll_unite_betnum);
        ll_unite_basic_detail = (LinearLayout) this.findViewById(R.id.ll_unite_basic_detail);
        re_ifshow_detail = (RelativeLayout) this.findViewById(R.id.re_ifshow_detail);
        re_ifshow_detail.setOnClickListener(this);
        img_show_detail = (ImageView) this.findViewById(R.id.img_show_detail);
        countDownTime = (TextView) this.findViewById(R.id.bet_countdown_time);
        accountLayout = (LinearLayout) this.findViewById(R.id.layout_account);
        accountLayout.setOnClickListener(this);
        betMoney = (TextView) this.findViewById(R.id.order_money);
        account = (TextView) this.findViewById(R.id.account);
        accountStatus = (TextView) this.findViewById(R.id.account_status_tips);
        line = (ImageView) this.findViewById(R.id.bet_line);
        lotteryNum = (TextView) this.findViewById(R.id.lottery_num);
        warningTips = (TextView) findViewById(R.id.bet_warning_tips);

        directionLayout = (RelativeLayout) this.findViewById(R.id.layout_direction);
        locationInf = (TextView) this.findViewById(R.id.location_inf);
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
        editShare.setOnClickListener(this);
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
        imgHelp = (ImageView) findViewById(R.id.bet_unite_help);
        imgHelp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("url", LotteryUtils.HELP_URL + "#hemai");
                bundle.putString("title", "帮助说明");
                intent.putExtras(bundle);
                intent.setClass(BetPayUnite.this, WebBrowser.class);
                startActivity(intent);
            }
        });

    }

    private void setupWheelViews() {
        wheelBetMoney = (TextView) this.findViewById(R.id.wheel_order_money);
        stopPursuit = (ImageView) this.findViewById(R.id.stop_pursuit_select);
        stopPursuitLayout = (RelativeLayout) this.findViewById(R.id.stop_pursuit_layout);// 中奖停止追号
        stopPursuitLayout.setOnClickListener(this);
        superaddition = (ImageView) this.findViewById(R.id.superaddition_select);
        superadditionLayout = (RelativeLayout) this.findViewById(R.id.superaddition_layout);
        superadditionLayout.setOnClickListener(this);
        wheelLayout = (LinearLayout) this.findViewById(R.id.wheel_layout);
// layoutWholeWheel = (LinearLayout) this.findViewById(R.id.layout_whole_wheel);
        cancle = (TextView) this.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        makeSure = (TextView) this.findViewById(R.id.make_sure);
        makeSure.setOnClickListener(this);
        layoutSingleWheel = (LinearLayout) this.findViewById(R.id.layout_single_wheel);
        timesWheel2 = (WheelView) this.findViewById(R.id.times_choose2);
        timesWheel2.setNormalTx("倍");
    }

    // 刷新滚轮顶部金额
    protected void invalidateWheelMoney() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bet_unite_commission:
                if (ifCommission) {
                    ifCommission = false;
                    imgCommission.setBackgroundResource(R.drawable.choosing_not_select);
                    imgComDevider.setVisibility(View.GONE);
                    mGridView.setVisibility(View.GONE);
                }
                else {
                    ifCommission = true;
                    imgCommission.setBackgroundResource(R.drawable.choosing_select);
                    imgComDevider.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.VISIBLE);
                }
                databaseData.putBoolean("bet_unite_ifcommission", ifCommission);
                databaseData.commit();
                invalidateMoney();
                resetSelfBuyHintWithoutChangeText();
                break;

// case R.id.layout_account:
// refreshUserMoney();
// break;
            case R.id.submit:
                if (checkInput() == true) {
                    if (!readProtocol) {
                        ViewUtil.showTipsToast(this, "请勾选委托投注协议");
                        return;
                    }
                    if (toRechage) {
                        ifRefreshMoney = true;
                        ActionUtil.toTopupNew(this);
                    }
                    else if (checkEdiText() == true) {
                        if (HttpConnectUtil.isNetworkAvailable(BetPayUnite.this)) {
                            clickSubmit();
                        }
                        else {
                            ViewUtil.showTipsToast(this, noNetTips);
                        }
                    }
                }
                break;
            case R.id.bet_times_pursuit_num:
                switchBottomLayout();
                break;
            case R.id.make_sure:
                setBetSetting();
                break;
            case R.id.cancle:
                switchBottomLayout();
                break;
            case R.id.layout_location:
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
                break;
            case R.id.re_ifshow_detail:
                if (ifShowUniteBasicDetail == true) {
                    ifShowUniteBasicDetail = false;
                    ll_unite_basic_detail.setVisibility(View.GONE);
                    img_show_detail.setBackgroundResource(R.drawable.arrow_right);
                }
                else {
                    ifShowUniteBasicDetail = true;
                    ll_unite_basic_detail.setVisibility(View.VISIBLE);
                    img_show_detail.setBackgroundResource(R.drawable.arrow_down);
                }
                databaseData.putBoolean("bet_unite_show_basic_detail", ifShowUniteBasicDetail);
                databaseData.commit();
                break;
            case R.id.more_inf_close:
                if (showBetInf) {
                    closeMore.setVisibility(View.GONE);
                    showMore.setVisibility(View.VISIBLE);
                    shareToGardenLayout.setVisibility(View.GONE);
                    showBetInf = false;
                }
                break;
            case R.id.show_more_inf:
                if (!showBetInf) {
                    closeMore.setVisibility(View.VISIBLE);
                    showMore.setVisibility(View.GONE);
                    shareToGardenLayout.setVisibility(View.VISIBLE);
                    showBetInf = true;
                }
                break;
            case R.id.layout_transpond:
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
                break;
            case R.id.stop_pursuit_layout:
                if (isStopPursuit) {
                    isStopPursuit = false;
                    stopPursuit.setBackgroundResource(R.drawable.choosing_not_select);
                }
                else {
                    isStopPursuit = true;
                    stopPursuit.setBackgroundResource(R.drawable.choosing_select);
                }
                break;
            case R.id.superaddition_layout:
                if (isSuperaddion) {
                    isSuperaddion = false;
                    superaddition.setBackgroundResource(R.drawable.choosing_not_select);
                }
                else {
                    isSuperaddion = true;
                    superaddition.setBackgroundResource(R.drawable.choosing_select);
                }
                invalidateWheelMoney();// 大乐透的追加投注会更改总额，此处要刷新滚轮顶部显示的总额
                invalidateMoney();

                resetSelfBuyHintWithoutChangeText();
                break;
            case R.id.bet_protocol:
                Intent intent = new Intent();
                Bundle bun = new Bundle();
                bun.putString("url", LotteryUtils.BET_PROTOCOL_URL);
                bun.putString("title", "委托投注协议");
                intent.putExtras(bun);
                intent.setClass(BetPayUnite.this, WebBrowser.class);
                startActivity(intent);
                break;
            case R.id.bet_check:
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
                break;
            case R.id.foot_ball_bet_detail:
                Intent intent2 = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("displayCode", code);
                bundle.putString("bet_kind", kind);
                bundle.putString("bet_term", term);
                bundle.putBoolean("ifShowInf", ifShowInf);
                intent2.putExtras(bundle);
                intent2.setClass(BetPayUnite.this, BetFootBallTeamListShow.class);
                startActivity(intent2);
                break;
            case R.id.bet_to_compass:
                Intent intent3 = new Intent();
                intent3.setClass(BetPayUnite.this, Compass.class);
                startActivity(intent3);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(BetPayUnite.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                }
                break;
            default:
                break;
        }
    }

    public void refreshUserMoney() {
        if (HttpConnectUtil.isNetworkAvailable(BetPayUnite.this)) {
            userInfTask = new UserInfTask(BetPayUnite.this);
            userInfTask.setOnGetUserInfListener(this);
            userInfTask.execute(1);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    public void resetSelfBuyHintWithoutChangeText() {
        int selfTemp = getSelfBuyNum();
        int guaTemp = getGuaNum();
        if (selfTemp < buyNums) {
            tv_buynum_ll.setHint("最少需自购" + buyNums + "份");
        }
        else {
            if (guaTemp > 0) {
                if (selfTemp + guaTemp > lastNums) {
                    tv_guanum_ll.setText("");
                    ViewUtil.showTipsToast(this, "最多可购买" + lastNums + "份");
                }
            }
        }

        if (lastNums > 1 && selfTemp > (lastNums - 1)) {
            if (guaTemp == 0) {
                tv_buynum_ll.setText(String.valueOf(lastNums - 1));
                ViewUtil.showTipsToast(this, "最多可自购" + (lastNums - 1) + "份");
            }
            else {
                tv_buynum_ll.setText(String.valueOf(lastNums - guaTemp));
                ViewUtil.showTipsToast(this, "最多可自购" + (lastNums - guaTemp) + "份");
            }
            Editable ea = tv_buynum_ll.getText();
            Selection.setSelection(ea, ea.length());
        }
    }

    public void resetGuaHintWithoutChangeText() {
        int selfTemp = getSelfBuyNum();
        int guaTemp = getGuaNum();
        if (selfTemp > buyNums) {
            if ((selfTemp + guaTemp) > lastNums) {
                tv_guanum_ll.setText(String.valueOf(lastNums - selfTemp));
                Editable ea = tv_guanum_ll.getText();
                Selection.setSelection(ea, ea.length());
                ViewUtil.showTipsToast(this, "最多可保底" + (lastNums - selfTemp) + "份");
            }
        }
        else {
            if ((buyNums + guaTemp) > lastNums) {
                tv_guanum_ll.setText(String.valueOf(lastNums - buyNums));
                Editable ea = tv_guanum_ll.getText();
                Selection.setSelection(ea, ea.length());
                ViewUtil.showTipsToast(this, "最多可保底" + (lastNums - buyNums) + "份");
            }
        }

    }

    private Boolean checkInput() {
        String warning = null;
        uniteTitle = eduniteTitle.getText().toString();
        uniteDescribe = eduniteDescribe.getText().toString();
        if (uniteTitle == null || uniteTitle.equals("")) {
            warning = "请输入方案标题";
            eduniteTitle.requestFocus();
            eduniteTitle.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        else if (uniteDescribe == null || uniteDescribe.equals("")) {
            warning = "请输入方案描述";
            eduniteDescribe.requestFocus();
            eduniteDescribe.setError(warning, this.getResources().getDrawable(R.drawable.transparent));
        }
        if (warning != null) {
            return false;
        }
        return true;
    }

    /**
     * 每个影响最终付款金额事件要调用此方法
     * 
     * @author Vimcent 2013-3-20
     */
    protected void invalidateMoney() {
        initCommission();
        // 此处重新获取单价
        lastNums = (int) (wholeMoney / unitePrice);
        tv_lastnums.setText(String.valueOf(lastNums));
        int buyNumTemp = getSelfBuyNum();
        int guaNumTemp = getGuaNum();

        toPayMoney = unitePrice * (buyNumTemp + guaNumTemp);
        tv_toPayMoney.setText(String.valueOf(toPayMoney + "元"));

        checkMoney();
        showShareContent();
    }

    public void initCommission() {
        wholeMoney = (int) (money * timesNum);
        long num = wholeMoney / 2;
        if (isSuperaddion)
            wholeMoney = wholeMoney / 2 * 3;
        String moneyStr = "<font color='red'>" + wholeMoney + "元</font>";
        betMoney.setText(Html.fromHtml(moneyStr));

        float currentCommission = 0;
        if (ifCommission) {
            currentCommission = (float) ((index + 1) * 0.01);
        }
        else {
            currentCommission = (float) 0.01;
        }
        buyNums = (int) Math.ceil(wholeMoney * currentCommission);// 最少自购份数

    }

    public void checkMoney() {
        if (submit.isEnabled() && canBet) {
            if (toPayMoney > Double.valueOf(appState.getAccount())) {
                line.setVisibility(View.VISIBLE);
                accountStatus.setVisibility(View.VISIBLE);
                accountStatus.setText(getResources().getString(R.string.bet_pay_status_topup));
                toRechage = true;
                submit.setText("  充 值  ");
            }
            else {
                line.setVisibility(View.GONE);
                accountStatus.setVisibility(View.GONE);
                toRechage = false;
                submit.setText("  付 款  ");
            }
        }
    }

    // 点击倍数按钮，显示倍数追期选择滚轮
    protected void setBetSetting() {
    }

    // 切换底部滚轮是否显示，显示的时候设置初始值
    protected void switchBottomLayout() {
    }

    private void clickSubmit() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet pay click submit");
        map.put("more_inf", "open bet pay click submit of " + kind);
        String eventName = "v2 bet pay click submit";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMobNew = "bet_pay_click_submit";
        MobclickAgent.onEvent(this, eventNameMobNew, kind);
        if ("".equals(tv_buynum_ll.getText().toString()) || null == tv_buynum_ll.getText().toString()) {
            ViewUtil.showTipsToast(this, "请输入购买份数");
        }
        else
            toBet();
    }

    protected void toBet() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView t_v = (TextView) parent.getChildAt(index).findViewById(R.id.unite_grid_view_item_click);
        t_v.setTextColor(getResources().getColor(R.color.dark_purple));
        t_v.setBackgroundResource(R.drawable.bet_popup_item_normal);
        TextView tv = (TextView) parent.getChildAt(position).findViewById(R.id.unite_grid_view_item_click);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.bet_popup_item_choosed);
        index = position;
        databaseData.putInt("commission_percent", index);
        databaseData.commit();
        invalidateMoney();
        // 取得佣金比例
        uniteCommission = String.valueOf(index + 1);
// float currentCommission = (float) ((position + 1) * 0.01);
// buyNums = (int) Math.ceil(tempMoney * currentCommission);
        resetSelfBuyHintWithoutChangeText();
    }

    @Override
    public void onPre() {
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
                checkMoney();
            }
            else if (status.equals("302")) {
                OperateInfUtils.clearSessionId(BetPayUnite.this);
                inf = getResources().getString(R.string.login_timeout);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else if (status.equals("304")) {
                OperateInfUtils.clearSessionId(BetPayUnite.this);
                inf = getResources().getString(R.string.login_again);
                searchFail(inf);
                showLoginAgainDialog(inf);
            }
            else {
                searchFail(null);
            }
        }
    }

    private void searchFail(String inf) {
        String failInf = "余额：查询失败";
        if (inf != null)
            failInf = "余额：" + inf;
        ViewUtil.showTipsToast(this, failInf);
    }

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_base_info");
            parameter.put("pid", LotteryUtils.getPid(BetPayUnite.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        private HashMap<String, String> initFastHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001070");
            parameter.put("pid", LotteryUtils.getPid(BetPayUnite.this));
            parameter.put("lottery_id", kind);
            parameter.put("new", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetPayUnite.this);
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
                        betTimeInf.setVisibility(View.VISIBLE);
                        countDownTime.setVisibility(View.VISIBLE);
                        term = getTerm;
                        setBetPayTitle();
                        GetServerTime time = new GetServerTime(BetPayUnite.this);
                        OperateInfUtils.refreshTime(BetPayUnite.this, time.formatTime(systemtime));
                        startCountDown(endtime, systemtime);
                    }
                    else
                        setGetTermFail();
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

    public void startCountDown(String endtime, String systemtime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (endtime == null || systemtime == null)
            return;
        try {
            Date date1 = format1.parse(endtime);
            Date date2 = format1.parse(systemtime);
            endTimeMillis = date1.getTime();
            // 合买时间提前20分钟
            endTimeMillis -= 1200000;
            gapMillis = date2.getTime() - System.currentTimeMillis();
            long millis = endTimeMillis - gapMillis - System.currentTimeMillis() - 16 * 1000;
            millis = date1.getTime() - date2.getTime() - 10 * 1000;
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

    protected void startBet() {
        submit.setText("  付 款  ");
        enableBet();
    }

    protected void enableBet() {
        submit.setEnabled(true);
    }

    public void setGetTermFail() {
        stopBet();
        betTimeInf.setVisibility(View.GONE);
        countDownTime.setVisibility(View.VISIBLE);
        countDownTime.setText("没有拿到期数信息，无法投注");
    }

    protected void stopBet() {
        betTimeInf.setVisibility(View.GONE);
        submit.setText("投注截止");
        disableBet();
    }

    protected void disableBet() {
        submit.setEnabled(false);
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
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(BetPayUnite.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
                }
            }
        }
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
    protected void submitStatisticsUniteSuccess() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "user unite " + kind + " success");
        map.put("more_inf", "username [" + appState.getUsername() + "]: user unite success");
        String eventName = "unite success";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "unite_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("kind", kind);
        MobclickAgent.onEvent(BetPayUnite.this, eventNameMob, map1);
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

    protected String getUniteSecret() {
        uniteSecret = "1";
        return uniteSecret;
    }

    protected String getUniteCommission() {
        return uniteCommission;
    }

    protected String getUnitePrice() {
        unitePrice = 1;
        return String.valueOf(unitePrice);
    }

    public int getSelfBuyNum() {
        int selfTemp = 0;
        if (null != tv_buynum_ll.getText().toString() && !"".equals(tv_buynum_ll.getText().toString())) {
            selfTemp = Integer.valueOf(tv_buynum_ll.getText().toString());
            if (selfTemp < 0) {
                selfTemp = 0;
            }
        }
        return selfTemp;
    }

    public int getGuaNum() {
        int guaTemp = 0;
        if (null != tv_guanum_ll.getText().toString() && !"".equals(tv_guanum_ll.getText().toString())) {
            guaTemp = Integer.valueOf(tv_guanum_ll.getText().toString());
            if (guaTemp < 0) {
                guaTemp = 0;
            }
        }
        return guaTemp;
    }

    protected String getSessionId() {
        String sessionId = appState.getSessionid();
        return sessionId;
    }
}
