package com.haozan.caipiao.activity.bet.jczq;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.umeng.analytics.MobclickAgent;

public class JCZQBasicActivity
    extends BasicActivity
    implements OnClickListener {
    public static final String[] WEEKDAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private static final String[] KIND = {"norfspf", "spf", "zjq", "bqc", "bf", "norfspf_dg", "spf_dg",
            "zjq_dg", "bqc_dg", "bf_dg"};
    protected static final String[] PLAYTYPE_STR = {"  购 彩  ", "", "  合 买  "};
    protected TextView betInf;
    protected ImageView clear;
    protected Button betBt;
    protected TextView title;
    protected LinearLayout helpLin;
    protected ProgressBar progressBar;
    protected String betCode = null;
    protected TextView tv_zhu_sign;

// private LinearLayout firstIcon;
// private LinearLayout secondIcon;
// private LinearLayout thirdIcon;
// private LinearLayout forthIcon;
// protected TextView jczqSpf;
// protected TextView jczqZjq;
// protected TextView jczqBqc;
// protected TextView jczqBf;
// private LinearLayout firstIcon_dg;
// private LinearLayout secondIcon_dg;
// private LinearLayout thirdIcon_dg;
// private LinearLayout forthIcon_dg;
// protected TextView jczqSpf_dg;
// protected TextView jczqZjq_dg;
// protected TextView jczqBqc_dg;
// protected TextView jczqBf_dg;

// protected LinearLayout[] icons = {firstIcon, secondIcon, thirdIcon, forthIcon, firstIcon_dg, secondIcon_dg,
// thirdIcon_dg, forthIcon_dg};
    protected LinearLayout[] icons = new LinearLayout[10];
    private int[] icons_id = {R.id.item_zero_layout, R.id.item_first_layout, R.id.item_second_layout,
            R.id.item_third_layout, R.id.item_forth_layout, R.id.item_zero_layout_dg,
            R.id.item_first_layout_dg, R.id.item_second_layout_dg, R.id.item_third_layout_dg,
            R.id.item_forth_layout_dg};

// protected TextView[] tvs = {jczqSpf,jczqZjq,jczqBqc,jczqBf,jczqSpf_dg,jczqZjq_dg,jczqBqc_dg,jczqBf_dg};
    protected TextView[] tvs = new TextView[10];
    private int[] tvs_id = {R.id.jczq_norfspf, R.id.jczq_spf, R.id.jczq_zjq, R.id.jczq_bqc, R.id.jczq_bf,
            R.id.jczq_norfspf_dg, R.id.jczq_spf_dg, R.id.jczq_zjq_dg, R.id.jczq_bqc_dg, R.id.jczq_bf_dg};

    protected CustomExpandleListView lv;
    protected ImageView topArrow;

    protected String kind = "jczq";
    protected int lotteryType = 1;
    protected int lotteryTypeInit = 0;
    protected String[] playName = {"足球-过关胜平负", "足球-过关让分胜平负", "足球-过关总进球", "足球-过关半全场", "足球-过关比分", "足球-单关胜平负",
            "足球-单关让分胜平负", "足球-单关总进球", "足球-单关半全场", "足球-单关比分"};
    protected String[] playStr = {"jczq_norfspf", "jczq_spf", "jczq_zjq", "jczq_bqc", "jczq_bf",
            "jczq_norfspf_dg", "jczq_spf_dg", "jczq_zjq_dg", "jczq_bqc_dg", "jczq_bf_dg"};
    protected String[] lotteryStr = {"jczq|75", "jczq|71", "jczq|72", "jczq|74", "jczq|73"};
    protected boolean isGuoguan = true;
    protected String[] playID = {"75", "71", "72", "74", "73", "75", "71", "72", "74", "73"};

    // 合买相关
    protected Button imgShowBet;
    protected int betWay = 1;// 1.购彩；3.合买
    protected PopupWindow betWayPopupWindow;
    // 彩种是否具有合买功能合买
    protected boolean isUnite = true;
    // 是否发起合买
    protected boolean doUnite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jczq);
        initData();
        setupViews();
        init();
    }

    private void init() {
        orgBetWay();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
// int type = bundle.getInt("bet_way");
            String lotteryString = bundle.getString("title");
            betCode = bundle.getString("bet_code");
            if (betCode != null && !betCode.equals("")) {
                String pass = betCode.split("\\:")[2];
                if (!pass.equals("100")) {
                    isGuoguan = true;// 过关
                    if (!lotteryString.equals("") && lotteryString != null) {
                        // 设置从其他地方过来的无法修改玩法
                        // termLayout.setEnabled(false);
                        for (int i = 0; i < 5; i++) {
                            if (lotteryString.equals(lotteryStr[i])) {
                                lotteryType = i + 1;
                                lotteryTypeInit = i + 1;
                                databaseData.putString("jczq_way", playStr[i]);
                                break;
                            }
                        }
                        databaseData.commit();
                    }
                    else {
                        resetLotteryType();
                    }
                }
                else {
                    isGuoguan = false;// 单关
                    if (!lotteryString.equals("") && lotteryString != null) {
                        // 设置从其他地方过来的无法修改玩法
                        // termLayout.setEnabled(false);
                        for (int i = 5; i < 10; i++) {
                            if (lotteryString.equals(lotteryStr[i - 5])) {
                                lotteryType = i + 1;
                                lotteryTypeInit = i + 1;
                                databaseData.putString("jczq_way", playStr[i]);
                                break;
                            }
                        }

                        databaseData.commit();
                    }
                    else {
                        resetLotteryType();
                    }

                }
            }
            else {
                resetLotteryType();
            }
        }
        else {
            resetLotteryType();
        }
        showWayInit();
        if (bundle != null) {
            Boolean fromHall = bundle.getBoolean("from_hall", false);
            if (!fromHall) {
                // 设置从其他地方过来的无法修改玩法
// termLayout.setEnabled(false);
            }
        }
    }

    public void showWayInit() {
        for (int i = 0; i < 10; i++) {
            if (lotteryType == i + 1) {
                title.setText(playName[i]);
            }
        }
    }

    private void resetLotteryType() {
        String jczqWay = preferences.getString("jczq_way", "jczq_spf");
        for (int i = 0; i < 10; i++) {
            if (jczqWay.equals(playStr[i])) {
                lotteryType = i + 1;
                if (i < 5) {
                    isGuoguan = true;
                }
                else {
                    isGuoguan = false;
                }
                break;
            }
        }
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null)
            return;
        kind = bundle.getString("kind");
    }

    private void orgBetWay() {
        betWay = preferences.getInt("bet_way_jczq", 1);
        for (int i = 0; i < PLAYTYPE_STR.length; i++) {
            if (betWay == (i + 1)) {
                betBt.setText(PLAYTYPE_STR[i]);
                break;
            }
        }
        if (betWay == 3) {
            doUnite = true;
        }
    }

    private void setupViews() {
        // add by vincent
        title = (TextView) this.findViewById(R.id.bet_title);
        helpLin = (LinearLayout) this.findViewById(R.id.bet_help_lin);
        helpLin.setOnClickListener(this);
        lv = (CustomExpandleListView) this.findViewById(R.id.match_list);
        clear = (ImageView) this.findViewById(R.id.bet_clear_button);
        clear.setOnClickListener(this);
        betInf = (TextView) this.findViewById(R.id.bet_inf);
        betBt = (Button) this.findViewById(R.id.bet_button);
        betBt.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_large);
        topArrow = (ImageView) this.findViewById(R.id.arrow_top);
        tv_zhu_sign = (TextView) this.findViewById(R.id.tv_jczq_zhusign);
        imgShowBet = (Button) this.findViewById(R.id.img_show_bet_way);
        imgShowBet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showBetWayPopupViews();
            }
        });
    }

    protected void showBetWayPopupViews() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = null;
        waySwitchLayout = mLayoutInflater.inflate(R.layout.popup_bet_way_swtich, null);

        TextView betUnite = (TextView) waySwitchLayout.findViewById(R.id.bet_add);// 合买
        betUnite.setText("合  买");
        betUnite.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (betWay != 3) {
                    betWay = 3;
                    doUnite = true;
                    databaseData.putInt("bet_way_jczq", betWay);
                    databaseData.commit();
                    betBt.setText("  合 买  ");
                }
                betWayPopupWindow.dismiss();
            }
        });

        TextView betDirectly = (TextView) waySwitchLayout.findViewById(R.id.bet_directly);
        betDirectly.setText("购  彩");
        betDirectly.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (betWay != 1) {
                    betWay = 1;
                    doUnite = false;
                    databaseData.putInt("bet_way_jczq", betWay);
                    databaseData.commit();
                    betBt.setText("  购 彩  ");
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
            betUnite.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betUnite.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        else if (betWay == 3) {// 合买
            betUnite.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            betUnite.setTextColor(getResources().getColor(R.color.white));
            betDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            betDirectly.setTextColor(getResources().getColor(R.color.dark_purple));
        }
        betWayPopupWindow.showAsDropDown(betBt, 0, -4 * betBt.getHeight());
    }

    protected void initDefault(View view) {
        for (int i = 0; i < icons.length; i++) {
            icons[i] = (LinearLayout) view.findViewById(icons_id[i]);
            tvs[i] = (TextView) view.findViewById(tvs_id[i]);
        }
    }

    protected void iniSelectedItemBg() {
        for (int i = 0; i < icons.length; i++) {
            icons[i].setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
        }
    }

    protected void initIcon(int LuckyType) {
        for (int i = 0; i < icons.length; i++) {
            if (LuckyType == i + 1) {
                icons[i].setBackgroundResource(R.drawable.bet_popup_item_choosed);
                tvs[i].setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    protected void bet() {

    };

    protected void clear() {

    };

    protected void enableClearBtn() {
        clear.setEnabled(true);
    }

    protected void disableClearBtn() {
        clear.setEnabled(false);
    }

    protected void enableBetBtn() {
        betBt.setEnabled(true);
    }

    protected void disableBetBtn() {
        betBt.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bet_button) {
            bet();
        }
        else if (v.getId() == R.id.bet_clear_button) {
            clear();
        }
        else if (v.getId() == R.id.bet_help_lin) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "竞彩足球游戏规则");
            bundel.putString("lottery_help", "help_new/jczq.html");
            intent.putExtras(bundel);
            intent.setClass(JCZQBasicActivity.this, LotteryWinningRules.class);
            startActivity(intent);
        }

    }

    protected void showPopView() {
        // TODO Auto-generated method stub
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
        popup.showAtLocation(title, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
                             findViewById(R.id.layout_title).getHeight() + 24);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    public class GetLotteryInfTask
        extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            ConnectService connect = new ConnectService(JCZQBasicActivity.this);
            String json = null;
            try {
                json = connect.getJsonGet(5, false, initHashMap(params[0], params[1], params[2]));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            progressBar.setVisibility(View.GONE);
            getMatchData(json);
            showWay();
            if (lotteryType == lotteryTypeInit) {
                initChoosed(betCode);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void showWay() {
    }

    private HashMap<String, String> initHashMap(String kind, String play, String pass)
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009020");
        parameter.put("pid", LotteryUtils.getPid(JCZQBasicActivity.this));
        parameter.put("lottery_id", kind);
        parameter.put("play", play);
        parameter.put("pass", pass);
        return parameter;
    }

    protected void getMatchData(String json) {
        // TODO Auto-generated method stub

    }

    protected void initChoosed(String betCode) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet");
        map.put("more_inf", "open bet " + kind);
        map.put("extra_inf", "jczq" + KIND[lotteryType - 1]);
        String eventName = "v2 open bet";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }
}
