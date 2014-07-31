package com.haozan.caipiao.activity.bet;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.task.SportLotteryInfTask.OnTaskChangeListener;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.DrawBalls;
import com.haozan.caipiao.widget.wheelview.OnWheelChangedListener;
import com.haozan.caipiao.widget.wheelview.WheelView;
import com.haozan.caipiao.widget.wheelview.adapter.DateNumericAdapter;

public class BetPayUniteDigital
    extends BetPayUnite
    implements OnTaskChangeListener {
    private static final String BET_PAY_UNITE_DIGITAL_REQUEST_SERVICE = "2003071";

    private String displayCode;
    private String luckyNum;
    private String mStar;
    private String todayLucky;
    private ArrayList<AthleticsListItemData> matchDataList;
    private String[][] teams = new String[15][2];
    private boolean scrolling2 = false;
    private int timesNumSingleWheel = 1;

    private boolean tempb = false;

    private int selfTemp = 0;// 倍数改变时保存自购份数
    private int guaTemp;// 倍数改变时保存保底份数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSpecailData();
        setupSpecailView();
        initSpecail();
    }

    private void initSpecail() {
        if (kind.equals("sfc") || kind.equals("r9")) {
            matchDataList = new ArrayList<AthleticsListItemData>();
            zucaiPredicate.setVisibility(View.VISIBLE);
            lotteryNum.setVisibility(View.GONE);
            excuteZuicaiTask();
        }
        else {
            lotteryNum.setVisibility(View.VISIBLE);
            zucaiPredicate.setVisibility(View.GONE);
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
        if (kind.equals("dlt")) {
            if (isGetDltNormalType(code))
                superadditionLayout.setVisibility(View.VISIBLE);
            else
                superadditionLayout.setVisibility(View.GONE);
        }
        if (kind.equals("sfc") || kind.equals("r9"))
            sfcBetDetail.setVisibility(View.VISIBLE);
    }

    private void excuteZuicaiTask() {
        if (HttpConnectUtil.isNetworkAvailable(BetPayUniteDigital.this)) {

            if (kind.equals("sfc") || kind.equals("r9")) {
                SportLotteryInfTask sportLotteryInfTask =
                    new SportLotteryInfTask(BetPayUniteDigital.this, matchDataList, kind, term);
                sportLotteryInfTask.execute();
                sportLotteryInfTask.setTaskChangeListener(this);
            }
        }
        else {
            displayBetSFCCode();
            // show message
            ViewUtil.showTipsToast(this, noNetTips);
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
        drawBalls.drawSFCHistoryBall(BetPayUniteDigital.this, zucaiPredicate, kind, betCode, teams,
                                     screenWidth);
    }

    private boolean isGetDltNormalType(String s) {
        if (s.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private void initWheel() {
        betNum.setText("倍投");
        stopPursuitLayout.setVisibility(View.GONE);
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

    protected void updateSingleTimesText(WheelView item) {
        timesNumSingleWheel = item.getCurrentItem() + 1;
        invalidateWheelMoney();
    }

    private void setupSpecailView() {
    }

    private void initSpecailData() {
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
            if (HttpConnectUtil.isNetworkAvailable(BetPayUniteDigital.this)) {
                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                getLotteryInf.execute();
            }
            else
                setGetTermFail();
        }
    }

    @Override
    protected void invalidateWheelMoney() {
        wheelMoney = money * timesNumSingleWheel;// money为上个类传来的参数 timesNumSingleWheel为监听到的当前的倍数
        shareMoney = wheelMoney;
        long num = wheelMoney / 2;
        if (isSuperaddion)
            wheelMoney = wheelMoney / 2 * 3;
        String moneyStr = num + "注     <font color='red'>" + wheelMoney + "元</font>";
        wheelBetMoney.setText(Html.fromHtml(moneyStr));
    }

    @Override
    protected void setBetSetting() {
        tempMoney = wheelMoney;
        timesNum = timesNumSingleWheel;
        invalidateMoney();

// int buyNumTemp = getSelfBuyNum();
//
// if (buyNumTemp < buyNums) {
// tv_buynum_ll.setText("");
// }
// else if (buyNumTemp > lastNums) {
// tv_buynum_ll.setText(String.valueOf(lastNums - 1));
// }
        resetSelfBuyHintWithoutChangeText();
        refreshDoubleFollowText();
        switchBottomLayout();
    }

    /**
     * 设置倍投按钮处显示的内容
     * 
     * @author Vimcent 2013-3-20
     */
    private void refreshDoubleFollowText() {
        if (timesNum == 1)
            betNum.setText("单倍");
        else
            betNum.setText(timesNum + "倍");
        invalidateMoney();
    }

    /**
     * 设置滚轮是否显示
     */
    @Override
    protected void switchBottomLayout() {
        if (wheelLayout.isShown()) {
            wheelLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
        else {
            timesWheel2.setViewAdapter(new DateNumericAdapter(this, 1, 99, timesNum - 1));
            timesWheel2.setCurrentItem(timesNum - 1);

            wheelLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
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
// bundle.putInt("type", 1);
// intent.putExtras(bundle);
// intent.setClass(BetPayUniteDigital.this, BetSuccessPage.class);
// startActivityForResult(intent, 2);
// setResult(RESULT_OK);
// finish();
    }

    public class BetTask
        extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... kind) {
            ConnectService connectNet = new ConnectService(BetPayUniteDigital.this);
            String json = null;
            try {
                json = connectNet.getJsonPost(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            String error = null;
            submit.setEnabled(true);
            progress.dismiss();
            if (json != null) {
                Logger.inf("vincent", json);
                String inf = null;
                JsonAnalyse ja = new JsonAnalyse();
                // get the status of the http data
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    mUploadRequestTime.submitConnectSuccess(BET_PAY_UNITE_DIGITAL_REQUEST_SERVICE);

                    submitStatisticsUniteSuccess();
                    appState.setAccount(Double.valueOf(appState.getAccount()) - wholeMoney);
// betResultDialog.setTextContent("发起合买请求发送成功！如需帮助请与客服联系，QQ：" + LotteryConfig.QQ);
// betResultDialog.show();
                    // TODO changed by vincent
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
                    intent.setClass(BetPayUniteDigital.this, BetSuccessPage.class);
                    startActivityForResult(intent, 2);
                    setResult(RESULT_OK);
                    finish();
                }
                else if (status.equals("300")) {
                    error = ja.getData(json, "error_desc");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(BetPayUniteDigital.this);
// databaseData.putString("username_for_third", "");
// databaseData.commit();
                    inf = getResources().getString(R.string.login_timeout);
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(BetPayUniteDigital.this);
// databaseData.putString("username_for_third", "");
// databaseData.commit();
                    inf = getResources().getString(R.string.login_again);
                    showLoginAgainDialog(inf);
                }
                else {
                    error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
                }
            }
            else {
                Logger.inf("vincent", "json = null");
                error = "网络不稳定，请查看个人中心是否投注成功，如需帮助请联系客服";
            }
            if (error != null) {
                mUploadRequestTime.submitConnectFail(BET_PAY_UNITE_DIGITAL_REQUEST_SERVICE, json);

                ViewUtil.showFailDialog(BetPayUniteDigital.this, error);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            progress.show();
        }

    }

    public HashMap<String, String> initHashMap() {
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", BET_PAY_UNITE_DIGITAL_REQUEST_SERVICE);// 服务接口名称
        parameter.put("pid", LotteryUtils.getPid(BetPayUniteDigital.this));// 分配的ID号
        parameter.put("lottery_id", kind);// 彩种id号
        parameter.put("term", term);// 请求购买的期数
        parameter.put("codes", generateCode());//
        parameter.put("money", String.valueOf(wholeMoney));// 本次合买方案的金额
// parameter.put("lucknum", luckyNum);
// parameter.put("mstar", HttpConnectUtil.encodeParameter(mStar));
// parameter.put("todayluck", HttpConnectUtil.encodeParameter(todayLucky));
        parameter.put("mode", mode);//
        parameter.put("phone", phone);// 手机号
        // 合买基本信息
        parameter.put("title", HttpConnectUtil.encodeParameter(eduniteTitle.getText().toString()));// 合买方案标题
        parameter.put("content", HttpConnectUtil.encodeParameter(eduniteDescribe.getText().toString()));// 合买方案描述
        parameter.put("open", getUniteSecret());// 投注号码保密设置
        if (ifCommission) {
            parameter.put("reward_percentage", getUniteCommission());// 中奖佣金的百分比
        }
        else {
            parameter.put("reward_percentage", "0");
        }
        parameter.put("each_money", getUnitePrice());// 每份金额
        parameter.put("buy_num_himself", String.valueOf(getSelfBuyNum()));// 发起人自己购买份数
        parameter.put("paul", String.valueOf(getGuaNum()));// 保底份数

        parameter.put("source",
                      HttpConnectUtil.encodeParameter(LotteryUtils.versionName(BetPayUniteDigital.this,
                                                                                 true)));
        if (betInfTranspond)
            parameter.put("m", HttpConnectUtil.encodeParameter(("".equals(editShare.getText().toString())
                ? "" : (editShare.getText().toString() + "\n")) + editShareContent.getText().toString()));

        if (betInfTranspond) {
            parameter.put("action", "1");
        }
        if (address != null) {
            if (address.length() > 50) {
                address = address.substring(0, 50);
            }
            parameter.put("x", String.valueOf(longitude));// 用户投注时的手机所处的经纬度
            parameter.put("y", String.valueOf(latitude));//
        }
        if (presentOrentation != -1.0f && presentOrentation >= 0 && presentOrentation <= 360)
            parameter.put("d", String.valueOf(presentOrentation));// 投注时的方向

        mUploadRequestTime.onConnectStart();
        return parameter;
    }

    private String generateCode() {
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
                            allCode.append("2:");// 复式
                        else
                            allCode.append("1:");// 但是
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

    private boolean isGetDltDantuoType(String s) {
        if (s.indexOf('$') != -1) {
            return true;
        }
        else {
            return false;
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
                drawBalls.drawSFCHistoryBall(BetPayUniteDigital.this, zucaiPredicate, kind, betCode, teams,
                                             screenWidth);
            }
            else {
                displayBetSFCCode();
            }
        }
    }

}
