package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.JingCaiOrderDetailItem;

public class JingCaiOrderDetailAdapter
    extends BaseAdapter {

    private static final String[] BUNCHNAME = {"单关","2串1", "3串1", "4串1", "5串1", "6串1", "7串1", "8串1"};
    private static final int[] SFCCODE = {100,201, 301, 401, 501, 601, 701, 801};

    private Context context;
    private ArrayList<JingCaiOrderDetailItem> jingCaiBetHistoryDetailItemDataList;
    private LayoutInflater inflater;
    private String splitNum;

    public class ViewHolder {
        private TextView betOrder;
        private TextView betType;
        private TextView betAward;
        private TextView betIndex;
        private TextView betInf;
    }

    public JingCaiOrderDetailAdapter(Context context,
                                     ArrayList<JingCaiOrderDetailItem> jingCaiBetHistoryDetailItemDataList,
                                     String splitNum) {
        this.context = context;
        this.jingCaiBetHistoryDetailItemDataList = jingCaiBetHistoryDetailItemDataList;
        this.splitNum = splitNum;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return jingCaiBetHistoryDetailItemDataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return jingCaiBetHistoryDetailItemDataList.get(arg0);
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
            arg1 = inflater.inflate(R.layout.jing_cai_order_detail_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.betOrder = (TextView) arg1.findViewById(R.id.jing_cai_order_bet_num);
            viewHolder.betType = (TextView) arg1.findViewById(R.id.jing_cai_order_bet_type);
            viewHolder.betAward = (TextView) arg1.findViewById(R.id.jing_cai_order_bet_award);
            viewHolder.betIndex = (TextView) arg1.findViewById(R.id.index);
            viewHolder.betInf = (TextView) arg1.findViewById(R.id.jing_cai_order_bet);
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        JingCaiOrderDetailItem jingCaiOrderDetailItem = jingCaiBetHistoryDetailItemDataList.get(arg0);
        viewHolder.betOrder.setText(Html.fromHtml(jingCaiOrderDetailItem.getJingCaiBetItem()));
        viewHolder.betType.setText(getBetType(jingCaiOrderDetailItem.getJincaiBetType()));
        if (jingCaiOrderDetailItem.getJincaiBuyMode() == 0 && jingCaiOrderDetailItem.getJincaiAward() == 1)
            viewHolder.betAward.setTextColor(Color.RED);
        else
            viewHolder.betAward.setTextColor(Color.BLACK);
        viewHolder.betAward.setText(getBetStatus(jingCaiOrderDetailItem.getJincaiBuyMode(),
                                                 jingCaiOrderDetailItem.getJincaiAward(),
                                                 jingCaiOrderDetailItem));
        viewHolder.betInf.setText(jingCaiOrderDetailItem.getZhuShu() + "注" +
            jingCaiOrderDetailItem.getBetTimes() + "倍");
        viewHolder.betIndex.setText(String.valueOf(Integer.valueOf(splitNum) - arg0));
        return arg1;
    }

    private String getBetStatus(int buy, int status, JingCaiOrderDetailItem jingCaiOrderDetailItem) {
        if (buy == 0) {
            if (status == 0)
                return "未中奖";
            else if (status == 1)
                return "奖金：" + jingCaiOrderDetailItem.getJingcaiAwardMoney() + "元";
            else if (status == 2)
                return "等待开奖";
            else
                return "已出票";
        }
        else if (buy == 1 || buy == 2) {
            return "出票失败";
        }
        else if (buy == 8 || buy == 9) {
            return "处理中";
        }
        return "";
    }

    private String getBetType(String guoGuan) {
        StringBuilder guoGuanSb = new StringBuilder();
        String[] guoGuanArray = guoGuan.split("\\;");
        for (int j = 0; j < guoGuanArray.length; j++)
            for (int i = 0; i < SFCCODE.length; i++)
                if (SFCCODE[i] == Integer.valueOf(guoGuanArray[j])) {
                    guoGuanSb.append(BUNCHNAME[i]);
                    guoGuanSb.append(",");
                }
        guoGuanSb.delete(guoGuanSb.length() - 1, guoGuanSb.length());
        return guoGuanSb.toString();
    }
}
