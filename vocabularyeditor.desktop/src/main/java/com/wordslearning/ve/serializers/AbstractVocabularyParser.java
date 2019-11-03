package com.wordslearning.ve.serializers;

import java.io.File;

import com.wordslearning.ve.model.article.AbstractLocalWordsStorage;



public interface AbstractVocabularyParser {
	public AbstractLocalWordsStorage parseVocabularyFile(File file);
}
