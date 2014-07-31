package com.haozan.caipiao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.activity.BetFootBallTeamListShow;
import com.haozan.caipiao.activity.bet.shisichang.ShisichangActivity;
import com.haozan.caipiao.adapter.AthleticsListAdapter;
import com.haozan.caipiao.adapter.FootBallBetSelectionShowAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.JingCaiBetHistoryDetailItemData;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class JingcaiBetHistoryDetailTask
    extends AsyncTask<Void, Object, String> {

    private static final String[] BUNCHNAME = {"一", "二", "三", "四", "五", "六", "日"};
    private static final int[] SFCCODE = {1, 2, 3, 4, 5, 6, 7};

    private Context context;
    private ArrayList<JingCaiBetHistoryDetailItemData> matchDataList;
    private String kind;
    private String orderId;
    private AthleticsListAdapter athleticsListAdapter;
    private FootBallBetSelectionShowAdapter footBallBetSelectionShowAdapter;
    private OnJingCaiHisDetailTaskChangeListener clickListener;
    private Map<String, String> mapBetHistoryDetail;

    public JingcaiBetHistoryDetailTask(Context context,
                                       ArrayList<JingCaiBetHistoryDetailItemData> matchDataList, String kind,
                                       String orderId, Map<String, String> mapBetHistoryDetail) {
        this.context = context;
        this.matchDataList = matchDataList;
        this.kind = kind;
        this.orderId = orderId;
        this.mapBetHistoryDetail = mapBetHistoryDetail;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009040");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("lottery_id", kind);
        parameter.put("order_id", orderId);
        return parameter;
    }

    @Override
    protected String doInBackground(Void... params) {
        ConnectService connect = new ConnectService(context);
        String json = null;
        try {
            json = connect.getJsonGet(5, false, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private String getBetType(String guoGuan) {
        int j = 0;
        for (int i = 0; i < SFCCODE.length; i++)
            if (SFCCODE[i] == Integer.valueOf(guoGuan))
                j = i;
        return BUNCHNAME[j];
    }

    private String analyseBetCodeOrBetType(String betCode, int i, int infoType) {
        if (infoType == 0) {
            int length = 3;
            String[] betNumBase = betCode.split("\\|")[i].split("\\=")[1].split("\\:")[0].split(",");
            String term = mapBetHistoryDetail.get("term");
            if (term.equals("71") || term.equals("75")) {//竞彩足球 75：胜平负 71：让球胜平负 
                length = 3;
            }
            else {
                length = betCode.split("\\|").length;
            }
            String[] betCodeArray = new String[length];
            if (term.equals("71") || term.equals("75")) {
                for (int j = 0; j < betNumBase.length; j++) {
                    if (betNumBase[j].equals("0"))
                        betCodeArray[2] = "0";
                    else if (betNumBase[j].equals("1"))
                        betCodeArray[1] = "1";
                    else if (betNumBase[j].equals("3"))
                        betCodeArray[0] = "3";
                }
                return betCodeArray[0] + "," + betCodeArray[1] + "," + betCodeArray[2];
            }
            if (!term.equals("71") && !term.equals("75")) {//竞彩足球 75：胜平负 71：让球胜平负 
                StringBuilder betCodeArrayStringBuilder = new StringBuilder();
                for (int n = 0; n < betNumBase.length; n++) {
                    betCodeArrayStringBuilder.append(betNumBase[n]);
                    betCodeArrayStringBuilder.append(",");
                }
                betCodeArrayStringBuilder.delete(betCodeArrayStringBuilder.length() - 1,  betCodeArrayStringBuilder.length());
                return betCodeArrayStringBuilder.toString();
            }
        }
        else if (infoType == 1) {
            return betCode.split("\\:")[2];
        }
        else if (infoType == 2) {
            String betDay = betCode.split("\\|")[i].split("\\=")[0].split("\\$")[0];
            String betNo = betCode.split("\\|")[i].split("\\=")[0].split("\\$")[1];
            return "周" + getBetType(betDay) + " " + betNo;
        }
        return null;
    }

    private String getBetTermOrBetRateOrBetcode(String term, int i, int infType) {
        StringBuilder termStringBuilder = new StringBuilder();
        String[] termArray = term.split("\\|");
        String[] betDayArray = termArray[i].split("\\$");
        String[] betRateArray = betDayArray[1].split("\\@");
        String jcTerm = mapBetHistoryDetail.get("term");
        if (infType == 0) {
            termStringBuilder.append("周" + getBetType(betDayArray[0]) + " " + betRateArray[0]);
            return termStringBuilder.toString();
        }
        else if (infType == 1) {
            int length = 3;
            String[] betNumArray = betRateArray[1].split(",");

            if (jcTerm.equals("71") || jcTerm.equals("75")) { //竞彩足球 71：胜平负 75：让球胜平负 
                length = 3;
            }
            else {
                length = betNumArray.length;
            }

            String[] betCode = new String[length];
            if (jcTerm.equals("71") || jcTerm.equals("75")) { //竞彩足球 71：胜平负 75：让球胜平负 
                for (int b = 0; b < betNumArray.length; b++) {
                    if (betNumArray[b].split("\\=")[0].equals("0"))
                        betCode[2] = "0";
                    else if (betNumArray[b].split("\\=")[0].equals("1"))
                        betCode[1] = "1";
                    else if (betNumArray[b].split("\\=")[0].equals("3"))
                        betCode[0] = "3";
                }
                return betCode[0] + "," + betCode[1] + "," + betCode[2];
            }
            if (!jcTerm.equals("71") && !jcTerm.equals("75")) { //竞彩足球 71：胜平负 75：让球胜平负 
                String[] betHisCodeArray = betRateArray[1].split("\\,");
                StringBuilder betCodeArrayStringBuilder = new StringBuilder();
                for (int n = 0; n < betHisCodeArray.length; n++) {
                    betCodeArrayStringBuilder.append(betHisCodeArray[n].split("\\=")[0]);
                    betCodeArrayStringBuilder.append(",");
                }
                betCodeArrayStringBuilder.delete(betCodeArrayStringBuilder.length() - 1,
                                                 betCodeArrayStringBuilder.length());
                return betCodeArrayStringBuilder.toString();
            }
        }
        else if (infType == 2) {
            int length = 3;
            String[] betNumArray = betRateArray[1].split(",");
            if (jcTerm.equals("71")||jcTerm.equals("75")) {//竞彩足球 71：胜平负 75：让球胜平负 
                length = 3;
            }
            else {
                length = betNumArray.length;
            }
            String[] betRate = new String[length];
            if (jcTerm.equals("71") || jcTerm.equals("75")) {//竞彩足球 71：胜平负 75：让球胜平负 
                for (int b = 0; b < betNumArray.length; b++) {
                    if (betNumArray[b].split("\\=")[0].equals("0"))
                        betRate[2] = betNumArray[b].split("\\=")[1];
                    else if (betNumArray[b].split("\\=")[0].equals("1"))
                        betRate[1] = betNumArray[b].split("\\=")[1];
                    else if (betNumArray[b].split("\\=")[0].equals("3"))
                        betRate[0] = betNumArray[b].split("\\=")[1];
                }
                return betRate[0] + "," + betRate[1] + "," + betRate[2];
            }
            if (!jcTerm.equals("71") && !jcTerm.equals("75")) {//竞彩足球 71：胜平负 75：让球胜平负 
                String[] betHisCodeArray = betRateArray[1].split("\\,");
                StringBuilder betCodeArrayStringBuilder = new StringBuilder();
                for (int n = 0; n < betHisCodeArray.length; n++) {
                    betCodeArrayStringBuilder.append(betHisCodeArray[n].split("\\=")[1]);
                    betCodeArrayStringBuilder.append(",");
                }
                betCodeArrayStringBuilder.delete(betCodeArrayStringBuilder.length() - 1,
                                                 betCodeArrayStringBuilder.length());
                return betCodeArrayStringBuilder.toString();
            }
        }
        return null;

    }

    private String[] getLetScoreAndSetScore(String resultGG, String cmBB, int type) {
        String[] cmBBArray = cmBB.split("\\|");
        String[] betResult = new String[cmBBArray.length];
        String[] letScore = new String[cmBBArray.length];
        String[] betScore = new String[cmBBArray.length];
        String[] resultGGArray = resultGG.split("\\,");
// String[] resultGGArray = {"大186"};
        Map<String, String> resultGGMap = new HashMap<String, String>();
        if (!resultGG.equals("")) {
            for (int i = 0; i < resultGGArray.length; i++) {
                String num = resultGGArray[i].replaceAll("[\u4E00-\u9FA5]", "");
                String text = resultGGArray[i].replaceAll("[^\u4E00-\u9FA5]", "");
                resultGGMap.put(num, text);
            }
        }
        if (type == 1) {
            for (int j = 0; j < cmBBArray.length; j++) {
                betResult[j] = resultGGMap.get(cmBBArray[j].split("\\=")[1]);
            }
            return betResult;
        }
        else if (type == 2) {
            for (int j = 0; j < cmBBArray.length; j++) {
                letScore[j] = cmBBArray[j].split("\\=")[1];
            }
            return letScore;
        }
        else if (type == 3) {
            for (int j = 0; j < cmBBArray.length; j++) {
                betScore[j] = cmBBArray[j].split("\\=")[1];
            }
            return betScore;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            String status = ja.getStatus(json);
            if (status.equals("200")) {
                try {
                    JSONObject footBallTeamArray = new JSONObject(ja.getData(json, "response_data"));
                    mapBetHistoryDetail.put("d", footBallTeamArray.getString("d"));
                    mapBetHistoryDetail.put("loc", footBallTeamArray.getString("loc"));
                    mapBetHistoryDetail.put("mode", footBallTeamArray.getString("mode"));
                    mapBetHistoryDetail.put("order_id", footBallTeamArray.getString("order_id"));
                    mapBetHistoryDetail.put("teams", footBallTeamArray.getString("teams"));
                    mapBetHistoryDetail.put("term", footBallTeamArray.getString("term"));
                    mapBetHistoryDetail.put("xy", footBallTeamArray.getString("xy"));
                    mapBetHistoryDetail.put("bet_type",
                                            analyseBetCodeOrBetType(footBallTeamArray.getString("codes"), -1,
                                                                    1));
                    mapBetHistoryDetail.put("split_num", footBallTeamArray.getString("split_num"));
                    mapBetHistoryDetail.put("codes", footBallTeamArray.getString("codes"));
                    mapBetHistoryDetail.put("prize", footBallTeamArray.getString("prize"));
                    mapBetHistoryDetail.put("win", footBallTeamArray.getString("win"));
                    mapBetHistoryDetail.put("bet_money", footBallTeamArray.getString("money"));
                    mapBetHistoryDetail.put("buy_mode", footBallTeamArray.getString("buy"));

                    JSONArray footteamNameArray = new JSONArray(footBallTeamArray.getString("data"));
                    if(footteamNameArray.length()==0) {
                    	clickListener.onJingCaiHisActionClick("nodata");
                    	return;
                    }
                    for (int i = 0; i < footteamNameArray.length(); i++) {
                        JingCaiBetHistoryDetailItemData jingCaiBetHistoryDetailItemData =
                            new JingCaiBetHistoryDetailItemData();
                        JSONObject footBallTeamObject = new JSONObject(footteamNameArray.getString(i));
                        try {
                            jingCaiBetHistoryDetailItemData.setMaster(footBallTeamObject.getString("master"));
                            jingCaiBetHistoryDetailItemData.setGuest(footBallTeamObject.getString("guest"));
                            if (footBallTeamObject.has("handicap"))
                                jingCaiBetHistoryDetailItemData.setConcedePoints(Integer.valueOf(footBallTeamObject.getString("handicap")));
                            else {
                                String cmBB = footBallTeamArray.getString("cm_bb");
                                String resultGG = "";
                                if (footBallTeamObject.has("result_gg")) {
                                    resultGG = footBallTeamObject.getString("result_gg");
                                    if (!cmBB.equals("") && !resultGG.equals("")) {
                                        jingCaiBetHistoryDetailItemData.setResultGG(getLetScoreAndSetScore(resultGG,
                                                                                                           cmBB,
                                                                                                           1));
                                    }
                                    if (!cmBB.equals("")) {
                                        jingCaiBetHistoryDetailItemData.setLetScore(getLetScoreAndSetScore(resultGG,
                                                                                                           cmBB,
                                                                                                           2));
                                        jingCaiBetHistoryDetailItemData.setScore(getLetScoreAndSetScore(resultGG,
                                                                                                        cmBB,
                                                                                                        3));
                                    }
                                }
                            }

                            String cmSp = footBallTeamArray.getString("cm_sp");
                            String codes = footBallTeamArray.getString("codes");
                            if (mapBetHistoryDetail.get("term").equals("71") ||
                                mapBetHistoryDetail.get("term").equals("75")) {//竞彩足球 71：胜平负 75：让球胜平负 
                                String betCode01 = null;
                                String betCode02 = null;
                                String betCode03 = null;

                                String betCodeRate01 = "";
                                String betCodeRate02 = "";
                                String betCodeRate03 = "";

                                if (!cmSp.equals("")) {
                                    betCode01 = getBetTermOrBetRateOrBetcode(cmSp, i, 1).split(",")[0];
                                    betCode02 = getBetTermOrBetRateOrBetcode(cmSp, i, 1).split(",")[1];
                                    betCode03 = getBetTermOrBetRateOrBetcode(cmSp, i, 1).split(",")[2];

                                    betCodeRate01 = getBetTermOrBetRateOrBetcode(cmSp, i, 2).split(",")[0];
                                    betCodeRate02 = getBetTermOrBetRateOrBetcode(cmSp, i, 2).split(",")[1];
                                    betCodeRate03 = getBetTermOrBetRateOrBetcode(cmSp, i, 2).split(",")[2];
                                }
                                else {
                                    betCode01 = analyseBetCodeOrBetType(codes, i, 0).split(",")[0];
                                    betCode02 = analyseBetCodeOrBetType(codes, i, 0).split(",")[1];
                                    betCode03 = analyseBetCodeOrBetType(codes, i, 0).split(",")[2];
                                }

                                if (!betCode01.equals("null"))
                                    jingCaiBetHistoryDetailItemData.setBetResultWin("胜 " +
                                        getRateString(betCodeRate01) + "");
                                else
                                    jingCaiBetHistoryDetailItemData.setBetResultWin(null);

                                if (!betCode02.equals("null"))
                                    jingCaiBetHistoryDetailItemData.setBetResultEqual("平 " +
                                        getRateString(betCodeRate02) + "");
                                else
                                    jingCaiBetHistoryDetailItemData.setBetResultEqual(null);

                                if (!betCode03.equals("null"))
                                    jingCaiBetHistoryDetailItemData.setBetResultLost("负 " +
                                        getRateString(betCodeRate03) + "");
                                else
                                    jingCaiBetHistoryDetailItemData.setBetResultLost(null);
                            }
                            else {
                                if (!cmSp.equals("")) {
                                    jingCaiBetHistoryDetailItemData.setBetGoal(getBetTermOrBetRateOrBetcode(cmSp,
                                                                                                            i,
                                                                                                            1) +
                                        "|" + getBetTermOrBetRateOrBetcode(cmSp, i, 2));
                                }
                                else {
                                    jingCaiBetHistoryDetailItemData.setBetGoal(analyseBetCodeOrBetType(codes,
                                                                                                       i, 0));
                                }
                            }

                            jingCaiBetHistoryDetailItemData.setGameResult(footBallTeamObject.getString("result"));

                            if (!cmSp.equals(""))
                                jingCaiBetHistoryDetailItemData.setBetTerm(getBetTermOrBetRateOrBetcode(cmSp,
                                                                                                        i, 0));
                            else
                                jingCaiBetHistoryDetailItemData.setBetTerm(analyseBetCodeOrBetType(codes, i,
                                                                                                   2));
                            if (footBallTeamObject.has("half_score"))
                                jingCaiBetHistoryDetailItemData.setHalfMatchScore(footBallTeamObject.getString("half_score"));
                            jingCaiBetHistoryDetailItemData.setFullMatchScore(footBallTeamObject.getString("final_score"));
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        matchDataList.add(jingCaiBetHistoryDetailItemData);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                if (context.getClass() == ShisichangActivity.class)
                    athleticsListAdapter.notifyDataSetChanged();
                else if (context.getClass() == BetFootBallTeamListShow.class)
                    footBallBetSelectionShowAdapter.notifyDataSetChanged();
                if (clickListener != null)
                    clickListener.onJingCaiHisActionClick("200");
            }
            else {
                String inf = "数据获取失败";
                ViewUtil.showTipsToast(context, inf);
            }
        }
        else {
            clickListener.onJingCaiHisActionClick(null);
        }
    }

    private String getRateString(String rate) {
        if (!rate.equals("")) {
            if (rate.indexOf(".") == -1)
                return rate + ".0";
            else
                return rate;
        }
        else {
            return rate;
        }
    }

    public void setOnJinCaiHisDeTaskChangeListener(OnJingCaiHisDetailTaskChangeListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnJingCaiHisDetailTaskChangeListener {
        public void onJingCaiHisActionClick(String status);
    }
}
