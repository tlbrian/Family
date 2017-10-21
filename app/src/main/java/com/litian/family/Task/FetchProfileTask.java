package com.litian.family.Task;

import com.litian.family.model.User;

/**
 * Created by TianLi on 2017/10/21.
 */

public class FetchProfileTask extends BatchTask<User> {
	public FetchProfileTask(User user) {
		super(user);
	}

	public void addTasks() {
		SearchFriendsTask searchFriendsTask = new SearchFriendsTask(data);
		searchFriendsTask.setOnCompletedListener(new OnCompletedListener() {
			@Override
			public void onComplete(TaskStatus status) {
				checkTasks();
			}
		});
		addTask(searchFriendsTask);

		SearchFriendRequestsTask searchFriendRequestsTask = new SearchFriendRequestsTask(data);
		searchFriendRequestsTask.setOnCompletedListener(new OnCompletedListener() {
			@Override
			public void onComplete(TaskStatus status) {
				checkTasks();
			}
		});
		addTask(searchFriendRequestsTask);
	}

	private void checkTasks() {
		if (getStatus() == TaskStatus.failure || getStatus() == TaskStatus.success) return;
		for (Task task : getTasks()) {
			if (task.getStatus() == TaskStatus.working || task.getStatus() == TaskStatus.none) {
				return;
			}
			else if (task.getStatus() == TaskStatus.failure) {
				setStatus(TaskStatus.failure);
				onCompletedListener.onComplete(getStatus());
				return;
			}
		}
		setStatus(TaskStatus.success);
		onCompletedListener.onComplete(getStatus());
	}
}

