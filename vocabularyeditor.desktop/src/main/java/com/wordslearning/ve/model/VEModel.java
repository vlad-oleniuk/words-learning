package com.wordslearning.ve.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.article.WLVocabulary;
import com.wordslearning.ve.model.article.WordsStorage;
import com.wordslearning.ve.model.vocabularies.VocabulariesAccessor;
import com.wordslearning.ve.model.vocabularies.ResultReceiver;
import com.wordslearning.ve.model.words_storages.DudenWordsStorage;
import com.wordslearning.ve.model.words_storages.LongmanWordsStorage;

public class VEModel implements ResultReceiver<List<WLVocabulary>> {

	private VocabulariesAccessor vocabulariesAccessor;

	private static final String VOC_FILE = "voc_file";

	private List<VEModelListener> listeners = new ArrayList<VEModelListener>();
	private List<WLVocabulary> wlVocabularies = new ArrayList<WLVocabulary>();
	private WLVocabulary editedVocabulary;

	private List<WordsStorage> sourceVocabularies = new ArrayList<WordsStorage>();

	private Properties properties = new Properties();
	{
		sourceVocabularies.add(new DudenWordsStorage());
		sourceVocabularies.add(new LongmanWordsStorage());
	}

	public void init() {
		vocabulariesAccessor.retrieveVocabularies(this);
	}

	public List<WordsStorage> getSourceVocabularies() {
		return sourceVocabularies;
	}

	public void addWLVocabulary(WLVocabulary vocabulary) {

		if (vocabulary.getName() == null || vocabulary.getName().equals("")) {
			vocabulary.setName("_voc_" + (int) (Math.random() * 100));
		}

		boolean alreadyIncluded = false;
		for (WordsStorage wordsStorage : getWlVocabularies()) {
			if (wordsStorage.getName().equals(vocabulary.getName())) {
				alreadyIncluded = true;
				break;
			}
		}
		if (!alreadyIncluded)
			this.getWlVocabularies().add(vocabulary);
	}

	public void addWLArticle(WLArticle wlArticle, boolean homonymsChecked,
			boolean synonymsChecked, boolean lastInBatch) {
		if (!homonymsChecked) {
			List<WLArticle> homonyms = getHomonyms(wlArticle.getKey(),
					editedVocabulary.getArticles());
			if (homonyms.size() > 0) {
				homonymsRevealed(wlArticle, homonyms);
				return;
			}
		}
		if (!synonymsChecked) {
			List<WLArticle> synonyms = getSynonyms(wlArticle,
					editedVocabulary.getArticles());
			if (synonyms.size() > 0) {
				synonymsRevealed(wlArticle, synonyms);
				return;
			}
		}
		editedVocabulary.addArticle(wlArticle);
		if (lastInBatch) {
			modelUpdated();
		}
	}

	private List<WLArticle> getSynonyms(WLArticle wlArticle,
			List<WLArticle> articles) {
		List<WLArticle> synonyms = new ArrayList<WLArticle>();
		for (WLArticle article : articles) {
			if (article.getSynonyms().contains(wlArticle.getKey())
					|| wlArticle.getSynonyms().contains(article.getKey())) {
				synonyms.add(article);
			}
		}
		return synonyms;
	}

	private List<WLArticle> getHomonyms(String key, List<WLArticle> articles) {
		List<WLArticle> homonyms = new ArrayList<WLArticle>();
		for (WLArticle article : articles) {
			if (key.equals(article.getKey())) {
				homonyms.add(article);
			}
		}
		return homonyms;
	}

	public void addWLArticle(WLArticle wlArticle, boolean homonymsChecked,
			boolean synonymsChecked) {
		addWLArticle(wlArticle, homonymsChecked, synonymsChecked, true);
	}

	public void addWLArticle(WLArticle wlArticle) {
		addWLArticle(wlArticle, false, false);
	}

	public Language getCurrentLanguage() {
		return editedVocabulary.getLangFrom();
	}

	private void homonymsRevealed(WLArticle wlArticle, List<WLArticle> homonyms) {
		for (VEModelListener listener : listeners) {
			listener.homonymsRevealed(wlArticle, homonyms);
		}
	}

	private void synonymsRevealed(WLArticle wlArticle, List<WLArticle> synonyms) {
		for (VEModelListener listener : listeners) {
			listener.synonymsRevealed(wlArticle, synonyms);
		}
	}

	private void modelUpdated() {
		for (VEModelListener listener : listeners) {
			listener.modelChanged();
		}
	}

	/**
	 * Persists model.
	 */
	public void flush() {
		vocabulariesAccessor.saveVocabularies(getWlVocabularies());
	}

	public void addListener(VEModelListener listener) {
		listeners.add(listener);
	}

	public void updateWord(WLArticle oldWord, WLArticle newWord) {
		if (oldWord != newWord) {
			editedVocabulary.getArticles().remove(oldWord);
			addWLArticle(newWord, false, false);
		}
		modelUpdated();
	}

	public void removeArticle(WLArticle wlArticle) {
		editedVocabulary.getArticles().remove(wlArticle);
		modelUpdated();
	}

	/**
	 * read only properties
	 * 
	 * @return list of the properties
	 */
	public Properties getProperties() {
		Properties props = new Properties();
		props.put(VOC_FILE,
				editedVocabulary != null ? editedVocabulary.getName() : "");
		return props;
	}

	public void setEditedVocabulary(String vocName) {
		if (vocName != null) {
			properties.setProperty(VOC_FILE, vocName);
			WLVocabulary voc = getWLVocabulary(vocName);
			if (voc != null)
				setEditedVocabulary(voc);
		}
	}

	public void setEditedVocabulary(WLVocabulary voc) {
		this.editedVocabulary = voc;
		for (VEModelListener listener : listeners) {
			listener.editedVocabularyChanged();
		}
	}

	public void removeWLVocabulary(WLVocabulary storage) {
		getWlVocabularies().remove(storage);
		modelUpdated();
	}

	public WLVocabulary getEditedVocabulary() {
		return editedVocabulary;
	}

	private WLVocabulary getWLVocabulary(String vocName) {
		for (WLVocabulary voc : getWlVocabularies()) {
			if (voc.getName().equals(vocName))
				return voc;
		}
		return null;
	}

	public void createVocabulary(WLVocabulary voc) {
		addWLVocabulary(voc);
		modelUpdated();
	}

	public void setVocabulariesAccessor(VocabulariesAccessor vocsAccessor) {
		this.vocabulariesAccessor = vocsAccessor;
	}

	public List<WLVocabulary> getWlVocabularies() {
		return wlVocabularies;
	}

	@Override
	public void setResult(List<WLVocabulary> wlVocabularies) {
		this.wlVocabularies = wlVocabularies;

		if (properties.getProperty(VOC_FILE) != null) {
			setEditedVocabulary(properties.getProperty(VOC_FILE));
		}

		if (getEditedVocabulary() == null && this.wlVocabularies != null
				&& this.wlVocabularies.size() > 0) {
			setEditedVocabulary(this.wlVocabularies.get(0));
		}

		for (VEModelListener listener : listeners) {
			listener.vocabulariesInitialized();
		}
	}

	public void setProperties(Properties props) {
		this.properties = props;
	}
}
