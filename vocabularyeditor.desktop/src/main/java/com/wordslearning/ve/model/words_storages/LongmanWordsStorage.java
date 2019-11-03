package com.wordslearning.ve.model.words_storages;

import java.util.List;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;


public class LongmanWordsStorage extends AbstractRemoteWordsStorage {

	private WordsExtractor longmanExtractor=new WordsExtractor("articles/longman");
	
	@Override
	public List<WLArticle> getWords(String key) {
		return longmanExtractor.searchArticles(key);
	}

	@Override
	public Language getLangFrom() {
		return Language.ENG;
	}

	@Override
	public String getName() {
		return "Longman Dictionary";
	}

}
