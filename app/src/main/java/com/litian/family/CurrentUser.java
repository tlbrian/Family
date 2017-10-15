package com.litian.family;

import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/14.
 */

public class CurrentUser {

	private static User currentUser;

	private CurrentUser(){}

	public static User getCurrentUser() {
		return currentUser;
	}
}
