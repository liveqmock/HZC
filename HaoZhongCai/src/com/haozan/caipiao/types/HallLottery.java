package com.haozan.caipiao.types;

import com.haozan.caipiao.util.LotteryUtils;

/**
 * 大厅增减彩种item
 * 
 * @author peter_feng
 * @create-time 2013-5-22 下午4:50:02
 */
public class HallLottery {
    private String lotteryEnglishName;
    private String lotteryChineseName;

    public String getLotteryEnglishName() {
        return lotteryEnglishName;
    }

    public void setLotteryEnglishName(String lotteryEnglishName) {
        this.lotteryEnglishName = lotteryEnglishName;
        this.lotteryChineseName = LotteryUtils.getLotteryName(lotteryEnglishName);
    }

    public String getLotteryChineseName() {
        return lotteryChineseName;
    }
}