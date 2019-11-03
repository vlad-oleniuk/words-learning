package com.wordslearning.wl.exercises;

import java.util.Set;

import com.wordslearning.wl.model.WLWord;


public class SelectAntonyms extends SelectExercise {

	@Override
	protected boolean isMultipleSelectionPossible() {
		return true;
	}

	@Override
	protected Set<String> getAnswerVariants(WLWord wlWord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isCorrectVariant(String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getExerciseName() {
		return "Select Antonyms";
	}

	@Override
	public boolean isApplicable(WLWord word) {
		// TODO Auto-generated method stub
		return false;
	}

}
