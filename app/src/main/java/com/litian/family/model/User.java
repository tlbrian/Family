package com.litian.family.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TianLi on 2017/10/13.
 */

public class User {
	private String uid;
	private String email;
	private String name;
	private String FCMToken;
	private String iconPath;

	public User() {}

	public User(String email) {
		this.email = email;
	}

	public User(String uid, String email) {
		this.uid = uid;
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFCMToken() {
		return FCMToken;
	}

	public void setFCMToken(String FCMToken) {
		this.FCMToken = FCMToken;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

}
