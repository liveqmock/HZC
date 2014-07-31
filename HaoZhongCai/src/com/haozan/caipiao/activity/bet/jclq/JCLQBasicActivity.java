package com.haozan.caipiao.activity.bet.jclq;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.umeng.analytics.MobclickAgent;

public class JCLQBasicActivity
    extends BasicActivity
    implements OnClickListener {
    public static final String[] WEEKDAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private static final String[] KIND = {"sf", "rfsf", "dxf", "sf_dg", "rfsf_dg", "dxf_dg"};
    protected TextView betInf;
    protected ImageView clear;
    protected Button betBt;
    protected TextView title;
    protected LinearLayout helpLin;
    protected ProgressBar progressBar;
    protected String betCode = null;
    protected RelativeLayout rl_term;

    protected RelativeLayout analyseTipsRa;
    protected RelativeLayout shakeRela;

// private LinearLayout firstIcon;
// private LinearLayout secondIcon;
// private LinearLayout thirdIcon;
// protected TextView jclqSf;
// protected TextView jclqRfsf;
// protected TextView jclqDxf;
    protected LinearLayout[] icons = new LinearLayout[6];
    protected TextView[] tvs = new TextView[6];
    private int[] icons_id = {R.id.item_first_layout, R.id.item_second_layout, R.id.item_third_layout,
            R.id.item_first_layout_dg, R.id.item_second_layout_dg, R.id.item_third_layout_dg};
    private int[] tvs_id = {R.id.jclq_sf, R.id.jclq_rfsf, R.id.jclq_dxf, R.id.jclq_sf_dg, R.id.jclq_rfsf_dg,
            R.id.jclq_dxf_dg};
    protected ImageView shake;
    protected ImageView topArrow;
    protected TextView tv_zhu_sign;
// protected RelativeLayout topBgLinear;

    protected CustomExpandleListView lv;

    protected String kind = "jclq";
    protected RelativeLayout termLayout;
    protected int lotteryType = 1;
    protected int lotteryTypeInit = 0;
    protected String[] playName = {"篮球-过关胜负", "篮球-过关让分胜负", "篮球-过关大小分", "篮球-单关胜负", "篮球-单关让分胜负", "篮球-单关大小分"};
    protected String[] playStr = {"jclq_sf", "jclq_rfsf", "jclq_dxf", "jclq_sf_dg", "jclq_rfsf_dg",
            "jclq_dxf_dg"};
    protected String[] lotteryStr = {"jclq|82", "jclq|81", "jclq|84"};
    protected boolean isGuoguan = true;
    protected String[] playID = {"82", "81", "84", "82", "81", "84"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jclq);
        initData();
        setupViews();
        init();
    }

    private void init() {
        shake.setVisibility(View.INVISIBLE);
        rl_term.setVisibility(View.GONE);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String lotteryString = bundle.getString("title");
            betCode = bundle.getString("bet_code");
            if (betCode != null && !betCode.equals("")) {
                String pass = betCode.split("\\:")[2];
                if (!pass.equals("100")) {
                    isGuoguan = true;
                    if (!lotteryString.equals("") && lotteryString != null) {
                        for (int i = 0; i < 3; i++) {
                            if (lotteryString.equals(lotteryStr[i])) {
                                lotteryType = i + 1;
                                lotteryTypeInit = i + 1;
                                databaseData.putString("jclq_way", playStr[i]);
                                break;
                            }
                        }
                        /*
                         * if (lotteryString.equals("jclq|82")) { lotteryType = 1; lotteryTypeInit = 1;
                         * databaseData.putString("jclq_way", "jclq_sf"); } else if
                         * (lotteryString.equals("jclq|81")) { lotteryType = 2; lotteryTypeInit = 2;
                         * databaseData.putString("jclq_way", "jclq_rfsf"); } else if
                         * (lotteryString.equals("jclq|84")) { lotteryType = 3; lotteryTypeInit = 3;
                         * databaseData.putString("jclq_way", "jclq_dxf"); }
                         */
                        databaseData.commit();
                    }
                    else {
                        resetLotteryType();
                    }
                }
                else {
                    isGuoguan = false;
                    if (!lotteryString.equals("") && lotteryString != null) {
                        // 设置从其他地方过来的无法修改玩法
                        // termLayout.setEnabled(false);
                        for (int i = 3; i < 6; i++) {
                            if (lotteryString.equals(lotteryStr[i - 3])) {
                                lotteryType = i + 1;
                                lotteryTypeInit = i + 1;
                                databaseData.putString("jclq_way", playStr[i]);
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
        for (int i = 0; i < 6; i++) {
            if (lotteryType == i + 1) {
                title.setText(playName[i]);
            }
        }
        /*
         * if (lotteryType == 1) { title.setText("竞彩篮球（胜负）"); } else if (lotteryType == 2) {
         * title.setText("竞彩篮球（让分胜负）"); } else if (lotteryType == 3) { title.setText("竞彩篮球（大小分）"); }
         */
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
                             findViewById(R.id.top).getHeight() + 24);
    }

    private void resetLotteryType() {
        String jclqWay = preferences.getString("jclq_way", "jclq_sf");
        for (int i = 0; i < 6; i++) {
            if (jclqWay.equals(playStr[i])) {
                lotteryType = i + 1;
                if (i < 3) {
                    isGuoguan = true;
                }
                else {
                    isGuoguan = false;
                }
                break;
            }
        }
        /*
         * if (jclqWay.equals("jclq_sf")) { lotteryType = 1; } else if (jclqWay.equals("jclq_rfsf")) {
         * lotteryType = 2; } else if (jclqWay.equals("jclq_dxf")) { lotteryType = 3; }
         */
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null)
            return;
        kind = bundle.getString("kind");
    }

    protected void setupViews() {
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
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
        shake = (ImageView) findViewById(R.id.bet_shake);
        topArrow = (ImageView) this.findViewById(R.id.arrow_top);
        rl_term = (RelativeLayout) this.findViewById(R.id.rl_term);
        tv_zhu_sign = (TextView) this.findViewById(R.id.tv_jclq_zhusign);
// topBgLinear= (RelativeLayout)findViewById(R.id.top_bg_linear);
        analyseTipsRa = (RelativeLayout) this.findViewById(R.id.analyse_tips_rala);
        analyseTipsRa.setVisibility(View.GONE);
        shakeRela = (RelativeLayout) findViewById(R.id.rela_bet_shake);
        shakeRela.setVisibility(View.GONE);

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
        for (int i = 0; i < tvs.length; i++) {
            if (LuckyType == i + 1) {
                icons[i].setBackgroundResource(R.drawable.bet_popup_item_choosed);
                tvs[i].setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    protected void showPopView() {
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
            bundel.putString("lottery_name", "竞彩篮球游戏规则");
            // TODO 待修改成精彩蓝球
            bundel.putString("lottery_help", "help_new/jclq.html");
            intent.putExtras(bundel);
            intent.setClass(JCLQBasicActivity.this, LotteryWinningRules.class);
            startActivity(intent);
        }
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
            ConnectService connect = new ConnectService(JCLQBasicActivity.this);
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
            initChoosed(betCode);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void initChoosed(String betCode) {
    }

    private HashMap<String, String> initHashMap(String kind, String play, String pass)
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        // TODO 参数待修改
        parameter.put("service", "2009020");
        parameter.put("pid", LotteryUtils.getPid(JCLQBasicActivity.this));
        parameter.put("lottery_id", kind);
        parameter.put("play", play);
        parameter.put("pass", pass);
        return parameter;
    }

    protected void showWay() {
    }

    protected void getMatchData(String json) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet");
        map.put("more_inf", "open bet " + kind);
        map.put("extra_inf", "jclq" + KIND[lotteryType - 1]);
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
