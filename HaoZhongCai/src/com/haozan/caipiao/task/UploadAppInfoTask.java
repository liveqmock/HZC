package com.haozan.caipiao.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.types.AppInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ServiceUtil;

/**
 * 上传用户手机app信息
 * 
 * @author peter_wang
 * @create-time 2013-11-12 上午11:39:21
 */
public class UploadAppInfoTask
    extends Task {
    private static final String FOLDER = "/CaipiaoCache/";
    private static final String SUFFIX = ".txt";
    private static final String UPLOAD_URL = "http://m.haozan88.com/User/UpApp/upload";

    private Context mContext;
    private Handler mHandler;

    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    public UploadAppInfoTask(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        mSharedPreferences = ActionUtil.getSharedPreferences(context);
        mEditor = ActionUtil.getEditor(context);
    }

    @Override
    public void runTask() {
        List<AppInfo> mlistAppInfo = queryFilterAppInfo(); // 查询所有应用程序信息
        File file = saveAppInfoToSdCard(mlistAppInfo);
        String result = ServiceUtil.uploadFile(file, UPLOAD_URL);

        if ("200".equals(result)) {
            String localVersion = mSharedPreferences.getString("local_version", null);
            mEditor.putString("last_upload_version", LotteryUtils.fullVersion(mContext));
            mEditor.commit();
        }
    }

    private File saveAppInfoToSdCard(List<AppInfo> mlistAppInfo) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < mlistAppInfo.size(); i++) {
            AppInfo info = mlistAppInfo.get(i);
            str.append(info.getAppLabel() + ";" + info.getPkgName() + ";" + info.getPkVersion() + "\n");
        }

        String foldername = Environment.getExternalStorageDirectory().getPath() + FOLDER;
        File folder = new File(foldername);
        if (folder != null && !folder.exists()) {
            if (!folder.mkdir() && !folder.isDirectory()) {
                Logger.inf("vincent", "Error: make dir failed!");
                return null;
            }
        }

        String stringToWrite = str.toString();
        TelephonyManager telephone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String udid = telephone.getDeviceId();
        if (null == udid || "".equals(udid)) {
            udid = String.valueOf("T" + System.currentTimeMillis());
        }
        String localVersion = mSharedPreferences.getString("local_version", "").replace(".", "");
        String targetPath = foldername + localVersion + "_" + udid + SUFFIX;
        File targetFile = new File(targetPath);
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
        return targetFile;
    };

    private List<AppInfo> queryFilterAppInfo() {
        PackageManager pm = mContext.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations =
            pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfo> appInfos = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        appInfos.clear();
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                appInfos.add(getAppInfo(pm, app));
            }
        }
        return appInfos;
    }

    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(PackageManager pm, ApplicationInfo app) {
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
}