package com.haozan.caipiao.types;

public class upompInfReturn {
    private String application;
    private String merchantId;
    private String merchantOrderId;
    private String merchantOrderTime;
    private String merchantOrderAmt;
    private String cupsQid;
    private String cupsTraceNum;
    private String cupsTraceTime;
    private String cupsRespCode;
    private String respCode;
    private String respDesc;
    
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.merchantId = application;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getMerchantOrderTime() {
        return merchantOrderTime;
    }

    public void setMerchantOrderTime(String merchantOrderTime) {
        this.merchantOrderTime = merchantOrderTime;
    }

    public String getMerchantOrderAmt() {
        return merchantOrderAmt;
    }

    public void setMerchantOrderAmt(String merchantOrderAmt) {
        this.merchantOrderAmt = merchantOrderAmt;
    }

    public String getCupsQid() {
        return cupsQid;
    }

    public void setCupsQid(String cupsQid) {
        this.cupsQid = cupsQid;
    }
    
    public String getCupsTraceNum() {
        return cupsTraceNum;
    }

    public void setCupsTraceNum(String cupsTraceNum) {
        this.cupsTraceNum = cupsTraceNum;
    }
    
    public String getCupsTraceTime() {
        return cupsTraceTime;
    }

    public void setCupsTraceTime(String cupsTraceTime) {
        this.cupsTraceTime = cupsTraceTime;
    }
    
    public String getCupsRespCode() {
        return cupsRespCode;
    }

    public void setCupsRespCode(String cupsRespCode) {
        this.cupsRespCode = cupsRespCode;
    }
    
    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }
    
    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }
}