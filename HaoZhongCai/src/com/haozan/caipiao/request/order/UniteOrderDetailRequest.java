package com.haozan.caipiao.request.order;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.types.order.UniteOrderDetail;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 合买订单详情接口
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午3:44:39
 */
public class UniteOrderDetailRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    private String mOrderId;

    public UniteOrderDetailRequest(Context context, Handler handler, String orderId) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;

        this.mOrderId = orderId;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2003100");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("program_id", mOrderId);
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getGateways(false, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    private UniteOrderDetail reslutAnalyseResult(String data) {
        String programId = RequestResultAnalyse.getData(data, "program_id");// 方案编号
        String term = RequestResultAnalyse.getData(data, "term");// 期次
        String userId = RequestResultAnalyse.getData(data, "user_id");// 发起人ID
        String nickname = RequestResultAnalyse.getData(data, "nickname");// 昵称
        String price = RequestResultAnalyse.getData(data, "per_amount");// 每份金额
        int allAmount = RequestResultAnalyse.getInt(data, "all_num");// 总份数
        double perMoney = RequestResultAnalyse.getDouble(data, "per_amount");// 每份金额
        int boughtAmount = RequestResultAnalyse.getInt(data, "bought_num");// 已购份数
        int buySelfAmount = RequestResultAnalyse.getInt(data, "buyself_num");// 发起者自购份数
        int rate = RequestResultAnalyse.getInt(data, "rate");// 进度
        int insurance = (int) (RequestResultAnalyse.getInt(data, "paul_num") * 100 / allAmount);// 保底比例
        String title = HttpConnectUtil.decodeParameter(RequestResultAnalyse.getData(data, "title"));// 标题
        String describe = HttpConnectUtil.decodeParameter(RequestResultAnalyse.getData(data, "content"));// 方案描述
        // 方案状态 0：未满员；1：已满员下单；2：已撤单(发起者主动撤单)；3：合买失败(未达到发起条件)
        String programStatus = RequestResultAnalyse.getData(data, "pro_status");
        // 订单状态 0：未中奖；1：中奖；2：未开奖
        String winStatus = RequestResultAnalyse.getData(data, "win_status");
        Double winMoney = -1d;
        if (winStatus.equals("1")) {
            winMoney = RequestResultAnalyse.getDouble(data, "win_money");// 中奖金额
        }
        String secrecy = RequestResultAnalyse.getData(data, "is_open");// 保密级别(1：完全公开;2：跟单可见；3：开奖后公开)
        String codes = RequestResultAnalyse.getData(data, "codes");// 投注号码
        int commission = RequestResultAnalyse.getInt(data, "reward");// 佣金比例
        String uniteTime = RequestResultAnalyse.getData(data, "create_time");
        String lotteryId = RequestResultAnalyse.getData(data, "cat_id");// 彩种id

        UniteOrderDetail order = new UniteOrderDetail();
        order.setProgramId(programId);
        order.setTerm(term);
        order.setUserId(userId);
        order.setNickname(nickname);
        order.setPrice(price);
        order.setAllAmount(allAmount);
        order.setPerMoney(perMoney);
        order.setBoughtAmount(boughtAmount);
        order.setBuySelfAmount(buySelfAmount);
        order.setRate(rate);
        order.setInsurance(insurance);
        order.setTitle(title);
        order.setDescribe(describe);
        order.setProgramStatus(programStatus);
        order.setWinStatus(winStatus);
        order.setWinMoney(winMoney);
        order.setSecrecy(secrecy);
        order.setCodes(codes);
        order.setCommission(commission);
        order.setUniteTime(uniteTime);
        order.setLotteryId(lotteryId);

        return order;
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        String response = RequestResultAnalyse.getData(rspContent, "response_data");
        Message message =
            Message.obtain(mHandler, ControlMessage.UNITE_ORDER_DETAIL_SUCCESS_RESULT,
                           reslutAnalyseResult(response));
        message.sendToTarget();
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.UNITE_ORDER_DETAIL_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}