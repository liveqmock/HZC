package com.haozan.caipiao.types.topup;

/**
 * 银行信息
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午1:29:32
 */
public class BankInfo
    implements Cloneable {
    private String chinesename;// 中文名
    private String key;// 快捷查询关键字 如：gh - 工商银行
    private String deposit;// 储蓄卡支持方式
    private String credit;// 信用卡支持方式
    private String firstchar;// 拼音首字母，搜索中排列分组使用，比如a放在一组中
    private String shorthand;// 拼音简写
    private int iconResource = -1;// icon图标，-1代表没有图标

    public String getChinesename() {
        return chinesename;
    }

    public void setChinesename(String chinesename) {
        this.chinesename = chinesename;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getFirstchar() {
        return firstchar;
    }

    public void setFirstchar(String firstchar) {
        this.firstchar = firstchar;
    }

    public String getShorthand() {
        return shorthand;
    }

    public void setShorthand(String shorthand) {
        this.shorthand = shorthand;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    @Override
    public Object clone() {
        BankInfo stu = null;
        try {
            stu = (BankInfo) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
