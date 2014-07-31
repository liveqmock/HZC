package com.haozan.caipiao.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class MyConmentData
    implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = -7322730474690198457L;
    private String userId;
    private String weiboId;
    private String originWeiboId;
    private String userName;
    private String content;
    private String srcContent;
    private String time;
    private String avatar;
    private String source;
    private Bitmap bitmap;

    private static ByteBuffer dst;
    private static byte[] bytesar;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getOriginWeiboId() {
        return originWeiboId;
    }

    public void setOriginWeiboId(String originWeiboId) {
        this.originWeiboId = originWeiboId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSrcContent() {
        return srcContent;
    }

    public void setSrcContent(String srcContent) {
        this.srcContent = srcContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
        out.writeObject(userId);
        out.writeObject(weiboId);
        out.writeObject(userName);
        out.writeObject(content);
        out.writeObject(srcContent);
        out.writeObject(time);
        out.writeObject(avatar);
        out.writeObject(source);
        out.writeObject(bitmap);

        out.writeObject(source);
        // out.writeInt(page);
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
        userId = (String) in.readObject();
        weiboId = (String) in.readObject();
        userName = (String) in.readObject();
        content = (String) in.readObject();
        srcContent = (String) in.readObject();
        time = (String) in.readObject();
        avatar = (String) in.readObject();
        source = (String) in.readObject();
        // page = in.readInt();
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
