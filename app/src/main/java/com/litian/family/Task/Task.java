package com.litian.family.Task;

/**
 * Created by TianLi on 2017/10/21.
 */

public abstract class Task<T> {
	T data;
	TaskStatus status;
	OnCompletedListener onCompletedListener;

	public Task(T data) {
		this.data = data;
	}

	void startTask() {
		if (onCompletedListener == null) throw new NullPointerException("onCompletedListener can not be NULL");
		setStatus(TaskStatus.working);
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public OnCompletedListener getOnCompletedListener() {
		return onCompletedListener;
	}

	public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
		this.onCompletedListener = onCompletedListener;
	}



	public interface OnCompletedListener {
		void onComplete(TaskStatus status);
	}
}
