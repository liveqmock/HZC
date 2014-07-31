package com.haozan.caipiao.types;

/**
 * 竞技足球存储每一个串的选择信息
 * 
 * @author peter_feng
 */
public class BunchInf {
    private String name;
    private boolean choose;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public void reverseChoose() {
        if (choose) {
            choose = false;
        }
        else {
            choose = true;
        }
    }
}