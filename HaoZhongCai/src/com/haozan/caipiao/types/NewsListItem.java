package com.haozan.caipiao.types;

public class NewsListItem {
    private String title;
    private String date;
    private String comment;
    private int newsId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }
    
    public String getNewsComment() {
        return comment;
    }

    public void setNewsComment(String comment) {
        this.comment = comment;
    }
}
