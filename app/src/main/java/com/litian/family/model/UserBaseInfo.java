package com.litian.family.model;

import android.support.annotation.NonNull;

/**
 * This is the model class for friend list info
 */

public class UserBaseInfo {
	String name;
	String email;
	String uid;
	String icon;

	public UserBaseInfo() {}

	public UserBaseInfo(@NonNull User user) {
		name = user.getName();
		email = user.getEmail();
		uid = user.getUid();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
