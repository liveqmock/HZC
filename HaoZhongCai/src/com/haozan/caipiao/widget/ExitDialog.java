package com.haozan.caipiao.widget;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Main;
import com.haozan.caipiao.control.LoginControl;
import com.haozan.caipiao.fragment.MainActivity;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.weiboutil.AccessInfoHelper;
import com.haozan.caipiao.util.weiboutil.InfoHelper;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

public class ExitDialog
    extends Dialog {
    private LotteryApp appState;
    private SharedPreferences preferences;
    private Editor databaseData;

    private Button actionFirst;
    private Button actionSecond;
    private Button actionThird;

    private Context context;

    public ExitDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.exit_dialog);
        setupViews();
        init();
    }

    private void setupViews() {
        actionFirst = (Button) findViewById(R.id.action_first);
        actionFirst.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                String eventName;
                map.put("inf", "username [" + appState.getUsername() + "]: v2 user center click log off");
                eventName = "v2 user center click log off";
                FlurryAgent.onEvent(eventName, map);
                String eventNameMob = "exit_dialog_operate";
                MobclickAgent.onEvent(context, eventNameMob, "logoff");
                OperateInfUtils.clearSessionId(context);
                OperateInfUtils.broadcast(context, "logoff");
                // add by vincent
                cancelBind();
                LoginControl control = new LoginControl(context, null);
                control.cancleAutoLogin();

                ViewUtil.showTipsToast(context, "注销成功");
                closeDialog();
            }

        });
        actionSecond = (Button) findViewById(R.id.action_second);
        actionSecond.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeDialog();
                exit();
                String eventNameMob = "exit_dialog_operate";
                MobclickAgent.onEvent(context, eventNameMob, "exit");
            }
        });
        actionThird = (Button) findViewById(R.id.action_third);
        actionThird.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void exit() {
// Intent intent = new Intent();
// intent.setClass(context, StartUp.class);
// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
// intent.setAction("caipiao.action.EXIT");
// context.startActivity(intent);
        ((Activity) context).finish();
    }

    private void init() {
        preferences = context.getSharedPreferences("user", 0);
        databaseData = context.getSharedPreferences("user", 0).edit();
        appState = (LotteryApp) ((Activity) context).getApplication();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.95
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p); // 设置生效
        if (appState.getUsername() == null)
            actionFirst.setVisibility(View.GONE);
        else {
            String registerType = appState.getRegisterType();
            if ("1".equals(registerType)) {
                actionFirst.setText("注销登录");
            }
            else {
                actionFirst.setText("注销登录");
            }
        }
    }

    private void closeDialog() {
        this.cancel();
    }

    public void cancelBind() {
        databaseData.putString("last_login_type", "");
        databaseData.commit();
        // 取消新浪微博绑定或qq绑定
        String registerType = appState.getRegisterType();
        if (!"1".equals(registerType)) {
            if ("2".equals(registerType)) {// sina
                if (isBindSina()) {
                    Thread thread = new Thread(new EndSessionThread());
                    thread.start();
                }
            }
            else if ("3".equals(registerType)) {// qq
                databaseData.putString("qq_openid", null);
                databaseData.commit();
                Tencent mTencent = Tencent.createInstance(LotteryConfig.getQQAPPID(context), context);
                mTencent.setOpenId(null);
            }
            else if ("4".equals(registerType)) {
                SharedPreferences.Editor editor = ActionUtil.getEditor(context);
                editor.putString("alipay_account", "");
                editor.commit();
            }
        }
    }

    // 是否已经绑定新浪
    protected Boolean isBindSina() {
        LotteryApp accessInfo = null;
        accessInfo = InfoHelper.getAccessInfo(context);
        if (accessInfo != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public Handler endSessionHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ViewUtil.showTipsToast(context, "取消绑定成功");
        }
    };

    public class EndSessionThread
        implements Runnable {
        public void run() {
            AccessInfoHelper accessDBHelper = new AccessInfoHelper(context);
            accessDBHelper.open();
            accessDBHelper.delete();
            accessDBHelper.close();
            endSessionHandle.sendEmptyMessage(201);
        }
    }
}
