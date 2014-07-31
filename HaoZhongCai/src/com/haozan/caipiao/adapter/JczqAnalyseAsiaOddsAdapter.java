package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.JczqAnalyseAsiaOddsListItemData;

public class JczqAnalyseAsiaOddsAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<JczqAnalyseAsiaOddsListItemData> analyseOddsList;
    private LayoutInflater inflater;

    public JczqAnalyseAsiaOddsAdapter(Context context, ArrayList<JczqAnalyseAsiaOddsListItemData> analystList) {
        this.context = context;
        this.analyseOddsList = analystList;
        inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView companyTv;
        private TextView initWater1;
        private TextView initWater2;
        private TextView currentWater1;
        private TextView currentWater2;
        private TextView initOdds;
        private TextView currentOdds;
    }

    @Override
    public int getCount() {
        return analyseOddsList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return analyseOddsList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.jczq_analyse_asiaodds_item_view, null);
            viewHolder = new ViewHolder();
            viewHolder.companyTv = (TextView) convertView.findViewById(R.id.company);
            viewHolder.initWater1 = (TextView) convertView.findViewById(R.id.init_water1);
            viewHolder.initWater2 = (TextView) convertView.findViewById(R.id.init_water2);
            viewHolder.currentWater1 = (TextView) convertView.findViewById(R.id.current_water1);
            viewHolder.currentWater2 = (TextView) convertView.findViewById(R.id.current_water2);
            viewHolder.initOdds = (TextView) convertView.findViewById(R.id.init_odds);
            viewHolder.currentOdds = (TextView) convertView.findViewById(R.id.current_odds);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (index % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
        }
        else {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_single);
        }
        initListView(viewHolder, index);
        return convertView;
    }

    private void initListView(ViewHolder viewHolder, int index) {
        viewHolder.companyTv.setText(analyseOddsList.get(index).getCompany());
        viewHolder.initWater1.setText(analyseOddsList.get(index).getInitWater1());
        viewHolder.initWater2.setText(analyseOddsList.get(index).getInitWater2());
        viewHolder.currentWater1.setText(analyseOddsList.get(index).getCurrentWater1());
        viewHolder.currentWater2.setText(analyseOddsList.get(index).getCurrentWater2());
        viewHolder.initOdds.setText(analyseOddsList.get(index).getInitOdds());
        viewHolder.currentOdds.setText(analyseOddsList.get(index).getCurrentOdds());
    }
}