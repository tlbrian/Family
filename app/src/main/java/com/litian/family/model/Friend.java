package com.litian.family.model;

import com.litian.family.UserProfile;

/**
 * Created by TianLi on 2017/10/20.
 */

public class Friend {
	String friendOf;
	String uid;
	String name;
	String iconPath;

	public Friend() {}

	public Friend(User fromUser, User toUser) {
		friendOf = fromUser.getUid();
		uid = toUser.getUid();
		name = toUser.getName();
		iconPath = toUser.getIconPath();
	}

	public String getFriendOf() {
		return friendOf;
	}

	public void setFriendOf(String friendOf) {
		this.friendOf = friendOf;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
}
