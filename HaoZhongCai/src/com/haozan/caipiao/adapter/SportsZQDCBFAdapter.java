package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;

public class SportsZQDCBFAdapter
    extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<SportsSFCBetGroup> list;
    private OnBallClickListener clickListener;

    public SportsZQDCBFAdapter(Context context, ArrayList<SportsSFCBetGroup> list) {
        this.context = context;
        this.list = list;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getZjqitemList().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getZjqitemList().size();
    }

    public final class ViewHolderChild {
        private TextView time;
        private TextView index;
        private TextView league;
        private TextView firstTeam;
        private TextView concedePoint;
        private TextView secondTeam;
        private Button one;
        private Button two;
        private Button three;
        private Button four;
        private Button five;
        private Button six;
        private Button seven;
        private Button eight;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ViewHolderChild viewHolder;
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_zjq_item_view, null);
            viewHolder = new ViewHolderChild();
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.concedePoint = (TextView) convertView.findViewById(R.id.concede_point);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
            viewHolder.one = (Button) convertView.findViewById(R.id.zjq_one);
            viewHolder.two = (Button) convertView.findViewById(R.id.zjq_two);
            viewHolder.three = (Button) convertView.findViewById(R.id.zjq_three);
            viewHolder.four = (Button) convertView.findViewById(R.id.zjq_four);
            viewHolder.five = (Button) convertView.findViewById(R.id.zjq_five);
            viewHolder.six = (Button) convertView.findViewById(R.id.zjq_six);
            viewHolder.seven = (Button) convertView.findViewById(R.id.zjq_seven);
            viewHolder.eight = (Button) convertView.findViewById(R.id.zjq_eight);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderChild) convertView.getTag();
        }
//        if (childPosition % 2 == 0) {
//            convertView.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
//        }
//        else {
//            convertView.setBackgroundResource(R.drawable.user_detail_inf_single);
//        }
        SportsItem sportsItem = list.get(groupPosition).getZjqitemList().get(childPosition);
        viewHolder.time.setText(sportsItem.getEndTime().subSequence(11, 16) + "截止");
        viewHolder.index.setText(sportsItem.getIdBetNum());
        viewHolder.league.setText(sportsItem.getLeague());
        viewHolder.firstTeam.setText(sportsItem.getMatchHomeTeamName());
        //TODO
//        if (sportsItem.getConcede().equals("0")) {
            viewHolder.concedePoint.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.concedePoint.setText("vs");
