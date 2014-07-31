package com.haozan.caipiao.activity.topup;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.umeng.analytics.MobclickAgent;
import com.unicom.dcLoader.Utils;


public class UnicomRecharge
        extends
        BasicActivity
        implements
        View.OnClickListener,
        SelecteTypePopupWindow.PopupSelectTypeClickListener
{
	private static final String[]  CODE;
	//	private static final String    CONTACT_US          = "如有任何问题请<u><font color='blue'>联系我们</color></u>。";
	//	private static final String    FAIL_TIPS           = "充值失败";
	private static final int[]     MONEY               = {
	        4,
	        10,
	        20,
	        40,
	        100,
	        200
	                                                   };
	private static final String[]  PRODUCT_DESCRIPTION = {
	        "彩金2元",
	        "彩金5元",
	        "彩金10元",
	        "彩金20元",
	        "彩金50",
	        "彩金100元"
	                                                   };
	private static final String[]  VACCODE_SUFFIX      = {
	        "004",
	        "005",
	        "006",
	        "007",
	        "008",
	        "009"
	                                                   };
	private boolean                isInit              = false;
	private Button                 mBtnSubmit;
	private String                 mCallUrl;
	private RelativeLayout         mLayoutProductSelected;
	private SelecteTypePopupWindow mPpwSelecteType;
	private int                    mProductIndex       = 0;
	private TextView               mTvContact;
	private TextView               mTvMoneyCost;
	private TextView               mTvProductSelected;
	private String[]               mVacCode            = null;
	private BetShowLotteryWay[]    mWayDataArray;
	
	static
	{
		CODE = new String[] {
		    "130910010789",
		    "130910010790",
		    "130910010791",
		    "130910010792",
		    "130910010793",
		    "131024013877"
		};
	}
	
	private void init() {
		this.mTvMoneyCost.setText(Html.fromHtml("<font color='#9D9D9D'>扣取话费：</font>" + MONEY[this.mProductIndex] + "元"));
		this.mTvProductSelected.setText(PRODUCT_DESCRIPTION[this.mProductIndex]);
	}
	
	private void initData() {
		this.mWayDataArray = new BetShowLotteryWay[1];
		BetShowLotteryWay localBetShowLotteryWay = new BetShowLotteryWay();
		String[] arrayOfString = new String[PRODUCT_DESCRIPTION.length];
		for (int i = 0;; i++)
		{
			if (i >= PRODUCT_DESCRIPTION.length)
			{
				localBetShowLotteryWay.setUpsInf(arrayOfString);
				this.mWayDataArray[0] = localBetShowLotteryWay;
				return;
			}
			arrayOfString[i] = PRODUCT_DESCRIPTION[i];
		}
	}
	
	private void pay(String paramString) {
		Utils.getInstances().setBaseInfo(this, true, true, this.mCallUrl);
		Utils.getInstances().pay(this, CODE[this.mProductIndex], this.mVacCode[this.mProductIndex], PRODUCT_DESCRIPTION[this.mProductIndex], String.valueOf(MONEY[this.mProductIndex]), paramString, "uid", Utils.VacMode.single, new PayResultListener());
	}
	
	private void setupViews() {
		this.mLayoutProductSelected = ((RelativeLayout) findViewById(R.id.spinner_layout));
		this.mLayoutProductSelected.setOnClickListener(this);
		this.mTvProductSelected = ((TextView) findViewById(R.id.spinner_text));
		this.mTvMoneyCost = ((TextView) findViewById(R.id.real_money));
		this.mBtnSubmit = ((Button) findViewById(R.id.submit));
		this.mBtnSubmit.setOnClickListener(this);
		this.mTvContact = ((TextView) findViewById(R.id.contact));
		this.mTvContact.setOnClickListener(this);
	}
	
	private void showPopupViews() {
		this.mWayDataArray[0].setSelectedIndex(this.mProductIndex);
		this.mPpwSelecteType = new SelecteTypePopupWindow(this, this.mWayDataArray);
		this.mPpwSelecteType.init();
		this.mPpwSelecteType.setPopupSelectTypeListener(this);
		this.mPpwSelecteType.showAsDropDown(this.mLayoutProductSelected);
	}
	
	private void submitStatisticsTopupSuccess() {
		HashMap<String, String> localHashMap1 = new HashMap<String, String>();
		localHashMap1.put("inf", "user top up unicom success");
		localHashMap1.put("more_inf", "username [" + this.appState.getUsername() + "]: user top up success");
		FlurryAgent.onEvent("top up success", localHashMap1);
		HashMap<String, String> localHashMap2 = new HashMap<String, String>();
		localHashMap2.put("way", "unicom");
		localHashMap2.put("MONEY", String.valueOf(MONEY[this.mProductIndex]));
		MobclickAgent.onEvent(this, "top_up_success", localHashMap2);
		besttoneEventCommint("top_up_success");
	}
	
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.submit)
		{
			if (HttpConnectUtil.isNetworkAvailable(this))
			{
				UnicomGetInfTask localUnicomGetInfTask = new UnicomGetInfTask();
				Integer[] arrayOfInteger = new Integer[1];
				arrayOfInteger[0] = Integer.valueOf(MONEY[this.mProductIndex]);
				localUnicomGetInfTask.execute(arrayOfInteger);
			}
			else
			{
				ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
			}
		}
		else if (arg0.getId() == R.id.contact)
		{
			Intent localIntent = new Intent();
			localIntent.setClass(this, Feedback.class);
			startActivity(localIntent);
		}
		else if (arg0.getId() == R.id.spinner_layout)
		{
			showPopupViews();
		}
	}
	
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_unicom_recharge);
		initData();
		setupViews();
		init();
	}
	
	protected void onStart() {
		super.onStart();
		HashMap<String, String> localHashMap1 = new HashMap<String, String>();
		localHashMap1.put("inf", "username [" + this.appState.getUsername() + "]: open phone card recharge");
		FlurryAgent.onEvent("v2 open phone card recharge", localHashMap1);
		HashMap<String, String> localHashMap2 = new HashMap<String, String>();
		localHashMap2.put("inf", "open phone card top up");
		localHashMap2.put("more_inf", "username [" + this.appState.getUsername() + "]: open top up");
		FlurryAgent.onEvent("open top up", localHashMap2);
	}
	
	public void selecteType(int paramInt1, int paramInt2) {
		this.mProductIndex = paramInt2;
		this.mTvMoneyCost.setText(Html.fromHtml("<font color='#9D9D9D'>扣取话费：</font>" + MONEY[paramInt2] + "元"));
		this.mTvProductSelected.setText(PRODUCT_DESCRIPTION[paramInt2]);
		this.mPpwSelecteType.dismiss();
	}
	
	protected void submitData() {
		MobclickAgent.onEvent(this, "open_phonecard_topup");
		besttoneEventCommint("open_phonecard_topup");
		MobclickAgent.onEvent(this, "open_topup", "phonecard");
	}
	
	public class PayResultListener
	        implements
	        Utils.UnipayPayResultListener
	{
		public PayResultListener() {
		}
		
		public void PayResult(String paramString1, int paramInt1, int paramInt2, String paramString2) {
			switch (paramInt1) {
				case 3:
				default:
					return;
				case 1:
					ViewUtil.showTipsToast(UnicomRecharge.this.mContext, "充值成功");
					return;
				case 2:
			}
			Toast.makeText(mContext,  "充值失败+"+paramString2+"----错误码："+String.valueOf(paramInt1), Toast.LENGTH_LONG).show();
			//ViewUtil.showTipsToast(UnicomRecharge.this.mContext, "充值失败+"+paramString2);
		}
	}
	
	class UnicomGetInfTask
	        extends
	        AsyncTask<Integer, Object, String>
	{
		UnicomGetInfTask() {
		}
		
		private HashMap<String, String> initHashMap(Integer paramInteger) throws Exception {
			HashMap<String, String> localHashMap = new HashMap<String, String>();
			localHashMap.put("service", "2005051");
			localHashMap.put("pid", LotteryUtils.getPid(UnicomRecharge.this));
			localHashMap.put("phone", AndroidHttpClient.encodeParameter(UnicomRecharge.this.appState.getUsername()));
			localHashMap.put("pay_type", "UNI");
			localHashMap.put("money", String.valueOf(paramInteger));
			return localHashMap;
		}
		
		@Override
		protected String doInBackground(Integer... params) {
			
			ConnectService localConnectService = new ConnectService(UnicomRecharge.this);
			String str = null;
			try
			{
				str = localConnectService.getJsonGet(3, Boolean.valueOf(true), initHashMap(params[0]));
				Logger.inf(str);
				return str;
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
			}
			return str;
			
		}
		
		protected void onPostExecute(String paramString) {
			super.onPostExecute(paramString);
			UnicomRecharge.this.dismissProgressDialog();
			String str1 = null;
			if (paramString == null)
			{
				str1 = "充值失败";
				ViewUtil.showTipsToast(UnicomRecharge.this.mContext, str1);
				return;
			}
			else
			{
				JsonAnalyse localJsonAnalyse = new JsonAnalyse();
				String str2 = localJsonAnalyse.getStatus(paramString);
				if (str2.equals("200"))
				{
					String str3 = localJsonAnalyse.getData(paramString, "response_data");
					UnicomRecharge.this.mCallUrl = localJsonAnalyse.getData(str3, "call_url");
					String str4 = localJsonAnalyse.getData(str3, "order_no");
					String str5 = localJsonAnalyse.getData(str3, "appid");
					String str6 = localJsonAnalyse.getData(str3, "mid");
					String str7 = localJsonAnalyse.getData(str3, "mcode");
					String str8 = localJsonAnalyse.getData(str3, "key");
					int i = 0;
					if ((UnicomRecharge.this.mCallUrl != null) && (str4 != null) && (str5 != null) && (str4 != null) && (str5 != null) && (str6 != null) && (str7 != null) && (str8 != null)) if (!UnicomRecharge.this.isInit)
					{
						Utils.getInstances().init(UnicomRecharge.this, str5, str7, str6, "明舜科技（北京）有限公司", "400 632 6360", "沃彩票", new PayResultListener());
						UnicomRecharge.this.mVacCode = new String[UnicomRecharge.VACCODE_SUFFIX.length];
						i = 0;
						for (i = 0; i < UnicomRecharge.VACCODE_SUFFIX.length; i++)
						{
							UnicomRecharge.this.mVacCode[i] = (str5 + UnicomRecharge.VACCODE_SUFFIX[i]);
						}
						UnicomRecharge.this.submitStatisticsTopupSuccess();
						UnicomRecharge.this.pay(str4);
						return;
					}
				}
				else if (str2.equals("300"))
				{
					str1 = localJsonAnalyse.getData(paramString, "error_desc");
				}
				else if (str2.equals("302"))
				{
					OperateInfUtils.clearSessionId(UnicomRecharge.this);
					UnicomRecharge.this.showLoginAgainDialog(UnicomRecharge.this.getResources().getString(2131296264));
					str1 = null;
				}
				else if (str2.equals("304"))
				{
					OperateInfUtils.clearSessionId(UnicomRecharge.this);
					UnicomRecharge.this.showLoginAgainDialog(UnicomRecharge.this.getResources().getString(2131296265));
					str1 = null;
				}
				if (null != str1) str1 = "充值失败";
				ViewUtil.showTipsToast(UnicomRecharge.this.mContext, str1);
			}
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			UnicomRecharge.this.showProgressDialog("充值提交中。。");
		}
		
	}
}
