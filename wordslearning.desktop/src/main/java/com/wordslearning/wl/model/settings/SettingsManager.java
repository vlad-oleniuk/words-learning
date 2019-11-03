package com.wordslearning.wl.model.settings;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.wordslearning.ve.model.vocabularies.ResultReceiver;
import com.wordslearning.wl.model.WordsLearning;

public class SettingsManager implements SettingsController,
		ResultReceiver<SettingsBundle> {

	private static Logger LOG = Logger.getLogger(SettingsManager.class
			.getName());

	private static final String P_LEARN_REPEAT_RATIO = "learn.repeat.ratio";
	private static final String P_NOTIFICATION_PERIOD = "notification.period";
	private static final String P_SESSION_WORDS_AMOUNT = "session.words.ammount";
	private static final String P_LEARN_WORDS_AMOUNT = "learn.words.ammount";

	// private static final String P_DATA_DIR = "data.dir";

	private Properties learnProperties;

	private List<RepeatingProfile> repeatingProfiles;

	private WordsLearning wordsLearning;

	private WLProfile wlProfile;
	private Properties filePathes;

	private SettingsAccessor filesAccessor;

	public void init() {
		filesAccessor.retrieveSettings(this);
	}

	public void setResult(SettingsBundle settingsBundle) {
		this.learnProperties = settingsBundle.getLearnProperties();
		this.filePathes = settingsBundle.getFilePathes();
		this.repeatingProfiles = settingsBundle.getRepeatingProfiles();
		this.wlProfile = settingsBundle.getWlProfile();
		// wordsLearning.dataDirChanged(getDataDir());
		wordsLearning.settingsInitialized();
	}

	@Override
	public void addRepeatProfile(RepeatingProfile newProfile) {
		this.repeatingProfiles.add(newProfile);
	}

	@Override
	public void removeRepeatProfile(RepeatingProfile profile) {
		this.repeatingProfiles.remove(profile);
	}

	@Override
	public RepeatingProfile[] getRepeatingProfiles() {
		return this.repeatingProfiles
				.toArray(new RepeatingProfile[this.repeatingProfiles.size()]);
	}

	@Override
	public RepeatingProfile getRepeatProfile(String repProfId) {
		for (RepeatingProfile repProfile : this.repeatingProfiles) {
			if (repProfId.equals(repProfile.getId()))
				return repProfile;
		}
		return null;
	}

	// @Override
	// public String getDataDir() {
	// return filePathes.getProperty(P_DATA_DIR);
	// }

	@Override
	public WLProfile getWLProfile() {
		return this.wlProfile;
	}

	@Override
	public void setNotificationPeriod(int value) {
		this.learnProperties.put(P_NOTIFICATION_PERIOD, String.valueOf(value));
	}

	@Override
	public void setSessionsWordAmount(int value) {
		this.learnProperties.put(P_SESSION_WORDS_AMOUNT, String.valueOf(value));
	}

	@Override
	public void setLearnWordsAmounty(int value) {
		this.learnProperties.put(P_LEARN_WORDS_AMOUNT, String.valueOf(value));
	}

	// @Override
	// public void setDataDir(String filePath) {
	// wordsLearning.dataDirChanged(filePath);
	// filePathes.put(P_DATA_DIR, filePath);
	// }

	@Override
	public void setLearnRepeatRatio(double d) {
		this.learnProperties.put(P_LEARN_REPEAT_RATIO, String.valueOf(d));
	}

	// @Override
	// public void setLastLearntVocabulary(String vocName) {
	// this.learnProperties.put(P_LAST_LEARNT_VOC, vocName);
	// }

	@Override
	public void storePreferences() {
		SettingsBundle bundle = new SettingsBundle();
		bundle.setLearnProperties(learnProperties);
		bundle.setFilePathes(filePathes);
		bundle.setRepeatingProfiles(repeatingProfiles);
		bundle.setWlProfile(wlProfile);
		filesAccessor.storePreferences(bundle);
	}

	@Override
	public int getNotificationPeriod() {
		return Integer.parseInt(learnProperties
				.getProperty(P_NOTIFICATION_PERIOD));
	}

	@Override
	public double getLearnRepeatRatio() {
		return Double.parseDouble(learnProperties
				.getProperty(P_LEARN_REPEAT_RATIO));
	}

	@Override
	public int getLearnWordsAmount() {
		return Integer.parseInt(learnProperties
				.getProperty(P_LEARN_WORDS_AMOUNT));
	}

	@Override
	public int getSessionWordsAmount() {
		return Integer.parseInt(learnProperties
				.getProperty(P_SESSION_WORDS_AMOUNT));
	}

	// @Override
	// public String getLastLearntVocabulary() {
	// return learnProperties.getProperty(P_LAST_LEARNT_VOC);
	// }

	public void setWordsLearning(WordsLearning wordsLeanring) {
		this.wordsLearning = wordsLeanring;
	}

	public void setFilesAccessor(SettingsAccessor filesAccessor) {
		this.filesAccessor = filesAccessor;
	}

	public SettingsAccessor getFilesAccessor() {
		return this.filesAccessor;
	}

}
