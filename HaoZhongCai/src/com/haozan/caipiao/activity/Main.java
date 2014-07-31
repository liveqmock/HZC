package com.haozan.caipiao.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.news.LotteryNewsHall;
import com.haozan.caipiao.activity.unite.UniteHallActivity;
import com.haozan.caipiao.activity.userinf.UserCenter;
import com.haozan.caipiao.activity.weibo.WeiboHallActivity;
import com.haozan.caipiao.task.CheckUpdateTask;
import com.haozan.caipiao.types.AppInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ServiceUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.ExitDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 彩票tab主页面
 * 
 * @author peter_feng
 * @create-time 2013-5-22 下午5:11:32
 */
public class Main
    extends TabActivity
    implements OnClickListener {
    private static final int TAB_DRAWABLE_NOT_SELCTED[] = {R.drawable.main_tab_hall_normal,
            R.drawable.main_tab_groupbuy_normal, R.drawable.main_tab_news_normal,
            R.drawable.main_tab_garden_normal, R.drawable.main_tab_usercenter_normal};
    private static final int TAB_DRAWABLE_SELCTED[] = {R.drawable.main_tab_hall_selected,
            R.drawable.main_tab_groupbuy_selected, R.drawable.main_tab_news_selected,
            R.drawable.main_tab_garden_selected, R.drawable.main_tab_usercenter_selected};

    final static String FOLDER = "/CaipiaoCache/";
    final static String SUFFIX = ".txt";
    final static String UPLOAD_URL = "http://m.haozan88.com/User/UpApp/upload";

    private TabHost tabHost;
    private ProgressBar progress;
    private LotteryApp appState;
    private SharedPreferences preferences;
    private Editor databaseData;
    private boolean toLogin = false;
    private int lastTab = -1;

    private ArrayList<ImageButton> tabList = new ArrayList<ImageButton>();

    // 获取包名
    private PackageManager pm;
    private List<AppInfo> mlistAppInfo;
    StringBuilder str;
    String foldername;
    String targetPath;
    File targetFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initData();
        setupViews();
        init();
    }

    private void initData() {
        checkCloseApp(getIntent());

        preferences = getSharedPreferences("user", 0);
        databaseData = getSharedPreferences("user", 0).edit();
        appState = (LotteryApp) this.getApplication();
    }

    private void setupViews() {
        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("hall").setIndicator("Hall").setContent(new Intent(this, Hall.class)));
        tabHost.addTab(tabHost.newTabSpec("groupbuy").setIndicator("Groupbuy").setContent(new Intent(
                                                                                                     this,
                                                                                                     UniteHallActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("news").setIndicator("News").setContent(new Intent(
                                                                                             this,
                                                                                             LotteryNewsHall.class)));
        tabHost.addTab(tabHost.newTabSpec("garden").setIndicator("Garden").setContent(new Intent(
                                                                                                 this,
                                                                                                 WeiboHallActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("mylottery").setIndicator("MyLottery").setContent(new Intent(
                                                                                                       this,
                                                                                                       UserCenter.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        ImageButton btnHall = (ImageButton) this.findViewById(R.id.tab_hall);
        btnHall.setOnClickListener(this);
        tabList.add(btnHall);
        ImageButton btnGroupbuy = (ImageButton) this.findViewById(R.id.tab_groupbuy);
        btnGroupbuy.setOnClickListener(this);
        tabList.add(btnGroupbuy);
        ImageButton btnNews = (ImageButton) this.findViewById(R.id.tab_news);
        btnNews.setOnClickListener(this);
        tabList.add(btnNews);
        ImageButton btnWealthGarden = (ImageButton) this.findViewById(R.id.tab_wealth_garden);
        btnWealthGarden.setOnClickListener(this);
        tabList.add(btnWealthGarden);
        ImageButton btnMyLottery = (ImageButton) this.findViewById(R.id.tab_my_lottery);
        btnMyLottery.setOnClickListener(this);
        tabList.add(btnMyLottery);

        progress = (ProgressBar) this.findViewById(R.id.progress);
    }

    private void init() {
        checkUpdate();
        setTab(0);
        initCSMM();
        initBroadcast();
        // TODO加入时间限制
        getPackagesData();
        notificationAction();
    }

    /**
     * 判断是否要退出应用
     * 
     * @param intent
     * @return
     */
    public boolean checkCloseApp(Intent intent) {
        String str = intent.getAction();
        if ("caipiao.action.EXIT".equals(str)) {
            finish();
            return true;
        }
        return false;
    }

    private void notificationAction() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String type = bundle.getString("type");
            String action = bundle.getString("action");
            String actionInf = bundle.getString("action_inf");
            if (type != null && type.equals("notification") && action != null && actionInf != null) {
                if (action.equals("webpage")) {
                    ActionUtil.toWebBrowser(Main.this, "推送信息页面", actionInf);
                }
                else if (actionInf.equals("open_local_page")) {
                    ActionUtil.toPage(this, actionInf);
                }
            }
        }
    }

    /**
     * 获取本地第三方安装包信息 并上传
     */
    private void getPackagesData() {
        String currentVersion = preferences.getString("local_version", "");
        String lastUploadVersion = preferences.getString("last_upload_version", "");
        if (!currentVersion.equals(lastUploadVersion)) {
            executeGetPakeInfo();
        }

    }

    public void executeGetPakeInfo() {
        SearchPackageInsTask task = new SearchPackageInsTask();
        task.execute();
    }

    /**
     * 查找本地第三方安装包的信息
     * 
     * @author Vincent
     * @create-time 2013-8-23 下午4:03:25
     */
    class SearchPackageInsTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            mlistAppInfo = queryFilterAppInfo(); // 查询所有应用程序信息
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            str = new StringBuilder();
            for (int i = 0; i < mlistAppInfo.size(); i++) {
                AppInfo info = mlistAppInfo.get(i);
                str.append(info.getAppLabel() + ";" + info.getPkgName() + ";" + info.getPkVersion() + "\n");
            }
            SaveToSdCardTask task = new SaveToSdCardTask();
            task.execute();
        }
    }

    /**
     * 将获取到的第三方的安装包的信息以txt文件保存到本地
     * 
     * @author Vincent
     * @create-time 2013-8-23 下午4:03:52
     */
    class SaveToSdCardTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            writeFile(str);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            upload();
        }

        public void upload() {
            if (targetFile != null) {
                UploadToServiceTask task = new UploadToServiceTask();
                task.execute();
            }
        }
    }

    /**
     * 上传安装包信息
     * 
     * @author Vincent
     * @create-time 2013-8-23 下午4:04:24
     */
    class UploadToServiceTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... params) {
            return ServiceUtil.uploadFile(targetFile, UPLOAD_URL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ("200".equals(result)) {
                String localVersion = preferences.getString("local_version", null);
                databaseData.putString("last_upload_version", LotteryUtils.fullVersion(Main.this));
                databaseData.commit();
            }
        }
    }

    public List<AppInfo> queryFilterAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations =
            pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfo> appInfos = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        appInfos.clear();
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                appInfos.add(getAppInfo(app));
            }
        }
        return appInfos;
    }

    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(ApplicationInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);

        try {
            PackageInfo info = pm.getPackageInfo(app.packageName, 0);
            String version = info.versionName;
            appInfo.setPkVersion(version);
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    private void writeFile(StringBuilder sb) {
        foldername = Environment.getExternalStorageDirectory().getPath() + FOLDER;
        File folder = new File(foldername);
        if (folder != null && !folder.exists()) {
            if (!folder.mkdir() && !folder.isDirectory()) {
                Logger.inf("vincent", "Error: make dir failed!");
                return;
            }
        }

        String stringToWrite = sb.toString();
        TelephonyManager telephone = (TelephonyManager) Main.this.getSystemService(Context.TELEPHONY_SERVICE);
        String udid = telephone.getDeviceId();
        if (null == udid || "".equals(udid)) {
            udid = String.valueOf("T" + System.currentTimeMillis());
        }
        String localVersion = preferences.getString("local_version", "").replace(".", "");
        targetPath = foldername + localVersion + "_" + udid + SUFFIX;
        targetFile = new File(targetPath);
        if (targetFile != null) {
            if (targetFile.exists()) {
                targetFile.delete();
            }

            OutputStreamWriter osw;
            try {
                osw = new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8");
                try {
                    osw.write(stringToWrite);
                    osw.flush();
                    osw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    private UpdateReceiver receiver;

    public class UpdateReceiver
        extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastType = intent.getStringExtra("type");
            if (broadcastType != null) {
                if (broadcastType.equals("logoff") && lastTab == 4) {
                    setTab(0);
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

    public void moveFrontBg(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(200);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    private void setTab(int tabIndex) {
        if (tabIndex == lastTab) {
            return;
        }
        if (lastTab != -1) {
            tabList.get(lastTab).setImageDrawable(getResources().getDrawable(TAB_DRAWABLE_NOT_SELCTED[lastTab]));
            tabList.get(lastTab).setBackgroundDrawable(getResources().getDrawable(R.drawable.hall_tab));
        }

        if (tabIndex == 0) {
            tabHost.setCurrentTabByTag("hall");
            lastTab = tabIndex;
        }
        else if (tabIndex == 1) {
            tabHost.setCurrentTabByTag("groupbuy");
            lastTab = tabIndex;

        }
        else if (tabIndex == 2) {
            tabHost.setCurrentTabByTag("news");
            lastTab = tabIndex;
        }
        else if (tabIndex == 3) {
            tabHost.setCurrentTabByTag("garden");
            lastTab = tabIndex;
        }
        else if (tabIndex == 4) {
            if (appState.getUsername() == null) {
                toLogin = true;
                ActionUtil.toLogin(Main.this, null);
            }
            else {
                tabHost.setCurrentTabByTag("mylottery");
                lastTab = tabIndex;
            }
        }
        tabList.get(lastTab).setImageDrawable(getResources().getDrawable(TAB_DRAWABLE_SELCTED[lastTab]));
        tabList.get(lastTab).setBackgroundDrawable(getResources().getDrawable(R.drawable.hall_tab_selected));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toLogin) {
            toLogin = false;
            if (((LotteryApp) getApplication()).getUsername() != null) {
                setTab(4);
            }
            else {
                setTab(lastTab);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OperateInfUtils.clearSessionId(this);
// TaskPoolService.service.stopService();
        this.unregisterReceiver(receiver);
    }

    private void showExitDialog() {
        ExitDialog exitDialog = new ExitDialog(this);
        exitDialog.show();
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
        String eventName;
        String eventNameMob;
        switch (item.getItemId()) {
            case 1:
                map.put("inf", "username [" + appState.getUsername() + "]: click hall update");
                eventName = "v2 hall click update";
                FlurryAgent.onEvent(eventName, map);
                eventNameMob = "hall_click_update";
                MobclickAgent.onEvent(this, eventNameMob);
                updateVersion();
                return true;
            case 2:
                intent.setClass(this, Feedback.class);
                startActivity(intent);
                return true;
            case 3:
                map.put("inf", "username [" + appState.getUsername() + "]: click hall about");
                map.put("more_inf", "hall click menu about from top");
                eventName = "v2 hall click about";
                FlurryAgent.onEvent(eventName, map);
                eventNameMob = "hall_click_about";
                MobclickAgent.onEvent(this, eventNameMob, "top");
                intent.setClass(Main.this, About.class);
                startActivity(intent);
                return true;
            case 4:
                intent.setClass(Main.this, Help.class);
                startActivity(intent);
                return true;
            case 5:
                finish();
        }
        return false;
    }

    /**
     * 检测升级
     */
    protected void updateVersion() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            CheckUpdateTask checkUpdate = new CheckUpdateTask(this);
            checkUpdate.setProgress(progress);
            checkUpdate.execute();
        }
        else {
            ViewUtil.showTipsToast(this, getResources().getString(R.string.network_not_avaliable));
        }
    }

    // 检测是否需要升级，先判断是否要强制升级，如不需要再判断是否选择两周内不提醒，若未设置默认一天检测一次
    private void checkUpdate() {
        Boolean forceUpdate = preferences.getBoolean("update_force", false);
        long lastTime = preferences.getLong("last_check_update_time", 0);
        Boolean checkUpdateLater = preferences.getBoolean("check_update_later", false);
        long updateFixTime = preferences.getLong("date_fix_time", 0);
        String localVersion = preferences.getString("local_version", null);
        if (localVersion == null) {
            resetUpdateDatabase();
        }
        else {
            if (!localVersion.equals(LotteryUtils.fullVersion(Main.this))) {
                checkUpdateLater = false;
                updateFixTime = 0;
                resetUpdateDatabase();
            }
        }
        long gapTime = System.currentTimeMillis() - updateFixTime;
        // if (gapTime > 60 * 60 * 24 * 14 * 1000 || gapTime < 0 ||
        // updateFixTime == 0) {
        // }
        if (forceUpdate) {
            autoUpdate();
        }
        else {
            if (checkUpdateLater) {
                gapTime = System.currentTimeMillis() - updateFixTime;
                if (gapTime > 60 * 60 * 24 * 14 * 1000 || gapTime < 0 || updateFixTime == 0) {
                    databaseData.putLong("date_fix_time", 0);
                    databaseData.putBoolean("check_update_later", false);
                    autoUpdate();
                }
            }
            else {
                gapTime = System.currentTimeMillis() - lastTime;
                if (gapTime > 60 * 60 * 24 * 1000 || gapTime < 0 || lastTime == 0) {
                    autoUpdate();
                }
            }
            databaseData.commit();
        }
    }

    private void initCSMM() {
// ICSAPI api = CSAPIFactory.getCSAPI();
// try {
// String username = api.init(this, appState.getUsername());
// api.bindAccount(this, username);
// }
// catch (CSInitialException e) {
// e.printStackTrace();
// }
// catch (CSUnValidateOperationException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
    }

    private void resetUpdateDatabase() {
        databaseData.putString("local_version", LotteryUtils.fullVersion(this));
        databaseData.putLong("date_fix_time", 0);
        databaseData.putBoolean("check_update_later", false);
        databaseData.putString("update_web_url", null);
        databaseData.commit();
    }

    // 检测彩票是否有更新
    private void autoUpdate() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            databaseData.putLong("last_check_update_time", System.currentTimeMillis());
            CheckUpdateTask updateTask = new CheckUpdateTask(this);
            updateTask.execute();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (LotteryUtils.getPid(this).equals("101201"))
                finish();
            else
                showExitDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tab_hall) {
            setTab(0);
        }
        else if (v.getId() == R.id.tab_groupbuy) {
            setTab(1);
        }
        else if (v.getId() == R.id.tab_news) {
            setTab(2);
        }
        else if (v.getId() == R.id.tab_wealth_garden) {
            setTab(3);
        }
        else if (v.getId() == R.id.tab_my_lottery) {
            setTab(4);
        }
    }
}