package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.JczqAnalyse;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.task.SportsSimpleAnalyseTask;
import com.haozan.caipiao.task.SportsSimpleAnalyseTask.OnGetSportsSimpleAnalyseInf;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;
import com.umeng.analytics.MobclickAgent;

public class SportsJCZQSPFAdapter
    extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<SportsSFCBetGroup> list;
    private OnBallClickListener clickListener;
    public String[] betRe = {"胜", "平", "负"};

    public SportsJCZQSPFAdapter(Context context, ArrayList<SportsSFCBetGroup> list) {
        this.context = context;
        this.list = list;
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

    public final class ViewHolderChild {
        private TextView time;
        private TextView index;
        private TextView league;
        private ImageView showAnalyse;
        private LinearLayout layout_index;
        private RelativeLayout analyse_all;
        private Button to_detail;
        private TextView firstTeam;
        private TextView concedePoint;
        private TextView secondTeam;
        private Button[] btns = new Button[3];
        private ImageView[] imgs = new ImageView[3];
        private TextView[] tvs = new TextView[9];
        private String[] betRe = {"胜", "平", "负"};
        private int[] btns_id = {R.id.win, R.id.draw, R.id.lose};
        private int[] imgs_id = {R.id.img_01, R.id.img_02, R.id.img_03};
        private int[] tvs_id = {R.id.tv_01, R.id.tv_02, R.id.tv_03, R.id.tv_04, R.id.tv_05, R.id.tv_06,
                R.id.tv_07, R.id.tv_08, R.id.tv_09};
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final ViewHolderChild viewHolder;
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_spf_item_view, null);
            viewHolder = new ViewHolderChild();
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.showAnalyse = (ImageView) convertView.findViewById(R.id.show_analyse);
            viewHolder.layout_index = (LinearLayout) convertView.findViewById(R.id.layout_index);
            viewHolder.analyse_all = (RelativeLayout) convertView.findViewById(R.id.analyse_all);
            viewHolder.to_detail = (Button) convertView.findViewById(R.id.to_detail);
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.concedePoint = (TextView) convertView.findViewById(R.id.concede_point);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
            for (int i = 0; i < viewHolder.btns.length; i++) {
                viewHolder.btns[i] = (Button) convertView.findViewById(viewHolder.btns_id[i]);
                viewHolder.imgs[i] = (ImageView) convertView.findViewById(viewHolder.imgs_id[i]);
            }
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i] = (TextView) convertView.findViewById(viewHolder.tvs_id[i]);
            }
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderChild) convertView.getTag();
        }
