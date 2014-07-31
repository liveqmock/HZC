package com.haozan.caipiao.util.weiboutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 类说明： 字符串操作类
 * 
 * @author @Cundong
 * @weibo http://weibo.com/lovexiaofu
 * @date Feb 23, 2012 2:50:48 PM
 * @version 1.0
 */
public class StringUtils {
    /**
     * 判断给定字符串是否空白串。<br>
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
     * 若输入字符串为null或空字符串，返回true
     * 
     * @param input
     * @return boolean
     */
    public static boolean isBlank(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 半角转换为全角,解决TextView排版自动换行问题
     * 
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
    
    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     * 解决TextView排版自动换行问题
     * 
     * @param str
     * @return
     */
    public static String StringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


}