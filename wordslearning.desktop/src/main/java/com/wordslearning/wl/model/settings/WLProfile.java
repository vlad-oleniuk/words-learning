package com.wordslearning.wl.model.settings;

import java.util.Map;
import java.util.TreeMap;

public class WLProfile {
	private RepeatingProfile learnProfile;
	private Map<Integer, RepeatingProfile> repeatProfiles=new TreeMap<Integer, RepeatingProfile>();

	public RepeatingProfile getLearnProfile() {
		return learnProfile;
	}

	public void setLearnProfile(RepeatingProfile learnProfile) {
		this.learnProfile = learnProfile;
	}

	public Map<Integer, RepeatingProfile> getRepeatProfiles() {
		return repeatProfiles;
	}

	public void putRepeatProfile(Integer interval, RepeatingProfile profile) {
		this.repeatProfiles.put(interval, profile);
	}

}