// if (childPosition % 2 == 0) {
// convertView.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
// }
// else {
// convertView.setBackgroundResource(R.drawable.user_detail_inf_single);
// }
        final SportsItem sportsItem = list.get(groupPosition).getItemList().get(childPosition);
        viewHolder.time.setText(sportsItem.getEndTime().subSequence(11, 16) + "截止");
        viewHolder.index.setText(sportsItem.getIdBetNum());
        if (sportsItem.isIfShowAnalyse()) {
            viewHolder.analyse_all.setVisibility(View.VISIBLE);
            viewHolder.showAnalyse.setImageResource(R.drawable.up_arrow);
            // 分析部分
            for (int i = 0; i < 3; i++) {
                viewHolder.tvs[i].setText(list.get(groupPosition).getItemList().get(childPosition).getHisCom(i));
                viewHolder.tvs[i + 3].setText(list.get(groupPosition).getItemList().get(childPosition).getEveOdds(i));
            }
            viewHolder.tvs[6].setText(list.get(groupPosition).getItemList().get(childPosition).getRank(0));
            viewHolder.tvs[8].setText(list.get(groupPosition).getItemList().get(childPosition).getRank(1));
        }
        else {
            viewHolder.analyse_all.setVisibility(View.GONE);
            viewHolder.showAnalyse.setImageResource(R.drawable.down_arrow);
        }
        viewHolder.layout_index.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (viewHolder.analyse_all.getVisibility() == View.VISIBLE) {
                    list.get(groupPosition).getItemList().get(childPosition).setIfShowAnalyse(false);
                    viewHolder.analyse_all.setVisibility(View.GONE);
                    viewHolder.showAnalyse.setImageResource(R.drawable.down_arrow);
                }
                else {
                    submitAnalyseShowSportCondtion();
                    list.get(groupPosition).getItemList().get(childPosition).setIfShowAnalyse(true);
                    viewHolder.analyse_all.setVisibility(View.VISIBLE);
                    viewHolder.showAnalyse.setImageResource(R.drawable.up_arrow);
                    // 分析部分
                    if (null != list.get(groupPosition).getItemList().get(childPosition).getHisCom(0) &&
                        !list.get(groupPosition).getItemList().get(childPosition).getHisCom(0).equals("")) {
                        for (int i = 0; i < 3; i++) {
                            viewHolder.tvs[i].setText(list.get(groupPosition).getItemList().get(childPosition).getHisCom(i));
                            viewHolder.tvs[i + 3].setText(list.get(groupPosition).getItemList().get(childPosition).getEveOdds(i));
                        }
                        viewHolder.tvs[6].setText(list.get(groupPosition).getItemList().get(childPosition).getRank(0));
                        viewHolder.tvs[8].setText(list.get(groupPosition).getItemList().get(childPosition).getRank(1));
                    }
                    else {
                        SportsSimpleAnalyseTask task =
                            new SportsSimpleAnalyseTask(context, sportsItem.getId(), "jczq");
                        task.setOnGetSportsSimpleAnalyseInf(new OnGetSportsSimpleAnalyseInf() {

                            @Override
                            public void onPre() {
// viewHolder.info.setVisibility(View.GONE);
                                for (int i = 0; i < 3; i++) {
                                    viewHolder.tvs[i].setText("");
                                    viewHolder.tvs[i + 3].setText("");
                                }
                                viewHolder.tvs[6].setText("");
                                viewHolder.tvs[8].setText("");
                            }

                            @Override
                            public void onPost(String json) {
                                if (json == null) {
                                    searchFail();
                                }
                                else {
                                    JsonAnalyse analyse = new JsonAnalyse();
                                    String status = analyse.getStatus(json);
                                    if (status.equals("200")) {
                                        String data = analyse.getData(json, "response_data");
                                        try {
                                            // 历史交锋
                                            String duizhen = analyse.getData(data, "duizheng");
                                            if (null != duizhen && !duizhen.equals("")) {
                                                String[] hisCom = new String[3];
                                                String[] temp01 = duizhen.split("\\#");
                                                StringBuilder sb0 = new StringBuilder();
                                                sb0.append(temp01[0] + "胜" + temp01[2] + "负");
                                                viewHolder.tvs[0].setText(sb0.toString());
                                                hisCom[0] = sb0.toString();

                                                StringBuilder sb1 = new StringBuilder();
                                                sb1.append(temp01[1] + "平");
                                                viewHolder.tvs[1].setText(sb1.toString());
                                                hisCom[1] = sb1.toString();

                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append(temp01[2] + "胜" + temp01[0] + "负");
                                                viewHolder.tvs[2].setText(sb2.toString());
                                                hisCom[2] = sb2.toString();

                                                list.get(groupPosition).getItemList().get(childPosition).setHisCom(hisCom);// 保存历史交锋数据
                                            }
                                            else {
                                                for (int i = 0; i < 3; i++) {
                                                    list.get(groupPosition).getItemList().get(childPosition).setHisCom(i,
                                                                                                                       "-");
                                                }
                                            }
                                            // 平均赔率
                                            try {
                                                String sheng = analyse.getData(data, "sheng");
                                                String ping = analyse.getData(data, "ping");
                                                String fu = analyse.getData(data, "fu");
                                                if (null != sheng && !sheng.equals("")) {
                                                    viewHolder.tvs[3].setText(sheng);
                                                }
                                                else {
                                                    viewHolder.tvs[3].setText("-");
                                                }
                                                if (null != ping && !ping.equals("")) {
                                                    viewHolder.tvs[4].setText(ping);
                                                }
                                                else {
                                                    viewHolder.tvs[4].setText("-");
                                                }
                                                if (null != fu && !fu.equals("")) {
                                                    viewHolder.tvs[5].setText(fu);
                                                }
                                                else {
                                                    viewHolder.tvs[5].setText("-");
                                                }

                                                String[] eveOdds = new String[3];
                                                eveOdds[0] = sheng;
                                                eveOdds[1] = ping;
                                                eveOdds[2] = fu;
                                                list.get(groupPosition).getItemList().get(childPosition).setEveOdds(eveOdds);
                                            }
                                            catch (Exception e) {
                                                for (int i = 0; i < 3; i++) {
                                                    list.get(groupPosition).getItemList().get(childPosition).setEveOdds(i,
                                                                                                                        "-");
                                                }
                                            }

                                            // 两队排名
                                            try {
                                                String zhupai = analyse.getData(data, "zhupai");
                                                String kepai = analyse.getData(data, "kepai");
                                                if (null != zhupai && !zhupai.equals("")) {
                                                    viewHolder.tvs[6].setText(zhupai);
                                                }
                                                else {
                                                    viewHolder.tvs[6].setText("-");
                                                }
                                                if (null != kepai && !kepai.equals("")) {
                                                    viewHolder.tvs[8].setText(kepai);
                                                }
                                                else {
                                                    viewHolder.tvs[8].setText("-");
                                                }

                                                String[] rank = new String[2];
                                                rank[0] = zhupai;
                                                rank[1] = kepai;
                                                list.get(groupPosition).getItemList().get(childPosition).setRank(rank);
                                            }
                                            catch (Exception e) {
                                                for (int i = 0; i < 2; i++) {
                                                    list.get(groupPosition).getItemList().get(childPosition).setRank(i,
                                                                                                                     "-");
                                                }
                                            }
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                            searchFail();
                                        }
                                    }
                                    else if (status.equals("302")) {
                                        searchFail();
                                    }
                                    else if (status.equals("304")) {
                                        searchFail();
                                    }
                                    else {
                                        searchFail();
                                    }

                                }
                            }

                            private void searchFail() {
// viewHolder.info.setVisibility(View.VISIBLE);
                            }
                        });
                        task.execute();
                    }
                }
            }
        });
        viewHolder.to_detail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("game_id", sportsItem.getId());
                bundle.putString("master_name", sportsItem.getMatchHomeTeamName());
                bundle.putString("guester_name", sportsItem.getMatchGuessTeamName());
                bundle.putString("leage", sportsItem.getLeague());
                intent.putExtras(bundle);
                intent.setClass(context, JczqAnalyse.class);
                context.startActivity(intent);
            }
        });
        viewHolder.league.setText(sportsItem.getLeague());
        viewHolder.firstTeam.setText(sportsItem.getMatchHomeTeamName());
        if (sportsItem.getConcede().equals("0")) {
            viewHolder.concedePoint.setTextColor(context.getResources().getColor(R.color.dark_purple));
            viewHolder.concedePoint.setText("vs");
        }
        else {
            if (Integer.valueOf(sportsItem.getConcede()) > 0) {
                viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#CD2626\">(" +
                    sportsItem.getConcede() + ")</font>vs"));
            }
            else {
                viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#008000\">(" +
                    sportsItem.getConcede() + ")</font>vs"));
            }
        }
        viewHolder.secondTeam.setText(sportsItem.getMatchGuessTeamName());
        for (int i = 0; i < viewHolder.btns.length; i++) {

            viewHolder.btns[i].setOnClickListener(new ClickAction(groupPosition, childPosition, i,
                                                                  viewHolder.imgs[i]));

            if (!sportsItem.getStatus(i)) {
                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\">" + viewHolder.betRe[i] +
                    "</font><br>" + "<font color=\"#DB0010\">" + sportsItem.getOdds()[i] +
                    "</font>"));
                viewHolder.btns[i].setBackgroundResource(R.drawable.custom_button_redside_unselected);
                viewHolder.imgs[i].setVisibility(View.GONE);
            }
            else {
                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\">" + viewHolder.betRe[i] +
                    "<br>" + sportsItem.getOdds()[i] +
                    "</font>"));
                viewHolder.btns[i].setBackgroundResource(R.drawable.custom_button_redside_normal);
//                viewHolder.imgs[i].setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    /**
     * 提交竞彩足球胜平负数据统计信息
     */
    public void submitAnalyseShowSportCondtion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "football");
        map.put("more_inf", "football spf");
        String eventName = "sport click show team condtion";
        FlurryAgent.onEvent(eventName, map);
        String eventName1 = "sport_click_show_team_condtion";
        MobclickAgent.onEvent(context, eventName1, "football spf");
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
            SportsItem item = list.get(groupPosition).getItemList().get(childPosition);
            if (!item.getStatus(index)) {
                button.setText(Html.fromHtml("<font color=\"#A8A8A8\">" + betRe[index] +
                                                         "</font><br>" + "<font color=\"#DB0010\">" + item.getOdds()[index] +
                                                         "</font>"));
                button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
                img.setVisibility(View.GONE);
            }
            else {
                button.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + betRe[index] +
                                             "<br>" + item.getOdds()[index] +
                                             "</font>"));
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
