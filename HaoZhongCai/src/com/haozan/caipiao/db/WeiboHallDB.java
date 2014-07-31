package com.haozan.caipiao.db;

public class WeiboHallDB {
    public static final String SINAWEIBO = "sina_weibo.db"; //databse name
    public static final String HOME_TABLE = "home_table"; //weibo table name
    
    /*
     * weibo table field
     *   *package*
     */
    static final String WEIBOID="weiboid"; //weiboid
    static final String USERID = "userid"; //user id
    static final String NAME = "name"; //昵称
    static final String CONTENT = "content"; //内容
    static final String PIC = "pic"; //内容图片
    static final String TIME = "time"; //发表时间
    static final String AVATAR = "avatar"; //头像
    static final String SOURCE = "source"; //来源
    static final String REPLYCOUNT = "replyCount"; //评论数
    static final String RETWEETCOUNT = "retweetCount"; //转发数
    static final String TITLE = "title"; //新闻标题
    static final String TYPE = "type"; //类型
    static final String PREVIEW = "preview"; //新闻内容
    static final String ATTACHID = "attachid"; //新闻id
    static final String WEIBOTYPE = "weibotype"; //微博缓存类别

}
