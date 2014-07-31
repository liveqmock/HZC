package com.haozan.caipiao.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.CaiJinAccountDetailData;

public class UserDetailAdapter
    extends BaseAdapter {
    private ArrayList<CaiJinAccountDetailData> listArray;
    private Context context;
    private LayoutInflater inflater;

    public final class ViewHolder {
        private TextView caijinDetailInfPrize;
        private TextView caijinDetailInfStatus;
        private TextView caijinDetailInfReturnTime;
        private TextView caijinDetailInfUsedTime;
        private TextView caijinDetailInfExpiredTime;
        private TextView caijinDetailInfUsedMoney;
        private LinearLayout viewBgInner;
    }

    public UserDetailAdapter(Context context, ArrayList<CaiJinAccountDetailData> listArray) {
        this.context = context;
        this.listArray = listArray;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.caijin_detail_inf_adapter, null);

            viewHolder.caijinDetailInfPrize = (TextView) convertView.findViewById(R.id.user_detail_inf_prize);
            viewHolder.caijinDetailInfStatus =
                (TextView) convertView.findViewById(R.id.user_detail_inf_status);
            viewHolder.caijinDetailInfReturnTime =
                (TextView) convertView.findViewById(R.id.user_detail_inf_return_time);
            viewHolder.caijinDetailInfUsedTime =
                (TextView) convertView.findViewById(R.id.user_detail_inf_used_time);
            viewHolder.caijinDetailInfExpiredTime =
                (TextView) convertView.findViewById(R.id.user_detail_inf_expired_time);
            viewHolder.viewBgInner = (LinearLayout) convertView.findViewById(R.id.view_bg_inner);
            viewHolder.caijinDetailInfUsedMoney =
                (TextView) convertView.findViewById(R.id.user_detail_inf_used_money);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == this.getCount() - 1) {
            viewHolder.viewBgInner.setBackgroundResource(R.drawable.list_bg);
        }
        else {
            viewHolder.viewBgInner.setBackgroundResource(R.drawable.list_bg);
        }
        initView(viewHolder, position);
        return convertView;
    }

    private void initView(ViewHolder viewHolder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String statusCode = listArray.get(position).getStatus();
        viewHolder.caijinDetailInfPrize.setText(listArray.get(position).getPrize() + "元");
        viewHolder.caijinDetailInfStatus.setText(parceCode(statusCode));
        viewHolder.caijinDetailInfReturnTime.setText(dateFormat.format(stringConvertToDate(listArray.get(position).getReturnTime())));
        viewHolder.caijinDetailInfExpiredTime.setText(dateFormat.format(stringConvertToDate(listArray.get(position).getExpiredTime())));
        if (statusCode.equals("0") || statusCode.equals("1")) {
            viewHolder.caijinDetailInfUsedTime.setText("--");
            viewHolder.caijinDetailInfUsedTime.setVisibility(View.GONE);
            viewHolder.caijinDetailInfExpiredTime.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.caijinDetailInfUsedTime.setText(dateFormat.format(stringConvertToDate(listArray.get(position).getUsedTime())));
            viewHolder.caijinDetailInfUsedTime.setVisibility(View.VISIBLE);
            viewHolder.caijinDetailInfExpiredTime.setVisibility(View.GONE);
        }
        viewHolder.caijinDetailInfUsedMoney.setText(listArray.get(position).getUsedMoney() + "元");
    }

    private String parceCode(String code) {
        if (code.equals("0"))
            return "失效";
        else if (code.equals("1"))
            return "可用";
        else if (code.equals("2"))
            return "已用";
        return null;
    }

    private Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            toDate = dateFormat.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    @Override
    public int getCount() {
        return listArray.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
