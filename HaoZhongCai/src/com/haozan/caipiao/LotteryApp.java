package com.haozan.caipiao;

import android.app.Application;

public class LotteryApp
    extends Application {

    // the number of the message
    private String nickname;
    private String email;
    private String birthdday_url;
    private String birthdday_text;
    private double winBalance;
    // available money get from lottery account
    private double availableBalance;
    // the sessionid from server
    private String sessionid;
    // the user name
    private String username;
    // the server time the user first open the software
    private String time;
    // whether the user have perfect information
    private int perfectInf;
    // reserved phonenum
    private String reservedPhone;
    // registe type
    private String registerType;// 1.正常注册；2.sina；3.qq; 4.支付宝
    // the account of the user
    private double account;

    // the number of the message
    private int messageNumber;
    private Boolean willShake;
    private Boolean lockShake;
    private int buttonBackGround;
    private int score;
    private int band;
    private String collectedPoint;
    private String userid;
    private String service;
    private String betAddress;
    private int allProfileCount;
    // sina
    // 用户Id
    private String userID;
    // accessToken
    private String accessToken;
    // accessSecret
    private String accessSecret;

    // 盛付通要用到的，其他版本不需要
    private String name;
    private String shenfenId;
    private String lastLogin;

    // 个推id
    private String gexinId;

    public String getWeiboUserID() {
        return userID;
    }

    public void setWeiboUserID(String userID) {
        this.userID = userID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReservedPhone() {
        return reservedPhone;
    }

    public void setReservedPhone(String reservedPhone) {
        this.reservedPhone = reservedPhone;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public Boolean getWillShake() {
        return willShake;
    }

    public void setWillShake(Boolean willShake) {
        this.willShake = willShake;
    }

    public Boolean getLockShake() {
        return lockShake;
    }

    public void setLockShake(Boolean lockShake) {
        this.lockShake = lockShake;
    }

    public String getBirthday() {
        return birthdday_url;
    }

    public void setBirthday(String birthdday_url) {
        this.birthdday_url = birthdday_url;
    }

    public String getBirthdayText() {
        return birthdday_text;
    }

    public void setBirthdayText(String birthdday_text) {
        this.birthdday_text = birthdday_text;
    }

    public int getButtonBackGround() {
        return buttonBackGround;
    }

    public void setButtonGround(int buttonBackGround) {
        this.buttonBackGround = buttonBackGround;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBand() {
        return band;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setBand(int band) {
        this.band = band;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPointed() {
        return collectedPoint;
    }

    public void setPointed(String collectedPoint) {
        this.collectedPoint = collectedPoint;
    }

    public String getServiceed() {
        return service;
    }

    public void setServiced(String service) {
        this.service = service;
    }

    public String getBetAddress() {
        return betAddress;
    }

    public void setBetAddress(String betAddress) {
        this.betAddress = betAddress;
    }

    public int getAllProfileCount() {
        return allProfileCount;
    }

    public void setAllProfileCount(int allProfileCount) {
        this.allProfileCount = allProfileCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShenfenId() {
        return shenfenId;
    }

    public void setShenfenId(String shenfenId) {
        this.shenfenId = shenfenId;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getGexinId() {
        return gexinId;
    }

    public void setGexinId(String gexinId) {
        this.gexinId = gexinId;
    }

    public String getBirthdday_url() {
        return birthdday_url;
    }

    public void setBirthdday_url(String birthdday_url) {
        this.birthdday_url = birthdday_url;
    }

    public String getBirthdday_text() {
        return birthdday_text;
    }

    public void setBirthdday_text(String birthdday_text) {
        this.birthdday_text = birthdday_text;
    }

    public double getWinBalance() {
        return winBalance;
    }

    public void setWinBalance(double winBalance) {
        this.winBalance = winBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public int getPerfectInf() {
        return perfectInf;
    }

    public void setPerfectInf(int perfectInf) {
        this.perfectInf = perfectInf;
    }

    public String getCollectedPoint() {
        return collectedPoint;
    }

    public void setCollectedPoint(String collectedPoint) {
        this.collectedPoint = collectedPoint;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setButtonBackGround(int buttonBackGround) {
        this.buttonBackGround = buttonBackGround;
    }

    public double getAccount() {
        return account;
    }

    public void setAccount(double account) {
        this.account = account;
    }
}
