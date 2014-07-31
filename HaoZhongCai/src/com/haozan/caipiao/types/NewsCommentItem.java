package com.haozan.caipiao.types;

public class NewsCommentItem {
    private int comment_id;
    private String add_time;
    private String nickname;
    private String content;
    private int good;
    private int bad;
    private int replyId;
    private boolean isDing = false;
    private boolean isCai = false;

// private int replyId;

    public void setCommentId(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getComentId() {
        return comment_id;
    }

    public void setAddTime(String add_time) {
        this.add_time = add_time;
    }

    public String getAddTime() {
        return add_time;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setCommentGood(int good) {
        this.good = good;
    }

    public int getCommentGood() {
        return good;
    }

    public void setCommentBad(int bad) {
        this.bad = bad;
    }

    public int getCommentBad() {
        return bad;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }
    public boolean getDing() {
        return isDing;
    }

    public void setDing(boolean isDing) {
        this.isDing = isDing;
    }
    
    public boolean getCai() {
        return isCai;
    }

    public void setCai(boolean isCai) {
        this.isCai = isCai;
    }
}
