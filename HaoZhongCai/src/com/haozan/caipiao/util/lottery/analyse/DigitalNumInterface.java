package com.haozan.caipiao.util.lottery.analyse;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * 投注彩种格式操作类
 * 
 * @author peter_wang
 * @create-time 2013-11-1 下午3:53:50
 */
public interface DigitalNumInterface {
    /**
     * 解析号码
     * 
     * @param codes
     * @return
     */
    public String[] analyseBetCode(String codes);

    /**
     * 解析号码,匹配开奖号码
     * 
     * @param codes
     * @return
     */
    public String[] analyseBetCode(String codes, String openNum);

    /**
     * 解析号码并生成显示的views
     * 
     * @param orderLayout
     * @param codes
     */
    public void showLotteryOrderViews(Context context, LinearLayout orderLayout, String codes);

    /**
     * 解析号码并生成显示的views,同时匹配开奖号码
     * 
     * @param context
     * @param orderLayout
     * @param codes
     * @param openNum
     */
    public void showLotteryOrderViews(Context context, LinearLayout orderLayout, String codes, String openNum);
}