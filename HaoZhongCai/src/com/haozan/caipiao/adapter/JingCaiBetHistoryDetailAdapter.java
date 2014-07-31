package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.JingCaiBetHistoryDetailItemData;

public class JingCaiBetHistoryDetailAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<JingCaiBetHistoryDetailItemData> jingCaiBetHistoryDetailList;
    private String dataType = "71";
    private String[] goal;
    private String[] rate;
//    private int viewWidth;
    private Map<String, String> jingCaiBanQuan;
    private String[] jingCaiBanNum = {"33", "31", "30", "13", "11", "10", "03", "01", "00"};
    private String[] jingCaiBanText = {"胜胜", "胜平", "胜负", "平胜", "平平", "平负", "负胜", "负平", "负负"};
    private String jiuQiuSu;
    private int screenWidth=0;

    public final class ViewHolder {
        private TextView betTerm;
        private TextView betTeam;
        private TextView gameScore;
        private TextView betWinResult;
        private TextView betEqualResult;
        private TextView betLostResult;
        private TextView concedePoints;
        private TextView betGoal;
        private LinearLayout shengpingfuLayout;
        private LinearLayout goalScoreLayout;
    }

    public JingCaiBetHistoryDetailAdapter(Context context,
                                          ArrayList<JingCaiBetHistoryDetailItemData> jingCaiBetHistoryDetailList,int screenWidth) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.jingCaiBetHistoryDetailList = jingCaiBetHistoryDetailList;
        this.screenWidth=screenWidth;
        initMap();
    }

    private void initMap() {
        jingCaiBanQuan = new HashMap<String, String>();
        for (int i = 0; i < 9; i++)
            jingCaiBanQuan.put(jingCaiBanNum[i], jingCaiBanText[i]);
    }

    public void setJingCaiDetailDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public int getCount() {
        return jingCaiBetHistoryDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return jingCaiBetHistoryDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.jingcai_bet_history_detail_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.betTerm = (TextView) convertView.findViewById(R.id.bet_term);
            viewHolder.betTeam = (TextView) convertView.findViewById(R.id.bet_foot_ball_team_name);
            viewHolder.gameScore = (TextView) convertView.findViewById(R.id.bet_match_score);
            viewHolder.betWinResult = (TextView) convertView.findViewById(R.id.bet_win_status);
            viewHolder.betEqualResult = (TextView) convertView.findViewById(R.id.bet_equal_status);
            viewHolder.betLostResult = (TextView) convertView.findViewById(R.id.bet_lost_status);
            viewHolder.concedePoints = (TextView) convertView.findViewById(R.id.concede_points);
            viewHolder.betGoal = (TextView) convertView.findViewById(R.id.jing_cai_goal_score);
            viewHolder.shengpingfuLayout =
                (LinearLayout) convertView.findViewById(R.id.sheng_ping_fu_container);
            viewHolder.goalScoreLayout = (LinearLayout) convertView.findViewById(R.id.jin_qiu_container);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if (getCount() == 1)
//            convertView.setBackgroundResource(R.drawable.bet_order_listview_bg);
//        else {
//            if (position == (getCount() - 1))
//                convertView.setBackgroundResource(R.drawable.bet_order_listview_bg);
//            else
//                convertView.setBackgroundResource(R.drawable.order_list_item_bg);
//        }

        viewHolder.betTerm.setText(jingCaiBetHistoryDetailList.get(position).getBetTerm());
        if (dataType.substring(0, 1).equals("7")) {
            viewHolder.betTeam.setText(getGameTeamName(position));
        }
        else {
            viewHolder.betTeam.setText(getLqGameTeamName(position));
        }
        if (dataType.substring(0, 1).equals("7")) {
            viewHolder.gameScore.setText("比分：" +
                jingCaiBetHistoryDetailList.get(position).getFullMatchScore());
            viewHolder.concedePoints.setText("让球:" +
                jingCaiBetHistoryDetailList.get(position).getConcedePoints());
        }else if(dataType.equals("75")){
            viewHolder.concedePoints.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.gameScore.setText("比分：" +
                jingCaiBetHistoryDetailList.get(position).getFullMatchScore());
            if (dataType.equals("81")) {
                if (jingCaiBetHistoryDetailList.get(position).getLetScore() != null) {
                    String letScore = jingCaiBetHistoryDetailList.get(position).getLetScore()[position];
                    if (letScore != null)
                        viewHolder.concedePoints.setText("让分：" + letScore);
                }
                else {
                    viewHolder.concedePoints.setText("让分：--");
                }
            }
            else if (dataType.equals("84")) {
                if (jingCaiBetHistoryDetailList.get(position).getScore() != null) {
                    String setScore = jingCaiBetHistoryDetailList.get(position).getScore()[position];
                    if (setScore != null)
                        viewHolder.concedePoints.setText("预设分：" + setScore);
                }
                else {
                    viewHolder.concedePoints.setText("预设分：--");
                }
            }
            else
                viewHolder.concedePoints.setVisibility(View.INVISIBLE);
        }

        if (dataType.equals("71")) { //竞彩足球 71：让球胜平负 
            viewHolder.shengpingfuLayout.setVisibility(View.VISIBLE);
            viewHolder.goalScoreLayout.setVisibility(View.GONE);
            initDataWinLostDraw(viewHolder, position);
        }
        else if (dataType.equals("72")) { //竞彩足球 72：总进球数
            viewHolder.shengpingfuLayout.setVisibility(View.GONE);
            viewHolder.goalScoreLayout.setVisibility(View.VISIBLE);
            spiltBetGoal(jingCaiBetHistoryDetailList.get(position).getBetGoal());
            addItemView(viewHolder.goalScoreLayout, jingCaiBetHistoryDetailList.get(position).getBetGoal(),position);
        }
        else if (dataType.equals("75")) {//竞彩足球  75：胜平负 
            viewHolder.shengpingfuLayout.setVisibility(View.VISIBLE);
            viewHolder.goalScoreLayout.setVisibility(View.GONE);
            initDataWinLostDraw(viewHolder, position);
        }
        else {
            viewHolder.shengpingfuLayout.setVisibility(View.GONE);
            viewHolder.betGoal.setVisibility(View.GONE);
            viewHolder.goalScoreLayout.setVisibility(View.VISIBLE);
            spiltBetGoal(jingCaiBetHistoryDetailList.get(position).getBetGoal());
            addItemView(viewHolder.goalScoreLayout, jingCaiBetHistoryDetailList.get(position).getBetGoal(),
                        position);
        }
        return convertView;
    }

    private void stringToArray(String betGoal) {
        goal = betGoal.split("\\|")[0].split("\\,");
        if (betGoal.split("\\|").length > 1)
            rate = betGoal.split("\\|")[1].split("\\,");
    }

    private void spiltBetGoal(String betGoal) {
        stringToArray(betGoal);
        StringBuilder sbGoalAndRate = new StringBuilder();
        for (int i = 0; i < goal.length; i++) {
            sbGoalAndRate.append(goal[i]);
            if (betGoal.split("\\|").length > 1)
                sbGoalAndRate.append("[" + rate[i] + "]");
            sbGoalAndRate.append(",");
        }
        sbGoalAndRate.delete(sbGoalAndRate.length() - 1, sbGoalAndRate.length());
        jiuQiuSu = sbGoalAndRate.toString();
    }

    private void addItemView(LinearLayout itemContainer, String betGoal, int position) {
        int length = goal.length;
        int yu = length % 3;
        int lineNum = length / 3 + (yu + (3 - yu)) / 3;
        int[] lineNumArray = new int[lineNum];

        for (int n = 0; n < length / 3; n++)
            lineNumArray[n] = 3;

        if (length / 3 == 0)
            lineNumArray[lineNum - 1] = yu;
        else if (length / 3 < lineNum)
            lineNumArray[lineNum - 1] = yu;

        itemContainer.removeAllViews();
        for (int j = 0; j < lineNum; j++) {
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams linParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(linParams);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.removeAllViews();
            for (int i = 0; i < lineNumArray[j]; i++) {
                TextView tv = new TextView(context);
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
//                if (itemContainer.getMeasuredWidth() != 0)
//                    viewWidth = itemContainer.getMeasuredWidth() - 15;
                tv.setWidth((screenWidth-70) / 3 );
                tv.setGravity(Gravity.CENTER);
                setTextViewText(tv, betGoal, i, j);
                addAwardSign(tv, i, position);
                tv.setPadding(5, 1, 5, 1);
                linParams.setMargins(5, 1, 5, 1);
                linearLayout.addView(tv, linParams);
            }
            itemContainer.addView(linearLayout);
        }
    }

    private void addAwardSign(TextView tv, int i, int position) {
        String fullGameScore = jingCaiBetHistoryDetailList.get(position).getFullMatchScore();
        String halfGameScore = jingCaiBetHistoryDetailList.get(position).getHalfMatchScore();
        if (!fullGameScore.equals("") || !halfGameScore.equals("")) {
            if (dataType.equals("72")) {//竞彩足球 72：总进球数
                String[] fullScore = fullGameScore.split("\\:");
                if (String.valueOf(Integer.valueOf(fullScore[0]) + Integer.valueOf(fullScore[1])).equals(goal[i])) {
                    tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    tv.setTextColor(context.getResources().getColor(R.color.golden));
                }

            }
            else if (dataType.equals("73")) {//竞彩足球 73：比分
                String matchScore = fullGameScore.split("\\:")[0] + fullGameScore.split("\\:")[1];
                if (goal[i].equals(matchScore)) {
                    tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    tv.setTextColor(context.getResources().getColor(R.color.golden));
                }
            }
            else if (dataType.equals("74")) {//竞彩足球 74：半全场
                String[] halfScore = halfGameScore.split("\\:");
                if (String.valueOf(Integer.valueOf(halfScore[0]) + Integer.valueOf(halfScore[1])).equals(goal[i])) {
                    tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    tv.setTextColor(context.getResources().getColor(R.color.golden));
                }
            }
            else if (dataType.equals("81")) {//竞彩篮球 81：让分胜负 
                if (jingCaiBetHistoryDetailList.get(position).getResultGG() != null) {
                    if (jingCaiBetHistoryDetailList.get(position).getResultGG()[position] != null) {
                        if (goal[i].equals("2") &&
                            jingCaiBetHistoryDetailList.get(position).getResultGG()[position].equals("让分主负")) {
                            tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                            tv.setTextColor(context.getResources().getColor(R.color.golden));
                        }
                        else if (goal[i].equals("1") &&
                            jingCaiBetHistoryDetailList.get(position).getResultGG()[position].equals("让分主胜")) {
                            tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                            tv.setTextColor(context.getResources().getColor(R.color.golden));
                        }
                    }
                }
            }
            else if (dataType.equals("82")) {//竞彩篮球 82：胜负 
                if (goal[i].equals("1") &&
                    jingCaiBetHistoryDetailList.get(position).getGameResult().equals("主胜")) {
                    tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    tv.setTextColor(context.getResources().getColor(R.color.golden));
                }
                else if (goal[i].equals("2") &&
                    jingCaiBetHistoryDetailList.get(position).getGameResult().equals("主负")) {
                    tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    tv.setTextColor(context.getResources().getColor(R.color.golden));
                }
            }
            else if (dataType.equals("84")) { //竞彩篮球 84：大小分 
                if (jingCaiBetHistoryDetailList.get(position).getResultGG() != null) {
                    if (jingCaiBetHistoryDetailList.get(position).getResultGG()[position] != null) {
                        if (goal[i].equals("1") &&
                            jingCaiBetHistoryDetailList.get(position).getResultGG()[position].equals("大")) {
                            tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                            tv.setTextColor(context.getResources().getColor(R.color.golden));
                        }
                        else if (goal[i].equals("2") &&
                            jingCaiBetHistoryDetailList.get(position).getResultGG()[position].equals("小")) {
                            tv.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                            tv.setTextColor(context.getResources().getColor(R.color.golden));
                        }
                    }
                }
            }
        }
    }

    private void setTextViewText(TextView tv, String betGoal, int i, int j) {
        if (dataType.equals("72")) {
// tv.setText(jiuQiuSu);
            if (betGoal.split("\\|").length > 1)
                tv.setText(goal[3 * j + i] + "[" + rate[3 * j + i] + "]");
            else
                tv.setText(goal[3 * j + i]);
        }
        else if (dataType.equals("74")) {
            if (betGoal.split("\\|").length > 1)
                tv.setText(jingCaiBanQuan.get(goal[i]) + "[" + rate[i] + "]");
            else
                tv.setText(jingCaiBanQuan.get(goal[i]));
        }
        else if (dataType.equals("73")) {
            char[] masterAndGuestScore = goal[i].toCharArray();
            String mScore = String.valueOf(masterAndGuestScore[0]);
            String gScore = String.valueOf(masterAndGuestScore[1]);
            if (betGoal.split("\\|").length > 1) {
                if (mScore.equals("9") && gScore.equals("0"))
                    tv.setText("胜其他");
                else if (mScore.equals("9") && gScore.equals("9"))
                    tv.setText("平其他");
                else if (mScore.equals("0") && gScore.equals("9"))
                    tv.setText("负其他");
                else
                    tv.setText(mScore + ":" + gScore + "[" + rate[i] + "]");
            }
            else {
                if (mScore.equals("9") && gScore.equals("0"))
                    tv.setText("胜其他");
                else if (mScore.equals("9") && gScore.equals("9"))
                    tv.setText("平其他");
                else if (mScore.equals("0") && gScore.equals("9"))
                    tv.setText("负其他");
                else
                    tv.setText(mScore + ":" + gScore);
            }
        }
        else if (dataType.equals("81") || dataType.equals("82")) {
            if (betGoal.split("\\|").length > 1) {
                if (goal[i].equals("2"))
                    tv.setText("主负" + "[" + rate[i] + "]");
                else if (goal[i].equals("1"))
                    tv.setText("主胜" + "[" + rate[i] + "]");
            }
            else {
                if (goal[i].equals("2"))
                    tv.setText("主负");
                else if (goal[i].equals("1"))
                    tv.setText("主胜");
            }
        }
        else if (dataType.equals("84")) {
            if (betGoal.split("\\|").length > 1) {
                if (goal[i].equals("2"))
                    tv.setText("小" + "[" + rate[i] + "]");
                else if (goal[i].equals("1"))
                    tv.setText("大" + "[" + rate[i] + "]");
            }
            else {
                if (goal[i].equals("2"))
                    tv.setText("小");
                else if (goal[i].equals("1"))
                    tv.setText("大");
            }
        }
    }

    private void initDataWinLostDraw(ViewHolder viewHolder, int position) {

        String winBetResult = jingCaiBetHistoryDetailList.get(position).getBetResultWin();
        String equalBetResult = jingCaiBetHistoryDetailList.get(position).getBetResultEqual();
        String lostBetResult = jingCaiBetHistoryDetailList.get(position).getBetResultLost();
        String gameResult = jingCaiBetHistoryDetailList.get(position).getGameResult();

        if (winBetResult != null) {
            viewHolder.betWinResult.setText(winBetResult.trim());
            viewHolder.betWinResult.setTextColor(Color.BLACK);
            setTextBold(viewHolder.betWinResult, true);
            if (gameResult.equals("3")) {
                viewHolder.betWinResult.setTextColor(context.getResources().getColor(R.color.golden));
                viewHolder.betWinResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
            }
        }
        else {
            viewHolder.betWinResult.setText("胜");
            viewHolder.betWinResult.setTextColor(context.getResources().getColor(R.color.light_gray));
            setTextBold(viewHolder.betWinResult, false);
        }
        if (equalBetResult != null) {
            viewHolder.betEqualResult.setText(equalBetResult.trim());
            viewHolder.betEqualResult.setTextColor(Color.BLACK);
            setTextBold(viewHolder.betEqualResult, true);
            if (gameResult.equals("1")) {
                viewHolder.betEqualResult.setTextColor(context.getResources().getColor(R.color.golden));
                viewHolder.betEqualResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
            }
        }
        else {
            viewHolder.betEqualResult.setText("平");
            viewHolder.betEqualResult.setTextColor(context.getResources().getColor(R.color.light_gray));
            setTextBold(viewHolder.betEqualResult, false);
        }

        if (lostBetResult != null) {
            viewHolder.betLostResult.setText(lostBetResult.trim());
            viewHolder.betLostResult.setTextColor(Color.BLACK);
            setTextBold(viewHolder.betLostResult, true);
            if (gameResult.equals("0")) {
                viewHolder.betLostResult.setTextColor(context.getResources().getColor(R.color.golden));
                viewHolder.betLostResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
            }
        }
        else {
            viewHolder.betLostResult.setText("负");
            viewHolder.betLostResult.setTextColor(context.getResources().getColor(R.color.light_gray));
            setTextBold(viewHolder.betLostResult, false);
        }
    }

    private void setTextBold(TextView bt, boolean isBold) {
        if (isBold) {
            bt.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
            bt.setTextColor(Color.WHITE);
        }
        else {
            bt.setBackgroundResource(R.drawable.jingcai_history_button_bg_normal);
        }
    }

    private String getGameTeamName(int position) {
        return jingCaiBetHistoryDetailList.get(position).getMaster() + "[主]  " + "VS" + "  " +
            jingCaiBetHistoryDetailList.get(position).getGuest() + "[客]";

    }

    private String getLqGameTeamName(int position) {
        return jingCaiBetHistoryDetailList.get(position).getGuest() + "[客]  " + "VS" + "  " +
            jingCaiBetHistoryDetailList.get(position).getMaster() + "[主]";

    }
}
