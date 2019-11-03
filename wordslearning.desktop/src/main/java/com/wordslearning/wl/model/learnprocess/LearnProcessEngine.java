package com.wordslearning.wl.model.learnprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.wordslearning.ve.model.article.WLVocabulary;
import com.wordslearning.ve.model.vocabularies.ResultReceiver;
import com.wordslearning.wl.model.ExerciseMeta;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.WordsLearning;
import com.wordslearning.wl.model.exceptions.ExerciseNotAvailableException;
import com.wordslearning.wl.model.exceptions.VocabularyNotAvailableException;
import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.model.settings.SettingsManager;
import com.wordslearning.wl.model.wordsstorage.WordsStorage;

public class LearnProcessEngine implements ResultReceiver<LearnProcessData> {
	private static final int NO_AVAILABLE_REPEATING_PROFILES = -2;
	private static final int POINTS_OVERFLOW = -1;

	private Set<Integer> learningWordsIds = new HashSet<Integer>();
	private WordsStorage wordsStorage;
	private LinkedList<Integer> usedWords = new LinkedList<Integer>();
	private int sessionWordsNumber = 0;
	private final static Logger LOG = Logger.getLogger(LearnProcessEngine.class
			.getName());

	private StatisticSerializer statisticSerializer;

	private LearnProcessData lpData;

	private WordsLearning wordsLearning;
	private SettingsManager settingsManager;

	private LearnProcessDataAccessor statisticAccessor;

	public void init() {
		statisticAccessor.retrieveLPData(this);
	}

	public void startLearnSession() throws VocabularyNotAvailableException {
		if (wordsStorage.getVocabulariesAmount() == 0)
			throw new VocabularyNotAvailableException();

		List<Integer> intervals = new ArrayList<Integer>(settingsManager
				.getWLProfile().getRepeatProfiles().keySet());
		Collections.sort(intervals);

		LearnStatistic learnStatistic = statisticSerializer
				.extractRelStatistic(lpData.getStatistic(), intervals);

		String lastLearntVocabulary = lpData.getLastLearntVoc();
		LearnVocabulary learnVoc;
		String vocCandidate = lastLearntVocabulary;
		do {
			WLVocabulary wlVoc = wordsStorage.getNextWlVocabulary(vocCandidate);
			vocCandidate = wlVoc.getName();
			learnVoc = extractLearnVocabulary(learnStatistic, wlVoc);
			if (vocCandidate.equals(lastLearntVocabulary))
				break;
			if (lastLearntVocabulary == null)
				lastLearntVocabulary = learnVoc.getName();
		} while (learnVoc.getVocSize() == 0);
		wordsStorage.setLearnVocabulary(learnVoc);
		initLearningWordsList(learnVoc.getWords());
	}

	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

	private void initLearningWordsList(Map<Integer, WLWord> words) {
		learningWordsIds = lpData.getWordsBeingLearnt().get(
				wordsStorage.getLearntVocName());
		if (learningWordsIds == null)
			learningWordsIds = new HashSet<Integer>();
		Iterator<Integer> iterator = learningWordsIds.iterator();
		while (iterator.hasNext()) {
			if (!words.containsKey(iterator.next()))
				iterator.remove();
		}
		replenishLearningWords();
	}

	private void replenishLearningWords() {
		List<WLWord> wordsList = new ArrayList<WLWord>(wordsStorage.getWords()
				.values());
		Collections.shuffle(wordsList);
		int learnWordAmount = settingsManager.getLearnWordsAmount();
		for (WLWord wlWord : wordsList) {
			if (learningWordsIds.size() < learnWordAmount) {
				if (!isLearnt(wlWord))
					learningWordsIds.add(wlWord.getId());
			} else
				break;
		}
	}

	public void stopLearnSession() {
		storeState();
		wordsStorage.clearWords();
		usedWords.clear();
		learningWordsIds.clear();
		sessionWordsNumber = 0;
	}

	public void setWordsStorage(WordsStorage model) {
		this.wordsStorage = model;
	}

	private WLWord getRandomWordToBeLearnt() {
		List<Integer> lwIdsList = new ArrayList<Integer>(learningWordsIds);
		Collections.shuffle(lwIdsList);
		for (int j = 0; j < lwIdsList.size(); j++) {
			if (!usedWords.contains(lwIdsList.get(j))) {
				return wordsStorage.getWords().get(lwIdsList.get(j));
			}
		}
		return null;
	}

	private boolean isBeingLearntToday(WLWord word) {
		return learningWordsIds.contains(word.getId());
	}

