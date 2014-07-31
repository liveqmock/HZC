package com.haozan.caipiao.activity.unite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.adapter.unite.UniteHallListAdapter;
import com.haozan.caipiao.connect.unite.GetUniteHallService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.types.unite.UniteListItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.weiboutil.MyListView;
import com.haozan.caipiao.util.weiboutil.MyListView.ListViewScrollStateChanged;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.umeng.analytics.MobclickAgent;

public class UniteHallActivity
    extends ContainTipsPageBasicActivity
    implements OnHeaderRefreshListener, OnItemClickListener, OnLastItemVisibleListener, OnPageChangeListener,
    OnClickListener, PopupSelectTypeClickListener {
    // the amount of the record in each page
    private final static int SIZE = 20;
    private final static int TYPE_NUM = 2;
    protected String noRecord = "没有合买记录\n别等人家啦，自己也搞个方案~";
    // 合买
    public static final String[] textArrayUnite = {"所有", "双色球", "大乐透", "竞彩足球", "3D"};
    public static final String[] uniteWay = {"unite_all", "unite_ssq", "unite_dlt", "unite_jczq", "unite_3d"};
    public static final String[] uniteKind = {"all", "ssq", "dlt", "jczq", "3d"};
    // 下拉刷新
    private TextView title;
    private String kind;
// private ArrayList<UniteListItem> jsonRecord;
    private ArrayList<ArrayList<UniteListItem>> arrays = new ArrayList<ArrayList<UniteListItem>>();
    private LinearLayout imgHelp;
    private ArrayList<UniteHallListAdapter> adapter = new ArrayList<UniteHallListAdapter>();;
    private int[] pageNo = {1, 1};
    // protected int normalPage3 = 1;
    protected int orderType = 0; // 0:按进度（默认）；1：按总额；2:按战绩
    protected int orderby = 1;// 0：升序；1：降序
// private RadioGroup rg;
// private RadioButton r1;
// private RadioButton r2;
    private String lottery_id = null;
    private Button btnUnite;
    private RelativeLayout btnUniteRe;
    // 传递给具体彩种的参数
    private boolean ifFromHall;
    private String betTerm;
    private String awardTime;
    private long endTime;
    private long gapTime;
    private boolean doUnite;

    boolean firstIsInc = false;// 进度是否为降序排列
    boolean secondIsInc = false;// 总额是否为降序排列

    String titleStr;
// private ImageButton toTopButton;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView[] triAngle;
    private int[] triAngleIds = {R.id.cursor1, R.id.cursor2};
    private TextView[] textView;
    private int[] textViewIds = {R.id.text1, R.id.text2};
    private int currIndex = 0;// 当前页卡编号
    private ProgressBar[] progress;
    private PullToRefreshListView[] pullToRefresh;
    private View[] showFailPage;
    private TextView[] message;
    private RelativeLayout tab1;
    private RelativeLayout tab2;
    private ImageView[] imgs;
    private int[] imgsIds = {R.id.img01, R.id.img02};
    private ListView[] actualListView;
    private View[] footView;
    private boolean[] ifGetMoreData = {false, false};
    private boolean[] ifClearArray = {false, false};
    private View[] view;
    private GetUniteHallTask task;
    private RelativeLayout termLayout;
    protected ImageView topArrow;
    private int lotteryType = 1;

    private BetShowLotteryWay[] wayDataArray;
    private SelecteTypePopupWindow popupWindowWaySelect;

// private View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unite_hall);
        initData();
        setupViews();
        init();
        loadData();
    }

    private void init() {
        title.setText(titleStr + "合买");
        if (kind.equals("all")) {
            btnUniteRe.setVisibility(View.GONE);
        }

        loadData();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (null != bundle)
            kind = bundle.getString("kind");
        if (null == kind) {
            kind = "all";
        }
        lottery_id = kind;
        for (int i = 0; i < uniteKind.length; i++) {
            if (uniteKind[i].equals(kind)) {
                lotteryType = i + 1;
            }
        }
        // 取到合买类别
        titleStr = LotteryUtils.getLotteryName(lottery_id);
        if (titleStr.equals("")) {
            titleStr = "所有";
        }
        if (null != bundle) {
            ifFromHall = bundle.getBoolean("from_hall");
            betTerm = bundle.getString("bet_term");
            awardTime = bundle.getString("awardtime");
            endTime = bundle.getLong("endtime");
            gapTime = bundle.getLong("gaptime");
        }
        doUnite = true;
        for (int i = 0; i < TYPE_NUM; i++) {
            ArrayList<UniteListItem> array = new ArrayList<UniteListItem>();
            arrays.add(array);
            UniteHallListAdapter ad = new UniteHallListAdapter(UniteHallActivity.this, array);
            adapter.add(ad);
        }

        wayDataArray = new BetShowLotteryWay[1];
        BetShowLotteryWay lotteryway = new BetShowLotteryWay();
        lotteryway.setUpsInf(textArrayUnite);
        wayDataArray[0] = lotteryway;
    }

    private void setupViews() {
        title = (TextView) this.findViewById(R.id.unite_title);

        tab1 = (RelativeLayout) findViewById(R.id.tab1);
        tab2 = (RelativeLayout) findViewById(R.id.tab2);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view = new View[TYPE_NUM];
        pullToRefresh = new PullToRefreshListView[TYPE_NUM];
        actualListView = new ListView[TYPE_NUM];
        progress = new ProgressBar[TYPE_NUM];
        showFailPage = new View[TYPE_NUM];
        message = new TextView[TYPE_NUM];
        triAngle = new ImageView[TYPE_NUM];
        textView = new TextView[TYPE_NUM];
        imgs = new ImageView[TYPE_NUM];
        footView = new View[TYPE_NUM];
        for (int i = 0; i < TYPE_NUM; i++) {
            view[i] = inflater.inflate(R.layout.unite_hall_viewflow_left, null);
            progress[i] = (ProgressBar) view[i].findViewById(R.id.progress_large);
            showFailPage[i] = view[i].findViewById(R.id.show_fail_page);
            message[i] = (TextView) view[i].findViewById(R.id.message);
            triAngle[i] = (ImageView) findViewById(triAngleIds[i]);
            textView[i] = (TextView) findViewById(textViewIds[i]);
            imgs[i] = (ImageView) findViewById(imgsIds[i]);
            pullToRefresh[i] = (PullToRefreshListView) view[i].findViewById(R.id.unite_hall_listview);
            pullToRefresh[i].setOnHeaderRefreshListener(this);
            pullToRefresh[i].setOnLastItemVisibleListener(this);
            actualListView[i] = pullToRefresh[i].getRefreshableView();
            actualListView[i].setOnItemClickListener(this);
            actualListView[i].setAdapter(adapter.get(i));
            views.add(view[i]);
            footView[i] = View.inflate(UniteHallActivity.this, R.layout.list_item_load_more_view, null);
        }
        triAngle[0].setVisibility(View.VISIBLE);
        triAngle[1].setVisibility(View.INVISIBLE);
        viewPagerAdapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
        termLayout = (RelativeLayout) findViewById(R.id.bet_top_term_layout);
        termLayout.setOnClickListener(this);
        topArrow = (ImageView) this.findViewById(R.id.arrow_top);
        tab1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
// pullToRefresh[0].onHeaderRefreshComplete();
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                }
                else {
                    progressGetData();
                }

            }
        });
        textView[orderType].setTextColor(getResources().getColor(R.color.red_text));
        tab2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
