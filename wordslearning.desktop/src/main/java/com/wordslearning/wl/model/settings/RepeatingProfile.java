package com.wordslearning.wl.model.settings;

import java.util.ArrayList;
import java.util.List;

public class RepeatingProfile {
	private String id;
	private List<String> exercises = new ArrayList<String>();
	
	/**
	 * general, correct, incorrect
	 */
	private List<int[]> exercisePoints = new ArrayList<int[]>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getExercises() {
		return exercises;
	}

	public void putExercise(String exId, int[] points) {
		exercises.add(exId);
		exercisePoints.add(points);
	}
	
	public void setExercisePoints(int exIndex, int[] points){
		exercisePoints.set(exIndex, points);
	}
	
	public int[] getExercisePoints(int index){
		return exercisePoints.get(index);
	}
	
	@Override
	public String toString() {
		return id;
	}

}
