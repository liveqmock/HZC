package com.haozan.caipiao.activity.bet.dcsfgg;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.LotteryWinningRules;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.CustomExpandleListView;
import com.umeng.analytics.MobclickAgent;

public class DCSFGGBasicActivity extends BasicActivity implements OnClickListener {
	public static final String[] WEEKDAY = { "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六", "星期日" };
	protected static final String[] PLAYTYPE_STR = { "  购 彩  ", "", "  合 买  " };
	protected TextView betInf;
	protected ImageView clear;
	protected Button betBt;
	protected TextView title;
	protected LinearLayout helpLin;
	protected ProgressBar progressBar;
	protected String betCode = null;
	protected TextView tv_zhu_sign;

	protected CustomExpandleListView lv;
	protected ImageView topArrow;

	protected String kind = "dcsfgg";
	protected boolean isGuoguan = true;

	// 合买相关
	protected Button imgShowBet;
	protected int betWay = 1;// 1.购彩；3.合买
	protected PopupWindow betWayPopupWindow;
	// 彩种是否具有合买功能合买
	protected boolean isUnite = true;
	// 是否发起合买
	protected boolean doUnite = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dcsfgg);
		initData();
		setupViews();
		init();
	}

	private void init() {
		orgBetWay();
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			// int type = bundle.getInt("bet_way");
			String lotteryString = bundle.getString("title");
			betCode = bundle.getString("bet_code");
			if (betCode != null && !betCode.equals("")) {
				String pass = betCode.split("\\:")[2];
				if (!pass.equals("100")) {
					isGuoguan = true;// 过关
					if (!lotteryString.equals("") && lotteryString != null) {

						databaseData.commit();
					} else {
					}
				} else {
					isGuoguan = false;// 单关
					if (!lotteryString.equals("") && lotteryString != null) {
						// 设置从其他地方过来的无法修改玩法
						// termLayout.setEnabled(false);
						for (int i = 5; i < 10; i++) {
							break;

						}

						databaseData.commit();
					} else {
					}

				}
			} else {
			}
		} else {
		}
		if (bundle != null) {
			Boolean fromHall = bundle.getBoolean("from_hall", false);
			if (!fromHall) {
				// 设置从其他地方过来的无法修改玩法
				// termLayout.setEnabled(false);
			}
		}
	}

	private void initData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null)
			return;
		kind = bundle.getString("kind");
	}

	private void orgBetWay() {
		betWay = preferences.getInt("bet_way_jczq", 1);
		for (int i = 0; i < PLAYTYPE_STR.length; i++) {
			if (betWay == (i + 1)) {
				betBt.setText(PLAYTYPE_STR[i]);
				break;
			}
		}
		if (betWay == 3) {
			doUnite = true;
		}
	}

	private void setupViews() {
		// add by vincent
		title = (TextView) this.findViewById(R.id.bet_title);
		helpLin = (LinearLayout) this.findViewById(R.id.bet_help_lin);
		helpLin.setOnClickListener(this);
		lv = (CustomExpandleListView) this.findViewById(R.id.match_list);
		clear = (ImageView) this.findViewById(R.id.bet_clear_button);
		clear.setOnClickListener(this);
		betInf = (TextView) this.findViewById(R.id.bet_inf);
		betBt = (Button) this.findViewById(R.id.bet_button);
		betBt.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progress_large);
		topArrow = (ImageView) this.findViewById(R.id.arrow_top);
		tv_zhu_sign = (TextView) this.findViewById(R.id.tv_jczq_zhusign);
		imgShowBet = (Button) this.findViewById(R.id.img_show_bet_way);
		imgShowBet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showBetWayPopupViews();
			}
		});
	}

	protected void showBetWayPopupViews() {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View waySwitchLayout = null;
		waySwitchLayout = mLayoutInflater.inflate(
				R.layout.popup_bet_way_swtich, null);

		TextView betUnite = (TextView) waySwitchLayout
				.findViewById(R.id.bet_add);// 合买
		betUnite.setText("合  买");
		betUnite.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (betWay != 3) {
					betWay = 3;
					doUnite = true;
					databaseData.putInt("bet_way_dcsfgg", betWay);
					databaseData.commit();
					betBt.setText("  合 买  ");
				}
				betWayPopupWindow.dismiss();
			}
		});

		TextView betDirectly = (TextView) waySwitchLayout
				.findViewById(R.id.bet_directly);
		betDirectly.setText("购  彩");
		betDirectly.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (betWay != 1) {
					betWay = 1;
					doUnite = false;
					databaseData.putInt("bet_way_dcsfgg", betWay);
					databaseData.commit();
					betBt.setText("  购 彩  ");
				}
				betWayPopupWindow.dismiss();
			}
		});
		betWayPopupWindow = new PopupWindow(this);
		betWayPopupWindow.setBackgroundDrawable(new ColorDrawable(
				Color.TRANSPARENT));
		betWayPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		betWayPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		betWayPopupWindow.setOutsideTouchable(true);
		betWayPopupWindow.setFocusable(true);
		betWayPopupWindow.setContentView(waySwitchLayout);
		betWayPopupWindow.setAnimationStyle(R.style.popup_ball);
		if (betWay == 1) {
			betDirectly
					.setBackgroundResource(R.drawable.bet_popup_item_choosed);
			betDirectly.setTextColor(getResources().getColor(R.color.white));
			betUnite.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
			betUnite.setTextColor(getResources().getColor(R.color.dark_purple));
		} else if (betWay == 3) {// 合买
			betUnite.setBackgroundResource(R.drawable.bet_popup_item_choosed);
			betUnite.setTextColor(getResources().getColor(R.color.white));
			betDirectly
					.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
			betDirectly.setTextColor(getResources().getColor(
					R.color.dark_purple));
		}
		betWayPopupWindow.showAsDropDown(betBt, 0, -4 * betBt.getHeight());
	}

	protected void bet() {

	};

	protected void clear() {

	};

	protected void enableClearBtn() {
		clear.setEnabled(true);
	}

	protected void disableClearBtn() {
		clear.setEnabled(false);
	}

	protected void enableBetBtn() {
		betBt.setEnabled(true);
	}

	protected void disableBetBtn() {
		betBt.setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bet_button) {
			bet();
		} else if (v.getId() == R.id.bet_clear_button) {
			clear();
		} else if (v.getId() == R.id.bet_help_lin) {
			Intent intent = new Intent();
			Bundle bundel = new Bundle();
			bundel.putString("lottery_name", "竞彩足球游戏规则");
			bundel.putString("lottery_help", "help_new/jczq.html");
			intent.putExtras(bundel);
			intent.setClass(DCSFGGBasicActivity.this, LotteryWinningRules.class);
			startActivity(intent);
		}

	}

	protected void showPopView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	public class GetLotteryInfTask extends AsyncTask<String, Object, String> {

		@Override
		protected String doInBackground(String... params) {
			ConnectService connect = new ConnectService(DCSFGGBasicActivity.this);
			String json = null;
			try {
				json = connect.getJsonGet(5, false,
						initHashMap(params[0], params[1], params[2]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String json) {
			super.onPostExecute(json);
			progressBar.setVisibility(View.GONE);
			getMatchData(json);
			showWay();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	protected void showWay() {
	}

	private HashMap<String, String> initHashMap(String kind, String play,
			String pass) throws Exception {
		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("service", "2009020");
		parameter.put("pid", LotteryUtils.getPid(DCSFGGBasicActivity.this));
		parameter.put("lottery_id", kind);
		parameter.put("play", play);
		parameter.put("pass", pass);
		return parameter;
	}

	protected void getMatchData(String json) {
		// TODO Auto-generated method stub

	}

	protected void initChoosed(String betCode) {

	}

	@Override
	protected void onStart() {
		super.onStart();
		Map<String, String> map = new HashMap<String, String>();
		map.put("inf", "username [" + appState.getUsername() + "]: open bet");
		map.put("more_inf", "open bet " + kind);
		map.put("extra_inf", "dcsfgg");
		String eventName = "v2 open bet";
		FlurryAgent.onEvent(eventName, map);
		besttoneEventCommint(eventName);
	}

	@Override
	protected void submitData() {
		String eventName = "open_bet";
		MobclickAgent.onEvent(this, eventName, kind);
		besttoneEventCommint(eventName);
	}
}
