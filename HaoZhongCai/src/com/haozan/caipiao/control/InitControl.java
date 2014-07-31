package com.haozan.caipiao.control;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.StartUp;
import com.haozan.caipiao.task.CheckConnectTask;
import com.haozan.caipiao.task.ClientCommunicationTask;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.cache.FileCache;
import com.igexin.sdk.aidl.Tag;
import com.igexin.slavesdk.MessageManager;

/**
 * 初始化操作control
 * 
 * @author peter_wang
 * @create-time 2013-10-12 下午1:40:43
 */
public class InitControl {
    private Context mContext;
    private Handler mHandler;

    public InitControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    /**
     * 创建快捷方式
     */
    public void createShortCut() {
        SharedPreferences preferences = ActionUtil.getSharedPreferences(mContext);
        Editor databaseData = ActionUtil.getEditor(mContext);
        boolean isCreateShortCut = preferences.getBoolean("create_shortcut", true);
        if (isCreateShortCut) {
            boolean flag = ifaddShortCut();// 如果已经创建，则不需要在创建
            if (flag == false) {
                addShortCut();
                databaseData.putBoolean("create_shortcut", false);
                databaseData.commit();
            }
        }
    }

    /**
     * 判断桌面是否已有快捷方式
     * 
     * @return boolean
     */
    private boolean ifaddShortCut() {
        boolean isInstallShortcut = false;
        final ContentResolver cr = mContext.getContentResolver();
        // 2.2系统是”com.android.launcher2.settings”,网上见其他的为"com.android.launcher.settings"
        final String AUTHORITY = "com.android.launcher.settings";
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c =
            cr.query(CONTENT_URI, new String[] {"title", "iconResource"}, "title=?",
                     new String[] {mContext.getString(R.string.app_name)}, null);// XXX表示应用名称。
        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        return isInstallShortcut;
    }

    /**
     * 具体执行创建快捷方式
     */
    private void addShortCut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置属性
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getResources().getString(R.string.app_name));
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(mContext, R.drawable.push);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        // 是否允许重复创建
        shortcut.putExtra("duplicate", false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(mContext, StartUp.class);
        // 设置启动程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        mContext.sendBroadcast(shortcut);
    }

    /**
     * 开启推送
     */
    public void startPushService() {
        MessageManager.getInstance().initialize(mContext);
        Tag tag[] = new Tag[2];
        tag[0] = new Tag();
        tag[1] = new Tag();
        tag[0].setName(LotteryUtils.getPid(mContext));
        tag[1].setName(mContext.getString(R.string.app_name));
        try {
        	MessageManager.getInstance().setTag(mContext, tag);
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("log", "MessageManager.getInstance().setTag error!");
		}
    }

    public void checkConnection() {
        TaskPoolService.getInstance().requestService(new CheckConnectTask(mContext, mHandler));
    }

    public void refreshLocalConnectionInf() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            long updatePhoneTime =
                ActionUtil.getSharedPreferences(mContext).getLong("last_use_phone_time", 0);
            long gapTime = System.currentTimeMillis() - updatePhoneTime;
            if (gapTime < 0 || gapTime > 60 * 60 * 24 * 7 * 1000 || updatePhoneTime == 0) {
                TaskPoolService.getInstance().requestService(new ClientCommunicationTask(mContext));
            }
        }
    }

    /**
     * 初始化聊天室
     */
    public void initChatroom() {
        // Bundle bundle = new Bundle();
        // bundle.putString(QplusSDK.APP_KEY, "9beece54-af94-45c2-85b1-9cb0acbcf4f6");
        // QplusSDK.getInstance().Init(mContext, bundle);
    }

    public Bitmap showFlash() {
        String url = ActionUtil.getSharedPreferences(mContext).getString("flash_imgUrl", "");

        if (!TextUtils.isEmpty(url)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                String endtime = ActionUtil.getSharedPreferences(mContext).getString("flash_endTime", "");
                String starttime = ActionUtil.getSharedPreferences(mContext).getString("flash_startTime", "");

                Date date1 = format.parse(endtime);
                Date date2 = format.parse(starttime);
                long endTimeMillis = date1.getTime();
                long startTimeMillis = date2.getTime();

                if (System.currentTimeMillis() - startTimeMillis > 0 &&
                    System.currentTimeMillis() - endTimeMillis < 0) {
// imageLoader.DisplayImage(imgUrl, flashImg);
                    Bitmap bitmap = FileCache.resizeImage(FileCache.getPicFilePath(url), 800, 480);
                    return bitmap;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
