package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.JczqAnalyseAdapter;
import com.haozan.caipiao.adapter.JczqAnalyseAsiaOddsAdapter;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.JczqAnalyseAsiaOddsListItemData;
import com.haozan.caipiao.types.JczqAnalyseGroup;
import com.haozan.caipiao.types.JczqAnalyseListItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.haozan.caipiao.widget.TopMenuLayout;
import com.haozan.caipiao.widget.TopMenuLayout.OnTabSelectedItemListener;
import com.umeng.analytics.MobclickAgent;

public class JczqAnalyse
    extends BasicActivity
    implements OnTabSelectedItemListener, OnPageChangeListener {
    private static final String[] TYPE = new String[] {"总榜", "主榜", "客榜"};
    private static final String[] rank = new String[] {"overall_rank", "host_rank", "away_rank"};
    private static final String[] zc = new String[] {"overall_zc", "host_zc", "away_zc"};
    private static final String[] w = new String[] {"overall_w", "host_w", "away_w"};
    private static final String[] d = new String[] {"overall_d", "host_d", "away_d"};
    private static final String[] l = new String[] {"overall_l", "host_l", "away_l"};
    private static final String[] wq = new String[] {"overall_wq", "host_wq", "away_wq"};
    private static final String[] lq = new String[] {"overall_lq", "host_lq", "away_lq"};
    private static final String[] jq = new String[] {"overall_jq", "host_jq", "away_jq"};
    private static final String[] score = new String[] {"overall_score", "host_score", "away_score"};

    private String[] tabContent = new String[] {"对阵数据", "亚盘指数"};

    private TextView masterTv;
    private TextView guesterTv;
    private TextView timeTv;
    private TextView leageTv;
    private ProgressBar progress1;
    private ProgressBar progress2;
    private TopMenuLayout topMenuLayout;

    private CustomExpandleListView lv;
    private ListView oddsList;

    private JczqAnalyseAdapter adapter;
    private JczqAnalyseAsiaOddsAdapter oddsAdapter;

    private ArrayList<JczqAnalyseGroup> arrayList;
    private ArrayList<JczqAnalyseAsiaOddsListItemData> oddsArrayList;

    private View view1, view2;
    private View showFailPage1;
    private View showFailPage2;
    private TextView message1;
    private TextView message2;
    private static final String GET_FAIL = "查询失败,请稍后再试...";
    private static final String NO_DATA = "暂无分析数据";

    private String gameId;
    private String masterName;
    private String guesterName;
    private String time;
    private String leage;
    private String listStr;
    private LinearLayout yapeiTitle;

    private String dui;// 历史交锋所有数据
    private String duiListStr;
    private String jin;// 近期战绩所有数据
    private String jinListStr_away;
    private String jinListStr_host;
    private String pai;// 积分排名所有数据
    private String paiListStr_away;
    private String paiListStr_host;

    private boolean ifComHasData = false;// 判断是否已得到对阵数据
    private boolean ifOddsHasData = false;// 判断时候已得到亚赔数据
    private String kind = "jczq";

    private int dataType = 0;// 0:对阵数据 4:亚赔数据
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

// private View view1, view2;// 各个页卡

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jczq_analyse_viewflow);
        setUpViews();
        initData();
    }

    private void setUpViews() {
        topMenuLayout = (TopMenuLayout) this.findViewById(R.id.top_menu_layout);
        topMenuLayout.setTopMenuItemContent(tabContent);
        topMenuLayout.setTabSelectedListener(this);
        masterTv = (TextView) findViewById(R.id.tv_icon_zhu);
        guesterTv = (TextView) findViewById(R.id.tv_icon_ke);
        timeTv = (TextView) findViewById(R.id.time);
        leageTv = (TextView) findViewById(R.id.league);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.jczq_analyse_duizhen, null);
        view2 = inflater.inflate(R.layout.jczq_analyse_yapei, null);
        views.add(view1);
        views.add(view2);
        viewPagerAdapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try {
            kind = bundle.getString("kind");
            if (kind == null) {
                kind = "jczq";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        gameId = bundle.getString("game_id");
        masterName = bundle.getString("master_name");
        guesterName = bundle.getString("guester_name");
        leage = bundle.getString("leage");
        masterTv.setText(masterName);
        guesterTv.setText(guesterName);
        leageTv.setText(leage);

        lv = (CustomExpandleListView) view1.findViewById(R.id.match_list);
        oddsList = (ListView) view2.findViewById(R.id.list_odds);
        progress1 = (ProgressBar) view1.findViewById(R.id.progress_large);
        progress2 = (ProgressBar) view2.findViewById(R.id.progress_large);
        showFailPage1 = view1.findViewById(R.id.show_fail_page);
        showFailPage2 = view2.findViewById(R.id.show_fail_page);
        message1 = (TextView) view1.findViewById(R.id.message);
        message2 = (TextView) view2.findViewById(R.id.message);
        yapeiTitle = (LinearLayout) view2.findViewById(R.id.odds_sub_title);

        arrayList = new ArrayList<JczqAnalyseGroup>();
        oddsArrayList = new ArrayList<JczqAnalyseAsiaOddsListItemData>();
        adapter = new JczqAnalyseAdapter(JczqAnalyse.this, arrayList);
        lv.setAdapter(adapter, R.layout.jczq_analyse_group_view);
        oddsAdapter = new JczqAnalyseAsiaOddsAdapter(JczqAnalyse.this, oddsArrayList);
        oddsList.setAdapter(oddsAdapter);

        executeTask();
    }

    private void executeTask() {
        if (HttpConnectUtil.isNetworkAvailable(JczqAnalyse.this)) {
            GetAnalyseTask task = new GetAnalyseTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class GetAnalyseTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dataType == 0) {
                progress1.setVisibility(View.VISIBLE);
                showFailPage1.setVisibility(View.GONE);
            }
            else {
                progress2.setVisibility(View.VISIBLE);
                showFailPage2.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(JczqAnalyse.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(5, false, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (dataType == 0) {
                progress1.setVisibility(View.GONE);
                if (json == null) {
                    searchFail(0, message1);
                }
            }
            else {
                progress2.setVisibility(View.GONE);
                if (json == null) {
                    searchFail(0, message2);
                }
            }

            if (null != json) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    if (dataType == 0) {
                        showFailPage1.setVisibility(View.GONE);
                    }
                    else {
                        showFailPage2.setVisibility(View.GONE);
                    }
                    if (dataType == 0) {
                        try {
                            String data = analyse.getData(json, "response_data");
                            if (!"null".equals(data) && null != data && !"".equals(data) &&
                                !"{}".equals(data)) {
                                ifComHasData = true;
                                dui = analyse.getData(data, "dui");
                                jin = analyse.getData(data, "jin");
                                pai = analyse.getData(data, "pai");
                                getData(dui, jin, pai);
                                lv.refreshContent();
                                adapter.notifyDataSetChanged();
                                lv.expandList();
                            }
                            else {
                                searchFail(1, message1);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (dataType == 4) {
                        try {
                            listStr = analyse.getData(json, "response_data");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("[]".equals(listStr) == false) {
                            ifOddsHasData = true;
                            yapeiTitle.setVisibility(View.VISIBLE);
                            getOddsListData(oddsArrayList, listStr);
                            oddsAdapter.notifyDataSetChanged();
                        }
                        else {
                            searchFail(1, message2);
                            yapeiTitle.setVisibility(View.GONE);
                        }
                    }

                }
                else {
                    if (dataType == 0) {
                        searchFail(0, message1);
                    }
                    else {
                        searchFail(0, message2);
                    }

                }
            }
        }

        public void searchFail(int flag, TextView message) {
            if (flag == 0) {
                message.setText(GET_FAIL);
            }
            else if (flag == 1) {
                message.setText(NO_DATA);
            }

            if (dataType == 0) {
                ifComHasData = false;
                showFailPage1.setVisibility(View.VISIBLE);
            }
            else if (dataType == 4) {
                ifOddsHasData = false;
                showFailPage2.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 初始化访问参数
     * 
     * @author Vimcent 2013-3-15
     * @return
     */
    public HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", getServiceId());
        parameter.put("pid", LotteryUtils.getPid(JczqAnalyse.this));
        if ("jczq".equals(kind)) {
            parameter.put("game_id", gameId);
        }
        else {
            parameter.put("match_id", gameId);
        }

        if (dataType == 0) {
            parameter.put("host_name", HttpConnectUtil.encodeParameter(masterName));
            parameter.put("away_name", HttpConnectUtil.encodeParameter(guesterName));
        }
        else if (dataType == 4) {
            parameter.put("team_name", HttpConnectUtil.encodeParameter(masterName));
        }
        return parameter;
    }

    private String getServiceId() {
        String id = "";
        if (dataType == 0) {
            id = "2009070";
        }
        else if (dataType == 4) {
            id = "2009073";
        }
        return id;
    }

    public void getData(String dui, String jin, String pai) {
        JsonAnalyse analyse = new JsonAnalyse();
        time = analyse.getData(dui, "start_time");
        timeTv.setText(time);
        leage = analyse.getData(dui, "game_name");
        leageTv.setText(leage);

        duiListStr = analyse.getData(dui, "list");
        paiListStr_away = analyse.getData(pai, "away");
        paiListStr_host = analyse.getData(pai, "host");
        jinListStr_away = analyse.getData(jin, "away");
        jinListStr_host = analyse.getData(jin, "host");

        // 历史交锋数据添加
        JczqAnalyseGroup group0 = new JczqAnalyseGroup();
        group0.setGroupName("历史交锋");
        group0.setDataType(0);
        ArrayList<JczqAnalyseListItemData> array0 = new ArrayList<JczqAnalyseListItemData>();
        addTitle(array0, 0);
        try {
            JSONArray hallArray = new JSONArray(duiListStr);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                addHisItem(jo, array0, 0);
            }
        }
        catch (Exception e) {
        }
        group0.setArrayList(array0);
        arrayList.add(group0);

        // 主队积分排名数据添加
        JczqAnalyseGroup group1 = new JczqAnalyseGroup();
        group1.setGroupName("主队积分排名");
        group1.setDataType(1);
        ArrayList<JczqAnalyseListItemData> array1 = new ArrayList<JczqAnalyseListItemData>();
        addRankTitle(array1, 1);
        if (null != paiListStr_host && !"".equals(paiListStr_host)) {
            addRankItem(paiListStr_host, array1);
        }
        group1.setArrayList(array1);
        arrayList.add(group1);

        // 客队积分排名数据添加
        JczqAnalyseGroup group2 = new JczqAnalyseGroup();
        group2.setGroupName("客队积分排名");
        group2.setDataType(1);
        ArrayList<JczqAnalyseListItemData> array2 = new ArrayList<JczqAnalyseListItemData>();
        addRankTitle(array2, 1);
        if (!"null".equals(paiListStr_away)) {
            addRankItem(paiListStr_away, array2);
        }
        group2.setArrayList(array2);
        arrayList.add(group2);

        // 主队近期战绩数据添加
        JczqAnalyseGroup group3 = new JczqAnalyseGroup();
        group3.setGroupName("主队近期战绩");
        group3.setDataType(2);
        ArrayList<JczqAnalyseListItemData> array3 = new ArrayList<JczqAnalyseListItemData>();
        addTitle(array3, 2);
        try {
            JSONArray hallArray = new JSONArray(jinListStr_host);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                addHisItem(jo, array3, 2);
            }
        }
        catch (Exception e) {
        }
        group3.setArrayList(array3);
        arrayList.add(group3);

        // 客队近期战绩数据添加
        JczqAnalyseGroup group4 = new JczqAnalyseGroup();
        group4.setGroupName("客队近期战绩");
        group3.setDataType(3);
        ArrayList<JczqAnalyseListItemData> array4 = new ArrayList<JczqAnalyseListItemData>();
        addTitle(array4, 3);
        try {
            JSONArray hallArray = new JSONArray(jinListStr_away);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                addHisItem(jo, array4, 3);
            }
        }
        catch (Exception e) {
        }
        group4.setArrayList(array4);
        arrayList.add(group4);
    }

    /**
     * @author Vimcent 2013-3-14
     * @param paiListStr 存储所有排名数据的json字符串
     * @param array 保存积分排名数据的动态数组
     */
    private void addRankItem(String paiListStr, ArrayList<JczqAnalyseListItemData> array) {
        JczqAnalyseListItemData item;
        JsonAnalyse analyse = new JsonAnalyse();
        for (int i = 0; i < 3; i++) {
            if ("null".equals(analyse.getData(paiListStr, rank[i]))) {
                break;
            }
            item = new JczqAnalyseListItemData();
            item.setType(TYPE[i]);
            item.setRank(analyse.getData(paiListStr, rank[i]));// 排名
            item.setAllMatch(analyse.getData(paiListStr, zc[i]));// 总场次
            item.setWinNum(analyse.getData(paiListStr, w[i]));// 胜场次
            item.setEvenNum(analyse.getData(paiListStr, d[i]));// 平场次
            item.setLoseNum(analyse.getData(paiListStr, l[i]));// 负场次
            item.setGetScore(analyse.getData(paiListStr, wq[i]));// 进球数
            item.setLoseScore(analyse.getData(paiListStr, lq[i]));// 失球数
            item.setLastScore(analyse.getData(paiListStr, jq[i]));// 净球数
            item.setAllScore(analyse.getData(paiListStr, score[i]));// 积分
            item.setDataType(1);
            array.add(item);
        }
    }

    /**
     * 初始化积分排名的小标题
     * 
     * @author Vimcent 2013-3-16
     * @param array
     * @param dataType
     */
    public void addRankTitle(ArrayList<JczqAnalyseListItemData> array, int dataType) {
        JczqAnalyseListItemData item = new JczqAnalyseListItemData();
        // 初始化积分排名的小标题
        item.setType("类型");
        item.setRank("排名");// 排名
        item.setAllMatch("赛");// 总场次
        item.setWinNum("胜");// 胜场次
        item.setEvenNum("平");// 平场次
        item.setLoseNum("负");// 负场次
        item.setGetScore("进");// 进球数
        item.setLoseScore("失");// 失球数
        item.setLastScore("净");// 净球数
        item.setAllScore("积分");// 积分
        item.setDataType(dataType);
        array.add(item);
    }

    /**
     * @author Vimcent 2013-3-14
     * @param jo
     * @param array
     * @param dataType
     */
    private void addHisItem(JSONObject jo, ArrayList<JczqAnalyseListItemData> array, int dataType) {
        JczqAnalyseListItemData item = new JczqAnalyseListItemData();
        boolean ifAdd = true;
        try {
            if ("null".equals(jo.getString("game_name"))) {
                ifAdd = false;
            }
            if (ifAdd == true) {
                item.setLeague(jo.getString("game_name"));
                item.setTime(jo.getString("game_date"));
                item.setMaster(jo.getString("master_name"));
                item.setScore(jo.getString("result"));
                item.setGuest(jo.getString("guest_name"));
                if (dataType == 0) {
                    item.setHalfCourt(jo.getString("sf"));
                }
                item.setDataType(dataType);
            }

        }
        catch (Exception e) {
        }
        if (ifAdd == true) {
            array.add(item);
        }

    }

    /**
     * 初始化除排名以外的小标题
     * 
     * @author Vimcent 2013-3-14
     * @param array
     * @param dataType
     */
    public void addTitle(ArrayList<JczqAnalyseListItemData> array, int dataType) {
        JczqAnalyseListItemData item = new JczqAnalyseListItemData();
        item.setLeague("赛事");
        item.setTime("时间");
        item.setMaster("主队");
        item.setScore("比分");
        item.setGuest("客队");
        if (dataType == 0) {
            item.setHalfCourt("胜负");
        }
        item.setDataType(dataType);
        array.add(item);
    }

    /**
     * 解析亚赔数据
     * 
     * @author Vimcent 2013-3-15
     * @param oddsArrayList
     * @param json
     */
    private void getOddsListData(ArrayList<JczqAnalyseAsiaOddsListItemData> oddsArrayList, String json) {
        JSONArray hallArray;
        try {
            hallArray = new JSONArray(json);
            for (int i = 0; i < hallArray.length(); i++) {
                JczqAnalyseAsiaOddsListItemData item = new JczqAnalyseAsiaOddsListItemData();
                JSONObject jo = hallArray.getJSONObject(i);
                item.setCompany(jo.getString("gs"));
                item.setInitWater1(jo.getString("chu_q"));
                item.setInitWater2(jo.getString("chu_b"));
                item.setCurrentWater1(jo.getString("ji_q"));
                item.setCurrentWater2(jo.getString("ji_b"));
                item.setInitOdds(jo.getString("chu"));
                item.setCurrentOdds(jo.getString("ji"));
                oddsArrayList.add(item);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabSelectedAction(int selection) {
        if (viewPager.getCurrentItem() != selection) {
            viewPager.setCurrentItem(selection);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        topMenuLayout.check(position);
        if (position == 0) {
            dataType = 0;
            if (ifComHasData == false) {
                executeTask();
            }
        }
        else if (position == 1) {
            dataType = 4;
            if (oddsArrayList.size() == 0) {
                yapeiTitle.setVisibility(View.GONE);
                executeTask();
            }
            else {
                yapeiTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_sport_analyse";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

}
