package com.wordslearning.wl.ui;

import com.wordslearning.wl.model.WLWord;

public interface WordsLearningUI {

	public boolean isExerciseApplicable(String curExId, WLWord lWord);

	public void notifyNoVocabularies();

}