package com.haozan.caipiao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

public class  TextViewLinkSetUrl {
    public static void extractMention2Link(TextView v) {
        v.setAutoLinkMask(0);
//        Pattern pattern = Pattern.compile("#(\\w+?)(?=\\W|$)");
//        Pattern pattern = Pattern.compile("介(\\w+?)(?=\\W|$)");
//        Pattern pattern = Pattern.compile("#(\\w+?)#");
        Pattern pattern = Pattern.compile("介(\\w+?)乙");
        String scheme = String.format("%s/?%s=", Defs.SCHEMA, Defs.PARAM_UID);
        Linkify.addLinks(v, pattern, scheme, null, new TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return match.group(1); // 要传到到此人页面的东西
            }
        });
        Pattern pattern1 = Pattern.compile("志(\\w+?)你");
//        Pattern pattern1 = Pattern.compile("#(\\w+?)#");
//        Pattern pattern1 = Pattern.compile("志(\\w+?)(?=\\W|$)");
        String scheme1 = String.format("%s/?%s=", Defs.SCHEMA1, Defs.PARAM_UID);
        Linkify.addLinks(v, pattern1, scheme1, null, new TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return match.group(1); // 要传到到此人页面的东西
            }
        });
    }

    public class Defs {
        public static final String SCHEMA = "buke://sina_profile";
        public static final String SCHEMA1 = "buke://sina_profile2";
        public static final String PARAM_UID = "uid";
    }
}
