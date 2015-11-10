package fr.upem.ijavabook.exmanager.parser;

import java.util.ArrayList;
import java.util.List;

public class TaskList {//DEV
	private final ArrayList<Task> tasks = new ArrayList<>();

	public TaskList add(final Task task) {
		tasks.add(task);
		return this;
	}

	public List<Task> tasks() {
		return tasks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaskList [tasks=");
		builder.append(tasks);
		builder.append("]");
		return builder.toString();
	}
}
