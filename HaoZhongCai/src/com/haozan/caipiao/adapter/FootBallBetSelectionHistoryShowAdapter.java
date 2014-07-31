package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.AthleticsListItemData;

public class FootBallBetSelectionHistoryShowAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<AthleticsListItemData> footBallBetSelectionList;
    private LayoutInflater inflater;
    private String[] betCode = null;
    String[] codeArray = null;
    private String openCode;

    public FootBallBetSelectionHistoryShowAdapter(Context context,
                                                  ArrayList<AthleticsListItemData> footBallBetSelectionList,
                                                  String betDisplayCode, String openCode) {
        this.context = context;
        this.footBallBetSelectionList = footBallBetSelectionList;
        this.openCode = openCode;
        inflater = LayoutInflater.from(this.context);
        betCode = betDisplayCode.split("\\:")[0].split("\\,");
    }

    public final class ViewHolder {
        private TextView index;
        private TextView contestants;
        private TextView matchResult;
        private TextView gameResult;
    }

    @Override
    public int getCount() {
        return footBallBetSelectionList.size() + 1;
    }

    @Override
    public Object getItem(int arg0) {
        return footBallBetSelectionList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sports_match_bet_history_team_item, null);
            viewHolder = new ViewHolder();
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.contestants = (TextView) convertView.findViewById(R.id.the_two_contestants);
            viewHolder.matchResult = (TextView) convertView.findViewById(R.id.match_result);
            viewHolder.gameResult = (TextView) convertView.findViewById(R.id.game_result);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (index % 2 == 0) {
            convertView.setBackgroundResource(R.color.gray_light_for_list);
        }
        else {
            convertView.setBackgroundResource(R.color.gray_dark_for_list);
        }
        initListView(viewHolder, index);
        return convertView;
    }

    private void initListView(ViewHolder viewHolder, int index) {

        if (index == 0) {
            viewHolder.index.setText("序号");
            viewHolder.contestants.setText("球队");
            viewHolder.matchResult.setTextColor(Color.BLACK);
            viewHolder.matchResult.setText("投注");
            viewHolder.gameResult.setTextColor(Color.BLACK);
            viewHolder.gameResult.setText("结果");
        }
        else {
            viewHolder.matchResult.setTextColor(Color.RED);
            viewHolder.gameResult.setTextColor(Color.RED);
            viewHolder.index.setText(String.valueOf(index));
            viewHolder.contestants.setText(footBallBetSelectionList.get(index - 1).getMatchHomeTeamName() +
                "VS" + footBallBetSelectionList.get(index - 1).getMatchGuessTeamName());
            viewHolder.matchResult.setText(betCode[index - 1]);
            if (!openCode.equals("null")) {
                codeArray = openCode.split("\\,");
                viewHolder.gameResult.setText(codeArray[index - 1]);
            }
            else {
                viewHolder.gameResult.setText("未开始");
            }
        }
    }
}