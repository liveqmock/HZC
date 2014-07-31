package com.haozan.caipiao.activity.bet.jczq;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.BetPaySports;
import com.haozan.caipiao.activity.bet.BetPayUniteSports;
import com.haozan.caipiao.adapter.BetHistoryTypeSelectionAdapter;
import com.haozan.caipiao.adapter.SportsJCZQBFAdapter;
import com.haozan.caipiao.adapter.SportsJCZQBQCAdapter;
import com.haozan.caipiao.adapter.SportsJCZQSPFAdapter;
import com.haozan.caipiao.adapter.SportsJCZQSPFAdapter.OnBallClickListener;
import com.haozan.caipiao.adapter.SportsJCZQZJQAdapter;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;

public class JCZQActivity
    extends JCZQBasicActivity
    implements OnBallClickListener, com.haozan.caipiao.adapter.SportsJCZQZJQAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsJCZQBQCAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsJCZQBFAdapter.OnBallClickListener {
    private static final int MIN_SELECTED = 2;
    private static final int MAX_SELECTED = 15;

    private int selectedNum = 0;
    protected long endTimeMillis = 0;

    // 过关
    private SportsJCZQSPFAdapter norfspfadapter;
    private SportsJCZQSPFAdapter spfadapter;
    private SportsJCZQZJQAdapter zjqadapter;
    private SportsJCZQBQCAdapter bqcadapter;
    private SportsJCZQBFAdapter bfadapter;
    private ArrayList<BaseExpandableListAdapter> adapter = new ArrayList<BaseExpandableListAdapter>();
    private ArrayList<ArrayList<SportsItem>> betList = new ArrayList<ArrayList<SportsItem>>();
    // 单关
    private SportsJCZQSPFAdapter norfspfadapter_dg;
    private SportsJCZQSPFAdapter spfadapter_dg;
    private SportsJCZQZJQAdapter zjqadapter_dg;
    private SportsJCZQBQCAdapter bqcadapter_dg;
    private SportsJCZQBFAdapter bfadapter_dg;
    private ArrayList<ArrayList<SportsSFCBetGroup>> matchList = new ArrayList<ArrayList<SportsSFCBetGroup>>();
    private ArrayList<ArrayList<SportsSFCBetGroup>> matchListSub =
        new ArrayList<ArrayList<SportsSFCBetGroup>>();
    private GridView menuGidView;
    private RelativeLayout termLayout;
    private PopupWindow mPopupWindow;
    private RelativeLayout topBgLinear;
    // add by vincent
    private PopupWindow betwayPop;

    private BetHistoryTypeSelectionAdapter betHisTypeSeleAdapter;
    private Button leagueFilter;
    private RelativeLayout leagueFilterLin;
    private String[] buttonNameArray;
    public static Map<String, String> jcLeague;
    private boolean isClickable = false;
    private int index = 0;
    private int[] bqcLot = new int[] {33, 31, 30, 13, 11, 10, 03, 01, 00};
    private int[] bfLot = new int[] {10, 20, 21, 30, 31, 32, 40, 41, 42, 50, 51, 52, 90, 00, 11, 22, 33, 99,
            01, 02, 03, 04, 05, 12, 13, 14, 15, 23, 24, 25, 9};
    private int[] num = {3, 3, 8, 9, 31, 3, 3, 8, 9, 31};

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
        leagueFilterLin = (RelativeLayout) findViewById(R.id.league_filter_lin);
        leagueFilter.setText("全部");
        leagueFilter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isClickable) {
                    mapToArray();
                    InitPopupViews(R.layout.pop_grid_view, buttonNameArray);
// mPopupWindow.showAsDropDown(leagueFilter);
                    showPopupCenter(mPopupWindow);
                    setLiagueFilterImg("up");
                }
                else {
                    ViewUtil.showTipsToast(JCZQActivity.this, "暂无数据");
                }
            }
        });
    }

    protected void setLiagueFilterImg(String str) {
        if (str.equals("up")) {
            leagueFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_white, 0);
        }
        else {
            leagueFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_white, 0);
        }

        // 另一种方法
