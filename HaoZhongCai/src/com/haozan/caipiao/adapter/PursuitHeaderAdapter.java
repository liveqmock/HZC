package com.haozan.caipiao.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.widget.PredicateLayout;

public class PursuitHeaderAdapter
    extends BaseAdapter {

    private Context context;
    private String pursuitCode;
    private String lotteryId;
    private String[] ballCodeNum;
    private LayoutInflater inflater;
    private String[] lotteryBetWay01;
    private String[] lotteryBetWay02;

    public PursuitHeaderAdapter(Context context, String pursuitCode, String lotteryId) {
        this.context = context;
        this.pursuitCode = pursuitCode;
        this.lotteryId = lotteryId;
        inflater = LayoutInflater.from(this.context);
        ballCodeNum = StringUtil.spliteString(pursuitCode, ";");
        setRecord();
        spiltLotteryCode(ballCodeNum);
        BetHistoryDetailTool.initAnalyseData();
    }

    private void spiltLotteryCode(String[] lotteryNum) {
        lotteryBetWay01 = new String[lotteryNum.length];
        lotteryBetWay02 = new String[lotteryNum.length];
        for (int i = 0; i < lotteryNum.length; i++) {
            String[] code = lotteryNum[i].split("\\:");
            lotteryBetWay01[i] = code[1];
            lotteryBetWay02[i] = code[2];
        }
    }

    public final class ViewHolder {
        public TextView puruistLotteryCodeIndex;
        public PredicateLayout investBallsLayout;
    }

    @Override
    public int getCount() {
        if (lotteryId.equals("ssq") || lotteryId.equals("qlc") || lotteryId.equals("swxw"))
            return lotteryBetCodeNum(ballCodeNum).split("\\;").length;
        else
            return sdZlZsNum(ballCodeNum).split("\\;").length;
    }

    @Override
    public Object getItem(int position) {

        return puruistCodeNum(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String puruistCodeNum(int position) {
        if (lotteryId.equals("ssq") || lotteryId.equals("qlc") || lotteryId.equals("swxw") ||
            lotteryId.equals("22x5"))
            return lotteryBetCodeNum(ballCodeNum).split("\\;")[position];
        else
            return sdZlZsNum(ballCodeNum).split("\\;")[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bet_ball_pursuit, null);
            viewHolder.puruistLotteryCodeIndex = (TextView) convertView.findViewById(R.id.puruist_ball_index);
            viewHolder.investBallsLayout = (PredicateLayout) convertView.findViewById(R.id.invest_balls01);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.puruistLotteryCodeIndex.setText(BetHistoryDetailTool.subName(lotteryId,
                                                                                lotteryBetWay01[position],
                                                                                lotteryBetWay02[position]));
        if (lotteryId.equals("ssq") || lotteryId.equals("qlc") || lotteryId.equals("swxw") ||
            lotteryId.equals("dlt") || lotteryId.equals("22x5")) {
            LotteryUtils.drawBallsLargeNumber(context,
                                              viewHolder.investBallsLayout,
                                              BetHistoryDetailTool.getDanTuoCodeNormal(puruistCodeNum(position),
                                                                                       lotteryBetWay02[position]),
                                              lotteryId);
        }
        else if (lotteryId.equals("3d")) {
            if (lotteryBetWay02[position].equals("4"))
                LotteryUtils.drawBallsLargeNumber(context,
                                                  viewHolder.investBallsLayout,
                                                  BetHistoryDetailTool.getDanTuoCodeNormal(puruistCodeNum(position),
                                                                                           lotteryBetWay02[position]),
                                                  lotteryId);
            else
                LotteryUtils.drawBallsSmallNumber(context, viewHolder.investBallsLayout,
                                                  puruistCodeNum(position), lotteryId);
        }
        else if (lotteryId.equals("jx11x5")) {
            if (puruistCodeNum(position).indexOf("(") != -1) {
                String syxwDantuoCode = BetHistoryDetailTool.filterSyxwDantuoCode(puruistCodeNum(position));
                String syxwDantuoBetCode = BetHistoryDetailTool.annalyseDanTuoCode(syxwDantuoCode);
                LotteryUtils.drawBallsLargeNumber(context, viewHolder.investBallsLayout, syxwDantuoBetCode,
                                                  lotteryId);
            }
            else {
                if (map.get(lotteryBetWay02[position]).equals("9") ||
                    map.get(lotteryBetWay02[position]).equals("10") ||
                    map.get(lotteryBetWay02[position]).equals("11") ||
                    map.get(lotteryBetWay02[position]).equals("12"))
                    LotteryUtils.drawBallsNumberSyxw(context, viewHolder.investBallsLayout,
                                                     puruistCodeNum(position), lotteryId);
                else
                    LotteryUtils.drawBallsLargeNumber(context, viewHolder.investBallsLayout,
                                                      puruistCodeNum(position), lotteryId);
            }
        }
        else if (lotteryId.equals("hnklsf")) {
            if (lotteryBetWay02[position].equals("341") || lotteryBetWay02[position].equals("342") ||
                lotteryBetWay02[position].equals("221") || lotteryBetWay02[position].equals("222"))
                LotteryUtils.drawBallsNumberSyxw(context, viewHolder.investBallsLayout,
                                                 puruistCodeNum(position), lotteryId);
            else
                LotteryUtils.drawBallsLargeNumber(context, viewHolder.investBallsLayout,
                                                  puruistCodeNum(position), lotteryId);
        }
        else if (lotteryId.equals("cqssc") || lotteryId.equals("jxssc")) {
            String betBallCode =
                changeCQSSCBetBallCodeNormal(puruistCodeNum(position), lotteryBetWay02[position]);
            LotteryUtils.drawBallsSmallNumber(context, viewHolder.investBallsLayout, betBallCode, lotteryId);
        }
        else if (lotteryId.equals("jlk3")) {
            if (!lotteryBetWay02[position].equals("103") && !lotteryBetWay02[position].equals("116")) {// 103,
// 116 三同号通选，三连号通选
                if (lotteryBetWay02[position].equals("101") || lotteryBetWay02[position].equals("102"))
                    LotteryUtils.drawBallsLargeNumber(context, viewHolder.investBallsLayout,
                                                      puruistCodeNum(position), lotteryId);
                else
                    LotteryUtils.drawBallsSmallNumber(context, viewHolder.investBallsLayout,
                                                      puruistCodeNum(position), lotteryId);
            }
// else {//三同号通选，三连号通选 需要用文字显示
// if (lotteryBetWay02[position].equals("103")) {
// orgBetBallNum.setText("玩法：三同号通选");
// betBallNum.setText("玩法：三同号通选");
// }
//
// if (lotteryBetWay02[position].equals("116")) {
// orgBetBallNum.setText("玩法：三连号通选");
// betBallNum.setText("玩法：三连号通选");
// }
// }
        }
        else {
            // 3d组三或者组六
            LotteryUtils.drawBallsSmallNumber(context, viewHolder.investBallsLayout,
                                              puruistCodeNum(position), lotteryId);
        }
        return convertView;
    }

    private String changeCQSSCBetBallCodeNormal(String ballNumber, String lotteryBetWay01) {
        StringBuilder sbCQSSC = new StringBuilder();
        String[] ballNumberArray = ballNumber.split("\\,");
        if (lotteryId.equals("cqssc") && lotteryBetWay01.equals("701") || lotteryId.equals("jxssc") &&
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

    private String[] xyx5TypeArray = {"11_RX1", "11_RX2", "11_RX3", "11_RX4", "11_RX5", "11_RX6", "11_RX7",
            "11_RX8", "11_ZXQ2_D", "11_ZXQ2_F", "11_ZXQ3_D", "11_ZXQ3_F", "11_ZXQ2", "11_ZXQ3"};
    Map<String, String> map = new HashMap<String, String>();

    private void setRecord() {
        for (int i = 0; i < xyx5TypeArray.length; i++) {
            map.put(xyx5TypeArray[i], String.valueOf(i + 1));
        }
    }

    private String sdZlZsNum(String[] ballCodeNum) {
        StringBuilder sb_ballNum = new StringBuilder();
        for (int i = 0; i < ballCodeNum.length; i++) {
            String[] ballNumbers = StringUtil.spliteString(ballCodeNum[i], ":");
            if (lotteryId.equals("3d") && ballNumbers[2].equals("2") &&
                (ballNumbers[1].equals("2") || ballNumbers[1].equals("3"))) {
                String[] ball = StringUtil.spliteString(ballNumbers[0], ",");
                ballNumbers[0] = ball[0];
                sb_ballNum.append(ballNumbers[0]);
                sb_ballNum.append(";");
            }
            else {
                sb_ballNum.append(ballNumbers[0]);
                sb_ballNum.append(";");
            }
            ballNumbers = null;
        }
        sb_ballNum.delete(sb_ballNum.length() - 1, sb_ballNum.length());
        return sb_ballNum.toString();
    }

    private String lotteryBetCodeNum(String[] ballCodeNum) {
        StringBuilder sb_ballNumbers = new StringBuilder();
        for (int i = 0; i < ballCodeNum.length; i++) {
            String[] ballNumbers = StringUtil.spliteString(ballCodeNum[i], ":");
            sb_ballNumbers.append(ballNumbers[0]);
            sb_ballNumbers.append(";");
            ballNumbers = null;
        }
        sb_ballNumbers.delete(sb_ballNumbers.length() - 1, sb_ballNumbers.length());
        return sb_ballNumbers.toString();
    }

}
