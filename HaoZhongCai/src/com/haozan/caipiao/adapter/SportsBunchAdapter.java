package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.BunchInf;

public class SportsBunchAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<BunchInf> bunchInfList;

    public final class ViewHolder {
        public TextView button;
        public ImageView img;
    }

    public SportsBunchAdapter(Context context, ArrayList<BunchInf> bunchInfList) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.bunchInfList = bunchInfList;
    }

    @Override
    public int getCount() {
        return bunchInfList.size();
    }

    @Override
    public Object getItem(int position) {
        return bunchInfList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_view_select_item, null);
            viewHolder = new ViewHolder();
            viewHolder.button = (TextView) convertView.findViewById(R.id.grid_view_item_click);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.button.setText(" " + bunchInfList.get(position).getName());
        if (bunchInfList.get(position).isChoose()) {
            viewHolder.button.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            viewHolder.img.setVisibility(View.VISIBLE);
            viewHolder.button.setTextColor(context.getResources().getColor(R.color.white));
        }
        else {
            viewHolder.button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.img.setVisibility(View.GONE);
            viewHolder.button.setTextColor(context.getResources().getColor(R.color.dark_purple));
        }
        return convertView;
    }
}
