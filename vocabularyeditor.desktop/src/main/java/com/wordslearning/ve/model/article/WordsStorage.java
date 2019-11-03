package com.wordslearning.ve.model.article;

import java.io.IOException;
import java.util.List;

public abstract class WordsStorage {

	public abstract List<WLArticle> getWords(String key) throws IOException;

	public abstract Language getLangFrom();

	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}
}
