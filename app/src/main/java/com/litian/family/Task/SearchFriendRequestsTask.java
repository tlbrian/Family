package com.litian.family.Task;

import com.litian.family.UserProfile;
import com.litian.family.firestore.MyFirestore;
import com.litian.family.model.Notification;
import com.litian.family.model.User;

import java.util.List;

/**
 * Created by TianLi on 2017/10/21.
 */

public class SearchFriendRequestsTask extends Task<User> {
	public SearchFriendRequestsTask(User data) {
		super(data);
	}

	@Override
	public void startTask() {
		MyFirestore.getInstance().searchFriendRequests(data, new MyFirestore.OnAccessDatabase<List<Notification>>() {
			@Override
			public void onComplete(List<Notification> data) {
				if (data != null) {
					UserProfile.getInstance().setNotifications(data);
					onCompletedListener.onComplete(TaskStatus.success);
				}
				else {
					onCompletedListener.onComplete(TaskStatus.failure);
				}
			}
		});
	}
}
