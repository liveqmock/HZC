package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haozan.caipiao.R;
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
import com.haozan.caipiao.widget.PredicateLayout;

public class LotteryCodeAdapter
    extends BaseAdapter {
    private Context context;
    private int length;
    private int screenWidth;
    private LayoutInflater inflater;
    private String kind;
    private String opens;
    private ArrayList<String> ballNumbers;
    private ArrayList<ArrayList<Ball>> awardBallNumbers;
    private ArrayList<Ball> balls = null;
    public Map<Integer, Boolean> isSelected;
    private String[] lotteryCode;
    private String[] lotteryBetWay01;
    private String[] lotteryBetWay02;
    private String[][] teams;

    public final class ViewHolder {
        private TextView betLotteryCodeIndex;
        private PredicateLayout betLotteryCode03;
        private RelativeLayout viewContainer;
        private CheckBox checkBox;
    }

    public LotteryCodeAdapter(Context context, String lotteryCodeRepeatedly, String kind, String opnes,
                              String[][] teams, int screenWidth) {
        this.context = context;
        this.kind = kind;
        this.opens = opnes;
        this.teams = teams;
        this.screenWidth = screenWidth;
        inflater = LayoutInflater.from(this.context);

        ballNumbers = new ArrayList<String>();
        awardBallNumbers = new ArrayList<ArrayList<Ball>>();

        System.out.println("bet_code:" + lotteryCodeRepeatedly);
        if (lotteryCodeRepeatedly != null) {
            lotteryCode = lotteryCodeRepeatedly.split("\\;");
            length = lotteryCode.length;
            init();
            spiltLotteryCode(lotteryCode);
        }
        if (opens.length() >= 5)
            getAwardCode();
    }

    @SuppressLint("UseSparseArrays")
    private void init() {
        // 这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < length; i++) {
            isSelected.put(i, true);
        }
        BetHistoryDetailTool.initAnalyseData();
        setRecord();
    }

    private void spiltLotteryCode(String[] lotteryNum) {
        lotteryBetWay01 = new String[lotteryNum.length];
        lotteryBetWay02 = new String[lotteryNum.length];
        for (int i = 0; i < lotteryNum.length; i++) {
            String[] code = lotteryNum[i].split("\\:");
            ballNumbers.add(code[0]);
            lotteryBetWay01[i] = code[1];
            lotteryBetWay02[i] = code[2];
        }
    }

    private void getAwardCode() {
        try {
            for (int i = 0; i < lotteryCode.length; i++) {
                if (kind.equals("ssq")) {// 双色球投注历史中奖号码高亮显示
                    String betCode =
                        BetHistoryDetailTool.getDanTuoCodeNormal(lotteryCode[i], lotteryBetWay02[i]);
                    String openCode =
                        BetHistoryDetailTool.getDanTuoCodeOpen(opens, betCode.indexOf("$"),
                                                               lotteryBetWay02[i]);
                    balls = SSQRecordMatch.isMatch(betCode, openCode);
                }
                if (kind.equals("3d")) {// 3D球投注历史中奖号码高亮显示
                    if (lotteryBetWay02[i].equals("4"))
                        balls = SWXWRecordMatch.isMatch(lotteryCode[i], opens);
                    else
                        balls = SDRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("swxw")) {// 15选5球投注历史中奖号码高亮显示
                    balls = SWXWRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("qlc")) {// 7乐彩球投注历史中奖号码高亮显示
                    balls = QLCRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("dfljy")) {// 东方6+1球投注历史中奖号码高亮显示
                    balls = DFLJYRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("ssl")) {// 时时乐球投注历史中奖号码高亮显示
                    balls = SSLRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("dlt")) {// 大乐透球投注历史中奖号码高亮显示
                    String betCode =
                        BetHistoryDetailTool.getDanTuoCodeNormal(lotteryCode[i], lotteryBetWay02[i]);
                    String openCode =
                        BetHistoryDetailTool.getDanTuoCodeOpen(opens, betCode.indexOf("$"),
                                                               lotteryBetWay02[i]);
                    if (isGetDltNormalType(lotteryCode[i]))
                        balls = DLTRecordMatch.isMatch(betCode, openCode);
                    else
                        balls = QLCRecordMatch.isMatch(betCode, openCode);
                }
                if (kind.equals("pls")) {// 排列三球投注历史中奖号码高亮显示
                    balls = PLSRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("plw")) {// 排列五球投注历史中奖号码高亮显示
                    balls = PLWRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("qxc")) {// 七星彩球投注历史中奖号码高亮显示
                    balls = SFCRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("22x5")) {// 22选5球投注历史中奖号码高亮显示
                    balls = SWXWRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("cqssc") || kind.equals("jxssc")) {// 时时彩球投注历史中奖号码高亮显示
                    balls = CQSSCRecordMatch.isMatch(lotteryCode[i], opens);
                }
                if (kind.equals("jx11x5")) {// 11选5球投注历史中奖号码高亮显示
                    if (lotteryCode[i].indexOf("(") != -1) {
                        String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(lotteryCode[i]);
                        String betCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                        String openCode =
                            BetHistoryDetailTool.analyseDantuoCodeOpen(opens,
                                                                       betCode.split("\\$")[0].split("\\,").length + 1);
                        balls = SYXRecordMatch.isMatch(betCode, openCode);
                    }
                    else {
                        balls = SYXRecordMatch.isMatch(lotteryCode[i], opens);
                    }
                }
                if (kind.equals("klsf") || kind.equals("hnklsf")) {// 排列五球投注历史中奖号码高亮显示
                    balls = HNKLSFRecordMatch.isMatch(lotteryCode[i], opens);
                }
// else if (kind.equals("jlk3")) {// 吉林快三球投注历史中奖号码高亮显示
// balls = JLK3RecordMatch.isMatch(lotteryCode[i], opens);
// }
                awardBallNumbers.add(balls);
            }
        }
        catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "数据解析失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGetDltNormalType(String s) {
        if (s.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (arg1 == null) {
            arg1 = inflater.inflate(R.layout.lottery_code_display, null);
            viewHolder = new ViewHolder();
            viewHolder.betLotteryCodeIndex = (TextView) arg1.findViewById(R.id.lottery_code_index);
            viewHolder.betLotteryCode03 = (PredicateLayout) arg1.findViewById(R.id.lottery_balls_num_03);
            viewHolder.betLotteryCode03.setQuick(true);
            viewHolder.checkBox = (CheckBox) arg1.findViewById(R.id.checkBox1);
            viewHolder.viewContainer = (RelativeLayout) arg1.findViewById(R.id.lottery_code_container);
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

// if (length == 1)
// arg1.setBackgroundResource(R.drawable.bet_order_listview_bg);
// else {
// if (arg0 == (length - 1))
// arg1.setBackgroundResource(R.drawable.bet_order_listview_bg);
// else
// arg1.setBackgroundResource(R.drawable.order_list_item_bg);
// }

        getCOdeWithCircleBg(viewHolder, arg0);
        viewHolder.betLotteryCodeIndex.setText(BetHistoryDetailTool.subName(kind, lotteryBetWay01[arg0], lotteryBetWay02[arg0]));

        if (kind.equals("jlk3") && lotteryBetWay02[arg0].equals("103")) {
            viewHolder.betLotteryCode03.setVisibility(View.INVISIBLE);
            viewHolder.betLotteryCodeIndex.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
            viewHolder.betLotteryCodeIndex.setTextColor(context.getResources().getColor(R.color.white));
        }
        else if (kind.equals("jlk3") && lotteryBetWay02[arg0].equals("116")) {
            viewHolder.betLotteryCode03.setVisibility(View.INVISIBLE);
            viewHolder.betLotteryCodeIndex.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
            viewHolder.betLotteryCodeIndex.setTextColor(context.getResources().getColor(R.color.white));
        }
        else {
            viewHolder.betLotteryCode03.setVisibility(View.VISIBLE);
            viewHolder.betLotteryCodeIndex.setBackgroundResource(R.color.transparent);
            viewHolder.betLotteryCodeIndex.setTextColor(context.getResources().getColor(R.color.gray));
        }

        if (kind.equals("sfc") || kind.equals("r9")) {
            viewHolder.betLotteryCodeIndex.setVisibility(View.GONE);
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnClickListener(new LvCheckBoxListener(arg0, arg1));
        viewHolder.checkBox.setChecked(isSelected.get(arg0));
        return arg1;
    }

    private ArrayList<Ball> changeCQSSCBetBallCodeAward(ArrayList<Ball> ballNumber, String lotteryBetWay01) {// 过滤器，为了对彩种重庆时时彩大小单双玩法格式进行调整，如果传入的投注数据不属于重庆时时彩大小单双玩法就直接跳过。
        if (kind.equals("cqssc") && lotteryBetWay01.equals("701") || kind.equals("jxssc") &&
            lotteryBetWay01.equals("701")) {
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
        if (kind.equals("cqssc") && lotteryBetWay01.equals("701") || kind.equals("jxssc") &&
            lotteryBetWay01.equals("701")) {
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

    private void getCOdeWithCircleBg(ViewHolder viewHolder, int i) {
        if (!opens.equals("null") && !kind.equals("null")) {
            if (!kind.equals("sfc") && !kind.equals("r9")) {
                if (kind.equals("jlk3")) {
                    if (!lotteryBetWay02[i].equals("103") && !lotteryBetWay02[i].equals("116")) {// 103, 116
// 三同号通选，三连号通选
                        if (lotteryBetWay02[i].equals("101") || lotteryBetWay02[i].equals("102"))
                            LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                              ballNumbers.get(i), kind);
                        else
                            LotteryUtils.drawBallsSmallNumber(context, viewHolder.betLotteryCode03,
                                                              ballNumbers.get(i), kind);
                    }
                }
                else if (kind.equals("cqssc") || kind.equals("jxssc")) {
                    ArrayList<Ball> betBallCode =
                        changeCQSSCBetBallCodeAward(awardBallNumbers.get(i), lotteryBetWay02[i]);
                    LotteryUtils.drawHallBalls(context, viewHolder.betLotteryCode03, betBallCode, kind);
                }
                else {
                    if (lotteryBetWay02[i].equals("4")) {
                        String sqdtDantuoCode =
                            BetHistoryDetailTool.getDanTuoCodeNormal(ballNumbers.get(i), lotteryBetWay02[i]);
                        LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                          sqdtDantuoCode, kind);
                    }
                    else if (ballNumbers.get(i).indexOf("(") != -1) {
                        String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(ballNumbers.get(i));
                        String syxwDantuoBetCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                        LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                          syxwDantuoBetCode, kind);
                    }
                    else {
                        LotteryUtils.drawHallBalls(context, viewHolder.betLotteryCode03,
                                                   awardBallNumbers.get(i), kind);
                    }
                }
            }
        }
        else {
            if (kind.equals("dfljy") || kind.equals("ssl") || kind.equals("pls") || kind.equals("plw") ||
                kind.equals("qxc")) {
                LotteryUtils.drawBallsSmallNumber(context, viewHolder.betLotteryCode03, ballNumbers.get(i),
                                                  kind);
            }
            else if (kind.equals("3d")) {
                if (lotteryBetWay02[i].equals("4")) {
                    String betDantuoCode =
                        BetHistoryDetailTool.getDanTuoCodeNormal(ballNumbers.get(i), lotteryBetWay02[i]);
                    LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03, betDantuoCode,
                                                      kind);
                }
                else
                    LotteryUtils.drawBallsSmallNumber(context, viewHolder.betLotteryCode03,
                                                      ballNumbers.get(i), kind);
            }
            else if (kind.equals("jx11x5")) {
                if (ballNumbers.get(i).indexOf("(") != -1) {
                    String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(ballNumbers.get(i));
                    String syxwDantuoBetCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                    LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                      syxwDantuoBetCode, kind);
                }
                else {
                    if (map.get(lotteryBetWay02[i]).equals("9") || map.get(lotteryBetWay02[i]).equals("10") ||
                        map.get(lotteryBetWay02[i]).equals("11") || map.get(lotteryBetWay02[i]).equals("12"))
                        LotteryUtils.drawBallsNumberSyxw(context, viewHolder.betLotteryCode03,
                                                         ballNumbers.get(i), kind);
                    else
                        LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                          ballNumbers.get(i), kind);
                }
            }
            else if (kind.equals("hnklsf")) {
                if (lotteryBetWay02[i].equals("341") || lotteryBetWay02[i].equals("342") ||
                    lotteryBetWay02[i].equals("221") || lotteryBetWay02[i].equals("222"))
                    LotteryUtils.drawBallsNumberSyxw(context, viewHolder.betLotteryCode03,
                                                     ballNumbers.get(i), kind);
                else
                    LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                      ballNumbers.get(i), kind);
            }
            else if (kind.equals("jlk3")) {
                if (!lotteryBetWay02[i].equals("103") && !lotteryBetWay02[i].equals("116")) {// 103, 116
// 三同号通选，三连号通选
                    if (lotteryBetWay02[i].equals("101") || lotteryBetWay02[i].equals("102"))
                        LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03,
                                                          ballNumbers.get(i), kind);
                    else
                        LotteryUtils.drawBallsSmallNumber(context, viewHolder.betLotteryCode03,
                                                          ballNumbers.get(i), kind);
                }
            }
            else if (kind.equals("cqssc") || kind.equals("jxssc")) {
                String betBallCode = changeCQSSCBetBallCodeNormal(ballNumbers.get(i), lotteryBetWay02[i]);
                LotteryUtils.drawBallsSmallNumber(context, viewHolder.betLotteryCode03, betBallCode, kind);
            }
            else {
                String dantuoCode =
                    BetHistoryDetailTool.getDanTuoCodeNormal(ballNumbers.get(i), lotteryBetWay02[i]);
                LotteryUtils.drawBallsLargeNumber(context, viewHolder.betLotteryCode03, dantuoCode, kind);
            }
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

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    class LvCheckBoxListener
        implements OnClickListener {
        private int position;
        private View arg1;

        LvCheckBoxListener(int pos, View arg) {
            position = pos;
            arg1 = arg;
        }

        @Override
        public void onClick(View v) {
            ViewHolder vHollder = (ViewHolder) arg1.getTag();
            isSelected.put(position, vHollder.checkBox.isChecked());
        }
    }
}
