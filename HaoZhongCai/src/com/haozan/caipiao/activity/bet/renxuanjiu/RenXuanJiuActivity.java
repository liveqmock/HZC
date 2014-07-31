package com.haozan.caipiao.activity.bet.renxuanjiu;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.activity.bet.FootBallBetBasic;
import com.haozan.caipiao.adapter.AthleticsListAdapter;
import com.haozan.caipiao.adapter.AthleticsListAdapter.OnAthleticsListItemListener;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.task.SportLotteryInfTask.OnTaskChangeListener;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.ViewUtil;

// import android.widget.TextView;

public class RenXuanJiuActivity
    extends FootBallBetBasic
    implements OnAthleticsListItemListener, OnClickListener, OnTaskChangeListener {
    private ArrayList<AthleticsListItemData> matchDataList;
    private ListView athleticsBetDataList;
    private AthleticsListAdapter athleticsListAdapter;
    private String[][] betSelectionArray;
    private boolean isWork = false;
    private String chargeMoney = null;
    private int betNum = 1;
    private int listClickedNum = 0;
    private ProgressBar progressBar;
    private Button randomButton;
// private boolean isBetable = true;
    private ArrayList<Integer> orgNumArray;
    private ArrayList<Integer> betNumArray;
    private String resource = null;
    private String betOrgNum = null;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shi_si_chang);
        setUpView();
        init();
    }

    @Override
    public void setKind() {
        super.setKind();
        this.kind = "r9";
    }

    private void setUpView() {
        athleticsBetDataList = (ListView) findViewById(R.id.shi_si_chang_list_view);
        setBasicView();
        betOderClear.setOnClickListener(this);
        makeBetOder.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_large);
        randomButton = (Button) findViewById(R.id.random_button);
        randomButton.setVisibility(View.VISIBLE);
        randomButton.setOnClickListener(this);
        randomButton.setEnabled(false);
        helpLin.setOnClickListener(this);
