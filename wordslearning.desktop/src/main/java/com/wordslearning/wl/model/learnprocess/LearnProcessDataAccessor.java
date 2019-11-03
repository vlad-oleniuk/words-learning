package com.wordslearning.wl.model.learnprocess;

import com.wordslearning.ve.model.vocabularies.ResultReceiver;

public abstract class LearnProcessDataAccessor {

	protected final static String STATISTIC_FILE_NAME = "statistic.xml";
	protected static final String LEARNING_WORDS_FILE_NAME = "learning_words.json";
	protected static final String LEARN_DATA_FILE_NAME = "learn_data.properties";

	public abstract void retrieveLPData(
			ResultReceiver<LearnProcessData> receiver);

	public abstract void storeLPData(LearnProcessData learnProcessData);

}
