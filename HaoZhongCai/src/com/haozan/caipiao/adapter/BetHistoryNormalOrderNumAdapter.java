package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.BetHistoryOrderNum;

public class BetHistoryNormalOrderNumAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<BetHistoryOrderNum> orderNumList;

    public BetHistoryNormalOrderNumAdapter(Context context, ArrayList<BetHistoryOrderNum> orderNumList) {
        this.context = context;
        this.orderNumList = orderNumList;
    }

    public final class ViewHolder {
        // 投注玩法
        private TextView orderBetWay;
        // 投注号码
        private TextView orderBetNum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.listitem_bet_history_order_num, null);
            viewHolder = new ViewHolder();
            viewHolder.orderBetWay = (TextView) convertView.findViewById(R.id.way);
            viewHolder.orderBetNum = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.orderBetWay.setText(orderNumList.get(position).getWay());
        viewHolder.orderBetNum.setText(orderNumList.get(position).getNum());
        return convertView;
    }

    @Override
    public int getCount() {
        return orderNumList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderNumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
