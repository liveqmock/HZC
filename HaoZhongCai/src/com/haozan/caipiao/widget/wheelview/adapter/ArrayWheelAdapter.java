package com.haozan.caipiao.widget.wheelview.adapter;

import android.content.Context;

public class ArrayWheelAdapter<T>
    extends WheelTextAdapterBase {

    private T items[];

    public ArrayWheelAdapter(Context context, T items[]) {
        super(context);

        this.items = items;
    }

    public CharSequence getItemText(int index) {
        if (index >= 0 && index < items.length) {
            T item = items[index];
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    public int getItemsCount() {
        return items.length;
    }
}
