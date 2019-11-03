package com.wordslearning.ve.model;

import java.util.List;

import com.wordslearning.ve.model.article.WLArticle;

public interface VEModelListener {
	
	public void vocabulariesInitialized();
	
	public void modelChanged();
	
	public void editedVocabularyChanged();

	public void homonymsRevealed(WLArticle wlArticle, List<WLArticle> homonyms);

	public void synonymsRevealed(WLArticle wlArticle, List<WLArticle> synonyms);
}
