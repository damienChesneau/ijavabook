package fr.upem.ijavabook.exmanager.parser;

import java.util.HashSet;
import java.util.Set;

public class Task {//DEV
	private String summary;
	private String assignee;
	private final HashSet<String> labels = new HashSet<>();

	public final String summary() {
		return summary;
	}

	public final Task summary(final String summary) {
		this.summary = summary;
		return this;
	}

	public final String assignee() {
		return assignee;
	}

	public final Task assignee(final String assignee) {
		this.assignee = assignee;
		return this;
	}

	public Set<String> labels() {
		return labels;
	}

	public Task label(final String label) {
		labels.add(label);
		return this;
	}

	@Override
	public String toString() {
		return String.format("TaskDto [summary=%s, assignee=%s, labels=%s]",
				summary, assignee, labels);
	}

}
