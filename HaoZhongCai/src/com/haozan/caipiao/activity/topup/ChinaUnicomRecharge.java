package com.haozan.caipiao.activity.topup;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haozan.caipiao.R;
import com.haozan.caipiao.control.topup.ChinaUnionOldPayTopupControl;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.widget.SelecteTypePopupWindow;
import com.haozan.caipiao.widget.SelecteTypePopupWindow.PopupSelectTypeClickListener;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;
import com.unicom.dcLoader.Utils.VacMode;
import com.unionpay.upomp.lthj.plugin.ui.BaseActivity;


public class ChinaUnicomRecharge
        extends
        BaseActivity
        implements
        OnClickListener,
        PopupSelectTypeClickListener
{
	private final static String          appID         = "907455415820130827175225224700";
	private final static String          cpCode        = "9074554158";
	private final static String          cpID          = "86007252";
	private final static String          key           = "29921001f2f04bd3baee";
	private final static String          servicePhone  = "";
	private final static String          company       = "明舜科技";
	private final static String          appName       = "沃彩票";
	
	private final static String[]        customCodes   = {
	        "907455415820130827175225224700004",
	        "907455415820130827175225224700005",
	        "907455415820130827175225224700006",
	        "907455415820130827175225224700007",
	        "907455415820130827175225224700008",
	        "907455415820130827175225224700009"
	                                                   };
	private final static String[]        vacCode       = {
	        "130910010789",
	        "130910010790",
	        "130910010791",
	        "130910010792",
	        "130910010793",
	        "131024013877"
	                                                   };
	private final static String[]        props         = {
	        "彩金2元",
	        "彩金5元",
	        "彩金10元",
	        "彩金20元",
	        "彩金50元",
	        "彩金100元"
	                                                   };
	
	private final static String[]        priceStrings  = {
	        "4元",
	        "10元",
	        "20元",
	        "40元",
	        "100元",
	        "200元"
	                                                   };
	Format                               f             = new SimpleDateFormat("yyyyMMddHHmmss");
	private BetShowLotteryWay[]          wayDataArray;
	private ChinaUnionOldPayTopupControl mTopupControl;
	private Handler                      mHandler;
	/**************************************/
	private ImageButton                  help;
	private TextView                     contact;
	
	private LinearLayout                 eposSpinner;
	private TextView                     eposSpinnerText;
	private ImageView                    eposSpinnerIcon;
	
	private Button                       btnTopup;
	private String[]                     realMoney     = {
	        "4",
	        "10",
	        "20",
	        "40",
	        "100",
	        "100"
	                                                   };
	private String                       rechargeMoney;
	private int                          selectedIndex = 0;
	private boolean                      fromFast      = false;
	private String                       bankName;
	private String                       type;
	
	private SelecteTypePopupWindow       popupWindowWaySelect;
	
	private String                       mOrderId;
	
	private Button                       mBtnContackServer;
	private Button                       mBtnOtherTopup;
	
	/***********************************************/
	@Override
	protected void onCreate(Bundle arg0) {
		initData();
		super.onCreate(arg0);
		setContentView(R.layout.activity_china_unicom_topup);
		SetupView();
		init();
		
	}
	
	/**
	 * 
	 */
	private void initRecharge() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Utils.getInstances().init(ChinaUnicomRecharge.this, appID, cpCode, cpID, company, servicePhone, appName, new PayResultListener());
	}
	
	private void initData() {
		mHandler = new Handler();
		wayDataArray = new BetShowLotteryWay[1];
		BetShowLotteryWay lotteryway = new BetShowLotteryWay();
		lotteryway.setUpsInf(priceStrings);
		wayDataArray[0] = lotteryway;
		mTopupControl = new ChinaUnionOldPayTopupControl(this, mHandler);
	}
	
	private void SetupView() {
		contact = (TextView) this.findViewById(R.id.contact);
		contact.setOnClickListener(this);
		eposSpinner = (LinearLayout) findViewById(R.id.epos_spinner);
		eposSpinner.setOnClickListener(this);
		eposSpinnerText = (TextView) findViewById(R.id.epos_spinner_text);
		eposSpinnerIcon = (ImageView) findViewById(R.id.epos_spinner_icon);
		help = (ImageButton) findViewById(R.id.help);
		help.setOnClickListener(this);
		btnTopup = (Button) findViewById(R.id.submit);
		btnTopup.setOnClickListener(this);
		
		mBtnContackServer = (Button) this.findViewById(R.id.btn_contact_customer_service);
		mBtnContackServer.setOnClickListener(this);
		mBtnOtherTopup = (Button) this.findViewById(R.id.btn_more_top_up_way);
		mBtnOtherTopup.setOnClickListener(this);
	}
	
	private void init() {
		rechargeMoney = realMoney[0];
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null)
		{
			selectedIndex = bundle.getInt("money_index_num");
			fromFast = bundle.getBoolean("from_fast");
			rechargeMoney = realMoney[selectedIndex];
			bankName = bundle.getString("bank_name");
			type = bundle.getString("type");
		}
		eposSpinnerText.setText(Html.fromHtml(mTopupControl.createMoneyShow(rechargeMoney)));
		
		contact.setText(Html.fromHtml(getString(R.string.topup_contact_us)));
		
		if (fromFast)
		{
			mTopupControl.toTopup(bankName, type, rechargeMoney);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.epos_spinner)
		{
			showPopupViews();
		}
		else if (v.getId() == R.id.submit)
		{
			
		}
	}
	
	private void showPopupViews() {
		wayDataArray[0].setSelectedIndex(selectedIndex);
		
		popupWindowWaySelect = new SelecteTypePopupWindow(ChinaUnicomRecharge.this, wayDataArray);
		popupWindowWaySelect.init();
		popupWindowWaySelect.setPopupSelectTypeListener(this);
		
		popupWindowWaySelect.showAsDropDown(eposSpinner);
	}
	
	public class PayResultListener
	        implements
	        UnipayPayResultListener
	{
		
		public void PayResult(String paycode, int flag, int flag2, String error) {
			Toast.makeText(ChinaUnicomRecharge.this, "flag=" + flag + ";code=" + paycode + ";error=" + error, Toast.LENGTH_LONG).show();
			// flag为支付回调结果，flag2为回调类型，error为当前结果描述
			
			switch (flag) {
				case Utils.SUCCESS:
					//此处放置支付请求已提交的相关处理代码
					break;
				
				case Utils.FAILED:
					//此处放置支付请求失败的相关处理代码
					break;
				
				case Utils.CANCEL:
					//此处放置支付请求被取消的相关处理代码
					break;
				
				default:
					break;
			}
		}
		
	}
	
	@Override
	public void selecteType(int type, int index) {
		// TODO Auto-generated method stub
		rechargeMoney = realMoney[index];
		eposSpinnerText.setText(Html.fromHtml("<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元"));
		selectedIndex = index;
		popupWindowWaySelect.dismiss();
	}
	
	private void submit(String money) {
		Utils.getInstances().setBaseInfo(ChinaUnicomRecharge.this, true, true, "http://123.125.219.84/log-app/test");
		int i = -1;
		if (money.equals(priceStrings[0])) i = 0;
		else if (money.equals(priceStrings[1])) i = 1;
		else if (money.equals(priceStrings[2])) i = 2;
		else if (money.equals(priceStrings[3])) i = 3;
		else if (money.equals(priceStrings[4])) i = 4;
		else if (money.equals(priceStrings[5])) i = 5;
		if (i >= 0)
		{
			String uid="";
			Utils.getInstances().pay(ChinaUnicomRecharge.this, vacCode[i], customCodes[i], props[i], realMoney[i], f.format(new Date()) + "0"+uid, uid, VacMode.single, new PayResultListener());
		}
	}
}
