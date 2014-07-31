package com.haozan.caipiao.widget.wheelview.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DateNumericAdapter
    extends NumericWheelAdapter {
    int currentItem;
    int currentValue;

    public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
        super(context, minValue, maxValue);
        this.currentValue = current;
        setTextSize(16);
    }

    protected void configureTextView(TextView view) {
        super.configureTextView(view);
        if (currentItem == currentValue) {
            //设置选中字体颜色为红色
            view.setTextColor(0xFFcc0000);
        }
        view.setTypeface(Typeface.SANS_SERIF);
    }

    public View getItem(int index, View cachedView, ViewGroup parent) {
        currentItem = index;
        return super.getItem(index, cachedView, parent);
    }
}