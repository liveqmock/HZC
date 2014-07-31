package com.haozan.caipiao.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Context;

public class EncryptUtil {

    // encrypt the string data with MD5
    public final static String MD5Encrypt(String s) {
        String result = "";
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        byte[] strTemp = s.getBytes();
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            result = new String(str);
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] rsaEncrypt(Context context, String code) {
        return RSA.rsaEncrypt(context, code);
    }

    public static String base64Encode(byte[] cardByte) {
        return encodeParameterBase64(cardByte);
    }

    public static String encryptString(Context context, String code) {
        return base64Encode(rsaEncrypt(context, code));
    }
    @SuppressLint("NewApi")
    public static String encodeParameterBase64(byte[] src) {
        try {
            return Base64.encodeToString(src, Base64.DEFAULT);
        }
        catch (Exception ex) {
            return "";
        }
    }

    /**
     * 加密登录、注册、修改密码上传服务器的密码
     * 
     * @param context
     * @param password
     * @return
     */
    public static String encryptPassword(Context context, String password) {
        return encodeParameterBase64(rsaEncrypt(context, EncryptUtil.MD5Encrypt(password))).replace("\n", "").trim();
    }
}
