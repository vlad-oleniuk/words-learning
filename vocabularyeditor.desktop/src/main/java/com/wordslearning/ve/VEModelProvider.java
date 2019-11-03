package com.wordslearning.ve;

import com.wordslearning.ve.model.article.WLArticle;

public interface VEModelProvider {
	public void addWLArticleToModel(WLArticle article, boolean homonymsChecked);
}
