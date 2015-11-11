package fr.upem.ijavabook.exmanager.parser;

import java.util.HashSet;
import java.util.Set;

class Img {//DEV
	private String altText;
	private String src;
	private final HashSet<String> labels = new HashSet<>();

	public final String altText() {
		return altText;
	}

	public final Img altText(final String summary) {
		this.altText = summary;
		return this;
	}

	public final String src() {
		return src;
	}

	public final Img setSrc(final String src) {
		this.src = src;
		return this;
	}

	public Set<String> labels() {
		return labels;
	}

	public Img label(final String label) {
		labels.add(label);
		return this;
	}

	@Override
	public String toString() {
		return String.format("TaskDto [altText=%s, src=%s, labels=%s]",
				altText, src, labels);
	}

}
