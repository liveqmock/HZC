package com.haozan.caipiao.control.order;

import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.order.UniteOrderDetailRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;

/**
 * 订单control
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午4:41:37
 */
public class OrderControl {

    private Context mContext;

    private Handler mHandler;

    public OrderControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    /**
     * 获取合买订单详情
     * 
     * @param orderid 订单id
     */
    public void setUniteOrderDetail(String orderid) {
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                              new UniteOrderDetailRequest(
                                                                                                          mContext,
                                                                                                          mHandler,
                                                                                                          orderid)));
    }
}
