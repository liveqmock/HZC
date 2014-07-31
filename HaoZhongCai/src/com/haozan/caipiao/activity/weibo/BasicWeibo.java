package com.haozan.caipiao.activity.weibo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.guess.GuessParty;
import com.haozan.caipiao.activity.news.NewsCommentList;
import com.haozan.caipiao.activity.unite.UniteHallDetail;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.JsonUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.MyListView;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.MyListView.ListViewScrollStateChanged;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class BasicWeibo
    extends BasicActivity {
    private static final String TAG = "BasicWeibo";
    public static final String[] weiboTypesName = new String[] {"双色球动态", "3D动态", "七乐彩动态", "时时乐动态", "东方6+1动态",
            "15选5动态", "大乐透动态", "排列3动态", "排列5动态", "七星彩动态", "22选5动态", "竞彩足球动态", "任选九动态", "胜负彩动态", "购彩动态",
            "财友互动", "新闻评论", "中奖动态", "11选5动态", "游戏动态", "快乐十分动态", "竞彩篮球动态", "快乐十分动态", "时时彩动态", "合买动态", "竞猜动态",
            "新时时彩动态", "新快三动态"};
    public static final String[] weiboTypesEnglishNames = new String[] {"ssq", "sd", "qlc", "ssl", "dfljy",
            "swxw", "dlt", "pls", "plw", "qxc", "eexw", "jczq", "rxj", "sfc", "goucai", "putong", "xinwen",
            "zhongjiang", "jx11x5", "games", "hnklsf", "jclq", "klsf", "cqssc", "hemai", "jingcai", "jxssc",
            "jlk3"};
    public static final int[] layoutIdNames = new int[] {R.id.ssqly, R.id.sdly, R.id.qlcly, R.id.sslly,
            R.id.dfljyly, R.id.swxwly, R.id.dltly, R.id.plsly, R.id.plwly, R.id.qxcly, R.id.eexwly,
            R.id.jjzqly, R.id.rxjly, R.id.sfcly, R.id.goucaily, R.id.putongly, R.id.xinwenly,
            R.id.zhongjiangly, R.id.syxwly, R.id.gamesly, R.id.square_hnklsfly, R.id.square_jclq,
            R.id.square_klsfly, R.id.square_cqsscly, R.id.hemaily, R.id.jingcaily, R.id.jxsscly, R.id.jlksly};
    // add by vincent
    public static final String[] zjqNum = new String[] {"0", "1", "2", "3", "4", "5", "6", "7"};
    public static final String[] zjqStr = new String[] {"0,", "1,", "2,", "3,", "4,", "5,", "6,", "7+,"};
    public static final String[] bqcNum = new String[] {"33", "31", "30", "13", "11", "10", "03", "01", "00"};
    public static final String[] bqcStr = new String[] {"胜胜,", "胜平,", "胜负,", "平胜,", "平平,", "平负,", "负胜,",
            "负平,", "负负,"};
    public static final String[] jczqBfNum = {"10", "20", "21", "30", "31", "32", "40", "41", "42", "50",
            "51", "52", "90", "00", "11", "22", "33", "99", "01", "02", "03", "04", "05", "12", "13", "14",
            "15", "23", "24", "25", "09"};
    public static final String[] jczqBfStr = {"1:0,", "2:0,", "2:1,", "3:0,", "3:1,", "3:2,", "4:0,", "4:1,",
            "4:2,", "5:0,", "5:1,", "5:2,", "胜其他,", "0:0,", "1:1,", "2:2,", "3:3,", "平其他,", "0:1,", "0:2,",
            "0:3,", "0:4,", "0:5,", "1:2,", "1:3,", "1:4,", "1:5,", "2:3,", "2:4,", "2:5,", "负其他,"};
    // 快乐十分
    private static final String[] klsfNum = {"9", "1", "2", "3", "4", "5"};
    private static final String[] klsfStr = {"直选好运特:", "好运五:", "好运四:", "好运三:", "好运二:", "好运一:"};
    // 时时乐
    private static final String[] sslNum = {"1", "2", "3", "4", "5", "6", "7"};
    private static final String[] sslStr = {"直选:", "组三:", "组六(包号):", "前二:", "后二:", "前一:", "后一:"};

    public static final String SCHEMANAME = "gerden://gerden_name";
    public static final String SCHEMATOPIC = "gerden://gerden_topic";
    public static final String PARAM_UID = "uid";

    // add by vincent
    private static String gameName = null;
    private static ArrayList<GameDownloadInf> gameList;
    private static CustomDialog checkDownloadDialog;
    private static int position = 0;

    /**
     * 财园投注跳转
     * 
     * @param context 上下文
     * @param lotterytype 彩种类别
     * @param betcode 投注号码
     */
    public static void betJump(Context context, String lotterytype, String betcode) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("resource", "bet_weibo");
        bundle.putString("bet_code", betcode);
        bundle.putString("title", lotterytype);
        intent.putExtras(bundle);

        // <通用方法>判断来自哪个彩种，与if语句效果相同
        Boolean isFail = false;
        for (int i = 0; i < LotteryUtils.lotteryclass.length; i++) {
            if (lotterytype.equals("jczq|75") || lotterytype.equals("jczq|71") ||
                lotterytype.equals("jczq|72") || lotterytype.equals("jczq|73") ||
                lotterytype.equals("jczq|74") || lotterytype.equals("jclq|82") ||
                lotterytype.equals("jclq|81") || lotterytype.equals("jclq|84")) {
                if (lotterytype.substring(0, 4).equals(LotteryUtils.LOTTERY_ID[i])) {
                    // changed by vincent
                    isFail = true;
                    intent.setClass(context, LotteryUtils.lotteryclass[i]);
                    context.startActivity(intent);
                    Jump();
                }
            }
            else if (lotterytype.equals(LotteryUtils.LOTTERY_ID[i])) {
                isFail = true;
                intent.setClass(context, LotteryUtils.lotteryclass[i]);
                context.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                       R.anim.push_to_right_out);
                }
                Jump();
            }
        }
        if (isFail == false) {
            Toast.makeText(context, "版本暂不支持该彩种转投", 1000).show();
        }
    }

    /**
     * 菜园子内容显示
     * 
     * @param newsTitle 用于显示的TextView
     * @param type 显示类别
     * @param title 新闻标题、彩种名称
     * @param preview 投注号码
     * @param newsTitleLy 微博正文投注号码、新闻类容显示
     * @param content
     * @param string
     */
    public static void subContentShow(Context context, TextView newsTitle, int type, String title,
                                      String preview, LinearLayout newsTitleLy, TextView content,
                                      String contentData) {
        if (title.equals("null") || preview.equals("") && type != 6) {
            newsTitle.setVisibility(View.GONE);
        }
        else {
            if (type == 3) {
                if (newsTitleLy != null) {
                    newsTitleLy.setVisibility(View.VISIBLE);
                }
                newsTitle.setVisibility(View.VISIBLE);
                newsTitle.setText(Html.fromHtml("<font color='#000000' size='5'>" + "<b>" + "文章标题：" + title +
                    "</b>" + "</font>" + "    " + preview + "..."));
            }
            else if (type == 4 || type == 5 || type == 7) {
                // 如果不是ListView列表就让微博正文投注号码、新闻类容显示
                if (newsTitleLy != null) {
                    newsTitleLy.setVisibility(View.VISIBLE);
                }
                newsTitle.setVisibility(View.VISIBLE);
                // add by vincent
                if (type == 7) {
                    title = title.split("\\|")[0];
                }

                String[] previews;
                boolean isGuoguan = true;
                // TODO
                if (title.length() > 6 &&
                    (title.subSequence(0, 4).equals("jczq") || title.subSequence(0, 4).equals("jclq")) ||
                    title.substring(0, 1).equals("7")) {
// if (title.subSequence(0, 4).equals("jclq") || title.length() > 6 && title.subSequence(0, 4).equals("jczq"))
// {
                    previews = preview.split("\\|");
                    String pass = preview.split("\\:")[2];
                    if (!pass.equals("100"))
                        isGuoguan = true;
                    else
                        isGuoguan = false;
                }
                else {
                    previews = preview.split(";");
                }
                StringBuilder s = new StringBuilder();
                if (type == 4) {
                    s.append("<font color='#000000' size='5'>" + "<b>" + "投注信息：");
                }
                else if (type == 7) {
                    s.append("<font color='#000000' size='5'>" + "<b>" + "合买信息：");
                }
                else if (type == 5) {
                    s.append("<font color='#000000' size='5'>" + "<b>" + "中奖信息：");
                }

                if (title.length() > 6 && title.substring(0, 4).equals("jczq") ||
                    title.substring(0, 1).equals("7")) {
                    if (title.equals("jczq|71") || title.equals("71")) {
                        s.append(isGuoguan ? "竞彩足球 - 过关让分胜平负" : "竞彩足球 - 单关让分胜平负");
                    }
                    else if (title.equals("jczq|72") || title.equals("72")) {
                        s.append(isGuoguan ? "竞彩足球 - 过关总进球" : "竞彩足球 - 单关总进球");
                    }
                    else if (title.equals("jczq|73") || title.equals("73")) {
                        s.append(isGuoguan ? "竞彩足球 - 过关比分" : "竞彩足球 - 单关比分");
                    }
                    else if (title.equals("jczq|74") || title.equals("74")) {
                        s.append(isGuoguan ? "竞彩足球 - 过关半全场" : "竞彩足球 - 单关半全场");
                    }
                    else if (title.equals("jczq|75") || title.equals("75")) {
                        s.append(isGuoguan ? "竞彩足球 - 过关胜平负" : "竞彩足球 - 单关胜平负");
                    }
                    else {
                        s.append("竞彩足球");
                    }
                }
                else if (title.length() > 6 && title.substring(0, 4).equals("jclq")) {
                    if (title.equals("jclq|82")) {
                        s.append(isGuoguan ? "竞彩篮球 - 过关胜负" : "竞彩篮球 - 单关胜负");
                    }
                    else if (title.equals("jclq|81")) {
                        s.append(isGuoguan ? "竞彩篮球 - 过关让分胜负" : "竞彩篮球 - 单关让分胜负");
                    }
                    else if (title.equals("jclq|84")) {
                        s.append(isGuoguan ? "竞彩篮球 - 过关大小分" : "竞彩篮球 - 单关大小分");
                    }
                    else {
                        s.append("竞彩篮球");
                    }
                }
                else {
                    // <通用方法>判断来自哪个彩种投注信息，与if语句效果相同
                    s.append(LotteryUtils.getLotteryName(title));
                }
                s.append("</b>" + "</font>" + "<br/>");

                if (title.equals("dfljy")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String[] ball = lotteryNum[0].split("\\|");
                        String[] animals = ball[1].split(",");
                        ball[1] = "";
                        for (int i1 = 0; i1 < animals.length; i1++) {
                            animals[i1] = LotteryUtils.animals[Integer.valueOf(animals[i1]) - 1];
                            ball[1] += animals[i1];
                        }
                        s.append(ball[0] + "|" + ball[1].subSequence(0, ball[1].length()) + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                // changed by vincent, new plays
                else if (title.equals("jczq|71") || title.equals("jczq|72") || title.equals("jczq|73") ||
                    title.equals("jczq|74") || title.equals("jczq|75") || title.substring(0, 1).equals("7")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");

                        String[] jczqInfo = lotteryNum[0].split("\\$");
                        byte[] chars = jczqInfo[0].getBytes();
                        char c = (char) chars[0];
                        switch (c) {
                            case '1':
                                s.append("星期一 -");
                                break;
                            case '2':
                                s.append("星期二 -");
                                break;
                            case '3':
                                s.append("星期三 -");
                                break;
                            case '4':
                                s.append("星期四 -");
                                break;
                            case '5':
                                s.append("星期五 -");
                                break;
                            case '6':
                                s.append("星期六 -");
                                break;
                            case '7':
                                s.append("星期日 -");
                                break;
                            default:
                                break;
                        }
                        String[] info1 = jczqInfo[1].split("\\=");
                        s.append(info1[0] + " 场：");

                        String[] chars1 = info1[1].split("\\,");
                        for (int j = 0; j < chars1.length; j++) {
                            if (title.equals("jczq|71") || title.equals("jczq|75") || title.equals("71") ||
                                title.equals("75")) {
                                if (chars1[j].equals("0")) {
                                    s.append("<font color='#008000'>" + "负," + "</font>");
                                }
                                else if (chars1[j].equals("1")) {
                                    s.append("<font color='#ff666666'>" + "平," + "</font>");
                                }
                                else if (chars1[j].equals("3")) {
                                    s.append("<font color='#FF0000'>" + "胜," + "</font>");
                                }
                            }
                            else if (title.equals("jczq|72") || title.equals("72")) {
                                for (int m = 0; m < 8; m++) {
                                    if (chars1[j].equals(zjqNum[m])) {
                                        s.append("<font color='#ff666666'>" + zjqStr[m] + "</font>");
                                    }
                                }
                            }
                            else if (title.equals("jczq|73") || title.equals("73")) {
                                for (int m = 0; m < 31; m++) {
                                    if (chars1[j].equals(jczqBfNum[m])) {
                                        s.append("<font color='#ff666666'>" + jczqBfStr[m] + "</font>");
                                    }
                                }
                            }
                            else if (title.equals("jczq|74") || title.equals("74")) {
                                for (int m = 0; m < 9; m++) {
                                    if (chars1[j].equals(bqcNum[m])) {
                                        s.append("<font color='#ff666666'>" + bqcStr[m] + "</font>");
                                    }
                                }
                            }
                        }
                        s.deleteCharAt(s.length() - 8);
                        s.append("</font>" + "<br/>");

// s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                // TODO
                else if (title.equals("jclq|82") || title.equals("jclq|81") || title.equals("jclq|84")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");

                        String[] jclqInfo = lotteryNum[0].split("\\$");
                        byte[] chars = jclqInfo[0].getBytes();
                        char c = (char) chars[0];
                        switch (c) {
                            case '1':
                                s.append("星期一 -");
                                break;
                            case '2':
                                s.append("星期二 -");
                                break;
                            case '3':
                                s.append("星期三 -");
                                break;
                            case '4':
                                s.append("星期四 -");
                                break;
                            case '5':
                                s.append("星期五 -");
                                break;
                            case '6':
                                s.append("星期六 -");
                                break;
                            case '7':
                                s.append("星期日 -");
                                break;
                            default:
                                break;
                        }
                        String[] info1 = jclqInfo[1].split("\\=");
                        s.append(info1[0] + "场：");

                        String[] chars1 = info1[1].split("\\,");
                        for (int j = 0; j < chars1.length; j++) {
                            // TODO
                            if (title.equals("jclq|82")) {
// if (title.equals("jclq")) {
                                if (chars1[j].equals("1")) {
                                    s.append("<font color='#FF0000'>" + "主胜," + "</font>");
                                }
                                else if (chars1[j].equals("2")) {
                                    s.append("<font color='#008000'>" + "主负," + "</font>");
                                }
                            }
                            else if (title.equals("jclq|81")) {
                                if (chars1[j].equals("1")) {
                                    s.append("<font color='#FF0000'>" + "主胜," + "</font>");
                                }
                                else if (chars1[j].equals("2")) {
                                    s.append("<font color='#008000'>" + "主负," + "</font>");
                                }
                            }
                            else if (title.equals("jclq|84")) {
                                if (chars1[j].equals("1")) {
                                    s.append("<font color='#FF0000'>" + "大," + "</font>");
                                }
                                else if (chars1[j].equals("2")) {
                                    s.append("<font color='#008000'>" + "小," + "</font>");
                                }
                            }
                        }
                        s.deleteCharAt(s.length() - 8);
                        s.append("</font>" + "<br/>");

// s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("jx11x5")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[2];
                        if (lotterytype.equals("11_RX2")) {
                            s.append("任选二:");
                        }
                        else if (lotterytype.equals("11_RX3")) {
                            s.append("任选三:");
                        }
                        else if (lotterytype.equals("11_RX4")) {
                            s.append("任选四:");
                        }
                        else if (lotterytype.equals("11_RX5")) {
                            s.append("任选五:");
                        }
                        else if (lotterytype.equals("11_RX6")) {
                            s.append("任选六:");
                        }
                        else if (lotterytype.equals("11_RX7")) {
                            s.append("任选七:");
                        }
                        else if (lotterytype.equals("11_RX8")) {
                            s.append("任选八:");
                        }
                        else if (lotterytype.equals("11_RX1")) {
                            s.append("前一:");
                        }
                        else if (lotterytype.equals("11_ZXQ2_D") || lotterytype.equals("11_ZXQ2_F")) {
                            s.append("前二直选:");
                        }
                        else if (lotterytype.equals("11_ZXQ2")) {
                            s.append("前二组选:");
                        }
                        else if (lotterytype.equals("11_ZXQ3_D") || lotterytype.equals("11_ZXQ3_F")) {
                            s.append("前三直选:");
                        }
                        else if (lotterytype.equals("11_ZXQ3")) {
                            s.append("前三组选:");
                        }
                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("klsf")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[1];
                        for (int j = 0; j < klsfNum.length; j++) {
                            if (lotterytype.equals(klsfNum[j])) {
                                s.append(klsfStr[j]);
                            }
                        }

                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("hnklsf")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[2];
                        if (lotterytype.equals("101")) {
                            s.append("选一数投:");
                        }
                        else if (lotterytype.equals("102")) {
                            s.append("选一红投:");
                        }
                        else if (lotterytype.equals("201") || lotterytype.equals("202")) {
                            s.append("任选二:");
                        }
                        else if (lotterytype.equals("211") || lotterytype.equals("212")) {
                            s.append("选二连组:");
                        }
                        else if (lotterytype.equals("221") || lotterytype.equals("222")) {
                            s.append("选二连直:");
                        }
                        else if (lotterytype.equals("301") || lotterytype.equals("302")) {
                            s.append("任选三:");
                        }
                        else if (lotterytype.equals("331") || lotterytype.equals("332")) {
                            s.append("选三前组:");
                        }
                        else if (lotterytype.equals("341") || lotterytype.equals("342")) {
                            s.append("选三前直:");
                        }
                        else if (lotterytype.equals("401") || lotterytype.equals("402")) {
                            s.append("任选四:");
                        }
                        else if (lotterytype.equals("501") || lotterytype.equals("502")) {
                            s.append("任选五:");
                        }

                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("3d")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[1];
                        String way = lotteryNum[2];
                        if (lotterytype.equals("1")) {
                            if (way.equals("1") || way.equals("2")) {
                                s.append("直选单复式:");
                            }
                            else if (way.equals("4")) {
                                s.append("直选和值:");
                            }
                        }
                        else if (lotterytype.equals("2")) {
                            if (way.equals("1") || way.equals("2")) {
                                s.append("组三单复式:");
                            }
                            else if (way.equals("3")) {
                                s.append("组三包号:");
                            }
                            else if (way.equals("4")) {
                                s.append("组三和值:");
                            }
                        }
                        else if (lotterytype.equals("3")) {
                            if (way.equals("3")) {
                                s.append("组六包号:");
                            }
                            else if (way.equals("4")) {
                                s.append("组六和值:");
                            }
                        }

                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("pls")) {

                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[1];
                        String way = lotteryNum[2];
                        if (lotterytype.equals("1")) {
                            s.append("直选单复式:");
                        }
                        else if (lotterytype.equals("2")) {
                            if (way.equals("1") || way.equals("2")) {
                                s.append("组三单复式:");
                            }
                            else if (way.equals("3")) {
                                s.append("组三包号:");
                            }
                        }
                        else if (lotterytype.equals("3")) {
                            s.append("组六包号:");
                        }

                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));

                }
                else if (title.equals("ssl")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[1];
                        for (int j = 0; j < sslNum.length; j++) {
                            if (lotterytype.equals(sslNum[j])) {
                                s.append(sslStr[j]);
                            }
                        }

                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("cqssc")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[2];
                        if (lotterytype.equals("511")) {
                            s.append("五星通选:");
                        }
                        else if (lotterytype.equals("501") || lotterytype.equals("502")) {
                            s.append("五星直选:");
                        }
                        else if (lotterytype.equals("361") || lotterytype.equals("362")) {
                            s.append("三星组六:");
                        }
                        else if (lotterytype.equals("332")) {
                            s.append("三星组三:");
                        }
                        else if (lotterytype.equals("301") || lotterytype.equals("302")) {
                            s.append("三星直选:");
                        }
                        else if (lotterytype.equals("211") || lotterytype.equals("217")) {
                            s.append("二星组选:");
                        }
                        else if (lotterytype.equals("201") || lotterytype.equals("202")) {
                            s.append("二星直选:");
                        }
                        else if (lotterytype.equals("101")) {
                            s.append("一星:");
                        }
                        else if (lotterytype.equals("701")) {
                            s.append("大小单双:");
                        }
                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("jxssc")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[2];
                        if (lotterytype.equals("511") || lotterytype.equals("512")) {
                            s.append("五星通选:");
                        }
                        else if (lotterytype.equals("501") || lotterytype.equals("502")) {
                            s.append("五星直选:");
                        }
                        else if (lotterytype.equals("401") || lotterytype.equals("402")) {
                            s.append("四星直选:");
                        }
                        else if (lotterytype.equals("361") || lotterytype.equals("362")) {
                            s.append("三星组六:");
                        }
                        else if (lotterytype.equals("331") || lotterytype.equals("332")) {
                            s.append("三星组三:");
                        }
                        else if (lotterytype.equals("301") || lotterytype.equals("302")) {
                            s.append("三星直选:");
                        }
                        else if (lotterytype.equals("201") || lotterytype.equals("202")) {
                            s.append("二星直选:");
                        }
                        else if (lotterytype.equals("911") || lotterytype.equals("912")) {
                            s.append("任选二:");
                        }
                        else if (lotterytype.equals("101") || lotterytype.equals("102")) {
                            s.append("一星直选:");
                        }
                        else if (lotterytype.equals("901") || lotterytype.equals("902")) {
                            s.append("任选一:");
                        }
                        else if (lotterytype.equals("701")) {
                            s.append("大小单双:");
                        }
                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else if (title.equals("jlk3")) {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        String lotterytype = lotteryNum[2];
                        if (lotterytype.equals("101") || lotterytype.equals("102")) {
                            s.append("和值:");
                        }
                        else if (lotterytype.equals("103")) {
                            s.append("三同号通选");
                        }
                        else if (lotterytype.equals("104") || lotterytype.equals("105")) {
                            s.append("三同号:");
                        }
                        else if (lotterytype.equals("106") || lotterytype.equals("107") ||
                            lotterytype.equals("108") || lotterytype.equals("109")) {
                            s.append("二同号:");
                        }
                        else if (lotterytype.equals("110") || lotterytype.equals("111")) {
                            s.append("三不同号:");
                        }
                        else if (lotterytype.equals("113") || lotterytype.equals("114")) {
                            s.append("二不同号:");
                        }
                        else if (lotterytype.equals("116")) {
                            s.append("三连号通选");
                        }
                        if (!lotterytype.equals("103") && !lotterytype.equals("116"))
                            s.append(lotteryNum[0] + "<br/>");
                        else
                            s.append("<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
                else {
                    for (int i = 0; i < previews.length; i++) {
                        String[] lotteryNum = previews[i].split("\\:");
                        s.append(lotteryNum[0] + "<br/>");
                    }
                    String str = s.subSequence(0, s.length() - 5).toString();
                    newsTitle.setText(Html.fromHtml(str));
                }
            }
            // add by vincent, about game
            else if (type == 6) {
                if (gameList == null) {
                    gameList = new ArrayList<GameDownloadInf>();
                    SharedPreferences preferences = context.getSharedPreferences("user", 0);
                    String gameListJson = preferences.getString("game_list", null);
                    if (gameListJson != null) {
                        JsonUtil.analyseGameListData(gameList, gameListJson);
                    }
                }

                for (int i = 0; i < gameList.size(); i++) {
                    if (title.equals(gameList.get(i).getGameIndex())) {
                        gameName = gameList.get(i).getGameName();
                        break;
                    }
                }
                // 显示投注信息
                if (newsTitleLy != null) {
                    newsTitleLy.setVisibility(View.VISIBLE);
                }
                newsTitle.setVisibility(View.VISIBLE);
                StringBuilder str = new StringBuilder();
                if (gameName != null) {
                    str.append("<font color='#000000' size='5'>" + "<b>" + "我正在玩" + gameName + "游戏！" +
                        "</font>");
                }
                else {
                    str.append("<font color='#000000' size='5'>" + "<b>" + "我正在玩" + "未知" + "游戏！" + "</font>");
                }
                content.setText(Html.fromHtml(str.toString()));
                newsTitle.setText(contentData);
            }
            else if (type == 8) {
                if (newsTitleLy != null) {
                    newsTitleLy.setVisibility(View.VISIBLE);
                }
                newsTitle.setVisibility(View.VISIBLE);
                newsTitle.setText(preview);
            }
            else {
                newsTitle.setVisibility(View.VISIBLE);
                newsTitle.setText(Html.fromHtml("<font color='#000000' size='5'>" + "<b>" + "未知信息" + "</b>" +
                    "</font>" + "    " + preview + "..."));
            }
        }
    }

    // 提交财园正文子类别点击事件统计信息
    private static void submitStatisticsGardenJump(Context context, String kind) {
        LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", kind);
        map.put("more_inf", "username [" + appState.getUsername() + "]: user garden subcontent jump");
        String eventName = "garden subcontent jump";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "garden_subcontent_jump";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", kind);
        MobclickAgent.onEvent(context, eventNameMob, map1);
    }

    /**
     * 财园判断发自哪个版本以及投注跳转、新闻跳转控制
     * 
     * @param context 上下文
     * @param type 显示类别
     * @param version 版本号
     * @param title 新闻标题、彩种名称
     * @param weiboFrom 用于显示信息来源
     * @param newsTitle 用于显示新闻标题、彩种名称
     * @param attachid 新闻id
     * @param preview 投注号码、新闻内容
     */
    public static void weiboFrom(final Context context, int type, String version, final String title,
                                 TextView weiboFrom, TextView newsTitle, final String attachid,
                                 final String preview) {
        if (type == 0) {
            weiboFrom.setText("发自：动态发表");
        }
        else if (type == 1) {
            weiboFrom.setText("发自：动态评论");
        }
        else if (type == 2) {
            weiboFrom.setText("发自：动态转发");
        }
        else if (type == 3) {
            weiboFrom.setText("发自：" + version + "新闻评论");
            newsTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    submitStatisticsGardenJump(context, "news");
                    if (HttpConnectUtil.isNetworkAvailable(context)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("news_id", Integer.valueOf(attachid));
                        bundle.putString("title", "新　 闻");
                        intent.putExtras(bundle);
                        intent.setClass(context, NewsCommentList.class);
                        context.startActivity(intent);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                               R.anim.push_to_right_out);
                        }
                        Jump();
                    }
                    else {
                        Toast.makeText(context, "网络连接失败，请检查网络设置", 1000).show();
                    }
                }
            });
        }
        else if (type == 4 || type == 5) {
            if (type == 4) {
                weiboFrom.setText("发自：" + version + "彩票投注");
            }
            else if (type == 5) {
                weiboFrom.setText("发自：" + version + "中奖转发");
            }
            newsTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    submitStatisticsGardenJump(context, "bet");
                    // 投注跳转
                    BasicWeibo.betJump(context, title, preview);
                }
            });
        }
        // add by vincnet
        else if (type == 6) {
            gameList = new ArrayList<GameDownloadInf>();
            SharedPreferences preferences = context.getSharedPreferences("user", 0);
            String gameListJson = preferences.getString("game_list", null);
            if (gameListJson != null) {
                JsonUtil.analyseGameListData(gameList, gameListJson);
            }

            boolean isMatch = false;
            for (int i = 0; i < gameList.size(); i++) {
                if (title.equals(gameList.get(i).getGameIndex())) {
                    gameName = gameList.get(i).getGameName();
                    position = i;
                    isMatch = true;
                    break;
                }
            }
            StringBuilder str = new StringBuilder();
            if (isMatch) {
                str.append("<font color='#000000'>" + "发自：" + "</font>" + "<font color='#0000FF'><u>" +
                    gameName + "游戏" + "</u></font>");
                final int fposition = position;
                weiboFrom.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goGame(fposition, context);
                    }
                });
                newsTitle.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goGame(fposition, context);
                    }
                });
            }
            else {
                str.append("<font color='#000000'>" + "发自：未知游戏" + "</font>");
            }
            weiboFrom.setText(Html.fromHtml(str.toString()));
        }
        else if (type == 7) {
            weiboFrom.setText("发自：" + version + "彩票投注");
            newsTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!LotteryUtils.getPid(context).equals("101201")) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("program_id", title.split("\\|")[1]);
                        intent.putExtras(bundle);
                        intent.setClass(context, UniteHallDetail.class);
                        context.startActivity(intent);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                               R.anim.push_to_right_out);
                        }
                    }
                    else {
                        Toast.makeText(context, "该版本暂不支持合买", 1000).show();
                    }
                }
            });
        }
        else if (type == 8) {
            weiboFrom.setText("发自：" + version + "竞猜");
            newsTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (HttpConnectUtil.isNetworkAvailable(context)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("schema_id", title);
                        intent.putExtras(bundle);
                        intent.setClass(context, GuessParty.class);
                        context.startActivity(intent);
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                            (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                               R.anim.push_to_right_out);
                        }
                    }
                }
            });
        }
        else {
            weiboFrom.setText("发自：未知类别");
            newsTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    submitStatisticsGardenJump(context, "not_support");
                    Toast.makeText(context, "版本暂不支持该类别", 1000).show();
                }
            });
        }
    }

    /**
     * 跳转到游戏
     * 
     * @param currentPostion 指定游戏序号
     */
    private static void goGame(int currentPostion, Context context) {
        if (PluginUtils.checkGameExist(context, gameList.get(currentPostion).getGamePackageName())) {
            LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
            Bundle bundle = new Bundle();
            bundle.putString("pid", LotteryUtils.getPid(context));
            bundle.putString("key", LotteryUtils.getKey(context));
            bundle.putString("phone", appState.getUsername());
            bundle.putString("time", appState.getTime());
            bundle.putString("sessionid", appState.getSessionid());
            PluginUtils.goPlugin(context, bundle, gameList.get(currentPostion).getGamePackageName(),
                                 gameList.get(currentPostion).getGameActivityName());
        }
        else {
            PluginUtils.showPluginDownloadDialog(context, gameList.get(currentPostion).getGameName(), "确定下载" +
                                                     gameList.get(currentPostion).getGameName() + "?",
                                                 gameList.get(currentPostion).getGameDownloadUrl(), false);
        }

    }

    /**
     * 显示用户资料消息数
     * 
     * @param context Activity
     * @param promptLy 消息提现Layout
     * @param allProfileCount 用于显示消息数目
     */
    public static void showAllProfileCount(Context context, RelativeLayout promptLy, TextView allProfileCount) {
        LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
        if (appState.getUsername() != null) {
            if (appState.getAllProfileCount() > 0) {
                if (promptLy != null) {
                    promptLy.setVisibility(View.VISIBLE);
                }
                allProfileCount.setText("新");
                allProfileCount.setVisibility(View.VISIBLE);
            }
            else {
                if (promptLy != null) {
                    promptLy.setVisibility(View.GONE);
                }
                allProfileCount.setVisibility(View.GONE);
            }
        }
        else {
            if (promptLy != null) {
                promptLy.setVisibility(View.GONE);
            }
            allProfileCount.setVisibility(View.GONE);
        }
    }

    public static String updateWeiboProfileStatus(Context context) {
        LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
        if (appState.getUsername() != null) {
            if (appState.getAllProfileCount() > 0) {
                return "|新";
            }
            else {
                return "";
            }
        }
        else {
            return "";
        }
    }

    /**
     * 将日期从String转为Data类型
     * 
     * @param adateStrteStr 时间
     * @param format 时间格式
     */
    public static Date stringDate(String adateStrteStr, String format) {
        java.util.Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            date = simpleDateFormat.parse(adateStrteStr);
        }
        catch (Exception ex) {

        }
        return date;
    }

    /**
     * 存入手机存储空间中
     * 
     * @param bitName 图片名字
     * @param mBitmap 需要保存的图片
     */
    public static void saveMyBitmap(String bitName, Bitmap mBitmap)
        throws IOException {
        File f = new File("/sdcard/" + bitName + ".png");
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 财园点击昵称跳转
    public static void contextLink(TextView v) {
        v.setAutoLinkMask(0);
        Pattern pattern = Pattern.compile("@(\\w+?)(?=\\W|$)");
        String scheme = String.format("%s/?%s=", SCHEMANAME, PARAM_UID);
        Linkify.addLinks(v, pattern, scheme, null, new TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return match.group(1);
            }
        });

        Pattern pattern1 = Pattern.compile("#(\\w+?)#");
        String scheme1 = String.format("%s/?%s=", SCHEMATOPIC, PARAM_UID);
        Linkify.addLinks(v, pattern1, scheme1, null, new TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return match.group(1);
            }
        });

    }

    // 如果包含"投注位置"则显示位置图标
    public static void locationPicShow(String contentStr, ImageView locationPic) {
        if (contentStr.indexOf("投注位置") != -1) {
            locationPic.setVisibility(View.VISIBLE);
        }
        else {
            locationPic.setVisibility(View.GONE);
        }
    }

    // 如果包含"合买"，则显示合买图标
    public static void unitePicShow(String contentStr, ImageView unitePic) {
        if (contentStr.indexOf("合买") != -1) {
            unitePic.setVisibility(View.VISIBLE);
        }
        else {
            unitePic.setVisibility(View.GONE);
        }
    }

    /**
     * 返回ListView顶部
     * 
     * @param view Listview
     */
    public static void backTop(ListView view) {
        if (!view.isStackFromBottom()) {
            view.setStackFromBottom(true);
        }
        view.setStackFromBottom(false);
    }

    /**
     * 返回ListView顶部图标显示
     * 
     * @param view Listview
     * @param imageButton 返回顶部图标
     */
    public static void backTopShow(MyListView listview, final ImageButton imageButton) {

        listview.setChanged(new ListViewScrollStateChanged() {

            @Override
            public void onScrollStateChangedView(int VisiblePosition) {
                if (VisiblePosition > 3) {
                    imageButton.setVisibility(View.VISIBLE);
                }
                else {
                    imageButton.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * ListView搜索关键字高亮显示
     * 
     * @param textview 用来显示内容
     * @param content 内容
     * @param keyWords 搜索关键字
     */
    public static void hightKeyWords(Context context, TextView textview, String content, String keyWords) {
        View number = textview;
        if (number instanceof TextView) {
            TextView number_tv = (TextView) number;
            number_tv = (TextView) number;
            String number_temp = "";
            number_temp = content;

            String input = keyWords;

            if (number_temp.contains(input)) {

                // Methods 1
                int index = number_temp.indexOf(input);
                int len = input.length();
                Spanned temp =
                    Html.fromHtml(number_temp.substring(0, index) + "<font color=#FF0000>" +
                        number_temp.substring(index, index + len) + "</font>" +
                        number_temp.substring(index + len, number_temp.length()));

                // 方法 2
                /*
                 * int start = number.indexOf(input); SpannableStringBuilder style=new
                 * SpannableStringBuilder(number); style.setSpan(new Tex(Color.RED), start, start +
                 * input.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                 */
                number_tv.setText(TextUtil.formatContent(temp, context));
            }
        }
    }

    /**
     * 把图片变成圆角
     * 
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // Activity跳转动画
    protected static void Jump() {
// ((Activity) context).overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

}
