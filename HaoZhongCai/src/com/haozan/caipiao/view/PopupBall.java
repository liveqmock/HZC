package com.haozan.caipiao.view;

import android.content.Context;
import android.text.Html;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class PopupBall
    extends View {

    private TextView textView;
    private View trendView;
    private RelativeLayout ballLayout;

    private Context mContext;
    private PopupWindow mPopupWindow;
    private OnPopupClickListener fullListener;
    private ShowOnDismissListener dismissListener;

    public PopupBall(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater mLayoutInflater =
            (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        trendView = mLayoutInflater.inflate(R.layout.popup_ball, null);
        ballLayout = (RelativeLayout) trendView.findViewById(R.id.layout_popup_ball);

        mPopupWindow =
            new PopupWindow(trendView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
// mPopupWindow = new PopupWindow(context);
// mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// mPopupWindow.setContentView(trendView);
// mPopupWindow.setWidth(120);
// mPopupWindow.setHeight(80);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                dismissListener.isDismiss();
            }
        });
        textView = (TextView) trendView.findViewById(R.id.ball_open_count);
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fullListener.onBallClick();
                disAblePopupWindow();
            }
        });
    }

    public void setText(String content) {
        textView.setText(Html.fromHtml(content));
    }

    public void showPopupWindow(View parent) {
        BallTextView ball = (BallTextView) parent;
        if (ball.getColor().equals("red")) {
            ballLayout.setBackgroundResource(R.drawable.bet_number_popup_red);
        }
        else {
            ballLayout.setBackgroundResource(R.drawable.bet_number_popup_blue);
        }
        if (ball.getWord() != null)
            setText(ball.getWord());
        else {
            setText(String.valueOf(ball.getNumber()));
        }
        int location[] = new int[2];
        parent.getLocationOnScreen(location);
        mPopupWindow.setAnimationStyle(R.style.popup_ball);
// mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - 40, location[1] - 100);
// mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1]);
        mPopupWindow.showAsDropDown(parent, -9, -ball.getHeight() / 2 * 5);
    }

    public void disAblePopupWindow() {
        mPopupWindow.dismiss();
    }

    public void setPopupClickListener(OnPopupClickListener fullListener) {
        this.fullListener = fullListener;
    }

    public interface OnPopupClickListener {
        public void onBallClick();
    }

    public void setDismissListener(ShowOnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface ShowOnDismissListener {
        public void isDismiss();
    }
}
