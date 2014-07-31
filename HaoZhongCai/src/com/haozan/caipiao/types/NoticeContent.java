package com.haozan.caipiao.types;

import android.text.SpannableStringBuilder;

public class NoticeContent {
    private String id;
    private String title;
    private SpannableStringBuilder digest;
    private String urgent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SpannableStringBuilder getDigest() {
        return digest;
    }

    public void setDigest(SpannableStringBuilder digest) {
        this.digest = digest;
    }

    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    private String content;
    private String time;

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
}