	private WLWord getRandomWordToBeRepeated() {
		List<Integer> keys = new ArrayList<Integer>(wordsStorage.getWords()
				.keySet());
		Collections.shuffle(keys);
		for (int i = 0; i < keys.size(); i++) {
			WLWord word = wordsStorage.getWords().get(keys.get(i));
			if (!isBeingLearntToday(word) && !usedWords.contains(word.getId()))
				return word;
		}
		return null;
	}

	private boolean isLearnt(WLWord word) {
		return word.getLearnDate() != null;
	}

	private void updatePoints(int wordId, int points,
			boolean updateLastRepeatInterval) {
		WLWord word = wordsStorage.getWords().get(wordId);
		RepeatingProfile lpf = getCurrentRepeatingProfile(wordId);
		List<String> exercises = lpf.getExercises();
		int pointsSum = 0;
		boolean isLastExercise = true;
		for (int i = 0; i < exercises.size(); i++) {
			pointsSum += lpf.getExercisePoints(i)[0];
			isLastExercise = exercises.size() - 1 == i;
			if (pointsSum > word.getCurrentPoints()) {
				break;
			}
		}

		// if the word is still in the same exercise
		if (word.getCurrentPoints() + points < pointsSum)
			word.setCurrentPoints(word.getCurrentPoints() + points);
		else {
			processExerciseChange(word, isLastExercise,
					updateLastRepeatInterval, pointsSum);
		}
	}

	private void processExerciseChange(WLWord word,
			boolean isLastExerciseInLPF, boolean updateLastRepeatInterval,
			int pointsSum) {
		// if interval limit is reached
		if (isLastExerciseInLPF) {
			processRepeatIntervalChange(word, updateLastRepeatInterval);
		} else {
			// if exercise limit is reached
			word.setCurrentPoints(pointsSum);
		}
	}

	private void processRepeatIntervalChange(WLWord word,
			boolean updateLastRepeatInterval) {
		if (word.getLearnDate() == null)
			word.setLearnDate(new Date());
		else {
			word.setLastRepeatDate(new Date());
			if (updateLastRepeatInterval) {
				word.setLastRepeatInterval(getCurrentRepeatingInterval(word
						.getId()));
			}
		}
		word.setCurrentPoints(0);
	}

	public void processAnswer(int wordId, AnswerGrade answerGrade) {
		if (answerGrade != AnswerGrade.INCORRECT_ACCIDENTALY) {
			int[] points = getCurrentPointsForWord(wordId);

			updatePoints(wordId, answerGrade.isCorrect() ? points[1]
					: points[2], answerGrade != AnswerGrade.INCORRECT_FORGET);

			WLWord currentWord = wordsStorage.getWords().get(wordId);
			if (!isLearnt(currentWord)) {
				postProcessAnswerLearnWord(wordId, answerGrade.isCorrect());
			} else {
				postProcessAnswerRepeatWord(wordId, answerGrade);
			}
		}
		sessionWordsNumber++;
	}

	private void postProcessAnswerRepeatWord(int wordId, AnswerGrade answerGrade) {

		if (answerGrade == AnswerGrade.INCORRECT_FAIL) {
			resetWord(wordId);
			return;
		}
		WLWord word = wordsStorage.getWords().get(wordId);
		// if the answer is correct, but the previous answer was
		// incorrect_forget difficulty should be set to difficult
		if (answerGrade.isCorrect()
				&& AnswerGrade.getGradeByDifficultyFactor(word.getDifficulty()) == AnswerGrade.INCORRECT_FORGET) {
			word.setDifficulty(AnswerGrade.CORRECT_DIFFICULT
					.getDifficultyFactor());
		} else {
			word.setDifficulty(answerGrade.getDifficultyFactor());
		}
	}

	private int[] getCurrentPointsForWord(int wordId) {
		int exIndex = getCurrentExerciseIndex(wordId);
		if (exIndex < 0)
			throw new ExerciseNotAvailableException("The errorCode " + exIndex
					+ " for the word " + wordId + " was returned");
		return getCurrentRepeatingProfile(wordId).getExercisePoints(exIndex);
	}

	private void postProcessAnswerLearnWord(int wordId, boolean correct) {
		WLWord word = wordsStorage.getWords().get(wordId);
		if (!correct) {
			word.setDifficulty(AnswerGrade.INCORRECT_FORGET
					.getDifficultyFactor());
		} else {
			word.setDifficulty(AnswerGrade.CORRECT_EASY.getDifficultyFactor());
		}
		if (isLearnt(word)) {
			learningWordsIds.remove(wordId);
			replenishLearningWords();
		}
	}

