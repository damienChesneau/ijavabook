package fr.upem.ijavabook.exmanager.parser;

import java.util.ArrayList;
import java.util.List;

class MarkdownItem {//DEV
	private final ArrayList<Img> imgs = new ArrayList<>();

	public MarkdownItem add(final Img img) {
		imgs.add(img);
		return this;
	}

	public List<Img> tasks() {
		return (List<Img>) imgs.clone();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkdownItem [imgs=");
		builder.append(imgs);
		builder.append("]");
		return builder.toString();
	}
}
