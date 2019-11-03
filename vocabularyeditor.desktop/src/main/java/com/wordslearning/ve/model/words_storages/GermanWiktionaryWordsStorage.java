package com.wordslearning.ve.model.words_storages;

import java.util.Collections;
import java.util.List;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;


public class GermanWiktionaryWordsStorage extends AbstractRemoteWordsStorage {

	@Override
	public Language getLangFrom() {
		return Language.GER;
	}

	@Override
	public String getName() {
		return "German Wiktionary";
	}

	@Override
	public List<WLArticle> getWords(String key) {
//		String newkey = SearchReqPreprocessor.processReqKey(key, Language.GER);
//		return WiktionaryExtractor.searchWords(newkey, Language.GER);
		return Collections.emptyList();
	}
}
