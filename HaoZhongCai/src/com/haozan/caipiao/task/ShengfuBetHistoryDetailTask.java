package com.haozan.caipiao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.activity.BetFootBallTeamListShow;
import com.haozan.caipiao.activity.bet.shisichang.ShisichangActivity;
import com.haozan.caipiao.adapter.AthleticsListAdapter;
import com.haozan.caipiao.adapter.FootBallBetSelectionShowAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.ShengFuBetHistoryDetailItemData;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class ShengfuBetHistoryDetailTask extends
		AsyncTask<Void, Object, String> {

	private Context context;
	private ArrayList<ShengFuBetHistoryDetailItemData> matchDataList;
	private String kind;
	private String orderId;
	private AthleticsListAdapter athleticsListAdapter;
	private FootBallBetSelectionShowAdapter footBallBetSelectionShowAdapter;
	private OnShengFuHisDetailTaskChangeListener clickListener;
	private Map<String, String> mapBetHistoryDetail;

	public ShengfuBetHistoryDetailTask(Context context,
			ArrayList<ShengFuBetHistoryDetailItemData> matchDataList,
			String kind, String orderId, Map<String, String> mapBetHistoryDetail) {
		this.context = context;
		this.matchDataList = matchDataList;
		this.kind = kind;
		this.orderId = orderId;
		this.mapBetHistoryDetail = mapBetHistoryDetail;
	}
	
    private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2009040");
            parameter.put("pid", LotteryUtils.getPid(context));
            parameter.put("lottery_id", kind);
            parameter.put("order_id", orderId);
            parameter.put("term", "91");
            return parameter;
        }
 
	@Override
	protected String doInBackground(Void... params) {
		ConnectService connect = new ConnectService(context);
		String json = null;
		try {
			json = connect.getJsonGet(5, false, initHashMap());
			//TODO fake data
			/*InputStream inputStream = context.getResources().openRawResource(R.raw.json);
			InputStreamReader inputStreamReader = null;  
		        inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
		    BufferedReader reader = new BufferedReader(inputStreamReader);  
		    StringBuffer sb = new StringBuffer("");  
		    String line;  
		    while ((line = reader.readLine()) != null) {  
	            sb.append(line);  
	            sb.append("\n");  
		    }  
		    json = sb.toString();*/  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void onPostExecute(String json) {
		super.onPostExecute(json);
		if (json != null) {
			JsonAnalyse ja = new JsonAnalyse();
			String status = ja.getStatus(json);
			if (status.equals("200")) {
				try {
					/*
					String home_team;
					String guest_team;
					String game_id;
					String term;
					String match_id;
					
 					String match_name;
					String sellout_time;
					String remark;
					//赔率
					String sp1;
					sp2;
					
					String result;
					 */
					JSONObject footBallTeamArray = new JSONObject(ja.getData(json, "response_data"));//1
					
					mapBetHistoryDetail.put("order_id", footBallTeamArray.getString("order_id"));//1
					mapBetHistoryDetail.put("buy", footBallTeamArray.getString("buy"));
					mapBetHistoryDetail.put("win",footBallTeamArray.getString("win"));
					mapBetHistoryDetail.put("prize", footBallTeamArray.getString("prize"));
					mapBetHistoryDetail.put("money",footBallTeamArray.getString("money"));
					
                    // mapBetHistoryDetail.put("bet_type",analyseBetCodeOrBetType(footBallTeamArray.getString("codes"), -1,1));
					
					mapBetHistoryDetail.put("xy",footBallTeamArray.getString("xy"));
					mapBetHistoryDetail.put("term",footBallTeamArray.getString("term"));
					mapBetHistoryDetail.put("d",footBallTeamArray.getString("d"));
					mapBetHistoryDetail.put("mode",footBallTeamArray.getString("mode"));
					mapBetHistoryDetail.put("codes", footBallTeamArray.getString("codes"));
					
					mapBetHistoryDetail.put("loc",footBallTeamArray.getString("loc"));
					mapBetHistoryDetail.put("teams",footBallTeamArray.getString("teams"));
					mapBetHistoryDetail.put("cm_sp",footBallTeamArray.getString("cm_sp"));
					mapBetHistoryDetail.put("cm_bb",footBallTeamArray.getString("cm_bb"));
					mapBetHistoryDetail.put("split_num",footBallTeamArray.getString("split_num"));
					
					JSONArray footteamNameArray = new JSONArray(footBallTeamArray.getString("data"));
					for (int i = 0; i < footteamNameArray.length(); i++) {
						ShengFuBetHistoryDetailItemData shengFuBetHistoryDetailItemData = new ShengFuBetHistoryDetailItemData();
						JSONObject footBallTeamObject = new JSONObject(footteamNameArray.getString(i));
						try {
							//参赛队主
							shengFuBetHistoryDetailItemData.setHome_team(footBallTeamObject.getString("home_team"));
							shengFuBetHistoryDetailItemData.setGuest_team(footBallTeamObject.getString("guest_team"));//参赛队次
							shengFuBetHistoryDetailItemData.setGame_id(footBallTeamObject.getString("game_id"));
							shengFuBetHistoryDetailItemData.setTerm(footBallTeamObject.getString("term"));
							shengFuBetHistoryDetailItemData.setMatch_id(footBallTeamObject.getString("match_id"));
							
							shengFuBetHistoryDetailItemData.setMatch_name(footBallTeamObject.getString("match_name"));
							shengFuBetHistoryDetailItemData.setSellout_time(footBallTeamObject.getString("sellout_time"));
							shengFuBetHistoryDetailItemData.setRemark(footBallTeamObject.getString("remark"));
							shengFuBetHistoryDetailItemData.setSp1(footBallTeamObject.getString("sp1"));
							shengFuBetHistoryDetailItemData.setSp2(footBallTeamObject.getString("sp2"));
							
							shengFuBetHistoryDetailItemData.setResult(footBallTeamObject.getString("result"));
							
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						matchDataList.add(shengFuBetHistoryDetailItemData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (context.getClass() == ShisichangActivity.class)
					athleticsListAdapter.notifyDataSetChanged();
				else if (context.getClass() == BetFootBallTeamListShow.class)
					footBallBetSelectionShowAdapter.notifyDataSetChanged();
				if (clickListener != null)
					clickListener.onShengFuHisActionClick("200");
			} else {
				String inf = "数据获取失败";
				ViewUtil.showTipsToast(context, inf);
			}
		} else {
			
			clickListener.onShengFuHisActionClick(null);
		}
	}

	public void setOnShengFuHisDeTaskChangeListener(
			OnShengFuHisDetailTaskChangeListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public interface OnShengFuHisDetailTaskChangeListener {
		public void onShengFuHisActionClick(String status);
	}
}
