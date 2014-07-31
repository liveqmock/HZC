package com.haozan.caipiao.activity.bethistory;

// import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.About;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.SharePage;
import com.haozan.caipiao.activity.bet.cqssc.CQSSCBetConfirm;
import com.haozan.caipiao.activity.bet.dcsfgg.DCSFGGActivity;
import com.haozan.caipiao.activity.bet.dfljy.DFLJYBetConfirm;
import com.haozan.caipiao.activity.bet.dlt.DLTBetConfirm;
import com.haozan.caipiao.activity.bet.hnklsf.HNKLSFBetComfirm;
import com.haozan.caipiao.activity.bet.jlks.JLKSBetConfirm;
import com.haozan.caipiao.activity.bet.jxssc.JXSSCBetConfirm;
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
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.adapter.JingCaiBetHistoryDetailAdapter;
import com.haozan.caipiao.adapter.LotteryCodeAdapter;
import com.haozan.caipiao.adapter.LotterySfcR9CodeAdapter;
import com.haozan.caipiao.adapter.ShengFuAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.JingcaiBetHistoryDetailTask;
import com.haozan.caipiao.task.JingcaiBetHistoryDetailTask.OnJingCaiHisDetailTaskChangeListener;
import com.haozan.caipiao.task.ShengfuBetHistoryDetailTask;
import com.haozan.caipiao.task.ShengfuBetHistoryDetailTask.OnShengFuHisDetailTaskChangeListener;
import com.haozan.caipiao.task.SportLotteryInfTask;
import com.haozan.caipiao.task.SportLotteryInfTask.OnTaskChangeListener;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.types.JingCaiBetHistoryDetailItemData;
import com.haozan.caipiao.types.ShengFuBetHistoryDetailItemData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryOperateUtils;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.ExpandAnimation;
import com.haozan.caipiao.util.anamation.ExpandAnimation.ExpanAnimationListener;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener;
import com.haozan.caipiao.weibo.sdk.AuthDialogListener.OnFinishSinaAuthListener;
import com.haozan.caipiao.weibo.sdk.demo.ConstantS;
import com.haozan.caipiao.widget.CustomDialog;
import com.haozan.caipiao.widget.DrawBalls;
import com.haozan.caipiao.widget.PredicateLayout;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;

