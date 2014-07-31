package com.haozan.caipiao.util;

import java.util.Arrays;
import java.util.Random;

public class MathUtil {

    // permutation with count(up) and max(down)
    public static long factorial(int max, int count) {
        long sum = 1;
        if (count > 1) {
            int num = max;
            sum = num;
            for (int i = 0; i < count - 1; i++) {
                sum *= (num - 1);
                num--;
            }
        }
        else if (count == 1) {
            return max;
        }
        else {
            return 0;
        }
        return sum;
    }

    /**
     * 组合算法，Cmn，up是m,down是n
     * 
     * @param down 代表n
     * @param up 代表m
     * @return 返回组合组合数
     */
    public static long combination(int down, int up) {
        if (down < 1 || up < 1)
            return 0;
        return factorial(down, up) / factorial(up, up);
    }

    //
    /**
     * 随机生成一组int型数据，个数为count,大小范围是0 ~ chooseLength(不包括chooseLength)，且值均不相等
     * 
     * @author Vimcent 2013-4-8
     * @param count
     * @param chooseLength
     * @return
     */

    public static int[] getRandomNumNotEquals(int count, int chooseLength) {
        int[] num = new int[count];
        Random rd = new Random();
        Boolean flag = true;
        for (int i = 0; i < count; i++) {
            while (flag) {
                flag = false;
                int newNum = rd.nextInt(chooseLength);
                for (int j = 0; j < i; j++) {
                    if (num[j] == newNum) {
                        flag = true;
                        break;
                    }
                }
                num[i] = newNum;
            }
            flag = true;
        }
        return num;
    }

    /**
     * 获取count个长度不超过chooseLength的数字
     * 
     * @param count
     * @param chooseLength
     * @return
     */
    public static int[] getRandomNum(int count, int chooseLength) {
        int[] num = new int[count];
        Random rd = new Random();
        for (int i = 0; i < count; i++) {
            int newNum = rd.nextInt(chooseLength);
            num[i] = newNum;
        }
        return num;
    }

    /**
     * 获取数组最大值
     * 
     * @param wholeNum 原始数组
     * @return 返回最大值
     */
    public static int getMax(int[] wholeNum) {
        int[] temp = new int[wholeNum.length];
        for (int i = 0; i < wholeNum.length; i++) {
            temp[i] = wholeNum[i];
        }
        Arrays.sort(temp);
        return temp[temp.length - 1];
    }

    /**
     * 获取数组最小值
     * 
     * @param wholeNum 原始数组
     * @return 返回最小值
     */
    public static int getMin(int[] wholeNum) {
        int[] temp = new int[wholeNum.length];
        for (int i = 0; i < wholeNum.length; i++) {
            temp[i] = wholeNum[i];
        }
        Arrays.sort(temp);
        return temp[0];
    }
}
