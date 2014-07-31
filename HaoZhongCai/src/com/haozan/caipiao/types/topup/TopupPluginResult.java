package com.haozan.caipiao.types.topup;

/**
 * 第三方充值方式插件支付结果
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午3:16:57
 */
public class TopupPluginResult {
    // 订单号
    private String orderNo;
    // 充值插件类型，ALI = 支付宝 UPOP = 银联快捷 UPMP = 新银联
    private String tradeType;
    // 银联的交易号
    private String tradeId;
    // 银联的流水号
    private String tradeNo;
    // 交易状态
    private String tradeCode;
    // 交易描述
    private String tradeDesc;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
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
