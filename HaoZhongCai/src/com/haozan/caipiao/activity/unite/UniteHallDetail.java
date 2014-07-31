package com.haozan.caipiao.activity.unite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.bet.BetPayControl;
import com.haozan.caipiao.control.order.OrderControl;
import com.haozan.caipiao.types.bet.UniteJoinSubmitOrder;
import com.haozan.caipiao.types.order.UniteOrderDetail;
import com.haozan.caipiao.types.order.jczq.JCZQOrderDetail;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TextUtil;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.lottery.analyse.JCZQNumAnalyse;
import com.haozan.caipiao.util.lottery.analyse.LotteryNumAnalyse;

/**
 * 合买订单详细
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午11:01:54
 */
public class UniteHallDetail
    extends BasicActivity
    implements OnClickListener {

    private TextView mTvTitle;

    private TextView mTvCommission;
    private TextView mTvProgress;
    private TextView mTvInsurance;
    private TextView mTvUniteOrder;

    private ImageView mIvLotteryIcon;
    private TextView mTvKind;
    private TextView mTvTerm;

    private TextView mTvTopFirstDesc;
    private TextView mTvTopSecondDesc;
    private TextView mTvTopThirdDesc;
    private TextView mTvSum;
    private TextView mTvEachPrice;
    private TextView mTvLastAmount;

    private TextView mTvSponsor;

    private boolean isShowProgramInf = false;
    private TextView mTvShowDetail;
    private LinearLayout mLayoutDetail;
    private TextView mTvUnitTitle;
    private TextView mTvDescribe;
    private TextView mTvIdentifier;
    private TextView mTvTime;

    private boolean isShowNum = true;
    private RelativeLayout mLayoutShowNum;
    private TextView mTvNumType;
    private ImageView mViewShowNum;

    private RelativeLayout mLayoutNumContainer;
    private LinearLayout mLayoutNumber;
    private TextView mTvSportDetails;

    private LinearLayout mLayoutProtocol;
    private ImageView mIvProtocolFlag;
    private TextView mTvProtocol;

    private String mLotteryName;

    private boolean isShowShareContent = true;
    private View mSharenWholeLayout;
    private RelativeLayout mLayoutTranspond;
    private ImageView mIvTranspondFlag;
    private ImageView mIvTranspondClose;
    private TextView mTvTranspondOpen;
    private LinearLayout mLayoutShareToGarden;
    private EditText mEtShareContent;

    private TextView mTvAccountStatus;
    private LinearLayout mLayoutBottomTips;
    private EditText mEtAmountBuy;
    private TextView mTvLastAmountBottom;
    private TextView mTvAccount;
    private TextView mTvOrderMoney;
    private Button mBtBet;

    // 购买金额
    private double mOrderMoney;
    // 每份金额
    private double mEachOrderMoney;
    // 购买份数
    private int mBuyAmount = 1;
    // 账号状态，见BetPayControl
    private int mAccountStatus;

    private String mLotteryKind;
    // 是否是合买投注记录，显示内容不同
    private boolean isUniteBetHistory = false;
    private String mOrderId;
    private UniteOrderDetail mOrderDetail;
    private OrderControl mOrderControl;
    private BetPayControl mBetPayControl;

    private JCZQOrderDetail mJCZQOrderTeam;

    private MyHandler mHandler;

    private static class MyHandler
        extends WeakReferenceHandler<UniteHallDetail> {

        public MyHandler(UniteHallDetail reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(UniteHallDetail activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.SHOW_PROGRESS:
                    activity.showProgress();
                    break;
                case ControlMessage.DISMISS_PROGRESS:
                    activity.dismissProgress();
                    break;
                case ControlMessage.UNITE_ORDER_DETAIL_SUCCESS_RESULT:
                    activity.mOrderDetail = (UniteOrderDetail) msg.obj;
                    if (activity.mOrderDetail != null) {
                        activity.showOrderDetail();
                    }
                    break;
                case ControlMessage.UNITE_ORDER_DETAIL_FAIL_RESULT:
                    break;

                case ControlMessage.UNITE_JOIN_SUCCESS_RESULT:
                    ViewUtil.showTipsToast(activity, "跟单成功");
                    activity.appState.setAccount(activity.appState.getAccount() - activity.mOrderMoney);
                    activity.mBetPayControl.betStatusView(activity.mTvAccount, activity.mBtBet,
                                                          activity.mOrderMoney);
                    //activity.getUniteOrderDetail();
                    break;

                case ControlMessage.UNITE_JOIN_FAIL_RESULT:
                    ViewUtil.showTipsToast(activity, (String) msg.obj);
                    break;

                case ControlMessage.SPORT_ORDER_DETAIL_SUCCESS_RESULT:
                    activity.displaySportCodes((String) msg.obj);
                    break;

                case ControlMessage.SPORT_ORDER_DETAIL_FAIL_RESULT:
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unite_hall_detail);
        setupViews();
        init();
    }

    private void showOrderDetail() {
        mLotteryKind = mOrderDetail.getLotteryId();
        mLotteryName = LotteryUtils.getLotteryName(mLotteryKind);
        mTvKind.setText(mLotteryName);
        mIvLotteryIcon.setImageResource(LotteryUtils.getLotteryIcon(mLotteryKind));
        if (LotteryUtils.isDigitalBet(mLotteryKind)) {
            mTvTerm.setText(mOrderDetail.getTerm() + "期");
        }

        mBtBet.setEnabled(true);

        int rate = mOrderDetail.getRate();
        mTvProgress.setText(String.valueOf(rate) + "%");
        if (rate == 100) {
            mTvProgress.setTextSize(28);
        }
        if (rate < 30) {
            mTvProgress.setTextColor(mContext.getResources().getColor(R.color.yellow_light));
        }
        else if (rate > 69) {
            mTvProgress.setTextColor(mContext.getResources().getColor(R.color.red_text));
        }
        else {
            mTvProgress.setTextColor(mContext.getResources().getColor(R.color.orange_bg_middle));
        }

        if (mOrderDetail.getInsurance() > 0) {
            mTvInsurance.setVisibility(View.VISIBLE);
            mTvInsurance.setText("保" + mOrderDetail.getInsurance() + "%");

        }
        else {
            mTvInsurance.setVisibility(View.GONE);
        }

        if (mOrderDetail.getCommission() > 0) {
            mTvCommission.setVisibility(View.VISIBLE);
            mTvCommission.setText("佣金" + mOrderDetail.getCommission() + "%");
        }
        else {
            mTvCommission.setVisibility(View.GONE);
        }

        showTopViewInf();

        mTvSponsor.setText("发起人：" + mOrderDetail.getNickname());

        // 0代表成功，1代表满员，其他是失败
        int orderStatus = Integer.valueOf(mOrderDetail.getProgramStatus());
        if (orderStatus != 0) {
            mLayoutProtocol.setVisibility(View.GONE);
            mSharenWholeLayout.setVisibility(View.GONE);

            if (isUniteBetHistory && getIntent().getExtras().getBoolean("is_win", false)) {
                mTvAccountStatus.setVisibility(View.VISIBLE);
                mTvAccountStatus.setText(getString(R.string.unite_order_win_status_tips));
                mLayoutBottomTips.setVisibility(View.GONE);
            }
            else {
                if (orderStatus == 1) {
                    mTvAccountStatus.setVisibility(View.VISIBLE);
                    mTvAccountStatus.setText(getString(R.string.unite_order_full_status_tips));
                }
                mLayoutBottomTips.setVisibility(View.GONE);
            }
        }

        mTvUnitTitle.setText(mOrderDetail.getTitle());
        mTvDescribe.setText(mOrderDetail.getDescribe());
        mTvIdentifier.setText(mOrderDetail.getProgramId());

        String secrecy = mOrderDetail.getSecrecy();
        if (secrecy.equals("1")) {
            mViewShowNum.setVisibility(View.VISIBLE);
            if (LotteryUtils.isDigitalBet(mLotteryKind)) {
                displayDigitalCodes(mOrderDetail.getCodes());
            }
            else {
                mBetPayControl.setUniteSportOrderDetail(mLotteryKind, mOrderId);
            }
            mTvNumType.setText("");
            mLayoutShowNum.setEnabled(true);
        }
        else if (secrecy.equals("2")) {
            mViewShowNum.setVisibility(View.GONE);
            mTvNumType.setText("跟单可见");
            mLayoutShowNum.setEnabled(false);
        }
        else if (secrecy.equals("3")) {
            mViewShowNum.setVisibility(View.GONE);
            mTvNumType.setText("开奖后公开");
            mLayoutShowNum.setEnabled(false);
        }

        mTvTime.setText(mOrderDetail.getUniteTime());

        mEachOrderMoney = mOrderDetail.getPerMoney();
        moneyChange();
    }

    private void showTopViewInf() {
        if (isUniteBetHistory) {
            double betMoney = getIntent().getExtras().getDouble("bet_money");
            mTvSum.setText((int) (betMoney / mOrderDetail.getPerMoney()) + "份");

            mTvUniteOrder.setVisibility(View.VISIBLE);
            mTvUniteOrder.setText("合买拆分：￥" + mOrderDetail.getAllAmount() * mOrderDetail.getPerMoney() +
                ",每份" + mOrderDetail.getPerMoney() + "元");
        }
        else {
            mTvSum.setText("￥" + mOrderDetail.getAllAmount() * mOrderDetail.getPerMoney());
            mTvEachPrice.setText("￥" + mOrderDetail.getPerMoney());
            mTvLastAmount.setText(String.valueOf(mOrderDetail.getAllAmount() - mOrderDetail.getBoughtAmount()));
        }

        mTvLastAmountBottom.setText("/" + (mOrderDetail.getAllAmount() - mOrderDetail.getBoughtAmount()) +
            "份");
    }

    private void displayDigitalCodes(String codes) {
        LotteryNumAnalyse.showOrderNum(this, mLotteryKind, mLayoutNumber, codes);
    }

    private void displaySportCodes(String codes) {
        mJCZQOrderTeam = LotteryNumAnalyse.getSportOrderAnalyse(this, mLotteryKind, codes);

        showBunch();
        if (mJCZQOrderTeam != null) {
            mTvTerm.setText(JCZQNumAnalyse.getSportWayName(mJCZQOrderTeam.getBetway()));
        }

        LotteryNumAnalyse.showSportOrderTeam(this, mLotteryKind, mLayoutNumber, mJCZQOrderTeam);
    }

    private void showBunch() {
        if (mJCZQOrderTeam != null) {
            StringBuilder sb = new StringBuilder();
            String bunch = mJCZQOrderTeam.getBunch();
            if (TextUtils.isEmpty(bunch) == false) {
                String[] bunchSplite = bunch.split(";");
                for (int i = 0; i < bunchSplite.length; i++) {
                    sb.append(JCZQNumAnalyse.getSportBunchName(bunchSplite[i]));

                    if (i != bunchSplite.length - 1) {
                        sb.append(",");
                    }
                }

                sb.append(" " + mJCZQOrderTeam.getTimes() + "倍");

                mTvNumType.setText(sb.toString());
            }
            sb = null;
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        }

        mHandler = new MyHandler(this);

        isUniteBetHistory = bundle.getBoolean("is_unite_bet_historoy", false);
        mOrderId = bundle.getString("program_id");// 方案编号

        mOrderControl = new OrderControl(this, mHandler);
        mBetPayControl = new BetPayControl(this, mHandler);
    }

    private void setupViews() {
        mTvTitle = (TextView) findViewById(R.id.newCmtextView);

        mTvCommission = (TextView) findViewById(R.id.commision);
        mTvProgress = (TextView) findViewById(R.id.unite_progress);
        mTvInsurance = (TextView) findViewById(R.id.insurance);
        mTvUniteOrder = (TextView) findViewById(R.id.unite_order_desc);

        mIvLotteryIcon = (ImageView) findViewById(R.id.lottery_icon);
        mTvKind = (TextView) findViewById(R.id.kind);
        mTvTerm = (TextView) findViewById(R.id.term);

        mTvTopFirstDesc = (TextView) findViewById(R.id.top_desc_first);
        mTvTopSecondDesc = (TextView) findViewById(R.id.top_desc_second);
        mTvTopThirdDesc = (TextView) findViewById(R.id.top_desc_third);
        mTvSum = (TextView) findViewById(R.id.top_first);
        mTvEachPrice = (TextView) findViewById(R.id.top_second);
        mTvLastAmount = (TextView) findViewById(R.id.top_third);

        mTvSponsor = (TextView) findViewById(R.id.sponsor);
        mTvSponsor.setOnClickListener(this);
        mTvShowDetail = (TextView) findViewById(R.id.show_detail);
        mTvShowDetail.setOnClickListener(this);

        mLayoutDetail = (LinearLayout) findViewById(R.id.programInf_layout);
        mTvUnitTitle = (TextView) findViewById(R.id.unite_title);
        mTvDescribe = (TextView) findViewById(R.id.unite_describe);
        mTvIdentifier = (TextView) findViewById(R.id.unite_identifier);
        mTvTime = (TextView) findViewById(R.id.unite_time);

        mLayoutShowNum = (RelativeLayout) findViewById(R.id.show_betcodes);
        mLayoutShowNum.setOnClickListener(this);
        mTvNumType = (TextView) findViewById(R.id.unite_lottery_num_type);
        mViewShowNum = (ImageView) findViewById(R.id.icon_flag_betcode);
        mLayoutNumContainer = (RelativeLayout) findViewById(R.id.lottery_num_container);
        mLayoutNumber = (LinearLayout) findViewById(R.id.number_layout);
        mTvSportDetails = (TextView) findViewById(R.id.foot_ball_bet_detail);

        mLayoutProtocol = (LinearLayout) findViewById(R.id.protocol_layout);
        mIvProtocolFlag = (ImageView) findViewById(R.id.bet_protocol_select);
        mIvProtocolFlag.setOnClickListener(this);
        mTvProtocol = (TextView) findViewById(R.id.bet_protocol);
        mTvProtocol.setOnClickListener(this);

        mSharenWholeLayout = findViewById(R.id.share_content_layout);
        mLayoutTranspond = (RelativeLayout) findViewById(R.id.layout_transpond);
        mLayoutTranspond.setOnClickListener(this);
        mIvTranspondFlag = (ImageView) findViewById(R.id.transpond_select);
        mIvTranspondClose = (ImageView) findViewById(R.id.share_more_inf_close);
        mIvTranspondClose.setOnClickListener(this);
        mTvTranspondOpen = (TextView) findViewById(R.id.share_more_inf_open);
        mTvTranspondOpen.setOnClickListener(this);
        mLayoutShareToGarden = (LinearLayout) findViewById(R.id.layout_share_to_garden);
        mEtShareContent = (EditText) findViewById(R.id.edit_share_content);

        mTvAccountStatus = (TextView) findViewById(R.id.status_tips);
        mLayoutBottomTips = (LinearLayout) findViewById(R.id.bottom_buy_layout);
        mEtAmountBuy = (EditText) findViewById(R.id.amount_buy);
        mTvLastAmountBottom = (TextView) findViewById(R.id.tv_unite_last_num_bottom);
        mTvAccount = (TextView) findViewById(R.id.account);
        mTvOrderMoney = (TextView) findViewById(R.id.order_money);
        mBtBet = (Button) findViewById(R.id.bet);
        mBtBet.setOnClickListener(this);
    }

    private void init() {
        initInfShow();

        mEtAmountBuy.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amount = mEtAmountBuy.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    mBuyAmount = 1;
                }
                else {
                    int num = Integer.valueOf(amount);

                    if (mOrderDetail != null &&
                        num > mOrderDetail.getAllAmount() - mOrderDetail.getBoughtAmount()) {

                        mBuyAmount = mOrderDetail.getAllAmount() - mOrderDetail.getBoughtAmount();

                        mEtAmountBuy.setText(String.valueOf(mBuyAmount));
                        mEtAmountBuy.setSelection(mEtAmountBuy.getText().toString().length());

                        ViewUtil.showTipsToast(UniteHallDetail.this, "最多购买" + mBuyAmount + "份");
                    }
                    else {
                        mBuyAmount = num;
                    }
                }

                moneyChange();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getUniteOrderDetail();
    }

    private void moneyChange() {
        refreshBottomMoney();
        mBetPayControl.betStatusView(mTvAccount, mBtBet, mOrderMoney);
        refreshShareContent();
    }

    private void refreshBottomMoney() {
        mOrderMoney = mEachOrderMoney * mBuyAmount;
        mTvOrderMoney.setText(mOrderMoney + "元");
    }

    private void getUniteOrderDetail() {
        if (HttpConnectUtil.isNetworkAvailable(this)) {
            mOrderControl.setUniteOrderDetail(mOrderId);
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    private void refreshShareContent() {
        String shareContent = "参与了" + mLotteryName + "合买" + mOrderMoney + "元";
        String location = mBetPayControl.getmLocation();
        if (location != null) {
            shareContent += "\n" + location;
        }
        mEtShareContent.setText(shareContent);
    }

    private void initInfShow() {
        if (isUniteBetHistory) {
            changeViewToUniteBetHistory();
        }
        else {
            changeViewToUniteJoin();
        }

        TextUtil.setTextBold(mTvKind);

        mTvProtocol.setText(Html.fromHtml("<u>" + "同意委托投注协议" + "</u>"));

        mBetPayControl.initLastShareSetting(mIvTranspondFlag);

        mBetPayControl.betStatusView(mTvAccount, mBtBet, mOrderMoney);
    }

    /**
     * 合买跟单详细页面
     */
    private void changeViewToUniteJoin() {
        mTvTitle.setText("方案详情");

        Bundle bundle = getIntent().getExtras();
        double betMoney = bundle.getDouble("bet_money");
        double eachMoney = bundle.getDouble("each_price");
        int lastAmount = bundle.getInt("last_amount");

        mTvSum.setText("￥" + betMoney);
        mTvEachPrice.setText("￥" + eachMoney);
        mTvLastAmount.setText(String.valueOf(lastAmount));
        mEachOrderMoney = eachMoney;

        mTvLastAmountBottom.setText("/" + (lastAmount) + "份");
    }

    /**
     * 传入的是合买投注记录详细的显示，对显示上做特别处理
     */
    private void changeViewToUniteBetHistory() {
        Bundle bundle = getIntent().getExtras();
        double betMoney = bundle.getDouble("bet_money");
        boolean isWin = bundle.getBoolean("is_win", false);
        double winMoney = bundle.getDouble("win_money");
        String orderStatus = bundle.getString("order_status");

        mTvTitle.setText("合买记录详细");
        mTvTopFirstDesc.setText("购买份数");
        mTvEachPrice.setText("￥" + betMoney);
        if (isWin) {
            mTvLastAmount.setTextColor(getResources().getColor(R.color.red));
            mTvLastAmount.setText("中奖" + winMoney + "元");
        }
        else {
            mTvLastAmount.setText(orderStatus);
        }
        mTvTopSecondDesc.setText("购买金额");
        mTvTopThirdDesc.setText("订单状态");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_detail:
                if (!isShowProgramInf) {
                    mLayoutDetail.setVisibility(View.VISIBLE);
                    isShowProgramInf = true;
                    mTvShowDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_new, 0);
                }
                else {
                    mLayoutDetail.setVisibility(View.GONE);
                    isShowProgramInf = false;
                    mTvShowDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_new, 0);
                }
                break;

            case R.id.show_betcodes:
                if (!isShowNum) {
                    mLayoutNumContainer.setVisibility(View.VISIBLE);
                    isShowNum = true;
                    mViewShowNum.setBackgroundResource(R.drawable.icon_up_new);
                }
                else {
                    mLayoutNumContainer.setVisibility(View.GONE);
                    isShowNum = false;
                    mViewShowNum.setBackgroundResource(R.drawable.icon_down_new);
                }
                break;

            case R.id.share_more_inf_open:
            case R.id.share_more_inf_close:
                if (!isShowShareContent) {
                    mLayoutShareToGarden.setVisibility(View.VISIBLE);
                    mIvTranspondClose.setVisibility(View.VISIBLE);
                    mTvTranspondOpen.setVisibility(View.GONE);
                    isShowShareContent = true;
                }
                else {
                    mLayoutShareToGarden.setVisibility(View.GONE);
                    mIvTranspondClose.setVisibility(View.GONE);
                    mTvTranspondOpen.setVisibility(View.VISIBLE);
                    isShowShareContent = false;
                }
                break;

            case R.id.layout_transpond:
                mBetPayControl.clickShareContent(mIvTranspondFlag);
                break;

            case R.id.bet_protocol:
                mBetPayControl.toBetProtocol();
                break;

            case R.id.bet_protocol_select:
                mBetPayControl.clickProtocol(mBtBet, mIvProtocolFlag);
                break;

            case R.id.sponsor:
                Intent intent = new Intent();
                if (appState.getUserid().equals(mOrderDetail.getUserId())) {
                    intent.setClass(UniteHallDetail.this, MyProfileActivity.class);
                }
                else {
                    Bundle b = new Bundle();
                    b.putInt("userId", Integer.valueOf(mOrderDetail.getUserId()));
                    b.putBoolean("is_continue_pass", true);
                    b.putString("class_name", UserProfileActivity.class.getName());
                    intent.putExtras(b);
                    intent.setClass(UniteHallDetail.this, Login.class);
                }
                startActivity(intent);
                break;

            case R.id.bet:
                if (mBetPayControl.canBet(mOrderMoney)) {
                    if (HttpConnectUtil.isNetworkAvailable(this)) {
                        UniteJoinSubmitOrder order = new UniteJoinSubmitOrder();
                        order.setProgramId(mOrderId);
                        order.setJoinNum(mBuyAmount);
                        order.setShare(isShowShareContent);
                        order.setShareContent(mEtShareContent.getText().toString());
                        order.setLatitude(mBetPayControl.getmLatitude());
                        order.setLongitude(mBetPayControl.getmLongitude());
                        order.setLocation(mBetPayControl.getmLocation());

                        mBetPayControl.submitJoinUnit(order, mLotteryName);
                    }
                    else {
                        ViewUtil.showTipsToast(this, noNetTips);
                    }
                }
                break;
        }
    }

    @Override
    protected void submitData() {
        UEDataAnalyse.onEvent(this, "open_bet_order_detail", "合买投注");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBetPayControl.betStatusView(mTvAccount, mBtBet, mOrderMoney);
    }
}
