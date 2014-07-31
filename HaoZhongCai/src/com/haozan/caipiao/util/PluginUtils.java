package com.haozan.caipiao.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.PluginDownloadTask;
import com.haozan.caipiao.types.PluginInf;
import com.haozan.caipiao.widget.CustomDialog;

/**
 * 插件操作工具栏
 * 
 * @author peter_feng
 * @create-time 2012-8-29 上午11:27:04
 */
public class PluginUtils {
    /**
     * 判断游戏插件是否存在
     * 
     * @param pluginInf 游戏信息
     * @return 是否存在
     */
    public static boolean checkGameExist(Context context, String packageName) {
        boolean isExist = true;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        }
        catch (NameNotFoundException e) {
            isExist = false;
        }
        return isExist;
    }

    /**
     * 显示确认下载对话框
     * 
     * @param context
     * @param name 下载的应用名
     * @param message 下载的描述语
     * @param url 下载的地址
     * @param isUpdate 是更新还是下载
     */
    public static void showPluginDownloadDialog(final Context context, final String name, String message,
                                                final String url, boolean isUpdate) {
        String title = null;
        String firstBtnMessage = null;
        if (isUpdate) {
            title = "升级提示";
            firstBtnMessage = "升  级";
        }
        else {
            title = "下载提示";
            firstBtnMessage = "下  载";
        }
        CustomDialog checkDownloadDialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle(title).setMessage(message).setPositiveButton(firstBtnMessage,
                                                                            new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog,
                                                                                                    int which) {
                                                                                    dialog.dismiss();
                                                                                    // 开启以通知方式下载游戏的线程
                                                                                    PluginDownloadTask downloadTask =
                                                                                        new PluginDownloadTask(
                                                                                                               context,
                                                                                                               name,
                                                                                                               null,
                                                                                                               null,
                                                                                                               null);
                                                                                    downloadTask.execute(url);
                                                                                }
                                                                            }).setNegativeButton("取  消",
                                                                                                 new DialogInterface.OnClickListener() {
                                                                                                     public void onClick(DialogInterface dialog,
                                                                                                                         int which) {
                                                                                                         dialog.dismiss();
                                                                                                     }
                                                                                                 });
        checkDownloadDialog = customBuilder.create();
        checkDownloadDialog.show();
    }

    /**
     * 打开第三方应用
     * 
     * @param context
     * @param bundle
     * @param packageName
     * @param activityName
     */
    public static void goPlugin(Context context, Bundle bundle, String packageName, String activityName) {
        ComponentName componetName = new ComponentName(packageName, activityName);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setComponent(componetName);
        context.startActivity(intent);
    }

    /**
     * 检测插件是否需要升级 无升级提示dialog
     * 
     * @param pluginInf 游戏信息
     * @param context
     * @return 是否插件版本较低，true代表版本较低
     */
    public static boolean checkPluginVersionNodialog(final PluginInf pluginInf, final Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pluginInf.getGamePackageName(), 0);
            if (packageInfo.versionCode < Integer.valueOf(pluginInf.getGameVersion())) {
                return true;
            }
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 获取服务器返回的插件信息
     * 
     * @param pluginInf 保存游戏信息
     * @param response_data 服务器返回的json格式游戏列表
     */
    public static void analysePluginData(PluginInf pluginInf, String json) {
        JsonAnalyse analyse = new JsonAnalyse();
        pluginInf.setGameName(analyse.getData(json, "name"));
        pluginInf.setGameDescription(analyse.getData(json, "description"));
        pluginInf.setGameDownloadUrl(analyse.getData(json, "url"));
        pluginInf.setGamePackageName(analyse.getData(json, "package"));
        pluginInf.setGameActivityName(analyse.getData(json, "class"));
        pluginInf.setGameVersion(analyse.getData(json, "version"));
    }
}
