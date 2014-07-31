package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ViewFlowAdapter
    extends BaseAdapter {
    private ArrayList<View> layouts;

    public ViewFlowAdapter(Context context, ArrayList<View> layouts) {
        this.layouts = layouts;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return layouts.size();
    }

    @Override
    public int getCount() {
        return layouts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int view = getItemViewType(position);
        if (convertView == null) {
            for (int i = 0; i < layouts.size(); i++) {
                if (view == i) {
                    convertView = layouts.get(i);
                    break;
                }
            }

// switch (view) {
// case VIEW1:
// convertView = mInflater.inflate(layout_id_1, null);
// break;
// case VIEW2:
// convertView = mInflater.inflate(layout_id_2, null);
// break;
// }
        }
        return convertView;
    }

}
