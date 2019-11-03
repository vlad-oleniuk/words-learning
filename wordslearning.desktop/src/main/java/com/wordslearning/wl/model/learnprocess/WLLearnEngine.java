package com.wordslearning.wl.model.learnprocess;

import java.util.Map;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;


public interface WLLearnEngine {
	
	void finishAnswer(int wordId, AnswerGrade correct);
	
	Language getCurrentLanguage();
	
	Map<Integer, WLWord> getAllWords();
	
}