// betMoney = (TextView) findViewById(R.id.sport_bet_money);
// betTitle = (TextView) findViewById(R.id.sport_zucai_bet_title);
    }

    private void init() {
        sportTitle.setText("任选九");
// sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font>" + "0元"));
        betSelectionArray = new String[14][3];
        matchDataList = new ArrayList<AthleticsListItemData>();
        athleticsListAdapter = new AthleticsListAdapter(RenXuanJiuActivity.this, matchDataList);
        athleticsListAdapter.setListItemClickListener(this);
        athleticsBetDataList.setAdapter(athleticsListAdapter);
        iniBetList(RenXuanJiuActivity.this);
        orgNumArray = new ArrayList<Integer>();
        betNumArray = new ArrayList<Integer>();
        for (int i = 0; i < 14; i++)
            orgNumArray.add(i + 1);
// for (int j = 0; j < 9; j++)
// betNumArray.add(0);
        checkBetIsWork();
        countingBetMoney();
        try {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                resource = bundle.getString("resource");
                betOrgNum = bundle.getString("bet_code");
            }
            if (resource != null)
                if (resource.equals("bet_history")) {
                    mode = "1011";
                }
                else if (resource.equals("bet_weibo")) {
                    mode = "1012";
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActionClick(String selection, int grounpIndex, int childIndex,
                              ArrayList<AthleticsListItemData> athleticsDataList) {
        if (betSelectionArray[grounpIndex][childIndex] == null)
            betSelectionArray[grounpIndex][childIndex] = selection;
        else
            betSelectionArray[grounpIndex][childIndex] = null;
        checkBetStatus(grounpIndex, childIndex, athleticsDataList);
        countingBetMoney();
        code = getBetCode(betSelectionArray);
        luckyNum = code;
        counttingListSelection();
        setClearButtonStatus();
    }

    private String getBetCode(String[][] betSelectedCodeArray) {
        StringBuilder betCode = new StringBuilder();
        for (int i = 0; i < betSelectedCodeArray.length; i++) {
            boolean isNotSelected = false;
            for (int j = 0; j < 3; j++) {
                if (betSelectedCodeArray[i][j] != null) {
                    betCode.append(betSelectedCodeArray[i][j]);
                    isNotSelected = true;
                }
// else {
// if (isNotSelected == false) {
// betCode.append("*");
// isNotSelected = true;
// }
// }
            }
            if (isNotSelected == false) {
                betCode.append("X");
            }
            betCode.append(",");
        }
        betCode.delete(betCode.length() - 1, betCode.length());
        displayCode = getBallsDisplayInf(betCode.toString());
        betCode.append(":");
        betCode.append(chargeMoney);
        betCode.append(":");
        betCode.append(getBetType(betNum));
        betCode.append(":");
        return betCode.toString();
    }

    private String getBallsDisplayInf(String displayCode) {
        StringBuilder betText = new StringBuilder();
        betText.append("<font color='red'>");
        betText.append(displayCode);
        betText.append("</font>");
        return betText.toString();
    }

    private String getBetType(int betNumInner) {
        if (betNumInner > 1)
            return "102";
        else
            return "101";
    }

    @Override
    protected boolean checkInput() {
        return isWork;
    }

    private void counttingListSelection() {
        listClickedNum = 0;
        int j = 0;
        while (j < betSelectionArray.length) {
            for (int i = 0; i < 3; i++)
                if (betSelectionArray[j][i] != null)
                    listClickedNum++;
            j++;
        }
    }

    private void setClearButtonStatus() {
        if (listClickedNum == 0)
            resetBtn();
        else {
            enableClearBtn();
            if (isWork)
                enableBetBtn();
            else
                disableBetBtn();
        }
    }

    private void checkBetStatus(int grounpIndex, int childIndex,
                                ArrayList<AthleticsListItemData> athleticsDataList) {
        int num = 0;
        matchDataList = athleticsDataList;
        for (int j = 0; j < matchDataList.size(); j++)
            if (matchDataList.get(j).getClickStatus())
                num++;

        if (num < 9) {
            if (isWork) {
                if (matchDataList.get(grounpIndex).getClickStatus())
                    matchDataList.get(grounpIndex).setClickStatus(true);
                else
                    matchDataList.get(grounpIndex).setClickStatus(false);
                athleticsListAdapter.notifyDataSetChanged();
            }
            isWork = false;
        }
        else {
            isWork = true;
            athleticsListAdapter.notifyDataSetChanged();
        }
    }

    private void checkBetIsWork() {
        int num = 0;
        for (int j = 0; j < matchDataList.size(); j++)
            if (matchDataList.get(j).getClickStatus())
                num++;

        if (num < 9) {
            isWork = false;
        }
        else {
            isWork = true;
        }
    }

    private void countingBetMoney() {
        betNum = 1;
        if (isWork) {
            for (int h = 0; h < betSelectionArray.length; h++) {
                if (getBetNum(h) != 0)
                    betNum = betNum * getBetNum(h);
                sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">" + betNum + "注</font> " +
                    String.valueOf(betNum * 2) + "元"));
                chargeMoney = String.valueOf(betNum * 2);
                betMoney = Long.valueOf(chargeMoney);
            }
        }
        else {
            sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font>" + "0元"));
        }
    }

    private int getBetNum(int h) {
        int num = 0;
        for (int i = 0; i < 3; i++)
            if (betSelectionArray[h][i] != null)
                num++;
        return num;
    }

    boolean isOk = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bet_help_lin) {
            submitStatisticClickRules();
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "任选九游戏规则");
            bundel.putString("lottery_help", "help_new/f9.html");
            intent.putExtras(bundel);
            intent.setClass(RenXuanJiuActivity.this, LotteryWinningRules.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.sport_bet_clear) {
            clearBettingSelection();
        }
        else if (v.getId() == R.id.sport_bet_make_a_order) {
            bet();
        }
        else if (v.getId() == R.id.random_button) {
            submitStatisticRandomInf();
            betNumArray.clear();
            Random random = new Random();
            clearBettingSelection();
            for (int j = 0; j < 9;) {
                int betRamNum = random.nextInt(orgNumArray.size());
                if (j == 0) {
                    betNumArray.add(betRamNum);
                    j++;
                }
                else {
                    for (int k = 0; k < betNumArray.size(); k++) {
                        isOk = true;
                        if (betRamNum == betNumArray.get(k)) {
                            isOk = false;
                            break;
                        }
                    }
                    if (isOk) {
                        betNumArray.add(betRamNum);
                        j++;
                    }
                }
            }

            for (int i = 0; i < betNumArray.size(); i++) {
                matchDataList.get(betNumArray.get(i)).setClickStatus(true);
                String[] oddsArray = matchDataList.get(betNumArray.get(i)).getGameOdds().split(" ");
                String betRate = oddsArray[0];
                int index = 0;
                for (int j = 1; j < oddsArray.length; j++) {
                    if (Double.valueOf(betRate) > Double.valueOf(oddsArray[j])) {
                        betRate = oddsArray[j];
                        index = j;
                    }
                }
                setListButtonStatus(i, index);
                if (index == 0)
                    betSelectionArray[betNumArray.get(i)][0] = "3";
                else if (index == 1)
                    betSelectionArray[betNumArray.get(i)][1] = "1";
                else if (index == 2)
                    betSelectionArray[betNumArray.get(i)][2] = "0";
            }
        }
