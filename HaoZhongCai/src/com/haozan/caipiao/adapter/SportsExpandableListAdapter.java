package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.SportsHistory;
import com.haozan.caipiao.types.SportsHistoryItem;

public class SportsExpandableListAdapter
    extends BaseExpandableListAdapter {
    // add by vincent
    private static final String[] bqcNum =
        new String[] {"33", "31", "30", "13", "11", "10", "03", "01", "00"};
    private static final String[] bqcStr =
        new String[] {"胜胜", "胜平", "胜负", "平胜", "平平", "平负", "负胜", "负平", "负负"};

    private int mHideGroupPos = -1;
    private String kind;
    private Context context;
    private ArrayList<SportsHistory> list;

    public SportsExpandableListAdapter(Context context, ArrayList<SportsHistory> list, String kind) {
        this.context = context;
        this.list = list;
        this.kind = kind;
    }

    public void hideGroup(int groupPos) {
        mHideGroupPos = groupPos;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getItemList().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getItemList().size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (kind.equals("竞彩足球")) {
                convertView = inflater.inflate(R.layout.sports_history_item_view, null);
            }
            else if (kind.equals("竞彩篮球")) {
                convertView = inflater.inflate(R.layout.sports_history_item_view_jclq, null);
            }
            else if (kind.equals("胜负过关")) {
                convertView = inflater.inflate(R.layout.sports_history_item_view, null);
            }
        }
        if (childPosition % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
        }
        else {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_single);
        }
        SportsHistoryItem sportsHistoryItem = list.get(groupPosition).getItemList().get(childPosition);
        TextView index = (TextView) convertView.findViewById(R.id.index);
        index.setText(sportsHistoryItem.getIdBetNum());
        TextView league = (TextView) convertView.findViewById(R.id.league);
        league.setText(sportsHistoryItem.getLeague());
        TextView firstTeam = (TextView) convertView.findViewById(R.id.first_team);
        TextView secondTeam = (TextView) convertView.findViewById(R.id.second_team);
        secondTeam.setText(sportsHistoryItem.getMatchGuessTeamName());
        TextView point = (TextView) convertView.findViewById(R.id.point);
        TextView result01 = (TextView) convertView.findViewById(R.id.result01);
        boolean isNull = false;
        if (sportsHistoryItem.getPoint() == null || sportsHistoryItem.getPoint().equals("")) {
            point.setText("-:-");
            // TODO 其他显示项目设为“-”
            isNull = true;
        }
        else {
            point.setText(sportsHistoryItem.getPoint());
        }
        if (kind.equals("竞彩足球") || kind.equals("胜负过关")) {
            // add by vincent
            TextView bqcRe = (TextView) convertView.findViewById(R.id.bqc_result);
            if (sportsHistoryItem.getBqcResult() == null || sportsHistoryItem.getBqcResult().equals("")) {
                bqcRe.setText("-");
            }
            else {
                bqcRe.setText(getBqcResultText(sportsHistoryItem.getBqcResult()));
            }
            // changed by vincent
            // firstTeam.setText(sportsHistoryItem.getMatchHomeTeamName());
            if(kind.equals("胜负过关")){
            	StringBuilder str = new StringBuilder();
                
                double concede = 0;
                if(sportsHistoryItem.getConcede() != null){
                	if(!sportsHistoryItem.getConcede().equals("")){
                    	concede = Double.parseDouble(sportsHistoryItem.getConcede());
                    }
                }
                
                if (concede > 0) {
                    str.append("<font color='#FF0000'>+" + String.valueOf(concede) + "</font>");
                }
                else if (concede < 0) {
                    str.append("<font color='#458B00'>" + String.valueOf(concede) + "</font>");
                }
                
                firstTeam.setText(sportsHistoryItem.getMatchHomeTeamName());
                
                point.setText(Html.fromHtml(str.toString()));
            }else{
            	StringBuilder str = new StringBuilder();
            	
                str.append(sportsHistoryItem.getMatchHomeTeamName());
                
                double concede = 0;
                if(sportsHistoryItem.getConcede() != null){
                	if(!sportsHistoryItem.getConcede().equals("")){
                    	concede = Double.parseDouble(sportsHistoryItem.getConcede());
                    }
                }
                
                if (concede > 0) {
                    str.append("<font color='#FF0000'>(+" + String.valueOf(concede) + ")</font>");
                }
                else if (concede < 0) {
                    str.append("<font color='#458B00'>(" + String.valueOf(concede) + ")</font>");
                }
                
                firstTeam.setText(Html.fromHtml(str.toString()));
            }
            
            
            if (sportsHistoryItem.getResult() == null || sportsHistoryItem.getResult().equals("")) {
                result01.setVisibility(View.GONE);
            }
            else {
            	if(kind.equals("胜负过关")){
            		result01.setVisibility(View.GONE);
            		bqcRe.setText(sportsHistoryItem.getResult());
            	}else{
                    result01.setVisibility(View.VISIBLE);
                    result01.setText(getBetResultText(sportsHistoryItem.getResult()));
                    result01.setTextColor(getBetResultTextColor(sportsHistoryItem.getResult()));
            	}
            }

        }
        else if (kind.equals("竞彩篮球")) {
            firstTeam.setText(sportsHistoryItem.getMatchHomeTeamName());
// TextView result02 = (TextView) convertView.findViewById(R.id.result02);
            TextView rf_tv = (TextView) convertView.findViewById(R.id.tv_rf);
// TextView result03 = (TextView) convertView.findViewById(R.id.result03);
            TextView dxf_tv = (TextView) convertView.findViewById(R.id.tv_dxf);
            TextView sfc_tv = (TextView) convertView.findViewById(R.id.tv_sfc);
            TextView day_tv = (TextView) convertView.findViewById(R.id.day);
            day_tv.setText(sportsHistoryItem.getDay());
            RelativeLayout ll = (RelativeLayout) convertView.findViewById(R.id.layout_index);
            if (sportsHistoryItem.getColor() != null && !sportsHistoryItem.getColor().equals("") && !sportsHistoryItem.getColor().equals("null")) {
                String temp =
                    sportsHistoryItem.getColor().substring(1, sportsHistoryItem.getColor().length());
                if (temp != null && !temp.equals(""))
                    ll.setBackgroundColor(Integer.parseInt(temp, 16) | 0xff000000);
// ll.setBackgroundColor(0xff0000);
            }
            else {
                ll.setBackgroundColor(0xFF99CCFF);
            }

            // 让分彩果
            if (sportsHistoryItem.getConcede() == null || sportsHistoryItem.getConcede().equals("") ||
                isNull == true) {
                rf_tv.setText("-");
            }
            else {
                rf_tv.setText(sportsHistoryItem.getConcede());
            }
            // 大小分彩果
            if (sportsHistoryItem.getDxfPoint() == null || sportsHistoryItem.getDxfPoint().equals("") ||
                isNull == true) {
                dxf_tv.setText("-");
            }
            else {
                dxf_tv.setText(sportsHistoryItem.getDxfPoint());
            }
            // 胜分差彩果
            if (sportsHistoryItem.getSfcResult() == null || sportsHistoryItem.getSfcResult().equals("") ||
                isNull == true) {
                sfc_tv.setText("-");
            }
            else {
                sfc_tv.setText(sportsHistoryItem.getSfcResult());
            }

            /*
             * if (sportsHistoryItem.getResult() == null || sportsHistoryItem.getResult().equals("")) {
             * result01.setVisibility(View.GONE); } else { result01.setVisibility(View.VISIBLE);
             * result01.setText(sportsHistoryItem.getResult());
             * result01.setTextColor(getBetResultTextColor_jclq(sportsHistoryItem.getResult())); }
             */
            /*
             * //让分彩果 if (sportsHistoryItem.getRfResult() == null ||
             * sportsHistoryItem.getRfResult().equals("") || isNull == true) {
             * result02.setVisibility(View.GONE); rf_tv.setText("-"); } else {
             * result02.setVisibility(View.VISIBLE); result02.setText(sportsHistoryItem.getRfResult());
             * rf_tv.setText(sportsHistoryItem.getConcede()); } //大小分彩果 if (sportsHistoryItem.getDxfResult()
             * == null || sportsHistoryItem.getDxfResult().equals("") || isNull == true) {
             * result03.setVisibility(View.GONE); dxf_tv.setText("-"); } else {
             * result03.setVisibility(View.VISIBLE); result03.setText(sportsHistoryItem.getDxfResult());
             * dxf_tv.setText(sportsHistoryItem.getDxfPoint()); }
             */
        }
        return convertView;
    }

    private int getBetResultTextColor_jclq(String result) {
        int color = R.color.dark_purple;
        if (result.equals("主胜"))
            color = R.color.red;
        else if (result.equals("主负"))
            color = R.color.bottle_green;
        return context.getResources().getColor(color);
    }

    private String getBetResultText(String result) {
        if (result.equals("3"))
            return "让分胜";
        else if (result.equals("1"))
            return "让分平";
        else if (result.equals("0"))
            return "让分负";
        return null;
    }

    private String getBqcResultText(String result) {
        for (int m = 0; m < 9; m++) {
            if (result.equals(bqcNum[m])) {
                return bqcStr[m];
            }
        }
        return null;
    }

    private int getBetResultTextColor(String result) {
        int color = R.color.dark_purple;
        if (result.equals("3"))
            color = R.color.red;
        else if (result.equals("1"))
            color = R.color.dark_purple;
        else if (result.equals("0"))
            color = R.color.bottle_green;
        return context.getResources().getColor(color);
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_history_group_view, null);
        }
        SportsHistory sportsHistory = list.get(groupPosition);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(sportsHistory.getDate());
        TextView day = (TextView) convertView.findViewById(R.id.day);
        day.setText(sportsHistory.getDay());
        TextView numberInf = (TextView) convertView.findViewById(R.id.number_inf);
        numberInf.setText(sportsHistory.getGameNumber());
        ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
        if (isExpanded)
            statusIcon.setImageResource(R.drawable.double_arrow_down);
        else
            statusIcon.setImageResource(R.drawable.double_arrow_up);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }
}
