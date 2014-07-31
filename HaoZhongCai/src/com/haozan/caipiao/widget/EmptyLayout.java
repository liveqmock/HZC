package com.haozan.caipiao.widget;

import com.haozan.caipiao.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmptyLayout extends LinearLayout {
	private static final String TAG = "wang";

	private ImageView ivEmptyImage;
	private TextView tvTips;

	private OnGetDataAgainListener mOnFailToGetDataListener;
	private Context context;

	public EmptyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public EmptyLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		setOrientation(VERTICAL);

		View view = View.inflate(getContext(), R.layout.show_fail_page, null);
		addView(view);

		ivEmptyImage = (ImageView) view.findViewById(R.id.empty_image);
		tvTips = (TextView) view.findViewById(R.id.message);
	}

	public void showNetErrorPage() {
		tvTips.setText("未连接网络，点击查看网络");
		setVisibility(View.VISIBLE);
		setEnabled(true);
		ivEmptyImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settingNet();
			}
		});
		tvTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settingNet();
			}
		});
	}

	protected void settingNet() {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			Intent intentToNetwork = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			context.startActivity(intentToNetwork);
		} else {
			Intent intentToNetwork = new Intent("/");
			ComponentName componentName = new ComponentName(
					"com.android.settings",
					"com.android.settings.WirelessSettings");
			intentToNetwork.setComponent(componentName);
			intentToNetwork.setAction("android.intent.action.VIEW");
			context.startActivity(intentToNetwork);
		}
	}

	public void showFailPage() {
		tvTips.setText("查询失败，点击屏幕尝试刷新");
		setVisibility(View.VISIBLE);
		setEnabled(true);
		ivEmptyImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnFailToGetDataListener.onClickToGetData();
			}
		});
		tvTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnFailToGetDataListener.onClickToGetData();
			}
		});
	}

	public void showNoDataPage(String inf) {
		tvTips.setText(inf);
		setEnabled(false);
	}

	public void setOnGetDataAgainListener(
			OnGetDataAgainListener mOnFailToGetDataListener) {
		this.mOnFailToGetDataListener = mOnFailToGetDataListener;
	}

	public interface OnGetDataAgainListener {
		public void onClickToGetData();
	}
}
