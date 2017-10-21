package com.litian.family.Task;

import com.litian.family.UserProfile;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.Friend;
import com.litian.family.model.User;

import java.util.List;

/**
 * Created by TianLi on 2017/10/21.
 */

public class SearchFriendsTask extends Task<User> {
	public SearchFriendsTask(User data) {
		super(data);
	}

	@Override
	public void startTask() {
		super.startTask();
		MyFirestore.getInstance().searchFriends(data, new MyFirestore.OnAccessDatabase<List<Friend>>() {
			@Override
			public void onComplete(List<Friend> data) {
				if (data != null) {
					UserProfile.getInstance().setFriends(data);
					setStatus(TaskStatus.success);
				}
				else {
					setStatus(TaskStatus.failure);
				}
				onCompletedListener.onComplete(getStatus());
			}
		});
	}
}
