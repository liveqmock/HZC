package com.haozan.caipiao.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class FocusListData
    implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 7650954350554754245L;
    private String id;
    private String name;
    private String avatar;
    private String Gender;
    private String signature;
    private boolean isfollows = true;//防止乱序
    private String follows;//是否关注该用户，1关注，0未关注
    private Bitmap bitmap;

    private static ByteBuffer dst;
    private static byte[] bytesar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isIsfollows() {
        return isfollows;
    }

    public void setIsfollows(boolean isfollows) {
        this.isfollows = isfollows;
    }

    public String getFollows() {
        return follows;
    }

    public void setFollows(String follows) {
        this.follows = follows;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {

        out.writeObject(id);
        out.writeObject(name);
        out.writeObject(avatar);
        out.writeObject(Gender);
        out.writeObject(signature);
        out.writeObject(isfollows);
        out.writeObject(follows);
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

        id = (String) in.readObject();
        name = (String) in.readObject();
        avatar = (String) in.readObject();
        Gender = (String) in.readObject();
        signature = (String) in.readObject();
        isfollows = (Boolean) in.readObject();
        follows = (String) in.readObject();

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
