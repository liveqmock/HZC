package com.haozan.caipiao.activity.bet.jclq;

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
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.BetPaySports;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.adapter.SportsJCLQDXFAdapter;
import com.haozan.caipiao.adapter.SportsJCLQRFSFAdapter;
import com.haozan.caipiao.adapter.SportsJCLQSFAdapter;
import com.haozan.caipiao.adapter.SportsJCLQSFAdapter.OnBallClickListener;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.SportsJCLQItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;

public class JCLQActivity
    extends JCLQBasicActivity
    implements OnBallClickListener, com.haozan.caipiao.adapter.SportsJCLQRFSFAdapter.OnBallClickListener,
    com.haozan.caipiao.adapter.SportsJCLQDXFAdapter.OnBallClickListener {
    private static final int MIN_SELECTED = 2;
    private static final int MAX_SELECTED = 15;

    private int selectedNum = 0;
    protected long endTimeMillis = 0;

    private SportsJCLQSFAdapter sfadapter;
    private SportsJCLQRFSFAdapter rfsfadapter;
    private SportsJCLQDXFAdapter dxfadapter;
    private SportsJCLQSFAdapter sfadapter_dg;
    private SportsJCLQRFSFAdapter rfsfadapter_dg;
    private SportsJCLQDXFAdapter dxfadapter_dg;
    private ArrayList<BaseExpandableListAdapter> adapter = new ArrayList<BaseExpandableListAdapter>();
    private ArrayList<ArrayList<SportsSFCBetGroup>> matchList = new ArrayList<ArrayList<SportsSFCBetGroup>>();
    private ArrayList<ArrayList<SportsSFCBetGroup>> matchListSub =
        new ArrayList<ArrayList<SportsSFCBetGroup>>();
    private ArrayList<ArrayList<SportsJCLQItem>> betList = new ArrayList<ArrayList<SportsJCLQItem>>();

    private PopupWindow betwayPop;
    public static Map<String, String> jcLeague;

// private boolean isClickable = false;
// private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        for (int i = 0; i < 6; i++) {
            ArrayList<SportsSFCBetGroup> list0 = new ArrayList<SportsSFCBetGroup>();
            matchList.add(list0);
            ArrayList<SportsSFCBetGroup> list1 = new ArrayList<SportsSFCBetGroup>();
            matchListSub.add(list1);
            ArrayList<SportsJCLQItem> list2 = new ArrayList<SportsJCLQItem>();
            betList.add(list2);
        }
        jcLeague = new HashMap<String, String>();
        sfadapter = new SportsJCLQSFAdapter(JCLQActivity.this, matchListSub.get(0));
        adapter.add(sfadapter);
        ((SportsJCLQSFAdapter) adapter.get(0)).setClickListener(this);
        rfsfadapter = new SportsJCLQRFSFAdapter(JCLQActivity.this, matchListSub.get(1));
        adapter.add(rfsfadapter);
        ((SportsJCLQRFSFAdapter) adapter.get(1)).setClickListener(this);
        dxfadapter = new SportsJCLQDXFAdapter(JCLQActivity.this, matchListSub.get(2));
        adapter.add(dxfadapter);
        ((SportsJCLQDXFAdapter) adapter.get(2)).setClickListener(this);

        sfadapter_dg = new SportsJCLQSFAdapter(JCLQActivity.this, matchListSub.get(3));
        adapter.add(sfadapter_dg);
        ((SportsJCLQSFAdapter) adapter.get(3)).setClickListener(this);
        rfsfadapter_dg = new SportsJCLQRFSFAdapter(JCLQActivity.this, matchListSub.get(4));
        adapter.add(rfsfadapter_dg);
        ((SportsJCLQRFSFAdapter) adapter.get(4)).setClickListener(this);
        dxfadapter_dg = new SportsJCLQDXFAdapter(JCLQActivity.this, matchListSub.get(5));
        adapter.add(dxfadapter_dg);
        ((SportsJCLQDXFAdapter) adapter.get(5)).setClickListener(this);

        for (int i = 0; i < matchList.size(); i++) {
            if (lotteryType == i + 1) {
                lv.setAdapter(adapter.get(i), R.layout.sports_history_group_view);
                break;
            }
        }
        betInf.setText("请选择2-15场比赛");
        kind = "jclq";
        for (int i = 0; i < 6; i++) {
            if (lotteryType == i + 1) {
                resetZhusign(lotteryType);
                if (HttpConnectUtil.isNetworkAvailable(JCLQActivity.this)) {
                    GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                    if (i < 3)
                        getLotteryInf.execute(kind, playID[i], "1");
                    else {
                        getLotteryInf.execute(kind, playID[i], "0");
                    }
                }
                else {
                    String inf = getResources().getString(R.string.network_not_avaliable);
                    ViewUtil.showTipsToast(this, inf);
                }
                break;
            }
        }

        // changed by vincent 12.08.06
