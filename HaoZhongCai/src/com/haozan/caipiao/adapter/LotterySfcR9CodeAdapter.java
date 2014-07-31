package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haozan.caipiao.R;
import com.haozan.caipiao.matchrecord.SFCRecordMatch;
import com.haozan.caipiao.types.Ball;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.widget.DrawBalls;

public class LotterySfcR9CodeAdapter
    extends BaseAdapter {
    private Context context;
    private int length;
    private int screenWidth;
    private LayoutInflater inflater;
    private String kind;
    private String opens;
    private ArrayList<String> ballNumbers;
    private ArrayList<ArrayList<Ball>> awardBallNumbers;
    private ArrayList<Ball> balls = null;
    public Map<Integer, Boolean> isSelected;
    private String[] lotteryCode;
    private String[] lotteryBetWay01;
    private String[] lotteryBetWay02;
    private String[][] teams;

    public final class ViewHolder {
        private TextView betLotteryCodeIndex;
// private PredicateLayout betLotteryCode03;
        private RelativeLayout viewContainer;
        private CheckBox checkBox;
        private LinearLayout sfcr9BetCodeContainer;
    }

    public LotterySfcR9CodeAdapter(Context context, String lotteryCodeRepeatedly, String kind, String opnes,
                                   String[][] teams, int screenWidth) {
        this.context = context;
        this.kind = kind;
        this.opens = opnes;
        this.teams = teams;
        this.screenWidth = screenWidth;
        inflater = LayoutInflater.from(this.context);

        ballNumbers = new ArrayList<String>();
        awardBallNumbers = new ArrayList<ArrayList<Ball>>();

        if (lotteryCodeRepeatedly != null) {
            lotteryCode = lotteryCodeRepeatedly.split("\\;");
            length = lotteryCode.length;
            init();
            spiltLotteryCode(lotteryCode);
        }
        if (opens.length() >= 5)
            getAwardCode();
    }

    @SuppressLint("UseSparseArrays")
    private void init() {
        // 这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < length; i++) {
            isSelected.put(i, true);
        }
        BetHistoryDetailTool.initAnalyseData();
        setRecord();
    }

    private void spiltLotteryCode(String[] lotteryNum) {
        lotteryBetWay01 = new String[lotteryNum.length];
        lotteryBetWay02 = new String[lotteryNum.length];
        for (int i = 0; i < lotteryNum.length; i++) {
            String[] code = lotteryNum[i].split("\\:");
            ballNumbers.add(code[0]);
            lotteryBetWay01[i] = code[1];
            lotteryBetWay02[i] = code[2];
        }
    }

    private void getAwardCode() {
        try {
            for (int i = 0; i < lotteryCode.length; i++) {
                if (kind.equals("sfc") || kind.equals("r9")) {// 胜负彩球投注历史中奖号码高亮显示
                  balls = SFCRecordMatch.isMatch(lotteryCode[i], opens);
              }
                awardBallNumbers.add(balls);
            }
        }
        catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "数据解析失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (arg1 == null) {
            arg1 = inflater.inflate(R.layout.lottery_sfc_r9_code_display, null);
            viewHolder = new ViewHolder();
            viewHolder.betLotteryCodeIndex = (TextView) arg1.findViewById(R.id.lottery_code_index);
// viewHolder.betLotteryCode03 = (PredicateLayout) arg1.findViewById(R.id.lottery_balls_num_03);
// viewHolder.betLotteryCode03.setQuick(true);
            viewHolder.checkBox = (CheckBox) arg1.findViewById(R.id.checkBox1);
            viewHolder.viewContainer = (RelativeLayout) arg1.findViewById(R.id.lottery_code_container);
            viewHolder.sfcr9BetCodeContainer = (LinearLayout) arg1.findViewById(R.id.contain_predicate);
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

// if (length == 1)
// arg1.setBackgroundResource(R.drawable.bet_order_listview_bg);
// else {
// if (arg0 == (length - 1))
// arg1.setBackgroundResource(R.drawable.bet_order_listview_bg);
// else
// arg1.setBackgroundResource(R.drawable.order_list_item_bg);
// }

        getCOdeWithCircleBg(viewHolder, arg0);
// viewHolder.betLotteryCodeIndex.setText(BetHistoryDetailTool.subName(kind, lotteryBetWay01[arg0],
// lotteryBetWay02[arg0]));
// if (kind.equals("sfc") || kind.equals("r9")) {
        viewHolder.betLotteryCodeIndex.setVisibility(View.GONE);
        viewHolder.checkBox.setVisibility(View.GONE);
// }
// viewHolder.checkBox.setOnClickListener(new LvCheckBoxListener(arg0, arg1));
// viewHolder.checkBox.setChecked(isSelected.get(arg0));
        return arg1;
    }

    private void getCOdeWithCircleBg(ViewHolder viewHolder, int i) {
        if (!opens.equals("null") && !kind.equals("null")) {

            if (kind.equals("sfc") || kind.equals("r9")) {
                DrawBalls drawBalls = new DrawBalls();
                drawBalls.drawSFCBetHistoryAwardBall(context, viewHolder.sfcr9BetCodeContainer, kind,
                                                     awardBallNumbers.get(i), ballNumbers.get(i), teams,
                                                     screenWidth);
// DrawBalls drawBalls = new DrawBalls();
// drawBalls.drawSFCHistoryBall(context, viewHolder.betLotteryCode03, kind, ballNumbers.get(i),
// teams, screenWidth);
            }
        }
        else {
            if (kind.equals("sfc") || kind.equals("r9")) {
                DrawBalls drawBalls = new DrawBalls();
                drawBalls.drawSFCHistoryBall(context, viewHolder.sfcr9BetCodeContainer, kind,
                                             ballNumbers.get(i), teams, screenWidth);
            }

        }
    }

    private String[] xyx5TypeArray = {"11_RX1", "11_RX2", "11_RX3", "11_RX4", "11_RX5", "11_RX6", "11_RX7",
            "11_RX8", "11_ZXQ2_D", "11_ZXQ2_F", "11_ZXQ3_D", "11_ZXQ3_F", "11_ZXQ2", "11_ZXQ3"};

    Map<String, String> map = new HashMap<String, String>();

    private void setRecord() {
        for (int i = 0; i < xyx5TypeArray.length; i++) {
            map.put(xyx5TypeArray[i], String.valueOf(i + 1));
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    class LvCheckBoxListener
        implements OnClickListener {
        private int position;
        private View arg1;

        LvCheckBoxListener(int pos, View arg) {
            position = pos;
            arg1 = arg;
        }

        @Override
        public void onClick(View v) {
            ViewHolder vHollder = (ViewHolder) arg1.getTag();
            isSelected.put(position, vHollder.checkBox.isChecked());
        }
    }
}
