package com.haozan.caipiao.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class SearchWeiboDate
    implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4514273309337520135L;
    private String userid;
    private String weiboid;
    private String name;
    private String time;
    private String content;
    private String avatar;
    private String retweetCount;
    private String replyCount;
    private String weiboCount;
    private String attachid;
    private String title;
    private String preview;
    private int type;
    private String source;
// private int page;
    private Bitmap bitmap;

    private static ByteBuffer dst;
    private static byte[] bytesar;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWeiboid() {
        return weiboid;
    }

    public void setWeiboid(String weiboid) {
        this.weiboid = weiboid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(String retweetCount) {
        this.retweetCount = retweetCount;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public String getWeiboCount() {
        return weiboCount;
    }

    public void setWeiboCount(String weiboCount) {
        this.weiboCount = weiboCount;
    }

    public String getAttachid() {
        return attachid;
    }

    public void setAttachid(String attachid) {
        this.attachid = attachid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {

        out.writeObject(userid);
        out.writeObject(weiboid);
        out.writeObject(name);
        out.writeObject(content);
        out.writeObject(time);
        out.writeObject(avatar);
        out.writeObject(retweetCount);
        out.writeObject(replyCount);
        out.writeObject(attachid);
        out.writeObject(title);
        out.writeObject(preview);
        out.writeInt(type);
        out.writeObject(source);
        if (bitmap != null) {
            out.writeInt(bitmap.getRowBytes());
            out.writeInt(bitmap.getHeight());
            out.writeInt(bitmap.getWidth());

            int bmSize = bitmap.getRowBytes() * bitmap.getHeight();
            if (dst == null || bmSize > dst.capacity())
                dst = ByteBuffer.allocate(bmSize);
            out.writeInt(dst.capacity());

            dst.position(0);

            bitmap.copyPixelsToBuffer(dst);
            if (bytesar == null || bmSize > bytesar.length)
                bytesar = new byte[bmSize];

            dst.position(0);
            dst.get(bytesar);
            out.write(bytesar, 0, bytesar.length);

        }
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        userid = (String) in.readObject();
        weiboid = (String) in.readObject();
        name = (String) in.readObject();
        content = (String) in.readObject();
        time = (String) in.readObject();
        avatar = (String) in.readObject();
        retweetCount = (String) in.readObject();
        replyCount = (String) in.readObject();
        attachid = (String) in.readObject();
        title = (String) in.readObject();
        preview = (String) in.readObject();
        type = in.readInt();
        source = (String) in.readObject();
        if (bitmap != null) {
            int nbRowBytes = in.readInt();
            int height = in.readInt();
            int width = in.readInt();

            int bmSize = in.readInt();

            if (bytesar == null || bmSize > bytesar.length)
                bytesar = new byte[bmSize];

            int offset = 0;

            while (in.available() > 0) {
                offset = offset + in.read(bytesar, offset, in.available());
            }

            if (dst == null || bmSize > dst.capacity())
                dst = ByteBuffer.allocate(bmSize);
            dst.position(0);
            dst.put(bytesar);
            dst.position(0);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(dst);
            // in.close();
        }
    }
}
