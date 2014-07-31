package com.haozan.caipiao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
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
import com.haozan.caipiao.activity.topup.AlipayRecharge;
import com.haozan.caipiao.activity.topup.BankCardNetRecharge;
import com.haozan.caipiao.activity.topup.BonusRecharge;
import com.haozan.caipiao.activity.topup.ChinaUnionPaycharge;
import com.haozan.caipiao.activity.topup.PhoneCardRecharge;
import com.haozan.caipiao.activity.topup.SNDARecharge;
import com.haozan.caipiao.activity.topup.UnionPayCreditCardTopup;
import com.haozan.caipiao.activity.topup.UnionpayDebitCardTopup;
import com.haozan.caipiao.activity.topup.WapBankRecharge;
import com.haozan.caipiao.types.Ball;
import com.haozan.caipiao.util.weiboutil.InfoHelper;
import com.haozan.caipiao.widget.PredicateLayout;
import com.haozan.caipiao.widget.SFCHistoryItemLayout;

public class LotteryUtils {

    public static final String TELEPHONE = "4006326360";
    public static final String QQ = "4006938038";

    public static final String SSQ = "ssq";
    public static final String SD = "3d";
    public static final String QLC = "qlc";
    public static final String DFLJY = "dfljy";
    public static final String SWXW = "swxw";
    public static final String DLT = "dlt";
    public static final String PLS = "pls";
    public static final String PLW = "plw";
    public static final String QXC = "qxc";
    public static final String SFC = "sfc";
    public static final String JCZQ = "jczq";
    public static final String RJ = "r9";
    public static final String KLSF = "klsf";
    public static final String CQSSC = "cqssc";
    public static final String HNKLSF = "hnklsf";
    public static final String JXSSXW = "jx11x5";
    public static final String JCLQ = "jclq";
    public static final String JXSSC = "jxssc";
    public static final String JLKS = "jlk3";
    public static final String DCSFGG = "dcsfgg";
    public static final String GUESS = "guess";

    // 彩种齐全版
    public static final Class[] lotteryclass = new Class[] {SSQBetConfirm.class, SDBetConfirm.class,
            QLCBetConfirm.class, DFLJYBetConfirm.class, SWXWBetConfirm.class, DLTBetConfirm.class,
            PLSBetConfirm.class, PLWBetConfirm.class, QXCBetConfirm.class, ShisichangActivity.class,
            JCZQActivity.class, RenXuanJiuActivity.class, KLSFBetComfirm.class, CQSSCBetConfirm.class,
            HNKLSFBetComfirm.class, SYXWBetConfirm.class, JCLQActivity.class, JXSSCBetConfirm.class,
            JLKSBetConfirm.class, DCSFGGActivity.class};

    public static final Class[] lotteryKindclass = new Class[] {SSQActivity.class, SDActivity.class,
            QLCActivity.class, DFLJYActivity.class, SWXWActivity.class, DLTActivity.class, PLSActivity.class,
            PLWActivity.class, QXCActivity.class, ShisichangActivity.class, JCZQActivity.class,
            RenXuanJiuActivity.class, KLSFActivity.class, CQSSCActivity.class, HNKLSFActivity.class,
            SYXWActivity.class, JCLQActivity.class, JXSSCActivity.class, JLKSActivity.class, DCSFGGActivity.class};

    public static final String[] LOTTERY_NAMES = new String[] {"双色球", "3D", "七乐彩", "东方6+1", "15选5", "大乐透",
            "排列3", "排列5", "七星彩", "胜负彩", "竞彩足球", "任选9", "广西快乐十分", "时时彩", "快乐十分", "11选5", "竞彩篮球", "新时时彩",
            "新快三", "胜负过关", "积分竞猜"};
    public static final String[] LOTTERY_ID = new String[] {SSQ, SD, QLC, DFLJY, SWXW, DLT, PLS, PLW, QXC,
            SFC, JCZQ, RJ, KLSF, CQSSC, HNKLSF, JXSSXW, JCLQ, JXSSC, JLKS, DCSFGG, GUESS};

    public static final String[] LOTTERY_DIGITAL = new String[] {SSQ, SD, QLC, DFLJY, SWXW, DLT, PLS, PLW,
            QXC, SFC, RJ, KLSF, CQSSC, HNKLSF, JXSSXW, JXSSC, JLKS};

    public static final String getLotteryName(String name) {
        for (int j = 0; j < LotteryUtils.LOTTERY_ID.length; j++)
            if (LotteryUtils.LOTTERY_ID[j].equals(name))
                return LotteryUtils.LOTTERY_NAMES[j];
        return "";
    }

    public static final int getLotteryIcon(String name) {
        for (int j = 0; j < LotteryUtils.LOTTERY_ID.length; j++)
            if (LotteryUtils.LOTTERY_ID[j].equals(name))
                return LotteryUtils.HALL_ITEM_ICON[j];
        return -1;
    }

