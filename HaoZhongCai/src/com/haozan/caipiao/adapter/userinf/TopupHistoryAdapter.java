package com.haozan.caipiao.adapter.userinf;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.userinf.TopupHistoryData;

public class TopupHistoryAdapter
    extends BaseAdapter {
    private ArrayList<TopupHistoryData> topupHistoryList;
    private Context context;
    private LayoutInflater inflater;

    public TopupHistoryAdapter(Context context, ArrayList<TopupHistoryData> topupHistoryList) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.topupHistoryList = topupHistoryList;
    }

    public final class ViewHolder {
        private TextView topupYear;
        private TextView topupMonth;
        private LinearLayout leftLayout;
        private TextView topupWay;
        private TextView topupMoney;
        private TextView topupInf;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        TopupHistoryData topupData = topupHistoryList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.topup_history_adapter_item, null);
            viewHolder.topupYear = (TextView) convertView.findViewById(R.id.topup_year);
            viewHolder.topupMonth = (TextView) convertView.findViewById(R.id.topup_month);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.list_left_layout);
            viewHolder.topupWay = (TextView) convertView.findViewById(R.id.topup_way);
            viewHolder.topupMoney = (TextView) convertView.findViewById(R.id.topup_money);
            viewHolder.topupInf = (TextView) convertView.findViewById(R.id.topup_inf);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String topupWay = topupData.getWay();
        if (topupData.isShowLine()) {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg_with_line);
        }
        else {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg);
        }
        if (topupData.isShowDate()) {
            viewHolder.topupYear.setText(topupData.getYear() + "年");
            viewHolder.topupMonth.setText(topupData.getMonth() + "月");
        }
        else {
            viewHolder.topupYear.setText("");
            viewHolder.topupMonth.setText("");
        }
        viewHolder.topupWay.setText(topupWay);
        viewHolder.topupMoney.setText("￥" + topupData.getMoney());
        viewHolder.topupInf.setText(topupData.getDay() + "日" + topupData.getTime() + topupData.getInf());
        return convertView;
    }

    @Override
    public int getCount() {
        return topupHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return topupHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
