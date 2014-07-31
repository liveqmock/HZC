package com.haozan.caipiao.activity.topup;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.adapter.RechargeSelectionListViewAdapter;
import com.haozan.caipiao.types.RechargeUiItem;
import com.haozan.caipiao.util.ActionUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 充值推荐
 * 
 * @author peter_wang
 * @create-time 2013-11-10 下午7:06:07
 */
public class TopupRecommend
    extends BasicActivity
    implements OnClickListener, OnItemClickListener  {
    public static final int UNIONPAY_DEBIT = 0;
    public static final int UNIONPAY_CREDIT = 1;

    public static final String[] TOPUP_TYPES = {"YY", "UP", "AP", "AN"};// 语音 银联快捷 支付宝 网页支付
    public static final int[] ICONS = {R.drawable.unionpay_voice_icon, R.drawable.unionpay_icon,
            R.drawable.alipay_icon, R.drawable.yinhangka};
    public static final String[] DESCRIPTIONS = {"回拨电话，安全可靠", "输入金额，跳到银联支付输入信息", "支付宝账户余额支付和银行卡支付",
            "选择银行，输入相关信息充值"};
    public static final String[] TOPUP_DEBIT_NAMES = {"储蓄卡语音支付", "快捷支付", "支付宝支付", "网页支付"};
    public static final String[] TOPUP_CREDIT_NAMES = {"信用卡语音支付", "快捷支付", "支付宝支付", "网页支付"};
    public static final String[] TOPUP_DEBIT_CLASSNAME = {UnionpayDebitCardTopup.class.getName(),
            ChinaUnionPaycharge.class.getName(), AlipayRecharge.class.getName(),
            BankCardNetRecharge.class.getName()};
    public static final String[] TOPUP_CREDIT_CLASSNAME = {UnionPayCreditCardTopup.class.getName(),
            ChinaUnionPaycharge.class.getName(), AlipayRecharge.class.getName(),
            BankCardNetRecharge.class.getName()};

    private ArrayList<RechargeUiItem> mTopupOtherWayItem;

    private RelativeLayout mLayoutBack;

    private LinearLayout mLayoutMainWay;
    private ImageView mIvRecommendWayIcon;
    private TextView mTvRecommendWay;
    private Button mBtnSubmit;

    private LinearLayout mLayoutOtherWay;
    private ListView mLvOtherWay;
    private RechargeSelectionListViewAdapter mOtherWayAdapter;

    private int mType;// 借记卡或信用卡
    private int mMainwayIndex;
    
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_recommend);
        
        setupViews();
        init();
    }

    private void initData() {
        mTopupOtherWayItem = new ArrayList<RechargeUiItem>();
    }

    private void setupViews() {
        mLayoutBack = (RelativeLayout) this.findViewById(R.id.back_layout);
        mLayoutBack.setOnClickListener(this);

        mLayoutMainWay = (LinearLayout) this.findViewById(R.id.main_way_layout);
        mLayoutMainWay.setOnClickListener(this);
        mIvRecommendWayIcon = (ImageView) this.findViewById(R.id.recommend_way_icon);
        mTvRecommendWay = (TextView) this.findViewById(R.id.recommend_way);
        mBtnSubmit = (Button) this.findViewById(R.id.topup_submit);
        mBtnSubmit.setOnClickListener(this);

        mLayoutOtherWay = (LinearLayout) this.findViewById(R.id.other_way_layout);
        mLvOtherWay = (ListView) this.findViewById(R.id.other_way_list_view);
        mLvOtherWay.setOnItemClickListener(this);
    }

    private void init() {
        mOtherWayAdapter = new RechargeSelectionListViewAdapter(this, mTopupOtherWayItem);
        mLvOtherWay.setAdapter(mOtherWayAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        mType = bundle.getInt("type");
        String way = bundle.getString("way");

        if (way != null) {
            initTopupWay(mType, way);
        }
    }

    /**
     * @param type
     * @param way 如YY，UP代表语音，快捷
     */
    private void initTopupWay(int type, String way) {
        String[] ways = way.split("\\,");

        initMainWay(type, ways[0]);

        initOtherWay(type, ways);
    }

    private void initMainWay(int type, String way) {
        mMainwayIndex = getWayIndex(way);

        if (mMainwayIndex != -1) {
            String bankName = getIntent().getExtras().getString("bank_name");

            mIvRecommendWayIcon.setBackgroundResource(ICONS[mMainwayIndex]);

            if (type == UNIONPAY_DEBIT) {
                mTvRecommendWay.setText(bankName + TOPUP_DEBIT_NAMES[mMainwayIndex]);
            }
            else {
                mTvRecommendWay.setText(bankName + TOPUP_CREDIT_NAMES[mMainwayIndex]);
            }
        }
    }

    private void initOtherWay(int type, String[] ways) {
        if (ways.length <= 1) {
            mLayoutOtherWay.setVisibility(View.GONE);
            return;
        }
        else {
            mLayoutOtherWay.setVisibility(View.VISIBLE);
        }

        for (int i = 1; i < ways.length; i++) {
            int wayIndex = getWayIndex(ways[i]);

            RechargeUiItem item = new RechargeUiItem();
            if (type == UNIONPAY_DEBIT) {
                item.setClassName(TOPUP_DEBIT_CLASSNAME[wayIndex]);
                item.setName(TOPUP_DEBIT_NAMES[wayIndex]);
            }
            else {
                item.setClassName(TOPUP_CREDIT_CLASSNAME[wayIndex]);
                item.setName(TOPUP_CREDIT_NAMES[wayIndex]);
            }
            item.setDescription(DESCRIPTIONS[wayIndex]);
            item.setEmphasis(false);
            item.setIcon(ICONS[wayIndex]);

            mTopupOtherWayItem.add(item);
        }

        mOtherWayAdapter.notifyDataSetChanged();
    }

    /**
     * 获取充值方式序号
     * 
     * @param way
     * @return
     */
    private int getWayIndex(String way) {
        for (int i = 0; i < TOPUP_TYPES.length; i++) {
            if (way.equals(TOPUP_TYPES[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void submitData() {
        String eventName = "open_topup_third";
        MobclickAgent.onEvent(this, eventName);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	ActionUtil.toPage(this, mTopupOtherWayItem.get(position).getClassName());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_layout) {
            finish();
        }
        else if (v.getId() == R.id.main_way_layout || v.getId() == R.id.topup_submit) {
            if (mType == UNIONPAY_DEBIT) {
                ActionUtil.toPage(this, TOPUP_DEBIT_CLASSNAME[mMainwayIndex]);
            }
            else {
                ActionUtil.toPage(this, TOPUP_CREDIT_CLASSNAME[mMainwayIndex]);
            }
        }
    }
}
