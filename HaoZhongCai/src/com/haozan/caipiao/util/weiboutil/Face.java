package com.haozan.caipiao.util.weiboutil;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.R;

public class Face {
// public static String[] faceNames=new String[]{"织","围观","威武","奥特曼","爱心传递","围脖","温暖帽子","手套"
// ,"雪","雪人","落叶","照相机","浮云","帅","礼物","呵呵","嘻嘻","哈哈","爱你","晕","泪","馋嘴","抓狂","哼","可爱"
// ,"怒","汗","害羞","睡觉","钱","偷笑","酷","衰","吃惊","闭嘴","鄙视","挖鼻屎","花心","鼓掌","失望",
// "思考","生病","亲亲","怒骂","太开心","懒得理你","左哼哼","右哼哼","嘘","委屈","吐","可怜","打哈气",
// "握手","耶","good","弱","不要","ok","赞","来","蛋糕","心","伤心","猪头","咖啡","话筒","干杯","绿丝带",
// "蜡烛","钟","微风","月亮","做鬼脸","给力","神马","互粉","萌","熊猫","鸭梨","兔子"};
    public static String[] faceNames = new String[] {"怒", "衰", "晕", "哼", "哈哈", "拜拜", "心", "打哈气", "猪头", "泪",
            "呵呵", "汗", "怒骂", "酷", "悲伤", "吃惊", "馋嘴", "鼓掌", "思考", "口罩", "鄙视", "花心", "害羞", "睡觉", "挖鼻屎", "可怜",
            "嘻嘻", "抱抱", "钱", "偷笑", "生病", "折磨", "失望", "吐", "嘘", "可爱"};
    public static int faceId[] = new int[] {R.drawable.face1, R.drawable.face2, R.drawable.face3,
            R.drawable.face4, R.drawable.face5, R.drawable.face6, R.drawable.face7, R.drawable.face8,
            R.drawable.face9, R.drawable.face10, R.drawable.face11, R.drawable.face12, R.drawable.face13,
            R.drawable.face14, R.drawable.face15, R.drawable.face16, R.drawable.face17, R.drawable.face18,
            R.drawable.face19, R.drawable.face20, R.drawable.face21, R.drawable.face22, R.drawable.face23,
            R.drawable.face24, R.drawable.face25, R.drawable.face26, R.drawable.face27, R.drawable.face28,
            R.drawable.face29, R.drawable.face30, R.drawable.face31, R.drawable.face32, R.drawable.face33,
            R.drawable.face34, R.drawable.face35, R.drawable.face36

    };
    private static HashMap<String, Integer> faces;

    public static HashMap<String, Integer> getfaces(Context context) {
        if (faces != null) {
            return faces;
        }
        faces = new HashMap<String, Integer>();
        String faceName = "";
        for (int i = 1; i <= faceNames.length; i++) {
            faceName = "face" + i;
            try {
//                int id = R.drawable.class.getDeclaredField(faceName).getInt(context);
// int id = faceId.getClass().getDeclaredField(faceName).getInt(context);
//                faces.put(faceNames[i - 1], id);
                faces.put(faceNames[i - 1], faceId[i - 1]);
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
//            catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
        }

        faces.put("给力", R.drawable.geili_thumb);
        faces.put("神马", R.drawable.horse2_thumb);
        faces.put("互粉", R.drawable.hufen_thumb);
        faces.put("萌", R.drawable.kawayi_thumb);
        faces.put("熊猫", R.drawable.panda_thumb);
        faces.put("鸭梨", R.drawable.pear_thumb);
        faces.put("兔子", R.drawable.rabbit_thumb);

        return faces;
    }
}
