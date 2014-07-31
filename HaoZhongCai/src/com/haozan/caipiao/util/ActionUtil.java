package com.haozan.caipiao.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.cqssc.CQSSCBetConfirm;
import com.haozan.caipiao.activity.bet.dfljy.DFLJYBetConfirm;
import com.haozan.caipiao.activity.bet.dlt.DLTBetConfirm;
import com.haozan.caipiao.activity.bet.hnklsf.HNKLSFBetComfirm;
import com.haozan.caipiao.activity.bet.klsf.KLSFBetComfirm;
import com.haozan.caipiao.activity.bet.pls.PLSBetConfirm;
import com.haozan.caipiao.activity.bet.plw.PLWBetConfirm;
import com.haozan.caipiao.activity.bet.qlc.QLCBetConfirm;
import com.haozan.caipiao.activity.bet.qxc.QXCBetConfirm;
import com.haozan.caipiao.activity.bet.renxuanjiu.RenXuanJiuActivity;
import com.haozan.caipiao.activity.bet.sd.SDBetConfirm;
import com.haozan.caipiao.activity.bet.shisichang.ShisichangActivity;
import com.haozan.caipiao.activity.bet.ssq.SSQBetConfirm;
import com.haozan.caipiao.activity.bet.swxw.SWXWBetConfirm;
import com.haozan.caipiao.activity.bet.syxw.SYXWBetConfirm;
import com.haozan.caipiao.activity.topup.AlipayRecharge;
import com.haozan.caipiao.activity.topup.RechargeFirstPage;
import com.haozan.caipiao.activity.webbrowser.GeneralWebBrowser;
import com.haozan.caipiao.util.security.EncryptUtil;

public class ActionUtil {

    public static SharedPreferences preferences;
    public static Editor databaseData;

    public static void buyIntegral(Context context) {
        LotteryApp appState = (LotteryApp) context.getApplicationContext();

        if (appState.getUsername() == null) {
            toLogin(context, "购买积分");
        }
        else if (appState.getAccount() <= 0) {
            toTopup(context);
            ViewUtil.showTipsToast(context, "余额不足，请先充值");
        }
        else {
            StringBuilder interalUrl = new StringBuilder();
            interalUrl.append(Domain.getHTTPURL(context) + "pagebuyscore");
            interalUrl.append(";jsessionid=" + appState.getSessionid());
            interalUrl.append("?phone=" + appState.getUsername());
            interalUrl.append("&types=0");
            interalUrl.append("&pid=" + LotteryUtils.getPid(context));
            interalUrl.append("&source=android");
            interalUrl.append("&timestamp=" + appState.getTime());
            interalUrl.append("&sign=" +
                EncryptUtil.MD5Encrypt(appState.getTime() + LotteryUtils.getKey(context)));

            toWebBrowser(context, "购买积分", interalUrl.toString());
        }
    }

    /**
     * 打开内嵌网页
     * 
     * @param context
     * @param title 页面标题
     * @param url 打开的url
     */
    public static void toWebBrowser(Context context, String title, String url) {
        Intent intent = new Intent();
        Bundle browserBundle = new Bundle();
        browserBundle.putString("title", title);
        browserBundle.putString("url", url);
        intent.putExtras(browserBundle);
        intent.setClass(context, GeneralWebBrowser.class);
        context.startActivity(intent);
    }

    public static void toPointReward(Context context) {
        LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
        String url =
            Domain.getHTTPURL(context) + "scoreIndex.jsp;jsessionid=" + appState.getSessionid() + "?pid=" +
                LotteryUtils.getPid(context) + "&source=android";

        toWebBrowser(context, "积分乐园", url);
    }

