package com.wordslearning.ve.word_extractors;

import java.util.ArrayList;
import java.util.List;

import com.wordslearning.ve.gui.HomonymsResolverDialog;
import com.wordslearning.ve.model.article.WLArticle;


public class TestConflictsResolverDialog {

	public static void main(String[] args) {
		WLArticle article1 = new WLArticle();
		article1.setKey("key-1");
		article1.setValue("value-1 value-1 value-1 value-1 value-1 value-1 value-1 value-1 value-1 value-1 value-1");

		List<WLArticle> conflictingWords = new ArrayList<WLArticle>();
		for (int i = 0; i < 5; i++) {
			WLArticle article2 = new WLArticle();
			article2.setKey("key" + i);
			article2.setValue("value value value value value value value value value value value value value value"
					+ i);
			conflictingWords.add(article2);
		}

		new HomonymsResolverDialog().showDialog(article1, conflictingWords);
	}

}
