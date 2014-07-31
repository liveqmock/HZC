package com.haozan.caipiao.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.JczqAnalyse;
import com.haozan.caipiao.activity.bet.renxuanjiu.RenXuanJiuActivity;
import com.haozan.caipiao.types.AthleticsListItemData;
import com.haozan.caipiao.util.ViewUtil;

public class AthleticsListAdapter
    extends BaseAdapter {

    private static final int WINSTATUS = 3;
    private static final int EQUALSTATUS = 1;
    private static final int LOSTSTATUS = 0;
    private static final String NO_ANALYSE_DATA = "暂无分析数据";
    private Context context;
    private ArrayList<AthleticsListItemData> athleticsDataList;
    private LayoutInflater inflater;
    private OnAthleticsListItemListener clickListener;
    private boolean gameWinBt = false;
    private boolean gameEqualBt = false;
    private boolean gameLostBt = false;
    private int[] buttonBgArray = {R.drawable.custom_button_redside_unselected,
            R.drawable.custom_button_redside_normal};

    public AthleticsListAdapter(Context context, ArrayList<AthleticsListItemData> athleticsDataList) {
        this.context = context;
        this.athleticsDataList = athleticsDataList;
        inflater = LayoutInflater.from(this.context);
    }

    private int initWinButtonStatus(int arg0) {
        return athleticsDataList.get(arg0).getWinButtonStatus();
    }

    private int initEqualButtonStatus(int arg0) {
        return athleticsDataList.get(arg0).getEqualButtonStatus();
    }

    private int initLostButtonStatus(int arg0) {
        return athleticsDataList.get(arg0).getLostButtonStatus();
    }

    public final class ViewHolder {
        private TextView matchTypeTv;
        private TextView matchDateTv;
        private TextView theHomeTeam;
        private TextView theGuessTeam;
        private ImageView showAnalyse;
        private LinearLayout layout_index;
// private LinearLayout ll_analyse;
// private Button to_detail;
// private LinearLayout ll_todetail;
// private ImageView theVsIcon;
        private Button gameWinBt;
        private Button gameEqualBt;
        private Button gameLostBt;
        private Button gameDanMa;
        private ImageView img01;
        private ImageView img02;
        private ImageView img03;
        private TextView[] tvs = new TextView[9];
        private int[] tvs_id = {R.id.tv_01, R.id.tv_02, R.id.tv_03, R.id.tv_04, R.id.tv_05, R.id.tv_06,
                R.id.tv_07, R.id.tv_08, R.id.tv_09};

        private LinearLayout footBallBetListContainer;
    }

    @Override
    public int getCount() {
        return athleticsDataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return athleticsDataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        final ViewHolder viewHolder;
        if (arg1 == null) {
            arg1 = inflater.inflate(R.layout.shi_si_chang_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.matchTypeTv = (TextView) arg1.findViewById(R.id.match_type);
            viewHolder.matchDateTv = (TextView) arg1.findViewById(R.id.match_time);
            viewHolder.theHomeTeam = (TextView) arg1.findViewById(R.id.the_home_team);
            viewHolder.theGuessTeam = (TextView) arg1.findViewById(R.id.the_guess_team);
            viewHolder.showAnalyse = (ImageView) arg1.findViewById(R.id.show_analyse);
            viewHolder.layout_index = (LinearLayout) arg1.findViewById(R.id.layout_index);
// viewHolder.ll_analyse = (LinearLayout) arg1.findViewById(R.id.ll_analyse);
// viewHolder.ll_analyse.setBackgroundColor(context.getResources().getColor(R.color.blackground));
// viewHolder.to_detail = (Button) arg1.findViewById(R.id.to_detail);
// viewHolder.ll_todetail = (LinearLayout) arg1.findViewById(R.id.ll_todetail);
// viewHolder.theVsIcon = (ImageView) arg1.findViewById(R.id.the_vs_icon);
            viewHolder.gameWinBt = (Button) arg1.findViewById(R.id.game_win);
            viewHolder.gameEqualBt = (Button) arg1.findViewById(R.id.game_equal);
            viewHolder.gameLostBt = (Button) arg1.findViewById(R.id.game_lost);
            viewHolder.gameDanMa = (Button) arg1.findViewById(R.id.game_dan_ma);
            viewHolder.img01 = (ImageView) arg1.findViewById(R.id.img_01);
            viewHolder.img02 = (ImageView) arg1.findViewById(R.id.img_02);
            viewHolder.img03 = (ImageView) arg1.findViewById(R.id.img_03);
            viewHolder.footBallBetListContainer = (LinearLayout) arg1.findViewById(R.id.foot_ballbet_list);
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i] = (TextView) arg1.findViewById(viewHolder.tvs_id[i]);
            }
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

// if (arg0 % 2 == 0) {
// viewHolder.footBallBetListContainer.setBackgroundResource(R.drawable.user_detail_inf_dublicate);
// }
// else {
// viewHolder.footBallBetListContainer.setBackgroundResource(R.drawable.user_detail_inf_single);
// }

        initListItemView(viewHolder, arg0);
        viewHolder.gameWinBt.setOnClickListener(new AthleticListListner(arg1, arg0, 0, viewHolder));
        viewHolder.gameEqualBt.setOnClickListener(new AthleticListListner(arg1, arg0, 1, viewHolder));
        viewHolder.gameLostBt.setOnClickListener(new AthleticListListner(arg1, arg0, 2, viewHolder));

        viewHolder.gameWinBt.setBackgroundResource(buttonBgArray[initWinButtonStatus(arg0)]);
//        viewHolder.img01.setVisibility(initWinButtonStatus(arg0) == 0 ? View.GONE : View.VISIBLE);
        viewHolder.gameEqualBt.setBackgroundResource(buttonBgArray[initEqualButtonStatus(arg0)]);
//        viewHolder.img02.setVisibility(initEqualButtonStatus(arg0) == 0 ? View.GONE : View.VISIBLE);
        viewHolder.gameLostBt.setBackgroundResource(buttonBgArray[initLostButtonStatus(arg0)]);
//        viewHolder.img03.setVisibility(initLostButtonStatus(arg0) == 0 ? View.GONE : View.VISIBLE);

        if (initWinButtonStatus(arg0) == 0) {
            viewHolder.gameWinBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">胜</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(arg0)[0] + "</font>"));
        }
        else {
            viewHolder.gameWinBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">胜   " + getGameOdds(arg0)[0] +
                "</font>"));
        }
        if (initEqualButtonStatus(arg0) == 0) {
            viewHolder.gameEqualBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">平</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(arg0)[1] + "</font>"));
        }
        else {
            viewHolder.gameEqualBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">平   " +
                getGameOdds(arg0)[1] + "</font>"));
        }
        if (initLostButtonStatus(arg0) == 0) {
            viewHolder.gameLostBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">负</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(arg0)[2] + "</font>"));
        }
        else {
            viewHolder.gameLostBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">负   " +
                getGameOdds(arg0)[2] + "</font>"));
        }

        final AthleticsListItemData sportsItem = athleticsDataList.get(arg0);
        if (countClickNum(context, viewHolder) >= 9) {
            viewHolder.gameWinBt.setEnabled(sportsItem.getClickStatus());
            viewHolder.gameEqualBt.setEnabled(sportsItem.getClickStatus());
            viewHolder.gameLostBt.setEnabled(sportsItem.getClickStatus());
        }
        else {
            viewHolder.gameWinBt.setEnabled(true);
            viewHolder.gameEqualBt.setEnabled(true);
            viewHolder.gameLostBt.setEnabled(true);
        }
        /*
         * //简单分析数据显示 if (sportsItem.isIfShowAnalyse()) { viewHolder.ll_analyse.setVisibility(View.VISIBLE);
         * viewHolder.ll_todetail.setVisibility(View.VISIBLE);
         * viewHolder.showAnalyse.setImageResource(R.drawable.arrow_up); // 分析部分 for (int i = 0; i < 3;
         * i++) { viewHolder.tvs[i].setText(sportsItem.getHisCom(i)); viewHolder.tvs[i +
         * 3].setText(sportsItem.getEveOdds(i)); } viewHolder.tvs[6].setText(sportsItem.getRank(0));
         * viewHolder.tvs[8].setText(sportsItem.getRank(1)); } else {
         * viewHolder.ll_analyse.setVisibility(View.GONE); viewHolder.ll_todetail.setVisibility(View.GONE);
         * viewHolder.showAnalyse.setImageResource(R.drawable.arrow_down); }
         */
        /*
         * viewHolder.layout_index.setOnClickListener(new OnClickListener() {
         * @Override public void onClick(View v) { if (viewHolder.ll_analyse.getVisibility() == View.VISIBLE)
         * { athleticsDataList.get(arg0).setIfShowAnalyse(false);
         * viewHolder.ll_analyse.setVisibility(View.GONE); viewHolder.ll_todetail.setVisibility(View.GONE);
         * viewHolder.showAnalyse.setImageResource(R.drawable.arrow_down); } else {
         * athleticsDataList.get(arg0).setIfShowAnalyse(true);
         * viewHolder.ll_analyse.setVisibility(View.VISIBLE);
         * viewHolder.ll_todetail.setVisibility(View.VISIBLE);
         * viewHolder.showAnalyse.setImageResource(R.drawable.arrow_up); // 分析部分 if (null !=
         * athleticsDataList.get(arg0).getHisCom(0) && !athleticsDataList.get(arg0).getHisCom(0).equals("")) {
         * for (int i = 0; i < 3; i++) { viewHolder.tvs[i].setText(athleticsDataList.get(arg0).getHisCom(i));
         * viewHolder.tvs[i + 3].setText(athleticsDataList.get(arg0).getEveOdds(i)); }
         * viewHolder.tvs[6].setText(athleticsDataList.get(arg0).getRank(0));
         * viewHolder.tvs[8].setText(athleticsDataList.get(arg0).getRank(1)); } else { SportsSimpleAnalyseTask
         * task = new SportsSimpleAnalyseTask(context, sportsItem.getId(),"ctzq");
         * task.setOnGetSportsSimpleAnalyseInf(new OnGetSportsSimpleAnalyseInf() {
         * @Override public void onPre() { // viewHolder.info.setVisibility(View.GONE); for (int i = 0; i < 3;
         * i++) { viewHolder.tvs[i].setText(""); viewHolder.tvs[i + 3].setText(""); }
         * viewHolder.tvs[6].setText(""); viewHolder.tvs[8].setText(""); }
         * @Override public void onPost(String json) { if (json == null) { searchFail(); } else { JsonAnalyse
         * analyse = new JsonAnalyse(); String status = analyse.getStatus(json); if (status.equals("200")) {
         * String data = analyse.getData(json, "response_data"); try { // 历史交锋 String duizhen =
         * analyse.getData(data, "duizheng"); if (null != duizhen && !duizhen.equals("")) { String[] hisCom =
         * new String[3]; String[] temp01 = duizhen.split("\\#"); StringBuilder sb0 = new StringBuilder();
         * sb0.append(temp01[0] + "胜" + temp01[2] + "负"); viewHolder.tvs[0].setText(sb0.toString()); hisCom[0]
         * = sb0.toString(); StringBuilder sb1 = new StringBuilder(); sb1.append(temp01[1] + "平");
         * viewHolder.tvs[1].setText(sb1.toString()); hisCom[1] = sb1.toString(); StringBuilder sb2 = new
         * StringBuilder(); sb2.append(temp01[2] + "胜" + temp01[0] + "负");
         * viewHolder.tvs[2].setText(sb2.toString()); hisCom[2] = sb2.toString();
         * athleticsDataList.get(arg0).setHisCom(hisCom);// 保存历史交锋数据 } else { for (int i = 0; i < 3; i++) {
         * athleticsDataList.get(arg0).setHisCom(i, "-"); } } // 平均赔率 try { String sheng =
         * analyse.getData(data, "sheng"); String ping = analyse.getData(data, "ping"); String fu =
         * analyse.getData(data, "fu"); if (null != sheng && !sheng.equals("")) {
         * viewHolder.tvs[3].setText(sheng); } else { viewHolder.tvs[3].setText("-"); } if (null != ping &&
         * !ping.equals("")) { viewHolder.tvs[4].setText(ping); } else { viewHolder.tvs[4].setText("-"); } if
         * (null != fu && !fu.equals("")) { viewHolder.tvs[5].setText(fu); } else {
         * viewHolder.tvs[5].setText("-"); } String[] eveOdds = new String[3]; eveOdds[0] = sheng; eveOdds[1]
         * = ping; eveOdds[2] = fu; athleticsDataList.get(arg0).setEveOdds(eveOdds); } catch (Exception e) {
         * for (int i = 0; i < 3; i++) { athleticsDataList.get(arg0).setEveOdds(i, "-"); } } // 两队排名 try {
         * String zhupai = analyse.getData(data, "zhupai"); String kepai = analyse.getData(data, "kepai"); if
         * (null != zhupai && !zhupai.equals("")) { viewHolder.tvs[6].setText(zhupai); } else {
         * viewHolder.tvs[6].setText("-"); } if (null != kepai && !kepai.equals("")) {
         * viewHolder.tvs[8].setText(kepai); } else { viewHolder.tvs[8].setText("-"); } String[] rank = new
         * String[2]; rank[0] = zhupai; rank[1] = kepai; athleticsDataList.get(arg0).setRank(rank); } catch
         * (Exception e) { for (int i = 0; i < 2; i++) { athleticsDataList.get(arg0).setRank(i,"-"); } } }
         * catch (Exception e) { e.printStackTrace(); searchFail(); } } else if (status.equals("302")) {
         * searchFail(); } else if (status.equals("304")) { searchFail(); } else { searchFail(); } } } private
         * void searchFail() { // viewHolder.info.setVisibility(View.VISIBLE); } }); task.execute(); } } } });
         */
        viewHolder.layout_index.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!"".equals(sportsItem.getMatchId()) && null != sportsItem.getMatchId()) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    // bundle.putString("game_id", sportsItem.getId());
                    bundle.putString("game_id", sportsItem.getMatchId());
                    bundle.putString("master_name", sportsItem.getMatchHomeTeamName());
                    bundle.putString("guester_name", sportsItem.getMatchGuessTeamName());
                    bundle.putString("leage", sportsItem.getMatchType());
                    bundle.putString("kind", "zq");
                    intent.putExtras(bundle);
                    intent.setClass(context, JczqAnalyse.class);
                    context.startActivity(intent);
                }
                else {
                    ViewUtil.showTipsToast(context, NO_ANALYSE_DATA);
                }
            }
        });

        return arg1;
    }

    private String[] getGameOdds(int index) {
        return athleticsDataList.get(index).getGameOdds().split(" ");
    }

    private void initListItemView(ViewHolder viewHolder, int index) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        viewHolder.matchTypeTv.setText(athleticsDataList.get(index).getMatchType());
        viewHolder.matchDateTv.setText(dateFormat.format(stringConvertToDate(athleticsDataList.get(index).getMatchDate())));
        viewHolder.theHomeTeam.setText(athleticsDataList.get(index).getMatchHomeTeamName());
        viewHolder.theGuessTeam.setText(athleticsDataList.get(index).getMatchGuessTeamName());

    }

    private Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            toDate = dateFormat.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    private int countClickNum(Context context, ViewHolder viewHolder) {
        int num = 0;
        if (context.getClass() == RenXuanJiuActivity.class) {
            for (int i = 0; i < athleticsDataList.size(); i++)
                if (athleticsDataList.get(i).getClickStatus())
                    num++;
        }
        return num;

    }

    public void setListItemClickListener(OnAthleticsListItemListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnAthleticsListItemListener {
        public void onActionClick(String selection, int grounpIndex, int childIndex,
                                  ArrayList<AthleticsListItemData> athleticsDataList);
    }

    private void changeButtonStatus(int posi, boolean gameBtStatus, int buttonKind,
                                    ArrayList<AthleticsListItemData> athleticsDataList) {
        for (int i = 0; i < athleticsDataList.size(); i++)
            if (i == posi) {
                if (gameBtStatus) {
                    if (buttonKind == 3)
                        athleticsDataList.get(posi).setWinButtonStatus(1);
                    if (buttonKind == 1)
                        athleticsDataList.get(posi).setEqualButtonStatus(1);
                    if (buttonKind == 0)
                        athleticsDataList.get(posi).setLostButtonStatus(1);
                }
                else {
                    if (buttonKind == 3)
                        athleticsDataList.get(posi).setWinButtonStatus(0);
                    if (buttonKind == 1)
                        athleticsDataList.get(posi).setEqualButtonStatus(0);
                    if (buttonKind == 0)
                        athleticsDataList.get(posi).setLostButtonStatus(0);
                }
            }
    }

    class AthleticListListner
        implements OnClickListener {
        private int grounpPosition;
        private int childPosition;
        private ViewHolder viewHolder;
        private ArrayList<AthleticsListItemData> athleticsDataListInner;

        public AthleticListListner(View arg1, int grounpPosition, int childPosition, ViewHolder viewHolder) {
            this.grounpPosition = grounpPosition;
            this.childPosition = childPosition;
            this.viewHolder = viewHolder;
            this.athleticsDataListInner = athleticsDataList;
        }

        @Override
        public void onClick(View v) {

            if (athleticsDataListInner.get(grounpPosition).getClickStatus() == false) {
                athleticsDataListInner.get(grounpPosition).setClickStatus(true);
            }

            if (v.getId() == R.id.game_win) {
                setClickWinButtonBackGround(viewHolder, grounpPosition);
                setInerserButtonStatus(grounpPosition, athleticsDataListInner);
                clickListener.onActionClick("3", grounpPosition, childPosition, athleticsDataListInner);
            }
            else if (v.getId() == R.id.game_equal) {
                setClickEqualButtonBackGround(viewHolder, grounpPosition);
                setInerserButtonStatus(grounpPosition, athleticsDataListInner);
                clickListener.onActionClick("1", grounpPosition, childPosition, athleticsDataListInner);
            }
            else if (v.getId() == R.id.game_lost) {
                setClicLostkButtonBackGround(viewHolder, grounpPosition);
                setInerserButtonStatus(grounpPosition, athleticsDataListInner);
                clickListener.onActionClick("0", grounpPosition, childPosition, athleticsDataListInner);
            }
        }
    }

    private void setInerserButtonStatus(int grounpPosition,
                                        ArrayList<AthleticsListItemData> athleticsDataListInner) {
        int winButtonStatus = athleticsDataList.get(grounpPosition).getWinButtonStatus();
        int equalButtonStatus = athleticsDataList.get(grounpPosition).getEqualButtonStatus();
        int lostButtonStatus = athleticsDataList.get(grounpPosition).getLostButtonStatus();

        if (athleticsDataListInner.get(grounpPosition).getClickStatus()) {
            if (winButtonStatus == 0 && equalButtonStatus == 0 && lostButtonStatus == 0)
                athleticsDataListInner.get(grounpPosition).setClickStatus(false);
        }
    }

    private void setClickWinButtonBackGround(ViewHolder viewHolder, int position) {
        if (athleticsDataList.get(position).getWinButtonStatus() == 0)
            gameWinBt = true;
        else if (athleticsDataList.get(position).getWinButtonStatus() == 1)
            gameWinBt = false;

        if (gameWinBt) {
            changeButtonStatus(position, gameWinBt, WINSTATUS, athleticsDataList);
            viewHolder.gameWinBt.setBackgroundResource(R.drawable.custom_button_redside_normal);
            viewHolder.gameWinBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">胜   " +
                getGameOdds(position)[0] + "</font>"));
//            viewHolder.img01.setVisibility(View.VISIBLE);
            gameWinBt = false;
        }
        else {
            changeButtonStatus(position, gameWinBt, WINSTATUS, athleticsDataList);
            viewHolder.gameWinBt.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.gameWinBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">胜</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(position)[0] + "</font>"));
            viewHolder.img01.setVisibility(View.GONE);
            gameWinBt = true;
        }
    }

    private void setClickEqualButtonBackGround(ViewHolder viewHolder, int position) {
        if (athleticsDataList.get(position).getEqualButtonStatus() == 0)
            gameEqualBt = true;
        else if (athleticsDataList.get(position).getEqualButtonStatus() == 1)
            gameEqualBt = false;

        if (gameEqualBt) {
            changeButtonStatus(position, gameEqualBt, EQUALSTATUS, athleticsDataList);
            viewHolder.gameEqualBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">平   " +
                getGameOdds(position)[1] + "</font>"));
            viewHolder.gameEqualBt.setBackgroundResource(R.drawable.custom_button_redside_normal);
