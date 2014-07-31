package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.UserAccountDetail;

public class UserDetailInfExpanAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<UserAccountDetail> userAccountDetailList;
    private ArrayList<Boolean> listItemClickStatus;

    public UserDetailInfExpanAdapter(Context context, ArrayList<UserAccountDetail> userAccountDetailList,
                                     ArrayList<Boolean> listItemClickStatus) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.userAccountDetailList = userAccountDetailList;
        this.listItemClickStatus = listItemClickStatus;
    }

    public final class ViewHolder {
        private TextView userDetailInfMonth;
        private TextView userDetailInfDay;
        private TextView userDetailInfDayOfweek;
// private LinearLayout userDetailInfContainer;
        private TextView moneyTv;
        private TextView moneyTvTag;
        private TextView balanceTv;
// private TextView balanceTvTag;
        private TextView userDetailInfDescrition;
        private TextView userDetailInfOrderId;
        private TextView detailTime;
        private ImageView directionSign;
        private ImageView rightBottomLine;
        private ImageView leftBottomLine;
        private LinearLayout bottomExtraInf;
        private LinearLayout childViewContainer;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        return userAccountDetailList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return userAccountDetailList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_detail_list_view_item, null);
            viewHolder.userDetailInfMonth =
                (TextView) convertView.findViewById(R.id.user_account_detail_month);
            viewHolder.userDetailInfDay = (TextView) convertView.findViewById(R.id.user_account_detail_day);
            viewHolder.userDetailInfDayOfweek =
                (TextView) convertView.findViewById(R.id.user_account_detail_day_of_week);
            viewHolder.moneyTv = (TextView) convertView.findViewById(R.id.account_money);
            viewHolder.moneyTvTag = (TextView) convertView.findViewById(R.id.account_money_tag);
            viewHolder.balanceTv = (TextView) convertView.findViewById(R.id.account_balance);
            viewHolder.userDetailInfDescrition = (TextView) convertView.findViewById(R.id.bet_description);
            viewHolder.userDetailInfOrderId = (TextView) convertView.findViewById(R.id.bet_order_id);
            viewHolder.detailTime = (TextView) convertView.findViewById(R.id.detail_time);
            viewHolder.directionSign =
                (ImageView) convertView.findViewById(R.id.user_account_detail_direction_sign);
            viewHolder.bottomExtraInf =
                (LinearLayout) convertView.findViewById(R.id.account_list_item_bottom_extra_inf);
            viewHolder.childViewContainer =
                (LinearLayout) convertView.findViewById(R.id.child_view_container);
            viewHolder.rightBottomLine =
                (ImageView) convertView.findViewById(R.id.user_new_center_line_right);
            viewHolder.leftBottomLine = (ImageView) convertView.findViewById(R.id.user_new_center_line_left);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String balance = userAccountDetailList.get(index).getBalance();
// String date = userAccountDetailList.get(index).getDate();
        String discription = userAccountDetailList.get(index).getDescription();
        String money = userAccountDetailList.get(index).getMoney();
        String orderId = userAccountDetailList.get(index).getOrderId();
// String type = userAccountDetailList.get(index).getType();
        String month = userAccountDetailList.get(index).getMonth();
        String dayOfMonth = userAccountDetailList.get(index).getDayofMonth();
        String dayOfWeek = userAccountDetailList.get(index).getDayOfWeek();
        String textOfType = userAccountDetailList.get(index).getTextOfType();
        String infoInOrOut = userAccountDetailList.get(index).getInOrOut();
        int bgId = userAccountDetailList.get(index).getIsSingleBg();
        int listDiverBgLeft = userAccountDetailList.get(index).getIsDiviverLeft();
        int listDiverBgRight = userAccountDetailList.get(index).getIsDiviverRight();
        String time = userAccountDetailList.get(index).getTime();

        viewHolder.userDetailInfMonth.setText(month);
        viewHolder.userDetailInfDay.setText(dayOfMonth);
        viewHolder.userDetailInfDayOfweek.setText(dayOfWeek);

        viewHolder.childViewContainer.setOnClickListener(new ListItemBottomExpandListener(
                                                                                          index,
                                                                                          convertView,
                                                                                          viewHolder.directionSign,
                                                                                          listItemClickStatus));

        viewHolder.balanceTv.setText(" ￥" + balance);
        viewHolder.moneyTvTag.setText(textOfType.substring(0, 2) + "：");

        if (infoInOrOut.equals("1")) {
            viewHolder.moneyTv.setTextColor(context.getResources().getColor(R.color.red));
            viewHolder.moneyTv.setText(" ￥" + money);
        }
        else if (infoInOrOut.equals("2")) {
            viewHolder.moneyTv.setTextColor(context.getResources().getColor(R.color.bottle_green));
            viewHolder.moneyTv.setText("-￥" + money);
        }

        viewHolder.childViewContainer.setBackgroundResource(bgId);
        viewHolder.leftBottomLine.setBackgroundResource(listDiverBgLeft);
        viewHolder.rightBottomLine.setBackgroundResource(listDiverBgRight);
        viewHolder.detailTime.setText(time);
        viewHolder.userDetailInfDescrition.setText(discription);
        viewHolder.userDetailInfOrderId.setText(orderId);
        return convertView;
    }

    class ListItemBottomExpandListener
        implements OnClickListener {
        private ImageView directionSign;
        private int index;
        private View convertView;
        private ArrayList<Boolean> listClickStatus;

        public ListItemBottomExpandListener(int index, View convertView, ImageView directionSign,
                                            ArrayList<Boolean> listClickStatus) {
            this.directionSign = directionSign;
            this.index = index;
            this.convertView = convertView;
            this.listClickStatus = listClickStatus;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.child_view_container) {

                View toolbar = convertView.findViewById(R.id.account_list_item_bottom_extra_inf);
                // Creating the expand animation for the item
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
                // Start the animation on the toolbar
// toolbar.startAnimation(expandAni);

                if (listClickStatus.get(index) == false) {
                    listClickStatus.set(index, true);
                    directionSign.setImageResource(R.drawable.arrow_down);
                    toolbar.setVisibility(View.VISIBLE);
                }
                else {
                    listClickStatus.set(index, false);
                    directionSign.setImageResource(R.drawable.arrow_right);
                    toolbar.setVisibility(View.GONE);
                }
            }
        }

    }

}
