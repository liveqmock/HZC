package com.haozan.caipiao.control;

/**
 * handler传递消息值
 * 
 * @author peter_wang
 * @create-time 2013-10-12 下午1:40:43
 */
public class ControlMessage {
    public static final int FINISH_ACTIVITY = 200;
    public static final int FINISH_CHECK_CONNECTION = FINISH_ACTIVITY + 1;
    public static final int PLUGIN_INF = FINISH_CHECK_CONNECTION + 1;
    public static final int GAME_INF = PLUGIN_INF + 1;
    public static final int HALL_NOTICE = GAME_INF + 1;
    public static final int HAS_BANNER_INF = HALL_NOTICE + 1;
    public static final int DOWNLOAD_FLASH_PIC = HAS_BANNER_INF + 1;
    public static final int GET_ALL_LOTTERY_INFO = DOWNLOAD_FLASH_PIC + 1;

    public static final int DOWNLOAD_START = GET_ALL_LOTTERY_INFO + 1;
    public static final int DOWNLOAD_ING = DOWNLOAD_START + 1;
    public static final int DOWNLOAD_FINISH = DOWNLOAD_ING + 1;

    public static final int SHOW_PROGRESS = DOWNLOAD_FINISH + 1;
    public static final int DISMISS_PROGRESS = SHOW_PROGRESS + 1;//

    public static final int SHOW_PROGRESS_DIALOG = DISMISS_PROGRESS + 1;
    public static final int DISMISS_PROGRESS_DIALOG = SHOW_PROGRESS_DIALOG + 1;

    public static final int REVEICE_LOTTERY_INF = DISMISS_PROGRESS_DIALOG + 1;

    public static final int HALL_GUESS_COTENT = REVEICE_LOTTERY_INF + 1;

    public static final int LOGIN_OK_RESULT = REVEICE_LOTTERY_INF + 1;
    public static final int LOGIN_FAIL_RESULT = LOGIN_OK_RESULT + 1;

    public static final int DELAY_INIT = LOGIN_FAIL_RESULT + 1;

    public static final int USER_INF_SUCCESS_RESULT = DELAY_INIT + 1;
    public static final int USER_INF_FAIL_RESULT = USER_INF_SUCCESS_RESULT + 1;

    public static final int UPDATE_INFO_SUCCESS_RESULT = USER_INF_FAIL_RESULT + 1;
    public static final int UPDATE_INFO_FAIL_RESULT = UPDATE_INFO_SUCCESS_RESULT + 1;

    // 充值部分
    public static final int TOPUP_FAIL = 300;
    public static final int TENCENT_TOPUP_SUCCESS = TOPUP_FAIL + 1;
    public static final int ALIPAY_SECURITY_TOPUP_SUCCESS = TENCENT_TOPUP_SUCCESS + 1;
    public static final int UNIONPAY_PLUGIN_TOPUP_SUCCESS = ALIPAY_SECURITY_TOPUP_SUCCESS + 1;
    public static final int PHONECARD_TOPUP_SUCCESS = UNIONPAY_PLUGIN_TOPUP_SUCCESS + 1;
    public static final int UNIONPAY_DEBITCARD_TOPUP_SUCCESS = PHONECARD_TOPUP_SUCCESS + 1;
    public static final int UNIONPAY_VOICE_TOPUP_SUCCESS = UNIONPAY_DEBITCARD_TOPUP_SUCCESS + 1;
    public static final int GET_BANK_LIST_VERSION_FAIL = UNIONPAY_VOICE_TOPUP_SUCCESS + 1;
    public static final int GET_BANK_LIST_VERSION_SUCCESS = GET_BANK_LIST_VERSION_FAIL + 1;
    public static final int GET_BANK_LIST_FAIL = GET_BANK_LIST_VERSION_SUCCESS + 1;
    public static final int GET_BANK_LIST_SUCCESS = GET_BANK_LIST_FAIL + 1;
    public static final int SAVE_BANK_LIST = GET_BANK_LIST_SUCCESS + 1;

    // 投注部分
    public static final int UNITE_JOIN_SUCCESS_RESULT = 400;
    public static final int UNITE_JOIN_FAIL_RESULT = UNITE_JOIN_SUCCESS_RESULT + 1;

    // 订单部分
    public static final int UNITE_ORDER_DETAIL_SUCCESS_RESULT = 500;
    public static final int UNITE_ORDER_DETAIL_FAIL_RESULT = UNITE_ORDER_DETAIL_SUCCESS_RESULT + 1;
    public static final int SPORT_ORDER_DETAIL_SUCCESS_RESULT = UNITE_ORDER_DETAIL_FAIL_RESULT + 1;
    public static final int SPORT_ORDER_DETAIL_FAIL_RESULT = SPORT_ORDER_DETAIL_SUCCESS_RESULT + 1;
}
