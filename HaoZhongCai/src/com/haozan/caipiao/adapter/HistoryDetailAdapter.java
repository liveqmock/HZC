package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.HistoryListItemData;
import com.haozan.caipiao.util.StringUtil;

public class HistoryDetailAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<HistoryListItemData> historyList;
    private LayoutInflater inflater;

    public HistoryDetailAdapter(Context context, ArrayList<HistoryListItemData> historyList) {
        this.context = context;
        this.historyList = historyList;
        inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView name;
        private TextView num;
        private TextView money;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return historyList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_lottery_history_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initListView(viewHolder, index);
        return convertView;
    }

    private void initListView(ViewHolder viewHolder, int index) {
        viewHolder.name.setText(historyList.get(index).getAwardName());
        viewHolder.num.setText(StringUtil.divideLongNum(historyList.get(index).getAwardNum()));
        viewHolder.money.setText(StringUtil.divideLongNum(historyList.get(index).getAwardMoney()));
    }
}