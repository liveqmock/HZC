package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.types.NewsCommentItem;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;

public class LotteryNewsCommentAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<NewsCommentItem> list;
    private LayoutInflater inflater;
    public int comment_num;
    private LotteryApp appState;
    private int userId;
    private boolean isDing = false;
    private boolean isCai = false;
    private OnReplyClickListener clickListener;

    public LotteryNewsCommentAdapter(Context context, ArrayList<NewsCommentItem> list, int comment_num) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(this.context);
        appState = (LotteryApp) context.getApplicationContext();
    }

    public final class ViewHolder {
        private TextView nickname;
        private TextView issueDate;
        private TextView comment;
        private TextView commentTop;
        private TextView commentDown;
        private TextView commentDing;
        private TextView commentCai;
        private TextView commentReply;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (list != null) {
            final NewsCommentItem commentList = list.get(arg0);
            if (commentList != null) {
                if (arg1 == null) {
                    viewHolder = new ViewHolder();
                    arg1 = inflater.inflate(R.layout.news_comment_list_item, null);
                    viewHolder.nickname = (TextView) arg1.findViewById(R.id.news_commment_nickname);
                    viewHolder.issueDate = (TextView) arg1.findViewById(R.id.news_commment_issue_date);
                    viewHolder.comment = (TextView) arg1.findViewById(R.id.news_comment_content);
                    viewHolder.commentTop = (TextView) arg1.findViewById(R.id.comment_top);
                    viewHolder.commentDown = (TextView) arg1.findViewById(R.id.comment_down);
                    viewHolder.commentDing = (TextView) arg1.findViewById(R.id.textViewDing);
                    viewHolder.commentCai = (TextView) arg1.findViewById(R.id.textViewCai);
                    viewHolder.commentReply = (TextView) arg1.findViewById(R.id.comment_reply);
                    arg1.setTag(viewHolder);
                }
                else {
                    viewHolder = (ViewHolder) arg1.getTag();
                }
                viewHolder.nickname.setText(Html.fromHtml("<u>" + commentList.getNickname() + "</u>"));
                viewHolder.issueDate.setText(commentList.getAddTime());
                viewHolder.comment.setText(TextUtil.formatContent(commentList.getContent(), context));
                int hit = commentList.getCommentGood() - commentList.getCommentBad();
                if (hit <= 0) {
                    viewHolder.commentTop.setText(String.valueOf(hit));
                }
                else {
                    viewHolder.commentTop.setText("+" + String.valueOf(hit));
                }
                isDing = commentList.getDing();
                isCai = commentList.getCai();
                if (isDing) {
                    commentAnima(viewHolder.commentDing);
                }
                if (isCai) {
                    commentAnima(viewHolder.commentCai);
                }
                viewHolder.commentDown.setText(String.valueOf(commentList.getCommentBad()));
                commentList.setDing(false);
                commentList.setCai(false);
                viewHolder.nickname.setOnClickListener(new TextViewClickListener(arg0, arg1));
                viewHolder.commentReply.setOnClickListener(new ReplyClickListener(arg0,
                                                                                  viewHolder.commentReply));// 回复
                viewHolder.commentTop.setOnClickListener(new ReplyClickListener(arg0, viewHolder.commentTop));// 顶
            }
        }
        arg1.setBackgroundResource(R.drawable.list_bg);
        return arg1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public NewsCommentItem getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    class ReplyClickListener
        implements OnClickListener {
        private int position;
        private TextView commentTop;

        ReplyClickListener(int pos, TextView commentTop) {
            position = pos;
            this.commentTop = commentTop;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.comment_reply) {
                if (!clickListener.checkClick(position, 0))// 回复
                    return;
            }
            else if (v.getId() == R.id.comment_top) {
                if (!clickListener.checkClick(position, 1))// 顶
                    return;
                commentTop.setBackgroundResource(R.drawable.news_up_button_selected);
            }
        }
    }

    public interface OnReplyClickListener {
        public boolean checkClick(int position, int flag);
    }

    public void setClickListener(OnReplyClickListener clickListener) {
        this.clickListener = clickListener;
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
            userId = list.get(position).getReplyId();
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
                (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                   R.anim.push_to_left_out);
            }
        }
    }

    private void commentAnima(View view) {
        AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation translateAnimation =
            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                                   Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f);
        translateAnimation.setDuration(1000);
        animationSet.addAnimation(translateAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setFillBefore(false);
        animationSet.setFillAfter(true);
        view.setAnimation(animationSet);
    }
}
