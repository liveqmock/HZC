package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.HistoryDetailAdapter;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.HistoryDetailsTask;
import com.haozan.caipiao.types.HistoryListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.DrawBalls;
import com.haozan.caipiao.widget.PredicateLayout;
import com.umeng.analytics.MobclickAgent;

public class LotteryHistoryDetail
    extends BasicActivity
    implements OnClickListener {

    private static final String RESPONSE = "response_data";

    private String kind;
    private String searchTerm;
    private String codes;
    private String time;
    private TextView title;
    private TextView term;
    private TextView awardTime;
    private TextView jackpotTv;
    private TextView jackpot;
    private TextView lotterySale;
    private Button gameInf;

    private ListView historyDetailList;
    private HistoryDetailAdapter historyListAdapter;
    private ArrayList<HistoryListItemData> historyList;

    private PredicateLayout ballsLayout;
    private LinearLayout saleLayout;
    private LinearLayout jackpotLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_history_detail);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("kind");
        searchTerm = bundle.getString("term");
        codes = bundle.getString("balls");
        time = bundle.getString("time");
    }

    private void setupViews() {
        title = (TextView) this.findViewById(R.id.title);
        term = (TextView) this.findViewById(R.id.lottery_term);
        ballsLayout = (PredicateLayout) this.findViewById(R.id.balls);
        awardTime = (TextView) this.findViewById(R.id.lottery_award_time);
        jackpotTv = (TextView) this.findViewById(R.id.jackpot);
        jackpot = (TextView) this.findViewById(R.id.lottery_jackpot);
        gameInf = (Button) this.findViewById(R.id.watch_game);
        gameInf.setOnClickListener(this);
        historyDetailList = (ListView) this.findViewById(R.id.history_detail_list);
        lotterySale = (TextView) this.findViewById(R.id.lottery_sale);
        saleLayout = (LinearLayout) this.findViewById(R.id.layout_lottery_sale);
        jackpotLayout = (LinearLayout) this.findViewById(R.id.layout_lottery_jackpot);
    }

    private void init() {
        initContent();
        historyList = new ArrayList<HistoryListItemData>();
        historyListAdapter = new HistoryDetailAdapter(LotteryHistoryDetail.this, historyList);
        historyDetailList.setAdapter(historyListAdapter);
        if (HttpConnectUtil.isNetworkAvailable(LotteryHistoryDetail.this)) {
            LotteryDetailTask detailTask = new LotteryDetailTask();
            detailTask.execute();
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this, inf);
        }
    }

    private void initContent() {
        title.setText(LotteryUtils.getLotteryName(kind) + "开奖详细信息");
        term.setText(searchTerm + " 期");
        awardTime.setText(time);
        setBalls();
    }

    private void setBalls() {
        DrawBalls drawBalls = new DrawBalls();
        drawBalls.setBigBall(true);
        drawBalls.drawBallsLayout(LotteryHistoryDetail.this, ballsLayout, kind, codes);
    }

    class LotteryDetailTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            dismissProgress();
            if (result == null) {
                searchFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    String data = analyse.getData(result, RESPONSE);
                    try {
                        String levels = analyse.getData(data, "levels");
                        showPrize(levels);
                        codes = analyse.getData(data, "codes");
                        if (kind.equals("dfljy")) {
                            if (!codes.equals(LotteryUtils.HALL_DEFALUT_CONTENT[3])) {
                                codes = StringUtil.getDFLJY(codes);
                            }
                        }
                        setBalls();
                        if (kind.equals("sfc") || kind.equals("r9")) {
                            gameInf.setVisibility(View.VISIBLE);
                        }
                        time = analyse.getData(data, "awardtime");
                        awardTime.setText(time);
                        String jackpot1 = analyse.getData(data, "jackpot");
                        if (jackpot1 == null ||
                            (!jackpot1.equals("null") && !jackpot1.equals("0") && !jackpot1.equals(""))) {
                            if (kind.equals("3d")) {
                                jackpotTv.setText("试  机  号：");
                            }
                            jackpotLayout.setVisibility(View.VISIBLE);
                            jackpot.setText(StringUtil.divideLongNum(jackpot1));
                        }
                        else {
                            jackpotLayout.setVisibility(View.GONE);
                        }
                        String sale = analyse.getData(data, "sale");
                        if (sale == null || (!sale.equals("null") && !sale.equals("0") && !sale.equals(""))) {
                            saleLayout.setVisibility(View.VISIBLE);
                            lotterySale.setText(StringUtil.divideLongNum(sale));
                        }
                        else {
                            saleLayout.setVisibility(View.GONE);
                        }
                    }
                    catch (JSONException e) {
                        searchFail();
                        e.printStackTrace();
                    }
                }
                else if (status.equals("202")) {
                    searchNoData();
                }
                else {
                    searchFail();
                }
            }
        }

        private void showPrize(String levels)
            throws JSONException {
            JSONArray hallArray = new JSONArray(levels);
            for (int i = 0; i < hallArray.length(); i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                HistoryListItemData historyItem = new HistoryListItemData();
                historyItem.setAwardName(jo.getString("name"));
                historyItem.setAwardNum(jo.getString("num"));
                historyItem.setAwardMoney(jo.getString("money"));
                historyList.add(historyItem);
            }
            historyListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Void... para) {
            HistoryDetailsTask getDetails = new HistoryDetailsTask(LotteryHistoryDetail.this);
            String json = getDetails.getting(kind, searchTerm);
            return json;
        }
    }

    public void searchFail() {
        ViewUtil.showTipsToast(this, failTips);
    }

    public void searchNoData() {
        ViewUtil.showTipsToast(this, "暂时还没有数据，请再等等哦");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.watch_game) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("displayCode", codes);
            bundle.putString("bet_kind", kind);
            bundle.putString("bet_term", searchTerm);
            intent.putExtras(bundle);
            intent.setClass(LotteryHistoryDetail.this, BetFootBallTeamListShow.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open history details");
        map.put("more_inf", "open history details of " + kind);
        String eventName = "v2 open history details";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("kind");
        }
        String eventName = "open_history_details";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LotteryHistoryDetail.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(LotteryHistoryDetail.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                          R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}