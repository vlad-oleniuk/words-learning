package com.wordslearning.wl.ui;

public class ExampleWord {

	private String word;
	private boolean requestedWord;

	public ExampleWord(String word, boolean isRequestedWord) {
		this.word = word;
		this.requestedWord = isRequestedWord;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public boolean isRequestedWord() {
		return requestedWord;
	}

	public void setRequestedWord(boolean requestedWord) {
		this.requestedWord = requestedWord;
	}

}
