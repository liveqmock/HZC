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
import com.haozan.caipiao.types.userinf.WithdrawHistoryData;
import com.haozan.caipiao.util.TextUtil;

public class WithdrawHistoryAdapter
    extends BaseAdapter {
    private ArrayList<WithdrawHistoryData> withdrawHistoryList;
    private Context context;
    private LayoutInflater inflater;

    public WithdrawHistoryAdapter(Context context, ArrayList<WithdrawHistoryData> withdrawHistoryList) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.withdrawHistoryList = withdrawHistoryList;
    }

    public final class ViewHolder {
        private TextView year;
        private TextView month;
        private LinearLayout leftLayout;
        private TextView bank;
        private TextView money;
        private TextView status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.withdraw_history_adapter_item, null);
            viewHolder.year = (TextView) convertView.findViewById(R.id.withdraw_year);
            viewHolder.month = (TextView) convertView.findViewById(R.id.withdraw_month);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.list_left_layout);
            viewHolder.bank = (TextView) convertView.findViewById(R.id.withdraw_bank);
            viewHolder.money = (TextView) convertView.findViewById(R.id.withdraw_money);
            viewHolder.status = (TextView) convertView.findViewById(R.id.withdraw_status);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WithdrawHistoryData withdrawData = withdrawHistoryList.get(position);
        String statusFlag = withdrawHistoryList.get(position).getStatus();
        String statusInf = null;
        if (statusFlag.equals("0")) {
            statusInf = "提现成功";
        }
        else if (statusFlag.equals("9") || statusFlag.equals("3")) {
            statusInf = "处理中";
            TextUtil.setTextBold(viewHolder.status);
        }
        else if (statusFlag.equals("4")) {
            statusInf = "提现失败";
        }
        viewHolder.status.setText(statusInf);
        if (withdrawData.isShowLine()) {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg_with_line);
        }
        else {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg);
        }
        if (withdrawData.isShowDate()) {
            viewHolder.year.setText(withdrawData.getYear() + "年");
            viewHolder.month.setText(withdrawData.getMonth() + "月");
        }
        else {
            viewHolder.year.setText("");
            viewHolder.month.setText("");
        }
        viewHolder.bank.setText(withdrawData.getDay() + "日" + withdrawData.getTime() + withdrawData.getBank());
        viewHolder.money.setText("￥ " + withdrawData.getMoney());
        return convertView;
    }

    @Override
    public int getCount() {
        return withdrawHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return withdrawHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
