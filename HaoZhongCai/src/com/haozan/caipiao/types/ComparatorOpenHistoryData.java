package com.haozan.caipiao.types;

import java.util.Comparator;

public class ComparatorOpenHistoryData
    implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        OpenHistoryRequest ophr0 = (OpenHistoryRequest) arg0;
        OpenHistoryRequest ophr1 = (OpenHistoryRequest) arg1;
        int flag = ophr1.getType().compareTo(ophr0.getType());
        return flag;
    }
}