// checkListClick(matchDataList);
        checkBetIsWork();
        countingBetMoney();
        code = getBetCode(betSelectionArray);
        counttingListSelection();
        setClearButtonStatus();
        athleticsListAdapter.notifyDataSetChanged();
    }

// private void checkListClick(ArrayList<AthleticsListItemData> athleticsDataList) {
// isBetable = true;
// for (int i = 0; i < athleticsDataList.size(); i++) {
// if (!athleticsDataList.get(i).getClickStatus()) {
// isBetable = false;
// break;
// }
// }
// }

    private void setListButtonStatus(int i, int index) {
        if (index == 0)
            matchDataList.get(betNumArray.get(i)).setWinButtonStatus(1);
        else if (index == 1)
            matchDataList.get(betNumArray.get(i)).setEqualButtonStatus(1);
        else if (index == 2)
            matchDataList.get(betNumArray.get(i)).setLostButtonStatus(1);
    }

    private void clearBettingSelection() {
        for (int i = 0; i < 14; i++) {
            matchDataList.get(i).setWinButtonStatus(0);
            matchDataList.get(i).setEqualButtonStatus(0);
            matchDataList.get(i).setLostButtonStatus(0);
            matchDataList.get(i).setClickStatus(false);
            for (int j = 0; j < 3; j++)
                betSelectionArray[i][j] = null;
        }
        athleticsListAdapter.notifyDataSetChanged();
        checkBetIsWork();
        countingBetMoney();
// sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font>" + "0元"));
        resetBtn();
    }

    @Override
    protected void exTask() {
        super.exTask();
        if (HttpConnectUtil.isNetworkAvailable(RenXuanJiuActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            SportLotteryInfTask sportLotteryInfTask =
                new SportLotteryInfTask(this, matchDataList, athleticsListAdapter, kind, term);
            sportLotteryInfTask.execute();
            sportLotteryInfTask.setTaskChangeListener(this);
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this, inf);
        }
    }

    @Override
    public void onSFCR9ActionClick(String status) {
        if (status == null) {
            showWarningInfo();
        }
        else if (status.equals("200")) {
            if (resource != null) {
                vertifyListData(betOrgNum);
                checkBetIsWork();
// checkListClick(matchDataList);
                countingBetMoney();
                code = getBetCode(betSelectionArray);
                counttingListSelection();
                setClearButtonStatus();
                athleticsListAdapter.notifyDataSetChanged();
            }
            randomButton.setEnabled(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void vertifyListData(String orgNum) {
        String[] orgNumArray = orgNum.split("\\:")[0].split("\\,");
        for (int i = 0; i < orgNumArray.length; i++) {
            if (!orgNumArray[i].equals("X"))
                matchDataList.get(i).setClickStatus(true);
            char[] orgNumCodeArray = orgNumArray[i].toCharArray();
            for (int j = 0; j < orgNumCodeArray.length; j++) {
                if (orgNumCodeArray[j] == '0') {
                    matchDataList.get(i).setLostButtonStatus(1);
                    betSelectionArray[i][2] = "0";
                }
                else if (orgNumCodeArray[j] == '1') {
                    matchDataList.get(i).setEqualButtonStatus(1);
                    betSelectionArray[i][1] = "1";
                }
                else if (orgNumCodeArray[j] == '3') {
                    matchDataList.get(i).setWinButtonStatus(1);
                    betSelectionArray[i][0] = "3";
                }
            }
        }
    }
}
