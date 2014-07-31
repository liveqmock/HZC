package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.haozan.caipiao.types.Category;
import com.haozan.caipiao.util.Logger;

public abstract class CategoryAdapter
    extends BaseAdapter {
    public static String TAG = "CategoryAdapter";
    private List<Category> categories = new ArrayList<Category>();

    public void addCategory(String title, Adapter adapter) {
        categories.add(new Category(title, adapter));

        Logger.inf(TAG, "addCategory");
    }

    @Override
    public int getCount() {
        int total = 0;

        for (Category category : categories) {
            total += category.getAdapter().getCount() + 1;
        }

        Logger.inf(TAG, "getCount:" + total);

        return total;
    }

    @Override
    public Object getItem(int position) {
        for (Category category : categories) {
            if (position == 0) {
                Logger.inf(TAG, "getItem 0");
                return category;
            }

            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                Logger.inf(TAG, "getItem:" + category.getAdapter().getItem(position - 1));
                return category.getAdapter().getItem(position - 1);
            }
            position -= size;
        }
        Logger.inf(TAG, "getItem null");
        return null;
    }

    @Override
    public long getItemId(int position) {
        Logger.inf(TAG, "getItemId:" + position);
        return position;
    }

    public int getViewTypeCount() {
        int total = 1;

        for (Category category : categories) {
            total += category.getAdapter().getViewTypeCount();
        }

        Logger.inf(TAG, "getViewTypeCount:" + total);

        return total;
    }

    public int getItemViewType(int position) {
        int typeOffset = 1;

        for (Category category : categories) {
            if (position == 0) {
                Logger.inf(TAG, "getItemViewType 0");
                return 0;
            }

            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                Logger.inf(TAG,
                      "getItemViewType : " + typeOffset + category.getAdapter().getItemViewType(position - 1));
                return typeOffset + category.getAdapter().getItemViewType(position - 1);
            }
            position -= size;

            typeOffset += category.getAdapter().getViewTypeCount();
        }
        Logger.inf(TAG, "getItemViewType -1");
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int categoryIndex = 0;

        for (Category category : categories) {
            if (position == 0) {
                Logger.inf(TAG, "getView 0");
                return getTitleView(category.getTitle(), categoryIndex, convertView, parent);
            }
            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                Logger.inf(TAG, "getView : " + position);
                return category.getAdapter().getView(position - 1, convertView, parent);
            }
            position -= size;

            categoryIndex++;
        }

        Logger.inf(TAG, "getView null");
        return null;
    }

    protected abstract View getTitleView(String caption, int index, View convertView, ViewGroup parent);

}
