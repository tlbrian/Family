package com.litian.family.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TianLi on 2017/10/21.
 */

public class BatchTask<T> extends Task<T> {
	private List<Task<T>> tasks;

	public BatchTask(T data) {
		super(data);
		tasks = new ArrayList<>();
	}

	@Override
	public void startTask() {
		super.startTask();
		for (Task task : tasks) {
			task.startTask();
		}
	}

	public List<Task<T>> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task<T>> tasks) {
		this.tasks = tasks;
	}

	public void addTask(Task<T> task) {
		tasks.add(task);
	}
}