public class NormalOrderDetail
    extends BasicActivity
    implements OnClickListener, OnItemClickListener, OnTaskChangeListener,
    OnJingCaiHisDetailTaskChangeListener, AnimationListener, OnShengFuHisDetailTaskChangeListener, OnFinishSinaAuthListener, IWeiboHandler.Response {
    private static final int SHARE_TYPE_SINA = 1;
    private static final int SHARE_TYPE_OTHERS = 2;
    private static final int SHARE_TYPE_WEIXIN = 3;
    private static final int SHARE_TYPE_WEIXINFANS = 4;
    private static final int SHARE_TYPE_QQWEIBO = 5;
    private static final int THUMB_SIZE = 150;
    private static final String[] BUNCHNAME = {"单关", "2串1", "3串1", "4串1", "5串1", "6串1", "7串1", "8串1"};
    private static final int[] SFCCODE = {100, 201, 301, 401, 501, 601, 701, 801};
    private String[][] teams = new String[15][2];
    private int shareWay = 1;
    private String PicPath = "/sdcard/MyScreenshot.png";
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    // 最高理论奖金
    private float highAward = -1;
    private Button shareImg;
    private PopupWindow shareWayPopupWindow;

    private static final String RESPONSE = "response_data";
    private Bitmap bm;

    private int buyMode;
    private int lotteryType;
    private StringBuilder number;
    private StringBuilder spHignArray;
    private String betDouble = null;
    private String kind;
    private String term;
    private String codes;
    private String opens;
    private String money;
    private String winmoney;
    private String orderId;
    private String shareText;
    private String winStatus;
    private String guoGuan;
    private String splitNum;
    private String orderTime;

    private TextView kindTV;
    private TextView termTV;
    private TextView moneyTV;
    private TextView winMoney;
    private TextView orderIdTV;
    private TextView orderDateTV;
    private TextView message;
    private TextView winStatu;
    private TextView awardMoneyTv;
    private ListView lotteryNumBet;
    private TextView userBetLocation;
    private TextView betLocation;
    private TextView betLocationTv;
    private TextView openCodeTips;
    private TextView loteryBetDoublenum;
    private TextView jingCaiGuoGuan;
    private TextView jingCaiMethod;
    private TextView jingcaiMessage;

    private Button betAgain;
    private Button betShare;

    private View headerView;
    private View footerView;

    private LayoutInflater factory;
    private LotteryCodeAdapter lotteryCodeAdapter;
    
    private ProgressBar progress;
    private PredicateLayout ballsLayout2;

    private LinearLayout tvOpenCode;
    private LinearLayout moneyLy;
    private LinearLayout layout;
    private LinearLayout headViewInfoContainer01;
    private LinearLayout headViewInfoContainer02;
    private LinearLayout headViewInfoContainer04;
    private LinearLayout awardMoneyLinear;
    private LinearLayout winStatusLinear;

    private LinearLayout betLocationLinear;
    // sfc r9
    private ArrayList<AthleticsListItemData> matchDataList;
    private LotterySfcR9CodeAdapter lotterySfcR9CodeAdapter;
    // jingcai
    private ArrayList<JingCaiBetHistoryDetailItemData> jingCaiBetHistoryDetailDataList;
    // 胜负过关
    private ArrayList<ShengFuBetHistoryDetailItemData> shengFuBetHistoryDetailDataList;    
    private JingCaiBetHistoryDetailAdapter jingCaiBetHistoryDetailAdapter;
    private Map<String, String> mapJCBetHistoryDetail;
    private Map<String, String> mapSFBetHistoryDetail;

    private ImageView imageButton;

    // 分享
    private TextView shareDirectly;
    private TextView shareWeixin;
    private TextView shareWeixinFriends;
    private TextView shareQQWeibo;
    private TextView shareOther;

    private CustomDialog dlgCheckBindSina;

    // lottery headerview icon and line
    private Map<String, Integer> mapLotteryIcon;
    private ImageView lotteryIcon;
    private ImageView lotteryVerticalLine;
    private ImageView containerLeftLine;
    private ImageView betMoneyLeftLine;
    private ImageView openBallBottomLine;

    // 投注详情按钮
    private RelativeLayout rechargeBottomBtLaout;
    // show faile
    private View showFailPage;

    // 新浪微博分享的开放接口
    IWeiboAPI weiboAPI;
    private SsoHandler mSsoHandler;
    Weibo mWeibo;

    private int screenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.bet_history_order_details_new);
        setupViews();
        init();
    }

    private void initData() {
        mWeibo =
            Weibo.getInstance(LotteryConfig.getSinaKey(NormalOrderDetail.this), ConstantS.REDIRECT_URL,
                              ConstantS.SCOPE);
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("lottery_id");
        term = bundle.getString("term");
        orderId = bundle.getString("order_id");

        weiboAPI = WeiboSDK.createWeiboAPI(this, LotteryConfig.getSinaKey(NormalOrderDetail.this));
        weiboAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
            @Override
            public void onCancel() {
                Toast.makeText(NormalOrderDetail.this, "取消下载", Toast.LENGTH_SHORT).show();
            }
        });
        weiboAPI.registerApp();
    }

    private void init() {
        // 获取屏幕分辨率
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        screenWidth = display.getWidth();

        View toolbar = headerView.findViewById(R.id.extral_inf_container);
        mapLotteryIcon = new HashMap<String, Integer>();
        initLotteryIcon();
        ((LinearLayout.LayoutParams) toolbar.getLayoutParams()).bottomMargin = -80;
// intiHashMap();
        // 设置分享方式
        orgShareWay();
        // 显示彩种名称
        kindTV.setText(LotteryUtils.getLotteryName(kind));
        lotteryIcon.setImageResource(mapLotteryIcon.get(kind));
        // 在没有从服务器获取数据之前禁掉列表和”再投“按钮
        betAgain.setEnabled(false);
        betShare.setEnabled(false);
        lotteryNumBet.setEnabled(false);
        // 变量初始化
        if (kind.equals("jczq") || kind.equals("jclq")) {
            // 初始化竞彩数据变量
            spHignArray = new StringBuilder();
            jingCaiBetHistoryDetailDataList = new ArrayList<JingCaiBetHistoryDetailItemData>();
            mapJCBetHistoryDetail = new HashMap<String, String>();
            // 初始化相关控件
            tvOpenCode.setVisibility(View.GONE);
            headViewInfoContainer01.setVisibility(View.VISIBLE);
            headViewInfoContainer02.setVisibility(View.VISIBLE);
            containerLeftLine.setVisibility(View.VISIBLE);
            betAgain.setText("订单详细");
        }
        if (kind.equals("dcsfgg")) {
        	// 初始化胜负过关数据变量
        	spHignArray = new StringBuilder();
        	shengFuBetHistoryDetailDataList = new ArrayList<ShengFuBetHistoryDetailItemData>();
        	mapSFBetHistoryDetail = new HashMap<String, String>();
        	tvOpenCode.setVisibility(View.GONE);
        	headViewInfoContainer01.setVisibility(View.GONE);
            headViewInfoContainer02.setVisibility(View.GONE);
            containerLeftLine.setVisibility(View.GONE);
        }
        else {
            if (kind.equals("sfc") || kind.equals("r9"))
                matchDataList = new ArrayList<AthleticsListItemData>();

            headViewInfoContainer01.setVisibility(View.GONE);
            headViewInfoContainer02.setVisibility(View.GONE);
            containerLeftLine.setVisibility(View.GONE);
        }
        initSharePopUpWindow();
        excuteExtraTask();
    }

    private void initLotteryIcon() {
        mapLotteryIcon.put("dlt", R.drawable.hall_cjdlt_icon_normal);
        mapLotteryIcon.put("cqssc", R.drawable.hall_cqssc_icon_normal);
        mapLotteryIcon.put("22x5", R.drawable.hall_eexw_icon_normal);
        mapLotteryIcon.put("hnklsf", R.drawable.hall_hnklsf_normal);
        mapLotteryIcon.put("jclq", R.drawable.hall_jclq_icon_normal);
        mapLotteryIcon.put("jczq", R.drawable.hall_jczq_icon_normal);
        mapLotteryIcon.put("jlk3", R.drawable.hall_jlks_icon_normal);
        mapLotteryIcon.put("jx11x5", R.drawable.hall_jx11x5_icon_normal);
        mapLotteryIcon.put("jxssc", R.drawable.hall_jxssc_icon_normal);
        mapLotteryIcon.put("klsf", R.drawable.hall_klsf_icon_normal);
        mapLotteryIcon.put("pls", R.drawable.hall_pls_icon_normal);
        mapLotteryIcon.put("plw", R.drawable.hall_plw_icon_normal);
        mapLotteryIcon.put("qlc", R.drawable.hall_qlc_icon_normal);
        mapLotteryIcon.put("qxc", R.drawable.hall_qxc_icon_normal);
        mapLotteryIcon.put("r9", R.drawable.hall_rxj_icon_normal);
        mapLotteryIcon.put("3d", R.drawable.hall_sd_icon_normal);
        mapLotteryIcon.put("sfc", R.drawable.hall_sfc_icon_normal);
        mapLotteryIcon.put("ssq", R.drawable.hall_ssq_icon_normal);
        mapLotteryIcon.put("swxw", R.drawable.hall_swxw_icon_normal);
        mapLotteryIcon.put("dfljy", R.drawable.hall_dfljy_icon_normal);
        mapLotteryIcon.put("dcsfgg", R.drawable.hall_dcsfgg_icon_normal);
    }

    private void setWinStatus() {
        if (buyMode == 9) {
            winStatu.setText("处理中");
            moneyLy.setVisibility(View.GONE);
            lotteryVerticalLine.setVisibility(View.GONE);
        }
        else if (buyMode == 0) {
            if (winStatus.equals("1")) {
                // 中奖状态
                winStatu.setText("中奖");
                winStatusLinear.setVisibility(View.GONE);
                lotteryVerticalLine.setVisibility(View.GONE);

                // 奖金
                moneyLy.setVisibility(View.VISIBLE);
                betMoneyLeftLine.setVisibility(View.VISIBLE);
                winMoney.setText(winmoney + "元");
            }
            else if (winStatus.equals("0")) {
                winStatu.setText("未中奖");
                moneyLy.setVisibility(View.GONE);
                betMoneyLeftLine.setVisibility(View.GONE);
            }
            else if (winStatus.equals("2")) {
                winStatu.setText("等待开奖");
                moneyLy.setVisibility(View.GONE);
                betMoneyLeftLine.setVisibility(View.GONE);
            }
            else {
                winStatu.setText("未知");
                moneyLy.setVisibility(View.GONE);
                betMoneyLeftLine.setVisibility(View.GONE);
            }
        }
        else if (buyMode == 1 || buyMode == 2) {
            winStatu.setText("出票失败");
            moneyLy.setVisibility(View.GONE);
            betMoneyLeftLine.setVisibility(View.GONE);
        }

        // 设置中奖金额
// if (winmoney != null)
// if (Double.parseDouble(winmoney) == 0) {//如果奖金为0，就不显示控件。
// moneyLy.setVisibility(View.GONE);
// }
// else {
// moneyLy.setVisibility(View.VISIBLE);
// winMoney.setText(winmoney + "元");
// }
    }

    private void calculateAward() {
        // 过关方式数组，比如201&301
        highAward = 0;

        for (int i = guoGuan.split("\\;").length - 1; i >= 0; i--) {
            if (lotteryType == 1 || lotteryType == 4 || lotteryType == 5 || lotteryType == 6) {
                // 假设是某种过关方式如第一种方式2串1，则需要将最大赔率和过关方式传入计算最高奖金
                highAward +=
                    LotteryOperateUtils.getBiggestAward(Integer.valueOf(guoGuan.split("\\;")[i]),
                                                        spHignArray.toString());
            }
            else if (lotteryType == 2) {
                highAward +=
                    LotteryOperateUtils.getBiggestAward(Integer.valueOf(guoGuan.split("\\;")[i]),
                                                        spHignArray.toString());
            }
            else if (lotteryType == 3 || lotteryType == 7) {
                highAward +=
                    LotteryOperateUtils.getBiggestAward(Integer.valueOf(guoGuan.split("\\;")[i]),
                                                        spHignArray.toString());
            }
        }
        // 最高理论奖金显示
        DecimalFormat format = new DecimalFormat("#.00");
        if (highAward != 0.0)
            awardMoneyTv.setText(Html.fromHtml(format.format(highAward * Integer.valueOf(betDouble)) +
                "元(仅供参考)"));
        else
            awardMoneyLinear.setVisibility(View.GONE);
    }

    private void getTheHightRate(String term) {
        double winRate = 0;
        double drawRate = 0;
        double lostRate = 0;
        String[] rateArray;
        int iq = jingCaiBetHistoryDetailDataList.size();
        double rate = 0;
        for (int i = 0; i < jingCaiBetHistoryDetailDataList.size(); i++) {
            winRate = 0;
            drawRate = 0;
            lostRate = 0;
            if (term.equals("71") || term.equals("75")) {// 竞彩足球 75：胜平负 71：让球胜平负
                if (jingCaiBetHistoryDetailDataList.get(i).getBetResultWin() != null) {
                    if (!jingCaiBetHistoryDetailDataList.get(i).getBetResultWin().trim().equals("胜"))
                        winRate =
                            Double.valueOf(jingCaiBetHistoryDetailDataList.get(i).getBetResultWin().replaceAll("胜",
                                                                                                               "").trim());
                }
                if (jingCaiBetHistoryDetailDataList.get(i).getBetResultEqual() != null) {
                    if (!jingCaiBetHistoryDetailDataList.get(i).getBetResultEqual().trim().equals("平"))
                        drawRate =
                            Double.valueOf(jingCaiBetHistoryDetailDataList.get(i).getBetResultEqual().replaceAll("平",
                                                                                                                 "").trim());
                }
                if (jingCaiBetHistoryDetailDataList.get(i).getBetResultLost() != null) {
                    if (!jingCaiBetHistoryDetailDataList.get(i).getBetResultLost().trim().equals("负"))
                        lostRate =
                            Double.valueOf(jingCaiBetHistoryDetailDataList.get(i).getBetResultLost().replaceAll("负",
                                                                                                                "").trim());
                }
                rate = winRate;
                if (rate < drawRate)
                    rate = drawRate;
                if (rate < lostRate)
                    rate = lostRate;
            }
            else {
                if (jingCaiBetHistoryDetailDataList.get(i).getBetGoal().indexOf("|") != -1) {
                    rateArray =
                        jingCaiBetHistoryDetailDataList.get(i).getBetGoal().split("\\|")[1].split("\\,");
                    rate = Double.valueOf(rateArray[0]);
                    for (int j = 1; j < rateArray.length; j++) {
                        if (rate < Double.valueOf(rateArray[j]))
                            rate = Double.valueOf(rateArray[j]);
                    }
                }
            }

            spHignArray.append(rate + "|");
        }
        spHignArray.delete(spHignArray.length() - 1, spHignArray.length());
    }

    private String getJingCaiBetWay(String term) {
        if (term.equals("75")) {// 竞彩足球 75：胜平负
            lotteryType = 1;
            return "胜平负";
        }
        if (term.equals("72")) {// 竞彩足球 72：总进球数
            lotteryType = 2;
            return "总进球";
        }
        if (term.equals("73")) {// 竞彩足球 73：比分
            lotteryType = 7;
            return "比分";
        }
        if (term.equals("74")) {// 竞彩足球 74：半全场
            lotteryType = 3;
            return "半全场";
        }
        if (term.equals("71")) {// 竞彩足球 71：让球胜平负
            lotteryType = 1;
            return "让球胜平负";
        }
        if (term.equals("81")) {// 竞彩篮球 81：让分胜负
            lotteryType = 4;
            return "让分胜负";
        }
        if (term.equals("82")) {// 竞彩篮球 82：胜负
            lotteryType = 5;
            return "胜负";
        }
        if (term.equals("84")) {// 竞彩篮球 84：大小分
            lotteryType = 6;
            return "大小分";
        }
        return null;
    }

    // 显示开奖号码
    private void drawOpenBall() {
        if (!opens.equals("null")) {
            if (kind.equals("dfljy"))
                LotteryUtils.drawBallsAnimalsNumber(NormalOrderDetail.this, ballsLayout2, opens, kind);
            else if (kind.equals("sfc") || kind.equals("r9")) {
                DrawBalls drawBalls = new DrawBalls();
                drawBalls.drawSFCHistoryBallOpen(NormalOrderDetail.this, ballsLayout2, kind, opens);
            }
            else if (kind.equals("cqssc"))
                LotteryUtils.drawBallsSmallNumber(NormalOrderDetail.this, ballsLayout2, opens, kind);
            else
                LotteryUtils.drawBallsLargeNumber(NormalOrderDetail.this, ballsLayout2, opens, kind);
        }
        else {
            ballsLayout2.setVisibility(View.GONE);
            openCodeTips.setVisibility(View.VISIBLE);
            openCodeTips.setText("--");
        }

    }

    // 解析投注号码（竞彩）
    private void spiltBetCodeJinCai() {
        String[] ballNumbers = null;
        number = new StringBuilder();
        try {
            ballNumbers = StringUtil.spliteString(codes, ":");
            betDouble = ballNumbers[3];
            number.append(getJingCaiShareText(ballNumbers[0]));
            betDouble = ballNumbers[3];
        }
        catch (Exception e) {
            System.out.print("投注详细投注号码解析出错！");
        }
    }

    // 解析投注号码（数字彩）dcsfgg
    private void spiltBetCode() {
        String[] ballNumbers = null;
        //TODO 1$002=3|1$026=3|1$027=3:2:301:1  LUGQ
        String[] ballCodeArray = StringUtil.spliteString(codes, ";");
        number = new StringBuilder();
        try {
            if (ballCodeArray.length == 1) {
                ballNumbers = StringUtil.spliteString(ballCodeArray[0], ":");
                betDouble = ballNumbers[3];
                number.append(ballNumbers[0]);
            }
            else {
                ballNumbers = StringUtil.spliteString(ballCodeArray[0], ":");
                betDouble = ballNumbers[3];
                number.append(ballNumbers[0]);
                number.append("...");
            }
        }
        catch (Exception e) {
            System.out.print("投注详细投注号码解析出错！");
        }
    }

    private static final String[] BUNCHNAMEDATE = {"一", "二", "三", "四", "五", "六", "日"};
    private static final int[] SFCCODEDATE = {1, 2, 3, 4, 5, 6, 7};

    private String getJingCaiShareText(String text) {
        String[] shareTextArray = text.split("\\|");
        StringBuilder shareTextStringBuilder = new StringBuilder();
        for (int i = 0; i < shareTextArray.length; i++) {
            shareTextStringBuilder.append(getJingCaiShareTextTermAndCode(shareTextArray[i]));
            shareTextStringBuilder.append("|");
        }
        shareTextStringBuilder.delete(shareTextStringBuilder.length() - 1, shareTextStringBuilder.length());
        return shareTextStringBuilder.toString();
    }

    private String getJingCaiShareTextTermAndCode(String termAndCode) {
        String[] shareTermAndCode = termAndCode.split("\\=");
        String[] shareDayAndNum = shareTermAndCode[0].split("\\$");
        return "周" + getBetTypeDate(shareDayAndNum[0]) + " " + shareDayAndNum[1] + "=" + shareTermAndCode[1];
    }

    private String getBetTypeDate(String guoGuan) {
        int j = 0;
        for (int i = 0; i < SFCCODEDATE.length; i++)
            if (SFCCODEDATE[i] == Integer.valueOf(guoGuan))
                j = i;
        return BUNCHNAMEDATE[j];
    }

    private void excuteExtraTask() {
        if (HttpConnectUtil.isNetworkAvailable(NormalOrderDetail.this)) {
            if (!kind.equals("jczq") && !kind.equals("jclq")) {
                OrderDetailTask detailTask = new OrderDetailTask();
                detailTask.execute();
                if(kind.equals("dcsfgg")) {
                	progress.setVisibility(View.VISIBLE);
                	ShengfuBetHistoryDetailTask shengfuBetHistoryDetailTask = 
                			new ShengfuBetHistoryDetailTask(NormalOrderDetail.this, shengFuBetHistoryDetailDataList, 
                					kind, orderId, mapSFBetHistoryDetail);
                	shengfuBetHistoryDetailTask.execute();
                }
            }
            
            /*if (kind.equals("dcsfgg")) {//胜负过关
            	progress.setVisibility(View.VISIBLE);
            	ShengfuBetHistoryDetailTask shengfuBetHistoryDetailTask = 
            			new ShengfuBetHistoryDetailTask(NormalOrderDetail.this, shengFuBetHistoryDetailDataList, 
            					kind, orderId, mapSFBetHistoryDetail);
            	shengfuBetHistoryDetailTask.execute();
            	shengfuBetHistoryDetailTask.setOnShengFuHisDeTaskChangeListener(this);
            }*/
            
            else {
                progress.setVisibility(View.VISIBLE);
                JingcaiBetHistoryDetailTask jingcaiBetHistoryDetailTask =
                    new JingcaiBetHistoryDetailTask(NormalOrderDetail.this, jingCaiBetHistoryDetailDataList,
                                                    kind, orderId, mapJCBetHistoryDetail);//orderI
                jingcaiBetHistoryDetailTask.execute();
                jingcaiBetHistoryDetailTask.setOnJinCaiHisDeTaskChangeListener(this);
            }
        }
        else {
            message.setVisibility(View.VISIBLE);
            String inf = getResources().getString(R.string.network_not_avaliable);
            message.setText(inf);
            showFailPage.setVisibility(View.VISIBLE);
        }
    }

    private void initShareView() {
        shareText = getShareText();
    }

    public String getShareText() {
        String versionName = null;
        String shareText = null;
        try {
            InputStream in = getResources().getAssets().open("version.properties");
            Properties prop = new Properties();
            prop.load(in);
            versionName = prop.getProperty("name");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (winmoney != null)
            if (Double.parseDouble(winmoney) == 0) {
                shareText =
                    "我投注了" + LotteryUtils.getLotteryName(kind) + ",投注号码:" + number + "\n" + "下载#" +
                        getString(R.string.app_name) + "#你也试试运气,一起发财吧 " + getString(R.string.download_url);
            }
            else {
                shareText =
                    "#" + versionName + "#, 我用  @" + getString(R.string.app_name) + " 中奖啦！" +
                        LotteryUtils.getLotteryName(kind) + "第" + term + "期中了" + winmoney +
                        "元，把幸运分享给大家，你们也来试试用手机买彩票，真的非常方便，时刻准备迎接好运！ " + getString(R.string.download_url);
            }
        return shareText;
    }

    private String d;
    private String m;
    private String orgNum;
    private String star;
    private String luckThings;
    private String location;

    private void setupViews() {
        // view
        factory = LayoutInflater.from(this);
        headerView = factory.inflate(R.layout.bet_detail_header_view, null);
        footerView = factory.inflate(R.layout.bet_detail_footer_view, null);
        // headview widget
        winStatu = (TextView) headerView.findViewById(R.id.bet_win_status);
        layout = (LinearLayout) headerView.findViewById(R.id.details_layout);
        ballsLayout2 = (PredicateLayout) headerView.findViewById(R.id.balls_open);
        ballsLayout2.setQuick(true);
        moneyLy = (LinearLayout) headerView.findViewById(R.id.moneyLy);
        kindTV = (TextView) headerView.findViewById(R.id.lottery_kind);
        termTV = (TextView) headerView.findViewById(R.id.lottery_term);
        winMoney = (TextView) headerView.findViewById(R.id.bet_win_money);
        moneyTV = (TextView) headerView.findViewById(R.id.lottery_money);
        orderIdTV = (TextView) headerView.findViewById(R.id.lottery_order_id);
        orderDateTV = (TextView) headerView.findViewById(R.id.lottery_order_date);
        betLocation = (TextView) headerView.findViewById(R.id.bet_location);
        betLocationLinear = (LinearLayout) headerView.findViewById(R.id.bet_location_linear);
        betLocationTv = (TextView) headerView.findViewById(R.id.bet_location_tv);
        openCodeTips = (TextView) headerView.findViewById(R.id.lottery_open_code_tips);
        loteryBetDoublenum = (TextView) headerView.findViewById(R.id.bet_double_num);
        awardMoneyLinear = (LinearLayout) headerView.findViewById(R.id.award_money_linear);
        awardMoneyTv = (TextView) headerView.findViewById(R.id.award_money_tv);
        userBetLocation = (TextView) headerView.findViewById(R.id.user_bet_location);
        jingCaiGuoGuan = (TextView) headerView.findViewById(R.id.user_bet_guo_guan_tv);
        jingCaiMethod = (TextView) headerView.findViewById(R.id.user_bet_method_tv);
        tvOpenCode = (LinearLayout) headerView.findViewById(R.id.open_ball_container);
        headViewInfoContainer01 = (LinearLayout) headerView.findViewById(R.id.inf_container_01);
        headViewInfoContainer02 = (LinearLayout) headerView.findViewById(R.id.inf_container_02);
        headViewInfoContainer04 = (LinearLayout) headerView.findViewById(R.id.inf_container_04);
        imageButton = (ImageView) headerView.findViewById(R.id.rechargr_more_bank_inf);
        rechargeBottomBtLaout = (RelativeLayout) headerView.findViewById(R.id.recharge_bottom_bt_laout);
        imageButton.setOnClickListener(this);
        rechargeBottomBtLaout.setOnClickListener(this);
        winStatusLinear = (LinearLayout) headerView.findViewById(R.id.win_status_linear);
        lotteryIcon = (ImageView) headerView.findViewById(R.id.lottery_icon);
        lotteryVerticalLine = (ImageView) headerView.findViewById(R.id.moneyLy_image_view);
        containerLeftLine = (ImageView) headerView.findViewById(R.id.inf_container_02_left_line);
        betMoneyLeftLine = (ImageView) headerView.findViewById(R.id.bet_moneyLy_image_view);
        openBallBottomLine = (ImageView) headerView.findViewById(R.id.balls_open_bottom_line);
        // footview widget
        shareImg = (Button) this.findViewById(R.id.img_show_share_way);
        shareImg.setOnClickListener(this);
        betAgain = (Button) this.findViewById(R.id.bet_again);
        betAgain.setOnClickListener(this);
        betShare = (Button) this.findViewById(R.id.bet_share);
        betShare.setOnClickListener(this);
        jingcaiMessage = (TextView) footerView.findViewById(R.id.jingcai_message);
        message = (TextView) this.findViewById(R.id.message);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        // middle widget
        lotteryNumBet = (ListView) this.findViewById(R.id.lottery_detail_num_bet);
        lotteryNumBet.setOnItemClickListener(this);
        lotteryNumBet.addHeaderView(headerView, null, false);
        lotteryNumBet.addFooterView(footerView, null, false);
        showFailPage = findViewById(R.id.show_fail_page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet order details");
        map.put("more_inf", "normal");
        FlurryAgent.onEvent("open bet order details", map);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bet_share) {
            Map<String, String> map = new HashMap<String, String>();
            String eventName;
            map.put("inf", "username [" + appState.getUsername() + "]: v2 bet history share");
            String way;
            if (shareWay == SHARE_TYPE_SINA) {
                way = "xinlang";
            }
            else {
                way = "other";
            }
            map.put("more_inf", "username [" + appState.getUsername() + "]: v2 bet history share by " + way);
            eventName = "v2 bet history share";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMob = "bet_history_share";
            MobclickAgent.onEvent(this, eventNameMob, way);
            if (shareWay == SHARE_TYPE_SINA) {
                submitAnalyseDataShare("新浪微博");
                String uid = preferences.getString("sinaweibo_userid", null);
                if (null != uid && LotteryUtils.isBindSina(this)) {
// if (winmoney.equals("0.0")) {
                    doOnceCompleteAuth();
                }
                else {
                    if (dlgCheckBindSina == null) {
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                        customBuilder.setTitle("绑定提示").setMessage("还未绑定新浪微博，是否马上绑定，分享自己的运气？").setNegativeButton("下次再说",
                                                                                                                new DialogInterface.OnClickListener() {
                                                                                                                    public void onClick(DialogInterface dialog,
                                                                                                                                        int which) {
                                                                                                                        dlgCheckBindSina.dismiss();
                                                                                                                        shareBySina();
                                                                                                                    }
                                                                                                                }).setPositiveButton("绑定",
                                                                                                                                     new DialogInterface.OnClickListener() {
                                                                                                                                         public void onClick(DialogInterface dialog,
                                                                                                                                                             int which) {
                                                                                                                                             dlgCheckBindSina.dismiss();
// Intent intent =
// new Intent();
// intent.setClass(NormalOrderDetail.this,
// AuthorizeActivity.class);
// startActivityForResult(intent,
// 1);
                                                                                                                                             AuthDialogListener lis =
                                                                                                                                                 new AuthDialogListener(
                                                                                                                                                                        NormalOrderDetail.this,
                                                                                                                                                                        weiboAPI);
                                                                                                                                             lis.setOnFinishSinaAuthListener(NormalOrderDetail.this);
                                                                                                                                             mSsoHandler =
                                                                                                                                                 new SsoHandler(
                                                                                                                                                                NormalOrderDetail.this,
                                                                                                                                                                mWeibo);
                                                                                                                                             mSsoHandler.authorize(lis,
                                                                                                                                                                   null);
                                                                                                                                         }
                                                                                                                                     });
                        dlgCheckBindSina = customBuilder.create();
                        dlgCheckBindSina.show();
                    }
                    else {
                        dlgCheckBindSina.show();
                    }
                }
            }
            else if (shareWay == SHARE_TYPE_OTHERS) {
                submitAnalyseDataShare("系统分享");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "投注分享");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, "投注分享"));
            }
            else if (shareWay == SHARE_TYPE_WEIXIN || shareWay == SHARE_TYPE_WEIXINFANS) {
                // 通过WXAPIFactory工厂，获取IWXAPI的实例
                api = WXAPIFactory.createWXAPI(this, LotteryConfig.getWeixinAPPID(this), false);
                // 将该app注册到微信
                api.registerApp(LotteryConfig.getWeixinAPPID(this));

                if (api.isWXAppInstalled()) {
                    if (shareWay == SHARE_TYPE_WEIXIN) {// 分享给微信好友
                        submitAnalyseDataShare("微信好友");
                    }
                    else if (shareWay == SHARE_TYPE_WEIXINFANS) {// 分享到微信朋友圈
                        submitAnalyseDataShare("微信朋友圈");
                    }
                    cutScreen();
                    String path = PicPath;
                    File file = new File(path);
                    if (!file.exists()) {
                        ViewUtil.showTipsToast(NormalOrderDetail.this, "未检测到内存卡，截图失败");
                    }
                    else {
                        WXImageObject imgObj = new WXImageObject();
                        imgObj.setImagePath(path);
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = imgObj;
                        Bitmap bmp = BitmapFactory.decodeFile(path);
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                        bmp.recycle();
                        msg.thumbData = ViewUtil.bmpToByteArray(thumbBmp, true);
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("img");
                        req.message = msg; //
                        req.scene =
                            shareWay == SHARE_TYPE_WEIXINFANS ? SendMessageToWX.Req.WXSceneTimeline
                                : SendMessageToWX.Req.WXSceneSession;
                        api.sendReq(req);
                    }

                }
                else {
                    ViewUtil.showTipsToast(NormalOrderDetail.this, About.UNINSTALL_WXAPP);
                }
            }
            else if (shareWay == SHARE_TYPE_QQWEIBO) {
                if (Double.parseDouble(winmoney) == 0) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("shareText", shareText);
                    bundle.putInt("from", -1);
                    bundle.putInt("share_type", SHARE_TYPE_QQWEIBO);
                    bundle.putString("commer_class", "NormalOrderDetail");
                    intent.putExtras(bundle);
                    intent.setClass(NormalOrderDetail.this, SharePage.class);
                    startActivityForResult(intent, 0);
                }
                else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("shareText", shareText);
                    bundle.putInt("from", 1);
                    bundle.putInt("share_type", SHARE_TYPE_QQWEIBO);
                    intent.putExtras(bundle);
                    intent.setClass(NormalOrderDetail.this, SharePage.class);
                    cutScreen();

                    NormalOrderDetail.this.startActivity(intent);
                }
            }
        }
        else if (v.getId() == R.id.bet_again) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("inf", "username [" + appState.getUsername() + "]: click bet history details bet again");
            String eventName = "v2 click bet history details bet again";
            FlurryAgent.onEvent(eventName, map);
            besttoneEventCommint(eventName);
            String eventNameMob = "bet_history_betagain";
            MobclickAgent.onEvent(this, eventNameMob);
            if (!kind.equals("jczq") && !kind.equals("jclq")) {
                if (getSelectedSize() > 0)
                    betAgain();
                else
                    ViewUtil.showTipsToast(this, "请在号码前面打上钩!");
            }

            if (kind.equals("jczq") && !kind.equals("jclq")) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("bet_kind", kind);
                bundle.putString("order_id", orderId);
                bundle.putString("split_num", splitNum);
                bundle.putString("lottery_way_type", mapJCBetHistoryDetail.get("term"));
                intent.putExtras(bundle);
                intent.setClass(NormalOrderDetail.this, JingCaiOrderDetail.class);
                startActivity(intent);
            }
        }
        else if (v.getId() == R.id.img_show_share_way) {
            showShareWayPopupViews();
        }
        else if (v.getId() == R.id.rechargr_more_bank_inf) {
            View toolbar = headerView.findViewById(R.id.extral_inf_container);
            // Creating the expand animation for the item
            expandAni = new ExpandAnimation(toolbar, 300);
            expandAni.setExpanListener(new ExpanAnimationListener() {

                @Override
                public void listItemExpan(boolean mWasEndedAlready) {
                    if (mWasEndedAlready)
                        imageButton.setImageResource(R.drawable.icon_up_new);
                    else
                        imageButton.setImageResource(R.drawable.icon_down_new);
                }
            });
            expandAni.setAnimationListener(this);

            toolbar.startAnimation(expandAni);
        }
        else if (v.getId() == R.id.recharge_bottom_bt_laout) {
            View toolbar = headerView.findViewById(R.id.extral_inf_container);
            // Creating the expand animation for the item
            expandAni = new ExpandAnimation(toolbar, 300);
            expandAni.setExpanListener(new ExpanAnimationListener() {

                @Override
                public void listItemExpan(boolean mWasEndedAlready) {
                    if (mWasEndedAlready)
                        imageButton.setImageResource(R.drawable.icon_down_new);
                    else
                        imageButton.setImageResource(R.drawable.icon_up_new);
                }
            });
            expandAni.setAnimationListener(this);
            toolbar.startAnimation(expandAni);
        }
    }

    public void cutScreen() {
        // 截图
        bm = shot(NormalOrderDetail.this);
        // 存入手机存储空间中
        try {
            BasicWeibo.saveMyBitmap("MyScreenshot", bm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareBySina() {
        if (weiboAPI.isWeiboAppSupportAPI()) {
            databaseData.putString("place_sina_share", "normalorderdetail");
            databaseData.commit();
            // 截图并保存在本地
            cutScreen();
            int supportApi = weiboAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351) {
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.textObject = getTextObj();
                if (winmoney != null) {
                    if (Double.parseDouble(winmoney) != 0) {
                        weiboMessage.imageObject = getImageObj();
                    }
                }
                // 初始化从三方到微博的消息请求
                SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
                req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
                req.multiMessage = weiboMessage;
                // 发送请求消息到微博
                weiboAPI.sendRequest(this, req);
            }
            else {
                WeiboMessage weiboMessage = new WeiboMessage();
                weiboMessage.mediaObject = getTextObj();
                // 初始化从三方到微博的消息请求
                SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
                req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
                req.message = weiboMessage;
                // 发送请求消息到微博
                weiboAPI.sendRequest(this, req);
            }
        }
        else {
            // TODO
// ViewUtil.showTipsToast(this, "当前微博版本不支持SDK分享");
        }
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getShareText();
        return textObject;
    }

    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        String path = PicPath;
        File file = new File(path);
        Bitmap thumbBmp;
        if (!file.exists()) {
            thumbBmp = null;
        }
        else {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        }
        if (null != thumbBmp)
            imageObject.setImageObject(thumbBmp);
        return imageObject;
    }

    ExpandAnimation expandAni;

    public void doOnceCompleteAuth() {
        if (Double.parseDouble(winmoney) == 0) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("shareText", shareText);
            bundle.putInt("from", -1);
            bundle.putInt("share_type", SHARE_TYPE_SINA);
            intent.putExtras(bundle);
            intent.setClass(NormalOrderDetail.this, SharePage.class);
            NormalOrderDetail.this.startActivity(intent);
        }
        else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("shareText", shareText);
            bundle.putInt("from", 1);
            bundle.putInt("share_type", SHARE_TYPE_SINA);
            intent.putExtras(bundle);
            intent.setClass(NormalOrderDetail.this, SharePage.class);
            cutScreen();

            NormalOrderDetail.this.startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
// gotoSharePage();
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                ViewUtil.showTipsToast(NormalOrderDetail.this, About.SEND_QQWEIBO_SUCCESS);
            }
        }
