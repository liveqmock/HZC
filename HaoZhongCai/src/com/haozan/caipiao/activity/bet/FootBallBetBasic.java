package com.haozan.caipiao.activity.bet;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.util.TimeUtils;
import com.umeng.analytics.MobclickAgent;

public class FootBallBetBasic
    extends BetBasic {

    private static final String BETEND = "投注已截止";
    protected String mode = "0000";
    protected String code = null;
    protected String displayCode = null;
    protected long betMoney = 0;
    protected String luckyNum = null;
// top
// protected LinearLayout betSportHeaderTitleLinear;
    protected RelativeLayout rl_term;
    protected LinearLayout betSportHeaderNewsLinear;
    protected TextView sportTitle;
    protected TextView sportBetTerm;
    protected TextView sportBetTimeInf;
    protected TextView sportCountDownTime;
    protected ImageView sportTopArrow;
    protected LinearLayout helpLin;
    protected ImageView shakeLock;
    protected TextView cutOffTime;
// middle
// protected ListView athleticsBetDataList;
// bottom
    protected ImageView betOderClear;
    protected Button makeBetOder;
    protected TextView sportBetMoney;

    protected RelativeLayout analyseTipsRa;
    protected RelativeLayout shakeRela;

    protected void setBasicView() {
        helpLin = (LinearLayout) this.findViewById(R.id.bet_help_lin);
// betSportHeaderTitleLinear = (LinearLayout) this.findViewById(R.id.bet_title_linear);
        betSportHeaderNewsLinear = (LinearLayout) this.findViewById(R.id.bet_header_news_linear);
        sportTitle = (TextView) this.findViewById(R.id.bet_title);
        sportBetTerm = (TextView) this.findViewById(R.id.bet_term);
        sportBetTimeInf = (TextView) this.findViewById(R.id.bet_time_inf);
        sportCountDownTime = (TextView) this.findViewById(R.id.bet_countdown_time);
        sportTopArrow = (ImageView) this.findViewById(R.id.arrow_top);
        sportTopArrow.setVisibility(View.GONE);
        shakeLock = (ImageView) this.findViewById(R.id.bet_shake);
        shakeLock.setVisibility(View.GONE);
        cutOffTime = (TextView) this.findViewById(R.id.cut_off_time);
        // bottom
        betOderClear = (ImageView) this.findViewById(R.id.sport_bet_clear);
        betOderClear.setEnabled(false);
        makeBetOder = (Button) this.findViewById(R.id.sport_bet_make_a_order);
        sportBetMoney = (TextView) this.findViewById(R.id.sport_bet_money);
        rl_term = (RelativeLayout) findViewById(R.id.rl_term);
// rl_term.setBackgroundColor(getResources().getColor(R.color.middle_gray));
        analyseTipsRa = (RelativeLayout) this.findViewById(R.id.analyse_tips_rala);
        analyseTipsRa.setVisibility(View.GONE);
        shakeRela = (RelativeLayout) findViewById(R.id.rela_bet_shake);
        shakeRela.setVisibility(View.GONE);
    }

    protected void iniBetList(Context context) {
        initBasic();
        GetLotteryInfTask getLotteryInfTask = new GetLotteryInfTask();
        getLotteryInfTask.execute();
    }

    @Override
    public void setKind() {

    }

    @Override
    protected void betCountTime(long millis) {
        if (millis > 60 * 60 * 1000) {
            rl_term.setVisibility(View.GONE);
        }
        else {
            rl_term.setVisibility(View.VISIBLE);
        }
        sportBetTimeInf.setVisibility(View.VISIBLE);
        sportCountDownTime.setText(TimeUtils.getCountDownTime(millis));
    }

    @Override
    protected void endBet() {
        sportBetTimeInf.setVisibility(View.GONE);
        sportCountDownTime.setText(BETEND);
    }

    protected void resetBtn() {
        betOderClear.setEnabled(false);
        makeBetOder.setEnabled(false);
    }

    protected void enableClearBtn() {
        betOderClear.setEnabled(true);
    }

    @Override
    protected void enableBetBtn() {
        makeBetOder.setEnabled(true);
    }

    @Override
    protected void disableBetBtn() {
        makeBetOder.setEnabled(false);
    }

    @Override
    public void setLotteryTerm() {
        sportBetTerm.setText(term + "期");
        exTask();
    }

    protected void exTask() {

    }

// 上传用户点击帮助按钮事件
    protected void submitStatisticClickRules() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet click help");
        map.put("more_inf", "bet click help of " + kind);
        map.put("extra_inf", "bet click help from top");
        String eventName = "v2 bet click help";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_help";
        MobclickAgent.onEvent(FootBallBetBasic.this, eventNameMob, kind);
    }

// 上传用户点击低赔按钮事件
    protected void submitStatisticRandomInf() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet low odds");
        String eventName = "bet low odds";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        String eventNameMob = "bet_low_odds";
        MobclickAgent.onEvent(FootBallBetBasic.this, eventNameMob, kind);
    }

    protected void bet() {
        if (checkInput()) {
            if (code == null)
                return;
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Bundle bundle = new Bundle();
            bundle.putString("bet_kind", kind);
            bundle.putLong("endtime", endTimeMillis);
            bundle.putLong("gaptime", gapMillis);
            bundle.putString("bet_term", term);
            bundle.putString("awardtime", awardTime);
            bundle.putString("mode", mode);
            bundle.putString("bet_code", code);
            bundle.putString("bet_display_code", displayCode);
            bundle.putString("luckynum", luckyNum);
            bundle.putLong("bet_money", betMoney);
            if (appState.getUsername() == null) {
                bundle.putBoolean("fromBet", true);
                bundle.putString("forwardFlag", "收银台");
                bundle.putBoolean("ifStartSelf", false);
                bundle.putString("about", "left");
                bundle.putBoolean("is_continue_pass", true);
                bundle.putString("class_name", BetPayDigital.class.getName());
                intent.putExtras(bundle);
                intent.setClass(FootBallBetBasic.this, Login.class);
// intent.setClass(FootBallBetBasic.this, StartUp.class);
            }
            else {
                intent.putExtras(bundle);
                intent.setClass(FootBallBetBasic.this, BetPayDigital.class);
            }
            startActivityForResult(intent, 1);
        }
    }

    protected boolean checkInput() {
        return false;
    }

    @Override
    public void showWarningInfo() {
        showFailPage();
    };

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet");
        map.put("more_inf", "open bet " + kind);
        String eventName = "v2 open bet";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        setKind();
        String eventName = "open_bet";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }
}
