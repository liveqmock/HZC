package com.haozan.caipiao.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.About;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.Help;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.MainTabControl;
import com.haozan.caipiao.types.NewVersionInfo;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 主页面tab
 * 
 * @author peter_wang
 * @create-time 2013-11-12 上午9:37:54
 */

public class MainActivity extends FragmentActivity  {
	//tabhost没有按下的效果图
    private static final int TAB_DRAWABLE_NOT_SELCTED[] = {R.drawable.main_tab_hall_normal,
            R.drawable.main_tab_groupbuy_normal, R.drawable.main_tab_news_normal,
            R.drawable.main_tab_garden_normal, R.drawable.main_tab_usercenter_normal};
    
    //tabhost按下的效果图
    private static final int TAB_DRAWABLE_SELCTED[] = {R.drawable.main_tab_hall_selected,
            R.drawable.main_tab_groupbuy_selected, R.drawable.main_tab_news_selected,
            R.drawable.main_tab_garden_selected, R.drawable.main_tab_usercenter_selected};

    //每个选项卡的描述
    private static final String[] FRAGMENT_DESC = {"hall", "unite", "news", "garden", "mylottery"};

    private TabHost tabHost;
    private TabWidget tabWidget;
    private ProgressBar mProgress;
    private int mCurrentTab = 0; // 选中哪个tab，从1开始
    private RelativeLayout mTabIndicator1, mTabIndicator2, mTabIndicator3, mTabIndicator4, mTabIndicator5;

    private HallFragment mHallFragment;
    private UniteHallFragment mUniteFragment;
    private LotteryNewsHallFragment mNewsFragment;
    private WeiboHallFragment mGardenFragment;
    private UserCenterFragment mUserCenterFragment;

    private final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
    private android.support.v4.app.FragmentTransaction mFragmentTrasaction;

    private MainTabControl mControl;

    private boolean isToLogin = false;
    private LotteryApp mAppState;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler
        extends WeakReferenceHandler<MainActivity> {

        public MyHandler(MainActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(MainActivity activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.DISMISS_PROGRESS:			//隐藏进度圈
                    activity.mProgress.setVisibility(View.GONE);
                    break;

                case ControlMessage.UPDATE_INFO_SUCCESS_RESULT://更新
                    NewVersionInfo info = (NewVersionInfo) msg.obj;
                    activity.mControl.showUpdateDialog(info);
                    break;
                case ControlMessage.UPDATE_INFO_FAIL_RESULT:	//已经是最新版本
                    activity.mControl.showUpdateFail();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.inf("call main onCreate");
        initData();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_activity);
        setupViews();
        checkWhetherRestoreFragment();
        init();
    }

    private void checkWhetherRestoreFragment() {
        if (fm.getBackStackEntryCount() > 0) {
            mHallFragment = (HallFragment) fm.findFragmentByTag(FRAGMENT_DESC[0]);
            if (mHallFragment != null) {
                mFragmentTrasaction.add(R.id.realtabcontent, mHallFragment, FRAGMENT_DESC[0]);
            }

            mUniteFragment = (UniteHallFragment) fm.findFragmentByTag(FRAGMENT_DESC[1]);
            if (mUniteFragment != null) {
                mFragmentTrasaction.add(R.id.realtabcontent, mUniteFragment, FRAGMENT_DESC[1]);
            }

            mNewsFragment = (LotteryNewsHallFragment) fm.findFragmentByTag(FRAGMENT_DESC[2]);
            if (mNewsFragment != null) {
                mFragmentTrasaction.add(R.id.realtabcontent, mNewsFragment, FRAGMENT_DESC[2]);
            }

            mGardenFragment = (WeiboHallFragment) fm.findFragmentByTag(FRAGMENT_DESC[3]);
            if (mGardenFragment != null) {
                mFragmentTrasaction.add(R.id.realtabcontent, mGardenFragment, FRAGMENT_DESC[3]);
            }

            mUserCenterFragment = (UserCenterFragment) fm.findFragmentByTag(FRAGMENT_DESC[4]);
            if (mUserCenterFragment != null) {
                mFragmentTrasaction.add(R.id.realtabcontent, mUserCenterFragment, FRAGMENT_DESC[4]);
            }
        }
    }

    private void initData() {
        mControl = new MainTabControl(this, mHandler);

        mControl.checkCloseApp(getIntent());//检查是否需要退出应用

        mAppState = (LotteryApp) this.getApplication();

        mFragmentTrasaction = fm.beginTransaction();
    }

    /**
     * 找到Tabhost布局
     */
    public void setupViews() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        mProgress = (ProgressBar) this.findViewById(R.id.progress);
        LinearLayout layout = (LinearLayout) tabHost.getChildAt(0);
        TabWidget tw = (TabWidget) layout.getChildAt(1);

        mTabIndicator1 =
            (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
        ImageView ivTab1 = (ImageView) mTabIndicator1.getChildAt(0);//找到图标
        ivTab1.setImageResource(R.drawable.main_tab_hall);//修改图标

        mTabIndicator2 =
            (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
        ImageView ivTab2 = (ImageView) mTabIndicator2.getChildAt(0);
        ivTab2.setImageResource(R.drawable.main_tab_groupbuy);

        mTabIndicator3 =
            (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
        ImageView ivTab3 = (ImageView) mTabIndicator3.getChildAt(0);
        ivTab3.setImageResource(R.drawable.main_tab_news);

        mTabIndicator4 =
            (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
        ImageView ivTab4 = (ImageView) mTabIndicator4.getChildAt(0);
        ivTab4.setImageResource(R.drawable.main_tab_garden);

        mTabIndicator5 =
            (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
        ImageView ivTab5 = (ImageView) mTabIndicator5.getChildAt(0);
        ivTab5.setImageResource(R.drawable.main_tab_usercenter);
    }

    /**
     * 初始化选项卡
     */
    public void initTab() {
        TabHost.TabSpec tSpecHome = tabHost.newTabSpec(FRAGMENT_DESC[0]);
        tSpecHome.setIndicator(mTabIndicator1);
        tSpecHome.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecHome);

        TabHost.TabSpec tSpecWall = tabHost.newTabSpec(FRAGMENT_DESC[1]);
        tSpecWall.setIndicator(mTabIndicator2);
        tSpecWall.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecWall);

        TabHost.TabSpec tSpecCamera = tabHost.newTabSpec(FRAGMENT_DESC[2]);
        tSpecCamera.setIndicator(mTabIndicator3);
        tSpecCamera.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecCamera);

        TabHost.TabSpec tSpecMessage = tabHost.newTabSpec(FRAGMENT_DESC[3]);
        tSpecMessage.setIndicator(mTabIndicator4);
        tSpecMessage.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecMessage);

        TabHost.TabSpec tSpecMe = tabHost.newTabSpec(FRAGMENT_DESC[4]);
        tSpecMe.setIndicator(mTabIndicator5);
        tSpecMe.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecMe);
    }