    public static void toTopup(Context context) {
// SharedPreferences preferences = context.getSharedPreferences("user", 0);
// int kind = preferences.getInt("last_topup_way", 0);
// Class<?> cls = RechargeSelection.class;
// if (kind == 0) {
// cls = RechargeSelection.class;
// }
// else if (kind == 1) {
// cls = RechargeSelection.class;
// }
// else if (kind == 2) {
// cls = UnionpayRecharge.class;
// }
// else if (kind == 3) {
// cls = UnionRreditCardpayRecharge.class;
// }
// else if (kind == 4) {
// cls = SNDARecharge.class;
// }
// else if (kind == 5) {
// cls = AlipayRecharge.class;
// }
// else if (kind == 6) {
// cls = PhoneCardRecharge.class;
// }
// else if (kind == 7) {
// cls = WapBankRecharge.class;
// }
// else if (kind == 8) {
// cls = BonusRecharge.class;
// }
// else if (kind == 9) {
// cls = MobileRecharge.class;
// }
// else if (kind == 10) {
// cls = ChinaUnionPaycharge.class;
// }
// Intent intent = new Intent();
// intent.setClass(context, cls);
// context.startActivity(intent);
        String packName = LotteryUtils.getPackageName(context) + ".activity.topup";
        String clsName = ActionUtil.getTopUpClassName(context);
        Class<?> c = null;
        Intent intent = new Intent();
        try {
            c = Class.forName(packName + "." + clsName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        intent.setClass(context, c);
        context.startActivity(intent);
    }

// public static String toTopupClassName(int kind) {
// return LotteryUtils.topUpClassNameOrg[kind];
// }

    public static String getTopUpClassName(Context context) {
        String className = "RechargeSelection";
        SharedPreferences preferences = context.getSharedPreferences("user", 0);
        try {
            className = preferences.getString("last_topup_way", "RechargeSelection");
        }
        catch (Exception e) {
            className = "RechargeSelection";
        }
        return className;
    }

    public static void toLogin(Context context, String flag) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", flag);
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
// intent.setClass(context, StartUp.class);
        intent.setClass(context, Login.class);
        context.startActivity(intent);
    }

    /**
     * 是否完善信息
     * 
     * @param context
     * @return
     */
    public static boolean isPerfectInf(Context context) {
        LotteryApp appState = (LotteryApp) context.getApplicationContext();
        if (appState.getUsername() != null) {
            if (appState.getPerfectInf() == 0) {
                return false;
            }
        }
        return true;
    }

    public static void toBetOldNum(Context context, String kind, Bundle bundle) {
        Class<?> cls = null;
        if (kind.equals("ssq")) {
            cls = SSQBetConfirm.class;
            SSQBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("3d")) {
            cls = SDBetConfirm.class;
            SDBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("qlc")) {
            cls = QLCBetConfirm.class;
            QLCBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("dfljy")) {
            cls = DFLJYBetConfirm.class;
            DFLJYBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("swxw")) {
            cls = SWXWBetConfirm.class;
            SWXWBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("dlt")) {
            cls = DLTBetConfirm.class;
            DLTBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("pls")) {
            cls = PLSBetConfirm.class;
            PLSBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("plw")) {
            cls = PLWBetConfirm.class;
            PLWBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("qxc")) {
            cls = QXCBetConfirm.class;
            QXCBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("sfc")) {
            cls = ShisichangActivity.class;
        }
        else if (kind.equals("r9")) {
            cls = RenXuanJiuActivity.class;
        }
        else if (kind.equals("jx11x5")) {
            cls = SYXWBetConfirm.class;
            SYXWBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("hnklsf")) {
            cls = HNKLSFBetComfirm.class;
            HNKLSFBetComfirm.betLocalList.clear();
        }
        else if (kind.equals("klsf")) {
            cls = KLSFBetComfirm.class;
            KLSFBetComfirm.betLocalList.clear();
        }
        else if (kind.equals("cqssc")) {
            cls = CQSSCBetConfirm.class;
            CQSSCBetConfirm.betLocalList.clear();
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtras(bundle);
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public static boolean toPage(Context context, String packageName, String pageClass, Bundle bundle) {
        Class<?> c = null;
        Intent intent = new Intent();
        try {
            c = Class.forName(packageName + "." + pageClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.setClass(context, c);
            context.startActivity(intent);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            ViewUtil.showTipsToast(context, "客户端版本较低，建议升级");
            return false;
        }
    }

    /**
     * 带参数传递页面跳转
     * 
     * @param context
     * @param pageClass
     * @param bundle
     * @return
     */
    public static boolean toPage(Context context, String pageClass, Bundle bundle) {
        Class<?> c = null;
        Intent intent = new Intent();
        try {
            c = Class.forName(pageClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.setClass(context, c);
            context.startActivity(intent);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 跳转到指定页面
     * 
     * @param context
     * @param pageClass 包含路径的全局页面
     * @return
     */
    public static boolean toPage(Context context, String pageClass) {
        return toPage(context, pageClass, null);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        if (preferences == null) {
            Context appContext = context.getApplicationContext();
            preferences = appContext.getSharedPreferences("user", 0);
        }
        return preferences;
    }

    public static Editor getEditor(Context context) {
        if (databaseData == null) {
            Context appContext = context.getApplicationContext();
            databaseData = getSharedPreferences(appContext).edit();
        }
        return databaseData;
    }

    public static LotteryApp getLotteryApp(Context context) {
        return (LotteryApp) ((Activity) context).getApplicationContext();
    }

    public static void toTopupNew(Context context) {
        SharedPreferences mSharedPreferences = getSharedPreferences(context);
        Intent intent = new Intent();
        if (mSharedPreferences.getString("last_login_type", "").equals("alipay")) {
            intent.setClass(context, AlipayRecharge.class);
        }
        else {
            String className = mSharedPreferences.getString("last_topup_way", "");
            if ("".equals(className)) {
                intent.setClass(context, RechargeFirstPage.class);
                context.startActivity(intent);
            }
            else {
                if (toPage(context, className) == false) {
                    intent.setClass(context, RechargeFirstPage.class);
                    context.startActivity(intent);
                }
            }
        }
    }

    /**
     * 判断是否登录超时
     * 
     * @param context
     * @param status
     * @return
     */
    public static boolean checkIfLogoff(Context context, int status) {
        if (status == 302 || status == 304) {
            ActionUtil.toLogin(context, "");
            ViewUtil.showTipsToast(context, "登陆超时，请重新登录");
            return true;
        }
        else {
            return false;
        }
    }
}