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
import com.haozan.caipiao.types.SportsJCLQItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;

public class SportsJCLQRFSFAdapter
    extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<SportsSFCBetGroup> list;
    private OnBallClickListener clickListener;
    private String[] result = {"主负", "主胜"};

    public SportsJCLQRFSFAdapter(Context context, ArrayList<SportsSFCBetGroup> list) {
        this.context = context;
        this.list = list;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getItemJCLQList().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getItemJCLQList().size();
    }

    public final class ViewHolderChild {
        private TextView time;
        private TextView index;
        private TextView league;
        private TextView firstTeam;
        private TextView concedePoint;
        private TextView secondTeam;
        private Button win;
        private Button lose;
        private ImageView img01;
        private ImageView img02;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ViewHolderChild viewHolder;
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_jclq_item_view, null);
            viewHolder = new ViewHolderChild();
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.concedePoint = (TextView) convertView.findViewById(R.id.concede_point);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
            viewHolder.win = (Button) convertView.findViewById(R.id.win);
            viewHolder.lose = (Button) convertView.findViewById(R.id.lose);
            viewHolder.img01 = (ImageView) convertView.findViewById(R.id.img_01);
            viewHolder.img02 = (ImageView) convertView.findViewById(R.id.img_02);
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
        SportsJCLQItem sportsItem = list.get(groupPosition).getItemJCLQList().get(childPosition);
        viewHolder.time.setText(sportsItem.getEndTime().subSequence(11, 16) + "截止");
        viewHolder.index.setText(sportsItem.getIdBetNum());
        viewHolder.league.setText(sportsItem.getLeague());
        viewHolder.firstTeam.setText(sportsItem.getMatchGuessTeamName());
        if (sportsItem.getConcede().equals("0")) {
            viewHolder.concedePoint.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.concedePoint.setText("vs");
        }
        else {
            if (Double.valueOf(sportsItem.getConcede()) > 0) {
                viewHolder.concedePoint.setText(Html.fromHtml("vs<font color=\"#CD2626\">(" +
                    sportsItem.getConcede() + ")</font>"));
            }
            else {
                viewHolder.concedePoint.setText(Html.fromHtml("vs<font color=\"#008000\">(" +
                    sportsItem.getConcede() + ")</font>"));
            }
        }
        viewHolder.secondTeam.setText(sportsItem.getMatchHomeTeamName());
        viewHolder.win.setOnClickListener(new ClickAction(groupPosition, childPosition, 0, viewHolder.img01));
        viewHolder.lose.setOnClickListener(new ClickAction(groupPosition, childPosition, 1, viewHolder.img02));
        if (!sportsItem.getStatus(0)) {
            viewHolder.win.setText(Html.fromHtml("<font color=\"#A8A8A8\">主负</font>   " +
                "<font color=\"#DB0010\">" + sportsItem.getOdds()[1] + "</font>"));
            viewHolder.win.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.img01.setVisibility(View.GONE);
        }
        else {
            viewHolder.win.setText(Html.fromHtml("<font color=\"#FFFFFF\">主负   " + sportsItem.getOdds()[1] +
                "</font>"));
            viewHolder.win.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            viewHolder.img01.setVisibility(View.VISIBLE);
        }
        if (!sportsItem.getStatus(1)) {
            viewHolder.lose.setText(Html.fromHtml("<font color=\"#A8A8A8\">主胜</font>   " +
                "<font color=\"#DB0010\">" + sportsItem.getOdds()[0] + "</font>"));
            viewHolder.lose.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.img02.setVisibility(View.GONE);
        }
        else {
            viewHolder.lose.setText(Html.fromHtml("<font color=\"#FFFFFF\">主胜   " + sportsItem.getOdds()[0] +
                "</font>"));
            viewHolder.lose.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            viewHolder.img02.setVisibility(View.VISIBLE);
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
        private ImageView img;

        public ClickAction(int groupPosition, int childPosition, int index, ImageView img) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
            this.index = index;
            this.img = img;
        }

        @Override
        public void onClick(View v) {
            if (!clickListener.checkClick(groupPosition, childPosition, index))
                return;
            Button button = (Button) v;
            SportsJCLQItem item = list.get(groupPosition).getItemJCLQList().get(childPosition);
            if (!item.getStatus(index)) {
                button.setText(Html.fromHtml("<font color=\"#A8A8A8\">" + result[index] + "</font>   " +
                    "<font color=\"#DB0010\">" + item.getOdds()[index == 0 ? 1 : 0] + "</font>"));
                button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
                img.setVisibility(View.GONE);
            }
            else {
                button.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + result[index] + "   " +
                    item.getOdds()[index == 0 ? 1 : 0] + "</font>"));
                button.setBackgroundResource(R.drawable.custom_button_redside_normal);
//                img.setVisibility(View.VISIBLE);
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