// Drawable img_up, img_down;
// Resources res = getResources();
// img_up = res.getDrawable(R.drawable.arrow_up);
// img_down = res.getDrawable(R.drawable.arrow_down);
// // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
// img_up.setBounds(0, 0, img_up.getMinimumWidth(), img_up.getMinimumHeight());
// img_down.setBounds(0, 0, img_down.getMinimumWidth(), img_down.getMinimumHeight());
// if (str.equals("up")) {
// leagueFilter.setCompoundDrawables(null, null, img_up, null); // 设置右图标
// }
// else {
// leagueFilter.setCompoundDrawables(null, null, img_down, null); // 设置右图标
// }
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
// topBgLinear= (RelativeLayout)this.findViewById(R.id.layout_title);
        for (int i = 0; i < 10; i++) {
            ArrayList<SportsSFCBetGroup> list0 = new ArrayList<SportsSFCBetGroup>();
            matchList.add(list0);
            ArrayList<SportsSFCBetGroup> list1 = new ArrayList<SportsSFCBetGroup>();
            matchListSub.add(list1);
            ArrayList<SportsItem> list2 = new ArrayList<SportsItem>();
            betList.add(list2);
        }

        jcLeague = new HashMap<String, String>();

        norfspfadapter = new SportsJCZQSPFAdapter(JCZQActivity.this, matchListSub.get(0));
        adapter.add(norfspfadapter);
        ((SportsJCZQSPFAdapter) adapter.get(0)).setClickListener(this);

        spfadapter = new SportsJCZQSPFAdapter(JCZQActivity.this, matchListSub.get(1));
        adapter.add(spfadapter);
        ((SportsJCZQSPFAdapter) adapter.get(1)).setClickListener(this);

        zjqadapter = new SportsJCZQZJQAdapter(JCZQActivity.this, matchListSub.get(2));
        adapter.add(zjqadapter);
        ((SportsJCZQZJQAdapter) adapter.get(2)).setClickListener(this);

        bqcadapter = new SportsJCZQBQCAdapter(JCZQActivity.this, matchListSub.get(3));
        adapter.add(bqcadapter);
        ((SportsJCZQBQCAdapter) adapter.get(3)).setClickListener(this);

        bfadapter = new SportsJCZQBFAdapter(JCZQActivity.this, matchListSub.get(4));
        adapter.add(bfadapter);
        ((SportsJCZQBFAdapter) adapter.get(4)).setClickListener(this);
        // 单关
        norfspfadapter_dg = new SportsJCZQSPFAdapter(JCZQActivity.this, matchListSub.get(5));
        adapter.add(norfspfadapter_dg);
        ((SportsJCZQSPFAdapter) adapter.get(5)).setClickListener(this);

        spfadapter_dg = new SportsJCZQSPFAdapter(JCZQActivity.this, matchListSub.get(6));
        adapter.add(spfadapter_dg);
        ((SportsJCZQSPFAdapter) adapter.get(6)).setClickListener(this);

        zjqadapter_dg = new SportsJCZQZJQAdapter(JCZQActivity.this, matchListSub.get(7));
        adapter.add(zjqadapter_dg);
        ((SportsJCZQZJQAdapter) adapter.get(7)).setClickListener(this);

        bqcadapter_dg = new SportsJCZQBQCAdapter(JCZQActivity.this, matchListSub.get(8));
        adapter.add(bqcadapter_dg);
        ((SportsJCZQBQCAdapter) adapter.get(8)).setClickListener(this);

        bfadapter_dg = new SportsJCZQBFAdapter(JCZQActivity.this, matchListSub.get(9));
        adapter.add(bfadapter_dg);
        ((SportsJCZQBFAdapter) adapter.get(9)).setClickListener(this);

        for (int i = 0; i < matchList.size(); i++) {
            if (lotteryType == i + 1) {
                lv.setAdapter(adapter.get(i), R.layout.sports_history_group_view);
                break;
            }
        }

        // TODO
        betInf.setText("请选择2-15场比赛");

        kind = "jczq";
        for (int i = 0; i < 10; i++) {
            if (lotteryType == i + 1) {
                resetZhusign(lotteryType);
                if (HttpConnectUtil.isNetworkAvailable(JCZQActivity.this)) {
                    GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                    if (i < 5)
                        getLotteryInf.execute(kind, playID[i], "1");
                    else {
                        if (i != 9) {
                            getLotteryInf.execute(kind, playID[i], "0");
                        }
                        else {
                            getLotteryInf.execute(kind, playID[i], "1");
                        }
                    }
                }
                else {
                    String inf = getResources().getString(R.string.network_not_avaliable);
                    ViewUtil.showTipsToast(this, inf);
                }
                break;
            }
        }
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
        View waySwitchLayout = mInflater.inflate(R.layout.jczq_bet_way_select, null);

        initDefault(waySwitchLayout);
        for (int i = 0; i < matchList.size(); i++) {
            final int j = i;
            icons[i].setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    betwayPop.dismiss();
                    if (lotteryType != j + 1) {
                        databaseData.putString("jczq_way", playStr[j]);
                        databaseData.commit();
                        clear();

                        lv.setAdapter(adapter.get(j), R.layout.sports_history_group_view);
                        lotteryType = j + 1;
                        resetZhusign(lotteryType);
                        if (j < 5)
                            isGuoguan = true;
                        else
                            isGuoguan = false;
                        title.setText(playName[j]);
                        if (matchList.get(j).size() == 0) {
                            if (HttpConnectUtil.isNetworkAvailable(JCZQActivity.this)) {
                                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                                if (j < 5)
                                    getLotteryInf.execute(kind, playID[j], "1");
                                else {
                                    if (j != 9) {
                                        getLotteryInf.execute(kind, playID[j], "0");
                                    }
                                    else {
                                        getLotteryInf.execute(kind, playID[j], "1");
                                    }
                                }
                            }
                            else {
                                String inf = getResources().getString(R.string.network_not_avaliable);
                                ViewUtil.showTipsToast(JCZQActivity.this, inf);
                            }
                        }
                        if (index != 0) {
                            initFilter(buttonNameArray[index].trim());
                        }
                        else {
                            matchListSub.get(j).clear();
                            for (int i = 0; i < matchList.get(j).size(); i++) {
                                matchListSub.get(j).add(matchList.get(j).get(i));
                            }
                        }
                        lv.refreshContent();
                        adapter.get(j).notifyDataSetChanged();
                        lv.expandList();
                    }
                }

            });
        }

        iniSelectedItemBg();
        initIcon(lotteryType);

        betwayPop = new PopupWindow(this);
        betwayPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // betwayPop.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        betwayPop.setWidth(findViewById(R.id.layout_title).getWidth() - 20);
        betwayPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        betwayPop.setOutsideTouchable(true);
        betwayPop.setFocusable(true);
        betwayPop.setContentView(waySwitchLayout);
        showPopupCenter(betwayPop);
        topArrow.setImageResource(R.drawable.arrow_up_white);
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        betwayPop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
            }
        });
    }

    @Override
    public void showWay() {
        for (int i = 0; i < matchList.size(); i++) {
            if (lotteryType == i + 1) {
                if (index != 0) {
                    initFilter(buttonNameArray[index].trim());
                }
                else {
                    matchListSub.get(i).clear();
                    for (int j = 0; j < matchList.get(i).size(); j++) {
                        matchListSub.get(i).add(matchList.get(i).get(j));
                    }
                }
                lv.refreshContent();
                adapter.get(i).notifyDataSetChanged();
                lv.expandList();
                break;
            }
        }

    }

    private void InitPopupViews(int layout, String[] textArray) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(layout, null);
