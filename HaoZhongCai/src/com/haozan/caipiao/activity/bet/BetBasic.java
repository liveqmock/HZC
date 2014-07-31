package com.haozan.caipiao.activity.bet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.umeng.analytics.MobclickAgent;

public abstract class BetBasic
    extends ContainTipsPageBasicActivity {
    protected static final String MONEY_TIPS = "0注 <font color='red'>0元</font>";

    protected static final int GETNEWINF = 1;
    protected static final int UPDATEBETTIME = 3;

    protected long endTimeMillis = 0;
    protected long gapMillis = 0;
    protected String term;
    // the English name of the lottery
    protected String kind;
    protected String awardTime;
    private StringBuilder betLastTime;

    protected SimpleDateFormat dateFormat;

    protected Animation anim3;
    protected Animation anim4;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETNEWINF:
                    if (HttpConnectUtil.isNetworkAvailable(BetBasic.this)) {
                        GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                        getLotteryInf.execute();
                    }
                    else
                        setGetTermFail();
                    break;
                case UPDATEBETTIME:
                    betLastTime.delete(0, betLastTime.length());
                    long millis = endTimeMillis - gapMillis - System.currentTimeMillis() - 16 * 1000;
                    betCountTime(millis);
                    millis -= 1000;
                    if (millis >= 0)
                        handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                    else {
                        endBet();
                        handler.sendEmptyMessageDelayed(GETNEWINF, 3000);
                    }
                    break;
            }
        }
    };

    abstract protected void betCountTime(long millis);

    abstract protected void endBet();

    protected void initBasic() {
        anim3 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim4 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        setKind();
        betLastTime = new StringBuilder();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    abstract protected void enableBetBtn();

    abstract protected void disableBetBtn();

    /**
     * 根据金额状态设置按钮状态
     * 
     * @param money 投注金额
     */
    protected void checkBet(long money) {
        if (money > 0) {
            enableBetBtn();
        }
        else {
            disableBetBtn();
        }
    }

    protected String getBetInf(long betNumber, long betMoney) {
        if (betNumber <= 0 || betMoney <= 0)
            return null;
        String betInf = null;
        betInf = betNumber + "注  <font color='red'>" + betMoney + "元</font>";
        return betInf;
    }

    private void startCountDown(String endtime, String systemtime) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (endtime == null || systemtime == null)
            return;
        try {
            Date date1 = format1.parse(endtime);
            Date date2 = format1.parse(systemtime);
            endTimeMillis = date1.getTime();
            gapMillis = date2.getTime() - System.currentTimeMillis();
            long millis = endTimeMillis - gapMillis - System.currentTimeMillis() - 16 * 1000;
            if (millis >= 0) {
                betCountTime(millis);
                millis -= 1000;
                if (millis >= 0)
                    handler.sendEmptyMessageDelayed(UPDATEBETTIME, 1000);
                else {
                    endBet();
                    betLastTime = null;
                }
            }
            else {
                endBet();
            }
        }
        catch (NotFoundException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    abstract public void setKind();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(UPDATEBETTIME);
    }

    abstract public void setLotteryTerm();

    public class GetLotteryInfTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> initHashMap(String kind)
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "lottery_base_info");
            parameter.put("pid", LotteryUtils.getPid(BetBasic.this));
            parameter.put("lottery_id", kind);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetBasic.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, initHashMap(kind));
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
                    String response = ja.getData(json, "response_data");
                    String getTerm = ja.getData(response, "term");
                    String endtime = ja.getData(response, "endtime");
                    String awardtime = ja.getData(response, "awardtime");
                    String systemtime = ja.getData(response, "systemtime");
                    if (getTerm != null && systemtime != null && endtime != null && !endtime.equals("") &&
                        !systemtime.equals("")) {
// countDownTime.setVisibility(View.VISIBLE);
                        term = getTerm;
                        setLotteryTerm();
                        GetServerTime time = new GetServerTime(BetBasic.this);
                        OperateInfUtils.refreshTime(BetBasic.this, time.formatTime(systemtime));
                        startCountDown(endtime, systemtime);
                        awardTime = awardtime;
                    }
                    else
                        setGetTermFail();
                }
                else
                    setGetTermFail();
            }
            else {
                setGetTermFail();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            endBet();
        }
    }

    protected void setGetTermFail() {
        endBet();
        showWarningInfo();
    }

    abstract public void showWarningInfo();

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet");
        map.put("more_inf", "open bet " + kind);
        String eventName = "v2 open bet";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet";
        MobclickAgent.onEvent(this, eventName, kind);
        besttoneEventCommint(eventName);
    }

}
