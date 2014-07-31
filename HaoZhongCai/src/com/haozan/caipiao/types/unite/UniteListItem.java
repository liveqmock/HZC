package com.haozan.caipiao.types.unite;

import android.graphics.drawable.BitmapDrawable;

public class UniteListItem
    extends UniteBaseItem {
    // 级别
    private String grade;
    // 发自
    private String uniteFrom;
    // subcontent isShown
    private boolean isShown = false;
    private String tempAmount;
    private String tempRb = "1";
    private BitmapDrawable progressBitmap;
    private String kind;
    private String progress;
    private String guarantee;

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTempRb() {
        return tempRb;
    }

    public void setTempRb(String tempRb) {
        this.tempRb = tempRb;
    }

    public String getTempAmount() {
        return tempAmount;
    }

    public void setTempAmount(String tempAmount) {
        this.tempAmount = tempAmount;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean isShown) {
        this.isShown = isShown;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUniteFrom() {
        return uniteFrom;
    }

    public void setUniteFrom(String uniteFrom) {
        this.uniteFrom = uniteFrom;
    }

    public BitmapDrawable getProgressBitmap() {
        return progressBitmap;
    }

    public void setProgressBitmap(BitmapDrawable progressBitmap) {
        this.progressBitmap = progressBitmap;
    }
}