//        }
//        else {
//            if (Integer.valueOf(sportsItem.getConcede()) > 0) {
//                viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#CD2626\">(" +
//                    sportsItem.getConcede() + ")</font>vs"));
//            }
//            else {
//                viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#008000\">(" +
//                    sportsItem.getConcede() + ")</font>vs"));
//            }
//        }
        
        viewHolder.secondTeam.setText(sportsItem.getMatchGuessTeamName());
        viewHolder.one.setText(Html.fromHtml("<font color=\"#888888\"><big>0</big></font> " + sportsItem.getOdds()[0].subSequence(0, sportsItem.getOdds()[0].length() - 1)));
        viewHolder.one.setOnClickListener(new ClickAction(groupPosition, childPosition, 0));
        viewHolder.two.setText(Html.fromHtml("<font color=\"#888888\"><big>1</big></font> " + sportsItem.getOdds()[1].subSequence(0, sportsItem.getOdds()[1].length() - 1)));
        viewHolder.two.setOnClickListener(new ClickAction(groupPosition, childPosition, 1));
        viewHolder.three.setText(Html.fromHtml("<font color=\"#888888\"><big>2</big></font> " + sportsItem.getOdds()[2].subSequence(0, sportsItem.getOdds()[2].length() - 1)));
        viewHolder.three.setOnClickListener(new ClickAction(groupPosition, childPosition, 2));
        viewHolder.four.setText(Html.fromHtml("<font color=\"#888888\"><big>3</big></font> " + sportsItem.getOdds()[3].subSequence(0, sportsItem.getOdds()[3].length() - 1)));
        viewHolder.four.setOnClickListener(new ClickAction(groupPosition, childPosition, 3));
        viewHolder.five.setText(Html.fromHtml("<font color=\"#888888\"><big>4</big></font> " + sportsItem.getOdds()[4].subSequence(0, sportsItem.getOdds()[4].length() - 1)));
        viewHolder.five.setOnClickListener(new ClickAction(groupPosition, childPosition, 4));
        viewHolder.six.setText(Html.fromHtml("<font color=\"#888888\"><big>5</big></font> " + sportsItem.getOdds()[5].subSequence(0, sportsItem.getOdds()[5].length() - 1)));
        viewHolder.six.setOnClickListener(new ClickAction(groupPosition, childPosition, 5));
        viewHolder.seven.setText(Html.fromHtml("<font color=\"#888888\"><big>6</big></font> " + sportsItem.getOdds()[6].subSequence(0, sportsItem.getOdds()[6].length() - 1)));
        viewHolder.seven.setOnClickListener(new ClickAction(groupPosition, childPosition, 6));
        viewHolder.eight.setText(Html.fromHtml("<font color=\"#888888\"><big>7+</big></font>" + sportsItem.getOdds()[7].subSequence(0, sportsItem.getOdds()[7].length() - 1)));
        viewHolder.eight.setOnClickListener(new ClickAction(groupPosition, childPosition, 7));
        
        if (!sportsItem.getStatus(0)) {
            viewHolder.one.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.one.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.one.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.one.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(1)) {
            viewHolder.two.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.two.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.two.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.two.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(2)) {
            viewHolder.three.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.three.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.three.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.three.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(3)) {
            viewHolder.four.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.four.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.four.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.four.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(4)) {
            viewHolder.five.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.five.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.five.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.five.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(5)) {
            viewHolder.six.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.six.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.six.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.six.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(6)) {
            viewHolder.seven.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.seven.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.seven.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.seven.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        if (!sportsItem.getStatus(7)) {
            viewHolder.eight.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.eight.setBackgroundResource(R.drawable.custom_button);
        }
        else {
            viewHolder.eight.setTextColor(context.getResources().getColor(R.color.light_white));
            viewHolder.eight.setBackgroundResource(R.drawable.custom_button_highlight);
        }
        
        
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public final class ViewHolderGroup {
        private TextView date;
        private TextView day;
        private TextView numberInf;
        private ImageView statusIcon;
    }

    @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolderGroup viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolderGroup();
                LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.sports_history_group_view, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.day = (TextView) convertView.findViewById(R.id.day);
                viewHolder.numberInf = (TextView) convertView.findViewById(R.id.number_inf);
                viewHolder.statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolderGroup) convertView.getTag();
            }
            SportsSFCBetGroup sportsHistory = list.get(groupPosition);
            if (viewHolder == null) {
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
            }
            else {
                viewHolder.date.setText(sportsHistory.getDate());
                viewHolder.day.setText(sportsHistory.getDay());
                viewHolder.numberInf.setText(sportsHistory.getGameNumber());
                if (isExpanded)
                    viewHolder.statusIcon.setImageResource(R.drawable.double_arrow_down);
                else
                    viewHolder.statusIcon.setImageResource(R.drawable.double_arrow_up);
            }
            return convertView;
        }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ClickAction
        implements OnClickListener {
        private int groupPosition;
        private int childPosition;
        private int index;

        public ClickAction(int groupPosition, int childPosition, int index) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (!clickListener.checkClick(groupPosition, childPosition, index))
                return;
            Button button = (Button) v;
            SportsItem item = list.get(groupPosition).getZjqitemList().get(childPosition);
            if (!item.getStatus(index)) {
                button.setTextColor(context.getResources().getColor(R.color.dark_purple));
                button.setBackgroundResource(R.drawable.custom_button);
            }
            else {
                button.setTextColor(context.getResources().getColor(R.color.light_white));
                button.setBackgroundResource(R.drawable.custom_button_highlight);
            }
        }
    }

    public void setClickListener(OnBallClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnBallClickListener {
        /**
         * 是否允许点击球队
         * 
         * @param groupPosition 球队所属组
         * @param childPosition 球队所属组中位置
         * @param index 选中胜平负中哪项
         * @return
         */
        public boolean checkClick(int groupPosition, int childPosition, int index);
    }
}
