package com.haozan.caipiao.activity.bet;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.task.SportLotteryInfTask.OnTaskChangeListener;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.DrawBalls;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;

public class BetPayDigital
    extends BetPay
    implements OnTaskChangeListener {
    private static final String BET_PAY_DIGITAL_REQUEST_SERVICE = "1003011";

    private boolean scrolling2 = false;
    private int timesNumSingleWheel = 1;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String[][] teams = new String[15][2];

    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSpecailData();
        setupSpecailView();
        initSpecail();
    }

    private void setupSpecailView() {
    }

    private void initSpecail() {
        matchDataList = new ArrayList<AthleticsListItemData>();
        if (kind.equals("sfc") || kind.equals("r9")) {
            zucaiPredicate.setVisibility(View.VISIBLE);
            lotteryNum.setVisibility(View.GONE);
            excuteTask();
        }
        else {
            lotteryNum.setVisibility(View.VISIBLE);
            zucaiPredicate.setVisibility(View.GONE);
            if (kind.equals("jlk3")) {
                if (displayCode.indexOf(";") > 0) {
                    String[] codes = displayCode.split(";");
                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(codes[0]);
                    strBuilder.append("<br/>");
                    strBuilder.append(codes[1]);
                    displayCode = null;
                    displayCode = strBuilder.toString();
                }
            }
            lotteryNum.setText(Html.fromHtml(displayCode));
        }
        invalidateMoney();
        initWheel();
        Boolean supportDirection =
            sm.registerListener(orienttationListener, orienttationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (!supportDirection) {
            directionLayout.setVisibility(View.GONE);
            editShareContent.setText("投注了" + kindChineseName + "：" + wholeMoney + "元");
        }
        // 体彩提示用户出票比较慢
        if (kind.equals("dlt") || kind.equals("plw") || kind.equals("pls")) {
            warningTips.setVisibility(View.VISIBLE);
        }
        if (kind.equals("dlt")) {
            if (isGetDltNormalType(code))
                superadditionLayout.setVisibility(View.VISIBLE);
            else
                superadditionLayout.setVisibility(View.GONE);
        }
        if (kind.equals("sfc") || kind.equals("r9"))
            sfcBetDetail.setVisibility(View.VISIBLE);
    }

    private void initWheel() {
        if (kind.equals("sfc") || kind.equals("r9")) {
            betNum.setText("倍投");
            stopPursuitLayout.setVisibility(View.GONE);
            layoutWholeWheel.setVisibility(View.GONE);
            layoutSingleWheel.setVisibility(View.VISIBLE);
            timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, 0));
            timesWheel2.setCurrentItem(0);
            timesWheel2.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (!scrolling2) {
                        updateSingleTimesText(timesWheel2);
                    }
                }
            });
        }
    }

    protected void updateSingleTimesText(WheelView item) {
        timesNumSingleWheel = item.getCurrentItem() + 1;
        invalidateWheelMoney();
    }

    private void initSpecailData() {
        // 获取屏幕分辨率
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        screenWidth = display.getWidth();

        Bundle bundle = getIntent().getExtras();
        endTimeMillis = (bundle.getLong("endtime"));
        gapMillis = (bundle.getLong("gaptime"));
        code = bundle.getString("bet_code");
        displayCode = bundle.getString("bet_display_code");
        luckyNum = bundle.getString("luckynum");
        if (luckyNum == null)
            luckyNum = "-";
        mStar = bundle.getString("mstar");
        if (mStar == null)
            mStar = "-";
        todayLucky = bundle.getString("today_lucky");
        if (todayLucky == null)
            todayLucky = "-";
        long millis = endTimeMillis - gapMillis - System.currentTimeMillis() - 16 * 1000;
        if (term != null && millis > 0) {
            handler.removeMessages(UPDATEBETTIME);
            handler.sendEmptyMessage(UPDATEBETTIME);
        }
        else {
            ifBetEnd = true;
            stopBet();
            if (HttpConnectUtil.isNetworkAvailable(BetPayDigital.this)) {
                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                getLotteryInf.execute();
            }
            else
                setGetTermFail();
        }
    }

    @Override
    protected void invalidateWheelMoney() {
        long wheelMoney;
        if (kind.equals("sfc") || kind.equals("r9")) {
            wheelMoney = allMoney * timesNumSingleWheel;
        }
        else {
            wheelMoney = allMoney * followNumWheel * timesNumWheel;
        }
        shareMoney = wheelMoney;
        long num = wheelMoney / 2;
        if (isSuperaddion)
            wheelMoney = wheelMoney / 2 * 3;
        String moneyStr = num + "注     <font color='red'>" + wheelMoney + "元</font>";
        wheelBetMoney.setText(Html.fromHtml(moneyStr));
    }

    @Override
    protected void setBetSetting() {
        if (kind.equals("sfc") || kind.equals("r9")) {
            timesNum = timesNumSingleWheel;
            refreshDoubleFollowText();
        }
        else {
            timesNum = timesNumWheel;
            followNum = followNumWheel;
            refreshDoubleFollowText();
        }
        switchBottomLayout();
    }

    @Override
    protected void refreshDoubleFollowText() {
        if (kind.equals("sfc") || kind.equals("r9")) {
            if (timesNum == 1)
                betNum.setText("单倍");
            else
                betNum.setText(timesNum + "倍");
        }
        else {
            if (timesNum == 1 && followNum == 1)
                betNum.setText("倍投\n追期");
            else if (timesNum == 1)
                betNum.setText("单倍   " + "\n追" + followNum + "期");
            else if (followNum == 1)
                betNum.setText(timesNum + "倍\n" + " 无追期");
            else
                betNum.setText(timesNum + "倍\n" + " 追" + followNum + "期");
        }
        invalidateMoney();
    }

    @Override
    protected void switchBottomLayout() {
        if (wheelLayout.isShown()) {
            wheelLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
        else {
            if (kind.equals("sfc") || kind.equals("r9")) {
                timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, timesNum - 1));
                timesWheel2.setCurrentItem(timesNum - 1);
            }
            else {
                followWheel.setViewAdapter(new DateNumericAdapter(this, 1, getTerms(kind), followNum - 1));
                followWheel.setCurrentItem(followNum - 1);
// timesWheel.setViewAdapter(new DateArrayAdapter(this, timesArray, timesNum - 1));
                timesWheel.setViewAdapter(new DateNumericAdapter(this, 1, 99, timesNum - 1));
                timesWheel.setCurrentItem(timesNum - 1);
            }
            wheelLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            invalidateWheelMoney();
        }
    }

    @Override
    protected void toBet() {
        // TODO
        BetTask task = new BetTask();
        task.execute(kind);
// Intent intent = new Intent();
// Bundle bundle = new Bundle();
// bundle.putString("bet_kind", kind);
// bundle.putInt("type", 0);
// intent.putExtras(bundle);
// intent.setClass(BetPayDigital.this, BetSuccessPage.class);
// startActivityForResult(intent, 2);
// setResult(RESULT_OK);
// finish();
    }

    public class BetTask
        extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String json) {
            String error = null;
            submit.setEnabled(true);
            progress.dismiss();
            if (json != null) {
                String inf = null;
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    mUploadRequestTime.submitConnectSuccess(BET_PAY_DIGITAL_REQUEST_SERVICE);

                    submitStatisticsBetSuccess();
                    appState.setAccount(Double.valueOf(appState.getAccount()) - wholeMoney);
// betResultDialog.setTextContent("投注请求发送成功，请查看" + "<font color=\"#0000FF\"><u>" + "消息盒" + "</u></font>" +
// "是否出票成功，祝您中奖！如需帮助请与客服联系，QQ：" + LotteryConfig.QQ);
// betResultDialog.show();
                    // TODO changed by vincent
                    setResult(RESULT_OK);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("bet_kind", kind);
                    if (!(Double.valueOf(appState.getAccount()) < 2)) {
                        bundle.putInt("type", 1);
                    }
                    else {
                        bundle.putInt("type", 0);
                    }
                    intent.putExtras(bundle);
                    intent.setClass(BetPayDigital.this, BetSuccessPage.class);
                    startActivityForResult(intent, 2);
                    finish();
                }
                else if (status.equals("300")) {
                    error = ja.getData(json, "error_desc");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(BetPayDigital.this);
                    inf = getResources().getString(R.string.login_timeout);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(BetPayDigital.this);
                    inf = getResources().getString(R.string.login_again);
                    showLoginAgainDialog(inf);
                }
                else {
                    error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
                }
            }
            else {
                error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
            }
            if (error != null) {
                mUploadRequestTime.submitConnectFail(BET_PAY_DIGITAL_REQUEST_SERVICE, json);

                ViewUtil.showFailDialog(BetPayDigital.this, error);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... kind) {
            ConnectService connectNet = new ConnectService(BetPayDigital.this);
            String json = null;
            try {
                json = connectNet.getJsonPost(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    private HashMap<String, String> initHashMap() {
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", BET_PAY_DIGITAL_REQUEST_SERVICE);
        parameter.put("pid", LotteryUtils.getPid(BetPayDigital.this));
        parameter.put("lottery_id", kind);
        parameter.put("term", term);
        parameter.put("codes", generateCode());
        parameter.put("money", String.valueOf(wholeMoney / followNum));
        parameter.put("lucknum", luckyNum);
        parameter.put("mstar", HttpConnectUtil.encodeParameter(mStar));
        parameter.put("todayluck", HttpConnectUtil.encodeParameter(todayLucky));
        parameter.put("mode", mode);
        parameter.put("phone", phone);
        if (address != null) {
            if (address.length() > 50) {
                address = address.substring(0, 50);
            }
            parameter.put("x", String.valueOf(longitude));
            parameter.put("y", String.valueOf(latitude));
            parameter.put("l", HttpConnectUtil.encodeParameter(address));
        }
        parameter.put("source",
                      HttpConnectUtil.encodeParameter(LotteryUtils.versionName(BetPayDigital.this, true)));
        parameter.put("version", LotteryUtils.fullVersion(BetPayDigital.this));
        // parameter.put("l", HttpConnectUtil.encodeParameter(appState.getBetAddress()));
        if (betInfTranspond) {
            parameter.put("m", HttpConnectUtil.encodeParameter(("".equals(editShare.getText().toString())
                ? "" : (editShare.getText().toString() + "\n")) + editShareContent.getText().toString()));
        }

// presentOrentation=-1;
        if (presentOrentation != -1.0f && presentOrentation >= 0 && presentOrentation <= 360)
            parameter.put("d", String.valueOf(presentOrentation));

        if (followNum > 1) {

            if (isStopPursuit) {
                parameter.put("pursuit", String.valueOf(followNum) + "|1");
            }
            else {
                parameter.put("pursuit", String.valueOf(followNum) + "|0");
            }
        }
        if (betInfTranspond) {
            parameter.put("action", "1");
        }

        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    public String generateCode() {
        StringBuilder allCode = new StringBuilder();
        String[] prevCode = code.split(";");
        for (String s : prevCode) {
            allCode.append(s);
            if (kind.equals("dlt")) {
                if (isGetDltNormalType(s)) { // 非生肖乐
                    if (isSuperaddion) // 追加
                        allCode.append(":2:");
                    else
                        allCode.append(":1:");

                    if (isGetDltDantuoType(s)) {// 大乐透胆拖时
                        allCode.append("5:");
                    }
                    else {
                        String[] nums = s.split("\\|");
                        String[] redBall = nums[0].split("\\,");
                        String[] blueBall = nums[1].split("\\,");
                        if (redBall.length > 5 || blueBall.length > 2)
                            allCode.append("2:");
                        else
                            allCode.append("1:");
                    }

                }
                else {
                    allCode.append(":3:");
                    String[] blueBall = s.split("\\,");
                    if (blueBall.length > 2)
                        allCode.append("2:");
                    else
                        allCode.append("1:");
                }
            }
            allCode.append(timesNum + ";");
        }
        allCode.delete(allCode.length() - 1, allCode.length());
        return allCode.toString();
    }

    private boolean isGetDltNormalType(String s) {
        if (s.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isGetDltDantuoType(String s) {
        if (s.indexOf('$') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private void excuteTask() {
        if (HttpConnectUtil.isNetworkAvailable(BetPayDigital.this)) {

            if (kind.equals("sfc") || kind.equals("r9")) {
                SportLotteryInfTask sportLotteryInfTask =
                    new SportLotteryInfTask(BetPayDigital.this, matchDataList, kind, term);
                sportLotteryInfTask.execute();
                sportLotteryInfTask.setTaskChangeListener(this);
            }
        }
        else {
            displayBetSFCCode();
            // show message
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this, inf);
        }
    }

    @Override
    public void onSFCR9ActionClick(String status) {
        if (status != null) {
            if (status.equals("200")) {
                teams[0][0] = "主";
                teams[0][1] = "客";
                for (int i = 1; i < 15; i++) {
                    teams[i][0] = matchDataList.get(i - 1).getMatchHomeTeamName();
                    teams[i][1] = matchDataList.get(i - 1).getMatchGuessTeamName();
                }
                String betCode = code + "+01";
                DrawBalls drawBalls = new DrawBalls();
                drawBalls.drawSFCHistoryBall(BetPayDigital.this, zucaiPredicate, kind, betCode, teams,
                                             screenWidth);
            }
            else {
                displayBetSFCCode();
            }
        }
    }

    private void displayBetSFCCode() {
        teams[0][0] = "主";
        teams[0][1] = "客";
        for (int i = 1; i < 15; i++) {
            teams[i][0] = String.valueOf(i);
            teams[i][1] = String.valueOf(i);
        }
        String betCode = code + "+01";
        DrawBalls drawBalls = new DrawBalls();
        drawBalls.drawSFCHistoryBall(BetPayDigital.this, zucaiPredicate, kind, betCode, teams, screenWidth);
    }

}
