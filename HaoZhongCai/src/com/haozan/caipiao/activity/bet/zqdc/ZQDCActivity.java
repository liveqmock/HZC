package com.haozan.caipiao.activity.bet.zqdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.BetPaySports;
import com.haozan.caipiao.adapter.BetHistoryTypeSelectionAdapter;
import com.haozan.caipiao.adapter.SportsJCZQBQCAdapter;
import com.haozan.caipiao.adapter.SportsJCZQSPFAdapter;
import com.haozan.caipiao.adapter.SportsJCZQZJQAdapter;
import com.haozan.caipiao.adapter.SportsZQDCBFAdapter;
import com.haozan.caipiao.adapter.SportsZQDCSXDSAdapter;
import com.haozan.caipiao.adapter.SportsJCZQSPFAdapter.OnBallClickListener;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;

public class ZQDCActivity
    extends ZQDCBasicActivity
    implements OnBallClickListener, com.haozan.caipiao.adapter.SportsJCZQZJQAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsJCZQBQCAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsZQDCSXDSAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsZQDCBFAdapter.OnBallClickListener {
    private static final int MIN_SELECTED = 2;
    private static final int MAX_SELECTED = 15;

    private int selectedNum = 0;
    protected long endTimeMillis = 0;

    private SportsJCZQSPFAdapter spfadapter;// 胜平负
    private SportsJCZQZJQAdapter zjqadapter;// 总进球
    private SportsJCZQBQCAdapter bqcadapter;// 半全场
    private SportsZQDCSXDSAdapter sxdsadapter;// 上下单场
    private SportsZQDCBFAdapter bfadapter;// 比分
    private ArrayList<SportsSFCBetGroup> spfmatchList;
    private ArrayList<SportsSFCBetGroup> spfmatchListSub;
    private ArrayList<SportsSFCBetGroup> zjqmatchList;
    private ArrayList<SportsSFCBetGroup> zjqmatchListSub;
    private ArrayList<SportsSFCBetGroup> bqcmatchList;
    private ArrayList<SportsSFCBetGroup> bqcmatchListSub;
    private ArrayList<SportsSFCBetGroup> sxdsmatchList;
    private ArrayList<SportsSFCBetGroup> sxdsmatchListSub;
    private ArrayList<SportsSFCBetGroup> bfmatchList;
    private ArrayList<SportsSFCBetGroup> bfmatchListSub;
    private ArrayList<SportsItem> spfbetList;
    private ArrayList<SportsItem> zjqbetList;
    private ArrayList<SportsItem> bqcbetList;
    private ArrayList<SportsItem> sxdsbetList;
    private ArrayList<SportsItem> bfbetList;

    private GridView menuGidView;
    private RelativeLayout termLayout;
    private PopupWindow mPopupWindow;
    // add by vincent
    private PopupWindow betwayPop;

    private BetHistoryTypeSelectionAdapter betHisTypeSeleAdapter;
    private Button leagueFilter;
    private String[] buttonNameArray;
    public static Map<String, String> jcLeague;
    private boolean isClickable = false;
    private int index = 0;
    private int[] bqcLot = new int[] {33, 31, 30, 13, 11, 10, 03, 01, 00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpView();
        init();
    }

    private void setUpView() {
        // add by vincent
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);

        leagueFilter = (Button) findViewById(R.id.league_filter);
        leagueFilter.setText("全部");
        leagueFilter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isClickable) {
                    mapToArray();
                    InitPopupViews(R.layout.pop_grid_view, buttonNameArray);
                    mPopupWindow.showAsDropDown(leagueFilter);
                }
                else {
                    ViewUtil.showTipsToast(ZQDCActivity.this,"请稍等");
                }
            }
        });
    }

    private void mapToArray() {
        int buttonNameArrayLength = jcLeague.size() + 1;
        int yu = buttonNameArrayLength % 4;
        if (yu != 0) {
            buttonNameArrayLength = buttonNameArrayLength + (4 - yu);
            for (int i = buttonNameArrayLength - (4 - yu); i < buttonNameArrayLength; i++)
                jcLeague.put(String.valueOf(i), "");
        }
        String[] buttonNameArraySec = new String[jcLeague.size()];
        jcLeague.values().toArray(buttonNameArraySec);
        buttonNameArraySec = rebuildStringArray(buttonNameArraySec).split("\\,");
        buttonNameArray = new String[buttonNameArrayLength];
        buttonNameArray[0] = "全部";
        for (int i = 0; i < buttonNameArrayLength - 1; i++)
            if (i == 0) {
                buttonNameArray[1] = buttonNameArraySec[i];
            }
            else {
                if (buttonNameArraySec[i].equals("-"))
                    buttonNameArray[i + 1] = "";
                else
                    buttonNameArray[i + 1] = buttonNameArraySec[i];
            }
    }

    private String rebuildStringArray(String[] buttonNameArraySec) {
        StringBuilder strB1 = new StringBuilder();
        StringBuilder strB2 = new StringBuilder();
        for (int i = 0; i < buttonNameArraySec.length; i++) {
            if (buttonNameArraySec[i].equals("")) {
                strB1.append("-");
                strB1.append(",");
            }
            else {
                strB2.append(buttonNameArraySec[i]);
                strB2.append(",");
            }
        }
        if (!strB1.toString().equals(""))
            strB1.delete(strB1.length() - 1, strB1.length());
        if (!strB2.toString().equals(""))
            strB2.delete(strB2.length() - 1, strB2.length());
        if (!strB1.toString().equals(""))
            return strB2.toString() + "," + strB1.toString();
        else
            return strB2.toString();
    }