    private void init() {
        tabHost.setup();

        /** 监听 */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mFragmentTrasaction = fm.beginTransaction();

                recoverLastTab();
                /** 如果当前选项卡是home */
                if (tabId.equalsIgnoreCase(FRAGMENT_DESC[0])) {
                    hideLastPage();
                    mCurrentTab = 0;
                    isTabHall();
                }
                else if (tabId.equalsIgnoreCase(FRAGMENT_DESC[1])) {
                    hideLastPage();
                    mCurrentTab = 1;
                    isTabUnite();
                }
                else if (tabId.equalsIgnoreCase(FRAGMENT_DESC[2])) {
                    hideLastPage();
                    mCurrentTab = 2;
                    isTabNews();
                }
                else if (tabId.equalsIgnoreCase(FRAGMENT_DESC[3])) {
                    hideLastPage();
                    mCurrentTab = 3;
                    isTabGarden();
                }
                else if (tabId.equalsIgnoreCase(FRAGMENT_DESC[4])) {
                    if (mAppState.getUsername() != null) {
                        hideLastPage();
                        mCurrentTab = 4;
                        isTabMyLottery();
                    }
                    else {
                        isToLogin = true;

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, Login.class);
                        MainActivity.this.startActivity(intent);
                    }
                }
                setCurrentTab();

                mFragmentTrasaction.commitAllowingStateLoss();

