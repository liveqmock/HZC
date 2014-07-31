package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.ConmentListData;

public class ConmentsAdapter
    extends BaseAdapter {
    private ArrayList<ConmentListData> conments;
    private Context context;
    private LayoutInflater inflater;

    public ConmentsAdapter(Context context, ArrayList<ConmentListData> conments) {
        super();
        this.context = context;
        this.conments = conments;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ConmentListData con = conments.get(position);
        convertView = inflater.inflate(R.layout.weibo_conments_item, null);

        TextView niceName = (TextView) convertView.findViewById(R.id.niceName);
        TextView content = (TextView) convertView.findViewById(R.id.tvItemContent);
        TextView creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
        String name = con.getName();
        if (name == null) {
            niceName.setText("匿名彩友");
        }
        else {
            if (con.getName().equals("null") || con.getName().equals("")) {
                niceName.setText("匿名彩友");
            }
            else {
                niceName.setText(con.getName());
            }
        }
        content.setText(con.getContent());
        creattime.setText(con.getTime());
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return conments.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return conments.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
