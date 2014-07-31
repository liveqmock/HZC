package com.haozan.caipiao.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.RechargeSelection;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;

public class RechargeSelectionListAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private int[] rechargeIconArray;
    private int[] bankName;
    private String[] topUpName;
    private String[] activityStr;

    public RechargeSelectionListAdapter(Context context, int type, String[] activityStr) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        if (type == 1) {
            rechargeIconArray = LotteryUtils.rechargeIconArrayM_cmpay;
            bankName = LotteryUtils.bankNameM_cmpay;
            topUpName = RechargeSelection.topUpNameM_cmpay;
        }
        else if (type == 2) {
            rechargeIconArray = LotteryUtils.rechargeIconArrayM_union;
            bankName = LotteryUtils.bankNameM_union;
            topUpName = RechargeSelection.topUpNameM_union;
        }
        else if (type == 3) {
            rechargeIconArray = LotteryUtils.rechargeIconArrayM_aly;
            bankName = LotteryUtils.bankNameM_aly;
            topUpName = RechargeSelection.topUpNameM_aly;
        }
// else if (type == 4) {
// rechargeIconArray = LotteryUtils.rechargeIconArrayM_recharge_card;
// bankName = LotteryUtils.bankNameM_recharge_card;
// topUpName = LotteryUtils.topUpNameM_recharge_card;
// }
        else if (type == 5) {
            rechargeIconArray = LotteryUtils.rechargeIconArrayM_other;
            bankName = LotteryUtils.bankNameM_other;
            topUpName = RechargeSelection.topUpNameM_other;
        }
// else if (type == 6) {
// rechargeIconArray = LotteryUtils.rechargeIconArrayM_web_bank;
// bankName = LotteryUtils.bankNameM_web_bank;
// topUpName = LotteryUtils.topUpNameM_web_bank;
// }
        this.activityStr = activityStr;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.activityStr.length; i++) {
            str.append(this.activityStr[i]);
        }
        Logger.inf("vincent", str.toString());
    }

    public final class ViewHolder {
        private ImageView topUpIcon;
        private TextView topUpName;
        private TextView topUpNormalInf;
// private TextView topUpMoreInf;
// private ImageView topUpMoreBankBt;
        private ImageView listDivider;
        private LinearLayout linearL;
        private TextView activityTv;
    }

    @Override
    public int getCount() {
        return topUpName.length;
    }

    @Override
    public Object getItem(int arg0) {
        return topUpName[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        boolean isSingle = true;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.recharge_list_item, null);
            viewHolder.topUpIcon = (ImageView) view.findViewById(R.id.top_up_icon);
            viewHolder.topUpName = (TextView) view.findViewById(R.id.recharge_selection_bt);
// setTextBold(viewHolder.topUpName);
            viewHolder.topUpNormalInf = (TextView) view.findViewById(R.id.recharge_selection_inf);
// viewHolder.topUpMoreInf = (TextView) arg1.findViewById(R.id.recharge_extra_inf);
// viewHolder.topUpMoreBankBt = (ImageView) arg1.findViewById(R.id.rechargr_more_bank_inf);
// viewHolder.topUpMoreBankBt.setOnClickListener(new ListItemExpandListener(arg1, viewHolder));
// viewHolder.topUPmoreInfContainer = (LinearLayout) arg1.findViewById(R.id.extral_inf_container);
            viewHolder.listDivider = (ImageView) view.findViewById(R.id.recharge_list_divider);
            viewHolder.linearL = (LinearLayout) view.findViewById(R.id.linear_layout);
            viewHolder.activityTv = (TextView) view.findViewById(R.id.recharge_tv_activity);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (position % 2 == 0)
            isSingle = false;
        else
            isSingle = true;

        if (position == 0) {
            viewHolder.linearL.setBackgroundResource(R.drawable.five_tab);
            if (topUpName.length == 1) {
                viewHolder.listDivider.setVisibility(View.INVISIBLE);
                viewHolder.linearL.setBackgroundResource(R.drawable.list_single_item);
            }
            else {
                viewHolder.listDivider.setVisibility(View.VISIBLE);
            }
        }
        else if (position == topUpName.length - 1) {
            viewHolder.linearL.setBackgroundResource(R.drawable.six_tab);
            viewHolder.listDivider.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.linearL.setBackgroundResource(R.drawable.four_tab);
            viewHolder.listDivider.setVisibility(View.VISIBLE);
        }

// if (arg0 < (rechargeIconArray.length - 1)) {
        viewHolder.topUpIcon.setImageResource(rechargeIconArray[position]);
        viewHolder.topUpName.setText(topUpName[position]);
        if (!"".equals(activityStr[position]) && null != activityStr[position]) {
            viewHolder.activityTv.setVisibility(View.VISIBLE);
            viewHolder.activityTv.setText(activityStr[position]);
        }
        else {
            viewHolder.activityTv.setVisibility(View.GONE);
        }
        String bankNameInf = context.getResources().getString(bankName[position]).toString();
        viewHolder.topUpNormalInf.setText(bankNameInf);
// }
        return view;
    }

    private void setTextBold(TextView bt) {
        TextPaint tp = bt.getPaint();
        tp.setFakeBoldText(true);
    }

    class ListItemExpandListener
        implements OnClickListener {
        private View view;
        private ViewHolder viewHolder;

        public ListItemExpandListener(View view, ViewHolder viewHolder) {
            this.view = view;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
// if (v.getId() == R.id.rechargr_more_bank_inf) {
// View toolbar = view.findViewById(R.id.extral_inf_container);
// // Creating the expand animation for the item
// ExpandAnimation expandAni = new ExpandAnimation(toolbar, 300);
// expandAni.setExpanListener(new ExpanAnimationListener() {
//
// @Override
// public void listItemExpan(boolean mWasEndedAlready) {
// if (mWasEndedAlready)
// viewHolder.topUpMoreBankBt.setImageResource(R.drawable.open_up_button);
// else
// viewHolder.topUpMoreBankBt.setImageResource(R.drawable.pull_back_button);
// }
// });
// // Start the animation on the toolbar
// toolbar.startAnimation(expandAni);
// }
        }

    }
}
