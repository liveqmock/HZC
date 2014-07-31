package com.haozan.caipiao.types;

/**
 * 竞技足球存储每一个场比赛被选择的球队的高低赔率
 * 
 * @author peter_feng
 */
public class OddsInf {
    // 在比赛中的比赛场次，比如第1场，index=1
    private int index;
    private float highOdds;
    private float lowOdds;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getHighOdds() {
        return highOdds;
    }

    public void setHighOdds(float highOdds) {
        this.highOdds = highOdds;
    }

    public float getLowOdds() {
        return lowOdds;
    }

    public void setLowOdds(float lowOdds) {
        this.lowOdds = lowOdds;
    }
}