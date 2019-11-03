package com.wordslearning.ve.model.words_storages;

import java.util.List;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;


public class DudenWordsStorage extends AbstractRemoteWordsStorage {
	private WordsExtractor dudenWordsExtractor = new WordsExtractor("articles/duden");

	@Override
	public List<WLArticle> getWords(String key) {
		return dudenWordsExtractor.searchArticles(key);
	}

	@Override
	public Language getLangFrom() {
		return Language.GER;
	}

	@Override
	public String getName() {
		return "Duden Dictionary";
	}

}