// pullToRefresh[1].onHeaderRefreshComplete();
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                else {
                    amountGetData();
                }
            }

        });
        btnUnite = (Button) findViewById(R.id.title_btinit_right);
        btnUnite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < LotteryUtils.LOTTERY_ID.length; i++) {
                    if (lottery_id.equals(LotteryUtils.LOTTERY_ID[i])) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("bet_way_bottom", 3);
                        bundle.putBoolean("from_hall", true);
                        bundle.putString("bet_term", betTerm);
                        bundle.putString("awardtime", awardTime);
                        bundle.putLong("endtime", endTime);
                        bundle.putLong("gaptime", gapTime);
                        bundle.putBoolean("bet_is_unite", doUnite);
                        intent.putExtras(bundle);
                        intent.setClass(mContext, LotteryUtils.lotteryKindclass[i]);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
        btnUniteRe = (RelativeLayout) findViewById(R.id.re_titleright);
        imgHelp = (LinearLayout) findViewById(R.id.bet_unite_help);
        imgHelp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("url", LotteryUtils.HELP_URL + "#hemai");
                bundle.putString("title", "帮助说明");
                intent.putExtras(bundle);
                intent.setClass(UniteHallActivity.this, WebBrowser.class);
                startActivity(intent);
            }
        });
    }

    protected void amountGetData() {
        if (orderType != 1)
            orderType = 1;// 按总额
        ifClearArray[orderType] = true;
        ifGetMoreData[orderType] = true;
        if (secondIsInc) {
            if (orderby != 1)
                orderby = 1;// 降序
            secondIsInc = false;
            imgs[orderType].setImageResource(R.drawable.icon_down);
            backTop(actualListView[1]);
// toTopButton.setVisibility(View.GONE);
            pageNo[orderType] = 1;
            loadData();
        }
        else {
            if (orderby != 0)
                orderby = 0;// 升序
            secondIsInc = true;
            imgs[orderType].setImageResource(R.drawable.icon_up);
            backTop(actualListView[1]);
// toTopButton.setVisibility(View.GONE);
            pageNo[orderType] = 1;
            loadData();
        }
    }

    protected void progressGetData() {
        if (orderType != 0)
            orderType = 0;// 按进度
        ifClearArray[orderType] = true;
        ifGetMoreData[orderType] = true;
        if (firstIsInc) {
            firstIsInc = false;
            if (orderby != 1)
                orderby = 1;// 降序
            imgs[orderType].setImageResource(R.drawable.icon_down);
            backTop(actualListView[0]);
            // toTopButton.setVisibility(View.GONE);
            pageNo[orderType] = 1;
            loadData();
        }
        else {
            if (orderby != 0)
                orderby = 0;// 升序
            firstIsInc = true;
            imgs[orderType].setImageResource(R.drawable.icon_up);
            backTop(actualListView[0]);
            // toTopButton.setVisibility(View.GONE);
            pageNo[orderType] = 1;
            loadData();
        }
    }

    protected void setFlurrySecond() {
        LotteryApp appState = ((LotteryApp) getApplicationContext());
        Map<String, String> map = new HashMap<String, String>();
        map.put("zzc_desc", "username [" + appState.getUsername() + "]: load unite_hall more");
        FlurryAgent.onEvent("user unite_hall load", map);
    }

    public class GetUniteHallTask
        extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress[orderType].setVisibility(View.VISIBLE);
            showFailPage[orderType].setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Object... params) {
            GetUniteHallService getUniteHall =
                new GetUniteHallService(UniteHallActivity.this, orderType, orderby, pageNo[orderType], SIZE,
                                        lottery_id);
            String json = null;
            try {
                json = getUniteHall.sending();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(Object result) {
            dismissProgress();
            progress[orderType].setVisibility(View.GONE);
            pullToRefresh[orderType].onHeaderRefreshComplete();
            actualListView[orderType].removeFooterView(footView[orderType]);
            ifGetMoreData[orderType] = true;

            String inf = null;
            String json = (String) result;
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    int allSize = 0;
                    String data = analyse.getData(json, "response_data");
                    if (ifClearArray[orderType]) {
                        arrays.get(orderType).clear();
                    }
                    ifClearArray[orderType] = false;

                    if (data.equals("[]") == false) {
                        showFailPage[orderType].setVisibility(View.GONE);
                        pageNo[orderType]++;
                        pullToRefresh[orderType].setVisibility(View.VISIBLE);
                        int lastSize = arrays.get(orderType).size();
                        getUniteHallArray(arrays.get(orderType), data);
                        allSize = arrays.get(orderType).size();
                        if (allSize - lastSize < SIZE) {
                            ifGetMoreData[orderType] = false;
                        }
                        adapter.get(orderType).notifyDataSetChanged();
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UniteHallActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UniteHallActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(failTips);
                }
            }
        }
    }

    private void showNoData() {
        if (arrays.get(orderType).size() == 0) {
            message[orderType].setText(noRecord);
            showFailPage[orderType].setVisibility(View.VISIBLE);
        }
        actualListView[orderType].removeFooterView(footView[orderType]);
        adapter.get(orderType).notifyDataSetChanged();
    }

    public void getUniteHallArray(ArrayList<UniteListItem> jsonRecord, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    UniteListItem item = new UniteListItem();
                    JSONObject jo = hallArray.getJSONObject(i);
                    item.setProgramId(jo.getString("program_id"));// 方案编号
                    item.setSponsor(jo.getString("user_id"));// 发起人ID
                    item.setNickname(jo.getString("nickname"));// 发起人昵称
                    item.setPrice(String.valueOf(Double.parseDouble(jo.getString("per_amount"))));// 每份金额
                    item.setTotalMoney(String.valueOf(Integer.parseInt(jo.getString("all_num")) *
                        Double.parseDouble(jo.getString("per_amount"))));// 方案金额
                    item.setLastAmount(String.valueOf(Integer.parseInt(jo.getString("all_num")) -
                        Integer.parseInt(jo.getString("bought_num"))));// 剩余份数
                    item.setScheduled(jo.getString("rate"));// 进度
                    item.setTempAmount("1");
                    item.setKind(LotteryUtils.getLotteryName(jo.getString("cat_id")));
                    String paulNum = jo.getString("paul_num");
                    int allNum = jo.getInt("all_num");
                    item.setGuarantee(paulNum);// 保底份数
                    int num = 0;
                    if (null != paulNum) {
                        num = Integer.valueOf(paulNum);
                    }
                    item.setGuaRate(String.valueOf((int) (num * 100 / allNum)));// 保底比例
                    item.setProgressBitmap(createProgressBitmap(Integer.parseInt(item.getScheduled()),
                                                                Integer.parseInt(item.getGuaRate())));
                    jsonRecord.add(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BitmapDrawable createProgressBitmap(int progressNum, int guaranteedNum) {
        BitmapDrawable circleRemaining =
            (BitmapDrawable) getResources().getDrawable(R.drawable.circle_remaining);
        BitmapDrawable circleGuaranteed =
            (BitmapDrawable) getResources().getDrawable(R.drawable.circle_guaranteed);
        BitmapDrawable circleBount = (BitmapDrawable) getResources().getDrawable(R.drawable.circle_bought);
        BitmapDrawable circleTop = (BitmapDrawable) getResources().getDrawable(R.drawable.circle_top);
        int width = circleRemaining.getMinimumWidth();
        int height = circleRemaining.getMinimumHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(circleRemaining.getBitmap(), 0, 0, new Paint(1));
        Bitmap bitmapBought = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasBought = new Canvas(bitmapBought);
        creatCircle(canvasBought, circleBount.getBitmap(), 0, progressNum, width, height);
        Bitmap bitmapGuarantee = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasGuarantee = new Canvas(bitmapGuarantee);
        creatCircle(canvasGuarantee, circleGuaranteed.getBitmap(), progressNum, guaranteedNum, width, height);
        Bitmap bitmapRemaining = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasRemaining = new Canvas(bitmapRemaining);
        creatCircle(canvasRemaining, circleRemaining.getBitmap(), progressNum + guaranteedNum, 100 -
            progressNum - guaranteedNum, width, height);
        canvas.drawBitmap(bitmapBought, 0, 0, new Paint(1));
        canvas.drawBitmap(bitmapGuarantee, 0, 0, new Paint(1));
        canvas.drawBitmap(bitmapRemaining, 0, 0, new Paint(1));
        Bitmap bitmap1 = circleRemaining.getBitmap();
        Bitmap bitmap2 = circleTop.getBitmap();
        canvas.drawBitmap(bitmap2, (int) ((bitmap1.getWidth() - bitmap2.getWidth()) / 2),
                          (int) ((bitmap1.getHeight() - bitmap2.getHeight()) / 2), new Paint(1));
        if (bitmapBought != null) {
            bitmapBought.recycle();
        }
        if (bitmapGuarantee != null) {
            bitmapGuarantee.recycle();
        }
        if (bitmapRemaining != null) {
            bitmapRemaining.recycle();
        }
        return new BitmapDrawable(bitmap);
    }

    private void creatCircle(Canvas canvas, Bitmap bitmapCircle, int progressNumStart, int progressNum,
                             int width, int height) {
        int start;
        int progress;
        if (progressNumStart + progressNum > 100) {
            start = 0;
            progress = progressNumStart;
        }
        else {
            start = progressNumStart + progressNum;
            progress = 100 - progressNum;
        }
        RectF rect = new RectF(0, 0, width, height);
        Path path = new Path();
        path.moveTo(width / 2, height / 2);
        path.addArc(rect, -90 + (float) (3.6 * start), (float) (3.6 * progress));
        path.lineTo(width / 2, height / 2);
        path.close();
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        canvas.drawBitmap(bitmapCircle, 0, 0, new Paint(1));
    }

    public void showFail(String str) {
        showFailPage[orderType].setVisibility(View.VISIBLE);
        message[orderType].setText(str);
        actualListView[orderType].removeFooterView(footView[orderType]);
        arrays.get(orderType).clear();
        adapter.get(orderType).notifyDataSetChanged();
    }

    /**
     * 返回ListView顶部
     * 
     * @param view Listview
     */
    public static void backTop(ListView view) {
        if (!view.isStackFromBottom()) {
            view.setStackFromBottom(true);
        }
        view.setStackFromBottom(false);
    }

    /**
     * 返回ListView顶部图标显示
     * 
     * @param view Listview
     * @param imageButton 返回顶部图标
     */
    public static void backTopShow(MyListView listview, final ImageButton imageButton) {

        listview.setChanged(new ListViewScrollStateChanged() {

            @Override
            public void onScrollStateChangedView(int VisiblePosition) {
                if (VisiblePosition > 3) {
                    imageButton.setVisibility(View.VISIBLE);
                }
                else {
                    imageButton.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open group buy hall");
        String eventName = "v2 open group buy hall";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_group_buy_hall";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(UniteHallActivity.this)) {
            if (null != task) {
                task.cancel(true);
            }
            task = new GetUniteHallTask();
            task.execute();
        }
        else {
            pullToRefresh[orderType].onHeaderRefreshComplete();
            showFail(noNetTips);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UniteListItem item = arrays.get(orderType).get(position - 1);

        Intent intent = new Intent();
        intent.setClass(this, UniteHallDetail.class);
        Bundle b = new Bundle();
        b.putString("program_id", item.getProgramId());
        b.putDouble("bet_money", Double.valueOf(item.getTotalMoney()));
        b.putInt("last_amount", Integer.valueOf(item.getLastAmount()));
        b.putDouble("each_price", Double.valueOf(item.getPrice()));
        intent.putExtras(b);
        this.startActivity(intent);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        pageNo[orderType] = 1;
        ifClearArray[orderType] = true;
        loadData();
    }

    @Override
    public void onLastItemVisible() {
        if (ifGetMoreData[orderType]) {
            actualListView[orderType].addFooterView(footView[orderType]);
            loadData();
        }
        else {
            actualListView[orderType].removeFooterView(footView[orderType]);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        orderType = position;
        pullToRefresh[position].onHeaderRefreshComplete();
        triAngle[position].setVisibility(View.VISIBLE);
        triAngle[Math.abs(position - 1)].setVisibility(View.INVISIBLE);
        progress[Math.abs(position - 1)].setVisibility(View.GONE);
        textView[position].setTextColor(getResources().getColor(R.color.red_text));
        textView[Math.abs(position - 1)].setTextColor(getResources().getColor(R.color.light_purple));

        if (arrays.get(position).size() == 0) {
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bet_top_term_layout) {
            showPopView();
        }
    }

    private void showPopView() {
        wayDataArray[0].setSelectedIndex(lotteryType - 1);

        popupWindowWaySelect = new SelecteTypePopupWindow(UniteHallActivity.this, wayDataArray);
        popupWindowWaySelect.init();
        popupWindowWaySelect.setPopupSelectTypeListener(this);
        popupWindowWaySelect.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                topArrow.setImageResource(R.drawable.arrow_down_white);
            }
        });
        topArrow.setImageResource(R.drawable.arrow_up_white);
        popupWindowWaySelect.showAsDropDown(termLayout);
    }

    private void showWay() {
        for (int i = 0; i < TYPE_NUM; i++) {
            firstIsInc = false;
            secondIsInc = false;
            imgs[i].setImageResource(R.drawable.icon_down);

            pageNo[i] = 1;
            ifClearArray[i] = true;
            ifGetMoreData[i] = true;
            backTop(actualListView[i]);
            arrays.get(i).clear();
        }
        orderby = 1;
        loadData();
    }

    @Override
    public void selecteType(int type, int index) {
        lotteryType = index + 1;
        databaseData.putString("unite_way", uniteWay[index]);
        databaseData.commit();
        if (index == 0) {
            title.setText("所有合买");
            btnUniteRe.setVisibility(View.GONE);
        }
        else {
            title.setText(textArrayUnite[index] + "合买");
            btnUniteRe.setVisibility(View.VISIBLE);
        }
        lottery_id = uniteKind[index];
        popupWindowWaySelect.dismiss();
        showWay();
    }
}
