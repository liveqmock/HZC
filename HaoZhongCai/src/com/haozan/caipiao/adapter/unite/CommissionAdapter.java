package com.haozan.caipiao.adapter.unite;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class CommissionAdapter
    extends BaseAdapter {
    private Context context;
    private String[] list;
    private LayoutInflater inflater;
    private int pos;
    private String type;

    public CommissionAdapter(Context context, String[] list, int pos, String type) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(this.context);
        this.pos = pos;
        this.type = type;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if(type.equals("unite_commissin")){
                convertView = inflater.inflate(R.layout.unite_grid_view_select_item, null);
            }
            else if(type.equals("bet_tools")){
                convertView = inflater.inflate(R.layout.bettools_grid_view_select_item, null);
            }
            viewHolder.button = (TextView) convertView.findViewById(R.id.unite_grid_view_item_click);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position == pos){
            viewHolder.button.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            viewHolder.button.setTextColor(Color.WHITE);
        }
        viewHolder.button.setText(list[position]);
        
        return convertView;
    }
    private final class ViewHolder{
        public TextView button;
    }

}
