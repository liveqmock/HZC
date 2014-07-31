package com.haozan.caipiao.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PredicateLayout;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.ArrayWheelAdapter;
import com.haozan.caipiao.widget.wheelview.adapter.DateArrayAdapter;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;
import com.umeng.analytics.MobclickAgent;

public class LotteryDiviningActivity
    extends BasicActivity
    implements OnClickListener {

    private boolean tryAgain = true;
    private boolean close = false;
    private Boolean clickHour = true;
    private Boolean clickAgain = false;
    private final Calendar calendar = Calendar.getInstance();
    private int mYear = calendar.get(Calendar.YEAR);
    private int mMonth = calendar.get(Calendar.MONTH) + 1;
    private int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    private int lastDay = mDay;
    private boolean scrolling = false;
    private String months[] = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String[] hours = {"(时辰) 00:00~00:59 (子)", "(时辰) 01:00~02:59 (丑)", "(时辰) 03:00~04:59 (寅)",
            "(时辰) 05:00~06:59 (卯)", "(时辰) 07:00~08:59 (辰)", "(时辰) 09:00~10:59 (巳)", "(时辰) 11:00~12:59 (午)",
            "(时辰) 13:00~14:59 (未)", "(时辰) 15:00~16:59 (申)", "(时辰) 17:00~18:59 (酉)", "(时辰) 19:00~20:59 (戌)",
            "(时辰) 21:00~22:59 (亥)", "(时辰) 23:00~23:59 (晚子)"};
    private String[] sexs = {"女", "男"};
    private Button lotteryDiviningSend;
    private Button lotteryDivingShare;
    private RelativeLayout lotteryDivingShareRala;
    private PredicateLayout luckyNum;
    private TextView mstar;
    private TextView todayLuckyText01;
    private TextView titleLuckyNum;
    private TextView lotteryDiviningWarning;
    private TextView lotteryDiviningTitle;
    private TextView lotteryDiviningTopTitle;
    private TextView getInf;
    private Button birthDay;
    private Button hoursSelected;
    private Button sexualitySelected;
    private Button selectNum;
    private RelativeLayout birthday_frame;
    private RelativeLayout textLy;

    private LinearLayout wheelLayout;
    private TextView wheelMakesure;
    private WheelView numWheel;
    private TextView makeSure;
    private TextView cancle;
    private TextView wayDec;
    private TextView secondTitle;

    private LinearLayout wheelCalenderLayout;
    private TextView calenderMakeSure;
    private TextView calendarCancle;
    private int wheelYear;
    private int wheelMonth;
    private int wheelDay;
    private int wheelHour;
    private int wheelSexPostion;
    private WheelView month;
    private WheelView year;
    private WheelView day;
    private String str_BirthDay;
    private String luckyLotteryNum = null;
    private String lotteryType;
    private String lotteryTypeZS = "0";
    private String str_shareInf;
    private String str_mstar;
    private String str_todayLuck;
    private BetListener betListener;
    private OnWheelChangedListener listener;
    private ProgressDialog progressDialog;
    private GetLuckyBallTask uwdt;
    private Editor databaseData;
    private SharedPreferences preferences;
    private Editor selectionSaved;
    private SharedPreferences dropDownSelectionpreferences;
    private int selectedHour;
    private int selectedSexPostion;
    private char selectedSexuality;

    private CustomDialog dlgShareContent;
    private View eventview;
    private EditText etShareContent;
    private LayoutInflater factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_divining);
        setUpView();
        init();
    }

    protected void setUpView() {
        setupWholeViews();
        setupDialogViews();
        setupWheelViews();
        setupWheelCanlenderViews();
    }

    private void setupWholeViews() {
        lotteryDiviningSend = (Button) findViewById(R.id.lottery_divning_send);
        lotteryDiviningSend.setOnClickListener(this);
        hoursSelected = (Button) findViewById(R.id.lottery_divning_birthhours);
        hoursSelected.setOnClickListener(this);
        sexualitySelected = (Button) findViewById(R.id.lottery_divning_sexuality);
        sexualitySelected.setOnClickListener(this);
        selectNum = (Button) findViewById(R.id.lottery_divning_select);
        selectNum.setOnClickListener(this);
        mstar = (TextView) findViewById(R.id.mstar);
        todayLuckyText01 = (TextView) findViewById(R.id.lottery_divining_back_todayluck_text01);
        titleLuckyNum = (TextView) findViewById(R.id.lottery_divining_title);
        lotteryDiviningWarning = (TextView) findViewById(R.id.lottery_divining_warning);
        lotteryDiviningTitle = (TextView) findViewById(R.id.lottery_divining_title);
        lotteryDiviningTopTitle = (TextView) findViewById(R.id.birth_day_hours);
        birthday_frame = (RelativeLayout) findViewById(R.id.lottery_birthday);
        textLy = (RelativeLayout) findViewById(R.id.lottery_divining_back_todayluck_text_frame);
        getInf = (TextView) findViewById(R.id.getting_inf);
        birthDay = (Button) findViewById(R.id.lottery_divning_birthday);
        birthDay.setOnClickListener(this);
        luckyNum = (PredicateLayout) findViewById(R.id.lottery_divining_back_luckynum);
        luckyNum.setOnClickListener(this);
        lotteryDivingShare = (Button) findViewById(R.id.click_to_share);
        lotteryDivingShareRala = (RelativeLayout) findViewById(R.id.analyse_tips_rala);
        lotteryDivingShare.setOnClickListener(this);
    }

    private void setupDialogViews() {
        factory = LayoutInflater.from(LotteryDiviningActivity.this);
        eventview = factory.inflate(R.layout.dlg_divining_share_inf, null);

        etShareContent = (EditText) eventview.findViewById(R.id.share_content);
    }

    private void setupWheelViews() {
        wheelLayout = (LinearLayout) this.findViewById(R.id.wheel_layout);
        wheelMakesure = (TextView) this.findViewById(R.id.calendar_make_sure);
        wheelMakesure.setOnClickListener(this);
        makeSure = (TextView) this.findViewById(R.id.make_sure);
        makeSure.setOnClickListener(this);
        cancle = (TextView) this.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        wayDec = (TextView) this.findViewById(R.id.wheel_dec);
        secondTitle = (TextView) this.findViewById(R.id.lottery_divining_back_mstar);
        secondTitle.setText("幸运选号");
        numWheel = (WheelView) this.findViewById(R.id.wheel_view);
        numWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateText(numWheel);
                }
            }
        });
    }

    private void setupWheelCanlenderViews() {
        wheelCalenderLayout = (LinearLayout) this.findViewById(R.id.wheel_calendar_layout);
        wheelCalenderLayout.setVisibility(View.GONE);
        calenderMakeSure = (TextView) this.findViewById(R.id.calendar_make_sure);
        calenderMakeSure.setOnClickListener(this);
        calendarCancle = (TextView) this.findViewById(R.id.calendar_cancle);
        calendarCancle.setOnClickListener(this);
        month = (WheelView) findViewById(R.id.calendar_month_wheel_view);
        month.setNormalTx("月");
        year = (WheelView) findViewById(R.id.calendar_year_wheel_view);
        year.setNormalTx("年");
        day = (WheelView) findViewById(R.id.calendar_day_wheel_view);
        day.setNormalTx("日");
    }

    private void updateDays() {
        birthDay.setText(mYear + "年" + mMonth + "月" + mDay + "日");
    }

    private void updateWheelDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 100 + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, lastDay - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
        wheelYear = calendar.get(Calendar.YEAR);
        wheelMonth = month.getCurrentItem() + 1;
        wheelDay = curDay;
    }

    protected void init() {
        lotteryType = this.getIntent().getExtras().getString("lotteryType");
        lotteryTypeZS = this.getIntent().getExtras().getString("lotteryTypeZS");
        databaseData = this.getSharedPreferences("user_birthday", 0).edit();
        preferences = this.getSharedPreferences("user_birthday", 0);

        selectionSaved = this.getSharedPreferences("savedSelection", 0).edit();
        listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateWheelDays(year, month, day);
            }
        };
        int dYear = preferences.getInt("mYear", -1);
        int dMonth = preferences.getInt("mMonth", -1);
        int dDay = preferences.getInt("mDay", -1);
        if (dYear == -1 || dMonth == -1 || dDay == -1) {
            mYear = 1985;
            mMonth = calendar.get(Calendar.MONTH) + 1;
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            lastDay = mDay;
            birthDay.setText(mYear + "年" + mMonth + "月" + mDay + "日");
        }
        else {
            mYear = dYear;
            mMonth = dMonth;
            mDay = dDay;
            lastDay = mDay;
            birthDay.setText(mYear + "年" + mMonth + "月" + mDay + "日");
        }
        dropDownSelectionpreferences = this.getSharedPreferences("savedSelection", 0);
        selectedHour = dropDownSelectionpreferences.getInt("birthHours", 0);
        hoursSelected.setText(hours[selectedHour]);
        selectedSexPostion = dropDownSelectionpreferences.getInt("sexuality", 0);
        if (selectedSexPostion == 0) {
            selectedSexuality = '0';
        }
        else if (selectedSexPostion == 1) {
            selectedSexuality = '1';
        }
        sexualitySelected.setText(sexs[selectedSexPostion]);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在测算中...");
        firstStatus();
        setLuckyNumTitle();
