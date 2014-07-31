package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.JLKSItem;

public class JLKSGridViewAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<JLKSItem> list;
    private LayoutInflater inflater;

    public JLKSGridViewAdapter(Context context, ArrayList<JLKSItem> list) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        JLKSItem item = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.jlks_grid_view_item, null);
            viewHolder.upTv = (TextView) convertView.findViewById(R.id.up_tv);
            viewHolder.downTv = (TextView) convertView.findViewById(R.id.down_tv);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if ("" != item.getDownStr()) {
            viewHolder.downTv.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.downTv.setVisibility(View.GONE);
        }

        if (item.isIfSelected()) {
            convertView.setBackgroundResource(R.drawable.btn_k3_bet_dice_pressed);
            viewHolder.upTv.setTextColor(context.getResources().getColor(R.color.dark_purple));
            if ("" != item.getDownStr())
                viewHolder.downTv.setTextColor(context.getResources().getColor(R.color.dark_purple));
        }
        else {
            convertView.setBackgroundResource(R.drawable.btn_k3_bet_dice);
            viewHolder.upTv.setTextColor(context.getResources().getColor(R.color.white));
            if ("" != item.getDownStr())
                viewHolder.downTv.setTextColor(context.getResources().getColor(R.color.white_gray));
        }
        viewHolder.upTv.setText(list.get(position).getUpStr());
        if ("" != item.getDownStr())
            viewHolder.downTv.setText(list.get(position).getDownStr());
        return convertView;
    }

    private final class ViewHolder {
        public TextView upTv;
        public TextView downTv;
    }

}
