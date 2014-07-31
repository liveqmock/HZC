package com.haozan.caipiao.adapter.unite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.types.unite.UniteListItem;

public class UniteHallListAdapter
    extends BaseAdapter {

    private ArrayList<UniteListItem> list;
    private Context context;
    private LayoutInflater inflater;
    protected LotteryApp appState;

    public UniteHallListAdapter(Context context, ArrayList<UniteListItem> list) {
        super();
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        appState = (LotteryApp) ((Activity) context).getApplication();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final UniteListItem data = list.get(position);
        int a = position;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.unite_list_item, null);
            viewHolder.progressTv = (TextView) convertView.findViewById(R.id.progress_tv);
// viewHolder.percentFlag = (TextView) convertView.findViewById(R.id.progress_tv_per);
            viewHolder.guaranteeTv = (TextView) convertView.findViewById(R.id.gua_tv);
            viewHolder.kindTv = (TextView) convertView.findViewById(R.id.kind_tv);
            viewHolder.tvSponsor = (TextView) convertView.findViewById(R.id.unite_sponsor);
            viewHolder.tvGrade = (TextView) convertView.findViewById(R.id.unite_grade);
            viewHolder.tvTotalMoney = (TextView) convertView.findViewById(R.id.unite_total_money);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.unite_price);
            viewHolder.tvAmount = (TextView) convertView.findViewById(R.id.unite_amount);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progressTv.setText(data.getScheduled() + "%");
        int rate = Integer.parseInt(data.getScheduled());
        if (rate < 30) {
            viewHolder.progressTv.setTextColor(context.getResources().getColor(R.color.yellow_light));
// viewHolder.percentFlag.setTextColor(context.getResources().getColor(R.color.yellow_light));
        }
        else if (rate > 69) {
            viewHolder.progressTv.setTextColor(context.getResources().getColor(R.color.red_text));
// viewHolder.percentFlag.setTextColor(context.getResources().getColor(R.color.red_text));
        }
        else {
            viewHolder.progressTv.setTextColor(context.getResources().getColor(R.color.orange_bg_middle));
// viewHolder.percentFlag.setTextColor(context.getResources().getColor(R.color.orange_bg_middle));
        }

        if (null != data.getGuaRate()) {
            if (0 != Integer.parseInt(data.getGuaRate())) {
                viewHolder.guaranteeTv.setVisibility(View.VISIBLE);
                viewHolder.guaranteeTv.setText("保" + data.getGuaRate() + "%");
            }
            else {
                viewHolder.guaranteeTv.setVisibility(View.GONE);
            }
        }
        else {
            viewHolder.guaranteeTv.setVisibility(View.GONE);
        }

        viewHolder.kindTv.setText(data.getKind());
        viewHolder.tvSponsor.setText(data.getNickname());
        viewHolder.tvGrade.setText(data.getGrade());
        viewHolder.tvTotalMoney.setText(Html.fromHtml("<font color=\"#B8B8B8\">" + "￥" + "</font>" +
            data.getTotalMoney()));
        viewHolder.tvPrice.setText(Html.fromHtml("<font color=\"#B8B8B8\">" + "￥" + "</font>" +
            data.getPrice()));
        viewHolder.tvAmount.setText(data.getLastAmount());

        return convertView;
    }

    public final class ViewHolder {
        public TextView tvSponsor;
        public TextView tvGrade;
        public TextView tvTotalMoney;
        public TextView tvPrice;
        public TextView tvAmount;
        public TextView progressTv;
        public TextView percentFlag;
        public TextView guaranteeTv;
        public TextView kindTv;
    }
}
