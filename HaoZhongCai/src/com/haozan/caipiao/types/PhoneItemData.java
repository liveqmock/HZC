package com.haozan.caipiao.types;

public class PhoneItemData {
    private String name;
    private String[] phonenums = new String[10];
    private boolean[] ifChecked = new boolean[10];

    public boolean[] getIfChecked() {
        return ifChecked;
    }

    public void setIfChecked(boolean[] ifChecked) {
        this.ifChecked = ifChecked;
    }

    public boolean getIfChecked(int index) {
        return ifChecked[index];
    }

    public void setIfChecked(int index, boolean ifChecked) {
        this.ifChecked[index] = ifChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPhonenums() {
        return phonenums;
    }

    public void setPhonenums(String[] phonenums) {
        this.phonenums = phonenums;
    }

    public String getPhonenums(int index) {
        return phonenums[index];
    }

    public void setPhonenums(int index, String phonenums) {
        this.phonenums[index] = phonenums;
    }
}