                if (isToLogin) {
                    tabHost.setCurrentTab(mCurrentTab);
                }
            }

        };
        tabHost.setOnTabChangedListener(tabChangeListener);
        initTab();
        tabHost.setCurrentTab(0);

        initBroadcast();
        mControl.notificationAction(getIntent());
        mControl.submitLocalAppData();
        mControl.autoCheckUpdate();
    }

    private void recoverLastTab() {
        RelativeLayout layout = (RelativeLayout) tabWidget.getChildAt(mCurrentTab);
        layout.setBackgroundResource(R.drawable.hall_tab);
        ((ImageView) layout.getChildAt(0)).setImageResource(TAB_DRAWABLE_NOT_SELCTED[mCurrentTab]);
    }

    private void setCurrentTab() {
        RelativeLayout layout = (RelativeLayout) tabWidget.getChildAt(mCurrentTab);
        layout.setBackgroundResource(R.drawable.hall_tab_selected);
        ((ImageView) layout.getChildAt(0)).setImageResource(TAB_DRAWABLE_SELCTED[mCurrentTab]);
    }

    private void hideLastPage() {
        if (mHallFragment != null) {
            mFragmentTrasaction.hide(mHallFragment);
        }

        if (mUniteFragment != null) {
            mFragmentTrasaction.hide(mUniteFragment);
        }

        if (mNewsFragment != null) {
            mFragmentTrasaction.hide(mNewsFragment);
        }

        if (mGardenFragment != null) {
            mFragmentTrasaction.hide(mGardenFragment);
        }

        if (mUserCenterFragment != null) {
            mFragmentTrasaction.hide(mUserCenterFragment);
        }
    }

    // 判断当前
    public void isTabHall() {
        if (mHallFragment == null) {
            mHallFragment = new HallFragment();

            mFragmentTrasaction.add(R.id.realtabcontent, mHallFragment, FRAGMENT_DESC[0]);
            mFragmentTrasaction.addToBackStack(FRAGMENT_DESC[0]);
        }
        else {
            mFragmentTrasaction.show(mHallFragment);
        }
    }

    public void isTabUnite() {
        if (mUniteFragment == null) {
            mUniteFragment = new UniteHallFragment();

            mFragmentTrasaction.add(R.id.realtabcontent, mUniteFragment, FRAGMENT_DESC[1]);
            mFragmentTrasaction.addToBackStack(FRAGMENT_DESC[1]);
        }
        else {
            mFragmentTrasaction.show(mUniteFragment);
        }
    }

    public void isTabNews() {
        if (mNewsFragment == null) {
            mNewsFragment = new LotteryNewsHallFragment();

            mFragmentTrasaction.add(R.id.realtabcontent, mNewsFragment, FRAGMENT_DESC[2]);
            mFragmentTrasaction.addToBackStack(FRAGMENT_DESC[2]);
        }
        else {
            mFragmentTrasaction.show(mNewsFragment);
        }
    }

    public void isTabGarden() {
        if (mGardenFragment == null) {
            mGardenFragment = new WeiboHallFragment();

            mFragmentTrasaction.add(R.id.realtabcontent, mGardenFragment, FRAGMENT_DESC[3]);
            mFragmentTrasaction.addToBackStack(FRAGMENT_DESC[3]);
        }
        else {
            mFragmentTrasaction.show(mGardenFragment);
        }
    }

    public void isTabMyLottery() {
        if (mUserCenterFragment == null) {
            mUserCenterFragment = new UserCenterFragment();

            mFragmentTrasaction.add(R.id.realtabcontent, mUserCenterFragment, FRAGMENT_DESC[4]);
            mFragmentTrasaction.addToBackStack(FRAGMENT_DESC[4]);
        }
        else {
            mUserCenterFragment.onRefresh();
            mFragmentTrasaction.show(mUserCenterFragment);
        }
    }

    private UpdateReceiver receiver;

    public class UpdateReceiver
        extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastType = intent.getStringExtra("type");
            if (broadcastType != null) {
                if (broadcastType.equals("logoff") && mCurrentTab == 4) {
                    tabHost.setCurrentTab(0);
                }
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "检测升级").setIcon(R.drawable.icon_update);
        menu.add(0, 2, 0, "反馈").setIcon(R.drawable.icon_feedback_list);
        menu.add(0, 3, 0, "关于").setIcon(R.drawable.icon_about);
        menu.add(0, 4, 0, "帮助").setIcon(R.drawable.icon_help);
        if (LotteryUtils.getPid(this).equals("101201"))
            menu.add(0, 5, 0, "退出彩票").setIcon(R.drawable.icon_exit);
        else
            menu.add(0, 5, 0, "退  出").setIcon(R.drawable.icon_exit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        Map<String, String> map = new HashMap<String, String>();
        String eventNameMob;
        switch (item.getItemId()) {
            case 1:
                mProgress.setVisibility(View.VISIBLE);
                mControl.checkUpdateRequest();
                return true;
            case 2:
                intent.setClass(this, Feedback.class);
                startActivity(intent);
                return true;
            case 3:
                eventNameMob = "hall_click_about";
                MobclickAgent.onEvent(this, eventNameMob, "top");
                intent.setClass(this, About.class);
                startActivity(intent);
                return true;
            case 4:
                intent.setClass(this, Help.class);
                startActivity(intent);
                return true;
            case 5:
                finish();
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mControl.pressBackKey();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        Logger.inf("call main onResume " + isToLogin);
        super.onResume();
        if (isToLogin) {
            isToLogin = false;
            if (mAppState.getUsername() != null) {
                tabHost.setCurrentTab(4);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Logger.inf("call main onDestroy");
        super.onDestroy();
        OperateInfUtils.clearSessionId(this);
// TaskPoolService.service.stopService();
        this.unregisterReceiver(receiver);
    }
}
