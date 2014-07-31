package com.haozan.caipiao.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.LotteryAwardRank;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;

public class AwardConditionAdapter
    extends BaseAdapter {

    private Context context;
    private ArrayList<LotteryAwardRank> awardRankList;
    private LayoutInflater inflater;
    private boolean isNewlyRank = false;
    private int userId;
    private ViewHolder holdView;
    private DecimalFormat myFormatter;
    private LotteryApp appState;

    public AwardConditionAdapter(Context context, ArrayList<LotteryAwardRank> awardRankList,
                                 boolean isNewlyRank) {
        this.context = context;
        this.awardRankList = awardRankList;
        inflater = LayoutInflater.from(this.context);
        this.isNewlyRank = isNewlyRank;
        myFormatter = new DecimalFormat("###,##0.00");
        appState = (LotteryApp) context.getApplicationContext();
    }

    public final class ViewHolder {
        private TextView awardItemIndex;
        private TextView awardItemNickname;
        private TextView awardItemPrize;
        private Button awardItemFollow;
        private TextView awardItemTime;
        private LinearLayout awardCondotionLinear;
    }

    @Override
    public int getCount() {
        return awardRankList.size();
    }

    @Override
    public Object getItem(int position) {
        return awardRankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.award_list_item, null);
            viewHolder.awardItemIndex = (TextView) convertView.findViewById(R.id.award_item_index);
            viewHolder.awardItemNickname = (TextView) convertView.findViewById(R.id.award_item_nick_name);
            viewHolder.awardItemPrize = (TextView) convertView.findViewById(R.id.award_item_prize);
            viewHolder.awardItemFollow = (Button) convertView.findViewById(R.id.award_item_follow);
            viewHolder.awardItemTime = (TextView) convertView.findViewById(R.id.award_item_time);
            viewHolder.awardCondotionLinear =
                (LinearLayout) convertView.findViewById(R.id.award_condotion_linear);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initWidget(viewHolder, awardRankList, position);
//        viewHolder.awardItemNickname.setOnClickListener(new TextViewClickListener(position, convertView));
        convertView.setOnClickListener(new TextViewClickListener(position, convertView));
        return convertView;
    }

    private void initWidget(ViewHolder viewHolder, ArrayList<LotteryAwardRank> awardRankList, int position) {

        holdView = viewHolder;
        if (isNewlyRank)
            viewHolder.awardItemIndex.setText(String.valueOf((position + 1)));
        else
            viewHolder.awardItemIndex.setText("第" + String.valueOf((position + 1)) + "名");
        viewHolder.awardItemNickname.setText(Html.fromHtml("<u>" + awardRankList.get(position).getPhone() +
            "</u>"));
        viewHolder.awardItemPrize.setText(myFormatter.format(Double.valueOf(awardRankList.get(position).getLotteryPrize())) +
            "元");
        if (isNewlyRank)
            viewHolder.awardItemTime.setText(awardRankList.get(position).getAwardTime());
        viewHolder.awardItemFollow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddFocusTask aft = new AddFocusTask(userId, holdView);
                aft.execute();
            }
        });
    }

    class AddFocusTask
        extends AsyncTask<Void, Void, String> {
        private int user_id;
        private ViewHolder viewHolder;

        public AddFocusTask(int user_id, ViewHolder viewHolder) {
            this.user_id = user_id;
            this.viewHolder = viewHolder;
        }

        private HashMap<String, String> initHashMap() {
            String phone = appState.getUsername();
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(context));
            parameter.put("phone", String.valueOf(phone));
            parameter.put("user_id", "" + user_id);
            parameter.put("type", "1");
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(context);
            String json = null;
            try {
                json = connectNet.getJsonGet(3, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            String json = (String) result;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    ViewUtil.showTipsToast(context, "关注成功");
                    viewHolder.awardItemFollow.setText("已关注");
                }
                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(context, "已经关注该用户");
                }
                else {
                    ViewUtil.showTipsToast(context, "关注失败");
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class TextViewClickListener
        implements OnClickListener {
        private int position;
        private View arg1;

        TextViewClickListener(int pos, View arg) {
            position = pos;
            arg1 = arg;
        }

        @Override
        public void onClick(View v) {
            userId = awardRankList.get(position).getUserId();
// ViewHolder vHollder = (ViewHolder) arg1.getTag();
// isSelected.put(position, vHollder.checkBox.isChecked());
            if (appState.getSessionid() != null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(context, UserProfileActivity.class);
                bundle.putInt("userId", userId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
            else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(context, Login.class);
// intent.setClass(context, StartUp.class);
                bundle.putString("forwardFlag", "用户资料");
                bundle.putBoolean("ifStartSelf", false);
                bundle.putInt("userId", userId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                   R.anim.push_to_right_out);
            }
        }
    }
}