//            viewHolder.img02.setVisibility(View.VISIBLE);
            gameEqualBt = false;
        }
        else {
            changeButtonStatus(position, gameEqualBt, EQUALSTATUS, athleticsDataList);
            viewHolder.gameEqualBt.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.gameEqualBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">平</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(position)[1] + "</font>"));
            viewHolder.img02.setVisibility(View.GONE);
            gameEqualBt = true;
        }
    }

    private void setClicLostkButtonBackGround(ViewHolder viewHolder, int position) {
        if (athleticsDataList.get(position).getLostButtonStatus() == 0)
            gameLostBt = true;
        else if (athleticsDataList.get(position).getLostButtonStatus() == 1)
            gameLostBt = false;

        if (gameLostBt) {
            changeButtonStatus(position, gameLostBt, LOSTSTATUS, athleticsDataList);
            viewHolder.gameLostBt.setBackgroundResource(R.drawable.custom_button_redside_normal);
            viewHolder.gameLostBt.setText(Html.fromHtml("<font color=\"#FFFFFF\">负   " +
                getGameOdds(position)[2] + "</font>"));
//            viewHolder.img03.setVisibility(View.VISIBLE);
            gameLostBt = false;
        }
        else {
            changeButtonStatus(position, gameLostBt, LOSTSTATUS, athleticsDataList);
            viewHolder.gameLostBt.setText(Html.fromHtml("<font color=\"#A8A8A8\">负</font>   " +
                "<font color=\"#DB0010\">" + getGameOdds(position)[2] + "</font>"));
            viewHolder.gameLostBt.setBackgroundResource(R.drawable.custom_button_redside_unselected);
            viewHolder.img03.setVisibility(View.GONE);
            gameLostBt = true;
        }
    }
}
