package com.wordslearning.wl.model.learnprocess;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.wordslearning.wl.model.WLWord;

public class LearnStatistic {

	private static final Logger LOG = Logger.getLogger(LearnStatistic.class
			.getName());
	private Set<Integer> ignoredWords = new HashSet<Integer>();
	private Map<Integer, WLWord> relevantWords = new HashMap<Integer, WLWord>();

	public void addWordForLearning(int articleId, int currentPoints) {
		WLWord word = new WLWord();
		word.setCurrentPoints(currentPoints);
		relevantWords.put(articleId, word);
	}

	public void addWordForRepeating(int articleId, Date learnDate,
			Date lastRepeatDate, int lastRepeatInterval, int currentPoints,
			double difficulty) {
		WLWord word = new WLWord();
		word.setLearnDate(learnDate);
		word.setLastRepeatDate(lastRepeatDate);
		word.setLastRepeatInterval(lastRepeatInterval);
		word.setDifficulty(difficulty);
		word.setCurrentPoints(currentPoints);
		relevantWords.put(articleId, word);
	}

	public void addToIgnore(int articleId) {
		ignoredWords.add(articleId);
	}

	public WLWord getWord(int id) {
		if (!ignoredWords.contains(id)) {
			// LOG.info("Relevant word hit - " + id);
			WLWord relevantWord = relevantWords.get(id);
			if (relevantWord != null) {
				return relevantWord;
			} else {
				return new WLWord();
			}
		} else {
			return null;
		}
	}

	public Set<Integer> getIgnoredWords() {
		return ignoredWords;
	}

	public void setIgnoredWords(Set<Integer> ignoredWords) {
		this.ignoredWords = ignoredWords;
	}

	public Map<Integer, WLWord> getRelevantWords() {
		return relevantWords;
	}

	public void setRelevantWords(Map<Integer, WLWord> relevantWords) {
		this.relevantWords = relevantWords;
	}

}
