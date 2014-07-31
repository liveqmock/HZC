package com.haozan.caipiao.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.GetServiceContentTask;
import com.haozan.caipiao.task.GetServiceContentTask.OnGetServiceListener;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 中奖短信订阅
 * 
 * @author peter_feng
 * @create-time 2013-6-27 下午1:03:12
 */
public class AdditionalService
    extends BasicActivity
    implements OnClickListener, OnGetServiceListener {

    private LinearLayout awardSmsLinearSbuscribe;
    private LinearLayout awardSmsLinearCancle;
    private TextView awardSmsNoticeTitle01;
    private TextView awardSmsNoticeTitle02;
    private TextView awardSmsNoticeTitle03;
    private TextView title;
    private Button AwardSmsNoticeSubmit;
    private Button AwardSmsNoticeCancle;
    private TextView awardNoticeServiceText;
    private TextView awardNoticeServiceText01;
    private TextView awardNoticeServiceText02;
    private TextView awardNoticeServiceText03;

    private boolean isRegister = false;
    private String scorePerWeek;
    private int pointed;
    private String subcrite;
    private Calendar cal;
    private int day;
    private int willConsumePoint = 0;
    private boolean haveNotEnoughPoint = false;

    private CustomDialog dlgSubcrite;
    private ProgressDialog dlgProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.additional_service);
        setupViews();
        init();
        initViews();
    }

    private void init() {
        getServiceInf();
    }

    private void initData() {
        cal = Calendar.getInstance();
        cal.setTime(stringConvertToDate(appState.getTime()));
        day = getDayOfWeek(stringConvertToDate(appState.getTime()));

        if (appState.getServiceed() != null) {
            if (getSmsNoticeservice(appState.getServiceed())) {
                isRegister = true;
            }
            else {
                isRegister = false;
            }
        }
    }

    private boolean getSmsNoticeservice(String serviceCode) {
        String[] serviceCodeArray = serviceCode.split("\\,");
        for (int i = 0; i < serviceCodeArray.length; i++)
            if (serviceCodeArray[i].equals("WSN"))
                return true;
        return false;
    }

    private void setupViews() {
        awardNoticeServiceText = (TextView) findViewById(R.id.award_sms_notice_text02);
        awardNoticeServiceText01 = (TextView) findViewById(R.id.award_sms_notice_text01);
        awardNoticeServiceText03 = (TextView) findViewById(R.id.award_sms_notice_text03);
        awardSmsLinearSbuscribe = (LinearLayout) findViewById(R.id.award_sms_notice_Linear01);
        awardSmsLinearCancle = (LinearLayout) findViewById(R.id.award_sms_notice_Linear02);
        awardSmsNoticeTitle01 = (TextView) findViewById(R.id.award_sms_notice_title01);
        setTextBold(awardSmsNoticeTitle01);
        awardSmsNoticeTitle02 = (TextView) findViewById(R.id.award_sms_notice_title02);
        setTextBold(awardSmsNoticeTitle02);
        awardSmsNoticeTitle03 = (TextView) findViewById(R.id.award_sms_notice_title03);
        setTextBold(awardSmsNoticeTitle03);
        title = (TextView) findViewById(R.id.award_sms_notice_title);
        AwardSmsNoticeSubmit = (Button) findViewById(R.id.award_sms_notice_subcrite);
        AwardSmsNoticeSubmit.setOnClickListener(this);
        AwardSmsNoticeCancle = (Button) findViewById(R.id.award_sms_notice_cancle);
        AwardSmsNoticeCancle.setOnClickListener(this);
        dlgProgress = new ProgressDialog(AdditionalService.this);
        dlgProgress.setMessage("正在提交...");
    }

    private void getServiceInf() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            GetServiceContentTask getServiceInf = new GetServiceContentTask(this);
            getServiceInf.setOnGetServiceListener(this);
            getServiceInf.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private void initViews() {
        boolean isExceptioned = false;
        int weeklyScore = 1;
        try {
            weeklyScore = Integer.valueOf(scorePerWeek);
            isExceptioned = false;
        }
        catch (Exception e) {
            e.printStackTrace();
            isExceptioned = true;
        }

        String messageInDlg = null;
        if (isRegister) {
            awardSmsLinearSbuscribe.setVisibility(View.GONE);
            awardSmsLinearCancle.setVisibility(View.VISIBLE);
            AwardSmsNoticeSubmit.setText("取消订阅");
            title.setText("取消中奖短信通知服务");
            subcrite = "no";
            messageInDlg = "确定取消中奖短信通知服务?";
        }
        else {
            title.setText("定制中奖短信通知服务");
            awardSmsLinearSbuscribe.setVisibility(View.VISIBLE);
            awardSmsLinearCancle.setVisibility(View.GONE);
            AwardSmsNoticeSubmit.setText("订阅");
            subcrite = "yes";
            if (day == 1) {
                messageInDlg =
                    "现有积分：" + appState.getScore() + "分\n\n要扣积分：" + weeklyScore + "分\n\n可用时间：" +
                        appState.getScore() / weeklyScore + "星期";
                pointed = weeklyScore;
                willConsumePoint = weeklyScore;
            }
            else {
                messageInDlg = setDialogContentMessage();
                pointed = (7 - day + 1) * weeklyScore / 7;
            }
        }

// int weeklyScore = 0;
// try {
// weeklyScore = Integer.valueOf(score_per_week);
// }
// catch (Exception e) {
// e.printStackTrace();
// }

        if (isExceptioned == false) {
            awardNoticeServiceText.setText(this.getResources().getString(R.string.award_notice_service02_01) +
                weeklyScore + this.getResources().getString(R.string.award_notice_service02_02) +
                Integer.valueOf(weeklyScore) / 7 +
                this.getResources().getString(R.string.award_notice_service02_03));
            AwardSmsNoticeSubmit.setEnabled(true);
        }
        else {
            awardNoticeServiceText.setText("--");
            awardNoticeServiceText01.setText("--");
            awardNoticeServiceText03.setText("--");
            AwardSmsNoticeSubmit.setEnabled(false);
        }

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示").setMessage(messageInDlg).setNegativeButton("取  消",
                                                                                new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog,
                                                                                                        int which) {
                                                                                        dlgSubcrite.dismiss();
                                                                                    }
                                                                                }).setPositiveButton("确  定",
                                                                                                     new DialogInterface.OnClickListener() {
                                                                                                         public void onClick(DialogInterface dialog,
                                                                                                                             int which) {
                                                                                                             dlgSubcrite.dismiss();
                                                                                                             if (HttpConnectUtil.isNetworkAvailable(AdditionalService.this)) {
                                                                                                                 RegisterServiceContentTask gsct =
                                                                                                                     new RegisterServiceContentTask();
                                                                                                                 gsct.execute();
                                                                                                             }
                                                                                                             else {
                                                                                                                 ViewUtil.showTipsToast(AdditionalService.this,
                                                                                                                                        noNetTips);
                                                                                                             }
                                                                                                         }
                                                                                                     });
        dlgSubcrite = customBuilder.create();
    }

    private String setDialogContentMessage() {
        int lottery_score = appState.getScore() - pointed;
        int weeklyScore = 1;
        try {
            weeklyScore = Integer.valueOf(scorePerWeek);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (lottery_score < weeklyScore) {
            haveNotEnoughPoint = true;
            willConsumePoint = (7 - day + 1) * weeklyScore / 7;
            return "现有积分：" + String.valueOf(appState.getScore()) + "分\n\n现开始至下周一扣除" + (7 - day + 1) *
                weeklyScore / 7 + "分，您的剩余积分将不足续订本服务，请及时赚取积分。";
        }
        else if (lottery_score > weeklyScore) {
            int num_of_week = lottery_score / weeklyScore;
            cal.add(Calendar.WEEK_OF_MONTH, num_of_week);
            willConsumePoint = (7 - day + 1) * weeklyScore / 7;
            return "现有积分：" + String.valueOf(appState.getScore()) + "分\n\n现开始至下周一扣除" + (7 - day + 1) *
                weeklyScore / 7 + "分，以后每周扣除" + weeklyScore + "分\n\n可用时间：" + num_of_week + "个星期\n\n结束日期：" +
                formatDatefmt(cal.getTime()) + "";
        }
        else
            return null;
    }

    private String formatDatefmt(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            formatDateString(date);
            toDate = dateFormat.parse(formatDateString(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    private int getDayOfWeek(Date date) {

        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private String formatDateString(String date) {
        StringBuilder sb_date = new StringBuilder();
        sb_date.append(date.substring(0, 8));
        sb_date.insert(4, "-");
        sb_date.insert(7, "-");
        return sb_date.toString();
    }

    private void setTextBold(TextView bt) {
        TextPaint tp = bt.getPaint();
        tp.setFakeBoldText(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.award_sms_notice_subcrite) {
            dlgSubcrite.show();
        }
        else if (v.getId() == R.id.award_sms_notice_cancle) {
            this.finish();
        }

    }

    class RegisterServiceContentTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPostExecute(String result) {
            dlgProgress.dismiss();
            JsonAnalyse analyse = new JsonAnalyse();
            if (result != null) {
                String status = analyse.getStatus(result);
                if (status.equals("200")) {

                    if (isRegister) {
                        ViewUtil.showTipsToast(AdditionalService.this, "取消中奖短信服务成功");
                        AdditionalService.this.setResult(4);
                        AdditionalService.this.finish();
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel(AdditionalService.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                   R.anim.push_to_left_out);
                        }
                        appState.setServiced(dealSmsNoticeservice(appState.getServiceed(), isRegister));
                    }
                    else {
                        ViewUtil.showTipsToast(AdditionalService.this, "中奖短信订阅成功");
                        AdditionalService.this.setResult(3);
                        AdditionalService.this.finish();
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel(AdditionalService.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                                   R.anim.push_to_left_out);
                        }
                        appState.setScore(appState.getScore() - willConsumePoint);
                        appState.setServiced(dealSmsNoticeservice(appState.getServiceed(), isRegister));
                    }
                }
                else if (status.equals("204")) {
                    ViewUtil.showTipsToast(AdditionalService.this, "该服务维护中(0:00-4:00点)，暂停订阅 ");
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(AdditionalService.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(AdditionalService.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    if (haveNotEnoughPoint)
                        ViewUtil.showTipsToast(AdditionalService.this, "积分不足");
                    else
                        ViewUtil.showTipsToast(AdditionalService.this, "中奖短信通知订阅失败");
                }
            }
            else {
                if (haveNotEnoughPoint)
                    ViewUtil.showTipsToast(AdditionalService.this, "积分不足");
                else {
                    String inf = analyse.getData(result, "error_desc");
                    ViewUtil.showTipsToast(AdditionalService.this, inf);
                }
            }
        }

        private String dealSmsNoticeservice(String serviceCode, boolean isSubcrybte) {
            StringBuilder smsNoticeCode = new StringBuilder();
            if (isSubcrybte) {
                String[] serviceCodeArray = serviceCode.split("\\,");
                for (int i = 0; i < serviceCodeArray.length; i++) {
                    if (!serviceCodeArray[i].equals("WSN")) {
                        smsNoticeCode.append(serviceCodeArray[i]);
                        smsNoticeCode.append(",");
                    }
                }
            }
            else {
                smsNoticeCode.append("WSN,");
                if (serviceCode != null) {
                    smsNoticeCode.append(serviceCode);
                    smsNoticeCode.append(",");
                }
            }
            if (!smsNoticeCode.toString().equals(""))
                smsNoticeCode.delete(smsNoticeCode.length() - 1, smsNoticeCode.length());
            return smsNoticeCode.toString();
        }

        @Override
        protected String doInBackground(Void... kind) {
            ConnectService connectNet = new ConnectService(AdditionalService.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private HashMap<String, String> initHashMap() {
// LotteryApp appState = ((LotteryApp) UserInf.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2007021");
            parameter.put("pid", LotteryUtils.getPid(AdditionalService.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(phone));
            parameter.put("type", "WSN");
            parameter.put("do", subcrite);
            parameter.put("value", String.valueOf(pointed));
            return parameter;
        }

        @Override
        protected void onPreExecute() {
            dlgProgress.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open about");
        String eventName = "v2 open sms subscribe";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_SMS_subscribe";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public void onServicePre() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onContentPost(String json) {
        if (json != null) {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                String data = analyse.getData(json, "response_data");
                try {
                    JSONObject jo = new JSONObject(data);
                    scorePerWeek = jo.getString("cost");
                    initViews();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
