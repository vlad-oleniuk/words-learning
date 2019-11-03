package com.wordslearning.wl.exercises;

import java.util.HashSet;
import java.util.Set;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.WordsLearningUI;


public class SelectSynonyms extends SelectExercise {
	@Override
	protected boolean isMultipleSelectionPossible() {
		return true;
	}

	@Override
	protected Set<String> getAnswerVariants(WLWord word) {
		Set<String> res = new HashSet<String>();
		if (word.getId() == getWord().getId())
			res.addAll(word.getWLArticle().getSynonyms());
		else
			res.add(word.getWLArticle().getKey());
		return res;

	}

	@Override
	protected boolean isCorrectVariant(String variant) {
		return getWord().getWLArticle().getSynonyms().contains(variant);
	}

	@Override
	public String getExerciseName() {
		return "Select Synonyms";
	}

	@Override
	public boolean isApplicable(WLWord word) {
		return word.getWLArticle().getSynonyms() != null
				&& word.getWLArticle().getSynonyms().size() > 0;
	}
}
