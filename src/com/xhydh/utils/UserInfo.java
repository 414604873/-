package com.xhydh.utils;

import android.R.integer;

public class UserInfo {
	private String phone;
	private String pswd;
	UserInfo(){
		
	}
	
	public UserInfo(String phone, String pswd) {
		super();
		this.phone = phone;
		this.pswd = pswd;
	}


	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPswd() {
		return pswd;
	}
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
	
}
