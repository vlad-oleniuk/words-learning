package com.wordslearning.wl.model.learnprocess;

import com.wordslearning.wl.model.ExerciseAction;
import com.wordslearning.wl.model.WLWord;

public interface LearnProcessController {

	/**
	 * @return ExerciseAction instance, representing a next possible exercise in
	 *         the current context. If the word is available and session limit
	 *         is not reached , the status is set to READY, the word to the
	 *         appropriate word and the exercise id to the current exercise id
	 *         for the current word
	 * 
	 *         If session limit is reached an ExerciseAction is returned, that
	 *         has the status set to SESSION_WORDS_LIMIT_REACHED.
	 * 
	 *         If no words are left to be returned an ExerciseAction is
	 *         returned, that has the status set to NO_FURTHER_WORDS.
	 */
	ExerciseAction getNextExercise();

	void resetCurrentWord();

	void startLearnSession();

	void stopLearnSession();

	WLWord getCurrentWord();

}
