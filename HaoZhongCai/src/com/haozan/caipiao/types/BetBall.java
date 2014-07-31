package com.haozan.caipiao.types;

public class BetBall {
    // 球id
    private int id;
    // 是否选中
    private boolean choosed = false;
    // 球显示的文字
    private String content;
    // 点击球弹出显示的信息
    private String ballsInf;
    // 号码分析数据
    private String ballAnalase;
    // 号码遗漏数据
    private String ballOmit;
    // 球是否可点击
    private boolean isEnabled = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBallsInf() {
        return ballsInf;
    }

    public void setBallsInf(String ballsInf) {
        this.ballsInf = ballsInf;
    }

    public String getBallAnalase() {
        return ballAnalase;
    }

    public void setBallAnalase(String ballAnalase) {
        this.ballAnalase = ballAnalase;
    }

    public String getBallOmit() {
        return ballOmit;
    }

    public void setBallOmit(String ballOmit) {
        this.ballOmit = ballOmit;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