// else if (requestCode == 1) {
// if (resultCode == RESULT_OK) {
// doOnceCompleteAuth();
// }
// }
    }

    private void submitAnalyseDataShare(String way) {
        Map<String, String> map = new HashMap<String, String>();
        String user = appState.getUsername();
        if (user == null) {
            user = "";
        }
        map.put("user", user);
        map.put("way", way);
        map.put("location", "投注记录");
        String eventNameMob = "share_information";
        MobclickAgent.onEvent(this, eventNameMob, way);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type +
            System.currentTimeMillis();
    }

    class OrderDetailTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            progress.setVisibility(View.GONE);
            if (result == null) {
                searchFail();
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(result);
                if (status.equals("200")) {
                    lotteryNumBet.setEnabled(true);
                    betAgain.setEnabled(true);
                    betShare.setEnabled(true);
                    String data = analyse.getData(result, RESPONSE);
                    showData(data);
                    initView();
                }
                else if (status.equals("202")) {
                    searchNoData();
                }
                else {
                    searchFail();
                }
            }
            progress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "order_relation_detail");
            parameter.put("pid", LotteryUtils.getPid(NormalOrderDetail.this));
            parameter.put("order_id", orderId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... para) {
            ConnectService connectNet = new ConnectService(NormalOrderDetail.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(1, false, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    // 设置竞彩的过关方式
    private String getBetType(String guoGuan) {
        StringBuilder guoGuanSb = new StringBuilder();
        String[] guoGuanArray = guoGuan.split("\\;");
        for (int j = 0; j < guoGuanArray.length; j++)
            for (int i = 0; i < SFCCODE.length; i++)
                if (SFCCODE[i] == Integer.valueOf(guoGuanArray[j])) {
                    guoGuanSb.append(BUNCHNAME[i]);
                    guoGuanSb.append(",");
                }
        guoGuanSb.delete(guoGuanSb.length() - 1, guoGuanSb.length());
        return guoGuanSb.toString();
    }

    private void searchFail() {
        message.setVisibility(View.VISIBLE);
        message.setText("查询失败");
        showFailPage.setVisibility(View.VISIBLE);
    }

    private void searchNoData() {
        message.setVisibility(View.VISIBLE);
        message.setText("查询无数据");
        showFailPage.setVisibility(View.VISIBLE);
    }

    private void orgShareWay() {
        shareWay = preferences.getInt("share_way", SHARE_TYPE_SINA);
        if (shareWay == SHARE_TYPE_SINA)
            betShare.setText("  分享到新浪  ");
        else if (shareWay == SHARE_TYPE_OTHERS)
            betShare.setText("    其他分享    ");
        else if (shareWay == SHARE_TYPE_WEIXIN)
            betShare.setText("  分享到微信  ");
        else if (shareWay == SHARE_TYPE_WEIXINFANS)
            betShare.setText("  微信朋友圈  ");
        else if (shareWay == SHARE_TYPE_QQWEIBO)
            betShare.setText("    腾讯微博    ");
        betShare.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showShareWayPopupViews();
                return false;
            }
        });
    }

    private void initSharePopUpWindow() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View waySwitchLayout = mLayoutInflater.inflate(R.layout.share_ways, null);

        shareWayPopupWindow = new PopupWindow(this);
        shareWayPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        shareWayPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        shareWayPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        shareWayPopupWindow.setOutsideTouchable(true);
        shareWayPopupWindow.setFocusable(true);
        shareWayPopupWindow.setContentView(waySwitchLayout);
        shareWayPopupWindow.setAnimationStyle(R.style.popup_ball);
        setUpPopWindowViewWidget(waySwitchLayout);
    }

    private void setUpPopWindowViewWidget(View waySwitchLayout) {
        // 分享到新浪
        shareDirectly = (TextView) waySwitchLayout.findViewById(R.id.share_to_sina_weibo);
        shareDirectly.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_SINA) {
                    shareWay = SHARE_TYPE_SINA;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    betShare.setText("  分享到新浪  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到微信好友
        shareWeixin = (TextView) waySwitchLayout.findViewById(R.id.share_to_weixin);
        shareWeixin.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_WEIXIN) {
                    shareWay = SHARE_TYPE_WEIXIN;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    betShare.setText("  分享到微信  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到朋友圈
        shareWeixinFriends = (TextView) waySwitchLayout.findViewById(R.id.share_to_weixin_fans);
        shareWeixinFriends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_WEIXINFANS) {
                    shareWay = SHARE_TYPE_WEIXINFANS;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    betShare.setText("  微信朋友圈  ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 分享到腾讯微博
        shareQQWeibo = (TextView) waySwitchLayout.findViewById(R.id.share_to_qq_weibo);
        shareQQWeibo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_QQWEIBO) {
                    shareWay = SHARE_TYPE_QQWEIBO;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    betShare.setText("    腾讯微博    ");
                }
                shareWayPopupWindow.dismiss();
            }
        });
        // 其他分享
        shareOther = (TextView) waySwitchLayout.findViewById(R.id.share_to_others);
        shareOther.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (shareWay != SHARE_TYPE_OTHERS) {
                    shareWay = SHARE_TYPE_OTHERS;
                    databaseData.putInt("share_way", shareWay);
                    databaseData.commit();
                    betShare.setText("    其他分享    ");
                }
                shareWayPopupWindow.dismiss();
            }
        });

    }

    private void showShareWayPopupViews() {
        if (shareWay == SHARE_TYPE_SINA) {
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareDirectly.setTextColor(Color.WHITE);
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(Color.BLACK);
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(Color.BLACK);
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(Color.BLACK);
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(Color.BLACK);
        }
        else if (shareWay == SHARE_TYPE_OTHERS) {
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareOther.setTextColor(Color.WHITE);
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(Color.BLACK);
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(Color.BLACK);
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(Color.BLACK);
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(Color.BLACK);
        }
        else if (shareWay == SHARE_TYPE_WEIXIN) {
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareWeixin.setTextColor(Color.WHITE);
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(Color.BLACK);
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(Color.BLACK);
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(Color.BLACK);
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(Color.BLACK);
        }
        else if (shareWay == SHARE_TYPE_WEIXINFANS) {
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareWeixinFriends.setTextColor(Color.WHITE);
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(Color.BLACK);
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(Color.BLACK);
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(Color.BLACK);
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareQQWeibo.setTextColor(Color.BLACK);
        }
        else if (shareWay == SHARE_TYPE_QQWEIBO) {
            shareQQWeibo.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            shareQQWeibo.setTextColor(Color.WHITE);
            shareWeixinFriends.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixinFriends.setTextColor(Color.BLACK);
            shareWeixin.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareWeixin.setTextColor(Color.BLACK);
            shareOther.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareOther.setTextColor(Color.BLACK);
            shareDirectly.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            shareDirectly.setTextColor(Color.BLACK);
        }
        shareWayPopupWindow.showAsDropDown(betShare, 0, -(betShare.getHeight() * 6 + 40));
    }

    private void betAgain() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Bundle bundle = new Bundle();
        bundle.putString("resource", "bet_history");
        bundle.putString("bet_code", getSelectedCodes());
        intent.putExtras(bundle);
        intent.setClass(NormalOrderDetail.this, setBetCompirClass(kind));
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    private Class<?> setBetCompirClass(String kind) {
        Class<?> cls = SSQBetConfirm.class;
        this.kind = kind;
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
        else if (kind.equals("ssl")) {
// cls = SSLBetConfirm.class;
// SSLBetConfirm.betLocalList.clear();
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
        else if (kind.equals("22x5")) {
// cls = EEXWBetConfirm.class;
// EEXWBetConfirm.betLocalList.clear();
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
        else if (kind.equals("jlk3")) {
            cls = JLKSBetConfirm.class;
            JLKSBetConfirm.betLocalList.clear();
        }
        else if (kind.equals("jxssc")) {
            cls = JXSSCBetConfirm.class;
            JXSSCBetConfirm.betLocalList.clear();
        }
        return cls;
    }

    private String direction = null;

    private void showLOcation(float d) {
        float presentOrentation = 0.0f;
        presentOrentation = d;
        float range = (float) 22.5;
        // 指向正北
        if (presentOrentation > 360 - range && presentOrentation < 360 + range) {
            direction = "正北 ";
        }
        // 指向正东
        if (presentOrentation > 90 - range && presentOrentation < 90 + range) {
            direction = "正东 ";
        }
        // 指向正南
        if (presentOrentation > 180 - range && presentOrentation < 180 + range) {
            direction = "正南 ";
        }
        // 指向正西
        if (presentOrentation > 270 - range && presentOrentation < 270 + range) {
            direction = "正西 ";
        }
        // 指向东北
        if (presentOrentation > 45 - range && presentOrentation < 45 + range) {
            direction = "东北 ";
        }
        // 指向东南
        if (presentOrentation > 135 - range && presentOrentation < 135 + range) {
            direction = "东南 ";
        }
        // 指向西南
        if (presentOrentation > 225 - range && presentOrentation < 225 + range) {
            direction = "西南 ";
        }
        // 指向西北
        if (presentOrentation > 315 - range && presentOrentation < 315 + range) {
            direction = "西北 ";
        }
        // 判断是否显示方向,根据参数direction
// if (direction == null) {
// betLocationLinear.setVisibility(View.GONE);
// }
// else {
// betLocationLinear.setVisibility(View.VISIBLE);
// }
        if (presentOrentation != -1.0) {
            betLocation.setText(direction);
        }
        else {
            betLocationLinear.setVisibility(View.GONE);
            betLocationTv.setVisibility(View.GONE);
            betLocation.setVisibility(View.GONE);
        }

    }

    private Bitmap shot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 != 0) {
            if (kind.equals("sfc") || kind.equals("r9")) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("displayCode", codes);
                bundle.putString("bet_kind", kind);
                bundle.putString("bet_term", term);
                bundle.putString("opne_codes", opens);
                intent.putExtras(bundle);
                intent.setClass(NormalOrderDetail.this, FootballBetOrderDetail.class);
                startActivity(intent);
            }
            else {
                if (!kind.equals("jczq") && !kind.equals("jclq")) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("lottery_id", kind);
                    bundle.putString("term", term);
                    bundle.putString("codes", codes.split("\\;")[arg2 - 1]);
                    bundle.putString("opens", opens);
                    bundle.putDouble("money", Double.valueOf(money));
                    bundle.putString("order_id", orderId);
                    bundle.putString("prize_status", winStatus);
                    bundle.putInt("selected_index", arg2);
                    bundle.putString("m", m);
                    bundle.putString("d", d);
                    bundle.putString("orgNum", orgNum);
                    bundle.putString("star", star);
                    bundle.putString("luckThings", luckThings);
                    intent.putExtras(bundle);
                    intent.setClass(NormalOrderDetail.this, BetOrderDetail.class);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onSFCR9ActionClick(String status) {
        if (status != null) {
            if (status.equals("200")) {
                teams[0][0] = "主";
                teams[0][1] = "客";
                for (int i = 1; i < 15; i++) {
                    teams[i][0] = matchDataList.get(i - 1).getMatchHomeTeamName();
                    teams[i][1] = matchDataList.get(i - 1).getMatchGuessTeamName();
                }
                lotterySfcR9CodeAdapter =
                    new LotterySfcR9CodeAdapter(NormalOrderDetail.this, codes, kind, opens, teams,
                                                screenWidth);
                lotteryNumBet.setAdapter(lotterySfcR9CodeAdapter);
            }
        }
    }

    @Override
    public void onJingCaiHisActionClick(String status) {
        if (status != null) {
            if (status.equals("200")) {
                betAgain.setEnabled(true);
                betShare.setEnabled(true);
                lotteryNumBet.setEnabled(true);
                showData(null);
                initView();

                if (splitNum.equals("1")) {
                    betAgain.setEnabled(false);
                    betAgain.setText("订单详细");
                    betAgain.setVisibility(View.INVISIBLE);
                }
                else {
                    jingcaiMessage.setVisibility(View.VISIBLE);
                    betAgain.setText("订单详细(" + splitNum + ")");
                }
                awardMoneyLinear.setVisibility(View.VISIBLE);
                getTheHightRate(term);
                calculateAward();
            }
            if(status.equals("nodata")) {
            	Toast.makeText(this, "投注信息暂无", 0).show();
            	this.finish();
            }
        }
        progress.setVisibility(View.GONE);
    }
    
    //胜负彩点击
    @Override
    public void onShengFuHisActionClick(String status) {
        if (status != null) {
            if (status.equals("200")) {
                betAgain.setEnabled(true);
                betShare.setEnabled(true);
                lotteryNumBet.setEnabled(true);
                showData(null);
                initView();

                if (splitNum.equals("1")) {
                    betAgain.setEnabled(false);
                    betAgain.setText("订单详细");
                    betAgain.setVisibility(View.INVISIBLE);
                }
                else {
                    jingcaiMessage.setVisibility(View.VISIBLE);
                    betAgain.setText("订单详细(" + splitNum + ")");
                }
                awardMoneyLinear.setVisibility(View.VISIBLE);
                getTheHightRate(term);
                calculateAward();
            }
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            NormalOrderDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 判断投注号码是否被选中
    private int getSelectedSize() {
        int length = 0;
        for (int i = 0; i < lotteryCodeAdapter.isSelected.size(); i++)
            if (lotteryCodeAdapter.isSelected.get(i)) {
                length = length + 1;
            }
        return length;
    }

    // 根据被选中的号码进行合成新的投注号码
    private String getSelectedCodes() {
        String[] betCodes = codes.split("\\;");
        StringBuilder sb_code = new StringBuilder();
        for (int i = 0; i < lotteryCodeAdapter.isSelected.size(); i++)
            if (lotteryCodeAdapter.isSelected.get(i)) {
// if (betCodes[i].indexOf("(") != -1)
// sb_code.append(BetHistoryDetailTool.filterSyxwDantuoCode(betCodes[i]));
// else
                sb_code.append(betCodes[i]);
                sb_code.append(";");
            }

        sb_code.delete(sb_code.length() - 1, sb_code.length());

        return sb_code.toString();
    }

    // 对页面变量进行赋值
    private void showData(String data) {
        try {
            if (!kind.equals("jczq") && !kind.equals("jclq")) {
                JsonAnalyse analyse = new JsonAnalyse();
                d = analyse.getData(data, "d");
                m = analyse.getData(data, "mode");
                orgNum = analyse.getData(data, "lucknum");
                star = analyse.getData(data, "mstar");
                luckThings = analyse.getData(data, "todayluck");
                location = analyse.getData(data, "loc");
                codes = analyse.getData(data, "codes");
                opens = analyse.getData(data, "open");
// opens="1,2,3";
                money = analyse.getData(data, "money");
                winmoney = analyse.getData(data, "prize");
                winStatus = analyse.getData(data, "win");
                buyMode = Integer.valueOf(analyse.getData(data, "buy"));
                orderTime = analyse.getData(data, "time");
            }
            else if (kind.equals("jczq") || kind.equals("jclq")) {
                d = mapJCBetHistoryDetail.get("d");
                m = mapJCBetHistoryDetail.get("mode");
                guoGuan = mapJCBetHistoryDetail.get("bet_type");
                location = mapJCBetHistoryDetail.get("loc");
                splitNum = mapJCBetHistoryDetail.get("split_num");
                codes = mapJCBetHistoryDetail.get("codes");
                money = mapJCBetHistoryDetail.get("bet_money");
                winStatus = mapJCBetHistoryDetail.get("win");
                buyMode = Integer.valueOf(mapJCBetHistoryDetail.get("buy_mode"));
                winmoney = mapJCBetHistoryDetail.get("prize");
            }

            // 根据服务器返回的地理数据进行解析然后显示方向例如：正东。
            showLOcation(Float.valueOf(d));

            if (!location.equals("")) {
                userBetLocation.setText(location);
                headViewInfoContainer04.setVisibility(View.VISIBLE);
            }

            layout.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            searchFail();
            e.printStackTrace();
        }
    }

    private void initView() {
        if (!kind.equals("jczq") && !kind.equals("jclq")) {// 如果彩种是数字彩和胜负彩，任选9
            // 解析数字彩投注号码，分解出：投注号码，倍数，玩法
            spiltBetCode();
            // 给开奖号码添加背景
            drawOpenBall();
            // 显示订单的期数
            termTV.setText(term + "期");
            // 设置列表适配器
            if (!kind.equals("sfc") && !kind.equals("r9")) {
            	if(kind.equals("dcsfgg")) {           
            		ShengFuAdapter shengFuAdapter = new ShengFuAdapter(NormalOrderDetail.this, shengFuBetHistoryDetailDataList);
            		lotteryNumBet.setAdapter(shengFuAdapter);
            		//ShengFuAdapter
            	} else {            		
            		lotteryCodeAdapter = new LotteryCodeAdapter(NormalOrderDetail.this, codes, kind, opens, null, screenWidth);
            		lotteryNumBet.setAdapter(lotteryCodeAdapter);
            	}
            }

            if (kind.equals("sfc") || kind.equals("r9")) {
                betAgain.setEnabled(false);
                betAgain.setVisibility(View.INVISIBLE);
                SportLotteryInfTask sportLotteryInfTask =
                    new SportLotteryInfTask(NormalOrderDetail.this, matchDataList, kind, term);
                sportLotteryInfTask.execute();
                sportLotteryInfTask.setTaskChangeListener(this);
            }
        }
  
        else {
        	//竞彩足球
            openBallBottomLine.setVisibility(View.GONE);
            jingCaiGuoGuan.setText(getBetType(guoGuan));
            // 解析竞彩投注号码，分解出：投注号码，倍数，玩法
            spiltBetCodeJinCai();
            // 设置列表适配器  second null
            jingCaiBetHistoryDetailAdapter =
                new JingCaiBetHistoryDetailAdapter(NormalOrderDetail.this, jingCaiBetHistoryDetailDataList,
                                                   screenWidth);
            lotteryNumBet.setAdapter(jingCaiBetHistoryDetailAdapter);
            jingCaiBetHistoryDetailAdapter.setJingCaiDetailDataType(mapJCBetHistoryDetail.get("term"));
        }
        // 设置倍数
        loteryBetDoublenum.setText(betDouble + "倍");

        // 设置投注金额
        moneyTV.setText(money + "元");
        orderIdTV.setText(orderId);

        //
        try {
            if (orderTime != null)
                orderDateTV.setText(orderTime);
            else
                orderDateTV.setText("--");
        }
        catch (Exception e) {
            orderDateTV.setText("--");
        }

        // 显示订单状态
        setWinStatus();
        // 设置竞彩的玩法
        jingCaiMethod.setText(getJingCaiBetWay(term));

        // 初始分享内容
        initShareView();
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet_order_detail";
        MobclickAgent.onEvent(this, eventName, "普通投注");
        besttoneEventCommint(eventName);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        imageButton.setClickable(true);
        rechargeBottomBtLaout.setClickable(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        imageButton.setClickable(false);
        rechargeBottomBtLaout.setClickable(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        weiboAPI.responseListener(getIntent(), this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
                Toast.makeText(this, "成功！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, "用户取消！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
                break;
        }

    }

}