package com.haozan.caipiao.types;

import java.util.ArrayList;

public class JczqAnalyseGroup {
    private String groupName;
    private int dataType;
    private ArrayList<JczqAnalyseListItemData> arrayList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public ArrayList<JczqAnalyseListItemData> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<JczqAnalyseListItemData> arrayList) {
        this.arrayList = arrayList;
    }

}
