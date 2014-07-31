package com.haozan.caipiao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.JingCaiOrderDetailItem;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class JingCaiOrderDetailTask
    extends AsyncTask<String, Integer, String> {

    private static final String[] STATUSNAME = {"负", "平", null, "胜"};
    private static final String[] BUNCHNAME = {"一", "二", "三", "四", "五", "六", "日"};
    private static final int[] SFCCODE = {1, 2, 3, 4, 5, 6, 7};
    private Map<String, String> jingCaiBanQuan;
    private String[] jingCaiBanNum = {"33", "31", "30", "13", "11", "10", "03", "01", "00"};
    private String[] jingCaiBanText = {"胜胜", "胜平", "胜负", "平胜", "平平", "平负", "负胜", "负平", "负负"};

    private Context context;
    private ArrayList<JingCaiOrderDetailItem> matchDataList;
    private String kind;
    private String orderId;
    private String lotteryWayType;
    private OnJingCaiHisDetailTaskChangeListener clickListener;
    private int page_no;

    public JingCaiOrderDetailTask(Context context, ArrayList<JingCaiOrderDetailItem> matchDataList,
                                  String kind, String orderId, String lotteryWayType) {
        this.context = context;
        this.matchDataList = matchDataList;
        this.kind = kind;
        this.orderId = orderId;
        this.lotteryWayType = lotteryWayType;
        initMap();
    }

    private void initMap() {
        jingCaiBanQuan = new HashMap<String, String>();
        for (int i = 0; i < 9; i++)
            jingCaiBanQuan.put(jingCaiBanNum[i], jingCaiBanText[i]);
    }

    private HashMap<String, String> initHashMap(String page)
        throws Exception {
        page_no = Integer.valueOf(page);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009050");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("lottery_id", kind);
        parameter.put("order_id", orderId);
        parameter.put("page_no", page);
        parameter.put("size", String.valueOf(50));
        return parameter;
    }

    @Override
    protected String doInBackground(String... params) {
        ConnectService connect = new ConnectService(context);
        String json = null;
        try {
            json = connect.getJson(5, false, initHashMap(params[0]));
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
            String[] betCodeArray = {"2", "2", "2"};
            String[] betNumBase = betCode.split("\\|")[i].split("\\=")[1].split("\\:")[0].split(",");
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
        else if (infoType == 1) {
            return betCode.split("\\:")[2];
        }
        else if (infoType == 2) {
            String betDay = betCode.split("\\|")[i].split("\\=")[0].split("\\$")[0];
            String betNo = betCode.split("\\|")[i].split("\\=")[0].split("\\$")[1];
            return "周" + getBetType(betDay) + " " + betNo;
        }
        else if (infoType == 3) {
            String[] betNumBase = betCode.split("\\|")[i].split("\\=")[1].split("\\:")[0].split(",");
// length = betCode.split("\\|").length;
            StringBuilder betCodeArrayStringBuilder = new StringBuilder();
            for (int n = 0; n < betNumBase.length; n++) {
                if (lotteryWayType.equals("72"))
                    betCodeArrayStringBuilder.append(betNumBase[n]);
                else if (lotteryWayType.equals("82")||lotteryWayType.equals("81")) {
                    if (betNumBase[n].equals("2"))
                        betCodeArrayStringBuilder.append("主胜");
                    else if (betNumBase[n].equals("1"))
                        betCodeArrayStringBuilder.append("主负");
                }else if(lotteryWayType.equals("84")){
                    if (betNumBase[n].equals("2"))
                        betCodeArrayStringBuilder.append("小");
                    else if (betNumBase[n].equals("1"))
                        betCodeArrayStringBuilder.append("大");
                }
                else
                    betCodeArrayStringBuilder.append(jingCaiBanQuan.get(betNumBase[n]));
                betCodeArrayStringBuilder.append(",");
            }
            betCodeArrayStringBuilder.delete(betCodeArrayStringBuilder.length() - 1,
                                             betCodeArrayStringBuilder.length());
            return betCodeArrayStringBuilder.toString();
        }
        return null;
    }

    private String getBetTermOrBetRateOrBetcode(String term, int i, int infType) {
        StringBuilder termStringBuilder = new StringBuilder();
        String[] termArray = term.split("\\|");
        String[] betDayArray = termArray[i].split("\\$");
        String[] betRateArray = betDayArray[1].split("\\@");
        if (infType == 0) {
            termStringBuilder.append("周" + getBetType(betDayArray[0]) + " " + betRateArray[0]);
            return termStringBuilder.toString();
        }
        else if (infType == 1) {
            String[] betNumArray = betRateArray[1].split(",");
            String[] betCode = {"2", "2", "2"};
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
        else if (infType == 2) {
            String[] betNumArray = betRateArray[1].split(",");
            String[] betRate = {"null", "null", "null"};
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
        else if (infType == 3) {
            String[] betHisCodeArray = betRateArray[1].split("\\,");
            StringBuilder betCodeArrayStringBuilder = new StringBuilder();
            for (int n = 0; n < betHisCodeArray.length; n++) {
                if (lotteryWayType.equals("72"))
                    betCodeArrayStringBuilder.append(betHisCodeArray[n].split("\\=")[0]);
                else if (lotteryWayType.equals("82")||lotteryWayType.equals("81")) {
                    if (betHisCodeArray[n].split("\\=")[0].equals("2"))
                        betCodeArrayStringBuilder.append("主胜");
                    else if (betHisCodeArray[n].split("\\=")[0].equals("1"))
                        betCodeArrayStringBuilder.append("主负");
                } else if(lotteryWayType.equals("84")){
                    if (betHisCodeArray[n].split("\\=")[0].equals("2"))
                        betCodeArrayStringBuilder.append("小");
                    else if (betHisCodeArray[n].split("\\=")[0].equals("1"))
                        betCodeArrayStringBuilder.append("大");
                }
                else 
                    betCodeArrayStringBuilder.append(jingCaiBanQuan.get(betHisCodeArray[n].split("\\=")[0]));
                betCodeArrayStringBuilder.append(",");
            }
            betCodeArrayStringBuilder.delete(betCodeArrayStringBuilder.length() - 1,
                                             betCodeArrayStringBuilder.length());
            return betCodeArrayStringBuilder.toString();
        }
        else if (infType == 4) {
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
                    JSONArray footBallTeamArray = new JSONArray(ja.getData(json, "response_data"));
                    for (int i = 0; i < footBallTeamArray.length(); i++) {
                        JingCaiOrderDetailItem jingCaiBetHistoryDetailItemData = new JingCaiOrderDetailItem();
                        JSONObject footBallTeamObject = new JSONObject(footBallTeamArray.getString(i));
                        try {
                            String cmSp = footBallTeamObject.getString("cm_sp");
                            String codes = footBallTeamObject.getString("codes");
                            StringBuilder codeSb = new StringBuilder();
                            for (int j = 0; j < codes.split("\\|").length; j++) {
                                if (j > 0)
                                    codeSb.append("*");
                                codeSb.append(analyseBetCodeOrBetType(codes, j, 2) + ":");
                                codeSb.append("<font color=\"#6CA6CD\">");
                                String betCode01 = "2";
                                String betCode02 = "2";
                                String betCode03 = "2";

                                String betCodeRate01 = "null";
                                String betCodeRate02 = "null";
                                String betCodeRate03 = "null";
                                if (!cmSp.equals("")) {

                                    if (lotteryWayType.equals("71")) {
                                        betCode01 = getBetTermOrBetRateOrBetcode(cmSp, j, 1).split(",")[0];
                                        betCode02 = getBetTermOrBetRateOrBetcode(cmSp, j, 1).split(",")[1];
                                        betCode03 = getBetTermOrBetRateOrBetcode(cmSp, j, 1).split(",")[2];

                                        betCodeRate01 =
                                            getBetTermOrBetRateOrBetcode(cmSp, j, 2).split(",")[0];
                                        betCodeRate02 =
                                            getBetTermOrBetRateOrBetcode(cmSp, j, 2).split(",")[1];
                                        betCodeRate03 =
                                            getBetTermOrBetRateOrBetcode(cmSp, j, 2).split(",")[2];
                                    }
                                    else {
                                        betCode01 = getBetTermOrBetRateOrBetcode(cmSp, j, 3).split(",")[0];
                                        betCodeRate01 =
                                            getBetTermOrBetRateOrBetcode(cmSp, j, 4).split(",")[0];
                                    }

                                }
                                else {
                                    if (lotteryWayType.equals("71")) {
                                        betCode01 = analyseBetCodeOrBetType(codes, j, 0).split(",")[0];
                                        betCode02 = analyseBetCodeOrBetType(codes, j, 0).split(",")[1];
                                        betCode03 = analyseBetCodeOrBetType(codes, j, 0).split(",")[2];
                                    }
                                    else {
                                        betCode01 = analyseBetCodeOrBetType(codes, j, 3);
                                    }
                                }
                                codeSb.append("[");
                                if (lotteryWayType.equals("71")) {
                                    if (!betCode01.equals("2")) {
                                        codeSb.append(STATUSNAME[Integer.valueOf(betCode01)]);
                                    }
                                    if (!betCodeRate01.equals("null")) {
                                        codeSb.append("(" + getRateString(betCodeRate01) + ")");
                                    }

                                    if (!betCode02.equals("2")) {
                                        if (!betCode01.equals("2"))
                                            codeSb.append(",");
                                        codeSb.append(STATUSNAME[Integer.valueOf(betCode02)]);
                                    }
                                    if (!betCodeRate02.equals("null")) {
                                        codeSb.append("(" + getRateString(betCodeRate02) + ")");
                                    }

                                    if (!betCode03.equals("2")) {
                                        if (!betCode01.equals("2") && !betCode02.equals("2"))
                                            codeSb.append(",");
                                        else if (betCode01.equals("2") && !betCode02.equals("2"))
                                            codeSb.append(",");
                                        else if (!betCode01.equals("2") && betCode02.equals("2"))
                                            codeSb.append(",");
                                        codeSb.append(STATUSNAME[Integer.valueOf(betCode03)]);
                                    }
                                    if (!betCodeRate03.equals("null")) {
                                        codeSb.append("(" + getRateString(betCodeRate03) + ")");
                                    }
                                }
                                else {
                                    codeSb.append(betCode01);
                                    if(!betCodeRate01.equals("null"))
                                    {
                                        codeSb.append("("+betCodeRate01+")");
                                    }
                                }
                                codeSb.append("]");
                                codeSb.append("</font>");
                            }
                            jingCaiBetHistoryDetailItemData.setJingCaiBetItem(codeSb.toString());
                            jingCaiBetHistoryDetailItemData.setJincaiBetType(footBallTeamObject.getString("sub_play").split("\\;")[1]);
                            jingCaiBetHistoryDetailItemData.setJincaiAward(footBallTeamObject.getInt("win"));
                            jingCaiBetHistoryDetailItemData.setJingcaiAwardMoney(footBallTeamObject.getString("prize"));
                            jingCaiBetHistoryDetailItemData.setJincaiBuyMode(footBallTeamObject.getInt("buy"));
                            jingCaiBetHistoryDetailItemData.setZhuShu((footBallTeamObject.getInt("money") / footBallTeamObject.getInt("times")) / 2);
                            jingCaiBetHistoryDetailItemData.setBetTimes(footBallTeamObject.getInt("times"));
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        matchDataList.add(jingCaiBetHistoryDetailItemData);
                    }
                    if (footBallTeamArray.length() < 50)
                        clickListener.onJingCaiHisActionClick("no_data");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                if (clickListener != null)
                    clickListener.onJingCaiHisActionClick("200");
            }
            else {
                if (page_no == 1)
                    clickListener.onJingCaiHisActionClick("2021");
// else
// clickListener.onJingCaiHisActionClick("2022");
                clickListener.onJingCaiHisActionClick(null);
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
