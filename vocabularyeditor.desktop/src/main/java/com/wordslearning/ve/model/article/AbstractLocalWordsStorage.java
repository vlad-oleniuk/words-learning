package com.wordslearning.ve.model.article;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractLocalWordsStorage extends WordsStorage {

	private Language langFrom;
	private String name;
	protected List<WLArticle> articles = new ArrayList<WLArticle>();

	@Override
	public Language getLangFrom() {
		return langFrom;
	}

	public void setLangFrom(Language langFrom) {
		this.langFrom = langFrom;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String fullName) {
		this.name = fullName;
	}

	public void addArticle(WLArticle ar) {
		articles.add(ar);
	}

	public void setArticles(List<WLArticle> ars) {
		this.articles = new ArrayList<WLArticle>(ars);
	}

	@JsonIgnore
	public int getDictionarySize() {
		return articles.size();
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public List<WLArticle> getWords(String key) {
		return filterWords(articles, key);
	}

	private List<WLArticle> filterWords(List<WLArticle> articles, String key) {
		List<WLArticle> filteredArticles = new ArrayList<WLArticle>();
		for (WLArticle wlArticle : articles) {
			if (wlArticle.getKey().toLowerCase().contains(key.toLowerCase())) {
				filteredArticles.add(wlArticle);
			}
		}
		return filteredArticles;
	}

	public List<WLArticle> getArticles() {
		return articles;
	}

}
