package com.wordslearning.wl.ui;

import java.util.List;

public class KeyParts {
	private boolean phrase;
	private List<String> parts;

	public boolean isPhrase() {
		return phrase;
	}

	public void setPhrase(boolean phrase) {
		this.phrase = phrase;
	}

	public List<String> getParts() {
		return parts;
	}

	public void setParts(List<String> parts) {
		this.parts = parts;
	}

}
