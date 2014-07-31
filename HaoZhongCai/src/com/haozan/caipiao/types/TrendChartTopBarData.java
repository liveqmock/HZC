package com.haozan.caipiao.types;

public class TrendChartTopBarData {
    // 颜色color的id值
    private int color;

    // 走势图顶部显示的文字
    private String[] trendTopText;

    // 是否选中相应的号码
    private boolean[] trendTopSelected;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String[] getTrendTopText() {
        return trendTopText;
    }

    public void setTrendTopText(String[] trendTopText) {
        this.trendTopText = trendTopText;
        trendTopSelected = new boolean[trendTopText.length];
    }

    public void selectedNum(int index, boolean selected) {
        trendTopSelected[index] = selected;
    }

}
