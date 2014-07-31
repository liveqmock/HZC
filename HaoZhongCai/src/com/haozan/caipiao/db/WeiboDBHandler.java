package com.haozan.caipiao.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haozan.caipiao.types.WeiboData;

public class WeiboDBHandler {
    private Context mContext;
    private ArrayList<WeiboData> dataStatus;

    public WeiboDBHandler(Context context) {
        mContext = context;
    }

    /**
     * 获取数据库内所有微博Status
     * 
     * @return
     */
    public ArrayList<WeiboData> getStatus(String weiboTypes) {
        dataStatus = new ArrayList<WeiboData>();
        WeiboSQLHelper dbHelper = new WeiboSQLHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = "weibotype=?";// 查询条件
        String[] selectionArgs = new String[] {weiboTypes};// 查询条件参数
        Cursor cursor =
            database.query(WeiboHallDB.HOME_TABLE, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            WeiboData weiboData = new WeiboData();
            String weiboid = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.WEIBOID));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.CONTENT));
// String pic = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.PIC));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.TIME));
            String source = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.SOURCE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.TITLE));
            String userid = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.USERID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.TYPE));
            String preview = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.PREVIEW));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.AVATAR));
            String replyCount = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.REPLYCOUNT));
            String retweetCount = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.RETWEETCOUNT));
            String attachid = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.ATTACHID));
            String weibotype = cursor.getString(cursor.getColumnIndexOrThrow(WeiboHallDB.WEIBOTYPE));

            weiboData.setId(Integer.valueOf(weiboid));
            weiboData.setContent(content);
            weiboData.setTime(time);
            weiboData.setSource(source);
            weiboData.setTitle(title);
            weiboData.setUserid(userid);
            weiboData.setName(name);
            weiboData.setType(Integer.valueOf(type));
            weiboData.setPreview(preview);
            weiboData.setAvatar(avatar);
            weiboData.setReplyCount(replyCount);
            weiboData.setRetweetCount(Integer.valueOf(retweetCount));
            weiboData.setAttachid(attachid);
            weiboData.setAttachid(weibotype);

            // 添加到Status
            dataStatus.add(weiboData);
        }
        cursor.close();
        database.close();
        dbHelper.close();
        return this.dataStatus;
    }

    /**
     * 保存微博及其转发数&评论数
     * 
     * @param status
     * @param count
     */
    public void save(WeiboData status, String weibotype) {
        int weiboid = status.getId();
        String content = status.getContent();
        String avatar = status.getAvatar();
        String time = String.valueOf((status.getTime()));
        String source = status.getSource();
        String userid = status.getUserid();
        String name = status.getName();
        String preview = status.getPreview();
        String title = status.getTitle();
        int retweetcount = status.getRetweetCount();
        String replycount = status.getReplyCount();// 评论数
        int type = status.getType();
        String attachid = status.getAttachid();

// ////////////////////////////判断转发的暂时屏蔽
// if (status.getRetweetCount() == 0) {
// save(id, content, pic, create_at, source, uid, nick, portrait, redirectNum, commentNum);
// }
// else { // 若存在转发微博，一起存入数据库中
// long rtId = status.getRetweeted_status().getId();
// String rtContent = status.getRetweeted_status().getText();
// String rtPic = status.getRetweeted_status().getThumbnail_pic();
// long rtUid = status.getRetweeted_status().getUser().getId();
// String rtNick = status.getRetweeted_status().getUser().getScreenName();
// save(id, content, pic, create_at, source, uid, nick, portrait, verified, redirectNum, commentNum,
// rtId, rtContent, rtPic, rtUid, rtNick);
// }
        // ////////////////
        save(weiboid, content, time, source, userid, name, avatar, preview, retweetcount, replycount, title,
             type, attachid, weibotype);
    }

    /**
     * 保存微博
     */
    private void save(int weiboid, String content, String time, String source, String userid, String name,
                      String avatar, String preview, int retweetCount, String replyCount, String title,
                      int type, String attachid, String weibotype) {
        ContentValues values = new ContentValues();
        values.put(WeiboHallDB.WEIBOID, weiboid);
        values.put(WeiboHallDB.CONTENT, content);
// values.put(WeiboHallDB.PIC, pic);
        values.put(WeiboHallDB.TIME, time);
        values.put(WeiboHallDB.SOURCE, source);
        values.put(WeiboHallDB.USERID, userid);
        values.put(WeiboHallDB.NAME, name);
        values.put(WeiboHallDB.AVATAR, avatar);
        values.put(WeiboHallDB.PREVIEW, preview);
        values.put(WeiboHallDB.RETWEETCOUNT, retweetCount);
        values.put(WeiboHallDB.REPLYCOUNT, replyCount);
        values.put(WeiboHallDB.TITLE, title);
        values.put(WeiboHallDB.TYPE, type);
        values.put(WeiboHallDB.ATTACHID, attachid);
        values.put(WeiboHallDB.WEIBOTYPE, weibotype);

        WeiboSQLHelper dbHelper = new WeiboSQLHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.insertOrThrow(WeiboHallDB.HOME_TABLE, null, values);
        database.close();
        dbHelper.close();
    }

    /**
     * 清除指定表内所有数据
     * 
     * @param tableName
     */
    public void clearTable(String tableName, String weiboType) {
        WeiboSQLHelper sqlHelper = new WeiboSQLHelper(mContext);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        String[] type = {weiboType};
        database.delete(tableName, "weibotype=?", type);
        database.close();
    }

    void log(String msg) {
    }

}
