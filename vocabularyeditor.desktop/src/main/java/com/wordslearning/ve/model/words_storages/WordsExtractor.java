package com.wordslearning.ve.model.words_storages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordslearning.ve.model.article.WLArticle;

class WordsExtractor {
	private String baseUrl;
	private ObjectMapper mapper = new ObjectMapper();

	public WordsExtractor(String relPath) {
		this.baseUrl = "http://wordsextractor-oleniuk.rhcloud.com/wordsextractor-oleniuk/"
				+ relPath + "/";
	}

	public List<WLArticle> searchArticles(String searchKey) {
		try {
			URL url = new URL(baseUrl + searchKey);
			JsonNode articlesList = mapper.readTree(url.openStream());
			List<WLArticle> articles = new ArrayList<WLArticle>();
			for (JsonNode articleEl : articlesList) {
				articles.add(extractArticle(articleEl));
			}
			return articles;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	private WLArticle extractArticle(JsonNode articleEl) {
		WLArticle article = new WLArticle();
		article.setKey(articleEl.get("key").asText());
		article.setValue(articleEl.get("value").asText());
		Set<String> examples = new HashSet<String>();
		JsonNode exampleList = articleEl.get("examples");
		for (JsonNode exampleNode : exampleList) {
			examples.add(exampleNode.asText());
		}
		article.setRawExamples(examples);

		Set<String> synonyms = new HashSet<String>();
		JsonNode synonymList = articleEl.get("synonyms");
		for (JsonNode synonymNode : synonymList) {
			synonyms.add(synonymNode.asText());
		}
		article.setSynonyms(synonyms);

		Set<String> antonyms = new HashSet<String>();
		JsonNode antonymList = articleEl.get("antonyms");
		for (JsonNode synonymNode : antonymList) {
			antonyms.add(synonymNode.asText());
		}
		article.setAntonyms(antonyms);

		JsonNode illustration = articleEl.get("illustration");
		if (illustration != null)
			article.setIllustrationURL(illustration.asText());

		JsonNode pronunciation = articleEl.get("pronunciation");
		if (pronunciation != null)
			article.setPronunciation(pronunciation.asText());

		return article;
	}

}
