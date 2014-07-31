package com.haozan.caipiao.activity.bet.dcsfgg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.bet.BetPayDCSFGG;
import com.haozan.caipiao.activity.bet.BetPaySports;
import com.haozan.caipiao.activity.bet.BetPayUniteSports;
import com.haozan.caipiao.adapter.SportsDCSFGGAdapter;
import com.haozan.caipiao.adapter.SportsDCSFGGAdapter.OnBallClickListener;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.SportsItem;
import com.haozan.caipiao.types.SportsSFCBetGroup;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.TimeUtils;
import com.haozan.caipiao.util.ViewUtil;

public class DCSFGGActivity extends DCSFGGBasicActivity implements
		OnBallClickListener {
	private static final int MIN_SELECTED = 3;
	private static final int MAX_SELECTED = 15;

	private int selectedNum = 0;
	protected long endTimeMillis = 0;

	private int[] num = { 3, 3, 8, 9, 31, 3, 3, 8, 9, 31 };

	public static Map<String, String> jcLeague;

	private SportsDCSFGGAdapter bjdcsfadapter;
	private ArrayList<BaseExpandableListAdapter> adapter = new ArrayList<BaseExpandableListAdapter>();
	private ArrayList<ArrayList<SportsItem>> betList = new ArrayList<ArrayList<SportsItem>>();
	private ArrayList<ArrayList<SportsSFCBetGroup>> matchList = new ArrayList<ArrayList<SportsSFCBetGroup>>();
	private ArrayList<ArrayList<SportsSFCBetGroup>> matchListSub = new ArrayList<ArrayList<SportsSFCBetGroup>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		ArrayList<SportsSFCBetGroup> list0 = new ArrayList<SportsSFCBetGroup>();
		matchList.add(list0);
		ArrayList<SportsSFCBetGroup> list1 = new ArrayList<SportsSFCBetGroup>();
		matchListSub.add(list1);
		ArrayList<SportsItem> list2 = new ArrayList<SportsItem>();
		betList.add(list2);

		jcLeague = new HashMap<String, String>();

		bjdcsfadapter = new SportsDCSFGGAdapter(DCSFGGActivity.this,
				matchListSub.get(0));
		adapter.add(bjdcsfadapter);
		((SportsDCSFGGAdapter) adapter.get(0)).setClickListener(this);

		for (int i = 0; i < matchList.size(); i++) {
			lv.setAdapter(adapter.get(i), R.layout.sports_history_group_view);
		}

		// TODO
		betInf.setText("请选择3-15场比赛");

		kind = "dcsfgg";
		if (HttpConnectUtil.isNetworkAvailable(DCSFGGActivity.this)) {
			GetLotteryInfTask getLotteryInf = new GetLotteryInfTask();
			getLotteryInf.execute(kind, "91", "1");
		} else {
			String inf = getResources().getString(
					R.string.network_not_avaliable);
			ViewUtil.showTipsToast(this, inf);
		}
	}

	@Override
	public void showWay() {
		for (int i = 0; i < matchList.size(); i++) {
			matchListSub.get(i).clear();
			for (int j = 0; j < matchList.get(i).size(); j++) {
				matchListSub.get(i).add(matchList.get(i).get(j));
			}

			lv.refreshContent();
			adapter.get(i).notifyDataSetChanged();
			lv.expandList();
			break;
		}
	}

	@Override
	protected void getMatchData(String json) {
		Boolean getSuccess = false;
		if (json != null) {
			Log.i("log", json);
			JsonAnalyse analyse = new JsonAnalyse();
			String status = analyse.getStatus(json);
			if (status.equals("200")) {
				for (int i = 0; i < matchList.size(); i++) {
					matchList.get(i).clear();
					matchListSub.get(i).clear();
				}
				String response_data = analyse.getData(json, "response_data");
				if (response_data.equals("[]") == false) {
					try {
						JSONArray hallArray = new JSONArray(response_data);
						int length = hallArray.length();
						for (int i = 0; i < length; i++) {
							JSONObject jo = hallArray.getJSONObject(i);
							String gameDate = jo.getString("match_time");
							jcLeague.put(jo.getString("match_name"),
									jo.getString("match_name"));
							int index = 0;
							boolean hasSameDay = false;
							for (int j = 0; j < matchList.size(); j++) {
								for (index = 0; index < matchList.get(j).size(); index++) {
									if (matchList.get(j).get(index).getDate()
											.equals(gameDate)) {
										hasSameDay = true;
										break;
									}
								}
							}

							// 格式化获取比赛是一周第几天
							int date = TimeUtils.getDate(gameDate);
							// 取出来的1代表星期天，转换成第七天形式,2代表星期一
							if (date == 1) {
								date = 7;
							} else {
								date = date - 1;
							}
							// 列表中无此天的比赛，新增该天次
							if (!hasSameDay) {
								SportsSFCBetGroup groupTemp = new SportsSFCBetGroup();
								groupTemp.setDate(gameDate);
								if (date != -1) {
									groupTemp.setDay(WEEKDAY[date - 1]);
								} else {
									groupTemp.setDay("");
								}
								for (int j = 0; j < matchList.size(); j++) {
									ArrayList<SportsItem> sportsItemList = new ArrayList<SportsItem>();
									try {
										addSportsItem(date, jo, sportsItemList,
												j);
									} catch (Exception e) {
										e.printStackTrace();
									}
									groupTemp.setItemList(sportsItemList);
									matchList.get(j).add(groupTemp);
									matchListSub.get(j).add(groupTemp);
									break;
								}
							} else {
								for (int j = 0; j < matchList.size(); j++) {
									try {
										addSportsItem(date, jo, matchList
												.get(j).get(index)
												.getItemList(), j);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				getSuccess = true;
				for (int j = 0; j < matchList.size(); j++) {
					for (int i = 0; i < matchList.get(j).size(); i++) {
						SportsSFCBetGroup groupTemp = matchList.get(j).get(i);
						groupTemp.setGameNumber("共"
								+ groupTemp.getItemList().size() + "场赛事");
					}
					for (int i = 0; i < matchListSub.get(j).size(); i++) {
						SportsSFCBetGroup groupTemp = matchListSub.get(j)
								.get(i);
						groupTemp.setGameNumber("共"
								+ groupTemp.getItemList().size() + "场赛事");
					}

				}
				lv.refreshContent();
				for (int i = 0; i < matchList.size(); i++) {
					adapter.get(i).notifyDataSetChanged();
				}
				lv.expandList();
			}
		}
		if (!getSuccess) {
			ViewUtil.showTipsToast(this, "比赛数据获取失败");
		}
	}

	private void initBetList() {
		betList.get(0).clear();
		for (int i = 0; i < matchList.get(0).size(); i++) {
			ArrayList<SportsItem> sportsItemList = matchList.get(0).get(i)
					.getItemList();
			for (int j = 0; j < sportsItemList.size(); j++) {
				SportsItem item = sportsItemList.get(j);
				boolean isAdd = false;
				for (int m = 0; m < num[1]; m++) {
					if (item.getStatus(m)) {
						isAdd = true;
						break;
					}
				}
				if (isAdd == true) {
					betList.get(0).add(item);
					long millis = TimeUtils.getDateMillisecond(item
							.getEndTime());
					if (endTimeMillis == 0 || endTimeMillis > millis) {
						endTimeMillis = millis;
					}
				}
			}
		}

	}

	/*
	 * 跳转支付页面
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.haozan.caipiao.activity.bet.dcsfgg.BJDCBasicActivity#bet()
	 */
	@Override
	protected void bet() {
		super.bet();
		initBetList();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("bet_kind", "dcsfgg");
		bundle.putString("bet_term", "91");
		bundle.putParcelableArrayList("bet_sports_data", betList.get(0));
		bundle.putString("mode", "0000");
		bundle.putLong("endtime", endTimeMillis);
		bundle.putBoolean("fromBet", true);
		bundle.putString("about", "left");
		bundle.putBoolean("isGuoguan", true);
		bundle.putBoolean("ifStartSelf", false);
		bundle.putBoolean("bet_is_unite", doUnite);
		bundle.putString("forwardFlag", "收银台");
		bundle.putBoolean("is_continue_pass", true);
		if (betWay == 1) {
			bundle.putString("class_name", BetPayDCSFGG.class.getName());
		} else if (betWay == 3) {
			bundle.putString("class_name", BetPayUniteSports.class.getName());
		}
		intent.putExtras(bundle);
		if (appState.getUsername() == null) {
			intent.setClass(DCSFGGActivity.this, Login.class);
		} else {
			if (betWay == 1) {
				intent.setClass(DCSFGGActivity.this, BetPayDCSFGG.class);
			} else if (betWay == 3) {
				intent.setClass(DCSFGGActivity.this, BetPayUniteSports.class);
			}
		}
		startActivityForResult(intent, 1);
	}

	/**
	 * 添加每场比赛到每天的场次中
	 * 
	 * @param date
	 *            比赛是一周的哪天
	 * @param jo
	 *            从服务器获取到的数据格式
	 * @param sportsItemList
	 *            要存储的总场次列表
	 * @throws JSONException
	 *             读取不到数据异常
	 */
	private void addSportsItem(int date, JSONObject jo,
			ArrayList<SportsItem> sportsItemList, int i) throws JSONException {
		SportsItem sportItem = new SportsItem();
		sportItem.setId(jo.getString("game_id"));
		sportItem.setWeekDay(String.valueOf(date));
		sportItem.setIdBetNum(jo.getString("match_id"));
		sportItem.setLeague(jo.getString("match_name"));
		sportItem.setMatchHomeTeamName(jo.getString("home_team"));
		sportItem.setMatchGuessTeamName(jo.getString("guest_team"));
		sportItem.setEndTime(jo.getString("sellout_time"));
		sportItem.setMatchTime(jo.getString("match_time"));
		sportItem.setConcede(jo.getString("remark"));
		if(jo.has("sp1") && jo.has("sp2")){
			if(!jo.getString("sp1").equals("") && !jo.getString("sp2").equals("")){
				sportItem.setOdds(0, jo.getString("sp1"));
				sportItem.setOdds(1, "0");
				sportItem.setOdds(2, jo.getString("sp2"));
				sportItem.setStatus(0, false);
				sportItem.setStatus(1, false);
				sportItem.setStatus(2, false);
				sportsItemList.add(sportItem);
			}
		}
		
	}

	/**
	 * 当有比赛点击事件触发时，判断是否允许点击，并作处理
	 * 
	 * @param groupPosition
	 *            点击的比赛属于哪一天
	 * @param childPosition
	 *            点击的比赛属于哪一队
	 * @param index
	 *            点击的是胜平负的哪个
	 * @return 是否允许点击
	 */
	@Override
	public boolean checkClick(int groupPosition, int childPosition, int index) {
		// TODO Auto-generated method stub
		boolean canClick = false;
		SportsItem item = (matchListSub.get(0)).get(groupPosition)
				.getItemList().get(childPosition);
		if (item.getStatus(index)) {
			item.reverseStatus(index);
			// 如果减少了一项，则选中数减一
			if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
				selectedNum--;
			}
			canClick = true;
		} else {
			if (selectedNum >= MAX_SELECTED && !item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
				ViewUtil.showTipsToast(this, "最多只允许选择15场比赛");
			} else {
				// 如果增加了一项，则选中数加一
				if (!item.getStatus(0) && !item.getStatus(1) && !item.getStatus(2)) {
					selectedNum++;
				}
				item.reverseStatus(index);
				canClick = true;
			}
		}
		if (canClick) {
			showMatchSelectedNum();
			checkBtnStatus();
			return true;
		} else {
			return false;
		}
	}
	
	private void clearBet() {
        for (int k = 0; k < 5; k++) {
                for (int i = 0; i < matchListSub.get(0).size(); i++) {
                    SportsSFCBetGroup sports = matchListSub.get(0).get(i);
                    ArrayList<SportsItem> sportsItemList = sports.getItemList();
                    for (int j = 0; j < sportsItemList.size(); j++) {
                        SportsItem sportItem = sportsItemList.get(j);
                        sportItem.setShowStr(null);
                        for (int m = 0; m < num[1]; m++) {
                            sportItem.setStatus(m, false);
                        }
                    }
                }
        }
        for (int i = 0; i < matchList.size(); i++) {
        	adapter.get(i).notifyDataSetChanged();
        }

        selectedNum = 0;
    }
	
	@Override
    protected void clear() {
        super.clear();
        clearBet();
        showMatchSelectedNum();
        checkBtnStatus();
    }

	/**
	 * 设置清空投注按钮属性，是否可点击
	 */
	private void checkBtnStatus() {
		if (selectedNum == 0) {
			disableBetBtn();
			disableClearBtn();
		} else if (selectedNum < MIN_SELECTED ) {
			disableBetBtn();
			enableClearBtn();
		} else {
			enableBetBtn();
			enableClearBtn();
		}
	}

	/**
	 * 页面顶部显示选中场次
	 */
	private void showMatchSelectedNum() {
		betInf.setText(Html.fromHtml("已选择<font color='red'>" + selectedNum
				+ "</font>场比赛"));
	}
}
