package com.wordslearning.wl.exercises;

import java.util.HashSet;
import java.util.Set;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.WordsLearningUI;


public class SelectTranslation extends SelectExercise {

	@Override
	protected boolean isCorrectVariant(String answer) {
		return answer.equals(getWord().getWLArticle().getValue());
	}
	
	@Override
	protected boolean isMultipleSelectionPossible() {
		return false;
	}

	@Override
	protected Set<String> getAnswerVariants(WLWord word) {
		Set<String> res=new HashSet<String>();
		res.add(word.getWLArticle().getValue());
		return res;
	}

	@Override
	public String getExerciseName() {
		return "Select Meaning";
	}

	@Override
	public boolean isApplicable(WLWord word) {
		return true;
	}
	
}
