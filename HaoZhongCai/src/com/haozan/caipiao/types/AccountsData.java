package com.haozan.caipiao.types;


public class AccountsData {

    private String account;// 帐号
    private String password;// 密码
    private int status;// 状态（是否保存密码） 0：不保存；1：保存

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
