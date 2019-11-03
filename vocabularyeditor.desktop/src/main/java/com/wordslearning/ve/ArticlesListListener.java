package com.wordslearning.ve;

import com.wordslearning.ve.model.article.WLArticle;

public interface ArticlesListListener {
	
	void articleSelected(WLArticle article);
	
	void articleDoubleClicked(WLArticle article);
	
	void customArticleSelected(String key);
}