	/**
	 * @param wordId
	 *            id of the word to be processed
	 * @return exercise index (1,2,..n), that should be shown next for this word
	 *         or POINTS_OVERFLOW if no such exercise exists
	 */
	private int getCurrentExerciseIndex(int wordId) {
		RepeatingProfile repProfile = getCurrentRepeatingProfile(wordId);
		// if current word has already been repeated maximum possible times.
		// the reason
		if (repProfile == null)
			return NO_AVAILABLE_REPEATING_PROFILES;
		WLWord word = wordsStorage.getWords().get(wordId);
		int sumPoints = 0;
		int i = -1;
		do {
			// if maximum needed points were decreased, there might be words
			// which already have more points than max reachable
			if (++i == repProfile.getExercises().size())
				return POINTS_OVERFLOW;
			sumPoints += repProfile.getExercisePoints(i)[0];
		} while (word.getCurrentPoints() >= sumPoints);
		return i;
	}

	private RepeatingProfile getCurrentRepeatingProfile(int wordId) {
		WLWord word = wordsStorage.getWords().get(wordId);
		if (!isLearnt(word)) {
			return settingsManager.getWLProfile().getLearnProfile();
		} else {
			int lastRepInt = word.getLastRepeatInterval();
			Set<Integer> keys = settingsManager.getWLProfile()
					.getRepeatProfiles().keySet();
			Integer[] keysArray = new Integer[keys.size()];
			keys.toArray(keysArray);
			Arrays.sort(keysArray);
			for (Integer interval : keysArray) {
				if (interval > lastRepInt) {
					return settingsManager.getWLProfile().getRepeatProfiles()
							.get(interval);
				}
			}
		}
		return null;
	}

	private int getCurrentRepeatingInterval(int wordId) {
		WLWord word = wordsStorage.getWords().get(wordId);
		if (!isLearnt(word))
			return 0;
		else {
			int lastRepInt = word.getLastRepeatInterval();
			Set<Integer> keys = settingsManager.getWLProfile()
					.getRepeatProfiles().keySet();
			Integer[] keysArray = new Integer[keys.size()];
			keys.toArray(keysArray);
			Arrays.sort(keysArray);
			for (Integer interval : keysArray) {
				if (interval > lastRepInt) {
					return interval;
				}
			}
		}
		return -1;
	}

	private WLWord getNextWordCandidate() {
		// get two objects
		// if one of them is null return second one
		// if none of them is null return one based on ratio

		WLWord learnWordCandidate = getRandomWordToBeLearnt();
		WLWord repeatWordCandidate = getRandomWordToBeRepeated();

		if (repeatWordCandidate == null)
			return learnWordCandidate;
		else if (learnWordCandidate == null)
			return repeatWordCandidate;
		else {
			if (Math.random() < settingsManager.getLearnRepeatRatio()) {
				return learnWordCandidate;
			} else {
				return repeatWordCandidate;
			}
		}
	}

	public WLWord getNextWord() {
		WLWord lWord = getNextWordCandidate();
		if (lWord != null) {
			usedWords.add(lWord.getId());
			return lWord;
		} else {
			return null;
		}
	}

	/**
	 * return the exercise for the specified word id.
	 * 
	 * Null is returned if no exercise can be applied to the word. This might
	 * happen if the word has already more points that maximally possible or the
	 * appropriate profile is not available any more
	 */
	public ExerciseMeta getCurrentExercise(int wordId) {
		int index = getCurrentExerciseIndex(wordId);
		if (index >= 0) {
			RepeatingProfile repProfile = getCurrentRepeatingProfile(wordId);
			return ExerciseMeta.getById(repProfile.getExercises().get(index));
		} else {
			if (index == POINTS_OVERFLOW)
				updatePoints(wordId, 0, true);
			return null;
		}
	}

	public void resetCurrentWord() {
		resetWord(usedWords.getLast());
	}

	public WLWord getCurrentWord() {
		return wordsStorage.getWords().get(usedWords.getLast());
	}

	private void resetWord(int wordId) {
		WLWord word = wordsStorage.getWords().get(wordId);
		word.setCurrentPoints(0);
		word.setLastRepeatDate(null);
		word.setLearnDate(null);
		word.setLastRepeatInterval(0);

	}

	public LearnReadiness getReadiness() {
		if (sessionWordsNumber >= settingsManager.getSessionWordsAmount())
			return LearnReadiness.SESSION_WORDS_LIMIT_REACHED;
		else
			return LearnReadiness.READY_FOR_NEXT_WORD;

	}

