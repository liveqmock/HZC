package com.haozan.caipiao.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.activity.BetFootBallTeamListShow;
import com.haozan.caipiao.activity.bet.BetPayDigital;
import com.haozan.caipiao.activity.bet.shisichang.ShisichangActivity;
import com.haozan.caipiao.adapter.AthleticsListAdapter;
import com.haozan.caipiao.adapter.FootBallBetSelectionShowAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class SportLotteryInfTask
    extends AsyncTask<Void, Object, String> {

    private Context context;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String kind;
    private String term;
    private AthleticsListAdapter athleticsListAdapter;
    private FootBallBetSelectionShowAdapter footBallBetSelectionShowAdapter;
    private OnTaskChangeListener clickListener;

    public SportLotteryInfTask(Context context, ArrayList<AthleticsListItemData> matchDataList, String kind,
                               String term) {
        this.context = context;
        this.matchDataList = matchDataList;
        this.kind = kind;
        this.term = term;
    }

    public SportLotteryInfTask(Context context, ArrayList<AthleticsListItemData> matchDataList,
                               AthleticsListAdapter athleticsListAdapter, String kind, String term) {
        this.context = context;
        this.matchDataList = matchDataList;
        this.athleticsListAdapter = athleticsListAdapter;
        this.kind = kind;
        this.term = term;
    }

    public SportLotteryInfTask(Context context, ArrayList<AthleticsListItemData> matchDataList,
                               FootBallBetSelectionShowAdapter footBallBetSelectionShowAdapter, String kind,
                               String term) {
        this.context = context;
        this.matchDataList = matchDataList;
        this.footBallBetSelectionShowAdapter = footBallBetSelectionShowAdapter;
        this.kind = kind;
        this.term = term;
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2009010");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("lottery_id", kind);
        parameter.put("term", term);
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

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        if (json != null) {
            JsonAnalyse ja = new JsonAnalyse();
            String status = ja.getStatus(json);
            if (status.equals("200")) {

                JSONArray footBallTeamArray = ja.getJsonArray(ja.getData(json, "response_data"));
                for (int i = 0; i < footBallTeamArray.length(); i++) {
                    AthleticsListItemData athleticsListItemData = new AthleticsListItemData();
                    try {
                        athleticsListItemData.setIdBetNum(ja.getData(footBallTeamArray.getString(i), "i"));
                        athleticsListItemData.setClickStatus(false);
                        athleticsListItemData.setMatchType(ja.getData(footBallTeamArray.getString(i), "u"));
                        athleticsListItemData.setMatchDate(ja.getData(footBallTeamArray.getString(i), "t"));
                        athleticsListItemData.setMatchHomeTeamName(ja.getData(footBallTeamArray.getString(i),
                                                                              "m"));
                        athleticsListItemData.setMatchGuessTeamName(ja.getData(footBallTeamArray.getString(i),
                                                                               "g"));
                        athleticsListItemData.setGameOdds(ja.getData(footBallTeamArray.getString(i), "o"));
                        if(kind.equals("sfc") || kind.equals("r9")){
                            athleticsListItemData.setMatchId(ja.getData(footBallTeamArray.getString(i), "a"));
                        }
                        athleticsListItemData.setWinButtonStatus(0);
                        athleticsListItemData.setEqualButtonStatus(0);
                        athleticsListItemData.setLostButtonStatus(0);
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    matchDataList.add(athleticsListItemData);
                }
                if (context.getClass() == ShisichangActivity.class)
                    athleticsListAdapter.notifyDataSetChanged();
                else if (context.getClass() == BetFootBallTeamListShow.class)
                    footBallBetSelectionShowAdapter.notifyDataSetChanged();
                if (clickListener != null)
                    clickListener.onSFCR9ActionClick("200");
            }
            else {
                if (context.getClass() == BetPayDigital.class)
                    clickListener.onSFCR9ActionClick(null);

                String inf = "数据获取失败";
                ViewUtil.showTipsToast(context, inf);
            }
        }
        else {
            clickListener.onSFCR9ActionClick(null);
        }
    }

    public void setTaskChangeListener(OnTaskChangeListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnTaskChangeListener {
        public void onSFCR9ActionClick(String status);
    }
}