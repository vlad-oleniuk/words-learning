package com.wordslearning.wl.model.settings;

import java.util.List;
import java.util.Properties;

public class SettingsBundle {

	private Properties learnProperties;
	private Properties filePathes;
	private List<RepeatingProfile> repeatingProfiles;
	private WLProfile wlProfile;

	public Properties getLearnProperties() {
		return learnProperties;
	}

	public void setLearnProperties(Properties learnProperties) {
		this.learnProperties = learnProperties;
	}

	public Properties getFilePathes() {
		return filePathes;
	}

	public void setFilePathes(Properties filePathes) {
		this.filePathes = filePathes;
	}

	public List<RepeatingProfile> getRepeatingProfiles() {
		return repeatingProfiles;
	}

	public void setRepeatingProfiles(List<RepeatingProfile> repeatingProfiles) {
		this.repeatingProfiles = repeatingProfiles;
	}

	public WLProfile getWlProfile() {
		return wlProfile;
	}

	public void setWlProfile(WLProfile wlProfile) {
		this.wlProfile = wlProfile;
	}

}
