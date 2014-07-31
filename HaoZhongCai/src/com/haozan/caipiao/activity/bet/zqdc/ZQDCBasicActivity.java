package com.haozan.caipiao.activity.bet.zqdc;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ZQDCBasicActivity
    extends BasicActivity
    implements OnClickListener {
    public static final String[] WEEKDAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    protected TextView betInf;
    protected Button clear;
    protected Button betBt;
    protected TextView title;
    protected ImageView help;
    protected ProgressBar progressBar;
    protected String betCode = null;

    private LinearLayout firstIcon;
    private LinearLayout secondIcon;
    private LinearLayout thirdIcon;
    private LinearLayout forthIcon;
    private LinearLayout fifthIcon;
    private TextView zqdcSpf;
    private TextView zqdcZjq;
    private TextView zqdcBqc;
    private TextView zqdcSxds;
    private TextView zqdcBf;

    protected CustomExpandleListView lv;

    protected String kind;
    protected int lotteryType = 1;
    protected int lotteryTypeInit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zqdc);
        initData();
        setupViews();
        // add by vincent
        init();
    }

    // add by vincent
    private void init() {
        // TODO Auto-generated method stub
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
// int type = bundle.getInt("bet_way");
            String lotteryString = bundle.getString("title");

            if (!lotteryString.equals("") && lotteryString != null) {
                // 设置从其他地方过来的无法修改玩法
//                termLayout.setEnabled(false);
                //TODO
                if (lotteryString.equals("zqdc|")) {
                    lotteryType = 1;
                    lotteryTypeInit = 1;
                    databaseData.putString("zqdc_way", "zqdc_spf");
                }
                else if (lotteryString.equals("zqdc|")) {
                    lotteryType = 2;
                    lotteryTypeInit = 2;
                    databaseData.putString("zqdc_way", "zqdc_zjq");
                }
                else if (lotteryString.equals("zqdc|")) {
                    lotteryType = 3;
                    lotteryTypeInit = 3;
                    databaseData.putString("zqdc_way", "zqdc_bqc");
                }
                else if (lotteryString.equals("zqdc|")) {
                    lotteryType = 4;
                    lotteryTypeInit = 4;
                    databaseData.putString("zqdc_way", "zqdc_sxds");
                }
                else if (lotteryString.equals("zqdc|")) {
                    lotteryType = 5;
                    lotteryTypeInit = 5;
                    databaseData.putString("zqdc_way", "zqdc_bf");
                }
                databaseData.commit();
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
//                termLayout.setEnabled(false);
            }
        }
    }

    public void showWayInit() {
        if (lotteryType == 1) {
            title.setText("足球单场（胜平负）");
        }
        else if (lotteryType == 2) {
            title.setText("足球单场（总进球）");
        }
        else if (lotteryType == 3) {
            title.setText("足球单场（半全场）");
        }
        else if (lotteryType == 4) {
            title.setText("足球单场（上下单双）");
        }
        else if (lotteryType == 5) {
            title.setText("足球单场（比分）");
        }
    }

    private void resetLotteryType() {
        String zqdcWay = preferences.getString("zqdc_way", "zqdc_spf");
        if (zqdcWay.equals("zqdc_spf")) {
            lotteryType = 1;
        }
        else if (zqdcWay.equals("zqdc_zjq")) {
            lotteryType = 2;
        }
        else if (zqdcWay.equals("zqdc_bqc")) {
            lotteryType = 3;
        }
        else if (zqdcWay.equals("zqdc_sxds")) {
            lotteryType = 4;
        }
        else if (zqdcWay.equals("zqdc_bf")) {
            lotteryType = 5;
        }
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null)
            return;
        kind = bundle.getString("kind");
    }

    private void setupViews() {
        // add by vincent
        title = (TextView) this.findViewById(R.id.bet_title);

        help = (ImageView) this.findViewById(R.id.bet_help);
        lv = (CustomExpandleListView) this.findViewById(R.id.match_list);
        help.setOnClickListener(this);
        clear = (Button) this.findViewById(R.id.bet_clear_button);
        clear.setOnClickListener(this);
        betInf = (TextView) this.findViewById(R.id.bet_inf);
        betBt = (Button) this.findViewById(R.id.bet_button);
        betBt.setOnClickListener(this);
        betBt.setTextColor(Color.GRAY);
        progressBar = (ProgressBar) findViewById(R.id.progress_large);
    }

    protected void initDefault(View view) {
        firstIcon = (LinearLayout) view.findViewById(R.id.item_first_layout);
        secondIcon = (LinearLayout) view.findViewById(R.id.item_second_layout);
        thirdIcon = (LinearLayout) view.findViewById(R.id.item_third_layout);
        forthIcon = (LinearLayout) view.findViewById(R.id.item_forth_layout);
        fifthIcon = (LinearLayout) view.findViewById(R.id.item_fifth_layout);
        zqdcSpf = (TextView) view.findViewById(R.id.zqdc_spf);
        zqdcZjq = (TextView) view.findViewById(R.id.zqdc_zjq);
        zqdcBqc = (TextView) view.findViewById(R.id.zqdc_bqc);
        zqdcSxds = (TextView) view.findViewById(R.id.zqdc_sxds);
        zqdcBf = (TextView) view.findViewById(R.id.zqdc_bf);
    }

    protected void iniSelectedItemBg() {
        firstIcon.setBackgroundResource(Color.TRANSPARENT);
        secondIcon.setBackgroundResource(Color.TRANSPARENT);
        thirdIcon.setBackgroundResource(Color.TRANSPARENT);
        forthIcon.setBackgroundResource(Color.TRANSPARENT);
        fifthIcon.setBackgroundResource(Color.TRANSPARENT);
    }

    protected void initIcon(int LuckyType) {
        if (LuckyType == 1) {
            firstIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            zqdcSpf.setTextColor(Color.WHITE);
            zqdcZjq.setTextColor(Color.BLACK);
            zqdcBqc.setTextColor(Color.BLACK);
            zqdcSxds.setTextColor(Color.BLACK);
            zqdcBf.setTextColor(Color.BLACK);
        }
        else if (LuckyType == 2) {
            secondIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            zqdcSpf.setTextColor(Color.BLACK);
            zqdcZjq.setTextColor(Color.WHITE);
            zqdcBqc.setTextColor(Color.BLACK);
            zqdcSxds.setTextColor(Color.BLACK);
            zqdcBf.setTextColor(Color.BLACK);
        }
        else if (LuckyType == 3) {
            thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            zqdcSpf.setTextColor(Color.BLACK);
            zqdcZjq.setTextColor(Color.BLACK);
            zqdcBqc.setTextColor(Color.WHITE);
            zqdcSxds.setTextColor(Color.BLACK);
            zqdcBf.setTextColor(Color.BLACK);
        }
        else if (LuckyType == 4) {
            thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            zqdcSpf.setTextColor(Color.BLACK);
            zqdcZjq.setTextColor(Color.BLACK);
            zqdcBqc.setTextColor(Color.BLACK);
            zqdcSxds.setTextColor(Color.WHITE);
            zqdcBf.setTextColor(Color.BLACK);
        }
        else if (LuckyType == 5) {
            thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            zqdcSpf.setTextColor(Color.BLACK);
            zqdcZjq.setTextColor(Color.BLACK);
            zqdcBqc.setTextColor(Color.BLACK);
            zqdcSxds.setTextColor(Color.BLACK);
            zqdcBf.setTextColor(Color.WHITE);
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
        betBt.setTextColor(Color.WHITE);
    }

    protected void disableBetBtn() {
        betBt.setEnabled(false);
        betBt.setTextColor(Color.GRAY);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bet_button) {
            bet();
        }
        else if (v.getId() == R.id.bet_clear_button) {
            clear();
        }
        else if (v.getId() == R.id.bet_help) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "足球单场游戏规则");
            bundel.putString("lottery_help", "help_new/jczq.html");
            intent.putExtras(bundel);
            intent.setClass(ZQDCBasicActivity.this, LotteryWinningRules.class);
            startActivity(intent);
        }

    }

    protected void showPopView() {
        // TODO Auto-generated method stub
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
            ConnectService connect = new ConnectService(ZQDCBasicActivity.this);
            String json = null;
            try {
                json = connect.getJson(5, false, initHashMap(params[0], params[1], params[2]));
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

    protected void showWay() {
    }

    //TODO
    private HashMap<String, String> initHashMap(String kind, String play, String pass)
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009020");
        parameter.put("pid", LotteryUtils.getPid(ZQDCBasicActivity.this));
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
        map.put("extra_inf", "open bet " + kind + " spf");
        String eventName = "v2 open bet";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_lottery_history";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }
}
