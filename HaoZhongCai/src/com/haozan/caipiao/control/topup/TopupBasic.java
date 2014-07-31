package com.haozan.caipiao.control.topup;

import android.content.Context;

/**
 * 充值控制类基类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午5:29:13
 */
public class TopupBasic {
    protected TopupUtil mTopupUtil;

    protected Context mContext;

    public TopupBasic(Context context) {
        this.mContext = context;

        mTopupUtil = new TopupUtil(context);
    }

    public void toFeedback() {
        mTopupUtil.toFeedback();
    }

    public void toAllTopupWay() {
        mTopupUtil.toAllTopupWay();
    }

    /**
     * 充值显示的金额信息
     * 
     * @param index 金额对应的位置
     * @return
     */
    public String createMoneyShow(String money) {
        return mTopupUtil.createMoneyShow(money);
    }

    public void showSuccessDialog(String inf) {
        mTopupUtil.showSuccessDialog(inf);
    }

    protected void showFailDialog(String error) {
        mTopupUtil.showFailDialog(error);
    }
}
