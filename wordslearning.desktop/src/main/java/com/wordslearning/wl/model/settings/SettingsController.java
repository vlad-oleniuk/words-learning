package com.wordslearning.wl.model.settings;

public interface SettingsController {

	void addRepeatProfile(RepeatingProfile newProfile);

	void removeRepeatProfile(RepeatingProfile profile);

	RepeatingProfile[] getRepeatingProfiles();

	RepeatingProfile getRepeatProfile(String repProfId);

	WLProfile getWLProfile();

	int getLearnWordsAmount();

	void storePreferences();

	int getNotificationPeriod();

	double getLearnRepeatRatio();

//	String getDataDir();

	void setNotificationPeriod(int value);

	void setSessionsWordAmount(int value);

	void setLearnWordsAmounty(int value);

//	void setDataDir(String filePath);

	void setLearnRepeatRatio(double d);

	int getSessionWordsAmount();

//	String getLastLearntVocabulary();

//	void setLastLearntVocabulary(String currentVoc);

}