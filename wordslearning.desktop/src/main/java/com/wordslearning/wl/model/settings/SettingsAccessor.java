package com.wordslearning.wl.model.settings;

import com.wordslearning.ve.model.vocabularies.ResultReceiver;

public abstract class SettingsAccessor {

	protected final static String RPFS_FILENAME = "repeating_profiles.xml";
	protected final static String WLPROFILE_FILENAME = "wl_profile.xml";
	protected static final String FILE_PATHES_FILE_NAME = "file_pathes.properties";
	protected static final String PROPERTIES_FILE_NAME = "words_learning.properties";

	public abstract void retrieveSettings(
			ResultReceiver<SettingsBundle> receiver);

	public abstract void storePreferences(SettingsBundle settingsBundle);

}
