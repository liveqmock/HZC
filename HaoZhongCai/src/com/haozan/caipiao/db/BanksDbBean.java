package com.haozan.caipiao.db;

public class BanksDbBean {
    static final String ID = "_id";
    static final String FIRSTCHAR="fc"; //拼音首字母
    static final String CHINESENAME = "cn"; //中文名
    static final String KEY = "ke"; //快捷查询关键字 如：gh - 工商银行
    static final String DEPOSIT = "deposit"; //储蓄卡支持方式
    static final String CREDIT = "credit"; //信用卡支持方式
    static final String LASTUSE = "lastuse";//上次是否使用 1:使用  0:未使用
    static final String RECHARGETYPE = "rechargetype";//上次使用的充值方式 方式代码，如"AP" 支付宝
}
