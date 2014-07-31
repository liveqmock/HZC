package com.haozan.caipiao.types;

import android.util.Log;
import android.widget.Adapter;

public class Category {
    public static String TAG = "Category";

    private String mTitle;

    private Adapter mAdapter;

    public Category(String title, Adapter adapter) {
        mTitle = title;
        mAdapter = adapter;
        Log.i(TAG, "init Category");
    }

    public void setTile(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public Adapter getAdapter() {
        return mAdapter;
    }
}
