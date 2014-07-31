package com.haozan.caipiao.util.lottery.analyse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.types.bet.jczq.SportEachTeam;
import com.haozan.caipiao.types.bet.jczq.SportOrderItem;
import com.haozan.caipiao.types.order.jczq.JCZQOrderDetail;
import com.haozan.caipiao.types.order.jczq.JCZQOrderEachTeam;
import com.haozan.caipiao.util.error.LocalExceptionHandler;

public class JCZQNumAnalyse {

    public static final String[] JCZQ_BETWAY_CODE = {"71", "72", "73", "74", "75"};
    public static final String[] JCZQ_BETWAY_NAME = {"让分胜平负", "总进球", "比分", "半全场", "胜平负"};

    public static final String[] JCZQ_BUNCH_CODE = {"201", "301", "401", "501", "601", "701", "801", "100"};
    public static final String[] JCZQ_BUNCH_NAME = {"2串1", "3串1", "4串1", "5串1", "6串1", "7串1", "8串1", "单关"};

    public static final String[] JCZQ_SPF_CODE = {"3", "1", "0"};
    public static final String[] JCZQ_SPF_NAME = {"胜", "平", "负"};

    public static final String[] JCZQ_DAY = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public static final String JCZQ_GUOGUAN_CODE = "100";// 当串数为100时候代表单关
    public static final String JCZQ_DANGUAN_NAME = "单关";
    public static final String JCZQ_GUOGUAN_NAME = "过关";

    public static SportOrderItem analyseJCZQOrder(String codes) {

        try {
            SportOrderItem order = new SportOrderItem();

            String[] spliteInf = codes.split(":");
            String[] eachTeam = spliteInf[0].split("\\|");

            ArrayList<SportEachTeam> list = new ArrayList<SportEachTeam>();
            for (int i = 0; i < eachTeam.length; i++) {
                SportEachTeam item = new SportEachTeam();
                String[] each = eachTeam[i].split("\\$|=");
                item.setDay(Integer.valueOf(each[0]));
                item.setIndex(each[1]);
                item.setBetInf(each[2]);
                list.add(item);
            }

            order.setTeamInfList(list);
            order.setBunch(spliteInf[2]);
            order.setTimes(Integer.valueOf(spliteInf[3]));
            return order;
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
            return null;
        }
    }

    /**
     * 解析赔率
     * 
     * @param codes
     * @return 返回格式数组形式，每个数组内容格式如"3.10,2.52"赔率形式
     */
    private static String[] getOdds(String codes) {
        try {
            String[] splite = codes.split("\\|");
            String[] result = new String[splite.length];

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < splite.length; i++) {
                String[] spliteEach = splite[i].split("@|,");
                for (int j = 1; j < spliteEach.length; j++) {
                    sb.append(spliteEach[j].split("=")[1]);

                    if (j != spliteEach.length) {
                        sb.append(",");
                    }
                }

                result[i] = sb.toString();
                sb.delete(0, sb.length());
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
            return null;
        }
    }

