package com.haozan.caipiao.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.CaiJinListData;

public class CaiJinListAdapter
    extends BaseAdapter {
    private ArrayList<CaiJinListData> listArray;
    private Context context;
    private LayoutInflater inflater;

// private String type;
// private String time;
// private int amount;

// private String[] typeName = {"充值", "投注", "投中", "提现", "追号", "追中", "其他"};

    public final class ViewHolder {
        private TextView caijinListInfName;
        private TextView caijinListInfProgressing;
        private TextView caijinListInfTimes;
        private TextView caijinLIstInfAmount;
        private TextView caiJingMoneySum;
        private TextView caiJinStatus;
// private LinearLayout userDetailInf;
    }

    public CaiJinListAdapter(Context context, ArrayList<CaiJinListData> listArray) {
        this.context = context;
        this.listArray = listArray;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

// analyseTheJsonObject(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.caijin_list_inf_adapter, null);

// viewHolder.userDetailInf = (LinearLayout) convertView.findViewById(R.id.user_detail_inf_item);
            viewHolder.caijinListInfName = (TextView) convertView.findViewById(R.id.caijin_list_inf_name);
            viewHolder.caijinListInfProgressing =
                (TextView) convertView.findViewById(R.id.caijin_list_inf_progressing);
            viewHolder.caijinListInfTimes = (TextView) convertView.findViewById(R.id.caijin_list_inf_time);
            viewHolder.caijinLIstInfAmount = (TextView) convertView.findViewById(R.id.caijin_list_inf_amount);
            viewHolder.caiJingMoneySum = (TextView) convertView.findViewById(R.id.caijin_list_money_sum);
            viewHolder.caiJinStatus = (TextView) convertView.findViewById(R.id.caijin_list_inf_status);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(viewHolder, position);
        return convertView;
    }

    private void initView(ViewHolder viewHolder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        viewHolder.caijinListInfName.setText(Html.fromHtml("<font color=\"#A8A8A8\">名称：</font>" +
            listArray.get(position).getName()));
        viewHolder.caijinListInfProgressing.setText(Html.fromHtml("<font color=\"#A8A8A8\">已返：</font>" +
            Integer.valueOf(listArray.get(position).getCharge()) + "元"));
        viewHolder.caijinListInfTimes.setText(Html.fromHtml("<font color=\"#A8A8A8\">时间：</font>" +
            dateFormat.format(stringConvertToDate(listArray.get(position).getTime()))));
        viewHolder.caijinLIstInfAmount.setText(Html.fromHtml("<font color=\"#A8A8A8\">方式：</font>" +
            listArray.get(position).getDiscription()));
        viewHolder.caiJingMoneySum.setText(Html.fromHtml("<font color=\"#A8A8A8\">总额：</font>" +
            listArray.get(position).getAmount() + "元"));
        if (listArray.get(position).getCharge().equals(listArray.get(position).getAmount()))
            viewHolder.caiJinStatus.setText(Html.fromHtml("<font color=\"#A8A8A8\">状态：</font>" + "完成赠送"));
        else
            viewHolder.caiJinStatus.setText(Html.fromHtml("<font color=\"#A8A8A8\">状态：</font>" + "正在进行"));
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

// public void analyseTheJsonObject(int i) {
// }

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
