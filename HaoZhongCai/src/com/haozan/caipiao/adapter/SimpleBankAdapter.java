package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class SimpleBankAdapter
    extends BaseAdapter {
    private ArrayList<String> array;
    private Context context;

    public SimpleBankAdapter(Context context, ArrayList<String> array) {
        super();
        this.context = context;
        this.array = array;
    }

    private static class ViewHolder {
        private TextView bankName;
        private LinearLayout fatherLin;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bank_name_item, null);
            viewHolder = new ViewHolder();
            viewHolder.bankName = (TextView) convertView.findViewById(R.id.tv);
            viewHolder.fatherLin = (LinearLayout) convertView.findViewById(R.id.father_lin);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if (position == 0) {
//            viewHolder.fatherLin.setBackgroundResource(R.drawable.five_tab);
//            if (array.size() == 1) {
//                viewHolder.fatherLin.setBackgroundResource(R.drawable.list_single_item);
//            }
//        }
//        else if (position == array.size() - 1) {
//            viewHolder.fatherLin.setBackgroundResource(R.drawable.six_tab);
//        }
//        else {
//            viewHolder.fatherLin.setBackgroundResource(R.drawable.four_tab);
//        }

        viewHolder.bankName.setText(array.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
