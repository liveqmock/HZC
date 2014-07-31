package com.haozan.caipiao.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class SearchPeopleDate
    implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6657980410194182904L;
    private String userid;
    private String name;
    private String city;
    private String qianming;
    private String gender;
    private String avatar;
    private String retweetCount;
    private String replyCount;
    private String weiboCount;
    private Bitmap bitmap;

    private static ByteBuffer dst;
    private static byte[] bytesar;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String string) {
        this.userid = string;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getQianming() {
        return qianming;
    }

    public void setQianming(String qianming) {
        this.qianming = qianming;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {

        out.writeObject(userid);
        out.writeObject(name);
        out.writeObject(city);
        out.writeObject(qianming);
        out.writeObject(gender);
        out.writeObject(avatar);
        out.writeObject(retweetCount);
        out.writeObject(replyCount);
        out.writeObject(weiboCount);
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
        name = (String) in.readObject();
        city = (String) in.readObject();
        qianming = (String) in.readObject();
        gender = (String) in.readObject();
        avatar = (String) in.readObject();
        retweetCount = (String) in.readObject();
        replyCount = (String) in.readObject();
        weiboCount = (String) in.readObject();
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
