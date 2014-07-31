package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.guess.LotteryGuessHome;
import com.haozan.caipiao.adapter.ViewPagerAdapter;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.GuessControl;
import com.haozan.caipiao.control.HallControl;
import com.haozan.caipiao.control.LotteryInfControl;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.HallItem;
import com.haozan.caipiao.types.LotteryInf;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.CommonUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.cache.FileCache;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.PredicateLayout;
import com.haozan.caipiao.widget.RefreshLayout;
import com.haozan.caipiao.widget.RefreshLayout.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.banner.CirclePageIndicator;
import com.umeng.analytics.MobclickAgent;

/**
 * 购彩大厅
 * 
 * @author peter_feng
 * @create-time 2013-6-27 下午1:55:11
 */
public class Hall
    extends BasicActivity
    implements OnClickListener, OnHeaderRefreshListener, OnTouchListener {

    private LinearLayout mLayoutTitle;
    private TextView mTvTitleVersion;
    private LinearLayout mLayoutTitleMessage;
    private boolean hasPersonalMessage = false;
    private TextView mTvMessageNewFlag;

    private ArrayList<View> views;
    private int mBannerScrollTime = 5000;
    private RelativeLayout mlayoutBanner;
    private ViewPager mBannerViewPager;
    private ViewPagerAdapter mBannerViewPagerAdapter;
    private CirclePageIndicator mPageIndicator;
    private ImageView mIvBannerClose;

    private LinearLayout mLayoutHall;
    private ArrayList<HallItem> mHallItemList;

    private ArrayList<Bitmap> bitmapList;

    private RefreshLayout mLayoutRefresh;

    private HallControl mHallControl;
    private LotteryInfControl mLotteryInfControl;

    private boolean isInitLottery = false;
    private boolean isShowHallViews = false;

    private ArrayList<TextView> mCountTimeTvList;

    private TextView mTvGuess = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HallControl.BANNER_POLLING:
                    if (mBannerViewPager.getCurrentItem() == views.size() - 1) {
                        mBannerViewPager.setCurrentItem(0);
                    }
                    else {
                        mBannerViewPager.setCurrentItem(mBannerViewPager.getCurrentItem() + 1);
                    }
                    mHandler.sendEmptyMessageDelayed(HallControl.BANNER_POLLING, mBannerScrollTime);
                    break;

                case ControlMessage.DOWNLOAD_FLASH_PIC:
                    String url = (String) msg.obj;
                    mHallControl.downloadPic(url);
                    break;

                case ControlMessage.HAS_BANNER_INF:
                    if (bitmapList == null) {
                        bitmapList = new ArrayList<Bitmap>();
                    }

                    mHallControl.addBannerList();
                    sendEmptyMessage(HallControl.BANNER_POLLING);
                    break;

                case ControlMessage.HALL_NOTICE:
                    String[] inf = (String[]) msg.obj;// inf数组三个数据（公告内容，跳转到网页显示的地址和公告时间）
                    showUrgencyNoticeDialog(inf[0]);
                    break;

                case ControlMessage.DOWNLOAD_FINISH:
                    // 数组0代表下载是否成功（200代表成功），1代表下载的url
                    String[] downloadInf = (String[]) msg.obj;

                    if (downloadInf[0].equals("200")) {
                        if (mHallControl.isBanner(downloadInf[1])) {
                            showBanner(downloadInf[1]);
                        }
                    }
                    break;

                case ControlMessage.SHOW_PROGRESS:
                    showProgress();
                    break;

                case ControlMessage.DISMISS_PROGRESS:
                    dismissProgress();
                    break;

                case ControlMessage.PLUGIN_INF:
                    String json = (String) msg.obj;
                    mHallControl.toMapPlugin(json);
                    break;

                case HallControl.HALL_COUNT_TIME:
                    mHandler.sendEmptyMessageDelayed(HallControl.HALL_COUNT_TIME, 1000);

                    if (isInitLottery == false && HallControl.sLotteryInf.size() > 0) {
                        refreshHallItem();
                        isInitLottery = true;
                    }
                    else if (HallControl.sLotteryInf.size() != 0) {
                        mHallControl.minusGlobalTime();
                        refreshCountTimeView();
                    }
                    break;

                case ControlMessage.HALL_GUESS_COTENT:
                    if (mTvGuess != null) {
                        String guessContent = "数据获取失败";
                        if (msg.obj != null) {
                            guessContent = (String) msg.obj;
                        }

                        mTvGuess.setText(guessContent);
                    }
                    break;

                case ControlMessage.REVEICE_LOTTERY_INF:
                    // arg1彩种类型，是否成功（200代表成功，405代表失败），彩种名称null或者""代表所有彩种
                    mLayoutRefresh.onHeaderRefreshComplete();
                    if (msg.arg2 == 200 && (msg.obj == null || ((String) msg.obj).equals(""))) {
                        mHandler.removeMessages(HallControl.HALL_COUNT_TIME);
                        refreshHallItem();
                        mHandler.sendEmptyMessage(HallControl.HALL_COUNT_TIME);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_hall);
        setupViews();
        init();
    }

    private void refreshCountTimeView() {
        for (int i = 0; i < mHallItemList.size(); i++) {
            HallItem hallItem = mHallItemList.get(i);
            LotteryInf lotteryInf = HallControl.sLotteryInf.get(hallItem.getId());

            if (lotteryInf != null) {
                if (lotteryInf.getLastTimeMillis() > 0) {
                    if (hallItem.getLastTime() != null && checkTimeChange(lotteryInf.getLastTimeMillis())) {
                        hallItem.setLastTime(TimeUtils.getCountDownTime(lotteryInf.getLastTimeMillis()));
                        mCountTimeTvList.get(i).setText(hallItem.getLastTime());
                    }
                }
                else if ((lotteryInf.getLastTimeMillis() < -1 || lotteryInf.getLastTimeMillis() == 0) &&
                    !mCountTimeTvList.get(i).getText().toString().equals("投注已截止")) {
                    mCountTimeTvList.get(i).setText("投注已截止");
                }
            }
        }
    }

    private void refreshHallItem() {
        mHallControl.setHallItem(mHallItemList);

        for (int i = 0; i < mHallItemList.size(); i++) {
            getView(mLayoutHall.getChildAt(i + 1), i);
        }
    }

    private void showHallItem() {
        mLayoutHall.removeAllViews();
        mCountTimeTvList.clear();

        addHallListTop();

        mHallControl.setHallItem(mHallItemList);
        for (int i = 0; i < mHallItemList.size(); i++) {
            mLayoutHall.addView(getView(null, i));
        }

        addHallListBottom();
        isShowHallViews = true;
    }

    private void addHallListTop() {
        View itemTopView = View.inflate(mContext, R.layout.include_hall_list_top, null);
        TextView tvAppCenter = (TextView) itemTopView.findViewById(R.id.app_center);
        tvAppCenter.setOnClickListener(this);
        TextView tvPointReward = (TextView) itemTopView.findViewById(R.id.point_reward);
        tvPointReward.setOnClickListener(this);

        mLayoutHall.addView(itemTopView);
    }

    private void addHallListBottom() {
        View itemBottomView = View.inflate(mContext, R.layout.include_hall_list_bottom, null);
        RelativeLayout layoutEditLottery =
            (RelativeLayout) itemBottomView.findViewById(R.id.edit_lottery_layout);
        layoutEditLottery.setOnClickListener(this);
        RelativeLayout layoutMap = (RelativeLayout) itemBottomView.findViewById(R.id.map_layout);
        layoutMap.setOnClickListener(this);

        mLayoutHall.addView(itemBottomView);
    }

    private void getGuessData() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            GuessControl guessControl = new GuessControl(this, mHandler);
            guessControl.setHallGuessContent();
        }
    }

    /**
     * 组建大厅列表每一项
     * 
     * @param itemView 是否已经建好了view，直接传入修改显示内容
     * @param position 生成哪个彩种
     * @return
     */
    private View getView(View itemView, final int position) {
        HallItem hallItem = mHallItemList.get(position);

        boolean isNotShow = (itemView == null);// 是否已经显示了大厅列表view，这次只更新内容

        String id = hallItem.getId();
        if (id != null) {
            if (id.equals(LotteryUtils.GUESS) && isNotShow) {
                itemView = View.inflate(mContext, R.layout.hall_guess_item, null);
                itemView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(Hall.this, LotteryGuessHome.class);
                        Hall.this.startActivity(intent);
                    }
                });
                mTvGuess = (TextView) itemView.findViewById(R.id.guess_title);

                if (isNotShow) {
                    mCountTimeTvList.add(mTvGuess);
                }
            }
            else if (id.equals(LotteryUtils.GUESS)) {
                getGuessData();
            }
            else if (id.equals(LotteryUtils.JCZQ) || id.equals(LotteryUtils.JCLQ)) {
                if (isNotShow) {
                    itemView = View.inflate(mContext, R.layout.hall_sports_item, null);
                    TextView itemName = (TextView) itemView.findViewById(R.id.bet_item_name);
                    ImageView itemIcon = (ImageView) itemView.findViewById(R.id.bet_item_icon);

                    RelativeLayout toHistory = (RelativeLayout) itemView.findViewById(R.id.history_layout);
                    toHistory.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mHallControl.topClick(Hall.this, mHallItemList.get(position));
                        }
                    });

                    TextView tips1 = (TextView) itemView.findViewById(R.id.sports_tips1);
                    TextView tips2 = (TextView) itemView.findViewById(R.id.sports_tips2);
                    RelativeLayout toBet = (RelativeLayout) itemView.findViewById(R.id.bet_layout);
                    toBet.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mHallControl.bottomClick(Hall.this, mHallItemList.get(position));
                        }
                    });

                    if (id.equals(LotteryUtils.JCZQ)) {
                        itemName.setText("竞彩足球");
                        itemIcon.setBackgroundResource(R.drawable.hall_jczq_icon_normal);
                        tips1.setText("返奖率高达69%");
                        tips2.setText("每天早上九点开售");
                    }
                    else if (id.equals(LotteryUtils.JCLQ)) {
                        itemName.setText("竞彩篮球");
                        itemIcon.setBackgroundResource(R.drawable.hall_jclq_icon_normal);
                        tips1.setText("返奖率高达69%");
                        tips2.setText("最高奖金一百万");
                    }
                }

                TextView team = (TextView) itemView.findViewById(R.id.sports_team);
                team.setText(hallItem.getLastNum());

                if (isNotShow) {
                    mCountTimeTvList.add(team);
                }
            }
            else {
                if (isNotShow) {
                    itemView = View.inflate(mContext, R.layout.hall_each_item, null);
                    ImageView ivIcon = (ImageView) itemView.findViewById(R.id.bet_item_icon);
                    ivIcon.setBackgroundResource(hallItem.getIcon());
                    TextView tvName = (TextView) itemView.findViewById(R.id.bet_item_name);
                    tvName.setText(hallItem.getName());
                    RelativeLayout toHistory = (RelativeLayout) itemView.findViewById(R.id.history_layout);
                    toHistory.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mHallControl.topClick(Hall.this, mHallItemList.get(position));
                        }
                    });
                }

                TextView tvLastTerm = (TextView) itemView.findViewById(R.id.bet_term);
                tvLastTerm.setText(hallItem.getLastTerm());
                TextView tvLastOpenTime = (TextView) itemView.findViewById(R.id.bet_time);
                tvLastOpenTime.setText(hallItem.getLastOpenTime());
                TextView tvExtra = (TextView) itemView.findViewById(R.id.lottery_jackpot);
                tvExtra.setText(hallItem.getExtraInf());
                TextView tvCountTime = (TextView) itemView.findViewById(R.id.bet_count_down);
                tvCountTime.setText(hallItem.getLastTime());
                RelativeLayout toBet = (RelativeLayout) itemView.findViewById(R.id.bet_layout);
                toBet.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mHallControl.bottomClick(Hall.this, mHallItemList.get(position));
                    }
                });
                PredicateLayout openBallsLayout = (PredicateLayout) itemView.findViewById(R.id.open_balls);
                LotteryUtils.drawBallsLayout(mContext, openBallsLayout, hallItem.getId(),
                                             hallItem.getLastNum());

                if (isNotShow) {
                    mCountTimeTvList.add(tvCountTime);
                }
            }
        }

        return itemView;
    }

    /**
     * 时间大于1小时则一分钟更新一次
     * 
     * @param lastTimeMillis
     * @return
     */
    private boolean checkTimeChange(long lastTimeMillis) {
        int seconds = (int) lastTimeMillis / 1000;
        if (seconds < 3600 || seconds % 60 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    private void showBanner(String url) {
        Bitmap bitmap = FileCache.resizeImage(FileCache.getPicFilePath(url), 480, 800);
        if (bitmap != null) {
            int tempWidth = bitmap.getWidth();
            int tempHeight = bitmap.getHeight();
            if (tempHeight != 0) {
                RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) mlayoutBanner.getLayoutParams();
                params.height =
                    (int) (tempHeight * ((double) CommonUtil.getScreenWdith(mContext) / (double) tempWidth));
                mlayoutBanner.setLayoutParams(params);
            }

            mlayoutBanner.setVisibility(View.VISIBLE);

            ImageView img = new ImageView(Hall.this);
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mHallControl.OnClickBanner(mBannerViewPager.getCurrentItem());
                }
            });

            views.add(img);

            if (views.size() > 1) {
                mPageIndicator.setVisibility(View.VISIBLE);
            }

            img.setBackgroundDrawable(new BitmapDrawable(bitmap));

            bitmapList.add(bitmap);

            mBannerViewPagerAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        views = new ArrayList<View>();

        mCountTimeTvList = new ArrayList<TextView>();

        mHallItemList = new ArrayList<HallItem>();

        mHallControl = new HallControl(this, mHandler);
        mLotteryInfControl = new LotteryInfControl(this, mHandler);
    }

    protected void setupViews() {
        setupMainViews();
        setupBannerViews();
    }

    private void setupMainViews() {
        mLayoutTitle = (LinearLayout) this.findViewById(R.id.app_name_layout);
        mLayoutTitle.setOnClickListener(this);
        mTvTitleVersion = (TextView) this.findViewById(R.id.version);

        mLayoutTitleMessage = (LinearLayout) this.findViewById(R.id.message_layout);
        mLayoutTitleMessage.setOnClickListener(this);
        mTvMessageNewFlag = (TextView) this.findViewById(R.id.message_new);

        mLayoutRefresh = (RefreshLayout) this.findViewById(R.id.main_pull_refresh_view);
        mLayoutRefresh.setOnHeaderRefreshListener(this);

        mLayoutHall = (LinearLayout) this.findViewById(R.id.hall_list);
    }

    private void setupBannerViews() {
        mBannerViewPagerAdapter = new ViewPagerAdapter(views);
        mBannerViewPager = (ViewPager) this.findViewById(R.id.pager);
        mBannerViewPager.setAdapter(mBannerViewPagerAdapter);
        mBannerViewPager.setOnTouchListener(this);

        mlayoutBanner = (RelativeLayout) this.findViewById(R.id.banner_layout);
        mPageIndicator = (CirclePageIndicator) this.findViewById(R.id.indicator);
        mPageIndicator.setViewPager(mBannerViewPager);
        mIvBannerClose = (ImageView) this.findViewById(R.id.hide_banner);
        mIvBannerClose.setOnClickListener(this);
    }

    protected void init() {
        mHallControl.showNotice();

        mHallControl.showBanners();

        mHallControl.vipVersionControl();

        showHallItem();

        initBroadcast();

        mHandler.sendEmptyMessage(HallControl.HALL_COUNT_TIME);

        mHallControl.wakeLock();
    }

    private void showUrgencyNoticeDialog(String notice) {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setMessage(notice).setNegativeButton("取  消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("查看公告", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHallControl.toNotice();
            }
        });
        CustomDialog dlgNotice = customBuilder.create();
        dlgNotice.show();
    }

    private void initBroadcast() {
        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        // 为 BroadcastReceiver 指定 action ，使之用于接收同 action 的广播
        filter.addAction(getResources().getString(R.string.broadcast_name));
        // 以编程方式注册 BroadcastReceiver 。配置方式注册 BroadcastReceiver 的例子见
        // AndroidManifest.xml 文件
        // 一般在 OnStart 时注册，在 OnStop 时取消注册
        this.registerReceiver(receiver, filter);
    }

    private void getMessageStatus() {
        if (appState.getUsername() != null) {
            if (appState.getMessageNumber() <= 0) {
                if (HttpConnectUtil.isNetworkAvailable(this)) {
                    GetMessageTask task = new GetMessageTask();
                    task.execute();
                }
            }
            else {
                hasPersonalMessage = true;
                refreshMessageIcon();
            }
        }
    }

    private void refreshMessageStatus() {
        if (appState.getUsername() != null) {
            if (appState.getMessageNumber() <= 0) {
                hasPersonalMessage = false;
            }
            else {
                hasPersonalMessage = true;
            }
        }
        refreshMessageIcon();
    }

    private void refreshMessageIcon() {
        if (hasPersonalMessage) {
            mTvMessageNewFlag.setVisibility(View.VISIBLE);
        }
        else {
            mTvMessageNewFlag.setVisibility(View.GONE);
        }
    }

    private UpdateReceiver receiver;

    public class UpdateReceiver
        extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastType = intent.getStringExtra("type");
            if (broadcastType != null) {
                if (broadcastType.equals("invalidate_notice")) {
                    refreshMessageStatus();
                }
                else if (broadcastType.equals("logoff")) {
                    refreshMessageStatus();
                }
            }
        }
    }

    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open hall");
        String eventName = "v2 open hall";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        mHandler.removeCallbacksAndMessages(null);
        disposeRelease();
    }

    private void disposeRelease() {
        if (bitmapList != null && bitmapList.size() > 0) {
            int lenght = bitmapList.size();
            for (int i = 0; i < lenght; i++) {
                bitmapList.get(i).recycle();
            }
        }

        HallControl.sLotteryInf.clear();

        mHallControl.wakeUnclok();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.app_name_layout:
                mHallControl.toTopAbout();
                break;
            case R.id.edit_lottery_layout:
                intent.setClass(Hall.this, PlusMinusLottery.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.app_center:
                intent.setClass(Hall.this, LotteryGameList.class);
                startActivity(intent);
                break;
            case R.id.point_reward:
                ActionUtil.toPointReward(Hall.this);
                break;
            case R.id.map_layout:
                mHallControl.checkMapPlugin();
                break;
            case R.id.hide_banner:
                mlayoutBanner.setVisibility(View.GONE);
                break;
            case R.id.message_layout:
                mHallControl.toMessage();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            isInitLottery = false;
            
            showHallItem();
        }
    }

    /**
     * 获取大厅用户消息数
     * 
     * @author peter_feng
     * @create-time 2012-8-14 下午03:00:19
     */
    class GetMessageTask
        extends AsyncTask<Void, Object, String> {

        private HashMap<String, String> iniHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001020");
            parameter.put("pid", LotteryUtils.getPid(Hall.this));
            parameter.put("type", "2");
            parameter.put("phone", appState.getUsername());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(Hall.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, iniHashMap());
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
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String msgNum = analyse.getData(json, "num");
                    try {
                        int num = Integer.valueOf(msgNum);
                        appState.setMessageNumber(num);
                        if (num <= 0) {
                            hasPersonalMessage = false;
                        }
                        else {
                            hasPersonalMessage = true;
                        }
                        refreshMessageIcon();
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void submitData() {
        String eventName = "open_hall";
        MobclickAgent.onEvent(this, eventName);
    }

    @Override
    public void onRefresh() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mLotteryInfControl.setLotteryInf();
        }
        else {
            mLayoutRefresh.onHeaderRefreshComplete();
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(HallControl.BANNER_POLLING);
                return false;
            case MotionEvent.ACTION_MOVE:
                mHandler.removeMessages(HallControl.BANNER_POLLING);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.sendEmptyMessageDelayed(HallControl.BANNER_POLLING, mBannerScrollTime);
                return false;
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeMessages(HallControl.BANNER_POLLING);
                break;
        }
        return false;
    }
}
