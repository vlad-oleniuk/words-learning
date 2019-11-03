package com.wordslearning.wl.ui.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.swing.JPanel;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.learnprocess.WLLearnEngine;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;

public abstract class WLExercise extends JPanel {

	private WLWord word;

	private WLLearnEngine learnEngineAccessor;

	private File dataDir;

	private WordsLearningMainUI wlMainUI;

	public abstract String getExerciseName();

	/**
	 * 
	 * @param word
	 *            to be shown in the exercise
	 */

	public abstract void showArticle(WLWord word);

	/**
	 * determines whether this exercise is applicable to the given word
	 * 
	 * @param word
	 *            to be checked
	 * @return true if might be shown false otherwise
	 */
	public abstract boolean isApplicable(WLWord word);

	protected File getFilesDirectory() {
		return dataDir;
	}

	protected void finishAnswer(AnswerGrade grade) {
		learnEngineAccessor.finishAnswer(getWord().getId(), grade);
		wlMainUI.exerciseFinished();
	}

	protected void finishAnswer(boolean correct) {
		finishAnswer(mapToAnswerGrade(correct));
	}

	protected Map<Integer, WLWord> getAllWords() {
		return Collections.unmodifiableMap(learnEngineAccessor.getAllWords());
	}

	public final void setLearnEngineAccessor(WLLearnEngine manager) {
		this.learnEngineAccessor = manager;
	}

	public final void setDataDirectory(String workDir) {
		this.dataDir = new File(workDir);
		if (!this.dataDir.exists()) {
			this.dataDir.mkdirs();
		}
	}

	protected WLWord getWord() {
		return word;
	}

	protected void setWord(WLWord word) {
		this.word = word;
	}

	private AnswerGrade mapToAnswerGrade(boolean correct) {
		return correct ? AnswerGrade.CORRECT_EASY
				: AnswerGrade.INCORRECT_FORGET;
	}

	protected Language getCurrentLanguage() {
		return learnEngineAccessor.getCurrentLanguage();
	}

	@Override
	public String toString() {
		return getExerciseName();
	}

	public void setWlMainUI(WordsLearningMainUI wlUI) {
		this.wlMainUI = wlUI;
	}

	protected class AnswerHandler implements ActionListener {

		private AnswerGrade grade;
		private WLExercise exercise;

		public AnswerHandler(AnswerGrade grade) {
			this.grade = grade;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			finishAnswer(grade);
		}
	}

}