// ImageView upArrowSign=(ImageView)waySwitchLayout.findViewById(R.id.direction_sign_up);
// upArrowSign.setVisibility(View.GONE);
        waySwitchLayout.findViewById(R.id.direction_sign_up).setVisibility(View.GONE);
        betHisTypeSeleAdapter =
            new BetHistoryTypeSelectionAdapter(JCZQActivity.this, textArray, index,
                                               R.layout.bet_method_select_item);
        menuGidView = (GridView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
        menuGidView.setAdapter(betHisTypeSeleAdapter);
        griViewClick();
        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                setLiagueFilterImg("down");
            }
        });
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
                        for (int i = 0; i < matchList.size(); i++) {
                            if (lotteryType == i + 1) {
                                matchListSub.get(i).clear();
                                for (int j = 0; j < matchList.get(i).size(); j++) {
                                    matchListSub.get(i).add(matchList.get(i).get(j));
                                }
                                break;
                            }
                        }
                    }
                    // mPopupWindow.dismiss();
                    lv.refreshContent();
                    for (int i = 0; i < matchList.size(); i++) {
                        if (lotteryType == i + 1) {
                            adapter.get(i).notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void initFilter(String league) {
        boolean isHaveCertainLeague = false;
        for (int k = 0; k < matchList.size(); k++) {
            if (lotteryType == k + 1) {
                matchListSub.get(k).clear();
                for (int i = 0; i < matchList.get(k).size(); i++) {
                    int gameNum = 0;
                    SportsSFCBetGroup sportsSFCBetGroup = new SportsSFCBetGroup();
                    for (int j = 0; j < matchList.get(k).get(i).getItemList().size(); j++) {
                        String leagueType = matchList.get(k).get(i).getItemList().get(j).getLeague();
                        if (leagueType.equals(league)) {
                            sportsSFCBetGroup.setItemList(getArrayList(matchList.get(k).get(i).getItemList(),
                                                                       league));
                            isHaveCertainLeague = true;
                            gameNum++;
                        }
                    }
                    if (isHaveCertainLeague) {
                        sportsSFCBetGroup.setDate(matchList.get(k).get(i).getDate());
                        sportsSFCBetGroup.setDay(matchList.get(k).get(i).getDay());
                        sportsSFCBetGroup.setGameNumber("共" + gameNum + "场赛事");
                        matchListSub.get(k).add(sportsSFCBetGroup);
                        isHaveCertainLeague = false;
                    }
                }
            }
        }
    }

    private ArrayList<SportsItem> getArrayList(ArrayList<SportsItem> sportsList, String leagueName) {
        ArrayList<SportsItem> sportLeagueArray = new ArrayList<SportsItem>();
        for (int i = 0; i < sportsList.size(); i++) {
            if (leagueName.equals(sportsList.get(i).getLeague())) {
                sportLeagueArray.add(sportsList.get(i));
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
                for (int i = 0; i < matchList.size(); i++) {
                    if (lotteryType == i + 1) {
                        resetZhusign(lotteryType);
                        matchList.get(i).clear();
                        matchListSub.get(i).clear();
                        break;
                    }
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
                            for (int j = 0; j < matchList.size(); j++) {
                                if (lotteryType == j + 1) {
                                    for (index = 0; index < matchList.get(j).size(); index++) {
                                        if (matchList.get(j).get(index).getDate().equals(gameDate)) {
                                            hasSameDay = true;
                                            break;
                                        }
                                    }
                                    break;
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
                                for (int j = 0; j < matchList.size(); j++) {
                                    if (lotteryType == j + 1) {
                                        ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
                                        try {
                                            addSportsItem(date, jo, sportsItemList, j);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        groupTemp.setItemList(sportsItemList);
                                        matchList.get(j).add(groupTemp);
                                        matchListSub.get(j).add(groupTemp);
                                        break;
                                    }
                                }
                            }
                            else {
                                for (int j = 0; j < matchList.size(); j++) {
                                    if (lotteryType == j + 1) {
                                        try {
                                            addSportsItem(date, jo,
                                                          matchList.get(j).get(index).getItemList(), j);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getSuccess = true;
                for (int j = 0; j < matchList.size(); j++) {
                    if (lotteryType == j + 1) {
                        for (int i = 0; i < matchList.get(j).size(); i++) {
                            SportsSFCBetGroup groupTemp = matchList.get(j).get(i);
                            groupTemp.setGameNumber("共" + groupTemp.getItemList().size() + "场赛事");
                        }
                        for (int i = 0; i < matchListSub.get(j).size(); i++) {
                            SportsSFCBetGroup groupTemp = matchListSub.get(j).get(i);
                            groupTemp.setGameNumber("共" + groupTemp.getItemList().size() + "场赛事");
                        }
                        break;
                    }
                }
                isClickable = true;
                lv.refreshContent();
                for (int i = 0; i < matchList.size(); i++) {
                    if (lotteryType == i + 1) {
                        adapter.get(i).notifyDataSetChanged();
                        break;
                    }
                }
                lv.expandList();
            }
        }
        if (!getSuccess) {
            ViewUtil.showTipsToast(this, "比赛数据获取失败");
        }
    }

    private void resetZhusign(int lotteryType) {
        if (lotteryType == 2 || lotteryType == 7) {
            tv_zhu_sign.setText("主场(让球)");
        }
        else {
            tv_zhu_sign.setText("主场");
        }
    }

    /**
     * By vincent 2012-08-07 跳转投注
     */
    @Override
    protected void initChoosed(String betCode) {
        if (betCode != null) {
            if (lotteryType == 1 || lotteryType == 6) {
                // for (int i = 0; i < spfmatchList.size(); i++) {
                for (int i = 0; i < (isGuoguan ? matchList.get(0) : matchList.get(5)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(0) : matchList.get(5)).get(i).getItemList();// 根据日期分组

                    String gameDate = (isGuoguan ? matchList.get(0) : matchList.get(5)).get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode:
                                                             // 5$002=1 |
                                                             // 5$003=1 |
                    // 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]:
                                                                 // 5$002=1 |
                                                                 // 5$003=1 |
                    // 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0]
                                                                    // 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1]
                                                                    // 002=1
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
                if (lotteryTypeInit != 1 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 6 && isGuoguan == false)
                    clear();
            }
            else if (lotteryType == 2 || lotteryType == 7) {
                // for (int i = 0; i < spfmatchList.size(); i++) {
                for (int i = 0; i < (isGuoguan ? matchList.get(1) : matchList.get(6)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(1) : matchList.get(6)).get(i).getItemList();// 根据日期分组

                    String gameDate = (isGuoguan ? matchList.get(1) : matchList.get(6)).get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode:
                                                             // 5$002=1 |
                                                             // 5$003=1 |
                    // 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]:
                                                                 // 5$002=1 |
                                                                 // 5$003=1 |
                    // 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0]
                                                                    // 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1]
                                                                    // 002=1
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
                if (lotteryTypeInit != 2 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 7 && isGuoguan == false)
                    clear();
            }
            else if (lotteryType == 3 || lotteryType == 8) {
                // for (int i = 0; i < zjqmatchList.size(); i++) {
                for (int i = 0; i < (isGuoguan ? matchList.get(2) : matchList.get(7)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(2) : matchList.get(7)).get(i).getItemList();// 根据日期分组
                    String gameDate = (isGuoguan ? matchList.get(2) : matchList.get(7)).get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode:
                                                             // 5$002=1 |
                                                             // 5$003=1 |
                    // 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]:
                                                                 // 5$002=1 |
                                                                 // 5$003=1 |
                    // 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0]
                                                                    // 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1]
                                                                    // 002=1
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
                if (lotteryTypeInit != 3 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 8 && isGuoguan == false)
                    clear();
            }
            else if (lotteryType == 4 || lotteryType == 9) {
                // for (int i = 0; i < bqcmatchList.size(); i++) {
                for (int i = 0; i < (isGuoguan ? matchList.get(3) : matchList.get(8)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(3) : matchList.get(8)).get(i).getItemList();// 根据日期分组
                    String gameDate = (isGuoguan ? matchList.get(3) : matchList.get(8)).get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode:
                                                             // 5$002=1 |
                                                             // 5$003=1 |
                    // 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]:
                                                                 // 5$002=1 |
                                                                 // 5$003=1 |
                    // 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0]
                                                                    // 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1]
                                                                    // 002=1
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
                if (lotteryTypeInit != 4 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 9 && isGuoguan == false)
                    clear();
            }
            // TODO
            else if (lotteryType == 5 || lotteryType == 10) {
                // for (int i = 0; i < bfmatchList.size(); i++) {
                for (int i = 0; i < (isGuoguan ? matchList.get(4) : matchList.get(9)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(4) : matchList.get(9)).get(i).getItemList();// 根据日期分组
                    String gameDate = (isGuoguan ? matchList.get(4) : matchList.get(9)).get(i).getDate();
                    int date = TimeUtils.getDate(gameDate);
                    // 取出来的1代表星期天，转换成第七天形式,2代表星期一
                    if (date == 1) {
                        date = 7;
                    }
                    else {
                        date = date - 1;
                    }

                    String[] betCode1 = betCode.split("\\:");// 例：betCode:
                                                             // 5$002=1 |
                                                             // 5$003=1 |
                    // 5$004=3,1,0:6:301:1
                    String[] dayCodes = betCode1[0].split("\\|");// 例：betCode1[0]:
                                                                 // 5$002=1 |
                                                                 // 5$003=1 |
                    // 5$004=3,1,0
                    for (int k = 0; k < dayCodes.length; k++) {
                        String[] dayCode = dayCodes[k].split("\\$");// 例：dayCodes[0]
                                                                    // 5$002=1
                        String[] numbers = dayCode[1].split("\\="); // 例：dayCode[1]
                                                                    // 002=1
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
                                        for (int m = 0; m < 31; m++) {
                                            if (num == bfLot[m]) {
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
                if (lotteryTypeInit != 5 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 10 && isGuoguan == false)
                    clear();
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
    private void addSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList, int i)
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
        if (i == 0 || i == 5) {
            sportItem.setConcede("0");
        }
        else {
            sportItem.setConcede(jo.getString("handicap"));
        }
        String sp = null;
        if (jo.has("sp")) {
            sp = jo.getString("sp");
            if (sp != "null" && sp != "") {
                String[] spTemp = sp.split(",");
                for (int m = 0; m < num[i]; m++) {
                    sportItem.setOdds(m, spTemp[m].split("\\|")[1]);
                    sportItem.setStatus(m, false);
                }
                sportsItemList.add(sportItem);
            }
        }
    }

// private void addSportsItem(int date, JSONObject jo, ArrayList<SportsItem> sportsItemList, int i)
// throws JSONException {
// SportsItem sportItem = new SportsItem();
// sportItem.setId(jo.getString("game_id"));
// sportItem.setWeekDay(String.valueOf(date));
// sportItem.setIdBetNum(jo.getString("no"));
// sportItem.setLeague(jo.getString("match_name"));
// sportItem.setMatchHomeTeamName(jo.getString("master"));
// sportItem.setMatchGuessTeamName(jo.getString("guest"));
// sportItem.setEndTime(jo.getString("stop_time"));
// sportItem.setMatchTime(jo.getString("match_time"));
// String sp = jo.getString("sp");
// String[] spTemp = sp.split(",");
// for (int m = 0; m < num[i]; m++) {
// sportItem.setOdds(m, spTemp[m].split("\\|")[1]);
// sportItem.setStatus(m, false);
// }
// sportItem.setConcede(jo.getString("handicap"));
// sportsItemList.add(sportItem);
// }

    @Override
    protected void bet() {
        super.bet();
        initBetList();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("bet_kind", "jczq");
        for (int i = 0; i < 5; i++) {
            if (lotteryType == i + 1 || lotteryType == i + 6) {
                bundle.putString("bet_term", playID[i]);
                bundle.putParcelableArrayList("bet_sports_data",
                                              (isGuoguan ? betList.get(i) : betList.get(i + 5)));
                break;
            }
        }

        bundle.putString("mode", "0000");
        bundle.putLong("endtime", endTimeMillis);
        bundle.putBoolean("fromBet", true);
        bundle.putString("about", "left");
        bundle.putBoolean("isGuoguan", isGuoguan);
        bundle.putBoolean("ifStartSelf", false);
        bundle.putBoolean("bet_is_unite", doUnite);
        bundle.putString("forwardFlag", "收银台");
        bundle.putBoolean("is_continue_pass", true);
        if (betWay == 1) {
            bundle.putString("class_name", BetPaySports.class.getName());
        }
        else if (betWay == 3) {
            bundle.putString("class_name", BetPayUniteSports.class.getName());
        }
        intent.putExtras(bundle);
        if (appState.getUsername() == null) {
            intent.setClass(JCZQActivity.this, Login.class);
        }
        else {
            if (betWay == 1) {
                intent.setClass(JCZQActivity.this, BetPaySports.class);
            }
            else if (betWay == 3) {
                intent.setClass(JCZQActivity.this, BetPayUniteSports.class);
            }
        }
        startActivityForResult(intent, 1);
    }

    private void initBetList() {
        for (int k = 0; k < 5; k++) {
            if (lotteryType == k + 1 || lotteryType == k + 6) {
                (isGuoguan ? betList.get(k) : betList.get(k + 5)).clear();
                for (int i = 0; i < (isGuoguan ? matchList.get(k) : matchList.get(k + 5)).size(); i++) {
                    ArrayList<SportsItem> sportsItemList =
                        (isGuoguan ? matchList.get(k) : matchList.get(k + 5)).get(i).getItemList();
                    for (int j = 0; j < sportsItemList.size(); j++) {
                        SportsItem item = sportsItemList.get(j);
                        boolean isAdd = false;
                        for (int m = 0; m < num[k]; m++) {
                            if (item.getStatus(m)) {
                                isAdd = true;
                                break;
                            }
                        }
                        if (isAdd == true) {
                            (isGuoguan ? betList.get(k) : betList.get(k + 5)).add(item);
                            long millis = TimeUtils.getDateMillisecond(item.getEndTime());
                            if (endTimeMillis == 0 || endTimeMillis > millis) {
                                endTimeMillis = millis;
                            }
                        }

                    }
                }
                break;
            }
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
        for (int k = 0; k < 5; k++) {
            if (lotteryType == k + 1 || lotteryType == k + 6) {
                for (int i = 0; i < (isGuoguan ? matchListSub.get(k) : matchListSub.get(k + 5)).size(); i++) {
                    SportsSFCBetGroup sports =
                        (isGuoguan ? matchListSub.get(k) : matchListSub.get(k + 5)).get(i);
                    ArrayList<SportsItem> sportsItemList = sports.getItemList();
                    for (int j = 0; j < sportsItemList.size(); j++) {
                        SportsItem sportItem = sportsItemList.get(j);
                        sportItem.setShowStr(null);
                        for (int m = 0; m < num[k]; m++) {
                            sportItem.setStatus(m, false);
                        }
                    }
                }
                break;
            }
        }
        for (int i = 0; i < matchList.size(); i++) {
            if (lotteryType == i + 1) {
                adapter.get(i).notifyDataSetChanged();
                break;
            }
        }

        selectedNum = 0;
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
        if (lotteryType == 1 || lotteryType == 6) {
            SportsItem item =
                (isGuoguan ? matchListSub.get(0) : matchListSub.get(5)).get(groupPosition).getItemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) &&
                    !item.getStatus(2)) {
                    ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
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
        if (lotteryType == 2 || lotteryType == 7) {
            SportsItem item =
                (isGuoguan ? matchListSub.get(1) : matchListSub.get(6)).get(groupPosition).getItemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) &&
                    !item.getStatus(2)) {
                    ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
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
        else if (lotteryType == 3 || lotteryType == 8) {
            SportsItem item =
                (isGuoguan ? matchListSub.get(2) : matchListSub.get(7)).get(groupPosition).getItemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                boolean ifMin = true;
                for (int i = 0; i < 8; i++) {
                    if (item.getStatus(i)) {// 只要有任何一个被选中了，就不减
                        ifMin = false;
                        break;
                    }
                }
                if (ifMin == true) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) &&
                    !item.getStatus(2) && !item.getStatus(3) && !item.getStatus(4) && !item.getStatus(5) &&
                    !item.getStatus(6) && !item.getStatus(7)) {
                    ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    boolean ifAdd = true;
                    for (int i = 0; i < 8; i++) {
                        if (item.getStatus(i)) {
                            ifAdd = false;
                            break;
                        }
                    }
                    if (ifAdd == true) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
        }
        else if (lotteryType == 4 || lotteryType == 9) {
            SportsItem item =
                (isGuoguan ? matchListSub.get(3) : matchListSub.get(8)).get(groupPosition).getItemList().get(childPosition);
            if (item.getStatus(index)) {
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                boolean ifMin = true;
                for (int i = 0; i < 9; i++) {
                    if (item.getStatus(i)) {// 只要有任何一个被选中了，就不减
                        ifMin = false;
                        break;
                    }
                }
                if (ifMin == true) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) &&
                    !item.getStatus(2) && !item.getStatus(3) && !item.getStatus(4) && !item.getStatus(5) &&
                    !item.getStatus(6) && !item.getStatus(7) && !item.getStatus(8)) {
                    ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    boolean ifAdd = true;
                    for (int i = 0; i < 9; i++) {
                        if (item.getStatus(i)) {
                            ifAdd = false;
                            break;
                        }
                    }
                    if (ifAdd == true) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
        }
        // TODO
        else if (lotteryType == 5 || lotteryType == 10) {
            SportsItem item =
                (isGuoguan ? matchListSub.get(4) : matchListSub.get(9)).get(groupPosition).getItemList().get(childPosition);
            if (item.getStatus(index)) {
                // if (index != 31 && index != 32 & index != 33)
                item.reverseStatus(index);
                // 如果减少了一项，则选中数减一
                boolean ifMin = true;
                for (int i = 0; i < 31; i++) {
                    if (item.getStatus(i)) {// 只要有任何一个被选中了，就不减
                        ifMin = false;
                        break;
                    }
                }
                for (int i = 31; i < 34; i++) {
                    if (item.getStatus(i)) {// 只要有任何一个被选中了，就不减
                        ifMin = false;
                        break;
                    }
                }
                if (ifMin == true) {
                    selectedNum--;
                }
                canClick = true;
            }
            else {
                if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) &&
                    !item.getStatus(2) && !item.getStatus(3) && !item.getStatus(4) && !item.getStatus(5) &&
                    !item.getStatus(6) && !item.getStatus(7) && !item.getStatus(8) && !item.getStatus(9) &&
                    !item.getStatus(10) && !item.getStatus(11) && !item.getStatus(12) &&
                    !item.getStatus(13) && !item.getStatus(14) && !item.getStatus(15) &&
                    !item.getStatus(16) && !item.getStatus(17) && !item.getStatus(18) &&
                    !item.getStatus(19) && !item.getStatus(20) && !item.getStatus(21) &&
                    !item.getStatus(22) && !item.getStatus(23) && !item.getStatus(24) &&
                    !item.getStatus(25) && !item.getStatus(26) && !item.getStatus(27) &&
                    !item.getStatus(28) && !item.getStatus(29) && !item.getStatus(30)) {
                    ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
                }
                else {
                    // 如果增加了一项，则选中数加一
                    boolean ifAdd = true;
                    for (int i = 0; i < 31; i++) {
                        if (item.getStatus(i)) {
                            ifAdd = false;
                            break;
                        }
                    }
                    if (ifAdd == true) {
                        selectedNum++;
                    }
                    item.reverseStatus(index);
                    canClick = true;
                }
            }
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
        else if (selectedNum < MIN_SELECTED && isGuoguan == true) {
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
