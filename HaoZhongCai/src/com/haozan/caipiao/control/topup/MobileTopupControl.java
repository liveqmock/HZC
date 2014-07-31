package com.haozan.caipiao.control.topup;

import android.content.Context;

import com.haozan.caipiao.util.LotteryUtils;

/**
 * 手机移动充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午10:10:54
 */
public class MobileTopupControl
    extends TopupBasic {

    public MobileTopupControl(Context context) {
        super(context);
    }

    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#mobile");
    }
}
