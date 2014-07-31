package com.haozan.caipiao.util;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class StringUtil {
    // splite the string s with sp
    public static String[] spliteString(String s, String sp) {
        return s.split(sp);
    }

    // if the number less than 10,add 0 to the front
    public static String[] decorateString(String s[]) {
        try {
            int length = s.length;
            for (int i = 0; i < length; i++) {
                int number = Integer.valueOf(s[i]);
                if (number < 10) {
                    s[i] = "0" + number;
                }
            }

        }
        catch (Exception e) {
            return null;
        }
        return s;
    }

    public static String betDataTransite(int org, int org1) {
        if (org1 == 10) {
            return String.valueOf(org);
        }
        else if (org < 10)
            return "0" + String.valueOf(org);
        else
            return String.valueOf(org);

    }

    public static String deleteZeroPrefix(String orgNum) {
        Integer num;
        try {
            num = Integer.valueOf(orgNum);
        }
        catch (NumberFormatException e) {
            num = 0;
        }
        return num.toString();
    }

    public static String betDataTransite(int org) {
        if (org < 10)
            return "0" + String.valueOf(org);
        else
            return String.valueOf(org);
    }

    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDFLJY(String balls) {
        String[] ball = balls.split("\\|");
        String[] animals = ball[1].split(",");
        ball[1] = "";
        for (int i = 0; i < animals.length; i++) {
            try {
                animals[i] = LotteryUtils.animals[Integer.valueOf(animals[i]) - 1];
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ball[1] += animals[i] + ",";
        }
        return ball[0] + "|" + ball[1].subSequence(0, ball[1].length());
    }

    /**
     * 将一长串数字，每三个直接逗号隔开，如11,111,111
     * 
     * @param balls
     * @return
     */
    public static String divideLongNum(String num) {
        int length = num.length();
        if (length <= 3)
            return num;
        // 需要多少个逗号
        int divideFlag = length / 3;
        StringBuilder divideNum = new StringBuilder();
        // 将num前面逗号及之前的字符添加进去，比如12345，添加12以及逗号到divideNum中
        if (length - 3 * divideFlag != 0) {
            divideNum.append(num.substring(0, length - 3 * divideFlag) + ",");
        }
        for (int i = 0; i < divideFlag; i++) {
            divideNum.append(num.substring(length - 3 * (divideFlag - i), length - 3 * (divideFlag - i - 1)) +
                ",");
        }
        divideNum.delete(divideNum.length() - 1, divideNum.length());
        return divideNum.toString();
    }

    /**
     * 获取江西时时彩大小单双格式,如"大单,小双"
     * 
     * @param code
     * @return
     */
    public static String getSSCInf(String code) {
        if (code == null)
            return null;
        try {
            String num[] = code.split(",");
            if (num.length == 5) {
                int forth = Integer.valueOf(num[3]);
                int fifth = Integer.valueOf(num[4]);
                String ssc;
                if (forth > 4) {
                    ssc = "大";
                }
                else {
                    ssc = "小";
                }
                if (forth % 2 == 0) {
                    ssc += "双";
                }
                else {
                    ssc += "单";
                }
                if (fifth > 4) {
                    ssc += "|大";
                }
                else {
                    ssc += "|小";
                }
                if (fifth % 2 == 0) {
                    ssc += "双";
                }
                else {
                    ssc += "单";
                }
                return ssc;
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 是否包含汉字
     * 
     * @param str
     * @return
     */
    public static boolean containChinese(String str) {
        boolean isGB2312 = false;
        isGB2312 = str.matches(".*[\u4e00-\u9faf].*");
        return isGB2312;
    }
}
