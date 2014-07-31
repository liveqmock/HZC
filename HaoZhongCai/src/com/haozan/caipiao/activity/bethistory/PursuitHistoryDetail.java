package com.haozan.caipiao.activity.bethistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.adapter.PursuitDetailAdapter;
import com.haozan.caipiao.adapter.PursuitHeaderAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.PursuitHistoryDetailItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.scroll.OnViewChangeListener;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class PursuitHistoryDetail
    extends BasicActivity
    implements OnClickListener, OnViewChangeListener {
    private static final String RESPONSE = "response_data";
    protected String noRecord = "没有追号记录";
    protected String noPursuitRecord = "没有追号记录";
    private ListView pursuitDetailList;
    private ListView pursuitHeadList;
    private TextView lotId;
    private TextView pursuitNo;
    private TextView time;
    private TextView message;
    private TextView pursuitMoney;
    private TextView puruistStatusMessage;
    private Button stopPursuit;
    private ArrayList<PursuitHistoryDetailItem> pursuitBetDetailJsonRecord;
    private ArrayList<Map<String, Object>> data;
    private int mViewCount;
    private int mCurSel;
    private int listIndex = 0;
    private int q_bet = 2;
    private String q_no = "0";
    private String lotteryId;
    private String term;
    private boolean isGet = false;
    private boolean is_stop = false;
    private ProgressBar progressBar;
    private PursuitHeaderAdapter pha;
    private ImageView[] mImageViews;
    private PursuitDetailAdapter pursuitAdapter;
    private View showFailPage;

    private CustomDialog dlgStopPursuit;
    private ImageView kindImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pursuit_history_detail);
        setupView();
        setUpHeaderView();
        init();
    }

    private void setUpHeaderView() {
        pursuitNo = (TextView) this.findViewById(R.id.pursuit_no);
        time = (TextView) this.findViewById(R.id.pursuit_time);
        lotId = (TextView) this.findViewById(R.id.pursuit_lot_id);
        message = (TextView) this.findViewById(R.id.message);
        pursuitMoney = (TextView) this.findViewById(R.id.puruist_money);
        puruistStatusMessage = (TextView) this.findViewById(R.id.puruist_status_message);
        pursuitHeadList = (ListView) this.findViewById(R.id.puruist_ball_head_list);
        // 停止追号按钮
        stopPursuit = (Button) this.findViewById(R.id.bt_stop_pursuit);
        stopPursuit.setEnabled(false);
        stopPursuit.setOnClickListener(this);
        kindImg = (ImageView) findViewById(R.id.pursuit_kind_img);
    }

    private void setupView() {
        pursuitBetDetailJsonRecord = new ArrayList<PursuitHistoryDetailItem>();
        data = new ArrayList<Map<String, Object>>();
        // 初始化页面控件
        pursuitDetailList = (ListView) this.findViewById(R.id.pursuit_history_list);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        // 当页面出现异常情况时显示
        showFailPage = this.findViewById(R.id.show_fail_page);

    }

    // 把彩种代码转为彩种名称
    private String pursuitLotteryTypeName(String lotteryType) {
        StringBuilder lotteryName = new StringBuilder();
        if (!lotteryType.equals("klsf"))
            lotteryName.append(LotteryUtils.LOTTERY_NAMES[(LotteryUtils.getTypeIndex(lotteryType))]);
        return lotteryName.toString();
    }

    // 构建列表数据
    private void initRecord(ArrayList<PursuitHistoryDetailItem> pursuitBetDetailJsonRecord) {
        int buyMode = -1;
        int winStatus = 2;
        String openCode;
        String orderId;
        String[] openCodes;
        String lotteryType = lotteryId;
        String lotteryTerm = null;
        Double prize = 0.0;
        Double betMoney;

        for (int i = 0; i < puruistNo; i++) {
            Map<String, Object> map = new HashMap<String, Object>();

            lotteryTerm = pursuitBetDetailJsonRecord.get(i).getTerm();
            prize = pursuitBetDetailJsonRecord.get(i).getPrize();
            openCode = pursuitBetDetailJsonRecord.get(i).getOpenCodes();
            buyMode = pursuitBetDetailJsonRecord.get(i).getPursuitBuyMode();
            winStatus = pursuitBetDetailJsonRecord.get(i).getWin();
            orderId = pursuitBetDetailJsonRecord.get(i).getOrderId();
            betMoney = pursuitBetDetailJsonRecord.get(i).getMoney();

            map.put("lot_id", pursuitBetDetailJsonRecord.get(i).getLotteryId());

            if (lotteryTerm != null)
                map.put("term", "第" + lotteryTerm + "期");
            else
                map.put("term", "");

            // 投注金额
            if (betMoney < 0)
                map.put("money", "金额：--");
            else
                map.put("money", "金额：" + betMoney + "元");

            map.put("opneNum", "开奖：");

            // pursuitBetStatus: 追号状态 。0=进行中，1=追号已完成，2=出牌失败，3=追号手动停止
            // win: 中奖状态，0=未开奖，1=中奖，2=等待开奖
            String winDesc = "--";
            if (winStatus == 1) {
                winDesc = "<font color='#FF0000'>￥" + prize + "</font>";
            }
            else if (winStatus == 0) {
                winDesc = "未  中  奖";
            }
            else {
                if (pursuitBetStatus == 0) {
                    winDesc = "--";
                    if (buyMode == 0 || buyMode == 9) {
                        winDesc = "等待开奖";
                    }
                }
                else { // 3
                    winDesc = "已退款";
                    if (buyMode == 0 || buyMode == 9) {
                        winDesc = "等待开奖";
                    }
                }
            }
            map.put("win", winDesc);

            if (!openCode.equals("null")) {
                openCodes = openCode.split("\\|");

                map.put("open_code_blue", null);

                if (openCode.indexOf("|") != -1 && lotteryType.equals("dfljy"))
                    map.put("open_code_blue", "|" + LotteryUtils.animals[Integer.valueOf(openCodes[1]) - 1]);
                else if (openCode.indexOf("|") != -1)
                    map.put("open_code_blue", "|" + openCodes[1]);

                if (openCode.indexOf("|") == -1)
                    map.put("open_code_red", openCode);
                else
                    map.put("open_code_red", openCodes[0]);
            }
            else {
                map.put("open_code_red", "--");
                map.put("open_code_blue", null);
            }

            if (buyMode != -1) {// 投注状态：0=出票成功，1=出票失败，2=出票失败， 9=订单处理中
                if (buyMode == 0)
                    map.put("bet_status", "出票成功");
                if (buyMode == 1 || buyMode == 2)
                    map.put("bet_status", "出票失败");
                if (buyMode == 9)
                    map.put("bet_status", "订单处理中");
            }
            else {
                if (pursuitBetStatus == 2) // 追号状态：0=进行中 1=追号完成 2=出票失败 3=追号手动停止
                    map.put("bet_status", "出票失败");
                else if (pursuitBetStatus == 3) {
                    if (i == 0)
                        map.put("bet_status", "尚未出票");
                    else
                        map.put("bet_status", "手动停止");
                }
                else if (pursuitBetStatus == 1)
                    map.put("bet_status", "追号完成");
                else if (pursuitBetStatus == 0)
                    map.put("bet_status", "尚未出票");

            }

            map.put("index", i + 1);
            map.put("bet_time", "下注时间：" + pursuitBetTime);
            map.put("clickStatus", false);
            data.add(map);
        }
    }

    private String firstTerm = "-1";
    private String LotteryCode = "09,14,16,22,26|10,12:1:1:1";
    private int winStop = 0;
    private String pursuitBetTime = "2013-04-11 17:50:49_";
    private double pursuitBetMoney = -1;
    private int pursuitBetStatus = -1;// 追号状态
    private int LotteryPursuitId = -1;// 追号ID
    private int pursuitDoNo = -1;// 已经追号期数
    private int puruistNo = -1;// 需要追号的期数

    private void constructPursuitListData() {
        int buy = -1;
        Double money = Double.valueOf(pursuitBetMoney);
        String open = "null";
        String orderId = null;
        Double prize = 0.0;
        String term = null;
        int win = 2;
        for (int i = 0; i < puruistNo; i++) {
            PursuitHistoryDetailItem history = new PursuitHistoryDetailItem();
            history.setLotteryId(pursuitLotteryTypeName(lotteryId));
            history.setPursuitBuyMode(buy);
            history.setMoney(money);
            history.setOpenCodes(open);
            history.setOrderId(orderId);
            history.setPrize(prize);
            history.setTerm(term);
            history.setWin(win);
            pursuitBetDetailJsonRecord.add(history);
        }
    }

    private void init() {
        q_bet = 2;
        q_no = getIntent().getExtras().getString("pursuit_id");
        lotteryId = getIntent().getExtras().getString("lottery_id");
        int type = LotteryUtils.getTypeIndex(lotteryId);
        if (-1 != type) {
            kindImg.setBackgroundResource(LotteryUtils.HALL_ITEM_ICON[type]);
        }
        term = getIntent().getExtras().getString("term");

        pursuitAdapter = new PursuitDetailAdapter(PursuitHistoryDetail.this, data);
        pursuitDetailList.setAdapter(pursuitAdapter);

        if (HttpConnectUtil.isNetworkAvailable(PursuitHistoryDetail.this)) {
            PursuitBasicTask pbinfTask = new PursuitBasicTask();
            pbinfTask.execute();
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            pursuitDetailList.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            message.setText(inf);
            showFailPage.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        lotId.setText(pursuitLotteryTypeName(lotteryId));

        pursuitNo.setText(String.valueOf(pursuitDoNo) + "/" + String.valueOf(puruistNo));

        pursuitMoney.setText(String.valueOf(pursuitBetMoney * puruistNo) + "元");

        if (winStop == 1) {
            puruistStatusMessage.setText("中奖停追，");
        }
        else if (winStop == 0) {
        }

        time.setText(TimeUtils.stringDate(pursuitBetTime, "yyyy-MM-dd HH:mm:ss"));
        if (pursuitBetStatus == 1) {// 追号已完成
            puruistStatusMessage.setText(puruistStatusMessage.getText() + "追号完成");
            stopPursuit.setEnabled(false);
            stopPursuit.setVisibility(View.GONE);
        }
        else if (pursuitBetStatus == 0) {// 追号进行中
            puruistStatusMessage.setText(puruistStatusMessage.getText() + "进行中...");
        }
        else if (pursuitBetStatus == 2) {// 追号出票失败
            puruistStatusMessage.setText(puruistStatusMessage.getText() + "追号停止");
            stopPursuit.setEnabled(false);
            stopPursuit.setVisibility(View.GONE);
        }
        else if (pursuitBetStatus == 3) {// 追号手动撤单
            puruistStatusMessage.setText(puruistStatusMessage.getText() + "手动停止");
            stopPursuit.setEnabled(false);
            stopPursuit.setVisibility(View.GONE);
        }

        if (lotteryId.equals("dlt")) {
            String betAfter = LotteryCode.split("\\:")[1];
            if (betAfter.equals("2")) {
                lotId.setText(pursuitLotteryTypeName(lotteryId) + "[追加]");
            }
        }

        pha = new PursuitHeaderAdapter(PursuitHistoryDetail.this, LotteryCode, lotteryId);
        pursuitHeadList.setAdapter(pha);

        pursuitDetailList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                data.get(listIndex).put("clickStatus", false);
                data.get(arg2).put("clickStatus", true);
                listIndex = arg2;
                pursuitAdapter.notifyDataSetChanged();

                int winStatu = 2;
                String lotteryOrderId = null;
                String openCodes = null;
                String codes = null;
                String money = null;
                String winMoney = "0.0";
                String betTerm = null;
                try {
                    lotteryOrderId = pursuitBetDetailJsonRecord.get(arg2).getOrderId();
                    openCodes = pursuitBetDetailJsonRecord.get(arg2).getOpenCodes();
                    winStatu = pursuitBetDetailJsonRecord.get(arg2).getWin();
                    winMoney = String.valueOf(pursuitBetDetailJsonRecord.get(arg2).getPrize());
                    money = String.valueOf(pursuitBetMoney);
                    codes = LotteryCode;
                    betTerm = pursuitBetDetailJsonRecord.get(arg2).getTerm();
                }
                catch (Exception e) {
                    lotteryOrderId = null;
                }

                if (lotteryOrderId != null) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("kind", 1);
                    bundle.putString("lottery_id", lotteryId);
                    bundle.putString("term", betTerm);
                    bundle.putString("codes", codes);
                    bundle.putString("money", money);
                    bundle.putString("order_id", lotteryOrderId);
                    bundle.putString("winmoney", winMoney);
                    bundle.putString("prize_status", String.valueOf(winStatu));
                    bundle.putString("opens", openCodes);
                    intent.putExtras(bundle);
                    intent.setClass(PursuitHistoryDetail.this, NormalOrderDetail.class);
                    startActivity(intent);
                }
                else {
                    if (arg2 != 0) {
                        String inf;
                        if (pursuitBetStatus == 2) {
                            inf = "该注出票失败";
                        }
                        else if (pursuitBetStatus == 3) {
                            inf = "追号投注已停止，该投注已注销";
                        }
                        else if (isGet) {
                            inf = "投注停止，解冻金额已返回";
                        }
                        else {
                            inf = "该注尚未出票";
                        }
                        ViewUtil.showTipsToast(PursuitHistoryDetail.this, inf);
                    }
                }
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            PursuitHistoryDetail.this.finish();
        }
        return false;
    }

    class PursuitDetailTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "pursuit_detail");
            parameter.put("pid", LotteryUtils.getPid(PursuitHistoryDetail.this));
            parameter.put("pursuit_id", "" + LotteryPursuitId);
            if (is_stop == true)
                parameter.put("is_stop", "" + is_stop);
            return parameter;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            ConnectService connectNet = new ConnectService(PursuitHistoryDetail.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            String inf;
            String json = (String) result;
            if (result == null) {
                pursuitDetailList.setVisibility(View.GONE);
                message.setText(R.string.search_fail);
                showFailPage.setVisibility(View.VISIBLE);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String str_data = analyse.getData(json, RESPONSE);
                    if (is_stop) {
                        data.clear();
                        pursuitBetStatus = 3;
                        stopPursuit.setEnabled(false);
                        stopPursuit.setText("追号停止成功");
                    }
                    else {
                        getPursuitDetailArray(pursuitBetDetailJsonRecord, str_data);
                    }
                    initRecord(pursuitBetDetailJsonRecord);
                    pursuitAdapter.notifyDataSetChanged();
                    stopPursuit.setEnabled(true);
                }
                else if (status.equals("202")) {
                    if (pursuitBetDetailJsonRecord.size() == 0) {
                        pursuitDetailList.setVisibility(View.GONE);
                        message.setText(noPursuitRecord);
                        showFailPage.setVisibility(View.VISIBLE);
                    }
                    else {
                        inf = noRecord;
                    }
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(PursuitHistoryDetail.this, "追号停止失败");
                }
                else if (status.equals("302")) {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.login_timeout);
                    message.setText(inf);
                    OperateInfUtils.clearSessionId(PursuitHistoryDetail.this);
                    showLoginAgainDialog(inf);
                    showFailPage.setVisibility(View.VISIBLE);
                }
                else if (status.equals("304")) {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.login_again);
                    message.setText(inf);
                    OperateInfUtils.clearSessionId(PursuitHistoryDetail.this);
                    showLoginAgainDialog(inf);
                    showFailPage.setVisibility(View.VISIBLE);
                }
                else {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.search_fail);
                    showFailPage.setVisibility(View.VISIBLE);
                    message.setText(inf);
                }
            }
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

        private void getPursuitDetailArray(ArrayList<PursuitHistoryDetailItem> pursuitBetJsonRecordDetail,
                                           String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    for (int i = 0; i < hallArray.length(); i++) {
                        PursuitHistoryDetailItem history = pursuitBetJsonRecordDetail.get(i);
                        JSONObject jo = hallArray.getJSONObject(i);
                        history.setMoney(jo.getDouble("money"));
                        history.setOpenCodes(jo.getString("open"));
                        history.setOrderId(jo.getString("order_id"));
                        history.setPrize(jo.getDouble("prize"));
                        history.setTerm(jo.getString("term"));
                        history.setWin(jo.getInt("win"));
                        history.setPursuitBuyMode(jo.getInt("buy"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class PursuitBasicTask
        extends AsyncTask<Object, Object, Object> {

        private HashMap<String, String> iniHashMap() {
            LotteryApp appState = ((LotteryApp) PursuitHistoryDetail.this.getApplicationContext());
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "3003170");
            parameter.put("pid", LotteryUtils.getPid(PursuitHistoryDetail.this));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("q_bet", String.valueOf(q_bet));
            parameter.put("q_no", q_no);
            return parameter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object... params) {
            ConnectService connectNet = new ConnectService(PursuitHistoryDetail.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(9, true, iniHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            String inf;
            String json = (String) result;
            if (json == null) {
                pursuitDetailList.setVisibility(View.GONE);
                message.setText(R.string.search_fail);
                showFailPage.setVisibility(View.VISIBLE);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (data.equals("[]") == false) {
                        getPursuitHistoryArray(data);
                        initView();
                        initRecord(pursuitBetDetailJsonRecord);
                        pursuitAdapter.notifyDataSetChanged();
                    }
                    else {
                        ViewUtil.showTipsToast(PursuitHistoryDetail.this, noMoreData);
                    }
                    stopPursuit.setEnabled(true);
                }
                else if (status.equals("202")) {
                    if (pursuitBetDetailJsonRecord.size() == 0) {
                        pursuitDetailList.setVisibility(View.GONE);
                        message.setText(noPursuitRecord);
                        showFailPage.setVisibility(View.VISIBLE);
                    }
                    else {
                        inf = noRecord;
                    }
                }
                else if (status.equals("302")) {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.login_timeout);
                    message.setText(inf);
                    OperateInfUtils.clearSessionId(PursuitHistoryDetail.this);
                    showLoginAgainDialog(inf);
                    showFailPage.setVisibility(View.VISIBLE);
                }
                else if (status.equals("304")) {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.login_again);
                    message.setText(inf);
                    OperateInfUtils.clearSessionId(PursuitHistoryDetail.this);
                    showLoginAgainDialog(inf);
                    showFailPage.setVisibility(View.VISIBLE);
                }
                else {
                    pursuitDetailList.setVisibility(View.GONE);
                    inf = getResources().getString(R.string.search_fail);
                    showFailPage.setVisibility(View.VISIBLE);
                    message.setText(inf);
                }
            }
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

        private void getPursuitHistoryArray(String json) {
            if (json != null) {
                JSONObject hallObject;
                try {
                    hallObject = new JSONObject(json);
                    initPursuitData(hallObject);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void initPursuitData(JSONObject jo) {
            try {
                firstTerm = jo.getString("start_term");
                LotteryCode = jo.getString("codes");
                winStop = jo.getInt("win_stop");
                pursuitBetTime = jo.getString("time");
                pursuitBetMoney = jo.getDouble("money");
                pursuitBetStatus = jo.getInt("status");
                LotteryPursuitId = Integer.valueOf(q_no);
                pursuitDoNo = jo.getInt("do_no");
                puruistNo = jo.getInt("no");
                constructPursuitListData();
                setPursuitRecord(jo.getString("data"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void setPursuitRecord(String data) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    pursuitBetDetailJsonRecord.get(i).setPursuitBuyMode(jsonArray.getJSONObject(i).getInt("buy"));
                    pursuitBetDetailJsonRecord.get(i).setMoney(jsonArray.getJSONObject(i).getDouble("money"));
                    pursuitBetDetailJsonRecord.get(i).setOpenCodes(jsonArray.getJSONObject(i).getString("open"));
                    pursuitBetDetailJsonRecord.get(i).setOrderId(jsonArray.getJSONObject(i).getString("order_id"));
                    pursuitBetDetailJsonRecord.get(i).setPrize(jsonArray.getJSONObject(i).getDouble("prize"));
                    pursuitBetDetailJsonRecord.get(i).setTerm(jsonArray.getJSONObject(i).getString("term"));
                    pursuitBetDetailJsonRecord.get(i).setWin(jsonArray.getJSONObject(i).getInt("win"));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet order details");
        map.put("more_inf", "prusuit");
        FlurryAgent.onEvent("open bet order details", map);
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.bt_stop_pursuit) {
            if (dlgStopPursuit == null) {
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setWarning().setMessage("以下操作将会取消尚未出票的投注,确定停止追号？").setPositiveButton("确  定",
                                                                                                   new DialogInterface.OnClickListener() {
                                                                                                       public void onClick(DialogInterface dialog,
                                                                                                                           int which) {
                                                                                                           dlgStopPursuit.dismiss();
                                                                                                           is_stop =
                                                                                                               true;
                                                                                                           PursuitDetailTask pdt =
                                                                                                               new PursuitDetailTask();
                                                                                                           pdt.execute(1);
                                                                                                       }
                                                                                                   }).setNegativeButton("取  消",
                                                                                                                        new DialogInterface.OnClickListener() {
                                                                                                                            public void onClick(DialogInterface dialog,
                                                                                                                                                int which) {
                                                                                                                                dlgStopPursuit.dismiss();
                                                                                                                            }
                                                                                                                        });
                dlgStopPursuit = customBuilder.create();
                dlgStopPursuit.show();
            }
            else {
                dlgStopPursuit.show();
            }
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet_order_detail";
        MobclickAgent.onEvent(this, eventName, "追号投注");
        besttoneEventCommint(eventName);
    }

    @Override
    public void OnViewChange(int view) {
        if (view < 0 || mCurSel == view) {
            return;
        }
        else if (view > mViewCount - 1) {
            finish();
        }
        setCurPoint(view);
    }

    private void setCurPoint(int index) {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
            return;
        }
        mImageViews[mCurSel].setEnabled(true);
        mImageViews[index].setEnabled(false);
        mCurSel = index;
    }
}
