package com.wordslearning.wl.model;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.vocabularies.VocabulariesAccessor;
import com.wordslearning.wl.model.ExerciseAction.Status;
import com.wordslearning.wl.model.exceptions.VocabularyNotAvailableException;
import com.wordslearning.wl.model.learnprocess.LearnProcessController;
import com.wordslearning.wl.model.learnprocess.LearnProcessDataAccessor;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.LearnReadiness;
import com.wordslearning.wl.model.learnprocess.StatisticSerializer;
import com.wordslearning.wl.model.learnprocess.WLLearnEngine;
import com.wordslearning.wl.model.settings.SettingsAccessor;
import com.wordslearning.wl.model.settings.SettingsController;
import com.wordslearning.wl.model.settings.SettingsManager;
import com.wordslearning.wl.model.wordsstorage.WordsStorage;
import com.wordslearning.wl.ui.WordsLearningUI;

public class WordsLearning implements WLLearnEngine, LearnProcessController {

	private LearnProcessEngine lpEngine = new LearnProcessEngine();
	private WordsStorage wordsStorage = new WordsStorage();
	private SettingsManager settingsManager = new SettingsManager();

	private WordsLearningUI wlUI;

	private VocabulariesAccessor vocsAccessor;
	private LearnProcessDataAccessor statisticFileAccessor;
	private SettingsAccessor settingsFilesAccessor;

	private CountDownLatch initializedLatch = new CountDownLatch(3);

	private static Logger LOG = Logger.getLogger(WordsLearning.class.getName());

	private void initSettingsManager(SettingsAccessor settingsFilesAccessor) {
		settingsManager.setFilesAccessor(settingsFilesAccessor);
		settingsManager.setWordsLearning(this);
		settingsManager.init();
	}

	public void init() {
		initSettingsManager(settingsFilesAccessor);
		initWordsStorage(vocsAccessor);
		initLPEngine(statisticFileAccessor);
		try {
			initializedLatch.await();
		} catch (InterruptedException e) {
			LOG.warning("The initialization latch was interrupted. The program might not work properly");
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public ExerciseAction getNextExercise() {
		if (lpEngine.getReadiness() == LearnReadiness.SESSION_WORDS_LIMIT_REACHED) {
			LOG.info("getNextExercise. SESSION_WORDS_LIMIT_REACHED");
			ExerciseAction response = new ExerciseAction();
			response.setStatus(Status.SESSION_WORDS_LIMIT_REACHED);
			return response;
		} else {
			LOG.info("getNextExercise. READY_FOR_NEXT_WORD");
			ExerciseAction response = new ExerciseAction();
			do {
				WLWord lWord = lpEngine.getNextWord();
				if (lWord != null) {
					ExerciseMeta currentExercise = getNextApplicableExercise(lWord);
					if (currentExercise != null) {
						response.setStatus(Status.READY);
						response.setWord(lWord);
						response.setExercise(currentExercise);
					}
				} else {
					response.setStatus(Status.NO_FURTHER_WORDS);
				}
			} while (response.getStatus() != Status.READY
					&& response.getStatus() != Status.NO_FURTHER_WORDS);
			return response;
		}
	}

	@Override
	public void finishAnswer(int wordId, AnswerGrade grade) {
		lpEngine.processAnswer(wordId, grade);
	}

	/**
	 * @return next applicable exercise for the specified word. Next exercise is
	 *         also determined by the fact, whether the exercise was previously
	 *         correct answered. If correct next exercise is searched, otherwise
	 *         a previous one
	 */
	private ExerciseMeta getNextApplicableExercise(WLWord lWord) {
		boolean up = AnswerGrade.getGradeByDifficultyFactor(
				lWord.getDifficulty()).isCorrect();
		ExerciseMeta curExercise;
		while ((curExercise = lpEngine.getCurrentExercise(lWord.getId())) != null
				&& !wlUI.isExerciseApplicable(curExercise.getId(), lWord)) {
			// if the word is at its first exercise - it can move only to the
			// next exercise
			if (lWord.getCurrentPoints() == 0) {
				up = true;
			}
			if (up) {
				lpEngine.moveToTheNextExercise(lWord.getId());
			} else {
				lpEngine.moveToThePreviousExercise(lWord.getId());
			}
		}
		return curExercise;
	}

	@Override
	public Map<Integer, WLWord> getAllWords() {
		return getWordsStorage().getWords();
	}

	public void stopLearnSession() {
		lpEngine.stopLearnSession();
	}

	@Override
	public void resetCurrentWord() {
		lpEngine.resetCurrentWord();
	}

	@Override
	public WLWord getCurrentWord() {
		return lpEngine.getCurrentWord();
	}

	public void setWlUI(WordsLearningUI wlUI) {
		this.wlUI = wlUI;
	}

	@Override
	public Language getCurrentLanguage() {
		return getWordsStorage().getLearntVocLanguage();
	}

	public WordsStorage getWordsStorage() {
		return wordsStorage;
	}

	public void setWordsStorage(WordsStorage wordsStorage) {
		this.wordsStorage = wordsStorage;
	}

	public void settingsInitialized() {
		LOG.info("Settings initialized");
		initializedLatch.countDown();
	}

	private void initLPEngine(LearnProcessDataAccessor statisticFileAccessor) {
		lpEngine.setWordsStorage(wordsStorage);
		lpEngine.setStatisticSerializer(new StatisticSerializer());
		lpEngine.setSettingsManager(settingsManager);
		lpEngine.setStatisticAccessor(statisticFileAccessor);
		lpEngine.setWordsLearning(this);
		lpEngine.init();
	}

	private void initWordsStorage(VocabulariesAccessor vocsAccessor) {
		wordsStorage.setVocabulariesAccessor(vocsAccessor);
		wordsStorage.setWordsLearning(this);
		wordsStorage.init();
	}

	public void wordsStorageInitialized() {
		LOG.info("Words Storage initialized");
		initializedLatch.countDown();
	}

	public void learnEngineInitialized() {
		LOG.info("Learn Engine initialized");
		initializedLatch.countDown();
	}

	public SettingsController getSettingsController() {
		return settingsManager;
	}

	public VocabulariesAccessor getVocsAccessor() {
		return vocsAccessor;
	}

	public void setVocsAccessor(VocabulariesAccessor vocsAccessor) {
		this.vocsAccessor = vocsAccessor;
	}

	public LearnProcessDataAccessor getStatisticFileAccessor() {
		return statisticFileAccessor;
	}

	public void setStatisticFileAccessor(
			LearnProcessDataAccessor statisticFileAccessor) {
		this.statisticFileAccessor = statisticFileAccessor;
	}

	public SettingsAccessor getSettingsFilesAccessor() {
		return settingsFilesAccessor;
	}

	public void setSettingsFilesAccessor(SettingsAccessor settingsFilesAccessor) {
		this.settingsFilesAccessor = settingsFilesAccessor;
	}

	@Override
	public void startLearnSession() {
		try {
			lpEngine.startLearnSession();
		} catch (VocabularyNotAvailableException e) {
			LOG.warning("No Vocabulries");
			wlUI.notifyNoVocabularies();
		}
	}

	void setLpEngine(LearnProcessEngine lpEngine) {
		this.lpEngine = lpEngine;
	}

}