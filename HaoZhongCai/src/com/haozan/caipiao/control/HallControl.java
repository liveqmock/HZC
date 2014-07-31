package com.haozan.caipiao.control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.activity.About;
import com.haozan.caipiao.activity.LotteryHistory;
import com.haozan.caipiao.activity.SportsLotteryHistory;
import com.haozan.caipiao.activity.bet.cqssc.CQSSCActivity;
import com.haozan.caipiao.activity.bet.cqssc.CQSSCBetConfirm;
import com.haozan.caipiao.activity.bet.dcsfgg.DCSFGGActivity;
import com.haozan.caipiao.activity.bet.dfljy.DFLJYActivity;
import com.haozan.caipiao.activity.bet.dfljy.DFLJYBetConfirm;
import com.haozan.caipiao.activity.bet.dlt.DLTActivity;
import com.haozan.caipiao.activity.bet.dlt.DLTBetConfirm;
import com.haozan.caipiao.activity.bet.hnklsf.HNKLSFActivity;
import com.haozan.caipiao.activity.bet.hnklsf.HNKLSFBetComfirm;
import com.haozan.caipiao.activity.bet.jclq.JCLQActivity;
import com.haozan.caipiao.activity.bet.jczq.JCZQActivity;
import com.haozan.caipiao.activity.bet.jlks.JLKSActivity;
import com.haozan.caipiao.activity.bet.jlks.JLKSBetConfirm;
import com.haozan.caipiao.activity.bet.jxssc.JXSSCActivity;
import com.haozan.caipiao.activity.bet.jxssc.JXSSCBetConfirm;
import com.haozan.caipiao.activity.bet.klsf.KLSFActivity;
import com.haozan.caipiao.activity.bet.klsf.KLSFBetComfirm;
import com.haozan.caipiao.activity.bet.pls.PLSActivity;
import com.haozan.caipiao.activity.bet.pls.PLSBetConfirm;
import com.haozan.caipiao.activity.bet.plw.PLWActivity;
import com.haozan.caipiao.activity.bet.plw.PLWBetConfirm;
import com.haozan.caipiao.activity.bet.qlc.QLCActivity;
import com.haozan.caipiao.activity.bet.qlc.QLCBetConfirm;
import com.haozan.caipiao.activity.bet.qxc.QXCActivity;
import com.haozan.caipiao.activity.bet.qxc.QXCBetConfirm;
import com.haozan.caipiao.activity.bet.renxuanjiu.RenXuanJiuActivity;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.activity.bet.sd.SDBetConfirm;
import com.haozan.caipiao.activity.bet.shisichang.ShisichangActivity;
import com.haozan.caipiao.activity.bet.ssq.SSQActivity;
import com.haozan.caipiao.activity.bet.ssq.SSQBetConfirm;
import com.haozan.caipiao.activity.bet.swxw.SWXWActivity;
import com.haozan.caipiao.activity.bet.swxw.SWXWBetConfirm;
import com.haozan.caipiao.activity.bet.syxw.SYXWActivity;
import com.haozan.caipiao.activity.bet.syxw.SYXWBetConfirm;
import com.haozan.caipiao.activity.userinf.MessageCenter;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.BannerRequest;
import com.haozan.caipiao.request.HallNoticeRequest;
import com.haozan.caipiao.requestInf.PluginInfRequestInf;
import com.haozan.caipiao.task.DownloadTask;
import com.haozan.caipiao.task.PluginInfTask;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.Banner;
import com.haozan.caipiao.types.HallItem;
import com.haozan.caipiao.types.LotteryInf;
import com.haozan.caipiao.types.PluginInf;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.util.cache.FileCache;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 大厅control
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:19:10
 */
public class HallControl {
    public static HashMap<String, LotteryInf> sLotteryInf = new HashMap<String, LotteryInf>();

    public static final int BANNER_POLLING = 0;
    public static final int BANNER_SHOW = 1;

    public static final int HALL_COUNT_TIME = 2;

    private Context mContext;

    private ArrayList<Banner> banners;

    private Handler mHandler;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private LotteryInfControl mLotteryInfControl;

