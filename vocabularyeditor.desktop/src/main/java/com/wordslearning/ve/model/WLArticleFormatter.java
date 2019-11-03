package com.wordslearning.ve.model;

import java.util.HashSet;
import java.util.Set;

import com.wordslearning.ve.model.article.WLArticle;

public class WLArticleFormatter {

	private final static String INLINE_DIVIDER = ":";
	private final static String LINES_DIVIDER = "\n";
	public final static String HEADER_SYNONYMS = "Synonyms";
	public final static String HEADER_ANTONYMS = "Antonyms";
	public final static String HEADER_EXAMPLES = "Examples";
	public final static String HEADER_ILLUSTRATION = "Illustration";
	public final static String HEADER_PRONUNCIATION = "Pronunciation";

	public WLArticle parseArticle(String rawArticle) {
		String[] articleLines = rawArticle.split("\n");
		WLArticle article = new WLArticle();
		article.setKey(articleLines[0]);
		article.setValue(articleLines[1].trim());
		Set<String> examples = new HashSet<String>();
		for (int i = 2; i < articleLines.length; i++) {
			String line = articleLines[i];
			if (line.startsWith(HEADER_SYNONYMS)) {
				article.setSynonyms(parseCSV(line.substring(line.indexOf(":") + 1)));
			} else if (line.startsWith(HEADER_ANTONYMS)) {
				article.setAntonyms(parseCSV(line.substring(line.indexOf(":") + 1)));
			} else if (line.startsWith(HEADER_ILLUSTRATION)) {
				article.setIllustrationURL(line
						.substring(line.indexOf(":") + 1).trim());
			} else if (line.startsWith(HEADER_PRONUNCIATION)) {
				article.setPronunciation(line.substring(line.indexOf(":") + 1)
						.trim());
			} else if (line.startsWith(HEADER_EXAMPLES)) {
				examples.add(line.substring(line.indexOf(":") + 1).trim());
			} else {
				examples.add(line.trim());
			}
		}
		article.setRawExamples(examples);
		return article;
	}

	private Set<String> parseCSV(String substring) {
		String[] elements = substring.split(",");
		Set<String> values = new HashSet<String>();
		for (String el : elements) {
			String str = el.trim();
			if (!str.equals(""))
				values.add(str);
		}
		return values;
	}

	public String format(WLArticle article) {
		StringBuilder sb = new StringBuilder();
		if (article.getKey() != null)
			sb.append(article.getKey());
		sb.append(LINES_DIVIDER);
		if (article.getValue() != null)
			sb.append(article.getValue());
		sb.append(LINES_DIVIDER);
		sb.append(HEADER_SYNONYMS);
		sb.append(INLINE_DIVIDER + " ");
		if (article.getSynonyms() != null && article.getSynonyms().size() > 0) {
			sb.append(getSetAsCSV(article.getSynonyms()));
		}
		sb.append(LINES_DIVIDER);
		sb.append(HEADER_ANTONYMS);
		sb.append(INLINE_DIVIDER + " ");
		if (article.getAntonyms() != null && article.getAntonyms().size() > 0) {
			sb.append(getSetAsCSV(article.getAntonyms()));
		}
		sb.append(LINES_DIVIDER);
		sb.append(HEADER_ILLUSTRATION);
		sb.append(INLINE_DIVIDER + " ");
		if (article.getIllustrationURL() != null) {
			sb.append(article.getIllustrationURL());
		}

		sb.append(LINES_DIVIDER);
		sb.append(HEADER_PRONUNCIATION);
		sb.append(INLINE_DIVIDER + " ");
		if (article.getPronunciation() != null) {
			sb.append(article.getPronunciation());
		}

		sb.append(LINES_DIVIDER);
		sb.append(HEADER_EXAMPLES);
		sb.append(INLINE_DIVIDER + " ");
		if (article.getRawExamples() != null
				&& article.getRawExamples().size() > 0) {
			for (String example : article.getRawExamples()) {
				sb.append(example);
				if (sb.charAt(sb.length() - 1) != '.')
					sb.append(".");
				sb.append(LINES_DIVIDER);
			}
		}
		return sb.toString();
	}

	private String getSetAsCSV(Set<String> stringSet) {
		StringBuilder sb = new StringBuilder();
		for (String synonym : stringSet) {
			sb.append(synonym + ", ");
		}
		// to remove last 2 characters
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}
}
