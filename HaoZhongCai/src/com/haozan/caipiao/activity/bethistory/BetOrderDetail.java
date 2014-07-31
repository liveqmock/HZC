package com.haozan.caipiao.activity.bethistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.matchrecord.CQSSCRecordMatch;
import com.haozan.caipiao.matchrecord.DFLJYRecordMatch;
import com.haozan.caipiao.matchrecord.DLTRecordMatch;
import com.haozan.caipiao.matchrecord.HNKLSFRecordMatch;
import com.haozan.caipiao.matchrecord.PLSRecordMatch;
import com.haozan.caipiao.matchrecord.PLWRecordMatch;
import com.haozan.caipiao.matchrecord.QLCRecordMatch;
import com.haozan.caipiao.matchrecord.SDRecordMatch;
import com.haozan.caipiao.matchrecord.SFCRecordMatch;
import com.haozan.caipiao.matchrecord.SSLRecordMatch;
import com.haozan.caipiao.matchrecord.SSQRecordMatch;
import com.haozan.caipiao.matchrecord.SWXWRecordMatch;
import com.haozan.caipiao.matchrecord.SYXRecordMatch;
import com.haozan.caipiao.types.Ball;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.widget.PredicateLayout;
import com.umeng.analytics.MobclickAgent;

public class BetOrderDetail
    extends BasicActivity
    implements OnClickListener {

    private String contactStr;
    private String kind;
    private String term;
    private String codes;
    private String opens;
    private int selected_index;
    private TextView kindTV;
    private TextView termTV;
    private TextView tipsFirst;
    private TextView tipsSecond;
    private TextView wayTV;
    private TextView luckyThingTV;
    private TextView luckyThingStar;
    private TextView luckyThingTVTitle;
    private TextView luckyThingStarTitle;
    private TextView orgBetBallNum;
    private TextView betBallNum;
    private LinearLayout luckyThingTVinear;
    private LinearLayout luckyThingStarLinear;
    private TextView message;
    private TextView contact;
    private LinearLayout layout;
    private PredicateLayout ballsLayout1;
    private PredicateLayout ballsLayout2;
    private LinearLayout orgNumLayout;
    private PredicateLayout orgBallsLayout;
    private ArrayList<Ball> balls = null;
    private TextView countTV;
    private TextView openBallInf;
    private int isMatchedCount;
    private String lotteryMethod;

    private void setIsMatchedCount(ArrayList<Ball> balls) {
        isMatchedCount = 0;
        for (Ball a : balls) {
            if (a.isState())
                isMatchedCount++;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_history_order_details);
        initData();
        setupViews();
        init();
    }

    private String m;
    private String orgNum;
    private String star;
    private String luckThings;

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("lottery_id");
        term = bundle.getString("term");
        codes = bundle.getString("codes");
        opens = bundle.getString("opens");
        selected_index = bundle.getInt("selected_index");
        m = bundle.getString("m");
        orgNum = bundle.getString("orgNum");
        star = bundle.getString("star");
        luckThings = bundle.getString("luckThings");
        lotteryMethod = codes.split("\\:")[2];
    }

    private boolean isGetDltNormalType(String s) {
        if (s.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private String getDanTuoCodeNormal(String originCode) {
        StringBuilder originCodeString = new StringBuilder();
        String[] originCodeArray = originCode.split("\\|");
        if (lotteryMethod.equals("5")) {
            originCodeString.append("#,");
            if (originCodeArray[0].indexOf("$") != -1) {
                for (int j = 0; j < originCodeArray[0].split("\\$").length; j++) {
                    originCodeString.append(originCodeArray[0].split("\\$")[j]);
                    originCodeString.append(",$,");
                }
                originCodeString.delete(originCodeString.length() - 2, originCodeString.length());
            }
            else {
                originCodeString.append(originCodeArray[0]);
            }

            if (originCodeArray[1].indexOf("$") != -1) {
                if (originCodeArray[1].indexOf("$") != 0) {
                    originCodeString.append("|#,");
                    for (int j = 0; j < originCodeArray[1].split("\\$").length; j++) {
                        originCodeString.append(originCodeArray[1].split("\\$")[j]);
                        originCodeString.append(",$,");
                    }
                    originCodeString.delete(originCodeString.length() - 2, originCodeString.length());
                }
                else {
                    originCodeString.append("|" + originCodeArray[1].split("\\$")[1]);
                }
            }
            else {
                originCodeString.append("|" + originCodeArray[1]);
            }
        }
        else {
            originCodeString.append(originCode);
        }
        return originCodeString.toString();
    }

    private String getDanTuoCodeOpen(String openCode, int boundIndex) {
        StringBuilder originCodeString = new StringBuilder();
        if (lotteryMethod.equals("5")) {
            originCodeString.append("none,");
            originCodeString.append(openCode.subSequence(0, boundIndex - 2));
            originCodeString.append(",none,");
            originCodeString.append(openCode.subSequence(boundIndex, openCode.length()));
        }
        else {
            originCodeString.append(openCode);
        }
        return originCodeString.toString();
    }

    private ArrayList<Ball> changeCQSSCBetBallCodeAward(ArrayList<Ball> ballNumber, String lotteryBetWay01) {

        if ((kind.equals("cqssc") && lotteryBetWay01.equals("701")) ||
            (kind.equals("jxssc") && lotteryBetWay01.equals("701"))) {
            for (int i = 0; i < ballNumber.size(); i++) {
                String dxdsBallNumber = ballNumber.get(i).getNumber();
                if (!dxdsBallNumber.equals("大") && !dxdsBallNumber.equals("小") &&
                    !dxdsBallNumber.equals("单") && !dxdsBallNumber.equals("双"))
                    ballNumber.get(i).setNumber(analyseBetCode(Integer.valueOf(dxdsBallNumber)));
            }
            return ballNumber;
        }
        else
            return ballNumber;
    }

    private String analyseBetCode(int code) {
        String dxdsCode = null;
        if (code == 1)
            dxdsCode = "大";
        else if (code == 2)
            dxdsCode = "小";
        else if (code == 3)
            dxdsCode = "单";
        else if (code == 4)
            dxdsCode = "双";
        return dxdsCode;
    }

    private String changeCQSSCBetBallCodeNormal(String ballNumber, String lotteryBetWay01) {
        StringBuilder sbCQSSC = new StringBuilder();
        String[] ballNumberArray = ballNumber.split("\\,");
        if (kind.equals("cqssc") && lotteryBetWay01.equals("701")) {
            for (int i = 0; i < ballNumberArray.length; i++) {
                sbCQSSC.append(analyseBetCode(Integer.valueOf(ballNumberArray[i])));
                sbCQSSC.append(",");
            }
            sbCQSSC.delete(sbCQSSC.length() - 1, sbCQSSC.length());
            return sbCQSSC.toString();
        }
        else
            return ballNumber;
    }

    private void init() {
        BetHistoryDetailTool.initAnalyseData();
        setRecord();
        showData();
        contactStr = "如有任何问题请<u><font color='blue'>联系我们</color></u>";
        contact.setOnClickListener(this);
        contact.setText(Html.fromHtml(contactStr));
        tipsFirst.setVisibility(View.VISIBLE);
        tipsSecond.setVisibility(View.GONE);
        String[] ballNumbers = StringUtil.spliteString(codes, ":");

        try {
            if (!opens.equals("null")) {
                if (kind.equals("dfljy"))
                    LotteryUtils.drawBallsAnimalsNumber(BetOrderDetail.this, ballsLayout2, opens, kind);
                else
                    LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout2, opens, kind);

                // 绘制投注记录球(已匹配）
                if (!kind.equals("null")) {
                    if (kind.equals("ssq")) {// 双色球投注历史中奖号码高亮显示
                        String betCode = getDanTuoCodeNormal(codes);
                        String openCode = getDanTuoCodeOpen(opens, betCode.indexOf("$"));
                        balls = SSQRecordMatch.isMatch(betCode, openCode);
                    }
                    if (kind.equals("3d")) {// 3D球投注历史中奖号码高亮显示
                        if (lotteryMethod.equals("4"))
                            balls = SWXWRecordMatch.isMatch(codes, opens);
                        else
                            balls = SDRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("swxw")) {// 15选5球投注历史中奖号码高亮显示
                        balls = SWXWRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("qlc")) {// 7乐彩球投注历史中奖号码高亮显示
                        balls = QLCRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("dfljy")) {// 东方6+1球投注历史中奖号码高亮显示
                        balls = DFLJYRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("ssl")) {// 时时乐球投注历史中奖号码高亮显示
                        balls = SSLRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("dlt")) {// 大乐透球投注历史中奖号码高亮显示
                        String betCode = getDanTuoCodeNormal(codes);
                        String openCode = getDanTuoCodeOpen(opens, betCode.indexOf("$"));
                        if (isGetDltNormalType(codes))
                            balls = DLTRecordMatch.isMatch(betCode, openCode);
                        else
                            balls = QLCRecordMatch.isMatch(codes, openCode);
                    }
                    if (kind.equals("pls")) {// 排列三球投注历史中奖号码高亮显示
                        balls = PLSRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("plw")) {// 排列五球投注历史中奖号码高亮显示
                        balls = PLWRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("qxc")) {// 七星彩球投注历史中奖号码高亮显示
                        balls = SFCRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("22x5")) {// 15选5球投注历史中奖号码高亮显示
                        balls = SWXWRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("cqssc") || kind.equals("jxssc")) {// 重庆时时彩投注历史中奖号码高亮显示
                        balls = CQSSCRecordMatch.isMatch(codes, opens);
                    }
                    if (kind.equals("jx11x5")) {// 排列五球投注历史中奖号码高亮显示
                        if (codes.indexOf("(") != -1) {
                            String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(codes);
                            String betCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                            String openCode =
                                BetHistoryDetailTool.analyseDantuoCodeOpen(opens,
                                                                           betCode.split("\\$")[0].split("\\,").length + 1);
                            balls = SYXRecordMatch.isMatch(betCode, openCode);
                        }
                        else {
                            balls = SYXRecordMatch.isMatch(codes, opens);
                        }
                    }
                    if (kind.equals("klsf") || kind.equals("hnklsf")) {// 排列五球投注历史中奖号码高亮显示
                        balls = HNKLSFRecordMatch.isMatch(codes, opens);
                    }
// else if (kind.equals("jlk3")) {// 吉林快三球投注历史中奖号码高亮显示
// balls = JLK3RecordMatch.isMatch(codes, opens);
// }

                    if (!kind.equals("jlk3"))
                        this.setIsMatchedCount(balls);

                    if (kind.equals("jlk3")) {
                        if (!lotteryMethod.equals("103") && !lotteryMethod.equals("116")) {// 103, 116
// 三同号通选，三连号通选
                            if (lotteryMethod.equals("101") || lotteryMethod.equals("102"))// 和值玩法不需要对投注号码拆分
// 例如 14不需要拆成 1和4
                                LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                                  ballNumbers[0], kind);
                            else
                                LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, ballsLayout1,
                                                                  ballNumbers[0], kind);
                        }
                        else {// 三同号通选，三连号通选 需要用文字显示
                            if (ballNumbers[2].equals("103")) {
                                orgBetBallNum.setText("玩法：三同号通选");
                                betBallNum.setText("玩法：三同号通选");
                            }

                            if (ballNumbers[2].equals("116")) {
                                orgBetBallNum.setText("玩法：三连号通选");
                                betBallNum.setText("玩法：三连号通选");
                            }
                        }
                    }
                    else if (kind.equals("cqssc") || kind.equals("jxssc")) {
                        ArrayList<Ball> betBallCode = changeCQSSCBetBallCodeAward(balls, lotteryMethod);
                        LotteryUtils.drawHallBalls(BetOrderDetail.this, ballsLayout1, betBallCode, kind);
                    }
                    else
                        LotteryUtils.drawHallBalls(BetOrderDetail.this, ballsLayout1,
                                                   changeCQSSCBetBallCodeAward(balls, lotteryMethod), kind);
                }
            }
            else {
                if (kind.equals("dfljy") || kind.equals("ssl") || kind.equals("pls") || kind.equals("plw") ||
                    kind.equals("qxc"))
                    LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this,
                                                      ballsLayout1,
                                                      changeCQSSCBetBallCodeNormal(ballNumbers[0],
                                                                                   lotteryMethod), kind);
                else if (kind.equals("3d")) {
                    if (ballNumbers[2].equals("4"))
                        LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                          getDanTuoCodeNormal(ballNumbers[0]), kind);
                    else
                        LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, ballsLayout1, ballNumbers[0],
                                                          kind);
                }
                else if (kind.equals("jx11x5")) {
                    if (ballNumbers[0].indexOf("(") != -1) {
                        String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(ballNumbers[0]);
                        String syxwDantuoBetCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                        LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                          syxwDantuoBetCode, kind);
                    }
                    else {
                        if (map.get(ballNumbers[2]).equals("9") || map.get(ballNumbers[2]).equals("10") ||
                            map.get(ballNumbers[2]).equals("11") || map.get(ballNumbers[2]).equals("12"))
                            LotteryUtils.drawBallsNumberSyxw(BetOrderDetail.this, ballsLayout1,
                                                             ballNumbers[0], kind);
                        else
                            LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                              ballNumbers[0], kind);
                    }
                }
                else if (kind.equals("hnklsf")) {
                    if (ballNumbers[2].equals("341") || ballNumbers[2].equals("342") ||
                        ballNumbers[2].equals("221") || ballNumbers[2].equals("222"))
                        LotteryUtils.drawBallsNumberSyxw(BetOrderDetail.this, ballsLayout1, ballNumbers[0],
                                                         kind);
                    else
                        LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1, ballNumbers[0],
                                                          kind);
                }
                else if (kind.equals("jlk3")) {
                    if (!ballNumbers[2].equals("103") && !ballNumbers[2].equals("116")) {// 103, 116
// 三同号通选，三连号通选
                        if (lotteryMethod.equals("101") || lotteryMethod.equals("102"))// 101, 102 和值单式，和值复式
                            LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                              ballNumbers[0], kind);
                        else
                            LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, ballsLayout1,
                                                              ballNumbers[0], kind);
                    }

                    if (ballNumbers[2].equals("103")) {
                        orgBetBallNum.setText("玩法：三连号通选");
                        betBallNum.setText("玩法：三连号通选");
                    }

                    if (ballNumbers[2].equals("116")) {
                        orgBetBallNum.setText("玩法：三连号通选");
                        betBallNum.setText("玩法：三连号通选");
                    }
                }
                else if (kind.equals("cqssc") || kind.equals("jxssc")) {
                    String betBallCode = changeCQSSCBetBallCodeNormal(ballNumbers[0], lotteryMethod);
                    LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, ballsLayout1, betBallCode, kind);
                }
                else
                    LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, ballsLayout1,
                                                      getDanTuoCodeNormal(ballNumbers[0]), kind);
                openBallInf.setText("尚未开奖...");
                openBallInf.setVisibility(View.VISIBLE);
                ballsLayout2.setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "数据解析失败", Toast.LENGTH_SHORT).show();
        }
        kindTV.setText(LotteryUtils.getLotteryName(kind));
        termTV.setText(term + "期");
        if (opens == null || opens.equals("") || opens.equals("null")) {
            countTV.setText("该注尚未开奖");
            countTV.setVisibility(View.GONE);
        }
        else {
            if (!kind.equals("jlk3"))
                countTV.setText("您这次选中 " + this.isMatchedCount + " 个球," + "选中球未必代表中奖,详细中奖规则请查询本软件帮助文档。");
            else {
                countTV.setText("该注尚未开奖");
                countTV.setVisibility(View.GONE);
            }
        }
    }

    private void setupViews() {
        layout = (LinearLayout) this.findViewById(R.id.details_layout);
        ballsLayout1 = (PredicateLayout) this.findViewById(R.id.balls_bet);
        ballsLayout2 = (PredicateLayout) this.findViewById(R.id.balls_open);
        openBallInf = (TextView) this.findViewById(R.id.open_ball_inf);
        kindTV = (TextView) this.findViewById(R.id.lottery_kind);
        termTV = (TextView) this.findViewById(R.id.lottery_term);
        countTV = (TextView) this.findViewById(R.id.lottery_prizeCount);
        tipsFirst = (TextView) this.findViewById(R.id.lottery_order_tips1);
        tipsSecond = (TextView) this.findViewById(R.id.lottery_order_tips2);
        wayTV = (TextView) this.findViewById(R.id.lottery_way);

        luckyThingTV = (TextView) this.findViewById(R.id.lottery_lucky_thing);
        luckyThingStar = (TextView) this.findViewById(R.id.lottery_lucky_star);

        luckyThingTVTitle = (TextView) this.findViewById(R.id.lottery_lucky_thing_tv);
        luckyThingStarTitle = (TextView) this.findViewById(R.id.lottery_lucky_star_tv);

        luckyThingStarLinear = (LinearLayout) this.findViewById(R.id.lottery_lucky_star_linear);
        luckyThingTVinear = (LinearLayout) this.findViewById(R.id.lottery_lucky_thing_linear);
        orgNumLayout = (LinearLayout) this.findViewById(R.id.org_balls_layout);
        orgBallsLayout = (PredicateLayout) this.findViewById(R.id.org_balls);
        message = (TextView) this.findViewById(R.id.message);
        contact = (TextView) this.findViewById(R.id.contact);
        orgBetBallNum = (TextView) this.findViewById(R.id.org_num_tag);
        betBallNum = (TextView) this.findViewById(R.id.bet_num_tag);
    }

    private void showData() {
        try {
            String[] ballNumbersOrg = StringUtil.spliteString(codes, ":");
            String[] originNumChild = orgNum.split("\\;");
            String[] luckyText = luckThings.split("\\;");
            String[] starText = star.split("\\;");
            String[] wayArray = m.split("\\;");
            String way = "未知";
            if (wayArray[selected_index - 1].equals("0000"))
                way = "自选";
            else if (wayArray[selected_index - 1].equals("1001"))
                way = "机选";
            else if (wayArray[selected_index - 1].equals("1002"))
                way = "摇号";
            else if (wayArray[selected_index - 1].equals("1003"))
                way = "幸运选号";
            else if (wayArray[selected_index - 1].equals("1004"))
                way = "查运选号";
            else if (wayArray[selected_index - 1].equals("1005"))
                way = "投注确认页面机选";
            else if (wayArray[selected_index - 1].equals("1006") ||
                wayArray[selected_index - 1].equals("1009") || wayArray[selected_index - 1].equals("1011"))
                way = "投注记录再投";
            else if (wayArray[selected_index - 1].equals("1010") ||
                wayArray[selected_index - 1].equals("1007") || wayArray[selected_index - 1].equals("1008") ||
                wayArray[selected_index - 1].equals("1012"))
                way = "财园转发";
            else if (wayArray[selected_index - 1].equals("1013"))
                way = "大厅快速投注";
            else if (wayArray[selected_index - 1].equals("1014"))
                way = "走势图投注";
            wayTV.setText(way);
            if (originNumChild[selected_index - 1].equals("-") ||
                originNumChild[selected_index - 1] == null || originNumChild[selected_index - 1].equals(""))
                orgNumLayout.setVisibility(View.GONE);
            else {
                if (!originNumChild[selected_index - 1].equals("-"))
                    if (kind.equals("ssq") || kind.equals("qlc") || kind.equals("swxw") ||
                        kind.equals("dlt") || kind.equals("klsf") || kind.equals("22x5")) {
                        LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout,
                                                          originNumChild[selected_index - 1], kind);
                    }
                    else if (kind.equals("3d")) {
                        if (ballNumbersOrg[2].equals("4"))
                            LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout,
                                                              originNumChild[selected_index - 1], kind);
                        else
                            LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, orgBallsLayout,
                                                              originNumChild[selected_index - 1], kind);
                    }
                    else if (kind.equals("jx11x5")) {
                        if (map.get(ballNumbersOrg[2]).equals("9") ||
                            map.get(ballNumbersOrg[2]).equals("10") ||
                            map.get(ballNumbersOrg[2]).equals("11") ||
                            map.get(ballNumbersOrg[2]).equals("12"))
                            LotteryUtils.drawBallsNumberSyxw(BetOrderDetail.this, orgBallsLayout,
                                                             originNumChild[selected_index - 1], kind);
                        else
                            LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout,
                                                              originNumChild[selected_index - 1], kind);
                    }
                    else if (kind.equals("hnklsf")) {
                        if (ballNumbersOrg[2].equals("341") || ballNumbersOrg[2].equals("342") ||
                            ballNumbersOrg[2].equals("221") || ballNumbersOrg[2].equals("222"))
                            LotteryUtils.drawBallsNumberSyxw(BetOrderDetail.this, orgBallsLayout,
                                                             originNumChild[selected_index - 1], kind);
                        else
                            LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout,
                                                              originNumChild[selected_index - 1], kind);
                    }
                    else if (kind.equals("jlk3")) {
                        if (!ballNumbersOrg[2].equals("103") && !ballNumbersOrg[2].equals("116")) {// 103, 116
// 三同号通选，三连号通选
                            if (lotteryMethod.equals("101") || lotteryMethod.equals("102"))// 101, 102
// 和值单式，和值复式
                                LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout,
                                                                  originNumChild[selected_index - 1], kind);
                            else
                                LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, orgBallsLayout,
                                                                  originNumChild[selected_index - 1], kind);
                        }

                        if (ballNumbersOrg[2].equals("103")) {
                            orgBetBallNum.setText("玩法：三连号通选");
                            betBallNum.setText("玩法：三连号通选");
                        }

                        if (ballNumbersOrg[2].equals("116")) {
                            orgBetBallNum.setText("玩法：三连号通选");
                            betBallNum.setText("玩法：三连号通选");
                        }
                    }
                    else if (kind.equals("cqssc") || kind.equals("jxssc")) {
                        String betBallCode =
                            changeCQSSCBetBallCodeNormal(originNumChild[selected_index - 1], lotteryMethod);
                        LotteryUtils.drawBallsLargeNumber(BetOrderDetail.this, orgBallsLayout, betBallCode,
                                                          kind);
                    }
                    else {
                        // draw the ball with number greater than ten
                        LotteryUtils.drawBallsSmallNumber(BetOrderDetail.this, orgBallsLayout,
                                                          originNumChild[selected_index - 1], kind);
                    }
            }
            if (starText[selected_index - 1].equals("-") || luckyText[selected_index - 1].equals("-") ||
                starText[selected_index - 1].equals("") || luckyText[selected_index - 1].equals("") ||
                starText[selected_index - 1] == null || luckyText[selected_index - 1] == null) {
                luckyThingTV.setVisibility(View.GONE);
                luckyThingStar.setVisibility(View.GONE);
                luckyThingTVTitle.setVisibility(View.GONE);
                luckyThingStarTitle.setVisibility(View.GONE);
                luckyThingStarLinear.setVisibility(View.GONE);
                luckyThingTVinear.setVisibility(View.GONE);
            }
            else {
                luckyThingTV.setText(luckyText[selected_index - 1]);
                luckyThingStar.setText(starText[selected_index - 1]);
            }
            layout.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            searchFail();
            e.printStackTrace();
        }
    }

    private String[] xyx5TypeArray = {"11_RX1", "11_RX2", "11_RX3", "11_RX4", "11_RX5", "11_RX6", "11_RX7",
            "11_RX8", "11_ZXQ2_D", "11_ZXQ2_F", "11_ZXQ3_D", "11_ZXQ3_F", "11_ZXQ2", "11_ZXQ3"};

    Map<String, String> map = new HashMap<String, String>();

    private void setRecord() {
        for (int i = 0; i < xyx5TypeArray.length; i++) {
            map.put(xyx5TypeArray[i], String.valueOf(i + 1));
        }
    }

    public void searchFail() {
        message.setVisibility(View.VISIBLE);
        message.setText("查询失败");
    }

    public void searchNoData() {
        message.setVisibility(View.VISIBLE);
        message.setText("查询无数据");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet history details");
        String eventName = "v2 open bet history details";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contact) {
            Intent intent = new Intent();
            intent.setClass(BetOrderDetail.this, Feedback.class);
            startActivity(intent);
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_order_details";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BetOrderDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}