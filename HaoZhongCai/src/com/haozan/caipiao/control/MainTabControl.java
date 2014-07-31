package com.haozan.caipiao.control;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.CheckUpdateRequest;
import com.haozan.caipiao.task.UploadAppInfoTask;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.NewVersionInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.ExitDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 初始化操作control
 * 
 * @author peter_wang
 * @create-time 2013-10-12 下午1:40:43
 */
public class MainTabControl {
    private Context mContext;
    private Handler mHandler;

    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    private boolean isUpdateByUser = false;

    public MainTabControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        mSharedPreferences = ActionUtil.getSharedPreferences(context);
        mEditor = ActionUtil.getEditor(context);
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
            ((Activity) mContext).finish();
            return true;
        }
        return false;
    }

    // 检测是否需要升级，先判断是否要强制升级，如不需要再判断是否选择两周内不提醒，若未设置默认一天检测一次
    public void autoCheckUpdate() {
        Boolean forceUpdate = mSharedPreferences.getBoolean("update_force", false);
        long lastTime = mSharedPreferences.getLong("last_check_update_time", 0);
        Boolean checkUpdateLater = mSharedPreferences.getBoolean("check_update_later", false);
        long updateFixTime = mSharedPreferences.getLong("date_fix_time", 0);
        String localVersion = mSharedPreferences.getString("local_version", null);
        if (localVersion == null) {
            resetUpdateDatabase();
        }
        else {
            if (!localVersion.equals(LotteryUtils.fullVersion(mContext))) {
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
            autoCheckUpdateRequest();
        }
        else {
            if (checkUpdateLater) {
                gapTime = System.currentTimeMillis() - updateFixTime;
                if (gapTime > 60 * 60 * 24 * 14 * 1000 || gapTime < 0 || updateFixTime == 0) {
                    mEditor.putLong("date_fix_time", 0);
                    mEditor.putBoolean("check_update_later", false);
                    autoCheckUpdateRequest();
                }
            }
            else {
                gapTime = System.currentTimeMillis() - lastTime;
                if (gapTime > 60 * 60 * 24 * 1000 || gapTime < 0 || lastTime == 0) {
                    autoCheckUpdateRequest();
                }
            }
            mEditor.commit();
        }
    }

    private void resetUpdateDatabase() {
        mEditor.putString("local_version", LotteryUtils.fullVersion(mContext));
        mEditor.putLong("date_fix_time", 0);
        mEditor.putBoolean("check_update_later", false);
        mEditor.putString("update_web_url", null);
        mEditor.commit();
    }

    // 自动检测彩票是否有更新
    public void autoCheckUpdateRequest() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mEditor.putLong("last_check_update_time", System.currentTimeMillis());

            if (HttpConnectUtil.isNetworkAvailable(mContext)) {
                updateRequest();
            }
        }
    }

    // 用户自己检测彩票是否有更新
    public void checkUpdateRequest() {
        UEDataAnalyse.onEvent(mContext, "hall_click_update");

        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mEditor.putLong("last_check_update_time", System.currentTimeMillis());
            isUpdateByUser = true;
            updateRequest();
        }
        else {
            mHandler.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    private void updateRequest() {
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(new CheckUpdateRequest(mContext,mHandler)));
    }

    /**
     * 亲加聊天室
     */
    public void initCSMM() {
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

    public void notificationAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String type = bundle.getString("type");
            String action = bundle.getString("action");
            String actionInf = bundle.getString("action_inf");
            if (type != null && type.equals("notification") && action != null && actionInf != null) {
                if (action.equals("webpage")) {
                    ActionUtil.toWebBrowser(mContext, "推送信息页面", actionInf);
                }
                else if (actionInf.equals("open_local_page")) {
                    ActionUtil.toPage(mContext, actionInf);
                }
            }
        }
    }

    /**
     * 获取本地第三方安装包信息 并上传
     */
    public void submitLocalAppData() {
        String currentVersion = mSharedPreferences.getString("local_version", "");
        String lastUploadVersion = mSharedPreferences.getString("last_upload_version", "");
        if (!currentVersion.equals(lastUploadVersion) && HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new UploadAppInfoTask(mContext, mHandler));
        }

    }

    public void pressBackKey() {
        if (LotteryUtils.getPid(mContext).equals("101201"))
            ((Activity) mContext).finish();
        else {
            ExitDialog exitDialog = new ExitDialog(mContext);
            exitDialog.show();
        }
    }

    public void showUpdateDialog(final NewVersionInfo info) {
        View eventView = View.inflate(mContext, R.layout.auto_update_dialog, null);
        TextView tvVersion = (TextView) eventView.findViewById(R.id.new_version_num);
        TextView tvAppSize = (TextView) eventView.findViewById(R.id.check_file_size);
        TextView tvUpdateContent = (TextView) eventView.findViewById(R.id.update_content);

        double fileMSize = info.getSize() / (1024.0 * 1024);
        DecimalFormat df = new DecimalFormat("###.##");
        tvAppSize.setText("文件大小：" + df.format(fileMSize) + "M");
        tvVersion.setText("\n新版本：" + info.getNewVersionNum());
        tvUpdateContent.setText(info.getUpdateContent());

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setTitle("更新提示").setWarning().setContentView(eventView).setPositiveButton("下  载", new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        dialog.dismiss();
                                                                                                        Intent it = new Intent( Intent.ACTION_VIEW, Uri.parse(info.getUpdateURL()));
                                                                                                        mContext.startActivity(it);
                                                                                                    }
                                                                                                }).beEmphasis();
        if (!info.isUpdateForce()) {
            customBuilder.setNegativeButton("两周后提醒", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mEditor.putLong("date_fix_time", System.currentTimeMillis());
                    mEditor.putBoolean("check_update_later", true);
                    mEditor.commit();
                }
            });
        }
        CustomDialog updateDialog = customBuilder.create();
        updateDialog.show();
        updateDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (info.isUpdateForce()) {
                    ((Activity) mContext).finish();
                }
            }
        });
    }

    public void showUpdateFail() {
        if (isUpdateByUser) {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.update_fail));
        }
    }
}