    /**
     * 是否是数字彩
     * 
     * @param id
     * @return
     */
    public static boolean isDigitalBet(String id) {
        for (int i = 0; i < LOTTERY_DIGITAL.length; i++) {
            if (LOTTERY_DIGITAL[i].equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static final int[] HALL_ITEM_ICON = {R.drawable.hall_ssq_icon_normal,
            R.drawable.hall_sd_icon_normal, R.drawable.hall_qlc_icon_normal,
            R.drawable.hall_dfljy_icon_normal, R.drawable.hall_swxw_icon_normal,
            R.drawable.hall_cjdlt_icon_normal, R.drawable.hall_pls_icon_normal,
            R.drawable.hall_plw_icon_normal, R.drawable.hall_qxc_icon_normal,
            R.drawable.hall_sfc_icon_normal, R.drawable.hall_jczq_icon_normal,
            R.drawable.hall_rxj_icon_normal, R.drawable.hall_hnklsf_normal,
            R.drawable.hall_cqssc_icon_normal, R.drawable.hall_hnklsf_normal,
            R.drawable.hall_jx11x5_icon_normal, R.drawable.hall_jclq_icon_normal,
            R.drawable.hall_jxssc_icon_normal, R.drawable.hall_jlks_icon_normal, R.drawable.hall_dcsfgg_icon_normal};
    public static final String[] HALL_DEFALUT_CONTENT = new String[] {"?,?,?,?,?,?|?", "?,?,?",
            "?,?,?,?,?,?,?|?", "?,?,?,?,?,?|?", "?,?,?,?,?", "?,?,?,?,?|?,?", "?,?,?", "?,?,?,?,?",
            "?,?,?,?,?,?,?,?", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "?,?,?,?,?",
            "?,?,?,?,?", "?,?,?,?,?,?,?,?", "?,?,?,?,?", "", "?,?,?,?,?", "?,?,?", "", ""};
    //public static final String lotteryArray =
     //   "jlk3|ssq|guess|dlt|jx11x5|jxssc|cqssc|jczq|hnklsf|3d|jclq|pls|r9|sfc|qlc|qxc|plw|swxw|dfljy";
    public static final String lotteryArray =
            "ssq|jx11x5|dlt|cqssc|3d|jczq|jclq|qlc|pls|plw|jlk3|guess|jxssc|hnklsf|r9|sfc|qxc|dcsfgg";

    public static final String lotteryArrayAdd =
        "jlk3|ssq|guess|dlt|jx11x5|jxssc|cqssc|jczq|hnklsf|3d|jclq|pls|r9|sfc|qlc|qxc|plw|swxw|dfljy";
    public static final String lotteryArrayMinus = "";

    public static final String[] animals = new String[] {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡",
            "狗", "猪"};
    public static final String[] BALL_NAME = new String[] {"大", "小", "单", "双"};

    // 合买
    public static final Class[] unitelotteryclass = new Class[] {SSQBetConfirm.class, SDBetConfirm.class,
            DLTBetConfirm.class};
// public static final String[] uniteLotteryEnglishNames = new String[] {"ssq", "3d", "dlt", "sfc", "r9"};
    public static final String[] uniteLotteryEnglishNames = new String[] {"ssq", "3d", "dlt", "jczq"};
    // 投注记录
// public static final String[] lotteryNamesPursuit = new String[] {"双色球", "3D", "七乐彩", "东方6+1", "15选5",
// "时时乐", "大乐透", "排列3", "排列5", "七星彩", "胜负彩", "22选5", "竞彩足球", "任选9", "广西快乐十分", "时时彩", "11选5", "竞彩篮球",
// "快乐十分", "新快3", "新时时彩"};
// public static final String[] lotteryEnglishNamesPursuit = new String[] {"ssq", "3d", "qlc", "dfljy",
// "swxw", "ssl", "dlt", "pls", "plw", "qxc", "sfc", "22x5", "jczq", "r9", "klsf", "cqssc",
// "jx11x5", "jclq", "hnklsf", "jlk3", "jxssc"};

    public static final int LOTTERYKIND = 20;

    public static final String[] betHisBtnText = new String[] {"所有彩种", "双色球", "3D", "七乐彩", "东方6+1", "15选5",
            "时时乐", "大乐透", "排列3", "排列5", "七星彩", "胜负彩", "22选5", "竞彩足球", "任选9", "广西快乐十分", "时时彩", "11选5", "竞彩篮球",
            "快乐十分", "胜负过关"};

    public static final String[] betHisWayBtnText = new String[] {"所有订单", "投注成功", "待开奖订单", "待支付订单", "中奖订单"};

    public static final String[] popMenuInBtnText = new String[] {"充值", "奖金", "活动赠送", "撤单返款", "提现失败",
            "电子劵转入", "解冻资金", "返点/佣金"};
    public static final String[] popMenuOutBtnText = new String[] {"购彩", "提现", "电子劵过期", "增值服务费", "冻结奖金",
            "充值退款", "其它扣款"};
    public static final String[] popMenuAwardBtnText = new String[] {"全部中奖", "普通中奖", "追号中奖"};
    // 全国省市名称
    private static final String[] provinces = {"安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河南",
            "河北", "黑龙江", "湖南", "湖北", "江苏", "江西", "吉林", "辽宁", "宁夏", "内蒙古", "上海", "山东", "四川", "山西", "陕西", "天津",
            "青海", "西藏", "新疆", "云南", "浙江"};
    // cqssc
    public static final String[] textArrayCQSSC = {"五星通选", "五星直选", "三星组六", "三星组三", "三星直选", "二星组选", "二星直选",
            "一星直选", "大小单双"};
    public static final String[] moneyArrayCQSSC = {"最高2万元", "最高10万元", "奖金160元", "奖金320元", "奖金1000元",
            "奖金50元", "奖金100元", "奖金10元", "奖金4元"};
    public static final String[] CQSSCWay = {"cqssc_wxtx", "cqssc_wxfs", "cqssc_sixfs", "cqssc_sxzl",
            "cqssc_sxzs", "cqssc_sxfs", "cqssc_exzx", "cqssc_exfs", "cqssc_yxfs", "cqssc_dxds"};
    // jxssc
    public static final String[] textArrayJXSSC = {"五星通选", "五星直选", "四星直选", "三星组六", "三星组三", "三星直选", "二星直选",
            "任选二", "一星直选", "任选一", "大小单双"};
    public static final String[] moneyArrayJXSSC = {"最高2万元", "奖金11.6万元", "最高1万元", "奖金190元", "奖金385元",
            "奖金1160元", "奖金116元", "奖金116元", "奖金11元", "奖金11元", "奖金4元"};
    public static final String[] JXSSCWay = {"jxssc_wxtx", "jxssc_wxfs", "jxssc_sixfs", "jxssc_sxzl",
            "jxssc_sxzs", "jxssc_sxfs", "jxssc_exfs", "jxssc_rxer", "jxssc_yxfs", "jxssc_rxy", "jxssc_dxds"};
    // klsf
    public static final String[] textArrayKLSF = {"好运特", "好运一", "好运二", "好运三", "好运四", "好运五"};
    public static final String[] moneyArrayKLSF = {"奖金20元", "奖金4元", "奖金20元", "奖金120元", "奖金1120元", "奖金20000元"};
    public static final String[] KLSFWay = {"klsf_lucky_special", "klsf_lucky_one", "klsf_lucky_two",
            "klsf_lucky_three", "klsf_lucky_four", "klsf_lucky_five"};
    // hnklsf
    public static final String[] textArrayHNKLSF = {"选一数投", "选一红投", "任选二", "选二连直", "选二连组", "任选三", "选三前直",
            "选三前组", "任选四", "任选五"};
    public static final String[] textArrayHNKLSFSwitch = {"选一数投", "选一红投", "任选二", "选二连直", "选二连组", "任选三",
            "选三前直", "选三前组", "任选四", "任选五"};
    public static final String[] moneyArrayHNKLSF = {"奖金24元", "奖金8元", "奖金8元", "奖金62元", "奖金31元", "奖金24元",
            "奖金8000元", "奖金1300元", "奖金80元", "奖金320元"};
    public static final String[] HNKLSFWay = {"hnklsf_lucky_zero", "hnklsf_lucky_one", "hnklsf_lucky_two",
            "hnklsf_lucky_three", "hnklsf_lucky_four", "hnklsf_lucky_five", "hnklsf_lucky_six",
            "hnklsf_lucky_seven", "hnklsf_lucky_eight", "hnklsf_lucky_nine"};
    public static final String[] HNTypeNum = {"101", "102", "20", "22", "21", "30", "34", "33", "40", "50",};

    // pls
    public static final String[] textArrayPLS = {"直选单复式", "组三包号", "组六包号", "组三单复式"};
    public static final String[] moneyArrayPLS = {"奖金1000元", "奖金320元", "奖金160元", "奖金160元"};
    public static final String[] plsWay = {"plszx", "plszs", "plszl", "plszsdf"};
    // ssq
    public static final String[] textArraySSQ = {"标准", "胆拖"};
    public static final String[] moneyArraySSQ = {"", ""};
    public static final String[] ssqWay = {"ssq_standard", "ssq_special"};
    // dlt
    public static final String[] textArrayDLT = {"标准", "胆拖"};
    public static final String[] moneyArrayDLT = {"", ""};
    public static final String[] dltWay = {"dlt_stanbdar", "dlt_dantuo"};
    // 3D
    public static final String[] textArrayThreeD = {"直选单复式", "组三包号", "组六包号", "组三单复式", "直选和值", "组三和值", "组六和值"};
    public static final String[] moneyArrayThreeD = {"奖金1000元", "奖金320元", "奖金160元", "奖金320元", "奖金1000元",
            "奖金320元", "奖金160元"};
    public static final String[] sdWay = {"sdzx", "sdzs", "sdzl", "sdzsdf", "sdzxhz", "sdzshz", "sdzlhz"};
    // SSL
    public static final String[] textArraySSL = {"直选", "组三", "组六(包号)", "前二", "后二", "前一", "后一"};
    public static final String[] moneyArraySSL = {"奖金980元", "奖金320元", "奖金160元", "奖金98元", "奖金98元", "奖金10元",
            "奖金10元"};
    public static final String[] sslWay = {"ssl_zx", "ssl_zs", "ssl_zl", "ssl_qe", "ssl_he", "ssl_qy",
            "ssl_hy"};
    // 十一选五
    public static final String[] textArray = {"任选二", "任选三", "任选四", "任选五", "任选六", "任选七", "任选八", "前一", "前二直选",
            "前二组选", "前三直选", "前三组选"};
    public static final String[] moneyArray = {"奖金6元", "奖金19元", "奖金78元", "奖金540元", "奖金90元", "奖金26元", "奖金9元",
            "奖金13元", "奖金130元", "奖金65元", "奖金1170元", "奖金195元"};
    public static final String[] syxwWay = {"syxwrx2", "syxwrx3", "syxwrx4", "syxwrx5", "syxwrx6", "syxwrx7",
            "syxwrx8", "syxwqy", "syxwqe_zhixuan", "syxwqe_zuxuan", "syxwqs_zhixuan", "syxwqs_zuxuan"};
    public static final String[] syxwWayMethod = {"11_RX2", "11_RX3", "11_RX4", "11_RX5", "11_RX6", "11_RX7",
            "11_RX8", "11_RX1", "11_ZXQ2_D", "11_ZXQ2", "11_ZXQ3_D", "11_ZXQ3"};

    public static final String BET_PROTOCOL_URL = "http://download.haozan88.com/publish/protocal/bet.html";
    public static final String GIVELOTTERY_PROTOCOL_URL =
        "http://download.haozan88.com/publish/protocal/givelottery.html";
    public static final String REGISTER_PROTOCOL_URL =
        "http://download.haozan88.com/publish/protocal/register.html";
    public static final String HELP_URL = "http://download.haozan88.com/publish/about/help.html";
    public static final String HELP_RECHARGE_URL = "http://download.haozan88.com/publish/about/recharge_help.html";
    // user new center
    public static final int AlL_lOTTERY_DATA = 0;
    public static final int WIN_lOTTERY_DATA = 1;
    public static final int WAIT_lOTTERY_DATA = 2;
    public static final int PURSUIT_lOTTERY_DATA = 3;
    public static final int UNITE_lOTTERY_DATA = 4;

    public static final String AUTHORIZE_URL_HEAD = "http://m.haozan88.com/?g=User&m=Login&a=login";
    public static final String REDIRECT_URL_START =
        "http://m.haozan88.com/index.php?g=User&m=Login&a=callback";

    public static final String[] initProvinceCityMap(String key) {
        Map<String, String[]> map = new HashMap<String, String[]>();
        for (int i = 0; i < provinces.length; i++)
            map.put(provinces[i], ZhongguoCityName.place[i]);
        return map.get(key);
    }

    public static final String[] FREQUENT_LOTTERY = {"cqssc", "jx11x5", "hnklsf", "jxssc", "jlk3"};

    public static boolean isFrequentLottery(String kind) {
        boolean isFre = false;
        for (int i = 0; i < FREQUENT_LOTTERY.length; i++) {
            if (kind.equals(FREQUENT_LOTTERY[i])) {
                isFre = true;
                break;
            }
        }
        return isFre;
    }

    public static void drawBall(Context context, PredicateLayout ballsView, String r, String color) {
        TextView tv = new TextView(context);
        String s = r.equals("?") ? "?" : r;
        tv.setText(s);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        int resId = color.equals("red") ? R.drawable.smallredball : R.drawable.smallblueball;
        tv.setBackgroundResource(resId);
        ballsView.addView(tv);
    }

    public static void drawSpace(Context context, ViewGroup ballsView) {
        TextView tv = new TextView(context);
        tv.setText(" ");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setGravity(Gravity.CENTER);
        ballsView.addView(tv);
    }

    public static void drawBalls(Context context, PredicateLayout view, String myBalls) {
        if (myBalls == null)
            return;
        view.removeAllViewsInLayout();
        String[] rbballs = myBalls.split("\\|");
        String[] reds = rbballs[0].split(",");
        for (String r : reds) {
            LotteryUtils.drawBall(context, view, r, "red");
        }
        if (rbballs.length > 1) {
            String[] blues = rbballs[1].split(",");
            for (String r : blues) {
                LotteryUtils.drawBall(context, view, r, "blue");
            }
        }
    }

    public static int getType(String lottery) {
        int type = -1;
        for (int i = 0; i < LotteryUtils.LOTTERY_NAMES.length; i++) {
            if (lottery.equals(LotteryUtils.LOTTERY_NAMES[i])) {
                type = i;
                break;
            }
        }
        return type;
    }

    public static int getTypeIndex(String lottery) {
        int type = -1;
        for (int i = 0; i < LotteryUtils.LOTTERY_ID.length; i++) {
            if (lottery.equals(LotteryUtils.LOTTERY_ID[i])) {
                type = i;
                break;
            }
        }
        return type;
    }

    public static void drawBallLine(Context context, PredicateLayout view) {
        TextView line = new TextView(context);
        line.setBackgroundResource(R.drawable.ball_line);
        view.addView(line, new PredicateLayout.LayoutParams(2, 0));
    }

    // 重载画 球方法
    public static void drawHallBalls(Context context, PredicateLayout view, ArrayList<Ball> ball, String kind) {
        view.removeAllViewsInLayout();
        // String[] redBall = StringUtil.spliteString(redBalls, "\\,");

        // int ballSize = 0;
        // if (danTuo.equals("5"))
        // ballSize = ball.size() + 2;
        // else
        // ballSize = ball.size();

        for (int i = 0; i < ball.size(); i++) {
            LotteryUtils.drawBall(context, view, ball.get(i), kind);
            if (i < ball.size() - 1) {
                if ((ball.get(i).getColor().equals("red") && !ball.get(i + 1).getColor().equals("red")) ||
                    (ball.get(i).getGroupIndex() != ball.get(i + 1).getGroupIndex()))
                    drawBallLine(context, view);
            }
        }
    }

    private static void drawBall(Context context, PredicateLayout ballsView, Ball b, String kind) {
        TextView tv = new TextView(context);
        int resId = 0;
        String s = b.getNumber();
        String color = b.getColor();
        if (color.equals("shengxiao")) {
            s = animals[Integer.valueOf(s) - 1];
        }
        else {
            if (s.length() == 1) {
                if (!s.equals("-"))
                    if (!kind.equals("3d") && !kind.equals("ssl") && !kind.equals("dfljy") &&
                        !kind.equals("qxc") && !kind.equals("sfc") && !kind.equals("r9") &&
                        !kind.equals("pls") && !kind.equals("plw") && !kind.equals("cqssc") &&
                        !kind.equals("jxssc"))
                        s = "0" + s;
            }
        }

        if (s.equals("0#"))
            tv.setText("[胆]");
        else if (s.equals("0$"))
            tv.setText("[拖]");
        else
            tv.setText(s);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setGravity(Gravity.CENTER);
        if (!kind.equals("sfc"))
            tv.setTextColor(context.getResources().getColor(R.color.white));
        if (b.isState()) {
            resId = b.getColor().equals("red") ? R.drawable.light_redball : R.drawable.light_blueball;
        }
        else {
            resId = b.getColor().equals("red") ? R.drawable.smallredball : R.drawable.smallblueball;
        }
        if (s.equals("0#") || s.equals("0$"))
            tv.setTextColor(context.getResources().getColor(R.color.black));
// tv.setBackgroundResource(R.drawabl/e.sfc_bet_history_detail_code_bg_red);
        else
            tv.setBackgroundResource(resId);
        // }
        ballsView.addView(tv);
    }

    // 重载画 球方法
    public static void drawHallBallsRect(Context context, PredicateLayout view, ArrayList<Ball> ball,
                                         String kind) {
        view.removeAllViewsInLayout();
        // String[] redBall = StringUtil.spliteString(redBalls, "\\,");

        // int ballSize = 0;
        // if (danTuo.equals("5"))
        // ballSize = ball.size() + 2;
        // else
        // ballSize = ball.size();

        for (int i = 0; i < ball.size(); i++) {
            LotteryUtils.drawBallRect(context, view, ball.get(i), kind);
// if (i < ball.size() - 1) {
// if ((ball.get(i).getColor().equals("red") && !ball.get(i + 1).getColor().equals("red")) ||
// (ball.get(i).getGroupIndex() != ball.get(i + 1).getGroupIndex()))
// drawBallLine(context, view);
// }
        }
    }

    static LinearLayout linearLayout = null;

    private static void drawBallRect(Context context, PredicateLayout ballsView, Ball b, String kind) {
        if (b.getDevideStart()) {
            linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams linParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linParams.rightMargin = 10;
            linearLayout.setLayoutParams(linParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundResource(R.drawable.jingcai_history_btn_red_normal);
        }

        TextView tv = new TextView(context);
        tv.setPadding(3, 0, 3, 0);
        int resId = 0;
        String s = b.getNumber();
        String color = b.getColor();
        if (color.equals("shengxiao")) {
            s = animals[Integer.valueOf(s) - 1];
        }
        else {
            if (s.length() == 1) {
                if (!s.equals("-"))
                    if (!kind.equals("3d") && !kind.equals("ssl") && !kind.equals("dfljy") &&
                        !kind.equals("qxc") && !kind.equals("sfc") && !kind.equals("r9") &&
                        !kind.equals("pls") && !kind.equals("plw") && !kind.equals("cqssc") &&
                        !kind.equals("jlk3") && !kind.equals("jxssc"))
                        s = "0" + s;
            }
        }

        if (s.equals("0#"))
            tv.setText("[胆]");
        else if (s.equals("0$"))
            tv.setText("[拖]");
        else
            tv.setText(s);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setGravity(Gravity.CENTER);
        if (!kind.equals("sfc"))
            tv.setTextColor(context.getResources().getColor(R.color.white));
        if (b.isState()) {
// resId =
// b.getColor().equals("red") ? R.drawable.jingcai_history_btn_red_award
// : R.drawable.jingcai_history_btn_red_award;
            resId =
                b.getColor().equals("red") ? context.getResources().getColor(R.color.yellow)
                    : context.getResources().getColor(R.color.yellow);
        }
        else {
// resId =
// b.getColor().equals("red") ? R.drawable.jingcai_history_btn_red_normal
// : R.drawable.jingcai_history_btn_red_normal;
            resId =
                b.getColor().equals("red") ? context.getResources().getColor(R.color.white)
                    : context.getResources().getColor(R.color.red);
        }
        if (s.equals("0#") || s.equals("0$"))
            tv.setTextColor(context.getResources().getColor(R.color.black));
// tv.setBackgroundResource(R.drawabl/e.sfc_bet_history_detail_code_bg_red);
        else
// tv.setBackgroundResource(resId);
            tv.setTextColor(resId);
        // }
        try {
            tv.setPadding(5, 5, 5, 5);
            linearLayout.addView(tv);
            if (b.getDevideEnd()) {
                ballsView.addView(linearLayout, new PredicateLayout.LayoutParams(7, 7));
            }
        }
        catch (Exception e) {
        }

    }

    public static void drawBallsLargeNumber(Context context, PredicateLayout view, String myBalls, String kind) {
        view.removeAllViewsInLayout();
        // isDanTuo = myBalls.indexOf("$");
        String[] rbballs = myBalls.split("\\|");
        String[] reds = rbballs[0].split(",");
        if (!rbballs[0].equals("")) {
            if (kind.equals("dlt") && !(rbballs.length > 1)) {
                for (int i = 0; i < reds.length; i++) {
                    LotteryUtils.drawBall(context, view, reds[i], "blue", kind);
                }
            }
            else {
                for (int i = 0; i < reds.length; i++) {
                    LotteryUtils.drawBall(context, view, reds[i], "red", kind);
                }
            }
        }
        if (rbballs.length > 1) {
            drawBallLine(context, view);
            String[] blues = rbballs[1].split(",");
            String color = "blue";
            if (kind.equals("jlk3"))
                color = "red";
            else
                color = "blue";

            for (String r : blues) {
                LotteryUtils.drawBall(context, view, r, color, kind);
            }
        }
    }

    public static void drawBallsLargeNumberRectBg(Context context, PredicateLayout view, String myBalls,
                                                  String kind) {
        view.removeAllViewsInLayout();
        String[] rbballs = myBalls.split("\\|");
        String[] reds = rbballs[0].split(",");
        if (!rbballs[0].equals("")) {
            if (kind.equals("dlt") && !(rbballs.length > 1)) {
                for (int i = 0; i < reds.length; i++) {
                    LotteryUtils.drawRect(context, view, reds[i], "blue", kind);
                }
            }
            else {
                for (int i = 0; i < reds.length; i++) {
                    LotteryUtils.drawRect(context, view, reds[i], "red", kind);
                }
            }
        }
        if (rbballs.length > 1) {
// drawBallLine(context, view);0.1
            String[] blues = rbballs[1].split(",");
            String color = "blue";
            if (kind.equals("jlk3"))
                color = "red";
            else
                color = "blue";

            for (String r : blues) {
                LotteryUtils.drawRect(context, view, r, color, kind);
            }
        }
    }

    public static void drawBallsAnimalsNumber(Context context, PredicateLayout view, String myBalls,
                                              String kind) {
        view.removeAllViewsInLayout();
        // isDanTuo = myBalls.indexOf("$");
        String[] rbballs = myBalls.split("\\|");
        String[] reds = rbballs[0].split(",");

        for (int i = 0; i < reds.length; i++) {
            LotteryUtils.drawBall(context, view, reds[i], "red", kind);
            drawBallLine(context, view);
        }
        if (rbballs.length > 1) {

            String[] blues = rbballs[1].split(",");
            for (String r : blues) {
                LotteryUtils.drawBall(context, view, animals[Integer.valueOf(r) - 1], "blue", kind);
            }
        }
    }

    public static void drawBallsSmallNumber(Context context, PredicateLayout view, String myBalls, String kind) {
        view.removeAllViewsInLayout();
        // isDanTuo = myBalls.indexOf("$");
        String[] rbballs = myBalls.split("\\|");
        String[] reds = rbballs[0].split(",");

        for (int i = 0; i < reds.length; i++) {
            for (int j = 0; j < reds[i].length(); j++) {
                LotteryUtils.drawBall(context, view, String.valueOf(reds[i].charAt(j)), "red", kind);
            }
            if (i < reds.length - 1)
                drawBallLine(context, view);
        }

        if (rbballs.length > 1) {
            String[] blues = rbballs[1].split(",");
            if (kind.equals("dfljy")) {
                for (String r : blues) {
                    LotteryUtils.drawBall(context, view, animals[Integer.valueOf(r) - 1], "blue", kind);
                }
            }
            else {
                drawBallLine(context, view);
                for (int i = 0; i < blues.length; i++) {
                    LotteryUtils.drawBall(context, view, String.valueOf(blues[i]), "red", kind);
                }
            }
        }
    }

    public static void drawBallsNumberSyxw(Context context, PredicateLayout view, String myBalls, String kind) {
        view.removeAllViewsInLayout();
        String[] rbballs = myBalls.split("\\|");
        for (int i = 0; i < rbballs.length; i++) {
            String[] reds = rbballs[i].split(",");
            for (int j = 0; j < reds.length; j++)
                LotteryUtils.drawBall(context, view, String.valueOf(reds[j]), "red", kind);
            drawBallLine(context, view);
        }
    }

    public static void drawBall(Context context, PredicateLayout ballsView, String r, String color,
                                String kind) {
        TextView tv = new TextView(context);
        if (color.equals("red") && r.length() == 1) {
            if (!r.equals("-"))
                if (!kind.equals("3d") && !kind.equals("ssl") && !kind.equals("dfljy") &&
                    !kind.equals("pls") && !kind.equals("plw") && !kind.equals("qxc") &&
                    !kind.equals("cqssc") && !kind.equals("jxssc") && !kind.equals("jlk3"))
                    r = "0" + r;
        }

        if (r.equals("#"))
            r = "0" + r;
        else if (r.equals("$"))
            r = "0" + r;

        initNormalCircle(tv, context, r, color);
        ballsView.addView(tv, new PredicateLayout.LayoutParams(0, 0));
    }

    public static void drawRect(Context context, PredicateLayout ballsView, String r, String color,
                                String kind) {
        TextView tv = new TextView(context);
        tv.setPadding(3, 0, 3, 0);
        if (color.equals("red") && r.length() == 1) {
            if (!r.equals("-"))
                if (!kind.equals("3d") && !kind.equals("ssl") && !kind.equals("dfljy") &&
                    !kind.equals("pls") && !kind.equals("plw") && !kind.equals("qxc") &&
                    !kind.equals("cqssc") && !kind.equals("jlk3") && !kind.equals("jxssc"))
                    r = "0" + r;
        }

        if (r.equals("#"))
            r = "0" + r;
        else if (r.equals("$"))
            r = "0" + r;

        initNormalRect(tv, context, r, color);
        ballsView.addView(tv, new PredicateLayout.LayoutParams(7, 7));
    }

    private static void initNormalCircle(TextView txv, Context context, String r, String color) {
        String s = r.equals("?") ? "?" : r;
        if (s.equals("0#"))
            txv.setText("[胆]");
        else if (s.equals("0$"))
            txv.setText("[拖]");
        else
            txv.setText(s);
        txv.setTypeface(Typeface.DEFAULT_BOLD);
        txv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        txv.setGravity(Gravity.CENTER);
        txv.setTextColor(context.getResources().getColor(R.color.white));
        int resId = color.equals("red") ? R.drawable.smallredball : R.drawable.smallblueball;
        if (s.equals("0#"))
            txv.setTextColor(context.getResources().getColor(R.color.black));
        else if (s.equals("0$"))
            txv.setTextColor(context.getResources().getColor(R.color.black));
        else
            txv.setBackgroundResource(resId);

    }

    private static void initNormalRect(TextView txv, Context context, String r, String color) {
        String s = r.equals("?") ? "?" : r;
        if (s.equals("0#"))
            txv.setText("[胆]");
        else if (s.equals("0$"))
            txv.setText("[拖]");
        else
            txv.setText(s);
        txv.setWidth(43);
        txv.setTypeface(Typeface.DEFAULT_BOLD);
        txv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        txv.setGravity(Gravity.CENTER);
        txv.setTextColor(context.getResources().getColor(R.color.white));
        int resId =
            color.equals("red") ? R.drawable.jingcai_history_btn_red_normal
                : R.drawable.jingcai_history_btn_red_normal;
        if (s.equals("0#"))
            txv.setTextColor(context.getResources().getColor(R.color.black));
        else if (s.equals("0$"))
            txv.setTextColor(context.getResources().getColor(R.color.black));
        else
            txv.setBackgroundResource(resId);

    }

    public static Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            toDate = dateFormat.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    // 获取activity的包名
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    // 充值选择页面需要带移动支付
    // icon
    public static int[] rechargeIconArrayM_cmpay = {R.drawable.cmpay_safe};
    public static int[] rechargeIconArrayM_union = {R.drawable.unionpay_icon, R.drawable.unionpay_icon,
            R.drawable.unionpay_icon, R.drawable.yinhangka};
    public static int[] rechargeIconArrayM_aly = {R.drawable.alipay_icon, R.drawable.icon_snda};
    public static int[] rechargeIconArrayM_other = {R.drawable.phone_card_icon, R.drawable.wap_bank_icon,
            R.drawable.red_packet,};

    public static int[] bankNameM_cmpay = {R.string.mobile_whole_tips_sub};
    public static int[] bankNameM_union = {R.string.unionpay_whole_tips, R.string.eco_credit_card_pay,
            R.string.chinaunionpay, R.string.bank_card_whole_tips};
    public static int[] bankNameM_aly = {R.string.alipay_whole_tips, R.string.snda};
    public static int[] bankNameM_other = {R.string.phone_card_topup, R.string.wap, R.string.caipiaoma};

    public static String[] topUpClassName = {"$", UnionpayDebitCardTopup.class.getSimpleName(),
            UnionPayCreditCardTopup.class.getSimpleName(), ChinaUnionPaycharge.class.getSimpleName(),
            BankCardNetRecharge.class.getSimpleName(), "$", AlipayRecharge.class.getSimpleName(),
            SNDARecharge.class.getSimpleName(), "$", PhoneCardRecharge.class.getSimpleName(),
            WapBankRecharge.class.getSimpleName(), BonusRecharge.class.getSimpleName()};

    public static String[] topUpName = {"$", "储蓄卡语音支付", "信用卡语音支付", "快捷支付（信用卡，储蓄卡）", "银行卡网页支付", "$", "支付宝支付",
            "盛付通", "$", "手机充值卡", "WAP银行", "彩票码"};

    private static Object readKey(Context context, String keyName) {
        try {
            ApplicationInfo appi =
                context.getPackageManager().getApplicationInfo(context.getPackageName(),
                                                               PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);
            return value;
        }
        catch (NameNotFoundException e) {
            return null;
        }
    }

    public static String getManifestMetaDataString(Context context, String keyName) {
        return (String) readKey(context, keyName);
    }

    public static Integer getManifestMetaDataInt(Context context, String keyName) {
        return (Integer) readKey(context, keyName);
    }

    public static String getPid(Context context) {
        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            return "101001";
        }
        else {
            return getManifestMetaDataInt(context, "LOTTERY_PID").toString();
        }
    }

    public static String getKey(Context context) {
        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            return "479gx6B7Yhw18j9f";
        }
        else {
            return getManifestMetaDataString(context, "LOTTERY_KEY");
        }
    }

    public static String getFlurry(Context context) {
        return getManifestMetaDataString(context, "LOTTERY_FLURRY");
    }

    public static String getVersion(Context context) {
        return getManifestMetaDataString(context, "LOTTERY_VERSION");
    }

    public static String simpleVersion(Context context) {
        String version = null;
        ApplicationInfo appInfo = context.getApplicationInfo();
        try {
            PackageInfo pinfo =
                context.getPackageManager().getPackageInfo(appInfo.packageName,
                                                           PackageManager.GET_CONFIGURATIONS);
            version = pinfo.versionName;
        }
        catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    public static String fullVersion(Context context) {
        return getManifestMetaDataString(context, "LOTTERY_VERSION") + simpleVersion(context);
    }

    public static String versionName(Context context, boolean hasVersionNum) {
        String versionName = null;
        String name = context.getResources().getString(R.string.app_name);
        if (hasVersionNum) {
            versionName = name + "v" + simpleVersion(context).substring(0, 3);
        }
        else {
            versionName = name;
        }
        return versionName;
    }

    /**
     * 是否绑定新浪微博
     * 
     * @return
     */
    public static Boolean isBindSina(Context context) {
        LotteryApp accessInfo = null;
        accessInfo = InfoHelper.getAccessInfo(context);
        if (accessInfo != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String getConnectionPhone(Context context) {
        String telephone = ActionUtil.getSharedPreferences(context).getString("last_use_phone", null);
        if (telephone != null) {
            return telephone;
        }
        else {
            return LotteryUtils.TELEPHONE;
        }
    }

    public static String getConnectionQQ(Context context) {
    	return "4006938038";
//        String qq = ActionUtil.getSharedPreferences(context).getString("last_use_qq", null);
//        if (qq != null) {
//            return qq;
//        }
//        else {
//            return LotteryUtils.QQ;
//        }
    }

    /**
     * 根据彩种和开奖数据画球
     * 
     * @param context 上下文情景
     * @param ballsLayout 画球的容器(the layout for drawing balls)
     * @param kind 彩种英文简写id(the id of the lottery)
     */
    public static void drawBallsLayout(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (kind.equals("sfc") || kind.equals("r9")) {
            drawSFC(context, ballsLayout, kind, balls);
        }
        else if (kind.equals("cqssc") || kind.equals("jxssc")) {
            drawSSC(context, ballsLayout, kind, balls);
        }
        else {
            drawNormalBalls(context, ballsLayout, balls);
        }
    }

    private static void drawSSC(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        String[] rbballs = balls.split("\\|");
        String[] reds = rbballs[0].split(",");
        for (String r : reds) {
            drawBall(context, ballsLayout, r, "red");
        }
        if (rbballs.length > 1) {
            String[] blues = rbballs[1].split(",");
            for (String r : blues) {
                drawBall(context, ballsLayout, r, "blue");
            }
        }
        String ssc = StringUtil.getSSCInf(balls);
        if (ssc != null) {
            PredicateLayout.LayoutParams layoutParams = new PredicateLayout.LayoutParams(0, 0);
            TextView number = new TextView(context);
            number.setTypeface(Typeface.DEFAULT_BOLD);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            number.setGravity(Gravity.CENTER);
            number.setTextColor(context.getResources().getColor(R.color.light_purple));
            number.setText(ssc);
            number.setPadding(1, 1, 1, 1);
            ballsLayout.addView(number, layoutParams);
        }
    }

    private static void drawSFC(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context);
        layout.setBackgroundResource(R.drawable.hall_sports_bg);
        layout.initInf(kind, balls);
        ballsLayout.addView(layout, new PredicateLayout.LayoutParams(2, 2));
    }

    private static void drawNormalBalls(Context context, PredicateLayout ballsLayout, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        String[] rbballs = balls.split("\\|");
        String[] reds = rbballs[0].split(",");
        if (rbballs.length < 3 && reds.length > 1) {
            for (String r : reds) {
                drawBall(context, ballsLayout, false, r, "red");
            }
            if (rbballs.length > 1) {
                String[] blues = rbballs[1].split(",");
                for (String r : blues) {
                    drawBall(context, ballsLayout, false, r, "blue");
                }
            }
        }
        else {
            for (String r : rbballs) {
                drawBall(context, ballsLayout, false, r, "red");
            }
        }
    }

    public static void drawBall(Context context, PredicateLayout ballsView, boolean isBigBall, String r,
                                String color) {
        TextView tv = new TextView(context);
        String s = r.equals("?") ? "?" : r;
        tv.setText(s);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        int resId;
        if (isBigBall) {
            resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
        }
        else {
            resId = color.equals("red") ? R.drawable.smallredball : R.drawable.smallblueball;
        }
        tv.setBackgroundResource(resId);
        ballsView.addView(tv);
    }

}
