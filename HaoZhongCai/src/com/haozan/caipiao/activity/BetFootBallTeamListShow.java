package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.FootBallBetSelectionShowAdapter;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class BetFootBallTeamListShow
    extends BasicActivity {
    private ListView betTeamList;
    private FootBallBetSelectionShowAdapter footBallBetSelectionShowAdapter;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String betDisplayCode = null;
    private String kind;
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_football_team_list_show);
        setUpView();
        init();
    }

    private void setUpView() {
        betTeamList = (ListView) findViewById(R.id.foot_ball_team_show_list_view);
    }

    private void init() {
        if (this.getIntent().getExtras() != null) {
            betDisplayCode = this.getIntent().getExtras().getString("displayCode");
            kind = this.getIntent().getExtras().getString("bet_kind");
            term = this.getIntent().getExtras().getString("bet_term");
        }
        matchDataList = new ArrayList<AthleticsListItemData>();
        footBallBetSelectionShowAdapter =
            new FootBallBetSelectionShowAdapter(BetFootBallTeamListShow.this, matchDataList, betDisplayCode);
        betTeamList.setAdapter(footBallBetSelectionShowAdapter);
        if (HttpConnectUtil.isNetworkAvailable(BetFootBallTeamListShow.this)) {
            SportLotteryInfTask sportLotteryInfTask =
                new SportLotteryInfTask(BetFootBallTeamListShow.this, matchDataList,
                                        footBallBetSelectionShowAdapter, kind, term);
            sportLotteryInfTask.execute();
        }
        else {
            ViewUtil.showTipsToast(this,noNetTips);
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BetFootBallTeamListShow.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(BetFootBallTeamListShow.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                             R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
