package com.wordslearning.wl.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.wordslearning.ve.model.vocabularies.ResultReceiver;
import com.wordslearning.wl.model.ExerciseMeta;

public class SettingsLocalFilesAccessor extends SettingsAccessor {
	private String workDir;
	private File baseDir;
	private static Logger LOG = Logger
			.getLogger(SettingsLocalFilesAccessor.class.getName());

	private RepeatingProfileSerializer repeatingProfileSerializer;
	private WLProfileSerializer wlProfileSerializer;

	private static final String P_LEARN_REPEAT_RATIO = "learn.repeat.ratio";
	private static final String P_NOTIFICATION_PERIOD = "notification.period";
	private static final String P_SESSION_WORDS_AMOUNT = "session.words.ammount";
	private static final String P_LEARN_WORDS_AMOUNT = "learn.words.ammount";
	private static final String P_DATA_DIR = "data.dir";

	@Override
	public void retrieveSettings(ResultReceiver<SettingsBundle> receiver) {
		
		baseDir = new File(workDir);
		baseDir.mkdirs();
		SettingsBundle bundle = new SettingsBundle();
		bundle.setLearnProperties(getLearnProperties());
		bundle.setFilePathes(getFilePathes());
		bundle.setRepeatingProfiles(getRepeatingProfiles());
		bundle.setWlProfile(getWLProfile());
		receiver.setResult(bundle);
	}

	public RepeatingProfileSerializer getRepeatingProfileSerializer() {
		return repeatingProfileSerializer;
	}

	public void setRepeatingProfileSerializer(
			RepeatingProfileSerializer repeatingProfileSerializer) {
		this.repeatingProfileSerializer = repeatingProfileSerializer;
	}

	public WLProfileSerializer getWlProfileSerializer() {
		return wlProfileSerializer;
	}

	public void setWlProfileSerializer(WLProfileSerializer wlProfileSerializer) {
		this.wlProfileSerializer = wlProfileSerializer;
	}

	public Properties getLearnProperties() {
		try {
			File propFile = new File(baseDir, PROPERTIES_FILE_NAME);
			if (!propFile.exists()) {
				createDefaultPropertiesFile(propFile);
			}
			Properties learnProperties = new Properties();
			learnProperties.load(new FileInputStream(propFile));
			return learnProperties;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Properties getFilePathes() {
		try {
			File filePathesFile = new File(baseDir, FILE_PATHES_FILE_NAME);
			if (!filePathesFile.exists()) {
				createDefaultFilePathes(filePathesFile);
			}
			Properties filePathes = new Properties();
			filePathes.load(new FileInputStream(filePathesFile));
			return filePathes;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<RepeatingProfile> getRepeatingProfiles() {
		File repeatingProfilesFile = new File(baseDir, RPFS_FILENAME);
		if (!repeatingProfilesFile.exists()) {
			createDefaultRepeatingProfilesFile(repeatingProfilesFile);
		}
		return getRepeatingProfileSerializer().deserializeRepeatingProfiles(
				repeatingProfilesFile);
	}

	public WLProfile getWLProfile() {
		File wlProfileFile = new File(baseDir, WLPROFILE_FILENAME);
		if (!wlProfileFile.exists()) {
			createDefaultWLProfileFile(wlProfileFile);
		}
		return getWlProfileSerializer().parseWLProfile(wlProfileFile,
				getRepeatingProfiles());
	}

	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	@Override
	public void storePreferences(SettingsBundle bundle) {
		try {
			bundle.getLearnProperties().store(
					new FileOutputStream(getLearnPropertiesFilePath()),
					"Words Learning properties");
			bundle.getFilePathes().store(
					new FileOutputStream(getFilePathesFilePath()),
					"Words Leanring File Pathes");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getRepeatingProfileSerializer().serializeRepeatingProfiles(
				new File(getRepeatingProfilesFilePath()),
				bundle.getRepeatingProfiles());
		getWlProfileSerializer().serializeWLProfile(
				new File(getWLProfileFilePath()), bundle.getWlProfile());
	}

	private String getWLProfileFilePath() {
		return getWorkDir() + "/" + WLPROFILE_FILENAME;
	}

	private String getRepeatingProfilesFilePath() {
		return getWorkDir() + "/" + RPFS_FILENAME;
	}

	private String getLearnPropertiesFilePath() {
		return getWorkDir() + "/" + PROPERTIES_FILE_NAME;
	}

	private String getFilePathesFilePath() {
		return getWorkDir() + "/" + FILE_PATHES_FILE_NAME;
	}

	// Methods for first initialization

	private void createDefaultRepeatingProfilesFile(File repeatingProfilesFile) {
		List<RepeatingProfile> repeatingProfiles = new ArrayList<RepeatingProfile>();
		repeatingProfiles.add(getDefaultLearnProfile());
		repeatingProfiles.add(getDefaultRepeatingProfile());
		getRepeatingProfileSerializer().serializeRepeatingProfiles(
				repeatingProfilesFile, repeatingProfiles);

	}

	private void createDefaultWLProfileFile(File wlProfileFile) {
		WLProfile wlProfile = new WLProfile();
		wlProfile.setLearnProfile(getDefaultLearnProfile());
		RepeatingProfile defaultRepeatingProfile = getDefaultRepeatingProfile();
		wlProfile.putRepeatProfile(1, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(2, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(5, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(12, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(28, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(58, defaultRepeatingProfile);
		wlProfile.putRepeatProfile(88, defaultRepeatingProfile);
		getWlProfileSerializer().serializeWLProfile(wlProfileFile, wlProfile);
	}

	private RepeatingProfile getDefaultRepeatingProfile() {
		RepeatingProfile profile = new RepeatingProfile();
		profile.setId("def_repeating_profile");
		profile.putExercise(ExerciseMeta.SPELLING_EX.getId(), new int[] { 3, 3,
				-2 });
		return profile;
	}

	private RepeatingProfile getDefaultLearnProfile() {
		RepeatingProfile profile = new RepeatingProfile();
		profile.setId("def_learn_profile");
		profile.putExercise(ExerciseMeta.CARD_EX.getId(),
				new int[] { 3, 3, -2 });
		profile.putExercise(ExerciseMeta.SELECT_EX.getId(), new int[] { 3, 3,
				-2 });
		profile.putExercise(ExerciseMeta.SELECT_SYNONYMS.getId(), new int[] {
				3, 3, -2 });
		profile.putExercise(ExerciseMeta.WORD_CONSTRUCTOR_EX.getId(),
				new int[] { 3, 3, -2 });
		profile.putExercise(ExerciseMeta.SPELLING_EX.getId(), new int[] { 5, 3,
				-2 });
		return profile;
	}

	private void createDefaultFilePathes(File filePathesFile) {
		Properties filePathes = new Properties();
		filePathes.put(P_DATA_DIR, filePathesFile.getParent());
		try {
			filePathes.store(new FileOutputStream(filePathesFile),
					"Words Learning Default File Pathes.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createDefaultPropertiesFile(File propFile) {
		Properties props = new Properties();
		props.setProperty(P_LEARN_REPEAT_RATIO, "0.5");
		props.setProperty(P_NOTIFICATION_PERIOD, "30");
		props.setProperty(P_SESSION_WORDS_AMOUNT, "10");
		props.setProperty(P_LEARN_WORDS_AMOUNT, "10");
		try {
			props.store(new FileOutputStream(propFile),
					"Words Learning Default Properties.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
