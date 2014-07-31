package com.haozan.caipiao.util.alipayfastlogin;

public class PartnerConfig {

    // 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
    public static final String PARTNER = "2088801741662991";
    // 商户收款的支付宝账号
    public static final String SELLER = "haozanalipay@163.com";
    // 商户（RSA）私钥
    public static final String RSA_PRIVATE =
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMeQ/37deHcmP1uClSnuv3yieJaV1/Ok9BC8NF1Xg+84c1WOTo/OHsrRWvIX3fnwCCl/CiFr9hI+Idsq/kjc6HwoJxeDPPkp0yPLd7W3x2UYvL1vVQqqMA2iMBS2OS6fBWwFzTA3mnaHMzsnyUukoK5TI9z3rSFdBS8sEYO96qOBAgMBAAECgYBeHJkAgPzgY58ZTpl9buPKqOvpR7zRX4bhpX+kTTMgyyRIwpV156RJzTTwWiUKSxHuT8S9QiaHL9nTe5DzV4oivTZTAyq4056ql6DzrrES8qefvl/HeK9GAgfCrj7VCx6EzI176WDyT9tddJL1p4mAjIKSyLF94oHLtyZacYNGAQJBAPH9Ob/PQDJUCnHeuimE+uZ1YnV23t49WEDQP7OgyznrkGvcsrLK1GUvsxig3rpTevaHxhO3U0S5Oq5AtqJO37ECQQDTHvr1IUCTgZDOvfsk/ZFpRjm+csGNgNeYfhfKAjQZLlWjCDFjo1qgM9P/+sy/XliaU6JNb5ieYzIgh6XDi0TRAkAUlcz+0xuL7HhC/YZql8RcLGomwejtkNxcDxsMJcaD89UCR/Dvoq90uB0NIEoyIX3ZUO15qeOsefFS93BR2FHRAkA1fcr+Iu+2sARdrZsdczXNo6Jr062gyb1WgNRMNaS3oJrIFQJWiYQjxR345LXN4vy2FWyxDA5ySxXfUh843WeRAkEAwJKa3tlwo2q56sKpoIAgJG1kOVcLl2sKB3RrwDzXtkoMFiTiTNkO0M/7HwicYCGliPCKgQ98GC5+5arU9x8cLQ==";
    // 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
    public static final String RSA_ALIPAY_PUBLIC =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDG2ajRGFuItBF8DCyDqTw3TjH8ObLew2RBcgJ PrIk0uq3b3YZ59iUN/L1ZGfaBUOVTjJLh6aQ0WaKp+9j1hP1Dha9vjWb/QC7BHCR4tIu50JGL+t2 LFtnbGziGQ+PmvurDDs5ku3+Mf8ChQdziWkPwg/ySqm/B4QEComfzlgxhQIDAQAB";
    // 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
    public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";

}
