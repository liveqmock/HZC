package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.JczqAnalyseGroup;
import com.haozan.caipiao.types.JczqAnalyseListItemData;

public class JczqAnalyseAdapter
    extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<JczqAnalyseGroup> list;

    public JczqAnalyseAdapter(Context context, ArrayList<JczqAnalyseGroup> list) {
        this.context = context;
        this.list = list;
    }

    public final class ViewHolderChild {
        private LinearLayout ll_match;
        private TextView leagueTv;
        private TextView timeTv;
        private TextView masterTv;
        private TextView scoreTv;
        private TextView guestTv;
        private TextView halfCourtTv;
        private View tempView;
        private LinearLayout ll_rank;
        private TextView type;
        private TextView rank;
        private TextView allMatch;
        private TextView winNum;
        private TextView evenNum;
        private TextView loseNum;
        private TextView getScore;
        private TextView loseScore;
        private TextView lastScore;
        private TextView allScore;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final ViewHolderChild viewHolder;
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.jczq_analyse_item_view, null);
            viewHolder = new ViewHolderChild();
            viewHolder.ll_match = (LinearLayout) convertView.findViewById(R.id.ll_match);
            viewHolder.leagueTv = (TextView) convertView.findViewById(R.id.tv_league);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.masterTv = (TextView) convertView.findViewById(R.id.tv_master);
            viewHolder.scoreTv = (TextView) convertView.findViewById(R.id.tv_score);
            viewHolder.guestTv = (TextView) convertView.findViewById(R.id.tv_guest);
            viewHolder.halfCourtTv = (TextView) convertView.findViewById(R.id.tv_halfCourt);
            viewHolder.tempView = (View) convertView.findViewById(R.id.temp_view);
            viewHolder.ll_rank = (LinearLayout) convertView.findViewById(R.id.ll_rank);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.rank = (TextView) convertView.findViewById(R.id.rank);
            viewHolder.allMatch = (TextView) convertView.findViewById(R.id.allMatch);
            viewHolder.winNum = (TextView) convertView.findViewById(R.id.winNum);
            viewHolder.evenNum = (TextView) convertView.findViewById(R.id.evenNum);
            viewHolder.loseNum = (TextView) convertView.findViewById(R.id.loseNum);
            viewHolder.getScore = (TextView) convertView.findViewById(R.id.getScore);
            viewHolder.loseScore = (TextView) convertView.findViewById(R.id.loseScore);
            viewHolder.lastScore = (TextView) convertView.findViewById(R.id.lastScore);
            viewHolder.allScore = (TextView) convertView.findViewById(R.id.allScore);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderChild) convertView.getTag();
        }
        if (childPosition % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
        }
        else {
            convertView.setBackgroundResource(R.drawable.user_detail_inf_single);
        }
        final JczqAnalyseListItemData item = list.get(groupPosition).getArrayList().get(childPosition);
        if (item.getDataType() == 1) {
            viewHolder.ll_match.setVisibility(View.GONE);
            viewHolder.ll_rank.setVisibility(View.VISIBLE);
            viewHolder.type.setText(item.getType());
            viewHolder.rank.setText(item.getRank());
            viewHolder.allMatch.setText(item.getAllMatch());
            viewHolder.winNum.setText(item.getWinNum());
            viewHolder.evenNum.setText(item.getEvenNum());
            viewHolder.loseNum.setText(item.getLoseNum());
            viewHolder.getScore.setText(item.getGetScore());
            viewHolder.loseScore.setText(item.getLoseScore());
            viewHolder.lastScore.setText(item.getLastScore());
            viewHolder.allScore.setText(item.getAllScore());
        }
        else {
            viewHolder.ll_match.setVisibility(View.VISIBLE);
            viewHolder.ll_rank.setVisibility(View.GONE);
            if (item.getDataType() == 0) {
                viewHolder.halfCourtTv.setVisibility(View.VISIBLE);
                viewHolder.tempView.setVisibility(View.VISIBLE);
                if(null != item.getHalfCourt() && !"".equals(item.getHalfCourt())){
                    viewHolder.halfCourtTv.setText(Html.fromHtml(resetColor(item.getHalfCourt(), 0)));
                }
                else {
                    viewHolder.halfCourtTv.setText("");
                }
            }
            else {
                viewHolder.halfCourtTv.setVisibility(View.GONE);
                viewHolder.tempView.setVisibility(View.GONE);
            }
            viewHolder.leagueTv.setText(item.getLeague());
            viewHolder.timeTv.setText(item.getTime());
            viewHolder.masterTv.setText(item.getMaster());
            if(childPosition != 0){
                viewHolder.scoreTv.setText(Html.fromHtml(resetColor(item.getScore(), 1)));
            }
            else {
                viewHolder.scoreTv.setText(item.getScore());
            }
            viewHolder.guestTv.setText(item.getGuest());
        }
        return convertView;
    }

    private String resetColor(String str, int flag) {
        if (flag == 0) {
            if ("胜".equals(str)) {
                str = "<font color=\"#CD2626\">" + str + "</font>";
            }
            else if ("负".equals(str)) {
                str = "<font color=\"#008000\">" + str + "</font>";
            }
        }
        else if(flag == 1){
            str = "<font color=\"#CD2626\">" + str + "</font>";
        }
        return str;

    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getArrayList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getArrayList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class ViewHolderGroup {
        private TextView groupName;
        private ImageView statusIcon;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolderGroup();
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.jczq_analyse_group_view, null);
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            viewHolder.statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderGroup) convertView.getTag();
        }
        JczqAnalyseGroup group = list.get(groupPosition);
        if (viewHolder == null) {
            TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
            groupName.setText(group.getGroupName());
            ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
            if (isExpanded)
                statusIcon.setImageResource(R.drawable.double_arrow_down);
            else
                statusIcon.setImageResource(R.drawable.double_arrow_up);
        }
        else {
            viewHolder.groupName.setText(group.getGroupName());
            if (isExpanded)
                viewHolder.statusIcon.setImageResource(R.drawable.double_arrow_down);
            else
                viewHolder.statusIcon.setImageResource(R.drawable.double_arrow_up);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}