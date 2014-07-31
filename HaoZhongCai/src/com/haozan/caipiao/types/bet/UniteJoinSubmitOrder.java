package com.haozan.caipiao.types.bet;

/**
 * 合买跟单
 * 
 * @author peter_wang
 * @create-time 2013-10-31 下午10:18:31
 */
public class UniteJoinSubmitOrder {
    // 方案编号
    private String programId;
    // 参与份数
    private int joinNum;
    // 发起人id
    private boolean isShare;
    // 地理位置：经度
    private double longitude;
    // 地理位置：纬度
    private double latitude;
    // 分享内容
    private String shareContent;
    // 地理位置
    private String location;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean isShare) {
        this.isShare = isShare;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
