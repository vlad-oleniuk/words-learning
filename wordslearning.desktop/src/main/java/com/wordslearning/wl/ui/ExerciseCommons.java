package com.wordslearning.wl.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExerciseCommons {

	public List<ExampleWord> parseExample(String exampleToShow) {
		List<ExampleWord> exampleWords = new ArrayList<ExampleWord>();
		String[] words = exampleToShow.split("(?=\\$)|(?<=\\$)");
		boolean learntWord = false;
		for (String word : words) {
			if (word.trim().equals("$")) {
				learntWord = !learntWord;
				continue;
			}
			if (learntWord) {
				String[] separateWords = word.split(" ");
				for (String separateWord : separateWords) {
					exampleWords.add(new ExampleWord(separateWord, true));
				}
			} else {
				exampleWords.add(new ExampleWord(word, false));
			}
		}
		return exampleWords;
	}

	public List<String> getExamplesWithRequestedWord(Set<String> examples) {
		List<String> examplesList = new ArrayList<String>();
		for (String example : examples) {
			if (example.matches(".*?\\$.*?\\$.*?"))
				examplesList.add(example);
		}
		return examplesList;
	}

	public KeyParts getShuffledKeyParts(String key) {
		List<String> keyParts = new ArrayList<String>();
		String[] words = key.split(" ");
		boolean phrase;
		if (words.length > 2) {
			phrase = true;
			keyParts.addAll(Arrays.asList(words));
		} else {
			phrase = false;
			char[] keyChars = key.toCharArray();
			for (char c : keyChars) {
				keyParts.add(new String(new char[] { c }));
			}

		}
		Collections.shuffle(keyParts);
		KeyParts parts = new KeyParts();
		parts.setPhrase(phrase);
		parts.setParts(keyParts);
		return parts;
	}

}
