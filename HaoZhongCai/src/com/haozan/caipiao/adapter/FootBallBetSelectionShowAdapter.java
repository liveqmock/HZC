package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.AthleticsListItemData;

public class FootBallBetSelectionShowAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<AthleticsListItemData> footBallBetSelectionList;
    private LayoutInflater inflater;
    private String[] betCode = null;

    public FootBallBetSelectionShowAdapter(Context context,
                                           ArrayList<AthleticsListItemData> footBallBetSelectionList,
                                           String betDisplayCode) {
        this.context = context;
        this.footBallBetSelectionList = footBallBetSelectionList;
        inflater = LayoutInflater.from(this.context);
        betCode = betDisplayCode.split("\\:")[0].split("\\,");
    }

    public final class ViewHolder {
        private TextView index;
        private TextView firstTeam;
        private TextView matchResult;
        private TextView secondTeam;
    }

    @Override
    public int getCount() {
        return footBallBetSelectionList.size();
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
            convertView = inflater.inflate(R.layout.sports_match_team_item, null);
            viewHolder = new ViewHolder();
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.matchResult = (TextView) convertView.findViewById(R.id.match_result);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
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
        viewHolder.index.setText(String.valueOf(index + 1));
        viewHolder.firstTeam.setText(footBallBetSelectionList.get(index).getMatchHomeTeamName());
        viewHolder.matchResult.setText(betCode[index]);
        viewHolder.secondTeam.setText(footBallBetSelectionList.get(index).getMatchGuessTeamName());
    }
}