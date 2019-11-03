package com.wordslearning.wl.model.learnprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.wl.model.WLWord;

public class WordsInflatter {
	private LearnStatistic statisticResponse;
	private static final Logger LOG = Logger.getLogger(WordsInflatter.class
			.getName());

	public void setStatistic(LearnStatistic statisticResponse) {
		this.statisticResponse = statisticResponse;
	}

	public Map<Integer, WLWord> inflateWords(List<WLArticle> articles) {
		Map<Integer, WLWord> words = new HashMap<Integer, WLWord>();
		for (WLArticle wlArticle : articles) {
			WLWord word = statisticResponse.getWord(WLWord
					.generateId(wlArticle));
			if (word != null) {
				word.setWlArticle(wlArticle);
				words.put(word.getId(), word);
			}
		}
		return words;
	}

}
