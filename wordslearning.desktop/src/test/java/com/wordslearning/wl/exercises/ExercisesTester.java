package com.wordslearning.wl.exercises;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.wl.exercises.ExampleExercise;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.learnprocess.WLLearnEngine;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;


public class ExercisesTester extends JFrame {

	public static void main(String[] args) {
		new ExercisesTester();
	}

	public ExercisesTester() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(640, 480);

		WLWord word = new WLWord();

		WLArticle article = new WLArticle();
		article.setKey("der Tisch");
		article.setValue("value value value value value value value value value value value value valuevalue valuevaluevalue" + 9);
		Set<String> examples = new HashSet<String>();
		for (int i = 0; i < 4; i++) {
			examples.add("konnte der $mutmaßliche Attentäter$, (von Aurora)in den vergangenen Wochen vollkommen legal kaufen.");
		}
		article.setIllustrationURL("http://images.orgill.com/200x200/3890662.jpg");
		article.setRawExamples(examples);
		word.setWlArticle(article);

		ExampleExercise sEx = new ExampleExercise();
		sEx.setLearnEngineAccessor(new WLLearnEngine() {
			
			@Override
			public Language getCurrentLanguage() {
				return Language.GER;
			}
			
			@Override
			public void finishAnswer(int wordId, AnswerGrade correct) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Map<Integer, WLWord> getAllWords() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		sEx.showArticle(word);
		add(sEx);

		setVisible(true);
	}

}
