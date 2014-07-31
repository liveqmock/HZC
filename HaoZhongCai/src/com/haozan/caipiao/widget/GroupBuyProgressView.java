package com.haozan.caipiao.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class GroupBuyProgressView extends LinearLayout {
	private BitmapDrawable circleRemaining = (BitmapDrawable) getResources()
			.getDrawable(R.drawable.circle_remaining);

	private int width;
	private int height;

	private TextView progress;
	private LinearLayout bottomLayout;
	private TextView guarantee;

	public GroupBuyProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GroupBuyProgressView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		Context context = getContext();
		progress = new TextView(context);
		progress.setTextColor(Color.parseColor("#b22222"));
		progress.setTextSize(15.0F);
		progress.setTypeface(Typeface.DEFAULT_BOLD);
		progress.setGravity(Gravity.CENTER);
		addView(progress);
		bottomLayout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin = 10;
		bottomLayout.setLayoutParams(params);
		bottomLayout.setOrientation(HORIZONTAL);
		bottomLayout.setGravity(Gravity.CENTER);
		bottomLayout.setVisibility(View.GONE);
		addView(bottomLayout);
		ImageView localImageView = new ImageView(context);
		localImageView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		localImageView.setImageResource(R.drawable.icon_guarantee);
		bottomLayout.addView(localImageView);
		guarantee = new TextView(context);
		guarantee.setTextColor(Color.parseColor("#f08080"));
		guarantee.setTextSize(11.5F);
		bottomLayout.addView(guarantee);
		this.width = this.circleRemaining.getMinimumWidth();
		this.height = this.circleRemaining.getMinimumHeight();
	}

	public void creatProgressView(int progressNum, int guaranteedNum,
			BitmapDrawable bitmapDrawable) {
		if (guaranteedNum == 0) {
			bottomLayout.setVisibility(View.GONE);
		} else {
			bottomLayout.setVisibility(View.VISIBLE);
			guarantee.setText(guaranteedNum + "%");
		}
		progress.setText(progressNum + "%");
		setBackgroundDrawable(bitmapDrawable);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}