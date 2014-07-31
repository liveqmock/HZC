package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class PursuitDetailAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Map<String, Object>> data;
    private int backGroundId = 0;

    public final class ViewHolder {
        public TextView persuitLotteryId;
        public TextView persuitLotteryTerm;
        public TextView listIndexNum;
        public TextView pursuitLotteryMoney;
        public TextView pursuitLotteryBetStatus;
        public TextView pursuitLotteryBetTime;
        public TextView pursuitLotteryWin;
        public TextView openNum;
        public TextView openCodesNormal;
        public TextView openCodesSpecial;
        public ImageView clickStatusRecord;
// public LinearLayout betHistoryListBg;
    }

    public PursuitDetailAdapter(Context context, ArrayList<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (arg1 == null) {
            viewHolder = new ViewHolder();
            arg1 = inflater.inflate(R.layout.pursuit_detail_history_adapter, null);
            viewHolder.persuitLotteryId = (TextView) arg1.findViewById(R.id.pursuit_lottery_id);
            viewHolder.persuitLotteryTerm = (TextView) arg1.findViewById(R.id.pursuit_lottery_term);
            viewHolder.listIndexNum = (TextView) arg1.findViewById(R.id.list_index_num);
            viewHolder.pursuitLotteryMoney = (TextView) arg1.findViewById(R.id.pursuit_lottery_money);
            viewHolder.pursuitLotteryBetStatus =
                (TextView) arg1.findViewById(R.id.pursuit_lottery_bet_status);
            viewHolder.pursuitLotteryBetTime = (TextView) arg1.findViewById(R.id.pursuit_lottery_bet_time);
            viewHolder.pursuitLotteryWin = (TextView) arg1.findViewById(R.id.pursuit_lottery_win);
            viewHolder.openNum = (TextView) arg1.findViewById(R.id.open_num);
            viewHolder.openCodesNormal = (TextView) arg1.findViewById(R.id.open_codes);
            viewHolder.openCodesSpecial = (TextView) arg1.findViewById(R.id.open_codes_special);
            viewHolder.clickStatusRecord = (ImageView) arg1.findViewById(R.id.click_status);
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

//        if (arg0 == 0)
//            backGroundId = R.drawable.history_item_first;
//        else
//            backGroundId = R.drawable.history_item;
//        arg1.setBackgroundResource(backGroundId);

        viewHolder.persuitLotteryId.setText(data.get(arg0).get("lot_id").toString());
        viewHolder.persuitLotteryTerm.setText(data.get(arg0).get("term").toString());
        viewHolder.listIndexNum.setText(data.get(arg0).get("index").toString());
        viewHolder.pursuitLotteryMoney.setText(data.get(arg0).get("money").toString());
        viewHolder.pursuitLotteryBetStatus.setText(Html.fromHtml(data.get(arg0).get("win").toString()));
        viewHolder.pursuitLotteryBetTime.setText(data.get(arg0).get("bet_time").toString());
        viewHolder.pursuitLotteryWin.setText(data.get(arg0).get("bet_status").toString());
// if (data.get(arg0).get("opneNum") != null)
        viewHolder.openNum.setText(data.get(arg0).get("opneNum").toString());
        viewHolder.openCodesNormal.setText(data.get(arg0).get("open_code_red").toString());
        if (data.get(arg0).get("open_code_blue") != null)
            viewHolder.openCodesSpecial.setText(data.get(arg0).get("open_code_blue").toString());
        else
            viewHolder.openCodesSpecial.setText("");
        if ((Boolean) data.get(arg0).get("clickStatus")) {
            viewHolder.clickStatusRecord.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.clickStatusRecord.setVisibility(View.INVISIBLE);
        }
        return arg1;
    }

}