// private void initHashMap() {
// jcLeague.clear();
// for (int i = 0; i < 10; i++) {
// jcLeague.put(String.valueOf(i), String.valueOf(i));
// }
// }

    private void init() {
        jcLeague = new HashMap<String, String>();
// initHashMap();
        spfmatchList = new ArrayList<SportsSFCBetGroup>();
        spfmatchListSub = new ArrayList<SportsSFCBetGroup>();
        zjqmatchList = new ArrayList<SportsSFCBetGroup>();
        zjqmatchListSub = new ArrayList<SportsSFCBetGroup>();
        bqcmatchList = new ArrayList<SportsSFCBetGroup>();
        bqcmatchListSub = new ArrayList<SportsSFCBetGroup>();
        sxdsmatchList = new ArrayList<SportsSFCBetGroup>();
        sxdsmatchListSub = new ArrayList<SportsSFCBetGroup>();
        bfmatchList = new ArrayList<SportsSFCBetGroup>();
        bfmatchListSub = new ArrayList<SportsSFCBetGroup>();

        // add by vincent
        spfbetList = new ArrayList<SportsItem>();
        spfadapter = new SportsJCZQSPFAdapter(ZQDCActivity.this, spfmatchListSub);
        spfadapter.setClickListener(this);
        zjqbetList = new ArrayList<SportsItem>();
        zjqadapter = new SportsJCZQZJQAdapter(ZQDCActivity.this, zjqmatchListSub);
        zjqadapter.setClickListener(this);
        bqcbetList = new ArrayList<SportsItem>();
        bqcadapter = new SportsJCZQBQCAdapter(ZQDCActivity.this, bqcmatchListSub);
        bqcadapter.setClickListener(this);
        sxdsbetList = new ArrayList<SportsItem>();
        sxdsadapter = new SportsZQDCSXDSAdapter(ZQDCActivity.this, sxdsmatchListSub);
        sxdsadapter.setClickListener(this);
        bfbetList = new ArrayList<SportsItem>();
        bfadapter = new SportsZQDCBFAdapter(ZQDCActivity.this, bfmatchListSub);
        bfadapter.setClickListener(this);

        if (lotteryType == 1) {
            lv.setAdapter(spfadapter, R.layout.sports_history_group_view);
        }
        else if (lotteryType == 2) {
            lv.setAdapter(zjqadapter, R.layout.sports_history_group_view);
        }
        else if (lotteryType == 3) {
            lv.setAdapter(bqcadapter, R.layout.sports_history_group_view);
        }
        else if (lotteryType == 4) {
            lv.setAdapter(sxdsadapter, R.layout.sports_history_group_view);
        }
        else if (lotteryType == 5) {
            lv.setAdapter(bfadapter, R.layout.sports_history_group_view);
        }

        betInf.setText("请选择2-15场比赛");
        // TODO
        kind = "zqdc";
        getLotteryInfo();

        // changed by vincent 12.08.06
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            betCode = bundle.getString("bet_code");
        }
    }

    private void getLotteryInfo() {
        if (HttpConnectUtil.isNetworkAvailable(ZQDCActivity.this)) {
            GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
            getLotteryInf.execute(kind, getType(), "1");
        }
        else {
            String inf = getResources().getString(R.string.network_not_avaliable);
            ViewUtil.showTipsToast(this,inf);
        }
    }

    //TODO
    public String getType() {
        if (lotteryType == 1) {
            return "";
        }
        else if (lotteryType == 2) {
            return "";
        }
        else if (lotteryType == 3) {
            return "";
        }
        else if (lotteryType == 4) {
            return "";
        }
        else if (lotteryType == 5) {
            return "";
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopView();
        }
    }

    @Override
    protected void showPopView() {
        super.showPopView();
        LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mInflater.inflate(R.layout.zqdc_bet_way_select, null);

        TextView zqdcSpf = (TextView) waySwitchLayout.findViewById(R.id.zqdc_spf);
        initDefault(waySwitchLayout);
        zqdcSpf.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                betwayPop.dismiss();
                if (lotteryType != 1) {
                    databaseData.putString("zqdc_way", "zqdc_spf");
                    databaseData.commit();
                    clear();

                    lv.setAdapter(spfadapter, R.layout.sports_history_group_view);
                    lotteryType = 1;
                    title.setText("足球单场（胜平负）");
                    if (spfmatchList.size() == 0) {
                        getLotteryInfo();
                    }
                    if (index != 0) {
                        initFilter(buttonNameArray[index].trim());
                    }
                    else {
                        spfmatchListSub.clear();
                        for (int i = 0; i < spfmatchList.size(); i++) {
                            spfmatchListSub.add(spfmatchList.get(i));
                        }
                    }
                    lv.refreshContent();
                    spfadapter.notifyDataSetChanged();
                    lv.expandList();
                }
            }
        });

        TextView zqdcZjq = (TextView) waySwitchLayout.findViewById(R.id.zqdc_zjq);
        initDefault(waySwitchLayout);
        zqdcZjq.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                betwayPop.dismiss();
                if (lotteryType != 2) {
                    databaseData.putString("zqdc_way", "zqdc_zjq");
                    databaseData.commit();
                    clear();
                    lv.setAdapter(zjqadapter, R.layout.sports_history_group_view);
                    lotteryType = 2;
                    title.setText("足球单场（总进球）");
                    if (zjqmatchList.size() == 0) {
                        getLotteryInfo();
                    }
                    if (index != 0) {
                        initFilter(buttonNameArray[index].trim());
                    }
                    else {
                        zjqmatchListSub.clear();
                        for (int i = 0; i < zjqmatchList.size(); i++) {
                            zjqmatchListSub.add(zjqmatchList.get(i));
                        }
                    }
                    lv.refreshContent();
                    zjqadapter.notifyDataSetChanged();
                    lv.expandList();
                }
            }
        });

        TextView zqdcBqc = (TextView) waySwitchLayout.findViewById(R.id.zqdc_bqc);
        zqdcBqc.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                betwayPop.dismiss();
                if (lotteryType != 3) {
                    databaseData.putString("zqdc_way", "zqdc_bqc");
                    databaseData.commit();
                    clear();
                    lv.setAdapter(bqcadapter, R.layout.sports_history_group_view);
                    lotteryType = 3;
                    title.setText("足球单场（半全场）");
                    if (bqcmatchList.size() == 0) {
                        getLotteryInfo();
                    }
                    if (index != 0) {
                        initFilter(buttonNameArray[index].trim());
                    }
                    else {
                        bqcmatchListSub.clear();
                        for (int i = 0; i < bqcmatchList.size(); i++) {
                            bqcmatchListSub.add(bqcmatchList.get(i));
                        }
                    }
                    lv.refreshContent();
                    bqcadapter.notifyDataSetChanged();
                    lv.expandList();
                }
            }
        });
        TextView zqdcSxds = (TextView) waySwitchLayout.findViewById(R.id.zqdc_sxds);
        zqdcSxds.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                betwayPop.dismiss();
                if (lotteryType != 4) {
                    databaseData.putString("zqdc_way", "zqdc_sxds");
                    databaseData.commit();
                    clear();
                    lv.setAdapter(sxdsadapter, R.layout.sports_history_group_view);
                    lotteryType = 4;
                    title.setText("足球单场（上下单双）");
                    if (sxdsmatchList.size() == 0) {
                        getLotteryInfo();
                    }
                    if (index != 0) {
                        initFilter(buttonNameArray[index].trim());
                    }
                    else {
                        sxdsmatchListSub.clear();
                        for (int i = 0; i < sxdsmatchList.size(); i++) {
                            sxdsmatchListSub.add(sxdsmatchList.get(i));
                        }
                    }
                    lv.refreshContent();
                    sxdsadapter.notifyDataSetChanged();
                    lv.expandList();
                }
            }
        });
        
        TextView zqdcBf = (TextView) waySwitchLayout.findViewById(R.id.zqdc_bf);
        zqdcBf.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                betwayPop.dismiss();
                if (lotteryType != 5) {
                    databaseData.putString("zqdc_way", "zqdc_bf");
                    databaseData.commit();
                    clear();
                    lv.setAdapter(bfadapter, R.layout.sports_history_group_view);
                    lotteryType = 5;
                    title.setText("足球单场（比分）");
                    if (bfmatchList.size() == 0) {
                        getLotteryInfo();
                    }
                    if (index != 0) {
                        initFilter(buttonNameArray[index].trim());
                    }
                    else {
                        bfmatchListSub.clear();
                        for (int i = 0; i < bfmatchList.size(); i++) {
                            bfmatchListSub.add(bfmatchList.get(i));
                        }
                    }
                    lv.refreshContent();
                    bfadapter.notifyDataSetChanged();
                    lv.expandList();
                }
            }
        });
        
        iniSelectedItemBg();
        initIcon(lotteryType);
        betwayPop = new PopupWindow(this);
        betwayPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        betwayPop.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        betwayPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        betwayPop.setOutsideTouchable(true);
        betwayPop.setFocusable(true);
        betwayPop.setContentView(waySwitchLayout);
        betwayPop.showAsDropDown(termLayout);
    }

    @Override
    public void showWay() {
        if (lotteryType == 1) {
            if (index != 0) {
                initFilter(buttonNameArray[index].trim());
            }
            else {
                spfmatchListSub.clear();
                for (int i = 0; i < spfmatchList.size(); i++) {
                    spfmatchListSub.add(spfmatchList.get(i));
                }
            }
            lv.refreshContent();
            spfadapter.notifyDataSetChanged();
            lv.expandList();
        }
        else if (lotteryType == 2) {
            if (index != 0) {
                initFilter(buttonNameArray[index].trim());
            }
            else {
                zjqmatchListSub.clear();
                for (int i = 0; i < zjqmatchList.size(); i++) {
                    zjqmatchListSub.add(zjqmatchList.get(i));
                }
            }
            lv.refreshContent();
            zjqadapter.notifyDataSetChanged();
            lv.expandList();
        }
        else if (lotteryType == 3) {
            if (index != 0) {
                initFilter(buttonNameArray[index].trim());
            }
            else {
                bqcmatchListSub.clear();
                for (int i = 0; i < bqcmatchList.size(); i++) {
                    bqcmatchListSub.add(bqcmatchList.get(i));
                }
            }
            lv.refreshContent();
            bqcadapter.notifyDataSetChanged();
            lv.expandList();
        }
        else if (lotteryType == 4) {
            if (index != 0) {
                initFilter(buttonNameArray[index].trim());
            }
            else {
                sxdsmatchListSub.clear();
                for (int i = 0; i < sxdsmatchList.size(); i++) {
                    sxdsmatchListSub.add(sxdsmatchList.get(i));
                }
            }
            lv.refreshContent();
            sxdsadapter.notifyDataSetChanged();
            lv.expandList();
        }
        else if (lotteryType == 5) {
            if (index != 0) {
                initFilter(buttonNameArray[index].trim());
            }
            else {
                bfmatchListSub.clear();
                for (int i = 0; i < bfmatchList.size(); i++) {
                    bfmatchListSub.add(bfmatchList.get(i));
                }
            }
            lv.refreshContent();
            bfadapter.notifyDataSetChanged();
            lv.expandList();
        }
    }

    private void InitPopupViews(int layout, String[] textArray) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(layout, null);
        betHisTypeSeleAdapter = new BetHistoryTypeSelectionAdapter(ZQDCActivity.this, textArray, index,R.layout.bet_method_select_item);
        menuGidView = (GridView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
        menuGidView.setAdapter(betHisTypeSeleAdapter);
        griViewClick();
        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(waySwitchLayout);
    }

    private void griViewClick() {
        menuGidView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!buttonNameArray[arg2].equals("")) {
                    mPopupWindow.dismiss();

                    TextView t_v = (TextView) arg0.getChildAt(index).findViewById(R.id.grid_view_item_click);
                    t_v.setTextColor(Color.BLACK);
                    t_v.setBackgroundResource(R.drawable.bet_pop_nomral);
                    TextView tv = (TextView) arg0.getChildAt(arg2).findViewById(R.id.grid_view_item_click);
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    index = arg2;
                    leagueFilter.setText(buttonNameArray[arg2]);
                    if (arg2 != 0) {
                        initFilter(buttonNameArray[arg2].trim());
                    }
                    else {
                        if (lotteryType == 1) {
                            spfmatchListSub.clear();
                            for (int i = 0; i < spfmatchList.size(); i++) {
                                spfmatchListSub.add(spfmatchList.get(i));
                            }
                        }
                        else if (lotteryType == 2) {
                            zjqmatchListSub.clear();
                            for (int i = 0; i < zjqmatchList.size(); i++) {
                                zjqmatchListSub.add(zjqmatchList.get(i));
                            }
                        }
                        else if (lotteryType == 3) {
                            bqcmatchListSub.clear();
                            for (int i = 0; i < bqcmatchList.size(); i++) {
                                bqcmatchListSub.add(bqcmatchList.get(i));
                            }
                        }
                        else if (lotteryType == 4) {
                            sxdsmatchListSub.clear();
                            for (int i = 0; i < sxdsmatchList.size(); i++) {
                                sxdsmatchListSub.add(sxdsmatchList.get(i));
                            }
                        }
                        else if (lotteryType == 5) {
                            bfmatchListSub.clear();
                            for (int i = 0; i < bfmatchList.size(); i++) {
                                bfmatchListSub.add(bfmatchList.get(i));
                            }
                        }
                    }
// mPopupWindow.dismiss();
                    lv.refreshContent();
                    notifyDataSetChange();
                }
            }
        });
    }

    private void initFilter(String league) {
        boolean isHaveCertainLeague = false;
        if (lotteryType == 1) {
            spfmatchListSub.clear();
            for (int i = 0; i < spfmatchList.size(); i++) {
                int gameNum = 0;
                SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                for (int j = 0; j < spfmatchList.get(i).getSpfitemList().size(); j++) {
                    String leagueType = spfmatchList.get(i).getSpfitemList().get(j).getLeague();
                    if (leagueType.equals(league)) {
                        sportsSFCBetGroup.setSpfitemList(getArrayList(spfmatchList.get(i).getSpfitemList(),
                                                                      league));
                        isHaveCertainLeague = true;
                        gameNum++;
                    }
                }
                if (isHaveCertainLeague) {
                    sportsSFCBetGroup.setDate(spfmatchList.get(i).getDate());
                    sportsSFCBetGroup.setDay(spfmatchList.get(i).getDay());
                    sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                    spfmatchListSub.add(sportsSFCBetGroup);
                    isHaveCertainLeague = false;
                }
            }
        }
        else if (lotteryType == 2) {
            zjqmatchListSub.clear();
            for (int i = 0; i < zjqmatchList.size(); i++) {
                int gameNum = 0;
                SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                for (int j = 0; j < zjqmatchList.get(i).getZjqitemList().size(); j++) {
                    String leagueType = zjqmatchList.get(i).getZjqitemList().get(j).getLeague();
                    if (leagueType.equals(league)) {
                        sportsSFCBetGroup.setZjqitemList(getZjqArrayList(zjqmatchList.get(i).getZjqitemList(),
                                                                         league));
                        isHaveCertainLeague = true;
                        gameNum++;
                    }
                }
                if (isHaveCertainLeague) {
                    sportsSFCBetGroup.setDate(zjqmatchList.get(i).getDate());
                    sportsSFCBetGroup.setDay(zjqmatchList.get(i).getDay());
                    sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                    zjqmatchListSub.add(sportsSFCBetGroup);
                    isHaveCertainLeague = false;
                }
            }
        }
        else if (lotteryType == 3) {
            bqcmatchListSub.clear();
            for (int i = 0; i < bqcmatchList.size(); i++) {
                int gameNum = 0;
                SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                for (int j = 0; j < bqcmatchList.get(i).getBqcitemList().size(); j++) {
                    String leagueType = bqcmatchList.get(i).getBqcitemList().get(j).getLeague();
                    if (leagueType.equals(league)) {
                        sportsSFCBetGroup.setBqcitemList(getBqcArrayList(bqcmatchList.get(i).getBqcitemList(),
                                                                         league));
                        isHaveCertainLeague = true;
                        gameNum++;
                    }
                }
                if (isHaveCertainLeague) {
                    sportsSFCBetGroup.setDate(bqcmatchList.get(i).getDate());
                    sportsSFCBetGroup.setDay(bqcmatchList.get(i).getDay());
                    sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                    bqcmatchListSub.add(sportsSFCBetGroup);
                    isHaveCertainLeague = false;
                }
            }
        }
        else if (lotteryType == 4) {
            sxdsmatchListSub.clear();
            for (int i = 0; i < sxdsmatchList.size(); i++) {
                int gameNum = 0;
                SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                for (int j = 0; j < sxdsmatchList.get(i).getZQDCSxdsitemList().size(); j++) {
                    String leagueType = sxdsmatchList.get(i).getZQDCSxdsitemList().get(j).getLeague();
                    if (leagueType.equals(league)) {
                        sportsSFCBetGroup.setZQDCSxdsitemList(getSxdsArrayList(sxdsmatchList.get(i).getZQDCSxdsitemList(),
                                                                         league));
                        isHaveCertainLeague = true;
                        gameNum++;
                    }
                }
                if (isHaveCertainLeague) {
                    sportsSFCBetGroup.setDate(sxdsmatchList.get(i).getDate());
                    sportsSFCBetGroup.setDay(sxdsmatchList.get(i).getDay());
                    sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                    sxdsmatchListSub.add(sportsSFCBetGroup);
                    isHaveCertainLeague = false;
                }
            }
        }
        else if (lotteryType == 5) {
            bfmatchListSub.clear();
            for (int i = 0; i < bfmatchList.size(); i++) {
                int gameNum = 0;
                SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                for (int j = 0; j < bfmatchList.get(i).getZQDCBfitemList().size(); j++) {
                    String leagueType = bfmatchList.get(i).getZQDCBfitemList().get(j).getLeague();
                    if (leagueType.equals(league)) {
                        sportsSFCBetGroup.setZQDCBfitemList(getBfArrayList(bfmatchList.get(i).getZQDCBfitemList(),
                                                                         league));
                        isHaveCertainLeague = true;
                        gameNum++;
                    }
                }
                if (isHaveCertainLeague) {
                    sportsSFCBetGroup.setDate(bfmatchList.get(i).getDate());
                    sportsSFCBetGroup.setDay(bfmatchList.get(i).getDay());
                    sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                    bfmatchListSub.add(sportsSFCBetGroup);
                    isHaveCertainLeague = false;
                }
            }
        }
    }

    private ArrayList<SportsItem> getArrayList(ArrayList<SportsItem> sportsSPFList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsSPFList.size(); i++) {
            if (leagueName.equals(sportsSPFList.get(i).getLeague())) {
                sportLeagueArray.add(sportsSPFList.get(i));
            }
        }
        return sportLeagueArray;
    }

    private ArrayList<SportsItem> getZjqArrayList(ArrayList<SportsItem> sportsZJQList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsZJQList.size(); i++) {
            if (leagueName.equals(sportsZJQList.get(i).getLeague())) {
                sportLeagueArray.add(sportsZJQList.get(i));
            }
        }
        return sportLeagueArray;
    }

    private ArrayList<SportsItem> getBqcArrayList(ArrayList<SportsItem> sportsBQCList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsBQCList.size(); i++) {
            if (leagueName.equals(sportsBQCList.get(i).getLeague())) {
                sportLeagueArray.add(sportsBQCList.get(i));
            }
        }
        return sportLeagueArray;
    }
    
    private ArrayList<SportsItem> getSxdsArrayList(ArrayList<SportsItem> sportsSXDSList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsSXDSList.size(); i++) {
            if (leagueName.equals(sportsSXDSList.get(i).getLeague())) {
                sportLeagueArray.add(sportsSXDSList.get(i));
            }
        }
        return sportLeagueArray;
    }
    
    private ArrayList<SportsItem> getBfArrayList(ArrayList<SportsItem> sportsBFList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsBFList.size(); i++) {
            if (leagueName.equals(sportsBFList.get(i).getLeague())) {
                sportLeagueArray.add(sportsBFList.get(i));
            }
        }
        return sportLeagueArray;
    }

    @Override
    protected void getMatchData(String json) {
        Boolean getSuccess = false;
        if (json != null) {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                if (lotteryType == 1) {
                    spfmatchList.clear();
                    spfmatchListSub.clear();
                }
                else if (lotteryType == 2) {
                    zjqmatchList.clear();
                    zjqmatchListSub.clear();
                }
                else if (lotteryType == 3) {
                    bqcmatchList.clear();
                    bqcmatchListSub.clear();
                }
                else if (lotteryType == 4) {
                    sxdsmatchList.clear();
                    sxdsmatchListSub.clear();
                }
                else if (lotteryType == 5) {
                    bfmatchList.clear();
                    bfmatchListSub.clear();
                }
                String response_data = analyse.getData(json, "response_data");
                if (response_data.equals("[]") == false) {
                    try {
                        JSONArray hallArray = new JSONArray(response_data);
                        int length = hallArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jo = hallArray.getJSONObject(i);
                            String gameDate = jo.getString("game_date");
                            jcLeague.put(jo.getString("match_name"), jo.getString("match_name"));
                            int index = 0;
                            boolean hasSameDay = false;
                            if (lotteryType == 1) {
                                for (index = 0; index < spfmatchList.size(); index++) {
                                    if (spfmatchList.get(index).getDate().equals(gameDate)) {
                                        hasSameDay = true;
                                        break;
                                    }
                                }
                            }
                            else if (lotteryType == 2) {
                                for (index = 0; index < zjqmatchList.size(); index++) {
                                    if (zjqmatchList.get(index).getDate().equals(gameDate)) {
                                        hasSameDay = true;
                                        break;
                                    }
                                }
                            }
                            else if (lotteryType == 3) {
                                for (index = 0; index < bqcmatchList.size(); index++) {
                                    if (bqcmatchList.get(index).getDate().equals(gameDate)) {
                                        hasSameDay = true;
                                        break;
                                    }
                                }
                            }
                            else if (lotteryType == 4) {
                                for (index = 0; index < sxdsmatchList.size(); index++) {
                                    if (sxdsmatchList.get(index).getDate().equals(gameDate)) {
                                        hasSameDay = true;
                                        break;
                                    }
                                }
                            }
                            else if (lotteryType == 5) {
                                for (index = 0; index < bfmatchList.size(); index++) {
                                    if (bfmatchList.get(index).getDate().equals(gameDate)) {
                                        hasSameDay = true;
                                        break;
                                    }
                                }
                            }

                            // 格式化获取比赛是一周第几天
                            int date = TimeUtils.getDate(gameDate);
                            // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                            if (date == 1) {
                                date = 7;
                            }
                            else {
                                date = date - 1;
                            }
                            // 列表中无此天的比赛，新增该天次
                            if (!hasSameDay) {
                                SportsSFCBetGroup groupTemp = new SportsSFCBetGroup();
                                groupTemp.setDate(gameDate);
                                if (date != -1) {
                                    groupTemp.setDay(WEEKDAY[date - 1]);
                                }
                                else {
                                    groupTemp.setDay("");
                                }
                                if (lotteryType == 1) {
                                    ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                    addsfSportsItem(date, jo, sportsItemList);
                                    groupTemp.setSpfitemList(sportsItemList);
                                    spfmatchList.add(groupTemp);
                                    spfmatchListSub.add(groupTemp);
                                }
                                else if (lotteryType == 2) {
                                    ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                    addzjqSportsItem(date, jo, sportsItemList);
                                    groupTemp.setZjqitemList(sportsItemList);
                                    zjqmatchList.add(groupTemp);
                                    zjqmatchListSub.add(groupTemp);
                                }
                                else if (lotteryType == 3) {
                                    ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                    addbqcSportsItem(date, jo, sportsItemList);
                                    groupTemp.setBqcitemList(sportsItemList);
                                    bqcmatchList.add(groupTemp);
                                    bqcmatchListSub.add(groupTemp);
                                }
                                else if (lotteryType == 4) {
                                    ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                    addsxdsSportsItem(date, jo, sportsItemList);
                                    groupTemp.setZQDCSxdsitemList(sportsItemList);
                                    sxdsmatchList.add(groupTemp);
                                    sxdsmatchListSub.add(groupTemp);
                                }
                                else if (lotteryType == 5) {
                                    ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                    addbfSportsItem(date, jo, sportsItemList);
                                    groupTemp.setZQDCBfitemList(sportsItemList);
                                    bfmatchList.add(groupTemp);
                                    bfmatchListSub.add(groupTemp);
                                }
                            }
                            else {
                                if (lotteryType == 1) {
                                    addsfSportsItem(date, jo, spfmatchList.get(index).getSpfitemList());
                                }
                                else if (lotteryType == 2) {
                                    addzjqSportsItem(date, jo, zjqmatchList.get(index).getZjqitemList());
                                }
                                else if (lotteryType == 3) {
                                    addbqcSportsItem(date, jo, bqcmatchList.get(index).getBqcitemList());
                                }
                                else if (lotteryType == 4) {
                                    addsxdsSportsItem(date, jo, sxdsmatchList.get(index).getZQDCSxdsitemList());
                                }
                                else if (lotteryType == 5) {
                                    addbfSportsItem(date, jo, bfmatchList.get(index).getZQDCBfitemList());
                                }
                            }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getSuccess = true;
                if (lotteryType == 1) {
                    for (int i = 0; i < spfmatchList.size(); i++) {
                        SportsSFCBetGroup groupTemp = spfmatchList.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getSpfitemList().size() + "场赛事");
                    }
                    for (int i = 0; i < spfmatchListSub.size(); i++) {
                        SportsSFCBetGroup groupTemp = spfmatchListSub.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getSpfitemList().size() + "场赛事");
                    }
                }
                else if (lotteryType == 2) {
                    for (int i = 0; i < zjqmatchList.size(); i++) {
                        SportsSFCBetGroup groupTemp = zjqmatchList.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZjqitemList().size() + "场赛事");
                    }
                    for (int i = 0; i < zjqmatchListSub.size(); i++) {
                        SportsSFCBetGroup groupTemp = zjqmatchListSub.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZjqitemList().size() + "场赛事");
                    }
                }
                else if (lotteryType == 3) {
                    for (int i = 0; i < bqcmatchList.size(); i++) {
                        SportsSFCBetGroup groupTemp = bqcmatchList.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getBqcitemList().size() + "场赛事");
                    }
                    for (int i = 0; i < bqcmatchListSub.size(); i++) {
                        SportsSFCBetGroup groupTemp = bqcmatchListSub.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getBqcitemList().size() + "场赛事");
                    }
                }
                else if (lotteryType == 4) {
                    for (int i = 0; i < sxdsmatchList.size(); i++) {
                        SportsSFCBetGroup groupTemp = sxdsmatchList.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZQDCSxdsitemList().size() + "场赛事");
                    }
                    for (int i = 0; i < sxdsmatchListSub.size(); i++) {
                        SportsSFCBetGroup groupTemp = sxdsmatchListSub.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZQDCSxdsitemList().size() + "场赛事");
                    }
                }
                else if (lotteryType == 5) {
                    for (int i = 0; i < bfmatchList.size(); i++) {
                        SportsSFCBetGroup groupTemp = bfmatchList.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZQDCBfitemList().size() + "场赛事");
                    }
                    for (int i = 0; i < bfmatchListSub.size(); i++) {
                        SportsSFCBetGroup groupTemp = bfmatchListSub.get(i);
                        groupTemp.setGameNumber("共" + groupTemp.getZQDCBfitemList().size() + "场赛事");
                    }
                }
                isClickable = true;
                lv.refreshContent();
                notifyDataSetChange();
                lv.expandList();
            }
        }
        if (!getSuccess) {
            ViewUtil.showTipsToast(this,"比赛数据获取失败");
        }
    }

    /**
     * By vincent 2012-08-07 跳转投注
     */
    @Override
    protected void initChoosed(String betCode) {
        if (betCode != null) {
            if (lotteryType == 1) {
                for (int i = 0; i < spfmatchList.size(); i++) {
                    ArrayList<SportsItem> sportsItemList = spfmatchList.get(i).getSpfitemList();// 根据日期分组

                    String gameDate = spfmatchList.get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode: 5$002=1 | 5$003=1 |
// 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]: 5$002=1 | 5$003=1 |
// 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0] 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1] 002=1
                        int day = Integer.parseInt(dayCode[0]);// dayCode[0] = 5
                        if (day == date) {
                            for (int j = 0; j < sportsItemList.size(); j++) {
                                SportsItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        if (num == 3)
                                            item.setStatus(0, true);
                                        else if (num == 1)
                                            item.setStatus(1, true);
                                        else if (num == 0)
                                            item.setStatus(2, true);
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 1)
                    clear();
            }
            else if (lotteryType == 2) {
                for (int i = 0; i < zjqmatchList.size(); i++) {
                    ArrayList<SportsItem> sportsItemList = zjqmatchList.get(i).getZjqitemList();// 根据日期分组
                    String gameDate = zjqmatchList.get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode: 5$002=1 | 5$003=1 |
// 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]: 5$002=1 | 5$003=1 |
// 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0] 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1] 002=1
                        int day = Integer.parseInt(dayCode[0]);// dayCode[0] = 5
                        if (day == date) {
                            for (int j = 0; j < sportsItemList.size(); j++) {
                                SportsItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        item.setStatus(num, true);
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 2)
                    clear();
            }
            else if (lotteryType == 3) {
                for (int i = 0; i < bqcmatchList.size(); i++) {
                    ArrayList<SportsItem> sportsItemList = bqcmatchList.get(i).getBqcitemList();// 根据日期分组
                    String gameDate = bqcmatchList.get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode: 5$002=1 | 5$003=1 |
// 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]: 5$002=1 | 5$003=1 |
// 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0] 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1] 002=1
                        int day = Integer.parseInt(dayCode[0]);// dayCode[0] = 5
                        if (day == date) {
                            for (int j = 0; j < sportsItemList.size(); j++) {
                                SportsItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        for (int m = 0; m < 9; m++) {
                                            if (num == bqcLot[m]) {
                                                item.setStatus(m, true);
                                            }
                                        }
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 3)
                    clear();
            }
            //TODO
            else if (lotteryType == 4) {
                
            }
            else if (lotteryType == 5) {
                
            }
        }

// initBetList();
        showMatchSelectedNum();
        checkBtnStatus();
    }

    /**
     * 添加每场比赛到每天的场次中
     * 
     * @param date 比赛是一周的哪天
     * @param jo 从服务器获取到的数据格式
     * @param sportsItemList 要存储的总场次列表
     * @throws JSONException 读取不到数据异常
     */
    private void addsfSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList)
        throws JSONException {
        SportsItem sportItem = new SportsItem();
        sportItem.setId(jo.getString("game_id"));
        sportItem.setWeekDay(String.valueOf(date));
        sportItem.setIdBetNum(jo.getString("no"));
        sportItem.setLeague(jo.getString("match_name"));
        sportItem.setMatchHomeTeamName(jo.getString("master"));
        sportItem.setMatchGuessTeamName(jo.getString("guest"));
        sportItem.setEndTime(jo.getString("stop_time"));
        sportItem.setMatchTime(jo.getString("match_time"));
        String sp = jo.getString("sp");
        String[] spTemp = sp.split(",");
        for (int m = 0; m < 3; m++) {
            sportItem.setOdds(m, spTemp[m].split("\\|")[1]);
            sportItem.setStatus(m, false);
        }
        sportItem.setConcede(jo.getString("handicap"));
        sportsItemList.add(sportItem);
    }

    private void addzjqSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList)
        throws JSONException {
        SportsItem sportItem = new SportsItem();
        sportItem.setId(jo.getString("game_id"));
        sportItem.setWeekDay(String.valueOf(date));
        sportItem.setIdBetNum(jo.getString("no"));
        sportItem.setLeague(jo.getString("match_name"));
        sportItem.setMatchHomeTeamName(jo.getString("master"));
        sportItem.setMatchGuessTeamName(jo.getString("guest"));
        sportItem.setEndTime(jo.getString("stop_time"));
        sportItem.setMatchTime(jo.getString("match_time"));
        String sp = jo.getString("sp");
        String[] spTemp = sp.split(",");
        for (int m = 0; m < 8; m++) {
            sportItem.setOdds(m, spTemp[m].split("\\|")[1]);
            sportItem.setStatus(m, false);
        }
        sportsItemList.add(sportItem);
    }

    private void addbqcSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList)
        throws JSONException {
        SportsItem sportItem = new SportsItem();
        sportItem.setId(jo.getString("game_id"));
        sportItem.setWeekDay(String.valueOf(date));
        sportItem.setIdBetNum(jo.getString("no"));
        sportItem.setLeague(jo.getString("match_name"));
        sportItem.setMatchHomeTeamName(jo.getString("master"));
        sportItem.setMatchGuessTeamName(jo.getString("guest"));
        sportItem.setEndTime(jo.getString("stop_time"));
        sportItem.setMatchTime(jo.getString("match_time"));
        String sp = jo.getString("sp");
        String[] spTemp = sp.split(",");
        for (int m = 0; m < 9; m++) {
            sportItem.setOdds(m, spTemp[m].split("\\|")[1]);
            sportItem.setStatus(m, false);
        }
        sportsItemList.add(sportItem);
    }
    //TODO
    private void addsxdsSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList)
        throws JSONException {
        
    }
    private void addbfSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList)
        throws JSONException {
        
    }

    @Override
    protected void bet() {
        super.bet();
        initBetList();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("bet_kind", "zqdc");
        if (lotteryType == 1) {
            bundle.putString("bet_term", getType());
            bundle.putParcelableArrayList("bet_sports_data", spfbetList);
        }
        else if (lotteryType == 2) {
            bundle.putString("bet_term", getType());
            bundle.putParcelableArrayList("bet_sports_data", zjqbetList);
        }
        else if (lotteryType == 3) {
            bundle.putString("bet_term", getType());
            bundle.putParcelableArrayList("bet_sports_data", bqcbetList);
        }
        else if (lotteryType == 4) {
            bundle.putString("bet_term", getType());
            bundle.putParcelableArrayList("bet_sports_data", sxdsbetList);
        }
        else if (lotteryType == 5) {
            bundle.putString("bet_term", getType());
            bundle.putParcelableArrayList("bet_sports_data", bfbetList);
        }

        bundle.putString("mode", "0000");
        bundle.putLong("endtime", endTimeMillis);
        bundle.putBoolean("fromBet", true);
        bundle.putString("about", "left");
        bundle.putBoolean("ifStartSelf", false);
        bundle.putString("forwardFlag", "收银台");
        bundle.putBoolean("is_continue_pass", true);
        bundle.putString("class_name", BetPaySports.class.getName());
        intent.putExtras(bundle);
        if (appState.getUsername() == null) {
            intent.setClass(ZQDCActivity.this, Login.class);
//            intent.setClass(ZQDCActivity.this, StartUp.class);
        }
        else {
            intent.setClass(ZQDCActivity.this, BetPaySports.class);
        }
        startActivityForResult(intent, 1);
    }

    private void initBetList() {
        if (lotteryType == 1) {
            spfbetList.clear();// Removes all elements from this ArrayList, leaving it empty. Offerd by
// system.
            for (int i = 0; i < spfmatchList.size(); i++) {
                ArrayList<SportsItem> sportsItemList = spfmatchList.get(i).getSpfitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem item = sportsItemList.get(j);
                    if (item.getStatus(0) || item.getStatus(1) || item.getStatus(2)) {
                        spfbetList.add(item);
                        long millis = TimeUtils.getDateMillisecond(item.getEndTime());
                        if (endTimeMillis == 0 || endTimeMillis > millis) {
                            endTimeMillis = millis;
                        }
                    }
                }
            }
        }
        else if (lotteryType == 2) {
            zjqbetList.clear();
            for (int i = 0; i < zjqmatchList.size(); i++) {
                ArrayList<SportsItem> sportsItemList = zjqmatchList.get(i).getZjqitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem item = sportsItemList.get(j);
                    if (item.getStatus(0) || item.getStatus(1) || item.getStatus(2) || item.getStatus(3) ||
                        item.getStatus(4) || item.getStatus(5) || item.getStatus(6) || item.getStatus(7)) {
                        zjqbetList.add(item);
                        long millis = TimeUtils.getDateMillisecond(item.getEndTime());
                        if (endTimeMillis == 0 || endTimeMillis > millis) {
                            endTimeMillis = millis;
                        }
                    }
                }
            }
        }
        else if (lotteryType == 3) {
            bqcbetList.clear();
            for (int i = 0; i < bqcmatchList.size(); i++) {
                ArrayList<SportsItem> sportsItemList = bqcmatchList.get(i).getBqcitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem item = sportsItemList.get(j);
                    if (item.getStatus(0) || item.getStatus(1) || item.getStatus(2) || item.getStatus(3) ||
                        item.getStatus(4) || item.getStatus(5) || item.getStatus(6) || item.getStatus(7) ||
                        item.getStatus(8)) {
                        bqcbetList.add(item);
                        long millis = TimeUtils.getDateMillisecond(item.getEndTime());
                        if (endTimeMillis == 0 || endTimeMillis > millis) {
                            endTimeMillis = millis;
                        }
                    }
                }
            }
        }
        //TODO
        else if (lotteryType == 4) {
            
        }
        else if (lotteryType == 5) {
            
        }
    }

    @Override
    protected void clear() {
        super.clear();
        clearBet();
        showMatchSelectedNum();
        checkBtnStatus();
    }

    private void clearBet() {
        if (lotteryType == 1) {
            for (int i = 0; i < spfmatchListSub.size(); i++) {
                SportsSFCBetGroup sports = spfmatchListSub.get(i);
                ArrayList<SportsItem> sportsItemList = sports.getSpfitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem sportItem = sportsItemList.get(j);
                    for (int m = 0; m < 3; m++) {
                        sportItem.setStatus(m, false);
                    }
                }
            }
        }
        if (lotteryType == 2) {
            for (int i = 0; i < zjqmatchListSub.size(); i++) {
                SportsSFCBetGroup sports = zjqmatchListSub.get(i);
                ArrayList<SportsItem> sportsItemList = sports.getZjqitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem sportItem = sportsItemList.get(j);
                    for (int m = 0; m < 8; m++) {
                        sportItem.setStatus(m, false);
                    }
                }
            }
        }
        if (lotteryType == 3) {
            for (int i = 0; i < bqcmatchListSub.size(); i++) {
                SportsSFCBetGroup sports = bqcmatchListSub.get(i);
                ArrayList<SportsItem> sportsItemList = sports.getBqcitemList();
                for (int j = 0; j < sportsItemList.size(); j++) {
                    SportsItem sportItem = sportsItemList.get(j);
                    for (int m = 0; m < 9; m++) {
                        sportItem.setStatus(m, false);
                    }
                }
            }
        }
        //TODO
        else if (lotteryType == 4) {
            
        }
        else if (lotteryType == 5) {
            
        }
        notifyDataSetChange();
        

        selectedNum = 0;
    }

    private void notifyDataSetChange() {
        if (lotteryType == 1) {
            spfadapter.notifyDataSetChanged();
        }
        else if (lotteryType == 2) {
            zjqadapter.notifyDataSetChanged();
        }
        else if (lotteryType == 3) {
            bqcadapter.notifyDataSetChanged();
        }
        else if (lotteryType == 4) {
            sxdsadapter.notifyDataSetChanged();
        }
        else if (lotteryType == 5) {
            bfadapter.notifyDataSetChanged();
        }
    }

    /**
     * 当有比赛点击事件触发时，判断是否允许点击，并作处理
     * 
     * @param groupPosition 点击的比赛属于哪一天
     * @param childPosition 点击的比赛属于哪一队
     * @param index 点击的是胜平负的哪个
     * @return 是否允许点击
     */
    @Override
    public boolean checkClick(int groupPosition, int childPosition, int index) {
        Boolean canClick = false;
        if (lotteryType == 1) {
            SportsItem item = spfmatchListSub.get(groupPosition).getSpfitemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED) {
                    ViewUtil.showTipsToast(this,"最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
        }
        else if (lotteryType == 2) {
            SportsItem item = zjqmatchListSub.get(groupPosition).getZjqitemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2) && !item.getStatus(3) &&
                    !item.getStatus(4) && !item.getStatus(5) && !item.getStatus(6) && !item.getStatus(7)) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED) {
                    ViewUtil.showTipsToast(this,"最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2) &&
                        !item.getStatus(3) && !item.getStatus(4) && !item.getStatus(5) &&
                        !item.getStatus(6) && !item.getStatus(7)) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
        }
        else if (lotteryType == 3) {
            SportsItem item = bqcmatchListSub.get(groupPosition).getBqcitemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2) && !item.getStatus(3) &&
                    !item.getStatus(4) && !item.getStatus(5) && !item.getStatus(6) && !item.getStatus(7) &&
                    !item.getStatus(8)) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED) {
                    ViewUtil.showTipsToast(this,"最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2) &&
                        !item.getStatus(3) && !item.getStatus(4) && !item.getStatus(5) &&
                        !item.getStatus(6) && !item.getStatus(7) && !item.getStatus(8)) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
        }
        //TODO
        else if (lotteryType == 4) {
            
        }
        else if (lotteryType == 5) {
            
        }

        if (canClick) {
            showMatchSelectedNum();
            checkBtnStatus();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 设置清空投注按钮属性，是否可点击
     */
    private void checkBtnStatus() {
        if (selectedNum == 0) {
            disableBetBtn();
            disableClearBtn();
        }
        else if (selectedNum < MIN_SELECTED) {
            disableBetBtn();
            enableClearBtn();
        }
        else {
            enableBetBtn();
            enableClearBtn();
        }
    }

    /**
     * 页面顶部显示选中场次
     */
    private void showMatchSelectedNum() {
        betInf.setText(Html.fromHtml("已选择<font color='red'>" + selectedNum + "</font>场比赛"));
    }

}
