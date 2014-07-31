package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.bet.BetDigitalBasic;
import com.haozan.caipiao.types.Ball;
import com.haozan.caipiao.types.OpenHistoryRequest;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.PredicateLayout;

public class MyExpandableListAdapter
    extends BaseExpandableListAdapter {
    private int mHideGroupPos = -1;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ArrayList<OpenHistoryRequest>> list;
    private String kind;
    private int selectItem = 0;
    private boolean isExpan;
    private ArrayList<View> viewList;
    private TextView textView;
    private TextView indicator;
    private ImageView imageView;
    private LinearLayout listLinear;
    private TextView award_tv;
    private ImageView award_icon;
// int po;
    int num;

    public void setSelectItem(int selectItem, boolean isExpan) {
        this.selectItem = selectItem;
        this.isExpan = isExpan;
    }

    public MyExpandableListAdapter(Context context, ArrayList<ArrayList<OpenHistoryRequest>> list,
                                   String kind, LayoutInflater inflater, String[] grounpName) {
        this.context = context;
        this.inflater = inflater;
        this.list = list;
        this.kind = kind;
        viewList = new ArrayList<View>();
// for (int i = 0; i < list.size(); i++)
// isExpan[i] = false;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).get(childPosition);
// return children[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).size();
// return children[groupPosition].length;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        convertView = inflater.inflate(R.layout.open_history_item_view, null);
        OpenHistoryRequest openRecord = list.get(groupPosition).get(childPosition);
        String openTerm = openRecord.getTerm();
        String openDate = openRecord.getOpenDate();
        String normalNum = openRecord.getNormalNum();
        String specialNum = openRecord.getSpecialNum();
        String opens = normalNum + "|" + specialNum;

        TextView term = (TextView) convertView.findViewById(R.id.term);
        TextView openInfViewIndex = (TextView) convertView.findViewById(R.id.list_index);
        term.setText("第  " + openTerm + " 期");

        TextView time = (TextView) convertView.findViewById(R.id.time);
        time.setText(openDate);

        PredicateLayout openBallsView = (PredicateLayout) convertView.findViewById(R.id.open_balls);
        openBallsView.removeAllViewsInLayout();
        LotteryUtils.drawHallBalls(context, openBallsView, matchBall(opens), kind);
        openInfViewIndex.setText(String.valueOf(childPosition + 1));
        return convertView;
    }

    private String awardTitle(int red_ball_num, int blue_ball_num) {
        if (kind.equals("ssq")) {
            if (red_ball_num == 6 && blue_ball_num == 1)
                return "一等奖";
            else if (red_ball_num == 6 && blue_ball_num == 0)
                return "二等奖";
            else if (red_ball_num == 5 && blue_ball_num == 1)
                return "三等奖";
            else if ((red_ball_num == 5 && blue_ball_num == 0) || (red_ball_num == 4 && blue_ball_num == 1))
                return "四等奖";
            else if (red_ball_num == 4 && blue_ball_num == 0 || (red_ball_num == 3 && blue_ball_num == 1))
                return "五等奖";
            else if (red_ball_num == 2 && blue_ball_num == 1 || (red_ball_num == 1 && blue_ball_num == 1) ||
                (red_ball_num == 0 && blue_ball_num == 1))
                return "六等奖";
        }
        else if (kind.equals("dlt")) {
            if (red_ball_num == 5 && blue_ball_num == 2)
                return "一等奖";
            else if (red_ball_num == 5 && blue_ball_num == 1)
                return "二等奖";
            else if (red_ball_num == 5 && blue_ball_num == 0)
                return "三等奖";
            else if (red_ball_num == 4 && blue_ball_num == 2)
                return "四等奖";
            else if (red_ball_num == 4 && blue_ball_num == 1)
                return "五等奖";
            else if (red_ball_num == 4 && blue_ball_num == 0 || (red_ball_num == 3 && blue_ball_num == 2))
                return "六等奖";
            else if (red_ball_num == 3 && blue_ball_num == 1 || (red_ball_num == 2 && blue_ball_num == 2))
                return "七等奖";
            else if (red_ball_num == 1 && blue_ball_num == 2 || (red_ball_num == 2 && blue_ball_num == 1) ||
                (red_ball_num == 0 && blue_ball_num == 2))
                return "八等奖";
        }
        return "";
    }

    public String[] getGroup(int groupPosition) {
        String name = list.get(groupPosition).get(0).getType();
        String name1 = null;
        String name2 = null;
        String []name3 = new String[2];
        if (kind.equals("ssq")) {
            name1 = name.substring(0, 1);
            name2 = name.substring(1);
            name3[0] =
                "红球:" + name1 + "   蓝球:" + name2 + "   命中次数:" + list.get(groupPosition).size();
            name3[1] = awardTitle(Integer.valueOf(name1), Integer.valueOf(name2));

        }
        if (kind.equals("3d")) {
            if (name.equals("4")) {
                name3[0] = "正彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else if (name.equals("3")) {
                name3[0] = "组彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else {
                name3[0] = "对上了" + name + "个号码   命中次数:" + list.get(groupPosition).size();
            }
        }
        if (kind.equals("qlc") || kind.equals("swxw")) {
            name3[0] = "对上了" + name + "号码   命中次数:" + list.get(groupPosition).size();
        }
        if (kind.equals("dfljy")) {
            name1 = name.substring(0, 1);
            name2 = name.substring(1);
            name3[0] = "红球:" + name1 + "  生肖:" + name2 + "   命中次数:" + list.get(groupPosition).size();
        }
        if (kind.equals("ssl")) {
            if (name.equals("4")) {
                name3[0] = "正彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else if (name.equals("3")) {
                name3[0] = "组彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else {
                name3[0] = "对上了" + name + "个号码   命中次数:" + list.get(groupPosition).size();
            }
        }
        if (kind.equals("dlt")) {
            name1 = name.substring(0, 1);
            name2 = name.substring(1);
            name3[0] =
                "红球:" + name1 + "   蓝球:" + name2 + "   命中次数:" + list.get(groupPosition).size();
            name3[1] = awardTitle(Integer.valueOf(name1), Integer.valueOf(name2));

        }
        if (kind.equals("pls")) {
            if (name.equals("4")) {
                name3[0] = "正彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else if (name.equals("3")) {
                name3[0] = "组彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else {
                name3[0] = "对上了" + name + "个号码   命中次数:" + list.get(groupPosition).size();
            }

        }
        if (kind.equals("plw")) {
            if (name.equals("4")) {
                name3[0] = "正彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else if (name.equals("3")) {
                name3[0] = "组彩" + "   命中次数:" + list.get(groupPosition).size();
            }
            else {
                name3[0] = "对上了" + name + "个号码   命中次数:" + list.get(groupPosition).size();
            }
        }
        return name3;
    }

    public int getGroupCount() {
        return list.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * create group view and bind data to view
     */
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.list_item_open_history_analyse, null);
        }
        else {
            v = convertView;
        }
        textView = (TextView) v.findViewById(R.id.textView);
        indicator = (TextView) v.findViewById(R.id.indicator);
        listLinear = (LinearLayout) v.findViewById(R.id.list_item);
        imageView = (ImageView) v.findViewById(R.id.user_account_detail_direction);
        award_tv = (TextView) v.findViewById(R.id.award_tv);
        award_icon = (ImageView) v.findViewById(R.id.award_icon);
        textView.setText(getGroup(groupPosition)[0]);
        String award = getGroup(groupPosition)[1];
        if(null != getGroup(groupPosition)[1] && !"".equals(getGroup(groupPosition)[1])){
            award_tv.setText(getGroup(groupPosition)[1]);
            award_icon.setVisibility(View.VISIBLE);
        }
        else{
            award_tv.setText("");
            award_icon.setVisibility(View.GONE);
        }
        
        if (num < list.size()) {
            viewList.add(indicator);
            num = num + 1;
        }

        if (mHideGroupPos == groupPosition) {
            textView.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.VISIBLE);
        }

        if (isExpanded)
            imageView.setImageResource(R.drawable.arrow_down);
        else
            imageView.setImageResource(R.drawable.arrow_right);

        return v;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void hideGroup(int groupPos) {
        mHideGroupPos = groupPos;
    }

    public ArrayList<Ball> matchBall(String openStr) {
        String[] q_code = null;
        String[] rbballs = null;
        String[] reds = null;
        String[] q_code_normal = null;
        String[] q_code_special = null;
        ArrayList<Ball> b = new ArrayList<Ball>();
        rbballs = openStr.split("\\|");
        if (kind.equals("dfljy")) {
            boolean isTrue = false;
            int k = 0;
            String[] dfljyNum;
            String[] dfljyOpenNum;
            q_code = BetDigitalBasic.requestCode.split("\\|");
            if (q_code.length > 1) {

                q_code_special = q_code[1].split("\\,");
                if (Integer.valueOf(q_code_special[0]) < 10)
                    q_code_special[0] = "0" + q_code_special[0];
            }
            dfljyNum = q_code[0].split("\\,");
            dfljyOpenNum = rbballs[0].split("\\,");
            for (int i = 0; i < dfljyOpenNum.length; i++) {
                if (dfljyOpenNum[i].length() == 1)
                    dfljyOpenNum[i] = "0" + dfljyOpenNum[i];
            }
            for (int i = 0; i < dfljyNum.length; i++) {
                if (dfljyNum[i].length() == 1)
                    dfljyNum[i] = "0" + dfljyNum[i];
            }

            for (int m = 0; m < dfljyOpenNum.length; m++) {
                isTrue = false;
                Ball openBall = new Ball();
                openBall.setNumber(dfljyOpenNum[m]);
                openBall.setColor("red");
                for (int n = k; n < dfljyNum.length; n++) {
                    if (!dfljyNum[n].equals("")) {
                        if (dfljyOpenNum[n].equals(dfljyNum[n])) {
                            openBall.setState(true);
                            isTrue = true;
                            k = n + 1;
                            break;
                        }
                        else {
                            if (isTrue == false) {
                                openBall.setState(false);
                            }
                            k = n + 1;
                            break;
                        }
                    }
                    else {
                        if (isTrue == false) {
                            openBall.setState(false);
                        }
                        k = n + 1;
                        break;
                    }
                }
                b.add(openBall);
            }
        }
        else {
            q_code = BetDigitalBasic.requestCode.split("\\|");
            q_code_normal = q_code[0].split(",");
            if (!q_code[0].equals("")) {
                for (int i = 0; i < q_code_normal.length; i++) {
                    if (Integer.valueOf(q_code_normal[i]) < 10) {
                        q_code_normal[i] = "0" + q_code_normal[i];
                    }
                }
            }

            if (q_code.length > 1) {
                q_code_special = q_code[1].split(",");
                for (int i = 0; i < q_code_special.length; i++) {
                    if (Integer.valueOf(q_code_special[i]) < 10)
                        q_code_special[i] = "0" + q_code_special[i];
                }
            }

            reds = rbballs[0].split(",");
            for (int i = 0; i < reds.length; i++) {
                if (reds[i].length() == 1)
                    reds[i] = "0" + reds[i];
            }
            for (int i = 0; i < reds.length; i++) {
                Ball openBall = new Ball();
                openBall.setNumber(reds[i]);
                openBall.setColor("red");
                for (int j = 0; j < q_code_normal.length; j++) {
                    if (reds[i].equals(q_code_normal[j])) {
                        openBall.setState(true);
                        break;
                    }
                    else
                        openBall.setState(false);
                }
                b.add(openBall);
            }
        }

        if (rbballs.length > 1) {
            String[] blues = rbballs[1].split(",");
            for (int i = 0; i < blues.length; i++) {
                if (blues[i].length() == 1)
                    blues[i] = "0" + blues[i];
            }
            for (int i = 0; i < blues.length; i++) {
                Ball openBall = new Ball();
                openBall.setNumber(blues[i]);
                if (kind.equals("dfljy"))
                    openBall.setColor("shengxiao");
                else
                    openBall.setColor("blue");

                if (kind.equals("qlc")) {
                    for (int j = 0; j < q_code_normal.length; j++) {
                        if (blues[i].equals(q_code_normal[j])) {
                            openBall.setState(true);
                            break;
                        }
                        else
                            openBall.setState(false);
                    }
                }

                if (q_code.length > 1) {
                    for (int j = 0; j < q_code_special.length; j++) {
                        if (blues[i].equals(q_code_special[j])) {
                            openBall.setState(true);
                            break;
                        }
                        else
                            openBall.setState(false);
                    }
                }
                else if (!kind.equals("qlc"))
                    openBall.setState(false);
                b.add(openBall);
            }
        }
        return b;
// }
    }
}