// Bundle bundle = getIntent().getExtras();
// if (bundle != null) {
// betCode = bundle.getString("bet_code");
// }
        betwayPop = new PopupWindow(this);
        betwayPop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
// topBgLinear.setBackgroundResource(R.drawable.top_bg);
                topArrow.setImageResource(R.drawable.arrow_down_white);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopView();
        }
    }

    protected void showPopView() {
        LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mInflater.inflate(R.layout.jclq_bet_way_select, null);

// TextView jclqSf = (TextView) waySwitchLayout.findViewById(R.id.jclq_sf);
        initDefault(waySwitchLayout);
        for (int i = 0; i < matchList.size(); i++) {
            final int j = i;
            icons[i].setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    betwayPop.dismiss();
                    if (lotteryType != j + 1) {
                        databaseData.putString("jclq_way", playStr[j]);
                        databaseData.commit();
                        clear();

                        lv.setAdapter(adapter.get(j), R.layout.sports_history_group_view);
                        lotteryType = j + 1;
                        resetZhusign(lotteryType);
                        if (j < 3)
                            isGuoguan = true;
                        else
                            isGuoguan = false;
                        title.setText(playName[j]);
                        if (matchList.get(j).size() == 0) {
                            if (HttpConnectUtil.isNetworkAvailable(JCLQActivity.this)) {
                                GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
                                if (j < 3)
                                    getLotteryInf.execute(kind, playID[j], "1");
                                else {
                                    getLotteryInf.execute(kind, playID[j], "0");
                                }
                            }
                            else {
                                String inf = noNetTips;
                                ViewUtil.showTipsToast(JCLQActivity.this, inf);
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
// topBgLinear.setBackgroundResource(R.drawable.top_bg_with_triangle);
        betwayPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        betwayPop.setWidth(findViewById(R.id.top_bg_linear).getWidth() - 20);
        betwayPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        betwayPop.setOutsideTouchable(true);
        betwayPop.setFocusable(true);
        betwayPop.setContentView(waySwitchLayout);
        showPopupCenter(betwayPop);
        topArrow.setImageResource(R.drawable.arrow_up_white);
    }

    @Override
    public void showWay() {
        super.showWay();
        for (int i = 0; i < matchList.size(); i++) {
            if (lotteryType == i + 1) {
                if (matchListSub.get(i) != null)
                    matchListSub.get(i).clear();
                for (int j = 0; j < matchList.get(i).size(); j++) {
                    matchListSub.get(i).add(matchList.get(i).get(j));
                }
                lv.refreshContent();
                if (adapter.get(i) != null)
                    adapter.get(i).notifyDataSetChanged();
                lv.expandList();
            }
        }
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
                                        ArrayList<SportsJCLQItem> sportsItemList =
                                            new ArrayList<SportsJCLQItem>();
                                        try {
                                            addSportsItem(date, jo, sportsItemList);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        groupTemp.setItemJCLQList(sportsItemList);
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
                                                          matchList.get(j).get(index).getItemJCLQList());
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
                for (int j = 0; j < matchList.size(); j++) {
                    if (lotteryType == j + 1) {
                        for (int i = 0; i < matchList.get(j).size(); i++) {
                            SportsSFCBetGroup groupTemp = matchList.get(j).get(i);
                            groupTemp.setGameNumber("共" + groupTemp.getItemJCLQList().size() + "场赛事");
                        }
                        for (int i = 0; i < matchListSub.get(j).size(); i++) {
                            SportsSFCBetGroup groupTemp = matchListSub.get(j).get(i);
                            groupTemp.setGameNumber("共" + groupTemp.getItemJCLQList().size() + "场赛事");
                        }
                        break;
                    }
                }

// isClickable = true;
                lv.refreshContent();
                for (int i = 0; i < matchList.size(); i++) {
                    if (lotteryType == i + 1) {
                        adapter.get(i).notifyDataSetChanged();
                        break;
                    }
                }
                lv.expandList();
            }
            else if (status.equals("202")) {
                ViewUtil.showTipsToast(this, "暂无比赛数据");
            }
            getSuccess = true;
        }
        if (!getSuccess) {
            ViewUtil.showTipsToast(this, "比赛数据获取失败");
        }
    }

    private void resetZhusign(int lotteryType) {
        if (lotteryType == 2 || lotteryType == 5) {
            tv_zhu_sign.setText("(让分)主场");
        }
        else {
            tv_zhu_sign.setText("主场");
        }
    }

    @Override
    protected void initChoosed(String betCode) {
        if (betCode != null) {
            if (lotteryType == 1 || lotteryType == 4) {
                for (int i = 0; i < (isGuoguan ? matchList.get(0) : matchList.get(3)).size(); i++) {
                    ArrayList<SportsJCLQItem> sportsItemList =
                        (isGuoguan ? matchList.get(0) : matchList.get(3)).get(i).getItemJCLQList();// 根据日期分组

                    String gameDate = (isGuoguan ? matchList.get(0) : matchList.get(3)).get(i).getDate();
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
                                SportsJCLQItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        if (num == 1)
                                            item.setStatus(1, true);
                                        else if (num == 2)
                                            item.setStatus(0, true);
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 1 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 4 && isGuoguan == false)
                    clear();
            }
            else if (lotteryType == 2 || lotteryType == 5) {
                for (int i = 0; i < (isGuoguan ? matchList.get(1) : matchList.get(4)).size(); i++) {
                    ArrayList<SportsJCLQItem> sportsItemList =
                        (isGuoguan ? matchList.get(1) : matchList.get(4)).get(i).getItemJCLQList();// 根据日期分组
                    String gameDate = (isGuoguan ? matchList.get(1) : matchList.get(4)).get(i).getDate();
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
                                SportsJCLQItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        if (num == 1)
                                            item.setStatus(1, true);
                                        else if (num == 2)
                                            item.setStatus(0, true);
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 2 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 5 && isGuoguan == false)
                    clear();
            }
            else if (lotteryType == 3 || lotteryType == 6) {
                for (int i = 0; i < (isGuoguan ? matchList.get(2) : matchList.get(5)).size(); i++) {
                    ArrayList<SportsJCLQItem> sportsItemList =
                        (isGuoguan ? matchList.get(2) : matchList.get(5)).get(i).getItemJCLQList();// 根据日期分组
                    String gameDate = (isGuoguan ? matchList.get(2) : matchList.get(5)).get(i).getDate();
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
                                SportsJCLQItem item = sportsItemList.get(j);
                                if (item.getIdBetNum().equals(numbers[0])) {
                                    String[] number = numbers[1].split("\\,");
                                    int num = 0;
                                    String str = null;
                                    for (int l = 0; l < number.length; l++) {
                                        str = number[l];
                                        num = Integer.parseInt(str);
                                        if (num == 1)
                                            item.setStatus(0, true);
                                        else if (num == 2)
                                            item.setStatus(1, true);
                                    }
                                    selectedNum += 1;
                                }
                            }
                        }
                    }
                }
                if (lotteryTypeInit != 3 && isGuoguan == true)
                    clear();
                else if (lotteryTypeInit != 6 && isGuoguan == false)
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
     * @param sportsSfitemList 要存储的总场次列表
     * @throws JSONException 读取不到数据异常
     */
    private void addSportsItem(int date, JSONObject jo, ArrayList<SportsJCLQItem> sportsItemList)
        throws JSONException {
        SportsJCLQItem sportItem = new SportsJCLQItem();
        sportItem.setId(jo.getString("game_id"));
        sportItem.setWeekDay(String.valueOf(date));
        sportItem.setIdBetNum(jo.getString("no"));
        sportItem.setLeague(jo.getString("match_name"));
        sportItem.setMatchHomeTeamName(jo.getString("master"));
        sportItem.setMatchGuessTeamName(jo.getString("guest"));
        sportItem.setEndTime(jo.getString("stop_time"));
        sportItem.setMatchTime(jo.getString("match_time"));
        if (jo.has("let_score"))
            sportItem.setConcede(jo.getString("let_score"));
        if (jo.has("set_score"))
            sportItem.setTotalPoints(jo.getString("set_score"));
        sportItem.setStatus(0, false);
        sportItem.setStatus(1, false);
        String sp = null;
        if (jo.has("sp")) {
            sp = jo.getString("sp");
            if (sp != "null" && sp != "") {
                String[] spTemp = sp.split(",");
                sportItem.setOdds(0, spTemp[0].split("\\|")[1]);
                sportItem.setOdds(1, spTemp[1].split("\\|")[1]);
                sportsItemList.add(sportItem);
            }
        }
    }

// private void addSportsItem(int date, JSONObject jo, ArrayList<SportsJCLQItem> sportsItemList)
// throws JSONException {
// SportsJCLQItem sportItem = new SportsJCLQItem();
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
// sportItem.setOdds(0, spTemp[0].split("\\|")[1]);
// sportItem.setOdds(1, spTemp[1].split("\\|")[1]);
// if (jo.has("let_score"))
// sportItem.setConcede(jo.getString("let_score"));
// if (jo.has("set_score"))
// sportItem.setTotalPoints(jo.getString("set_score"));
// sportItem.setStatus(0, false);
// sportItem.setStatus(1, false);
// sportsItemList.add(sportItem);
// }

    @Override
    protected void bet() {
        super.bet();
        initBetList();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("bet_kind", "jclq");
        for (int i = 0; i < 3; i++) {
            if (lotteryType == i + 1 || lotteryType == i + 4) {
                bundle.putString("bet_term", playID[i]);
                bundle.putParcelableArrayList("bet_sports_data",
                                              (isGuoguan ? betList.get(i) : betList.get(i + 3)));
                break;
            }
        }
        bundle.putString("mode", "0000");
        bundle.putLong("endtime", endTimeMillis);
        bundle.putBoolean("fromBet", true);
        bundle.putString("about", "left");
        bundle.putBoolean("isGuoguan", isGuoguan);
        bundle.putBoolean("ifStartSelf", false);
        bundle.putString("forwardFlag", "收银台");
        bundle.putBoolean("is_continue_pass", true);
        bundle.putString("class_name", BetPaySports.class.getName());
        intent.putExtras(bundle);
        if (appState.getUsername() == null) {
            intent.setClass(JCLQActivity.this, Login.class);
// intent.setClass(JCLQActivity.this, StartUp.class);
        }
        else {
            intent.setClass(JCLQActivity.this, BetPaySports.class);
        }
        startActivityForResult(intent, 1);
    }

    private void initBetList() {
        for (int k = 0; k < 3; k++) {
            if (lotteryType == k + 1 || lotteryType == k + 4) {
                (isGuoguan ? betList.get(k) : betList.get(k + 3)).clear();
                for (int i = 0; i < (isGuoguan ? matchList.get(k) : matchList.get(k + 3)).size(); i++) {
                    ArrayList<SportsJCLQItem> sportsItemList =
                        (isGuoguan ? matchList.get(k) : matchList.get(k + 3)).get(i).getItemJCLQList();
                    for (int j = 0; j < sportsItemList.size(); j++) {
                        SportsJCLQItem item = sportsItemList.get(j);
                        if (item.getStatus(0) || item.getStatus(1)) {
                            (isGuoguan ? betList.get(k) : betList.get(k + 3)).add(item);
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
        for (int k = 0; k < 3; k++) {
            if (lotteryType == k + 1 || lotteryType == k + 4) {
                for (int i = 0; i < (isGuoguan ? matchListSub.get(k) : matchListSub.get(k + 3)).size(); i++) {
                    SportsSFCBetGroup sports =
                        (isGuoguan ? matchListSub.get(k) : matchListSub.get(k + 3)).get(i);
                    ArrayList<SportsJCLQItem> sportsItemList = sports.getItemJCLQList();
                    for (int j = 0; j < sportsItemList.size(); j++) {
                        SportsJCLQItem sportItem = sportsItemList.get(j);
                        sportItem.setStatus(0, false);
                        sportItem.setStatus(1, false);
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
        for (int i = 0; i < 3; i++) {
            if (lotteryType == i + 1 || lotteryType == i + 4) {
                SportsJCLQItem item =
                    (isGuoguan ? matchListSub.get(i) : matchListSub.get(i + 3)).get(groupPosition).getItemJCLQList().get(childPosition);
                if (item.getStatus(index)) {
                    item.reverseStatus(index);
                    // 如果减少了一项，则选中数减一
                    if (!item.getStatus(0) && !item.getStatus(1)) {
                        selectedNum--;
                    }
                    canClick = true;
                }
                else {
                    if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1)) {
                        ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
                    }
                    else {
                        // 如果增加了一项，则选中数加一
                        if (!item.getStatus(0) && !item.getStatus(1)) {
                            selectedNum++;
                        }
                        item.reverseStatus(index);
                        canClick = true;
                    }
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
