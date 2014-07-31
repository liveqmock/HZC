package com.haozan.caipiao.activity.bethistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.adapter.JingCaiOrderDetailAdapter;
import com.haozan.caipiao.task.JingCaiOrderDetailTask;
import com.haozan.caipiao.task.JingCaiOrderDetailTask.OnJingCaiHisDetailTaskChangeListener;
import com.haozan.caipiao.types.JingCaiOrderDetailItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.AutoLoadListView;
import com.haozan.caipiao.widget.AutoLoadListView.LoadDataListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 竞猜投注记录每个大单拆分成小单后的详细投注单信息
 * 
 * @author peter_feng
 * @create-time 2013-5-2 下午3:12:32
 */
public class JingCaiOrderDetail
    extends ContainTipsPageBasicActivity
    implements OnJingCaiHisDetailTaskChangeListener, LoadDataListener {
    private AutoLoadListView lvBetTeam;
    private JingCaiOrderDetailAdapter jingCaiDetailShowAdapter;
    private ArrayList<JingCaiOrderDetailItem> matchDataList;

    private String kind;
    private String splitNum;
    private String orderId;
    private String lotteryWayType = "71";
    private int page_no = 1;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jing_cai_order_detail);
        setUpView();
        init();
    }

    private void setUpView() {
        title = (TextView) findViewById(R.id.bet_football_team_list_title);
        lvBetTeam = (AutoLoadListView) findViewById(R.id.foot_ball_team_show_list_view);
        lvBetTeam.setOnLoadDataListener(this);
    }

    private void init() {
        if (this.getIntent().getExtras() != null) {
            kind = this.getIntent().getExtras().getString("bet_kind");
            orderId = this.getIntent().getExtras().getString("order_id");
            splitNum = this.getIntent().getExtras().getString("split_num");
            lotteryWayType = this.getIntent().getExtras().getString("lottery_way_type");
            if (kind.equals("jclq"))
                title.setText("竞彩篮球订单详情");
        }
        matchDataList = new ArrayList<JingCaiOrderDetailItem>();
        jingCaiDetailShowAdapter =
            new JingCaiOrderDetailAdapter(JingCaiOrderDetail.this, matchDataList, splitNum);
        lvBetTeam.setAdapter(jingCaiDetailShowAdapter);
        loadData();
    }

    private void showFail() {
        showFail(failTips);
    }

    private void showFail(String failInf) {
        if (matchDataList.size() == 0) {
            showTipsPage(failTips);
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
        lvBetTeam.removeLoadingFootView();
    }

    private void showNoData() {
        if (matchDataList.size() == 0) {
            showTipsPage("没有查询到数据");
            lvBetTeam.setVisibility(View.GONE);
        }
        lvBetTeam.removeLoadingFootView();
    }

    @Override
    public void onJingCaiHisActionClick(String status) {
        if (status != null) {
            if (status.equals("200")) {
                page_no++;
                jingCaiDetailShowAdapter.notifyDataSetChanged();
            }
            else if (status.equals("no_data")) {
                showNoData();
            }
            else if (status.equals("2021")) {
                showFail();
            }
        }
        dismissProgress();
    }

// @Override
// public void onClick(View arg0) {
// if (HttpConnectUtil.isNetworkAvailable(JingCaiOrderDetail.this)) {
// JingCaiOrderDetailTask sportLotteryInfTask =
// new JingCaiOrderDetailTask(JingCaiOrderDetail.this, matchDataList, kind, orderId,
// lotteryWayType);
// sportLotteryInfTask.execute(String.valueOf(page_no));
// sportLotteryInfTask.setOnJinCaiHisDeTaskChangeListener(this);
// }
// else {
// ViewUtil.showTipsToast(this, noNetTips);
// }
// }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            JingCaiOrderDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open jingcai order detail");
        String eventName = "open jingcai order detail";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_jingcai_order_detail";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(JingCaiOrderDetail.this)) {
            showProgress();
            JingCaiOrderDetailTask sportLotteryInfTask =
                new JingCaiOrderDetailTask(JingCaiOrderDetail.this, matchDataList, kind, orderId,
                                           lotteryWayType);
            sportLotteryInfTask.execute(String.valueOf(page_no));
            sportLotteryInfTask.setOnJinCaiHisDeTaskChangeListener(this);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }
}
