package com.haozan.caipiao.types;

/**
 * 充值第三方返回信息
 * 
 * @author peter_wang
 * @create-time 2013-11-1 下午3:02:51
 */
public class TopupReturnInf {
    // 订单号
    private String orderId;
    // 订单编码
    private String tradeCode;
    // 订单描述
    private String tradeDesc;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getTradeDesc() {
        return tradeDesc;
    }

    public void setTradeDesc(String tradeDesc) {
        this.tradeDesc = tradeDesc;
    }
}