    /**
     * 根据返回的格式解析成对象
     * 
     * @param codes
     * @return
     */
    public static JCZQOrderDetail analyseBetCode(String json) {
        try {
            String response = RequestResultAnalyse.getData(json, "response_data");

            String betWay = RequestResultAnalyse.getData(response, "term");
            int splitNum = RequestResultAnalyse.getInt(response, "split_num");
            String orderId = RequestResultAnalyse.getData(response, "buy_order_id");
            String betCodes = RequestResultAnalyse.getData(response, "codes");
//            String oddsJson = RequestResultAnalyse.getData(response, "cm_sp");

            JCZQOrderDetail detail = new JCZQOrderDetail();
            detail.setOrderId(orderId);
            detail.setBetway(betWay);
            detail.setSplitNum(splitNum);
            detail.setBetway(betWay);

            // 解析号码格式将投注信息放入对象
            SportOrderItem orderItem = analyseJCZQOrder(betCodes);

            detail.setBunch(orderItem.getBunch());
            detail.setTimes(orderItem.getTimes());

            // 获取赔率
//            String[] odds = getOdds(oddsJson);

            ArrayList<JCZQOrderEachTeam> list = new ArrayList<JCZQOrderEachTeam>();
            String data = RequestResultAnalyse.getData(response, "data");
            JSONArray hallArray = new JSONArray(data);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JCZQOrderEachTeam eachTeam = new JCZQOrderEachTeam();
                JSONObject jo = hallArray.getJSONObject(i);
                eachTeam.setId(jo.getString("game_id"));
                eachTeam.setBetway(betWay);
                eachTeam.setHandicap(jo.getInt("handicap"));
                eachTeam.setMatchHalfPoint(jo.getString("half_score"));
                eachTeam.setMatchPoint(jo.getString("final_score"));
                eachTeam.setMaster(jo.getString("master"));
                eachTeam.setGuest(jo.getString("guest"));
                eachTeam.setBetResult(jo.getString("result"));
//                eachTeam.setOdds(odds[i]);
                SportEachTeam team = orderItem.getTeamInfList().get(i);
                eachTeam.setBetInf(team.getBetInf());
                eachTeam.setDay(team.getDay());
                eachTeam.setIndex(team.getIndex());

                list.add(eachTeam);
            }
            detail.setTeamInfList(list);
        	
            return detail;
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
            return null;
        }
    }

    private String getTeamIndex(JCZQOrderEachTeam team) {
        return JCZQ_DAY[team.getDay() - 1] + " " + team.getIndex();
    }

    /**
     * 有比分显示A 1:1 B，没比分显示A vs B
     * 
     * @param team
     * @return
     */
    private String getTeamInf(JCZQOrderEachTeam team) {
        StringBuilder sb = new StringBuilder();
        sb.append(team.getMaster() + "[主]");
//        if (TextUtils.isEmpty(team.getMatchPoint())) {
            sb.append(" vs ");
//        }
//        else {
//            sb.append(" "+team.getMatchPoint() + " ");
//        }
        sb.append(team.getGuest()+"[客]");
        return sb.toString();
    }

    /**
     * 根据竞彩足球玩法id获取描述语
     * 
     * @param wayId
     * @return
     */
    public static final String getSportWayName(String wayId) {
        for (int i = 0; i < JCZQ_BETWAY_CODE.length; i++) {
            if (JCZQ_BETWAY_CODE[i].equals(wayId)) {
                return JCZQ_BETWAY_NAME[i];
            }
        }

        return "";
    }

    /**
     * 根据串数id获取描述语，比如201代表2串1
     * 
     * @param bunch
     * @return
     */
    public static final String getSportBunchName(String bunch) {
        for (int i = 0; i < JCZQ_BUNCH_CODE.length; i++) {
            if (JCZQ_BUNCH_CODE[i].equals(bunch)) {
                return JCZQ_BUNCH_NAME[i];
            }
        }

        return "";
    }

    /**
     * 由胜平负代码获取相关描述，比如0代表负
     * 
     * @param inf
     * @return
     */
    private String getSPFInf(String inf) {
        for (int i = 0; i < JCZQ_SPF_CODE.length; i++) {
            if (JCZQ_SPF_CODE[i].equals(inf)) {
                return JCZQ_SPF_NAME[i];
            }
        }

        return null;
    }

    private String getBetInf(JCZQOrderEachTeam team) {
        StringBuilder sb = new StringBuilder();

        try {
            String teamInf = team.getBetInf();
            String odds = team.getOdds();
            String[] teamInfSplite = teamInf.split(",");
            String[] oddsSplite = odds.split(",");

            String wayId = team.getBetway();
            if (JCZQ_BETWAY_CODE[0].equals(wayId)) {
                for (int i = 0; i < teamInfSplite.length; i++) {
                    sb.append("让分");
                    sb.append("(" + team.getHandicap() + ")");
                    sb.append(getSPFInf(teamInfSplite[i]));
                    sb.append("\n赔率" + oddsSplite[i] + "\n");
                }
            }
            else if (JCZQ_BETWAY_CODE[1].equals(wayId)) {
                for (int i = 0; i < teamInfSplite.length; i++) {
                    sb.append("总进球");
                    sb.append(teamInfSplite[i]);
                    sb.append("\n赔率" + oddsSplite[i] + "\n");
                }
            }
            else if (JCZQ_BETWAY_CODE[2].equals(wayId)) {
                for (int i = 0; i < teamInfSplite.length; i++) {
                    sb.append("全场比分");
                    sb.append(teamInfSplite[i]);
                    sb.append("\n赔率" + oddsSplite[i] + "\n");
                }
            }
            else if (JCZQ_BETWAY_CODE[3].equals(wayId)) {
                for (int i = 0; i < teamInfSplite.length; i++) {
                    sb.append("半场比分");
                    sb.append("(" + team.getHandicap() + ")");
                    sb.append(getSPFInf(teamInfSplite[i]));
                    sb.append("\n赔率" + oddsSplite[i] + "\n");
                }
            }
            else if (JCZQ_BETWAY_CODE[4].equals(wayId)) {
                for (int i = 0; i < teamInfSplite.length; i++) {
                    sb.append(getSPFInf(teamInfSplite[i]));
                    sb.append("\n赔率" + oddsSplite[i] + "\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
        }

        return sb.toString();
    }

    public void showLotteryOrderViews(Context context, LinearLayout orderLayout, JCZQOrderDetail orderDetail) {
        if (orderDetail != null) {
            ArrayList<JCZQOrderEachTeam> list = orderDetail.getTeamInfList();
            int length = list.size();
            for (int i = 0; i < length; i++) {
                View item = View.inflate(context, R.layout.jingcai_bet_history_detail, null);
                TextView betTerm = (TextView) item.findViewById(R.id.bet_term);
                TextView betTeam = (TextView) item.findViewById(R.id.bet_foot_ball_team_name);
                TextView gameScore = (TextView) item.findViewById(R.id.bet_match_score);
                TextView betWinResult = (TextView) item.findViewById(R.id.bet_win_status);
                TextView betEqualResult = (TextView) item.findViewById(R.id.bet_equal_status);
                TextView betLostResult = (TextView) item.findViewById(R.id.bet_lost_status);
                TextView concedePoints = (TextView) item.findViewById(R.id.concede_points);
                TextView betGoal = (TextView) item.findViewById(R.id.jing_cai_goal_score);
                LinearLayout shengpingfuLayout =
                    (LinearLayout) item.findViewById(R.id.sheng_ping_fu_container);
                LinearLayout goalScoreLayout = (LinearLayout) item.findViewById(R.id.jin_qiu_container);
                
                JCZQOrderEachTeam team = list.get(i);
                betTerm.setText(getTeamIndex(team));
                betTeam.setText(getTeamInf(team));
                concedePoints.setText("让球："+team.getHandicap());
                String[] eachScore = team.getBetInf().split(",");
                for(int s = 0 ; s < eachScore.length ; s++){
                	if(eachScore[s].equals("3")){
                		betWinResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
                		betWinResult.setTextColor(Color.WHITE);
                		betEqualResult.setBackgroundResource(R.drawable.jingcai_history_button_bg_normal);
                		betLostResult.setBackgroundResource(R.drawable.jingcai_history_button_bg_normal);
                		if(team.getBetResult().equals("3")){
                			betWinResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                    		betWinResult.setTextColor(context.getResources().getColor(R.color.golden));
                		}
                	}else if(eachScore[s].equals("1")){
                		betEqualResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
                		betEqualResult.setTextColor(Color.WHITE);
                		betLostResult.setBackgroundResource(R.drawable.jingcai_history_button_bg_normal);
                		if(team.getBetResult().equals("1")){
                			betEqualResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                			betEqualResult.setTextColor(context.getResources().getColor(R.color.golden));
                		}
                	}else{
                		betLostResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
                		betLostResult.setTextColor(Color.WHITE);
                		if(team.getBetResult().equals("0")){
                			betLostResult.setBackgroundResource(R.drawable.jingcai_history_btn_red_award);
                			betLostResult.setTextColor(context.getResources().getColor(R.color.golden));
                		}
                	}
                }
                
                gameScore.setText("比分：" + team.getMatchPoint());
                
                orderLayout.addView(item);
            }

        }
    }
}
