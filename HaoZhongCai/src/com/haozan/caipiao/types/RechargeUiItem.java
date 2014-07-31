package com.haozan.caipiao.types;

/**
 * 充值列表元素
 * 
 * @author peter_wang
 * @create-time 2013-11-10 上午11:44:26
 */
public class RechargeUiItem {
    private int icon;
    private String name;
    private String description;
    private boolean isEmphasis;
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEmphasis() {
        return isEmphasis;
    }

    public void setEmphasis(boolean isEmphasis) {
        this.isEmphasis = isEmphasis;
    }

}
