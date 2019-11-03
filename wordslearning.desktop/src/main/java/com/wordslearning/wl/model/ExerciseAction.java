package com.wordslearning.wl.model;


public class ExerciseAction {
	
	public enum Status {
		SESSION_WORDS_LIMIT_REACHED, READY, NO_FURTHER_WORDS
	}
	
	private Status status;
	
	private ExerciseMeta exercise;
	
	private WLWord word;


	public WLWord getWord() {
		return word;
	}

	public void setWord(WLWord word) {
		this.word = word;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ExerciseMeta getExercise() {
		return exercise;
	}

	public void setExercise(ExerciseMeta exercise) {
		this.exercise = exercise;
	}

	
}
