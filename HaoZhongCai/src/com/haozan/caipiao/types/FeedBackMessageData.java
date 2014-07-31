package com.haozan.caipiao.types;

public class FeedBackMessageData {
    //the id of the message
    private String name;
    //title of the message
    private String content;
    //the time create the message
    private String time;
    //whether the message is read
    private String huifu;
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getHuifu() {
		return huifu;
	}
	public void setHuifu(String huifu) {
		this.huifu = huifu;
	}
}