    public HallControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        banners = new ArrayList<Banner>();
        mLotteryInfControl = new LotteryInfControl(context, mHandler);
    }

    public boolean isBanner(String url) {
        int length = banners.size();
        for (int i = 0; i < length; i++) {
            if (banners.get(i).getImgUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    public void showNotice() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new HallNoticeRequest(
                                                                                                        mContext,
                                                                                                        mHandler)));
        }
    }

    public void showBanners() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(new BannerRequest(mContext,
                                                                                                    mHandler,
                                                                                                    banners)));
        }
    }

    public void downloadPic(String url) {
        String name = FileCache.getPicFilePath(url);
        if (TextUtils.isEmpty(name) == false) {
            TaskPoolService.getInstance().requestService(new DownloadTask(mHandler, url, name));
        }
    }

    public boolean hasBannerLocal(Banner banner) {
        File file = new File(FileCache.getPicFilePath(banner.getImgUrl()));
        return file.exists();
    }

    public void addBannerList() {
        int lenght = banners.size();
        for (int i = 0; i < lenght; i++) {
            Banner banner = banners.get(i);
            if (hasBannerLocal(banner)) {
                Message message =
                    Message.obtain(mHandler, ControlMessage.DOWNLOAD_FLASH_PIC, banner.getImgUrl());
                message.sendToTarget();
            }
            else {
                downloadPic(banner.getImgUrl());
            }
        }
    }

    public void OnClickBanner(int index) {
        Banner banner = banners.get(index);

        submitDataStatisticsClickBanner(banner.getImgUrl());

        String actionType = banner.getActionType();
        if ("url".equals(actionType)) {
            ActionUtil.toWebBrowser(mContext, banner.getTitle(), banner.getUrl());
        }
        else if ("app".equals(actionType)) {
            if (PluginUtils.checkGameExist(mContext, banner.getAppPackage())) {
                Bundle bundle = new Bundle();
                PluginUtils.goPlugin(mContext, bundle, banner.getAppPackage(), banner.getAppClass());
            }
            else {
                PluginUtils.showPluginDownloadDialog(mContext, banner.getTitle(), banner.getDescription(),
                                                     banner.getUrl(), false);
            }
        }
        else if ("local".equals(actionType)) {
            ActionUtil.toPage(mContext, banner.getAppClass());
        }
    }

    private void submitDataStatisticsClickBanner(String picId) {
        String eventNameMob = "click_banner";
        HashMap<String, String> banner = new HashMap<String, String>();
        banner.put("type", "hall_banner");
        banner.put("id", picId);
        MobclickAgent.onEvent(mContext, eventNameMob, banner);
    }

    /**
     * 尊贵版特别处理，提示用户删除其他版本
     */
    public void vipVersionControl() {
        if (LotteryUtils.getPid(mContext).equals("191012")) {
            checkInstallApp();
        }
    }

    private void checkInstallApp() {
        SharedPreferences preferences = ActionUtil.getSharedPreferences(mContext);
        Editor databaseData = ActionUtil.getEditor(mContext);

        boolean hasCheck = preferences.getBoolean("check_install_other_app", false);
        if (hasCheck) {
            return;
        }
        else {
            databaseData.putBoolean("check_install_other_app", true);
            databaseData.commit();
        }

        int result = -1;

        String[] packageNames = {"com.haozan.caipiao", "buke.besttone.caipiao", "com.haozan.caipiao"};
        String[] appName = {"彩票尊贵版旧版本", "号百彩票", "好中彩"};
        for (int i = 0; i < appName.length; i++) {
            try {
                mContext.getPackageManager().getPackageInfo(packageNames[i], 0);
                result = i;
                break;
            }
            catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (result != -1) {
            showUnInstallDialog(appName[result], packageNames[result]);
        }
    }

    private void showUnInstallDialog(String name, final String packageName) {
        final Editor databaseData = ActionUtil.getEditor(mContext);

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setTitle("恭喜").setMessage("恭喜！您已成功安装彩票尊贵版，现检测到您使用" + name +
                                                    ",为了给您提供更好的服务，强烈建议您卸载掉老版本！新版本享受更多优惠！").setPositiveButton("确  定",
                                                                                                             new DialogInterface.OnClickListener() {
                                                                                                                 public void onClick(DialogInterface dialog,
                                                                                                                                     int which) {
                                                                                                                     unInstallMethod(packageName);
                                                                                                                     dialog.dismiss();
                                                                                                                     databaseData.putBoolean("isNew",
                                                                                                                                             false);
                                                                                                                     databaseData.commit();
                                                                                                                 }
                                                                                                             }).setNegativeButton("取  消",
                                                                                                                                  new DialogInterface.OnClickListener() {
                                                                                                                                      public void onClick(DialogInterface dialog,
                                                                                                                                                          int which) {
                                                                                                                                          dialog.dismiss();
                                                                                                                                          databaseData.putBoolean("isNew",
                                                                                                                                                                  false);
                                                                                                                                          databaseData.commit();
                                                                                                                                      }
                                                                                                                                  });
        CustomDialog dlgCheckBindSina = customBuilder.create();
        dlgCheckBindSina.show();
        dlgCheckBindSina.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                databaseData.putBoolean("isNew", false);
                databaseData.commit();
            }
        });
    }

    private void unInstallMethod(String packageName) {

        try {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            mContext.startActivity(uninstallIntent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转关于页面
     */
    public void toTopAbout() {
        String eventNameMob = "hall_click_about";
        MobclickAgent.onEvent(mContext, eventNameMob, "menu");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("about", "hall_top");
        intent.setClass(mContext, About.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    /**
     * 跳转消息中心-公告列表
     */
    public void toNotice() {
        String eventNameMob = "hall_click_notice";
        MobclickAgent.onEvent(mContext, eventNameMob, "hall_popup");
        Intent intent = new Intent();
        intent.setClass(mContext, MessageCenter.class);
        mContext.startActivity(intent);
    }

    /**
     * 跳转消息中心-个人消息
     */
    public void toMessage() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("message_type", 0);
        intent.putExtras(bundle);
        intent.setClass(mContext, MessageCenter.class);
        mContext.startActivity(intent);
    }

    public void checkMapPlugin() {
        SharedPreferences prefrences = ActionUtil.getSharedPreferences(mContext);

        String lastJson = prefrences.getString("plugin_" + PluginInfRequestInf.PLUGIN_MAP, null);
        if (lastJson == null) {
            TaskPoolService.getInstance().requestService(new PluginInfTask(mContext, mHandler,
                                                                           PluginInfRequestInf.PLUGIN_MAP));
        }
        else {
            toMapPlugin(lastJson);
        }
    }

    public void toMapPlugin(String json) {
        PluginInf mapPluginInf = new PluginInf();
        PluginUtils.analysePluginData(mapPluginInf, json);

        if (PluginUtils.checkGameExist(mContext, mapPluginInf.getGamePackageName())) {
            LotteryApp appState = (LotteryApp) mContext;

            Bundle bundle = new Bundle();
            bundle.putString("pid", LotteryUtils.getPid(mContext));
            bundle.putString("key", LotteryUtils.getKey(mContext));
            bundle.putString("time", appState.getTime());
            PluginUtils.goPlugin(mContext, bundle, mapPluginInf.getGamePackageName(),
                                 mapPluginInf.getGameActivityName());
        }
        else {
            PluginUtils.showPluginDownloadDialog(mContext, "投注站地图", "确定下载投注站地图?",
                                                 mapPluginInf.getGameDownloadUrl(), false);
        }
    }

    private long mLastTime;

    public void minusGlobalTime() {
        for (int i = 0; i < LotteryUtils.LOTTERY_DIGITAL.length; i++) {
            LotteryInf lotteryInf = sLotteryInf.get(LotteryUtils.LOTTERY_DIGITAL[i]);
            if (lotteryInf != null) {

                // 提前两秒发起获取彩种信息的请求
                if (lotteryInf.getLastTimeMillis() <= 2000 && lotteryInf.getLastTimeMillis() > 1000) {
                    if (HttpConnectUtil.isNetworkAvailable(mContext) &&
                        LotteryUtils.isFrequentLottery(lotteryInf.getId())) {
                        mLotteryInfControl.getLotteryInf(lotteryInf.getId());
                    }

                    mLastTime =
                        lotteryInf.getEndTime() - System.currentTimeMillis() - lotteryInf.getGapTimeMillis();

                    lotteryInf.setLastTimeMillis(mLastTime);
                }
                else if (lotteryInf.getLastTimeMillis() > 0) {
                    mLastTime =
                        lotteryInf.getEndTime() - System.currentTimeMillis() - lotteryInf.getGapTimeMillis();

                    lotteryInf.setLastTimeMillis(mLastTime);
                }
            }
        }
    }

    /**
     * 检测大厅数据是否获取全
     * 
     * @param hallItemList
     * @return
     */
    public boolean checkHallItemNoData(ArrayList<HallItem> hallItemList) {
        if (hallItemList == null) {
            return false;
        }

        boolean isSuccessGetData = true;
        for (int i = 0; i < hallItemList.size(); i++) {
            LotteryInf lottery = HallControl.sLotteryInf.get(hallItemList.get(i).getId());
            if (lottery == null) {
                isSuccessGetData = false;
                break;
            }
        }

        return isSuccessGetData;
    }

    /**
     * 初始化大厅彩种信息
     * 
     * @param hallItemList
     * @return 是否成功
     */
    public boolean setHallItem(ArrayList<HallItem> hallItemList) {
        boolean isSuccessGetData = true;

        hallItemList.clear();

        String[] localLottery = getLocalLottery();

        for (int i = 0; i < localLottery.length; i++) {

            HallItem item = new HallItem();
            isSuccessGetData = creatHallItem(item, localLottery[i]);

            hallItemList.add(item);
        }

        return isSuccessGetData;
    }

    public boolean creatHallItem(HallItem item, String lotteryId) {
        return mLotteryInfControl.creatHallItem(item, lotteryId);
    }

    public String[] getLocalLottery() {
        SharedPreferences preferences = ActionUtil.getSharedPreferences(mContext);
        Editor databaseData = ActionUtil.getEditor(mContext);

        String localKind = preferences.getString("local_kind", null);
        String lotteryArray = LotteryUtils.lotteryArray.substring(0, LotteryUtils.lotteryArray.length());
        String lotterySelected = preferences.getString("lottery_selected", lotteryArray);
        // 首次使用保存现在彩种列表
        if (localKind == null) {
            databaseData.putString("local_kind", lotteryArray);
            databaseData.commit();
        }
        else {
            // 判断是否更新版本有新彩种加入
            if (!localKind.equals(lotteryArray)) {
                databaseData.putBoolean("lottery_item_move", false);
                databaseData.commit();
                databaseData.putString("local_kind", lotteryArray);
                databaseData.commit();
// // 在原有选定彩种基础上加入新彩种
// if (localKind.length() < lotteryArray.length() &&
// localKind.equals(lotteryArray.substring(0, localKind.length()))) {
// lotterySelected =
// preferences.getString("lottery_selected", localKind) +
// lotteryArray.substring(localKind.length());
// databaseData.putString("lottery_selected", lotterySelected);
// databaseData.commit();
// }
// // 出现问题还原成全部彩种显示
// else {
                lotterySelected = lotteryArray;
                databaseData.remove("lottery_not_selected");
                databaseData.commit();
                databaseData.putString("lottery_selected", lotteryArray);
                databaseData.commit();
// }
            }
        }
        String[] lotteryKind = lotterySelected.split("\\|");
        return lotteryKind;
    }

    /**
     * 大厅item点击底部
     * 
     * @param context
     * @param hallItem
     */
    public void bottomClick(Context context, HallItem hallItem) {
        String eventNameMob = "hall_click_bet";
        MobclickAgent.onEvent(mContext, eventNameMob, hallItem.getId());

        Intent intent = new Intent();
        String id = hallItem.getId();
        if (id.equals("ssq")) {
            if (SSQBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, SSQBetConfirm.class);
            else
                intent.setClass(context, SSQActivity.class);
        }
        else if (id.equals("3d")) {
            if (SDBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, SDBetConfirm.class);
            else
                intent.setClass(context, SDActivity.class);
        }
        else if (id.equals("qlc")) {
            if (QLCBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, QLCBetConfirm.class);
            else
                intent.setClass(context, QLCActivity.class);
        }
        else if (id.equals("swxw")) {
            if (SWXWBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, SWXWBetConfirm.class);
            else
                intent.setClass(context, SWXWActivity.class);
        }
        else if (id.equals("dfljy")) {
            if (DFLJYBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, DFLJYBetConfirm.class);
            else
                intent.setClass(context, DFLJYActivity.class);
        }
        else if (id.equals("dlt")) {
            if (DLTBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, DLTBetConfirm.class);
            else
                intent.setClass(context, DLTActivity.class);
        }
        else if (id.equals("pls")) {
            if (PLSBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, PLSBetConfirm.class);
            else
                intent.setClass(context, PLSActivity.class);
        }
        else if (id.equals("plw")) {
            if (PLWBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, PLWBetConfirm.class);
            else
                intent.setClass(context, PLWActivity.class);
        }
        else if (id.equals("qxc")) {
            if (QXCBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, QXCBetConfirm.class);
            else
                intent.setClass(context, QXCActivity.class);
        }
        else if (id.equals("sfc")) {
            intent.setClass(context, ShisichangActivity.class);
        }
        else if (id.equals("r9")) {
            intent.setClass(context, RenXuanJiuActivity.class);
        }
        // TODO 重庆时时彩暂时改为吉林快三
        else if (id.equals("cqssc")) {
            if (CQSSCBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, CQSSCBetConfirm.class);
            else
                intent.setClass(context, CQSSCActivity.class);
        }
        // add by vincent
        else if (id.equals("jx11x5")) {
            if (SYXWBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, SYXWBetConfirm.class);
            else
                intent.setClass(context, SYXWActivity.class);
        }
        else if (id.equals("hnklsf")) {
            if (HNKLSFBetComfirm.betLocalList.size() > 0)
                intent.setClass(context, HNKLSFBetComfirm.class);
            else
                intent.setClass(context, HNKLSFActivity.class);
        }
        else if (id.equals("klsf")) {
            if (KLSFBetComfirm.betLocalList.size() > 0)
                intent.setClass(context, KLSFBetComfirm.class);
            else
                intent.setClass(context, KLSFActivity.class);
        }
        else if (id.equals("jxssc")) {
            if (JXSSCBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, JXSSCBetConfirm.class);
            else
                intent.setClass(context, JXSSCActivity.class);
        }
        else if (id.equals("jlk3")) {
            if (JLKSBetConfirm.betLocalList.size() > 0)
                intent.setClass(context, JLKSBetConfirm.class);
            else
                intent.setClass(context, JLKSActivity.class);
        }
        else if (id.equals(LotteryUtils.JCZQ)) {
            intent.setClass(context, JCZQActivity.class);
        }
        else if (id.equals(LotteryUtils.JCLQ)) {
            intent.setClass(context, JCLQActivity.class);
        }
        else if (id.equals(LotteryUtils.DCSFGG)) {
            intent.setClass(context, DCSFGGActivity.class);
        }

        LotteryInf lottery = sLotteryInf.get(id);

        if (lottery != null && lottery.getLastTimeMillis() > 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("from_hall", true);
            bundle.putString("bet_term", lottery.getNewTerm());
            bundle.putString("awardtime", lottery.getAwardTime());
            bundle.putLong("endtime", lottery.getEndTime());
            bundle.putLong("gaptime", lottery.getGapTimeMillis());
            intent.putExtras(bundle);

        }
        context.startActivity(intent);
    }

    /**
     * 大厅item点击顶部
     * 
     * @param context
     * @param hallItem
     */
    public void topClick(Context context, HallItem hallItem) {
        String eventNameMob = "hall_click_history";
        MobclickAgent.onEvent(mContext, eventNameMob, hallItem.getId());

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("kind", hallItem.getId());
        bundle.putString("lotteryname", hallItem.getName());
        intent.putExtras(bundle);
        if (LotteryUtils.isDigitalBet(hallItem.getId())) {
            intent.setClass(context, LotteryHistory.class);
        }
        else {
            intent.setClass(context, SportsLotteryHistory.class);
        }
        context.startActivity(intent);
    }

    public void wakeLock() {
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "time counter");
        mWakeLock.acquire();
    }

    public void wakeUnclok() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    public void setLotteryInf() {
        mLotteryInfControl.setLotteryInf();
    }

    public boolean checkTimeChange(long lastTimeMillis) {
        return mLotteryInfControl.checkTimeChange(lastTimeMillis);
    }
}
