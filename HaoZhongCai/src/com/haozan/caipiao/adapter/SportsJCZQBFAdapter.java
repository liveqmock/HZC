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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;

public class SportsJCZQBFAdapter
    extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<SportsSFCBetGroup> list;
    private OnBallClickListener clickListener;

    public SportsJCZQBFAdapter(Context context, ArrayList<SportsSFCBetGroup> list) {
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
        private TextView firstTeam;
        private TextView concedePoint;
        private TextView secondTeam;
        private Button show;
        private LinearLayout ll_sub;
        private RadioGroup rg;
        private RadioButton r1;
        private RadioButton r2;
        private RadioButton r3;
        private RadioButton[] rdbtns = new RadioButton[3];
        private Button[] btns = new Button[14];
        private ImageView[] imgs = new ImageView[14];
        private int[] btns_id = {R.id.bf_01, R.id.bf_02, R.id.bf_03, R.id.bf_04, R.id.bf_05, R.id.bf_06,
                R.id.bf_07, R.id.bf_08, R.id.bf_09, R.id.bf_10, R.id.bf_11, R.id.bf_12, R.id.bf_13,
                R.id.bf_16};
        private int[] imgs_id = {R.id.img_01, R.id.img_02, R.id.img_03, R.id.img_04, R.id.img_05,
                R.id.img_06, R.id.img_07, R.id.img_08, R.id.img_09, R.id.img_10, R.id.img_11, R.id.img_12,
                R.id.img_13, R.id.img_16};
        private LinearLayout l1;
        private LinearLayout l2;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final ViewHolderChild viewHolder;
        final String[] winStr =
            {"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他"};
        final String[] pingStr = {"0:0", "1:1", "2:2", "3:3", "平其他"};
        final String[] lostStr =
            {"0:1", "0:2", "0:3", "0:4", "0:5", "1:2", "1:3", "1:4", "1:5", "2:3", "2:4", "2:5", "负其他"};
        final String[] allStr =
            {"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他",
                    "0:0", "1:1", "2:2", "3:3", "平其他", "0:1", "0:2", "0:3", "0:4", "0:5", "1:2", "1:3",
                    "1:4", "1:5", "2:3", "2:4", "2:5", "负其他"};
        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_jczq_bf_item_view, null);
            viewHolder = new ViewHolderChild();
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.concedePoint = (TextView) convertView.findViewById(R.id.concede_point);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
            viewHolder.show = (Button) convertView.findViewById(R.id.show);
            viewHolder.ll_sub = (LinearLayout) convertView.findViewById(R.id.ll_bf_sub);
            for (int i = 0; i < viewHolder.btns.length; i++) {
                viewHolder.btns[i] = (Button) convertView.findViewById(viewHolder.btns_id[i]);
                viewHolder.imgs[i] = (ImageView) convertView.findViewById(viewHolder.imgs_id[i]);
            }
            viewHolder.l1 = (LinearLayout) convertView.findViewById(R.id.ll_jczq_bf_btns_1);
            viewHolder.l2 = (LinearLayout) convertView.findViewById(R.id.ll_jczq_bf_btns_2);
            viewHolder.rdbtns[0] = (RadioButton) convertView.findViewById(R.id.bf_radio1);
            viewHolder.rdbtns[1] = (RadioButton) convertView.findViewById(R.id.bf_radio2);
            viewHolder.rdbtns[2] = (RadioButton) convertView.findViewById(R.id.bf_radio3);
            viewHolder.rg = (RadioGroup) convertView.findViewById(R.id.bf_radiogroup);

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
        // 初始化“展开比分投注区”button的显示内容
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < allStr.length; i++) {
            if (sportsItem.getStatus(i)) {
                str.append(allStr[i] + ",");
            }
        }
        if (str.toString() != null && !str.toString().equals("")) {
            str.deleteCharAt(str.length() - 1);
            list.get(groupPosition).getItemList().get(childPosition).setShowStr(str.toString());
        }
        if (!sportsItem.isJczqBfIsShown()) {
            viewHolder.ll_sub.setVisibility(View.GONE);
            mainBtnShow(sportsItem.getShowStr(), "展开比分投注区", viewHolder.show);
            viewHolder.show.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
        }
        else {
            viewHolder.ll_sub.setVisibility(View.VISIBLE);
            mainBtnShow(sportsItem.getShowStr(), "收起比分投注区", viewHolder.show);
            viewHolder.show.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        }
        viewHolder.show.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (list.get(groupPosition).getItemList().get(childPosition).isJczqBfIsShown()) {
                    viewHolder.ll_sub.setVisibility(View.GONE);
                    list.get(groupPosition).getItemList().get(childPosition).setJczqBfIsShown(false);
                    viewHolder.show.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
                    mainBtnShow(sportsItem.getShowStr(), "展开比分投注区", viewHolder.show);
                }
                else {
                    list.get(groupPosition).getItemList().get(childPosition).setJczqBfIsShown(true);
                    viewHolder.ll_sub.setVisibility(View.VISIBLE);
                    viewHolder.show.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    mainBtnShow(sportsItem.getShowStr(), "收起比分投注区", viewHolder.show);
                }

            }
        });

        viewHolder.time.setText(sportsItem.getEndTime().subSequence(11, 16) + "截止");
        viewHolder.index.setText(sportsItem.getIdBetNum());
        viewHolder.league.setText(sportsItem.getLeague());
        viewHolder.firstTeam.setText(sportsItem.getMatchHomeTeamName());
        // TODO
        viewHolder.concedePoint.setTextColor(context.getResources().getColor(R.color.dark_purple));
        viewHolder.concedePoint.setText("vs");

        viewHolder.secondTeam.setText(sportsItem.getMatchGuessTeamName());
        viewHolder.rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.bf_radio1:
                        viewHolder.l1.setVisibility(View.VISIBLE);
                        viewHolder.l2.setVisibility(View.VISIBLE);
                        list.get(groupPosition).getItemList().get(childPosition).setType(0, true);
                        list.get(groupPosition).getItemList().get(childPosition).setType(1, false);
                        list.get(groupPosition).getItemList().get(childPosition).setType(2, false);

                        for (int i = 0; i < viewHolder.btns.length - 1; i++) {
                            viewHolder.btns[i].setOnClickListener(new ClickAction(groupPosition,
                                                                                  childPosition, i,
                                                                                  viewHolder.show,
                                                                                  viewHolder.btns[13],
                                                                                  viewHolder.imgs[i],
                                                                                  viewHolder.imgs[13]));
                            if (!sportsItem.getStatus(i)) {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" +
                                    winStr[i] +
                                    "</big></font><br>" +
                                    "<font color=\"#DB0010\">" +
                                    sportsItem.getOdds()[i].subSequence(0,
                                                                        sportsItem.getOdds()[i].length() - 1) +
                                    "</font>"));
                            }
                            else {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" +
                                    winStr[i] +
                                    "</big><br>" +
                                    sportsItem.getOdds()[i].subSequence(0,
                                                                        sportsItem.getOdds()[i].length() - 1) +
                                    "</font>"));
                            }
                            btnShow(sportsItem.getStatus(i), viewHolder.btns[i], viewHolder.imgs[i]);
                        }
                        boolean isAll1 = true;
                        for (int i = 0; i < 13; i++) {
                            if (!sportsItem.getStatus(i)) {
                                list.get(groupPosition).getItemList().get(childPosition).setStatus(31, false);
                                isAll1 = false;
                                break;
                            }
                        }
                        if (isAll1) {
                            list.get(groupPosition).getItemList().get(childPosition).setStatus(31, true);
                        }
                        btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(31),
                                  viewHolder.btns[13], viewHolder.imgs[13]);
                        break;
                    case R.id.bf_radio2:
                        viewHolder.l1.setVisibility(View.GONE);
                        viewHolder.l2.setVisibility(View.GONE);
                        list.get(groupPosition).getItemList().get(childPosition).setType(0, false);
                        list.get(groupPosition).getItemList().get(childPosition).setType(1, true);
                        list.get(groupPosition).getItemList().get(childPosition).setType(2, false);
                        for (int i = 8; i < 13; i++) {
                            viewHolder.btns[i].setOnClickListener(new ClickAction(groupPosition,
                                                                                  childPosition, i + 5,
                                                                                  viewHolder.show,
                                                                                  viewHolder.btns[13],
                                                                                  viewHolder.imgs[i],
                                                                                  viewHolder.imgs[13]));
                            if (!sportsItem.getStatus(i + 5)) {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" +
                                    pingStr[i - 8] +
                                    "</big></font><br>" +
                                    "<font color=\"#DB0010\">" +

                                    sportsItem.getOdds()[i + 5].subSequence(0,
                                                                            sportsItem.getOdds()[i + 5].length() - 1) +
                                    "</font>"));
                            }
                            else {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" +
                                    pingStr[i - 8] +
                                    "</big><br>" +
                                    sportsItem.getOdds()[i + 5].subSequence(0,
                                                                            sportsItem.getOdds()[i + 5].length() - 1) +
                                    "</font>"));
                            }
                            btnShow(sportsItem.getStatus(i + 5), viewHolder.btns[i], viewHolder.imgs[i]);
                        }
                        boolean isAll2 = true;
                        for (int i = 13; i < 18; i++) {
                            if (!sportsItem.getStatus(i)) {
                                list.get(groupPosition).getItemList().get(childPosition).setStatus(32, false);
                                isAll2 = false;
                                break;
                            }
                        }
                        if (isAll2) {
                            list.get(groupPosition).getItemList().get(childPosition).setStatus(32, true);
                        }
                        btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(32),
                                  viewHolder.btns[13], viewHolder.imgs[13]);
                        break;
                    case R.id.bf_radio3:
                        viewHolder.l1.setVisibility(View.VISIBLE);
                        viewHolder.l2.setVisibility(View.VISIBLE);
                        list.get(groupPosition).getItemList().get(childPosition).setType(0, false);
                        list.get(groupPosition).getItemList().get(childPosition).setType(1, false);
                        list.get(groupPosition).getItemList().get(childPosition).setType(2, true);
                        for (int i = 0; i < viewHolder.btns.length - 1; i++) {
                            viewHolder.btns[i].setOnClickListener(new ClickAction(groupPosition,
                                                                                  childPosition, i + 18,
                                                                                  viewHolder.show,
                                                                                  viewHolder.btns[13],
                                                                                  viewHolder.imgs[i],
                                                                                  viewHolder.imgs[13]));
                            if (!sportsItem.getStatus(i + 18)) {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" +
                                    lostStr[i] +
                                    "</big></font><br>" +
                                    "<font color=\"#DB0010\">" +

                                    sportsItem.getOdds()[i + 18].subSequence(0,
                                                                             sportsItem.getOdds()[i + 18].length() - 1) +
                                    "</font>"));
                            }
                            else {
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" +
                                    lostStr[i] +
                                    "</big><br>" +
                                    sportsItem.getOdds()[i + 18].subSequence(0,
                                                                             sportsItem.getOdds()[i + 18].length() - 1) +
                                    "</font>"));
                            }
                            btnShow(sportsItem.getStatus(i + 18), viewHolder.btns[i], viewHolder.imgs[i]);
                        }
                        boolean isAll3 = true;
                        for (int i = 18; i < 31; i++) {
                            if (!sportsItem.getStatus(i)) {
                                list.get(groupPosition).getItemList().get(childPosition).setStatus(33, false);
                                isAll3 = false;
                                break;
                            }
                        }
                        if (isAll3) {
                            list.get(groupPosition).getItemList().get(childPosition).setStatus(33, true);
                        }
                        btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(33),
                                  viewHolder.btns[13], viewHolder.imgs[13]);
                        break;
                    default:
                        break;
                }
            }
        });
        viewHolder.btns[13].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int[] num1 = {31, 32, 33};
                int[] num2 = {0, 8, 0};
                int[] num3 = {viewHolder.btns.length - 1, 13, viewHolder.btns.length - 1};
                int[] num4 = {0, 5, 18};
                for (int j = 0; j < 3; j++) {
                    if (viewHolder.rdbtns[j].isChecked()) {
                        if (!sportsItem.getStatus(num1[j])) {
                            for (int i = num2[j]; i < num3[j]; i++) {
                                if (!list.get(groupPosition).getItemList().get(childPosition).getStatus(i +
                                                                                                            num4[j])) {
                                    if (!clickListener.checkClick(groupPosition, childPosition, i + num4[j]))
                                        return;
                                    viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" +
                                        winStr[i] +
                                        "</big><br>" +
                                        sportsItem.getOdds()[i].subSequence(0,
                                                                            sportsItem.getOdds()[i].length() - 1) +
                                        "</font>"));
                                    viewHolder.btns[i].setBackgroundResource(R.drawable.custom_button_redside_normal);
//                                    viewHolder.imgs[i].setVisibility(View.VISIBLE);
                                }
                            }
                            viewHolder.btns[13].setBackgroundResource(R.drawable.custom_button_redside_normal);
//                            viewHolder.imgs[13].setVisibility(View.VISIBLE);
                            viewHolder.btns[13].setTextColor(context.getResources().getColor(R.color.white));
                            viewHolder.btns[13].setText("反选");
                        }
                        else {
                            for (int i = num2[j]; i < num3[j]; i++) {
                                list.get(groupPosition).getItemList().get(childPosition).setStatus(i +
                                                                                                       num4[j],
                                                                                                   true);
                                if (!clickListener.checkClick(groupPosition, childPosition, i + num4[j]))
                                    return;
                                viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" +
                                    winStr[i] +
                                    "</big></font><br>" +
                                    "<font color=\"#DB0010\">" +
                                    sportsItem.getOdds()[i].subSequence(0,
                                                                        sportsItem.getOdds()[i].length() - 1) +
                                    "</font>"));

                                viewHolder.btns[i].setBackgroundResource(R.drawable.custom_button_redside_unselected);
                                viewHolder.imgs[i].setVisibility(View.GONE);
                            }
                            viewHolder.btns[13].setBackgroundResource(R.drawable.custom_button_redside_unselected);
                            viewHolder.imgs[13].setVisibility(View.GONE);
                            viewHolder.btns[13].setTextColor(context.getResources().getColor(R.color.dark_purple));
                            viewHolder.btns[13].setText("全选");
                        }

                        if (!clickListener.checkClick(groupPosition, childPosition, num1[j]))
                            return;

                        break;
                    }
                }

                // 修改“show”button显示内容
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < allStr.length; i++) {
                    if (sportsItem.getStatus(i)) {
                        str.append(allStr[i] + ",");
                    }
                }
                if (str.toString() != null && !str.toString().equals("")) {
                    str.deleteCharAt(str.length() - 1);
                    list.get(groupPosition).getItemList().get(childPosition).setShowStr(str.toString());
                }
                else {
                    list.get(groupPosition).getItemList().get(childPosition).setShowStr(null);
                }
                mainBtnShow(str.toString(), "收起比分投注区", viewHolder.show);
            }
        });
        // 初始化显示的内容
        int[] num1 = {31, 32, 33};
        int[] num2 = {0, 8, 0};
        int[] num3 = {viewHolder.btns.length - 1, 13, viewHolder.btns.length - 1};
        int[] num4 = {0, 5, 18};
        int[] num5 = {0, 13, 18};
        int[] num6 = {13, 18, 31};
        for (int k = 0; k < 3; k++) {
            if (list.get(groupPosition).getItemList().get(childPosition).getType(k)) {
                viewHolder.rdbtns[k].setChecked(true);
            }
        }
        for (int j = 0; j < 3; j++) {
            if (viewHolder.rdbtns[j].isChecked()) {
                for (int i = num2[j]; i < num3[j]; i++) {
                    viewHolder.btns[i].setOnClickListener(new ClickAction(groupPosition, childPosition, i +
                        num4[j], viewHolder.show, viewHolder.btns[13], viewHolder.imgs[i],
                                                                          viewHolder.imgs[13]));
                    if (!sportsItem.getStatus(i + num4[j])) {
                        viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" +
                            allStr[i + num4[j]] +
                            "</big></font><br>" +
                            "<font color=\"#DB0010\">" +
                            sportsItem.getOdds()[i + num4[j]].subSequence(0,
                                                                          sportsItem.getOdds()[i + num4[j]].length() - 1) +
                            "</font>"));
                    }
                    else {

                        viewHolder.btns[i].setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" +
                            allStr[i + num4[j]] +
                            "</big><br>" +
                            sportsItem.getOdds()[i + num4[j]].subSequence(0,
                                                                          sportsItem.getOdds()[i + num4[j]].length() - 1) +
                            "</font>"));

                    }

                    btnShow(sportsItem.getStatus(i + num4[j]), viewHolder.btns[i], viewHolder.imgs[i]);
                }

                boolean isAll = true;
                for (int i = num5[j]; i < num6[j]; i++) {
                    if (!sportsItem.getStatus(i)) {
                        list.get(groupPosition).getItemList().get(childPosition).setStatus(num1[j], false);
                        isAll = false;
                        break;
                    }
                }
                if (isAll) {
                    list.get(groupPosition).getItemList().get(childPosition).setStatus(num1[j], true);
                }
                btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(num1[j]),
                          viewHolder.btns[13], viewHolder.imgs[13]);

                break;
            }
        }

        return convertView;
    }

    protected void btnShow(boolean status, Button button, ImageView img) {
        if (!status) {
            button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            img.setVisibility(View.GONE);
        }
        else {
            button.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            img.setVisibility(View.VISIBLE);
        }
    }

    private void btn13Show(boolean status, Button button, ImageView imgAll) {
        if (status) {
            button.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            imgAll.setVisibility(View.VISIBLE);
            button.setTextColor(context.getResources().getColor(R.color.white));
            button.setText("反选");
        }
        else {
            button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            imgAll.setVisibility(View.GONE);
            button.setTextColor(context.getResources().getColor(R.color.dark_purple));
            button.setText("全选");
        }
    }

    private void mainBtnShow(String string1, String string2, Button show) {
        if (string1 != null && !string1.equals("")) {
            show.setText(string1);
            show.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            show.setTextColor(context.getResources().getColor(R.color.white));
        }
        else {
            show.setText(string2);
            show.setBackgroundResource(R.drawable.custom_button);
            show.setTextColor(context.getResources().getColor(R.color.dark_purple));
        }
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
        private Button show;
        private Button allBtns;
        private ImageView img;
        private ImageView imgAll;
        public StringBuilder str = null;
        private String[] allStr = {"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0",
                "5:1", "5:2", "胜其他", "0:0", "1:1", "2:2", "3:3", "平其他", "0:1", "0:2", "0:3", "0:4", "0:5",
                "1:2", "1:3", "1:4", "1:5", "2:3", "2:4", "2:5", "负其他"};

        public ClickAction(int groupPosition, int childPosition, int index, Button show, Button allBtns,
                           ImageView img, ImageView imgAll) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
            this.index = index;
            this.show = show;
            this.allBtns = allBtns;
            this.img = img;
            this.imgAll = imgAll;
        }

        @Override
        public void onClick(View v) {
            if (!clickListener.checkClick(groupPosition, childPosition, index))
                return;
            Button button = (Button) v;
            SportsItem item = list.get(groupPosition).getItemList().get(childPosition);
            if (!item.getStatus(index)) {
                button.setText(Html.fromHtml("<font color=\"#A8A8A8\"><big>" + allStr[index] +
                    "</big></font><br>" + "<font color=\"#DB0010\">" +
                    item.getOdds()[index].subSequence(0, item.getOdds()[index].length() - 1) + "</font>"));

                button.setBackgroundResource(R.drawable.custom_button_redside_unselected);
                img.setVisibility(View.GONE);
            }
            else {
                button.setText(Html.fromHtml("<font color=\"#FFFFFF\"><big>" + allStr[index] + "</big><br>" +
                    item.getOdds()[index].subSequence(0, item.getOdds()[index].length() - 1) + "</font>"));

                button.setBackgroundResource(R.drawable.custom_button_redside_normal);
//                img.setVisibility(View.VISIBLE);
            }
            str = new StringBuilder();
            for (int i = 0; i < allStr.length; i++) {
                if (item.getStatus(i)) {
                    str.append(allStr[i] + ",");
                }
            }
            if (str.toString() != null && !str.toString().equals("")) {
                str.deleteCharAt(str.length() - 1);
            }
            mainBtnShow(str.toString(), "收起比分投注区", show);
            list.get(groupPosition).getItemList().get(childPosition).setShowStr(str.toString());

            list.get(groupPosition).getItemList().get(childPosition).setShowStr(str.toString());
            if (index < 13) {
                boolean isAll = true;
                for (int i = 0; i < 13; i++) {
                    if (!item.getStatus(i)) {
                        list.get(groupPosition).getItemList().get(childPosition).setStatus(31, false);
                        isAll = false;
                        break;
                    }
                }
                if (isAll) {
                    list.get(groupPosition).getItemList().get(childPosition).setStatus(31, true);
                }
                btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(31), allBtns,
                          imgAll);
            }
            else if (index > 12 && index < 18) {
                boolean isAll = true;
                for (int i = 13; i < 18; i++) {
                    if (!item.getStatus(i)) {
                        list.get(groupPosition).getItemList().get(childPosition).setStatus(32, false);
                        isAll = false;
                        break;
                    }
                }
                if (isAll) {
                    list.get(groupPosition).getItemList().get(childPosition).setStatus(32, true);
                }
                btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(32), allBtns,
                          imgAll);
            }
            else if (index > 17) {
                boolean isAll = true;
                for (int i = 18; i < 31; i++) {
                    if (!item.getStatus(i)) {
                        list.get(groupPosition).getItemList().get(childPosition).setStatus(33, false);
                        isAll = false;
                        break;
                    }
                }
                if (isAll) {
                    list.get(groupPosition).getItemList().get(childPosition).setStatus(33, true);
                }
                btn13Show(list.get(groupPosition).getItemList().get(childPosition).getStatus(33), allBtns,
                          imgAll);
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
