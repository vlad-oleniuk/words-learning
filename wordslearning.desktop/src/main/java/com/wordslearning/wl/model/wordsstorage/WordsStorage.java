package com.wordslearning.wl.model.wordsstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLVocabulary;
import com.wordslearning.ve.model.vocabularies.ResultReceiver;
import com.wordslearning.ve.model.vocabularies.VocabulariesAccessor;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.WordsLearning;
import com.wordslearning.wl.model.learnprocess.LearnVocabulary;

public class WordsStorage implements ResultReceiver<List<WLVocabulary>> {

	private final static Logger LOG = Logger.getLogger(WordsStorage.class
			.getName());

	private VocabulariesAccessor vocabulariesAccessor;

	private WordsLearning wordsLearning;

	private List<String> vocNames;

	private List<WLVocabulary> wlVocabularies;

	private LearnVocabulary learnVoc;

	public Map<Integer, WLWord> getWords() {
		return learnVoc.getWords();
	}

	public void init() {
		vocabulariesAccessor.retrieveVocabularies(this);
	}

	public void setLearnVocabulary(LearnVocabulary learnVoc) {
		this.learnVoc = learnVoc;
	}

	public void clearWords() {
		learnVoc = null;
	}

	private void setVocabularyNames(List<String> vocIds) {
		this.vocNames = vocIds;
	}

	public String getVocabularyName(int i) {
		return vocNames.get(i);
	}

	public int getVocabulariesAmount() {
		return wlVocabularies.size();
	}


	public Language getLearntVocLanguage() {
		return learnVoc.getLanguage();
	}

	public String getLearntVocName() {
		return learnVoc.getName();
	}

	public WordsLearning getWordsLearning() {
		return wordsLearning;
	}

	public void setWordsLearning(WordsLearning wordsLearning) {
		this.wordsLearning = wordsLearning;
	}

	@Override
	public void setResult(List<WLVocabulary> vocs) {
		this.wlVocabularies = vocs;
		List<String> vocNames = new ArrayList<String>();
		for (WLVocabulary wlVocabulary : vocs) {
			vocNames.add(wlVocabulary.getName());
		}
		setVocabularyNames(vocNames);
		wordsLearning.wordsStorageInitialized();
	}

	public VocabulariesAccessor getVocabulariesAccessor() {
		return vocabulariesAccessor;
	}

	public void setVocabulariesAccessor(
			VocabulariesAccessor vocabulariesAccessor) {
		this.vocabulariesAccessor = vocabulariesAccessor;
	}

	public WLVocabulary getWLVocabulary(String vocId) {
		for (WLVocabulary wlVoc : wlVocabularies) {
			if (vocId.equals(wlVoc.getName())) {
				return wlVoc;
			}
		}
		return null;
	}

	public WLVocabulary getNextWlVocabulary(String lastLearntVocabulary) {
		for (int i = 0; i < wlVocabularies.size(); i++) {
			if (wlVocabularies.get(i).getName().equals(lastLearntVocabulary)) {
				return wlVocabularies.get((i + 1) % wlVocabularies.size());
			}
		}

		if (wlVocabularies.size() > 0)
			return wlVocabularies.get(0);
		else
			return null;

	}
}
