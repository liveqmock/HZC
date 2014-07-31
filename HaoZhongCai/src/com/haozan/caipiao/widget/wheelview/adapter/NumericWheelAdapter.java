package com.haozan.caipiao.widget.wheelview.adapter;

import android.content.Context;

public class NumericWheelAdapter
    extends WheelTextAdapterBase {
    public static final int DEFAULT_MAX_VALUE = 9;
    private static final int DEFAULT_MIN_VALUE = 0;
    private int minValue;
    private int maxValue;
    private String format;

    public NumericWheelAdapter(Context context) {
        this(context, 0, 9);
    }

    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    public CharSequence getItemText(int index) {
        return String.valueOf(minValue + index);
    }

    public int getItemsCount() {
        return this.maxValue - this.minValue + 1;
    }

}