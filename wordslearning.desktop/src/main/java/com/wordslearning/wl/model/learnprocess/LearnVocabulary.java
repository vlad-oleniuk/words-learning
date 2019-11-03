package com.wordslearning.wl.model.learnprocess;

import java.util.Map;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.wl.model.WLWord;

public class LearnVocabulary {
	private String name;
	
	private Map<Integer, WLWord> words;
	
	private Language language;

	public Map<Integer, WLWord> getWords() {
		return words;
	}

	public void setWords(Map<Integer, WLWord> words) {
		this.words = words;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public int getVocSize(){
		return words.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
