package com.xhydh.utils;

public class MessageInfo {
	private String title;
	private String content;
	public MessageInfo() {
		
	}
	public MessageInfo(String title) {
		super();
		this.title = title;
	}
	public MessageInfo(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "MessageInfo [title=" + title + ", content=" + content + "]";
	}
	
}
