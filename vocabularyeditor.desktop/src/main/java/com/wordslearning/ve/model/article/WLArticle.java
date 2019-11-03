package com.wordslearning.ve.model.article;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WLArticle {

	private String key;
	private String value;

	private String illustrationURL;
	private String pronunciation;

	private Set<String> synonyms;
	private Set<String> antonyms;
	private Set<String> examples;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return key;
	}

	public Set<String> getSynonyms() {
		if (synonyms == null)
			synonyms = new HashSet<String>();
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}

	public Set<String> getAntonyms() {
		if (antonyms == null)
			antonyms = new HashSet<String>();
		return antonyms;
	}

	public void setAntonyms(Set<String> antonyms) {
		this.antonyms = antonyms;
	}

	@JsonIgnore
	public Set<String> getExamples() {
		Set<String> examples = getRawExamples();
		Set<String> processedExamples = new HashSet<String>();
		for (String example : examples) {
			processedExamples.add(example.replace("$", ""));
		}
		return processedExamples;
	}

	public String getIllustrationURL() {
		return illustrationURL;
	}

	public void setIllustrationURL(String illustrationURL) {
		this.illustrationURL = illustrationURL;
	}

	public void setRawExamples(Set<String> rawExamples) {
		// TODO remove this, after examples are cleaned
		Set<String> uniqueExamples = new HashSet<String>();
		for (String example : rawExamples) {
			if (!uniqueExamples.contains("$")) {
				uniqueExamples.add(example);
			}
		}

		for (String example : rawExamples) {
			if (example.contains("$")) {
				if (uniqueExamples.contains(example.replace("$", ""))) {
					uniqueExamples.remove(example.replace("$", ""));
				}
				uniqueExamples.add(example);
			}
		}
		this.examples = uniqueExamples;
	}

	public Set<String> getRawExamples() {
		return examples;
	}

	public String getPronunciation() {
		return pronunciation;
	}

	public void setPronunciation(String pronunciation) {
		this.pronunciation = pronunciation;
	}
}
