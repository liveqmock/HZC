package com.haozan.caipiao.task;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.UpdateUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.CustomDialog;

public class CheckUpdateTask
    extends AsyncTask<String, Long, Boolean> {

    private String updateURL;

    private Context context;
    // 是否服务器设置强制升级
    private boolean forceUpdate = false;
    private long fileSize;
    private DecimalFormat df;
    private String updateContent;
    private Editor databaseData;
    // add by vincent
    private String newVersionNum;

    private View eventView;
    private TextView tvVersion;
    private TextView tvAppSize;
    private TextView tvUpdateContent;

    private ProgressBar progress;

    private CustomDialog updateDialog;

    public CheckUpdateTask(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        databaseData = context.getSharedPreferences("user", 0).edit();
        df = new DecimalFormat("###.##");
        createDialogCenterPart();
    }

    public void setProgress(ProgressBar progress) {
        this.progress = progress;
    }

    private void createDialogCenterPart() {
        LayoutInflater inflater = LayoutInflater.from(context);
        eventView = inflater.inflate(R.layout.auto_update_dialog, null);
        tvVersion = (TextView) eventView.findViewById(R.id.new_version_num);
        tvAppSize = (TextView) eventView.findViewById(R.id.check_file_size);
        tvUpdateContent = (TextView) eventView.findViewById(R.id.update_content);
    }

    @Override
    protected void onPreExecute() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    private HashMap<String, String> initHashMap()
        throws Exception {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "soft_update");
        parameter.put("pid", LotteryUtils.getPid(context));
        TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String udid = telephone.getDeviceId();
        String mid = Build.MODEL;
        if (udid == null)
            udid = "0";
        else
            udid = HttpConnectUtil.encodeParameter(udid);
        if (mid == null)
            mid = "0";
        else
            mid = HttpConnectUtil.encodeParameter(mid);
        parameter.put("udid", udid);
        parameter.put("mid", mid);
        parameter.put("soft_version", LotteryUtils.fullVersion(context));
        parameter.put("mid", mid);
        return parameter;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean canDownload = false;
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(1, false, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (json != null) {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                updateURL = analyse.getData(json, "update_url");
                String updateWebURL = analyse.getData(json, "web_url");
                updateContent = analyse.getData(json, "update_content");
                String updateForce = analyse.getData(json, "force");
                // add by vincent
                newVersionNum = analyse.getData(json, "update_version");
                if (newVersionNum == null) {
                    newVersionNum = "未知版本";
                }
                else {
                    if (newVersionNum.length() > 6) {
                        newVersionNum =
                            newVersionNum.substring(newVersionNum.length() - 6, newVersionNum.length());
                    }
                }
                if (updateURL != null && updateWebURL != null) {
                    publishProgress(Long.valueOf(0));
                    fileSize = UpdateUtil.getUpdateSize(context, updateURL);
                    if (fileSize > 0) {
                        databaseData.putString("update_web_url", updateWebURL);
                        // add by vincent
                        databaseData.putString("update_content", updateContent);
                        databaseData.putString("update_version", newVersionNum);
                        if (updateForce != null && updateForce.equals("1")) {
                            forceUpdate = true;
                        }
                        databaseData.putBoolean("update_force", forceUpdate);
                        databaseData.commit();
                        OperateInfUtils.broadcast(context, "have_update");
                        canDownload = true;
                    }
                }
            }
        }
        return canDownload;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
        if (result) {
            double fileMSize = fileSize / (1024.0 * 1024);
            tvAppSize.setText("文件大小：" + df.format(fileMSize) + "M");
            tvVersion.setText("\n新版本：" + newVersionNum);
            tvUpdateContent.setText(updateContent);
            showUpdateDialog();
        }
        else {
            if (progress != null) {
                ViewUtil.showTipsToast(context, "您当前已经是最新版本");
            }
        }
    }

    protected void showUpdateDialog() {
        if (updateDialog == null) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("更新提示").setWarning().setContentView(eventView).setPositiveButton("下  载",
                                                                                                    new DialogInterface.OnClickListener() {
                                                                                                        public void onClick(DialogInterface dialog,
                                                                                                                            int which) {
                                                                                                            dialog.dismiss();
                                                                                                            Intent it =
                                                                                                                new Intent(
                                                                                                                           Intent.ACTION_VIEW,
                                                                                                                           Uri.parse(updateURL));
                                                                                                            context.startActivity(it);
                                                                                                        }
                                                                                                    }).beEmphasis();
            if (!forceUpdate) {
                customBuilder.setNegativeButton("两周后提醒", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        databaseData.putLong("date_fix_time", System.currentTimeMillis());
                        databaseData.putBoolean("check_update_later", true);
                        databaseData.commit();
                    }
                });
            }
            updateDialog = customBuilder.create();
        }
        updateDialog.show();
        updateDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (forceUpdate) {
                    ((Activity) context).finish();
                }
            }
        });
    }
}