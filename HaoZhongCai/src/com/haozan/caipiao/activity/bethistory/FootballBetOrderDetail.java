package com.haozan.caipiao.activity.bethistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.adapter.FootBallBetSelectionHistoryShowAdapter;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.task.SportLotteryInfTask.OnTaskChangeListener;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class FootballBetOrderDetail
    extends BasicActivity
    implements OnClickListener, OnTaskChangeListener {
    private ListView betTeamList;
    private FootBallBetSelectionHistoryShowAdapter footBallBetSelectionShowAdapter;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String betDisplayCode = null;
    private String kind;
    private String term;
    private String openCode;
    private String contactStr;
    private TextView lotteryKind;
    private TextView lotteryTerm;
// private TextView lotteryMessage;
    private TextView lotteryPhone;
    private TextView lotteryQQ;
    private View listHeadView;
    private View listFootView;
    private LayoutInflater factory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foot_ball_bet_order_detail);
        setUpView();
        init();
    }

    private void setUpView() {
        factory = LayoutInflater.from(this);
        listHeadView = factory.inflate(R.layout.foot_ball_order_detail_headview, null);
        listFootView = factory.inflate(R.layout.foot_ball_order_detail_footview, null);

        betTeamList = (ListView) findViewById(R.id.foot_ball_team_show_list_view);
        betTeamList.addHeaderView(listHeadView);
        betTeamList.addFooterView(listFootView);
        lotteryKind = (TextView) listHeadView.findViewById(R.id.lottery_kind);
        lotteryTerm = (TextView) listHeadView.findViewById(R.id.lottery_term);
// lotteryMessage = (TextView) listFootView.findViewById(R.id.lottery_order_tips1);
        lotteryPhone = (TextView) listFootView.findViewById(R.id.service_phone);
        lotteryQQ = (TextView) listFootView.findViewById(R.id.service_qq);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void init() {

        if (this.getIntent().getExtras() != null) {
            betDisplayCode = this.getIntent().getExtras().getString("displayCode");
            kind = this.getIntent().getExtras().getString("bet_kind");
            term = this.getIntent().getExtras().getString("bet_term");
            openCode = this.getIntent().getExtras().getString("opne_codes");
        }

        lotteryKind.setText(LotteryUtils.getLotteryName(kind));
        lotteryTerm.setText(term + "期");
        contactStr =
            "客服电话：<u> <font color='blue'>" + LotteryUtils.getConnectionPhone(mContext) + "</color></u>";
        lotteryPhone.setOnClickListener(this);
        lotteryPhone.setText(Html.fromHtml(contactStr));
        lotteryQQ.setText("客服QQ：" + LotteryUtils.getConnectionQQ(mContext));

        matchDataList = new ArrayList<AthleticsListItemData>();
        footBallBetSelectionShowAdapter =
            new FootBallBetSelectionHistoryShowAdapter(FootballBetOrderDetail.this, matchDataList,
                                                       betDisplayCode, openCode);
        betTeamList.setAdapter(footBallBetSelectionShowAdapter);
        if (HttpConnectUtil.isNetworkAvailable(FootballBetOrderDetail.this)) {
            progressBar.setVisibility(View.VISIBLE);
            SportLotteryInfTask sportLotteryInfTask =
                new SportLotteryInfTask(FootballBetOrderDetail.this, matchDataList, kind, term);
            sportLotteryInfTask.execute();
            sportLotteryInfTask.setTaskChangeListener(this);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.service_phone) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + LotteryUtils.getConnectionPhone(mContext)));
            startActivity(intent);
        }
    }

    @Override
    public void onSFCR9ActionClick(String status) {
        progressBar.setVisibility(View.GONE);
        if (status != null)
            if (status.equals("200")) {
                footBallBetSelectionShowAdapter.notifyDataSetChanged();
            }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FootballBetOrderDetail.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(FootballBetOrderDetail.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                            R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open sfc team list");
        String eventName = "v2 open sfc team list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_sfc_team_list";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }
}
