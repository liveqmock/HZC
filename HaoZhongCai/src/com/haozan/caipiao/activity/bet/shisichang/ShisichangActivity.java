package com.haozan.caipiao.activity.bet.shisichang;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ShisichangActivity
    extends FootBallBetBasic
    implements OnAthleticsListItemListener, OnClickListener, OnTaskChangeListener {

    private ListView athleticsBetDataList;
    private AthleticsListAdapter athleticsListAdapter;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String[][] betSelectionArray;
    private String chargeMoney = null;
    private boolean isBetable = true;
    private int betNum = 1;
    private int num = 1;
    private int listClickedNum = 0;
    private String resource = null;
    private String betOrgNum = null;
    private Bundle bundle;
    private Button randomButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shi_si_chang);
        setUpView();
        init();
        iniBetList(ShisichangActivity.this);
    }

    @Override
    public void setKind() {
        super.setKind();
        this.kind = "sfc";
    }

    private void setUpView() {
        setBasicView();
        athleticsBetDataList = (ListView) findViewById(R.id.shi_si_chang_list_view);
        betOderClear.setOnClickListener(this);
        makeBetOder.setOnClickListener(this);
        helpLin.setOnClickListener(this);
        randomButton = (Button) findViewById(R.id.random_button);
        randomButton.setVisibility(View.VISIBLE);
        randomButton.setOnClickListener(this);
        randomButton.setEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.progress_large);
    }

    private void init() {
        sportTitle.setText("胜负彩");
        sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font> 0元"));
        betSelectionArray = new String[14][3];
        matchDataList = new ArrayList<AthleticsListItemData>();
        athleticsListAdapter = new AthleticsListAdapter(ShisichangActivity.this, matchDataList);
        athleticsListAdapter.setListItemClickListener(this);
        athleticsBetDataList.setAdapter(athleticsListAdapter);
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

        if (betSelectionArray[grounpIndex][childIndex] == null) {
            betSelectionArray[grounpIndex][childIndex] = selection;
        }
        else {
            betSelectionArray[grounpIndex][childIndex] = null;
        }
        checkListClick(athleticsDataList);
        countingBetMoney();
        code = getBetCode(betSelectionArray);
        counttingListSelection();
        setClearButtonStatus();
    }

    private String getBetCode(String[][] betSelectedCodeArray) {
        StringBuilder betCode = new StringBuilder();
        for (int i = 0; i < betSelectedCodeArray.length; i++) {
            for (int j = 0; j < 3; j++) {
                if (betSelectedCodeArray[i][j] != null) {
                    betCode.append(betSelectedCodeArray[i][j]);
                }
            }
            betCode.append(",");
        }
        betCode.delete(betCode.length() - 1, betCode.length());
        displayCode = getBallsDisplayInf(betCode.toString());
        luckyNum = betCode.toString();
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
        return isBetable;
    }

    private void checkListClick(ArrayList<AthleticsListItemData> athleticsDataList) {
        isBetable = true;
        for (int i = 0; i < athleticsDataList.size(); i++) {
            if (!athleticsDataList.get(i).getClickStatus()) {
                isBetable = false;
                break;
            }
        }
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
            if (isBetable)
                enableBetBtn();
            else
                disableBetBtn();
        }
    }

    private void countingBetMoney() {
        betNum = 1;
        if (isBetable) {
            for (int h = 0; h < betSelectionArray.length; h++) {
                num = 0;
                for (int j = 0; j < 3; j++)
                    if (betSelectionArray[h][j] != null)
                        num++;
                betNum = betNum * num;
                chargeMoney = String.valueOf(betNum * 2);
                sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">" + betNum + "注</font> " +
                    chargeMoney + "元"));
                betMoney = Long.valueOf(chargeMoney);
            }
        }
        else {
            chargeMoney = String.valueOf(0);
            sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font> " + chargeMoney + "元"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sport_bet_clear) {
            clearBettingSelection();
        }
        else if (v.getId() == R.id.sport_bet_make_a_order) {
            bet();
        }
        else if (v.getId() == R.id.bet_help_lin) {
            submitStatisticClickRules();
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "胜负彩游戏规则");
            bundel.putString("lottery_help", "help_new/sfc.html");
            intent.putExtras(bundel);
            intent.setClass(ShisichangActivity.this, LotteryWinningRules.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.random_button) {
            submitStatisticRandomInf();
            clearBettingSelection();
            for (int i = 0; i < matchDataList.size(); i++) {
                matchDataList.get(i).setClickStatus(true);
                String[] oddsArray = matchDataList.get(i).getGameOdds().split(" ");
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
                    betSelectionArray[i][0] = "3";
                else if (index == 1)
                    betSelectionArray[i][1] = "1";
                else if (index == 2)
                    betSelectionArray[i][2] = "0";
            }
        }
        checkListClick(matchDataList);
        countingBetMoney();
        code = getBetCode(betSelectionArray);
        counttingListSelection();
        setClearButtonStatus();
        athleticsListAdapter.notifyDataSetChanged();
    }

    private void setListButtonStatus(int i, int index) {
        if (index == 0)
            matchDataList.get(i).setWinButtonStatus(1);
        else if (index == 1)
            matchDataList.get(i).setEqualButtonStatus(1);
        else if (index == 2)
            matchDataList.get(i).setLostButtonStatus(1);
    }

    @Override
    protected void exTask() {
        super.exTask();
        if (HttpConnectUtil.isNetworkAvailable(ShisichangActivity.this)) {
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
        resetBtn();
        sportBetMoney.setText(Html.fromHtml("<font color=\"#FF0000\">0注</font> 0元"));
    }

    @Override
    public void onSFCR9ActionClick(String status) {
        if (status == null) {
            showWarningInfo();
        }
        else if (status.equals("200")) {
            if (resource != null) {
                vertifyListData(betOrgNum);
                athleticsListAdapter.notifyDataSetChanged();
                checkListClick(matchDataList);
                countingBetMoney();
                code = getBetCode(betSelectionArray);
                counttingListSelection();
                setClearButtonStatus();
            }
            randomButton.setEnabled(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void vertifyListData(String orgNum) {
        String[] orgNumArray = orgNum.split("\\:")[0].split("\\,");
        for (int i = 0; i < orgNumArray.length; i++) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK)
                finish();
    }
}