	public void moveToTheNextExercise(int wordId) {
		int currentExIndex = getCurrentExerciseIndex(wordId);
		// increase current points until next exercise becomes current
		WLWord word = wordsStorage.getWords().get(wordId);
		while (getCurrentExerciseIndex(wordId) == currentExIndex)
			word.setCurrentPoints(word.getCurrentPoints() + 1);
	}

	public void moveToThePreviousExercise(int wordId) {
		// get current exerc. index
		int currentExIndex = getCurrentExerciseIndex(wordId);
		// decrease current points until prev exercise becomes current and
		// points are at the exercise start
		WLWord word = wordsStorage.getWords().get(wordId);
		while (word.getCurrentPoints() >= 0
				&& getCurrentExerciseIndex(wordId) != currentExIndex - 2) {
			word.setCurrentPoints(word.getCurrentPoints() - 1);
		}
		// to compensate going to the exercise before previous or going to -1
		word.setCurrentPoints(word.getCurrentPoints() + 1);
	}

	public enum AnswerGrade {
		CORRECT_EASY(true, 0), CORRECT_EFFORT(true, 0.5), CORRECT_DIFFICULT(
				true, 0.75), INCORRECT_FAIL(false, 2), INCORRECT_FORGET(false,
				1), INCORRECT_ACCIDENTALY(false, -1);

		private boolean correct;
		private double difficultyFactor;

		private AnswerGrade(boolean correct, double difficultyFactor) {
			this.correct = correct;
			this.difficultyFactor = difficultyFactor;
		}

		public static AnswerGrade getGradeByDifficultyFactor(double difficulty) {
			if (difficulty < 0)
				return AnswerGrade.INCORRECT_ACCIDENTALY;
			else if (difficulty < 0.5)
				return AnswerGrade.CORRECT_EASY;
			else if (difficulty < 0.75)
				return AnswerGrade.CORRECT_EFFORT;
			else if (difficulty < 1)
				return AnswerGrade.CORRECT_DIFFICULT;
			else if (difficulty < 2)
				return AnswerGrade.INCORRECT_FORGET;
			else
				return AnswerGrade.INCORRECT_FAIL;
		}

		public double getDifficultyFactor() {
			return difficultyFactor;
		}

		public boolean isCorrect() {
			return correct;
		}
	}

	public enum LearnReadiness {
		SESSION_WORDS_LIMIT_REACHED, READY_FOR_NEXT_WORD
	}

	private LearnVocabulary extractLearnVocabulary(
			LearnStatistic statisticResponse, WLVocabulary vocabulary) {
		WordsInflatter wordsInflatter = new WordsInflatter();
		wordsInflatter.setStatistic(statisticResponse);
		Map<Integer, WLWord> words = wordsInflatter.inflateWords(vocabulary
				.getArticles());
		LOG.info("Learnt words amount == " + words.size());
		LearnVocabulary voc = new LearnVocabulary();
		voc.setName(vocabulary.getName());
		voc.setLanguage(vocabulary.getLangFrom());
		voc.setWords(words);
		return voc;
	}

	private void storeState() {
		LearnProcessData lpData = new LearnProcessData();
		lpData.setStatistic(statisticSerializer.synchronizeStatistic(
				this.lpData.getStatistic(), this.wordsStorage.getWords()));
		lpData.setLastLearntVoc(wordsStorage.getLearntVocName());
		Map<String, Set<Integer>> wordsBeingLearnt = new HashMap<String, Set<Integer>>(
				this.lpData.getWordsBeingLearnt());
		wordsBeingLearnt.put(wordsStorage.getLearntVocName(),
				this.learningWordsIds);
		lpData.setWordsBeingLearnt(wordsBeingLearnt);
		statisticAccessor.storeLPData(lpData);
	}

	@Override
	public void setResult(LearnProcessData statistic) {
		lpData = statistic;
		wordsLearning.learnEngineInitialized();
	}

	public void setStatisticAccessor(LearnProcessDataAccessor statisticAccessor) {
		this.statisticAccessor = statisticAccessor;
	}

	public void setWordsLearning(WordsLearning wordsLearning) {
		this.wordsLearning = wordsLearning;
	}

	public StatisticSerializer getStatisticSerializer() {
		return statisticSerializer;
	}

	public void setStatisticSerializer(StatisticSerializer statisticSerializer) {
		this.statisticSerializer = statisticSerializer;
	}

}
