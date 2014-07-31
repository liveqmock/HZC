package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.MyExpandableListAdapter;
import com.haozan.caipiao.connect.GetOpenHistoryService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.ComparatorOpenHistoryData;
import com.haozan.caipiao.types.OpenHistoryRequest;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.haozan.caipiao.widget.SelectLuckyNumPup;
import com.haozan.caipiao.widget.SelectLuckyNumPup.ButtonClickListener;
import com.umeng.analytics.MobclickAgent;

/**
 * get the bet history from the Internet
 * 
 * @author peter
 */
public class OpenHistory
    extends BasicActivity
    implements ButtonClickListener {
    public static final String SELECTED_NUM_TITLE = "分析号码：";
    protected String noRecord = "抱歉，没有找到你所期望的历史开奖记录";
    // the page of the history
    protected String titleName;
    private CustomExpandleListView lv;
    private TextView title;
    private RelativeLayout check_relative;
    private RelativeLayout select_relative;
    private Button checkAgian;
    private TextView message;
    private ProgressBar firstLoadProgress;
    // private Toast toast;
    private String kind;
    private String q_code;
    private String d_q_code;
    private int no;
    private int size = 10;
    // private OpenHistoryAdapter adapter;
    private MyExpandableListAdapter adapter;
    private ArrayList<OpenHistoryRequest> jsonRecord;
    private ArrayList<ArrayList<OpenHistoryRequest>> doubleListRecord;
    private int page_no = 1;
    private int num = 0;
    private LayoutInflater inflater;
    private TextView topSelectedNum;
    private SelectLuckyNumPup selectLuckyNumPop;
    private int lotteryType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_history_record);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setupViews();
        init();
    }

    private void setupViews() {
        lv = (CustomExpandleListView) this.findViewById(R.id.open_history_list);
        title = (TextView) this.findViewById(R.id.open_history_title);

        checkAgian = (Button) this.findViewById(R.id.check_lucky_num_again);
        check_relative = (RelativeLayout) this.findViewById(R.id.open_check_top_term_layout);
        select_relative = (RelativeLayout) this.findViewById(R.id.open_select_top_term_layout);
        Bundle bundle = this.getIntent().getExtras();
        kind = bundle.getString("kind");
        q_code = bundle.getString("q_code");
        d_q_code = bundle.getString("dispaly_q_code");
        no = bundle.getInt("no");
        if (kind.equals("ssq"))
            titleName = "双色球";
        else if (kind.equals("3d"))
            titleName = "3D";
        else if (kind.equals("qlc"))
            titleName = "七乐彩";
        else if (kind.equals("dfljy"))
            titleName = "东方6+1";
        else if (kind.equals("swxw"))
            titleName = "15选5";
        else if (kind.equals("swxw"))
            titleName = "时时乐";
        else if (kind.equals("dlt"))
            titleName = "大乐透";
        else if (kind.equals("pls"))
            titleName = "排列三";
        else if (kind.equals("plw"))
            titleName = "排列五";

        title.setText(titleName);

        jsonRecord = new ArrayList<OpenHistoryRequest>();

        doubleListRecord = new ArrayList<ArrayList<OpenHistoryRequest>>();
        adapter = new MyExpandableListAdapter(OpenHistory.this, doubleListRecord, kind, inflater, grounpName);
        lv.setAdapter(adapter, R.layout.list_item_open_history_analyse);
        firstLoadProgress = (ProgressBar) this.findViewById(R.id.progress_large);
        message = (TextView) this.findViewById(R.id.history_message);
        topSelectedNum = (TextView) findViewById(R.id.selected_num);
        selectLuckyNumPop = new SelectLuckyNumPup(OpenHistory.this);
        selectLuckyNumPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectLuckyNumPop.setOutsideTouchable(true);
        selectLuckyNumPop.setFocusable(true);
        selectLuckyNumPop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                setSelectLuckyNumProImg("down");
            }
        });
        selectLuckyNumPop.setButtonClickListener(OpenHistory.this);
        checkAgian.setText("  20期  ");
        checkAgian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (LotteryUtils.getPid(OpenHistory.this).equals("101201") == false) {
                    selectLuckyNumPop.showAsDropDown(check_relative);
                    setSelectLuckyNumProImg("up");
                }
            }
        });
        select_relative.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OpenHistory.this.finish();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(OpenHistory.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
                }
            }
        });
    }

    protected void setSelectLuckyNumProImg(String str) {
        if (str.equals("up")) {
            checkAgian.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_white, 0);
        }
        else {
            checkAgian.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_white, 0);
        }

    }

    private void init() {
        getDataFromServe(1);
        if (kind.equals("dfljy")) {
            String[] dfljyNum = d_q_code.split("\\|");
            if (dfljyNum.length == 2) {
                if (dfljyNum[0].equals(",,,,,"))
                    topSelectedNum.setText(SELECTED_NUM_TITLE +
                        new LotteryUtils().animals[Integer.valueOf(dfljyNum[1]) - 1]);
                else {
                    String[] norNum = dfljyNum[0].split("\\,");
                    StringBuilder norNumSb = new StringBuilder();
                    for (int i = 0; i < norNum.length; i++) {
                        if (!norNum[i].equals("")) {
                            norNumSb.append(norNum[i]);
                            norNumSb.append(",");
                        }
                    }
                    norNumSb.delete(norNumSb.length() - 1, norNumSb.length());
                    topSelectedNum.setText(SELECTED_NUM_TITLE + norNumSb.toString() + "|" +
                        new LotteryUtils().animals[Integer.valueOf(dfljyNum[1]) - 1]);
                }
            }
            else {
                String[] normalNum = d_q_code.split("\\,");
                StringBuilder normalNumsb = new StringBuilder();
                for (int i = 0; i < normalNum.length; i++) {
                    if (!normalNum[i].equals("")) {
                        normalNumsb.append(normalNum[i]);
                        normalNumsb.append(",");
                    }
                }
                normalNumsb.deleteCharAt(normalNumsb.length() - 1);
                topSelectedNum.setText(SELECTED_NUM_TITLE + normalNumsb.toString());
            }
        }
        else {
            topSelectedNum.setText(SELECTED_NUM_TITLE + rebuiltString(d_q_code));
        }
    }

    private String rebuiltString(String str) {
        StringBuilder str_code = new StringBuilder();
        String[] code = str.split("\\,");
        for (int i = 0; i < code.length; i++) {
            if (!code[i].equals("")) {
                str_code.append(code[i]);
                str_code.append(",");
            }
        }
        str_code.delete(str_code.length() - 1, str_code.length());
        return str_code.toString();
    }

    class GetRequestOpenHistoryTask
        extends AsyncTask<Object, Object, Object> {
        private Context context;

        public GetRequestOpenHistoryTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            GetOpenHistoryService getUerHistory =
                new GetOpenHistoryService(context, kind, q_code, no, page_no, size);
            String json = getUerHistory.sending();
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            String inf = null;
            if (jsonRecord.size() == 0) {
                firstLoadProgress.setVisibility(View.GONE);
            }
            String json = (String) result;
            if (json == null) {
                if (jsonRecord.size() == 0) {
                    adapter.notifyDataSetChanged();
                    if (page_no < 2) {
                        lv.setVisibility(View.GONE);
                        message.setText(R.string.search_fail);
                        message.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    inf = getResources().getString(R.string.search_fail);
                }
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    if (data.equals("[]") == false) {
                        jsonRecord.clear();
                        setOpenHistoryData(jsonRecord, data);
                        getSameListItem();
                        if (no == 50) {
                            ViewUtil.showTipsToast(OpenHistory.this, "已扣除3积分");
                        }
                        else if (no == 100) {
                            ViewUtil.showTipsToast(OpenHistory.this, "已扣除5积分");
                        }
                        else if (no == 200) {
                            ViewUtil.showTipsToast(OpenHistory.this, "已扣除10积分");
                        }
                        else if (no == 300) {
                            ViewUtil.showTipsToast(OpenHistory.this, "已扣除15积分");
                        }
                        else if (no == 500) {
                            ViewUtil.showTipsToast(OpenHistory.this, "已扣除20积分");
                        }
                    }
                    firstLoadProgress.setVisibility(View.GONE);
                    lv.refreshContent();
                    adapter.notifyDataSetChanged();
                    lv.expandList();
                }
                else if (status.equals("202")) {
                    if (jsonRecord.size() == 0) {
                        adapter.notifyDataSetChanged();
                        if (page_no < 2) {
                            lv.setVisibility(View.GONE);
                            message.setText(noRecord);
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        inf = noRecord;
                        if (page_no < 2) {
                            lv.setVisibility(View.GONE);
                            message.setText(noRecord);
                            message.setVisibility(View.VISIBLE);
                        }
                        message.setText("没您想要查询的数据");
                        message.setVisibility(View.VISIBLE);
                    }
                }
                else if (status.equals("302")) {
                    inf = getResources().getString(R.string.login_timeout);
                    if (jsonRecord.size() == 0) {
                        adapter.notifyDataSetChanged();
                        OperateInfUtils.clearSessionId(OpenHistory.this);
                        message.setText(inf);
                        message.setVisibility(View.VISIBLE);
                    }
                    else {
                        OperateInfUtils.clearSessionId(OpenHistory.this);
                    }
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("304")) {
                    inf = getResources().getString(R.string.login_again);
                    if (jsonRecord.size() == 0) {
                        adapter.notifyDataSetChanged();
                        OperateInfUtils.clearSessionId(OpenHistory.this);
                        message.setText(inf);
                        message.setVisibility(View.VISIBLE);
                    }
                    else {
                        OperateInfUtils.clearSessionId(OpenHistory.this);
                    }
                    showLoginAgainDialog(inf);
                }
                else if (status.equals("300")) {
                    if (page_no < 2) {
                        lv.setVisibility(View.GONE);
                        message.setText("您的积分不足");
                        message.setVisibility(View.VISIBLE);
                    }
                    else {
                        ViewUtil.showTipsToast(OpenHistory.this, "您的积分不足");
                    }
                }
                else {
                    if (jsonRecord.size() == 0) {
                        adapter.notifyDataSetChanged();
                        if (page_no < 2) {
                            lv.setVisibility(View.GONE);
                            message.setText(R.string.search_fail);
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        inf = getResources().getString(R.string.search_fail);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    private void setOpenHistoryData(ArrayList<OpenHistoryRequest> historyAll, String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                OpenHistoryRequest temp = new OpenHistoryRequest();
                temp.setTerm(jo.getString("term"));
// temp.setOpenDate(jo.getString("date"));
                temp.setOpenDate(jo.getString("term_date"));
// if (jo.getString("s_no") != null)
                temp.setSpecialNum(jo.getString("s_no"));
                temp.setNormalNum(jo.getString("n_no"));
                temp.setType(jo.getString("type"));
                historyAll.add(temp);
            }
            ComparatorOpenHistoryData comparator = new ComparatorOpenHistoryData();
            Collections.sort(historyAll, comparator);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LotteryApp appState = ((LotteryApp) getApplicationContext());
        Map<String, String> map = new HashMap<String, String>();
        map.put("zzc_desc", "username [" + appState.getUsername() + "]: open lottery check history num");
        FlurryAgent.onEvent("user open lottery check history num", map);
    }

    ArrayList<OpenHistoryRequest> jsonRecord1 = new ArrayList<OpenHistoryRequest>();
    private boolean haveNext = false;
    private String[] grounpName = {""};

    private void getSameListItem() {
        jsonRecord1.clear();
        doubleListRecord.clear();
        jsonRecord1.add(jsonRecord.get(0));
        haveNext = true;
        if (jsonRecord.size() == 1) {
            doubleListRecord.add(jsonRecord1);
        }

        for (int i = num + 1; i < jsonRecord.size(); i++) {
            if (jsonRecord.get(num).getType().equals(jsonRecord.get(i).getType())) {
                jsonRecord1.add(jsonRecord.get(i));
                haveNext = true;
                if (i == (jsonRecord.size() - 1)) {
                    doubleListRecord.add(jsonRecord1);
                }
            }
            if (!jsonRecord.get(num).getType().equals(jsonRecord.get(i).getType())) {
                if (haveNext) {
                    doubleListRecord.add(jsonRecord1);
                    haveNext = false;
                }
                jsonRecord1 = new ArrayList<OpenHistoryRequest>();
                jsonRecord1.add(jsonRecord.get(i));
                num = i;
                if (i == (jsonRecord.size() - 1)) {
                    doubleListRecord.add(jsonRecord1);
                }
            }
        }
    }

    @Override
    public void setButtonClickListener(int buttonId) {

        if (buttonId == R.id.item_first_layout) {
            checkAgian.setText("  20期  ");
            no = 20;
            haveNext = false;
            num = 0;
            getDataFromServe(1);
            if (lotteryType != 1) {
                lotteryType = 1;
                getData();
            }
            selectLuckyNumPop.dismiss();
        }
        else if (buttonId == R.id.item_second_layout) {
            selectLuckyNumPop.dismiss();
            if (lotteryType != 2) {
                lotteryType = 2;
                getData();
            }
        }
        else if (buttonId == R.id.item_third_layout) {
            if (lotteryType != 3) {
                lotteryType = 3;
                getData();
            }
            selectLuckyNumPop.dismiss();
        }
        else if (buttonId == R.id.item_forth_layout) {
            if (lotteryType != 4) {
                lotteryType = 4;
                getData();
            }
            selectLuckyNumPop.dismiss();
        }
        else if (buttonId == R.id.item_fifth_layout) {
            if (lotteryType != 5) {
                lotteryType = 5;
                getData();
            }
            selectLuckyNumPop.dismiss();
        }
        else if (buttonId == R.id.item_sixth_layout) {
            if (lotteryType != 6) {
                lotteryType = 6;
                getData();
            }
            selectLuckyNumPop.dismiss();

        }
    }

    private int[] termArray = {20, 50, 100, 200, 300, 500};
    private String[] termStrArray = {"20期", "50期", "100期", "200期", "300期", "500期"};

    private void forwardTologin() {
        ViewUtil.showTipsToast(this, "请登录");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", "号码分析");
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
        intent.setClass(OpenHistory.this, Login.class);
// intent.setClass(OpenHistory.this, StartUp.class);
        startActivityForResult(intent, 10);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            (new AnimationModel(OpenHistory.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                             R.anim.push_to_right_out);
        }
    }

    private void getDataFromServe(int type) {
        if (HttpConnectUtil.isNetworkAvailable(OpenHistory.this)) {
            selectLuckyNumPop.iniSelectedItemBg();
            selectLuckyNumPop.init(type);
            firstLoadProgress.setVisibility(View.VISIBLE);
            GetRequestOpenHistoryTask task = new GetRequestOpenHistoryTask(OpenHistory.this);
            task.execute();
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            lv.setVisibility(View.GONE);
            message.setText(inf);
            message.setVisibility(View.VISIBLE);
        }
    }

// CustomDialog loginAgainDialog;

// private void initDialog(String message) {
// loginAgainDialog = new CustomDialog(OpenHistory.this, 2);
// loginAgainDialog.setTitle("注意");
// loginAgainDialog.setTextContent(message);
// loginAgainDialog.setFirstString("确定");
// loginAgainDialog.setSecondString("取消");
// loginAgainDialog.setActionListener(OpenHistory.this);
// loginAgainDialog.show();
// }

// @Override
// public void onActionFirstClick(int flag) {
// if (flag == 2) {
// getData();
// }
//
// }

    private void getData() {
        if (appState.getSessionid() != null) {
            getDataFromSer();
// loginAgainDialog.dismiss();

        }
        else {
            forwardTologin();
// loginAgainDialog.dismiss();
        }
    }

// @Override
// public void onActionSecondClick(int flag) {
// if (flag == 2) {
// // loginAgainDialog.dismiss();
// }
// }

    private void getDataFromSer() {
        checkAgian.setText(termStrArray[lotteryType - 1]);
        no = termArray[lotteryType - 1];
        haveNext = false;
        num = 0;
        getDataFromServe(lotteryType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 11) {
            getDataFromSer();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void submitData() {
        String eventName = "open_analyse_open_term";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            OpenHistory.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(OpenHistory.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                 R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
