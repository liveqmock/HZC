package com.haozan.caipiao.types;

import android.widget.ImageView;

public class Banner {
    private String imgUrl;
    private String actionType;// url app local
    private String url;// 第三方下载地址或者网页url
    private String title;
    private String description;
    private String appPackage;// 应用包名
    private String appClass;// 类名
    private ImageView bannerImg;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImageView getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(ImageView bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

}
