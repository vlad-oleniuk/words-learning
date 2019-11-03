package com.wordslearning.ve.model.vocabularies;

import java.util.List;

import com.wordslearning.ve.model.article.WLVocabulary;

public abstract class VocabulariesAccessor {

	public abstract void saveVocabularies(List<WLVocabulary> vocs);

	public abstract void retrieveVocabularies(ResultReceiver<List<WLVocabulary>> receiver);

}