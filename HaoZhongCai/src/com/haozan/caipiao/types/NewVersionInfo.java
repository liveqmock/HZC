package com.haozan.caipiao.types;

/**
 * 检测更新新版本的信息
 * 
 * @author peter_wang
 * @create-time 2013-11-12 上午10:20:41
 */
public class NewVersionInfo {
    private String updateURL;
    private String updateWebURL;
    private String updateContent;
    private boolean updateForce;// 是否强制升级
    private String newVersionNum;// 新版本号
    private long size;// 安装包大小

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public String getUpdateWebURL() {
        return updateWebURL;
    }

    public void setUpdateWebURL(String updateWebURL) {
        this.updateWebURL = updateWebURL;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public boolean isUpdateForce() {
        return updateForce;
    }

    public void setUpdateForce(boolean updateForce) {
        this.updateForce = updateForce;
    }

    public String getNewVersionNum() {
        return newVersionNum;
    }

    public void setNewVersionNum(String newVersionNum) {
        this.newVersionNum = newVersionNum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
