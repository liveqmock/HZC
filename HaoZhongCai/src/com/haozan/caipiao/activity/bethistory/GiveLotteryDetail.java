package com.haozan.caipiao.activity.bethistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.UserCenterPhoneGridAdapter;
import com.haozan.caipiao.adapter.UserCenterPhoneGridAdapter.OnSelectedPhoneNumClickListener;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.ExpandableHeightGridView;
import com.haozan.caipiao.widget.PredicateLayout;
import com.umeng.analytics.MobclickAgent;

public class GiveLotteryDetail
    extends ContainTipsPageBasicActivity
    implements OnSelectedPhoneNumClickListener, OnClickListener {

    private Bundle bundle;
    private String programId;
    private String kind;
    private String codes;
    private String time;
    private String term;
    private String money;
    private String phoneNums;
    private TextView kindTv;
    private TextView termTv;
    private TextView timeTv;
    private PredicateLayout investBallsLayout;
    private TextView moneyTv;
    private TextView numsTv;
    private TextView numFinishedTv;
    private TextView numUnFinishedTv;
    private ExpandableHeightGridView finishedGrid;
    private ExpandableHeightGridView unfinishedGrid;
    private ArrayList<String> finishedPhoneNums;
    private ArrayList<String> unfinishedPhoneNums;
    private ArrayList<String> backmoneyPhoneNums;
    private UserCenterPhoneGridAdapter finishedGridAdapter;
    private UserCenterPhoneGridAdapter unFinishedGridAdapter;

    private String[] lotteryBetWay;
    private ImageView kindImg;
    private RelativeLayout showDetail;
    private RelativeLayout showDetailFinish;
    private RelativeLayout showDetailUnfinish;
    private LinearLayout detail;
    private boolean ifShowDetail = true;
    private boolean ifShowDetailFinish = false;
    private boolean ifShowDetailUnfinish = false;
    private ImageView iconDetail;
    private ImageView iconDetailFinish;
    private ImageView iconDetailUnfinish;

    private TextView allMoneyTv;
    private TextView finishMoneyTv;
    private TextView statusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.give_lottery_detail);
        setupViews();
        initData();
        init();
    }

    private void init() {
        unfinishedPhoneNums = new ArrayList<String>();
        unFinishedGridAdapter = new UserCenterPhoneGridAdapter(GiveLotteryDetail.this, unfinishedPhoneNums);
        unfinishedGrid.setAdapter(unFinishedGridAdapter);
        unFinishedGridAdapter.setSelectedPhoneNumClickListener(this);
        finishedPhoneNums = new ArrayList<String>();
        finishedGridAdapter = new UserCenterPhoneGridAdapter(GiveLotteryDetail.this, finishedPhoneNums);
        finishedGrid.setAdapter(finishedGridAdapter);
        finishedGridAdapter.setSelectedPhoneNumClickListener(this);
        backmoneyPhoneNums = new ArrayList<String>();
        excuteTask();
    }

    private void excuteTask() {
        if (HttpConnectUtil.isNetworkAvailable(GiveLotteryDetail.this)) {
            GetGiveLotteryDetailTask task = new GetGiveLotteryDetailTask();
            task.execute();
        }
    }

    private void initData() {
        bundle = getIntent().getExtras();
        programId = bundle.getString("program_id");
    }

    private void setupViews() {
        kindTv = (TextView) findViewById(R.id.lottery_code_title);
        termTv = (TextView) findViewById(R.id.lottery_term);
        timeTv = (TextView) findViewById(R.id.create_time);
        investBallsLayout = (PredicateLayout) findViewById(R.id.invest_balls);
        moneyTv = (TextView) findViewById(R.id.trade_money);
        numsTv = (TextView) findViewById(R.id.nunms);
        numFinishedTv = (TextView) findViewById(R.id.finished_num);
        numUnFinishedTv = (TextView) findViewById(R.id.unfinished_num);
        finishedGrid = (ExpandableHeightGridView) findViewById(R.id.grid_given_names);
        unfinishedGrid = (ExpandableHeightGridView) findViewById(R.id.grid_ungiven_names);
        kindImg = (ImageView) findViewById(R.id.kind_img);
        showDetail = (RelativeLayout) findViewById(R.id.give_lottery_detail_select);
        showDetail.setOnClickListener(this);
        detail = (LinearLayout) findViewById(R.id.detail);
        iconDetail = (ImageView) findViewById(R.id.icon_flag_detail);
        iconDetailFinish = (ImageView) findViewById(R.id.icon_flag_finish);
        iconDetailUnfinish = (ImageView) findViewById(R.id.icon_flag_unfinish);
        showDetailFinish = (RelativeLayout) findViewById(R.id.give_lottery_detail_finish);
        showDetailFinish.setOnClickListener(this);
        showDetailUnfinish = (RelativeLayout) findViewById(R.id.give_lottery_detail_unfinish);
        showDetailUnfinish.setOnClickListener(this);

        allMoneyTv = (TextView) findViewById(R.id.all_money);
        finishMoneyTv = (TextView) findViewById(R.id.finish_money);
        statusTv = (TextView) findViewById(R.id.back_money);
    }

    class GetGiveLotteryDetailTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("请稍后...");
        }

        private HashMap<String, String> iniHashMap() {
            LotteryApp appState = ((LotteryApp) GiveLotteryDetail.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "3003170");
            parameter.put("pid", LotteryUtils.getPid(GiveLotteryDetail.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("q_bet", "5");
            parameter.put("q_no", programId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(GiveLotteryDetail.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(9, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                ViewUtil.showTipsToast(GiveLotteryDetail.this, "获取数据失败，请稍后再试。");
                finish();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, "response_data");
                    kind = analyse.getData(data, "cat_code");
                    codes = analyse.getData(data, "codes");
                    if (!"null".equals(codes) && !"".equals(codes) && null != codes) {
                        lotteryBetWay = codes.split("\\:");
                    }
                    time = analyse.getData(data, "create_time");
                    term = analyse.getData(data, "term");
                    money = analyse.getData(data, "trade_money");
                    phoneNums = analyse.getData(data, "data");
                    showBasicData();
                    if (null != phoneNums && !"[]".equals(phoneNums)) {
                        analyseData();
                        showPhoneNums();
                    }
                }
            }
            dismissProgressDialog();
        }

        private void analyseData() {
            try {
                JSONArray array = new JSONArray(phoneNums);
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jo = array.getJSONObject(i);
                    String phone = jo.getString("receiver");
                    String type = jo.getString("status");
                    if (type.equals("0")) {
                        finishedPhoneNums.add(phone);
                    }
                    else if (type.equals("9") || type.equals("2")) {
                        unfinishedPhoneNums.add(phone);
                        if (type.equals("2")) {
                            backmoneyPhoneNums.add(phone);
                        }
                    }
                }

                int allMoney =
                    Integer.parseInt(money) * Integer.parseInt(lotteryBetWay[3]) *
                        (finishedPhoneNums.size() + unfinishedPhoneNums.size());
                allMoneyTv.setText(String.valueOf(allMoney) + "元");
                if (backmoneyPhoneNums.size() == 0 && unfinishedPhoneNums.size() == 0) {
                    statusTv.setText("全部领取成功");
                }
                else if (backmoneyPhoneNums.size() == 0) {
                    statusTv.setText("已领取" + finishedPhoneNums.size() + "人");
                }
                else if (backmoneyPhoneNums.size() != 0) {
                    statusTv.setText("已退款" +
                        (backmoneyPhoneNums.size() * Integer.parseInt(money) * Integer.parseInt(lotteryBetWay[3])) +
                        "元");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showBasicData() {
        kindTv.setText(LotteryUtils.getLotteryName(kind));
        int type = LotteryUtils.getTypeIndex(kind);
        if (-1 != type) {
            kindImg.setBackgroundResource(LotteryUtils.HALL_ITEM_ICON[type]);
        }
        termTv.setText(term + "期");
        timeTv.setText(time);
        moneyTv.setText(money + " 元");
        numsTv.setText(lotteryBetWay[3]);
        if (kind.equals("ssq") || kind.equals("qlc") || kind.equals("swxw") || kind.equals("dlt") ||
            kind.equals("22x5")) {
            LotteryUtils.drawBallsLargeNumber(GiveLotteryDetail.this, investBallsLayout,
                                              BetHistoryDetailTool.getDanTuoCodeNormal(lotteryBetWay[0],
                                                                                       lotteryBetWay[2]),
                                              kind);
        }
        else if (kind.equals("3d")) {
            if (lotteryBetWay[2].equals("4"))
                LotteryUtils.drawBallsLargeNumber(GiveLotteryDetail.this, investBallsLayout,
                                                  BetHistoryDetailTool.getDanTuoCodeNormal(lotteryBetWay[0],
                                                                                           lotteryBetWay[2]),
                                                  kind);
            else
                LotteryUtils.drawBallsSmallNumber(GiveLotteryDetail.this, investBallsLayout,
                                                  lotteryBetWay[0], kind);
        }
        else {
            // 3d组三或者组六
            LotteryUtils.drawBallsSmallNumber(GiveLotteryDetail.this, investBallsLayout, lotteryBetWay[0],
                                              kind);
        }
    }

    public void showPhoneNums() {
        numUnFinishedTv.setText(String.valueOf(unfinishedPhoneNums.size()) + "人");
        numFinishedTv.setText(String.valueOf(finishedPhoneNums.size()) + "人");
        if (unfinishedPhoneNums.size() != 0) {
            unFinishedGridAdapter.notifyDataSetChanged();
        }
        else if (finishedPhoneNums.size() != 0) {
            finishedGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean checkSelectedPhonNumClick(int position, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet order details");
        map.put("more_inf", "send lottery");
        FlurryAgent.onEvent("open bet order details", map);
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet_order_detail";
        MobclickAgent.onEvent(this, eventName, "送彩票投注");
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.give_lottery_detail_select) {
            if (ifShowDetail) {
                ifShowDetail = false;
                detail.setVisibility(View.GONE);
                iconDetail.setBackgroundResource(R.drawable.icon_up_new);
            }
            else {
                ifShowDetail = true;
                detail.setVisibility(View.VISIBLE);
                iconDetail.setBackgroundResource(R.drawable.icon_down_new);
            }
        }
        else if (v.getId() == R.id.give_lottery_detail_finish) {
            if (ifShowDetailFinish) {
                ifShowDetailFinish = false;
                finishedGrid.setVisibility(View.GONE);
                iconDetailFinish.setBackgroundResource(R.drawable.icon_up_new);
            }
            else {
                ifShowDetailFinish = true;
                finishedGrid.setVisibility(View.VISIBLE);
                iconDetailFinish.setBackgroundResource(R.drawable.icon_down_new);
            }
        }
        else if (v.getId() == R.id.give_lottery_detail_unfinish) {
            if (ifShowDetailUnfinish) {
                ifShowDetailUnfinish = false;
                unfinishedGrid.setVisibility(View.GONE);
                iconDetailUnfinish.setBackgroundResource(R.drawable.icon_up_new);
            }
            else {
                ifShowDetailUnfinish = true;
                unfinishedGrid.setVisibility(View.VISIBLE);
                iconDetailUnfinish.setBackgroundResource(R.drawable.icon_down_new);
            }
        }
    }
}