// switchBottomCalenderLayout();
    }

    private void switchBottomLayout() {
        lastDay = mDay;
        if (wheelCalenderLayout.isShown())
            wheelCalenderLayout.setVisibility(View.GONE);
        if (clickHour) {
            if (wheelLayout.isShown() && !clickAgain) {
                updateContentText();
                wheelLayout.setVisibility(View.GONE);
            }
            else {
                wayDec.setText("生辰选择");
                numWheel.setViewAdapter(new DateArrayAdapter(this, hours, selectedHour));
                numWheel.setCurrentItem(selectedHour);
                wheelLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (wheelLayout.isShown() && !clickAgain) {
                updateContentText();
                wheelLayout.setVisibility(View.GONE);
            }
            else {
                wayDec.setText("性别选择");
                numWheel.setViewAdapter(new DateArrayAdapter(this, sexs, selectedSexPostion));
                numWheel.setCurrentItem(selectedSexPostion, true);
                wheelLayout.setVisibility(View.VISIBLE);
            }
        }
        clickAgain = false;
    }

    private void switchBottomCalenderLayout() {
        lastDay = mDay;
        if (wheelLayout.isShown())
            wheelLayout.setVisibility(View.GONE);
        if (wheelCalenderLayout.isShown()) {
            wheelCalenderLayout.setVisibility(View.GONE);
            updateDays();
        }
        else {
            month.setViewAdapter(new DateArrayAdapter(this, months, mMonth - 1));
            month.setCurrentItem(mMonth - 1);
            month.addChangingListener(listener);

            int currentYear = 100 - calendar.get(Calendar.YEAR) + mYear;
            year.setViewAdapter(new DateNumericAdapter(this, calendar.get(Calendar.YEAR) - 100,
                                                       calendar.get(Calendar.YEAR), currentYear));
            year.setCurrentItem(currentYear);
            year.addChangingListener(listener);

            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, mDay - 1));
            day.setCurrentItem(mDay - 1);
            day.addChangingListener(listener);
            updateWheelDays(year, month, day);
            wheelCalenderLayout.setVisibility(View.VISIBLE);
        }
    }

    void setLuckyNumTitle() {
        if (lotteryType.equals("ssq")) {
            titleLuckyNum.setText("双色球幸运号码:");
        }
        else if (lotteryType.equals("3d")) {
            if (lotteryTypeZS.equals("sdzx")) {
                titleLuckyNum.setText("3D直选幸运号码:");
            }
            else if (lotteryTypeZS.equals("sdzs")) {
                titleLuckyNum.setText("3D组三包号幸运号码:");
            }
            else if (lotteryTypeZS.equals("sdzl")) {
                titleLuckyNum.setText("3D组六包号幸运号码:");
            }
            else if (lotteryTypeZS.equals("sdzsdf")) {
                titleLuckyNum.setText("3D组三单复式幸运号码:");
            }
        }
        else if (lotteryType.equals("qlc")) {
            titleLuckyNum.setText("七乐彩幸运号码:");
        }
        else if (lotteryType.equals("dfljy")) {
            titleLuckyNum.setText("东方6+1幸运号码:");
        }
        else if (lotteryType.equals("swxw")) {
            titleLuckyNum.setText("15选5幸运号码:");
        }
        else if (lotteryType.equals("ssl")) {
            if (lotteryTypeZS.equals("1")) {
                titleLuckyNum.setText("时时乐直选幸运号码:");
            }
            if (lotteryTypeZS.equals("2")) {
                titleLuckyNum.setText("时时乐组三幸运号码:");
            }
            if (lotteryTypeZS.equals("3")) {
                titleLuckyNum.setText("时时乐组六（包号）幸运号码:");
            }
            if (lotteryTypeZS.equals("4")) {
                titleLuckyNum.setText("时时乐前二幸运号码:");
            }
            if (lotteryTypeZS.equals("5")) {
                titleLuckyNum.setText("时时乐后二幸运号码:");
            }
            if (lotteryTypeZS.equals("6")) {
                titleLuckyNum.setText("时时乐前一幸运号码:");
            }
            if (lotteryTypeZS.equals("7")) {
                titleLuckyNum.setText("时时乐后一幸运号码:");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wheelLayout.isShown())
                switchBottomLayout();
            else if (wheelCalenderLayout.isShown())
                switchBottomCalenderLayout();
            else {
                finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(LotteryDiviningActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                 R.anim.push_to_left_out);
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void updateText(WheelView item) {
        ArrayWheelAdapter<String> adapter = (ArrayWheelAdapter<String>) item.getViewAdapter();
        int current = item.getCurrentItem();
        if (clickHour) {
            wheelHour = current;
        }
        else {
            wheelSexPostion = current;
        }
    }

    private void updateContentText() {
        if (clickHour) {
            hoursSelected.setText(hours[selectedHour]);
            selectionSaved.putInt("birthHours", selectedHour);
            selectionSaved.commit();
        }
        else {
            sexualitySelected.setText(sexs[selectedSexPostion]);
            selectionSaved.putInt("sexuality", selectedSexPostion);
            selectionSaved.commit();
            if (selectedSexPostion == 0) {
                selectedSexuality = '0';
            }
            else if (selectedSexPostion == 1) {
                selectedSexuality = '1';
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lottery_divning_birthday)
            switchBottomCalenderLayout();
        else if (v.getId() == R.id.calendar_make_sure) {
            mYear = wheelYear;
            mMonth = wheelMonth;
            mDay = wheelDay;
            databaseData.putInt("mYear", mYear);
            databaseData.putInt("mMonth", mMonth);
            databaseData.putInt("mDay", mDay);
            databaseData.commit();
            updateDays();
            switchBottomCalenderLayout();
        }
        else if (v.getId() == R.id.calendar_cancle) {
            switchBottomCalenderLayout();
        }
        else if (v.getId() == R.id.lottery_divning_birthhours) {
            if (!clickHour)
                clickAgain = true;
            clickHour = true;
            switchBottomLayout();
        }
        else if (v.getId() == R.id.lottery_divning_sexuality) {
            if (clickHour)
                clickAgain = true;
            clickHour = false;
            switchBottomLayout();
        }
        else if (v.getId() == R.id.make_sure) {
            ArrayWheelAdapter<String> adapter = (ArrayWheelAdapter<String>) numWheel.getViewAdapter();
            String content = (String) adapter.getItemText(numWheel.getCurrentItem());
            if (clickHour) {
                selectedHour = wheelHour;
                hoursSelected.setText(content);
            }
            else {
                selectedSexPostion = wheelSexPostion;
                sexualitySelected.setText(content);
            }
            switchBottomLayout();
        }
        else if (v.getId() == R.id.cancle) {
            switchBottomLayout();
        }
        else if (v.getId() == R.id.lottery_divning_send) {

            if (checkInput()) {
                if (tryAgain) {
                    if (HttpConnectUtil.isNetworkAvailable(this)) {
                        getInf.setVisibility(View.VISIBLE);
                        wheelLayout.setVisibility(View.GONE);
                        lotteryDiviningTopTitle.setVisibility(View.GONE);
                        lotteryDiviningSend.setEnabled(false);
                        tryAgain = false;
                        close = false;
                        wheelCalenderLayout.setVisibility(View.GONE);
                        birthday_frame.setVisibility(View.GONE);
                        mstar.setVisibility(View.VISIBLE);
                        todayLuckyText01.setVisibility(View.VISIBLE);
                        uwdt = new GetLuckyBallTask();
                        uwdt.execute(0);
                    }
                    else {
                        ViewUtil.showTipsToast(this, noNetTips);
                    }
                }
                else {
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    StringtoVector(luckyLotteryNum, lotteryType);
                    b.putIntArray("luckyNumArray", vetor);
                    b.putString("mstar", str_mstar);
                    b.putString("today_lucky", str_todayLuck);
                    i.putExtras(b);
                    this.setResult(RESULT_OK, i);
                    tryAgain = true;
                    this.finish();
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(LotteryDiviningActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                     R.anim.push_to_left_out);
                    }
                }
            }
            else {
                ViewUtil.showTipsToast(this, "请输入生日");
            }
        }
        else if (v.getId() == R.id.lottery_divning_select) {
            Intent i = new Intent();
            Bundle b = new Bundle();
            StringtoVector(luckyLotteryNum, lotteryType);
            b.putIntArray("luckyNumArray", vetor);
            b.putString("mstar", str_mstar);
            b.putString("today_lucky", str_todayLuck);
            i.putExtras(b);
            this.setResult(RESULT_OK, i);
            tryAgain = true;
            this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(LotteryDiviningActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                             R.anim.push_to_left_out);
            }
        }
        else if (v.getId() == R.id.click_to_share) {
            showShareContentDialog();
        }
    }

    private void showShareContentDialog() {
        if (dlgShareContent == null) {
            etShareContent.setText(getShareInf());
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
            customBuilder.setTitle("分享信息").setContentView(eventview).setMessage(etShareContent.getText().toString()).setPositiveButton("分  享",
                                                                                                                                       new DialogInterface.OnClickListener() {
                                                                                                                                           public void onClick(DialogInterface dialog,
                                                                                                                                                               int which) {
                                                                                                                                               Intent intent =
                                                                                                                                                   new Intent(
                                                                                                                                                              Intent.ACTION_SEND);
                                                                                                                                               intent.setType("text/plain");
                                                                                                                                               intent.putExtra(Intent.EXTRA_SUBJECT,
                                                                                                                                                               "分享");
                                                                                                                                               intent.putExtra(Intent.EXTRA_TEXT,
                                                                                                                                                               etShareContent.getText().toString());
                                                                                                                                               LotteryDiviningActivity.this.startActivity(Intent.createChooser(intent,
                                                                                                                                                                                                               "分享"));
                                                                                                                                           }
                                                                                                                                       }).setNegativeButton("取  消",
                                                                                                                                                            new DialogInterface.OnClickListener() {
                                                                                                                                                                public void onClick(DialogInterface dialog,
                                                                                                                                                                                    int which) {
                                                                                                                                                                    dialog.dismiss();
                                                                                                                                                                }
                                                                                                                                                            });
            dlgShareContent = customBuilder.create();
        }
        dlgShareContent.show();
    }

    public String getShareInf() {
        if (todayLuckyText01.getText().toString().length() < 90) {
            return titleLuckyNum.getText().toString() + str_shareInf + "\n" +
                todayLuckyText01.getText().toString();
        }
        else {
            return titleLuckyNum.getText().toString() + str_shareInf + "\n" +
                todayLuckyText01.getText().toString().substring(0, 90) + "...";
        }
    }

    public boolean checkInput() {
        if (mYear == -1 || mMonth == -1 || mDay == -1) {
            return false;
        }
        else if (birthDay.getText().toString() == null || birthDay.getText().toString().equals("")) {
            return false;
        }
        else {
            return true;
        }
    }

    public void firstStatus() {
        tryAgain = true;
        birthday_frame.setVisibility(View.VISIBLE);
        luckyNum.setVisibility(View.GONE);
        lotteryDivingShareRala.setVisibility(View.GONE);
        mstar.setVisibility(View.GONE);
        todayLuckyText01.setVisibility(View.GONE);
        lotteryDiviningTitle.setVisibility(View.GONE);
        lotteryDiviningSend.setEnabled(true);
        lotteryDiviningSend.setText("开始测算");
        mstar.setText(null);
        todayLuckyText01.setText(null);
        progressDialog.dismiss();
        getInf.setVisibility(View.GONE);
        lotteryDiviningTopTitle.setVisibility(View.VISIBLE);
        lotteryDiviningWarning.setVisibility(View.GONE);
        textLy.setVisibility(View.GONE);
        secondTitle.setVisibility(View.GONE);
    }

    class GetLuckyBallTask
        extends AsyncTask<Integer, Object, String> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_augur");
            parameter.put("pid", LotteryUtils.getPid(LotteryDiviningActivity.this));
            parameter.put("lottery_id", lotteryType);
            parameter.put("birthday", mYear + "-" + mMonth + "-" + mDay);
            parameter.put("hour", "" + selectedHour);
            parameter.put("sex", "" + selectedSexuality);
            return parameter;
        }

        @Override
        protected String doInBackground(Integer... params) {
            ConnectService connectNet = new ConnectService(LotteryDiviningActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String inf = null;
            if (result == null) {
                inf = getResources().getString(R.string.inf_getting_fail);
                ViewUtil.showTipsToast(LotteryDiviningActivity.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, "response_data");
                    try {
                        JSONObject jo = new JSONObject(data);

                        if (lotteryTypeZS.equals("sdzs")) {
                            luckyLotteryNum = jo.getString("lucknum").substring(0, 3);
                        }
                        else if (lotteryTypeZS.equals("2") || lotteryTypeZS.equals("sdzsdf")) {
                            luckyLotteryNum =
                                new StringBuilder().append(jo.getString("lucknum")).delete(1, 3).toString();
                        }
                        else if (lotteryTypeZS.equals("4")) {
                            luckyLotteryNum = jo.getString("lucknum").substring(0, 3);
                        }
                        else if (lotteryTypeZS.equals("5")) {
                            luckyLotteryNum = jo.getString("lucknum").substring(2, 5);
                        }
                        else if (lotteryTypeZS.equals("6")) {
                            luckyLotteryNum = jo.getString("lucknum").substring(0, 1);
                        }
                        else if (lotteryTypeZS.equals("7")) {
                            luckyLotteryNum =
                                jo.getString("lucknum").substring(jo.getString("lucknum").length() - 1,
                                                                  jo.getString("lucknum").length());
                        }
                        else {
                            luckyLotteryNum = jo.getString("lucknum");
                        }
                        str_shareInf = luckyLotteryNum;
                        if (lotteryType.equals("dfljy")) {
                            LotteryUtils.drawBallsAnimalsNumber(LotteryDiviningActivity.this, luckyNum,
                                                                luckyLotteryNum, lotteryType);
                            str_shareInf = getDFLJYString(luckyLotteryNum);
                        }
                        else {
                            LotteryUtils.drawBallsLargeNumber(LotteryDiviningActivity.this, luckyNum,
                                                              luckyLotteryNum, lotteryType);
                        }
                        str_mstar = jo.getString("mstar");
                        mstar.setText(str_mstar);
                        JSONObject jow = new JSONObject(jo.getString("todayluck"));
                        todayLuckyText01.setText("(1)" + jow.getString("1") + "\n" + "(2)" +
                            jow.getString("2"));
                        str_todayLuck = jow.getString("1") + jow.getString("2");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        inf = getResources().getString(R.string.inf_getting_fail);
                        ViewUtil.showTipsToast(LotteryDiviningActivity.this, inf);
                    }
                    if (!close) {
                        getInf.setVisibility(View.GONE);
                        textLy.setVisibility(View.VISIBLE);
                        secondTitle.setVisibility(View.VISIBLE);
                        lotteryDiviningWarning.setVisibility(View.VISIBLE);
                        lotteryDivingShareRala.setVisibility(View.VISIBLE);
                        if (LotteryUtils.isBindSina(LotteryDiviningActivity.this) == true) {
                            lotteryDivingShareRala.setVisibility(View.GONE);
                        }
                        lotteryDiviningSend.setText("选号");
                        lotteryDiviningTitle.setVisibility(View.VISIBLE);
                        luckyNum.setVisibility(View.VISIBLE);
                        lotteryDiviningSend.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }
                else {
                    inf = getResources().getString(R.string.inf_getting_fail);
                    ViewUtil.showTipsToast(LotteryDiviningActivity.this, inf);
                    progressDialog.dismiss();
                    firstStatus();
                }
            }
            if (inf != null) {
                ViewUtil.showTipsToast(LotteryDiviningActivity.this, inf);
                progressDialog.dismiss();
                firstStatus();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
    }

    public void setBetListener(BetListener betListener) {
        this.betListener = betListener;
    }

    public interface BetListener {
        public void setBetListener(String luckyNum, String type, String shareInf, String str_mstar,
                                   String str_todayLuck);
    }

    public String getDFLJYString(String joString) {
        String jsonString01;
        String josonString[] = joString.split("\\|");
        jsonString01 = josonString[0] + "|" + LotteryUtils.animals[Integer.valueOf(josonString[1]) - 1];
        return jsonString01;
    }

    public void StringtoVector(String luckynum, String type) {
        if (type.equals("ssq") || type.equals("dfljy")) {
            num = luckynum.split("\\|");
            num02 = num[0].split("\\,");
            count = num02.length + num[1].length();
        }
        else {
            num = new String[1];
            num[0] = luckynum;
            num02 = num[0].split("\\,");
            count = num02.length;
        }

        vetor = new int[count];
        for (int j = 0; j < num02.length; j++) {
            vetor[j] = Integer.valueOf(num02[j]);
        }

        if (type.equals("ssq") || type.equals("dfljy")) {
            vetor[num02.length] = Integer.valueOf(num[1]);
        }
    }

    protected int[] vetor;
    protected String[] num;
    protected String[] num02;
    private int count;

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open divinig");
        String eventName = "open divinig";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_divining";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
