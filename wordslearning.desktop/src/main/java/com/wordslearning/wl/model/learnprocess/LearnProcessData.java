package com.wordslearning.wl.model.learnprocess;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

public class LearnProcessData {

	private Document statistic;

	private Map<String, Set<Integer>> wordsBeingLearnt;
	
	private String lastLearntVoc;

	public Document getStatistic() {
		return statistic;
	}

	public void setStatistic(Document statistic) {
		this.statistic = statistic;
	}

	public Map<String, Set<Integer>> getWordsBeingLearnt() {
		return wordsBeingLearnt;
	}

	public void setWordsBeingLearnt(Map<String, Set<Integer>> wordsBeingLearnt) {
		this.wordsBeingLearnt = wordsBeingLearnt;
	}

	public String getLastLearntVoc() {
		return lastLearntVoc;
	}

	public void setLastLearntVoc(String lastLearntVoc) {
		this.lastLearntVoc = lastLearntVoc;
	}

}
