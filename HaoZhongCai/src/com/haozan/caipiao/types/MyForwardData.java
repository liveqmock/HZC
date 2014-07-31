package com.haozan.caipiao.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class MyForwardData
    implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -963895573191445950L;
    private String weiboid;
    private String userid;
    private String name;
    private String phone;
    private String content;
    private String time;
    private String avatar;
    private String attachid;
    private String title;
    private String preview;
    private int type;
    private String source;
    private Bitmap bitmap;
    private static ByteBuffer dst;
    private static byte[] bytesar;

    public String getId() {
        return weiboid;
    }

    public void setId(String id) {
        this.weiboid = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

        out.writeObject(weiboid);

        out.writeObject(userid);
        out.writeObject(name);
        out.writeObject(phone);
        out.writeObject(content);
        out.writeObject(time);
        out.writeObject(avatar);
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
        weiboid = (String) in.readObject();
        userid = (String) in.readObject();
        name = (String) in.readObject();
        phone = (String) in.readObject();
        content = (String) in.readObject();
        time = (String) in.readObject();
        avatar = (String) in.readObject();
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
