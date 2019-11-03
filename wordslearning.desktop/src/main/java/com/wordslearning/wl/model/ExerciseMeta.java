package com.wordslearning.wl.model;

public enum ExerciseMeta {
	ARTICLE_EX, EXAMPLE_EX, WORD_CONSTRUCTOR_EX, PICTURE_SPELLING_EX, CARD_EX, SELECT_SYNONYMS, SPELLING_EX, SELECT_EX;

	public String getId() {
		return super.toString().toLowerCase();
	}

	public static ExerciseMeta getById(String string) {
		return valueOf(string.toUpperCase());
	}

}
